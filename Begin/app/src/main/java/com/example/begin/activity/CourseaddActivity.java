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

public class CourseaddActivity extends BaseActivity implements View.OnClickListener{

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // Log打印的通用Tag
    private final String TAG = "CourseaddActivity";

    private EditText mEtCourseaddActivityCoursename;
    private EditText mEtCourseaddActivityCourseID;
    private Button mBtCourseaddActivityAddBt;
    private ImageView mIvCourseaddActivityBack;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_courseadd);

        initView();
    }

    private void initView(){
        //mEtCourseaddActivityCourseID = findViewById(R.id.et_courseaddactivity_courseid);
        mEtCourseaddActivityCoursename = findViewById(R.id.et_courseaddactivity_coursename);
        mBtCourseaddActivityAddBt = findViewById(R.id.bt_courseaddactivity_addbt);
        mIvCourseaddActivityBack = findViewById(R.id.iv_courseaddactivity_back);

        mIvCourseaddActivityBack.setOnClickListener(this);
        mBtCourseaddActivityAddBt.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_courseaddactivity_addbt:
                String coursename = mEtCourseaddActivityCoursename.getText().toString().trim();
                asyncCourse(coursename);
                break;
            case R.id.iv_courseaddactivity_back:
                startActivity(new Intent(this, CourselistActivity.class));
                finish();
                break;
        }
    }

    private void asyncCourse(final String courseName) {
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
                        .add("courseName", courseName)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getCourseAddURL())
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
                                startActivity(new Intent(CourseaddActivity.this, CourselistActivity.class));
                                finish();
                            } else {
                                Log.d(TAG, "课程添加失败");
                                showToastInThread(CourseaddActivity.this, "课程添加失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(CourseaddActivity.this, "服务器异常");
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
