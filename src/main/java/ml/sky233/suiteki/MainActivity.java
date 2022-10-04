package ml.sky233.suiteki;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;

import static ml.sky233.suiteki.Utils.EsonUtils.*;
import static ml.sky233.suiteki.Utils.FileUtils.*;
import static ml.sky233.suiteki.Utils.LogUtils.*;
import static ml.sky233.suiteki.Utils.TextUtils.*;
import static ml.sky233.suiteki.Utils.SettingUtils.*;

public class MainActivity extends AppCompatActivity {
    public static ClipboardManager clipboardManager;
    public static final int REQUEST_CODE_FOR_DIR = 12;
    String path = "",
            path1 = "/storage/emulated/0/Android/data/com.mi.health/files/log/XiaomiFit.device.log",
    //path2 = "/storage/emulated/0/Android/data/com.xiaomi.wearable/files/log/Wearable.log",
    text = "",
            update = "",
            safe = "0";
    String[] txt = {"若汝无介意，愿作耳边墙", "思君无情拒，唯吾孤自哀", "希愿回心意，眷恋越星海", "欲采栀子花，赠予手心中", "流水长月，依恋不舍", "拾栀雨落，忆梦汐潮", "思向呼唤，尔已忘期", "汝之憨态，流连忘返"};
    //public static String code = "";
    ListView listView_main;
    ImageView imageView_main;
    TextView textView_title;
    TextView textView_tag;
    OkHttpClient okClient = new OkHttpClient();

    private void initView() {
        d("SDK:" + String.valueOf(Build.VERSION.SDK_INT));
        d("Model:"+Build.MODEL);
        d("Device:"+Build.DEVICE);
        d("Brand:"+Build.BRAND);
        d("DeviceManufacturer:"+Build.MANUFACTURER);
        listView_main = findViewById(R.id.listview_main);
        imageView_main = findViewById(R.id.imageView_main);
        textView_title = findViewById(R.id.textView_title);
        textView_tag = findViewById(R.id.textView_tag);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }


