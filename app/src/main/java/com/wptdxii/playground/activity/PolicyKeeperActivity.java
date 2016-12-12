package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.cloudhome.view.sortlistview.SideBar;
import com.cloudhome.view.sortlistview.SideBar.OnLetterChangedListener;
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

public class PolicyKeeperActivity extends BaseActivity implements
		IXListViewListener {

	private ClearEditText policy_keeper_edit;
	private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
	private MyAdapter adapter;
	private XListAdapter xadapter;
	private RelativeLayout policy_keeper_back;
	private RelativeLayout rl_right;
	private TextView tv_text;

	
	Map<String, String> key_value = new HashMap<String, String>();
	
	
	private Dialog dialog;
	private String user_id;
	private String token;
	private TextView sidebar_dialog;
	private Handler mHandler;

	private XListView p_k_xlist;
	private ListView p_k_list;
	private int pagenum = 0;
	private SideBar sideBar;
	private Handler Xlist_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			Log.d("44444","888888");
			resultdata.addAll(list);
			if (pagenum == 0) {
				xadapter.setData(list);
				p_k_xlist.setAdapter(xadapter);
				xadapter.notifyDataSetChanged();
				dialog.dismiss();
			} else {
				xadapter.setData(resultdata);
				p_k_xlist.setAdapter(xadapter);
				xadapter.notifyDataSetChanged();
				p_k_xlist.stopLoadMore();
			}

		}

	};



	private Handler list_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;
			Log.d("44444","777");
			adapter.setData(list);
			p_k_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			dialog.dismiss();

		}

	};


	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");
			String errmsg = data.get("errmsg");
			Log.d("455454", "455445" + errcode);
			if (!errcode.equals("0")) {

				Toast.makeText(PolicyKeeperActivity.this,
						errmsg, Toast.LENGTH_SHORT).show();
				p_k_xlist.stopLoadMore();
				dialog.dismiss();
			}
		}

	};

	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");
		//	String errmsg = data.get("errmsg");
			Log.d("455454", "455445" + errcode);
			if (!errcode.equals("0")) {

			//	Toast.makeText(PolicyKeeperActivity.this,errmsg, Toast.LENGTH_SHORT).show();
				p_k_xlist.stopLoadMore();
				dialog.dismiss();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.policy_keeper);
        

		
		init();
		
		initEvent();
		
	}

	void init() {
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		sidebar_dialog = (TextView) findViewById(R.id.sidebar_dialog);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		policy_keeper_back  = (RelativeLayout) findViewById(R.id.iv_back);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("客户列表");
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		sideBar.setTextView(sidebar_dialog);
		p_k_list = (ListView) findViewById(R.id.p_k_list);
		p_k_xlist = (XListView) findViewById(R.id.p_k_xlist);
		p_k_xlist.setPullLoadEnable(true);
		p_k_xlist.setXListViewListener(PolicyKeeperActivity.this);
	    dialog = new Dialog(this,R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");
		adapter = new MyAdapter(PolicyKeeperActivity.this);
		
		xadapter = new XListAdapter(PolicyKeeperActivity.this);
		
		mHandler = new Handler();
		policy_keeper_edit = (ClearEditText) findViewById(R.id.policy_keeper_edit);
		
		policy_keeper_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

				///////// filterData(s.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();

				int len = s.toString().length();
				if (len >= 1) {
					p_k_list.setVisibility(View.VISIBLE);
					p_k_xlist.setPullLoadEnable(false);
					p_k_xlist.setVisibility(View.GONE);
					//resultdata.clear();
					
					key_value.put("name", text);
					
					final String PRODUCT_URL = IpConfig.getUri("searchCustomerWithNameInitial");
					
					setlistdata(PRODUCT_URL);
					
				} else {
					
				//	pagenum = 0;
					
					p_k_xlist.setPullLoadEnable(true);
					p_k_xlist.setVisibility(View.VISIBLE);
					p_k_list.setVisibility(View.GONE);
					
				//	resultdata.clear();
				//	key_value.put("page", "0");
				//	final String PRODUCT_URL = IpConfig.getUri("getCustomerList");
			    //  new MyTask().execute(PRODUCT_URL);
				//	setxlistdata(PRODUCT_URL);
					
				}

			}
		});
	}

	void initEvent() {

		key_value.put("token", token);
		key_value.put("user_id", user_id);
		key_value.put("page", "0");
		final String PRODUCT_URL = IpConfig.getUri("getCustomerList");
		// new MyTask().execute(PRODUCT_URL);
		setxlistdata(PRODUCT_URL);

		policy_keeper_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		policy_keeper_edit.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO 自动生成的方法存根
				String pro_sosuo = policy_keeper_edit.getText().toString();
				if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能
					// 先隐藏键盘
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(PolicyKeeperActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				//	resultdata.clear();
					key_value.put("name", pro_sosuo);
					final String PRODUCT_URL = IpConfig.getUri("searchCustomerWithNameInitial");
					setxlistdata(PRODUCT_URL);

				}
				return false;

			}
		});

		sideBar.setOnLetterChangedListener(new OnLetterChangedListener() {

			@Override
			public void onTouchLetterChanged(String s) {

				System.out.println("1111111111" + s);

				policy_keeper_edit.setText(s);
			}
		});
		p_k_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {

				Log.d("pos11111111", pos + "");
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// 设置传递的参数
				intent.putExtra("customer_id", adapter.list.get(pos).get("id")
						.toString());
				// 从Activity IntentTest跳转到Activity IntentTest01
				intent.setClass(PolicyKeeperActivity.this,
						CustomerInfoActivity.class);
//				intent.setClass(PolicyKeeperActivity.this,
//						CustomerPageActivity.class);
				// // 启动intent的Activity
				PolicyKeeperActivity.this.startActivity(intent);
			}
		});
		p_k_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				// TODO Auto-generated method stub
				int pos;
				pos=position-1;
				if(pos>=0&&pos<xadapter.list.size())
				{
				Intent intent = new Intent();

				Log.d("pos22222", pos + "");

				// 设置传递的参数
				intent.putExtra("customer_id",
						xadapter.list.get(pos).get("id").toString());
				// 从Activity IntentTest跳转到Activity IntentTest01
				intent.setClass(PolicyKeeperActivity.this,
						CustomerInfoActivity.class);
//					intent.setClass(PolicyKeeperActivity.this,
//							CustomerPageActivity.class);
				// // 启动intent的Activity
				PolicyKeeperActivity.this.startActivity(intent);
				}
			}
		});
	}

	private void setxlistdata(String url) {

		if (pagenum == 0) {
			dialog.show();
		}
		
		
		OkHttpUtils.post()//
		.url(url)//

		.params(key_value)//
		.build()//

		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);
				Toast.makeText(PolicyKeeperActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
				dialog.dismiss();

			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				Map<String, String> errcode_map = new HashMap<String, String>();
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {

					if (jsonString.equals("") || jsonString.equals("")
							|| jsonString.equals("null")) {

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
								Map<String, Object> map = new HashMap<String, Object>();
								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									if (key.equals("sex")) {
										JSONObject value = jsonObject2
												.getJSONObject("sex");
										Object code = value.get("code");
										// String data =
										// jsonObject.getString("data");

										map.put(key, code);
									} else {

										Object value = jsonObject2.get(key);
										map.put(key, value);
									}
								}
								list.add(map);
							}
							Message message = Message.obtain();

							message.obj = list;
							//							String sousustr = policy_keeper_edit.getText()
							//									.toString();
							//							if (sousustr == null || sousustr.equals("")
							//									|| sousustr.equals("null")) {
							Xlist_handler.sendMessage(message);
							//							} else {
							//								list_handler.sendMessage(message);
							//							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		});
		
		


	}

	private void setlistdata(String url) {

		if (pagenum == 0) {
			dialog.show();
		}
		
		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);
				Toast.makeText(PolicyKeeperActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				Map<String, String> errcode_map = new HashMap<String, String>();
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {

					if (jsonString.equals("") || jsonString.equals("")
							|| jsonString.equals("null")) {

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
								Map<String, Object> map = new HashMap<String, Object>();
								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									if (key.equals("sex")) {
										JSONObject value = jsonObject2
												.getJSONObject("sex");
										Object code = value.get("code");
										// String data =
										// jsonObject.getString("data");

										map.put(key, code);
									} else {

										Object value = jsonObject2.get(key);
										map.put(key, value);
									}
								}
								list.add(map);
							}
							Message message = Message.obtain();

							message.obj = list;
							//		String sousustr = policy_keeper_edit.getText()
							//				.toString();
							//	if (sousustr == null || sousustr.equals("")
							//			|| sousustr.equals("null")) {
							//		Xlist_handler.sendMessage(message);
							//	} else {
							list_handler.sendMessage(message);
							//		}
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
			View view;

			if (convertView == null) {
				view = layoutInflater
						.inflate(R.layout.policy_keeper_item, null);
			} else {
				view = convertView;
			}

			// TextView s_sms_name = (TextView)
			// view.findViewById(R.id.s_sms_name);
			TextView q_k_name = (TextView) view.findViewById(R.id.q_k_name);
			TextView p_k_sex = (TextView) view.findViewById(R.id.p_k_sex);
			TextView p_k_num = (TextView) view.findViewById(R.id.p_k_num);
			// setText(list.get(position).get("title").toString());

			q_k_name.setText(list.get(position).get("name").toString());

			String sex = list.get(position).get("sex").toString();
			if (sex == null || sex.equals("") || sex.equals("null")||sex.equals("00")) {
				sex = "不详";
			} else if (sex.equals("01")) {
				sex = "男士";
			} else if (sex.equals("02")) {
				sex = "女士";
			}
			p_k_sex.setText(sex);
			p_k_num.setText("保单"
					+ list.get(position).get("policy_count").toString());

			return view;
		}

	}

	
	public class  XListAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, Object>> list = null;

		public XListAdapter(Context context) {
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
			View view;

			if (convertView == null) {
				view = layoutInflater
						.inflate(R.layout.policy_keeper_item, null);
			} else {
				view = convertView;
			}

			// TextView s_sms_name = (TextView)
			// view.findViewById(R.id.s_sms_name);
			TextView q_k_name = (TextView) view.findViewById(R.id.q_k_name);
			TextView p_k_sex = (TextView) view.findViewById(R.id.p_k_sex);
			TextView p_k_num = (TextView) view.findViewById(R.id.p_k_num);
			// setText(list.get(position).get("title").toString());

			q_k_name.setText(list.get(position).get("name").toString());

			String sex = list.get(position).get("sex").toString();
			if (sex == null || sex.equals("") || sex.equals("null")||sex.equals("00")) {
				sex = "不详";
			} else if (sex.equals("01")) {
				sex = "男士";
			} else if (sex.equals("02")) {
				sex = "女士";
			}
			p_k_sex.setText(sex);
			p_k_num.setText("保单"
					+ list.get(position).get("policy_count").toString());

			return view;
		}

	}

	// private void setListview() {
	// pagenum = 0;
	// resultdata.clear();
	// // /////// key_value.clear();
	//
	// key_value.put("page", "0");
	//
	// final String PRODUCT_URL = IpConfig.getUri("getCustomerList");
	// // new MyTask().execute(PRODUCT_URL);
	// setxlistdata(PRODUCT_URL);
	// }

	private void getRefreshItem() {
		Log.d("444444", pagenum + "");
		pagenum = 0;
		resultdata.clear();

		key_value.put("page", "0");

		final String PRODUCT_URL;

		PRODUCT_URL = IpConfig.getUri("getCustomerList");

		// new MyTask().execute(PRODUCT_URL);
		setxlistdata(PRODUCT_URL);

	}

	private void getLoadMoreItem() {
		pagenum++;
		

		key_value.put("page", pagenum + "");
		final String PRODUCT_URL;

		PRODUCT_URL = IpConfig.getUri("getCustomerList");

		// new MyTask().execute(PRODUCT_URL);
		setxlistdata(PRODUCT_URL);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {

				
				getRefreshItem();

				xadapter.notifyDataSetChanged();
				p_k_xlist.stopLoadMore();
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

				// adapter.notifyDataSetChanged();

				// p_k_xlist.stopLoadMore();

			}
		}, 2000);
	}

	private void onLoad() {

		p_k_xlist.stopRefresh();

		p_k_xlist.stopLoadMore();

		p_k_xlist.setRefreshTime("刚刚");

	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
	}
	
	public  boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			//获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			return !(event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom);
		}
		return false;
	}


}
