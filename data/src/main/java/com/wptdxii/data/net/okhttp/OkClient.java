package com.wptdxii.data.net.okhttp;

import com.wptdxii.data.net.okhttp.interceptor.OfflineCacheInterceptor;
import com.wptdxii.ext.BuildConfig;
import com.wptdxii.ext.Ext;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by wptdxii on 2016/8/16 0016.
 * 单例模式
 */
public class OkClient {
    private static final long TIMEOUT_READ = 15L;
    private static final long TIMEOUT_CONNECTION = 15L;
    private OkClient mInstance;
    private  final OkHttpClient mOkHttpClient;
    
    private OkClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG?
                HttpLoggingInterceptor.Level.BODY: HttpLoggingInterceptor.Level.NONE);

        //设置缓存目录
        File httpCacheDirectory = new File(Ext.getApplication().getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);



        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                // .addInterceptor(new HeaderInterceptor())
                .addNetworkInterceptor(new OfflineCacheInterceptor())
                .addInterceptor(new OfflineCacheInterceptor())//设置缓存策略
//                .addInterceptor(new ForceCacheInterceptor())
                .retryOnConnectionFailure(true)//失败重连
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .cache(cache)//设置缓存目录
                .build();
    }
    
    private static class OkClientHolder {
        private static OkClient instance = new OkClient();
    }
    
    public static OkClient getInstance() {
        return OkClientHolder.instance;
    }
    
    public OkHttpClient getClient() {
        return mOkHttpClient;
    }
}
