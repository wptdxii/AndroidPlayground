package com.wptdxii.data.net.retrofit.api.gankapi;

import com.wptdxii.domain.model.gank.BaseGankResponse;
import com.wptdxii.domain.model.gank.GankModel;

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
    //当使用动态指定baseURl时必须有该字段
    public static  String BASE_URL = "http://gank.io/";
    
    @GET("api/data/福利/{count}/{page}")
    Call<BaseGankResponse<ArrayList<GankModel>>> getGankList(
            @Path("count") int count,
            @Path("page") int page
    );
    
    @GET("api/data/福利/{count}/{page}")
    Observable<BaseGankResponse<ArrayList<GankModel>>> getGankListWithRx(
            @Path("count") int count,
            @Path("page") int page
    );
}