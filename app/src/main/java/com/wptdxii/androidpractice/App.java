package com.wptdxii.androidpractice;

import android.app.Application;

import com.wptdxii.androidpractice.util .AppStatusTracker;

/**
 * Created by wptdxii on 2016/7/28 0028.
 */
public class App extends Application {
    private static Application instance;
    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppStatusTracker.init(this);
    }
}
