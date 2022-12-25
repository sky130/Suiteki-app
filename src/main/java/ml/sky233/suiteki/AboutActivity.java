package ml.sky233.suiteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.Objects;

import ml.sky233.suiteki.builder.AdapterBuilder;
import ml.sky233.suiteki.builder.DialogBuilder;
import ml.sky233.suiteki.builder.IntentBuilder;
import ml.sky233.suiteki.view.NoScrollListView;

public class AboutActivity extends AppCompatActivity {
    NoScrollListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    public void initView() {
        this.setTitle(R.string.about_app);//设置标题
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//开启左上角返回键
        listView = findViewById(R.id.about_listView);
        int[] img = {R.drawable.sky233, R.drawable.cat, R.drawable.guodong, R.drawable.chen,R.drawable.shinezz, R.drawable.ic_lib, R.drawable.ic_lib, R.drawable.ic_lib, R.drawable.ic_text,R.drawable.ic_text,};
        String[] developers = getResources().getStringArray(R.array.developers);
        String[] title = getResources().getStringArray(R.array.title);
        listView.setAdapter(AdapterBuilder.getAboutAdapter(this,img,developers,title));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(IntentBuilder.build("https://sky233.ml/"));
                        break;
                    case 1:
                        startActivity(IntentBuilder.build("http://www.coolapk.com/u/1362030"));
                        break;
                    case 2:
                        startActivity(IntentBuilder.build("http://www.coolapk.com/u/1032987"));
                        break;
                    case 3:
                        startActivity(IntentBuilder.build("http://www.coolapk.com/u/3736924"));
                        break;
                    case 4:
                        startActivity(IntentBuilder.build("https://shinezz.cc"));
                        break;
                    case 5:
                        startActivity(IntentBuilder.build("https://github.com/sky130/Suiteki/"));
                        break;
                    case 6:
                        startActivity(IntentBuilder.build("https://github.com/argrento/huami-token"));
                        break;
                    case 7:
                        startActivity(IntentBuilder.build("https://github.com/square/okhttp"));
                        break;
                    case 8:
                        DialogBuilder.showText(AboutActivity.this);
                        break;
                    case 9:
                        DialogBuilder.showLicense(AboutActivity.this);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();//左上角的返回
        return true;
    }
}