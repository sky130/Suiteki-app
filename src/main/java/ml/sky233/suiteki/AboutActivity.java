package ml.sky233.suiteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ml.sky233.suiteki.SuitekiTool.saveSetting;


public class AboutActivity extends AppCompatActivity {

    public Activity AboutActivity = this;

    public void creatList() {
        String[] title = {
                "Sky233",
                "月下鱼猫",
                "一個小果冻",
                "Suiteki",
                "Huami-token",
                "Okhttp",
                "隐私政策"
                //"test"
        };

        int[] menuImg = {
                R.drawable.ic_user,
                R.drawable.ic_user,
                R.drawable.ic_user,
                R.drawable.ic_lib,
                R.drawable.ic_lib,
                R.drawable.ic_lib,
                R.drawable.ic_text,
                //R.drawable.ic_sign,
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

        ArrayAdapter adapter = new ArrayAdapter<String>(AboutActivity, R.layout.listview_about, title);
        ListView listView = (ListView) findViewById(R.id.about_list);
        Resources res = AboutActivity.getResources();
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < title.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("img", menuImg[i]);
            listItem.put("title", title[i]);
            listItem.put("note", note[i]);
            listItems.add(listItem);
        }

        SimpleAdapter sampleAdapter = new SimpleAdapter(AboutActivity, listItems, R.layout.listview_about, new String[]{"img", "title", "note"}, new int[]{R.id.image_about, R.id.title_about, R.id.note_about});
        listView.setAdapter(sampleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String result = String.valueOf(i);
                Uri uri = null;
                Intent intent = null;
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
                        title();
                        break;

                }
                //Toast.makeText(MainActivity.this,result,Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        creatList();

        this.setTitle("关于应用");
//        ScrollView scrollView = (ScrollView) findViewById(R.id.id_scrollView);
//        scrollView.setVerticalScrollBarEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public void title() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("隐私协议");
        builder.setMessage(R.string.text);
        builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

//    public void sky233(View view) {
//        Uri uri = Uri.parse("https://sky233.ml/");
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//    }
//
//    public void Suiteki(View view) {
//        Uri uri = Uri.parse("https://github.com/sky130/Suiteki");
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//    }
//
//    public void Okhttp(View view) {
//        Uri uri = Uri.parse("https://github.com/square/okhttp");
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//    }
//
//    public void huami_token(View view) {
//        Uri uri = Uri.parse("https://github.com/square/okhttp");
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//    }
//
//    public void privacy(View view) {
//        Uri uri = Uri.parse("https://sky233.ml/privacy/");
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//    }
}