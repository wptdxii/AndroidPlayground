package com.wptdxii.androidpractice.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UnderInsuerBean;
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

public class GetAgents {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private String user_id;
	private int action;
	private String page;
	private ArrayList<UnderInsuerBean> list;
	private Map<String,Object> dataValue;
	private String errmsg;

	public GetAgents(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url=IpConfig.getUri2("getAgents");
		builder=OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		action= (int) params[1];
		user_id= params[0].toString();
		page=params[2].toString();
		list= (ArrayList<UnderInsuerBean>) params[3];
		dataValue= (Map<String, Object>) params[4];
		key_value.put("user_id", user_id);
		key_value.put("page", page);

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
						boolean isVIP=dataObj.getBoolean("isVIP");
						JSONObject numObj=dataObj.getJSONObject("nums");
						int total=numObj.getInt("total");
						int indirect=numObj.getInt("indirect");
						int direct=numObj.getInt("direct");
						dataValue.put("isVIP",isVIP);
						dataValue.put("total",total+"人");
						dataValue.put("indirect",indirect+"人");
						dataValue.put("direct",direct+"人");

						JSONArray array=dataObj.getJSONArray("list");
						for(int i=0;i<array.length();i++){
							UnderInsuerBean bean=new UnderInsuerBean();
							JSONObject obj=array.getJSONObject(i);
							bean.setName(obj.getString("name"));
							bean.setCompany(obj.getString("company"));
							bean.setId(obj.getString("id"));
							bean.setAvatar(obj.getString("avatar"));
							bean.setState(obj.getString("state"));
							bean.setCity(obj.getString("city"));
							bean.setOrder_num(obj.getString("order_num"));
							bean.setPremiums(obj.getString("premiums"));
							bean.setRef_num(obj.getString("ref_num"));
							list.add(bean);
						}
						receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, errmsg);
					}else{
						receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, null);
					}

					}
				} catch (Exception e) {
					Log.d("JSON_ERROR", "JSON_ERROR " );
					receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
				}
			}});
	}
}
