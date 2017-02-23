package com.cloudhome.network.retrofit;

import com.cloudhome.BuildConfig;
import com.cloudhome.network.okhttp.OkClient;
import com.cloudhome.network.retrofit.converter.FastJsonConverterFactory;

import retrofit2.Retrofit;

/**
 * Created by wptdxii on 2017/2/15 0015.
 *
 */

public class RetrofitClient {
    private static final String BASE_URL_TEST = BuildConfig.BASE_URL_TEST;
    private static final String BASE_URL_PRODUCTION = BuildConfig.BASE_URL_PRODUCTION;
    private static RetrofitClient sInstance;
    private Retrofit mRetrofit;

    private RetrofitClient() {
        mRetrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .baseUrl(getActiveHttpScheme())
                .client(OkClient.getInstance().getClient())
                .build();
    }

    private static class RetrofitClientHolder {
        private static RetrofitClient instance = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return RetrofitClientHolder.instance;
    }

    private String getActiveHttpScheme() {
        return BuildConfig.DEBUG ? BASE_URL_TEST : BASE_URL_PRODUCTION;
    }

    public <T> T createApi(Class<T> clazz) {
        return mRetrofit.create(clazz);
    }
}
