package ml.sky233.suiteki.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NoScrollViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs) {
    // false 禁止ViewPager左右滑动。
    // true 普通ViewPager
    private var isScroll = false
    fun setScrollable(isScroll: Boolean) {
        this.isScroll = isScroll
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (!isScroll) {
            // 不允许拦截事件就返回false
            isScroll
        } else {
            // 正常ViewPager处理拦截事件就请求父类普通ViewPager中的onInterceptTouchEvent()
            super.onInterceptTouchEvent(ev)
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (!isScroll) {
            // 不允许消费事件就返回false.
            isScroll
        } else {
            // 正常ViewPager消耗事件就请求父类普通ViewPager中的onTouchEvent.
            super.onTouchEvent(ev)
        }
    }
}