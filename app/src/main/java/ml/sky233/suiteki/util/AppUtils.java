package ml.sky233.suiteki.util;

import static ml.sky233.suiteki.MainApplication.appList;
import static ml.sky233.suiteki.MainApplication.data_path;
import static ml.sky233.suiteki.util.FileUtils.getFileText;
import static ml.sky233.suiteki.util.FileUtils.unzipFile;

import android.annotation.SuppressLint;
import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import ml.sky233.suiteki.bean.app.AppInfo;
import ml.sky233.util.Eson;

public class AppUtils {

    public static boolean isArchiveFile(File file) {
        byte[] ZIP_HEADER_1 = new byte[]{80, 75, 3, 4};
        byte[] ZIP_HEADER_2 = new byte[]{80, 75, 5, 6};
        if (file == null)
            return false;
        if (file.isDirectory())
            return false;
        boolean isArchive = false;
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] buffer = new byte[4];
            int length = input.read(buffer, 0, 4);
            if (length == 4) {
                isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isArchive;
    }

    @SuppressLint("SdCardPath")
    public static int getBinType(String fileName) {
        int type = -1;
        String path = data_path + "/log_data/app.json";
        File file = new File(fileName);
        if (isArchiveFile(file)) {
            unzipFile(fileName);
            file = new File(path);
            if (file.isFile())
                switch (getType(getFileText(path))) {
                    case "watchface":
                        type = BytesUtils.WATCHFACE;
                        break;
                    case "app":
                        type = BytesUtils.APP;
                        appList.addAppInfo(new AppInfo(path));
                        break;
                }
            FileUtils.deleteDirWihtFile(data_path + "/log_data/");
        }
        LogUtils.infoArray.add("type:"+ type);
//        Log.d(TAG,"type:"+type);
        return type;
    }

    @SuppressLint("SdCardPath")
    public static void addApp(String fileName, Handler handler) {
        String path = data_path + "/log_data/app.json";
        File file = new File(fileName);
        if (isArchiveFile(file)) {
            unzipFile(fileName);
            file = new File(path);
            if (file.isFile())
                if ("app".equals(getType(getFileText(path)))) {
                    appList.addAppInfo(new AppInfo(path));
                    handler.sendMessage(MsgBuilder.build("添加成功", 0));
                    handler.sendMessage(MsgBuilder.build("", 1));
                } else {
                    handler.sendMessage(MsgBuilder.build("添加失败", 0));
                }
            FileUtils.deleteDirWihtFile(data_path + "/log_data/");
        }
    }


//
//    public static int getBinInfo(String fileName) {
//        int type = -1;
//        String path = "/data/data/ml.sky233.suiteki/log_data/app.json";
//        File file = new File(fileName);
//        if (isArchiveFile(file)) {
//            unzipFile(fileName);
//            file = new File(path);
//            appList.addAppInfo(new AppInfo(path));
//            if (file.isFile())
//                switch (getType(getFileText(path))) {
//                    case "watchface":
//                        type = BytesUtils.WATCHFACE;
//                        break;
//                    case "app":
//                        type = BytesUtils.APP;
//                        break;
//                }
//            FileUtils.deleteDirWihtFile(tempFileName);
//        }
////        Log.d(TAG,"type:"+type);
//        return type;
//    }

    public static String getType(String str) {
        return Eson.getObjectText(Eson.getObject(Eson.toObject(str), "app"), "appType");
    }

}
