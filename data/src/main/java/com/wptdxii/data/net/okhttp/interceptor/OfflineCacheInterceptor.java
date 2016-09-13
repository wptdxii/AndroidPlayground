package com.wptdxii.data.net.okhttp.interceptor;

import com.wptdxii.ext.Ext;
import com.wptdxii.ext.util.NetUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wptdxii on 2016/8/19 0019.
 * 缓存策略之一：离线读取本地缓存，在线获取最新数据(读取单个请求的请求头，亦可统一设置)
 */
public class OfflineCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

//        CacheControl cacheControl = new CacheControl.Builder()
//                .maxAge(0, TimeUnit.SECONDS)//控制缓存的最大生命时间
//                .maxStale(365, TimeUnit.DAYS)//控制缓存的过时时间
//                .build();

        Request request = chain.request();

        if (!NetUtils.isConnected(Ext.getContext())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }

        Response response = chain.proceed(request);

        if (NetUtils.isConnected(Ext.getContext())) {
            int maxAge = 0; // read from cache
            response = response.newBuilder()
                    .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .header("Cache-Control", "public ,max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        
        return response;
    }
}
