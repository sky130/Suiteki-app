package ml.sky233.suiteki.service.ble.callback;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ml.sky233.suiteki.MainApplication;
import ml.sky233.suiteki.service.ble.BleActions;
import ml.sky233.suiteki.service.ble.BleService;
import ml.sky233.suiteki.util.ArrayUtils;
import ml.sky233.suiteki.util.BleLogTools;
import ml.sky233.suiteki.util.BytesUtils;
import ml.sky233.suiteki.util.CryptoUtils;
import ml.sky233.suiteki.util.ECDH_B163;
import ml.sky233.suiteki.util.MsgBuilder;
import ml.sky233.suiteki.service.ble.HuamiService;

@SuppressLint("MissingPermission")
public class GattCallback extends BluetoothGattCallback {

    byte writeHandle;
    byte[] privateEC = new byte[24];
    byte[] publicEC;
    byte[] remotePublicEC = new byte[48];
    final byte[] remoteRandom = new byte[16];
    byte[] sharedEC;
    final byte[] finalSharedSessionAES = new byte[16];
    int encryptedSequenceNr;
    byte[] authKey;

    Byte currentHandle;
    int currentType;
    int currentLength;
    ByteBuffer reassemblyBuffer;
    BluetoothGatt mGatt;

    Context context;

    public GattCallback(String authKey, Context context) {
        this.authKey = getSecretKey(authKey);//设置密钥
        this.context = context;
    }

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(MainApplication.TAG, BytesUtils.bytesToHexStr(characteristic.getValue()));
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        mGatt = gatt;
        switch (newState) {
            case BluetoothProfile.STATE_CONNECTED:
                BleService.setStatus(HuamiService.STATUS_BLE_CONNECTED);
                gatt.requestMtu(512);
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                if (BleService.ble_status == HuamiService.STATUS_BLE_CONNECTING)
                    BleService.setStatus(HuamiService.STATUS_BLE_CONNECT_FAILURE);
                else BleService.setStatus(HuamiService.STATUS_BLE_DISCONNECT);
                break;
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        gatt.discoverServices();
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Map<String, BluetoothGattCharacteristic> characteristics = BleService.getCharacteristics();
                    setCharacteristicNotification(characteristics.get("firmware_notify_characteristic"));
                    setCharacteristicNotification(characteristics.get("auth_notify_characteristic"));
                    doPerform();
                    break;
            }
        }
    };
    boolean mtu = false;

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BluetoothGattService miband_service = gatt.getService(HuamiService.UUID_SERVICE_MIBAND_SERVICE);
            BluetoothGattService firmware_service = gatt.getService(HuamiService.UUID_SERVICE_FIRMWARE);

            BluetoothGattCharacteristic app_characteristic = miband_service.getCharacteristic(HuamiService.UUID_CHARACTERISTIC_APP);
            BluetoothGattCharacteristic auth_write_characteristic = miband_service.getCharacteristic(HuamiService.UUID_CHARACTERISTIC_AUTH_WRITE);
            BluetoothGattCharacteristic auth_notify_characteristic = miband_service.getCharacteristic(HuamiService.UUID_CHARACTERISTIC_AUTH_NOTIFY);
            BluetoothGattCharacteristic firmware_write_characteristic = firmware_service.getCharacteristic(HuamiService.UUID_CHARACTERISTIC_FIRMWARE_WRITE);
            BluetoothGattCharacteristic firmware_notify_characteristic = firmware_service.getCharacteristic(HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY);

            Map<String, BluetoothGattService> services = new HashMap<>();
            services.put("miband_service", miband_service);
            services.put("firmware_service", firmware_service);
            BleService.setServices(services);

            Map<String, BluetoothGattCharacteristic> characteristics = new HashMap<>();
            characteristics.put("app_characteristic", app_characteristic);
            characteristics.put("auth_write_characteristic", auth_write_characteristic);
            characteristics.put("auth_notify_characteristic", auth_notify_characteristic);
            characteristics.put("firmware_write_characteristic", firmware_write_characteristic);
            characteristics.put("firmware_notify_characteristic", firmware_notify_characteristic);
            BleService.setCharacteristics(characteristics);

