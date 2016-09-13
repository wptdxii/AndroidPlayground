package com.wptdxii.ext.util;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * 管理app状态
 * Created by wptdxii on 2016/7/28 0028.
 */
public class AppStatusTracker implements Application.ActivityLifecycleCallbacks {
    public static final int STATUS_FORCE_KILLED = -1;//被强强杀
    public static final int STATUS_LOGOUT = 0;//注销
    public static final int STATUS_OFFLINE = 1;//未登录
    public static final int STATUS_ONLINE = 2;//登录
    public static final int STATUS_KICK_OUT = 3;//被顶出
    private static final long MAX_INTERVAL = 5 * 60 * 1000L;//应用处于后台超时时间
    private long timestamp;
    private  Application application;
    private static AppStatusTracker tracker;
    private int mAppStatus = STATUS_ONLINE;
    private DaemonReceiver daemonReceiver;
    private boolean isScreenOff;
    private int activeCount;
    private boolean isForeground;

    private AppStatusTracker(Application application) {
        this.application = application;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static void init(Application application) {
        tracker = new AppStatusTracker(application);
    }

    public static AppStatusTracker getInstance() {
        return tracker;
    }

    public int getAppStatus() {
        return this.mAppStatus;
    }

    /**
     * 在对应的位置设置状态
     * 在splash页面的onCreate()方法中设置为ACTION_SCREEN_OFF
     * @param mAppStatus
     */
    public void setAppStatus(int mAppStatus) {
        this.mAppStatus = mAppStatus;
        if (mAppStatus == STATUS_ONLINE) {
            if (daemonReceiver == null) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
                daemonReceiver = new DaemonReceiver();
                application.registerReceiver(daemonReceiver,intentFilter);
            }
        } else if (daemonReceiver != null) {
            application.unregisterReceiver(daemonReceiver);
            daemonReceiver = null;
        }
    }

   
    public boolean checkIfShowGesture() {
        if (mAppStatus == STATUS_ONLINE) {
            if (isScreenOff) {
                return true;
            }
            if (timestamp != 0L && System.currentTimeMillis() - timestamp > MAX_INTERVAL) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activeCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        isForeground = true;
        timestamp = 0L;
        isScreenOff = false;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeCount--;
        if (activeCount == 0) {
            isForeground = false;
            timestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
    
    private class DaemonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF == action) {
               isScreenOff = true;
            }
        }
    }


}
