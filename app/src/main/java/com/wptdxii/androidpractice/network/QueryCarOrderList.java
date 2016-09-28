package com.wptdxii.androidpractice.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.OrderListCarBean;
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

public class QueryCarOrderList {


    private NetResultListener receiveDataListener;
    private GetBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<OrderListCarBean> dataMap;
    private int action;
    private ArrayList<String> codelist;



    public QueryCarOrderList(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        String url = IpConfig.getUri2("queryMobileCarOrderList");


        builder = OkHttpUtils.get().url(url);
    }

    public void execute(Object... params) {
        action = (Integer) params[1];
        dataMap = (ArrayList<OrderListCarBean>) params[2];

        codelist= (ArrayList<String>) params[6];
        key_value.put("userid", params[0].toString());

        key_value.put("page", params[3].toString());
        key_value.put("ordertype", params[4].toString());
        key_value.put("status", params[5].toString());

      Log.i("user_id----------", params[0].toString());
        Log.i("page------------", params[3].toString());
        Log.i("ordertype-------------", params[4].toString());
        Log.i("status------------", params[5].toString());

        builder.params(key_value).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {
                receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
            }

            @Override
            public void onResponse(String response) {

                String jsonString = response;
                Log.d("onSuccess", "onSuccess json = " + jsonString);
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

                            String isshowtuiguangfei = dataObject.getString("isshowtuiguangfei");
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



                                OrderListCarBean beanOut = new OrderListCarBean();
                                beanOut.setMonthDivider(obj.getString("month"));

                                ArrayList<OrderListCarBean> list = new ArrayList<OrderListCarBean>();



                                for (int m = 0; m < array.length(); m++) {

                                    JSONArray array2 = array.getJSONArray(m);
                                    JSONObject obj1 = (JSONObject) array2.get(0);

                                    OrderListCarBean bean = new OrderListCarBean();

                                    bean.setOrdertype(obj1.getString("ordertype"));

                                    bean.setOrderno(obj1.getString("orderno"));
                                    bean.setStatus(obj1.getString("status"));
                                    bean.setProductname(obj1.getString("productname"));
                                    bean.setProductimageurl(obj1.getString("productimageurl"));
                                    bean.setOrderDetailUrl(obj1.getString("orderDetailUrl"));


                                    bean.setActualTaxFee(obj1.getString("actualTaxFee"));
                                    bean.setTaxValue(obj1.getString("taxValue"));



                                    bean.setInsureperiod(obj1.getString("insureperiod"));
                                    bean.setHoldername(obj1.getString("holdername"));
                                    bean.setWageno(obj1.getString("wageno"));
                                    bean.setId(obj1.getString("id"));

                                    bean.setMoneys(obj1.getString("moneys"));
                                    bean.setFycs(obj1.getString("fycs"));
                                    bean.setOrdercreatetime(obj1.getString("ordercreatetime"));

                                    bean.setIsshowtuiguangfei(isshowtuiguangfei);

                                    bean.setProductsize(array2.length());

                                    if(array2.length()>1)
                                    {

                                        JSONObject obj2 = (JSONObject) array2.get(1);

                                        bean.setProductname2(obj2.getString("productname"));
                                        bean.setProductimageurl2(obj2.getString("productimageurl"));
                                        bean.setOrderDetailUrl2(obj2.getString("orderDetailUrl"));
                                        bean.setHoldername2(obj2.getString("holdername"));
                                        bean.setInsureperiod2(obj2.getString("insureperiod"));
                                        bean.setTaxValue2(obj2.getString("taxValue"));


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
