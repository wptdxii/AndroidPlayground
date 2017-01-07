package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.adapter.RecProductAdapter;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.iosalertview.CustomDialog;
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

public class RecProductDetailActivity extends BaseActivity {

	private String loginString;
	private RecProductAdapter adapter;
	private TextView age;
	private RelativeLayout age_rel;
	private String[] ages_checked;
	private String[] ages_code;
	private String[] ages_name;
	private String[] job,job_code;
	private Dialog dialog;
	private RelativeLayout hot_item_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private TextView immediately_insure;
	private Map<String, String> key_value = new HashMap<String, String>();
	private LinearLayout layout;
	private List<Map<String, String>> list;
	private ListView listViewSpinner;
	private String[] package_check;
	private String[] package_code;
	private String package_id = "";
	private String[] package_name;
	private RelativeLayout package_rel;
	private TextView package_text;
	private TextView period;
	private String[] period_checked;
	private String[] period_code;
	private String period_id = "";
	private String[] period_name;
	private RelativeLayout period_rel;
	private PopupWindow popupWindow;
	private View popview;
	private String price = "";
	private TextView price_basic;
	private ListViewScrollView price_list;
	private String product_id = "";
	private TextView profession;
	private String rate_id = "";

	//	private String token;
//	private String user_id;
	private String image_url;
	private ImageView iv_safe_plan;
	private TextView tv_insurance_title;
	private TextView tv_insurance_descrip;
	private String insurance_title;
	private String insurance_descrip;
	private String[] basics={};
	private String basicDesc="";
	private String feature="";
	private TextView tv_product_fetures;
	private RelativeLayout security_insure_notice;
	private RelativeLayout product_terms;
	private String insurance_information;
	private String clause;
	private String selling="";
	private RelativeLayout s_p_rel_below;
	private ImageView arrow1;
	private boolean isInsuranceFeeShown=true;
	private ImageView arrow5;

	private RelativeLayout profession_rel;

	private RelativeLayout  s_p_rel1;

	private RelativeLayout expand_rel;
	private ImageView expand_img;

	public static Boolean morePriceShow = false;
	private Handler change_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			Map<String, Object> data = (Map<String, Object>) msg.obj;
			dialog.dismiss();
			list.clear();
			adapter.notifyDataSetChanged();


			List<Map<String, String>>	list2 = (List<Map<String, String>>) data.get("BenifitList");



			Log.d("455454", "888" + list2.size());
			price = (String) data.get("money");
			price_basic.setText("￥" + price);

			adapter.setData(list2);
			price_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();


