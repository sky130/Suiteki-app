package ml.sky233.suiteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Objects;

import static ml.sky233.suiteki.MainApplication.TAG;
import static ml.sky233.suiteki.MainApplication.thread;
import static ml.sky233.suiteki.util.File.getLogText;
import static ml.sky233.suiteki.MainApplication.suiteki;

import ml.sky233.SuitekiObject;
import ml.sky233.suiteki.builder.IntentBuilder;
import ml.sky233.suiteki.util.AppThread;
import ml.sky233.suiteki.util.Setting;

public class GetKeyActivity extends AppCompatActivity {
    public static ListView listView;
    public static String[] AuthKeys, Macs, Names, Ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_key);
        initView();//初始化
    }

    public void initView() {
        listView = findViewById(R.id.get_key_listview);
        int app_mode = 0;
        String app_value = "";
        this.setTitle(R.string.get_key_title);//设置标题
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//开启左上角返回键
        if (getIntent().getExtras() != null) {//判断数据是否为空
            app_mode = getIntent().getIntExtra("app_mode", 0);
            app_value = getIntent().getStringExtra("app_value") != null ? getIntent().getStringExtra("app_value") : "";
        }
        thread.initActivity(GetKeyActivity.this);
        if (Build.VERSION.SDK_INT >= 32)
            Setting.setValue("all_mode", true);
        if (Build.VERSION.SDK_INT > 29)
            Setting.setValue("read_mode", true);
        if (Setting.getValue("all_mode"))
            app_mode = 4;
        switch (app_mode) {
            case 1://普通模式
                new Thread(thread.getKey).start();
                break;
            case 2://在线获取
                new Thread(thread.getHuami).start();
                break;
            case 3://小米账号登录
                new Thread(thread.loginMi).start();
                break;
            case 4://全兼容模式
                new Thread(thread.getZip).start();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();//左上角的返回
        return true;
    }
}