package ml.sky233.suiteki.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.sky233.suiteki.MainApplication;
import ml.sky233.suiteki.adapter.SuiteGraphAdapter;
import ml.sky233.suiteki.bean.GraphObject;
import ml.sky233.suiteki.callback.GraphCallback;
import ml.sky233.util.Eson;

public class SuiteGraphView extends RecyclerView {
    String title = "";
    GraphCallback callback;

    public SuiteGraphView(@NonNull Context context) {
        super(context);
    }

    public SuiteGraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuiteGraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGraph(Context context, String jsonStr, GraphCallback callback) {
        this.callback = callback;
        ArrayList<GraphObject> graph = new ArrayList<>();
        title = Eson.getObjectText(Eson.toObject(jsonStr), "title");
        Object obj = Eson.getArray(Eson.toObject(jsonStr), "graphic");
        for (int i = 0; i < Eson.getArrayLength(obj); i++) {
            Object object = Eson.getArrayObject(obj, i);
            if (Eson.getObjectText(object, "type").equals("str")) {
                graph.add(new GraphObject(Eson.getObjectText(object, "context"), "",Eson.getObjectText(object, "style")));
            } else {
                graph.add(new GraphObject("", Eson.getObjectText(object, "context"),""));
            }
        }
        this.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        this.setAdapter(new SuiteGraphAdapter(context, title, graph));
        callback.onGraphSuccess(title,0);
    }


    public String getTitle() {
        return title;
    }
}
