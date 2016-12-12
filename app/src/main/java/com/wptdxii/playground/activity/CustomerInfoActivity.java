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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.cloudhome.utils.IpConfig.getUri;

public class CustomerInfoActivity extends BaseActivity implements
		IXListViewListener {

	private MyAdapter madapter;
	private CpAdapter c_p_adapter;
	private ListAdapter listadapter;

	private RadioGroup radio_customer;

	private RadioGroup p_list_radio;
	private RelativeLayout customer_info_back;
	private TextView tv_text;
	private String raddio_button = "02";
	private String p_list_button = "00";
	private Map<String, String> key_value = new HashMap<String, String>();

	private Dialog dialog;

	private XListView per_policy_xlist;
	private XListView car_policy_xlist;
	private ListView c_i_list;
	private TextView c_i_name;
	private TextView c_i_sex;
	private TextView c_i_date;
	private TextView c_i_income;
	private TextView c_i_social_security;
	private TextView c_i_health;
	private TextView c_i_type;
	private TextView c_i_num;
	private TextView c_i_career;
	private TextView c_i_phonenum;
	private TextView c_i_email;
	private TextView c_i_remarks;
	private Button customer_info_edit;
	private Handler mHandler;
	private Button c_i_add_family;
	private Button c_i_add_policy;
	private String user_id;
	private String token;
	private String customer_id;
	private String c_i_name_str, c_i_sex_str, c_i_date_str, c_i_income_str,
			c_i_social_security_str, c_i_social_security_code, c_i_type_str,
			c_i_num_str, c_i_career_str, c_i_career_code, c_i_phonenum_str,
			c_i_email_str, c_i_remarks_str, c_i_health_str, c_i_health_code;
	private RelativeLayout p_list_rel;
	private RelativeLayout c_i_list_rel;
	private int p_pnum = 0;
	private int c_pnum = 0;
	private List<Map<String, Object>> p_p_data = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> c_p_data = new ArrayList<Map<String, Object>>();
	private Handler info_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			customer_info_edit.setVisibility(View.VISIBLE);
			Map<String, String> map = (Map<String, String>) msg.obj;
			dialog.dismiss();
			c_i_name_str = map.get("name");
			c_i_sex_str = map.get("sex");

			c_i_date_str = map.get("birthday");
			c_i_income_str = map.get("income");
			c_i_social_security_str = map.get("medicare");
			c_i_social_security_code = map.get("medicare_code");
			c_i_health_str = map.get("health");
			c_i_health_code = map.get("health_code");
			c_i_type_str = map.get("id_type");
			c_i_num_str = map.get("idno");
			c_i_career_str = map.get("job");
			c_i_career_code = map.get("job_code");
			c_i_phonenum_str = map.get("mobile");
			c_i_email_str = map.get("email");
			c_i_remarks_str = map.get("remark");

			c_i_name.setText(c_i_name_str);

			if (c_i_sex_str == null || c_i_sex_str.equals("null")
					|| c_i_sex_str.equals("")) {
				c_i_sex.setText("");
			} else {
				c_i_sex.setText(c_i_sex_str);
			}
			if (null==c_i_phonenum_str || c_i_phonenum_str.equals("null")
					|| c_i_phonenum_str.equals("")) {
				c_i_phonenum.setText("");
			} else {
				c_i_phonenum.setText(c_i_phonenum_str);
			}
			if (null==c_i_email_str || c_i_email_str.equals("null")
					|| c_i_email_str.equals("")) {
				c_i_email.setText("");
			} else {
				c_i_email.setText(c_i_email_str);
			}
			if (null==c_i_remarks_str || c_i_remarks_str.equals("null")
					|| c_i_remarks_str.equals("")) {
				c_i_remarks.setText("");
			} else {
				c_i_remarks.setText(c_i_remarks_str);
			}


			c_i_date.setText(c_i_date_str);

			c_i_income.setText(c_i_income_str);

			c_i_social_security.setText(c_i_social_security_str);

			c_i_health.setText(c_i_health_str);
			c_i_type.setText(c_i_type_str);

			c_i_num.setText(c_i_num_str);
			c_i_career.setText(c_i_career_str);

		}

	};

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

				Toast.makeText(CustomerInfoActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	private Handler policy_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;
			dialog.dismiss();



			if (p_list_button.equals("01")) {



				if (p_pnum == 0) {
					p_p_data.clear();
					p_p_data.addAll(list);
					madapter.setData(p_p_data);
					per_policy_xlist.setAdapter(madapter);
					madapter.notifyDataSetChanged();

				} else {
					p_p_data.addAll(list);
					madapter.notifyDataSetChanged();
					per_policy_xlist.stopLoadMore();
				}
			} else {

				if (c_pnum == 0) {
					c_p_data.clear();
					c_p_data.addAll(list);
					c_p_adapter.setData(c_p_data);
					car_policy_xlist.setAdapter(c_p_adapter);
					c_p_adapter.notifyDataSetChanged();

				} else {

					c_p_data.addAll(list);
					c_p_adapter.notifyDataSetChanged();
					car_policy_xlist.stopLoadMore();
				}
			}
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler family_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			listadapter.setData(list);
			c_i_list.setAdapter(listadapter);
			listadapter.notifyDataSetChanged();

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.customer_info_main);
		Intent intent = getIntent();

		customer_id = intent.getStringExtra("customer_id");

		Log.d("4545", customer_id);

		init();
		Infoinit();
		Policyinit();
		initEvent();
	
	
		Familyinit();

	}



	void init() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		customer_info_back = (RelativeLayout) findViewById(R.id.iv_back);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("我的客户");
		radio_customer = (RadioGroup) findViewById(R.id.radio_customer);

		customer_info_edit = (Button) findViewById(R.id.btn_right);
		customer_info_edit.setText("编辑");
		c_i_list_rel = (RelativeLayout) findViewById(R.id.c_i_list_rel);
		p_list_rel    = (RelativeLayout) findViewById(R.id.p_list_rel);
		
		  dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("请稍后...");
          
		
		mHandler = new Handler();
	}

	void Infoinit() {

		c_i_name = (TextView) findViewById(R.id.c_i_name);
		c_i_sex = (TextView) findViewById(R.id.c_i_sex);
		c_i_date = (TextView) findViewById(R.id.c_i_date);
		c_i_income = (TextView) findViewById(R.id.c_i_income);
		c_i_social_security = (TextView) findViewById(R.id.c_i_social_security);
		c_i_health = (TextView) findViewById(R.id.c_i_health);
		c_i_type = (TextView) findViewById(R.id.c_i_type);
		c_i_num = (TextView) findViewById(R.id.c_i_num);
		c_i_career = (TextView) findViewById(R.id.c_i_career);
		c_i_phonenum = (TextView) findViewById(R.id.c_i_phonenum);
		c_i_email = (TextView) findViewById(R.id.c_i_email);
		c_i_remarks = (TextView) findViewById(R.id.c_i_remarks);

	}

	void Policyinit() {

		

		p_list_radio  = (RadioGroup) findViewById(R.id.p_list_radio);
	 // person_policy = (RadioButton) findViewById(R.id.person_policy);
	 // car_policy    = (RadioButton) findViewById(R.id.car_policy);

		madapter = new MyAdapter(CustomerInfoActivity.this);

		c_p_adapter = new CpAdapter(CustomerInfoActivity.this);

		per_policy_xlist = (XListView) findViewById(R.id.per_policy_xlist);
		per_policy_xlist.setPullLoadEnable(true);
		per_policy_xlist.setXListViewListener(CustomerInfoActivity.this);
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		per_policy_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
Log.i("user_idtoken",user_id+"----"+token);
				int pos = position - 1;
				if (pos >= 0 && pos < madapter.list.size()) {

//					String url = getUri("policy_detail_mobile");
//					url = url
//							+ "&user_id="
//							+ user_id
//							+ "&token="
//							+ token
//							+ "&policy_code="
//							+ madapter.list.get(pos).get("policy_code")
//									.toString();
//					 url=url+"&user_id="+user_id+"&policy_code="+madapter.list.get(pos
//					 - 1).get("id").toString();
					String url=madapter.list.get(pos).get("url_policy_detail").toString();
					Intent intent = new Intent();
					intent.putExtra("url", url);
					intent.setClass(CustomerInfoActivity.this,
							PolicyInfoWebActivity.class);
					CustomerInfoActivity.this.startActivity(intent);

				}

			}
		});

		car_policy_xlist = (XListView) findViewById(R.id.car_policy_xlist);
		car_policy_xlist.setPullLoadEnable(true);
		car_policy_xlist.setXListViewListener(CustomerInfoActivity.this);
		car_policy_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {

				int pos = position - 1;

				if (pos >= 0 && pos < c_p_adapter.list.size()) {

//					String url = getUri("getAutoPolicyDetailForWeb");
//					url = url
//							+ "user_id="
//							+ user_id
//							+ "&token="
//							+ token
//							+ "&auto_id="
//							+ c_p_adapter.list.get(pos).get("id").toString()
//							+ "&type_id="
//							+ c_p_adapter.list.get(pos).get("type_id")
//									.toString();
					// url=url+"&user_id="+user_id+"&policy_code="+madapter.list.get(pos
					// - 1).get("id").toString();

					// String
					// policy="http://jr.sinosig.com/apitest/index.php?mod=getAutoPolicyDetailForWeb&user_id=144&token=33295a19b284441dae4f8529cc637227&auto_id=1&type_id=02";
					// setpolicyList(policy);
					String url=c_p_adapter.list.get(pos).get("url_policy_detail").toString();

					Log.d("7777", url);
					Intent intent = new Intent();

					intent.putExtra("url", url);
					intent.setClass(CustomerInfoActivity.this,
							PolicyInfoWebActivity.class);
					CustomerInfoActivity.this.startActivity(intent);

				}

			}
		});

		p_list_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {

				case R.id.person_policy:
					customer_info_edit.setVisibility(View.GONE);
					p_list_button = "01";
					c_i_list_rel.setVisibility(View.GONE);
					p_list_rel.setVisibility(View.VISIBLE);
					car_policy_xlist.setVisibility(View.GONE);
					per_policy_xlist.setVisibility(View.VISIBLE);
					p_pnum = 0;
					key_value.put("page_no", "0");
					key_value.put("policy_type", "life");
					c_p_data.clear();
					final String policyurl = IpConfig.getUri2("getPolicyList");
					setpolicyList(policyurl);
					break;

				case R.id.car_policy:
					customer_info_edit.setVisibility(View.GONE);
					p_list_button = "02";
					c_i_list_rel.setVisibility(View.GONE);
					p_list_rel.setVisibility(View.VISIBLE);
					per_policy_xlist.setVisibility(View.GONE);
					car_policy_xlist.setVisibility(View.VISIBLE);
				
					c_pnum = 0;
					key_value.put("page_no", "0");
					key_value.put("policy_type", "car");
					final String c_p_yurl = IpConfig.getUri2("getPolicyList");
					setpolicyList(c_p_yurl);

					break;

				default:
					break;
				}
			}
		});

		c_i_add_policy = (Button) findViewById(R.id.c_i_add_policy);
		c_i_add_policy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.setClass(CustomerInfoActivity.this,
						PolicyPictureActivity.class);
				CustomerInfoActivity.this.startActivity(intent);
			}
		});

	}

	void Familyinit() {
		listadapter = new ListAdapter(CustomerInfoActivity.this);
		c_i_list = (ListView) findViewById(R.id.c_i_list);
		c_i_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();

				intent.putExtra("customer_a_id", customer_id);

				intent.putExtra("relation",
						listadapter.list.get(pos).get("relation").toString());

				intent.putExtra("code", listadapter.list.get(pos).get("code")
						.toString());

				intent.putExtra("customer_b_id",
						listadapter.list.get(pos).get("id").toString());

				intent.setClass(CustomerInfoActivity.this,
						ModifyRelationActivity.class);

				CustomerInfoActivity.this.startActivity(intent);

			}
		});
	
		c_i_add_family = (Button) findViewById(R.id.c_i_add_family);
		c_i_add_family.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();

				// 设置传递的参数

				intent.putExtra("customer_id", customer_id);

				intent.setClass(CustomerInfoActivity.this,
						AddRelationActivity.class);

				// 启动intent的Activity

				CustomerInfoActivity.this.startActivity(intent);

			}
		});
	}

	void initEvent() {

		Log.d("user_id", user_id);
		Log.d("token", token);
		Log.d("customer_id", customer_id);
		key_value.put("holder_id", customer_id);
		key_value.put("user_id", user_id);
		key_value.put("token", token);
		//客户信息和家庭成员需要的key值是customer_id
		key_value.put("customer_id",customer_id);
		dialog.show();
//		final String url = IpConfig.getUri("getCustomerDetailInfo");
//
//		setinfoList(url);

		
		
		customer_info_edit.setVisibility(View.GONE);

		c_i_list_rel.setVisibility(View.GONE);
		p_list_rel.setVisibility(View.VISIBLE);
		
		p_list_button = "01";
		per_policy_xlist.setVisibility(View.VISIBLE);
		car_policy_xlist.setVisibility(View.GONE);
		key_value.put("page_no", "0");
		key_value.put("policy_type", "life");
		final String policyurl = IpConfig
				.getUri2("getPolicyList");
		Log.i("policyurl",policyurl);

		setpolicyList(policyurl);
		customer_info_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		customer_info_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();
				intent.putExtra("name", c_i_name_str);
				intent.putExtra("sex", c_i_sex_str);
				intent.putExtra("birthday", c_i_date_str);
				intent.putExtra("income", c_i_income_str);
				intent.putExtra("medicare", c_i_social_security_str);
				intent.putExtra("medicare_code", c_i_social_security_code);
				intent.putExtra("health", c_i_health_str);
				intent.putExtra("health_code", c_i_health_code);
				intent.putExtra("id_type", c_i_type_str);
				intent.putExtra("idno", c_i_num_str);
				intent.putExtra("job", c_i_career_str);
				intent.putExtra("job_code", c_i_career_code);
				intent.putExtra("mobile", c_i_phonenum_str);
				intent.putExtra("email", c_i_email_str);
				intent.putExtra("remark", c_i_remarks_str);
				intent.putExtra("customer_id", customer_id);
				intent.setClass(CustomerInfoActivity.this,
						C_InfoEditActivity.class);

				// 启动intent的Activity

				CustomerInfoActivity.this.startActivity(intent);

			}
		});
		radio_customer
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup arg0, int checkedId) {
						// TODO Auto-generated method stub
						switch (checkedId) {

						case R.id.customer_info:

							raddio_button = "01";
							c_i_list_rel.setVisibility(View.GONE);
							p_list_rel.setVisibility(View.GONE);
							final String url =
									getUri("getCustomerDetailInfo");

							setinfoList(url);

							break;

						case R.id.customer_policy:

							if (p_list_button.equals("00")) {
								customer_info_edit.setVisibility(View.GONE);

								c_i_list_rel.setVisibility(View.GONE);
								p_list_rel.setVisibility(View.VISIBLE);
								p_list_button = "01";
								per_policy_xlist.setVisibility(View.VISIBLE);
								car_policy_xlist.setVisibility(View.GONE);
								key_value.put("page_no", "0");
								key_value.put("policy_type", "life");
								final String policyurl = IpConfig.getUri2("getPolicyList");
								setpolicyList(policyurl);
							} else if (p_list_button.equals("01")) {
								customer_info_edit.setVisibility(View.GONE);
								c_i_list_rel.setVisibility(View.GONE);
								p_list_rel.setVisibility(View.VISIBLE);
								per_policy_xlist.setVisibility(View.VISIBLE);
								car_policy_xlist.setVisibility(View.GONE);
							} else {
								customer_info_edit.setVisibility(View.GONE);

								c_i_list_rel.setVisibility(View.GONE);
								p_list_rel.setVisibility(View.VISIBLE);
								per_policy_xlist.setVisibility(View.GONE);
								car_policy_xlist.setVisibility(View.VISIBLE);
							}

							raddio_button = "02";
							break;

						case R.id.customer_family:
							customer_info_edit.setVisibility(View.GONE);

							raddio_button = "03";

							p_list_rel.setVisibility(View.GONE);

							c_i_list_rel.setVisibility(View.VISIBLE);
							final String relationurl =
									getUri("getRelation");
							setfamilyList(relationurl);

							break;

						default:
							break;
						}
					}
				});

	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (raddio_button.equals("01")) {
			dialog.show();
			final String url = getUri("getCustomerDetailInfo");
			setinfoList(url);
		} else if (raddio_button.equals("03")) {
			final String relationurl = getUri("getRelation");
			setfamilyList(relationurl);
		}

	}

	private void setinfoList(String url) {

		
		
		
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
				Log.d("haha", "onSuccess json = " + jsonString);
				Map<String, String> map = new HashMap<String, String>();
				try {

					Log.d("44444", jsonString);

					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode=jsonObject.getString("errcode");
						if(errcode.equals("0")){
							JSONObject data = jsonObject.getJSONObject("data");

							Iterator<String> iterator = data.keys();
							while (iterator.hasNext()) {
								String key = iterator.next();
								if (key.equals("id_type") || key.equals("sex")
										|| key.equals("medicare")
										|| key.equals("health")
										|| key.equals("job")) {
									String value = data.getString(key);
									JSONObject valueObject = new JSONObject(
											value);
									String name = valueObject.getString("name");
									String code = valueObject.getString("code");
									Log.i("haha---"+name,code);

									map.put(key, name);
									map.put(key + "_code", code);

								} else {
									String value = data.getString(key);
									map.put(key, value);
								}
							}
							Message message = Message.obtain();
							message.obj = map;
							info_handler.sendMessage(message);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		

	}

	private void setpolicyList(String url) {

		
		
		
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
				per_policy_xlist.stopLoadMore();
				message.obj = status;

				errcode_handler.sendMessage(message);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {

					Log.d("44444", jsonString);
					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();
						per_policy_xlist.stopLoadMore();
						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						JSONArray dataList = jsonObject
								.getJSONArray("data");

						for (int i = 0; i < dataList.length(); i++) {
							JSONObject jsonObject2 = dataList
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

						policy_handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		


	}

	private void setfamilyList(String url) {

		
		
		
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

					Log.d("5555", jsonString);
					if (jsonString.equals("") || jsonString.equals("null")) {
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
							Map<String, Object> map = new HashMap<String, Object>();
							// 迭代输出json的key作为map的key

							Iterator<String> iterator = jsonObject2.keys();
							while (iterator.hasNext()) {

								String key = iterator.next();
								if (key.equals("relation")) {
									JSONObject relationObject = jsonObject2
											.getJSONObject("relation");

									Object value = relationObject
											.get("name");
									Object code = relationObject
											.get("code");
									map.put(key, value);
									map.put("code", code);
								} else {
									Object value = jsonObject2.get(key);
									map.put(key, value);
								}
							}
							list.add(map);
						}
						Message message = Message.obtain();

						message.obj = list;

						family_handler.sendMessage(message);
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
				view = layoutInflater.inflate(R.layout.policy_info_item, null);
			} else {
				view = convertView;
			}

			TextView p_i_name = (TextView) view.findViewById(R.id.p_i_name);

			TextView p_i_merchant = (TextView) view
					.findViewById(R.id.p_i_merchant);
			TextView p_i_num = (TextView) view.findViewById(R.id.p_i_num);
			TextView p_i_date = (TextView) view.findViewById(R.id.p_i_date);
			TextView p_i_insured = (TextView) view
					.findViewById(R.id.p_i_insured);
			TextView p_i_sum = (TextView) view.findViewById(R.id.p_i_sum);

			p_i_name.setText(list.get(position).get("product_name").toString());
			p_i_merchant.setText(list.get(position).get("company").toString());
			p_i_num.setText(list.get(position).get("policy_no").toString());
			p_i_date.setText(list.get(position).get("cooling_begin").toString());
			p_i_insured.setText(list.get(position).get("holder_name").toString());
			p_i_sum.setText(list.get(position).get("premium").toString());

			return view;
		}

	}

	public class CpAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, Object>> list = null;

		public CpAdapter(Context context) {
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
				view = layoutInflater.inflate(R.layout.policy_info_item, null);
			} else {
				view = convertView;
			}

			TextView p_i_name = (TextView) view.findViewById(R.id.p_i_name);

			TextView p_i_merchant = (TextView) view
					.findViewById(R.id.p_i_merchant);

			TextView p_i_num = (TextView) view.findViewById(R.id.p_i_num);
			TextView p_i_date = (TextView) view.findViewById(R.id.p_i_date);
			TextView p_i_insured = (TextView) view
					.findViewById(R.id.p_i_insured);
			TextView p_i_sum = (TextView) view.findViewById(R.id.p_i_sum);

			p_i_name.setText(list.get(position).get("product_name").toString());
			p_i_merchant.setText(list.get(position).get("company").toString());
			p_i_num.setText(list.get(position).get("policy_no").toString());

			String time = list.get(position).get("insurance_period").toString();
			if (time.length() > 28) {
				String timeStr = time.substring(0, 28) + "...";

				p_i_date.setText(timeStr);

			} else {
				p_i_date.setText(time);
			}

			p_i_insured.setText(list.get(position).get("holder_name").toString());
			p_i_sum.setText(list.get(position).get("premium").toString());

			return view;
		}

	}

	private void getRefreshItem() {
		Log.d("444444", p_pnum + "");
		if (p_list_button.equals("01")) {
			p_pnum = 0;
			
			key_value.put("page_no", "0");
			key_value.put("policy_type", "life");
			final String policyurl = IpConfig.getUri2("getPolicyList");
			setpolicyList(policyurl);

		} else {

			c_pnum = 0;
		
			key_value.put("page_no", "0");
			key_value.put("policy_type", "car");
			final String policyurl = IpConfig.getUri2("getPolicyList");
			setpolicyList(policyurl);

		}

	}

	private void getLoadMoreItem() {
		if (p_list_button.equals("01")) {
			p_pnum++;
			Log.d("555555", p_pnum + "");

			key_value.put("page_no", p_pnum + "");
			key_value.put("policy_type", "life");
			final String policyurl = IpConfig.getUri2("getPolicyList");
			setpolicyList(policyurl);

		} else {
			c_pnum++;
			Log.d("555555", c_pnum + "");

			key_value.put("page_no", c_pnum + "");
			key_value.put("policy_type", "car");
			final String policyurl = IpConfig.getUri2("getPolicyList");
			setpolicyList(policyurl);
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {

				per_policy_xlist.stopLoadMore();
				car_policy_xlist.stopLoadMore();
				getRefreshItem();
				if (p_list_button.equals("01")) {
					madapter.notifyDataSetChanged();
					
					onLoad();
				} else {
					c_p_adapter.notifyDataSetChanged();
					
					c_p_Load();
				}

			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			public void run() {

				per_policy_xlist.stopRefresh();
				car_policy_xlist.stopRefresh();
				getLoadMoreItem();

				// adapter.notifyDataSetChanged();

				// mListView.stopLoadMore();

			}
		}, 2000);
	}

	private void onLoad() {

		per_policy_xlist.stopRefresh();

		per_policy_xlist.stopLoadMore();

		per_policy_xlist.setRefreshTime("刚刚");

	}

	private void c_p_Load() {

		car_policy_xlist.stopRefresh();

		car_policy_xlist.stopLoadMore();

		car_policy_xlist.setRefreshTime("刚刚");

	}

	private class ListAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, Object>> list = null;

		public ListAdapter(Context context) {
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
						.inflate(R.layout.policy_family_item, parent, false);
			} else {
				view = convertView;
			}

			TextView p_f_relation = (TextView) view
					.findViewById(R.id.p_f_relation);

			TextView p_f_name = (TextView) view.findViewById(R.id.p_f_name);

			TextView p_f_brith = (TextView) view.findViewById(R.id.p_f_brith);
			TextView p_f_age = (TextView) view.findViewById(R.id.p_f_age);

			p_f_relation.setText(list.get(position).get("relation").toString());
			p_f_name.setText(list.get(position).get("name").toString());

			String brithstr = list.get(position).get("birthday").toString();
			String agestr = list.get(position).get("age").toString();

			if (brithstr == null || brithstr.equals("null")
					|| brithstr.equals("")) {
				p_f_brith.setText("未知");
			} else {
				p_f_brith.setText(brithstr);
			}
			if (agestr == null || agestr.equals("null") || agestr.equals("")) {
				p_f_age.setText("未知");
			} else {
				p_f_age.setText(agestr);
			}

			// p_f_brith.setText(list.get(position).get("birthday").toString());
			// p_f_age.setText(list.get(position).get("age").toString());

			return view;
		}

	}

}
