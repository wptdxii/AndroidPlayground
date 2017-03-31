package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
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

import static com.cloudhome.R.drawable.completed;

public class CheckPolicyDetailsActivity extends BaseActivity {


	private String user_id;
	private String token;
	private Map<String, String> key_value = new HashMap<String, String>();
	private Dialog dialog;

	private TextView insurance_code, company, period_from, period_to,
			product_name_txt;
	private ImageView arrow_img;
	private RelativeLayout c_p_d_back;
	private RelativeLayout rl_right;
	private TextView tv_text;

	private RelativeLayout h_i_rel, benefit_rel, emergency_rel, items_rel;
	private String order_id;
	@SuppressLint("HandlerLeak")
	private Handler errcode_handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;
			dialog.dismiss();

			Log.d("455454", "455445" + data);
			if (data.equals("false")) {

				Toast.makeText(CheckPolicyDetailsActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			Map<String, Object> data = (Map<String, Object>) msg.obj;

			dialog.dismiss();
			// List<Map<String, String>> pricelist = (List<Map<String, String>>)
			// data
			// .get("price_list");
			final List<Map<String, String>> holderlist = (List<Map<String, String>>) data
					.get("holder_list");
			final List<Map<String, String>> insuredlist = (List<Map<String, String>>) data
					.get("insured_list");

			final List<Map<String, String>> emergencylist = (List<Map<String, String>>) data
					.get("emergency_list");
			final List<Map<String, String>> benefitlist = (List<Map<String, String>>) data
					.get("benefit_list");

			
		
			
			final ArrayList<String> product_nameList = (ArrayList<String>) data
					.get("product_nameList");
			final ArrayList<ArrayList<HashMap<String, String>>> product_priceList = (ArrayList<ArrayList<HashMap<String, String>>>) data
					.get("product_priceList");
			
			
			product_name_txt.setText((String) data.get("product_name"));

			period_from.setText(data.get("period_from") + "至");

			period_to.setText((String) data.get("period_to"));

			insurance_code.setText((String) data.get("insurance_code"));

			company.setText((String) data.get("company"));

			h_i_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Intent intent = new Intent();

					intent.putExtra("holdername",
							holderlist.get(0).get("holder_title"));
					intent.putExtra("holdernum",
							holderlist.get(0).get("holder_value"));

					intent.putExtra("insuredname",
							insuredlist.get(0).get("insured_title"));
					intent.putExtra("insurednum",
							insuredlist.get(0).get("insured_value"));

					intent.setClass(CheckPolicyDetailsActivity.this,
							Holder_InsuredActivity.class);

					CheckPolicyDetailsActivity.this.startActivity(intent);

				}
			});

			benefit_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Intent intent = new Intent();

					intent.putExtra("benefit",
							benefitlist.get(0).get("benefit_value"));

					intent.setClass(CheckPolicyDetailsActivity.this,
							C_P_BenefitActivity.class);

					CheckPolicyDetailsActivity.this.startActivity(intent);

				}
			});

			emergency_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Intent intent = new Intent();

					intent.putExtra("emergencyname",
							emergencylist.get(0).get("emergency_title"));
					intent.putExtra("emergencynum",
							emergencylist.get(0).get("emergency_value"));

					intent.setClass(CheckPolicyDetailsActivity.this,
							C_P_EmergencyActivity.class);

					CheckPolicyDetailsActivity.this.startActivity(intent);

				}
			});

	

			items_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					
					
					Intent intent = new Intent();

					intent.putStringArrayListExtra("product_nameList",product_nameList);
					intent.putExtra("product_priceList",product_priceList);

					intent.setClass(CheckPolicyDetailsActivity.this,
							C_P_I_P_Activity.class);

					CheckPolicyDetailsActivity.this.startActivity(intent);
					
					
				}
			});
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.check_policy_details);

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");

		init();
		initEvent();

	}

	private void init() {

		key_value.put("order_id", order_id);
		key_value.put("user_id", user_id);
		key_value.put("token", token);

	

		  dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("请稍后...");
          
		c_p_d_back = (RelativeLayout) findViewById(R.id.rl_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_share);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_title);
		tv_text.setText("保单详情");
		company = (TextView) findViewById(R.id.company);
		period_from = (TextView) findViewById(R.id.period_from);
		period_to = (TextView) findViewById(R.id.period_to);
		product_name_txt = (TextView) findViewById(R.id.product_name_txt);
		insurance_code = (TextView) findViewById(R.id.insurance_code);
		h_i_rel = (RelativeLayout) findViewById(R.id.h_i_rel);
		benefit_rel = (RelativeLayout) findViewById(R.id.benefit_rel);
		emergency_rel = (RelativeLayout) findViewById(R.id.emergency_rel);
		items_rel = (RelativeLayout) findViewById(R.id.items_rel);
	}

	private void initEvent() {

		c_p_d_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

		dialog.show();
		final String url = IpConfig.getUri("getRecPolicyDetail");
		setData(url);

	}

	private void setData(String url) {

		
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

				Map<String, Object> map_all = new HashMap<String, Object>();

				List<Map<String, String>> holder_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> insured_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> emergency_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> benefit_list = new ArrayList<Map<String, String>>();

				ArrayList<String> product_nameList = new ArrayList<String>();

				ArrayList<ArrayList<HashMap<String, String>>> product_priceList = new ArrayList<ArrayList<HashMap<String, String>>>();

				try {

					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {

						JSONObject jsonObject = new JSONObject(jsonString);
						JSONObject dataObject = jsonObject
								.getJSONObject("data");

						JSONArray itemsArray = dataObject
								.getJSONArray("items");

						for (int i = 0; i < itemsArray.length(); i++) {
							JSONObject jsonObject2 = itemsArray
									.getJSONObject(i);

							Iterator<String> iterator = jsonObject2.keys();
							while (iterator.hasNext()) {

								String key = iterator.next();
								if (key.equals("name")) {
									String value = jsonObject2
											.getString(key);
									product_nameList.add(value);
								} else {

									ArrayList<HashMap<String, String>> price_list = new ArrayList<HashMap<String, String>>();

									JSONArray innerArray = jsonObject2
											.getJSONArray("inner");

									for (int j = 0; j < innerArray.length(); j++) {

										HashMap<String, String> map = new HashMap<String, String>();

										JSONArray jsonArray1 = innerArray
												.getJSONArray(j);

										map.put("policy_name",
												jsonArray1.getString(0));

										map.put("policy_price",
												jsonArray1.getString(1));

										price_list.add(map);
									}

									product_priceList.add(price_list);

								}

							}

						}

						JSONArray holderArray = dataObject
								.getJSONArray("holder");

						for (int i = 0; i < holderArray.length(); i++) {

							Map<String, String> map = new HashMap<String, String>();
							JSONArray jsonArray1 = holderArray
									.getJSONArray(i);

							map.put("holder_title", jsonArray1.getString(0));
							map.put("holder_value", jsonArray1.getString(1));

							holder_list.add(map);
						}

						JSONArray insuredArray = dataObject
								.getJSONArray("insured");

						for (int i = 0; i < insuredArray.length(); i++) {

							Map<String, String> map = new HashMap<String, String>();
							JSONArray jsonArray1 = insuredArray
									.getJSONArray(i);

							map.put("insured_title",
									jsonArray1.getString(0));
							map.put("insured_value",
									jsonArray1.getString(1));

							insured_list.add(map);
						}

						JSONArray benefitArray = dataObject
								.getJSONArray("benefit");

						Map<String, String> map3 = new HashMap<String, String>();

						map3.put("benefit_title", benefitArray.getString(0));
						map3.put("benefit_value", benefitArray.getString(1));

						benefit_list.add(map3);

						JSONArray emergencyArray = dataObject
								.getJSONArray("emergency");

						Map<String, String> map4 = new HashMap<String, String>();

						map4.put("emergency_title",
								emergencyArray.getString(0));
						map4.put("emergency_value",
								emergencyArray.getString(1));

						emergency_list.add(map4);

						// map_all.put("price_list", price_list);
						map_all.put("holder_list", holder_list);
						map_all.put("insured_list", insured_list);
						map_all.put("emergency_list", emergency_list);
						map_all.put("benefit_list", benefit_list);

						String insurance_code = dataObject
								.getString("insurance_code");
						String product_name = dataObject
								.getString("product_name");
						String period_from = dataObject
								.getString("period_from");
						String period_to = dataObject
								.getString("period_to");
						String company = dataObject.getString("company");

						map_all.put("insurance_code", insurance_code);
						map_all.put("product_name", product_name);
						map_all.put("period_from", period_from);
						map_all.put("period_to", period_to);
						map_all.put("company", company);

						map_all.put("product_nameList", product_nameList);
						map_all.put("product_priceList", product_priceList);

						Message message = Message.obtain();

						message.obj = map_all;
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
	

	}



}
