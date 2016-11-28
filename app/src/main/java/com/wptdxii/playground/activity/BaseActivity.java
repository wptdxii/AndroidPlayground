package com.wptdxii.playground.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by xionghu on 2016/7/27.
 * Email：965705418@qq.com
 */
public class BaseActivity extends FragmentActivity{


    protected SharedPreferences sp;
    protected SharedPreferences sp2;
    protected SharedPreferences sp3;
    protected SharedPreferences sp4;
    protected SharedPreferences sp5;
    public static String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("userInfo", 0);
        sp2 =getSharedPreferences("otherinfo", 0);
        sp3 =getSharedPreferences("expertmicro", 0);
        sp4 =getSharedPreferences("ActivityInfo", 0);
        sp5 =getSharedPreferences("temp", 0);
        TAG=getClass().getSimpleName();
    }


    @Override
    protected  void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected  void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }
}
