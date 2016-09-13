package com.wptdxii.data.net.retrofit.transformer;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 默认transformer ,在io()线程和主线程间切换
 * Created by wptdxii on 2016/8/24 0024.
 */
public class DefaultTransformer<T,R> implements Observable.Transformer<T, R> {

    @Override
    public Observable<R> call(Observable<T> observable) {
        return (Observable<R>) observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
