package ml.sky233.suiteki.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ml.sky233.choseki.sqlite.SuitekiDatabaseHelper
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.util.TextUtils.fixMac

class SuitekiDataPutter(context: Context) {
    private val helper: SuitekiDatabaseHelper = SuitekiDatabaseHelper(context)
    private val database: SQLiteDatabase = helper.writableDatabase

    fun put(device: Device) {
        val value = ContentValues().apply {
            put("name", device.name)
            put("auth_key", device.authKey)
            put("mac", device.mac)
            put("type", device.type)
            put("app", device.app)
        }
        database.insert("device", null, value)
    }

    fun insert(device: Device) {
        val value = ContentValues().apply {
            put("name", device.name)
            put("auth_key", device.authKey)
            put("mac", device.mac.fixMac())
            put("type", device.type)
            put("app", device.app)
        }
        database.update("device", value, "mac=${device.mac.fixMac()}", null)
    }

    fun close() {
        database.close()
        helper.close()
    }

    fun use(block: SuitekiDataPutter.() -> Unit) {
        block()
        close()
        SuitekiDataGetter.init()
    }
}