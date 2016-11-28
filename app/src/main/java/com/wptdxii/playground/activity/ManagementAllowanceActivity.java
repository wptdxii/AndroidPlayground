package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.IncomeRankAdapter;
import com.cloudhome.adapter.ManagementXiaXiaAdapter;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ManagementAllowanceActivity extends BaseActivity {

	private Map<String, String> key_value = new HashMap<String, String>();

	private String user_id;
	private String token;
	private TextView null_data_txt;

	private ImageView back;


	private ListView xiaxia_list;
	private ListView income_rank_list;
	private Dialog dialog;

    private IncomeRankAdapter top5Adapter;
    private ManagementXiaXiaAdapter xiaxiaAdapter;
    
	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;

			dialog.dismiss();
			String status = data;
			// String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {

				Toast.makeText(ManagementAllowanceActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");
			dialog.dismiss();
			Toast.makeText(ManagementAllowanceActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, Object> total_map = (Map<String, Object>) msg.obj;

			List<Map<String, String>> top5list = (List<Map<String, String>>) total_map.get("top5list");
			List<Map<String, String>> xiaxialist = (List<Map<String, String>>) total_map.get("xiaxialist");
			
			
			if(top5list.size()<1)
			{
				null_data_txt.setVisibility(View.VISIBLE);
			}
			
			top5Adapter.setData(top5list);
			income_rank_list.setAdapter(top5Adapter);
			top5Adapter.notifyDataSetChanged();
			
			
		
			xiaxiaAdapter.setData(xiaxialist);
			xiaxia_list.setAdapter(xiaxiaAdapter);
			xiaxiaAdapter.notifyDataSetChanged();
			
			
			dialog.dismiss();

		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.management_allowance);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		init();
		initEvent();

	}

	void init() {
		back= (ImageView) findViewById(R.id.back);
		xiaxia_list = (ListView) findViewById(R.id.xiaxialist);
		income_rank_list = (ListView) findViewById(R.id.income_rank_list);

		null_data_txt= (TextView) findViewById(R.id.null_data_txt);
		
		
		top5Adapter = new IncomeRankAdapter(ManagementAllowanceActivity.this);
		xiaxiaAdapter = new ManagementXiaXiaAdapter(ManagementAllowanceActivity.this);
		
		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");

	}

	void initEvent() {

		key_value.put("user_id", user_id);
		key_value.put("token", token);

		dialog.show();

		final String url = IpConfig.getUri("getsettle_guanli_jintie");
		setlistdata(url);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private void setlistdata(String url) {

		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e) {
				Log.e("error", "获取数据异常 ", e);

				String status = "false";
				Message message = Message.obtain();

				message.obj = status;

				errcode_handler.sendMessage(message);
			
				
			}

			@Override
			public void onResponse(String response) {
			
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, String>> xiaxialist = new ArrayList<Map<String, String>>();
				List<Map<String, String>> top5list = new ArrayList<Map<String, String>>();
				Map<String, Object> total_map = new HashMap<String, Object>();
				Map<String, String> errcode_map = new HashMap<String, String>();
				try {

					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						// Log.d("44444", jsonString);
						JSONObject jsonObject = new JSONObject(jsonString);

						String errcode = jsonObject.getString("errcode");
						if (!errcode.equals("0")) {
							String errmsg = jsonObject.getString("errmsg");

							errcode_map.put("errcode", errcode);
							errcode_map.put("errmsg", errmsg);

							Message message2 = Message.obtain();

							message2.obj = errcode_map;

							null_handler.sendMessage(message2);

						} else {

							JSONObject dataObject = jsonObject
									.getJSONObject("data");

							JSONArray top5List = dataObject
									.getJSONArray("top5");

							for (int i = 0; i < top5List.length(); i++) {

								JSONArray dataList2 = top5List
										.getJSONArray(i);

								String value1 = dataList2.getString(0);
								String value2 = dataList2.getString(1);
								String value3 = dataList2.getString(2);
								String value4 = dataList2.getString(3);

								Map<String, String> map = new HashMap<String, String>();

								map.put("date", value1);
								map.put("name", value2);
								map.put("price", value3);
								map.put("income", value4);
								top5list.add(map);
							}

							JSONArray xiaxiaList = dataObject
									.getJSONArray("xiaxia");

							for (int i = 0; i < xiaxiaList.length(); i++) {

								JSONArray dataList2 = xiaxiaList
										.getJSONArray(i);

								String value1 = dataList2.getString(0);
								String value2 = dataList2.getString(1);
								String value3 = dataList2.getString(2);

								Map<String, String> map = new HashMap<String, String>();

								map.put("title", value1);
								map.put("num", value2);
								map.put("code", value3);

								xiaxialist.add(map);
							}

							total_map.put("top5list", top5list);
							total_map.put("xiaxialist", xiaxialist);

							Message message = Message.obtain();

							message.obj = total_map;

							handler.sendMessage(message);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		
	

	}


}
