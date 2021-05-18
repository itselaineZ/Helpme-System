package com.example.begin;

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
import com.example.begin.DBOpenHelper;

public class FindpasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private DBOpenHelper mDBOpenHelper;
    private ImageView mIvFindpasswordactivityBack;
    private EditText mEtFindpasswordactivityUsername;
    private Button mBtFindpasswordactivitySend;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);

        initView();

        mDBOpenHelper = new DBOpenHelper(this);
    }

    private void initView(){
        mIvFindpasswordactivityBack = findViewById(R.id.iv_findpasswordactivity_back);
        mEtFindpasswordactivityUsername = findViewById(R.id.et_findpasswordactivity_username);
        mBtFindpasswordactivitySend = findViewById(R.id.bt_findpasswordactivity_sendpassword);

        mIvFindpasswordactivityBack.setOnClickListener(this);
        mBtFindpasswordactivitySend.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_findpasswordactivity_back:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.bt_findpasswordactivity_sendpassword:
                finish();
                break;
        }
    }

}
