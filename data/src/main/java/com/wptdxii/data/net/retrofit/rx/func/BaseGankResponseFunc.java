package com.wptdxii.data.net.retrofit.rx.func;

import com.wptdxii.domain.model.gank.BaseGankResponse;
import com.wptdxii.ext.util.json.JsonConvert;

import rx.functions.Func1;

/**
 * Created by wptdxii on 2016/8/24 0024.
 * 将String转换为BaseModel<ToastUtils>
 *  RxJava 中的 flatMap()
 */
public class BaseGankResponseFunc<T> implements Func1<String, BaseGankResponse<T>> {
    @Override
    public BaseGankResponse<T> call(String result) {
        JsonConvert<BaseGankResponse<T>> convert = new JsonConvert<>();
        return convert.parse(result);
    }
}
