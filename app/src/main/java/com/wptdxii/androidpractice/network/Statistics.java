package com.wptdxii.androidpractice.network;

import android.util.Log;

import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class Statistics {

	
	private NetResultListener receiveDataListener;
	private GetBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	
	private int action;
	
	public Statistics(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri("statistics");
		builder=OkHttpUtils.get().url(url);
	}
	
	public void execute(Object... params) {

		key_value.put("position", params[0].toString());
		Log.d("统计了------------", params[0].toString());
		builder.params(key_value).build().execute(new StringCallback(){

			@Override
			public void onError(Call call, Exception e) {
			}

			@Override
			public void onResponse(String response) {

				String jsonString = response;
				Log.d("statistics------", "onSuccess json = " + jsonString);
				
			}});
	}
}
