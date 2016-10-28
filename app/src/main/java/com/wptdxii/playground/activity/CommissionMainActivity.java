package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
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
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class CommissionMainActivity extends BaseActivity implements
		IXListViewListener {

	private Handler mHandler;
	private RelativeLayout main_rel;
	private XListView main_xlist;
	private MyAdapter adapter;
	private ImageView commission_back;
	private Map<String, String> key_value = new HashMap<String, String>();
	private TextView main_edit;
	private Dialog dialog;
	private String time = "";

	/** 透明动画 **/
	Animation mAnimation = null;

	private final String mPageName = "MainActivity";
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
	private RadioGroup rg_category_insurance;

	private String LIFE_INSURANCE_URL = IpConfig
			.getUri("getCompanyListForCommission") + "type=00";
	private String PROPERTY_INSURANCE_URL = IpConfig
			.getUri("getCompanyListForCommission") + "type=01";
	private String CAR_INSURANCE_URL = IpConfig
			.getUri("getCompanyListForCommission") + "type=05";
	private int insurance_category = 1;// 1表示是寿险，2表示是财险，3表示车险

	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;

			// String errcode = data;
			Log.d("455454", "455445" + (String) msg.obj);
			if (((String) msg.obj).equals("false")) {

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				adapter.setData(list);
				main_xlist.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
				Toast.makeText(CommissionMainActivity.this,
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
			main_xlist.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			dialog.dismiss();

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.commission_main);


		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("卖力加载中...");

		init();
		initEvent();

	}

	void init() {
		adapter = new MyAdapter(CommissionMainActivity.this);
		main_rel = (RelativeLayout) findViewById(R.id.main_rel);
		main_xlist = (XListView) findViewById(R.id.main_xlist);
		main_xlist.setPullLoadEnable(false);
		main_xlist.setPullRefreshEnable(true);
		main_xlist.setXListViewListener(CommissionMainActivity.this);
		mHandler = new Handler();

		main_edit = (TextView) findViewById(R.id.main_edit);
		commission_back = (ImageView) findViewById(R.id.commission_back);
		rg_category_insurance = (RadioGroup) findViewById(R.id.rg_category_insurance);
	}


	void initEvent() {

		commission_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		main_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.setClass(CommissionMainActivity.this,
						SearchProductActivity.class);
				CommissionMainActivity.this.startActivity(intent);
			}
		});

		main_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				// TODO Auto-generated method stub

				int pos;
				pos = position - 1;
				if (pos >= 0 && pos < adapter.list.size()) {
					Intent intent = new Intent();
					intent.putExtra("c_code", adapter.list.get(pos).get("code")
							.toString());
					if (insurance_category == 1) {
						intent.putExtra("type", "00");
					} else if (insurance_category == 2) {
						intent.putExtra("type", "01");
					} else {
						intent.putExtra("type", "05");
					}


					intent.setClass(CommissionMainActivity.this,
							Company_C_ListActivity.class);

					CommissionMainActivity.this.startActivity(intent);


				}
			}
		});

		rg_category_insurance
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup arg0, int checkedId) {
						// TODO Auto-generated method stub
						switch (checkedId) {
						case R.id.rb_life:
							insurance_category = 1;
							dialog.show();
							setdata(LIFE_INSURANCE_URL);
							break;
						case R.id.rb_property:
							insurance_category = 2;
							dialog.show();
							setdata(PROPERTY_INSURANCE_URL);
							break;
						case R.id.rb_car:
							insurance_category = 3;
							dialog.show();
							setdata(CAR_INSURANCE_URL);
							break;
						default:

							break;
						}
					}
				});
		dialog.show();
		setdata(LIFE_INSURANCE_URL);
	}

	private void setdata(String url) {

		
		
		
		OkHttpUtils.post()//
		.url(url)//
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
			View view = null;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.main_list_item, null);
			} else {
				view = convertView;
			}

			TextView company_name = (TextView) view
					.findViewById(R.id.company_name);

			company_name.setText(list.get(position).get("name").toString());

			return view;

		}

	}

	private void onLoad() {

		main_xlist.stopRefresh();

		if (time.equals("")) {

			main_xlist.setRefreshTime(" 刚刚 ");

			time = dateFormat.format(new Date());

		} else {

			main_xlist.setRefreshTime(" 今天 " + time);

		}

	}

	private void getRefreshItem() {
		// String PRODUCT_URL = IpConfig.getUri("getCompanyListForCommission");
		//
		// setdata(PRODUCT_URL);
		if (insurance_category == 1) {
			setdata(LIFE_INSURANCE_URL);
		} else if (insurance_category == 2) {
			setdata(PROPERTY_INSURANCE_URL);
		} else {
			setdata(CAR_INSURANCE_URL);
		}

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

		mHandler.postDelayed(new Runnable() {
			public void run() {

				getRefreshItem();

				adapter.notifyDataSetChanged();
				onLoad();

			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
}
