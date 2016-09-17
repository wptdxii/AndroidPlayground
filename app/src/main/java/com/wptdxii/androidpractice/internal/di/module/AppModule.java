package com.wptdxii.androidpractice.internal.di.module;

import android.app.Application;
import android.content.Context;

import com.wptdxii.androidpractice.executor.UIThread;
import com.wptdxii.data.executor.JobExecutor;
import com.wptdxii.domain.executor.PostExecutionThread;
import com.wptdxii.domain.executor.ThreadExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@Module
public class AppModule {

    private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }
}
