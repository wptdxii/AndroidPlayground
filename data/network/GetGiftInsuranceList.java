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
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class GetGiftInsuranceList {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();

	private int action;
	private String user_id;
	private String token;
	List<Map<String, Object>> list=new ArrayList<Map<String, Object>>() ;

	public GetGiftInsuranceList(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("activeGaveProduct");
		Log.d("GetGiftInsuranceList", url);
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		user_id= (String) params[0];
		action=(Integer) params[1];
		token=(String) params[2];
		key_value.put("user_id",user_id);
		key_value.put("token",token);
		builder.params(key_value).build().execute(new StringCallback(){
			@Override
			public void onError(Call call, Exception e, int id) {

				receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = new String(response);
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				try {
					if(jsonString==null||jsonString.equals("")||jsonString.equals("null"))
					{
						receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
					}
					else{
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						if(errcode.equals("0"))
						{
							JSONObject dataObj=jsonObject.getJSONObject("data");
							JSONObject userObj=dataObj.getJSONObject("user");
							int score=userObj.getInt("score");
							JSONArray productArray=dataObj.getJSONArray("products");
							for(int i=0;i<productArray.length();i++){
								JSONObject obj=productArray.getJSONObject(i);
								Map<String, Object> map=new HashMap<String, Object>();
								map.put("val",obj.getInt("val"));
								map.put("product_id",obj.getString("product_id"));
								map.put("product_icon",obj.getString("product_icon"));
								map.put("label",obj.getString("label"));
								map.put("product_name",obj.getString("product_name"));
								map.put("product_cover",obj.getString("product_cover"));
								map.put("age_title",obj.getString("age_title"));
								map.put("age_label",obj.getString("age_label"));
								map.put("ensure_label",obj.getString("ensure_label"));
								map.put("ensure_title",obj.getString("ensure_title"));
								map.put("integral_title",obj.getString("integral_title"));
								map.put("integral_label",obj.getString("integral_label"));
								map.put("score",score);
								list.add(map);
							}

							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, list);
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
