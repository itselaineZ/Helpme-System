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

public class PublishdetailActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private ImageView mIvPublishdetailActivityBack;
    private TextView mTvPublishdetailActivityTitle;
    private TextView mTvPublishdetailActivitydescription;
    private Button mBtPublishdetailActivityFinish;
    private Button mBtPublishdetailActivityGiveUp;
    private String taskId;
    private String title;
    private String description;
    private static String TAG = "PublishdetailActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_publishdetail);

        Bundle bundle = this.getIntent().getExtras();
        taskId = bundle.getString("taskId");
        title = bundle.getString("title");
        description = bundle.getString("description");

        initView();
    }

    private void initView(){
        mIvPublishdetailActivityBack = findViewById(R.id.iv_publishdetailactivity_back);
        mTvPublishdetailActivityTitle = findViewById(R.id.tv_publishdetailactivity_title);
        mTvPublishdetailActivitydescription = findViewById(R.id.tv_publishdetailactivity_description);
        mBtPublishdetailActivityFinish = findViewById(R.id.bt_publishdetailactivity_finish);
        mBtPublishdetailActivityGiveUp = findViewById(R.id.bt_publishdetailactivity_giveup);

        mTvPublishdetailActivitydescription.setText(description);
        mTvPublishdetailActivityTitle.setText(title);

        mIvPublishdetailActivityBack.setOnClickListener(this);
        mBtPublishdetailActivityFinish.setOnClickListener(this);
        mBtPublishdetailActivityGiveUp.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_publishdetailactivity_back:
                startActivity(new Intent(this, PublishedtaskActivity.class));
                finish();
                break;
            case R.id.bt_publishdetailactivity_finish:
                asyncFinish(NetConstant.getFinishByPublisherURL());
                break;
            case R.id.bt_publishdetailactivity_giveup:
                asyncFinish(NetConstant.getPublisherGiveUpURL());
                break;
        }
    }

    private void asyncFinish(final String URL) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                sp = getSharedPreferences("login_info", MODE_PRIVATE);
                final String token = sp.getString("token", "ERROR");

                // okhttp??????POST????????? ??????5???
                // 1????????????okhttpClient??????
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2??????????????????requestBody
                RequestBody requestBody = new FormBody.Builder()
                        .add("taskId", taskId)
                        .build();
                // 3????????????????????????????????????????????????POST??????
                Request request = new Request.Builder()
                        .url(URL)
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();
                // 4?????????okhttpClient????????????????????????????????????enqueue()????????????????????????
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5???????????????????????????
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "??????URL????????? " + e.getMessage());
                        showToastInThread(PublishdetailActivity.this, "??????URL??????, ????????????");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                startActivity(new Intent(PublishdetailActivity.this, PublishedtaskActivity.class));
                                finish();
                            } else {
                                Log.d(TAG, "????????????");
                                showToastInThread(PublishdetailActivity.this, "????????????");
                            }
                        } else {
                            Log.d(TAG, "???????????????");
                            showToastInThread(PublishdetailActivity.this, "???????????????");
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

}
