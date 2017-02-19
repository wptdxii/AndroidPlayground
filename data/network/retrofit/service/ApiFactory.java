package com.cloudhome.network.retrofit.service;

import com.cloudhome.network.retrofit.RetrofitClient;
import com.cloudhome.network.retrofit.service.api.AppInfoApi;

/**
 * Created by wptdxii on 2017/2/15 0015.
 */

public class ApiFactory {
    private static AppInfoApi sAppInfoApi;

    public static AppInfoApi getAppInfoApi() {
        if (sAppInfoApi == null) {
            sAppInfoApi = RetrofitClient.getInstance().createApi(AppInfoApi.class);
        }
        return sAppInfoApi;
    }
}
