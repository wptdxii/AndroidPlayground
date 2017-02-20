package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.Start_Brith_Wheel;
import com.zf.iosdialog.widget.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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

import okhttp3.Call;
public class SubmitOrdersActivity extends BaseActivity {

	private MyAdapter adapter;
	private RelativeLayout applicant_rel;

	private TextView date;
	private TextView protocol_text;

	@SuppressLint("SimpleDateFormat")
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private RelativeLayout date_rel;
	private Dialog dialog;
	private RelativeLayout holder_rel;
	private RelativeLayout insured_rel;
	private Map<String, String> key_value = new HashMap<String, String>();
	private RelativeLayout layout;
	private ListView listViewSpinner;
	private RelativeLayout other_info_rel;
	private PopupWindow popupWindow;
	private View popview;
	private String[] code = { "01", "02", "03", "04", "05", "06", "07" };
	private String[] relation = { "丈夫", "妻子", "父亲", "母亲", "儿子", "女儿", "本人" };
	private TextView relation_txt;
	private RelativeLayout s_o_back;
	private RelativeLayout rl_right;
	private TextView tv_text;

	private String startdate;
	private String token;
	private String product_id = "", price = "", packagestr = "", period = "",
			rate = "",titlestr="";

	String txttime;
	private String relation_str = "";
	private String user_id;
	Start_Brith_Wheel wheelMain;
	private TextView immediately_insure;
	private TextView per_price;
	private TextView holder, insurance, other_info;

	private TextView total_price,title;

	private String holderstr = "", insurancestr = "", otherstr = "";

	public static SubmitOrdersActivity SubmitOrdersininstance=null;

	private CheckBox protocol_check;
	private Boolean agree_flg = false;
	private String toubaorenInfo = "";
	private String beibaorenInfo = "";
	private String otherInfo = "";
	private String orderId = "";

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

