package com.wptdxii.data.net.retrofit.api.commonapi;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by wptdxii on 2016/8/24 0024.
 * 需要完整的完整的Url时这样定义
 */
public interface CommonApi {
    //当需要使用RetrofitClient.getInstance().createMultiApi()需要指定baseurl,随意指定
    public static final String BASE_URL = "http://www.example.com";
    @GET
    Observable<ResponseBody> loadString(@Url String url);
    
    @GET
    @Streaming
    Observable<ResponseBody> download(@Url String url);
}
