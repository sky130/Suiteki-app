package ml.sky233.suiteki.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ml.sky233.SuitekiObject
import ml.sky233.suiteki.R
import ml.sky233.suiteki.adapter.AddDeviceAdapter
import ml.sky233.suiteki.sqlite.SuitekiDataPutter
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.util.SettingUtils
import ml.sky233.suiteki.util.TextUtils.toast


@SuppressLint("InflateParams", "NotifyDataSetChanged")
class CustomDeviceTypeDialog(
    context: Context,
    mPublicArray: ArrayList<*>,
    position: Int,
    adapter: AddDeviceAdapter
) : MaterialAlertDialogBuilder(context) {
    var res: Resources = context.resources

    init {
        setTitle("选择设备型号")
        setPositiveButton("取消") { dialog, _ ->
            dialog.dismiss()
        }
        val entries = res.getStringArray(R.array.device_type_entries)
        setItems(entries) { dialog: DialogInterface, i: Int ->
            val obj = mPublicArray[position] as SuitekiObject

            SuitekiDataPutter(context).use {
                put(
                    Device(
                        0,
                        entries[i],
                        obj.mac,
                        obj.authKey,
                        res.getStringArray(R.array.device_type_values)[i],
                        "[]"
                    )
                )
                mPublicArray.removeAt(position)
                adapter.notifyItemRemoved(position)
                Handler(Looper.getMainLooper()).postDelayed({ adapter.notifyDataSetChanged() }, 500)
            }
            "添加成功".toast()
            dialog.dismiss()
        }
        show()
    }

}