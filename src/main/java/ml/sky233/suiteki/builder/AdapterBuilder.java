package ml.sky233.suiteki.builder;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ml.sky233.suiteki.MainActivity;
import ml.sky233.suiteki.R;

public class AdapterBuilder {

    public static SimpleAdapter getAppAdapter(Context context){
        String[] title = {"小米运动健康/小米穿戴", "ZeppLife/小米运动"};
        int[] img = {R.drawable.ic_key_lock, R.drawable.ic_sign};
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("title", title[i]);
            listItem.put("img", img[i]);
            listItems.add(listItem);
        }
        return new SimpleAdapter(context, listItems, R.layout.main_listview_app, new String[]{"title", "img"}, new int[]{R.id.title, R.id.image});
    }

    public static SimpleAdapter getKeyAdapter(Context context,String[] macs,String[] names){
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < macs.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("mac", macs[i]);
            listItem.put("name", names[i]);
            listItems.add(listItem);
        }
        return new SimpleAdapter(context, listItems, R.layout.get_key_listview, new String[]{"name", "mac"}, new int[]{R.id.item_title,R.id.item_mac});
    }

    public static SimpleAdapter getAboutAdapter(Context context,int[] id,String[] d,String[] t){
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < d.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("i",id[i]);
            listItem.put("d", d[i]);
            listItem.put("t", t[i]);
            listItems.add(listItem);
        }
        return new SimpleAdapter(context,listItems,R.layout.about_listview_item,new String[]{"i","d","t"},new int[]{R.id.about_image,R.id.about_title,R.id.about_note});
    }

}
