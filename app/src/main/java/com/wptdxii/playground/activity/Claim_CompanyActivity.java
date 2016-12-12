package com.wptdxii.playground.activity;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
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

public class Claim_CompanyActivity extends BaseActivity implements
		IXListViewListener {

	private MyAdapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();
	private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
	private Dialog dialog;
	private RelativeLayout s_m_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private int pagenum = 0;
	private Handler mHandler;
	private String companycode = "", phone = "", c_name = "";

	private String loginString;
	private XListView s_m_xlist;
	private TextView copmany_name, claim_phone;

	private RelativeLayout claim_service_rel2;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			resultdata.addAll(list);
			if (pagenum == 0) {

				adapter.setData(resultdata);
				s_m_xlist.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dialog.dismiss();

			} else {

				adapter.notifyDataSetChanged();
				s_m_xlist.stopLoadMore();

			}

		}

	};

	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");
			s_m_xlist.stopLoadMore();
			dialog.dismiss();
			Toast.makeText(Claim_CompanyActivity.this, errmsg,
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

				Toast.makeText(Claim_CompanyActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.claim_company);
		loginString = sp.getString("Login_STATE", "none");
		Intent intent = getIntent();

		companycode = intent.getStringExtra("companycode");
		phone = intent.getStringExtra("phone");
		c_name = intent.getStringExtra("companyname");
		init();
		initEvent();

	}

	private void init() {
		s_m_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("理赔服务");
		s_m_xlist = (XListView) findViewById(R.id.s_m_xlist);

		copmany_name = (TextView) findViewById(R.id.copmany_name);
		claim_phone = (TextView) findViewById(R.id.claim_phone);
		claim_service_rel2 = (RelativeLayout) findViewById(R.id.claim_service_rel2);
		copmany_name.setText(c_name);
		claim_phone.setText(phone);
		s_m_xlist.setPullLoadEnable(true);
		s_m_xlist.setXListViewListener(Claim_CompanyActivity.this);
		adapter = new MyAdapter(Claim_CompanyActivity.this);
		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");

		mHandler = new Handler();
	}

	private void initEvent() {

		key_value.put("companycode", companycode);
		key_value.put("page", "0");

		dialog.show();
		final String url = IpConfig.getUri("getpaymentListforCompany");
		setlistdata(url);

		claim_service_rel2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				
				if (phone.equals("")) {

				} else {
					Intent intent = new Intent();

					intent.setAction(Intent.ACTION_CALL);
					intent.setData(Uri.parse("tel:" + phone));
					startActivity(intent);
				}
				
			

			}
		});

		s_m_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				
				
				
				
				
				if (loginString.equals("none")) {

					Intent intent = new Intent();
					intent.setClass(Claim_CompanyActivity.this, LoginActivity.class);
					Claim_CompanyActivity.this.startActivity(intent);

				} else {

					int pos = position - 1;

					if (pos >= 0 && pos < adapter.list.size()) {

						Intent intent = new Intent();

						intent.putExtra("paymentid", adapter.list.get(pos)
								.get("id").toString());

						intent.setClass(Claim_CompanyActivity.this,
								ClaimsInstructionsActivity.class);

						Claim_CompanyActivity.this.startActivity(intent);

					}
				}
				
				
			
			}
		});

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
							JSONArray dataList = jsonObject
									.getJSONArray("data");
							for (int i = 0; i < dataList.length(); i++) {
								JSONObject jsonObject2 = dataList
										.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
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
				convertView = layoutInflater.inflate(
						R.layout.claim_company_item, null);

				holder.title = (TextView) convertView.findViewById(R.id.title);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			// setText(list.get(position).get("title").toString());
			holder.title.setText(list.get(position).get("title").toString());

			return convertView;
		}

	}

	class ViewHolder {
		TextView title;

	}


	private void getRefreshItem() {

		pagenum = 0;
		resultdata.clear();
		key_value.put("page", "0");

		final String url = IpConfig.getUri("getpaymentListforCompany");

		setlistdata(url);

	}

	private void getLoadMoreItem() {
		pagenum++;
		Log.d("555555", pagenum + "");
		key_value.put("page", pagenum + "");
		final String url = IpConfig.getUri("getpaymentListforCompany");

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

	@Override
	public void onRestart() {
		super.onRestart();


		loginString = sp.getString("Login_STATE", "none");
	
	}

	private void onLoad() {

		s_m_xlist.stopRefresh();

		s_m_xlist.stopLoadMore();

		s_m_xlist.setRefreshTime("刚刚");

	}

}
