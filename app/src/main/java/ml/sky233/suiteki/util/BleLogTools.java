package ml.sky233.suiteki.util;

//import static ml.sky233.suiteki.util.BleUtils.addLog;
//import static ml.sky233.suiteki.util.BleUtils.progress;
//import static ml.sky233.suiteki.util.BleUtils.writeLog;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ml.sky233.suiteki.MainApplication;

@SuppressLint("SimpleDateFormat")
public class BleLogTools {
    public static List<String> log_list = new ArrayList<>();
    ;
    public static boolean isDev = true;

    public static void start(String mode, int appId, String macAddress) {
//        isDev = SettingUtils.getValue("ble_log_cat");
        if (!isDev) return;
        log_list = new ArrayList<>();
        addLog("Mode:" + mode);
        addLog("Time:" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        addLog("macAddress:" + macAddress);
        if (appId != -1) addLog("appId:" + appId);
        writeLog();

    }

    public static void onStartConnect() {
        if (!isDev) return;
        addLog("---[onStartConnect]---");
        writeLog();
    }

    public static void onStartConnect(int data_length) {
        if (!isDev) return;
        addLog("---[onStartConnect]---");
        addLog("Data_length:" + data_length);
        writeLog();
    }

    public static void onConnectFail() {
        if (!isDev) return;
        addLog("ConnectStatus:onConnectFail");
        addLog("----");
        writeLog();
        // 连接失败
    }

    public static void onConnectFail(int index) {
        if (!isDev) return;
        addLog("ConnectStatus:onConnectFail");
        addLog("index:" + index);
        addLog("----");
        writeLog();
        // 连接失败
    }

    public static void onConnectSuccess() {
        if (!isDev) return;
        addLog("ConnectStatus:onConnectSuccess");
        writeLog();
    }

    public static void onWriteSuccess(byte[] bytes) {
        if (!isDev) return;
        addLog("statusOnWriteSuccess");
        addLog("data:" + BytesUtils.bytesToHexStr(bytes));
        addLog("----");
        writeLog();
    }

    public static void onWriteFailure() {
        if (!isDev) return;
        addLog("statusOnWriteFailure");
        addLog("data:" + "null");
        addLog("----");
        writeLog();
    }

    public static void onWriteFailure(byte[] data) {
        if (!isDev) return;
        addLog("statusOnWriteFailure");
        addLog("data:" + "null");
        addLog("just_data:" + BytesUtils.bytesToHexStr(data));
        addLog("----");
        writeLog();
    }

    public static void onDisConnected(boolean isActiveDisConnected) {
        if (!isDev) return;
        addLog("ConnectStatus:onDisConnected, isActiveDisConnected:" + isActiveDisConnected);
        writeLog();
    }

    public static void onNotifySuccess() {
        if (!isDev) return;
        addLog("NotifyStatus:onNotifySuccess");
        addLog("---[StartWriteData]---");
        addLog("Time:" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        writeLog();
    }

    public static void onNotifyFailure() {
        if (!isDev) return;
        addLog("NotifyStatus:onNotifyFailure");
        addLog("----");
        writeLog();
    }

    public static void onCharacteristicChanged(boolean mWaitingForResponse, int mDataIndex, byte[] data) {
        if (!isDev) return;
        addLog("onCharacteristicChanged");
        addLog("mWaitingForResponse:" + mWaitingForResponse);
        addLog("index:" + mDataIndex);
        addLog("data:" + BytesUtils.bytesToHexStr(data));
        addLog("----");
        writeLog();
    }

    public static void onCharacteristicChanged(byte[] data) {
        if (!isDev) return;
        addLog("onCharacteristicChanged");
        addLog("data:" + BytesUtils.bytesToHexStr(data));
        addLog("----");
        writeLog();
    }

    public static void onWriteSuccess(int mDataIndex, int firstCount, String uuid_characteristic_write, boolean mWaitingForResponse, byte[] data) {
        if (!isDev) return;
        addLog("statusOnWriteSuccess");
        addLog("index:" + mDataIndex);
//        addLog("progress:" + progress);
        addLog("firstCount:" + firstCount);
        addLog("uuid:" + uuid_characteristic_write);
        addLog("mWaitingForResponse:" + mWaitingForResponse);
        addLog("data:" + BytesUtils.bytesToHexStr(data));
        addLog("----");
        writeLog();
    }

    public static void onWriteFailure(int mDataIndex) {
        if (!isDev) return;
        addLog("statusOnWriteFailure");
        addLog("index:" + mDataIndex);
        addLog("data:" + "null");
        addLog("----");
        writeLog();
    }

    public static void onAuthSuccess() {
        if (!isDev) return;
        addLog("onAuthSuccess");
        addLog("----");
        writeLog();
    }

    private static void writeLog() {
//        FileUtils.writeFileText(MainApplication.e_data_path + "/log.txt", log_list.toString());
        File file = new File(MainApplication.e_data_path + "/log.txt");
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (log_list == null) {
            FileUtils.writeFileText(MainApplication.e_data_path + "/log.txt", "error");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < log_list.size(); i++)
            sb.append("\n").append(log_list.get(i));
        FileUtils.writeFileText(MainApplication.e_data_path + "/log.txt"
                , sb.toString());
    }

    private static void addLog(String s) {
        log_list.add(s);
    }

}
