package com.wptdxii.ext;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;

import com.wptdxii.ext.util.AppUtils;

/**
 * Created by wptdxii on 2016/9/13 0013.
 */
public abstract class Ext {
    private static Application sApplication;
    private static Context sContext;

    private static String sPackageName;
    private static String sBuilderNumber;
    private static String sVersionName;
    private static int sVersionCode;

    private static Ext sInstance = null;

    public static Ext getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("Ext not initialized");
        }

        return sInstance;
    }

    public static void init(Application application, Ext instance) {
        sApplication = application;
        sContext = application.getApplicationContext();
        sPackageName = application.getPackageName();
        sInstance = instance;
        initPackageInfo(application);
        initDebugConfig(application);
    }

    private static void initDebugConfig(Context context) {
        ApplicationInfo appInfo = AppUtils.getAppInfoWithFlags(context, PackageManager.GET_META_DATA);
        if (appInfo != null) {
            DebugConfig.isDebug = ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        }
    }

    public static final class DebugConfig {
        public static boolean isDebug = false;
    }

    private static void initPackageInfo(Application application) {
        String version = "";

        try {
            PackageInfo info = application.getPackageManager().getPackageInfo(sPackageName, 0);
            sVersionCode = info.versionCode;
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sVersionName = version.substring(0, version.lastIndexOf("."));
        sBuilderNumber = version.substring(version.lastIndexOf(".") + 1, version.length());

    }

    public static Context getContext() {
        if (sContext == null) {
            throw new RuntimeException("Ext not initialized");
        }
        return sContext;
    }

    public static Application getApplication() {
        if (sApplication == null) {
            throw new RuntimeException("Ext not initialized");
        }
        return sApplication;
    }

    public  String getPackageName() {
        return sPackageName;
    }

    public  String getBuilderNumber() {
        return sBuilderNumber;
    }

    public  String getVersionName() {
        return sVersionName;
    }

    public  int getVersionCode() {
        return sVersionCode;
    }

    public abstract String getCurOpenId();

    public abstract String getDeviceInfo();

    public abstract String getPackageNameForResource();

    public abstract int getScreenHeight();

    public abstract int getScreenWidth();

    // Network
    public abstract boolean isAvailable();

    public abstract boolean isWap();

    public abstract boolean isMobile();

    public abstract boolean is2G();

    public abstract boolean is3G();

    public abstract boolean isWifi();

    public abstract boolean isEthernet();

    public boolean isDebuggable() {
        return DebugConfig.isDebug;
    }

    /**
     * 拦截系统字体大小设置，并应用给定的字体大小
     */
    public abstract boolean fontInterceptorOnInterceptSetTextSize(View view, float textSize);

    public abstract void showAlertDialog(Context context, String title, String message,
                                         String positive,
                                         DialogInterface.OnClickListener positiveListener,
                                         String negative,
                                         DialogInterface.OnClickListener negativeListener);
}
