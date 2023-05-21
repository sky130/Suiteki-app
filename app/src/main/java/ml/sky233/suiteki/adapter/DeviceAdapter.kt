package ml.sky233.choseki.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ml.sky233.suiteki.R
import ml.sky233.suiteki.adapter.RecyclerExtras
import ml.sky233.suiteki.sqlite.SuitekiDataGetter
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.util.SettingUtils

class DeviceAdapter(private var mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerExtras.OnItemClickListener,
    RecyclerExtras.OnItemLongClickListener, RecyclerExtras.OnItemTouchListener {
    private val mPublicArray: ArrayList<Device> = SuitekiDataGetter.device

    // 获取列表项的个数
    override fun getItemCount(): Int {
        return mPublicArray.size
    }

    override fun onCreateViewHolder(vg: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.item_device, vg, false)
        return ItemHolder(v)
    }

    // 绑定列表项的视图持有者
    @SuppressLint("RecyclerView", "ClickableViewAccessibility")
    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        val holder = vh as ItemHolder
        holder.title.text = mPublicArray[position].name
        holder.subTitle.text = mPublicArray[position].mac
        if (mOnItemClickListener != null) {
            holder.button.setOnClickListener { v: View? ->
                mOnItemClickListener!!.onItemClick(v, position)
            }
        }
        if (mOnItemLongClickListener != null) {
            holder.button.setOnLongClickListener { v: View? ->
                mOnItemLongClickListener!!.onItemLongClick(v, position)
                true
            }
        }
        val device = SettingUtils.getInt(SettingUtils.DEVICE)
        if (device == position) {
            holder.button.isEnabled = false
            holder.button.text = "已选"
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

    private var mOnItemClickListener: RecyclerExtras.OnItemClickListener? = null
    fun setOnItemClickListener(listener: RecyclerExtras.OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    private var mOnItemLongClickListener: RecyclerExtras.OnItemLongClickListener? = null
    fun setOnItemLongClickListener(listener: RecyclerExtras.OnItemLongClickListener?) {
        mOnItemLongClickListener = listener
    }

    private var mOnItemTouchListener: RecyclerExtras.OnItemTouchListener? = null
    fun setOnItemTouchListener(listener: RecyclerExtras.OnItemTouchListener?) {
        mOnItemTouchListener = listener
    }

    @SuppressLint("DefaultLocale")
    override fun onItemClick(view: View?, position: Int) {

    }

    @SuppressLint("DefaultLocale")
    override fun onItemLongClick(view: View?, position: Int) {

    }

    override fun onItemTouch(view: View?, event: MotionEvent?): Boolean {
        return false
    }
}