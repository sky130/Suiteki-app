package ml.sky233.suiteki.util;

import static ml.sky233.suiteki.MainApplication.TAG;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

public class BytesUtils {
    public static final int WATCHFACE = 0, APP = 1, FIRMWARE = 2;

    public static int hexToInt(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static String bytesToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int bytesToInt(byte[] byteArray) {
        if (byteArray == null) {
            return 0;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return Integer.parseInt(new String(hexChars), 16);
    }

    public static byte[] intToBytes(int a) {
        return hexToBytes(String.format("%06x", a));
    }

    public static byte[] LongToBytes(Long a) {
        return hexToBytes(String.format("%08x", a));
    }

    public static byte[] getD2(int data_length, long CRC) {
        byte[] a1 = {-46, 8}, a2 = BytesUtils.intToBytes(data_length), a3 = {0}, a4 = BytesUtils.LongToBytes(CRC), a5 = {0, 32, 0, -1};
        ByteBuffer byteBuffer = ByteBuffer.allocate(a1.length + a2.length + a3.length + a4.length + a5.length);
        byteBuffer.put(a1).put(a2).put(a3).put(a4).put(a5);
        return byteBuffer.array();
    }

    public static int getCRC32(byte[] seq, int offset, int length) {
        CRC32 crc = new CRC32();
        crc.update(seq, offset, length);
        return (int) (crc.getValue());
    }

    public static byte[] getD2(byte[] bytes, byte mode) {
        byte[] arrayOfByte = new byte[14];
        arrayOfByte[0] = -46;
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
        arrayOfByte[1] = mode;
        byte[] arrayOfByte1 = fromUint32(bytes.length);
        arrayOfByte[2] = arrayOfByte1[0];
        arrayOfByte[3] = arrayOfByte1[1];
        arrayOfByte[4] = arrayOfByte1[2];
        arrayOfByte[5] = arrayOfByte1[3];
        CRC32 crc = new CRC32();
        crc.update(bytes);
        arrayOfByte1 = fromUint32((int) crc.getValue());
        arrayOfByte[6] = arrayOfByte1[0];
        arrayOfByte[7] = arrayOfByte1[1];
        arrayOfByte[8] = arrayOfByte1[2];
        arrayOfByte[9] = arrayOfByte1[3];
        arrayOfByte[10] = 0;
        arrayOfByte[11] = 32;
        arrayOfByte[12] = 0;
        arrayOfByte[13] = -1;
        return arrayOfByte;
    }

    public static byte[] hexToBytes(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }


    public static byte[] fromUint32(int paramInt) {
        return new byte[]{(byte) (paramInt & 0xFF), (byte) (paramInt >> 8 & 0xFF), (byte) (paramInt >> 16 & 0xFF), (byte) (paramInt >> 24 & 0xFF)};
    }

    public static byte[] getAppBytes(int paramInt) {
        byte[] arrayOfByte1 = BytesUtils.fromUint32(paramInt);//APP_ID
        byte[] arrayOfByte2 = BytesUtils.hexToBytes("030700570014000000A000020103000000000000000000000000002B670000");
        Log.d(TAG, Arrays.toString(arrayOfByte2));
        arrayOfByte2[arrayOfByte2.length - 4] = arrayOfByte1[0];
        arrayOfByte2[arrayOfByte2.length - 3] = arrayOfByte1[1];
        arrayOfByte2[arrayOfByte2.length - 2] = arrayOfByte1[2];
        arrayOfByte2[arrayOfByte2.length - 1] = arrayOfByte1[3];
        return arrayOfByte2;
    }


    public static ArrayList<byte[]> spiltBytes(byte[] original) {
        int length = original.length;
        ArrayList<byte[]> result = new ArrayList<>();
        int index = 0;
        int count = 0;
        while (length > 0)
            if (count < 33)
                if (length >= 244) {
                    byte[] sub = new byte[244];
                    System.arraycopy(original, index, sub, 0, 244);
                    result.add(sub);
                    index += 244;
                    length -= 244;
                    count++;
                } else
                    break;
            else if (length >= 140) {
                byte[] sub = new byte[140];
                System.arraycopy(original, index, sub, 0, 140);
                result.add(sub);
                index += 140;
                length -= 140;
                count = 0;
            } else
                break;
        if (length > 0) {
            byte[] sub = new byte[length];
            System.arraycopy(original, index, sub, 0, length);
            result.add(sub);
        }
        return result;
    }
}
