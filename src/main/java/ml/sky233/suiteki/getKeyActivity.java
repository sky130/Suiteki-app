package ml.sky233.suiteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import ml.sky233.Suiteki;

import static ml.sky233.suiteki.SuitekiTool.*;

public class getKeyActivity extends AppCompatActivity {
    public Context getKeyActivity = this;
    String path = "";
    String path1 = "/storage/emulated/0/Android/data/com.mi.health/files/log/XiaomiFit.device.log";
    String path2 = "/storage/emulated/0/Android/data/com.xiaomi.wearable/files/log/Wearable.log";
    String key[];
    String text = "";
    String name = "";
    String packageName = "";
    String mode = "0";
    public static String other = "";
    public static String code = "";

    public void tip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("未安装应用");
        builder.setMessage("你没有安装小米运动健康或者小米穿戴");
        builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        builder.show();
    }

    public void onResume() {
        super.onResume();
        mode = "0";
        path = path1;
        if (getPackageName("com.mi.health").equals(""))
            if (getPackageName("com.xiaomi.wearable").equals("")) if (code.equals("")) tip();

        if (!getPackageName("com.xiaomi.wearable").equals("")) {
            path = path2;
            name = "小米穿戴";
            packageName = "com.xiaomi.wearable";
        }

        if (!getPackageName("com.mi.health").equals("")) {
            path = path1;
            name = "小米运动健康";
            packageName = "com.mi.health";
        }


        //Log.d("Suiteki.test","start");
        //Log.d("Suiteki.test", code);

        if (code != "") {
            Log.d("Suiteki.test", code);
            mode = "1";
            Toast.makeText(getApplicationContext(), "已经获取code,准备获取AuthKey", Toast.LENGTH_SHORT).show();
            Button button = findViewById(R.id.button8);
            button.setEnabled(false);
            new Thread() {
                public void run() {
                    Message msg = Message.obtain();
                    msg.obj = Suiteki.getHuamiToken(code);
                    msg.what = 3;
                    handler.sendMessage(msg);
                    code = "";
                }
            }.start();
        }
//        if (!other.equals("")){
//            Toast.makeText(getApplicationContext(),"正在获取AuthKey，请等待",Toast.LENGTH_SHORT).show();
//            Button button = findViewById(R.id.button8);
//            button.setEnabled(false);
//            new Thread(){
//                public void run() {
//                    String[] list = Suiteki.getAuthKey(other);
//                    Message msg = Message.obtain();
//                    msg.obj = list;
//                    msg.what = 1;
//                    handler.sendMessage(msg);
//                    text = other;
//                    other = "";
//                }
//            }.start();
//
//        }

        if (Build.VERSION.SDK_INT >= 30) {
            saveSetting("id_mode", "true");
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_key);
        this.setTitle("获取密钥");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String result = ((TextView) view).getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData;
                if (mode.equals(0)) {
                    clipData = ClipData.newPlainText(null, result);
                } else {
                    clipData = ClipData.newPlainText(null, AnalyzeText(result, "\n")[0]);
                }
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getKeyActivity.this, "已复制到剪贴板", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void copykey(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        EditText editText = (EditText) findViewById(R.id.editText1);
        ClipData clipData = ClipData.newPlainText(null, String.valueOf(editText.getText()));
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }

    public void getTheKey(View view) {
        //String mode = "0";
        view.setEnabled(false);

        boolean a = false;
        if (isDocumentFile(path, getKeyActivity) && Build.VERSION.SDK_INT >= 30) a = true;
        if (isFile(path) && Build.VERSION.SDK_INT < 30) a = true;

        if (a) {
            Toast.makeText(getApplicationContext(), "正在获取AuthKey，请等待", Toast.LENGTH_SHORT).show();
            new Thread() {
                public void run() {
                    super.run();
                    String txt = "";
                    if (getSetting("id_mode").equals("true")) {
                        if (getSetting("text").equals("true"))
                            txt = getInText(getKeyActivity, path);

                        //Log.d("Suiteki.test","兼容模式");
                    } else {
                        txt = getFileText(path, "UTF-8");
                        //Log.d("Suiteki.test","普通模式");
                    }
                    String[] list = Suiteki.getAuthKeyList(txt);
                    Message msg = Message.obtain();
                    if (list.length != 0) {
                        if (list.length > 1) {
                            msg.obj = list;
                            msg.what = 2;
                            handler.sendMessage(msg);
                        } else {
                            msg.obj = list[0];
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                    } else {
                        msg.obj = "";
                        msg.what = 4;
                        handler.sendMessage(msg);
                    }
                    //Log.d("Suiteki.test", getSetting("id_mode"));
                }
            }.start();
        } else {
            view.setEnabled(true);
            Toast.makeText(this, "Log文件还没生成,请先打开小米运动健康/小米穿戴", Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Button button = findViewById(R.id.button8);
            ListView listView = (ListView) findViewById(R.id.listview);
            EditText editText = (EditText) findViewById(R.id.editText1);
            switch (msg.what) {
                case 1:
                    button.setEnabled(true);
                    String key = (String) msg.obj;
                    editText.setText(key);
                    break;
                case 2:
                    button.setEnabled(true);
                    String[] key1 = (String[]) msg.obj;
                    ArrayAdapter adapter = new ArrayAdapter<String>(
                            getKeyActivity, R.layout.activity_listview, key1);
                    listView.setAdapter(adapter);
                    editText.setText("点击列表框复制");
                    break;
                case 3:
                    String[] key2 = (String[]) msg.obj;
                    if (key2.length >= 2) {
                        ArrayAdapter adapter1 = new ArrayAdapter<String>(getKeyActivity, R.layout.activity_listview, key2);
                        listView.setAdapter(adapter1);
                        editText.setText("点击列表框复制");
                    } else {
                        editText.setText(AnalyzeText(key2[0], "\n")[0]);
                    }
                    break;
                case 4:
                    title();
                    break;
            }
        }
    };

    public void title() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("读取失败");
        builder.setMessage("Log文件中没有AuthKey,建议删除并重新打开" + name);
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("删除并打开" + name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteDocumentFile(path, getKeyActivity);
                Intent intent = getKeyActivity.getPackageManager().getLaunchIntentForPackage(packageName);
                intent.setFlags(335544320);
                getKeyActivity.startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        builder.show();

    }

    public String getPackageName(String packagename) {
        try {
            PackageManager pm = this.getPackageManager();
            ApplicationInfo localApplicationInfo = pm.getApplicationInfo(packagename, 0);
            return localApplicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
            return "";
        }
    }


}