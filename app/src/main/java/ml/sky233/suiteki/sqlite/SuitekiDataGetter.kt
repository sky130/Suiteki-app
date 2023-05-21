package ml.sky233.suiteki.sqlite

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ml.sky233.choseki.sqlite.SuitekiDatabaseHelper
import ml.sky233.suiteki.MainApplication.Companion.context
import ml.sky233.suiteki.sqlite.bean.Device

object SuitekiDataGetter {


    @JvmStatic
    val device: ArrayList<Device> = ArrayList()


    fun init() {
        SuitekiDatabaseHelper(context).apply {
            this.readableDatabase.apply {
                initDevice()
                this.close()
            }
            close()
        }
    }


    @SuppressLint("Recycle", "Range")
    fun SQLiteDatabase.initDevice() {
        val query = "SELECT * FROM device"
        val cursor = this.rawQuery(query, null)
        device.clear()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val mac = cursor.getString(cursor.getColumnIndex("mac"))
            val authKey = cursor.getString(cursor.getColumnIndex("auth_key"))
            val type = cursor.getString(cursor.getColumnIndex("type"))
            val app = cursor.getString(cursor.getColumnIndex("app"))

            device.add(Device(id, name, mac, authKey, type,app))
        }
    }

}