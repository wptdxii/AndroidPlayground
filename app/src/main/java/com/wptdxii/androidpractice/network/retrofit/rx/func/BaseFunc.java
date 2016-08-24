package com.wptdxii.androidpractice.network.retrofit.rx.func;

import com.wptdxii.androidpractice.model.BaseModel;

import rx.functions.Func1;

/**
 * Created by wptdxii on 2016/8/24 0024.
 */
public class BaseFunc<T> implements Func1<BaseModel<T>, T> {
    @Override
    public T call(BaseModel<T> baseModel) {
        if (baseModel.error == false) {
           
        }
        return baseModel.results;
    }
}
