package ml.sky233.suiteki;

import static ml.sky233.suiteki.MainApplication.TAG;
import static ml.sky233.suiteki.MainApplication.mService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import ml.sky233.suiteki.callback.ProgressCallback;
import ml.sky233.suiteki.service.ble.BleActions;
import ml.sky233.suiteki.service.ble.BleService;
import ml.sky233.suiteki.service.ble.HuamiService;
import ml.sky233.suiteki.util.BytesUtils;
import ml.sky233.suiteki.util.FileUtils;
import ml.sky233.suiteki.util.ViewUtils;
import ml.sky233.suiteki.widget.CircleProgressView;

public class InstallActivity extends AppCompatActivity implements ProgressCallback {
    TextView tv_int, tv_text;
    CircleProgressView progress;
    ProgressCallback callback = this;

    public static InputStream inputStream = null;
    public static byte[] bytes = null;

    boolean isInstalling = false;

    public static void setInputStream(InputStream input) {
        inputStream = input;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (this.getApplicationContext().getResources().getConfiguration().uiMode != 0x21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        View back = findViewById(R.id.title_install_back);
        ViewUtils.addTouchScale(back);
        back.setOnClickListener((v) -> {
            if (!isInstalling)
                InstallActivity.this.finish();
            else
                Toast.makeText(InstallActivity.this, "正在安装,请勿退出", Toast.LENGTH_SHORT).show();
        });
        back.setOnLongClickListener((v) -> {
            InstallActivity.this.finish();
            return false;
        });
        tv_int = findViewById(R.id.progress_int);
        tv_text = findViewById(R.id.install_text);
        progress = findViewById(R.id.progress);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleActions.ACTION_BLE_FIRMWARE_NOTIFY);
        intentFilter.addAction(BleActions.ACTION_BLE_FIRMWARE_INSTALLING);
        intentFilter.addAction(BleActions.ACTION_BLE_FIRMWARE_INSTALLED);
        this.registerReceiver(mReceiver, intentFilter);//注册广播

        progress.setProgress(0);
        tv_text.setText("正在安装");
        tv_int.setText("0%");
        isInstalling = true;
        if (inputStream != null)
            mService.writeFirmware(FileUtils.getFileBytes(inputStream), callback);
        else if (bytes != null)
            mService.writeFirmware(bytes, callback);
        else {
            tv_text.setText("安装失败");
            isInstalling = false;
        }
        inputStream = null;
        bytes = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isInstalling) InstallActivity.this.finish();
            else
                Toast.makeText(InstallActivity.this, "正在安装,请勿退出", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isInstalling)
                InstallActivity.this.finish();
            else
                Toast.makeText(InstallActivity.this, "正在安装,请勿退出", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        super.onActivityResult(requestCode, resultCode, resultData);
//        if (resultData == null) return;
//        progress.setProgress(0);
//        tv_text.setText("正在安装");
//        tv_int.setText("0%");
//        btn.setEnabled(false);
//        Log.d(TAG, resultData.getData().toString());
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            DocumentFile df = DocumentFile.fromSingleUri(this, resultData.getData());
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(df.getUri());
//                mService.writeFirmware(FileUtils.getFileBytes(inputStream), callback);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                StringWriter stringWriter = new StringWriter();
//                e.printStackTrace(new PrintWriter(stringWriter));
//            }
//        }
//    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BleActions.ACTION_BLE_FIRMWARE_INSTALLED)) {
                tv_int.setText(100 + "%");
                tv_text.setText("安装成功");
                isInstalling = false;
                inputStream = null;
                bytes = null;
                progress.setProgress(100);
                BleService.setStatus(HuamiService.STATUS_BLE_NORMAL);
            } else if (intent.getAction().equals(BleActions.ACTION_BLE_FIRMWARE_NOTIFY)) {
                byte[] value = intent.getByteArrayExtra("value");
                String value_str = BytesUtils.bytesToHexStr(value);
                switch (value_str) {
                    case "10D30A":
                        tv_text.setText("同步超时\n10D30A");
                        isInstalling = false;
                        //同步过程错误
                        BleService.setStatus(HuamiService.STATUS_BLE_NORMAL);

                        break;
                    case "10D656":
                        tv_int.setText(99 + "%");
                        progress.setProgress(99);
                        tv_text.setText("文件不支持\n10D656");
                        isInstalling = false;
                        BleService.setStatus(HuamiService.STATUS_BLE_NORMAL);

                        //文件不支持
                        break;
                    case "10D347":
                        //空间不足
                        tv_text.setText("设备空间不足\n请删除部分表盘再试\n10D347");
                        isInstalling = false;
                        BleService.setStatus(HuamiService.STATUS_BLE_NORMAL);

                        return;
                    case "10D203":
                        tv_text.setText("验证过时,请等待重新验证\n10D203");
                        isInstalling = false;
                        //手环验证过时,需要重新验证

                        break;
                    default:
                        if (!value_str.equals("10D501") && value_str.startsWith("10D5")) {
                            tv_text.setText("文件不支持\n" + value_str);
                            isInstalling = false;
                            //莫名其妙的文件错误
                            BleService.setStatus(HuamiService.STATUS_BLE_NORMAL);

                        }
                }
            }
            // 处理自定义广播
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChange(int progress) {
        tv_int.setText(progress + "%");
        this.progress.setProgress(progress);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}