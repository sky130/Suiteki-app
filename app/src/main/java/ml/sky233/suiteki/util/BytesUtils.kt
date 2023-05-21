package ml.sky233.suiteki.util

import android.util.Log
import java.nio.ByteBuffer
import java.util.Arrays
import java.util.zip.CRC32

object BytesUtils {
    const val WATCHFACE = 0
    const val APP = 1
    const val FIRMWARE = 2
    fun hexToInt(hex: String): Int {
        return hex.toInt(16)
    }

    fun addTwoArray(btX: ByteArray, btY: ByteArray): ByteArray {
        //定义目标数组  目标数组应该等于将要拼接的两个数组的总长度
        val btZ = ByteArray(btX.size + btY.size)
        System.arraycopy(btX, 0, btZ, 0, btX.size)
        System.arraycopy(btY, 0, btZ, btX.size, btY.size)
        return btZ
    }

    fun bytesToHexStr(byteArray: ByteArray?): String? {
        if (byteArray == null) {
            return null
        }
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(byteArray.size * 2)
        for (j in byteArray.indices) {
            val v = byteArray[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun bytesToInt(byteArray: ByteArray?): Int {
        if (byteArray == null) {
            return 0
        }
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(byteArray.size * 2)
        for (j in byteArray.indices) {
            val v = byteArray[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars).toInt(16)
    }

    fun intToBytes(a: Int): ByteArray {
        return hexToBytes(String.format("%06x", a))
    }

    fun LongToBytes(a: Long?): ByteArray {
        return hexToBytes(String.format("%08x", a))
    }

    fun getD2(data_length: Int, CRC: Long): ByteArray {
        val a1 = byteArrayOf(-46, 8)
        val a2 = intToBytes(data_length)
        val a3 = byteArrayOf(0)
        val a4 = LongToBytes(CRC)
        val a5 = byteArrayOf(0, 32, 0, -1)
        val byteBuffer = ByteBuffer.allocate(a1.size + a2.size + a3.size + a4.size + a5.size)
        byteBuffer.put(a1).put(a2).put(a3).put(a4).put(a5)
        return byteBuffer.array()
    }

    fun getCRC32(seq: ByteArray?, offset: Int, length: Int): Int {
        val crc = CRC32()
        crc.update(seq, offset, length)
        return crc.value.toInt()
    }

    fun getD2(bytes: ByteArray, mode: Byte): ByteArray {
        val arrayOfByte = ByteArray(14)
        arrayOfByte[0] = -46
        //        switch (mode) {
//            case APP:
//                arrayOfByte[1] = -96;//A0
//                break;
//            case FIRMWARE:
//                arrayOfByte[1] = -3;//FD
//                break;
//            default://WATCHFACE
//                arrayOfByte[1] = 8;//08
//                break;
//        }
        arrayOfByte[1] = mode
        var arrayOfByte1 = fromUint32(bytes.size)
        arrayOfByte[2] = arrayOfByte1[0]
        arrayOfByte[3] = arrayOfByte1[1]
        arrayOfByte[4] = arrayOfByte1[2]
        arrayOfByte[5] = arrayOfByte1[3]
        val crc = CRC32()
        crc.update(bytes)
        arrayOfByte1 = fromUint32(crc.value.toInt())
        arrayOfByte[6] = arrayOfByte1[0]
        arrayOfByte[7] = arrayOfByte1[1]
        arrayOfByte[8] = arrayOfByte1[2]
        arrayOfByte[9] = arrayOfByte1[3]
        arrayOfByte[10] = 0
        arrayOfByte[11] = 32
        arrayOfByte[12] = 0
        arrayOfByte[13] = -1
        return arrayOfByte
    }

    fun hexToBytes(inHex: String): ByteArray {
        var inHex = inHex
        var hexlen = inHex.length
        val result: ByteArray
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++
            result = ByteArray(hexlen / 2)
            inHex = "0$inHex"
        } else {
            //偶数
            result = ByteArray(hexlen / 2)
        }
        var j = 0
        var i = 0
        while (i < hexlen) {
            result[j] = hexToByte(inHex.substring(i, i + 2))
            j++
            i += 2
        }
        return result
    }

    fun hexToByte(inHex: String): Byte {
        return inHex.toInt(16).toByte()
    }

    fun fromUint32(paramInt: Int): ByteArray {
        return byteArrayOf(
            (paramInt and 0xFF).toByte(),
            (paramInt shr 8 and 0xFF).toByte(),
            (paramInt shr 16 and 0xFF).toByte(),
            (paramInt shr 24 and 0xFF).toByte()
        )
    }

    fun getAppBytes(paramInt: Int): ByteArray {
        val arrayOfByte1 = fromUint32(paramInt) //APP_ID
        val arrayOfByte2 =
            hexToBytes("030700570014000000A000020103000000000000000000000000002B670000")
        arrayOfByte2[arrayOfByte2.size - 4] = arrayOfByte1[0]
        arrayOfByte2[arrayOfByte2.size - 3] = arrayOfByte1[1]
        arrayOfByte2[arrayOfByte2.size - 2] = arrayOfByte1[2]
        arrayOfByte2[arrayOfByte2.size - 1] = arrayOfByte1[3]
        return arrayOfByte2
    }

    fun spiltBytes(original: ByteArray): ArrayList<ByteArray> {
        var length = original.size
        val result = ArrayList<ByteArray>()
        var index = 0
        var count = 0
        while (length > 0) if (count < 33) if (length >= 244) {
            val sub = ByteArray(244)
            System.arraycopy(original, index, sub, 0, 244)
            result.add(sub)
            index += 244
            length -= 244
            count++
        } else break else if (length >= 140) {
            val sub = ByteArray(140)
            System.arraycopy(original, index, sub, 0, 140)
            result.add(sub)
            index += 140
            length -= 140
            count = 0
        } else break
        if (length > 0) {
            val sub = ByteArray(length)
            System.arraycopy(original, index, sub, 0, length)
            result.add(sub)
        }
        return result
    }
}