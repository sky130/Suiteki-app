package ml.sky233.suiteki;

import static ml.sky233.suiteki.MainApplication.thread;
import static ml.sky233.suiteki.util.Text.lookFor;
import static ml.sky233.suiteki.util.Url.getParameter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

import ml.sky233.suiteki.builder.IntentBuilder;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    String url;
    int a = 0;//判断不要多开


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        webView.loadUrl(url);
    }

    public void initView() {
        webView = findViewById(R.id.web_webview);
        url = "https://account.xiaomi.com/fe/service/oauth2/authorize?skip_confirm=false&client_id=2882303761517383915&pt=0&scope=1+6000+16001+20000&redirect_uri=https%3A%2F%2Fhm.xiaomi.com%2Fwatch.do&_locale=" + R.string.url_language + "&response_type=code";
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//开启左上角返回键
        this.setTitle(R.string.web_title);//设置标题
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.supportMultipleWindows();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (a==0) {
                    if (lookFor(url, "hm.xiaomi.com/watch.do?code=")) {
                        a++;
                        thread.initCode(getParameter(url, "code"));
                        startActivity(IntentBuilder.build(WebActivity.this, GetKeyActivity.class,3));
                        LoginActivity.activity.finish();
                        WebActivity.this.finish();
                    }
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