package ml.sky233.suiteki.service.ble

import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import ml.sky233.suiteki.util.BytesUtils

class BleDeviceInfo(bleDevice: BleDevice) {
    var bleDevice: BleDevice
    var version = ""
    var sn_code = ""
    var device_name = ""
    var battery_level = -1

    init {
        this.bleDevice = bleDevice
        Thread {
            BleManager.getInstance().read(
                this.bleDevice,
                HuamiService.UUID_SERVICE_DEVICE_INFORMATION.toString(),
                HuamiService.UUID_CHARACTERISTIC_ZEPP_OS_VERSION.toString(),
                GetVersionCallback()
            )
            sleep()
            BleManager.getInstance().read(
                this.bleDevice,
                HuamiService.UUID_SERVICE_DEVICE_INFORMATION.toString(),
                HuamiService.UUID_CHARACTERISTIC_SERIAL_NUMBER.toString(),
                GetSNCallback()
            )
            sleep()
            BleManager.getInstance().read(
                this.bleDevice,
                HuamiService.UUID_SERVICE_GENERIC_ACCESS.toString(),
                HuamiService.UUID_CHARACTERISTIC_DEVICE_NAME.toString(),
                GetDeviceNameCallback()
            )
            sleep()
            BleManager.getInstance().read(
                this.bleDevice,
                HuamiService.UUID_SERVICE_DEVICE_BATTERY.toString(),
                HuamiService.UUID_CHARACTERISTIC_BATTERY_LEVEL.toString(),
                GetBatteryCallback()
            )
        }.start()
    }

    fun sleep() {
        try {
            Thread.sleep(300)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    inner class GetVersionCallback : BleReadCallback() {
        override fun onReadSuccess(data: ByteArray) {
            version = String(data)
        }

        override fun onReadFailure(exception: BleException) {}
    }

    inner class GetSNCallback : BleReadCallback() {
        override fun onReadSuccess(data: ByteArray) {
            sn_code = String(data)
        }

        override fun onReadFailure(exception: BleException) {}
    }

    inner class GetDeviceNameCallback : BleReadCallback() {
        override fun onReadSuccess(data: ByteArray) {
            device_name = String(data)
        }

        override fun onReadFailure(exception: BleException) {}
    }

    inner class GetBatteryCallback : BleReadCallback() {
        override fun onReadSuccess(data: ByteArray) {
            battery_level = BytesUtils.bytesToInt(data)
        }

        override fun onReadFailure(exception: BleException) {}
    }
}