package com.cloudhome.activity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.SceneProductsAdapter;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
public class InsuranceShopSceneActivity extends BaseActivity implements
		IXListViewListener {

	private String title = "";
	private String id = "";
	private Map<String, String> key_value = new HashMap<String, String>();
	private Map<String, String> key_value2 = new HashMap<String, String>();
	private List<Map<String, String>> total_data = new ArrayList<Map<String, String>>();
	private Dialog dialog;
	private ImageView iv_back;
	private XListView insurance_shop_xlist;
	private Handler mHandler;
	private int pagenum = 0;
	private SceneProductsAdapter is_adapter;

	private String user_id;
	private String token;
	private String cert_a;
	private String cert_b;
	private String licence;



	static public Boolean promotion_expenses_show = true;
	private Boolean Jumplogin = false;
	private PopupWindow popupWindow;
	private LinearLayout layout;
	private RelativeLayout promotion_expenses_off;
	private RelativeLayout promotion_expenses_on;

	private View p_view;

	private String loginString, type = "",	user_state;
	private ImageView iv_right;
	private TextView tv_text;
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

				Toast.makeText(InsuranceShopSceneActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}

		}

	};

	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");
			insurance_shop_xlist.stopLoadMore();
			dialog.dismiss();
			Toast.makeText(InsuranceShopSceneActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

			dialog.dismiss();
			if (pagenum == 0) {

				if (total_data.size() < 1) {

					total_data.addAll(list);
					is_adapter.setData(total_data);
					insurance_shop_xlist.setAdapter(is_adapter);
					is_adapter.notifyDataSetChanged();

				}
				insurance_shop_xlist.stopLoadMore();

				if (list.size() < 1) {

					Toast.makeText(InsuranceShopSceneActivity.this,
							"没有找到订单信息", Toast.LENGTH_SHORT).show();

				}
			} else if (list.size() < 1) {

				Toast.makeText(InsuranceShopSceneActivity.this, "没有找到订单信息",
						Toast.LENGTH_SHORT).show();
				insurance_shop_xlist.stopLoadMore();
			} else {

				total_data.addAll(list);
				is_adapter.notifyDataSetChanged();
				insurance_shop_xlist.stopLoadMore();


			}

		}

	};
	public static InsuranceShopSceneActivity InsuranceShopSceneinstance=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scene_products_list);

		Intent intent = getIntent();
		title = intent.getStringExtra("banner_title");
		id = intent.getStringExtra("bannerid");


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		type = sp.getString("Login_TYPE", "none");
	    user_state = sp.getString("Login_CERT", "none");
		loginString = sp.getString("Login_STATE", "none");

		cert_a = sp.getString("cert_a", "");
		cert_b = sp.getString("cert_b", "");
		licence = sp.getString("licence", "");


		InsuranceShopSceneinstance = this;
		init();
		initevent();
	}

	private void init() {

		is_adapter = new SceneProductsAdapter(InsuranceShopSceneActivity.this);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_text = (TextView) findViewById(R.id.tv_text);

		iv_right = (ImageView) findViewById(R.id.iv_right);
		p_view = findViewById(R.id.p_view);
		insurance_shop_xlist = (XListView) findViewById(R.id.insurance_shop_xlist);

		insurance_shop_xlist.setPullLoadEnable(true);
		insurance_shop_xlist
				.setXListViewListener(InsuranceShopSceneActivity.this);

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

	private void initevent() {

		
		
		if (loginString.equals("none") || type.equals("02")
				|| !user_state.equals("00")) {
 
			promotion_expenses_show = false;
			iv_right.setVisibility(View.GONE);
		} else {
			if(cert_a.equals("")&&cert_b.equals("")&&licence.equals(""))
			{
				promotion_expenses_show = false;
				iv_right.setVisibility(View.GONE);

			}else {
				promotion_expenses_show = true;
				iv_right.setVisibility(View.VISIBLE);
			}
		}
		
		tv_text.setText(title);
		key_value.put("token", token);
		key_value.put("user_id", user_id);

		String query_param = Json_Value(id,"0");

		key_value.put("query_param", query_param);

		final String url = IpConfig.getUri("getmall_scene_products");

		dialog.show();
		setxlistdata(url);

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

		iv_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showPopupWindow();
			}
		});

		insurance_shop_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				int pos = position - 1;

				if (pos >= 0 && pos < total_data.size()) {

					String Visit_url = IpConfig.getUri("getmall_product_visit");

					String query_param = Json_Visit(total_data.get(0).get("id"));
					
					if (!loginString.equals("none")) {
					key_value2.put("user_id", user_id);
					key_value2.put("token", token);
					}
					key_value2.put("query_param", query_param);
					
//					key_value2.put("id", total_data.get(pos).get("product_id")
//							.toString());
					setVisitData(Visit_url);

					
					
					if(total_data.get(pos).get("is_url").equals("0")){
						
						
						
					
							Jumplogin =true;
							Intent intent = new Intent();
							
							intent.putExtra("product_id", total_data.get(pos).get("product_id"));
				
							intent.setClass(InsuranceShopSceneActivity.this,
									RecProductDetailActivity.class);
							InsuranceShopSceneActivity.this.startActivity(intent);
							
						
						
				
						
					
							
					}else{
						
						if (loginString.equals("none")) {
							Jumplogin =true;
							Intent intent = new Intent();
							intent.setClass(InsuranceShopSceneActivity.this,
									LoginActivity.class);
							InsuranceShopSceneActivity.this.startActivity(intent);
						} else {
						   String strUTF8="";
					        try {  
					      
					             strUTF8 = URLEncoder.encode(total_data.get(pos).get("img_url"), "UTF-8");
					        } catch (UnsupportedEncodingException e) {  
					            e.printStackTrace();  
					        }
					        
						Intent intent = new Intent();

						intent.putExtra("title", total_data.get(pos).get("name"));
						intent.putExtra("url", total_data.get(pos).get("url"));

						intent.putExtra("biref",
								total_data.get(pos).get("feature_desc"));
						intent.putExtra("img",IpConfig.getIp3() + "/" +strUTF8);

						intent.setClass(InsuranceShopSceneActivity.this,
								InsuranceShopWebActivity.class);
						InsuranceShopSceneActivity.this.startActivity(intent);
					}
					
					}

				}

			}
		});

	}

	private void showPopupWindow() {

		layout = (LinearLayout) LayoutInflater.from(
				InsuranceShopSceneActivity.this).inflate(
				R.layout.scene_products_eye_popwindow, null);

		promotion_expenses_on = (RelativeLayout) layout
				.findViewById(R.id.promotion_expenses_on);
		promotion_expenses_off = (RelativeLayout) layout
				.findViewById(R.id.promotion_expenses_off);

		popupWindow = new PopupWindow(InsuranceShopSceneActivity.this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(getResources().getDimensionPixelSize(
				R.dimen.SceneProducts_p_width));
		popupWindow.setHeight(getResources().getDimensionPixelSize(
				R.dimen.SceneProducts_p_height));

		popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		popupWindow.setTouchable(true); // 设置PopupWindow可触摸
		popupWindow.setOutsideTouchable(false); // 设置非PopupWindow区域可触摸
		// popupWindow.setAnimationStyle(R.anim.popanim);

		popupWindow.setContentView(layout);

		// popupWindow.showAsDropDown(bt_order);
		popupWindow.showAsDropDown(p_view);
		//
		// popupWindow.showAtLocation(p_view,Gravity.NO_GRAVITY, 10,10);
		//
		//

		promotion_expenses_on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				promotion_expenses_show = true;
				is_adapter.notifyDataSetChanged();
				popupWindow.dismiss();
				popupWindow = null;
			}
		});

		promotion_expenses_off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				promotion_expenses_show = false;
				is_adapter.notifyDataSetChanged();
				popupWindow.dismiss();
				popupWindow = null;
			}
		});

	}

	private void getRefreshItem() {

		total_data.clear();
		pagenum = 0;
		
		String query_param = Json_Value(id,"0");

		key_value.put("query_param", query_param);

		
		final String url = IpConfig.getUri("getmall_scene_products");

		setxlistdata(url);
	}

	private void getLoadMoreItem() {
		pagenum++;
	

		String query_param = Json_Value(id,pagenum+"");

		key_value.put("query_param", query_param);

		final String url = IpConfig.getUri("getmall_scene_products");

		setxlistdata(url);
	}

	@Override
	public void onRefresh() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				insurance_shop_xlist.stopLoadMore();

				getRefreshItem();

				is_adapter.notifyDataSetChanged();

				onLoad();
			}
		}, 2000);

	}

	@Override
	public void onLoadMore() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				insurance_shop_xlist.stopRefresh();
				getLoadMoreItem();

				// adapter.notifyDataSetChanged();

				// mListView.stopLoadMore();

			}
		}, 2000);
	}

	private void onLoad() {

		insurance_shop_xlist.stopRefresh();

		insurance_shop_xlist.stopLoadMore();

		insurance_shop_xlist.setRefreshTime("刚刚");

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
				Log.d("onSuccess", "in_shopScene = " + jsonString);
				List<Map<String, String>> info_list = new ArrayList<Map<String, String>>();

				Map<String, String> errcode_map = new HashMap<String, String>();
				Map<String, Object> total_map = new HashMap<String, Object>();
				ArrayList<ArrayList<String>> itemposlists = new ArrayList<ArrayList<String>>();

				try {

					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {

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

							JSONArray dataArray = jsonObject
									.getJSONArray("data");
							for (int i = 0; i < dataArray.length(); i++) {

								JSONObject obj = (JSONObject) dataArray
										.get(i);
								Map<String, String> map = new HashMap<String, String>();
								Iterator<String> iterator = obj.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									String value = obj.getString(key);
									map.put(key, value);
								}
								info_list.add(map);

							}

							Message message = Message.obtain();

							message.obj = info_list;
							handler.sendMessage(message);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	
	}

	private String Json_Value(String id, String page) {
		String jsonresult = "";// 定义返回字符串

		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", id);
			jsonObj.put("page", page);
			jsonresult = jsonObj.toString();
			Log.i("JSON_______________", jsonresult);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}

	private void setVisitData(String url) {

		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value2)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {

				Log.e("error", "获取数据异常 ", e);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;

				Log.d("onSuccess", "onSuccess json = " + jsonString);

			}
		});
		


	}

	
	@Override
	protected void onRestart() {
		super.onRestart();

		type = sp.getString("Login_TYPE", "none");
		user_state = sp.getString("Login_CERT", "none");

		String loginStringOld = loginString;

		loginString = sp.getString("Login_STATE", "none");
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		if (loginString.equals("none") || type.equals("02")
				|| !user_state.equals("00")) {

			promotion_expenses_show = false;
			iv_right.setVisibility(View.GONE);
		} else {
			promotion_expenses_show = true;
			iv_right.setVisibility(View.VISIBLE);
		}

		if (Jumplogin && !loginString.equals("none")&&!loginString.equals(loginStringOld)) {

			if (loginString.equals("none") || type.equals("02")
					|| !user_state.equals("00")) {
	 
				promotion_expenses_show = false;
				iv_right.setVisibility(View.GONE);
			} else {
				promotion_expenses_show = true;
				iv_right.setVisibility(View.VISIBLE);
			}
			pagenum = 0;
	
			key_value.put("token", token);
			key_value.put("user_id", user_id);

			String query_param = Json_Value(id,"0");

			key_value.put("query_param", query_param);

			final String url = IpConfig.getUri("getmall_scene_products");

			dialog.show();

			setxlistdata(url);

			Jumplogin = false;
		}
	}
	
	private String Json_Visit(String id) {
		String jsonresult = "";// 定义返回字符串

		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", id);
		
			
			jsonresult = jsonObj.toString();
			Log.i("JSON_______________", jsonresult);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}


}
