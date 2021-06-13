package com.example.begin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import com.example.begin.constant.NetConstant;
import com.example.begin.util.FileChooseUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FileaddActivity extends BaseActivity implements View.OnClickListener{

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    // Log打印的通用Tag
    private final String TAG = "FileaddActivity";
    private String courseName;
    private String path;

    private ImageView mIvFileaddActivityBack;
    private EditText mEtFileaddActivityFilename;
    private Button mBtFileaddActivityChoose;
    private Button mBtFileaddActivityUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileadd);
        initView();

        Bundle bundle = this.getIntent().getExtras();
        courseName = bundle.getString("courseName");

    }

    private void initView(){
        mIvFileaddActivityBack = findViewById(R.id.iv_fileaddactivity_back);
        mEtFileaddActivityFilename = findViewById(R.id.et_fileaddactivity_filename);
        mBtFileaddActivityChoose = findViewById(R.id.bt_fileaddactivity_choose);
        mBtFileaddActivityUpload = findViewById(R.id.bt_fileaddactivity_upload);

        mIvFileaddActivityBack.setOnClickListener(this);

        mBtFileaddActivityChoose.setOnClickListener(v-> {
            chooseFile();
        });
        mBtFileaddActivityUpload.setOnClickListener(v->
        {
            uploadFile(path,mEtFileaddActivityFilename.getText().toString());
        });

    }

    public void chooseFile() {
        String [] permissions = new String[]{
                "android.permission.READ_EXTERNAL_STORAGE"
        };//所需权限
        if(ActivityCompat.checkSelfPermission(this,permissions[0]) != PackageManager.PERMISSION_GRANTED)
        //如果没有权限
            ActivityCompat.requestPermissions(this,permissions,1);//申请权限

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//使用系统的文件选择器
        intent.setType("*/*");//所有类型的文件
        intent.addCategory(Intent.CATEGORY_OPENABLE);//期望获取的数据可以作为一个File打开
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            Uri uri = data.getData();
            String chooseFilePath = FileChooseUtil.getInstance(this).getChooseFileResultPath(uri);
            Log.d(TAG,"选择文件返回：" + chooseFilePath);
            path = chooseFilePath;
            //sendFileMessage(chooseFilePath);
        }
    }

    public void uploadFile(String path, String filename) {
        File file = new File(path);
        if(path.isEmpty() || !file.exists()){
            showToastInThread(FileaddActivity.this, "获取文件失败，请检查文件路径是否存在");
            return ;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // okhttp异步POST请求； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("courseMaterial",filename,RequestBody.create(file, MediaType.parse("multipart/form-data")))
                        //.addFormDataPart("courseMaterial",filename,RequestBody.create(MEDIA_OBJECT_STREAM, file))
                        .addFormDataPart("courseMaterialName",filename)
                        .addFormDataPart("courseName", courseName)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                sp = getSharedPreferences("login_info", MODE_PRIVATE);
                String token = sp.getString("token", "ERROR");
                Request request = new Request.Builder()
                        .url(NetConstant.getFileAddURL())
                        .addHeader("Authorization", token)
                        .post(body)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToastInThread(FileaddActivity.this, "请求URL失败, 请重试！");
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
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                Intent intent = new Intent(FileaddActivity.this, FileActivity.class);
                                intent.putExtra("courseName", courseName);
                                startActivity(intent);
                                // 登录成功后，登录界面就没必要占据资源了
                                finish();
                            } else {
                                Log.d(TAG, "上传失败");
                                showToastInThread(FileaddActivity.this, "上传失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(FileaddActivity.this, responseStr);
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

    private final String getToken(JsonObject responseBodyJSONObject) {
        String token = responseBodyJSONObject.get("token").getAsString();
        return token;
    }


    public void showMessage(String message)
    {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_fileaddactivity_back:
                Intent intent = new Intent(this, FileActivity.class);
                intent.putExtra("courseName", courseName);
                startActivity(intent);
                break;
        }
    }

}
