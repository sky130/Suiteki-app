package ml.sky233.suiteki.service.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.clj.fastble.BleManager;

import java.util.Map;

import ml.sky233.suiteki.MainApplication;
import ml.sky233.suiteki.callback.ProgressCallback;
import ml.sky233.suiteki.callback.ReAuthCallback;
import ml.sky233.suiteki.callback.StatusCallback;
import ml.sky233.suiteki.service.ble.callback.BleGattCallback;

@SuppressLint("MissingPermission")
public class BleService extends Service {
    BleGattCallback bleGattCallback;
    FirmwareInstaller firmwareInstaller;

    public static BluetoothGatt mGatt;
    public static Context mContext;
    public static Map<String, BluetoothGattService> mServices;
    public static Map<String, BluetoothGattCharacteristic> mCharacteristics;
    public static int ble_status = HuamiService.STATUS_BLE_NOPE;
    public static StatusCallback callback;

    @Override
    public void onCreate() {
        super.onCreate();
        setStatus(HuamiService.STATUS_BLE_CONNECTING);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleActions.ACTION_BLE_AUTH_NOTIFY);
        intentFilter.addAction(BleActions.ACTION_BLE_NOTIFY_SUCCESS);
        intentFilter.addAction(BleActions.ACTION_BLE_FIRMWARE_NOTIFY);
        this.registerReceiver(mReceiver, intentFilter);//注册广播
    }

    public void connectDevice(String mac, String authKey) {
        bleGattCallback = new BleGattCallback(handler, this, authKey);
        BleManager.getInstance().connect(mac, bleGattCallback);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BleActions.ACTION_BLE_FIRMWARE_INSTALLED)) {
                firmwareInstaller = null;
                return;
            }
            bleGattCallback.onCharacteristicsChange(intent);
            if (firmwareInstaller != null)
                firmwareInstaller.onCharacteristicChange(intent);
            // 处理自定义广播
        }
    };

    public static void setCallback(StatusCallback callback) {
        BleService.callback = callback;
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setStatus((int) msg.obj);
                    break;
            }
        }
    };

    public boolean disconnectDevices() {
        BleManager.getInstance().disconnectAllDevice();
        return true;
    }

    public void writeFirmware(byte[] arrayBytes) {
        if (ble_status != HuamiService.STATUS_BLE_NORMAL) return;
        firmwareInstaller = new FirmwareInstaller(this, mGatt, arrayBytes, bleGattCallback.bleDevice, handler);
    }

    public void writeFirmware(byte[] arrayBytes, ProgressCallback callback) {
        if (ble_status != HuamiService.STATUS_BLE_NORMAL) return;
        firmwareInstaller = new FirmwareInstaller(this, mGatt, arrayBytes, bleGattCallback.bleDevice, handler, callback);
    }


    public static void setStatus(int status) {
        ble_status = status;
        if (callback != null)
            callback.onStatusChange(status);
        Log.d(MainApplication.TAG, String.valueOf(status));
    }

    public void reAuth(ReAuthCallback callback) {
        bleGattCallback.reAuth(callback);
    }

    public static void setServices(Map<String, BluetoothGattService> services) {
        mServices = services;
    }

    public static Map<String, BluetoothGattService> getServices() {
        return mServices;
    }

    public static void setCharacteristics(Map<String, BluetoothGattCharacteristic> characteristics) {
        mCharacteristics = characteristics;
    }

    public static Map<String, BluetoothGattCharacteristic> getCharacteristics() {
        return mCharacteristics;
    }

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        //解除注册
        disconnectDevices();
        unregisterReceiver(mReceiver);
        Log.d(MainApplication.TAG, "onDestroy");
        super.onDestroy();
    }

}
