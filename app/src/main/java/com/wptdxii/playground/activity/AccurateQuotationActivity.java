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
import android.widget.ImageView;
import android.widget.ListView;
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

public class AccurateQuotationActivity extends BaseActivity {


	private Handler car_info_handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;
			String holder_name = data.get("holder_name");
			String car_code = data.get("car_code");
			String cj_no = data.get("cj_no");
			String engine_no = data.get("engine_no");

			if(car_code.equals(""))
			{
				s_a_car_no.setText("新车未上牌");
			}else{
			s_a_car_no.setText(car_code);
			}
			s_a_owner_name.setText(holder_name);
			s_a_engine_no.setText(engine_no);
			s_a_vin.setText(cj_no);

		}

	};

	private TextView s_a_car_no, s_a_owner_name, s_a_engine_no, s_a_vin;

	private ListAdapter listadapter;

	private Map<String, String> key_value = new HashMap<String, String>();

	private ListView a_q_list;
	@SuppressLint("HandlerLeak")
	private Handler a_q_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			listadapter.setData(list);

			a_q_list.setAdapter(listadapter);
			listadapter.notifyDataSetChanged();

			dialog.dismiss();

		}

	};

	private String order_id, user_id, token, quote_status;
	private RelativeLayout a_q_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.accurate_quotation);

		Intent intent = getIntent();
		order_id = intent.getStringExtra("id");
		quote_status = intent.getStringExtra("quote_status");


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		init();
		initEvent();

	}

	private void init() {

		a_q_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right= (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("精准报价");
		s_a_car_no = (TextView) findViewById(R.id.s_a_car_no);
		s_a_owner_name = (TextView) findViewById(R.id.s_a_owner_name);
		s_a_engine_no = (TextView) findViewById(R.id.s_a_engine_no);
		s_a_vin = (TextView) findViewById(R.id.s_a_vin);

		listadapter = new ListAdapter(AccurateQuotationActivity.this);

		a_q_list = (ListView) findViewById(R.id.a_q_list);
		
		
		 dialog = new Dialog(this,R.style.progress_dialog);
         dialog.setContentView(R.layout.progress_dialog);
         dialog.setCancelable(true);
         dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
         TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
         p_dialog.setText("请稍后...");
         
		

	}

	private void initEvent() {

		key_value.put("user_id", user_id);
		Log.d("order_id", order_id);
		key_value.put("token", token);
		key_value.put("order_id", order_id);

		dialog.show();

		final String c_p_url = IpConfig.getUri("getOrderDetial");

		setdata(c_p_url);

		a_q_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		a_q_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {

				final ViewHolder holder = (ViewHolder) arg1.getTag();

				if (quote_status.equals("01")) {

				} else
				// if(quote_status.equals("02"))
				{
					if (holder.price_wenhao.isShown()) {
						holder.price_wenhao
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										if (holder.remarks.isShown()) {
											holder.remarks
													.setVisibility(View.GONE);
										} else {
											holder.remarks
													.setVisibility(View.VISIBLE);
										}
									}
								});

					} else if (!holder.price.getText().toString()
							.equals("暂无报价")) {

						Intent intent = new Intent();
						intent.putExtra("order_id", order_id);
						intent.putExtra("price_id", listadapter.list.get(pos)
								.get("price_id").toString());

						intent.setClass(AccurateQuotationActivity.this,
								OfferDetialActivity.class);

						AccurateQuotationActivity.this.startActivity(intent);

					}

				}

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
				Toast.makeText(AccurateQuotationActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, Object>> list = new ArrayList<>();
				try {

					Log.d("44444", jsonString);
					if (jsonString.equals("") || jsonString.equals("null")) {
						Toast.makeText(AccurateQuotationActivity.this,
								"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						JSONObject dataObject = jsonObject
								.getJSONObject("data");

						String holder_name = dataObject
								.getString("holder_name");
						String car_code = dataObject.getString("car_code");
						String cj_no = dataObject.getString("cj_no");
						String engine_no = dataObject
								.getString("engine_no");

						Map<String, String> car_info_map = new HashMap<String, String>();

						car_info_map.put("holder_name", holder_name);
						car_info_map.put("car_code", car_code);
						car_info_map.put("cj_no", cj_no);
						car_info_map.put("engine_no", engine_no);

						Message message2 = Message.obtain();

						message2.obj = car_info_map;

						car_info_handler.sendMessage(message2);

						JSONArray companyList = dataObject
								.getJSONArray("companys");

						for (int i = 0; i < companyList.length(); i++) {
							JSONObject jsonObject2 = companyList
									.getJSONObject(i);
							Map<String, Object> map = new HashMap<String, Object>();

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

						a_q_handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		
		
		


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

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.a_q_company_item,
						null);

				holder.company_name = (TextView) convertView
						.findViewById(R.id.company_name);
				holder.price_wenhao = (ImageView) convertView
						.findViewById(R.id.price_wenhao);
				holder.price = (TextView) convertView.findViewById(R.id.price);
				holder.remarks = (TextView) convertView
						.findViewById(R.id.remarks);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.company_name.setText(list.get(position).get("company_name")
					.toString());
			holder.price.setText(list.get(position).get("price").toString());

			String remarkstr = list.get(position).get("remarks").toString();

			if (remarkstr.equals("")) {
				holder.price_wenhao.setVisibility(View.GONE);

			} else {

				holder.price_wenhao.setVisibility(View.VISIBLE);
			}
			holder.remarks.setText(remarkstr);

			return convertView;
		}

	}

	class ViewHolder {

		TextView company_name;
		ImageView price_wenhao;
		TextView price;
		TextView remarks;

	}

}
