package ml.sky233.suiteki.service.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import ml.sky233.suiteki.MainApplication
import ml.sky233.suiteki.MainApplication.Companion.context
import ml.sky233.suiteki.callback.ProgressCallback
import ml.sky233.suiteki.callback.ReAuthCallback
import ml.sky233.suiteki.util.BytesUtils
import ml.sky233.suiteki.util.MsgUtils

@SuppressLint("MissingPermission")
class FirmwareInstaller : ReAuthCallback {
    private var doPerformBytes = ArrayList<ByteArray>()
    private var finalPerformBytes = ArrayList<ByteArray>()
    private var doPerform = 0
    private var finalPerform = 0
    private var dataIndex = 0
    private var dataIndex_for_progress = 0 //该变量的定义只是为了进度条而存在
    private var mGatt: BluetoothGatt
    private var firmware_bytes: ByteArray
    private var spilt_byte: ArrayList<ByteArray>
    private var bleDevice: BleDevice
    private var handler: Handler
    var callback: ProgressCallback? = null

    constructor(
        gatt: BluetoothGatt,
        bytes: ByteArray,
        bleDevice: BleDevice,
        handler: Handler,
    ) {
        mGatt = gatt
        firmware_bytes = bytes
        this.bleDevice = bleDevice
        this.handler = handler
        BleService.Companion.setStatus(HuamiService.STATUS_BLE_INSTALLING)
        spilt_byte = BytesUtils.spiltBytes(bytes)
        d2 = BytesUtils.getD2(bytes, HuamiService.TYPE_D2_WATCHFACE)
        doPerformBytes.add(d0)
        doPerformBytes.add(d1)
        doPerformBytes.add(d2)
        doPerformBytes.add(d3)
        finalPerformBytes.add(d5)
        finalPerformBytes.add(d6)
        doPerform()
    }

    constructor(
        gatt: BluetoothGatt,
        bytes: ByteArray,
        bleDevice: BleDevice,
        handler: Handler,
        callback: ProgressCallback,
    ) {
        mGatt = gatt
        firmware_bytes = bytes
        this.bleDevice = bleDevice
        this.handler = handler
        this.callback = callback
        BleService.Companion.setStatus(HuamiService.STATUS_BLE_INSTALLING)
        spilt_byte = BytesUtils.spiltBytes(bytes)
        d2 = BytesUtils.getD2(bytes, HuamiService.TYPE_D2_WATCHFACE)
        doPerformBytes.add(d0)
        doPerformBytes.add(d1)
        doPerformBytes.add(d2)
        doPerformBytes.add(d3)
        finalPerformBytes.add(d5)
        finalPerformBytes.add(d6)
        doPerform()
    }

    fun setStatus(status: Int) {
        handler.sendMessage(MsgUtils.build(status, 1))
    }

    fun writeFirmware(bytes: ByteArray?) {
        BleManager.getInstance().write(bleDevice,
            HuamiService.UUID_SERVICE_FIRMWARE.toString(),
            HuamiService.UUID_CHARACTERISTIC_FIRMWARE_WRITE.toString(),
            bytes,
            true,
            true,
            0,
            object : BleWriteCallback() {
                override fun onWriteSuccess(i: Int, i1: Int, bytes: ByteArray) {
                    updateProgress()
                }

                override fun onWriteFailure(e: BleException) {
                }
            })
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    fun writeNotify(bytes: ByteArray?) {
        BleManager.getInstance().write(bleDevice,
            HuamiService.UUID_SERVICE_FIRMWARE.toString(),
            HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY.toString(),
            bytes,
            object : BleWriteCallback() {
                override fun onWriteSuccess(i: Int, i1: Int, bytes: ByteArray) {
                }

                override fun onWriteFailure(e: BleException) {
                }
            })
    }

    fun doPerform() {
        if (doPerform >= doPerformBytes.size) return
        writeNotify(doPerformBytes[doPerform])
        doPerform++
    }

    fun install() {
        dataIndex = 0
        writeBytes()
    }

    fun writeBytes() {
        val remaining = Math.min(spilt_byte.size - dataIndex, 34)
        var bytes = ByteArray(0)
        for (i in 0 until remaining) {
            bytes = BytesUtils.addTwoArray(bytes, spilt_byte[dataIndex])
            dataIndex++
        }
        writeFirmware(bytes)
    }

    private fun updateProgress() {
        if (callback != null) callback!!.onProgressChange((dataIndex_for_progress.toDouble() / spilt_byte.size.toDouble() * 100.0).toInt()) else context.sendBroadcast(
            Intent().setAction(BleActions.ACTION_BLE_FIRMWARE_INSTALLING).putExtra(
                "progress",
                (dataIndex_for_progress.toDouble() / spilt_byte.size.toDouble() * 100.0).toInt()
            )
        )
        dataIndex_for_progress++
    }

    fun onCharacteristicChange(intent: Intent) {

        if (intent.getAction() == BleActions.ACTION_BLE_FIRMWARE_NOTIFY) {
            val value: ByteArray = intent.getByteArrayExtra("value")!!
            when (BytesUtils.bytesToHexStr(value)) {
                "10D001050020", "10D00105002001", "10D10100", "10D2010000000000000000" -> doPerform()
                "10D203" -> {
                    MainApplication.mService?.reAuth(this)
                    return
                }

                "10D347" -> {
                    //空间不足
                    setStatus(HuamiService.STATUS_BLE_NORMAL)
                    context.sendBroadcast(
                        Intent().setAction(BleActions.ACTION_BLE_FIRMWARE_INSTALL_FAILURE)
                    )
                    return
                }

                "10D301" -> {
                    install()
                    return
                }

                "10D501" -> {
                    finalPerform()
                    return
                }

                "10D601" -> {
                    //安装完毕
                    setStatus(HuamiService.STATUS_BLE_NORMAL)
                    context.sendBroadcast(
                        Intent().setAction(BleActions.ACTION_BLE_FIRMWARE_INSTALLED)
                    )
                    return
                }

                else -> {
                    if (value[0].toInt() == 16 && value[1].toInt() == -44) { //10D4
                        if (dataIndex >= spilt_byte.size) finalPerform() else writeBytes()

                    }
                    if (value[0].toInt() == 16 && value[1].toInt() == -46 && value[2].toInt() == 1) {
                        doPerform()
                        //10D201
                    }
                    //安装失败
                    return
                }
            }
        }
    }

    fun finalPerform() {
        if (finalPerform >= finalPerformBytes.size) return
        writeNotify(finalPerformBytes[finalPerform])
        finalPerform++
    }

    companion object {
        var d0 = byteArrayOf(-48)
        var d1 = byteArrayOf(-47)
        var d2 = byteArrayOf()
        var d3 = byteArrayOf(-45, 1)
        var d5 = byteArrayOf(-43)
        var d6 = byteArrayOf(-42)
    }

    override fun reAuthSuccess() {
        doPerform = 0
        doPerform()
    }
}