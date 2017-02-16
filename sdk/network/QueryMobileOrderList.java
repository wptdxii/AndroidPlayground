package com.cloudhome.network;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.OrderListBean;
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

public class QueryMobileOrderList {


    private NetResultListener receiveDataListener;
    private GetBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<OrderListBean> dataMap;
    private int action;
    private ArrayList<String> codelist;



    public QueryMobileOrderList(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("queryMobileOrderList");


        builder = OkHttpUtils.get().url(url);
    }

    public void execute(Object... params) {
        action = (Integer) params[1];
        dataMap = (ArrayList<OrderListBean>) params[2];

        codelist= (ArrayList<String> ) params[6];
        key_value.put("userid", params[0].toString());
        key_value.put("token", params[7].toString());
        key_value.put("page", params[3].toString());
        key_value.put("ordertype", params[4].toString());
        key_value.put("status", params[5].toString());


        builder.params(key_value).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
            }

            @Override
            public void onResponse(String response, int id) {
                String jsonString = response;
                try {
                    if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errcode = jsonObject.getString("errcode");
                        if (errcode.equals("0")) {


                            JSONObject dataObject = jsonObject.getJSONObject("data");


                            JSONArray datalistArray = dataObject.getJSONArray("datalist");
                            JSONArray codelistArray = dataObject.getJSONArray("codelist");

                            if(codelist.size()<1) {
                                for (int i = 0; i < codelistArray.length(); i++) {
                                    codelist.add(codelistArray.getString(i));
                                }
                            }
                            for (int i = 0; i < datalistArray.length(); i++) {
                                JSONObject obj = (JSONObject) datalistArray.get(i);
                                JSONArray array = obj.getJSONArray("list");
                                if (array.length() == 0) {
                                    continue;
                                }


                                OrderListBean beanOut = new OrderListBean();
                                beanOut.setMonthDivider(obj.getString("month"));

                                ArrayList<OrderListBean> list = new ArrayList<OrderListBean>();
                                for (int m = 0; m < array.length(); m++) {
                                    JSONObject obj1 = array.getJSONObject(m);

                                    OrderListBean bean = new OrderListBean();

                                    bean.setOrderType(obj1.getString("ordertype"));

                                    bean.setOrderNo(obj1.getString("orderno"));
                                    bean.setStatus(obj1.getString("status"));
                                    bean.setProductName(obj1.getString("productname"));
                                    bean.setProductImageurl(obj1.getString("productimageurl"));

                                    bean.setInsurePeriod(obj1.getString("insureperiod"));
                                    bean.setHolderName(obj1.getString("holdername"));
                                    bean.setWageNo(obj1.getString("wageno"));
                                    bean.setId(obj1.getString("id"));

                                    bean.setMoneys(obj1.getString("moneys"));
                                    bean.setFycs(obj1.getString("fycs"));
                                    bean.setOrderCreatetime(obj1.getString("ordercreatetime"));
                                    if (!obj1.isNull("source")) {
                                        bean.setSource(obj1.getString("source"));
                                    }

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