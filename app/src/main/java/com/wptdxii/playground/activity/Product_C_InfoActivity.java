package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.gghl.view.wheelcity.AddressData;
import com.gghl.view.wheelcity.OnWheelChangedListener;
import com.gghl.view.wheelcity.WheelView;
import com.gghl.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.gghl.view.wheelcity.adapters.ArrayWheelAdapter;
import com.zf.iosdialog.widget.MyAlertDialog;
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

public class Product_C_InfoActivity extends BaseActivity {

	private ListView pro_c_list;

	private String[] search_item;

	private String[] search_channel;

	private MyAdapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();

	private ImageView p_c_back;
	private String cid;

	private TextView info_name;

	private String citytext;

	private TextView info_city;
	private TextView info_channels;
	private TextView info_time;
	private TextView city_change;
	private TextView source_change;
	private String user_id;
	private String token;
	private RelativeLayout top_rel;
	private RelativeLayout c_info_rel;
	private View popview;

	private LinearLayout layout;
	private ListView listViewSpinner;
	private PopupWindow popupWindow;

	private String areaing = "";

	private boolean hasMeasured = false;
	private boolean hasMeasured2 = false;
	private Dialog dialog;
	String channelcode = "";
	String city = "";
	private String type="";
	// 新加的listview底部说明

	private String show_content = "";
	private String show_flag = "";
	private String show_title = "";

	@SuppressLint("SimpleDateFormat")
	private
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressLint("HandlerLeak")
	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;

			String status = data;
			dialog.dismiss();
			// String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {

