package ml.sky233.suiteki.Utils;

import static ml.sky233.suiteki.Utils.LogUtils.d;

import android.content.ClipData;
import android.content.ClipboardManager;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ml.sky233.suiteki.MainActivity;

public class TextUtils {

    public static String[] AnalyzeText(String str, String separator) {
        if (!"".equals(separator) && !"".equals(str)) {
            if (separator.equals("\n")) {
                str = exchangeText(str, "\r", "");
            }
            return getTextRight(str, getTextLength(separator)).equals(separator) ? getTheText(separator + str, separator, separator) : getTheText(separator + str + separator, separator, separator);
        } else {
            return new String[0];
        }
    }

    public static void copyText(String str){
        d("Copy:"+str);
        ClipboardManager clipboard = MainActivity.clipboardManager;
        ClipData clipData = ClipData.newPlainText(null, str);
        clipboard.setPrimaryClip(clipData);
    }

    public static String[] regexMatch(String text, String statement) {
        Pattern pn = Pattern.compile(statement, 40);
        Matcher mr = pn.matcher(text);
        ArrayList<String> list = new ArrayList<>();
        while (mr.find()) {
            list.add(mr.group());
        }
        String[] strings = new String[list.size()];
        return list.toArray(strings);
    }


    public static String[] getTheText(String str, String left, String right) {
        return !"".equals(str) && !"".equals(left) && !"".equals(right) ? regexMatch(str, "(?<=\\Q" + left + "\\E).*?(?=\\Q" + right + "\\E)") : new String[0];
    }

    public static String getTextRight(String str, int len) {
        if (!"".equals(str) && len > 0) {
            if (len > str.length()) {
                return str;
            } else {
                int start = str.length() - len;
                return str.substring(start);
            }
        } else {
            return "";
        }
    }

    public static int getTextLength(String str) {
        return str.length();
    }

    public static String exchangeText(String str, String find, String replace) {
        if (!"".equals(find) && !"".equals(str)) {
            find = "\\Q" + find + "\\E";
            return str.replaceAll(find, replace);
        } else {
            return "";
        }
    }

}
