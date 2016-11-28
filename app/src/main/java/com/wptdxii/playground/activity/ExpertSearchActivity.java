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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

public class ExpertSearchActivity extends BaseActivity implements
		IXListViewListener {

	private MyAdapter adapter;

	private Map<String, String> key_value = new HashMap<String, String>();
	private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
	private Dialog dialog;
	private XListView mListView;
	private ImageView more_back;
	private int pagenum = 1;
	private Handler mHandler;
	private String user_id;
	private String name;

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			resultdata.addAll(list);

			if (pagenum == 1) {
				adapter.setData(resultdata);
				mListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			} else {
				adapter.notifyDataSetChanged();
				mListView.stopLoadMore();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.expert_search);

		user_id = sp.getString("Login_UID", "");
		Intent intent = getIntent();
		name = intent.getStringExtra("name");

		key_value.put("name", name);
		init();
		initEvent();

	}

	void init() {
		more_back = (ImageView) findViewById(R.id.more_back);

		mListView = (XListView) findViewById(R.id.more_expert_xlist);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(ExpertSearchActivity.this);
		
		mHandler = new Handler();
		
		adapter = new MyAdapter(ExpertSearchActivity.this);
		  dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("卖力加载中...");
          
	

	}

	void initEvent() {

		key_value.put("orderType", "01");
		key_value.put("page", "1");

		final String PRODUCT_URL = IpConfig.getUri("getSearchExpertList");
		// new MyTask().execute(PRODUCT_URL);
		setRadio(PRODUCT_URL);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {

				int pos = position - 1;
				if (pos >= 0 && pos < adapter.list.size()) {

					if (adapter.list.get(pos).get("id").toString()
							.equals(user_id)) {

						Intent intent = new Intent();
						intent.setClass(ExpertSearchActivity.this,
								MyMicroActivity.class);
						ExpertSearchActivity.this.startActivity(intent);
					} else {
						Intent intent = new Intent();

						intent.putExtra("id", adapter.list.get(pos).get("id")
								.toString());
						intent.putExtra("avatar",
								adapter.list.get(pos).get("avatar").toString());
						intent.putExtra("user_name",
								adapter.list.get(pos).get("user_name")
										.toString());
						intent.putExtra("company_name", adapter.list.get(pos)
								.get("company_name").toString());
						intent.putExtra("mobile_area", adapter.list.get(pos)
								.get("mobile_area").toString());
						intent.putExtra("good_count", adapter.list.get(pos)
								.get("good_count").toString());
						intent.putExtra("cert_a",
								adapter.list.get(pos).get("cert_a").toString());
						intent.putExtra("cert_b",
								adapter.list.get(pos).get("cert_b").toString());
						intent.putExtra("licence",
								adapter.list.get(pos).get("licence").toString());

						intent.putExtra("mobile",
								adapter.list.get(pos).get("mobile").toString());

						intent.putExtra("cert_num_isShowFlg",
								adapter.list.get(pos).get("cert_num_isShowFlg")
										.toString());

						intent.putExtra("mobile_num_short",
								adapter.list.get(pos).get("mobile_num_short")
										.toString());

						intent.putExtra("state",
								adapter.list.get(pos).get("state").toString());

						intent.putExtra("personal_specialty",
								adapter.list.get(pos).get("personal_specialty")
										.toString());
						intent.putExtra("personal_context",
								adapter.list.get(pos).get("personal_context")
										.toString());

						intent.setClass(ExpertSearchActivity.this,
								ExpertMicroActivity.class);

						ExpertSearchActivity.this.startActivity(intent);
					}
				}
			}
		});

		more_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void setRadio(String url) {

		if (pagenum == 1) {
			dialog.show();
		}
		
		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e) {
			
				Log.e("error", "获取数据异常 ", e);
				
			}

			@Override
			public void onResponse(String response) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {

					Log.d("44444", jsonString);
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

				} catch (Exception e) {
					e.printStackTrace();
				}
			

			}
		});
		
	
	}

	private void getRefreshItem() {
		Log.d("444444", pagenum + "");
		pagenum = 1;
		resultdata.clear();

		key_value.put("page", "1");

		final String PRODUCT_URL;

		PRODUCT_URL = IpConfig.getUri("getSearchExpertList");

		// new MyTask().execute(PRODUCT_URL);
		setRadio(PRODUCT_URL);

	}

	private void getLoadMoreItem() {
		pagenum++;
		Log.d("555555", pagenum + "");

		key_value.put("page", pagenum + "");
		final String PRODUCT_URL;

		PRODUCT_URL = IpConfig.getUri("getSearchExpertList");

		// new MyTask().execute(PRODUCT_URL);
		setRadio(PRODUCT_URL);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {

				getRefreshItem();

				adapter.notifyDataSetChanged();
				mListView.stopLoadMore();
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {

				getLoadMoreItem();

				adapter.notifyDataSetChanged();

				mListView.stopLoadMore();

			}
		}, 2000);
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
			View view = null;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.listview_item, null);
			} else {
				view = convertView;
			}

			TextView username = (TextView) view
					.findViewById(R.id.pro_user_name);
			TextView company_name = (TextView) view
					.findViewById(R.id.pro_company_name);
			TextView mobile_area = (TextView) view
					.findViewById(R.id.pro_mobile_area);
			TextView like_num = (TextView) view.findViewById(R.id.pro_like_num);

			username.setText(list.get(position).get("user_name").toString());
			company_name.setText(list.get(position).get("company_name")
					.toString()
					+ "|");
			mobile_area.setText(list.get(position).get("mobile_area")
					.toString());
			like_num.setText(list.get(position).get("good_count").toString()
					+ "赞");

			// 如何加载图片
			final ImageView imageView = (ImageView) view
					.findViewById(R.id.q_image);
			String icon_url = list.get(position).get("avatar").toString();
			if (!(icon_url == null) && !(icon_url.length() < 5)) {


				Glide.with(ExpertSearchActivity.this)
						.load(icon_url)
						.placeholder(R.drawable.white)  //占位图 图片正在加载
						.into(imageView);

			}

			return view;
		}

	}

	private void onLoad() {

		mListView.stopRefresh();

		mListView.stopLoadMore();

		mListView.setRefreshTime("刚刚");

	}


}
