package ml.sky233.suiteki.service.ble.callback

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleMtuChangedCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import ml.sky233.suiteki.callback.ReAuthCallback
import ml.sky233.suiteki.service.ble.BleActions
import ml.sky233.suiteki.service.ble.BleDeviceInfo
import ml.sky233.suiteki.service.ble.BleService
import ml.sky233.suiteki.service.ble.HuamiService
import org.apache.commons.lang3.ArrayUtils
import ml.sky233.suiteki.util.BytesUtils
import ml.sky233.suiteki.util.Crypto.CryptoUtils
import ml.sky233.suiteki.util.Crypto.ECDH_B163
import ml.sky233.suiteki.util.MsgUtils
import java.nio.ByteBuffer
import java.util.Random

@SuppressLint("MissingPermission")
class BleGattCallback(var handler: Handler, var context: Context, authKey: String) :
    BleGattCallback() {
    var isAuthedBySuiteki = false //检查Suiteki自身是否验证过了
    var mGatt: BluetoothGatt? = null
    var bleDevice: BleDevice? = null
    var writeHandle: Byte = 0
    var privateEC = ByteArray(24)
    lateinit var publicEC: ByteArray
    var remotePublicEC = ByteArray(48)
    val remoteRandom = ByteArray(16)
    lateinit var sharedEC: ByteArray
    val finalSharedSessionAES = ByteArray(16)
    var encryptedSequenceNr = 0
    var authKey: ByteArray?
    var currentHandle: Byte? = null
    var currentType = 0
    var currentLength = 0
    var reassemblyBuffer: ByteBuffer? = null
    var callback: ReAuthCallback? = null
    var checkAuthed = false

    init {
        this.authKey = getSecretKey(authKey)
    }

    override fun onStartConnect() {
        setStatus(HuamiService.STATUS_BLE_CONNECTING)
    }

    override fun onConnectFail(bleDevice: BleDevice, e: BleException) {
        setStatus(HuamiService.STATUS_BLE_CONNECT_FAILURE)
    }

    override fun onConnectSuccess(bleDevice: BleDevice, bluetoothGatt: BluetoothGatt, i: Int) {
        setStatus(HuamiService.STATUS_BLE_CONNECTED)
        mGatt = bluetoothGatt
        this.bleDevice = bleDevice
        Thread {
            BleManager.getInstance().setMtu(bleDevice, 512, object : BleMtuChangedCallback() {
                override fun onSetMTUFailure(exception: BleException) {
                    // 设置MTU失败
                }

                override fun onMtuChanged(mtu: Int) {
                    BleManager.getInstance().notify(
                        bleDevice,
                        HuamiService.UUID_SERVICE_MIBAND_SERVICE.toString(),
                        HuamiService.UUID_CHARACTERISTIC_AUTH_NOTIFY.toString(),
                        BleNotifyCallback(context, HuamiService.UUID_CHARACTERISTIC_AUTH_NOTIFY)
                    )
                    BleManager.getInstance().notify(
                        bleDevice,
                        HuamiService.UUID_SERVICE_FIRMWARE.toString(),
                        HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY.toString(),
                        BleNotifyCallback(context, HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY)
                    )
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        throw RuntimeException(e)
                    }
                    checkAuthed = false
                    checkAuthed()
                    // 设置MTU成功，并获得当前设备传输支持的MTU值
                }
            })
        }.start()
    }

    fun reAuth(callback: ReAuthCallback?) {
        this.callback = callback
        setStatus(HuamiService.STATUS_BLE_AUTHING)
        doPerform()
    }

    fun onCharacteristicsChange(intent: Intent) {
        if (intent.getAction() == BleActions.ACTION_BLE_AUTH_NOTIFY) {
            val bytes: ByteArray = intent.getByteArrayExtra("value")!!
            if (bytes.size <= 1 || bytes[0].toInt() != 0x03) return
            decode(bytes)
        } else if (intent.getAction() == BleActions.ACTION_BLE_FIRMWARE_NOTIFY) {
            val bytes: ByteArray = intent.getByteArrayExtra("value")!!
            if (!checkAuthed) {
                if (bytes[0].toInt() == 16 && bytes[1].toInt() == -46 && bytes[2].toInt() == 1) { //10D201
                    setStatus(HuamiService.STATUS_BLE_NORMAL)
                } else doPerform() //当官方应用未连接时会尝试自己连接
                checkAuthed = true
            }
            if (bytes[0].toInt() == 16 && bytes[1].toInt() == -46 && bytes[2].toInt() == 10) { //10D20A
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    override fun onDisConnected(
        b: Boolean,
        bleDevice: BleDevice,
        bluetoothGatt: BluetoothGatt,
        i: Int,
    ) {
        setStatus(HuamiService.STATUS_BLE_DISCONNECT)
    }

    fun setStatus(status: Int) {
        handler.sendMessage(MsgUtils.build(status, 1))
    }

    fun doPerform() {
        BleService.Companion.setStatus(HuamiService.STATUS_BLE_AUTHING)
        Random().nextBytes(privateEC)
        publicEC = ml.sky233.suiteki.util.Crypto.ECDH_B163.ecdh_generate_public(privateEC)
        val sendPubkeyCommand = ByteArray(48 + 4)
        sendPubkeyCommand[0] = 0x04
        sendPubkeyCommand[1] = 0x02
        sendPubkeyCommand[2] = 0x00
        sendPubkeyCommand[3] = 0x02
        System.arraycopy(publicEC, 0, sendPubkeyCommand, 4, 48)
        write(0x0082.toShort(), sendPubkeyCommand, true, false)
    }

    fun decode(data: ByteArray) {
        var i = 0
        if (data[i++].toInt() != 0x03) {
            return
        }
        val flags = data[i++]
        val encrypted = flags.toInt() and 0x08 == 0x08
        val firstChunk = flags.toInt() and 0x01 == 0x01
        val lastChunk = flags.toInt() and 0x02 == 0x02
        i++
        val handle = data[i++]
        if (currentHandle != null && currentHandle != handle) {
            return
        }
        val count = data[i++]
        if (firstChunk) { // beginning
            var full_length =
                data[i++].toInt() and 0xff or (data[i++].toInt() and 0xff shl 8) or (data[i++].toInt() and 0xff shl 16) or (data[i++].toInt() and 0xff shl 24)
            currentLength = full_length
            if (encrypted) {
                var encrypted_length = full_length + 8
                val overflow = encrypted_length % 16
                if (overflow > 0) {
                    encrypted_length += 16 - overflow
                }
                full_length = encrypted_length
            }
            reassemblyBuffer = ByteBuffer.allocate(full_length)
            currentType = data[i++].toInt() and 0xff or (data[i++].toInt() and 0xff shl 8)
            currentHandle = handle
        }
        reassemblyBuffer!!.put(data, i, data.size - i)
        if (lastChunk) { // end
            var buf = reassemblyBuffer!!.array()
            if (encrypted) {
                if (authKey == null) {
                    currentHandle = null
                    currentType = 0
                    return
                }
                val messagekey = ByteArray(16)
                for (j in 0..15) {
                    messagekey[j] = (authKey!![j].toInt() xor handle.toInt()).toByte()
                }
                try {
                    buf = CryptoUtils.decryptAES(buf, messagekey)
                    buf = ArrayUtils.subarray(buf, 0, currentLength)
                } catch (e: Exception) {
                    e.printStackTrace()
                    currentHandle = null
                    currentType = 0
                    return
                }
            }
            try {
                val finalBuf = buf
                Thread { handle2021Payload(currentType.toShort(), finalBuf) }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            currentHandle = null
            currentType = 0
        }
    }

    fun getSecretKey(key: String): ByteArray {
        val authKeyBytes = byteArrayOf(
            0x30,
            0x31,
            0x32,
            0x33,
            0x34,
            0x35,
            0x36,
            0x37,
            0x38,
            0x39,
            0x40,
            0x41,
            0x42,
            0x43,
            0x44,
            0x45
        )
        var authKey = ""
        authKey = if (!key.startsWith("0x")) "0x$key" else key
        var srcBytes = authKey.trim { it <= ' ' }.toByteArray()
        if (authKey.length == 34 && authKey.startsWith("0x")) {
            srcBytes = BytesUtils.hexToBytes(authKey.substring(2))
        }
        System.arraycopy(srcBytes, 0, authKeyBytes, 0, Math.min(srcBytes.size, 16))
        return authKeyBytes
    }

    fun handle2021Payload(type: Short, payload: ByteArray) {
        if (payload[0].toInt() == 0x10 && payload[1].toInt() == 0x04 && payload[2].toInt() == 0x01) {
            System.arraycopy(payload, 3, remoteRandom, 0, 16)
            System.arraycopy(payload, 19, remotePublicEC, 0, 48)
            sharedEC = ECDH_B163.ecdh_generate_shared(privateEC, remotePublicEC)
            val encryptedSequenceNumber =
                sharedEC[0].toInt() and 0xff or (sharedEC[1].toInt() and 0xff shl 8) or (sharedEC[2].toInt() and 0xff shl 16) or (sharedEC[3].toInt() and 0xff shl 24)
            val secretKey = authKey
            for (i in 0..15) {
                finalSharedSessionAES[i] =
                    (sharedEC[i + 8].toInt() xor secretKey!![i].toInt()).toByte()
            }
            encryptedSequenceNr = encryptedSequenceNumber
            authKey = finalSharedSessionAES
            try {
                val encryptedRandom1: ByteArray = CryptoUtils.encryptAES(remoteRandom, secretKey)
                val encryptedRandom2: ByteArray =
                    CryptoUtils.encryptAES(remoteRandom, finalSharedSessionAES)
                if (encryptedRandom1.size == 16 && encryptedRandom2.size == 16) {
                    val command = ByteArray(33)
                    command[0] = 0x05
                    System.arraycopy(encryptedRandom1, 0, command, 1, 16)
                    System.arraycopy(encryptedRandom2, 0, command, 17, 16)
                    write(0x0082.toShort(), command, true, false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (payload[0].toInt() == 0x10 && payload[1].toInt() == 0x05 && payload[2].toInt() == 0x01) {
            if (callback != null) {
                callback!!.reAuthSuccess()
                callback = null
            }

            BleService.setStatus(HuamiService.STATUS_BLE_AUTHED)
            BleService.setStatus(HuamiService.STATUS_BLE_CONNECTED)
            BleService.setStatus(HuamiService.STATUS_BLE_NORMAL)
            isAuthedBySuiteki = true
            bleDevice?.let { BleDeviceInfo(it) }
            return
        } else {
//            bleAppInfo.decodeAndUpdateDisplayItems(currentType.toShort(), payload)
        }
    }

    fun write(
        type: Short, data: ByteArray, extended_flags: Boolean,
        encrypt: Boolean,
    ) {
        var data = data
        if (encrypt && authKey == null) return
        writeHandle++
        var remaining = data.size
        val length = data.size
        var count: Byte = 0
        var header_size = 10
        if (extended_flags) {
            header_size++
        }
        if (extended_flags && encrypt) {
            val messagekey = ByteArray(16)
            for (i in 0..15) {
                messagekey[i] = (authKey!![i].toInt() xor writeHandle.toInt()).toByte()
            }
            var encrypted_length = length + 8
            val overflow = encrypted_length % 16
            if (overflow > 0) {
                encrypted_length += 16 - overflow
            }
            val encryptable_payload = ByteArray(encrypted_length)
            System.arraycopy(data, 0, encryptable_payload, 0, length)
            encryptable_payload[length] = (encryptedSequenceNr and 0xff).toByte()
            encryptable_payload[length + 1] = (encryptedSequenceNr shr 8 and 0xff).toByte()
            encryptable_payload[length + 2] = (encryptedSequenceNr shr 16 and 0xff).toByte()
            encryptable_payload[length + 3] = (encryptedSequenceNr shr 24 and 0xff).toByte()
            encryptedSequenceNr++
            val checksum: Int = BytesUtils.getCRC32(encryptable_payload, 0, length + 4)
            encryptable_payload[length + 4] = (checksum and 0xff).toByte()
            encryptable_payload[length + 5] = (checksum shr 8 and 0xff).toByte()
            encryptable_payload[length + 6] = (checksum shr 16 and 0xff).toByte()
            encryptable_payload[length + 7] = (checksum shr 24 and 0xff).toByte()
            remaining = encrypted_length
            data = try {
                CryptoUtils.encryptAES(encryptable_payload, messagekey)
            } catch (e: Exception) {
                return
            }
        }
        while (remaining > 0) {
            val MAX_CHUNKLENGTH = 20 - header_size
            val copyBytes = Math.min(remaining, MAX_CHUNKLENGTH)
            val chunk = ByteArray(copyBytes + header_size)
            var flags: Byte = 0
            if (encrypt) {
                flags = (flags.toInt() or 0x08).toByte()
            }
            if (count.toInt() == 0) {
                flags = (flags.toInt() or 0x01).toByte()
                var i = 4
                if (extended_flags) {
                    i++
                }
                chunk[i++] = (length and 0xff).toByte()
                chunk[i++] = (length shr 8 and 0xff).toByte()
                chunk[i++] = (length shr 16 and 0xff).toByte()
                chunk[i++] = (length shr 24 and 0xff).toByte()
                chunk[i++] = (type.toInt() and 0xff).toByte()
                chunk[i] = (type.toInt() shr 8 and 0xff).toByte()
            }
            if (remaining <= MAX_CHUNKLENGTH) {
                flags = (flags.toInt() or 0x06).toByte() // last chunk?
            }
            chunk[0] = 0x03
            chunk[1] = flags
            if (extended_flags) {
                chunk[2] = 0
                chunk[3] = writeHandle
                chunk[4] = count
            } else {
                chunk[2] = writeHandle
                chunk[3] = count
            }
            System.arraycopy(data, data.size - remaining, chunk, header_size, copyBytes)
            writeAuth(chunk)
            remaining -= copyBytes
            header_size = 4
            if (extended_flags) {
                header_size++
            }
            count++
        }
    }

    fun checkAuthed() {
        val bytes = byteArrayOf(-46, 8, 0, 0, 1, 0, 67, -111, 17, -29, 0, 32, 0, -1)
        BleManager.getInstance().write(bleDevice,
            HuamiService.UUID_SERVICE_FIRMWARE.toString(),
            HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY.toString(),
            bytes, object : BleWriteCallback() {
                override fun onWriteSuccess(i: Int, i1: Int, bytes: ByteArray) {


                }

                override fun onWriteFailure(e: BleException) {


                }
            })
    }

    private fun writeAuth(bytes: ByteArray?) {
        BleManager.getInstance().write(bleDevice,
            HuamiService.UUID_SERVICE_MIBAND_SERVICE.toString(),
            HuamiService.UUID_CHARACTERISTIC_AUTH_WRITE.toString(),
            bytes, object : BleWriteCallback() {
                override fun onWriteSuccess(i: Int, i1: Int, bytes: ByteArray) {


                }

                override fun onWriteFailure(e: BleException) {


                }
            })
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

}