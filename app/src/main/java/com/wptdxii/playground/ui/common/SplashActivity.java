package com.wptdxii.playground.ui.common;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.WindowManager;

import com.wptdxii.ext.util.AppStatusTracker;
import com.wptdxii.ext.util.DeviceUtils;
import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;
import com.wptdxii.uiframework.callback.PermissionListener;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    private static final int MSG_TO_HOME_ACTIVITY = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TO_HOME_ACTIVITY:
                    //解决全屏向非全屏跳转的toolbar闪动问题,在启动Activity前调用
                    cancelFullScreen();
                    MainActivity.startActivity(SplashActivity.this);
                    finish();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppStatusTracker.getInstance().setAppStatus(AppStatusTracker.STATUS_OFFLINE);
        super.onCreate(savedInstanceState);
    }

    @LayoutRes
    @Override
    protected int onCreateContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreateContent(Bundle savedInstanceState) {
        //在主题中设定，可以定制 背景
        // setFullScreen();

        String[] permisions = new String[]{Manifest.permission.READ_PHONE_STATE};
        requestPermissions(permisions, new PermissionListener() {
            @Override
            public void onGranted() {
                String deviceInfo = DeviceUtils.getDeviceInfo(SplashActivity.this);
                Log.e(TAG, "onCreate: " + deviceInfo);
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {

            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {

            }
        });
        handler.sendEmptyMessageDelayed(MSG_TO_HOME_ACTIVITY, 1000);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacksAndMessages(null);
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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
