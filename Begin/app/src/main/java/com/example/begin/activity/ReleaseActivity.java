package com.example.begin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class ReleaseActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private ImageView mIvReleaseActivityBack;
    private EditText mEtReleaseActivityTitle;
    private EditText mEtReleaseActivityDescription;
    private Button mBtReleaseActivityRelease;

    private final String TAG = "ReleaseActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_release);

        initView();
    }

    private void initView(){
        mIvReleaseActivityBack = findViewById(R.id.iv_releaseactivity_back);
        mEtReleaseActivityTitle = findViewById(R.id.et_releaseactivity_title);
        mEtReleaseActivityDescription = findViewById(R.id.et_releaseactivity_description);
        mBtReleaseActivityRelease =findViewById(R.id.bt_releaseactivity_release);

        mBtReleaseActivityRelease.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.bt_releaseactivity_release:
                String title = mEtReleaseActivityTitle.getText().toString().trim();
                String description = mEtReleaseActivityDescription.getText().toString().trim();
                asyncRelease(title, description);
                break;
            case R.id.iv_releaseactivity_back:
                startActivity(new Intent(this, TaskActivity.class));
                finish();
                break;
        }
    }

    private void asyncRelease(final String title, final String description) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                sp = getSharedPreferences("login_info", MODE_PRIVATE);
                final String token = sp.getString("token", "ERROR");

                // okhttp异步POST请求； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                RequestBody requestBody = new FormBody.Builder()
                        .add("title", title)
                        .add("description", description)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getReleaseURL())
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        //showToastInThread(LoginActivity.this, "请求URL失败, 请重试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 先判断一下服务器是否异常
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            /* 使用Gson解析response的JSON数据的第二步 */
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            // 如果返回的status为success，则getStatus返回true，登录验证通过
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                startActivity(new Intent(ReleaseActivity.this, TaskActivity.class));
                                finish();
                            } else {
                                Log.d(TAG, "任务发布失败");
                                showToastInThread(ReleaseActivity.this, "任务发布失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(ReleaseActivity.this, "服务器异常");
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

}
