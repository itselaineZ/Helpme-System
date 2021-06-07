package com.example.begin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.example.begin.adapter.InformSearchAdapter;
import com.example.begin.bean.Information;
import com.example.begin.bean.Tasksource;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InformlistActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private ImageView mIvInformlistActivityBack;
    private static RecyclerView rvList;
    private static InformSearchAdapter searchAdapter;
    private List<Information> infoList = new ArrayList<>();
    private static final String TAG = "InformlistActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_inform);

        initView();
    }

    private void initView(){
        mIvInformlistActivityBack = findViewById(R.id.iv_informactivity_back);
        rvList = findViewById(R.id.rv_informactivity_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        setAdapter(infoList);

        String url = NetConstant.getInformListURL();
        InformlistActivity.MyAsyncTask task = new InformlistActivity.MyAsyncTask();
        task.execute(url);

        mIvInformlistActivityBack.setOnClickListener(this);
    }

    private void setAdapter(List<Information> infoList){
        if(infoList == null || infoList.size() == 0){
            rvList.removeAllViews();
            return;
        }
        searchAdapter = new InformSearchAdapter(infoList);
        rvList.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();

    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_informactivity_back:
                //startActivity(new Intent(this, UserActivity.class));
                finish();
                break;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, List<Information>> {

        @Override
        protected List<Information> doInBackground(String... strings) {

            sp = getSharedPreferences("login_info", MODE_PRIVATE);
            final String token = sp.getString("token", "ERROR");

            String url = NetConstant.getInformListURL();
            List<Information> infoList = null;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", token)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String jsonStr = response.body().string();
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                String status = jsonObject.get("status").getAsString();
                if (TextUtils.equals(status, "success")) {
                    JsonArray data = jsonObject.get("data").getAsJsonArray();
                    String dataStr = data.toString();
                    infoList = JSON.parseArray(dataStr, Information.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("infoList成功获取");
            return infoList;
        }

        @Override
        protected void onPostExecute(List<Information> info) {
            System.out.println("onPosetExecut() called");
            super.onPostExecute(info);
            setAdapter(info);
        }
    }
}
