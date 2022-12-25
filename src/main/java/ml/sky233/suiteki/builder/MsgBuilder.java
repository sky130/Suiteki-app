package ml.sky233.suiteki.builder;

import android.os.Message;

public class MsgBuilder {

    public static Message build(Object obj,int what){
        Message msg = Message.obtain();
        msg.obj = obj;
        msg.what = what;
        return msg;
    }

}
