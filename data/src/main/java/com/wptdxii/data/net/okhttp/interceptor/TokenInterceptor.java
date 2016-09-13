package com.wptdxii.data.net.okhttp.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 让所有请求都附上Token
 * Created by wptdxii on 2016/8/4 0004.
 */
public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
//        Request originalRequest = chain.request();
//        if (Yours.sToken == null || alreadyHasAuthorizationHeader(originalRequest)) {
//            return chain.proceed(originalRequest);
//        }
//        
//        Request authorised = originalRequest.newBuilder()
//                .header("Authorization",Yours.sToken)
//                .build();

//        return chain.proceed(authorised);
        
        return null;
    }
}