				Toast.makeText(Product_C_InfoActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler init_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String[]> data = (Map<String, String[]>) msg.obj;

			search_channel = data.get("code");
			search_item = data.get("name");

			source_change.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (search_channel.length > 1) {
						showPopupWindow();
					}
				}
			});

			// Companylist=data;
			// companyname_rel.setO
			// showPopupWindow();
		}
	};

	private Handler nodata_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");
			dialog.dismiss();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			adapter.setData(list);
			pro_c_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();

			CustomDialog.Builder builder = new CustomDialog.Builder(
					Product_C_InfoActivity.this);

			builder.setTitle("提示");
			builder.setMessage(errmsg);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			builder.create().show();
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			adapter.setData(list);
			pro_c_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			dialog.dismiss();
			if ("1".equals(show_flag)) {
				View view = LayoutInflater.from(Product_C_InfoActivity.this)
						.inflate(R.layout.footer_info_new, null);
				TextView tv_info_title = (TextView) view
						.findViewById(R.id.tv_info_title);
				TextView tv_info_content = (TextView) view
						.findViewById(R.id.tv_info_content);
				tv_info_title.setText(show_title);
				tv_info_content.setText(show_content);
				pro_c_list.addFooterView(view);
			}
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler change_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			adapter.setData(list);
			pro_c_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			dialog.dismiss();

		}

	};

	private Handler text_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String name = data.get("name");
			String code = data.get("code");
			String city = data.get("city");

			String channelcode = data.get("channelcode");

			String channelname = data.get("channelname");

			Log.i("-----------------------------", name + code + city
					+ channelcode + channelname + user_id + cid);

			key_value.put("channel", channelcode);

			Log.d("channel", channelcode);
			info_name.setText(name);

			info_channels.setText(channelname);

			info_city.setText(city);

			key_value.put("city", city);
			top_rel.setBackgroundResource(0);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			top_rel.setLayoutParams(layoutParams);

			ViewTreeObserver vto2 = top_rel.getViewTreeObserver();

			vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					if (!hasMeasured2) {

						int height = top_rel.getMeasuredHeight();
						int width = top_rel.getMeasuredWidth();
						// 获取到宽度和高度后，可用于计算

						top_rel.setLayoutParams(new RelativeLayout.LayoutParams(
								width, height + 15));
						top_rel.setBackgroundResource(R.drawable.p_c_info_background);
						hasMeasured2 = true;

					}
					return true;
				}
			});
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pro_c_info);



		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		Intent intent = getIntent();
		cid = intent.getStringExtra("cid");
		type = intent.getStringExtra("type");

		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("卖力加载中...");

		Log.d("cid", cid);
		init();
		initEvent();

	}

	private void init() {

		p_c_back = (ImageView) findViewById(R.id.p_c_back);

		info_name = (TextView) findViewById(R.id.info_name);

		source_change = (TextView) findViewById(R.id.source_change);
		info_time = (TextView) findViewById(R.id.info_time);
		// info_time.setText(dateFormat.format(new
		// Date(System.currentTimeMillis())));

		info_time.setText(dateFormat.format(new Date()));
		info_city = (TextView) findViewById(R.id.info_city);

		info_channels = (TextView) findViewById(R.id.info_channels);

		city_change = (TextView) findViewById(R.id.city_change);

		pro_c_list = (ListView) findViewById(R.id.pro_c_list);
		top_rel = (RelativeLayout) findViewById(R.id.top_rel);
		c_info_rel = (RelativeLayout) findViewById(R.id.c_info_rel);
		popview = findViewById(R.id.popview);

		adapter = new MyAdapter(Product_C_InfoActivity.this);
	}

	@SuppressLint("InflateParams")
	private View dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_cities_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_country);
		country.setVisibleItems(1);
		country.setViewAdapter(new CountryAdapter(this));

		final String cities[][] = AddressData.CITIES;
		final WheelView city = (WheelView) contentView
				.findViewById(R.id.wheelcity_city);
		city.setVisibleItems(1);

		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateCities(city, cities, newValue);
				// areaing = AddressData.PROVINCES[country.getCurrentItem()]
				// + AddressData.CITIES[country.getCurrentItem()][city
				// .getCurrentItem()];

				String provinces = AddressData.PROVINCES[country
						.getCurrentItem()];
				String citys = AddressData.CITIES[country.getCurrentItem()][city
						.getCurrentItem()];
				if (provinces.equals("北京") | provinces.equals("上海")
						| provinces.equals("天津") | provinces.equals("重庆")) {
					citytext = provinces;
					areaing = provinces;
				} else {
					citytext = citys;
					areaing = citys;
				}
				// areaing = provinces+"|"+citys;
			}
		});

		city.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				// areaing = AddressData.PROVINCES[country.getCurrentItem()]
				// + AddressData.CITIES[country.getCurrentItem()][city
				// .getCurrentItem()];

				String provinces = AddressData.PROVINCES[country
						.getCurrentItem()];
				String citys = AddressData.CITIES[country.getCurrentItem()][city
						.getCurrentItem()];
				if (provinces.equals("北京") | provinces.equals("上海")
						| provinces.equals("天津") | provinces.equals("重庆")) {
					citytext = provinces;
					areaing = provinces;
				} else {
					citytext = citys;
					areaing = citys;
				}
				// areaing = provinces+"|"+citys;

			}
		});

		country.setCurrentItem(0);
		city.setCurrentItem(0);
		updateCities(city, cities, 0);

		String provinces = AddressData.PROVINCES[country.getCurrentItem()];
		String citys = AddressData.CITIES[country.getCurrentItem()][city
				.getCurrentItem()];
		if (provinces.equals("北京") | provinces.equals("上海")
				| provinces.equals("天津") | provinces.equals("重庆")) {
			citytext = provinces;
			areaing = provinces;
		} else {
			citytext = citys;
			areaing = citys;
		}
		// areaing = provinces+"|"+citys;

		// areaing = AddressData.PROVINCES[country.getCurrentItem()]
		// + AddressData.CITIES[country.getCurrentItem()][city
		// .getCurrentItem()];
		// + AddressData.CITIES[country.getCurrentItem()][city
		// .getCurrentItem()];
		return contentView;
	}

	/**
	 * Updates the city wheel
	 */
	private void updateCities(WheelView city, String cities[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				cities[index]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(0);
	}

	/**
	 * Adapter for countries
	 */
	private class CountryAdapter extends AbstractWheelTextAdapter {
		// Countries names
		private String countries[] = AddressData.PROVINCES;

		/**
		 * Constructor
		 */
		CountryAdapter(Context context) {
			super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

	private void showPopupWindow() {
		layout = (LinearLayout) LayoutInflater
				.from(Product_C_InfoActivity.this).inflate(R.layout.dialog,
						null);
		listViewSpinner = (ListView) layout.findViewById(R.id.lv_dialog);
		listViewSpinner.setVerticalScrollBarEnabled(false);
		listViewSpinner.setAdapter(new ArrayAdapter<String>(
				Product_C_InfoActivity.this, R.layout.text, R.id.tv_text,
				search_item));

		popupWindow = new PopupWindow(Product_C_InfoActivity.this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(getResources().getDimensionPixelSize(

		R.dimen.comission_popup_width));
		popupWindow.setHeight(getResources().getDimensionPixelSize(
				R.dimen.comission_popup_height));

		popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		popupWindow.setTouchable(true); // 设置PopupWindow可触摸
		popupWindow.setOutsideTouchable(false); // 设置非PopupWindow区域可触摸
		// popupWindow.setAnimationStyle(R.anim.popanim);

		popupWindow.setContentView(layout);

		// popupWindow.showAsDropDown(popview);
		popupWindow.showAsDropDown(popview, -getResources()
				.getDimensionPixelSize(

				R.dimen.comission_popup_width) / 2, 0);

		listViewSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {

				arg0.setVisibility(View.VISIBLE);
				info_channels.setText(search_item[pos]);

				// posi = pos;
				// key_value.put("company_code",code[pos]);
				Log.d("44444", search_channel[pos]);
				key_value.put("channel", search_channel[pos]);
				dialog.show();
				String url = IpConfig.getUri("getPaymentList");
				setchangedata(url);
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

	private void initEvent() {

		ViewTreeObserver vto = top_rel.getViewTreeObserver();

		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (!hasMeasured) {

					int height = top_rel.getMeasuredHeight();
					int width = top_rel.getMeasuredWidth();
					// 获取到宽度和高度后，可用于计算

					top_rel.setLayoutParams(new RelativeLayout.LayoutParams(
							width, height + 15));
					top_rel.setBackgroundResource(R.drawable.p_c_info_background);
					hasMeasured = true;

				}
				return true;
			}
		});

		key_value.put("user_id", user_id);
		key_value.put("token", token);
		key_value.put("type", type);
		Log.i("user_id--search", user_id);
		Log.i("token--search",token);
		Log.i("type--search",type);

		final String channel_URL = IpConfig.getUri("getChannelList");
		initchannel(channel_URL);

		key_value.put("cid", cid);
		Log.d("user_id", user_id);
		Log.d("token", token);

		p_c_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				finish();
			}
		});

		city_change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				View view = dialogm();
				final MyAlertDialog dialog1 = new MyAlertDialog(
						Product_C_InfoActivity.this)
						.builder()
						.setTitle("请选择地区")
						// .setMsg("")
						// .setEditText("111")
						.setView(view)
						.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						});
				dialog1.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {

						info_city.setText(citytext);

						Log.d("city", areaing);
						key_value.put("city", areaing);
						dialog.show();
						String url = IpConfig.getUri("getPaymentList");
						setchangedata(url);

					}
				});
				dialog1.show();
			}
		});
		dialog.show();
		String url = IpConfig.getUri("getCommissionDetailInfo");
		setdata(url);

	}

	private void initchannel(String url) {

		
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
			
				ArrayList<String> stringArrayList = new ArrayList<String>();
				ArrayList<String> stringArrayList1 = new ArrayList<String>();
				String[] channelcode;
				String[] channelname;
				Map<String, String[]> map = new HashMap<String, String[]>();
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				try {

					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {

						JSONObject jsonObject = new JSONObject(jsonString);
						JSONArray dataList = jsonObject
								.getJSONArray("data");

						for (int i = 0; i < dataList.length(); i++) {
							JSONObject jsonObject2 = dataList
									.getJSONObject(i);
							stringArrayList.add(jsonObject2
									.getString("code"));
							stringArrayList1.add(jsonObject2
									.getString("name"));

						}
						channelcode = stringArrayList
								.toArray(new String[stringArrayList.size()]);
						channelname = stringArrayList1
								.toArray(new String[stringArrayList1.size()]);
						map.put("code", channelcode);
						map.put("name", channelname);
						Message message = Message.obtain();

						message.obj = map;

						init_handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		});
		
		
	

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
				if (jsonString == null || jsonString.equals("")
						|| jsonString.equals("null")) {
					String status = "false";
					Message message = Message.obtain();

					message.obj = status;

					errcode_handler.sendMessage(message);
				} else {

					JSONObject jsonObject = new JSONObject(jsonString);

					String errcode = jsonObject.getString("errcode");

					String errmsg = jsonObject.getString("errmsg");

					if (errcode.equals("0")) {
						JSONObject contentObject = jsonObject
								.getJSONObject("show_content");
						show_content = contentObject
								.getString("content");
						show_flag = contentObject
								.getString("show_flag");
						show_title = contentObject.getString("title");

						JSONObject dataObject = jsonObject
								.getJSONObject("data");

						String name = dataObject.getString("name");
						String code = dataObject.getString("code");
						String city = dataObject.getString("city");

						JSONObject channelObject = dataObject
								.getJSONObject("channel");

						String channelcode = channelObject
								.getString("code");

						String channelname = channelObject
								.getString("name");

						Map<String, String> text_map = new HashMap<String, String>();

						text_map.put("name", name);
						text_map.put("code", code);
						text_map.put("city", city);
						text_map.put("channelcode", channelcode);
						text_map.put("channelname", channelname);

						Message message2 = Message.obtain();

						message2.obj = text_map;

						text_handler.sendMessage(message2);

						JSONArray payinfoList = dataObject
								.getJSONArray("payinfo");

						for (int i = 0; i < payinfoList.length(); i++) {
							JSONObject jsonObject2 = payinfoList
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
					} else {
						Map<String, String> map = new HashMap<String, String>();

						map.put("errmsg", errmsg);

						Message message = Message.obtain();

						message.obj = map;

						nodata_handler.sendMessage(message);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			}
		});
		
		
		
	}

	private void setchangedata(String url) {

		
		
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
					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {

						JSONObject jsonObject = new JSONObject(jsonString);

						String errcode = jsonObject.getString("errcode");

						String errmsg = jsonObject.getString("errmsg");

						if (errcode.equals("0")) {

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

							change_handler.sendMessage(message);

						} else {
							Map<String, String> map = new HashMap<String, String>();

							map.put("errmsg", errmsg);

							Message message = Message.obtain();

							message.obj = map;

							nodata_handler.sendMessage(message);
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
			View view = null;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.pro_c_list_item, null);
			} else {
				view = convertView;
			}

			TextView title_text1 = (TextView) view
					.findViewById(R.id.title_text1);

			TextView title_text2 = (TextView) view
					.findViewById(R.id.title_text2);
			TextView title_text2_bottom = (TextView) view
					.findViewById(R.id.title_text2_bottom);

			TextView title_text3 = (TextView) view
					.findViewById(R.id.title_text3);

			TextView title_text4 = (TextView) view
					.findViewById(R.id.title_text4);

			TextView title_text5 = (TextView) view
					.findViewById(R.id.title_text5);

			TextView title_text6 = (TextView) view
					.findViewById(R.id.title_text6);

			title_text1.setText(list.get(position).get("years").toString());

			title_text2.setText(list.get(position).get("charge_1").toString());
			String icon_flag = (String) list.get(position).get("icon_flag");
			if (icon_flag.equals("1")) {
				String text6 = (String) list.get(position).get("charge_6");
				title_text2_bottom.setVisibility(View.VISIBLE);
				title_text2_bottom.setText(text6);
			} else {
				title_text2_bottom.setVisibility(View.GONE);
			}

			title_text3.setText(list.get(position).get("charge_2").toString());

			title_text4.setText(list.get(position).get("charge_3").toString());

			title_text5.setText(list.get(position).get("charge_4").toString());

			title_text6.setText(list.get(position).get("charge_5").toString());

			return view;

		}

	}


}
