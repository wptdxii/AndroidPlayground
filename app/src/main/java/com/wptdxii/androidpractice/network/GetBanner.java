package com.wptdxii.androidpractice.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.MainProBean;
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

public class GetBanner {
	private NetResultListener receiveDataListener;
	private PostFormBuilder builder;
	private ArrayList<MainProBean> bannerList;
	private int action;
	private Map<String, String> key_value = new HashMap<String, String>();

	public GetBanner(NetResultListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;
		String url= IpConfig.getUri("getBanner");
		Log.d("getBanner", url);
		builder=OkHttpUtils.post().url(url);
	}
	
	public void execute(Object... params) {
		action= (int) params[0];
		bannerList= (ArrayList<MainProBean>) params[1];

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
						JSONArray array=jsonObject.getJSONArray("data");
							for(int m=0;m<array.length();m++){
								MainProBean bean=new MainProBean();
								JSONObject obj=array.getJSONObject(m);
								bean.setTitle(obj.getString("Title"));
								bean.setBannerUrl(obj.getString("bannerUrl"));
								bean.setBrief(obj.getString("brief"));
								bean.setDetailUrl(obj.getString("detailUrl"));
								bean.setFlatUrl(obj.getString("flatUrl"));
								bean.setIs_share(obj.getString("is_share"));
								bean.setLoginFlag(obj.getString("loginFlag"));
								bean.setLogo(obj.getString("logo"));
								bean.setPage(obj.getString("page"));
								bean.setPicLength(obj.getString("picLength"));
								bean.setShowFlg(obj.getString("showFlg"));
								bean.setIsAdvertisement(true);
								bannerList.add(bean);
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