//            gatt.setCharacteristicNotification(firmware_notify_characteristic, true);
//            BluetoothGattDescriptor descriptor_firmware = firmware_notify_characteristic.getDescriptor(HuamiService.UUID_CLIENT_CHARACTERISTIC_CONFIG);
//            descriptor_firmware.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(descriptor_firmware);
//
//            gatt.setCharacteristicNotification(auth_notify_characteristic, true);
//            BluetoothGattDescriptor descriptor_auth = auth_notify_characteristic.getDescriptor(HuamiService.UUID_CLIENT_CHARACTERISTIC_CONFIG);
//            descriptor_auth.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(descriptor_auth);
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            handler.sendMessage(MsgBuilder.build(1));
        }

    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        if (!mGatt.setCharacteristicNotification(characteristic, true))
            return false;
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(HuamiService.UUID_CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return mGatt.writeDescriptor(descriptor);
    }


    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        Log.d(MainApplication.TAG, "onCharacteristicChanged!");
        byte[] value = characteristic.getValue();
        if (HuamiService.UUID_CHARACTERISTIC_AUTH_NOTIFY.equals(characteristic.getUuid())) {
            onAuthNotify(value);
        } else if (HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY.equals(characteristic.getUuid())) {
            context.sendBroadcast(new Intent().setAction(BleActions.ACTION_BLE_FIRMWARE_NOTIFY).putExtra("value", value));
        }
    }


    public void onAuthNotify(byte[] bytes) {
        if (bytes.length <= 1 || bytes[0] != 0x03) return;
        decode(bytes);
    }

    public void doPerform() {
        BleService.setStatus(HuamiService.STATUS_BLE_AUTHING);
        new Random().nextBytes(privateEC);
        publicEC = ECDH_B163.ecdh_generate_public(privateEC);
        byte[] sendPubkeyCommand = new byte[48 + 4];
        sendPubkeyCommand[0] = 0x04;
        sendPubkeyCommand[1] = 0x02;
        sendPubkeyCommand[2] = 0x00;
        sendPubkeyCommand[3] = 0x02;
        System.arraycopy(publicEC, 0, sendPubkeyCommand, 4, 48);
        write((short) 0x0082, sendPubkeyCommand, true, false);
    }

    public void decode(final byte[] data) {
        int i = 0;
        if (data[i++] != 0x03) {
            return;
        }
        final byte flags = data[i++];
        final boolean encrypted = ((flags & 0x08) == 0x08);
        final boolean firstChunk = ((flags & 0x01) == 0x01);
        final boolean lastChunk = ((flags & 0x02) == 0x02);
        i++;
        final byte handle = data[i++];
        if (currentHandle != null && currentHandle != handle) {
            return;
        }
        byte count = data[i++];
        if (firstChunk) { // beginning
            int full_length = (data[i++] & 0xff) | ((data[i++] & 0xff) << 8) | ((data[i++] & 0xff) << 16) | ((data[i++] & 0xff) << 24);
            currentLength = full_length;
            if (encrypted) {
                int encrypted_length = full_length + 8;
                int overflow = encrypted_length % 16;
                if (overflow > 0) {
                    encrypted_length += (16 - overflow);
                }
                full_length = encrypted_length;
            }
            reassemblyBuffer = ByteBuffer.allocate(full_length);
            currentType = (data[i++] & 0xff) | ((data[i++] & 0xff) << 8);
            currentHandle = handle;
        }
        reassemblyBuffer.put(data, i, data.length - i);
        if (lastChunk) { // end
            byte[] buf = reassemblyBuffer.array();
            if (encrypted) {
                if (authKey == null) {
                    currentHandle = null;
                    currentType = 0;
                    return;
                }
                byte[] messagekey = new byte[16];
                for (int j = 0; j < 16; j++) {
                    messagekey[j] = (byte) (authKey[j] ^ handle);
                }
                try {
                    buf = CryptoUtils.decryptAES(buf, messagekey);
                    buf = ArrayUtils.subarray(buf, 0, currentLength);
                } catch (Exception e) {
                    e.printStackTrace();
                    currentHandle = null;
                    currentType = 0;
                    return;
                }
            }
            try {
                handle2021Payload((short) currentType, buf);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            currentHandle = null;
            currentType = 0;
        }
    }

    public byte[] getSecretKey(String authKey) {
        byte[] authKeyBytes = new byte[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45};
        if (authKey != null && !authKey.isEmpty()) {
            byte[] srcBytes = authKey.trim().getBytes();
            if (authKey.length() == 34 && authKey.substring(0, 2).equals("0x")) {
                srcBytes = BytesUtils.hexToBytes(authKey.substring(2));
            }
            System.arraycopy(srcBytes, 0, authKeyBytes, 0, Math.min(srcBytes.length, 16));
        }
        return authKeyBytes;
    }

    public void handle2021Payload(final short type, final byte[] payload) {
        if (payload[0] == 0x10 && payload[1] == 0x04 && payload[2] == 0x01) {
            System.arraycopy(payload, 3, remoteRandom, 0, 16);
            System.arraycopy(payload, 19, remotePublicEC, 0, 48);
            sharedEC = ECDH_B163.ecdh_generate_shared(privateEC, remotePublicEC);
            int encryptedSequenceNumber = (sharedEC[0] & 0xff) | ((sharedEC[1] & 0xff) << 8) | ((sharedEC[2] & 0xff) << 16) | ((sharedEC[3] & 0xff) << 24);
            byte[] secretKey = authKey;
            for (int i = 0; i < 16; i++) {
                finalSharedSessionAES[i] = (byte) (sharedEC[i + 8] ^ secretKey[i]);
            }
            this.encryptedSequenceNr = encryptedSequenceNumber;
            authKey = finalSharedSessionAES;
            try {
                byte[] encryptedRandom1 = CryptoUtils.encryptAES(remoteRandom, secretKey);
                byte[] encryptedRandom2 = CryptoUtils.encryptAES(remoteRandom, finalSharedSessionAES);
                if (encryptedRandom1.length == 16 && encryptedRandom2.length == 16) {
                    byte[] command = new byte[33];
                    command[0] = 0x05;
                    System.arraycopy(encryptedRandom1, 0, command, 1, 16);
                    System.arraycopy(encryptedRandom2, 0, command, 17, 16);
                    write((short) 0x0082, command, true, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (payload[0] == 0x10 && payload[1] == 0x05 && payload[2] == 0x01) {
            BleLogTools.onAuthSuccess();
            Log.d(MainApplication.TAG, "onAuthSuccess");
            BleService.setStatus(HuamiService.STATUS_BLE_AUTHED);
            BleService.setStatus(HuamiService.STATUS_BLE_CONNECTED);
            BleService.setStatus(HuamiService.STATUS_BLE_NORMAL);
            return;
        } else {
            return;
        }
    }

    public void write(final short type, byte[] data, final boolean extended_flags, final boolean encrypt) {
        if (encrypt && authKey == null) return;

        writeHandle++;
        int remaining = data.length;
        int length = data.length;
        byte count = 0;
        int header_size = 10;

        if (extended_flags) {
            header_size++;
        }

        if (extended_flags && encrypt) {
            byte[] messagekey = new byte[16];
            for (int i = 0; i < 16; i++) {
                messagekey[i] = (byte) (authKey[i] ^ writeHandle);
            }
            int encrypted_length = length + 8;
            int overflow = encrypted_length % 16;
            if (overflow > 0) {
                encrypted_length += (16 - overflow);
            }

            byte[] encryptable_payload = new byte[encrypted_length];
            System.arraycopy(data, 0, encryptable_payload, 0, length);
            encryptable_payload[length] = (byte) (encryptedSequenceNr & 0xff);
            encryptable_payload[length + 1] = (byte) ((encryptedSequenceNr >> 8) & 0xff);
            encryptable_payload[length + 2] = (byte) ((encryptedSequenceNr >> 16) & 0xff);
            encryptable_payload[length + 3] = (byte) ((encryptedSequenceNr >> 24) & 0xff);
            encryptedSequenceNr++;
            int checksum = BytesUtils.getCRC32(encryptable_payload, 0, length + 4);
            encryptable_payload[length + 4] = (byte) (checksum & 0xff);
            encryptable_payload[length + 5] = (byte) ((checksum >> 8) & 0xff);
            encryptable_payload[length + 6] = (byte) ((checksum >> 16) & 0xff);
            encryptable_payload[length + 7] = (byte) ((checksum >> 24) & 0xff);
            remaining = encrypted_length;
            try {
                data = CryptoUtils.encryptAES(encryptable_payload, messagekey);
            } catch (Exception e) {
                return;
            }
        }

        while (remaining > 0) {
            int MAX_CHUNKLENGTH = 20 - header_size;
            int copyBytes = Math.min(remaining, MAX_CHUNKLENGTH);
            byte[] chunk = new byte[copyBytes + header_size];

            byte flags = 0;
            if (encrypt) {
                flags |= 0x08;
            }
            if (count == 0) {
                flags |= 0x01;
                int i = 4;
                if (extended_flags) {
                    i++;
                }
                chunk[i++] = (byte) (length & 0xff);
                chunk[i++] = (byte) ((length >> 8) & 0xff);
                chunk[i++] = (byte) ((length >> 16) & 0xff);
                chunk[i++] = (byte) ((length >> 24) & 0xff);
                chunk[i++] = (byte) (type & 0xff);
                chunk[i] = (byte) ((type >> 8) & 0xff);
            }
            if (remaining <= MAX_CHUNKLENGTH) {
                flags |= 0x06; // last chunk?
            }
            chunk[0] = 0x03;
            chunk[1] = flags;
            if (extended_flags) {
                chunk[2] = 0;
                chunk[3] = writeHandle;
                chunk[4] = count;
            } else {
                chunk[2] = writeHandle;
                chunk[3] = count;
            }

            System.arraycopy(data, data.length - remaining, chunk, header_size, copyBytes);
            writeAuth(chunk);
            remaining -= copyBytes;
            header_size = 4;
            if (extended_flags) {
                header_size++;
            }
            count++;
        }
    }

    public void writeAuth(byte[] bytes) {
        if (mGatt == null) return;
        BluetoothGattCharacteristic characteristic = BleService.getCharacteristics().get("auth_write_characteristic");
        if (characteristic == null) return;
        characteristic.setValue(bytes);
        mGatt.writeCharacteristic(characteristic);
    }

}
