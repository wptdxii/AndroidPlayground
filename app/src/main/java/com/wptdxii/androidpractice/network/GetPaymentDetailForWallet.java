package com.wptdxii.androidpractice.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
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

public class GetPaymentDetailForWallet {

	
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private ArrayList<ArrayList<String>> dataMap;
	
	private int action;
	
	public GetPaymentDetailForWallet(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("getPaymentDetailForWallet");
		builder=OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		action=(Integer) params[3];
		dataMap=(ArrayList<ArrayList<String>>) params[4];
		key_value.put("userid", params[0].toString());
		key_value.put("token", params[1].toString());
		key_value.put("detailsid", params[2].toString());

		Log.d("detailsuserid---------", params[0].toString());
		Log.d("detailstoken---------", params[1].toString());
		Log.d("detailsid---------", params[2].toString());
		builder.params(key_value).build().execute(new StringCallback(){

			@Override
			public void onError(Call call, Exception e) {
				receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
			}

			@Override
			public void onResponse(String response) {

				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				try {
					if(jsonString.equals("") || jsonString.equals("null"))
					{ 
						receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
					}
					else{
					JSONObject jsonObject = new JSONObject(jsonString);
					String errcode = jsonObject.getString("errcode");
					if(errcode.equals("0"))
					{
						JSONObject dataObj=jsonObject.getJSONObject("data");
						JSONArray detailArray=dataObj.getJSONArray("details");
						for(int i=0;i<detailArray.length();i++){

							ArrayList<String> key_value_list =new ArrayList<String>();

									JSONArray array=detailArray.getJSONArray(i);


							key_value_list.add(array.getString(0));
							key_value_list.add(array.getString(1));

							dataMap.add(key_value_list);
						}
						receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, null);
					}else{
						receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
					}
					}
				} catch (Exception e) {
					receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
				}
			
				
			}});
	}
}
