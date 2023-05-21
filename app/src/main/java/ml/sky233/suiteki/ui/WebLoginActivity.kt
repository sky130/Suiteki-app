package ml.sky233.suiteki.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ml.sky233.suiteki.MainApplication.Companion.suiteki
import ml.sky233.suiteki.R
import ml.sky233.suiteki.databinding.ActivityWebLoginBinding
import ml.sky233.suiteki.util.TextUtils.lookFor
import ml.sky233.suiteki.util.UrlUtils.getParameter
import ml.sky233.suiteki.util.barTextToBlack

class WebLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebLoginBinding
    private val url =
        "https://account.xiaomi.com/fe/service/oauth2/authorize?skip_confirm=false&client_id=2882303761517383915&pt=0&scope=1+6000+16001+20000&redirect_uri=https%3A%2F%2Fhm.xiaomi.com%2Fwatch.do&_locale=zh_CN&response_type=code"
    var a = 0 //判断不要多开


    @SuppressLint("MissingInflatedId", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barTextToBlack()
        binding = ActivityWebLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (this.applicationContext.resources.configuration.uiMode != 0x21) {
            binding.toolbar.overflowIcon?.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.black
                ), PorterDuff.Mode.SRC_ATOP
            )
            binding.toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        val webSettings: WebSettings = binding.webview.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.allowContentAccess = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.supportMultipleWindows()
        binding.webview.setWebViewClient(object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (a == 0) {
                    if (url.lookFor("hm.xiaomi.com/watch.do?code=")) {
                        a++
                        suiteki.setCode(getParameter(url, "code"))
                        finish()
                    }
                }
            }
        })
        binding.webview.loadUrl(url)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}