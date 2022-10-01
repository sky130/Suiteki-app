package ml.sky233.suiteki;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ml.sky233.suiteki.getKeyActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ml.sky233.suiteki.MainActivity.REQUEST_CODE_FOR_DIR;

public class SuitekiTool {

//    public static Uri toUri(Context context,String filePath) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return FileProvider.getUriForFile(context, context.getApplicationInfo().packageName + ".fileprovider", new File(filePath));
//        }
//        return Uri.fromFile(new File(filePath));
//    }

    public static void startFor(String path, Activity activity) {
        Uri uri1 = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        String uri = changeToUri(Environment.getExternalStorageDirectory().getPath());
        uri = uri + "/document/primary%3A" + Environment.getExternalStorageDirectory().getPath().replace("/storage/emulated/0/", "").replace("/", "%2F");
        Uri parse = Uri.parse(uri);
        DocumentFile documentFile = DocumentFile.fromTreeUri(activity, uri1);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        activity.startActivityForResult(intent, REQUEST_CODE_FOR_DIR);//开始授权
    }


//    public static void startForSDCard(String path, Activity activity) {
//        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/" + path.replace("/", "%2F"));
//        DocumentFile documentFile = DocumentFile.fromTreeUri(activity,uri);
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
//                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
//        activity.startActivityForResult(intent, REQUEST_CODE_FOR_DIR);//开始授权
//    }

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

    public static DocumentFile getDocumentFile(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
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
        if (documentFile.exists()) {
            return documentFile.delete();
        } else {
            return true;
        }

    }

    public static String getFileText(String filename, String charset) {
        //读入文本文件
        String res = "";
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


    public static Uri changeToUri3(String path) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path);
    }

    public static String[] AnalyzeText(String str, String separator) {
        if (!"".equals(separator) && !"".equals(str)) {
            if (separator.equals("\n")) {
                str = exchangeText(str, "\r", "");
            }
            return getTextRight(str, getTextLength(separator)).equals(separator) ? getTheText(separator + str, separator, separator) : getTheText(separator + str + separator, separator, separator);
        } else {
            return new String[0];
        }
    }

    public static String[] regexMatch(String text, String statement) {
        Pattern pn = Pattern.compile(statement, 40);
        Matcher mr = pn.matcher(text);
        ArrayList<String> list = new ArrayList<>();
        while (mr.find()) {
            list.add(mr.group());
        }
        String[] strings = new String[list.size()];
        return (String[]) list.toArray(strings);
    }


    public static String[] getTheText(String str, String left, String right) {
        return !"".equals(str) && !"".equals(left) && !"".equals(right) ? regexMatch(str, "(?<=\\Q" + left + "\\E).*?(?=\\Q" + right + "\\E)") : new String[0];
    }

    public static String getTextRight(String str, int len) {
        if (!"".equals(str) && len > 0) {
            if (len > str.length()) {
                return str;
            } else {
                int start = str.length() - len;
                return str.substring(start, str.length());
            }
        } else {
            return "";
        }
    }

    public static int getTextLength(String str) {
        return str.length();
    }

    public static String exchangeText(String str, String find, String replace) {
        if (!"".equals(find) && !"".equals(str)) {
            find = "\\Q" + find + "\\E";
            return str.replaceAll(find, replace);
        } else {
            return "";
        }
    }

    public static String getInText(Context context, String path) {
        String res = "";
        InputStream inputStream = null;
        try {
            Uri uri = changeToUri3(path);
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

    public static String changeToUri(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2;
    }

    public static Object toObject(String var1) {
        try {
            JSONObject var2 = new JSONObject(var1);
            return var2;
        } catch (JSONException var3) {
            return null;
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

    public static Object getObject(Object var1, String var2) {
        JSONObject var3 = (JSONObject) var1;
        if (var3 == null) {
            return null;
        } else {
            try {
                JSONObject var4 = var3.getJSONObject(var2);
                return var4;
            } catch (JSONException var5) {
                return null;
            }
        }
    }

    public static String getObjectText(Object var1, String var2) {
        JSONObject var3 = (JSONObject) var1;
        if (var3 == null) {
            return "";
        } else {
            try {
                String var4 = var3.getString(var2);
                return var4;
            } catch (JSONException var5) {
                return "";
            }
        }
    }

    public static Object getArray(Object var1, String var2) {
        JSONObject var3 = (JSONObject) var1;
        if (var3 == null) {
            return null;
        } else {
            try {
                JSONArray var4 = var3.getJSONArray(var2);
                return var4;
            } catch (JSONException var5) {
                return null;
            }
        }
    }

    public static int getArrayLength(Object var1) {
        JSONArray var2 = (JSONArray) var1;
        return var2 == null ? 0 : var2.length();
    }

    public static Object getArrayObject(Object var1, int var2) {
        JSONArray var3 = (JSONArray) var1;
        if (var3 == null) {
            return null;
        } else {
            try {
                JSONObject var4 = var3.getJSONObject(var2);
                return var4;
            } catch (JSONException var5) {
                return null;
            }
        }
    }

}
