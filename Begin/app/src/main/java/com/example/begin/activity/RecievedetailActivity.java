package com.example.begin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class RecievedetailActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private ImageView mIvRecievedetailActivityBack;
    private TextView mTvRecievedetailActivityTitle;
    private TextView mTvRecievedetailActivityDescription;
    private Button mBtRecievedetailActivityFinish;
    private Button mBtRecievedetailActivityGiveup;
    private String taskId;
    private String title;
    private String description;
    private static String TAG = "RecievedetailActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_recievedetail);

        Bundle bundle = this.getIntent().getExtras();
        taskId = bundle.getString("taskId");
        title = bundle.getString("title");
        description = bundle.getString("description");

        initView();
    }

    private void initView(){
        mIvRecievedetailActivityBack = findViewById(R.id.iv_recievedetailactivity_back);
        mTvRecievedetailActivityTitle = findViewById(R.id.tv_recievedetailactivity_title);
        mTvRecievedetailActivityDescription = findViewById(R.id.tv_recievedetailactivity_description);
        mBtRecievedetailActivityFinish = findViewById(R.id.bt_recievedetailactivity_finish);
        mBtRecievedetailActivityGiveup = findViewById(R.id.bt_recievedetailactivity_giveup);

        mTvRecievedetailActivityTitle.setText(title);
        mTvRecievedetailActivityDescription.setText(description);

        mBtRecievedetailActivityGiveup.setOnClickListener(this);
        mBtRecievedetailActivityFinish.setOnClickListener(this);
        mIvRecievedetailActivityBack.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_recievedetailactivity_back:
                startActivity(new Intent(this, RecievedtaskActivity.class));
                finish();
                break;
            case R.id.bt_recievedetailactivity_finish:
                asyncPost(NetConstant.getFinishByReceiverURL());
                break;
            case R.id.bt_recievedetailactivity_giveup:
                asyncPost(NetConstant.getReceiverGiveUpURL());
                break;
        }
    }

    private void asyncPost(final String URL) {
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
                        .url(URL)
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToastInThread(RecievedetailActivity.this, "请求URL失败, 请重试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                showToastInThread(RecievedetailActivity.this, "操作成功");
                                startActivity(new Intent(RecievedetailActivity.this, RecievedtaskActivity.class));
                                finish();
                            } else {
                                getResponseErrMsg(RecievedetailActivity.this, responseBodyJSONObject);
                                Log.d(TAG, "操作失败");
                                showToastInThread(RecievedetailActivity.this, "操作失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(RecievedetailActivity.this, "服务器异常");
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
