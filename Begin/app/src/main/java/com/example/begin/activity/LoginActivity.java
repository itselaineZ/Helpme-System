package com.example.begin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

import com.example.begin.activity.Code;
import com.example.begin.constant.NetConstant;
import com.example.begin.util.ValidUtils;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    // Log打印的通用Tag
    private final String TAG = "LoginActivity";

    private String realCode;
    private Button mBtLoginactivityLogin;
    private Button mBtLoginactivityRegister;
    private Button mBtLoginactivityCue;
    private EditText mEtLoginactivityUsername;
    private EditText mEtLoginactivityPassword;
    private EditText mEtLoginactivityInputcodes;
    private ImageView mIvLoginactivityShowcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        //将验证码用图片的形式显示出来
        mIvLoginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    private void initView() {
        // 初始化控件
        mBtLoginactivityLogin = findViewById(R.id.bt_loginactivity_login);
        mBtLoginactivityRegister = findViewById(R.id.bt_loginactivity_register);
        mBtLoginactivityCue = findViewById(R.id.bt_loginactivity_cue);
        mEtLoginactivityUsername = findViewById(R.id.et_loginactivity_username);
        mEtLoginactivityPassword = findViewById(R.id.et_loginactivity_password);
        mEtLoginactivityInputcodes = findViewById(R.id.et_loginactivity_inputcodes);
        mIvLoginactivityShowcode = findViewById(R.id.iv_loginactivity_Showcodes);

        // 设置点击事件监听器
        mBtLoginactivityLogin.setOnClickListener(this);
        mBtLoginactivityRegister.setOnClickListener(this);
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
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password))
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                else if (inputcode.equals(realCode) == false)
                    Toast.makeText(this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
                else if (!(ValidUtils.isEmailValid(name) && ValidUtils.isPasswordValid(password))) {
                    Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                else asyncLogin(name, password);
                break;
            case R.id.bt_loginactivity_cue:
                startActivity(new Intent(this, CueActivity.class));
                finish();
                break;
            case R.id.iv_loginactivity_Showcodes:
                mIvLoginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
        }
    }

    private void asyncLogin(final String email, final String password) {
        /*
         发送请求属于耗时操作，所以开辟子线程执行
         上面的参数都加上了final，否则无法传递到子线程中
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                // okhttp异步POST请求； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                RequestBody requestBody = new FormBody.Builder()
                        .add("email", email)
                        .add("password", password)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getLoginURL())
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToastInThread(LoginActivity.this, "请求URL失败, 请重试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 先判断一下服务器是否异常
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                             /*
                            注意这里，同一个方法内
                            response.body().string()只能调用一次，多次调用会报错
                             */
                            /* 使用Gson解析response的JSON数据的第一步 */
                            String responseBodyStr = response.body().string();
                            /* 使用Gson解析response的JSON数据的第二步 */
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            // 如果返回的status为success，则getStatus返回true，登录验证通过
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                            /*
                             更新token，下次自动登录
                             真实的token值应该是一个加密字符串
                             我为了让token不为null，就随便传了一个字符串
                             这里的telphone和password每次都要重写的
                             否则无法实现修改密码
                            */
                                String username = responseBodyJSONObject.get("username").getAsString();
                                final String token = getToken(responseBodyJSONObject);
                                sp = getSharedPreferences("login_info", MODE_PRIVATE);
                                editor = sp.edit();
                                editor.putString("token", token);
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putString("username", username);
                                if (editor.commit()) {
                                    Intent it_login_to_main = new Intent(LoginActivity.this, CourselistActivity.class);
                                    startActivity(it_login_to_main);
                                    // 登录成功后，登录界面就没必要占据资源了
                                    finish();
                                } else {
                                    showToastInThread(LoginActivity.this, "token保存失败，请重新登录");
                                }
                            } else {
                                //getResponseErrMsg(LoginActivity.this, responseBodyJSONObject);
                                Log.d(TAG, "账号或密码验证失败");
                                showToastInThread(LoginActivity.this, "帐号或密码验证失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(LoginActivity.this, responseStr);
                        }
                    }
                });

            }
        }).start();
    }
    /*
      使用Gson解析response的JSON数据
      本来总共是有三步的，一、二步在方法调用之前执行了
    */
    private String getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status;
    }

    private final String getToken(JsonObject responseBodyJSONObject) {
        String token = responseBodyJSONObject.get("token").getAsString();
        return token;
    }

    /*
      使用Gson解析response返回异常信息的JSON中的data对象
      这也属于第三步，一、二步在方法调用之前执行了
     */
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        /*
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);*/
    }
}
