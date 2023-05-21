package ml.sky233.suiteki.util

import android.content.SharedPreferences
import android.preference.PreferenceManager
import ml.sky233.suiteki.MainApplication.Companion.context

object SettingUtils {
    val DEVICE = "device"
    private val sharedPreferences: SharedPreferences? =
        PreferenceManager.getDefaultSharedPreferences(context);


    fun getString(name: String?): String? {
        return try {
            val preferences = sharedPreferences
            preferences!!.getString(name, "")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun setInt(name: String?, value: Int) {
        val preferences = sharedPreferences
        preferences!!.edit().putInt(name, value).apply()
    }

    fun getInt(name: String?): Int {
        return try {
            val preferences = sharedPreferences
            preferences!!.getInt(name, -1)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun setString(name: String?, value: String?) {
        val preferences = sharedPreferences
        preferences!!.edit().putString(name, value).apply()
    }

    fun getBoolean(name: String?): Boolean {
        return try {
            val preferences = sharedPreferences
            preferences!!.getBoolean(name, false)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun setBoolean(name: String?, value: Boolean) {
        try {
            val preferences = sharedPreferences
            preferences!!.edit().putBoolean(name, value).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}