			final int price_item_num=list2.size();
			if(price_item_num>2){
				expand_img.setVisibility(View.VISIBLE);
			}else {
				expand_img.setVisibility(View.GONE);
			}
			expand_rel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (price_item_num > 2) {
						if (morePriceShow) {
							expand_img.setImageResource(R.drawable.expand_down);
							morePriceShow=false;
							adapter.notifyDataSetChanged();
						} else {
							expand_img.setImageResource(R.drawable.expand_up);
							morePriceShow=true;
							adapter.notifyDataSetChanged();
						}
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
			dialog.dismiss();
			Toast.makeText(RecProductDetailActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

	@SuppressLint("HandlerLeak")
	private Handler errcode_handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;
			dialog.dismiss();
			String status = data;

			Log.d("455454", "455445" + status);
			if (status.equals("false")) {

				Toast.makeText(RecProductDetailActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			Map<String, Object> data = (Map<String, Object>) msg.obj;
			dialog.dismiss();

			tv_insurance_title.setText(insurance_title);
			tv_insurance_descrip.setText(insurance_descrip);
			if(null!=feature&&!"null".equals(feature)&&!"".equals(feature)){
				tv_product_fetures.setText(feature);
			}

			String url= IpConfig.getIp3()+"/"+image_url;
//			ImageLoader.getInstance().displayImage(url,
//					iv_safe_plan);
			Glide.with(RecProductDetailActivity.this)
					.load(url) //占位图 图片正在加载
					.into(iv_safe_plan);








			price = (String) data.get("money");
			price_basic.setText("￥" + price);

			list = (List<Map<String, String>>) data.get("BenifitList");



			expand_img=(ImageView) findViewById(R.id.expand_img);


			adapter.setData(list);
			price_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();


			final int price_item_num=list.size();
			if(price_item_num>2){
				expand_img.setVisibility(View.VISIBLE);
			}
			expand_rel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (price_item_num > 2) {
						if (morePriceShow) {
							expand_img.setImageResource(R.drawable.expand_down);
							morePriceShow=false;
							adapter.notifyDataSetChanged();
						} else {
							expand_img.setImageResource(R.drawable.expand_up);
							morePriceShow=true;
							adapter.notifyDataSetChanged();
						}
					}
				}
			});

			List<String> ages_list1 = (List<String>) data.get("ages_list1");
			List<String> ages_list2 = (List<String>) data.get("ages_list2");
			List<String> ages_list3 = (List<String>) data.get("ages_list3");
			List<String> package_list1 = (List<String>) data
					.get("package_list1");
			List<String> package_list2 = (List<String>) data
					.get("package_list2");
			List<String> package_list3 = (List<String>) data
					.get("package_list3");
			List<String> period_list1 = (List<String>) data.get("period_list1");
			List<String> period_list2 = (List<String>) data.get("period_list2");
			List<String> period_list3 = (List<String>) data.get("period_list3");

			package_code = package_list1
					.toArray(new String[package_list1.size()]);
			package_name = package_list2
					.toArray(new String[package_list2.size()]);
			package_check = package_list3
					.toArray(new String[package_list3.size()]);

			period_code = period_list1
					.toArray(new String[period_list1.size()]);
			period_name = period_list2
					.toArray(new String[period_list2.size()]);
			period_checked = period_list3
					.toArray(new String[period_list3.size()]);
			ages_code = ages_list1.toArray(new String[ages_list1
					.size()]);
			ages_name = ages_list2.toArray(new String[ages_list2
					.size()]);
			ages_checked = ages_list3.toArray(new String[ages_list3
					.size()]);

			if (package_code.length > 0 && period_code.length > 0
					&& ages_code.length > 0) {
				key_value.put("rate", ages_code[0]);
				key_value.put("package", package_code[0]);
				key_value.put("period", period_code[0]);
				package_id = package_code[0];
				period_id = period_code[0];
				rate_id = ages_code[0];
				age.setText(ages_name[0]);
				period.setText(period_name[0]);
				package_text.setText(package_name[0]);
			}

			age_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if ((package_code.length > 0) && (period_code.length > 0)
							&& (ages_code.length > 0)) {
						showPopupWindow(ages_code, ages_name, "1");
					}

				}
			});

			period_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if ((package_code.length > 0) && (period_code.length > 0)
							&& (ages_code.length > 0)) {
						showPopupWindow(period_code, period_name, "2");
					}

				}
			});

			package_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if ((package_code.length > 0) && (period_code.length > 0)
							&& (ages_code.length > 0)) {
						showPopupWindow(package_code, package_name, "3");
					}

				}
			});

		}

	};


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		setContentView(R.layout.rec_p_detail);


