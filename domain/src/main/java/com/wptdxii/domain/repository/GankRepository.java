package com.wptdxii.domain.repository;

import com.wptdxii.domain.model.BaseResponse;
import com.wptdxii.domain.model.gank.GankEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by wptdxii on 2016/9/12 0012.
 */
public interface GankRepository {
   Observable<BaseResponse<List<GankEntity>>> getGankList(int page, int count);
}
