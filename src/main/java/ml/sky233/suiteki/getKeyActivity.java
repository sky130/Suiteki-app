package ml.sky233.suiteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ml.sky233.Suiteki;

import static ml.sky233.suiteki.Utils.TextUtils.*;
import static ml.sky233.suiteki.Utils.SettingUtils.*;
import static ml.sky233.suiteki.Utils.FileUtils.*;

import java.util.Objects;

public class getKeyActivity extends AppCompatActivity {
    public Context getKeyActivity = this;
    String path1 = "/storage/emulated/0/Android/data/com.mi.health/files/log/XiaomiFit.device.log",
            path2 = "/storage/emulated/0/Android/data/com.xiaomi.wearable/files/log/Wearable.log", name = "", packageName = "", mode = "0", path = "";
    public static String other = "", code = "";
    Button button_get_key;
    EditText editText_get_key;
    ListView listView;

    public void onResume() {
        super.onResume();
        mode = "0";
        path = path1;
//        if (getPackageName("com.mi.health").equals(""))
//            if (getPackageName("com.xiaomi.wearable").equals("")) if (code.equals("")) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("未安装应用");
//                builder.setMessage("你没有安装小米运动健康或者小米穿戴");
//                builder.setPositiveButton("返回", (dialog, which) -> finish());
//                builder.setCancelable(false);
//                builder.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_SEARCH);
//                builder.show();
//            }
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
        if (!code.equals("")) {
            Log.d("Suiteki.test", code);
            mode = "1";
            Toast.makeText(getApplicationContext(), "已经获取code,准备获取AuthKey", Toast.LENGTH_SHORT).show();
            editText_get_key.setEnabled(false);
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
        if (Build.VERSION.SDK_INT >= 30) {
            saveSetting("id_mode", "true");
        }

        if (!other.equals("")) {
            button_get_key.setEnabled(false);
            Toast.makeText(getApplicationContext(), "正在获取AuthKey，请等待", Toast.LENGTH_SHORT).show();
            new Thread() {
                public void run() {
                    super.run();
                    String[] list = Suiteki.getAuthKeyList(other);
                    Log.d("Suiteki.test", other);
                    Message msg = Message.obtain();
                    if (list.length != 0) {
                        if (list.length > 1) {
                            msg.obj = list;
                            msg.what = 2;
                        } else {
                            msg.obj = list[0];
                            msg.what = 1;
                        }
                    } else {
                        msg.obj = "";
                        msg.what = 4;
                    }
                    handler.sendMessage(msg);
                    other = "";
                }
            }.start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    private void initView() {
        listView = findViewById(R.id.listview_get_key);
        editText_get_key = findViewById(R.id.editText_get_key);
        button_get_key = findViewById(R.id.button_get_key);
        this.setTitle("获取密钥");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        listView.setOnItemClickListener((parent, view, i, l) -> {
            String result = ((TextView) view).getText().toString();
            if (mode.equals(0)) {
                copyText(result);
            } else {
                copyText(AnalyzeText(result, "\n")[0]);
            }
            Toast.makeText(getKeyActivity.this, "已复制到剪贴板", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_key);
        initView();
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_copy_key:
                copyText(String.valueOf(editText_get_key.getText()));
                break;
            case R.id.button_get_key:
                view.setEnabled(false);
                boolean a = isDocumentFile(path, getKeyActivity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
                if (isFile(path) && Build.VERSION.SDK_INT < 30) a = true;
                if (a) {
                    Toast.makeText(getApplicationContext(), "正在获取AuthKey，请等待", Toast.LENGTH_SHORT).show();
                    new Thread() {
                        public void run() {
                            super.run();
                            String txt = "";
                            if (getSetting("id_mode").equals("true")) {
                                txt = getInText(getKeyActivity, path);
                            } else {
                                txt = getFileText(path, "UTF-8");
                            }
                            Log.d("Suiteki.test", txt);
                            String[] list = Suiteki.getAuthKeyList(txt);
                            Message msg = Message.obtain();
                            if (list.length != 0) {
                                if (list.length > 1) {
                                    msg.obj = list;
                                    msg.what = 2;
                                } else {
                                    msg.obj = list[0];
                                    msg.what = 1;
                                }
                            } else {
                                msg.obj = "";
                                msg.what = 4;
                            }
                            handler.sendMessage(msg);
                        }
                    }.start();
                } else {
                    view.setEnabled(true);
                    Toast.makeText(this, "Log文件还没生成,请先打开小米运动健康/小米穿戴", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String[] keys;
            ArrayAdapter adapter;
            switch (msg.what) {
                case 1:
                    button_get_key.setEnabled(true);
                    String key = (String) msg.obj;
                    editText_get_key.setText(key);
                    break;
                case 2:
                    button_get_key.setEnabled(true);
                    keys = (String[]) msg.obj;
                    adapter = new ArrayAdapter<String>(
                            getKeyActivity, R.layout.activity_listview, keys);
                    listView.setAdapter(adapter);
                    editText_get_key.setText("点击列表框复制");
                    break;
                case 3:
                    keys = (String[]) msg.obj;
                    if (keys.length >= 2) {
                        adapter = new ArrayAdapter<String>(getKeyActivity, R.layout.activity_listview, keys);
                        listView.setAdapter(adapter);
                        editText_get_key.setText("点击列表框复制");
                    } else {
                        editText_get_key.setText(AnalyzeText(keys[0], "\n")[0]);
                    }
                    break;
                case 4:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getKeyActivity.this);
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
                            intent.setFlags(0);
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
                    break;
            }
        }
    };

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