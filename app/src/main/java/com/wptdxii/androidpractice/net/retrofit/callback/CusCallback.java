package com.wptdxii.androidpractice.net.retrofit.callback;

import com.wptdxii.androidpractice.model.BaseModel;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wptdxii on 2016/8/24 0024.
 */
public abstract class CusCallback<T extends BaseModel> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.raw().code() == 200) {//与服务器约定
            if (response.body().error == true) {//在BaseModel中定义，根据实际情况可约定为boolean或者数字
                onSuccess(response.body());
            } else if (response.body().error == false) {
                onAutoLogin();
            }
        } else {
            
        } 
    }
    
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            //TODO 定义抽象方法
        } else if (t instanceof ConnectException) {
            //TODO 定义抽象方法
        } else if (t instanceof RuntimeException) {
            //TODO 定义抽象方法
        }
        
        //或者进行统一处理
        
    }
    
    protected abstract void onSuccess(T body);
    protected abstract void onAutoLogin();
} 