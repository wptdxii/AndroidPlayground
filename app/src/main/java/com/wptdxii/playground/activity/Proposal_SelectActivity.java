package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.activity.MyInterface.OnProposal_SelectActivityChangeListener;
import com.cloudhome.adapter.Proposal_Select_Adapter;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Proposal_SelectActivity extends BaseActivity implements
		IXListViewListener, OnProposal_SelectActivityChangeListener {

	public void setHandler(Handler handler) {
		FragmentHandler = handler;
	}

	private ImageView search_img;
	private RelativeLayout proposal_select_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private TextView insurance_kind;
	private ClearEditText proposal_select_edit;
	private DrawerLayout mDrawerLayout;
	private XListView expandable_xlist;
	private Handler mHandler, FragmentHandler;
	private View mView;
	private String loginString;
	private Proposal_Select_Adapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();
	private ArrayList<HashMap<String, String>> total_data = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> search_data = new ArrayList<HashMap<String, String>>();

	private SharedPreferences sp;
	private SharedPreferences sp2;

	private String mycompany_str = "";

	private String company_code_str = "";

	private int pagenum = 0;
	private Dialog dialog;
	private TextView company_select, period_select, type_select;
	private ImageView company_select_img, period_select_img, type_select_img;
	private RelativeLayout rel1, rel2, rel3;
	private String[] company_codeArray, company_nameArray;
	private String[] period_codeArray, period_nameArray;
	private String[] product_type_codeArray, product_type_nameArray;
	private RelativeLayout layout;
	private ListView listViewSpinner;
	private TextView lv_title;
	private PopupWindow popupWindow;
	private View popview;
	private String search_show = "false";
	private RelativeLayout null_data_rel;
	private String querykey = "", company_code = "", period = "",
			insurance_type = "", feature = "";

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

				Toast.makeText(Proposal_SelectActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}

		}

	};

	@SuppressLint("HandlerLeak")
	private Handler select_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			final Map<String, List<String>> data = (Map<String, List<String>>) msg.obj;

			List<String> company_code = data.get("company_code");
			List<String> company_name = data.get("company_name");
			List<String> period_code = data.get("period_code");
			List<String> period_name = data.get("period_name");
			List<String> product_type_code = data
					.get("product_type_code");
			List<String> product_type_name = data
					.get("product_type_name");

			company_codeArray = company_code
					.toArray(new String[company_code.size()]);
			company_nameArray = company_name
					.toArray(new String[company_name.size()]);

			period_codeArray = period_code
					.toArray(new String[period_code.size()]);
			period_nameArray = period_name
					.toArray(new String[period_name.size()]);

			product_type_codeArray = product_type_code
					.toArray(new String[product_type_code.size()]);
			product_type_nameArray = product_type_name
					.toArray(new String[product_type_name.size()]);

			for (int i = 0; i < company_nameArray.length; i++) {

				if (mycompany_str.equals(company_nameArray[i])) {
					company_code_str = company_codeArray[i];
					insurance_kind.setText(company_nameArray[i]);

				}
			}

			if (company_code_str.equals("")) {
				insurance_kind.setText("全部");

			}

			String query_info = Json_Value("0", querykey, "", "", company_code_str,
					"", "", "");

			key_value.put("query_info", query_info);

			final String url = IpConfig.getUri2("getSuggestProductList");
			setxlistdata(url);
			rel1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					company_select.setTextColor(getResources().getColor(
							R.color.orange_red));

					// if (company_codeArray.length > 0) {
					//
					// showPopupWindow(company_codeArray, company_nameArray,
					// "1");
					//
					// }

					period_select.setText("保障期间");
					period_select.setTextColor(getResources().getColor(
							R.color.p_s_grey));

					period_select_img
							.setImageResource(R.drawable.p_s_grey_sanjia);

					type_select.setText("保险类别");
					type_select.setTextColor(getResources().getColor(
							R.color.p_s_grey));
					type_select_img
							.setImageResource(R.drawable.p_s_grey_sanjia);

					dialog.show();


					period = "";
					insurance_type = "";
					feature = "";

					String query_info = Json_Value("0", querykey, "", "",
							company_code_str, "", "", "");

					key_value.put("query_info", query_info);
					Log.i("query_info-------", query_info);
					search_show = "false";
					pagenum = 0;
					total_data.clear();
					search_data.clear();
					adapter.notifyDataSetChanged();

					final String url = IpConfig
							.getUri2("getSuggestProductList");
					setxlistdata(url);

				}
			});

			rel2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (period_codeArray.length > 0) {
						showPopupWindow(period_codeArray, period_nameArray, "2");
					}

				}
			});

			rel3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (product_type_codeArray.length > 0) {
						showPopupWindow(product_type_codeArray,
								product_type_nameArray, "3");
					}

				}
			});

		}

	};
	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");
			expandable_xlist.stopLoadMore();

			if (pagenum == 0) {
				null_data_rel.setVisibility(View.VISIBLE);
				expandable_xlist.setVisibility(View.GONE);
			}

			dialog.dismiss();
			Toast.makeText(Proposal_SelectActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, Object> map = (Map<String, Object>) msg.obj;

			ArrayList<HashMap<String, String>> prodinfo_list = (ArrayList<HashMap<String, String>>) map
					.get("info_list");

			dialog.dismiss();

			if (search_show.equals("false")) {

				if (pagenum == 0) {

					if (total_data.size() < 1) {

						total_data.addAll(prodinfo_list);
						adapter.setData(total_data);
						expandable_xlist.setAdapter(adapter);
						adapter.notifyDataSetChanged();

					}

					expandable_xlist.stopLoadMore();

					if (prodinfo_list.size() < 1) {
						null_data_rel.setVisibility(View.VISIBLE);
						expandable_xlist.setVisibility(View.GONE);
					} else {
						null_data_rel.setVisibility(View.GONE);
						expandable_xlist.setVisibility(View.VISIBLE);
					}

				} else {

					total_data.addAll(prodinfo_list);
					adapter.setData(total_data);
					adapter.notifyDataSetChanged();
					expandable_xlist.stopLoadMore();

				}
			} else {

				if (pagenum == 0) {

					if (search_data.size() < 1) {
						search_data.addAll(prodinfo_list);

						adapter.setData(search_data);

						expandable_xlist.setAdapter(adapter);
						adapter.notifyDataSetChanged();

					}

					expandable_xlist.stopLoadMore();

					if (prodinfo_list.size() < 1) {
						null_data_rel.setVisibility(View.VISIBLE);
						expandable_xlist.setVisibility(View.GONE);
					} else {
						null_data_rel.setVisibility(View.GONE);
						expandable_xlist.setVisibility(View.VISIBLE);
					}

				} else {
					search_data.addAll(prodinfo_list);
					adapter.setData(total_data);
					adapter.notifyDataSetChanged();
					expandable_xlist.stopLoadMore();

				}

			}

		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.proposal_select);
		sp = this.getSharedPreferences("userInfo", 0);
		sp2 = this.getSharedPreferences("otherinfo", 0);
		init();
		initEvent();

	}

	private void init() {

		adapter = new Proposal_Select_Adapter(Proposal_SelectActivity.this);
		loginString = sp.getString("Login_STATE", "none");
		proposal_select_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("制作建议书");
		null_data_rel = (RelativeLayout) findViewById(R.id.null_data_rel);
		search_img = (ImageView) findViewById(R.id.search_img);
		proposal_select_edit = (ClearEditText) findViewById(R.id.proposal_select_edit);



		proposal_select_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();

				int len = s.toString().length();
				if (len == 0 && search_show.equals("ture")) {

					pagenum = 0;
					total_data.clear();

				
					company_code = "";
					period = "";
					insurance_type = "";
					feature = "";

					total_data.clear();
					search_data.clear();
					adapter.notifyDataSetChanged();

					String query_info = Json_Value("0", querykey, "", "",
							company_code_str, "", "", "");
					Log.e("query_info------------", query_info);
					key_value.put("query_info", query_info);
					final String url = IpConfig
							.getUri2("getSuggestProductList");
					dialog.show();
					search_show = "false";
					setxlistdata(url);

				}

			}
		});

		insurance_kind = (TextView) findViewById(R.id.insurance_kind);
		expandable_xlist = (XListView) findViewById(R.id.expandable_xlist);

		rel1 = (RelativeLayout) findViewById(R.id.rel1);
		rel2 = (RelativeLayout) findViewById(R.id.rel2);
		rel3 = (RelativeLayout) findViewById(R.id.rel3);

		company_select = (TextView) findViewById(R.id.company_select);
		period_select = (TextView) findViewById(R.id.period_select);
		type_select = (TextView) findViewById(R.id.type_select);

		company_select_img = (ImageView) findViewById(R.id.company_select_img);
		period_select_img = (ImageView) findViewById(R.id.period_select_img);
		type_select_img = (ImageView) findViewById(R.id.type_select_img);

		popview = findViewById(R.id.popview);
		mHandler = new Handler();
		expandable_xlist.setPullLoadEnable(true);
		expandable_xlist.setXListViewListener(Proposal_SelectActivity.this);

		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("卖力加载中...");

		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				Gravity.RIGHT);

		// 设置XlistView的item的点击事件
		expandable_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				if (loginString.equals("none")) {

					Intent intent = new Intent();
					intent.setClass(Proposal_SelectActivity.this,
							LoginActivity.class);
					Proposal_SelectActivity.this.startActivity(intent);

				} else {

					int pos = position - 1;
					int size = 0;
					if (search_show.equals("false")) {

						size = total_data.size();
					} else {

						size = search_data.size();
					}
					if (pos >= 0 && pos < size) {

						Intent intent = new Intent();

						if (search_show.equals("false")) {

							intent.putExtra("id", total_data.get(pos).get("id"));
							intent.putExtra("title",
									total_data.get(pos).get("name"));
						} else {

							intent.putExtra("id", search_data.get(pos)
									.get("id"));
							intent.putExtra("title",
									search_data.get(pos).get("name"));
						}

//						intent.setClass(Proposal_SelectActivity.this,
//								MakeInsurancePlanActivity.class);
						intent.setClass(Proposal_SelectActivity.this,
								MakeInsuranceActivity.class);
						Proposal_SelectActivity.this.startActivity(intent);
					}

				}

			}
		});

	}

	private void initEvent() {

		mycompany_str = sp2.getString("company_name", "");

		dialog.show();
		final String Conditions_url = IpConfig
				.getUri2("getSuggestProductQueryConditions");

		Log.d("77477777", Conditions_url);
		setselectdata(Conditions_url);

		proposal_select_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

		
		
		proposal_select_edit
		.setOnEditorActionListener(new OnEditorActionListener() {

		
			@Override
			public boolean onEditorAction(TextView v, int actionId,
										  KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					// 先隐藏键盘
					((InputMethodManager) proposal_select_edit.getContext()
							.getSystemService(
									Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(
									Proposal_SelectActivity.this.getCurrentFocus()
											.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

		
					querykey = proposal_select_edit.getText().toString();
					if (querykey.equals("null") || querykey.equals("")) {
						Toast.makeText(Proposal_SelectActivity.this, "请输入保险产品名称",
								Toast.LENGTH_SHORT).show();
					} else {

						pagenum = 0;
						search_show = "ture";
						search_data.clear();
						total_data.clear();
						search_data.clear();
						adapter.notifyDataSetChanged();
						String query_info = Json_Value("0", querykey, "", "", company_code_str,
								period, insurance_type, "");
						
					
						key_value.put("query_info", query_info);
						Log.i("query_info------------", query_info);
						final String url = IpConfig
								.getUri2("getSuggestProductList");
						dialog.show();
						setxlistdata(url);

					}
					

					return true;
				}
				return false;
			
			}
		});
		search_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				querykey = proposal_select_edit.getText().toString();
				if (querykey.equals("null") || querykey.equals("")) {
					Toast.makeText(Proposal_SelectActivity.this, "请输入保险产品名称",
							Toast.LENGTH_SHORT).show();
				} else {

					pagenum = 0;
					search_show = "ture";
					search_data.clear();
					total_data.clear();
					search_data.clear();
					adapter.notifyDataSetChanged();

					String query_info = Json_Value("0", querykey, "", "", company_code_str,
							period, insurance_type, "");
					key_value.put("query_info", query_info);
					Log.i("query_info-----------", query_info);
					final String url = IpConfig
							.getUri2("getSuggestProductList");
					dialog.show();
					setxlistdata(url);

				}

			}
		});
		insurance_kind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				OpenRightMenu();

			}
		});

		mDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int newState) {

			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				View mContent = mDrawerLayout.getChildAt(0);

				View mMenu = drawerView;

				float scale = 1 - slideOffset;

				if (drawerView.getTag().equals("RIGHT")) {

					// ViewHelper.setTranslationX(mContent,
					// -mMenu.getMeasuredWidth() * slideOffset);
					// ViewHelper.setPivotX(mContent,
					// mContent.getMeasuredWidth());
					ViewHelper.setPivotX(mMenu,
							mMenu.getMeasuredWidth() / 100 * 81);
					ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight());

					mContent.invalidate();
					ViewHelper.setScaleX(mContent, 1);
					ViewHelper.setScaleY(mContent, 1);

				}

			}

			@Override
			public void onDrawerOpened(View drawerView) {

			}

			@Override
			public void onDrawerClosed(View drawerView) {

				mDrawerLayout.setDrawerLockMode(
						DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
			}
		});

	}

	private void OpenRightMenu() {
		mDrawerLayout.openDrawer(Gravity.RIGHT);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
				Gravity.RIGHT);

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
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
						List<Map<String, String>> info_list = new ArrayList<Map<String, String>>();

						Map<String, String> errcode_map = new HashMap<String, String>();
						Map<String, Object> total_map = new HashMap<String, Object>();

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

									total_map.put("info_list", info_list);

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

	private void setselectdata(String url) {

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
						Map<String, String> errcode_map = new HashMap<String, String>();

						Map<String, List<String>> map_all = new HashMap<String, List<String>>();

						List<String> company_code = new ArrayList<String>();

						List<String> company_name = new ArrayList<String>();

						List<String> period_code = new ArrayList<String>();

						List<String> period_name = new ArrayList<String>();

						List<String> product_type_code = new ArrayList<String>();

						List<String> product_type_name = new ArrayList<String>();

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

									JSONArray companyArray = dataObject
											.getJSONArray("company");

									company_code.add("");

									company_name.add("全部");

									for (int i = 0; i < companyArray.length(); i++) {

										JSONArray jsonArray1 = companyArray
												.getJSONArray(i);

										company_code.add(jsonArray1
												.getString(0));

										company_name.add(jsonArray1
												.getString(1));

									}

									JSONArray holderArray = dataObject
											.getJSONArray("period");

									period_code.add("");

									period_name.add("全部");

									for (int i = 0; i < holderArray.length(); i++) {

										JSONArray jsonArray1 = holderArray
												.getJSONArray(i);

										period_code.add(jsonArray1.getString(0));

										period_name.add(jsonArray1.getString(1));

									}

									JSONArray insuredArray = dataObject
											.getJSONArray("product_type");

									product_type_code.add("");

									product_type_name.add("全部");

									for (int i = 0; i < insuredArray.length(); i++) {

										JSONArray jsonArray1 = insuredArray
												.getJSONArray(i);

										product_type_code.add(jsonArray1
												.getString(0));

										product_type_name.add(jsonArray1
												.getString(1));

									}

									map_all.put("company_code", company_code);
									map_all.put("company_name", company_name);
									map_all.put("period_code", period_code);
									map_all.put("period_name", period_name);
									map_all.put("product_type_code",
											product_type_code);
									map_all.put("product_type_name",
											product_type_name);

									Message message = Message.obtain();

									message.obj = map_all;
									select_handler.sendMessage(message);

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

	}

	private void showPopupWindow(final String[] code, final String[] name,
								 final String num) {
		layout = ((RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.proposal_select_pop, null));
		listViewSpinner = (ListView) this.layout.findViewById(R.id.lv_dialog);
		lv_title = (TextView) this.layout.findViewById(R.id.lv_title);

		 if (num.equals("2")) {
			lv_title.setText("请选择保障期间");
		} else if (num.equals("3")) {
			lv_title.setText("请选择保险类别");
		}

		listViewSpinner
				.setAdapter(new ArrayAdapter<String>(
						Proposal_SelectActivity.this, R.layout.text,
						R.id.tv_text, name));

		popupWindow = new PopupWindow(this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setContentView(this.layout);
		popupWindow.showAsDropDown(this.popview);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int heightTop = layout.findViewById(R.id.c_type_rel).getTop();
				int heightBottom = layout.findViewById(R.id.c_type_rel)
						.getBottom();
				int heightLeft = layout.findViewById(R.id.c_type_rel).getLeft();
				int heightRight = layout.findViewById(R.id.c_type_rel)
						.getRight();
				int y = (int) event.getY();
				int x = (int) event.getX();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.performClick();
					if (y < heightTop || y > heightBottom || x < heightLeft
							|| x > heightRight) {
						popupWindow.dismiss();
					}

				}

				return true;
			}
		});

		listViewSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				arg0.setVisibility(View.VISIBLE);

				 if (num.equals("2")) {
					if (name[pos].equals("全部")) {
						period_select.setText("保障期间");
						period_select.setTextColor(getResources().getColor(
								R.color.p_s_grey));

						period_select_img
								.setImageResource(R.drawable.p_s_grey_sanjia);
					} else {
						period_select.setText(name[pos]);
						period_select.setTextColor(getResources().getColor(
								R.color.orange_red));
						period_select_img
								.setImageResource(R.drawable.p_s_orange_sanjia);
					}

					company_select.setText("热销排名");
					company_select.setTextColor(getResources().getColor(
							R.color.p_s_grey));
					company_select_img
							.setImageResource(R.drawable.p_s_grey_sanjia);

					type_select.setText("保险类别");
					type_select.setTextColor(getResources().getColor(
							R.color.p_s_grey));
					type_select_img
							.setImageResource(R.drawable.p_s_grey_sanjia);

					search_show = "false";
					dialog.show();
					total_data.clear();
					search_data.clear();
					adapter.notifyDataSetChanged();

				
					company_code = "";
					period = code[pos];
					insurance_type = "";
					feature = "";
					String query_info = Json_Value("0", querykey, "", "",
							company_code_str, code[pos], "", "");

					key_value.put("query_info", query_info);
					pagenum = 0;
					final String url = IpConfig
							.getUri2("getSuggestProductList");
					setxlistdata(url);

				} else if (num.equals("3")) {

					if (name[pos].equals("全部")) {
						type_select.setText("保险类别");
						type_select.setTextColor(getResources().getColor(
								R.color.p_s_grey));
						type_select_img
								.setImageResource(R.drawable.p_s_grey_sanjia);
					} else {
						type_select.setText(name[pos]);
						type_select.setTextColor(getResources().getColor(
								R.color.orange_red));
						type_select_img
								.setImageResource(R.drawable.p_s_orange_sanjia);
					}

					company_select.setText("热销排名");
					company_select.setTextColor(getResources().getColor(
							R.color.p_s_grey));
					company_select_img
							.setImageResource(R.drawable.p_s_grey_sanjia);

					period_select.setText("保障期间");
					period_select.setTextColor(getResources().getColor(
							R.color.p_s_grey));

					period_select_img
							.setImageResource(R.drawable.p_s_grey_sanjia);

					search_show = "false";
					dialog.show();
					Log.d("99999", code[pos] + "99999");
					total_data.clear();
					search_data.clear();
					adapter.notifyDataSetChanged();

				
					company_code = "";
					period = "";
					insurance_type = code[pos];
					feature = "";
					String query_info = Json_Value("0", querykey, "", "",
							company_code_str, "", code[pos], "");

					key_value.put("query_info", query_info);
					pagenum = 0;
					final String url = IpConfig
							.getUri2("getSuggestProductList");
					setxlistdata(url);

				}

				popupWindow.dismiss();
				popupWindow = null;

			}
		});
	}

	private void getRefreshItem() {

		pagenum = 0;
		total_data.clear();
		search_data.clear();
		adapter.notifyDataSetChanged();
		String query_info = Json_Value("0", querykey, "", "", company_code_str,
				period, insurance_type, feature);

		key_value.put("query_info", query_info);

		final String url = IpConfig.getUri2("getSuggestProductList");

		setxlistdata(url);
	}

	private void getLoadMoreItem() {
		pagenum++;
		Log.d("555555", pagenum + "");

		String query_info = Json_Value(pagenum + "", querykey, "", "",
				company_code_str, period, insurance_type, feature);

		Log.d("99999999", query_info + "");
		key_value.put("query_info", query_info);
		final String url = IpConfig.getUri2("getSuggestProductList");

		setxlistdata(url);
	}

	@Override
	public void onRefresh() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				expandable_xlist.stopLoadMore();

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

				expandable_xlist.stopRefresh();
				getLoadMoreItem();

				// adapter.notifyDataSetChanged();

				// mListView.stopLoadMore();

			}
		}, 2000);
	}

	private void onLoad() {

		expandable_xlist.stopRefresh();

		expandable_xlist.stopLoadMore();

		expandable_xlist.setRefreshTime("刚刚");

	}



	@Override
	public void CloseRightMenu() {

		mDrawerLayout.closeDrawer(Gravity.RIGHT);

	}

	private String Json_Value(String page, String querykey, String age,
							  String sex, String company_code, String period,
							  String insurance_type, String feature) {
		String jsonresult = "";// 定义返回字符串

		try {
			JSONObject jsonObj = new JSONObject();// pet对象，json形式.

			jsonObj.put("page", page);// 向pet对象里面添加值
			jsonObj.put("querykey", querykey);
			jsonObj.put("age", age);
			jsonObj.put("sex", sex);
			jsonObj.put("company_code", company_code);
			jsonObj.put("period", period);
			jsonObj.put("insurance_type", insurance_type);
			jsonObj.put("feature", feature);

			jsonresult = jsonObj.toString();
			Log.i("SUBMIT_ORDER_________", jsonresult);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}

	@Override
	public void onActivityChange(String companyname, String companycode) {
		mDrawerLayout.closeDrawer(Gravity.RIGHT);

		insurance_kind.setText(companyname);
		// company_code = companycode;
		company_code_str = companycode;

		String query_info = Json_Value("0", querykey, "", "", company_code_str,
				period, insurance_type, "");

		key_value.put("query_info", query_info);

		dialog.show();
		pagenum = 0;
		total_data.clear();
		search_data.clear();
		adapter.notifyDataSetChanged();

		search_show = "false";

		final String url = IpConfig.getUri2("getSuggestProductList");
		setxlistdata(url);

	}

	@Override
	public void onRestart() {
		super.onRestart();

		loginString = sp.getString("Login_STATE", "none");

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

	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			// 点击的是输入框区域，保留点击EditText的事件
			return !(event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom);
		}
		return false;
	}

}