    public void onClick(View v) {
        Uri uri;
        Intent intent;
        switch (getSetting("pro")) {
            case "1":
            case "3":
                uri = Uri.parse("package:" + getPackageName());
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                startActivity(intent);
                break;
            case "2":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startFor(this);
                }
                break;
            case "0":
                Random ra = new Random();
                text = txt[ra.nextInt(txt.length)];
                textView_title.setText(text);
                break;
        }
    }

    public void creatList() {
        String[] name = {
                "小米运动健康/小米穿戴",
                "ZeppLife/小米运动",
        };
        int[] menuImg = {
                R.drawable.ic_key_lock,
                R.drawable.ic_sign,
        };
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("img", menuImg[i]);
            listItem.put("name", name[i]);
            listItems.add(listItem);
        }
        SimpleAdapter sampleAdapter = new SimpleAdapter(MainActivity.this, listItems, R.layout.listview, new String[]{"img", "name"}, new int[]{R.id.image, R.id.title});
        listView_main.setAdapter(sampleAdapter);
        listView_main.setOnItemClickListener((parent, view, i, l) -> {
            String result = String.valueOf(i);
            Log.d("Suiteki.test", result);
            switch (i) {
                case 0:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, getKeyActivity.class);
                    startActivity(intent);
                    break;

                case 1:
                    Intent intent1 = new Intent();
                    intent1.setClass(MainActivity.this, WebActivity.class);
                    startActivity(intent1);
                    break;
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        switch (item.getItemId()) {
            case R.id.copy_log:
                builder.setTitle("调试日志");
                builder.setMessage(b());
                builder.setPositiveButton("返回", (dialog, which) -> dialog.dismiss());
                builder.setNegativeButton("复制", (dialog, which) -> {
                    copyText(b());
                    dialog.dismiss();
                });
                builder.show();
                break;
            case R.id.id_mode:
                if (item.isChecked()) {
                    item.setChecked(false);
                    saveSetting("id_mode", "false");
                    Log.d("Suiteki.test", getSetting("id_mode"));
                    Toast.makeText(getApplicationContext(), "已关闭兼容模式", Toast.LENGTH_SHORT).show();
                } else {
                    item.setChecked(true);
                    saveSetting("id_mode", "true");
                    Log.d("Suiteki.test", getSetting("id_mode"));
                    Toast.makeText(getApplicationContext(), "已开启兼容模式", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.about:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.deleteFile:
                if (deleteDocumentFile(path, this)) {
                    Toast.makeText(this, "已经删除Log文件", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "删除Log文件失败", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.checkUpdate:
                Thread thread;
                Toast.makeText(MainActivity.this, "正在检查更新", Toast.LENGTH_SHORT).show();
                thread = new Thread(() -> {
                    Looper.prepare();
                    Request request = new Request.Builder().url("https://sky233.ml/update/Suiteki/index.json").build();
                    try {
                        Response response = okClient.newCall(request).execute();
                        if (response.isSuccessful()) {
                            update = Objects.requireNonNull(response.body()).string();
                        } else {
                            update = String.valueOf(response.code());
                        }
                        Log.d("Suiteki.test", update);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = update;
                    msg.what = 1;
                    handler.sendMessage(msg);
                    Looper.loop();
                });
                thread.start();
                break;
            case R.id.choose_log:
                builder.setTitle("手动选择Log文件");
                builder.setMessage("选择名为\"XiaomiFit.device.log\"的文件"+"\n此外,该方法不一定有效,仅供测试");
                builder.setPositiveButton("取消", (dialog, which) -> dialog.dismiss());
                builder.setNegativeButton("确认", (dialog, which) -> {
                    Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Fcom.mi.health%2Ffiles%2Flog%2F");
                    intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                    intent1.setType("application/octet-stream");
                    startActivityForResult(intent1, 2);
                    dialog.dismiss();
                });
                builder.show();
                break;
            case R.id.join_group:
                builder.setTitle("进群吹水 ヾ(≧▽≦*)o");
                builder.setMessage("Suiteki用户交流群 : \n\n" + "1群:648849444\n\n别问为什么只有1群,问就是没有人进(");
                builder.setPositiveButton("返回", (dialog, which) -> dialog.dismiss());
                builder.setNegativeButton("复制群号", (dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(null, "648849444");
                    clipboard.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                builder.show();
                break;
            default:
                break;
        }
        return true;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String getText = (String) msg.obj;
                    Object object = toObject(getText);
                    String version = getObjectText(object, "version_name");
                    String update_text = getObjectText(object, "update_text");
                    String update_time = getObjectText(object, "update_time");
                    String app_link = getObjectText(object, "app_link");
                    int version_code = Integer.parseInt(getObjectText(object, "version_code"));
                    if (version_code > getPackageVersion("ml.sky233.suiteki", MainActivity.this)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("获取到最新版本 - " + version);
                        builder.setMessage(update_text + "\n" + update_time);
                        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse(app_link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("已是最新版本");
                        builder.setMessage(update_text);
                        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                    break;
                case 2:
                    String[] str = (String[]) msg.obj;
                    //HitokotoUtils.d(String.valueOf(str));
                    Toast.makeText(MainActivity.this, str[2], Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onResume() {
        super.onResume();
        List<UriPermission> cache;
        path = path1;
        String setting = "0";
        if (Build.VERSION.SDK_INT >= 30) {
            saveSetting("id_mode", "true");
        }
        if (Build.VERSION.SDK_INT < 29) {
            saveSetting("id_mode", "false");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 29)
            setting = "1";
        if (Build.VERSION.SDK_INT >= 29) {
            cache = getContentResolver().getPersistedUriPermissions();
            if ((long) cache.size() == 0) {
                if (getSetting("pro").equals("1")) setting = "3";
                if (getSetting("pro").equals("0")) setting = "2";
            }
        }

        LinearLayout linearLayout = findViewById(R.id.card_layout);
        if (getSetting("open_mode").equals("false")) {
//          saveSetting("pro", "0");
            setting = "0";
        }
        Random ra = new Random();
        imageView_main.setImageResource(R.drawable.key);
        linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red));
        d("Permission:"+setting);
        switch (setting) {
            case "1":
                text = "未授权读写权限";
                break;
            case "2":
                text = "未授权Data目录权限";
                break;
            case "3":
                text = "未授权读写权限等";
                break;
            case "0":
                text = txt[ra.nextInt(txt.length)];
                textView_tag.setText("点击刷新");
                imageView_main.setImageResource(R.drawable.check_bold);
                linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.blue));
                break;
        }
        textView_title.setText(text);
    }

    public void pro() {
        if (Build.VERSION.SDK_INT >= 29)
            startFor(MainActivity.this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (getSetting("read").equals("1")) {
            if (!getSetting("prom").equals("1")) pro();
        } else {
            if (!getSetting("read").equals("1")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请先阅读隐私协议");
                builder.setMessage(R.string.text);
                builder.setPositiveButton("退出", (dialog, which) -> finish());
                builder.setNegativeButton("同意", (dialog, which) -> {
                    pro();
                    saveSetting("read", "1");
                    dialog.dismiss();
                });
                builder.setCancelable(false);
                builder.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_SEARCH);
                builder.show();
            }
        }
        creatList();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        String mode = getSetting("id_mode");
        String open = getSetting("open_mode");
        if (mode.equals("true")) {
            menu.findItem(R.id.id_mode).setChecked(true);
        }
        safe = "1";
        return true;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        d(String.valueOf(data.getData()));
        Uri uri;
        if (requestCode == REQUEST_CODE_FOR_DIR && (uri = data.getData()) != null) {
            saveSetting("prom", "1");
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            Log.d("Suiteki.test", String.valueOf(uri));
            d("CustomUri:"+String.valueOf(uri));
            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getKeyActivity.other = stringBuilder.toString();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, getKeyActivity.class);
            startActivity(intent);
        }
    }
}