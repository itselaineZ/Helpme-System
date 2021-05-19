package com.example.begin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.begin.activity.Code;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    // Log打印的通用Tag
    private final String TAG = "RegisterActivity";

    private String realCode;
    private DBOpenHelper mDBOpenHelper;
    private Button mBtRegisteractivityRegister;
    private RelativeLayout mRlRegisteractivityTop;
    private ImageView mIvRegisteractivityBack;
    private LinearLayout mLlRegisteractivityBody;
    private EditText mEtRegisteractivityUsername;
    private EditText mEtRegisteractivityPassword1;
    private EditText mEtRegisteractivityPassword2;
    private EditText mEtRegisteractivityInputcodes;
    private ImageView mIvRegisteractivityShowcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    
        initView();

        mDBOpenHelper = new DBOpenHelper(this);

        //将验证码用图片的形式显示出来
        mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    private void initView(){
        mBtRegisteractivityRegister = findViewById(R.id.bt_registeractivity_register);
        mRlRegisteractivityTop = findViewById(R.id.rl_registeractivity_top);
        mIvRegisteractivityBack = findViewById(R.id.iv_registeractivity_back);
        mLlRegisteractivityBody = findViewById(R.id.ll_registeractivity_body);
        mEtRegisteractivityUsername = findViewById(R.id.et_registeractivity_username);
        mEtRegisteractivityPassword1 = findViewById(R.id.et_registeractivity_password1);
        mEtRegisteractivityPassword2 = findViewById(R.id.et_registeractivity_password2);
        mEtRegisteractivityInputcodes = findViewById(R.id.et_registeractivity_inputcodes);
        mIvRegisteractivityShowcode = findViewById(R.id.iv_registeractivity_showcode);

        mIvRegisteractivityBack.setOnClickListener(this);
        mIvRegisteractivityShowcode.setOnClickListener(this);
        mBtRegisteractivityRegister.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_registeractivity_back: //返回登录页面
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.iv_registeractivity_showcode:    //改变随机验证码的生成
                mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
            case R.id.bt_registeractivity_register:    //注册按钮
                //获取用户输入的用户名、密码、验证码
                String username = mEtRegisteractivityUsername.getText().toString().trim();
                String password1 = mEtRegisteractivityPassword1.getText().toString().trim();
                String password2 = mEtRegisteractivityPassword2.getText().toString().trim();
                //需要实现两次密码是否相同的比较，此处暂时未实现
                String password = password1;
                String inputcode = mEtRegisteractivityInputcodes.getText().toString().toLowerCase();
                //注册验证
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(inputcode) ) {
                    if (inputcode.equals(realCode)) {
                        //将用户名和密码加入到数据库中
                        asyncRegister(username, password);
                        Intent intent2 = new Intent(this, MainActivity.class);
                        startActivity(intent2);
                        finish();
                        Toast.makeText(this,  "验证通过，注册成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "验证码错误,注册失败", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "未完善信息，注册失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // okhttp异步请求进行注册
    // 参数统一传递字符串
    // 传递到后端再进行类型转换以适配数据库
    private void asyncRegister(final String email, final String password) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "存在输入为空，注册失败", Toast.LENGTH_SHORT).show();
        } else {
            // 发送请求属于耗时操作，开辟子线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // okhttp的使用，POST，异步； 总共5步
                    // 1、初始化okhttpClient对象
                    OkHttpClient okHttpClient = new OkHttpClient();
                    // 2、构建请求体
                    // 注意这里的name 要和后端接收的参数名一一对应，否则无法传递过去
                    RequestBody requestBody = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .build();
                    // 3、发送请求，特别强调这里是POST方式
                    Request request = new Request.Builder()
                            .url(NetConstant.getRegisterURL())
                            .post(requestBody)
                            .build();
                    // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        // 5、重写两个回调方法
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // 先判断一下服务器是否异常
                            String responseStr = response.toString();
                            if (responseStr.contains("200")) {
                                // response.body().string()只能调用一次，多次调用会报错
                                String responseBodyStr = response.body().string();
                                JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                                // 如果返回的status为success，代表验证通过
                                if (getResponseStatus(responseBodyJSONObject).equals("success")) {
                                    // 注册成功，记录token
                                    sp = getSharedPreferences("login_info", MODE_PRIVATE);
                                    editor = sp.edit();
                                    editor.putString("token", "token_value");
                                    editor.putString("email", email);
                                    editor.putString("password", password); // 注意这里是password1

                                    if (editor.commit()) {
                                        Intent it_register_to_main = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(it_register_to_main);
                                        // 注册成功后，注册界面就没必要占据资源了
                                        finish();
                                    }
                                } else {
                                    getResponseErrMsg(RegisterActivity.this, responseBodyJSONObject);
                                }
                            } else {
                                Log.d(TAG, "服务器异常");
                                showToastInThread(RegisterActivity.this, responseStr);
                            }
                        }
                    });

                }
            }).start();
        }
    }

    // 使用Gson解析response的JSON数据中的status，返回布尔值
    private String getResponseStatus(JsonObject responseBodyJSONObject) {
        // Gson解析JSON，总共3步
        // 1、获取response对象的字符串序列化
        // String responseData = response.body().string();
        // 2、通过JSON解析器JsonParser()把字符串解析为JSON对象，
        //
        // *****前两步抽写方法外面了*****
        //
        // JsonObject jsonObject = (JsonObject) new JsonParser().parse(responseBodyStr);
        // 3、通过JSON对象获取对应的属性值
        String status = responseBodyJSONObject.get("status").getAsString();
        return status;
    }

    // 获取验证码响应data
    // 使用Gson解析response返回异常信息的JSON中的data对象
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }
}

