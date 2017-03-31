package com.wptdxii.playground.internal.di.module;

import com.wptdxii.playground.internal.di.scope.PerActivity;
import com.wptdxii.domain.executor.PostExecutionThread;
import com.wptdxii.domain.executor.ThreadExecutor;
import com.wptdxii.domain.interactor.gank.GetGanks;
import com.wptdxii.domain.repository.GankRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@Module
public class GanksModule {
    @Provides
    @PerActivity
    GetGanks provideGetGanksUseCase(GankRepository repository,
                                    ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread) {
        return new GetGanks(repository, threadExecutor, postExecutionThread);
    }

}
