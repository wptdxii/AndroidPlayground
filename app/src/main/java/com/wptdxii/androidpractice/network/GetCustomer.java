package com.wptdxii.androidpractice.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.MyClientBean;
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

public class GetCustomer {


	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private ArrayList<MyClientBean> myClientList;
	private int action;

	public GetCustomer(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url= IpConfig.getUri2("getCustomer");
		builder=OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {

		key_value.put("user_id", params[0].toString());
		action=(Integer) params[1];
		myClientList= (ArrayList<MyClientBean>) params[2];

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
						String[] b = {"A", "B", "C", "D", "E", "F", "G",
								"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
								"R", "S", "T", "U", "V", "W", "X", "Y", "Z","#" };
						JSONArray array=jsonObject.getJSONArray("data");
						for(int i=0;i<array.length();i++){
							JSONArray arrayObj=array.getJSONArray(i);
							for(int m=0;m<arrayObj.length();m++){
								MyClientBean bean=new MyClientBean();
								JSONObject obj=arrayObj.getJSONObject(m);
								bean.setNameFirstWord(b[i]);
								bean.setName(obj.getString("name"));
								bean.setClientId(obj.getString("id"));
								myClientList.add(bean);
							}
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
