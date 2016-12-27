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

public class SaveDeviceMsg {
    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();

    private int action;
    private String errmsg;

    public SaveDeviceMsg(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String Device_URL = IpConfig.getUri("saveDeviceMsg");
        builder = OkHttpUtils.post().url(Device_URL);
    }

    public void execute(Object... params) {
        key_value.put("user_id", params[0].toString());
        key_value.put("token", params[1].toString());
        key_value.put("device_id", params[2].toString());
        key_value.put("os_version", params[3].toString());
        action = (Integer) params[4];
        Log.i("user_id----------------", params[0].toString());
        Log.i("token----------------", params[1].toString());
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
                    if (jsonString.equals("") || jsonString.equals("null")) {
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errcode = jsonObject.getString("errcode");
                        if (errcode.equals("0")) {
                            errmsg = jsonObject.getString("errmsg");
                            String only_key = jsonObject.getJSONObject("data").getString("only_key");
                            if (only_key == null || only_key.equals("null")) {
                                only_key = "";
                            }
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, only_key);
                        } else {
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
                        }
                    }
                } catch (Exception e) {
                    receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
                }
            }

        });
    }
}
