package ml.sky233.suiteki.ui.dialog

import android.annotation.SuppressLint
import ml.sky233.suiteki.util.TextUtils
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ml.sky233.SuitekiUtils
import ml.sky233.suiteki.R
import ml.sky233.suiteki.sqlite.SuitekiDataPutter
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.util.TextUtils.fixMac
import ml.sky233.suiteki.util.TextUtils.isValidMac
import ml.sky233.suiteki.util.TextUtils.toast

class CustomAddDeviceBottomSheet() :
    BottomSheetDialogFragment() {
    lateinit var name: TextView
    lateinit var mac: TextView
    lateinit var key: TextView
    lateinit var btn: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.dialog_custom_add_device, container, false)
        name = view.findViewById(R.id.editText_name)
        mac = view.findViewById(R.id.editText_mac)
        key = view.findViewById(R.id.editText_key)
        btn = view.findViewById(R.id.button)
        btn.setOnClickListener {
            if (mac.text.toString().fixMac().isValidMac()) {
                SuitekiDataPutter(requireContext()).use {
                    put(
                        Device(
                            0,
                            name.text.toString(),
                            mac.text.toString().fixMac(),
                            key.text.toString(),
                            "",
                            "[]"
                        )
                    )
                    "添加成功".toast()
                    dismiss()
                }
            }else{
                "错误的蓝牙地址".toast()
            }
        }
        return view
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }


    companion object {
        const val TAG = "CustomAddDeviceBottomSheet"
    }
}