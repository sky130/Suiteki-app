package ml.sky233.suiteki.adapter;

import static ml.sky233.suiteki.util.ViewUtils.addTouchScale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.sky233.suiteki.R;
import ml.sky233.suiteki.bean.AppObject;
import ml.sky233.suiteki.bean.TipsObject;
import ml.sky233.suiteki.widget.RecyclerExtras.OnItemClickListener;
import ml.sky233.suiteki.widget.RecyclerExtras.OnItemLongClickListener;
import ml.sky233.suiteki.widget.RecyclerExtras.OnItemTouchListener;

public class TipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener, OnItemLongClickListener, OnItemTouchListener {
    public Context mContext;

    private ArrayList<TipsObject> mPublicArray;

    public TipsAdapter(Context context, ArrayList<TipsObject> publicArray) {
        mContext = context;
        mPublicArray = publicArray;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mPublicArray.size();
    }

    // 创建列表项的视图持有者
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_linear.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_app_blue, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    @SuppressLint({"RecyclerView", "ClickableViewAccessibility"})
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        addTouchScale(holder.itemView);
        holder.tv_title.setText(mPublicArray.get(position).title);
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v, position);
            }
            return true;
        });
    }


    // 获取列表项的类型
    public int getItemViewType(int position) {
        // 这里返回每项的类型，开发者可自定义头部类型与一般类型，
        // 然后在onCreateViewHolder方法中根据类型加载不同的布局，从而实现带头部的网格布局
        return 0;
    }

    // 获取列表项的编号
    public long getItemId(int position) {
        return position;
    }


    // 定义列表项的视图持有者
    public static class ItemHolder extends RecyclerView.ViewHolder {
        public TextView tv_title; // 声明列表项标题的文本视图

        public ItemHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.item_app_title);
        }
    }

    // 声明列表项的点击监听器对象
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // 声明列表项的长按监听器对象
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    // 声明列表项的触摸监听器对象
    private OnItemTouchListener mOnItemTouchListener;

    public void setOnItemTouchListener(OnItemTouchListener listener) {
        this.mOnItemTouchListener = listener;
    }

    // 处理列表项的点击事件
    @SuppressLint("DefaultLocale")
    public void onItemClick(View view, int position) {
//        String desc = String.format("您点击了第%d项，标题是%s", position + 1,
//                mPublicArray.get(position).title);
//        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    // 处理列表项的长按事件
    @SuppressLint("DefaultLocale")
    public void onItemLongClick(View view, int position) {
        String desc = String.format("您长按了第%d项，标题是%s", position + 1,
                mPublicArray.get(position).title);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    // 处理列表项的触摸事件
    public boolean onItemTouch(View view, MotionEvent event) {
        String desc = "列表被触摸";
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
        return false;
    }

}
