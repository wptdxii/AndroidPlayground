package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.LinearLayoutForListView;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class OfferDetialActivity extends BaseActivity {
	private LinearLayoutForListView price_list;


	private List<Map<String, String>> pricelist = new ArrayList<Map<String, String>>();
	private MyAdapter adapter;
	
	private TextView s_a_car_no, s_a_owner_name, s_a_engine_no, s_a_vin, total_title, total;


	private Map<String, String> key_value = new HashMap<String, String>();
	private String order_id, user_id, token, price_id;
	private RelativeLayout o_d_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private Dialog dialog;
    private Button o_detial_submit;
    private RadioGroup radio_group;
    private RadioButton radio_check_out, radio_service;
    
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			 Map<String, Object> data = (Map<String, Object>) msg.obj;


			 pricelist =(List<Map<String, String>>)data.get("price_list");

			 s_a_car_no.setText((String)data.get("car_msg"));
			 s_a_owner_name.setText((String)data.get("holder_name"));

			 s_a_engine_no.setText((String)data.get("engine_no"));
			 s_a_vin.setText((String)data.get("cj_no"));

			 total_title.setText("共选择"+ data.get("count") +"种险种");
			 total.setText((String)data.get("total"));

			 String is_bought =(String)data.get("is_bought");

			 o_detial_submit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {


						final String c_p_url = IpConfig.getUri("updateHaddleType");

						sethaddle_data(c_p_url);

					}
				});

			 if(is_bought.equals("1")){

				 o_detial_submit.setBackgroundResource(R.drawable.pub_grey_button_style);
				 o_detial_submit.setClickable(false);

			 }
			 adapter.setData(pricelist);

			 price_list.setAdapter(adapter);
			 adapter.notifyDataSetChanged();
			 dialog.dismiss();

		}

	};
	private Handler errcode_handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;
			 dialog.dismiss();
			String status = data;

			if (status.equals("false")) {

				Toast.makeText(OfferDetialActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};
	private Handler haddle_handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			 Map<String, String> data = (Map<String, String>) msg.obj;
		
            String errcode = data.get("errcode");
            String errmsg = data.get("errmsg");
            if(errcode.equals("0"))
            {
            	  String msgstr = data.get("msg");
            	  
            	  
            	  
            		CustomDialog.Builder builder = new CustomDialog.Builder(
            				OfferDetialActivity.this);

    				builder.setTitle("提示");
    				builder.setMessage(msgstr);
    				builder.setPositiveButton("确定",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog,
    									int which) {
    								dialog.dismiss();
    								finish();
    								            
    							}
    						});
    				builder.create().show();
            	
            	
           
            	
            }else{
            	Toast.makeText(OfferDetialActivity.this, errmsg,
						Toast.LENGTH_SHORT).show();
            }
		
		}

	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.offer_detial);

		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");
		price_id = intent.getStringExtra("price_id");


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		init();
		initEvent();

	}

	private void init() {

		o_d_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("车险询价");
		s_a_car_no = (TextView) findViewById(R.id.s_a_car_no);
		s_a_owner_name = (TextView) findViewById(R.id.s_a_owner_name);
		s_a_engine_no = (TextView) findViewById(R.id.s_a_engine_no);
		s_a_vin = (TextView) findViewById(R.id.s_a_vin);
		total_title = (TextView) findViewById(R.id.total_title);
		total = (TextView) findViewById(R.id.total);
		price_list=(LinearLayoutForListView)findViewById(R.id.price_list);
		
		radio_group = (RadioGroup) findViewById(R.id.radio_group);
		
		radio_check_out = (RadioButton) findViewById(R.id.radio_check_out);
		
		radio_service = (RadioButton) findViewById(R.id.radio_service);
		
		o_detial_submit=(Button)findViewById(R.id.o_detial_submit);
		adapter = new MyAdapter(OfferDetialActivity.this);
		
		
		 dialog = new Dialog(this,R.style.progress_dialog);
         dialog.setContentView(R.layout.progress_dialog);
         dialog.setCancelable(true);
         dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
         TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
         p_dialog.setText("请稍后...");
		

	}

	private void initEvent() {

		key_value.put("user_id", user_id);
		key_value.put("token", token);
		key_value.put("order_id", order_id);
		key_value.put("price_id", price_id);
		dialog.show();

		final String c_p_url = IpConfig.getUri("getOfferDetial");

		setdata(c_p_url);

		radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				switch (checkedId) {
				case R.id.radio_check_out:

				
					key_value.put("haddle_type", "01");
				
					break;

			
				case R.id.radio_service:

				
					key_value.put("haddle_type", "02");
						
					break;


				default:
					break;
				}
				
			}
		});
		o_d_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
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
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Map<String, Object> map_all = new HashMap<String, Object>();

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
						JSONObject dataObject = jsonObject
								.getJSONObject("data");

						JSONObject basicObject = dataObject
								.getJSONObject("basic");

						String holder_name = basicObject
								.getString("holder_name");

						String cj_no = basicObject.getString("cj_no");
						String engine_no = basicObject
								.getString("engine_no");
						String car_msg = basicObject.getString("car_msg");

						String is_bought = basicObject
								.getString("is_bought");

						map_all.put("holder_name", holder_name);
						map_all.put("cj_no", cj_no);
						map_all.put("engine_no", engine_no);
						map_all.put("car_msg", car_msg);
						map_all.put("is_bought", is_bought);


						JSONObject priceObject = dataObject.getJSONObject("price");


						// 迭代输出json的key作为map的key

						Iterator<String> iterator = priceObject.keys();
						while (iterator.hasNext()) {
							Map<String, String> map = new HashMap<String, String>();
							String key = iterator.next();
							String ty = policy_name.get(key);
							String value = priceObject.getString(key);
							if (ty == null || ty.equals("")) {


								map_all.put(key, value);


							}else{

								map.put("policy_name", ty);
								map.put("policy_price", value);
								list.add(map);
							}


						}


						map_all.put("price_list", list);


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

	
	private void sethaddle_data(String url) {

		
		
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

				Map<String, String> map = new HashMap<String, String>();

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
						String errmsg= jsonObject.getString("errmsg");

						JSONObject dataObject = jsonObject.getJSONObject("data");
						String msg= dataObject.getString("msg");

						map.put("errcode", errcode);
						map.put("errmsg", errmsg);
						map.put("msg", msg);
						Message message = Message.obtain();

						message.obj = map;

						haddle_handler.sendMessage(message);



					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	
	}
	
	

	private HashMap<String, String> policy_name = new HashMap<String, String>();
	{
		policy_name.put("jqx", "交强险/车船税");
		policy_name.put("clssx", "车辆损失险");
		policy_name.put("sydszzrx", "商业第三者责任险");
		policy_name.put("qcdqx", "全车盗抢险");
		policy_name.put("sjzwzrx", "司机座位责任险");
		policy_name.put("ckzwzrx", "乘客座位责任险");
		policy_name.put("blddpsx", "玻璃单独破碎险");
		policy_name.put("cshhssx", "车身划痕损失险");
		policy_name.put("zrssx", "自燃损失险");
		policy_name.put("dcjcd", "倒车镜、车灯单独损失险");
		policy_name.put("ssxs", "涉水行驶损失险");
		policy_name.put("bjmp", "不计免赔");

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
			View view;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.o_d_price_item,
						null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.p_name);
		
			TextView p_price = (TextView) view.findViewById(R.id.p_price);
		
			p_name.setText(list.get(position).get("policy_name"));
			
			p_price.setText(list.get(position).get("policy_price"));
			
			return view;

		}

	}

}
