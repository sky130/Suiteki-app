package ml.sky233.suiteki.adapter

import android.view.MotionEvent
import android.view.View

class RecyclerExtras {
    // 定义一个循环视图列表项的点击监听器接口
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    // 定义一个循环视图列表项的长按监听器接口
    interface OnItemLongClickListener {
        fun onItemLongClick(view: View?, position: Int)
    }

    // 定义一个循环视图列表项的删除监听器接口
    interface OnItemDeleteClickListener {
        fun onItemDeleteClick(view: View?, position: Int)
    }

    // 定义一个循环视图列表项的长按监听器接口
    interface OnItemTouchListener {
        fun onItemTouch(view: View?, event: MotionEvent?): Boolean
    }
}