package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserPrizeBean;
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

public class GetUserPrize {
    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<UserPrizeBean> list;
    private String user_id = "";
    private String token = "";
    private int action;
    private String errmsg;

    public GetUserPrize(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("userPrize");
        Log.i("url", url);
        builder = OkHttpUtils.post().url(url);
    }

    public void execute(Object... params) {
        user_id = (String) params[0];
        action = (int) params[2];
        list = (ArrayList<UserPrizeBean>) params[1];
        token = (String) params[3];
        key_value.put("user_id", user_id);
        key_value.put("token", token);
        key_value.put("past", params[4].toString());
        Log.i("user_id", user_id);

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
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                UserPrizeBean bean = new UserPrizeBean();
                                bean.setAddTime(obj.getString("addTime"));
                                bean.setEffectBegin(obj.getString("effectBegin"));
                                bean.setEffectEnd(obj.getString("effectEnd"));
                                bean.setId(obj.getString("id"));
                                bean.setImgUrl(obj.getString("imgUrl"));
                                bean.setMold(obj.getString("mold"));
                                bean.setMouldName(obj.getString("mouldName"));
                                bean.setState(obj.getString("state"));
                                bean.setStateName(obj.getString("stateName"));
                                bean.setNeedMsg(obj.getString("needMsg"));
                                bean.setGetType(obj.getString("getType"));
                                list.add(bean);
                            }

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
