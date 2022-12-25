package ml.sky233.suiteki.util;

import static ml.sky233.suiteki.MainApplication.TAG;
import static ml.sky233.suiteki.MainApplication.suiteki;
import static ml.sky233.suiteki.builder.DialogBuilder.updateApp;
import static ml.sky233.suiteki.util.File.getFileText;
import static ml.sky233.suiteki.util.File.getLogText;
import static ml.sky233.suiteki.util.File.startFor;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ml.sky233.SuitekiObject;
import ml.sky233.SuitekiUtils;
import ml.sky233.suiteki.GetKeyActivity;
import ml.sky233.suiteki.LoginActivity;
import ml.sky233.suiteki.R;
import ml.sky233.suiteki.WebActivity;
import ml.sky233.suiteki.builder.AdapterBuilder;
import ml.sky233.suiteki.builder.DialogBuilder;
import ml.sky233.suiteki.builder.IntentBuilder;
import ml.sky233.suiteki.builder.MsgBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppThread {
    OkHttpClient okClient = new OkHttpClient();
    Activity activity;
    String code;
    public Handler handler;
    public Thread updateApp, getKey, loginHuami, getHuami, loginMi, getZip;


    public void initActivity(Activity activity) {
        this.activity = activity;
    }

    public void initCode(String code) {
        this.code = code;
    }

    public void initThread() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0://升级APP
                        updateApp(activity, (String) msg.obj);
                        break;
                    case 1://设置ListView
                        GetKeyActivity.listView.setAdapter((SimpleAdapter) msg.obj);
                        GetKeyActivity.listView.setOnItemClickListener((parent, view, position, id) -> DialogBuilder.copyAuthKey(activity, position));
                        break;
                    case 2://登录返回操作
                        if (msg.obj.equals("200")) {
                            activity.startActivity(IntentBuilder.build(activity, GetKeyActivity.class, 2));
                            activity.finish();
                        } else
                            Toast.makeText(activity, R.string.login_error, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        updateApp = new Thread(() -> {
            Looper.prepare();
            String update = "";
            Request request = new Request.Builder().url("https://sky233.ml/update/Suiteki/index.json").build();
            try {
                Response response = okClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    update = Objects.requireNonNull(response.body()).string();
                } else {
                    update = String.valueOf(response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(MsgBuilder.build(update, 0));
            Looper.loop();
        });

        getKey = new Thread(() -> {
            Looper.prepare();
            suiteki.setLog(getLogText(activity));
            ArrayList<SuitekiObject> sobjs = suiteki.getAuthKeyList();
            if (sobjs.size() == 0)
                Toast.makeText(activity, R.string.not_found_file, Toast.LENGTH_SHORT).show();
            else
                handler.sendMessage(buildMsg(sobjs));
            Looper.loop();
        });

        loginHuami = new Thread(() -> {
            Looper.prepare();
            suiteki.loginHuami(LoginActivity.email, LoginActivity.password);
            if (suiteki.getResultCode().equals("200"))
                handler.sendMessage(MsgBuilder.build("200", 2));
            else
                handler.sendMessage(MsgBuilder.build("-1", 2));
            Looper.loop();
        });

        getHuami = new Thread(() -> {
            Looper.prepare();
            suiteki.getHuamiToken();
            ArrayList<SuitekiObject> sobjs = (ArrayList) suiteki.getResultData();
            if (sobjs.size() == 0)
                Toast.makeText(activity, R.string.not_found_net, Toast.LENGTH_SHORT).show();
            else
                handler.sendMessage(buildMsg(sobjs));
            Looper.loop();
        });

        loginMi = new Thread(() -> {
            Looper.prepare();
            suiteki.loginHuami(code);
            suiteki.getHuamiToken();
            ArrayList<SuitekiObject> sobjs = (ArrayList) suiteki.getResultData();
            if (sobjs.size() == 0)
                Toast.makeText(activity, R.string.not_found_net, Toast.LENGTH_SHORT).show();
            else
                handler.sendMessage(buildMsg(sobjs));
            Looper.loop();
        });

        getZip = new Thread(() -> {
            Looper.prepare();
            File.unzipFile(File.getNewLogZip());
            suiteki.setLog(File.getLogTextZip());
            ArrayList<SuitekiObject> sobjs = suiteki.getAuthKeyList();
            if (sobjs.size() == 0)
                Toast.makeText(activity, R.string.not_found_file, Toast.LENGTH_SHORT).show();
            else
                handler.sendMessage(buildMsg(sobjs));
            File.deleteDirWihtFile("/data/data/ml.sky233.suiteki/log_data/");
            Looper.loop();
        });
    }

    public Message buildMsg(ArrayList<SuitekiObject> sobjs) {
        String[] keys = new String[sobjs.size()], macs = new String[sobjs.size()], names = new String[sobjs.size()], ids = new String[sobjs.size()];
        for (int i = 0; i < sobjs.size(); i++) {
            keys[i] = sobjs.get(i).getAuthKey();
            names[i] = sobjs.get(i).getDeviceName() == "" ? "Null" : SuitekiUtils.getModelName(sobjs.get(i).getDeviceName());
            macs[i] = sobjs.get(i).getMac();
            ids[i] = SuitekiUtils.getModelName(sobjs.get(i).getDeviceName()) == "notFound" ? "Null" : sobjs.get(i).getDeviceName();
        }
        GetKeyActivity.AuthKeys = keys;
        GetKeyActivity.Macs = macs;
        GetKeyActivity.Names = names;
        GetKeyActivity.Ids = ids;
        return MsgBuilder.build(AdapterBuilder.getKeyAdapter(activity, macs, names), 1);
    }
}
