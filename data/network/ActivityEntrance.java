package com.cloudhome.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.MainPopBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.callback.StatusCallBack;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ActivityEntrance {
	private NetResultListener receiveDataListener;
	private GetBuilder builder;
	private Map<String, String> key_value = new HashMap<String, String>();
	private Context context;
	private int action;
	private String errmsg;
	private ArrayList<MainPopBean> mainPopList;

	public ActivityEntrance(NetResultListener receiveDataListener, Context context) {
		this.receiveDataListener = receiveDataListener;
		String url = IpConfig.getUri5("activityEntrance");
		builder = OkHttpUtils.get().url(url);
		this.context = context;
	}

	public void execute(Object... params) {
		key_value.put("userId", params[0].toString());
		key_value.put("model", params[1].toString());
		key_value.put("token", params[2].toString());
		key_value.put("suid", params[3].toString());
		action = (Integer) params[4];
		mainPopList= (ArrayList<MainPopBean>) params[5];

		builder.params(key_value).build().execute(new StatusCallBack(context) {


			@Override
			public void onError(Call call, Exception e, int id) {
				receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);
			}
			@Override
			public void onResponse(String response, int id) {
				if (!TextUtils.isEmpty(response)) {
					Log.d("onSuccess", "onSuccess json = " + response);
					mainPopList= (ArrayList<MainPopBean>) JSON.parseArray(response, MainPopBean.class);
					receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, mainPopList);
				} else {
					receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
				}
			}
		});
	}
}
