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
import com.example.begin.adapter.PublishedSearchAdapter;
import com.example.begin.adapter.TaskSearchAdapter;
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

public class PublishedtaskActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private ImageView mIvPublishedtaskActivityBack;
    private static RecyclerView rvList;
    private static PublishedSearchAdapter searchAdapter;
    private List<Tasksource> releasedList = new ArrayList<>();
    private static final String TAG = "PublishedtaskActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_publishedtask);

        initView();
    }

    private void initView(){
        mIvPublishedtaskActivityBack = findViewById(R.id.iv_publishedtaskactivity_back);
        rvList = findViewById(R.id.rv_publishedtaskactivity_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        setAdapter(releasedList);

        String url = NetConstant.getInformListURL();
        PublishedtaskActivity.MyAsyncTask task = new PublishedtaskActivity.MyAsyncTask();
        task.execute(url);

        mIvPublishedtaskActivityBack.setOnClickListener(this);
    }

    private void setAdapter(List<Tasksource> releasedList){
        if(releasedList == null || releasedList.size() == 0){
            rvList.removeAllViews();
            return;
        }
        searchAdapter = new PublishedSearchAdapter(releasedList);
        rvList.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();

    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_publishedtaskactivity_back:
                startActivity(new Intent(this, UserhomeActivity.class));
                finish();
                break;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, List<Tasksource>> {

        @Override
        protected List<Tasksource> doInBackground(String... strings) {

            sp = getSharedPreferences("login_info", MODE_PRIVATE);
            final String token = sp.getString("token", "ERROR");

            String url = NetConstant.getPublishedTaskURL();
            List<Tasksource> releaseList = null;
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
                    releasedList = JSON.parseArray(dataStr, Tasksource.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("releasedList成功获取");
            return releasedList;
        }

        @Override
        protected void onPostExecute(List<Tasksource> info) {
            System.out.println("onPosetExecut() called");
            super.onPostExecute(info);
            setAdapter(info);
        }
    }
}
