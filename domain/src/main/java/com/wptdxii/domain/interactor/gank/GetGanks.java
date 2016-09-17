package com.wptdxii.domain.interactor.gank;

import com.wptdxii.domain.executor.PostExecutionThread;
import com.wptdxii.domain.executor.ThreadExecutor;
import com.wptdxii.domain.interactor.UseCase;
import com.wptdxii.domain.model.gank.BaseGankResponse;
import com.wptdxii.domain.model.gank.GankModel;
import com.wptdxii.domain.repository.GankRepository;

import java.util.ArrayList;

import rx.Observable;

/**
 *This class is an implementation of UseCase that represents a use case
 * Created by wptdxii on 2016/9/12 0012.
 */
public class GetGanks extends UseCase<BaseGankResponse<ArrayList<GankModel>>> {

    private final GankRepository mGankRepository;
    private int count;
    private int page;

    public GetGanks(GankRepository gankRepository,
                    ThreadExecutor threadExecutor,
                    PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
        this.mGankRepository = gankRepository;

    }

    public void setParams(int count, int page) {
        this.count = count;
        this.page = page;
    }

    @Override
    protected Observable<BaseGankResponse<ArrayList<GankModel>>> buildUseCaseObservable() {
        return this.mGankRepository.getGankList(count, page);
    }
}
