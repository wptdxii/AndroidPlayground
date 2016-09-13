package com.wptdxii.data.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wptdxii.data.exception.NetworkConnectionException;
import com.wptdxii.data.exception.ResponseException;
import com.wptdxii.domain.model.gank.BaseGankResponse;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by wptdxii on 2016/9/13 0013.
 */
public class RepositoryUtils {
    private static Gson sGson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static <T> Observable<T> extractData(Observable<BaseGankResponse<T>> observable, final Class<T> clazz) {
        return observable.flatMap(new Func1<BaseGankResponse<T>, Observable<T>>() {

            @Override
            public Observable<T> call(BaseGankResponse<T> baseGankResponse) {
                if (baseGankResponse == null) {
                    return Observable.error(new NetworkConnectionException());
                } else if (baseGankResponse.isError() == true) {
                    return Observable.error(new ResponseException());
                } else {
                    return Observable.just(baseGankResponse.getResults());
                }
            }
        });
    }
}
