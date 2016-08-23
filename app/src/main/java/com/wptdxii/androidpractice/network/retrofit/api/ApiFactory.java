package com.wptdxii.androidpractice.network.retrofit.api;

import com.wptdxii.androidpractice.network.retrofit.RetrofitClient;

/**
 * Created by wptdxii on 2016/8/19 0019.
 * 封装类接口，接口实例直接通过此处获得
 */
public class ApiFactory {
    private static GankApi mGankApi;
    private static MovieApi mMovieApi;
    
    //GankApi
    public static GankApi getGankApi() {
        if (mGankApi == null) {
            //使用RetrofitClient中统一的baseUrl
            mGankApi = RetrofitClient.getInstance().createApi(GankApi.class);
            //使用MovieApi中的baseUrl
//            mGankApi = RetrofitClient.getInstance().createMultiApi(GankApi.class);
        }
        
        
        return mGankApi;
    }
    
    public static MovieApi getMovie() {
        if (mMovieApi == null) {
            //使用MovieApi中的baseUrl
            mMovieApi = RetrofitClient.getInstance().createMultiApi(MovieApi.class);
        }
        return  mMovieApi;
    }
}
