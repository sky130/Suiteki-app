package ml.sky233.suiteki.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ml.sky233.Suiteki
import ml.sky233.SuitekiObject
import ml.sky233.SuitekiUtils
import ml.sky233.suiteki.R
import ml.sky233.suiteki.sqlite.SuitekiDataPutter
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.ui.dialog.CustomDeviceTypeDialog
import ml.sky233.suiteki.util.SettingUtils
import ml.sky233.suiteki.util.TextUtils.toast

class AddDeviceAdapter(
    private var mContext: Context,
    private var suiteki: Suiteki,
    private val supportFragmentManager: FragmentManager,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mPublicArray = suiteki.resultData

    // 获取列表项的个数
    override fun getItemCount(): Int {
        return mPublicArray.size
    }

    override fun onCreateViewHolder(vg: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.item_device, vg, false)
        return ItemHolder(v)
    }

    // 绑定列表项的视图持有者
    @SuppressLint("RecyclerView", "ClickableViewAccessibility", "NotifyDataSetChanged")
    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        val holder = vh as ItemHolder
        val obj = mPublicArray[position] as SuitekiObject
        holder.title.text =
            if (SuitekiUtils.getModelName(
                    obj.deviceName
                ) === "notFound"
            ) "未知设备" else obj.deviceName
        holder.subTitle.text = obj.mac
        holder.button.text = "添加"
        holder.button.setOnClickListener {
            if (SuitekiUtils.getModelName(
                    obj.deviceName
                ) != "notFound"
            ) {
                SuitekiDataPutter(mContext).use {
                    put(
                        Device(
                            0,
                            holder.title.text.toString(),
                            obj.mac,
                            obj.authKey,
                            "",
                            "[]"
                        )
                    )
                }
                mPublicArray.removeAt(position)
                notifyItemRemoved(position)
                Handler(Looper.getMainLooper()).postDelayed({ notifyDataSetChanged() }, 500)
                "添加成功".toast()
            } else {
                CustomDeviceTypeDialog(mContext, mPublicArray, position, this)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ItemHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView
        val subTitle: TextView
        val button: MaterialButton

        init {
            title = v.findViewById(R.id.card_title_name)
            subTitle = v.findViewById(R.id.card_title_mac)
            button = v.findViewById(R.id.device_choose)
        }
    }


}