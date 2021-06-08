package com.example.begin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.example.begin.bean.Filesource;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class TaskdetailActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private ImageView mIvTaskdetailActivityBack;
    private Button mBtTaskdetailActivityRecieve;
    private TextView mTvTaskdetailActivityTitle;
    private TextView mTvTaskdetailActivityPublisher;
    private TextView mTvTaskdetailActivityDescription;
    private String taskId;
    private String publisherId;
    private String description;
    private String title;
    private final String TAG = "TaskdetailActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_taskdetail);

        Bundle bundle = this.getIntent().getExtras();
        taskId = bundle.getString("taskId");
        publisherId = bundle.getString("publisherId");
        description = bundle.getString("description");
        title = bundle.getString("title");

        initView();
    }

    private void initView(){
        mIvTaskdetailActivityBack = findViewById(R.id.iv_taskdetailactivity_back);
        mBtTaskdetailActivityRecieve = findViewById(R.id.bt_taskdetailactivity_recieve);
        mTvTaskdetailActivityTitle = findViewById(R.id.tv_taskdetailactivity_title);
        mTvTaskdetailActivityPublisher = findViewById(R.id.tv_taskdetailactivity_publisher);
        mTvTaskdetailActivityDescription = findViewById(R.id.tv_taskdetailactivity_description);

        mTvTaskdetailActivityDescription.setText(description);
        mTvTaskdetailActivityPublisher.setText(publisherId);
        mTvTaskdetailActivityTitle.setText(title);

        mBtTaskdetailActivityRecieve.setOnClickListener(this);
        mIvTaskdetailActivityBack.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_taskdetailactivity_back:
                startActivity(new Intent(this, RecievelistActivity.class));
                finish();
                break;
            case R.id.bt_taskdetailactivity_recieve:
                asyncRecieve(taskId);
                break;
        }
    }

    private void asyncRecieve(final String taskId) {
        /*
         发送请求属于耗时操作，所以开辟子线程执行
         上面的参数都加上了final，否则无法传递到子线程中
        */
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
                        .add("taskId", taskId)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getLoginURL())
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
                                startActivity(new Intent(TaskdetailActivity.this, RecievelistActivity.class));
                                finish();
                            } else {
                                getResponseErrMsg(TaskdetailActivity.this, responseBodyJSONObject);
                                Log.d(TAG, "课程添加失败");
                                showToastInThread(TaskdetailActivity.this, "课程添加失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(TaskdetailActivity.this, "服务器异常");
                        }
                    }
                });

            }
        }).start();
    }

    private String getStatus(JsonObject responseBodyJSONObject) {
        String status = responseBodyJSONObject.get("status").getAsString();
        return status;
    }

    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }


}
