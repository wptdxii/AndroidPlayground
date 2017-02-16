package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class GetUnreadArticle {
    private NetResultListener receiveDataListener;
    private GetBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();

    private int action;
    private String errmsg;
    String url="";

    public GetUnreadArticle(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        url = IpConfig.getUri2("newsInfo");
        builder = OkHttpUtils.get().url(url);
    }

    public void execute(Object... params) {
        key_value.put("userId", params[0].toString());
        key_value.put("token", params[1].toString());
        key_value.put("status", params[2].toString());
        Log.i("article",url);
        Log.i("articleuserId",params[0].toString());
        Log.i("articleusertoken",params[1].toString());
        Log.i("articleuserstatus",params[2].toString());


        builder.params(key_value).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
            }

            @Override
            public void onResponse(String response, int id) {
                String jsonString = response;
                Log.d("onSuccessArticle", "onSuccess json = " + jsonString);
                try {
                    if (jsonString.equals("") || jsonString.equals("null")) {
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errcode = jsonObject.getString("errcode");
                        String errmsg = jsonObject.getString("errmsg");
                        if (errcode.equals("0")) {
                            JSONObject dataObj=jsonObject.getJSONObject("data");
                            String count=dataObj.getString("count");
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, count);
                        } else {
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, errmsg);
                        }
                    }
                } catch (Exception e) {
                    receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
                }
            }
        });
    }
}
