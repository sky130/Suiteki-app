package ml.sky233.suiteki;

import static ml.sky233.suiteki.MainApplication.devicesList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import ml.sky233.suiteki.adapter.DeviceAdapter;
import ml.sky233.suiteki.bean.device.DeviceInfo;
import ml.sky233.suiteki.util.MsgUtils;
import ml.sky233.suiteki.util.ViewUtils;
import ml.sky233.suiteki.widget.SuitekiCardButton;

public class DeviceActivity extends AppCompatActivity {
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (this.getApplicationContext().getResources().getConfiguration().uiMode != 0x21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.dialog_device);
//        View add = findViewById(R.id.title_device_add);
//        ViewUtils.addTouchScale(add);
//        add.setOnClickListener((v) -> {
//            startActivity(new Intent(this, AddDeviceActivity.class));
//            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//        });
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecycler();
    }


    @Override
    public void onBackPressed() {
        MainApplication.handler.sendMessage(MsgUtils.build("", 1));
        super.onBackPressed();
    }

    public void setRecycler() {
        DeviceAdapter adapter = new DeviceAdapter(this, MainApplication.devicesList);
        adapter.setOnItemClickListener((v, i) -> {
            MainApplication.devicesList.setDeviceInfo(i);
            new Handler().postDelayed(this::setRecycler, 120);
        });
        adapter.setOnItemLongClickListener((v, i) -> {
            loginDialog(devicesList.getDeviceInfo(i));
        });
        recycler.setAdapter(adapter);
    }

    public void loginDialog(DeviceInfo deviceInfo) {
        LayoutInflater factory = LayoutInflater.from(DeviceActivity.this);
        final View view = factory.inflate(R.layout.dialog_edit_device, null);
        final SuitekiCardButton btn_y = view.findViewById(R.id.btn_y);//登录
        final SuitekiCardButton btn_n = view.findViewById(R.id.btn_n);//小米账号登录
        final EditText tv = view.findViewById(R.id.editText_name);

        AlertDialog.Builder dialog = new AlertDialog.Builder(DeviceActivity.this);
        dialog.setView(view);
        Dialog d = dialog.show();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_n.setOnClickListener((a) -> {
            MainApplication.devicesList.delInfo(deviceInfo);
            MainApplication.devicesList.saveData();
            setRecycler();
            d.dismiss();
        });//删除设备
        btn_y.setOnClickListener((a) -> {
            deviceInfo.Name = tv.getText().toString();
            MainApplication.devicesList.addDeviceInfo(deviceInfo);
            MainApplication.devicesList.saveData();
            setRecycler();
            d.dismiss();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

}