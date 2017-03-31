package com.cloudhome.activity;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.LinearLayoutForListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;

public class C_P_O_DetailsActivity extends BaseActivity {


	private String user_id;
	private String token;
	private Map<String, String> key_value = new HashMap<String, String>();

	private MyAdapter adapter;
	private TextView s_a_car_no, s_a_owner_name, s_a_engine_no, s_a_vin,id_no,
	toubao_num,per_price,p_price,period1,period2,period_syx1,period_syx2,unit_price,the_copies,total_price;
	private RelativeLayout s_p_rel1,s_p_rel_below,a_q_rel,tou_bei_bao_ren_rel;
	private Dialog dialog;
	private String  order_id,price_id,toubao_statusstr;
	private ImageView arrow_img;
	private RelativeLayout c_p_orders_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private LinearLayoutForListView price_list;
	private TextView policy_for_name,card_no,card_type,birthday,sex,mobile,email,insured_relationship,insured_name
	,insured_card_type,insured_card_no,insured_birthday,insured_card_sex,insured_mobile,insured_email,emergency_contact_tel,emergency_contact_name
	,beneficiary_name,remarks_type,remarks,insured_card_person_type,toubao_status;
	
	List<Map<String, String>> pricelist = new ArrayList<Map<String, String>>();
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

