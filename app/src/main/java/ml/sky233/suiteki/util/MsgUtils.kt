package ml.sky233.suiteki.util

import android.os.Message

object MsgUtils {
    fun build(obj: Any?, what: Int): Message {
        val msg = Message.obtain()
        msg.obj = obj
        msg.what = what
        return msg
    }

    fun build(what: Int): Message {
        val msg = Message.obtain()
        msg.obj = ""
        msg.what = what
        return msg
    }
}