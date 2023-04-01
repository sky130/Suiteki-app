package ml.sky233.suiteki.service.ble;

import static android.text.TextUtils.join;
import static org.apache.commons.lang3.ArrayUtils.subarray;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ml.sky233.suiteki.MainApplication;
import ml.sky233.suiteki.service.ble.callback.BleGattCallback;

public class BleAppInfo {

    BleGattCallback bleGattCallback;
    public ArrayList<String> app_list;

    public BleAppInfo(BleGattCallback bleGattCallback) {
        this.bleGattCallback = bleGattCallback;
    }

    public void requestAppItems() {
        bleGattCallback.write((short) 0x0026, new byte[]{0x03, 0x01}, true, true);//发送请求获取
    }

    public void decodeAndUpdateDisplayItems(final short type, final byte[] payload) {
        Log.d(MainApplication.TAG + ".2","test");
        app_list = new ArrayList<>();
        if (payload == null || payload.length == 0) return;
        if (payload[0] == 0x04) {
            final int numberScreens = payload[2];
            if (payload.length != 4 + numberScreens * 12) return;
            final Map<String, String> idMap;
            if (payload[1] == 0x01)
                idMap = displayItemNameLookup;
            else
                return;
            for (int i = 0; i < numberScreens; i++) {
                final String screenId = new String(subarray(payload, 4 + i * 12, 4 + i * 12 + 8));//小程序的16进制代号,appid
                final String screenNameOrId = idMap.containsKey(screenId) ? idMap.get(screenId) : screenId;//软件会检查一下是否为系统应用,如果是则返回16进制代号
                final int screenPosition = payload[4 + i * 12 + 10];
                if (screenPosition >= numberScreens)
                    continue;
                if (screenId.equals(screenNameOrId))
                    app_list.add(screenId);
            }
            Log.d(MainApplication.TAG + ".2", join(",", app_list.toArray(new String[0])));
        } //获取列表
    }

    public static final Map<String, String> displayItemNameLookup = new HashMap<String, String>() {
        {
            put("00000010", "nfc_card");
            put("0000003F", "mi_ai");
            put("00000001", "personal_activity_intelligence");
            put("00000002", "hr");
            put("00000003", "workout");
            put("00000004", "weather");
            put("00000009", "alarm");
            put("0000000A", "takephoto");
            put("0000000B", "music");
            put("0000000C", "stopwatch");
            put("0000000D", "countdown");
            put("0000000E", "findphone");
            put("0000000F", "mutephone");
            put("00000011", "alipay");
            put("00000013", "settings");
            put("00000014", "workout_history");
            put("00000015", "eventreminder");
            put("00000016", "compass");
            put("00000019", "pai");
            put("00000031", "wechat_pay");
            put("0000001A", "worldclock");
            put("0000001C", "stress");
            put("0000001D", "female_health");
            put("0000001E", "workout_status");
            put("00000020", "calendar");
            put("00000023", "sleep");
            put("00000024", "spo2");
            put("00000025", "phone");
            put("00000026", "events");
            put("00000033", "breathing");
            put("00000038", "pomodoro");
            put("0000003E", "todo");
            put("00000041", "barometer");
            put("00000042", "voice_memos");
            put("00000044", "sun_moon");
            put("00000045", "one_tap_measuring");
            put("00000047", "membership_cards");
            put("00000100", "alexa");
            put("00000101", "offline_voice");
            put("00000102", "flashlight");
        }
    };

}
