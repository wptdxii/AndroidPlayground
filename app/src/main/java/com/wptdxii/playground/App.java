package com.wptdxii.playground;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.wptdxii.ext.Ext;
import com.wptdxii.ext.component.info.Device;
import com.wptdxii.ext.component.info.Network;
import com.wptdxii.ext.util.AppStatusTracker;
import com.wptdxii.ext.util.ChannelUtils;
import com.wptdxii.ext.util.DeviceUtils;
import com.wptdxii.ext.util.ViewUtils;
import com.wptdxii.playground.internal.di.component.AppComponent;
import com.wptdxii.playground.internal.di.component.DaggerAppComponent;
import com.wptdxii.playground.internal.di.module.AppModule;

import java.lang.reflect.Method;

/**
 * Created by wptdxii on 2016/7/28 0028.
 */
public class App extends Application {
    private static final String TAG = "App";
    private static Application instance;
    private AppComponent mAppcomponent;

    public static Application getInstance() {
        return instance;
    }

    public AppComponent getAppcomponent() {
        return mAppcomponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppStatusTracker.init(this);
//        LeakCanary.install(this);

//        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this);
//        }

        initInjector();
        initExtension();
//        initUmengChannel();
        String deviceInfo = DeviceUtils.getDeviceInfo(this);
        Log.e(TAG, "onCreate: " + deviceInfo);

    }

    private void initUmengChannel() {
        String channel = ChannelUtils.getChannel(this);
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this, BuildConfig.UMENG_APPKEY, channel);
        MobclickAgent.startWithConfigure(config);
    }

    private void initInjector() {
        mAppcomponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
    private void initExtension() {
        Ext.init(this, new ExtImpl());
    }

    static final class ExtImpl extends Ext {
        @Override
        public String getCurOpenId() {
            //TODO get UserId from ApplicationComponent
            return null;
        }

        @Override
        public String getDeviceInfo() {
            return Device.getInfo();
        }

        @Override
        public String getPackageNameForResource() {
            return "com.wptdxii.playground";
        }

        @Override
        public int getScreenHeight() {
            return ViewUtils.getScreenHeight();
        }

        @Override
        public int getScreenWidth() {
            return ViewUtils.getScreenWidth();
        }

        @Override
        public boolean isAvailable() {
            return Network.isAvailable();
        }

        @Override
        public boolean isWap() {
            return Network.isWap();
        }

        @Override
        public boolean isMobile() {
            return Network.isMobile();
        }

        @Override
        public boolean is2G() {
            return Network.is2G();
        }

        @Override
        public boolean is3G() {
            return Network.is3G();
        }

        @Override
        public boolean isWifi() {
            return Network.isWifi();
        }

        @Override
        public boolean isEthernet() {
            return Network.isEthernet();
        }

        @Override
        public boolean fontInterceptorOnInterceptSetTextSize(View view, float textSize) {
            // TODO set font
            return false;
        }

        @Override
        public void showAlertDialog(Context context, String title, String message, String positive, DialogInterface.OnClickListener positiveListener, String negative, DialogInterface.OnClickListener negativeListener) {
            // TODO show alert dialog
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }
}
