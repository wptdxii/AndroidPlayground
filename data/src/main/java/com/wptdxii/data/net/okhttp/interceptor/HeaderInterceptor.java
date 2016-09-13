package com.wptdxii.data.net.okhttp.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加统一的请求头
 * Created by wptdxii on 2016/8/19 0019.
 */
public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3.full+json")
                .addHeader("User-Agent", "C-RetrofitBean-Sample-App")
                .addHeader("name", "wptdxii") //缩放比 1/2/3
                .build();

        return chain.proceed(request);
    }
}
