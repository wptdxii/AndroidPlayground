package com.cloudhome.network;

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

public class GetSuggestAppointForUserid {

	
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private ArrayList<Map<String,String>> dataMap;
	
	private int action;
	
	public GetSuggestAppointForUserid(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri("getSuggestAppointForUserid");
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		action=(Integer) params[3];
		dataMap=(ArrayList<Map<String, String>>) params[4];
		key_value.put("user_id", params[0].toString());
		key_value.put("token", params[1].toString());
		key_value.put("WithTime", params[2].toString());
		
		Log.i("user_id-------------", params[0].toString());
		Log.i("token-------------", params[1].toString());
		Log.i("WithTime-------------", params[2].toString());
		
		builder.params(key_value).build().execute(new StringCallback(){

			@Override
			public void onError(Call call, Exception e, int id) {

				receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				try {
					if(jsonString.equals("") || jsonString.equals("null"))
					{
						receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
					}
					else{
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						if(errcode.equals("0"))
						{
							JSONArray array=jsonObject.getJSONArray("data");
							for(int i=0;i<array.length();i++){
								JSONObject obj=array.getJSONObject(i);
								HashMap<String,String> hashMap=new HashMap<String,String>();
								hashMap.put("add_time", obj.getString("add_time"));
								hashMap.put("appoint_name", obj.getString("appoint_name"));
								hashMap.put("appoint_sex", obj.getString("appoint_sex"));
								hashMap.put("mobile", obj.getString("mobile"));
								dataMap.add(hashMap);
							}
							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, null);
						}else if(errcode.equals("1016")){
							receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
						}else{
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
