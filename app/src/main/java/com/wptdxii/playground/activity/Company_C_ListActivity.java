package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
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

public class Company_C_ListActivity extends BaseActivity {

	private ListView listview;

	private MyAdapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();

	private RelativeLayout c_c_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private String c_code;
	private String loginString;

	private String user_state;
	private Dialog dialog;

	private String type = "";
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

				Toast.makeText(Company_C_ListActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			adapter.setData(list);
			listview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			dialog.dismiss();

			if (list.size() < 1) {
				CustomDialog.Builder builder = new CustomDialog.Builder(
						Company_C_ListActivity.this);

				builder.setTitle("提示");
				builder.setMessage("没有找到佣金信息");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				builder.create().show();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.company_c_list);

		listview = (ListView) findViewById(R.id.listview);

		loginString = sp.getString("Login_STATE", "none");
		Intent intent = getIntent();
		c_code = intent.getStringExtra("c_code");
		type = intent.getStringExtra("type");

		Log.d("9999", c_code);
		Log.d("8888", type);
		user_state = sp.getString("Login_CERT", "none");
		c_c_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("产品列表");

		adapter = new MyAdapter(Company_C_ListActivity.this);

		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("卖力加载中...");

		initEvent();

	}

	void initEvent() {

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				if (loginString.equals("none")) {
					Intent intent = new Intent();
					intent.setClass(Company_C_ListActivity.this,
							LoginActivity.class);
					Company_C_ListActivity.this.startActivity(intent);
				} else if (user_state.equals("01") || user_state.equals("02")) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							Company_C_ListActivity.this);

					builder.setTitle("提示");
					builder.setMessage("您还是未认证保险专家，请至保客云集或在此完成认证！");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else {
					Intent intent = new Intent();
					intent.setClass(Company_C_ListActivity.this,
							Product_C_InfoActivity.class);

					intent.putExtra("cid", adapter.list.get(pos).get("id")
							.toString());
					intent.putExtra("type", adapter.list.get(pos).get("type")
							.toString());

					Company_C_ListActivity.this.startActivity(intent);
				}
			}
		});

		c_c_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		dialog.show();
		key_value.put("c_code", c_code);
		key_value.put("type", type);

		key_value.put("page", "-1");

		String url = IpConfig.getUri("getCommissionList");
		setdata(url);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		loginString = sp.getString("Login_STATE", "none");
		user_state = sp.getString("Login_CERT", "none");

	}

	private void setdata(String url) {

		
		
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
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {

					Log.d("44444", jsonString);
					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {

						JSONObject jsonObject = new JSONObject(jsonString);
						JSONArray dataList = jsonObject.getJSONArray("data");

						for (int i = 0; i < dataList.length(); i++) {
							JSONObject jsonObject2 = dataList.getJSONObject(i);
							Map<String, Object> map = new HashMap<String, Object>();
							// 迭代输出json的key作为map的key

							Iterator<String> iterator = jsonObject2.keys();
							while (iterator.hasNext()) {
								String key = iterator.next();
								Object value = jsonObject2.get(key);
								map.put(key, value);
							}
							list.add(map);
						}
						Message message = Message.obtain();

						message.obj = list;

						handler.sendMessage(message);

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
		private List<Map<String, Object>> list = null;

		public MyAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		public void setData(List<Map<String, Object>> list) {
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
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.search_product_item,
						null);

				viewHolder.product_name = (TextView) convertView
						.findViewById(R.id.product_name);
				viewHolder.commission_hot = (ImageView) convertView
						.findViewById(R.id.commission_hot);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.product_name.setText(list.get(position)
					.get("insurance_name").toString());
			if (list.get(position).get("hot_flag").toString().equals("1")) {
				viewHolder.commission_hot.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.product_name.getLayoutParams();
				int M_left = Common.dip2px(Company_C_ListActivity.this,15);
				layoutParams.setMargins(M_left,0,0,0);
				viewHolder.product_name.setLayoutParams(layoutParams);
			} else{
				viewHolder.commission_hot.setVisibility(View.GONE);
			}

			return convertView;

		}

	}

	class ViewHolder {
		public TextView product_name;
		public ImageView commission_hot;
	}


}
