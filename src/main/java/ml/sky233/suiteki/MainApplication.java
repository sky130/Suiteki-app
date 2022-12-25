package ml.sky233.suiteki;

import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.preference.PreferenceManager;

import ml.sky233.Suiteki;
import ml.sky233.suiteki.util.AppThread;
import ml.sky233.suiteki.util.Setting;

public class MainApplication extends Application {
    private static Application mApp;
    public static String TAG = "Suiteki.test";
    public static Suiteki suiteki;
    public static AppThread thread;
    public static ClipboardManager clipboardManager;
    public static SharedPreferences sharedPreferences;
    public static final int REQUEST_CODE_FOR_DIR = 1;

    public static Application getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        thread = new AppThread();
        thread.initThread();
        suiteki = new Suiteki();
        Setting.init(sharedPreferences);
        //Setting.setString("app_mode", "auto");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Setting.setValue("is_android_8", true);
        mApp = this;
    }


}
