package ml.sky233.suiteki.util;

import android.os.Message;

public class MsgUtils {

    public static Message build(Object obj,int what){
        Message msg = Message.obtain();
        msg.obj = obj;
        msg.what = what;
        return msg;
    }

    public static Message build(int what){
        Message msg = Message.obtain();
        msg.obj = "";
        msg.what = what;
        return msg;
    }

}
