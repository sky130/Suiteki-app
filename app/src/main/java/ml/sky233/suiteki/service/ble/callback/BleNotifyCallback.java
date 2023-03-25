package ml.sky233.suiteki.service.ble.callback;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clj.fastble.exception.BleException;

import java.util.UUID;

import ml.sky233.suiteki.MainApplication;
import ml.sky233.suiteki.service.ble.BleActions;
import ml.sky233.suiteki.service.ble.HuamiService;
import ml.sky233.suiteki.util.BytesUtils;

public class BleNotifyCallback extends com.clj.fastble.callback.BleNotifyCallback {
    Context context;
    UUID uuid;

    public BleNotifyCallback(Context context, UUID uuid) {
        this.uuid = uuid;
        this.context = context;
    }

    @Override
    public void onNotifySuccess() {
        context.sendBroadcast(new Intent()
                .setAction(BleActions.ACTION_BLE_NOTIFY_SUCCESS)
                .putExtra("value", true));
    }

    @Override
    public void onNotifyFailure(BleException e) {

    }

    @Override
    public void onCharacteristicChanged(byte[] value) {
        Log.d(MainApplication.TAG, "UUID:" + uuid.toString() + "\n" + "value:" + BytesUtils.bytesToHexStr(value));
        if (uuid.equals(HuamiService.UUID_CHARACTERISTIC_AUTH_NOTIFY))
            context.sendBroadcast(new Intent()
                    .setAction(BleActions.ACTION_BLE_AUTH_NOTIFY)
                    .putExtra("value", value));
        else if (uuid.equals(HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY))
            context.sendBroadcast(new Intent()
                    .setAction(BleActions.ACTION_BLE_FIRMWARE_NOTIFY)
                    .putExtra("value", value));
    }
}