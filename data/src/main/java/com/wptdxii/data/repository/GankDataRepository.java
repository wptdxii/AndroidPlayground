package com.wptdxii.data.repository;

import com.wptdxii.data.net.retrofit.api.ApiFactory;
import com.wptdxii.domain.model.gank.BaseGankResponse;
import com.wptdxii.domain.model.gank.GankModel;
import com.wptdxii.domain.repository.GankRepository;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by wptdxii on 2016/9/13 0013.
 */
public class GankDataRepository implements GankRepository{
    @Override
    public Observable<BaseGankResponse<ArrayList<GankModel>>> getGankList(int count, int page) {
        return ApiFactory.getGankApi().getGankListWithRx(count, page);
    }
}
