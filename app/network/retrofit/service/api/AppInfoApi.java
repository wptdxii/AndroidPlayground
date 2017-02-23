package com.cloudhome.network.retrofit.service.api;

import com.cloudhome.network.retrofit.entity.AppVersionEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wptdxii on 2017/2/15 0015.
 *
 * app信息相关接口
 */

public interface AppInfoApi {
    /**
     * app获取新版本接口
     * @param clientType 客户端类型  "android"
     * @return
     */
    @GET("misc/api/app-version/{type}")
    Call<AppVersionEntity> getAppVersion(@Path("type") String clientType);
}
