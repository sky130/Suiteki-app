package ml.sky233.suiteki.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ml.sky233.suiteki.R;

@SuppressLint("AppCompatCustomView")
public class SuitekiButton extends Button {

    public SuitekiButton(@NonNull Context context) {
        super(context,null,0,R.style.Theme_Suiteki_Button);
    }

    public SuitekiButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuitekiButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr,R.style.Theme_Suiteki_Button);
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
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (isClickable()) {
            if (paramMotionEvent.getAction() != 0) {
                if (3 == paramMotionEvent.getAction() || 1 == paramMotionEvent.getAction())
                    animateToNormal();
                return super.onTouchEvent(paramMotionEvent);
            }
            animateToPress();
        }
        return super.onTouchEvent(paramMotionEvent);
    }
}