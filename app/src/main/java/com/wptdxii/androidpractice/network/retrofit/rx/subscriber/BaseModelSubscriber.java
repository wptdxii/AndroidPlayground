package com.wptdxii.androidpractice.network.retrofit.rx.subscriber;

import com.wptdxii.androidpractice.model.BaseModel;

import rx.Subscriber;

/**
 * Created by wptdxii on 2016/8/23 0023.
 */
public abstract class BaseModelSubscriber<T> extends Subscriber<BaseModel<T>> {
    @Override
    public void onCompleted() {
        
    }

    @Override
    public void onError(Throwable e) {
        //自己处理，或者在这里统一处理
       onFailure(e);
    }


    @Override
    public void onNext(BaseModel<T> baseModel) {
        if (!baseModel.error) {
            onSuccess(baseModel.results);
        } else {
            onError(new Throwable("error = " + baseModel.error));
        }
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(Throwable e);
}
