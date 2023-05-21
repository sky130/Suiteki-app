package ml.sky233.suiteki.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ml.sky233.suiteki.R
import ml.sky233.suiteki.util.startActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread {
            try {
                Thread.sleep(200)
                startActivity<MainActivity> {}
                finish() //关闭当前活动
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}