package com.wptdxii.androidpractice.network.retrofit.api;

import com.wptdxii.androidpractice.model.BaseModel;
import com.wptdxii.androidpractice.model.Benefit;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wptdxii on 2016/8/1 0001.
 * 当需要通过RetrofitClient.getInstance().createMultiApi()
 * 设置不同的baseUrl时，一定要定义"BASE_URL"字段
 */
public interface GankApi {
    
    public static  String BASE_URL = "http://gank.io/";
    
    @GET("api/data/福利/{number}/{page}")
    Call<BaseModel<ArrayList<Benefit>>> defaultBenefits(
            @Path("number") int number,
            @Path("page") int page
    );
    @GET("api/data/福利/{number}/{page}")
    Observable<BaseModel<ArrayList<Benefit>>> rxBenefits(
            @Path("number") int number,
            @Path("page") int page
    );
}