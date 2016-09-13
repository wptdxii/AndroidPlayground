package com.wptdxii.data.net.okhttp.interceptor;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wptdxii on 2016/8/19 0019.
 * 缓存策略之一：有网和没网都先读缓存，统一缓存策略，降低服务器压力
 */
public class ForceCacheInterceptor  implements Interceptor {
    //缓存超时时间
    private static final int MAX_AGE = 60;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        //读取请求头中配置的Cache-Control
        String cacheControl = request.cacheControl().toString();
        if (TextUtils.isEmpty(cacheControl)) {
            cacheControl = "public, max-age=" + MAX_AGE;
        }

        Response response = chain.proceed(request);

        //将缓存设置到响应中
        return response.newBuilder()
                .header("Cache-Control", cacheControl)
                .removeHeader("Pragma") //移除干扰信息
                .build();
    }
}
