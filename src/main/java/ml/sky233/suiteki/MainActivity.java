package ml.sky233.suiteki;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


import ml.sky233.Suiteki;
import ml.sky233.hitokoto.Hitokoto;
import ml.sky233.hitokoto.HitokotoUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ml.sky233.suiteki.SuitekiTool.*;


public class MainActivity extends AppCompatActivity {


    //变量集
    public static final int REQUEST_CODE_FOR_DIR = 12;
    String path = "";
    String path1 = "/storage/emulated/0/Android/data/com.mi.health/files/log/XiaomiFit.device.log";
    String path2 = "/storage/emulated/0/Android/data/com.xiaomi.wearable/files/log/Wearable.log";
    String text = "";
    String update = "";
    String safe = "0";
    String[] txt = {"若汝无介意，愿作耳边墙", "思君无情拒，唯吾孤自哀", "希愿回心意，眷恋越星海", "欲采栀子花，赠予手心中", "流水长月，依恋不舍", "拾栀雨落，忆梦汐潮", "思向呼唤，尔已忘期", "汝之憨态，流连忘返"};
    public static String code = "";
    public Activity MainActivity = this;


    public void cardview(View v) {
        switch (getSetting("pro")) {
            case "1":
                Uri packageURI1 = Uri.parse("package:" + getPackageName());
                Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI1);
                startActivity(intent1);
                break;
            case "2":
                startFor("/storage/emulated/0/Android/data/", this);
                break;
            case "3":
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                startActivity(intent);
                break;
            case "0":
                Random ra = new Random();
                TextView textView = findViewById(R.id.textView);
                text = txt[ra.nextInt(txt.length)];
                textView.setText(text);
                break;
        }
    }

    public void creatList() {
        String[] name = {
                "小米运动健康/小米穿戴",
                "ZeppLife/小米运动",
                "测试一言"
                //"test"
        };

        int[] menuImg = {
                R.drawable.ic_key_lock,
                R.drawable.ic_sign,
                R.drawable.ic_sign,
                //R.drawable.ic_sign,
        };

        ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity, R.layout.listview, name);
        ListView listView = (ListView) findViewById(R.id.list);
        Resources res = MainActivity.getResources();
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < name.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("img", menuImg[i]);
            listItem.put("name", name[i]);
            listItems.add(listItem);
        }

        SimpleAdapter sampleAdapter = new SimpleAdapter(MainActivity, listItems, R.layout.listview, new String[]{"img", "name"}, new int[]{R.id.image, R.id.title});
        listView.setAdapter(sampleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String result = String.valueOf(i);
                Log.d("Suiteki.test", result);
                switch (i) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, getKeyActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Thread thread = null;
                        thread = new Thread(new Runnable() {
                            public void run() {
                                Looper.prepare();
//                                HitokotoUtils.login("reportbug@sky233.ml","Sky1303868337");
                                Message msg = Message.obtain();
                                msg.obj = HitokotoUtils.get();
                                msg.what = 2;
                                handler.sendMessage(msg);
                                Looper.loop();
                            }
                        });
                        thread.start();
                        break;
                    case 1:
                        Intent intent1 = new Intent();
                        intent1.setClass(MainActivity.this, WebActivity.class);
                        startActivity(intent1);
                        break;
                }
                //Toast.makeText(MainActivity.this,result,Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_mode:
                if (item.isChecked() == true) {
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
//            case R.id.open_mode:
//                if (item.isChecked() == true) {
//                    item.setChecked(false);
//                    saveSetting("open_mode", "false");
//                    Log.d("Suiteki.test", getSetting("open_mode"));
//                    checkMode();
//                    Toast.makeText(getApplicationContext(), "已关闭权限提醒", Toast.LENGTH_SHORT).show();
//                } else {
//                    item.setChecked(true);
//                    saveSetting("open_mode", "true");
//                    Log.d("Suiteki.test", getSetting("open_mode"));
//                    checkMode();
//                    Toast.makeText(getApplicationContext(), "已开启权限提醒", Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.deleteFile:
                if (deleteDocumentFile(path, this)) {
                    Toast.makeText(this, "已经删除Log文件", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "删除Log文件失败", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.checkUpdate:
                Thread thread = null;
                Toast.makeText(MainActivity, "正在检查更新", Toast.LENGTH_SHORT).show();
                thread = new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();
                        getUpdate();
                        Looper.loop();
                    }
                });
                thread.start();
                break;
            case R.id.choose_log:
