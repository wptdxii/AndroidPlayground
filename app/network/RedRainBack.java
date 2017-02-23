package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RedRainBack {
	private NetResultListener receiveDataListener;
	private GetBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();

	private int action;
	private String errmsg;

	public RedRainBack(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("actNewYear");
		builder= OkHttpUtils.get().url(url);
	}
	
	public void execute(Object... params) {
		action=(Integer) params[2];
		key_value.put("user_id", params[0].toString());
		key_value.put("token", params[3].toString());
		key_value.put("userPrizeRecordId", params[1].toString());

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
						receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
					}
					else{
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						if(errcode.equals("0"))
						{
							errmsg= jsonObject.getString("errmsg");
							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
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
