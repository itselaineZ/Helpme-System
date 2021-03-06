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

import static com.example.begin.activity.FileActivity.courseName;

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
    private long id;
    private final String TAG = "FiledetailActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_filedetail);

        Bundle bundle = this.getIntent().getExtras();
        fileName = bundle.getString("fileName");
        id = bundle.getLong("id");

        initView();
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

        mBtFiledetailActivityEvaluate.setOnClickListener(this);
        mIvFiledetailActivityBack.setOnClickListener(this);

        mBtFiledetailActivityDownload.setOnClickListener(v->
        {
            String [] permissions = new String[]{
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            };//????????????
            if(ActivityCompat.checkSelfPermission(this,permissions[0]) != PackageManager.PERMISSION_GRANTED)
                //??????????????????
                ActivityCompat.requestPermissions(this,permissions,1);//????????????

            File file = downloadFile(fileName);
            if(file == null)
                showMessage("???????????????");
            else
                showMessage("??????????????????");
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_filedetailactivity_evaluate:
                int count = mRgFiledetailActivity.getChildCount();
                int score = 0;
                for(int i = 0 ;i < count;i++){
                    RadioButton rb = (RadioButton)mRgFiledetailActivity.getChildAt(i);
                    if(rb.isChecked()){
                        //Toast.makeText(FiledetailActivity.this, "??????"+rb.getTag().toString(), Toast.LENGTH_SHORT).show();
                        score = i+1;
                        break;
                    }
                }
                mBtFiledetailActivityEvaluate.setEnabled(false);
                asyncEvaluate(String.valueOf(score));
            case R.id.iv_filedetailactivity_back:
                Intent intent = new Intent(this, FileActivity.class);
                intent.putExtra("courseName", courseName);
                startActivity(intent);

                finish();
                break;
        }
    }

    private void asyncEvaluate(final String score) {
        /*
         ????????????????????????????????????????????????????????????
         ???????????????????????????final????????????????????????????????????
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                sp = getSharedPreferences("login_info", MODE_PRIVATE);
                final String token = sp.getString("token", "ERROR");

                // okhttp??????POST????????? ??????5???
                // 1????????????okhttpClient??????
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2??????????????????requestBody
                RequestBody requestBody = new FormBody.Builder()
                        .add("id", String.valueOf(id))
                        .add("score", score)
                        .build();
                // 3????????????????????????????????????????????????POST??????
                Request request = new Request.Builder()
                        .url(NetConstant.getScoreURL())
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();
                // 4?????????okhttpClient????????????????????????????????????enqueue()????????????????????????
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5???????????????????????????
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "??????URL????????? " + e.getMessage());
                        showToastInThread(FiledetailActivity.this, "??????URL??????, ????????????");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // ????????????????????????????????????
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                             /*
                            ?????????????????????????????????
                            response.body().string()??????????????????????????????????????????
                             */
                            /* ??????Gson??????response???JSON?????????????????? */
                            String responseBodyStr = response.body().string();
                            /* ??????Gson??????response???JSON?????????????????? */
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            // ???????????????status???success??????getStatus??????true?????????????????????
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                Log.d(TAG, "????????????");
                                showToastInThread(FiledetailActivity.this, "????????????????????????");
                                return ;
                            } else {
                                Log.d(TAG, "??????????????????success");
                                showToastInThread(FiledetailActivity.this, "?????????????????????????????????");
                            }
                        } else {
                            Log.d(TAG, "???????????????");
                            showToastInThread(FiledetailActivity.this, "???????????????????????????????????????");
                        }
                    }
                });

            }
        }).start();
    }

    private String getStatus(JsonObject responseBodyJSONObject) {
        /* ??????Gson??????response???JSON??????????????????
           ??????JSON?????????????????????????????? */
        String status = responseBodyJSONObject.get("status").getAsString();
        // ?????????????????????json???{ "status":"success", "data":null }
        // ?????????status?????????data???null
        return status;
    }

    public File downloadFile(String filename)
    {
        OkHttpClient okhttp = new OkHttpClient();
        if(filename == null || filename.isEmpty())
            return null;
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("id", String.valueOf(id))
                .build();

        sp = getSharedPreferences("login_info" ,MODE_PRIVATE);
        String token = sp.getString("token", "ERROR");

        FutureTask<File> task = new FutureTask<>(()->
        {
            String URL = NetConstant.getDownloadURL();
            ResponseBody responseBody = okhttp.newCall(
                    new Request.Builder()
                            .addHeader("Authorization", token)
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
