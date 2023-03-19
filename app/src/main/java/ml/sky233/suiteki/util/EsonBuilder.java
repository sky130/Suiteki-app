package ml.sky233.suiteki.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class EsonBuilder {
    public final static int JSON_ARRAY = 1, JSON_OBJECT = 2;
    int mode = 0;
    JSONObject obj;
    JSONArray objs;

    public EsonBuilder(String json) {
        this.obj = (JSONObject) EsonUtils.toObject(json);
    }

    public EsonBuilder(JSONObject json) {
        this.obj = json;
    }


    public EsonBuilder(JSONArray json) {
        this.objs = json;
    }


    public EsonBuilder() {
        this.mode = JSON_OBJECT;
        this.obj = new JSONObject();
    }

    public EsonBuilder(int mode) {
        this.mode = mode;
        switch (this.mode) {
            case 1:
                this.objs = new JSONArray();
                break;
            case 2:
                this.obj = new JSONObject();
                break;
        }
    }


    public EsonBuilder put(String name, Object object) {
        try {
            this.obj.put(name, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public EsonBuilder put(EsonBuilder object) {
        this.objs.put(object.toJsonObject());
        return this;
    }

    public EsonBuilder putArray(String name, JSONArray jsonArray) {
        try {
            this.obj.put(name, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONArray toJsonArray() {
        if (mode == JSON_ARRAY) {
            return this.objs;
        }
        return null;
    }

    public JSONObject toJsonObject() {
        if (mode == JSON_OBJECT) {
            return this.obj;
        }
        return null;
    }

    public EsonBuilder putItem(HashMap<String, String> map) {
        if (this.mode == 2) {
            Iterator<String> iterable_key = map.keySet().iterator();
            Iterator<String> iterable_value = map.values().iterator();
            while (iterable_key.hasNext()) {
                EsonBuilder eson = new EsonBuilder();
                eson.put(iterable_key.next(), iterable_value.next());
                this.objs.put(eson);
            }
        }
        return this;
    }

    public String toString() {
        try {
            switch (this.mode) {
                case 1:
                    return this.objs.toString();
                case 2:
                    return this.obj.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
