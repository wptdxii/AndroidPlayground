package com.wptdxii.androidpractice;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.wptdxii.androidpractice.internal.di.component.AppComponent;
import com.wptdxii.androidpractice.internal.di.component.DaggerAppComponent;
import com.wptdxii.androidpractice.internal.di.module.AppModule;
import com.wptdxii.ext.Ext;
import com.wptdxii.ext.component.info.Device;
import com.wptdxii.ext.component.info.Network;
import com.wptdxii.ext.util.AppStatusTracker;
import com.wptdxii.ext.util.ViewUtils;

/**
 * Created by wptdxii on 2016/7/28 0028.
 */
public class App extends Application {
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
            return "com.wptdxii.androidpractice";
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
}
