package ml.sky233.suiteki.service.ble;

import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.Arrays;

import ml.sky233.suiteki.MainApplication;
import ml.sky233.suiteki.util.BytesUtils;

public class BleDeviceInfo {
    BleDevice bleDevice;
    public String version = "", sn_code = "", device_name = "";
    int battery_level = -1;

    public BleDeviceInfo(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
        new Thread(() -> {
            BleManager.getInstance().read(this.bleDevice, HuamiService.UUID_SERVICE_DEVICE_INFORMATION.toString(), HuamiService.UUID_CHARACTERISTIC_ZEPP_OS_VERSION.toString(), new GetVersionCallback());
            sleep();
            BleManager.getInstance().read(this.bleDevice, HuamiService.UUID_SERVICE_DEVICE_INFORMATION.toString(), HuamiService.UUID_CHARACTERISTIC_SERIAL_NUMBER.toString(), new GetSNCallback());
            sleep();
            BleManager.getInstance().read(this.bleDevice, HuamiService.UUID_SERVICE_GENERIC_ACCESS.toString(), HuamiService.UUID_CHARACTERISTIC_DEVICE_NAME.toString(), new GetDeviceNameCallback());
            sleep();
            BleManager.getInstance().read(this.bleDevice, HuamiService.UUID_SERVICE_DEVICE_BATTERY.toString(), HuamiService.UUID_CHARACTERISTIC_BATTERY_LEVEL.toString(), new GetBatteryCallback());
        }).start();
    }

    public void sleep() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public class GetVersionCallback extends BleReadCallback {
        @Override
        public void onReadSuccess(byte[] data) {
            version = new String(data);
            Log.d(MainApplication.TAG, "version:" + version);
        }

        @Override
        public void onReadFailure(BleException exception) {
            // 读特征值数据失败
            Log.d(MainApplication.TAG, "error");

        }
    }

    public class GetSNCallback extends BleReadCallback {
        @Override
        public void onReadSuccess(byte[] data) {
            sn_code = new String(data);
            Log.d(MainApplication.TAG, "sn_code:" + sn_code);

        }

        @Override
        public void onReadFailure(BleException exception) {
            // 读特征值数据失败
            Log.d(MainApplication.TAG, "error");

        }
    }

    public class GetDeviceNameCallback extends BleReadCallback {
        @Override
        public void onReadSuccess(byte[] data) {
            device_name = new String(data);
            Log.d(MainApplication.TAG, "device_name:" + device_name);
        }

        @Override
        public void onReadFailure(BleException exception) {
            // 读特征值数据失败
            Log.d(MainApplication.TAG, "error");

        }
    }

    public class GetBatteryCallback extends BleReadCallback {
        @Override
        public void onReadSuccess(byte[] data) {
            battery_level = BytesUtils.bytesToInt(data);
            Log.d(MainApplication.TAG, "battery_level:" + battery_level);
        }

        @Override
        public void onReadFailure(BleException exception) {
            // 读特征值数据失败
            Log.d(MainApplication.TAG, "error");

        }
    }


}
