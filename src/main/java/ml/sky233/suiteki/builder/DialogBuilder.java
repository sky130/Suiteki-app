package ml.sky233.suiteki.builder;

import static ml.sky233.suiteki.GetKeyActivity.AuthKeys;
import static ml.sky233.suiteki.GetKeyActivity.Ids;
import static ml.sky233.suiteki.GetKeyActivity.Macs;
import static ml.sky233.suiteki.GetKeyActivity.Names;
import static ml.sky233.suiteki.util.Eson.getObjectText;
import static ml.sky233.suiteki.util.Eson.toObject;
import static ml.sky233.suiteki.util.Setting.getPackageVersion;
import static ml.sky233.suiteki.util.Setting.getString;
import static ml.sky233.suiteki.util.Setting.setString;
import static ml.sky233.suiteki.util.Text.copyText;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.IOException;

import ml.sky233.suiteki.R;
import ml.sky233.suiteki.util.File;

public class DialogBuilder {

    public static void privacyText(Activity activity) {
        if (!getString("read_privacy").equals("1")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("请先阅读隐私协议");
            builder.setMessage(R.string.privacy_text);
            builder.setPositiveButton("退出", (dialog, which) -> activity.finish());
            builder.setNegativeButton("同意", (dialog, which) -> {
                setString("read_privacy", "1");
                dialog.dismiss();
            });
            builder.setCancelable(false);
            builder.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_SEARCH);
            builder.show();
        }
    }

    public static void joinGroup(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("进群吹水 ヾ(≧▽≦*)o");
        builder.setMessage("Suiteki用户交流群 : \n\n" + "1群:648849444\n\n别问为什么只有1群,问就是没有人进(");
        builder.setPositiveButton("返回", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("复制群号", (dialog, which) -> {
            copyText("648849444");
            Toast.makeText(activity, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.show();
    }

    public static void copyAuthKey(Activity activity,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("设备信息");
        builder.setMessage("设备型号 : "+Ids[position]+"\n设备名 : "+Names[position]+"\n设备地址 : "+Macs[position]+"\n设备密钥 : "+AuthKeys[position]);
        builder.setPositiveButton("返回", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("复制", (dialog, which) -> {
            Toast.makeText(activity,"已经复制到粘贴板",Toast.LENGTH_SHORT).show();
            copyText(AuthKeys[position]);
            dialog.dismiss();
        });
        builder.show();
    }

    public static void updateApp(Activity activity,String str){
        Object object = toObject(str);
        int version_code = Integer.parseInt(getObjectText(object, "version_code"));
        if (version_code > getPackageVersion("ml.sky233.suiteki", activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("获取到最新版本 - " + getObjectText(object, "version_name"));
            builder.setMessage(getObjectText(object, "update_text") + "\n" + getObjectText(object, "update_time"));
            builder.setPositiveButton("取消", (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("下载", (dialog, which) -> {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getObjectText(object, "app_link"))));
                dialog.dismiss();
            });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("已是最新版本 - " + getObjectText(object, "version_name"));
            builder.setMessage(getObjectText(object, "update_text"));
            builder.setPositiveButton("取消", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }

    public static void showText(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("隐私协议");
        builder.setMessage(R.string.privacy_text);
        builder.setPositiveButton("返回", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static void showLicense(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("源代码开源许可");
        try {
            builder.setMessage(File.getFileText(activity.getAssets().open("license.txt")));
        }catch (IOException e){
            e.printStackTrace();
            builder.setMessage("error");
        }
        builder.setPositiveButton("返回", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}
