package ml.sky233.suiteki

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import com.clj.fastble.BleManager
import ml.sky233.Suiteki
import ml.sky233.choseki.handler.CrashHandler
import ml.sky233.suiteki.service.ble.BleService
import ml.sky233.suiteki.service.ble.BleService.LocalBinder
import ml.sky233.suiteki.sqlite.SuitekiDataGetter
import ml.sky233.suiteki.sqlite.SuitekiDataPutter
import ml.sky233.suiteki.sqlite.bean.Device
import ml.sky233.suiteki.util.SettingUtils

class MainApplication : Application() {


    companion object {
        @SuppressLint("StaticFieldLeak")
        var mService: BleService? = null
        var mServiceConnection: ServiceConnection? = null
        var suiteki: Suiteki = Suiteki()

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        BleManager.getInstance().init(this)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(5, 5000)
            .setSplitWriteNum(244)
            .setConnectOverTime(10000).operateTimeout = 5000
        application = this
        context = this
        SuitekiDataGetter.init()
        CrashHandler.instance.init(this)
        startBleService()
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {

            }
        }
    }

    var service_intent: Intent? = null

    fun startBleService() {
        service_intent = Intent(application, BleService::class.java)
        mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                mService = (service as LocalBinder).service

            }

            override fun onServiceDisconnected(name: ComponentName) {
                mService = null
            }
        }
        application.bindService(
            service_intent,
            mServiceConnection as ServiceConnection, BIND_AUTO_CREATE
        )
    }

    fun reStartBleService() {
        if (service_intent == null) return
        mServiceConnection?.let { application.unbindService(it) }
        mService!!.stopSelf()
        startBleService()
    }


}