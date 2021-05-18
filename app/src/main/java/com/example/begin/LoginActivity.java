package com.example.begin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String realCode;
    private DBOpenHelper mDBOpenHelper;
    private Button mBtLoginactivityLogin;
    private Button mBtLoginactivityRegister;
    private Button mBtLoginactivityCue;
    private Button mbtLoginactivityFindpassword;
    private RelativeLayout mRlLoginactivityTop;
    private EditText mEtLoginactivityUsername;
    private EditText mEtLoginactivityPassword;
    private EditText mEtLoginactivityInputcodes;
    private ImageView mIvLoginactivityShowcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        mDBOpenHelper = new DBOpenHelper(this);
        //将验证码用图片的形式显示出来
        mIvLoginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    private void initView() {
        // 初始化控件
        mBtLoginactivityLogin = findViewById(R.id.bt_loginactivity_login);
        mBtLoginactivityRegister = findViewById(R.id.bt_loginactivity_register);
        mBtLoginactivityCue = findViewById(R.id.bt_loginactivity_cue);
        mbtLoginactivityFindpassword = findViewById(R.id.bt_loginactivity_findpassword);
        mRlLoginactivityTop = findViewById(R.id.rl_loginactivity_top);
        mEtLoginactivityUsername = findViewById(R.id.et_loginactivity_username);
        mEtLoginactivityPassword = findViewById(R.id.et_loginactivity_password);
        mEtLoginactivityInputcodes = findViewById(R.id.et_loginactivity_inputcodes);
        mIvLoginactivityShowcode = findViewById(R.id.iv_loginactivity_Showcodes);

        // 设置点击事件监听器
        mBtLoginactivityLogin.setOnClickListener(this);
        mBtLoginactivityRegister.setOnClickListener(this);
        mbtLoginactivityFindpassword.setOnClickListener(this);
        mBtLoginactivityCue.setOnClickListener(this);
        mIvLoginactivityShowcode.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            // 跳转到注册界面
            case R.id.bt_loginactivity_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;

            case R.id.bt_loginactivity_login:
                String name = mEtLoginactivityUsername.getText().toString().trim();
                String password = mEtLoginactivityPassword.getText().toString().trim();
                String inputcode = mEtLoginactivityInputcodes.getText().toString().toLowerCase();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && inputcode.equals(realCode)) {
                    ArrayList<User> data = mDBOpenHelper.getAllData();
                    boolean match = false;
                    for (int i = 0; i < data.size(); i++) {
                        User user = data.get(i);
                        if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                            match = true;
                            break;
                        } else {
                            match = false;
                        }
                    }
                    if (match) {
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();//销毁此Activity
                    } else {
                        Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else if(inputcode.equals(realCode) == false) {
                    Toast.makeText(this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_loginactivity_cue:
                startActivity(new Intent(this, CueActivity.class));
                finish();
                break;
            case R.id.bt_loginactivity_findpassword:
                startActivity(new Intent(this, FindpasswordActivity.class));
                finish();
                break;
            case R.id.iv_loginactivity_Showcodes:
                mIvLoginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
        }
    }
}



