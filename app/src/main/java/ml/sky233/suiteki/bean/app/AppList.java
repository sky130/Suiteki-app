package ml.sky233.suiteki.bean.app;

import static ml.sky233.suiteki.MainApplication.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ml.sky233.suiteki.util.Eson.EsonBuilder;
import ml.sky233.suiteki.util.Eson.EsonUtils;
import ml.sky233.suiteki.util.FileUtils;

@SuppressLint("SdCardPath")
public class AppList {
    private List<AppInfo> mList;

    private static String json_path = "/data/data/ml.sky233.suiteki/files/app_list.json";
    private String json_data = "";

    public AppList(Context context) {
        mList = new ArrayList<>();
        json_path = context.getFilesDir().toString() + "/app_list.json";
        json_data = FileUtils.getFileText(json_path);
        if (json_data.equals("")) {
            json_data = createAppJson();
        } else {
            Object objs = EsonUtils.getArray(EsonUtils.toObject(json_data), "data");
            for (int i = 0; i < EsonUtils.getArrayLength(objs); i++) {
                Object obj = EsonUtils.getArrayObject(objs, i);
                AppInfo appInfo = new AppInfo(EsonUtils.getObjectText(obj, "appName"),
                        EsonUtils.getObjectText(obj, "vender"),
                        EsonUtils.getObjectText(obj, "description"),
                        EsonUtils.getObjectInt(obj, "appId"),
                        EsonUtils.getObjectText(obj, "version"));
                mList.add(appInfo);
            }
        }
    }

    public List<AppInfo> addAppInfo(AppInfo appInfo) {
        for (int i = 0; i < mList.size(); i++) {
            AppInfo app = mList.get(i);
            if (app.appId == appInfo.appId) {
                mList.set(i, appInfo);
                saveData();
                return this.mList;
            }
        }
        this.mList.add(appInfo);
        saveData();
        return this.mList;
    }

    public String toJsonStr() {
        EsonBuilder ebo, eba = new EsonBuilder(EsonBuilder.JSON_ARRAY);
        for (AppInfo appInfo : mList) {
            ebo = new EsonBuilder(EsonBuilder.JSON_OBJECT);
            ebo.put("appName", appInfo.appName)
                    .put("appId", appInfo.appId)
                    .put("vender", appInfo.vender)
                    .put("version", appInfo.version)
                    .put("description", appInfo.description);
            eba.put(ebo);
        }
        ebo = new EsonBuilder(EsonBuilder.JSON_OBJECT);
        ebo.putArray("data", eba.toJsonArray());
        return ebo.toString();
    }

    public void delApp(int appId) {
        for (int i = 0; i < mList.size(); i++) {
            AppInfo appInfo = mList.get(i);
            if (appInfo.appId == appId) {
                mList.remove(i);
                Log.d(TAG, "index:" + i);
                saveData();
                return;
            }
        }
    }

    public void delApp(AppInfo app) {
        int appId = app.appId;
        for (int i = 0; i < mList.size(); i++) {
            AppInfo appInfo = mList.get(i);
            if (appInfo.appId == appId) {
                mList.remove(i);
                saveData();
                return;
            }
        }
    }

    public AppInfo getApp(int appId) {
        for (AppInfo appInfo : mList)
            if (appInfo.appId == appId)
                return appInfo;
        return null;
    }

    public List<AppInfo> getList() {
        return mList;
    }

    public void saveData() {
        Log.d(TAG, toJsonStr());
        FileUtils.writeFileText(json_path, toJsonStr());
    }

    public static String createAppJson() {
        return FileUtils.writeFileText(json_path, "{\"data\":[]}");
    }

}
