package ml.sky233.suiteki.widget;

import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

public class RecyclerExtras {

    // 定义一个循环视图列表项的点击监听器接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position) ;
    }

    // 定义一个循环视图列表项的长按监听器接口
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    // 定义一个循环视图列表项的删除监听器接口
    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(View view, int position);
    }

    // 定义一个循环视图列表项的长按监听器接口
    public interface OnItemTouchListener {
        boolean onItemTouch(View view, MotionEvent event);
    }

}