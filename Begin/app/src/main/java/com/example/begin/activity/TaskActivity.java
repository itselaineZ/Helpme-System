package com.example.begin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class TaskActivity extends BaseActivity implements View.OnClickListener{

    private ImageButton mIbTaskActivityCourselistBt;
    private ImageButton mIbTaskActivityUserBt;
    private RelativeLayout mRlTaskActivityRelease;
    private RelativeLayout mRlTaskActivityRecieve;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_task);

        initView();
    }

    private void initView(){
        mIbTaskActivityCourselistBt = findViewById(R.id.ib_taskactivity_courselistbt);
        mIbTaskActivityUserBt = findViewById(R.id.ib_taskactivity_userbt);
        mRlTaskActivityRelease = findViewById(R.id.rl_taskactivity_release);
        mRlTaskActivityRecieve = findViewById(R.id.rl_taskactivity_recieve);

        mIbTaskActivityUserBt.setOnClickListener(this);
        mIbTaskActivityCourselistBt.setOnClickListener(this);
        mRlTaskActivityRelease.setOnClickListener(this);
        mRlTaskActivityRecieve.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_taskactivity_courselistbt:
                startActivity(new Intent(this, CourselistActivity.class));
                finish();
                break;
            case R.id.ib_taskactivity_userbt:
                startActivity(new Intent(this, UserhomeActivity.class));
                finish();
                break;
            case R.id.rl_taskactivity_release:
                startActivity(new Intent(this, ReleaseActivity.class));
                finish();
                break;
            case R.id.rl_taskactivity_recieve:
                startActivity(new Intent(this, RecievelistActivity.class));
                finish();
                break;
        }
    }
}
