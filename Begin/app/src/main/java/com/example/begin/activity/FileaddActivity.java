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

        mBtFileaddActivityChoose.setOnClickListener(this);
        mBtFileaddActivityUpload.setOnClickListener(this);
        mIvFileaddActivityBack.setOnClickListener(this);

        mBtFileaddActivityChoose.setOnClickListener(v-> {
            chooseFile();
        });
        mBtFileaddActivityUpload.setOnClickListener(v->
        {
            showMessage(uploadFile(path,mEtFileaddActivityFilename.getText().toString()) ? "上传成功" : "上传失败");
        });

    }

    public void chooseFile()
    {
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
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            Uri uri = data.getData();
            File dir = getExternalFilesDir(null);
            if(dir != null)
            {
                path = dir.toString().substring(0,dir.toString().indexOf("0")+2) +
                        DocumentsContract.getDocumentId(uri).split(":")[1];
            }
        }
    }

    public boolean uploadFile(String path,String filename)
    {
        OkHttpClient okhttp = new OkHttpClient();
        File file = new File(path);
        if(path.isEmpty() || !file.exists())
            return false;
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",filename,RequestBody.create(new File(path), MediaType.parse("multipart/form-data")))
                .addFormDataPart("filename",filename)
                .build();
        FutureTask<Boolean> task = new FutureTask<>(()->
        {
            try
            {
                ResponseBody responseBody = okhttp.newCall(
                        new Request.Builder().post(body).url(NetConstant.getFileAddURL()).build()
                ).execute().body();

                if(responseBody != null)
                    return Boolean.parseBoolean(responseBody.string());
                return false;
            }
            catch (IOException e)
            {
                return false;
            }
        });
        try
        {
            new Thread(task).start();
            return task.get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
            return false;
        }
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
