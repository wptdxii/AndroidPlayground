package com.wptdxii.playground.internal.di.module;

import com.wptdxii.data.net.retrofit.RetrofitClient;
import com.wptdxii.data.net.retrofit.api.gankapi.GankApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@Module
public class GankApiModule {
    @Provides
    @Singleton
    GankApi provideGankApi() {
        return createApi(GankApi.class);
    }

    private <T> T createApi(Class<T> clazz) {
        return RetrofitClient.getInstance().createApi(clazz);
    }
}
