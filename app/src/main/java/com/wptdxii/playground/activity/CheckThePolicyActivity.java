package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
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

public class CheckThePolicyActivity extends BaseActivity {

	private MyAdapter adapter;
	private Dialog dialog;
	private Map<String, String> key_value = new HashMap<String, String>();
	private RelativeLayout c_t_p_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private ListView c_t_p_list;

	private String token;
	private String user_id;
	private String order_id;
	
	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;



				dialog.dismiss();

		}

	};



	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			dialog.dismiss();
			String errcode = data.get("errcode");
			String errmsg = data.get("errmsg");
			Log.d("455454", "455445" + errcode);
			if (!errcode.equals("0")) {

				Toast.makeText(CheckThePolicyActivity.this, errmsg,
						Toast.LENGTH_SHORT).show();


			}
		}

	};
	private Handler list_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

			adapter.setData(list);
			c_t_p_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			dialog.dismiss();

		}

	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.check_the_policy);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		
		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");
		
		init();
		initEvent();

	}

	private void init() {

		
	      dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("卖力加载中...");

		c_t_p_list = (ListView) findViewById(R.id.c_t_p_list);
		c_t_p_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("单品推荐");
		adapter = new MyAdapter(this);

	}

	private void initEvent() {

		key_value.put("order_id", order_id);
		key_value.put("user_id", user_id);
	    key_value.put("token",  token);
	    String url =IpConfig.getUri("getRecPolicyList");
	    setdata(url);
	    
	    c_t_p_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				finish();
			}
		});
	    
	    c_t_p_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				
				
			
				Intent intent = new Intent();

				intent.putExtra("order_id", order_id);
			
				
				intent.setClass(CheckThePolicyActivity.this,
						CheckPolicyDetailsActivity.class);

				CheckThePolicyActivity.this.startActivity(intent);
				
			}
			
		});
	}

	
	
	private void setdata(String url) {

		dialog.show();

		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);
				Toast.makeText(CheckThePolicyActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}

			@Override
			public void onResponse(String response, int id) {

				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				Map<String, String> errcode_map = new HashMap<String, String>();
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				try {

					if (jsonString.equals("") || jsonString.equals("null")) {

						errcode_map.put("errcode", "1003");
						errcode_map.put("errmsg", "网络连接失败，请确认网络连接后重试");

						Message message2 = Message.obtain();

						message2.obj = errcode_map;

						errcode_handler.sendMessage(message2);

					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						// String data = jsonObject.getString("data");
						String errcode = jsonObject.getString("errcode");
						if (!errcode.equals("0")) {
							String errmsg = jsonObject.getString("errmsg");

							errcode_map.put("errcode", errcode);
							errcode_map.put("errmsg", errmsg);

							Message message2 = Message.obtain();

							message2.obj = errcode_map;

							null_handler.sendMessage(message2);

						} else {
							JSONArray dataList = jsonObject
									.getJSONArray("data");

							for (int i = 0; i < dataList.length(); i++) {
								JSONObject jsonObject2 = dataList
										.getJSONObject(i);
								Map<String, String> map = new HashMap<String, String>();

								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();

									String value = jsonObject2.getString(key);
									map.put(key, value);

								}
								list.add(map);
							}
							Message message = Message.obtain();

							message.obj = list;

							list_handler.sendMessage(message);

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});


	}
	
	public class MyAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public MyAdapter(Context context) {
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
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.check_the_policy_item, null);

				viewHolder = new ViewHolder();

				viewHolder.check_no = (TextView) convertView
						.findViewById(R.id.check_no);
				viewHolder.product_name = (TextView) convertView
						.findViewById(R.id.product_name);
				viewHolder.holder_name = (TextView) convertView
						.findViewById(R.id.holder_name);
				viewHolder.insurance_name = (TextView) convertView
						.findViewById(R.id.insurance_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				// viewHolder = (ViewHolder) convertView.getTag();
				resetViewHolder(viewHolder);

			}

			String check_no = list.get(position).get("insurance_code");
			String product_name = list.get(position).get("product_name");
			String holder_name = list.get(position).get("holder_name");
			String insurance_name = list.get(position).get("insurance_name");
			
			viewHolder.check_no.setText(check_no);
			viewHolder.product_name.setText(product_name);
			viewHolder.holder_name.setText("投保人: "+holder_name);
			viewHolder.insurance_name.setText("被保险人: "+insurance_name);
		
			return convertView;
		}

	}

	public class ViewHolder {

		public TextView check_no;
		public TextView product_name;
		public TextView holder_name;
		public TextView insurance_name;
	}

	private void resetViewHolder(ViewHolder p_ViewHolder) {

		p_ViewHolder.check_no.setText(null);
		p_ViewHolder.product_name.setText(null);
		p_ViewHolder.holder_name.setText(null);
		p_ViewHolder.insurance_name.setText(null);
	

	}


}
