package ml.sky233.suiteki;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.clj.fastble.BleManager;

import java.io.IOException;

import ml.sky233.Suiteki;
import ml.sky233.suiteki.bean.device.DeviceInfo;
import ml.sky233.suiteki.service.ble.BleService;
import ml.sky233.suiteki.util.FileUtils;
import ml.sky233.suiteki.util.GirlFriend;
import ml.sky233.suiteki.util.LogUtils;
import ml.sky233.suiteki.util.SettingUtils;
import ml.sky233.suiteki.bean.app.AppList;
import ml.sky233.suiteki.bean.device.DevicesList;

public class MainApplication extends Application {
    public static ClipboardManager clipboardManager;
    public static Application application;
    public static SharedPreferences sharedPreferences;
    public static String TAG = "Suiteki.test", NAME = "ケ・ウェン・ティン";
    public static DevicesList devicesList;
    public static AppList appList;
    public static String data_path;

    public static Suiteki suiteki;
    public static String e_data_path;
    public static int i = -1;



    public void onCreate() {
        super.onCreate();
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(5, 5000)
                .setSplitWriteNum(512)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        GirlFriend sky233GirlFriend = new GirlFriend(NAME);
        Log.d(TAG, sky233GirlFriend.getName());
        appList = new AppList(this);
        devicesList = new DevicesList(this);
        LogUtils.initUtils();
        data_path = getFilesDir().toString();
        e_data_path= getExternalFilesDir("").getPath();
        i = devicesList.getDeviceInfoIndex();
        suiteki = new Suiteki();
        SettingUtils.init(sharedPreferences);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            SettingUtils.setValue("is_android_8", true);
        application = this;


//        devicesList.addDeviceInfo(new DeviceInfo("0x72beedb8a536a824a8ff05ba03a5f7c9","F5:4D:31:35:9B:57","miband7","小米手环7"));
//        devicesList.addDeviceInfo(new DeviceInfo("0x72beedb8a536a824a8ff05ba03a5f7c9","F5:4D:31:35:9B:56","miband7","小米手环7"));

//        devicesList.setDeviceInfo(0);
        startBleService();


    }

    public static Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (i != devicesList.getDeviceInfoIndex())
                        reStartBleService();
                    i = devicesList.getDeviceInfoIndex();
                    break;
            }
        }
    };

    static Intent service_intent;

    public static void startBleService() {
        service_intent = new Intent(application, BleService.class);
//        application.startService(service_intent);
//        BleManager.getInstance().disconnectAllDevice();
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((BleService.LocalBinder) service).getService();
                if (mService != null) {
                    if(devicesList.getDeviceInfoIndex()!=-1){
                        DeviceInfo deviceInfo = devicesList.getDeviceInfo();
                        mService.connectDevice(deviceInfo.Mac, deviceInfo.AuthKey);
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        application.bindService(service_intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public static void reStartBleService() {
        Log.d(TAG, "reStartBleService");
        if (service_intent == null) return;
        MainApplication.application.unbindService(mServiceConnection);
        mService.stopSelf();
//        application.stopSelf();
//        application.stopService(service_intent);

        startBleService();
    }

    @SuppressLint("StaticFieldLeak")
    public static BleService mService;

    public static ServiceConnection mServiceConnection;

    public void onClick(View view) {
        try {
            mService.writeFirmware(FileUtils.getFileBytes(getAssets().open("device.zip")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
