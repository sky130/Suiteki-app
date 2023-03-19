package ml.sky233.suiteki;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Objects;

import ml.sky233.suiteki.callback.GraphCallback;
import ml.sky233.suiteki.util.MsgBuilder;
import ml.sky233.suiteki.util.ViewUtils;
import ml.sky233.suiteki.widget.SuiteGraphView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GraphActivity extends AppCompatActivity implements GraphCallback {
    SuiteGraphView view;
    ProgressBar progressBar;
    TextView title_view;
    public static String graph_url = "https://sky233.ml/update/Suiteki/graph/1.json";

    public static void setGraphUrl(String url) {
        graph_url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        if (this.getApplicationContext().getResources().getConfiguration().uiMode != 0x21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        View v = findViewById(R.id.title_graph_back);
        ViewUtils.addTouchScale(v);
        v.setOnClickListener((i) -> {
            GraphActivity.this.finish();
        });
        view = findViewById(R.id.graph_view);
        title_view = findViewById(R.id.title_graph);
        progressBar = findViewById(R.id.progress_graph);
        new Thread(() -> {
            Request request = new Request.Builder().url(graph_url).build();
            OkHttpClient okClient = new OkHttpClient();
            try {
                Response response = okClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    handler.sendMessage(MsgBuilder.build(Objects.requireNonNull(response.body()).string(), 0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            view.setGraph(GraphActivity.this, (String) msg.obj, GraphActivity.this);
        }
    };

    @Override
    public void onGraphSuccess(String title, int status) {
        progressBar.setVisibility(View.INVISIBLE);
        title_view.setText(title);
    }
}