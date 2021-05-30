package com.example.begin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class FiledetailActivity extends BaseActivity implements View.OnClickListener{

    private TextView mTvFiledetailActivityCourseName;
    private TextView mTvFiledetailActivityFileName;
    private Button mBtFiledetailActivityDownload;
    private RadioButton mRbFiledetailActivityOne;
    private RadioButton mRbFiledetailActivityTwo;
    private RadioButton mRbFiledetailActivityThree;
    private RadioButton mRbFiledetailActivityFour;
    private RadioButton mRbFiledetailActivityFive;
    private String fileName;
    private String courseName;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_filedetail);

        Bundle bundle = this.getIntent().getExtras();
        fileName = bundle.getString("fileName");
        courseName = bundle.getString("courseName");

        initView();
    }

    private void initView(){
        mTvFiledetailActivityCourseName = findViewById(R.id.tv_filedetailactivity_coursename);
        mTvFiledetailActivityFileName = findViewById(R.id.tv_filedetailactivity_filename);
        mBtFiledetailActivityDownload = findViewById(R.id.bt_filedetailactivity_download);
        mRbFiledetailActivityOne = findViewById(R.id.rb_filedetailactivity_one);
        mRbFiledetailActivityTwo = findViewById(R.id.rb_filedetailactivity_two);
        mRbFiledetailActivityThree = findViewById(R.id.rb_filedetailactivity_three);
        mRbFiledetailActivityFour = findViewById(R.id.rb_filedetailactivity_four);
        mRbFiledetailActivityFive = findViewById(R.id.rb_filedetailactivity_five);

        mTvFiledetailActivityCourseName.setText(courseName);
        mTvFiledetailActivityFileName.setText(fileName);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.rb_filedetailactivity_one:
                //1分，发送给后端
                Toast.makeText(this, "感谢您的评分！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb_filedetailactivity_two:
                Toast.makeText(this, "感谢您的评分！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb_filedetailactivity_three:
                Toast.makeText(this, "感谢您的评分！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb_filedetailactivity_four:
                Toast.makeText(this, "感谢您的评分！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb_filedetailactivity_five:
                Toast.makeText(this, "感谢您的评分！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_filedetailactivity_download:
                //下载

                //startActivity(new Intent(this, ));
                finish();
                break;
        }
    }
}
