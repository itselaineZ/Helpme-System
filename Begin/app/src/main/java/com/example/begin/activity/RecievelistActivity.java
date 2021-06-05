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
import com.example.begin.adapter.FileSearchAdapter;
import com.example.begin.adapter.TaskSearchAdapter;
import com.example.begin.bean.Filesource;
import com.example.begin.bean.Tasksource;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecievelistActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private ImageView mIvRecievelistActivityBack;
    private static RecyclerView rvList;
    private static TaskSearchAdapter searchAdapter;
    private List<Tasksource> taskList = new ArrayList<>();
    private static final String TAG = "RecievelistActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_recievelist);
        initView();
    }

    private void initView(){
        mIvRecievelistActivityBack = findViewById(R.id.iv_recievelistactivity_back);
        rvList = findViewById(R.id.rv_recieveactivity_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        setAdapter(taskList);

        String url = NetConstant.getTaskListURL();
        RecievelistActivity.MyAsyncTask tsk = new RecievelistActivity.MyAsyncTask();
        tsk.execute(url);

        mIvRecievelistActivityBack.setOnClickListener(this);
    }

    private void setAdapter(List<Tasksource> taskList) {
        if (taskList == null || taskList.size() == 0) {
            rvList.removeAllViews();
            return;
        }
        searchAdapter = new TaskSearchAdapter(taskList);
        rvList.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_recievelistactivity_back:
                startActivity(new Intent(this, TaskActivity.class));
                finish();
                break;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, List<Tasksource>> {

        @Override
        protected List<Tasksource> doInBackground(String... strings) {

            sp = getSharedPreferences("login_info", MODE_PRIVATE);
            final String token = sp.getString("token", "ERROR");

            String url = NetConstant.getTaskListURL();
            List<Tasksource> taskList = null;
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
                    taskList = JSON.parseArray(dataStr, Tasksource.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("taskList成功获取");
            return taskList;
        }

        @Override
        protected void onPostExecute(List<Tasksource> task) {
            System.out.println("onPosetExecut() called");
            super.onPostExecute(task);
            setAdapter(task);
        }
    }
}
