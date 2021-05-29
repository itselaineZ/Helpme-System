package com.example.begin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class CourseaddActivity extends BaseActivity implements View.OnClickListener{

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    // Log打印的通用Tag
    private final String TAG = "CourseaddActivity";

    private EditText mEtCourseaddActivityCoursename;
    private EditText mEtCourseaddActivityCourseID;
    private Button mBtCourseaddActivityAddBt;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_courseadd);

        initView();
    }

    private void initView(){
        mEtCourseaddActivityCourseID = findViewById(R.id.et_courseaddactivity_courseid);
        mEtCourseaddActivityCoursename = findViewById(R.id.et_courseaddactivity_coursename);
        mBtCourseaddActivityAddBt = findViewById(R.id.bt_courseaddactivity_addbt);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_courseaddactivity_addbt:
                String coursename = mEtCourseaddActivityCoursename.getText().toString().trim();
                String coursrid = mEtCourseaddActivityCourseID.getText().toString().trim();
                asyncCourse(coursename, coursrid);
                break;
        }
    }

    private void asyncCourse(final String coursename, final String courseid) {
        /*
         发送请求属于耗时操作，所以开辟子线程执行
         上面的参数都加上了final，否则无法传递到子线程中
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                // okhttp异步POST请求； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                RequestBody requestBody = new FormBody.Builder()
                        .add("name", coursename)
                        .add("no", courseid)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getLoginURL())
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
                            /*
                             更新token，下次自动登录
                             真实的token值应该是一个加密字符串
                             我为了让token不为null，就随便传了一个字符串
                             这里的telphone和password每次都要重写的
                             否则无法实现修改密码
                            */
                                sp = getSharedPreferences("course_info", MODE_PRIVATE);
                                editor = sp.edit();
                                editor.putString("token", "token_value");
                                editor.putString("name", coursename);
                                editor.putString("no", courseid);
                                if (editor.commit()) {
                                    startActivity(new Intent(CourseaddActivity.this, FileActivity.class));
                                    finish();
                                } else {
                                    showToastInThread(CourseaddActivity.this, "token保存失败，请重新提交");
                                }
                            } else {
                                getResponseErrMsg(CourseaddActivity.this, responseBodyJSONObject);
                                //Log.d(TAG, "账号或密码验证失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            //showToastInThread(LoginActivity.this, responseStr);
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

    /*
      使用Gson解析response返回异常信息的JSON中的data对象
      这也属于第三步，一、二步在方法调用之前执行了
     */
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }

}
