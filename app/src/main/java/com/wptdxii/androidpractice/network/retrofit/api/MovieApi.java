package com.wptdxii.androidpractice.network.retrofit.api;

import com.wptdxii.androidpractice.model.Movie;
import com.wptdxii.androidpractice.model.MovieBaseModel;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wptdxii on 2016/8/23 0023.
 * 当需要通过RetrofitClient.getInstance().createMultiApi()
 * 设置不同的baseUrl时，一定要定义"BASE_URL"字段
 */
public interface MovieApi {
    public static  String BASE_URL = "https://api.douban.com/v2/movie/";

    @GET("top250")
    Observable<MovieBaseModel<List<Movie>>> getMove(
            @Query("start") int start,
            @Query("count") int count
    );
}
