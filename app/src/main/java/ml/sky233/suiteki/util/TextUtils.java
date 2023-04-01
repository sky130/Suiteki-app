package ml.sky233.suiteki.util;


import android.content.ClipData;
import android.content.ClipboardManager;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ml.sky233.suiteki.MainApplication;

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

    public static StringBuilder join(String separator, String... elements) {
        StringBuilder builder = new StringBuilder();
        if (elements == null) {
            return builder;
        }
        boolean hasAdded = false;
        for (String element : elements) {
            if (element != null && element.length() > 0) {
                if (hasAdded) {
                    builder.append(separator);
                }
                builder.append(element);
                hasAdded = true;
            }
        }
        return builder;
    }

    public static String listToString(ArrayList<String> arrayList) {
        if (arrayList == null) return "";
        if (arrayList.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String item : arrayList)
            sb.append(item).append("\n");
        return sb.toString();
    }

    public static boolean copyText(String str) {
        try {
            ClipboardManager clipboard = MainApplication.clipboardManager;
            ClipData clipData = ClipData.newPlainText(null, str);
            clipboard.setPrimaryClip(clipData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String checkUserName(String str) {
        if (str.length() == 0) return "";
        if (isValidEmail(str)) return str;
        else if (str.length() == 11) return "+86" + str;
        else if (str.length() == 14) return str;
        else if (String.valueOf(str.charAt(0)) == "+") return str;
        return "";
    }

    public static boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty()))
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        return false;
    }

    public static boolean isValidMac(String mac) {
        String patternMac = "^[A-F0-9]{2}(:[A-F0-9]{2}){5}$";
        return Pattern.compile(patternMac).matcher(mac).find();
    }

    public static String fixMac(String mac) {
        return mac.replaceAll(" ", "").replaceAll("：", ":").toUpperCase();
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

    public static boolean lookFor(String str1, String str2) {
        return (0 <= str1.length() && !"".equals(str1) && !"".equals(str2) ? str1.indexOf(str2, 0) : -1) != -1;
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
