package ml.sky233.suiteki.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NoScrollListView extends ListView {
    public NoScrollListView(Context context) {
        super(context);
    }

    public NoScrollListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NoScrollListView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void onMeasure(int width,int height){
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(width,expandSpec);
    }
}
