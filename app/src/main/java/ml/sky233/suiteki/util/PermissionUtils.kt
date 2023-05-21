@file:Suppress("DEPRECATION")

package ml.sky233.suiteki.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ml.sky233.suiteki.MainApplication.Companion.context
import ml.sky233.suiteki.util.TextUtils.lookFor

object PermissionUtils {
    val MI_HEALTH_PACKAEG_NAME = "com.mi.health"

    fun buildUri(path: String): Uri {
        return DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents", "primary:Android/data/$path"
        )
    }

    fun buildUri2(path: String): Uri {
        return DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:Android/data/" + path.replace("/storage/emulated/0/Android/data/", "")
        )
    }

    fun isInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun isGetUriPermission(pn: String): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            return true
        if (!isInstalled(pn)) {
            return true
        }
        val pList = context.contentResolver.persistedUriPermissions
        for (uriPermission in pList) {
            if (uriPermission.uri.path!!.lookFor(pn)) return true
        }
        return false
    }

    fun requestWritePermissions(activity: Activity, requestCode: Int) {
        if (!isGetWritePermission())
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), requestCode
            )
    }

    @SuppressLint("WrongConstant")
    fun saveUriPermission(data: Intent?) {
        if (data != null)
            context.contentResolver.takePersistableUriPermission(
                data.data!!,
                data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            ) //关键是这里，这个就是保存这个目录的访问权限
    }

    @SuppressLint("InlinedApi")
    fun requestBlePermissions(activity: Activity, requestCode: Int) {
        if (!isGetBlePermission())
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ), requestCode
            )
    }

    fun isGetBlePermission(): Boolean {
        if (Build.VERSION.SDK_INT <= 31) return true
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) return false
        return true
    }

    fun isGetWritePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) return false
        return true
    }

    fun isGetAllPermission(): Boolean {
        if (!isGetWritePermission()) return false
        if (!isGetBlePermission()) return false
        return isGetUriPermission(MI_HEALTH_PACKAEG_NAME)
    }

    fun getPermissionSubTitle(): String {
        return ArrayList<String>().apply {
            if (!isGetWritePermission()) add("读写权限")
            if (!isGetBlePermission()) add("蓝牙权限")
            if (!isGetUriPermission(MI_HEALTH_PACKAEG_NAME)) add("Data权限")
        }.joinToString(separator = "|")
    }
}