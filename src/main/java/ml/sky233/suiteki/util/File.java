package ml.sky233.suiteki.util;

import static ml.sky233.suiteki.MainApplication.REQUEST_CODE_FOR_DIR;
import static ml.sky233.suiteki.MainApplication.TAG;
import static ml.sky233.suiteki.util.Log.d;
import static ml.sky233.suiteki.util.Setting.setValue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ml.sky233.suiteki.MainActivity;

public class File {
    public static String path1 = "/storage/emulated/0/Android/data/com.mi.health/files/log/XiaomiFit.device.log",
            path2 = "/storage/emulated/0/Android/data/com.xiaomi.wearable/files/log/Wearable.log";


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startFor(Activity activity) {
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        DocumentFile documentFile = DocumentFile.fromTreeUri(activity, uri);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        assert documentFile != null;
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        activity.startActivityForResult(intent, REQUEST_CODE_FOR_DIR);//开始授权
    }

    public static void deleteDirWihtFile(String name) {
        java.io.File dir = new java.io.File(name);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (java.io.File file : dir.listFiles())
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file.getPath()); // 递规的方式删除文件夹
        dir.delete();// 删除目录本身
    }

    public static DocumentFile getDocumentFile(Context context, String path) {
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }

    public static Uri changeToUri(String path) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path);
    }

    public static String getLogText(Context context) {
        try {
            if (Setting.getValue("read_mode"))
                return getInputText(context, choosePath(context));
            else
                return getFileText(choosePath(context));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLogTextZip() {
        try {
            if (new java.io.File("/data/data/ml.sky233.suiteki/log_data/Wearable.log").exists())
                return getFileText("/data/data/ml.sky233.suiteki/log_data/Wearable.log");
            else if (new java.io.File("/data/data/ml.sky233.suiteki/log_data/Wearable.log").exists())
                return getFileText("XiaomiFit.device.log");
            else
                return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("Recycle")
    public static String getInputText(Context context, String path) {
        String res = "";
        InputStream inputStream = null;
        try {
            Uri uri = changeToUri(path);
            inputStream = context.getContentResolver().openInputStream(uri);
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
        return new java.io.File(path).exists();
    }

    public static boolean deleteDocumentFile(String path, Context context) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path));
        if (documentFile.exists())
            return documentFile.delete();
        else
            return true;
    }

    public static String getFileText(String filename) {
        String res = "";
        if ((new java.io.File(filename)).exists())
            try {
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
        java.io.File file = new java.io.File("/storage/emulated/0/Download/wearablelog/");
        java.io.File[] files = file.listFiles(file1 -> file1.getName().endsWith("log.zip"));
        num = new long[files.length];
        for (int i = 0; i < files.length; i++)
            num[i] = Long.parseLong(Text.getTheText("Suiteki:" + files[i].getName(), "Suiteki:", "log.zip")[0]);
        long name = 0;
        for (int i = 0; i < num.length; i++)
            if (name < num[i])
                name = num[i];
        if (name != 0)
            return file.getPath() + "/" + String.valueOf(name) + "log.zip";
        else
            return "";
    }


    public static boolean isPath(String path) {
        if (Build.VERSION.SDK_INT >= 30) {
            setValue("app_mode", true);
            return isDocumentFile(path, MainActivity.context);
        } else
            return isFile(path);
    }

    public static String choosePath(Context context) {
        switch (Setting.getString("app_mode")) {
            case "auto":
                if (!getPackageName("com.mi.health", context).equals("") && isPath(path1))
                    return path1;
                if (!getPackageName("com.xiaomi.wearable", context).equals("") && isPath(path2))
                    return path2;
                return "";
            case "mi_health":
                return path1;
            case "mi_wearable":
                return path2;
            default:
                return "";
        }
    }

    public static String getPackageName(String packageName, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo localApplicationInfo = pm.getApplicationInfo(packageName, 0);
            return localApplicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String tempFileName = "/data/data/ml.sky233.suiteki/log_data/";

    public static boolean unzipFile(String zipPath) {
        try {
            java.io.File file = new java.io.File(tempFileName);
            if (!file.exists())
                file.mkdirs();
            InputStream inputStream = new FileInputStream(zipPath);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            byte[] buffer = new byte[1024 * 1024];
            int count = 0;
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    String fileName = zipEntry.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    file = new java.io.File(tempFileName + java.io.File.separator + fileName);
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

