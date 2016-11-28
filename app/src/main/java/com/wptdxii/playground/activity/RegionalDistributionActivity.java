package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.CircleImage;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class RegionalDistributionActivity extends BaseActivity {
	private MyAdapter adapter;
	private ImageView r_d_back;
	private Map<String, String> key_value = new HashMap<String, String>();

	private Dialog dialog;
	private TextView r_d_time,r_d_totalCnt;
	private String user_id;
	private String token;
	private HashMap<Integer,Boolean> isSelected;
	private ListView r_d_list;
	private int All_Num = 0;
	
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	
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

				Toast.makeText(RegionalDistributionActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	private Handler handler = new Handler() {
		@SuppressLint("UseSparseArrays") @SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			 isSelected = new HashMap<Integer, Boolean>();
			 initDate(list);
			adapter.setData(list);
			r_d_list.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			dialog.dismiss();

		}

	};

	private Handler total_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			String data = (String) msg.obj;
		
			r_d_totalCnt.setText("您直接或间接推荐的用户总人数为: "+data+"人");
		}

	};
	
	 private void initDate(List<Map<String, Object>> list){
	        for(int i=0; i<list.size();i++) {
	            getIsSelected().put(i,false);
	        }
	    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.regioal_distribution);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		init();

		initEvent();

	}

	void init() {

		r_d_back = (ImageView) findViewById(R.id.r_d_back);
	
		r_d_time= (TextView) findViewById(R.id.r_d_time);
		r_d_totalCnt = (TextView) findViewById(R.id.r_d_totalCnt);
		r_d_list = (ListView) findViewById(R.id.r_d_list);
		adapter = new MyAdapter(RegionalDistributionActivity.this);
		
		  dialog = new Dialog(this,R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
        
		
	}

	void initEvent() {

		r_d_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				
				final ViewHolder holder = (ViewHolder) arg1.getTag();
				
			
//	
//				holder.rel10.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						
//
//						
//						if(holder.rel2.isShown())
//						{
//							holder.rel2.setVisibility(View.GONE);
//						}else{
//							holder.rel2.setVisibility(View.VISIBLE);
//						}
//					}
//				});
//					
				
				if(holder.rel2.isShown())
				{ getIsSelected().put(pos, false); 
					holder.rel2.setVisibility(View.GONE);
				}else{
					getIsSelected().put(pos, true); 
					holder.rel2.setVisibility(View.VISIBLE);
				}	
				
				
			}
		});
		r_d_time.setText("截止至"+dateFormat.format(new Date()));
		r_d_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

		key_value.put("token", token);
		key_value.put("user_id", user_id);
		final String PRODUCT_URL = IpConfig.getUri("get_agent_lit");
		setdata(PRODUCT_URL);

	}

	private void setdata(String url) {

		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e) {
			
				Log.e("error", "获取数据异常 ", e);
				Toast.makeText(RegionalDistributionActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}

			@Override
			public void onResponse(String response) {
			
				
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				Map<String, String> errcode_map = new HashMap<String, String>();

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

				try {

					if (jsonString.equals("") || jsonString.equals("")
							|| jsonString.equals("null")) {

						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);

					} else {

						JSONObject jsonObject = new JSONObject(jsonString);
						JSONArray dataList = jsonObject
								.getJSONArray("data");

						String totalCnt= jsonObject.getString("totalCnt");
						
						Message message2 = Message.obtain();
						message2.obj = totalCnt;

						total_handler.sendMessage(message2);
					    
						for (int i = 0; i < dataList.length(); i++) {
							JSONObject jsonObject2 = dataList
									.getJSONObject(i);

							Map<String, Object> total_map = new HashMap<String, Object>();

							Iterator<String> iterator = jsonObject2.keys();
							while (iterator.hasNext()) {

								String key = iterator.next();
								if (key.equals("detail_agent_info")) {

									List<Map<String, String>> agent_list = new ArrayList<Map<String, String>>();
									JSONArray agentList = jsonObject2
											.getJSONArray(key);

									for (int n = 0; n < agentList.length(); n++) {
										JSONObject agentObject = agentList
												.getJSONObject(n);
										Map<String, String> agent_map = new HashMap<String, String>();
										// 迭代输出json的key作为map的key

										Iterator<String> agent_iterator = agentObject
												.keys();
										while (agent_iterator.hasNext()) {

											String agent_key = agent_iterator
													.next();
											String value = agentObject
													.getString(agent_key);

											agent_map.put(agent_key, value);
										}

										agent_list.add(agent_map);

									}

									total_map.put("detail_agent_info",
											agent_list);

								} else if (key
										.equals("detail_customer_info")) {

									List<Map<String, String>> customer_list = new ArrayList<Map<String, String>>();
									JSONArray customerList = jsonObject2
											.getJSONArray(key);

									for (int n = 0; n < customerList
											.length(); n++) {
										JSONObject customerObject = customerList
												.getJSONObject(n);
										Map<String, String> customer_map = new HashMap<String, String>();
										// 迭代输出json的key作为map的key

										Iterator<String> customer_iterator = customerObject
												.keys();
										while (customer_iterator.hasNext()) {

											String customer_key = customer_iterator
													.next();
											String value = customerObject
													.getString(customer_key);
											customer_map.put(customer_key,
													value);

										}

										customer_list.add(customer_map);

									}

									total_map.put("detail_customer_info",
											customer_list);

								} else if (key.equals("agent_count")) {

									int value = jsonObject2.getInt(key);
									if (All_Num < value) {
										All_Num = value;
									}
									total_map.put(key, value);
								} else if (key.equals("customer_count")) {
									int value = jsonObject2.getInt(key);
									if (All_Num < value) {
										All_Num = value;
									}
									total_map.put(key, value);
								} else {
									String value = jsonObject2
											.getString(key);
									total_map.put(key, value);
								}
							}

							list.add(total_map);

						}

						Message message = Message.obtain();

						message.obj = list;

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

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.r_d_list_item,
						null);

				holder.city_name = (TextView) convertView
						.findViewById(R.id.city_name);
				holder.histogram1 = (ProgressBar) convertView
						.findViewById(R.id.histogram1);
				holder.histogram2 = (ProgressBar) convertView
						.findViewById(R.id.histogram2);
				holder.agent_count = (TextView) convertView
						.findViewById(R.id.agent_count);
				holder.customer_count = (TextView) convertView
						.findViewById(R.id.customer_count);

				holder.rel10 = (RelativeLayout) convertView
						.findViewById(R.id.rel10);
				holder.rel2 = (RelativeLayout) convertView
						.findViewById(R.id.rel2);

				holder.rd_img11 = (CircleImage) convertView
						.findViewById(R.id.rd_img11);
				holder.rd_img12 = (CircleImage) convertView
						.findViewById(R.id.rd_img12);
				holder.rd_img13 = (CircleImage) convertView
						.findViewById(R.id.rd_img13);
				holder.rd_img14 = (CircleImage) convertView
						.findViewById(R.id.rd_img14);
				holder.rd_img15 = (CircleImage) convertView
						.findViewById(R.id.rd_img15);

				holder.rd_img21 = (CircleImage) convertView
						.findViewById(R.id.rd_img21);
				holder.rd_img22 = (CircleImage) convertView
						.findViewById(R.id.rd_img22);
				holder.rd_img23 = (CircleImage) convertView
						.findViewById(R.id.rd_img23);
				holder.rd_img24 = (CircleImage) convertView
						.findViewById(R.id.rd_img24);
				holder.rd_img25 = (CircleImage) convertView
						.findViewById(R.id.rd_img25);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();

				resetViewHolder(holder);

			}

			
			holder.city_name.setText(list.get(position).get("mobile_area").toString());
			@SuppressWarnings("unchecked")
			List<Map<String, String>> agent_list = (List<Map<String, String>>) list
					.get(position).get("detail_agent_info");
			int agent_count = (Integer) list.get(position).get("agent_count");
			int customer_count = (Integer) list.get(position).get(
					"customer_count");
			if (agent_count == 0) {

				holder.rd_img11.setVisibility(View.GONE);
				holder.rd_img12.setVisibility(View.GONE);
				holder.rd_img13.setVisibility(View.GONE);
				holder.rd_img14.setVisibility(View.GONE);
				holder.rd_img15.setVisibility(View.GONE);

			} else if (agent_count == 1) {

				holder.rd_img11.setVisibility(View.VISIBLE);
				holder.rd_img12.setVisibility(View.GONE);
				holder.rd_img13.setVisibility(View.GONE);
				holder.rd_img14.setVisibility(View.GONE);
				holder.rd_img15.setVisibility(View.GONE);

				String img1_url = agent_list.get(0).get("avatar");

				if (img1_url.length() > 5) {


					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img11);


				}

			} else if (agent_count == 2) {

				holder.rd_img11.setVisibility(View.VISIBLE);
				holder.rd_img12.setVisibility(View.VISIBLE);
				holder.rd_img13.setVisibility(View.GONE);
				holder.rd_img14.setVisibility(View.GONE);
				holder.rd_img15.setVisibility(View.GONE);

				String img1_url = agent_list.get(0).get("avatar");
				String img2_url = agent_list.get(1).get("avatar");
				if (img1_url.length() > 5) {



					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img11);


				}
				if (img2_url.length() > 5) {



					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img12);


				}

			} else if (agent_count == 3) {

				holder.rd_img11.setVisibility(View.VISIBLE);
				holder.rd_img12.setVisibility(View.VISIBLE);
				holder.rd_img13.setVisibility(View.VISIBLE);
				holder.rd_img14.setVisibility(View.GONE);
				holder.rd_img15.setVisibility(View.GONE);

				String img1_url = agent_list.get(0).get("avatar");
				String img2_url = agent_list.get(1).get("avatar");
				String img3_url = agent_list.get(2).get("avatar");
				if (img1_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img11);
				}

				if (img2_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img12);
				}

				if (img3_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img3_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img13);
				}

			} else if (agent_count == 4) {

				holder.rd_img11.setVisibility(View.VISIBLE);
				holder.rd_img12.setVisibility(View.VISIBLE);
				holder.rd_img13.setVisibility(View.VISIBLE);
				holder.rd_img14.setVisibility(View.VISIBLE);
				holder.rd_img15.setVisibility(View.GONE);

				String img1_url = agent_list.get(0).get("avatar");
				String img2_url = agent_list.get(1).get("avatar");
				String img3_url = agent_list.get(2).get("avatar");
				String img4_url = agent_list.get(3).get("avatar");

				if (img1_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img11);
				}

				if (img2_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img12);
				}

				if (img3_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img3_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img13);
				}
				if (img4_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img4_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img14);
				}

			} else if (agent_count >= 4) {

				holder.rd_img11.setVisibility(View.VISIBLE);
				holder.rd_img12.setVisibility(View.VISIBLE);
				holder.rd_img13.setVisibility(View.VISIBLE);
				holder.rd_img14.setVisibility(View.VISIBLE);
				holder.rd_img15.setVisibility(View.VISIBLE);

				String img1_url = agent_list.get(0).get("avatar");
				String img2_url = agent_list.get(1).get("avatar");
				String img3_url = agent_list.get(2).get("avatar");
				String img4_url = agent_list.get(3).get("avatar");
				String img5_url = agent_list.get(4).get("avatar");


				if (img1_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img11);
				}

				if (img2_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img12);
				}

				if (img3_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img3_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img13);
				}
				if (img4_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img4_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img14);
				}
				if (img5_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img5_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img15);
				}
			}

			@SuppressWarnings("unchecked")
			List<Map<String, String>> customer_list = (List<Map<String, String>>) list
					.get(position).get("detail_customer_info");

			if (customer_count == 0) {

				holder.rd_img21.setVisibility(View.GONE);
				holder.rd_img22.setVisibility(View.GONE);
				holder.rd_img23.setVisibility(View.GONE);
				holder.rd_img24.setVisibility(View.GONE);
				holder.rd_img25.setVisibility(View.GONE);

			} else if (customer_count == 1) {

				holder.rd_img21.setVisibility(View.VISIBLE);
				holder.rd_img22.setVisibility(View.GONE);
				holder.rd_img23.setVisibility(View.GONE);
				holder.rd_img24.setVisibility(View.GONE);
				holder.rd_img25.setVisibility(View.GONE);

				String img1_url = customer_list.get(0).get("avatar");

				if (img1_url.length() > 5) {


					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img21);


				}

			} else if (customer_count == 2) {

				holder.rd_img21.setVisibility(View.VISIBLE);
				holder.rd_img22.setVisibility(View.VISIBLE);
				holder.rd_img23.setVisibility(View.GONE);
				holder.rd_img24.setVisibility(View.GONE);
				holder.rd_img25.setVisibility(View.GONE);

				String img1_url = customer_list.get(0).get("avatar");
				String img2_url = customer_list.get(1).get("avatar");
				if (img1_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img21);
				}
				if (img2_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img22);
				}

			} else if (customer_count == 3) {

				holder.rd_img21.setVisibility(View.VISIBLE);
				holder.rd_img22.setVisibility(View.VISIBLE);
				holder.rd_img23.setVisibility(View.VISIBLE);
				holder.rd_img24.setVisibility(View.GONE);
				holder.rd_img25.setVisibility(View.GONE);

				String img1_url = customer_list.get(0).get("avatar");
				String img2_url = customer_list.get(1).get("avatar");
				String img3_url = customer_list.get(2).get("avatar");
				if (img1_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img21);
				}

				if (img2_url.length()> 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img22);
				}

				if (img3_url.length() > 5) {



					Glide.with(RegionalDistributionActivity.this)
							.load(img3_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img23);


				}
			} else if (customer_count == 4) {

				holder.rd_img21.setVisibility(View.VISIBLE);
				holder.rd_img22.setVisibility(View.VISIBLE);
				holder.rd_img23.setVisibility(View.VISIBLE);
				holder.rd_img24.setVisibility(View.VISIBLE);
				holder.rd_img25.setVisibility(View.GONE);

				String img1_url = customer_list.get(0).get("avatar");
				String img2_url = customer_list.get(1).get("avatar");
				String img3_url = customer_list.get(2).get("avatar");
				String img4_url = customer_list.get(3).get("avatar");

				if (img1_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img21);
				}

				if (img2_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img22);
				}

				if (img3_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img3_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img23);
				}
				if (img4_url.length() > 5) {


					Glide.with(RegionalDistributionActivity.this)
							.load(img4_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img24);


				}

			} else if (customer_count >= 5) {

				holder.rd_img21.setVisibility(View.VISIBLE);
				holder.rd_img22.setVisibility(View.VISIBLE);
				holder.rd_img23.setVisibility(View.VISIBLE);
				holder.rd_img24.setVisibility(View.VISIBLE);
				holder.rd_img25.setVisibility(View.VISIBLE);

				
				String img1_url = customer_list.get(0).get("avatar");
				
				String img2_url = customer_list.get(1).get("avatar");
				String img3_url = customer_list.get(2).get("avatar");
				String img4_url = customer_list.get(3).get("avatar");
				String img5_url = customer_list.get(4).get("avatar");


				if (img1_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img1_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img21);
				}

				if (img2_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img2_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img22);
				}

				if (img3_url.length() > 5) {
					Glide.with(RegionalDistributionActivity.this)
							.load(img3_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img23);
				}
				if (img4_url.length() > 5) {


					Glide.with(RegionalDistributionActivity.this)
							.load(img4_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img24);


				}
				if (img5_url.length() > 5) {


					Glide.with(RegionalDistributionActivity.this)
							.load(img5_url)

							.placeholder(R.drawable.white)  //占位图 图片正在加载

							.into(holder.rd_img25);
				}

			}

			holder.agent_count.setText(agent_count + "");
			holder.customer_count.setText(customer_count + "");

			double rate1 = 0.0;
			double rate2 = 0.0;
			if (All_Num != 0) {
				rate1 = Double.valueOf(agent_count)    / Double.valueOf(All_Num);
				rate2 = Double.valueOf(customer_count) / Double.valueOf(All_Num);
			}
			
			double hv_1 = Math.round(rate1*100);
			double hv_2 = Math.round(rate2*100);
			
			int h1 = Integer.parseInt(new java.text.DecimalFormat("0").format(hv_1));
			int h2 = Integer.parseInt(new java.text.DecimalFormat("0").format(hv_2));
	
					Log.d("444554",h1+"");
					Log.d("444554",h2+"");
					
			holder.histogram1.setProgress(h1);
			holder.histogram2.setProgress(h2);
			
			if(getIsSelected().get(position)){
				
				 holder.rel2.setVisibility(View.VISIBLE);
			}else{
				 holder.rel2.setVisibility(View.GONE);
			}
			 
			
			return convertView;
		}

	}

	  public HashMap<Integer,Boolean> getIsSelected() {
	        return isSelected;
	    }

	    public  void setIsSelected(HashMap<Integer,Boolean> isSelected) {
	    	isSelected = isSelected;
	    }
	    
	class ViewHolder {
		TextView city_name;
		ProgressBar histogram1;
		ProgressBar histogram2;
		TextView agent_count;
		TextView customer_count;

		RelativeLayout rel10;
		RelativeLayout rel2;

		CircleImage rd_img11;
		CircleImage rd_img12;
		CircleImage rd_img13;
		CircleImage rd_img14;
		CircleImage rd_img15;

		CircleImage rd_img21;
		CircleImage rd_img22;
		CircleImage rd_img23;
		CircleImage rd_img24;
		CircleImage rd_img25;

	}

	protected void resetViewHolder(ViewHolder p_ViewHolder) {
		
		p_ViewHolder.city_name.setText(null);
		p_ViewHolder.agent_count.setText(null);
		p_ViewHolder.customer_count.setText(null);
		
	
	
		
		p_ViewHolder.rd_img11.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img12.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img13.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img14.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img15.setImageResource(R.drawable.expert_head);
		
		p_ViewHolder.rd_img21.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img22.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img23.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img24.setImageResource(R.drawable.expert_head);
		p_ViewHolder.rd_img25.setImageResource(R.drawable.expert_head);
		
	
		
	}




}
