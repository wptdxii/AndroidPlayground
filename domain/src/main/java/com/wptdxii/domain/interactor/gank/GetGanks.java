package com.wptdxii.domain.interactor.gank;

import com.wptdxii.domain.executor.PostExecutionThread;
import com.wptdxii.domain.executor.ThreadExecutor;
import com.wptdxii.domain.interactor.UseCase;
import com.wptdxii.domain.model.BaseResponse;
import com.wptdxii.domain.model.gank.GankEntity;
import com.wptdxii.domain.repository.GankRepository;

import java.util.List;

import rx.Observable;

/**
 * Created by wptdxii on 2016/9/12 0012.
 */
public class GetGanks extends UseCase<BaseResponse<List<GankEntity>>> {

    private final GankRepository mGankRepository;
    private int count;
    private int page;

    protected GetGanks(GankRepository gankRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.mGankRepository = gankRepository;
    }

    public void setParams(int count, int page) {
        this.count = count;
        this.page = page;
    }

    @Override
    protected Observable<BaseResponse<List<GankEntity>>> buildUseCaseObservable() {
        return this.mGankRepository.getGankList(page, count);
    }
}
