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

public class GetDailyMsg {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private Map<String, String> dataValue;
	private String user_id;
	private int action;

	private String errmsg;

	public GetDailyMsg(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("dailyMsg");
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		dataValue= (Map<String, String>) params[0];
		action= (int) params[1];

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
					if(jsonString==null||jsonString.equals("")||jsonString.equals("null"))
					{
						Log.d("DATA_EMPTY", "DATA_EMPTY " );
						receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
					}
					else{
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						if(errcode.equals("0"))
						{
							JSONObject dataObj=jsonObject.getJSONObject("data");
							JSONObject almanacObj=dataObj.getJSONObject("almanac");
							JSONObject weatherObj=dataObj.getJSONObject("weather");
							dataValue.put("avoid",almanacObj.getString("avoid"));
							dataValue.put("date",almanacObj.getString("date"));
							dataValue.put("lunar", almanacObj.getString("lunar"));
							dataValue.put("suit",almanacObj.getString("suit"));

							dataValue.put("city",weatherObj.getString("city"));
							dataValue.put("temperature",weatherObj.getString("temperature"));
							dataValue.put("weatherBgImg",weatherObj.getString("weatherBgImg"));
							dataValue.put("weatherImg",weatherObj.getString("weatherImg"));
							dataValue.put("weatherText",weatherObj.getString("weatherText"));
							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
						}else{
							receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
						}

					}
				} catch (Exception e) {
					Log.d("JSON_ERROR", "JSON_ERROR " );
					receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
				}
			}
		});
	}
}
