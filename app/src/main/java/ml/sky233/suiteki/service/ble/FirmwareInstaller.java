package ml.sky233.suiteki.service.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.ArrayList;


import ml.sky233.suiteki.MainApplication;
import ml.sky233.suiteki.callback.ProgressCallback;
import ml.sky233.suiteki.util.BleLogTools;
import ml.sky233.suiteki.util.BytesUtils;
import ml.sky233.suiteki.util.MsgUtils;

@SuppressLint("MissingPermission")
public class FirmwareInstaller {

    public static byte[] d0 = {-48}, d1 = {-47}, d2 = {}, d3 = {-45, 1}, d5 = {-43}, d6 = {-42};
    public ArrayList<byte[]> doPerformBytes = new ArrayList<>(), finalPerformBytes = new ArrayList<>();
    int doPerform = 0;
    int finalPerform = 0;
    int dataIndex = 0;
    Context context;
    public BluetoothGatt mGatt;
    public byte[] firmware_bytes;
    public ArrayList<byte[]> spilt_byte;
    BleDevice bleDevice;
    Handler handler;
    ProgressCallback callback;

    public FirmwareInstaller(Context context, BluetoothGatt gatt, byte[] bytes, BleDevice bleDevice, Handler handler) {
        this.mGatt = gatt;
        this.firmware_bytes = bytes;
        this.bleDevice = bleDevice;
        this.context = context;
        this.handler = handler;
        BleService.setStatus(HuamiService.STATUS_BLE_INSTALLING);
        spilt_byte = BytesUtils.spiltBytes(bytes);
        d2 = BytesUtils.getD2(bytes, HuamiService.TYPE_D2_WATCHFACE);
        doPerformBytes.add(d0);
        doPerformBytes.add(d1);
        doPerformBytes.add(d2);
        doPerformBytes.add(d3);
        finalPerformBytes.add(d5);
        finalPerformBytes.add(d6);
        doPerform();
    }

    public FirmwareInstaller(Context context, BluetoothGatt gatt, byte[] bytes, BleDevice bleDevice, Handler handler, ProgressCallback callback) {
        this.mGatt = gatt;
        this.firmware_bytes = bytes;
        this.bleDevice = bleDevice;
        this.context = context;
        this.handler = handler;
        this.callback = callback;
        BleService.setStatus(HuamiService.STATUS_BLE_INSTALLING);
        spilt_byte = BytesUtils.spiltBytes(bytes);
        d2 = BytesUtils.getD2(bytes, HuamiService.TYPE_D2_WATCHFACE);
        doPerformBytes.add(d0);
        doPerformBytes.add(d1);
        doPerformBytes.add(d2);
        doPerformBytes.add(d3);
        finalPerformBytes.add(d5);
        finalPerformBytes.add(d6);
        doPerform();
    }

    public void setStatus(int status) {
        handler.sendMessage(MsgUtils.build(status, 1));
    }

    public void writeFirmware(byte[] bytes) {
        BleManager.getInstance().write(bleDevice,
                HuamiService.UUID_SERVICE_FIRMWARE.toString(),
                HuamiService.UUID_CHARACTERISTIC_FIRMWARE_WRITE.toString(),
                bytes, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int i, int i1, byte[] bytes) {
                        BleLogTools.onWriteSuccess(bytes);
                    }

                    @Override
                    public void onWriteFailure(BleException e) {
                        BleLogTools.onWriteFailure(bytes);
                    }
                });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeNotify(byte[] bytes) {
        BleManager.getInstance().write(bleDevice,
                HuamiService.UUID_SERVICE_FIRMWARE.toString(),
                HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY.toString(),
                bytes, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int i, int i1, byte[] bytes) {
                        BleLogTools.onWriteSuccess(bytes);
                    }

                    @Override
                    public void onWriteFailure(BleException e) {
                        BleLogTools.onWriteFailure(bytes);
                    }
                });
    }

    public void doPerform() {
        if (doPerform >= doPerformBytes.size()) return;
        writeNotify(doPerformBytes.get(doPerform));
        doPerform++;
    }

    public void install() {
        dataIndex = 0;
        writeBytes();
    }

    public void writeBytes() {
        new Thread(() -> {
            int remaining = Math.min(spilt_byte.size() - dataIndex, 34);
            for (int i = 0; i < remaining; i++) {
                if (callback != null)
                    callback.onProgressChange((int) (((double) dataIndex / (double) spilt_byte.size()) * (double) 100));
                else
                    context.sendBroadcast(new Intent()
                            .setAction(BleActions.ACTION_BLE_FIRMWARE_INSTALLING)
                            .putExtra("progress", (int) (((double) dataIndex / (double) spilt_byte.size()) * (double) 100)));
                byte[] data = spilt_byte.get(dataIndex);
                dataIndex++;
                writeFirmware(data);
            }
        }).start();
    }

    public void onCharacteristicChange(Intent intent) {
        Log.d(MainApplication.TAG, intent.getAction());
        if (intent.getAction().equals(BleActions.ACTION_BLE_FIRMWARE_NOTIFY)) {
            byte[] value = intent.getByteArrayExtra("value");
            switch (BytesUtils.bytesToHexStr(value)) {
                case "10D001050020"://D0
                case "10D10100"://D1
                case "10D2010000000000000000"://D2
                    doPerform();
                    break;
                case "10D203"://D2
                    MainApplication.mService.reAuth(() -> {
                        doPerform = 0;
                        doPerform();
                    });
                    return;
                case "10D347":
                    //空间不足
                    setStatus(HuamiService.STATUS_BLE_NORMAL);
                    context.sendBroadcast(new Intent()
                            .setAction(BleActions.ACTION_BLE_FIRMWARE_INSTALL_FAILURE));
                    return;
                case "10D301"://D3
                    install();
                    return;
                case "10D501"://D5
                    finalPerform();
                    return;
                case "10D601"://D6
                    //安装完毕
                    setStatus(HuamiService.STATUS_BLE_NORMAL);
                    context.sendBroadcast(new Intent()
                            .setAction(BleActions.ACTION_BLE_FIRMWARE_INSTALLED));
                    return;
                default:
                    if (value[0] == 16 && value[1] == -44) {//10D4
                        if (dataIndex >= spilt_byte.size())
                            finalPerform();
                        else
                            writeBytes();
                        break;
                    }
                    if (value[0] == 16 && value[1] == -46 && value[2] == 1) {
                        doPerform();
                        break;//10D201
                    }
                    //安装失败
                    return;
            }
        }
    }

    public void finalPerform() {
        if (finalPerform >= finalPerformBytes.size()) return;
        writeNotify(finalPerformBytes.get(finalPerform));
        finalPerform++;
    }

}
