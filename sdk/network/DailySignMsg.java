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
import java.util.Map;

import okhttp3.Call;

public class DailySignMsg {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private Map<String, String> dataValue;
	private ArrayList<HashMap<String,String>> list;
	private String user_id="";
	private String token="";
	private int action;

	private String errmsg;

	public DailySignMsg(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("signMsg");
		Log.i("url",url);
		builder= OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		dataValue= (Map<String, String>) params[0];
		user_id= (String) params[1];
		action= (int) params[2];
		list= (ArrayList<HashMap<String, String>>) params[3];
		token=(String) params[4];
		key_value.put("user_id",user_id);
		key_value.put("token",token);
		Log.i("user_id",user_id);
		Log.i("token",token);

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
						receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
					}
					else{
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						String errmsg = jsonObject.getString("errmsg");
						if(errcode.equals("0"))
						{
							JSONObject dataObj=jsonObject.getJSONObject("data");
							JSONArray listArray=dataObj.getJSONArray("setList");
							for (int i = 0; i <listArray.length() ; i++) {
								JSONObject obj=listArray.getJSONObject(i);
								HashMap<String, String> hashMap=new HashMap<String, String>();
								hashMap.put("imgUrl",obj.getString("imgUrl"));
								hashMap.put("mouldName",obj.getString("mouldName"));
								list.add(hashMap);
							}
							JSONObject signObj=dataObj.getJSONObject("sign");
							JSONObject userBasicObj=dataObj.getJSONObject("userBasic");
							JSONObject theDayObj=signObj.getJSONObject("theDay");
							int code =signObj.getInt("code");
							dataValue.put("code",code+"");
							dataValue.put("imgUrl",theDayObj.getString("imgUrl"));
							dataValue.put("note",theDayObj.getString("note"));
							dataValue.put("signCount",dataObj.getInt("signCount")+"");
							dataValue.put("avatarUrl",userBasicObj.getString("avatarUrl"));
							dataValue.put("id",userBasicObj.getInt("id")+"");
							dataValue.put("name",userBasicObj.getString("name"));
							dataValue.put("score",userBasicObj.getInt("score")+"");
							receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
						}else{
							receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, errmsg);
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
