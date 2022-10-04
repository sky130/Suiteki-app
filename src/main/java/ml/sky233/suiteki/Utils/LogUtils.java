package ml.sky233.suiteki.Utils;

public class LogUtils {
    public static String log = "";

    public static void d(String str) {
        if (!log.equals("")) {
            log = log + "\n" + str;
        } else {
            log = str;
        }
    }

    public static String b(){
        return log;
    }
}
