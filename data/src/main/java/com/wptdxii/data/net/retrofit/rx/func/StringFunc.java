package com.wptdxii.data.net.retrofit.rx.func;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * Created by wptdxii on 2016/8/24 0024.
 * 将返回数据转换为string
 */
public class StringFunc implements Func1<ResponseBody, String> {
    @Override
    public String call(ResponseBody responseBody) {
        String result = null;

        try {
            result = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return result;
    }
}
