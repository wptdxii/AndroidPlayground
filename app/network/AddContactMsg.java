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

public class AddContactMsg {
    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private int action;

    private String errmsg;

    public AddContactMsg(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("addContactMsg");
        Log.i("url", url);
        builder = OkHttpUtils.post().url(url);
    }

    public void execute(Object... params) {
        action = (int) params[4];
        key_value.put("id", params[3].toString());
        key_value.put("name", params[0].toString());
        key_value.put("tel", params[1].toString());
        key_value.put("addr", params[2].toString());

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
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errcode = jsonObject.getString("errcode");
                        String errmsg = jsonObject.getString("errmsg");
                        if (errcode.equals("0")) {
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
                        } else {
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, errmsg);
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
