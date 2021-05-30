package com.example.begin.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.example.begin.adapter.FileSearchAdapter;
import com.example.begin.bean.Course;
import com.example.begin.bean.Filesource;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileActivity extends BaseActivity implements View.OnClickListener{

    private static RecyclerView rvList;
    private static FileSearchAdapter searchAdapter;
    private ImageButton mIbFileActivityTaskBt;
    private ImageButton mIbFileActivityUserBt;
    private ImageView mIvFileActivityAddfileBt;
    private List<Filesource> fileList = new ArrayList<>();
    private static final String TAG = "FileActivity";
    private ImageButton mIbFileActivityAddfile;
    public static String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);

        Bundle bundle = this.getIntent().getExtras();
        courseName = bundle.getString("courseName");

        initView();

    }

    private void initView(){
        mIbFileActivityTaskBt = findViewById(R.id.ib_fileactivity_taskbt);
        mIbFileActivityUserBt = findViewById(R.id.ib_fileactivity_userbt);
        mIvFileActivityAddfileBt = findViewById(R.id.iv_fileactivity_addfile);
        rvList = findViewById(R.id.rv_fileactivity_list);

        //布局管理
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        setAdapter(fileList);

        //获取数据
        String url = NetConstant.getCourseListURL();
        FileActivity.MyAsyncTask task = new FileActivity.MyAsyncTask();
        task.execute(url);

        mIbFileActivityUserBt.setOnClickListener(this);
        mIbFileActivityTaskBt.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_fileactivity_taskbt:
                startActivity(new Intent(this, TaskActivity.class));
                finish();
                break;
            case R.id.ib_fileactivity_userbt:
                //startActivity(new Intent(this, UsersetActivity.class));
                finish();
                break;
            case R.id.iv_fileactivity_addfile:
                //startActivity(new Intent(this, FileaddActivity.class));
                finish();
                break;

        }
    }

    private static void setAdapter(List<Filesource> fileList) {
        if (fileList == null || fileList.size() == 0) {
            rvList.removeAllViews();
            return;
        }
        searchAdapter = new FileSearchAdapter(fileList);
        rvList.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    private static class MyAsyncTask extends AsyncTask<String, Void, List<Filesource>> {

        @Override
        protected List<Filesource> doInBackground(String... strings) {
            String url = strings[0];
            List<Filesource> fileList = null;
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("CourseName", courseName)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
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
                    fileList = JSON.parseArray(dataStr, Filesource.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileList;
        }

        @Override
        protected void onPostExecute(List<Filesource> file) {
            super.onPostExecute(file);
            setAdapter(file);
        }
    }
}
