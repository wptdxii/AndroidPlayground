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

public class GetApplyCash {

	
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private ArrayList<HashMap<String,String>> cardList;
	private int action;
	
	public GetApplyCash(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		//String url=IpConfig.getUri("applyCash");
		String url=IpConfig.getUri2("getmyBankCards");
		builder=OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		action=(Integer) params[2];
		cardList=(ArrayList<HashMap<String,String>>) params[3];
		key_value.put("userId", params[0].toString());
		key_value.put("token", params[1].toString());
		
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


						JSONArray dataArray = jsonObject.getJSONArray("data");
						for (int i = 0; i < dataArray.length(); i++) {


							JSONObject obj = (JSONObject) dataArray.get(i);
							HashMap<String, String> dataMap = new HashMap<String, String>();

							dataMap.put("id", obj.getString("id"));
							dataMap.put("bankCardNo", obj.getString("bankCardNo"));


							JSONObject bankbinDtoobj = obj.getJSONObject("bankbinDto");

							dataMap.put("bankColor", bankbinDtoobj.getString("bankColor"));
							dataMap.put("bankName", bankbinDtoobj.getString("bankName"));
							dataMap.put("cardsType", bankbinDtoobj.getString("cardsType"));

							dataMap.put("bankLogoImg", bankbinDtoobj.getString("bankLogoImg"));

							dataMap.put("bankTel", bankbinDtoobj.getString("bankTel"));


							cardList.add(dataMap);

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
