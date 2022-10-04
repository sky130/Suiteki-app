package ml.sky233.suiteki.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EsonUtils {

    public static Object toObject(String var1) {
        try {
            return new JSONObject(var1);
        } catch (JSONException var3) {
            return null;
        }
    }

    public static Object getObject(Object var1, String var2) {
        JSONObject var3 = (JSONObject) var1;
        if (var3 == null) {
            return null;
        } else {
            try {
                return var3.getJSONObject(var2);
            } catch (JSONException var5) {
                return null;
            }
        }
    }

    public static String getObjectText(Object var1, String var2) {
        JSONObject var3 = (JSONObject) var1;
        if (var3 == null) {
            return "";
        } else {
            try {
                return var3.getString(var2);
            } catch (JSONException var5) {
                return "";
            }
        }
    }

    public static Object getArray(Object var1, String var2) {
        JSONObject var3 = (JSONObject) var1;
        if (var3 == null) {
            return null;
        } else {
            try {
                return var3.getJSONArray(var2);
            } catch (JSONException var5) {
                return null;
            }
        }
    }

    public static int getArrayLength(Object var1) {
        JSONArray var2 = (JSONArray) var1;
        return var2 == null ? 0 : var2.length();
    }

    public static Object getArrayObject(Object var1, int var2) {
        JSONArray var3 = (JSONArray) var1;
        if (var3 == null) {
            return null;
        } else {
            try {
                return var3.getJSONObject(var2);
            } catch (JSONException var5) {
                return null;
            }
        }
    }

}
