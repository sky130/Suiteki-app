package ml.sky233.suiteki.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ml.sky233.choseki.adapter.DeviceAdapter
import ml.sky233.suiteki.R
import ml.sky233.suiteki.adapter.RecyclerExtras.OnItemClickListener
import ml.sky233.suiteki.util.SettingUtils

class DeviceBottomSheet(c: Context, listener: () -> Unit) :
    BottomSheetDialogFragment() {
    lateinit var adapter: DeviceAdapter
    lateinit var recycler: RecyclerView
    val _context: Context = c
    val _listener = listener


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.dialog_device, container, false)
        recycler = view.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(_context)
        adapter = DeviceAdapter(_context).apply {
            setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    setSwitch(position)
                }
            })
        }
        recycler.adapter = adapter
        return view
    }


    override fun onDismiss(dialog: DialogInterface) {
        _listener()
        super.onDismiss(dialog)
    }



    fun setSwitch(position: Int) {
        val device = SettingUtils.getInt(SettingUtils.DEVICE)
        if (device != -1 && device <= adapter.itemCount) {
            val viewHolder =
                recycler.findViewHolderForAdapterPosition(device) as DeviceAdapter.ItemHolder
            viewHolder.button.isEnabled = true
            viewHolder.button.text = "选择"
        }
        val viewHolder =
            recycler.findViewHolderForAdapterPosition(position) as DeviceAdapter.ItemHolder
        viewHolder.button.isEnabled = false
        viewHolder.button.text = "已选"
        SettingUtils.setInt(SettingUtils.DEVICE, position)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}