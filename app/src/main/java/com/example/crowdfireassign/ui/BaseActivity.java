package com.example.crowdfireassign.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.crowdfireassign.R;
import com.example.crowdfireassign.data.ImageHelper;
import com.example.crowdfireassign.utils.Utils;


public class BaseActivity extends AppCompatActivity {


    Activity mActivity;
    Utils mUtils;
    ImageHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mUtils = new Utils();
        helper = new ImageHelper(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        //some issue with library 26
        if(Build.VERSION.SDK_INT == 26){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.setContentView(layoutResID);
    }
}
