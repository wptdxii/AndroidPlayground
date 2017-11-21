package com.wptdxii.playground.module.common;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.WindowManager;

import com.wptdxii.ext.util.AppStatusTracker;
import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    private static final int RESIDENCE_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppStatusTracker.getInstance().setAppStatus(AppStatusTracker.STATUS_OFFLINE);
        super.onCreate(savedInstanceState);
    }

    @LayoutRes
    @Override
    protected int onCreateContentView() {
        //在主题中设定，可以定制 背景
        // setFullScreen();
        return R.layout.activity_splash;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        getWindow().getDecorView().postDelayed(() -> {
            //解决全屏向非全屏跳转的toolbar闪动问题,在启动Activity前调用
            cancelFullScreen();
            MainActivity.startActivity(this);
            finish();
        }, RESIDENCE_TIME);
    }

    /**
     * 设置为全屏显示
     */
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏显示
     */
    private void cancelFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    }
}
