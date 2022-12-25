package ml.sky233.suiteki;

import static ml.sky233.suiteki.MainApplication.thread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Objects;

import ml.sky233.suiteki.util.Setting;

public class LoginActivity extends AppCompatActivity {
    EditText editText_email, editText_password;
    CheckBox checkBox;
    boolean isSave;
    public static String email, password;
    public static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView() {
        activity = this;
        editText_email = findViewById(R.id.login_editText_mail);
        editText_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText_password = findViewById(R.id.login_editText_password);
        editText_email.setText(Setting.getString("user_email"));
        editText_password.setText(Setting.getString("user_password"));
        checkBox = findViewById(R.id.login_checkBox);
        isSave = Setting.getValue("auto_save");
        checkBox.setChecked(Setting.getValue("auto_save"));
        checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> isSave = isChecked);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//开启左上角返回键
        this.setTitle(R.string.login_title);//设置标题
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                email = editText_email.getText().toString();
                password = editText_password.getText().toString();
                Setting.setValue("auto_save",isSave);
                if (isSave){
                    Setting.setString("user_email",email);
                    Setting.setString("user_password",password);
                }else{
                    Setting.setString("user_email","");
                    Setting.setString("user_password","");
                }
                    thread.initActivity(LoginActivity.this);
                new Thread(thread.loginHuami).start();
                break;
            case R.id.login_mi_cardView:
                startActivity(new Intent(LoginActivity.this,WebActivity.class));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();//左上角的返回
        return true;
    }
}