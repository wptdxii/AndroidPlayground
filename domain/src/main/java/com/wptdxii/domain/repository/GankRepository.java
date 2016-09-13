package com.wptdxii.domain.repository;

import com.wptdxii.domain.model.gank.BaseGankResponse;
import com.wptdxii.domain.model.gank.GankModel;

import java.util.ArrayList;

import rx.Observable;

/**
 * Interface that represents a Repository for getting  related data.
 * a method related a UserCase
 * Created by wptdxii on 2016/9/12 0012.
 */
public interface GankRepository {
   Observable<BaseGankResponse<ArrayList<GankModel>>> getGankList(int page, int count);
}
