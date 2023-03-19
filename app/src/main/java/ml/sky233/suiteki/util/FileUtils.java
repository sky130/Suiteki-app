package ml.sky233.suiteki.util;

import static ml.sky233.suiteki.MainApplication.TAG;
import static ml.sky233.suiteki.MainApplication.data_path;
import static ml.sky233.suiteki.util.SettingUtils.setValue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ml.sky233.suiteki.MainApplication;

public class FileUtils {
    public static String mi_health_path = "/storage/emulated/0/Android/data/com.mi.health/files/log/XiaomiFit.device.log", mi_wearable_path = "/storage/emulated/0/Android/data/com.xiaomi.wearable/files/log/Wearable.log";
    public static String uri_1 = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Fcom.mi.health%2F";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startFor(Activity activity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
                .putExtra("android.provider.extra.SHOW_ADVANCED", true)
                .putExtra("android.content.extra.SHOW_ADVANCED", true);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        activity.startActivityForResult(intent, 1);//开始授权
    }

    public static Uri buildUri(String path) {
        return DocumentsContract.buildDocumentUri("com.android.externalstorage.documents",
                "primary:Android/data/" + path);
    }

    public static Uri buildUri2(String path) {
        return DocumentsContract.buildDocumentUri("com.android.externalstorage.documents",
                "primary:Android/data/" + path.replace("/storage/emulated/0/Android/data/",""));
    }



    public static void deleteDirWihtFile(String name) {
        File dir = new File(name);
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        for (File file : dir.listFiles())
            if (file.isFile()) file.delete(); // 删除所有文件
            else if (file.isDirectory()) deleteDirWihtFile(file.getPath()); // 递规的方式删除文件夹
        dir.delete();// 删除目录本身
    }

    public static DocumentFile getDocumentFile(Context context, String path) {
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }

