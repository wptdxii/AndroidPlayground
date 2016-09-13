package com.wptdxii.data.net.retrofit.transformer;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by wptdxii on 2016/8/24 0024.
 * 保证链条在io()线程
 */
public class IOTransformer<T,R> implements Observable.Transformer<T,R> {
    @Override
    public Observable<R> call(Observable<T> observable) {
        return (Observable<R>) observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }
}
