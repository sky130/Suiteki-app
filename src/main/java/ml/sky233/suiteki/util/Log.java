package ml.sky233.suiteki.util;

public class Log {
    public static String log = "";
    public static String type = "";

    public static void s(String str) {
        if (!type.equals(str))
            if (!str.equals("")) {
                if (!log.equals(""))
                    log = log + "\n" + f(str);
                else
                    log = f(str);
                type = str;
            }
    }

    public static void d(String str) {

        if (!log.equals("")) {
            log = log + "\n" + str;
        } else {
            log = str;
        }
    }

    public static void g(String str) {
        if (!str.equals("")) {
            log = log + "\n" + str;
            type = str;
        }
    }

    public static String b() {
        return log;
    }

    public static String f(String str) {
        if (str.equals("")) {
            return "";
        } else {
            return "=======[" + str + "]=======";
        }
    }
}
