package ml.sky233.suiteki;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class StartPageActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(() -> {
            try {
                sleep(200);
                startActivity(new Intent(StartPageActivity.this, MainTestActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();//关闭当前活动
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}


