package com.wptdxii.data.net.retrofit.rx.subscriber;


import com.wptdxii.domain.model.gank.BaseGankResponse;

import rx.Subscriber;

/**
 * Created by wptdxii on 2016/8/23 0023.
 */
public abstract class BaseGankResponseSubscriber<T> extends Subscriber<BaseGankResponse<T>> {
    @Override
    public void onCompleted() {
        
    }

    @Override
    public void onError(Throwable e) {
        //自己处理，或者在这里统一处理
       onFailure(e);
    }


    @Override
    public void onNext(BaseGankResponse<T> baseGankResponse) {
        if (!baseGankResponse.isError()) {
            onSuccess(baseGankResponse.getResults());
        } else {
            onError(new Throwable("error = " + baseGankResponse.isError()));
        }
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(Throwable e);
}
