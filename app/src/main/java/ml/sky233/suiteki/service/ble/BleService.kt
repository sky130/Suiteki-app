package ml.sky233.suiteki.service.ble

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import com.clj.fastble.BleManager
import ml.sky233.suiteki.service.ble.callback.BleGattCallback
import ml.sky233.suiteki.callback.ProgressCallback
import ml.sky233.suiteki.callback.ReAuthCallback
import ml.sky233.suiteki.callback.StatusCallback

@SuppressLint("MissingPermission")
class BleService : Service() {
    var bleGattCallback: BleGattCallback? = null
    var firmwareInstaller: FirmwareInstaller? = null
    override fun onCreate() {
        super.onCreate()
        setStatus(HuamiService.STATUS_BLE_CONNECTING)
        val intentFilter = IntentFilter()
        intentFilter.addAction(BleActions.ACTION_BLE_AUTH_NOTIFY)
        intentFilter.addAction(BleActions.ACTION_BLE_NOTIFY_SUCCESS)
        intentFilter.addAction(BleActions.ACTION_BLE_FIRMWARE_NOTIFY)
        this.registerReceiver(mReceiver, intentFilter) //注册广播
    }

    fun connectDevice(mac: String?, authKey: String) {
        bleGattCallback = BleGattCallback(handler, this, authKey)
        BleManager.getInstance().connect(mac, bleGattCallback)
    }

    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getAction() == BleActions.ACTION_BLE_FIRMWARE_INSTALLED) {
                firmwareInstaller = null
                return
            }
            bleGattCallback!!.onCharacteristicsChange(intent)
            if (firmwareInstaller != null) firmwareInstaller!!.onCharacteristicChange(intent)
            // 处理自定义广播
        }
    }
    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> setStatus(msg.obj as Int)
            }
        }
    }

    fun disconnectDevices(): Boolean {
        BleManager.getInstance().disconnectAllDevice()
        return true
    }

    fun writeFirmware(arrayBytes: ByteArray) {
        if (ble_status != HuamiService.STATUS_BLE_NORMAL) return
        Thread {
            firmwareInstaller =
                FirmwareInstaller(mGatt!!, arrayBytes, bleGattCallback!!.bleDevice!!, handler)
        }.start()
    }

    fun writeFirmware(arrayBytes: ByteArray, callback: ProgressCallback) {
        if (ble_status != HuamiService.STATUS_BLE_NORMAL) return
        Thread {
            firmwareInstaller = FirmwareInstaller(
                mGatt!!, arrayBytes, bleGattCallback!!.bleDevice!!, handler, callback
            )
        }.start()
    }

    fun reAuth(callback: ReAuthCallback) {
        bleGattCallback!!.reAuth(callback)
    }

    inner class LocalBinder : Binder() {
        val service: BleService
            get() = this@BleService
    }

    private val mBinder: IBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        //解除注册
        disconnectDevices()
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    companion object {
        var mGatt: BluetoothGatt? = null
        var ble_status = HuamiService.STATUS_BLE_NOPE
        var callback: StatusCallback? = null
        fun setStatus(status: Int) {
            ble_status = status
            callback?.onStatusChange(status)
        }
    }
}