//    public static Uri changeToUri(String path) {
//        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
////        return Uri.parse("content://com.android.externalstorage.documents/tree/primary%3A" + path);
////        content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fcom.mi.health%2Ffiles%2Flog%2FXiaomiFit.device.log
////        content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fcom.mi.health
////        content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fcom.mi.health%2Ffiles%2Flog%2FXiaomiFit.device.log
////        content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2Fcom.mi.health%2Ffiles%2Flog%2FXiaomiFit.device.log
////        content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2Fcom.mi.health%2Ffiles%2Flog%2FXiaomiFit.device.log
//        return Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path);
//    }

    public static Uri changeToUri(String path) {
        path = path.replace("/storage/emulated/0/Android/data/", "");
        return DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Android/data/" + path);
    }

    public static Uri changeToUri2(String path) {
        path = path.replace("/storage/emulated/0/", "");
        return DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:" + path);
    }

    public static String getLogText(Context context) {
        try {
            if (SettingUtils.getValue("read_mode")) {
                return getInputText(context, chooseUri(context));
            } else return getFileText(choosePath(context));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLogTextZip() {
        try {
            if (new File("/data/data/ml.sky233.suiteki/log_data/Wearable.log").exists())
                return getFileText("/data/data/ml.sky233.suiteki/log_data/Wearable.log");
            else if (new File("/data/data/ml.sky233.suiteki/log_data/XiaomiFit.device.log").exists())
                return getFileText("/data/data/ml.sky233.suiteki/log_data/XiaomiFit.device.log");
            else return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    @SuppressLint("Recycle")
    public static String getInputText(Context context, Uri uri) {
        String res = "";
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(chooseUri(context));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fin = (FileInputStream) inputStream;
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, 0, length, StandardCharsets.UTF_8);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean isDocumentFile(String path, Context context) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path));
        return documentFile.exists();
    }

    public static boolean isFile(String path) {
        return new File(path).exists();
    }

    public static boolean deleteDocumentFile(String path, Context context) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path));
        if (documentFile.exists()) return documentFile.delete();
        else return true;
    }

    public static boolean fileIsExist(String fileName) {
        //传入指定的路径，然后判断路径是否存在
        File file = new File(fileName);
        if (file.exists()) return true;
        else {
            //file.mkdirs() 创建文件夹的意思
            return file.mkdirs();
        }
    }

    public static void saveBitmap(String name, Bitmap bm, Context mContext) {
        //判断指定文件夹的路径是否存在
        String TargetPath = "/sdcard/" + "/images/";
        if (!fileIsExist(TargetPath)) {
            android.util.Log.d("Save Bitmap", "TargetPath isn't exist");
        } else {
            //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
            File saveFile = new File(TargetPath, name);

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                // compress - 压缩的意思
                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                //存储完成后需要清除相关的进程
                saveImgOut.flush();
                saveImgOut.close();
                android.util.Log.d("Save Bitmap", "The picture is save to your phone!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static byte[] getFileBytes(String fileName) {
        if ((new File(fileName)).exists()) try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            fin.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getFileBytes(InputStream inputStream) {
        try {
            int length = inputStream.available();
            byte[] buffer = new byte[length];
            inputStream.read(buffer);
            inputStream.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] writeFileBytes(String filename, byte[] bytes) {
        File file = new File(filename);
        try {
            if (!file.isFile()) file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
//        return null;
    }


    public static String getFileText(String filename) {
        String res = "";
        File file = new File(filename);
        Log.d(TAG,filename);
        try {
            if (!file.exists()) {
                file.createNewFile();
                return res;
            }
            FileInputStream fin = new FileInputStream(filename);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, 0, length, "UTF-8");
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    public static String writeFileText(String filename, String text) {
        String res = text;
        File file = new File(filename);
        try {
            if (!file.exists()) file.createNewFile();
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    public static String getFileText(InputStream file) {
        String res = "";
        try {
            InputStream fin = file;
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }
        return res;
    }

    public static String getNewLogZip() {
        long[] num;
        File file = new File("/storage/emulated/0/Download/wearablelog/");
        File[] files = file.listFiles(file1 -> file1.getName().endsWith("log.zip"));
        if (files == null) return "";
        num = new long[files.length];
        for (int i = 0; i < files.length; i++)
            num[i] = Long.parseLong(TextUtils.getTheText("Suiteki:" + files[i].getName(), "Suiteki:", "log.zip")[0]);
        long name = 0;
        for (int i = 0; i < num.length; i++)
            if (name < num[i]) name = num[i];
        if (name != 0) return file.getPath() + "/" + String.valueOf(name) + "log.zip";
        else return "";
    }


    public static boolean isPath(String path) {
        if (Build.VERSION.SDK_INT >= 30) {
            setValue("read_mode", true);
            return isDocumentFile(path, MainApplication.application);
        } else return isFile(path);
    }

    public static String choosePath(Context context) {
        switch (SettingUtils.getString("app_mode")) {
            case "auto":
                if (isInstalled("com.mi.health", context) && isPath(mi_health_path)) return mi_health_path;
                if (isInstalled("com.xiaomi.wearable", context) && isPath(mi_wearable_path)) return mi_wearable_path;
                return "";
            case "mi_health":
                return mi_health_path;
            case "mi_wearable":
                return mi_wearable_path;
            default:
                return "";
        }
    }

    public static boolean isGetPermission(Context context, String pn) {
        List<UriPermission> pList = context.getContentResolver().getPersistedUriPermissions();
        for (UriPermission uriPermission : pList) {
            if (TextUtils.lookFor(uriPermission.getUri().getPath(), pn))
                return true;
        }
        return false;
    }


    public static Uri chooseUri(Context context) {
        DocumentFile mi_health = null, mi_wearable = null;
        List<UriPermission> pList = context.getContentResolver().getPersistedUriPermissions();
        if (Build.VERSION.SDK_INT < 33) {
            for (int i = 0; i < pList.size(); i++) {
                UriPermission up = pList.get(i);
                if (TextUtils.lookFor(up.getUri().getPath(), "Android/data")) {
                    try {//小米运动健康
                        DocumentFile df = DocumentFile.fromTreeUri(context, up.getUri()).findFile("com.mi.health").findFile("files").findFile("log").findFile("XiaomiFit.device.log");
                        if (df.isFile()) mi_health = df;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    try {
                        DocumentFile df = DocumentFile.fromTreeUri(context, up.getUri()).findFile("com.xiaomi.wearable").findFile("files").findFile("log").findFile("Wearable.log");
                        if (df.isFile()) mi_wearable = df;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (Build.VERSION.SDK_INT >= 33) {
            for (int i = 0; i < pList.size(); i++) {
                UriPermission up = pList.get(i);

                if (TextUtils.lookFor(up.getUri().getPath(), "com.mi.health")) {
                    try {
                        DocumentFile df = DocumentFile.fromTreeUri(context, up.getUri()).findFile("files").findFile("log").findFile("XiaomiFit.device.log");
                        if (df.isFile()) mi_health = df;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.lookFor(up.getUri().getPath(), "com.xiaomi.wearable")) {
                    try {
                        DocumentFile df = DocumentFile.fromTreeUri(context, up.getUri()).findFile("files").findFile("log").findFile("Wearable.log");
                        if (df.isFile()) mi_wearable = df;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        switch (SettingUtils.getString("app_mode")) {
            case "auto":
                if (mi_health != null)
                    if (isInstalled("com.mi.health", context) && mi_health.isFile())
                        return mi_health.getUri();
                if (mi_wearable != null)
                    if (isInstalled("com.xiaomi.wearable", context) && mi_wearable.isFile())
                        return mi_wearable.getUri();
                return null;
            case "mi_health":
                return mi_health.getUri();
            case "mi_wearable":
                return mi_wearable.getUri();
            default:
                return null;
        }
    }

    public static Map<String, Boolean> getInstallStatus(Context context) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("MiWearable", isInstalled("com.xiaom.wearable", context));
        map.put("MiHealth", isInstalled("com.mi.health", context));
        return map;
    }


    public static boolean isInstalled(String packageName, Context context) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean unzipFile(String zipPath) {
        String tempFileName = data_path + "/log_data/";
        try {
            File file = new File(tempFileName);
            if (!file.exists()) file.mkdirs();
            InputStream inputStream = new FileInputStream(zipPath);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            byte[] buffer = new byte[1024 * 1024];
            int count = 0;
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    String fileName = zipEntry.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    file = new File(tempFileName + File.separator + fileName);
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0)
                        fileOutputStream.write(buffer, 0, count);
                    fileOutputStream.close();
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}