				Toast.makeText(SubmitOrdersActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}

		}

	};

	private Handler success_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			dialog.dismiss();
			Intent intent = new Intent(SubmitOrdersActivity.this,
					RightPayActivity.class);
			intent.putExtra("id", orderId);
			intent.putExtra("prevoius_page", "SubmitOrders");
			startActivity(intent);

		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.submit_order);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		Intent intent = getIntent();
		product_id = intent.getStringExtra("product_id");
		price = intent.getStringExtra("price");
		packagestr = intent.getStringExtra("package");
		period = intent.getStringExtra("period");
		rate = intent.getStringExtra("rate");
		titlestr= intent.getStringExtra("title");

		SubmitOrdersininstance =this;
		init();
		initEvent();

	}

	void init() {
		s_o_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right= (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("提交订单");
		date_rel = (RelativeLayout) findViewById(R.id.date_rel);
		applicant_rel = (RelativeLayout) findViewById(R.id.applicant_rel);
		insured_rel = (RelativeLayout) findViewById(R.id.insured_rel);
		holder_rel = (RelativeLayout) findViewById(R.id.holder_rel);
		other_info_rel = (RelativeLayout) findViewById(R.id.other_info_rel);
		date = (TextView) findViewById(R.id.date);
		relation_txt = (TextView) findViewById(R.id.relation_txt);
		immediately_insure = (TextView) findViewById(R.id.immediately_insure);

		protocol_check = (CheckBox) findViewById(R.id.protocol_check);

		per_price = (TextView) findViewById(R.id.per_price);
		total_price = (TextView) findViewById(R.id.total_price);
		title= (TextView) findViewById(R.id.title);
		holder = (TextView) findViewById(R.id.holder);
		insurance = (TextView) findViewById(R.id.insurance);
		other_info = (TextView) findViewById(R.id.other_info);
		protocol_check = (CheckBox) findViewById(R.id.protocol_check);
		protocol_text = (TextView) findViewById(R.id.protocol_text);

		popview = findViewById(R.id.popview);
		adapter = new MyAdapter(this);

		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");

	}

	void initEvent() {

		key_value.put("user_id", user_id);
		key_value.put("token", token);
		per_price.setText("￥" + price);
		total_price.setText("￥" + price);
		title.setText(titlestr);
		s_o_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});

		date_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				LayoutInflater inflater1 = LayoutInflater
						.from(SubmitOrdersActivity.this);
				final View timepickerview1 = inflater1.inflate(
						R.layout.timepicker, null);
				ScreenInfo screenInfo1 = new ScreenInfo(
						SubmitOrdersActivity.this);
				wheelMain = new Start_Brith_Wheel(timepickerview1);
				wheelMain.screenheight = screenInfo1.getHeight();

				Calendar calendar1 = Calendar.getInstance();

				try {
					calendar1.setTime(dateFormat.parse(dateFormat
							.format(new Date())));
					calendar1.add(Calendar.DAY_OF_MONTH, 1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int year1 = calendar1.get(Calendar.YEAR);
				int month1 = calendar1.get(Calendar.MONTH);
				int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
				wheelMain.initDateTimePicker(year1, month1, day1);
				final MyAlertDialog dialog = new MyAlertDialog(
						SubmitOrdersActivity.this).builder()
						.setTitle("请选择")
						// .setMsg("22")
						// .setEditText("111")
						.setView(timepickerview1)
						.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						});
				dialog.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						startdate = wheelMain.getTime();

						date.setText(startdate);
						key_value.put("birthday", startdate);
						Toast.makeText(getApplicationContext(),
								wheelMain.getTime(), Toast.LENGTH_SHORT).show();
					}
				});
				dialog.show();
			}
		});

		applicant_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showPopupWindow();
			}
		});

		holder_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.setClass(SubmitOrdersActivity.this,
						FillInApplicantActivity.class);

				intent.putExtra("toubaorenInfo", toubaorenInfo);

				startActivityForResult(intent, 0);

			}
		});

		insured_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.setClass(SubmitOrdersActivity.this,
						FillInInsuredActivity.class);

				intent.putExtra("beibaorenInfo", beibaorenInfo);

				startActivityForResult(intent, 0);

			}

		});

		other_info_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.setClass(SubmitOrdersActivity.this,
						OtherInfoActicity.class);
				intent.putExtra("otherInfo", otherInfo);

				startActivityForResult(intent, 0);

			}
		});

		protocol_check
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {
						// TODO Auto-generated method stub

						agree_flg = isChecked;
					}
				});

		protocol_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SubmitOrdersActivity.this,
						InsuranceDeclarationActivity.class);
				startActivity(intent);

			}
		});

		immediately_insure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String datestr = date.getText().toString();

				String insurancestr = insurance.getText().toString();

				String holderstr = holder.getText().toString();

				String other_info_str = other_info.getText().toString();

				if (datestr.equals("请选择")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							SubmitOrdersActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请选择起保日期");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (relation_str.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							SubmitOrdersActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请选择我是被保险人的关系");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				} else if (insurancestr.equals("必选")) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							SubmitOrdersActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请填写被保人的相关信息");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				} else if (holderstr.equals("必选")) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							SubmitOrdersActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请填写投保人的相关信息");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				} else if (other_info_str.equals("必选")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							SubmitOrdersActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请填写其它信息");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (!agree_flg) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							SubmitOrdersActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请同意投保声明");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				} else {
					String json_value = Json_Value(datestr);

					key_value.put("order_info", json_value);

					Log.d("submit提交保单的最终数据--------", json_value);
					String url = IpConfig.getUri("saveRecOrder");
					setdata(url);

				}
			}
		});

	}

	/**
	 * 复写onActivityResult方法 当SecondActivity页面关闭时，接收SecondActiviy页面传递过来的数据。
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		Bundle bundle = data.getExtras();

		if (resultCode == 1) {

			holderstr = bundle.getString("holder");
			toubaorenInfo = holderstr;

			String name = bundle.getString("name");
			holder.setText(name);
			if (relation_txt.getText().equals("本人") && !holderstr.isEmpty()) {
				insurance.setText(name);
				beibaorenInfo = holderstr;
				insurancestr = holderstr;
			}
		} else if (resultCode == 2) {
			insurancestr = bundle.getString("insurance");
			beibaorenInfo = insurancestr;
			String name = bundle.getString("name");
			insurance.setText(name);
			if (relation_txt.getText().equals("本人") && !insurancestr.isEmpty()) {
				holder.setText(name);
				toubaorenInfo = insurancestr;
				holderstr = insurancestr;
			}
		} else if (resultCode == 3) {
			otherstr = bundle.getString("other");
			otherInfo = otherstr;
			String to = bundle.getString("to");
			other_info.setText(to);

		} else if (resultCode == 0) {

		}

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

				dialog.dismiss();
				Toast.makeText(SubmitOrdersActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(String response, int id) {

				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				try {

					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						JSONObject obj = new JSONObject(jsonString);
						String errcode = obj.getString("errcode");
						if (errcode.equals("0")) {
							orderId = obj.getString("data");

							success_handler.sendEmptyMessage(0);
						} else {
							String errorMsg = obj.getString("errmsg");
							dialog.dismiss();
							Toast.makeText(SubmitOrdersActivity.this,
									errorMsg, Toast.LENGTH_SHORT).show();
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}

	public String Json_Value(String date) {
		String jsonresult = "";// 定义返回字符串

		try {
			JSONObject jsonObj = new JSONObject();// pet对象，json形式.

			JSONObject holderObject = new JSONObject(holderstr);
			JSONObject insuranceObject = new JSONObject(insurancestr);
			JSONObject otherObject = new JSONObject(otherstr);

			JSONObject benefitObject = new JSONObject("{}");
			jsonObj.put("policy_begin", date);// 向pet对象里面添加值
			jsonObj.put("relationship", relation_str);
			jsonObj.put("holder", holderObject);
			jsonObj.put("insurance", insuranceObject);
			jsonObj.put("benefit", benefitObject);

			jsonObj.put("other", otherObject);

			jsonObj.put("price", price);
			jsonObj.put("count", "1");
			jsonObj.put("total_amount", price);

			jsonObj.put("product_id", product_id);

			jsonObj.put("package", packagestr);
			jsonObj.put("period", period);
			jsonObj.put("rate", rate);

			jsonresult = jsonObj.toString();
			Log.i("SUBMIT_ORDER__________", jsonresult);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}

	private void showPopupWindow() {
		layout = ((RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.type_pop, null));
		listViewSpinner = (ListView) this.layout.findViewById(R.id.lv_dialog);

		listViewSpinner.setAdapter(new ArrayAdapter<String>(
				SubmitOrdersActivity.this, R.layout.text, R.id.tv_text,
				relation));

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
				relation_str = code[pos];
				relation_txt.setText(relation[pos]);
				popupWindow.dismiss();
				popupWindow = null;
				if (relation_txt.getText().equals("本人")) {
					beibaorenInfo = toubaorenInfo;
					String toubaoName = holder.getText().toString();
					insurance.setText(toubaoName);
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
				view = layoutInflater.inflate(R.layout.policy_price_item, null);
			} else {
				view = convertView;
			}

			TextView p_name = (TextView) view.findViewById(R.id.p_name);
			TextView p_price = (TextView) view.findViewById(R.id.p_price);

			Log.d("44444", list.get(position).get("name"));
			p_name.setText(list.get(position).get("name"));

			p_price.setText(list.get(position).get("charge"));

			return view;

		}

	}
}
