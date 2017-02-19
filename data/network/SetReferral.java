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

public class SetReferral {
    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private HashMap<String, String> referalMap;

    private int action;
    private String errmsg;

    public SetReferral(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri("setReferral");
        builder = OkHttpUtils.post().url(url);
    }

    public void execute(Object... params) {
        action = (Integer) params[3];
        key_value.put("user_id", params[0].toString());
        key_value.put("token", params[1].toString());
        key_value.put("mobile", params[2].toString());
        referalMap = (HashMap<String, String>) params[4];


        Log.i("user_id----------------", params[0].toString());
        Log.i("token----------------", params[1].toString());
        Log.i("mobile----------------", params[3].toString());


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
                        String errmsg = jsonObject.getString("errmsg");
                        if (errcode.equals("0")) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            String name = dataObject.getString("name");
                            //						String user_id=dataObject.getString("user_id");
                            referalMap.put("name", name);
                            //						referalMap.put("user_id",user_id);
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
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
