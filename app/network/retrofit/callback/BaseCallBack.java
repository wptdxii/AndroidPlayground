package com.cloudhome.network.retrofit.callback;

import android.text.TextUtils;
import android.widget.Toast;

import com.cloudhome.application.MyApplication;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by wptdxii on 2017/2/15 0015.
 */

public abstract class BaseCallBack<T> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.raw().code() >= 200 && response.raw().code() < 300) {
            if (response.body() != null) {
                onResponse(response.body());
            } else {
                Toast.makeText(MyApplication.getInstance(), "当前没有数据", Toast.LENGTH_SHORT).show();
            }
        } else {
            Headers headers=response.raw().headers();
            String xError=headers.get("x-error");
            if(!TextUtils.isEmpty(xError)){
                Toast.makeText(MyApplication.getInstance(), xError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Toast.makeText(MyApplication.getInstance(), "获取数据失败", Toast.LENGTH_SHORT).show();
    }

    protected abstract void onResponse(T body);
}
