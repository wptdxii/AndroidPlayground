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

public class FetchMoney {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	
	private int action;
	private String errmsg;
	
	public FetchMoney(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri("fetchMoney");
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		action=(Integer) params[6];
		errmsg=params[5].toString();
		key_value.put("user_id", params[0].toString());
		key_value.put("token", params[1].toString());
		key_value.put("consume_Money", params[2].toString());
		key_value.put("mobile", params[3].toString());
		key_value.put("account_id", params[4].toString());


		Log.i("user_id----------------", params[0].toString());
		Log.i("token----------------", params[1].toString());
		Log.i("consume_Money------", params[2].toString());
		Log.i("mobile----------------", params[3].toString());
		Log.i("account_id------------", params[4].toString());



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
