package ml.sky233.suiteki.service.ble;

import java.util.UUID;

public class HuamiService {

    public static final UUID UUID_SERVICE_FIRMWARE = UUID.fromString("00001530-0000-3512-2118-0009af100700");//固件服务
    public static final UUID UUID_CHARACTERISTIC_FIRMWARE_NOTIFY = UUID.fromString("00001531-0000-3512-2118-0009af100700");//发送指令以及订阅通知特征
    public static final UUID UUID_CHARACTERISTIC_FIRMWARE_WRITE = UUID.fromString("00001532-0000-3512-2118-0009af100700");//发送固件特征
    public static final UUID UUID_SERVICE_MIBAND_SERVICE = UUID.fromString("0000FEE0-0000-1000-8000-00805f9b34fb");//米环服务
    public static final UUID UUID_CHARACTERISTIC_APP = UUID.fromString("00000016-0000-3512-2118-0009af100700");//小程序特征
    public static final UUID UUID_CHARACTERISTIC_AUTH_WRITE = UUID.fromString("00000016-0000-3512-2118-0009af100700");//验证发送特征
    public static final UUID UUID_CHARACTERISTIC_AUTH_NOTIFY = UUID.fromString("00000017-0000-3512-2118-0009af100700");//验证订阅通知特征
    public static final UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");//
    public static final int STATUS_BLE_NOPE = 0x00;//未连接
    public static final int STATUS_BLE_CONNECTING = 0x01;//正在连接
    public static final int STATUS_BLE_CONNECTED = 0x02;//连接完毕
    public static final int STATUS_BLE_AUTHING = 0x03;//正在验证
    public static final int STATUS_BLE_AUTHED = 0x04;//验证完毕
    public static final int STATUS_BLE_NORMAL = 0x05;//准备工作
    public static final int STATUS_BLE_INSTALLING = 0x06;//正在安装表盘
    public static final int STATUS_BLE_CONNECT_FAILURE = 0x07;//连接失败
    public static final int STATUS_BLE_DISCONNECT = 0x08;//断开连接

    public static final byte TYPE_D2_WATCHFACE = 8;//08 表盘
    public static final byte TYPE_D2_FIRMWARE = -3;//FD 固件
    public static final byte TYPE_D2_APP = -96;//A0 小程序
}