				Toast.makeText(C_P_O_DetailsActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			 Map<String, Object> data = (Map<String, Object>) msg.obj;
			 
			 
			 pricelist =(List<Map<String, String>>)data.get("pricelist");
			
			 policy_for_name.setText((String)data.get("policy_for_name"));
			 card_no.setText((String)data.get("card_no"));
			 card_type.setText((String)data.get("card_type"));
			 birthday.setText((String)data.get("birthday"));
			 sex.setText((String)data.get("sex"));
			 mobile.setText((String)data.get("mobile"));
			 
			 email.setText((String)data.get("email"));
			 insured_relationship.setText((String)data.get("insured_relationship"));
			 insured_name.setText((String)data.get("insured_name"));
			 insured_card_type.setText((String)data.get("insured_card_type"));
			 insured_card_no.setText((String)data.get("insured_card_no"));
			 insured_birthday.setText((String)data.get("insured_birthday")); 
			 insured_card_sex.setText((String)data.get("insured_card_sex"));
			 insured_mobile.setText((String)data.get("insured_mobile"));
			 insured_email.setText((String)data.get("insured_email"));
			 
			 emergency_contact_tel.setText((String)data.get("emergency_contact_tel")); 
			 emergency_contact_name.setText((String)data.get("emergency_contact_name"));
			 beneficiary_name.setText((String)data.get("beneficiary_name"));
			 remarks_type.setText((String)data.get("remarks_type"));
			 
			 remarks.setText((String)data.get("remarks"));
			 
			 insured_card_person_type.setText((String)data.get("insured_card_person_type"));
			 
			 s_a_car_no.setText((String)data.get("car_msg"));
			 s_a_owner_name.setText((String)data.get("holder_name"));
			 
			 id_no.setText((String)data.get("holder_card_id"));
			 s_a_engine_no.setText((String)data.get("engine_no"));
			 s_a_vin.setText((String)data.get("cj_no"));
		
			 toubao_num.setText("1");
			
			 
			 
				String period_str = (String)data.get("period");
				int i = 0;
				String split = "至";
				StringTokenizer token = new StringTokenizer(period_str, split);

				String[] period_array = new String[token.countTokens()];

				while (token.hasMoreTokens()) {

					period_array[i] = token.nextToken();

					i++;
				}

				String period_syx_str = (String)data.get("period_syx");

				int j = 0;

				StringTokenizer token2 = new StringTokenizer(period_syx_str, split);

				String[] period_syx_array = new String[token2.countTokens()];

				while (token2.hasMoreTokens()) {

					period_syx_array[j] = token2.nextToken();

					j++;
				}

				if (period_array.length < 2) {
					period1.setText(period_str);
				} else {
					period1.setText(period_array[0] + "至");
					period2.setText(period_array[1]);
				}
				if (period_syx_array.length < 2) {
					period1.setText(period_syx_str);
				} else {

					period_syx1.setText(period_syx_array[0] + "至");
					period_syx2.setText(period_syx_array[1]);
				}
		
			 
			
			 
				String total_amount = (String)data.get("total_amount");
				if(!total_amount.equals("")&&!total_amount.equals("null")){
				
				 unit_price.setText("￥"+total_amount);
				 total_price.setText("￥"+total_amount);
				 
				 per_price.setText("￥"+total_amount);
				 
				 p_price.setText("￥"+total_amount);
				 
				}
				
			 
			 the_copies.setText("1");
			
			 
			 
			 dialog.dismiss();
			 adapter.setData(pricelist);

			 price_list.setAdapter(adapter);
			 adapter.notifyDataSetChanged();
			
		
			 
	
			 
			 s_p_rel1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(s_p_rel_below.isShown())
						{
							s_p_rel_below.setVisibility(View.GONE);
							a_q_rel.setVisibility(View.GONE);
							arrow_img.setImageResource(R.drawable.arrow_down);
							tou_bei_bao_ren_rel.setVisibility(View.GONE);
						}else{
							
							arrow_img.setImageResource(R.drawable.arrow_up);
							s_p_rel_below.setVisibility(View.VISIBLE);
							a_q_rel.setVisibility(View.VISIBLE);
							tou_bei_bao_ren_rel.setVisibility(View.VISIBLE);
						}
							
						
					}
				});
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.car_order_details);
		

		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");
		price_id = intent.getStringExtra("id");
		toubao_statusstr = intent.getStringExtra("toubao_status");
		key_value.put("order_id", order_id);
		key_value.put("price_id", price_id);
		
		Log.d("33333",order_id);
		Log.d("44444",price_id);
		
		init();
		initEvent();


	}


	void init() {

		c_p_orders_back =(RelativeLayout)findViewById(R.id.rl_back);

		rl_right = (RelativeLayout) findViewById(R.id.rl_share);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_title);
		tv_text.setText("车险订单详情");
		price_list=(LinearLayoutForListView)findViewById(R.id.lv_insurance_price);
		

		
		arrow_img = (ImageView) findViewById(R.id.arrow_img);
		policy_for_name=(TextView)findViewById(R.id.policy_for_name);
		card_no=(TextView)findViewById(R.id.card_no);
		card_type=(TextView)findViewById(R.id.card_type);
		birthday=(TextView)findViewById(R.id.birthday);
		sex=(TextView)findViewById(R.id.sex);
		mobile=(TextView)findViewById(R.id.mobile);
		email=(TextView)findViewById(R.id.email);
		insured_relationship=(TextView)findViewById(R.id.insured_relationship);
		insured_name=(TextView)findViewById(R.id.insured_name);
		insured_card_type=(TextView)findViewById(R.id.insured_card_type);
		insured_card_no=(TextView)findViewById(R.id.insured_card_no);
		insured_birthday=(TextView)findViewById(R.id.insured_birthday);
		insured_card_sex=(TextView)findViewById(R.id.insured_card_sex);
		
		insured_mobile=(TextView)findViewById(R.id.insured_mobile);
		insured_email=(TextView)findViewById(R.id.insured_email);
		
		
		emergency_contact_tel=(TextView)findViewById(R.id.emergency_contact_tel);
		
		emergency_contact_name=(TextView)findViewById(R.id.emergency_contact_name);
		
		beneficiary_name=(TextView)findViewById(R.id.beneficiary_name);
		
		remarks_type=(TextView)findViewById(R.id.remarks_type);
		
		remarks=(TextView)findViewById(R.id.tv_remark);
		insured_card_person_type=(TextView)findViewById(R.id.insured_card_person_type);
		toubao_status=(TextView)findViewById(R.id.toubao_status);
		
	
		
		toubao_num=(TextView)findViewById(R.id.toubao_num);
		per_price=(TextView)findViewById(R.id.per_price);
		p_price=(TextView)findViewById(R.id.tv_insurance_price);
		period1=(TextView)findViewById(R.id.period1);
		period2=(TextView)findViewById(R.id.period2);
		
		period_syx1=(TextView)findViewById(R.id.period_syx1);
		period_syx2=(TextView)findViewById(R.id.period_syx2);
		unit_price=(TextView)findViewById(R.id.unit_price);
		the_copies=(TextView)findViewById(R.id.the_copies);
		total_price=(TextView)findViewById(R.id.total_price);
		
		s_p_rel1=(RelativeLayout)findViewById(R.id.s_p_rel1);
		
		s_p_rel_below=(RelativeLayout)findViewById(R.id.s_p_rel_below);
		a_q_rel=(RelativeLayout)findViewById(R.id.rl_car_info);
		tou_bei_bao_ren_rel=(RelativeLayout)findViewById(R.id.tou_bei_bao_ren_rel);
		a_q_rel=(RelativeLayout)findViewById(R.id.rl_car_info);
		
		
		s_a_car_no = (TextView) findViewById(R.id.tv_license_num);
		s_a_owner_name = (TextView) findViewById(R.id.tv_owner_name);
		s_a_engine_no = (TextView) findViewById(R.id.tv_engine_num);
		s_a_vin = (TextView) findViewById(R.id.tv_identify_num);
		id_no = (TextView) findViewById(R.id.id_no);
		
		user_id = sp.getString("Login_UID", "");
		token   = sp.getString("Login_TOKEN", "");

		Log.d("2222",user_id);
		Log.d("2222",token);
		
		key_value.put("user_id", user_id);
		key_value.put("token", token);
	
		adapter = new MyAdapter(C_P_O_DetailsActivity.this);
		

		
	      dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("请稍后...");
          
		


	}
	void initEvent() {
		
		dialog.show();
		final String c_p_url = IpConfig.getUri("getPolicyDetail");
		
		setdata(c_p_url);
		toubao_status.setText(toubao_statusstr);
		c_p_orders_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
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

				try {

					Log.d("44444", jsonString);
					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						JSONObject dataObject = jsonObject.getJSONObject("data");


						JSONObject basicObject = dataObject.getJSONObject("basic");

						String cj_no =basicObject.getString("cj_no");
						String engine_no =basicObject.getString("engine_no");
						String car_msg =basicObject.getString("car_msg");
						String type =basicObject.getString("type");
						String holder_card_id =basicObject.getString("holder_card_id");
						String holder_name =basicObject.getString("holder_name");
						String total_amount =basicObject.getString("total_amount");
						String period =basicObject.getString("period");
						String period_syx =basicObject.getString("period_syx");

						map_all.put("holder_name", holder_name);
						map_all.put("cj_no", cj_no);
						map_all.put("engine_no", engine_no);
						map_all.put("car_msg", car_msg);
						map_all.put("type", type);
						map_all.put("holder_card_id", holder_card_id);
						map_all.put("total_amount", total_amount);
						map_all.put("period", period);
						map_all.put("period_syx", period_syx);


						JSONArray priceList = dataObject.getJSONArray("price");


						for (int i = 0; i < priceList.length(); i++) {

							Map<String, String> map = new HashMap<String, String>();
							JSONArray jsonArray1 = priceList.getJSONArray(i);



							map.put("policy_name", jsonArray1.getString(0));
							map.put("policy_price", jsonArray1.getString(1));

							price_list.add(map);
						}



						JSONObject policyObject = dataObject.getJSONObject("policy");

						String policy_for_name =policyObject.getString("policy_for_name");
						String card_no =policyObject.getString("card_no");
						String card_type =policyObject.getString("card_type");
						String birthday =policyObject.getString("birthday");
						String sex =policyObject.getString("sex");
						String mobile =policyObject.getString("mobile");
						String email =policyObject.getString("email");
						String insured_relationship =policyObject.getString("insured_relationship");
						String insured_name =policyObject.getString("insured_name");
						String insured_card_type =policyObject.getString("insured_card_type");
						String insured_card_no =policyObject.getString("insured_card_no");
						String insured_birthday =policyObject.getString("insured_birthday");

						String insured_card_sex =policyObject.getString("insured_card_sex");
						String insured_mobile =policyObject.getString("insured_mobile");
						String insured_email =policyObject.getString("insured_email");
						String emergency_contact_tel =policyObject.getString("emergency_contact_tel");
						String emergency_contact_name =policyObject.getString("emergency_contact_name");
						String beneficiary_name =policyObject.getString("beneficiary_name");
						String remarks_type =policyObject.getString("remarks_type");
						String remarks =policyObject.getString("remarks");
						String insured_card_person_type =policyObject.getString("insured_card_person_type");


						map_all.put("policy_for_name", policy_for_name);
						map_all.put("card_no", card_no);
						map_all.put("card_type", card_type);
						map_all.put("birthday", birthday);
						map_all.put("sex", sex);
						map_all.put("mobile", mobile);
						map_all.put("email", email);
						map_all.put("insured_relationship", insured_relationship);
						map_all.put("insured_name", insured_name);
						map_all.put("insured_card_type", insured_card_type);
						map_all.put("insured_card_no", insured_card_no);
						map_all.put("insured_birthday", insured_birthday);
						map_all.put("insured_card_sex", insured_card_sex);
						map_all.put("insured_mobile", insured_mobile);
						map_all.put("insured_email", insured_email);
						map_all.put("emergency_contact_tel", emergency_contact_tel);
						map_all.put("emergency_contact_name", emergency_contact_name);
						map_all.put("beneficiary_name", beneficiary_name);
						map_all.put("remarks_type", remarks_type);
						map_all.put("remarks", remarks);
						map_all.put("insured_card_person_type", insured_card_person_type);



						map_all.put("pricelist", price_list);
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

	public class MyAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public MyAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		public void setData(List<Map<String, String>> list) {
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
				view = layoutInflater.inflate(R.layout.policy_price_item,
						null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.tv_insurance);
			TextView p_price = (TextView) view.findViewById(R.id.tv_insurance_price);
			
			Log.d("44444", list.get(position).get("policy_name"));
			p_name.setText(list.get(position).get("policy_name"));
			
			p_price.setText(list.get(position).get("policy_price"));
			
			return view;

		}

	}
	

}
