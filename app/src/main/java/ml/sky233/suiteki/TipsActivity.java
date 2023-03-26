package ml.sky233.suiteki;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ml.sky233.suiteki.adapter.TipsAdapter;
import ml.sky233.suiteki.bean.TipsObject;
import ml.sky233.suiteki.util.Eson.EsonUtils;
import ml.sky233.suiteki.util.MsgUtils;
import ml.sky233.suiteki.util.ViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TipsActivity extends AppCompatActivity {
    public static final String url = "https://sky233.ml/update/Suiteki/graph/";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        if (this.getApplicationContext().getResources().getConfiguration().uiMode != 0x21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        recyclerView = findViewById(R.id.recycler);
        View v = findViewById(R.id.title_tips_back);
        ViewUtils.addTouchScale(v);
        v.setOnClickListener((i) -> {
            TipsActivity.this.finish();
        });
        new Thread(() -> {
            Request request = new Request.Builder().url(url).build();
            OkHttpClient okClient = new OkHttpClient();
            try {
                Response response = okClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    handler.sendMessage(MsgUtils.build(Objects.requireNonNull(response.body()).string(), 0));
                } else
                    handler.sendMessage(MsgUtils.build("", 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Object obj = EsonUtils.getArray(EsonUtils.toObject((String) msg.obj), "list");
                    ArrayList<TipsObject> list = new ArrayList<>();
                    for (int i = 0; i < EsonUtils.getArrayLength(obj); i++) {
                        list.add(new TipsObject(EsonUtils.getObjectText(EsonUtils.getArrayObject(obj, i), "title"), EsonUtils.getObjectText(EsonUtils.getArrayObject(obj, i), "url")));
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(TipsActivity.this, LinearLayoutManager.VERTICAL, false));
                    TipsAdapter adapter = new TipsAdapter(TipsActivity.this, list);
                    adapter.setOnItemClickListener((v, i) -> {
                        GraphActivity.setGraphUrl(list.get(i).url);
                        TipsActivity.this.startActivity(new Intent(TipsActivity.this, GraphActivity.class));
                    });
                    recyclerView.setAdapter(adapter);
                    break;
                case 1:

                    break;
            }
        }
    };
}