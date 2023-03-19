package ml.sky233.suiteki.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class ViewUtils {


    @SuppressLint("ClickableViewAccessibility")
    public static void addTouchScale(View view) {
        view.setOnTouchListener((v, event) -> {
            final float scale = 0.9f;
            final long duration = 150;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(scale).scaleY(scale).setDuration(duration).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1).scaleY(1).setDuration(duration).start();
                    break;
                default:
                    break;
            }
            return v.onTouchEvent(event);
        });//today is crazy Thursday, who vivo 50 to have KFC?

    }

    @SuppressLint("Recycle")
    private void animateToNormal() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250L);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 0.92F, 1.0F);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 0.92F, 1.0F);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 0.7F, 1.0F);
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3);
        animatorSet.start();
    }

    @SuppressLint("Recycle")
    private void animateToPress() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200L);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.0F, 0.92F);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.0F, 0.92F);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 1.0F, 0.7F);
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3);
        animatorSet.start();
    }}
