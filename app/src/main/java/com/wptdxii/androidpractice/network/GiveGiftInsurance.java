package com.wptdxii.androidpractice.network;

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

public class GiveGiftInsurance {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();

	private int action;
	private String user_id;
	private String product_id;
	private boolean is_self;
	private int count;
	Map<String, Object> map=null;
	String errmsg="";

	public GiveGiftInsurance(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("giveGiftInsurance");
		Log.d("giveGiftInsurance", url);
		builder=OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		is_self= (boolean) params[0];
		user_id= (String) params[1];
		product_id=(String) params[2];
		count= (int) params[3];
		action=(Integer) params[5];
		map= (Map<String, Object>) params[4];
		key_value.put("user_id",user_id);
		key_value.put("product_id",product_id);
		key_value.put("count",count+"");
		key_value.put("is_self",is_self+"");
		builder.params(key_value).build().execute(new StringCallback(){
			@Override
			public void onError(Call call, Exception e) {
				receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
			}

			@Override
			public void onResponse(String response) {

				String jsonString = new String(response);
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				try {
					if(jsonString==null||jsonString.equals("")||jsonString.equals("null"))
					{ 
						receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, errmsg);
					}
					else{
					JSONObject jsonObject = new JSONObject(jsonString);
					String errcode = jsonObject.getString("err_code");
						errmsg=	jsonObject.getString("err_msg");
					if(errcode.equals("0"))
					{
						JSONObject dataObj=jsonObject.getJSONObject("data");
						map.put("score",dataObj.getInt("score"));
						map.put("is_self",dataObj.getString("is_self"));
						map.put("url",dataObj.getString("url"));
						if(!is_self){
							map.put("icon",dataObj.getString("icon"));
							map.put("title",dataObj.getString("title"));
							map.put("desc",dataObj.getString("desc"));
						}
						receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
					}else{
						receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, errmsg);
					}
					}
				} catch (Exception e) {
					receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, errmsg);
				}
			}});
	}
}
