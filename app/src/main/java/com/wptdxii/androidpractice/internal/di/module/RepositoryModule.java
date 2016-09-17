package com.wptdxii.androidpractice.internal.di.module;

import com.wptdxii.data.repository.GankDataRepository;
import com.wptdxii.domain.repository.GankRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@Module
public class RepositoryModule {
    @Provides
    @Singleton
    GankRepository provideGankRepository(GankDataRepository gankDataRepository) {
        return gankDataRepository;
    }
}
