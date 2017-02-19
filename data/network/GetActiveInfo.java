package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class GetActiveInfo {
    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private Map<String, String> dataValue;
    private String user_id = "";
    private String token = "";
    private int action;

    private String errmsg;

    public GetActiveInfo(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("getActiveInfo");
        //		String url="http://192.168.1.43/gateway/active/getActiveInfo?";
        builder = OkHttpUtils.post().url(url);
    }

    public void execute(Object... params) {
        dataValue = (Map<String, String>) params[2];
        action = (int) params[1];
        user_id = params[0].toString();
        token = params[3].toString();
        key_value.put("user_id", params[0].toString());
        key_value.put("token", token);


        builder.params(key_value).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
            }

            @Override
            public void onResponse(String response, int id) {
                String jsonString = response;
                Log.d("onSuccess", "onSuccess json = " + jsonString);
                try {
                    if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
                        Log.d("DATA_EMPTY", "DATA_EMPTY ");
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errcode = jsonObject.getString("errcode");
                        if (errcode.equals("0")) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            dataValue.put("activeDescription", dataObj.getString("activeDescription"));
                            dataValue.put("imgUrl", dataObj.getString("imgUrl"));
                            dataValue.put("lotteryInterface", dataObj.getString("lotteryInterface"));
                            dataValue.put("matchmaker", dataObj.getString("matchmaker"));
                            dataValue.put("microtopic", dataObj.getString("microtopic"));
                            dataValue.put("proposal", dataObj.getString("proposal"));
                            dataValue.put("userName", dataObj.getString("userName"));
                            String times = dataObj.getInt("times") + "";
                            dataValue.put("times", times);
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
                        } else {
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
                        }

                    }
                } catch (Exception e) {
                    Log.d("JSON_ERROR", "JSON_ERROR ");
                    receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
                }
            }

        });
    }
}
