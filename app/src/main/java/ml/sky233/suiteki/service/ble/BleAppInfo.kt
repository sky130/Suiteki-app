package ml.sky233.suiteki.service.ble

import ml.sky233.suiteki.service.ble.callback.BleGattCallback
import ml.sky233.suiteki.util.ArrayUtils

class BleAppInfo(var bleGattCallback: BleGattCallback) {
    var app_list: ArrayList<String>? = null
    fun requestAppItems() {
        bleGattCallback.write(0x0026.toShort(), byteArrayOf(0x03, 0x01), true, true) //发送请求获取
    }

    fun decodeAndUpdateDisplayItems(type: Short, payload: ByteArray?) {
        app_list = ArrayList()
        if (payload == null || payload.size == 0) return
        if (payload[0].toInt() == 0x04) {
            val numberScreens = payload[2].toInt()
            if (payload.size != 4 + numberScreens * 12) return
            val idMap: Map<String, String> = if (payload[1].toInt() == 0x01) displayItemNameLookup else return
            for (i in 0 until numberScreens) {
                val screenId = String(
                    ArrayUtils.subarray(
                        payload, 4 + i * 12, 4 + i * 12 + 8
                    )
                )
                val screenNameOrId =
                    if (idMap.containsKey(screenId)) idMap[screenId] else screenId //软件会检查一下是否为系统应用,如果是则返回16进制代号
                val screenPosition = payload[4 + i * 12 + 10].toInt()
                if (screenPosition >= numberScreens) continue
                if (screenId == screenNameOrId) app_list!!.add(screenId)
            }
        } //获取列表
    }

    companion object {
        val displayItemNameLookup: HashMap<String, String> = object : HashMap<String, String>() {
            init {
                put("00000001", "personal_activity_intelligence")
                put("00000002", "hr")
                put("00000003", "workout")
                put("00000004", "weather")
                put("00000009", "alarm")
                put("00000010", "wallet")
                put("0000000A", "takephoto")
                put("0000000B", "music")
                put("0000000C", "stopwatch")
                put("0000000D", "countdown")
                put("0000000E", "findphone")
                put("0000000F", "mutephone")
                put("00000011", "alipay")
                put("00000013", "settings")
                put("00000014", "workout_history")
                put("00000015", "eventreminder")
                put("00000016", "compass")
                put("00000019", "pai")
                put("00000031", "wechat_pay")
                put("0000001A", "worldclock")
                put("0000001C", "stress")
                put("0000001D", "female_health")
                put("0000001E", "workout_status")
                put("00000020", "calendar")
                put("00000023", "sleep")
                put("00000024", "spo2")
                put("00000025", "phone")
                put("00000026", "events")
                put("00000033", "breathing")
                put("00000038", "pomodoro")
                put("0000003E", "todo")
                put("0000003F", "mi_ai")
                put("00000041", "barometer")
                put("00000042", "voice_memos")
                put("00000044", "sun_moon")
                put("00000045", "one_tap_measuring")
                put("00000047", "membership_cards")
                put("00000100", "alexa")
                put("00000101", "offline_voice")
                put("00000102", "flashlight")
            }
        }
    }
}