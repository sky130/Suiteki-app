package ml.sky233.suiteki.bean.device;

public class DeviceInfo {
    public String AuthKey = "", Mac = "", Type = "", Name = "";

    public DeviceInfo(String authKey, String mac, String type, String name) {
        AuthKey = authKey;
        Mac = mac;
        Type = type;
        Name = name;
    }

    public String toString() {
        return "Type:" + Type + ", Mac:" + Mac + ", AuthKey:" + AuthKey + ", Name:" + Name;
    }
}
