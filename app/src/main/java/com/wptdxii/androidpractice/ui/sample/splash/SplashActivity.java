package com.wptdxii.androidpractice.ui.sample.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.ui.sample.home.ContentActivity;
import com.wptdxii.uiframework.base.BaseActivity;
import com.wptdxii.ext.util.AppStatusTracker;

public class SplashActivity extends BaseActivity {
    private static final int MSG_TO_HOME_ACTIVITY = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TO_HOME_ACTIVITY:
                    //解决全屏向非全屏跳转的toolbar闪动问题,在启动Activity前调用
                    cancelFullScreen();
                    
                    ContentActivity.actionStart(SplashActivity.this);
                    finish();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppStatusTracker.getInstance().setAppStatus(AppStatusTracker.STATUS_OFFLINE);
        super.onCreate(savedInstanceState);
        //在主题中设定，可以定制 背景
       // setFullScreen();
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_splash,-1,-1,MODE_NONE);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        handler.sendEmptyMessageDelayed(MSG_TO_HOME_ACTIVITY, 1000);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacksAndMessages(null);
        super.onPause();

    }

    /**
     * 设置为全屏显示
     */
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏显示
     */
    private void cancelFullScreen() {
        SplashActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    }
}
