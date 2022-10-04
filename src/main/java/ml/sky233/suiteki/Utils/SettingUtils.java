package ml.sky233.suiteki.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class SettingUtils {

    public static boolean saveSetting(String name, String value) {
        boolean result = true;
        Properties properties = new Properties();
        String file = "/data/data/ml.sky233.suiteki/setting.xml";
        try {
            FileInputStream i = new FileInputStream(file);
            properties.load(i);
        } catch (Exception var7) {
            var7.printStackTrace();
            result = false;
        }

        properties.setProperty(name, value);

        try {
            FileOutputStream o = new FileOutputStream(file, false);
            properties.store(o, "");
        } catch (Exception var6) {
            var6.printStackTrace();
            result = false;
        }

        return result;
    }

    public static String getSetting(String name) {
        Properties properties = new Properties();
        String file = "/data/data/ml.sky233.suiteki/setting.xml";
        try {
            FileInputStream i = new FileInputStream(file);
            properties.load(i);
        } catch (Exception var4) {
            var4.printStackTrace();
            return "";
        }

        String value = properties.getProperty(name, "");
        return value != null ? value : "";
    }

    public static String getPackageName(Activity activity, String packagename) {
        try {
            PackageManager pm = activity.getPackageManager();
            ApplicationInfo localApplicationInfo = pm.getApplicationInfo(packagename, 0);
            return localApplicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
            return "";
        }
    }

    public static int getPackageVersion(String packagename, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo localPackageInfo = pm.getPackageInfo(packagename, 0);
            return localPackageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
            return 0;
        }
    }

}
