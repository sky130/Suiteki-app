package ml.sky233.suiteki;

import static ml.sky233.suiteki.MainApplication.REQUEST_CODE_FOR_DIR;
import static ml.sky233.suiteki.MainApplication.thread;
import static ml.sky233.suiteki.util.File.startFor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ml.sky233.suiteki.builder.AdapterBuilder;
import ml.sky233.suiteki.builder.DialogBuilder;
import ml.sky233.suiteki.builder.IntentBuilder;
import ml.sky233.suiteki.util.Setting;


@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView cardView_title, cardView_tag;
    ListView listView;
    LinearLayout cardView;
    public static Context context;
    Random random = new Random();
    String[] hitokoto = {"若汝无介意，愿作耳边墙", "思君无情拒，唯吾孤自哀", "希愿回心意，眷恋越星海", "欲采栀子花，赠予手心中", "流水长月，依恋不舍", "拾栀雨落，忆梦汐潮", "思向呼唤，尔已忘期", "汝之憨态，流连忘返"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DialogBuilder.privacyText(this);
        initView();
    }

    public void onResume() {
        super.onResume();
        int permission = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permission = 1;
        if (Build.VERSION.SDK_INT < 33 && Build.VERSION.SDK_INT >= 26) {
            int length = getContentResolver().getPersistedUriPermissions().size();
            if (length == 0 && permission == 1)
                permission = 2;
            else if (length == 0 && permission == 0)
                permission = 3;
        }
        if (Setting.getValue("sync"))
            permission = 0;
        cardView_tag.setText(R.string.click_get_permission);
        imageView.setImageResource(R.drawable.ic_key_cardview);
        switch (permission) {
            case 0://获取全部权限
                cardView_title.setText(hitokoto[random.nextInt(hitokoto.length)]);
                cardView_tag.setText(R.string.click_review);
                imageView.setImageResource(R.drawable.ic_check);
                break;
            case 1://仅获取Android/Data访问权限
                cardView_title.setText(R.string.permission_file);
                break;
            case 2://全部权限未获取
                cardView_title.setText(R.string.permission_all);
                break;
            case 3://仅获取读写目录权限
                cardView_title.setText(R.string.permission_data);
                break;
        }
        Setting.setString("app_permission", String.valueOf(permission));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_settings://设置
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.join_group:
                DialogBuilder.joinGroup(MainActivity.this);
                break;
            case R.id.check_update:
                thread.initActivity(MainActivity.this);
                new Thread(thread.updateApp).start();
                break;
            case R.id.app_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return true;
    }

    public void initView() {
        imageView = findViewById(R.id.main_imageView);//CardView中的图标
        cardView = findViewById(R.id.card_layout);//CardView的背景
        cardView_title = findViewById(R.id.main_cardView_title);//CardView标题
        cardView_tag = findViewById(R.id.main_cardView_tag);//CardView底部文字
        listView = findViewById(R.id.main_listview);//ListView
        context = this;//自定义Context

        //构建列表
        listView.setAdapter(AdapterBuilder.getAppAdapter(this));
        listView.setOnItemClickListener((parent, view, i, l) -> {
            String result = String.valueOf(i);
            switch (i) {
                case 0:
                    startActivity(IntentBuilder.build(MainActivity.this, GetKeyActivity.class, 1));
                    break;
                case 1:
                    startActivity(IntentBuilder.build(MainActivity.this, LoginActivity.class, 1));
                    break;
            }
        });

        if (Setting.getValue("app_update")) {
            thread.initActivity(MainActivity.this);
            new Thread(thread.updateApp).start();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_cardView:
                switch (Integer.parseInt(Setting.getString("app_permission"))) {
                    case 0://获取全部权限
                        cardView_title.setText(hitokoto[random.nextInt(hitokoto.length)]);
                        break;
                    case 1://仅获取Android/Data访问权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        break;
                    case 2://全部权限未获取
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        break;
                    case 3://仅获取读写目录权限
                        startFor(MainActivity.this);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_DIR && data.getData() != null) {
            getContentResolver().takePersistableUriPermission(data.getData(), data.getFlags() & (
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
        }
    }

}