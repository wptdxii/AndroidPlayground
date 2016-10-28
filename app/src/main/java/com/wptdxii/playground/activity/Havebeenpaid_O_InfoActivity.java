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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import okhttp3.Call;

public class Havebeenpaid_O_InfoActivity extends BaseActivity {


	private String user_id;
	private String token;
	private Map<String, String> key_value = new HashMap<String, String>();
	private Dialog dialog;
	private PriceAdapter padapter;
	private HolderAdapter hdapter;
	private InsuredAdapter idapter;
	private OtherAdapter odapter;
	private BenefitAdapter bdapter;
	private LinearLayoutForListView items_list;
	private LinearLayoutForListView holder_list;
	private LinearLayoutForListView insured_list;
	private LinearLayoutForListView other_list;
	private LinearLayoutForListView benefit_list;
	private TextView subs_code_txt,period_from,period_to,unit_price,the_copies,total_price;
	private TextView scan_policy,buy_again;
	private ImageView p_i_o_image,arrow_img;
	private RelativeLayout havebeenpaid_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private RelativeLayout p_i_o_rel,p_i_o_rel2;
	private LinearLayout h_b_p_linear;
	private String order_id,RBstr;
	private View view10;

	public  static Havebeenpaid_O_InfoActivity Havebeenpaid_O_Infoinstance=null;

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

				Toast.makeText(Havebeenpaid_O_InfoActivity.this,
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

			dialog.dismiss();
			Toast.makeText(Havebeenpaid_O_InfoActivity.this,
					errmsg, Toast.LENGTH_SHORT).show();




		}

	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			final Map<String, Object> data = (Map<String, Object>) msg.obj;

			List<Map<String, String>> pricelist = (List<Map<String, String>>) data
					.get("price_list");
			final List<Map<String, String>> holderlist = (List<Map<String, String>>) data
					.get("holder_list");
			final List<Map<String, String>> insuredlist = (List<Map<String, String>>) data
					.get("insured_list");
			final List<Map<String, String>> otherlist = (List<Map<String, String>>) data
					.get("other_list");
			List<Map<String, String>> benefitlist = (List<Map<String, String>>) data
					.get("benefit_list");

			dialog.dismiss();

			padapter.setData(pricelist);
			items_list.setAdapter(padapter);
			padapter.notifyDataSetChanged();

			hdapter.setData(holderlist);
			holder_list.setAdapter(hdapter);
			hdapter.notifyDataSetChanged();

			idapter.setData(insuredlist);
			insured_list.setAdapter(idapter);
			idapter.notifyDataSetChanged();

			odapter.setData(otherlist);
			other_list.setAdapter(odapter);
			odapter.notifyDataSetChanged();

			bdapter.setData(benefitlist);
			benefit_list.setAdapter(bdapter);
			bdapter.notifyDataSetChanged();
			
		
			//map_all.put("product_id", product_id);
			
		
		
			String img_url = IpConfig.getIp3()+"/"+ data.get("img_url").toString();
			
			if (img_url.length() > 6) {


				Glide.with(Havebeenpaid_O_InfoActivity.this)
						.load(img_url)
						.placeholder(R.drawable.white)  //占位图 图片正在加载
						.into( p_i_o_image);
			}
			
			 subs_code_txt.setText("订单流水号:"+ data.get("subs_code"));
	
				final String price =(String) data.get("price");
			 period_from.setText(data.get("period_from") +"至");
			 period_to.setText((String)data.get("period_to"));
			 unit_price.setText("￥"+ price);
			 the_copies.setText((String)data.get("count"));
			 total_price.setText("￥"+ data.get("amount"));
		
