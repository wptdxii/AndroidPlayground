package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.IncomeExpendBean;
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

public class GetPaymentListForWallet {


    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();

    private ArrayList<String> codelist;
    private ArrayList<IncomeExpendBean> dataMap;

    private int action;
    private String type;
    private String title;


    public GetPaymentListForWallet(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("queryUserConsumerItem");
        builder = OkHttpUtils.post().url(url);
    }

    public void execute(Object... params) {
        action = (Integer) params[2];
        dataMap = (ArrayList<IncomeExpendBean>) params[3];
        type = (String) params[4];
        title = (String) params[5];

        codelist = (ArrayList<String>) params[6];

        key_value.put("userid", params[0].toString());
        key_value.put("token", params[1].toString());

        Log.i("user_id----------", params[0].toString());
        Log.i("token------------", params[1].toString());
        Log.i("type-------------", params[4].toString());
        Log.i("title------------", params[5].toString());

        key_value.put("type", params[4].toString());
        key_value.put("title", params[5].toString());


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
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errcode = jsonObject.getString("errcode");
                        if (errcode.equals("0")) {


                            JSONObject dataObject = jsonObject.getJSONObject("data");


                            JSONArray paylistArray = dataObject.getJSONArray("paylist");

                            JSONArray codelistArray = dataObject.getJSONArray("codelist");


                            if (codelist.size() < 1) {

                                for (int i = 0; i < codelistArray.length(); i++) {

                                    codelist.add(codelistArray.getString(i));

                                }
                            }

                            for (int i = 0; i < paylistArray.length(); i++) {
                                JSONObject obj = (JSONObject) paylistArray.get(i);
                                JSONArray array = obj.getJSONArray("list");
                                if (array.length() == 0) {
                                    continue;
                                }

                                IncomeExpendBean beanOut = new IncomeExpendBean();
                                beanOut.setMonthDivider(obj.getString("month"));

                                ArrayList<IncomeExpendBean> list = new ArrayList<IncomeExpendBean>();
                                for (int m = 0; m < array.length(); m++) {
                                    JSONObject obj1 = array.getJSONObject(m);
                                    IncomeExpendBean bean = new IncomeExpendBean();

                                    bean.setAddTime(obj1.getString("addtime"));
                                    bean.setId(obj1.getString("id"));
                                    bean.setMoney(obj1.getString("money"));
                                    bean.setTitle(obj1.getString("title"));
                                    bean.setCategory(obj1.getString("category"));
                                    list.add(bean);
                                }
                                beanOut.setList(list);
                                dataMap.add(beanOut);
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
