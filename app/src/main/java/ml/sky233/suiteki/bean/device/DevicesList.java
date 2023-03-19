package ml.sky233.suiteki.bean.device;

import static ml.sky233.suiteki.MainApplication.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ml.sky233.suiteki.util.EsonBuilder;
import ml.sky233.suiteki.util.EsonUtils;
import ml.sky233.suiteki.util.FileUtils;

@SuppressLint("SdCardPath")
public class DevicesList {

    private List<DeviceInfo> mList;

    private String json_path = "/data/data/ml.sky233.suiteki/files/devices_list.json";
    public DeviceInfo deviceInfo = null;
    private int b = -1;

    public DevicesList(Context context) {
        mList = new ArrayList<>();
        json_path = context.getFilesDir().toString() + "/devices_list.json";
        String json_data = FileUtils.getFileText(json_path);
        if (json_data.equals("")) {
            json_data = createAppJson();
        } else {
            Object objs = EsonUtils.getArray(EsonUtils.toObject(json_data), "data");
            for (int i = 0; i < EsonUtils.getArrayLength(objs); i++) {
                Object obj = EsonUtils.getArrayObject(objs, i);
                DeviceInfo deviceInfo = new DeviceInfo(EsonUtils.getObjectText(obj, "AuthKey"), EsonUtils.getObjectText(obj, "Mac"), EsonUtils.getObjectText(obj, "Type"), EsonUtils.getObjectText(obj, "Name"));
                mList.add(deviceInfo);
            }
            b = EsonUtils.getObjectInt(EsonUtils.toObject(json_data), "i");
            try {
                if (b != -1) deviceInfo = mList.get(b);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                b = -1;
            }
        }
    }

    public DevicesList(Context context, boolean isCache) {
        mList = new ArrayList<>();
        json_path = context.getFilesDir().toString() + "/devices_list_cache.json";
        new File(json_path).deleteOnExit();
        createAppJson();
    }

    public List<DeviceInfo> addDeviceInfo(DeviceInfo deviceInfo) {
        for (int i = 0; i < mList.size(); i++) {
            DeviceInfo device = mList.get(i);
            if (deviceInfo.Mac.equals(device.Mac)) {
                mList.set(i, deviceInfo);
                saveData();
                return this.mList;
            }
        }
        this.mList.add(deviceInfo);
        saveData();
        return this.mList;
    }

    public List<DeviceInfo> getList() {
        return mList;
    }

    public void delInfo(String mac) {
        for (int i = 0; i < mList.size(); i++) {
            DeviceInfo deviceInfo = mList.get(i);
            if (deviceInfo.Mac.equals(mac)) {
                mList.remove(i);
                if (b == i) b = -1;
                if (b > mList.size()) b = -1;
                Log.d(TAG, "index:" + i);
                saveData();
                return;
            }
        }
    }

    public void delInfo(DeviceInfo device) {
        for (int i = 0; i < mList.size(); i++) {
            DeviceInfo deviceInfo = mList.get(i);
            if (deviceInfo.Mac.equals(device.Mac)) {
                mList.remove(i);
                if (b == i) b = -1;
                if (b > mList.size()) b = -1;
                if (mList.size() == 0) b = -1;
                Log.d(TAG, "index:" + i);
                saveData();
                return;
            }
        }
    }

    public DeviceInfo getDeviceInfo(int i) {
        return mList.get(i);
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public int getDeviceInfoIndex() {
        return b;
    }

    public DeviceInfo setDeviceInfo(int i) {
        b = i;
        saveData();
        return deviceInfo = mList.get(i);
    }

    public void saveData() {
        Log.d(TAG, toJsonStr());
        FileUtils.writeFileText(json_path, toJsonStr());
    }

    public String toJsonStr() {
        EsonBuilder ebo, eba = new EsonBuilder(EsonBuilder.JSON_ARRAY);
        for (DeviceInfo deviceInfo : mList) {
            ebo = new EsonBuilder(EsonBuilder.JSON_OBJECT);
            ebo.put("AuthKey", deviceInfo.AuthKey).put("Mac", deviceInfo.Mac).put("Type", deviceInfo.Type).put("Name", deviceInfo.Name);
            eba.put(ebo);
        }
        ebo = new EsonBuilder(EsonBuilder.JSON_OBJECT);
        ebo.putArray("data", eba.toJsonArray());
        ebo.put("i", b);
        return ebo.toString();
    }

    public String createAppJson() {
        try {
            new File(json_path).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return FileUtils.writeFileText(json_path, "{\"data\":[],\"i\":-1}");
    }
}