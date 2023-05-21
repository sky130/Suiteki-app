package ml.sky233.suiteki.ui.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import ml.sky233.suiteki.util.startActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import ml.sky233.SuitekiObject
import ml.sky233.SuitekiUtils
import ml.sky233.suiteki.MainApplication.Companion.suiteki
import ml.sky233.suiteki.R
import ml.sky233.suiteki.sqlite.SuitekiDataPutter
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.ui.WebLoginActivity
import ml.sky233.suiteki.util.MsgUtils
import ml.sky233.suiteki.util.TextUtils.checkUserName
import ml.sky233.suiteki.util.TextUtils.toast
import kotlin.concurrent.thread


@Suppress("UNCHECKED_CAST")
class ZeppAddDeviceBottomSheet() :
    BottomSheetDialogFragment() {
    private lateinit var password: EditText
    private lateinit var username: EditText
    private lateinit var checkBox: CheckBox
    private lateinit var button: MaterialButton
    private lateinit var button_mi: MaterialButton



    private var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            msg.obj.toString().toast()
            button.isEnabled = true
            button.text = "登录"
            button_mi.isEnabled = true
            button_mi.text = "小米账号登录"
        }
    }

    override fun onResume() {
        super.onResume()
        if(suiteki.isUserEmpty){
            button_mi.text = "正在登录中"
            thread {
                suiteki.loginHuami()
                if (suiteki.resultCode.equals("200")) {
                    suiteki.getHuamiToken()
                    val list: ArrayList<SuitekiObject> =
                        suiteki.resultData as ArrayList<SuitekiObject>
                    SuitekiDataPutter(requireContext()).use {
                        for (obj in list) {
                            put(Device(0,SuitekiUtils.getModelName(obj.deviceName), obj.mac, obj.authKey, "", "[]"))
                        }
                    }
                    if (list.size > 0){
                        handler.sendMessage(MsgUtils.build("添加成功",0))
                        dismiss()
                    }
                    else
                        handler.sendMessage(MsgUtils.build("添加失败,账号下没有设备,请检查账号登录方式",1))
                } else {
                    handler.sendMessage(MsgUtils.build("登录失败,检查账号或密码是否正确",2))
                }
                suiteki.setCode("")
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.dialog_zepp_add_device, container, false)
        password = view.findViewById(R.id.login_editText_password)
        username = view.findViewById(R.id.login_editText_mail)
        checkBox = view.findViewById(R.id.login_checkBox)
        button = view.findViewById(R.id.button)
        button_mi = view.findViewById(R.id.button_mi)
        button_mi.setOnClickListener {
            activity?.startActivity<WebLoginActivity>()
        }
        button.setOnClickListener {it as Button
            it.isEnabled = false
            it.text = "正在登录中"
            thread {
                suiteki.loginHuami(
                    username.text.toString().checkUserName(),
                    password.text.toString()
                )
                if (suiteki.resultCode.equals("200")) {
                    suiteki.getHuamiToken()
                    val list: ArrayList<SuitekiObject> =
                        suiteki.resultData as ArrayList<SuitekiObject>
                    SuitekiDataPutter(requireContext()).use {
                        for (obj in list) {
                            put(Device(0, SuitekiUtils.getModelName(obj.deviceName), obj.mac, obj.authKey, "", "[]"))
                        }
                    }
                    if (list.size > 0){
                        handler.sendMessage(MsgUtils.build("添加成功",0))
                        dismiss()
                    }
                    else
                        handler.sendMessage(MsgUtils.build("添加失败,账号下没有设备,请检查账号登录方式",1))
                } else {
                    handler.sendMessage(MsgUtils.build("登录失败,检查账号或密码是否正确",2))
                }
            }
        }
        return view
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }


    companion object {
        const val TAG = "ZeppAddDeviceBottomSheet"
    }
}