package com.example.begin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.example.begin.activity.R;
import com.example.begin.adapter.CourseSearchAdapter;
import com.example.begin.bean.Course;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CourselistActivity extends BaseActivity implements View.OnClickListener{

    // 声明SharedPreferences对象
    SharedPreferences sp;

    private static RecyclerView rvList;
    private static CourseSearchAdapter searchAdapter;
    private ImageButton mIbCourselistActivityTaskBt;
    private ImageButton mIbCourselistActivityUserBt;
    private ImageView mIvCourselistActivityAddcourseBt;
    private static List<Course> courseList = new ArrayList<>();
    private static final String TAG = "CourselistActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courselist);
        initView();

    }

    private void initView(){
        mIbCourselistActivityTaskBt = findViewById(R.id.ib_courseactivity_taskbt);
        mIbCourselistActivityUserBt = findViewById(R.id.ib_courselistactivity_userbt);
        mIvCourselistActivityAddcourseBt = findViewById(R.id.iv_courseactivity_addcourse);
        rvList = findViewById(R.id.rv_CourselistActivity_list);

        //布局管理
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        System.out.println("开始布置Adapter");
        setAdapter(courseList);

        //获取数据
        String url = NetConstant.getCourseListURL();
        MyAsyncTask task = new MyAsyncTask();
        task.execute(url);

        System.out.println("获取数据完成");

        mIbCourselistActivityUserBt.setOnClickListener(this);
        mIbCourselistActivityTaskBt.setOnClickListener(this);
        mIvCourselistActivityAddcourseBt.setOnClickListener(this);
    }

    private void setAdapter(List<Course> courseList) {
        if (courseList == null || courseList.size() == 0) {
            rvList.removeAllViews();
            return;
        }
        searchAdapter = new CourseSearchAdapter(courseList);
        rvList.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }


    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_courseactivity_taskbt:
                startActivity(new Intent(this, TaskActivity.class));
                finish();
                break;
            case R.id.ib_courselistactivity_userbt:
                startActivity(new Intent(this, UserhomeActivity.class));
                finish();
                break;
            case R.id.iv_courseactivity_addcourse:
                startActivity(new Intent(this, CourseaddActivity.class));
                finish();
                break;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, List<Course>> {

        @Override
        protected List<Course> doInBackground(String... strings) {

            System.out.println("doInBackground() called");

            // 声明SharedPreferences对象
            sp = getSharedPreferences("login_info", MODE_PRIVATE);
            final String token = sp.getString("token", "ERROR");//（查找键， 找不到键的默认返回值）

            String url = NetConstant.getCourseListURL();
            List<Course> courseList = null;
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
                    courseList = JSON.parseArray(dataStr, Course.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("courseList成功获取");
            return courseList;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(List<Course> course) {
            System.out.println("onPostExecute() called");
            super.onPostExecute(course);
            setAdapter(course);
        }
    }

}
