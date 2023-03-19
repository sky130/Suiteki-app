package ml.sky233.suiteki.util;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ml.sky233.suiteki.MainApplication;

public class LogUtils {
    public static StringBuilder log;
    //    public static String type = "";
    public static final String APP_START = "APP_START"
            ,DEVICE_INFO = "DEVICE_INFO";

    public static String finalInfo = "null";
    public static ArrayList<String> infoArray = new ArrayList<>();
    public static void initUtils() {
        log = new StringBuilder();
        setType(DEVICE_INFO);
        add("Device", Build.DEVICE);
        add("Brand",Build.BRAND);
        add("Model",Build.MODEL);
        add("Android_Ver",Build.VERSION.CODENAME);
        add("SDK",Build.VERSION.SDK_INT);
        setType(APP_START);
    }

    public static void add(Object obj) {
        log.append(obj).append("\n");
    }

    public static void add(Object obj1, Object obj2) {
        log.append(obj1).append(":").append(obj2);
    }

    public static void setType(String type) {
        add("---[" + type + "]---");
    }

    public static String getString() {
        return log.toString();
    }

    @SuppressLint("SdCardPath")
    public static void writeLog() {
        FileUtils.writeFileText("/sdcard/Android/ml.sky233.suiteki/files/app.log", getString());
    }
//
//    public static void s(String str) {
//        if (!type.equals(str))
//            if (!str.equals("")) {
//                if (!log.equals(""))
//                    log = log + "\n" + f(str);
//                else
//                    log = f(str);
//                type = str;
//            }
//    }
//
//    public static void d(String str) {
//
//        if (!log.equals("")) {
//            log = log + "\n" + str;
//        } else {
//            log = str;
//        }
//    }
//
//    public static void g(String str) {
//        if (!str.equals("")) {
//            log = log + "\n" + str;
//            type = str;
//        }
////    }
//
//    public static String b() {
//        return log;
//    }

    public static String f(String str) {
        if (str.equals("")) {
            return "";
        } else {
            return "=======[" + str + "]=======";
        }
    }

    @SuppressLint("SdCardPath")
    public static void writeLog(List<String> list) {
//        String path = "/data/data/ml.sky233.suiteki/test.log";
        String path = MainApplication.application.getExternalFilesDir(null).getPath() + "/test.log";
        File file = new File(path);
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list == null) {
            FileUtils.writeFileText(path, "error");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++)
            sb.append("\n").append(list.get(i));
        FileUtils.writeFileText(path, sb.toString());

    }
}
