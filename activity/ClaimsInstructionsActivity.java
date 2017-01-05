package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.LinearLayoutForListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
public class ClaimsInstructionsActivity extends BaseActivity {

	private Map<String, String> key_value = new HashMap<String, String>();
	private Dialog dialog;
	private RelativeLayout s_m_back;
	private RelativeLayout rl_right;
	private TextView tv_text;

	private String paymentid = "";

	private TextView product_name, tips_txt,claim_phone;
	
	private RelativeLayout claim_service_rel2;
	private ZiliaoAdapter zadapter;
	private LiuchengAdapter ldapter;

	private LinearLayoutForListView ziliao_list;
	private LinearLayoutForListView liucheng_list;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			final Map<String, Object> data = (Map<String, Object>) msg.obj;
			dialog.dismiss();
		
			List<Map<String, String>> r_m_list = (List<Map<String, String>>) data
					.get("r_m_list");
			final List<Map<String, String>> process_list = (List<Map<String, String>>) data
					.get("process_list");

			final String mobile= (String) data
					.get("mobile");
			String title= (String) data
					.get("title");
			String prompt_msg= (String) data
					.get("prompt_msg");
			
		
			product_name.setText(title);
			tips_txt.setText(prompt_msg);
			claim_phone.setText(mobile);
			dialog.dismiss();

			zadapter.setData(r_m_list);
			ziliao_list.setAdapter(zadapter);
			zadapter.notifyDataSetChanged();

			ldapter.setData(process_list);
			liucheng_list.setAdapter(ldapter);
			ldapter.notifyDataSetChanged();

			claim_service_rel2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
				
					
					if (mobile.equals("")) {

					} else {
						Intent intent = new Intent();

						intent.setAction(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + mobile));
						startActivity(intent);
					}
					
				
					
				}
			});
		}

	};

	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");

			dialog.dismiss();
			Toast.makeText(ClaimsInstructionsActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

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

				Toast.makeText(ClaimsInstructionsActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.claims_instructions);

		Intent intent = getIntent();

		paymentid = intent.getStringExtra("paymentid");
		init();
		initEvent();

	}

	void init() {
		s_m_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("理赔须知");
		claim_service_rel2 = (RelativeLayout) findViewById(R.id.claim_service_rel2);
		product_name = (TextView) findViewById(R.id.product_name);
		tips_txt = (TextView) findViewById(R.id.tips_txt);
		claim_phone = (TextView) findViewById(R.id.claim_phone);
		
		
		ziliao_list = (LinearLayoutForListView) findViewById(R.id.ziliao_list);
		liucheng_list = (LinearLayoutForListView) findViewById(R.id.liucheng_list);

		zadapter = new ZiliaoAdapter(ClaimsInstructionsActivity.this);
		ldapter = new LiuchengAdapter(ClaimsInstructionsActivity.this);
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

		key_value.put("paymentid", paymentid);

		dialog.show();
		final String url = IpConfig.getUri("getpaymentDetail");
		setlistdata(url);

		s_m_back.setOnClickListener(new OnClickListener() {

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
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);

				String status = "false";
				Message message = Message.obtain();

				message.obj = status;

				errcode_handler.sendMessage(message);
			}

			@Override
			public void onResponse(String response, int id) {

				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Map<String, String> errcode_map = new HashMap<String, String>();
				Map<String, Object> map_all = new HashMap<String, Object>();
				List<Map<String, String>> r_m_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> process_list = new ArrayList<Map<String, String>>();

				try {

					if (jsonString.equals("") || jsonString.equals("null")) {
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

							JSONArray required_materialArray = dataObject
									.getJSONArray("required_material");

							for (int i = 0; i < required_materialArray
									.length(); i++) {
								JSONObject jsonObject2 = required_materialArray
										.getJSONObject(i);
								Map<String, String> map = new HashMap<String, String>();
								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									String value = jsonObject2
											.getString(key);
									map.put(key, value);
								}
								r_m_list.add(map);
							}

							JSONArray processArray = dataObject
									.getJSONArray("process");

							for (int i = 0; i < processArray.length(); i++) {
								JSONObject jsonObject2 = processArray
										.getJSONObject(i);
								Map<String, String> map = new HashMap<String, String>();
								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									String value = jsonObject2
											.getString(key);
									map.put(key, value);
								}
								process_list.add(map);
							}

							String mobile = dataObject.getString("mobile");
							String title = dataObject.getString("title");
							String prompt_msg = dataObject
									.getString("prompt_msg");

							map_all.put("r_m_list", r_m_list);
							map_all.put("process_list", process_list);

							map_all.put("mobile", mobile);
							map_all.put("title", title);
							map_all.put("prompt_msg", prompt_msg);

							Message message = Message.obtain();

							message.obj = map_all;

							handler.sendMessage(message);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	
	}



	public class ZiliaoAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public ZiliaoAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		public void setData(List<Map<String, String>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO 自动生成的方法存根
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO 自动生成的方法存根
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO 自动生成的方法存根
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.required_material_item, parent, false);
			} else {
				view = convertView;
			}

			TextView title = (TextView) view.findViewById(R.id.title);
	

			title.setText(list.get(position).get("title"));

			return view;

		}

	}

	public class LiuchengAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public LiuchengAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		public void setData(List<Map<String, String>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO 自动生成的方法存根
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO 自动生成的方法存根
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO 自动生成的方法存根
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.claim_process_item, null);
			} else {
				view = convertView;
			}

			TextView title = (TextView) view.findViewById(R.id.title);
			TextView content = (TextView) view.findViewById(R.id.content);
			View view2 = view.findViewById(R.id.view);
			
			title.setText(list.get(position).get("title"));

			content.setText(list.get(position).get("content"));
			if(position==list.size()-1)
			{
				view2.setVisibility(View.GONE);
			}
			return view;

		}

	}

}
