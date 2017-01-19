package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.Year_Month_Wheel;
import com.zf.iosdialog.widget.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;
public class Promotion_ExpensesActivity extends BaseActivity {

	private Map<String, String> key_value = new HashMap<String, String>();

	private String user_id;
	private String token;

	private TextView start_year;
	private TextView start_month;

	private TextView end_year;
	private TextView end_month;

	private TextView total_price;

	private TextView chexian_shouqi_price;
	private TextView chexian_xvqi_price;
	private TextView chexian_price;

	private TextView gexian_shouqi_price;
	private TextView gexian_xvqi_price;
	private TextView gexian_price;

	private RelativeLayout chexian_shouqi_rel;
	private RelativeLayout chexian_xvqi_rel;
	private RelativeLayout chexian_yeji_rel;

	private RelativeLayout gexian_shouqi_rel;
	private RelativeLayout gexian_xvqi_rel;
	private RelativeLayout gexian_yeji_rel;

	
	private RelativeLayout start_rel;
	private RelativeLayout end_rel;
	
	
	private String startdate = "";
	private String enddate = "";

	private ImageView back;


	private Dialog pdialog;
	Year_Month_Wheel wheelMain;
	
	@SuppressLint("SimpleDateFormat")
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;

			pdialog.dismiss();
			String status = data;
			// String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {

				Toast.makeText(Promotion_ExpensesActivity.this,
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
			pdialog.dismiss();
			Toast.makeText(Promotion_ExpensesActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, Object> total_map = (Map<String, Object>) msg.obj;

			List<Map<String, String>> gexian_list = (List<Map<String, String>>) total_map
					.get("gexian_list");
			List<Map<String, String>> chexian_list = (List<Map<String, String>>) total_map
					.get("chexian_list");
			String zongshouru = (String) total_map.get("zongshouru");

			total_price.setText(zongshouru);

			startdate = (String) total_map.get("start");
			 enddate = (String) total_map.get("end");
			
		
		    	int i = 0;
				String split = "-";
				StringTokenizer token = new StringTokenizer(startdate, split);

				String[] startArray = new String[token.countTokens()];

				while (token.hasMoreTokens()) {

					startArray[i] = token.nextToken();
					i++;
				}
				
		    	int j = 0;
					StringTokenizer token2 = new StringTokenizer(enddate, split);

					String[] endArray = new String[token2.countTokens()];

					while (token2.hasMoreTokens()) {

						endArray[j] = token2.nextToken();
						j++;
					}
	

		
			if(startArray.length>1&&endArray.length>1)
			{
			start_year.setText(startArray[0] );
			start_month.setText(startArray[1]);
			end_year.setText(endArray[0] );
			end_month.setText(endArray[1]);
			}
			
			gexian_shouqi_price
					.setText(gexian_list.get(0).get("gexian_shouqi"));
			gexian_xvqi_price.setText(gexian_list.get(0).get("gexian_xvqi"));
			gexian_price.setText(gexian_list.get(0).get("gexian_yeji"));

			chexian_shouqi_price.setText(chexian_list.get(0).get(
					"chexian_shouqi"));
			chexian_xvqi_price.setText(chexian_list.get(0).get("chexian_xvqi"));
			chexian_price.setText(chexian_list.get(0).get("chexian_yeji"));
			
			
			jumpActivity();
			pdialog.dismiss();

		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.promotion_expenses);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		// String txttime = calendar.get(Calendar.YEAR) + "-"
		// + (calendar.get(Calendar.MONTH) +1) + "-"
		// + calendar.get(Calendar.DAY_OF_MONTH);

		init();
		initEvent();

	}

