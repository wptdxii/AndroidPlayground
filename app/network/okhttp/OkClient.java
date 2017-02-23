package com.cloudhome.network.okhttp;

import android.util.Log;

import com.cloudhome.BuildConfig;
import com.cloudhome.network.okhttp.interceptor.HeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by wptdxii on 2017/2/15
 * <p>
 * init OkHttp for Retrofit
 */

public class OkClient {
    private static final long TIMEOUT_READ = 30000L;
    private static final long TIMEOUT_CONNECTION = 30000L;
    private final OkHttpClient mOkHttpClient;
    private static final String TAG = "Retrofit";

    private OkClient() {
        mOkHttpClient = new OkHttpClient.Builder()
//                .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
//                .addInterceptor(new MyInterceptor())
//                .addInterceptor(new TokenInterceptor(MyApplication.getInstance()))
                .addInterceptor(initLoggerInterceptor())
                .addInterceptor(new HeaderInterceptor())
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.MILLISECONDS)
                .build();
    }

    private HttpLoggingInterceptor initLoggerInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.e(TAG, message);
                    }
                });
        loggingInterceptor.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY: HttpLoggingInterceptor.Level.NONE);

        return loggingInterceptor;
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
