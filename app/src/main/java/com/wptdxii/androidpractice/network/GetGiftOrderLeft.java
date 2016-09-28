package com.wptdxii.androidpractice.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.GiftNotReceive;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class GetGiftOrderLeft {


    private NetResultListener receiveDataListener;
    private GetBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<GiftNotReceive> dataMap;
    private int action;



    public GetGiftOrderLeft(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("getUnclaimedList");
        builder = OkHttpUtils.get().url(url);
    }

    public void execute(Object... params) {
        key_value.put("user_id", params[0].toString());
        key_value.put("page", params[1].toString());
        action = (Integer) params[2];
        dataMap = (ArrayList<GiftNotReceive>) params[3];

        Log.i("user_id----------", params[0].toString());
        Log.i("page------------", params[1].toString());

        builder.params(key_value).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {
                receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
            }

            @Override
            public void onResponse(String response) {

                String jsonString = response;
                Log.d("onSuccess", "未领取" + jsonString);
                try {
                    if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errcode = jsonObject.getString("errcode");
                        String errmsg=jsonObject.getString("errmsg");
                        if (errcode.equals("0")) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            JSONArray productList = dataObject.getJSONArray("products");
                            for(int i=0;i<productList.length();i++){
                                JSONObject obj=productList.getJSONObject(i);
                                GiftNotReceive bean=new GiftNotReceive();
                                bean.setProduct_icon(obj.getString("product_icon"));
                                bean.setProduct_id(obj.getString("product_id"));
                                bean.setProduct_name(obj.getString("product_name"));
                                bean.setShare_desc(obj.getString("share_desc"));
                                bean.setShare_icon(obj.getString("share_icon"));
                                bean.setShare_title(obj.getString("share_title"));
                                bean.setShare_url(obj.getString("share_url"));
                                bean.setValid_time_begin(obj.getString("valid_time_begin"));
                                bean.setValid_time_end(obj.getString("valid_time_end"));
                                dataMap.add(bean);
                            }
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, null);

                        } else if(errcode.equals("2")){
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, null);
                        }else if(errcode.equals("1")){
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, null);
                        }
                        else {
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
