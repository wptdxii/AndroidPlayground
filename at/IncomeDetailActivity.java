package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.IncomeDetailAdapter;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
public class IncomeDetailActivity extends BaseActivity implements
		IXListViewListener {

	private Map<String, String> key_value = new HashMap<String, String>();

	private String user_id;
	private String token;
	private int pagenum = 0;
	private ImageView back;

	private Handler mHandler;
	private XListView income_detail_xlist;
	private IncomeDetailAdapter incomedetailAdapter;
	
	private String startdate = "";
	private String enddate = "";
	
	private Dialog dialog;
	private String type = "";
	List<Map<String, String>> total_data = new ArrayList<Map<String, String>>();
	private TextView top_title,detail_title,num;
	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;

			dialog.dismiss();
			// String errcode = data;
			Log.d("455454", "455445" + data);
			if (data.equals("false")) {

				Toast.makeText(IncomeDetailActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
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
			Toast.makeText(IncomeDetailActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, Object> total_map = (Map<String, Object>) msg.obj;

			List<Map<String, String>> title_list = (List<Map<String, String>>) total_map.get("title_list");
			List<Map<String, String>> list= (List<Map<String, String>>) total_map.get("detail_list");
			

			
			top_title = (TextView) findViewById(R.id.rl_title_bar);
			detail_title = (TextView) findViewById(R.id.detail_title);
			num = (TextView) findViewById(R.id.num);
			
			top_title.setText(title_list.get(0).get("title"));
			detail_title.setText(title_list.get(0).get("name"));
			num.setText(title_list.get(0).get("price"));
			
		
			
			dialog.dismiss();
			total_data.addAll(list);

			if (pagenum == 0) {

				incomedetailAdapter.setData(total_data);
				income_detail_xlist.setAdapter(incomedetailAdapter);
				incomedetailAdapter.notifyDataSetChanged();

				if (list.size() < 1) {

					Toast.makeText(IncomeDetailActivity.this, "没有找到明细信息",
							Toast.LENGTH_SHORT).show();

				}
			} else if (list.size() < 1) {

				Toast.makeText(IncomeDetailActivity.this, "没有找到明细信息",
						Toast.LENGTH_SHORT).show();
				income_detail_xlist.stopLoadMore();
			} else {

				incomedetailAdapter.notifyDataSetChanged();
				income_detail_xlist.stopLoadMore();

			}
			

		

		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.income_detail);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		startdate = intent.getStringExtra("startdate");
		enddate = intent.getStringExtra("enddate");
		
		

		
		
		
		init();
		initEvent();

	}

	void init() {
		back = (ImageView) findViewById(R.id.back);
		income_detail_xlist = (XListView) findViewById(R.id.income_detail_xlist);
		income_detail_xlist.setPullLoadEnable(true);
		income_detail_xlist.setXListViewListener(IncomeDetailActivity.this);

		top_title = (TextView) findViewById(R.id.rl_title_bar);
		detail_title = (TextView) findViewById(R.id.detail_title);
		num = (TextView) findViewById(R.id.num);

		incomedetailAdapter = new IncomeDetailAdapter(this);
		
		mHandler = new Handler();
		
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

		
	
		
		String query_param = Json_Value(type, startdate, enddate, "0");

		key_value.put("query_param", query_param);
		key_value.put("user_id", user_id);
		key_value.put("token", token);

		dialog.show();

		final String url = IpConfig.getUri("getsettle_income_detail");
		setxlistdata(url);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});

	}

	private void setxlistdata(String url) {

		
		
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


				List<Map<String, String>> title_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> detail_list = new ArrayList<Map<String, String>>();


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


							JSONArray titlelist = dataObject
									.getJSONArray("title");



							String titlevalue1 = titlelist.getString(0);
							String titlevalue2 = titlelist.getString(1);
							String titlevalue3 = titlelist.getString(2);

							Map<String, String> map2 = new HashMap<String, String>();

							map2.put("title", titlevalue1);
							map2.put("name", titlevalue2);
							map2.put("price", titlevalue3);
							title_list.add(map2);

							JSONArray detailList = dataObject.getJSONArray("detail");

							for (int i = 0; i < detailList.length(); i++) {

								JSONArray dataList2 = detailList.getJSONArray(i);

								String value1 = dataList2.getString(0);
								String value2 = dataList2.getString(1);

								Map<String, String> map = new HashMap<String, String>();

								map.put("date", value1);
								map.put("price", value2);

								detail_list.add(map);
							}

							total_map.put("title_list", title_list);
							total_map.put("detail_list", detail_list);


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

	public String Json_Value(String type, String start, String end, String page) {
		String jsonresult = "";// 定义返回字符串

		try {
			JSONObject jsonObj = new JSONObject();// pet对象，json形式.

			jsonObj.put("type", type);// 向pet对象里面添加值
			jsonObj.put("start", start);
			jsonObj.put("end", end);
			jsonObj.put("page", page);

			jsonresult = jsonObj.toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}


	private void getRefreshItem() {

		total_data.clear();
		pagenum = 0;

		
		
		
		String query_param = Json_Value(type, startdate, enddate, "0");
		
		key_value.put("query_param", query_param);
		
		final String url = IpConfig.getUri("getsettle_income_detail");

		setxlistdata(url);
	}

	private void getLoadMoreItem() {
		
	
		
		
		pagenum++;
		Log.d("555555", pagenum + "");
	     String query_param = Json_Value(type, startdate, enddate, pagenum+"");
		
		key_value.put("query_param", query_param);

		final String url = IpConfig.getUri("getsettle_income_detail");

		setxlistdata(url);
	}

	@Override
	public void onRefresh() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				income_detail_xlist.stopLoadMore();

				getRefreshItem();

				incomedetailAdapter.notifyDataSetChanged();

				onLoad();
			}
		}, 2000);

	}

	@Override
	public void onLoadMore() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				income_detail_xlist.stopRefresh();
				getLoadMoreItem();

				// adapter.notifyDataSetChanged();

				// mListView.stopLoadMore();

			}
		}, 2000);
	}

	private void onLoad() {

		income_detail_xlist.stopRefresh();

		income_detail_xlist.stopLoadMore();

		income_detail_xlist.setRefreshTime("刚刚");

	}

}
