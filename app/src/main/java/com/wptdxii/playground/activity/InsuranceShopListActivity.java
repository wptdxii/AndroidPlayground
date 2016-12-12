package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.In_ShopListCompanyAdapter;
import com.cloudhome.adapter.In_ShopListCompanyAdapter.In_ShopListCompanyHolder;
import com.cloudhome.adapter.InsuranceShopListAdapter;
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

public class InsuranceShopListActivity extends BaseActivity implements
		IXListViewListener {

	private PopupWindow popupWindow;
	private View p_view;

	private PopupWindow pop_company;
	private RelativeLayout pop_company_layout;
	private GridView company_gd;
	private RelativeLayout cancel_rel, submit_rel;
	private In_ShopListCompanyAdapter companyadapter;
	private String companyCodeing = "";
	private String companyCode = "";

	private View select_popview;

	private Map<String, String> key_value2 = new HashMap<String, String>();

	private PopupWindow pop_order;
	private RelativeLayout pop_order_layout;
	private ListView pop_order_list;


	public static InsuranceShopListActivity InsuranceShopListinstance=null;

	private String orderCode = "";
	private String[] order = null;
	private Boolean Jumplogin = false;

	private String title = "";
	private String url = "";
	private Map<String, String> key_value = new HashMap<String, String>();
	private List<Map<String, String>> total_data = new ArrayList<Map<String, String>>();
	private Dialog dialog;
	private ImageView iv_back;
	private XListView insurance_shop_xlist;
	private Handler mHandler;
	private int pagenum = 0;
	private ImageView iv_right;
	private TextView tv_text;
	private InsuranceShopListAdapter is_adapter;

	private String user_id, loginString, type = "", user_state;
	private String token;

	private String cert_a;
	private String cert_b;
	private String licence;


	static public Boolean promotion_expenses_show = true;
	private List<Map<String, String>> company_list = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> order_list = new ArrayList<Map<String, String>>();

	private int CompanyWindownum = 0;
	private int OrderPopupWindownum = 0;

	private RelativeLayout top_title_bar;
	private RelativeLayout rel1, rel2;

	private boolean windowinit = false;
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

				Toast.makeText(InsuranceShopListActivity.this,
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
			Toast.makeText(InsuranceShopListActivity.this, errmsg,
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

					Toast.makeText(InsuranceShopListActivity.this, "没有找到产品信息",
							Toast.LENGTH_SHORT).show();

				}

			} else if (list.size() < 1) {

				Toast.makeText(InsuranceShopListActivity.this, "没有找到产品信息",
						Toast.LENGTH_SHORT).show();
				insurance_shop_xlist.stopLoadMore();
			} else {



				total_data.addAll(list);
				is_adapter.notifyDataSetChanged();
				insurance_shop_xlist.stopLoadMore();

			}

		}

	};

	@SuppressLint("HandlerLeak")
	private Handler select_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			Map<String, Object> map = (Map<String, Object>) msg.obj;

			company_list = (List<Map<String, String>>) map.get("company_list");
			order_list = (List<Map<String, String>>) map.get("order_list");

			if (order_list.size() > 0) {
				order = new String[order_list.size()];

				for (int i = 0; i < order_list.size(); i++) {
					order[i] = order_list.get(i).get("ordername");
				}

			}

			rel1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					OrderPopupWindownum = 1;
					if (CompanyWindownum > 0) {
						CompanyWindownum = 0;
						showInsuranceCompanyWindow();
					} else {
						if (company_list.size() > 0) {

							if (pop_company == null) {
								CompanyWindownum = 0;
								showInsuranceCompanyWindow();

							} else {
								CompanyWindownum++;

							}

						}
					}

				}
			});
			rel2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					CompanyWindownum = 1;
					if (OrderPopupWindownum > 0) {

						OrderPopupWindownum = 0;
						showOrderPopupWindow(order);
					} else {
						if (order_list.size() > 0) {

							if (pop_order == null) {
								OrderPopupWindownum = 0;
								showOrderPopupWindow(order);

							} else {
								OrderPopupWindownum++;

							}

						}
					}

				}
			});
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.insurance_shop_list);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("param");


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		type = sp.getString("Login_TYPE", "none");
		user_state = sp.getString("Login_CERT", "none");
		loginString = sp.getString("Login_STATE", "none");

		cert_a = sp.getString("cert_a", "");
		cert_b = sp.getString("cert_b", "");
		licence = sp.getString("licence", "");

		InsuranceShopListinstance = this;
		init();
		initevent();
	}

	private void init() {

		is_adapter = new InsuranceShopListAdapter(
				InsuranceShopListActivity.this);
		iv_back = (ImageView) findViewById(R.id.iv_back);

		iv_right = (ImageView) findViewById(R.id.iv_right);
		tv_text = (TextView) findViewById(R.id.tv_text);
		p_view = findViewById(R.id.p_view);
		select_popview = findViewById(R.id.select_popview);

		rel1 = (RelativeLayout) findViewById(R.id.rel1);
		rel2 = (RelativeLayout) findViewById(R.id.rel2);
		top_title_bar = (RelativeLayout) findViewById(R.id.top_title_bar);
		insurance_shop_xlist = (XListView) findViewById(R.id.insurance_shop_xlist);
		insurance_shop_xlist.setPullLoadEnable(true);
		insurance_shop_xlist
				.setXListViewListener(InsuranceShopListActivity.this);

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

		tv_text.setText(title);
		if (loginString.equals("none") || type.equals("02")
				|| !user_state.equals("00")) {


			promotion_expenses_show = false;
			iv_right.setVisibility(View.GONE);
		} else {

			if(cert_a.equals("")&&cert_b.equals("")&&licence.equals(""))
			{
				promotion_expenses_show = false;
				iv_right.setVisibility(View.GONE);

			}else{
				promotion_expenses_show = true;
				iv_right.setVisibility(View.VISIBLE);
			}

		}

		key_value.put("user_id", user_id);
		key_value.put("token", token);

		String query_param = Json_Value("", title, "", "0");

		key_value.put("query_param", query_param);

		final String url = IpConfig.getUri("getmall_pro_list_v1");

		dialog.show();
		setxlistdata(url);

		final String conditionurl = IpConfig.getUri("getmall_pro_query_cfg");

		setconditiondata(conditionurl);

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

		iv_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				CompanyWindownum = 1;
				OrderPopupWindownum = 1;

				showPopupWindow();

			}
		});

		top_title_bar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				CompanyWindownum++;
				OrderPopupWindownum++;

			}
		});

		insurance_shop_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				int pos = position - 1;

				if (pos >= 0 && pos < total_data.size()) {

					String Visit_url = IpConfig.getUri("getmall_product_visit");

					if (!loginString.equals("none")) {
						key_value2.put("user_id", user_id);
						key_value2.put("token", token);
					}

					String query_param = Json_Visit(total_data.get(0).get("id"));

					key_value2.put("query_param", query_param);
					setVisitData(Visit_url);

					if (total_data.get(pos).get("is_url")
							.equals("0")) {

						Jumplogin =true;
						Intent intent = new Intent();

						intent.putExtra("product_id",
								total_data.get(pos).get("product_id"));

						intent.setClass(InsuranceShopListActivity.this,
								RecProductDetailActivity.class);
						InsuranceShopListActivity.this.startActivity(intent);

					} else {

						if (loginString.equals("none")) {

							Jumplogin = true;
							Intent intent = new Intent();
							intent.setClass(InsuranceShopListActivity.this,
									LoginActivity.class);
							InsuranceShopListActivity.this
									.startActivity(intent);
						} else {
							String strUTF8 = "";
							try {

								strUTF8 = URLEncoder.encode(total_data.get(pos)
										.get("img_url"), "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

							Intent intent = new Intent();

							intent.putExtra("title",
									total_data.get(pos).get("name"));
							intent.putExtra("url",
									total_data.get(pos).get("url"));

							intent.putExtra("biref",
									total_data.get(pos).get("feature_desc"));

							intent.putExtra("img", IpConfig.getIp3() + "/" +strUTF8);

							// intent.putExtra("img",
							// total_data.get(pos).get("img_url").toString());

							intent.setClass(InsuranceShopListActivity.this,
									InsuranceShopWebActivity.class);
							InsuranceShopListActivity.this
									.startActivity(intent);

						}
					}
				}

			}
		});
	}

	private void showPopupWindow() {

		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				InsuranceShopListActivity.this).inflate(
				R.layout.scene_products_eye_popwindow, null);

		RelativeLayout promotion_expenses_on = (RelativeLayout) layout
				.findViewById(R.id.promotion_expenses_on);
		RelativeLayout promotion_expenses_off = (RelativeLayout) layout
				.findViewById(R.id.promotion_expenses_off);

		popupWindow = new PopupWindow(InsuranceShopListActivity.this);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		popupWindow.setWidth(getResources().getDimensionPixelSize(
				R.dimen.SceneProducts_p_width));
		popupWindow.setHeight(getResources().getDimensionPixelSize(
				R.dimen.SceneProducts_p_height));
		popupWindow.setTouchable(true); // 设置PopupWindow可触摸
		popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		// popupWindow.setAnimationStyle(R.anim.popanim);

		popupWindow.setFocusable(false);
		popupWindow.setContentView(layout);

		// popupWindow.showAsDropDown(bt_order);
		popupWindow.showAsDropDown(p_view);
		// popupWindow.showAtLocation(p_view,Gravity.NO_GRAVITY, 10,10);


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



	private void showInsuranceCompanyWindow() {

		if(!windowinit) {
			pop_company_layout = ((RelativeLayout) LayoutInflater.from(this)
					.inflate(R.layout.insurance_shop_company_pop, null));

			company_gd = (GridView) pop_company_layout
					.findViewById(R.id.company_gd);

			cancel_rel = (RelativeLayout) pop_company_layout
					.findViewById(R.id.cancel_rel);
			submit_rel = (RelativeLayout) pop_company_layout
					.findViewById(R.id.submit_rel);

			companyadapter = new In_ShopListCompanyAdapter(company_list,
					InsuranceShopListActivity.this);
			company_gd.setAdapter(companyadapter);
			windowinit = true;
		}
		pop_company = new PopupWindow(this);
		pop_company.setBackgroundDrawable(new ColorDrawable(0x00000000));
		pop_company.setWidth(LayoutParams.MATCH_PARENT);
		pop_company.setHeight(LayoutParams.MATCH_PARENT);
		pop_company.setTouchable(true);
		pop_company.setOutsideTouchable(true);
		pop_company.setContentView(pop_company_layout);
		pop_company.showAsDropDown(select_popview);
		pop_company.setFocusable(true);
		pop_company.update();
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		pop_company_layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				// int heightTop = pop_company_layout.findViewById(
				// R.id.select_company_rel).getTop();
				int heightBottom = pop_company_layout.findViewById(
						R.id.select_company_rel).getBottom();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.performClick();
					if (y > heightBottom) {
						pop_company.dismiss();
						pop_company = null;
					}

				}

				return true;
			}
		});

		cancel_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				pop_company.dismiss();
				pop_company = null;
			}
		});

		submit_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (In_ShopListCompanyAdapter.getIsSelected().get(0)) {
					companyCode = "";
					String query_param = Json_Value("", title, orderCode, "0");

					key_value.put("query_param", query_param);

				} else {
					companyCode = companyCodeing;

					Log.d("companyCode", companyCode + "77");
					String query_param = Json_Value(companyCode, title,
							orderCode, "0");

					key_value.put("query_param", query_param);

				}

				total_data.clear();
				pagenum = 0;

				final String url = IpConfig.getUri("getmall_pro_list_v1");

				setxlistdata(url);

				dialog.show();
				pop_company.dismiss();
				pop_company = null;
			}
		});

		company_gd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {

				if (In_ShopListCompanyAdapter.getIsSelected().get(0) && pos != 0) {

				} else if( pos == 0 ){
					if(In_ShopListCompanyAdapter.getIsSelected().get(0))
					{

						HashMap<Integer,Boolean> isSelected = new HashMap<Integer,Boolean>() ;
						for (int i = 0; i < In_ShopListCompanyAdapter
								.getIsSelected().size(); i++) {
							isSelected.put(i, false);




						}
						companyCodeing ="";
						In_ShopListCompanyAdapter.setIsSelected(isSelected);
						companyadapter.notifyDataSetChanged();

					}else{
						HashMap<Integer,Boolean> isSelected = new HashMap<Integer,Boolean>() ;
						for (int i = 0; i < In_ShopListCompanyAdapter
								.getIsSelected().size(); i++) {


							isSelected.put(i ,true);


						}
						In_ShopListCompanyAdapter.setIsSelected(isSelected);

						companyCodeing ="";
						companyadapter.notifyDataSetChanged();
					}


				}else if(!In_ShopListCompanyAdapter.getIsSelected().get(0) && pos != 0) {
					In_ShopListCompanyHolder holder = (In_ShopListCompanyHolder) arg1
							.getTag();

					holder.cb.toggle();

					In_ShopListCompanyAdapter.getIsSelected().put(pos,
							holder.cb.isChecked());

					if (holder.cb.isChecked()) {

						holder.name.setTextColor(getResources().getColor(
								R.color.orange_red));

						holder.name.setBackgroundResource(R.drawable.company_checkbox_pressed);

						companyCodeing = "";

						for (int i = 0; i < In_ShopListCompanyAdapter
								.getIsSelected().size(); i++) {
							if (In_ShopListCompanyAdapter.getIsSelected().get(i)) {

								companyCodeing = companyCodeing
										+ company_list.get(i).get("companyname") + ",";
							}
						}

					} else {
						holder.name.setTextColor(getResources().getColor(
								R.color.color6));
						holder.name
								.setBackgroundResource(R.drawable.in_shoplist_company_checkbox_normal);
						companyCodeing = "";

						for (int i = 0; i < In_ShopListCompanyAdapter
								.getIsSelected().size(); i++) {
							if (In_ShopListCompanyAdapter.getIsSelected().get(i)) {

								companyCodeing = companyCodeing
										+ company_list.get(i).get("companyname") + ",";
							}
						}

					}

				}
			}
		});

	}

	private void showOrderPopupWindow(String[] name) {


		pop_order_layout = ((RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.insurance_shop_order_pop, null));

		pop_order_list = (ListView) pop_order_layout
				.findViewById(R.id.pop_order_list);


		pop_order_list.setAdapter(new ArrayAdapter<String>(
				InsuranceShopListActivity.this,
				R.layout.insurance_shop_order_pop_text, R.id.tv_text, name));

		pop_order = new PopupWindow(this);
		pop_order.setBackgroundDrawable(new ColorDrawable(0x00000000));
		pop_order.setWidth(LayoutParams.MATCH_PARENT);
		pop_order.setHeight(LayoutParams.MATCH_PARENT);
		pop_order.setTouchable(true);
		pop_order.setOutsideTouchable(true);
		pop_order.setContentView(pop_order_layout);
		pop_order.showAsDropDown(select_popview);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		pop_order_layout.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int heightTop = pop_order_layout.findViewById(
						R.id.pop_order_rel).getTop();
				int heightBottom = pop_order_layout.findViewById(
						R.id.pop_order_rel).getBottom();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < heightTop || y > heightBottom) {
						pop_order.dismiss();
						pop_order = null;

					}

				}

				return true;
			}
		});

		pop_order_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				arg0.setVisibility(View.VISIBLE);

				orderCode = order_list.get(pos).get("ordercode");

				Log.d("companyCode", companyCode + "77");
				String query_param = Json_Value(companyCode, title, orderCode, "0");
				total_data.clear();
				key_value.put("query_param", query_param);

				final String url = IpConfig.getUri("getmall_pro_list_v1");

				setxlistdata(url);

				dialog.show();


				pop_order.dismiss();
				pop_order = null;

			}
		});



	}

	private void getRefreshItem() {

		total_data.clear();
		pagenum = 0;

		String query_param = Json_Value(companyCode, title, orderCode, "0");
		key_value.put("query_param", query_param);
		final String url = IpConfig.getUri("getmall_pro_list_v1");

		setxlistdata(url);
	}

	private void getLoadMoreItem() {
		pagenum++;

		String query_param = Json_Value(companyCode, title, orderCode, pagenum
				+ "");

		key_value.put("query_param", query_param);

		final String url = IpConfig.getUri("getmall_pro_list_v1");

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
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					}

					@Override
					public void onResponse(String response, int id) {
						String jsonString = response;
						Log.d("onSuccess", "onSuccess json = " + jsonString);
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

								JSONObject jsonObject = new JSONObject(
										jsonString);
								String errcode = jsonObject
										.getString("errcode");
								if (!errcode.equals("0")) {
									String errmsg = jsonObject
											.getString("errmsg");

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

	private void setconditiondata(String url) {

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

						List<Map<String, String>> order_list = new ArrayList<Map<String, String>>();
						List<Map<String, String>> company_list = new ArrayList<Map<String, String>>();
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

								JSONObject jsonObject = new JSONObject(
										jsonString);
								String errcode = jsonObject
										.getString("errcode");
								if (!errcode.equals("0")) {
									String errmsg = jsonObject
											.getString("errmsg");

									errcode_map.put("errcode", errcode);
									errcode_map.put("errmsg", errmsg);

									Message message2 = Message.obtain();

									message2.obj = errcode_map;

									null_handler.sendMessage(message2);

								} else {

									JSONObject dataObject = jsonObject
											.getJSONObject("data");

									JSONArray companiesArray = dataObject
											.getJSONArray("companies");

									Map<String, String> map2 = new HashMap<String, String>();

									map2.put("companyname", "全部");

									company_list.add(map2);

									for (int i = 0; i < companiesArray.length(); i++) {

										String value = companiesArray
												.getString(i);
										Map<String, String> map = new HashMap<String, String>();

										map.put("companyname", value);

										company_list.add(map);

									}

									JSONArray orderArray = dataObject
											.getJSONArray("order");

									for (int i = 0; i < orderArray.length(); i++) {

										JSONArray obj = (JSONArray) orderArray
												.get(i);

										Map<String, String> map = new HashMap<String, String>();

										String orderstr = obj.getString(0);
										String orderstr1 = obj.getString(1);

										map.put("ordername", orderstr);
										map.put("ordercode", orderstr1);
										order_list.add(map);

									}

									total_map.put("company_list", company_list);
									total_map.put("order_list", order_list);
									Message message = Message.obtain();

									message.obj = total_map;
									select_handler.sendMessage(message);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}

	private void setVisitData(String url) {

		OkHttpUtils.post()//
				.url(url)//
				.params(key_value)//
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

	private String Json_Value(String company, String type, String order,
							  String page) {
		String jsonresult = "";// 定义返回字符串

		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("company", company);
			jsonObj.put("type", type);
			jsonObj.put("order", order);
			jsonObj.put("page", page);

			jsonresult = jsonObj.toString();
			Log.i("JSON_______________", jsonresult);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
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


			pagenum = 0;

			key_value.put("user_id", user_id);
			key_value.put("token", token);

			String query_param = Json_Value("", title, "", "0");

			key_value.put("query_param", query_param);

			final String url = IpConfig.getUri("getmall_pro_list_v1");

			dialog.show();
			setxlistdata(url);

			promotion_expenses_show = !(loginString.equals("none") || type.equals("02")
					|| !user_state.equals("00"));
			Jumplogin = false;
		}
	}

}
