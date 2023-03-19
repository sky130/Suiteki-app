package ml.sky233.suiteki;

import static ml.sky233.suiteki.util.TextUtils.lookFor;
import static ml.sky233.suiteki.util.UrlUtils.getParameter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    String url;
    int a = 0;//判断不要多开

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        if (this.getApplicationContext().getResources().getConfiguration().uiMode != 0x21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initView();
        webView.loadUrl(url);
    }

    public void initView() {
        webView = findViewById(R.id.web_web_view);
        url = "https://account.xiaomi.com/fe/service/oauth2/authorize?skip_confirm=false&client_id=2882303761517383915&pt=0&scope=1+6000+16001+20000&redirect_uri=https%3A%2F%2Fhm.xiaomi.com%2Fwatch.do&_locale=" + "zh_ch" + "&response_type=code";
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
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
                        Intent intent = new Intent();
                        intent.putExtra("code", getParameter(url, "code"));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }


}