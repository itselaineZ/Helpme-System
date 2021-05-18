package com.example.begin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.begin.R;
import com.example.begin.activity.DBOpenHelper;

import java.time.Instant;

public class CueActivity extends AppCompatActivity implements View.OnClickListener {

    private DBOpenHelper mDBOpenHelper;
    private EditText mEtCueavtivityUsername;
    private EditText mEtCueactivityPassword;
    private EditText mEtCueactivityReason;
    private Button mBtCueactivitySubmit;
    private ImageView mIvCueactivityBack;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cue);

        initView();

        mDBOpenHelper = new DBOpenHelper(this);
    }

    private void initView(){
        mEtCueavtivityUsername = findViewById(R.id.et_cueactivity_username);
        mEtCueactivityPassword = findViewById(R.id.et_cueactivity_password);
        mBtCueactivitySubmit = findViewById(R.id.bt_cueactivity_submit);
        mIvCueactivityBack = findViewById(R.id.iv_cueactivity_back);

        mBtCueactivitySubmit.setOnClickListener(this);
        mIvCueactivityBack.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_cueactivity_submit:
            case R.id.iv_cueactivity_back:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
