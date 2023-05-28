package ml.sky233.suiteki.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ml.sky233.suiteki.adapter.AddDeviceAdapter
import ml.sky233.choseki.adapter.DeviceAdapter
import ml.sky233.suiteki.MainApplication
import ml.sky233.suiteki.R
import ml.sky233.suiteki.adapter.RecyclerExtras.OnItemClickListener
import ml.sky233.suiteki.util.SettingUtils

class AddDeviceBottomSheet(
    private val supportFragmentManager: FragmentManager,
) :
    BottomSheetDialogFragment() {
    lateinit var adapter: AddDeviceAdapter
    lateinit var recycler: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_device, container, false)
        recycler = view.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context)

        adapter = AddDeviceAdapter(requireContext(), MainApplication.suiteki, supportFragmentManager)
        recycler.adapter = adapter
        return view
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
        const val TAG = "AddDeviceBottomSheet"
    }
}