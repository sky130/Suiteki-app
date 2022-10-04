package ml.sky233.suiteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AboutActivity extends AppCompatActivity {
    ListView listView;

    public void makeList() {
        String[] title = {
                "Sky233",
                "月下鱼猫",
                "一個小果冻",
                "Suiteki",
                "Huami-token",
                "Okhttp",
                "隐私政策"
        };
        int[] menuImg = {
                R.drawable.ic_user,
                R.drawable.ic_user,
                R.drawable.ic_user,
                R.drawable.ic_lib,
                R.drawable.ic_lib,
                R.drawable.ic_lib,
                R.drawable.ic_text,
        };
        String[] note = {
                "开发者,图标设计,UI设计",
                "宣传图设计",
                "文字设计",
                "一个可以获取小米手环的AuthKey的类库ヾ(≧▽≦*)o",
                "Script to obtain watch or band bluetooth token from Huami servers",
                "Square’s meticulous HTTP client for the JVM, Android, and GraalVM.",
                "已经阅读并同意"
        };
        listView = findViewById(R.id.about_list);
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("img", menuImg[i]);
            listItem.put("title", title[i]);
            listItem.put("note", note[i]);
            listItems.add(listItem);
        }
        SimpleAdapter sampleAdapter = new SimpleAdapter(AboutActivity.this, listItems, R.layout.listview_about, new String[]{"img", "title", "note"}, new int[]{R.id.image_about, R.id.title_about, R.id.note_about});
        listView.setAdapter(sampleAdapter);
        listView.setOnItemClickListener((parent, view, i, l) -> {
            String result = String.valueOf(i);
            Uri uri;
            Intent intent;
            Log.d("Suiteki.test", result);
            switch (i) {
                case 0:
                    uri = Uri.parse("https://sky233.ml/");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case 3:
                    uri = Uri.parse("https://github.com/sky130/Suiteki");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case 4:
                    uri = Uri.parse("https://github.com/argrento/huami-token");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case 5:
                    uri = Uri.parse("https://github.com/square/okhttp");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case 6:
                    AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                    builder.setTitle("隐私协议");
                    builder.setMessage(R.string.text);
                    builder.setPositiveButton("返回", (dialog, which) -> dialog.dismiss());
                    builder.show();
                    break;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        makeList();
        this.setTitle("关于应用");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}