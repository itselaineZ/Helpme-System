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
                        showToastInThread(RecievedetailActivity.this, "??????URL??????, ????????????");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                showToastInThread(RecievedetailActivity.this, "????????????");
                                startActivity(new Intent(RecievedetailActivity.this, RecievedtaskActivity.class));
                                finish();
                            } else {
                                Log.d(TAG, "????????????");
                                showToastInThread(RecievedetailActivity.this, "????????????");
                            }
                        } else {
                            Log.d(TAG, "???????????????");
                            showToastInThread(RecievedetailActivity.this, "???????????????");
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
