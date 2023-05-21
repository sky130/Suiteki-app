@file:Suppress("DEPRECATION")

package ml.sky233.suiteki.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.view.View
import androidx.annotation.RequiresApi
import ml.sky233.suiteki.util.PermissionUtils.buildUri
import ml.sky233.suiteki.util.PermissionUtils.isGetUriPermission

inline fun <reified T> Activity.startActivity(block: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent)
}

inline fun <reified T> Activity.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

fun Activity.barTextToBlack() {
    if (this.applicationContext.resources.configuration.uiMode != 0x21)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
}

@RequiresApi(api = Build.VERSION_CODES.O)
fun Activity.startFor(pn: String, requestCode: Int) {
    if (isGetUriPermission(pn))
        return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        return
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION

        // 使用标准 API 常量，避免使用字符串常量。
        putExtra("android.provider.extra.SHOW_ADVANCED", true)
        putExtra("android.content.extra.SHOW_ADVANCED", true)
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, buildUri(pn))
    }
    startActivityForResult(intent, requestCode) //开始授权
}