//		user_id = sp.getString("Login_UID", "");
//		token = sp.getString("Login_TOKEN", "");
		loginString = sp.getString("Login_STATE", "none");
		Intent intent = getIntent();

		product_id = intent.getStringExtra("product_id");

		init();
		initEvent();

	}

	void init() {
		popview = findViewById(R.id.popview);
		price_list = (ListViewScrollView) findViewById(R.id.price_list);
		age_rel = (RelativeLayout) findViewById(R.id.age_rel);
		period_rel = (RelativeLayout) findViewById(R.id.period_rel);
		package_rel = (RelativeLayout) findViewById(R.id.package_rel);
		hot_item_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("产品详情");
		age = (TextView) findViewById(R.id.age);
		profession = (TextView) findViewById(R.id.profession);
		period = (TextView) findViewById(R.id.period);
		package_text = (TextView) findViewById(R.id.package_text);
		price_basic = (TextView) findViewById(R.id.price_basic);
		immediately_insure = (TextView) findViewById(R.id.immediately_insure);
		iv_safe_plan=(ImageView)findViewById(R.id.iv_safe_plan);
		tv_insurance_title=(TextView) findViewById(R.id.tv_insurance_description);
		tv_insurance_descrip=(TextView) findViewById(R.id.tv_insurance_descrip);
		tv_product_fetures=(TextView) findViewById(R.id.tv_product_fetures);
		security_insure_notice=(RelativeLayout) findViewById(R.id.security_insure_notice);
		product_terms= (RelativeLayout) findViewById(R.id.product_terms);
		s_p_rel_below=(RelativeLayout) findViewById(R.id.s_p_rel_below);
		arrow1=(ImageView)findViewById(R.id.arrow1);
		arrow5=(ImageView)findViewById(R.id.arrow5);
		profession_rel=(RelativeLayout) findViewById(R.id.profession_rel);

		s_p_rel1=(RelativeLayout) findViewById(R.id.s_p_rel1);


		expand_rel=(RelativeLayout) findViewById(R.id.expand_rel);
		expand_img=(ImageView) findViewById(R.id.expand_img);


		adapter = new RecProductAdapter(this);

		dialog = new Dialog(this,R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");


	}

	void initEvent() {




		key_value.put("product_id", product_id);
//		key_value.put("user_id", user_id);
//		key_value.put("token", token);
//		Log.d("getRecProductDetail--------------",product_id+"----"+user_id+"----"+token);
		dialog.show();
		setdata(IpConfig.getUri("getRecProductDetail"));

		hot_item_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});

		s_p_rel1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(isInsuranceFeeShown){
					arrow1.setImageResource(R.drawable.arrow_down);
					s_p_rel_below.setVisibility(View.GONE);
					isInsuranceFeeShown=false;
				}else{
					arrow1.setImageResource(R.drawable.arrow_up);
					s_p_rel_below.setVisibility(View.VISIBLE);
					isInsuranceFeeShown=true;
				}
			}
		});



		profession_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(job_code.length>0){
					showPopupWindow(job_code, job, "4");
				}




			}
		});

		immediately_insure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (loginString.equals("none")) {

					Intent intent = new Intent();
					intent.setClass(RecProductDetailActivity.this,
							LoginActivity.class);
					RecProductDetailActivity.this.startActivity(intent);

				} else {
					if (price.equals("")) {

						CustomDialog.Builder builder = new CustomDialog.Builder(
								RecProductDetailActivity.this);

						builder.setTitle("提示");
						builder.setMessage("保费试算价格不能为空");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {

										dialog.dismiss();

									}
								});
						builder.create().show();

					} else if (rate_id.equals("")) {

						CustomDialog.Builder builder = new CustomDialog.Builder(
								RecProductDetailActivity.this);

						builder.setTitle("提示");
						builder.setMessage("被保人年龄不能为空");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {

										dialog.dismiss();

									}
								});
						builder.create().show();

					} else {

						Intent intent = new Intent();

						intent.putExtra("price", price);
						intent.putExtra("product_id", product_id);
						intent.putExtra("package", package_id);
						intent.putExtra("period", period_id);
						intent.putExtra("rate", rate_id);
						intent.putExtra("title", insurance_title);

						intent.setClass(RecProductDetailActivity.this,SubmitOrdersActivity.class);

						RecProductDetailActivity.this.startActivity(intent);

					}

				}}
		});

		security_insure_notice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(insurance_information !=null && !insurance_information.equals("")
						&& !insurance_information.equals("null")) {

					Intent intent=new Intent(RecProductDetailActivity.this,InsuranceNoticeActivity.class);
					intent.putExtra("web_url", insurance_information);
					startActivity(intent);
				}
			}
		});

		product_terms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(clause !=null && !clause.equals("")
						&& !clause.equals("null")) {

					Intent intent=new Intent(RecProductDetailActivity.this,ProductClauseActivity.class);
					intent.putExtra("clause", clause);
					startActivity(intent);
				}
			}
		});

		final String joburl = IpConfig.getUri("getJob");
		setjobcode(joburl);

	}

	private void setjobcode(String url) {

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

							Log.d("44444", jsonString);
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
								String code = "";
								String jobname = "";
								for (int i = 0; i < dataList.length(); i++) {
									JSONObject jsonObject2 = dataList
											.getJSONObject(i);

									code = code + "|"
											+ jsonObject2.getString("code");
									jobname = jobname + "|"
											+ jsonObject2.getString("name");

								}
								if(!jobname.isEmpty()){
									Log.i("jjobname------", jobname);
									job=jobname.split("\\|");
									job_code=code.split("\\|");
									Log.i("job_code的长度-------", job_code.length+"");
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
		layout = (LinearLayout) LayoutInflater.from(
				RecProductDetailActivity.this).inflate(R.layout.dialog, null);
		listViewSpinner = (ListView) layout.findViewById(R.id.lv_dialog);
		listViewSpinner.setAdapter(new ArrayAdapter<String>(
				RecProductDetailActivity.this, R.layout.text, R.id.tv_text,
				name));

		popupWindow = new PopupWindow(RecProductDetailActivity.this);
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

		// popupWindow.showAsDropDown(bt_order);
		popupWindow.showAsDropDown(popview, (-getResources()
				.getDimensionPixelSize(R.dimen.comission_popup_width)) / 2, 0);

		listViewSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {

				arg0.setVisibility(View.VISIBLE);
				if (num.equals("1")) {
					age.setText(name[pos]);
					rate_id = code[pos];
					key_value.put("rate", code[pos]);
					dialog.show();
					String url = IpConfig.getUri("getAmountBenifit");
					setchangedata(url);
				} else if (num.equals("2")) {
					period.setText(name[pos]);
					key_value.put("period", code[pos]);
					period_id = code[pos];
					dialog.show();
					String url = IpConfig.getUri("getAmountBenifit");
					setchangedata(url);
				} else if(num.equals("3")){
					package_text.setText(name[pos]);
					package_id = code[pos];
					Log.d("99999",code[pos]+"99999");
					key_value.put("package", code[pos]);
					dialog.show();
					String url = IpConfig.getUri("getAmountBenifit");
					setchangedata(url);
				}else if(num.equals("4")){
					profession.setText(job[pos]);
				}

				popupWindow.dismiss();
				popupWindow = null;
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
						List<Map<String, String>> price_list = new ArrayList<Map<String, String>>();
						Map<String, Object> map_all = new HashMap<String, Object>();
						Map<String, String> errcode_map = new HashMap<String, String>();

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
								if (!errcode.equals("0")) {
									String errmsg = jsonObject.getString("errmsg");

									errcode_map.put("errcode", errcode);
									errcode_map.put("errmsg", errmsg);

									Message message2 = Message.obtain();

									message2.obj = errcode_map;

									null_handler.sendMessage(message2);

								} else {
									JSONObject dataObject = jsonObject.getJSONObject("data");
									JSONArray package_list = dataObject
											.getJSONArray("package_list");
									insurance_title=dataObject.getString("tittle");
									insurance_descrip=dataObject.getString("description");
									image_url=dataObject.getString("img_url");
									feature=dataObject.getString("feature");
									selling=dataObject.getString("selling");
									insurance_information=dataObject.getString("insurance_information");
									clause=dataObject.getString("clause");
									JSONArray basic=dataObject.getJSONArray("basic");
									String tempBasic=basic.toString().replace("[", "").replace("]", "").replace("\"", "");
									basics=tempBasic.split(",");





									ArrayList<String> package_list1 = new ArrayList<String>();
									ArrayList<String> package_list2 = new ArrayList<String>();
									ArrayList<String> package_list3 = new ArrayList<String>();

									for (int i = 0; i < package_list.length(); i++) {
										JSONObject jsonObject2 = package_list.getJSONObject(i);
										Map<String, String> map = new HashMap<String, String>();

										Iterator<String> iterator = jsonObject2.keys();
										while (iterator.hasNext()) {
											String key = iterator.next();

											if (key.equals("code")) {
												package_list1.add(jsonObject2
														.getString("code"));

											} else if (key.equals("name")) {
												package_list2.add(jsonObject2
														.getString("name"));

											} else if (key.equals("checked")) {
												package_list3.add(jsonObject2
														.getString("checked"));
											}
										}

									}

									ArrayList<String> period_list1 = new ArrayList<String>();
									ArrayList<String> period_list2 = new ArrayList<String>();
									ArrayList<String> period_list3 = new ArrayList<String>();

									JSONArray period_list = dataObject.getJSONArray("period_list");
									for (int i = 0; i < period_list.length(); i++) {
										JSONObject jsonObject2 = period_list.getJSONObject(i);
										Map<String, String> map = new HashMap<String, String>();

										Iterator<String> iterator = jsonObject2.keys();
										while (iterator.hasNext()) {
											String key = iterator.next();

											if (key.equals("code")) {
												period_list1.add(jsonObject2
														.getString("code"));

											} else if (key.equals("name")) {
												period_list2.add(jsonObject2
														.getString("name"));

											} else if (key.equals("checked")) {
												period_list3.add(jsonObject2
														.getString("checked"));
											}
										}

									}


									JSONArray benifitList = dataObject
											.getJSONArray("benifit");
									String money = dataObject.getString("charge");

									for (int i = 0; i < benifitList.length(); i++) {
										JSONObject jsonObject2 = benifitList
												.getJSONObject(i);
										Map<String, String> map = new HashMap<String, String>();

										Iterator<String> iterator = jsonObject2.keys();
										while (iterator.hasNext()) {
											String key = iterator.next();
											String value = jsonObject2.getString(key);
											map.put(key, value);
										}
										price_list.add(map);
									}



									JSONArray ages_list = dataObject
											.getJSONArray("ages_list");

									ArrayList<String> ages_list1 = new ArrayList<String>();
									ArrayList<String> ages_list2 = new ArrayList<String>();
									ArrayList<String> ages_list3 = new ArrayList<String>();

									for (int i = 0; i < ages_list.length(); i++) {
										JSONObject jsonObject2 = ages_list
												.getJSONObject(i);
										Map<String, String> map = new HashMap<String, String>();

										Iterator<String> iterator = jsonObject2.keys();
										while (iterator.hasNext()) {

											String key = iterator.next();

											if (key.equals("code")) {
												ages_list1.add(jsonObject2
														.getString("code"));

											} else if (key.equals("name")) {
												ages_list2.add(jsonObject2
														.getString("name"));

											} else if (key.equals("checked")) {
												ages_list3.add(jsonObject2
														.getString("checked"));
											}
										}

									}


									map_all.put("package_list1", package_list1);
									map_all.put("package_list2", package_list2);
									map_all.put("package_list3", package_list3);
									map_all.put("period_list1", period_list1);
									map_all.put("period_list2", period_list2);
									map_all.put("period_list3", period_list3);
									map_all.put("ages_list1", ages_list1);
									map_all.put("ages_list2", ages_list2);
									map_all.put("ages_list3", ages_list3);
									map_all.put("BenifitList", price_list);
									map_all.put("money", money);

									Message message = Message.obtain();

									message.obj = map_all;

									handler.sendMessage(message);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});





	}


	@Override
	public void onRestart() {
		super.onRestart();

		loginString = sp.getString("Login_STATE", "none");
//		user_id = sp.getString("Login_UID", "");
//		token = sp.getString("Login_TOKEN", "");
	}

	private void setchangedata(String url) {


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
						List<Map<String, String>> price_list = new ArrayList<Map<String, String>>();
						Map<String, Object> map_all = new HashMap<String, Object>();

						try {


							if (jsonString == null || jsonString.equals("")
									|| jsonString.equals("null")) {
								String status = "false";
								Message message = Message.obtain();

								message.obj = status;

								errcode_handler.sendMessage(message);
							} else {
								JSONObject jsonObject = new JSONObject(jsonString);
								JSONObject dataObject = jsonObject
										.getJSONObject("data");

								JSONArray benifitList = dataObject
										.getJSONArray("benifit");
								String money = dataObject.getString("money");

								for (int i = 0; i < benifitList.length(); i++) {
									JSONObject jsonObject2 = benifitList
											.getJSONObject(i);
									Map<String, String> map = new HashMap<String, String>();

									Iterator<String> iterator = jsonObject2.keys();
									while (iterator.hasNext()) {
										String key = iterator.next();
										String value = jsonObject2.getString(key);
										map.put(key, value);
									}
									price_list.add(map);
								}

								map_all.put("BenifitList", price_list);
								map_all.put("money", money);

								Message message = Message.obtain();

								message.obj = map_all;

								change_handler.sendMessage(message);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});



	}



}
