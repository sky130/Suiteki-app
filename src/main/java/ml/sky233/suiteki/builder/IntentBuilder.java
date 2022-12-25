package ml.sky233.suiteki.builder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentBuilder {

    public static Intent build(Context context, Class<?> mClass, int mode) {
        Intent intent = new Intent(context, mClass);
        intent.putExtra("app_mode", mode);
        return intent;
    }

    public static Intent build(Context context, Class<?> mClass, int mode, String value) {
        Intent intent = new Intent(context, mClass);
        intent.putExtra("app_mode", mode);
        intent.putExtra("app_value", mode);
        return intent;
    }

    public static String getValue(Intent intent) {
        if (intent != null)
            return intent.getStringExtra("app_value");
        else
            return "";
    }

    public static String getMode(Intent intent) {
        if (intent != null)
            return intent.getStringExtra("app_mode");
        else
            return "";
    }

    public static Intent build(String url){
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

}
