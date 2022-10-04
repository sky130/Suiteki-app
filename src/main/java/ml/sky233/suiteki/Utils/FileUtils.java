package ml.sky233.suiteki.Utils;

import static ml.sky233.suiteki.MainActivity.REQUEST_CODE_FOR_DIR;
import static ml.sky233.suiteki.Utils.LogUtils.d;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtils {

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

    public static DocumentFile getDocumentFile(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }

    public static Uri changeToUri(String path) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        d("PathToUri"+"content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path);
        return Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path);
    }

    @SuppressLint("Recycle")
    public static String getInText(Context context, String path) {
        String res = "";
        d("Mode:2");
        d("path:"+path);
        InputStream inputStream = null;
        try {
            Uri uri = changeToUri(path);
            d("Uri:"+String.valueOf(uri));
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fin = (FileInputStream) inputStream;
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, 0, length, "UTF-8");
            fin.close();
        } catch (Exception var6) {
            var6.printStackTrace();
        }
        return res;
    }

    public static boolean isDocumentFile(String path, Context context) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        d("isDocumentFile:"+"content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path);
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path));
        return documentFile.exists();
    }

    public static boolean isFile(String path) {
        return new File(path).exists();
    }

    public static boolean deleteDocumentFile(String path, Context context) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path));
        if (documentFile.exists()) {
            return documentFile.delete();
        } else {
            return true;
        }

    }

    public static String getFileText(String filename, String charset) {
        //读入文本文件
        String res = "";
        d("Mode:1");
        d("path:"+filename);
        if ((new File(filename)).exists()) {
            try {
                FileInputStream fin = new FileInputStream(filename);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                res = new String(buffer, 0, length, charset);
                fin.close();


            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }
        return res;
    }

}
