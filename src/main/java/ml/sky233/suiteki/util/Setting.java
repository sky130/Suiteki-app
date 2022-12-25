package ml.sky233.suiteki.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Setting {
    private static SharedPreferences sharedPreferences = null;

    public static void init(SharedPreferences a){
        sharedPreferences = a;
    }

    public static int getPackageVersion(String packageName, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo localPackageInfo = pm.getPackageInfo(packageName, 0);
            return localPackageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getString(String name) {
        try {
            SharedPreferences preferences = sharedPreferences;
            return preferences.getString(name, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setString(String name, String value) {
        SharedPreferences preferences = sharedPreferences;
        preferences.edit().putString(name, value).apply();
    }


    public static boolean getValue(String name) {
        try {
            SharedPreferences preferences = sharedPreferences;
            return preferences.getBoolean(name,false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setValue(String name,boolean value) {
        try {
            SharedPreferences preferences = sharedPreferences;
            preferences.edit().putBoolean(name,value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
