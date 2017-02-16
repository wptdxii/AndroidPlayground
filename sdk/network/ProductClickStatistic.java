package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ProductClickStatistic {


    private NetResultListener receiveDataListener;
    private GetBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();

    private int action;

    public ProductClickStatistic(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;

    }

    public void execute(Object... params) {
        String productId = params[0].toString();
        String url = IpConfig.getUri2("proclick") + "productId=" + productId;
        builder = OkHttpUtils.get().url(url);
        //		key_value.put("productId", params[0].toString());
        Log.d("统计了------------", url);
        builder.build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                String jsonString = response;
                Log.d("proclick------", "onSuccess json = " + jsonString);
            }
        });
    }
}
