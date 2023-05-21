package ml.sky233.suiteki.service.ble.callback

import android.content.Context
import android.content.Intent
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.exception.BleException
import ml.sky233.suiteki.service.ble.BleActions
import ml.sky233.suiteki.service.ble.HuamiService
import java.util.UUID

class BleNotifyCallback(var context: Context, var uuid: UUID) : BleNotifyCallback() {
    override fun onNotifySuccess() {
        context.sendBroadcast(
            Intent()
                .setAction(BleActions.ACTION_BLE_NOTIFY_SUCCESS)
                .putExtra("value", true)
        )
    }

    override fun onNotifyFailure(e: BleException) {}
    override fun onCharacteristicChanged(value: ByteArray) {
        if (uuid == HuamiService.UUID_CHARACTERISTIC_AUTH_NOTIFY) context.sendBroadcast(
            Intent()
                .setAction(BleActions.ACTION_BLE_AUTH_NOTIFY)
                .putExtra("value", value)
        ) else if (uuid == HuamiService.UUID_CHARACTERISTIC_FIRMWARE_NOTIFY) context.sendBroadcast(
            Intent()
                .setAction(BleActions.ACTION_BLE_FIRMWARE_NOTIFY)
                .putExtra("value", value)
        )
    }
}