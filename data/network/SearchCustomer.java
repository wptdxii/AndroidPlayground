package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.MyClientBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class SearchCustomer {


    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<MyClientBean> myClientList;
    private int action;

    public SearchCustomer(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("searchCustomer");
        Log.d("searchCustomerurl", url);
        builder = OkHttpUtils.post().url(url);
    }

    public void execute(Object... params) {
        Log.d("searchCustomeuser_id", params[0].toString());
        Log.d("searchCustome", params[1].toString());
        key_value.put("user_id", params[0].toString());
        key_value.put("search", params[1].toString());
        action = (Integer) params[2];
        myClientList = (ArrayList<MyClientBean>) params[3];
        key_value.put("token", params[4].toString());

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
                        String errcode = jsonObject.getInt("errcode") + "";
                        if (errcode.equals("0")) {
                            JSONArray array = jsonObject.getJSONArray("data");
                            for (int m = 0; m < array.length(); m++) {
                                MyClientBean bean = new MyClientBean();
                                JSONObject obj = array.getJSONObject(m);
                                bean.setName(obj.getString("name"));
                                bean.setClientId(obj.getString("id"));
                                myClientList.add(bean);
                            }
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, null);
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
