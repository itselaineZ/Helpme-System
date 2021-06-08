package com.example.begin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import com.example.begin.constant.NetConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FiledetailActivity extends BaseActivity implements View.OnClickListener{

    SharedPreferences sp;

    private TextView mTvFiledetailActivityCourseName;
    private TextView mTvFiledetailActivityFileName;
    private Button mBtFiledetailActivityDownload;
    private Button mBtFiledetailActivityEvaluate;
    private ImageView mIvFiledetailActivityBack;
    private RadioButton mRbFiledetailActivityOne;
    private RadioButton mRbFiledetailActivityTwo;
    private RadioButton mRbFiledetailActivityThree;
    private RadioButton mRbFiledetailActivityFour;
    private RadioButton mRbFiledetailActivityFive;
    private RadioGroup mRgFiledetailActivity;
    private String fileName;
    private final String TAG = "FiledetailActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_filedetail);

        Bundle bundle = this.getIntent().getExtras();
        fileName = bundle.getString("fileName");

        initView();

        mBtFiledetailActivityDownload.setOnClickListener(v->
        {
            String [] permissions = new String[]{
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            };//所需权限
            if(ActivityCompat.checkSelfPermission(this,permissions[0]) != PackageManager.PERMISSION_GRANTED)
            //如果没有权限
                ActivityCompat.requestPermissions(this,permissions,1);//申请权限

            File file = downloadFile(fileName);
            if(file == null)
                showMessage("文件不存在");
            else
                showMessage("文件下载成功");
        });
    }

    private void initView(){
        mTvFiledetailActivityFileName = findViewById(R.id.tv_filedetailactivity_filename);
        mBtFiledetailActivityDownload = findViewById(R.id.bt_filedetailactivity_download);
        mBtFiledetailActivityEvaluate = findViewById(R.id.bt_filedetailactivity_evaluate);
        mIvFiledetailActivityBack = findViewById(R.id.iv_filedetailactivity_back);
        mRbFiledetailActivityOne = findViewById(R.id.rb_filedetailactivity_one);
        mRbFiledetailActivityTwo = findViewById(R.id.rb_filedetailactivity_two);
        mRbFiledetailActivityThree = findViewById(R.id.rb_filedetailactivity_three);
        mRbFiledetailActivityFour = findViewById(R.id.rb_filedetailactivity_four);
        mRbFiledetailActivityFive = findViewById(R.id.rb_filedetailactivity_five);
        mRgFiledetailActivity = findViewById(R.id.rg_filedetailactivity_evaluate);

        mTvFiledetailActivityFileName.setText(fileName);

        mBtFiledetailActivityDownload.setOnClickListener(this);
        mBtFiledetailActivityEvaluate.setOnClickListener(this);
        mIvFiledetailActivityBack.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_filedetailactivity_evaluate:
                int count = mRgFiledetailActivity.getChildCount();
                int score = 0;
                for(int i = 0 ;i < count;i++){
                    RadioButton rb = (RadioButton)mRgFiledetailActivity.getChildAt(i);
                    if(rb.isChecked()){
                        Toast.makeText(FiledetailActivity.this, "选中"+rb.getTag().toString(), Toast.LENGTH_SHORT).show();
                        score = i;
                        break;
                    }
                }
                mBtFiledetailActivityEvaluate.setEnabled(false);
                asyncEvaluate(String.valueOf(score));
            case R.id.bt_filedetailactivity_download:
                //下载
                //startActivity(new Intent(this, ));
                finish();
                break;
            case R.id.iv_filedetailactivity_back:
                startActivity(new Intent(this, CourselistActivity.class));
                finish();
                break;
        }
    }

    private void asyncEvaluate(final String score) {
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
                        .add("score", score)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getLoginURL())
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToastInThread(FiledetailActivity.this, "请求URL失败, 请重试！");
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
                                Log.d(TAG, "评价成功");
                                showToastInThread(FiledetailActivity.this, "评价成功，感谢您");
                            } else {
                                getResponseErrMsg(FiledetailActivity.this, responseBodyJSONObject);
                                Log.d(TAG, "返回状态不是success");
                                showToastInThread(FiledetailActivity.this, "评价失败，请检查服务器");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(FiledetailActivity.this, "评价失败，无法连接到服务器");
                        }
                    }
                });

            }
        }).start();
    }

    private String getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
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

    public File downloadFile(String filename)
    {
        OkHttpClient okhttp = new OkHttpClient();
        if(filename == null || filename.isEmpty())
            return null;
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("courseMaterialName",filename)
                .build();

        FutureTask<File> task = new FutureTask<>(()->
        {
            String URL = NetConstant.getDownloadURL();
            ResponseBody responseBody = okhttp.newCall(
                    new Request.Builder()
                            .post(body)
                            .url(URL)
                            .build()
            ).execute().body();
            if(responseBody != null)
            {
                if(getExternalFilesDir(null) != null)
                {
                    File file = new File(getExternalFilesDir(null).toString() + "/" + filename);
                    try (
                            InputStream inputStream = responseBody.byteStream();
                            FileOutputStream outputStream = new FileOutputStream(file)
                    )
                    {
                        byte[] b = new byte[1024];
                        int n;
                        if((n = inputStream.read(b)) != -1)
                        {
                            outputStream.write(b,0,n);
                            while ((n = inputStream.read(b)) != -1)
                                outputStream.write(b, 0, n);
                            return file;
                        }
                        else
                        {
                            file.delete();
                            return null;
                        }
                    }
                }
            }
            return null;
        });
        try
        {
            new Thread(task).start();
            return task.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void showMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void e(String message)
    {
        Log.e("LOG_E",message);
    }
}
