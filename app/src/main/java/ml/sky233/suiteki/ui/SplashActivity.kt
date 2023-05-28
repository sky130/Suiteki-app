package ml.sky233.suiteki.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ml.sky233.suiteki.R
import ml.sky233.suiteki.util.SettingUtils
import ml.sky233.suiteki.util.ActivityUtils.startActivity
import ml.sky233.suiteki.util.ActivityUtils.barTextToBlack

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread {
            try {
                Thread.sleep(200)
                if (SettingUtils.getBoolean("first_start"))
                    startActivity<MainActivity>()
                else
                    startActivity<WelcomeActivity>()
                finish() //关闭当前活动
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}