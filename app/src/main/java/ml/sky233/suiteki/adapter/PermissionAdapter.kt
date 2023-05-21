package ml.sky233.choseki.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ml.sky233.suiteki.R
import ml.sky233.suiteki.adapter.RecyclerExtras
import ml.sky233.suiteki.bean.PermissionObject
import ml.sky233.suiteki.sqlite.SuitekiDataGetter
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.util.SettingUtils

class PermissionAdapter(
    private var mContext: Context,
    private val mPublicArray: ArrayList<PermissionObject>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerExtras.OnItemClickListener,
    RecyclerExtras.OnItemLongClickListener, RecyclerExtras.OnItemTouchListener {

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
        holder.image.setImageResource(mPublicArray[position].icon)
        holder.title.text = mPublicArray[position].name
        holder.subTitle.text = mPublicArray[position].desc
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { v: View? ->
                mOnItemClickListener!!.onItemClick(v, position)
            }
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener { v: View? ->
                mOnItemLongClickListener!!.onItemLongClick(v, position)
                true
            }
        }
        holder.button.isVisible = false
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
        val image: ImageView

        init {
            title = v.findViewById(R.id.card_title_name)
            subTitle = v.findViewById(R.id.card_title_mac)
            button = v.findViewById(R.id.device_choose)
            image = v.findViewById(R.id.device_imageView)
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