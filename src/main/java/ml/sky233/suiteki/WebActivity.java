package ml.sky233.suiteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ml.sky233.suiteki.MainActivity;
import ml.sky233.suiteki.getKeyActivity;

public class WebActivity extends AppCompatActivity {
    String a = "0";


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("登录小米账号");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);

        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url

                view.loadUrl(url);
                //返回true
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //页面加载完成的回调方法
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String txt = url;
                if(a == "0"){
                    if(txt.length() == 71){
                        a = "1";
                        Log.d("Suiteki.test",url);
                        getKeyActivity.code = getTextRight(txt,35);
                        Log.d("Suiteki.test",getKeyActivity.code);
                        Intent intent = new Intent();
                        intent = new Intent(WebActivity.this, getKeyActivity.class);
                        startActivity(intent);
                        WebActivity.this.finish();

                    }
                }

            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result){

                return super.onJsAlert(view, url, message, result);
            }

        });
        webView.loadUrl("https://account.xiaomi.com/fe/service/oauth2/authorize?skip_confirm=false&client_id=2882303761517383915&pt=0&scope=1+6000+16001+20000&redirect_uri=https%3A%2F%2Fhm.xiaomi.com%2Fwatch.do&_locale=zh_CN&response_type=code");
    }



    private static String getTextRight(String str, int len) {
        if (!"".equals(str) && len > 0) {
            if (len > str.length()) {
                return str;
            } else {
                int start = str.length() - len;
                return str.substring(start, str.length());
            }
        } else {
            return "";
        }
    }
}