				final String product_id = data.get("product_id").toString();
			 p_i_o_rel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					// TODO Auto-generated method stub
					if(p_i_o_rel2.isShown())
					{
						p_i_o_rel2.setVisibility(View.GONE);
						arrow_img.setImageResource(R.drawable.arrow_down);
						
					}else{
						
						
						p_i_o_rel2.setVisibility(View.VISIBLE);
						arrow_img.setImageResource(R.drawable.arrow_up);
					
					}
					
					
				}
			});
			
			 scan_policy.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					
					Intent intent = new Intent();

					intent.putExtra("order_id", order_id);
			
					
					intent.setClass(Havebeenpaid_O_InfoActivity.this,
							CheckThePolicyActivity.class);

					Havebeenpaid_O_InfoActivity.this.startActivity(intent);
					
					
					
					
				}
			});
			 
			 final String product_name = data.get("product_name").toString();
			 buy_again.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.putExtra("product_id",(String)data.get("product_id"));
					intent.putExtra("order_id",order_id);
					intent.putExtra("title",product_name);
					intent.setClass(Havebeenpaid_O_InfoActivity.this,PurchaseAgainActivity.class);
					Havebeenpaid_O_InfoActivity.this.startActivity(intent);
				}
			});

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.havebeenpaid_o_info);

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		Intent intent = getIntent();
		order_id = intent.getStringExtra("id");
		RBstr = intent.getStringExtra("RBstr");


		Havebeenpaid_O_Infoinstance =this;

		init();
		initEvent();

	}

	void init() {

		key_value.put("order_id", order_id);
		key_value.put("user_id", user_id);
		key_value.put("token", token);

		
		Log.d("77777order_id",order_id);
		Log.d("77777user_id",user_id);
		Log.d("77777token",token);
		
		
	    dialog = new Dialog(this,R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
        
	

		padapter = new PriceAdapter(Havebeenpaid_O_InfoActivity.this);
		hdapter = new HolderAdapter(Havebeenpaid_O_InfoActivity.this);
		idapter = new InsuredAdapter(Havebeenpaid_O_InfoActivity.this);
		odapter = new OtherAdapter(Havebeenpaid_O_InfoActivity.this);
		bdapter = new BenefitAdapter(Havebeenpaid_O_InfoActivity.this);

		items_list = (LinearLayoutForListView) findViewById(R.id.items_list);
		holder_list = (LinearLayoutForListView) findViewById(R.id.holder_list);
		insured_list = (LinearLayoutForListView) findViewById(R.id.insured_list);
		other_list = (LinearLayoutForListView) findViewById(R.id.other_list);
		benefit_list = (LinearLayoutForListView) findViewById(R.id.benefit_list);

		subs_code_txt= (TextView) findViewById(R.id.subs_code_txt);
		p_i_o_image= (ImageView) findViewById(R.id.p_i_o_image);
		arrow_img= (ImageView) findViewById(R.id.arrow_img);
		period_from= (TextView) findViewById(R.id.period_from);
		
		period_to= (TextView) findViewById(R.id.period_to);
		
		unit_price= (TextView) findViewById(R.id.unit_price);
		
		the_copies= (TextView) findViewById(R.id.the_copies);
		total_price= (TextView) findViewById(R.id.total_price);
		
		p_i_o_rel= (RelativeLayout) findViewById(R.id.p_i_o_rel);
		p_i_o_rel2= (RelativeLayout) findViewById(R.id.p_i_o_rel2);
		
		h_b_p_linear= (LinearLayout) findViewById(R.id.h_b_p_linear);
		view10= findViewById(R.id.view10);
		scan_policy= (TextView) findViewById(R.id.scan_policy);
		buy_again= (TextView) findViewById(R.id.buy_again);
		
		havebeenpaid_back= (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("订单详情");
	}

	void initEvent() {

		
		havebeenpaid_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				finish();
				
			}
		});
		if(RBstr.equals("01"))
		{
			buy_again.setVisibility(View.GONE);
		}else if(RBstr.equals("02"))
		{
			h_b_p_linear.setVisibility(View.GONE);
			view10.setVisibility(View.GONE);
		}
		
		
		
		dialog.show();
		final String url = IpConfig.getUri("getRecOrderDetail");
		setData(url);

	}

	private void setData(String url) {

		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e) {
			
				Log.e("error", "获取数据异常 ", e);
				String status = "false";
				Message message = Message.obtain();

				message.obj = status;

				errcode_handler.sendMessage(message);
				
			}

			@Override
			public void onResponse(String response) {
			
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);

				Map<String, Object> map_all = new HashMap<String, Object>();
				Map<String, String> errcode_map = new HashMap<String, String>();
				List<Map<String, String>> price_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> holder_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> insured_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> other_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> benefit_list = new ArrayList<Map<String, String>>();
				try {

					if (jsonString.equals("") || jsonString.equals("null")) {
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
						JSONObject dataObject = jsonObject
								.getJSONObject("data");

						JSONArray itemsArray = dataObject
								.getJSONArray("items");

						for (int i = 0; i < itemsArray.length(); i++) {

							Map<String, String> map = new HashMap<String, String>();
							JSONArray jsonArray1 = itemsArray
									.getJSONArray(i);

							map.put("policy_name", jsonArray1.getString(0));
							map.put("policy_price", jsonArray1.getString(1));

							price_list.add(map);
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

						JSONArray otherArray = dataObject
								.getJSONArray("other");

						for (int i = 0; i < otherArray.length(); i++) {

							Map<String, String> map = new HashMap<String, String>();
							JSONArray jsonArray1 = otherArray
									.getJSONArray(i);

							map.put("other_title", jsonArray1.getString(0));
							map.put("other_value", jsonArray1.getString(1));

							other_list.add(map);
						}

						JSONArray benefitArray = dataObject
								.getJSONArray("benefit");

				
							Map<String, String> map3 = new HashMap<String, String>();
						

							map3.put("benefit_title",
									benefitArray.getString(0));
							map3.put("benefit_value",
									benefitArray.getString(1));

							benefit_list.add(map3);
					
						
						
						map_all.put("price_list", price_list);
						map_all.put("holder_list", holder_list);
						map_all.put("insured_list", insured_list);
						map_all.put("other_list", other_list);
						map_all.put("benefit_list", benefit_list);
						
						String subs_code  = dataObject.getString("subs_code");
						String product_id  = dataObject.getString("product_id");
						String img_url  = dataObject.getString("img_url");
						String product_name  = dataObject.getString("product_name");
						String price  = dataObject.getString("price");
						String amount  = dataObject.getString("amount");
						String count  = dataObject.getString("count");
						String period_from  = dataObject.getString("period_from");
						String period_to    = dataObject.getString("period_to");
						String relation_ship    = dataObject.getString("relation_ship");
						
						
						map_all.put("subs_code", subs_code);
						map_all.put("product_id", product_id);
						map_all.put("img_url", img_url);
						map_all.put("product_name", product_name);
						map_all.put("relation_ship", relation_ship);
						
						map_all.put("price", price);
						map_all.put("amount", amount);
						map_all.put("count", count);
						map_all.put("period_from", period_from);
						map_all.put("period_to", period_to);
					
						
						
					

						
						
						
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

	public class PriceAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public PriceAdapter(Context context) {
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
				view = layoutInflater.inflate(R.layout.policy_price_item, null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.p_name);
			TextView p_price = (TextView) view.findViewById(R.id.p_price);

			Log.d("44444", list.get(position).get("policy_name"));
			p_name.setText(list.get(position).get("policy_name"));

			p_price.setText(list.get(position).get("policy_price"));

			return view;

		}

	}

	public class HolderAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public HolderAdapter(Context context) {
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
				view = layoutInflater.inflate(R.layout.policy_price_item, null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.p_name);
			TextView p_price = (TextView) view.findViewById(R.id.p_price);

			p_name.setText(list.get(position).get("holder_title"));

			p_price.setText(list.get(position).get("holder_value"));

			return view;

		}

	}

	public class InsuredAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public InsuredAdapter(Context context) {
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
				view = layoutInflater.inflate(R.layout.policy_price_item, null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.p_name);
			TextView p_price = (TextView) view.findViewById(R.id.p_price);

			p_name.setText(list.get(position).get("insured_title"));
			p_price.setText(list.get(position).get("insured_value"));

			return view;

		}

	}

	public class OtherAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public OtherAdapter(Context context) {
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
				view = layoutInflater.inflate(R.layout.policy_price_item, null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.p_name);
			TextView p_price = (TextView) view.findViewById(R.id.p_price);

		
			p_name.setText(list.get(position).get("other_title"));

			p_price.setText(list.get(position).get("other_value"));

			return view;

		}

	}

	public class BenefitAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, String>> list = null;

		public BenefitAdapter(Context context) {
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
				view = layoutInflater.inflate(R.layout.policy_price_item, null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.p_name);
			TextView p_price = (TextView) view.findViewById(R.id.p_price);

		
			p_name.setText(list.get(position).get("benefit_title"));

			p_price.setText(list.get(position).get("benefit_value"));

			return view;

		}

	}



}
