package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class AuthRegister {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();

	private int action;
	private String errmsg;

	public AuthRegister(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("register");
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		key_value.put("mobile", params[0].toString());
		key_value.put("recommend_code", params[1].toString());
		key_value.put("password", params[2].toString());
		key_value.put("headimgurl", params[3].toString());
		key_value.put("client_type", params[4].toString());
		key_value.put("mold", params[5].toString());
		key_value.put("module", params[6].toString());
		key_value.put("code", params[7].toString());
		action=(Integer) params[8];

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
						String errmsg= jsonObject.getString("errmsg");
						if(errcode.equals("0"))
						{
							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
						}else{
							receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, errmsg);
						}
					}
				} catch (Exception e) {
					receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
				}
			}

		});
	}
}
