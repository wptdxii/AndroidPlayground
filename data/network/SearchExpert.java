package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.ExpertBean;
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

public class SearchExpert {


    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<ExpertBean> searchExpertList;
    private int action;

    public SearchExpert(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri("getSearchExpertListOnly");
        Log.d("searchUnderurl", url);
        builder = OkHttpUtils.post().url(url);
    }

    public void execute(Object... params) {
        Log.d("searchunderuser_id", params[0].toString());
        key_value.put("name", params[0].toString());
        key_value.put("orderType", params[1].toString());
        key_value.put("page", params[2].toString());
        action = (Integer) params[3];
        searchExpertList = (ArrayList<ExpertBean>) params[4];

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
                            JSONArray array = jsonObject.getJSONArray("data");
                            for (int m = 0; m < array.length(); m++) {
                                ExpertBean bean = new ExpertBean();
                                JSONObject obj = array.getJSONObject(m);
                                bean.setAvatar(obj.getString("avatar"));
                                bean.setCert_a(obj.getString("cert_a"));
                                bean.setCert_b(obj.getString("cert_b"));
                                bean.setCert_num_isShowFlg(obj.getString("cert_num_isShowFlg"));
                                bean.setCompany(obj.getString("company"));
                                bean.setCompany_name(obj.getString("company_name"));
                                bean.setGood_count(obj.getString("good_count"));
                                bean.setId(obj.getString("id"));
                                bean.setLicence(obj.getString("licence"));
                                bean.setMobile(obj.getString("mobile"));
                                bean.setMobile_area(obj.getString("mobile_area"));
                                bean.setMobile_num_short(obj.getString("mobile_num_short"));
                                bean.setPersonal_context(obj.getString("personal_context"));
                                bean.setPersonal_specialty(obj.getString("personal_specialty"));
                                bean.setState(obj.getString("state"));
                                bean.setType(obj.getString("type"));
                                bean.setUser_name(obj.getString("user_name"));
                                searchExpertList.add(bean);
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