//                Intent intent2 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                Uri weChatUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Fcom.mi.health%2Ffiles%2Flog%2F");
//                intent2.putExtra(DocumentsContract.EXTRA_INITIAL_URI, weChatUri);
//                intent2.setType("application/octet-stream");
//                startActivityForResult(intent2, 2);
                chooseFile();
                break;
            case R.id.join_group:
                joinGroup();
                break;
            default:
                break;
        }
        return true;
    }

    public void chooseFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity);
        builder.setTitle("手动选择Log文件");
        builder.setMessage("选择名为\"XiaomiFit.device.log\"的文件");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Fcom.mi.health%2Ffiles%2Flog%2F");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                intent.setType("application/octet-stream");
                startActivityForResult(intent, 2);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void joinGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity);
        builder.setTitle("进群吹水 ヾ(≧▽≦*)o");
        builder.setMessage("Suiteki用户交流群 : \n\n" + "1群:648849444\n\n别问为什么只有1群,问就是没有人进(");
        builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("复制群号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(null, "648849444");
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void getUpdate() {

        OkHttpClient okClient = new OkHttpClient();
        Request request = new Request.Builder().url("https://sky233.ml/update/Suiteki/index.json").build();
        try {
            Response response = okClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String text = response.body().string();
                update = text;
            } else {
                String code = String.valueOf(response.code());
                update = code;
            }
            Log.d("Suiteki.test", update);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = Message.obtain();
        msg.obj = update;
        msg.what = 1;
        handler.sendMessage(msg);
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

                    if (version_code > getPackageVersion("ml.sky233.suiteki", MainActivity)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity);
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
                    Toast.makeText(MainActivity, str[2], Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void checkMode() {
        List<UriPermission> cache;
        path = path1;
        String setting = "0";
        if (Build.VERSION.SDK_INT >= 30) {
            saveSetting("id_mode", "true");
        }
        if (Build.VERSION.SDK_INT < 29) {
            saveSetting("id_mode", "false");
        }

        //saveSetting("pro", "0");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 29)
//          saveSetting("pro", "1");
            setting = "1";
        if (Build.VERSION.SDK_INT >= 29) {
            cache = getContentResolver().getPersistedUriPermissions();
            if (cache.stream().count() == 0) {
//              if (getSetting("pro").equals("1")) saveSetting("pro", "3");
//              if (getSetting("pro").equals("0")) saveSetting("pro", "2");
                if (getSetting("pro").equals("1")) setting = "3";
                if (getSetting("pro").equals("0")) setting = "2";
            }
        }

        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);

        LinearLayout linearLayout = findViewById(R.id.card_layout);
        if (getSetting("open_mode").equals("false")) {
//          saveSetting("pro", "0");
            setting = "0";
        }
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.key);
        switch (setting) {
            case "1":
                text = "未授权读写权限";
                textView.setText(text);
                linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity, R.color.red));
                break;
            case "2":
                text = "未授权Data目录权限";
                textView.setText(text);
                linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity, R.color.red));
                break;
            case "3":
                text = "未授权读写权限等";
                textView.setText(text);
                linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity, R.color.red));
                //text = "还有一个权限未获取 点击跳转";
                //textView2.setText(text);
                break;
            case "0":
                Random ra = new Random();
                text = txt[ra.nextInt(txt.length)];
                textView.setText(text);
                text = "点击刷新";
                imageView = findViewById(R.id.imageView);
                textView2.setText(text);
                imageView.setImageResource(R.drawable.check_bold);
                linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity, R.color.blue));
                break;
        }
    }

    public void onResume() {
        super.onResume();
        checkMode();
    }

    public void pro() {
        if (Build.VERSION.SDK_INT >= 29)
            startFor("/storage/emulated/0/Android/data/", MainActivity);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSetting("read").equals("1")) {
            if (!getSetting("prom").equals("1")) pro();
        } else {
            title();
        }
        creatList();
    }

//    public void pro(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("申请应用权限");
//        builder.setMessage("文件读写权限权限 \n外部访问权限 \n \n以上权限的用途均在隐私权限提及");
//        builder.setPositiveButton("拒绝", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
//        builder.setNegativeButton("授权", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                saveSetting("prom","1");
//                ActivityCompat.requestPermissions(MainActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                startFor("/storage/emulated/0/Android/data/",MainActivity);
//            }
//        });
//        builder.setCancelable(false);
//        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
//                    return true;
//                }
//                else {
//                    return false;
//                }
//            }
//        });
//        builder.show();
//
//    }

    /**
     * 酷安sb
     * 怎么还没过审核
     * 烦死了都
     */


    public void title() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请先阅读隐私协议");
        builder.setMessage(R.string.text);
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
//        builder.setNeutralButton("阅读协议", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Uri uri = Uri.parse("https://sky233.ml/privacy/");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//                title();
//            }
//        });
        builder.setNegativeButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pro();
                saveSetting("read", "1");
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

    public boolean onCreateOptionsMenu(Menu menu) {
        //通过getMenuInflater()方法得到MenuInflater对象，再调用它的inflate()方法就可以给当前活动创建菜单了，第一个参数：用于指定我们通过哪一个资源文件来创建菜单；第二个参数：用于指定我们的菜单项将添加到哪一个Menu对象当中。
        getMenuInflater().inflate(R.menu.menu, menu);
        String mode = getSetting("id_mode");
        String open = getSetting("open_mode");
        if (mode.equals("true")) {
            menu.findItem(R.id.id_mode).setChecked(true);
        }
//        if (open.equals("false")) {
//            menu.findItem(R.id.open_mode).setChecked(false);
//        }
        safe = "1";
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;

        if (requestCode == REQUEST_CODE_FOR_DIR && (uri = data.getData()) != null) {
            saveSetting("prom", "1");
            //Log.d("Suiteki.test", String.valueOf(data.getData()));
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Uri uri1 = null;
            uri1 = data.getData();
            Log.d("Suiteki.test", String.valueOf(uri1));
            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream = getContentResolver().openInputStream(uri1);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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