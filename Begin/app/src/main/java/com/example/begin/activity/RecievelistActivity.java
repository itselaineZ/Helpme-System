package com.example.begin.activity;

import android.os.Bundle;
import android.view.View;

public class RecievelistActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_recievelist);
        initView();
    }

    private void initView(){

    }

    public void onClick(View view){
        switch(view.getId()){

        }
    }
}
