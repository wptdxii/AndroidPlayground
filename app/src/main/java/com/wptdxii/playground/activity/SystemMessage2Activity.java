package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
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

public class SystemMessage2Activity extends BaseActivity implements
		IXListViewListener {

	private MyAdapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();
	private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
	private Dialog dialog;
	private ImageView s_m_back;
	private String user_id;
	private String token;
	private int pagenum = 0;
	private Handler mHandler;

	private XListView s_m_xlist;

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;



			if (pagenum == 0) {


				if(resultdata.size()<1)
				{
					resultdata.addAll(list);
				adapter.setData(resultdata);
				s_m_xlist.setAdapter(adapter);
				adapter.notifyDataSetChanged();

				}


			} else {
				resultdata.addAll(list);
				adapter.notifyDataSetChanged();
				s_m_xlist.stopLoadMore();


			}

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

				Toast.makeText(SystemMessage2Activity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.system_message);

		init();
		initEvent();

	}

	void init() {
		s_m_back = (ImageView) findViewById(R.id.s_m_back);
		s_m_xlist = (XListView) findViewById(R.id.s_m_xlist);
		s_m_xlist.setPullLoadEnable(true);
		s_m_xlist.setXListViewListener(SystemMessage2Activity.this);
	

		adapter = new MyAdapter(SystemMessage2Activity.this);
		
		  dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("请稍后...");
          
		
		mHandler = new Handler();
	}

	void initEvent() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("token", token);
		key_value.put("user_id", user_id);
		key_value.put("page", "0");
		
		
		final String url = IpConfig.getUri("getSystemMessage");
		setlistdata(url);

		s_m_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				int pos = position - 1;
				if (pos >= 0 && pos < adapter.list.size()) {
					resultdata.get(pos).put("state","2");
					adapter.notifyDataSetChanged();

					Intent intent = new Intent();

					// 设置传递的参数

					intent.putExtra("message_id",
							adapter.list.get(pos).get("id").toString());

					// 从Activity IntentTest跳转到Activity IntentTest01

					intent.setClass(SystemMessage2Activity.this,
							MessageDetailActivity.class);

					// 启动intent的Activity

					SystemMessage2Activity.this.startActivity(intent);

				}
			}
		});

		s_m_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Editor editor4 = sp4.edit();
				editor4.putInt("page", 0);
				editor4.commit();

				Intent intent = new Intent(SystemMessage2Activity.this,
						AllPageActivity.class);

				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
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
						String data = jsonObject.getString("data");

						JSONObject dataObject = new JSONObject(data);
						Log.d("44444", data);
						JSONArray messageList = dataObject
								.getJSONArray("message");

						for (int i = 0; i < messageList.length(); i++) {
							JSONObject jsonObject2 = messageList
									.getJSONObject(i);
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

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.system_mes_item,
						null);
				// TextView s_sms_name = (TextView)
				// view.findViewById(R.id.s_sms_name);
				holder.s_sms_content = (TextView) convertView
						.findViewById(R.id.s_sms_content);
				holder.s_sms_time = (TextView) convertView
						.findViewById(R.id.s_sms_time);
				holder.s_sms_new = (TextView) convertView
						.findViewById(R.id.s_sms_new);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			String state = list.get(position).get("state").toString();
			if (state.equals("2")) {
				holder.s_sms_new.setVisibility(View.GONE);
			} else if (state.equals("1")) {
				holder.s_sms_new.setVisibility(View.VISIBLE);
			}
			// setText(list.get(position).get("title").toString());
			holder.s_sms_content.setText(list.get(position).get("title")
					.toString());
			holder.s_sms_time.setText(list.get(position).get("add_time")
					.toString());

			return convertView;
		}

	}

	class ViewHolder {
		TextView s_sms_content;
		TextView s_sms_time;
		TextView s_sms_new;
	}


	
	private void getRefreshItem() {
		
		pagenum = 0;
		resultdata.clear();
		key_value.put("page", "0");

		final String url = IpConfig.getUri("getSystemMessage");
		
		setlistdata(url);

	}

	private void getLoadMoreItem() {
		pagenum++;
		Log.d("555555", pagenum + "");
		key_value.put("page", pagenum + "");
		final String url = IpConfig.getUri("getSystemMessage");

		setlistdata(url);
	}

	@Override
	public void onRefresh() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				s_m_xlist.stopLoadMore();
				getRefreshItem();

				adapter.notifyDataSetChanged();
				
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				s_m_xlist.stopRefresh();
				getLoadMoreItem();

				// adapter.notifyDataSetChanged();

				// mListView.stopLoadMore();

			}
		}, 2000);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 如果是返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
		
			Editor editor4 = sp4.edit();
			editor4.putInt("page", 0);
			editor4.commit();

			Intent intent = new Intent(SystemMessage2Activity.this,
					AllPageActivity.class);

			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			
		
	
			
		}
		return super.onKeyDown(keyCode, event);
	}
	private void onLoad() {

		s_m_xlist.stopRefresh();

		s_m_xlist.stopLoadMore();

		s_m_xlist.setRefreshTime("刚刚");

	}

}
