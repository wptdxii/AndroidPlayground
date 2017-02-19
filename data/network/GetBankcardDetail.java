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

public class GetBankcardDetail {

	
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private Map<String,String> dataMap;
	
	private int action;
	private String url="";
	
	public GetBankcardDetail(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri("getBankcardDetail");
		Log.i("url------------------", url);
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		action=(Integer) params[3];
		dataMap=(Map<String, String>) params[4];
		key_value.put("user_id", params[0].toString());
		key_value.put("token", params[1].toString());
		key_value.put("card_id", params[2].toString());
//		Log.i("card_id------------------", params[2].toString());
//		Log.i("user_id------------------", params[0].toString());
//		Log.i("token------------------", params[1].toString());
		
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
							JSONObject obj=jsonObject.getJSONObject("data");
							String name=obj.getString("name");
							String bank_name=obj.getString("bank_name");
							String bank_code=obj.getString("bank_code");
							String open_account_bank=obj.getString("open_account_bank");
							String bank_account=obj.getString("bank_account");
							dataMap.put("name", name);
							dataMap.put("bank_name", bank_name);
							dataMap.put("bank_code", bank_code);
							dataMap.put("open_account_bank", open_account_bank);
							dataMap.put("bank_account", bank_account);

							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, null);
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
