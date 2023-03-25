package ml.sky233.suiteki.bean.app;

import ml.sky233.suiteki.util.Eson.EsonUtils;
import ml.sky233.suiteki.util.FileUtils;

public class AppInfo {
    public String appName = "", vender = "", description = "", version = "";
    public int appId = -1;

    public AppInfo(String path) {
        Object obj = EsonUtils.getObject(EsonUtils.toObject(FileUtils.getFileText(path)), "app");
        this.appId = EsonUtils.getObjectInt(obj, "appId");
        this.vender = EsonUtils.getObjectText(obj, "vender");
        this.description = EsonUtils.getObjectText(obj, "description");
        this.appName = EsonUtils.getObjectText(obj, "appName");
        this.version = EsonUtils.getObjectText(EsonUtils.getObject(obj, "version"),"name");
    }

    public AppInfo(String appName, String vender, String description, int appId, String version) {
        this.appId = appId;
        this.vender = vender;
        this.description = description;
        this.appName = appName;
        this.version = version;
//        Log.d(TAG, "appId:" + appId + "\nappName:" + appName + "\nvender:" + vender + "\ndescription:" + description + "\nversion:" + version);
    }

    public String toString() {
        return "appId:" + appId + "\nappName:" + appName + "\nvender:" + vender + "\ndescription:" + description + "\nversion:" + version;
    }
}
