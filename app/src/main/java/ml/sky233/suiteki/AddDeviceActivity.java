package ml.sky233.suiteki;

import static ml.sky233.suiteki.MainApplication.suiteki;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ml.sky233.SuitekiObject;
import ml.sky233.SuitekiUtils;
import ml.sky233.suiteki.adapter.AppAdapter;
import ml.sky233.suiteki.adapter.AppBlueAdapter;
import ml.sky233.suiteki.adapter.DeviceAdapter;
import ml.sky233.suiteki.bean.AppObject;
import ml.sky233.suiteki.bean.device.DeviceInfo;
import ml.sky233.suiteki.bean.device.DevicesList;
import ml.sky233.suiteki.util.FileUtils;
import ml.sky233.suiteki.util.MsgUtils;
import ml.sky233.suiteki.util.TextUtils;
import ml.sky233.suiteki.util.ViewUtils;
import ml.sky233.suiteki.widget.SuitekiCardButton;

public class AddDeviceActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (this.getApplicationContext().getResources().getConfiguration().uiMode != 0x21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        View back = findViewById(R.id.title_add_device_back);
        ViewUtils.addTouchScale(back);
        back.setOnClickListener((v) -> AddDeviceActivity.this.finish());
        progressBar = findViewById(R.id.progress_graph);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        setAppList();
    }

    String pm = "";

    public void setAppList() {
        ArrayList<AppObject> list = new ArrayList<>();
        list.add(new AppObject("小米运动健康", R.drawable.ic_device_miband_7));
        list.add(new AppObject("小米穿戴", R.drawable.ic_device_miband_7));
        list.add(new AppObject("ZeppLife", R.drawable.ic_device_miband_7));
        list.add(new AppObject("Zepp", R.drawable.ic_device_miband_7));
        AppBlueAdapter adapter = new AppBlueAdapter(this, list);
        adapter.setOnItemClickListener((v, i) -> {
            switch (i) {
                case 0://小米运动健康
                    pm = "com.mi.health";
                    if (!FileUtils.isInstalled(pm, this)) {
                        Dialog("未安装小米运动健康");
                        return;
                    }
                    if (!FileUtils.isGetPermission(AddDeviceActivity.this, pm)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            FileUtils.startFor(this, FileUtils.buildUri(pm));
                        } else {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                                getDeviceList(pm, false);
                            else
                                ActivityCompat.requestPermissions(AddDeviceActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        getDeviceList(pm, true);
                    }
                    break;
                case 1://小米穿戴
                    pm = "com.xiaomi.wearable";
                    if (!FileUtils.isInstalled(pm, this)) {
                        Dialog("未安装小米穿戴");
                        return;
                    }
                    if (!FileUtils.isGetPermission(AddDeviceActivity.this, pm)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            FileUtils.startFor(this, FileUtils.buildUri(pm));
                        } else {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                                getDeviceList(pm, false);
                            else
                                ActivityCompat.requestPermissions(AddDeviceActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        getDeviceList(pm, true);
                    }
                    break;
                case 2://ZeppLife
                case 3://Zepp
                    loginDialog();
                    break;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void getDeviceList(String path, boolean isAndroid11) {
        new Handler().postDelayed(() -> {
            recyclerView.setAdapter(new AppAdapter(this, new ArrayList<>()));
            progressBar.setVisibility(View.VISIBLE);
        }, 120);
        if (isAndroid11)
            new Thread(() -> {
                if (path.equals("com.mi.health"))
                    suiteki.setLog(FileUtils.getInputText(this, FileUtils.buildUri2(FileUtils.mi_health_path)));
                if (path.equals("com.xiaomi.wearable"))
                    suiteki.setLog(FileUtils.getInputText(this, FileUtils.buildUri2(FileUtils.mi_wearable_path)));
                ArrayList<SuitekiObject> sobjs = suiteki.getAuthKeyList();
                if (sobjs.size() == 0)
                    Dialog("请重新结束运行官方应用,详细请查看帮助");
                else {
                    DevicesList devicesList = new DevicesList(this, true);
                    for (int i = 0; i < sobjs.size(); i++) {
                        devicesList.addDeviceInfo(new DeviceInfo("0x"+sobjs.get(i).getAuthKey(), sobjs.get(i).getMac(), SuitekiUtils.getModelName(sobjs.get(i).getDeviceName()), sobjs.get(i).getDeviceName() == "" ? "未知设备" : SuitekiUtils.getModelName(sobjs.get(i).getDeviceName())));
                    }
                    DeviceAdapter adapter = new DeviceAdapter(this, devicesList);
                    adapter.setOnItemClickListener((v, i) -> {
                        MainApplication.devicesList.addDeviceInfo(devicesList.getDeviceInfo(i));
                        MainApplication.devicesList.saveData();
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                    });
                    handler.sendMessage(MsgUtils.build(adapter, 0));
                }
            }).start();
        else
            new Thread(() -> {
                if (path.equals("com.mi.health"))
                    suiteki.setLog(FileUtils.getFileText(FileUtils.mi_health_path));
                if (path.equals("com.xiaomi.wearable"))
                    suiteki.setLog(FileUtils.getFileText(FileUtils.mi_wearable_path));
                ArrayList<SuitekiObject> sobjs = suiteki.getAuthKeyList();
                if (sobjs.size() == 0)
                    Dialog("请重新结束运行官方应用,详细请查看帮助");
                else {
                    DevicesList devicesList = new DevicesList(this, true);
                    for (int i = 0; i < sobjs.size(); i++) {
                        devicesList.addDeviceInfo(new DeviceInfo("0x"+sobjs.get(i).getAuthKey(), sobjs.get(i).getMac(), SuitekiUtils.getModelName(sobjs.get(i).getDeviceName()), sobjs.get(i).getDeviceName() == "" ? "未知设备" : SuitekiUtils.getModelName(sobjs.get(i).getDeviceName())));
                    }
                    DeviceAdapter adapter = new DeviceAdapter(this, devicesList);
                    adapter.setOnItemClickListener((v, i) -> {
                        MainApplication.devicesList.addDeviceInfo(devicesList.getDeviceInfo(i));
                        MainApplication.devicesList.saveData();
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                    });
                    handler.sendMessage(MsgUtils.build(adapter, 0));
                }
            }).start();

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter((DeviceAdapter) msg.obj);
                    break;
                case 1://登录失败
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddDeviceActivity.this, "登录失败,检查账号密码", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {
                getContentResolver().takePersistableUriPermission(data.getData(), data.getFlags() & (
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            getDeviceList(pm, false);
        } else if (requestCode == 3) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new AppAdapter(this, new ArrayList<>()));
            new Thread(() -> {
                suiteki.loginHuami(data.getStringExtra("code"));
                if (suiteki.getResultCode().equals("200")) {
                    suiteki.getHuamiToken();
                    ArrayList<SuitekiObject> sobjs = suiteki.getResultData();
                    DevicesList devicesList = new DevicesList(this, true);
                    for (int i = 0; i < sobjs.size(); i++) {
                        SuitekiObject sobj = sobjs.get(i);
                        devicesList.addDeviceInfo(new DeviceInfo("0x"+sobjs.get(i).getAuthKey(), sobjs.get(i).getMac(), SuitekiUtils.getModelName(sobjs.get(i).getDeviceName()), sobjs.get(i).getDeviceName() == "" ? "未知设备" : SuitekiUtils.getModelName(sobjs.get(i).getDeviceName())));
                    }
                    DeviceAdapter adapter = new DeviceAdapter(this, devicesList);
                    adapter.setOnItemClickListener((v, i) -> {
                        MainApplication.devicesList.addDeviceInfo(devicesList.getDeviceInfo(i));
                        MainApplication.devicesList.saveData();
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                    });
                    handler.sendMessage(MsgUtils.build(adapter, 0));
                    //登录成功
                } else {
                    handler.sendMessage(MsgUtils.build("", 1));
                    //登录失败
                }
            }).start();
        }
    }

    public void Dialog(String str) {
        LayoutInflater factory = LayoutInflater.from(AddDeviceActivity.this);
        final View view = factory.inflate(R.layout.dialog_not_install, null);
        final SuitekiCardButton btn_y = view.findViewById(R.id.btn_y);
        final SuitekiCardButton btn_n = view.findViewById(R.id.btn_n);
        final TextView tv = view.findViewById(R.id.textView2);
        tv.setText(str);
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddDeviceActivity.this);
        dialog.setView(view);
        Dialog d = dialog.show();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_n.setOnClickListener((a) -> {
            startActivity(new Intent(this, TipsActivity.class));
            d.dismiss();
        });//查看帮助
        btn_y.setOnClickListener((a) -> {
            d.dismiss();
            setAppList();
        });
    }

    public void loginDialog() {
        LayoutInflater factory = LayoutInflater.from(AddDeviceActivity.this);
        final View view = factory.inflate(R.layout.dialog_zepp_login, null);
        final SuitekiCardButton btn_y = view.findViewById(R.id.btn_y);//登录
        final SuitekiCardButton btn_n = view.findViewById(R.id.btn_n);//小米账号登录
        final EditText tv_email = view.findViewById(R.id.editText_email);
        final EditText tv_password = view.findViewById(R.id.editText_name);

        AlertDialog.Builder dialog = new AlertDialog.Builder(AddDeviceActivity.this);
        dialog.setView(view);
        Dialog d = dialog.show();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_n.setOnClickListener((a) -> {
            startActivityForResult(new Intent(this, WebActivity.class), 3);
            d.dismiss();
        });//查看帮助
        btn_y.setOnClickListener((a) -> {
            String email = TextUtils.checkUserName(tv_email.getText().toString()), password = tv_password.getText().toString();
            if (email.equals("") | password.equals("")) {
                Toast.makeText(this, "账号密码不能为空", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    suiteki.loginHuami(email, password);
                    if (suiteki.getResultCode().equals("200")) {
                        suiteki.getHuamiToken();
                        ArrayList<SuitekiObject> sobjs = suiteki.getResultData();
                        DevicesList devicesList = new DevicesList(this, true);
                        for (int i = 0; i < sobjs.size(); i++) {
                            SuitekiObject sobj = sobjs.get(i);
                            devicesList.addDeviceInfo(new DeviceInfo("0x"+sobjs.get(i).getAuthKey(), sobjs.get(i).getMac(), SuitekiUtils.getModelName(sobjs.get(i).getDeviceName()), sobjs.get(i).getDeviceName() == "" ? "未知设备" : SuitekiUtils.getModelName(sobjs.get(i).getDeviceName())));
                        }
                        DeviceAdapter adapter = new DeviceAdapter(this, devicesList);
                        adapter.setOnItemClickListener((v, i) -> {
                            MainApplication.devicesList.addDeviceInfo(devicesList.getDeviceInfo(i));
                            MainApplication.devicesList.saveData();
                            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                        });
                        handler.sendMessage(MsgUtils.build(adapter, 0));
                        //登录成功
                    } else {
                        handler.sendMessage(MsgUtils.build("", 1));
                        //登录失败
                    }
                }).start();
            }
        });
    }


}