	void init() {
		back = (ImageView) findViewById(R.id.back);

		start_year = (TextView) findViewById(R.id.start_year);
		start_month = (TextView) findViewById(R.id.start_month);
		end_year = (TextView) findViewById(R.id.end_year);
		end_month = (TextView) findViewById(R.id.end_month);
		total_price = (TextView) findViewById(R.id.total_price);
		chexian_shouqi_price = (TextView) findViewById(R.id.chexian_shouqi_price);
		chexian_xvqi_price = (TextView) findViewById(R.id.chexian_xvqi_price);
		chexian_price = (TextView) findViewById(R.id.chexian_price);
		gexian_shouqi_price = (TextView) findViewById(R.id.gexian_shouqi_price);
		gexian_xvqi_price = (TextView) findViewById(R.id.gexian_xvqi_price);
		gexian_price = (TextView) findViewById(R.id.gexian_price);

		chexian_shouqi_rel = (RelativeLayout) findViewById(R.id.chexian_shouqi_rel);
		chexian_xvqi_rel = (RelativeLayout) findViewById(R.id.chexian_xvqi_rel);
		chexian_yeji_rel = (RelativeLayout) findViewById(R.id.chexian_yeji_rel);

		gexian_shouqi_rel = (RelativeLayout) findViewById(R.id.gexian_shouqi_rel);
		gexian_xvqi_rel = (RelativeLayout) findViewById(R.id.gexian_xvqi_rel);
		gexian_yeji_rel = (RelativeLayout) findViewById(R.id.gexian_yeji_rel);

		start_rel = (RelativeLayout) findViewById(R.id.start_rel);
		end_rel = (RelativeLayout) findViewById(R.id.end_rel);

		
		pdialog = new Dialog(this, R.style.progress_dialog);
		pdialog.setContentView(R.layout.progress_dialog);
		pdialog.setCancelable(true);
		pdialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) pdialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");

	}

	void initEvent() {

	


		key_value.put("user_id", user_id);
		key_value.put("token", token);

		pdialog.show();

		String query_param = Json_Value("", "");

		key_value.put("query_param", query_param);

		final String url = IpConfig.getUri("getsettle_tuiguang_fee");

		setlistdata(url);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

		

		
		
	}

	void jumpActivity() {

		
		start_rel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				
				
				LayoutInflater inflater1 = LayoutInflater
						.from(Promotion_ExpensesActivity.this);
				final View timepickerview1 = inflater1.inflate(
						R.layout.year_month_picker, null);
				ScreenInfo screenInfo1 = new ScreenInfo(
						Promotion_ExpensesActivity.this);
				wheelMain = new Year_Month_Wheel(timepickerview1);
				wheelMain.screenheight = screenInfo1.getHeight();

				Calendar calendar1 = Calendar.getInstance();

				try {
					calendar1.setTime(dateFormat.parse(dateFormat
							.format(new Date())));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int year1 = calendar1.get(Calendar.YEAR);
				int month1 = calendar1.get(Calendar.MONTH)+1;
				int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
				wheelMain.initDateTimePicker(year1, month1, day1);
				final MyAlertDialog dialog = new MyAlertDialog(
						Promotion_ExpensesActivity.this).builder()
						.setTitle("请选择")
						.setView(timepickerview1)
						.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						});
				dialog.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						int start_year_num = Integer.valueOf(wheelMain.getYear());
						int start_month_num = Integer.valueOf(wheelMain.getMonth());
					
						int end_yearnum =Integer.valueOf(end_year.getText().toString());
						int end_monthnum =Integer.valueOf(end_month.getText().toString());
						
						if(end_yearnum>start_year_num||(end_yearnum>=start_year_num&&end_monthnum>start_month_num))
						{
							startdate= wheelMain.getTime();
							start_year.setText(start_year_num+"");
							if(start_month_num>9)
							{
							start_month.setText(start_month_num+"");
							}else{
								start_month.setText("0"+start_month_num+"");
							}
							pdialog.show();

							String query_param = Json_Value(startdate, enddate);

							key_value.put("query_param", query_param);

							final String url = IpConfig.getUri("getsettle_tuiguang_fee");

							setlistdata(url);

						
						}else{
							
							
							Toast.makeText(getApplicationContext(),
									"截止时间大于应起始时间", Toast.LENGTH_SHORT).show();
							
						}
						
					
						
					
						
						
					}
				});
				dialog.show();
				
			}
		});
		
		end_rel	.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
				
				LayoutInflater inflater1 = LayoutInflater
						.from(Promotion_ExpensesActivity.this);
				final View timepickerview1 = inflater1.inflate(
						R.layout.year_month_picker, null);
				ScreenInfo screenInfo1 = new ScreenInfo(
						Promotion_ExpensesActivity.this);
				wheelMain = new Year_Month_Wheel(timepickerview1);
				wheelMain.screenheight = screenInfo1.getHeight();

				Calendar calendar1 = Calendar.getInstance();

				try {
					calendar1.setTime(dateFormat.parse(dateFormat
							.format(new Date())));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int year1 = calendar1.get(Calendar.YEAR);
				int month1 = calendar1.get(Calendar.MONTH)+1;
				int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
				wheelMain.initDateTimePicker(year1, month1, day1);
				final MyAlertDialog dialog = new MyAlertDialog(
						Promotion_ExpensesActivity.this).builder()
						.setTitle("请选择")
						.setView(timepickerview1)
						.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						});
				dialog.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						int end_yearnum = Integer.valueOf(wheelMain.getYear());
						int end_monthnum = Integer.valueOf(wheelMain.getMonth());
					
				
						int start_year_num =Integer.valueOf(start_year.getText().toString());
						int start_month_num =Integer.valueOf(start_month.getText().toString());
						
						
						Log.d("444444",end_yearnum+"");
						Log.d("444444",end_monthnum+"");
						Log.d("444444",start_year_num+"");
						Log.d("444444",start_month_num+"");
						
						if(end_yearnum>start_year_num||(end_yearnum>=start_year_num&&end_monthnum>start_month_num))
						{
							enddate= wheelMain.getTime();
							end_year.setText(end_yearnum+"");
							
							if(end_monthnum>9)
							{
								end_month.setText(end_monthnum+"");
							}else{
								end_month.setText("0"+end_monthnum+"");
							}
							
						
							pdialog.show();

							String query_param = Json_Value(startdate, enddate);

							key_value.put("query_param", query_param);

							final String url = IpConfig.getUri("getsettle_tuiguang_fee");

							setlistdata(url);

						
						}else{
							
							
							Toast.makeText(getApplicationContext(),
									"截止时间大于应起始时间", Toast.LENGTH_SHORT).show();
							
						}
						
					
						
					
						
						
					}
				});
				dialog.show();
				
			}
		});
		gexian_shouqi_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.putExtra("type", "010101");
			
				
				intent.putExtra("startdate", startdate);
				intent.putExtra("enddate", enddate);
				
				intent.setClass(Promotion_ExpensesActivity.this,
						IncomeDetailActivity.class);

				Promotion_ExpensesActivity.this.startActivity(intent);

			}
		});
		gexian_xvqi_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.putExtra("type", "010201");
				intent.putExtra("startdate", startdate);
				intent.putExtra("enddate", enddate);
				
				intent.setClass(Promotion_ExpensesActivity.this,
						IncomeDetailActivity.class);

				Promotion_ExpensesActivity.this.startActivity(intent);

			}
		});
		gexian_yeji_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.putExtra("type", "0501");
				intent.putExtra("startdate", startdate);
				intent.putExtra("enddate", enddate);
				intent.setClass(Promotion_ExpensesActivity.this,
						IncomeDetailActivity.class);

				Promotion_ExpensesActivity.this.startActivity(intent);
			}
		});

		chexian_shouqi_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.putExtra("type", "010102");
				intent.putExtra("startdate", startdate);
				intent.putExtra("enddate", enddate);
				intent.setClass(Promotion_ExpensesActivity.this,
						IncomeDetailActivity.class);

				Promotion_ExpensesActivity.this.startActivity(intent);

			}
		});
		chexian_xvqi_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.putExtra("type", "010202");
				intent.putExtra("startdate", startdate);
				intent.putExtra("enddate", enddate);
				intent.setClass(Promotion_ExpensesActivity.this,
						IncomeDetailActivity.class);

				Promotion_ExpensesActivity.this.startActivity(intent);
			}
		});
		chexian_yeji_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.putExtra("type", "0502");
				intent.putExtra("startdate", startdate);
				intent.putExtra("enddate", enddate);
				intent.setClass(Promotion_ExpensesActivity.this,
						IncomeDetailActivity.class);

				Promotion_ExpensesActivity.this.startActivity(intent);
			}
		});

	}

	public String Json_Value(String start, String end) {
		String jsonresult = "";// 定义返回字符串

		try {
			JSONObject jsonObj = new JSONObject();// pet对象，json形式.

			jsonObj.put("start", start);// 向pet对象里面添加值
			jsonObj.put("end", end);

			jsonresult = jsonObj.toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}

	private void setlistdata(String url) {

		
		
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
				List<Map<String, String>> gexian_list = new ArrayList<Map<String, String>>();
				List<Map<String, String>> chexian_list = new ArrayList<Map<String, String>>();
				Map<String, Object> total_map = new HashMap<String, Object>();
				Map<String, String> errcode_map = new HashMap<String, String>();
				try {

					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						// Log.d("44444", jsonString);
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

							JSONArray gexianlist = dataObject
									.getJSONArray("gexian");

							String value1 = gexianlist.getString(0);
							String value2 = gexianlist.getString(1);
							String value3 = gexianlist.getString(2);

							Map<String, String> map = new HashMap<String, String>();

							map.put("gexian_shouqi", value1);
							map.put("gexian_xvqi", value2);
							map.put("gexian_yeji", value3);

							gexian_list.add(map);

							JSONArray chexianlist = dataObject
									.getJSONArray("chexian");

							String v1 = chexianlist.getString(0);
							String v2 = chexianlist.getString(1);
							String v3 = chexianlist.getString(2);

							Map<String, String> map2 = new HashMap<String, String>();

							map2.put("chexian_shouqi", v1);
							map2.put("chexian_xvqi", v2);
							map2.put("chexian_yeji", v3);
							chexian_list.add(map2);

							String startstr = dataObject
									.getString("start");

							String endstr = dataObject
									.getString("end");

							String zongshourustr = dataObject
									.getString("zongshouru");
							total_map.put("gexian_list", gexian_list);
							total_map.put("chexian_list", chexian_list);
							total_map.put("zongshouru", zongshourustr);

							total_map.put("start", startstr);
							total_map.put("end", endstr);
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


}
