package ml.sky233.suiteki.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import ml.sky233.suiteki.R;

public class AnimationUtils {
    Animation fadeIn,fadeOut;

    public AnimationUtils(Context context){
        // 加载淡入动画
        fadeIn = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_in);
        // 加载淡出动画
        fadeOut = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_out);
    }

    public void fadeIn(View view){
        view.startAnimation(fadeIn);
    }

    public void fadeOut(View view){
        view.startAnimation(fadeOut);
    }
}
