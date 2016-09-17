package com.wptdxii.androidpractice.internal.di.component;

import android.content.Context;

import com.wptdxii.androidpractice.internal.di.module.AppModule;
import com.wptdxii.androidpractice.internal.di.module.GankApiModule;
import com.wptdxii.androidpractice.internal.di.module.RepositoryModule;
import com.wptdxii.androidpractice.ui.activity.ContentActivity;
import com.wptdxii.data.net.retrofit.api.gankapi.GankApi;
import com.wptdxii.domain.executor.PostExecutionThread;
import com.wptdxii.domain.executor.ThreadExecutor;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@Singleton
@Component(modules = {AppModule.class, GankApiModule.class, RepositoryModule.class})
public interface AppComponent {
    void inject(ContentActivity contentActivity);

    Context applicationContext();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    GankApi gankApi();
}
