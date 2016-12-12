package com.wptdxii.playground.activity;

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
import com.cloudhome.bean.BeiBaoBean;
import com.cloudhome.bean.OtherInfoBean;
import com.cloudhome.bean.TouBaoBean;
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

public class PurchaseAgainActivity extends BaseActivity {

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
	private TextView relation_txt;
	private RelativeLayout s_o_back;
	private RelativeLayout rl_right;
	private TextView tv_text;

	private String startdate;
	private String token;
	private String product_id = "", price = "", packagestr = "", period = "",
			rate = "",titlestr="";
	private String[] code = { "01", "02", "03", "04", "05", "06", "07" };
	private String[] relation = { "丈夫", "妻子", "父亲", "母亲", "儿子", "女儿", "本人" };
	String txttime;
	private String user_id;
	Start_Brith_Wheel wheelMain;
	private TextView immediately_insure;
	private TextView per_price;
	private TextView holder, insurance, other_info;

	private TextView total_price,title;

	private CheckBox protocol_check;
	private Boolean agree_flg = false;
	private String toubaorenInfo = "";
	private String beibaorenInfo = "";
	private String otherInfo = "";
	private String orderId = "";//提交订单成功传回的
	private String order_id;//从再次购买上个页面传过来
	private TouBaoBean beanOther;  //
	private BeiBaoBean insuredBeanOld;//被保人信息
	private OtherInfoBean otherOldBean;//其它信息
	private TouBaoBean holderOldBean;//投保人信息
	private String user_id_encode="";
	public static PurchaseAgainActivity PurchaseAgaininstance=null;

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

				Toast.makeText(PurchaseAgainActivity.this, "网络连接失败，请确认网络连接后重试",
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
			Intent intent = new Intent(PurchaseAgainActivity.this,
					RightPayActivity.class);
			Log.d("跳向支付页面传的id------", orderId);
			intent.putExtra("id", orderId);
			intent.putExtra("prevoius_page", "");

			startActivity(intent);

		}

	};

	private Handler old_success_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			dialog.dismiss();
			for(int i=0;i<code.length;i++){
				if(beanOther.getRealationShip().equals(code[i])){
					relation_txt.setText(relation[i]);
				}
			}
			
			holder.setText(holderOldBean.getChName());
			insurance.setText(insuredBeanOld.getChName());
			other_info.setText(otherOldBean.getTripDestination());
			total_price.setText(beanOther.getTotalPrice());
			per_price.setText(beanOther.getPerPrice());
//			String name, String pinyin, String id_type,
//			String id_no, String birthday, String sex, String tel, String email
			toubaorenInfo=Json_Value(holderOldBean.getChName(), holderOldBean.getEnname(), holderOldBean.getIdType(), holderOldBean.getIdNumber(), holderOldBean.getBirthDate(), holderOldBean.getSex(), holderOldBean.getPhoneNumber(), holderOldBean.getEmail());
			beibaorenInfo=Json_Value(insuredBeanOld.getChName(), insuredBeanOld.getEnname(), insuredBeanOld.getIdType(), insuredBeanOld.getIdNumber(), insuredBeanOld.getBirthDate(), insuredBeanOld.getSex(), insuredBeanOld.getPhoneNumber(), insuredBeanOld.getEmail());
			otherInfo=Json_Value(otherOldBean.getTripDestination(),otherOldBean.getTripGoal(),otherOldBean.getVisaCity(),otherOldBean.getEmergencyName(),otherOldBean.getEmergencyNum());
			
		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.submit_order);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		user_id_encode=sp.getString("Login_UID_ENCODE", "");
		Intent intent = getIntent();
		product_id = intent.getStringExtra("product_id");
		order_id=intent.getStringExtra("order_id");
		titlestr= intent.getStringExtra("title");

		PurchaseAgaininstance =this;
		init();
		initEvent();

	}

	void init() {
		s_o_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
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
		title= (TextView) findViewById(R.id.title);
		per_price = (TextView) findViewById(R.id.per_price);
		total_price = (TextView) findViewById(R.id.total_price);

		holder = (TextView) findViewById(R.id.holder);
		insurance = (TextView) findViewById(R.id.insurance);
		other_info = (TextView) findViewById(R.id.other_info);
		protocol_check = (CheckBox) findViewById(R.id.protocol_check);
		protocol_text=(TextView) findViewById(R.id.protocol_text);

		popview = findViewById(R.id.popview);
		adapter = new MyAdapter(this);
		
		
		  dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("请稍后...");
		

	}

	void initEvent() {
		key_value.put("user_id", user_id);
		key_value.put("token", token);
		title.setText(titlestr);
		Log.i("userId-----orderId   ", user_id+"---"+order_id);
//		String url = IpConfig.getIp()+ "user_id="+user_id_encode+"&token="+token+"&mod=preBuyAgain&order_id="+order_id;
		String url = IpConfig.getIp()+ "mod=preBuyAgain&order_id="+order_id;
		setOldOrderdata(url);
		dialog.show();
		
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
						.from(PurchaseAgainActivity.this);
				final View timepickerview1 = inflater1.inflate(
						R.layout.timepicker, null);
				ScreenInfo screenInfo1 = new ScreenInfo(
						PurchaseAgainActivity.this);
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
						PurchaseAgainActivity.this).builder()
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

				intent.setClass(PurchaseAgainActivity.this,
						FillInApplicantActivity.class);

				intent.putExtra("toubaorenInfo", toubaorenInfo);

				startActivityForResult(intent, 0);

			}
		});

		insured_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.setClass(PurchaseAgainActivity.this,
						FillInInsuredActivity.class);

				intent.putExtra("beibaorenInfo", beibaorenInfo);
				Log.i("BEIBAOREN----------", beibaorenInfo);

				startActivityForResult(intent, 0);

			}

		});

		other_info_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();

				intent.setClass(PurchaseAgainActivity.this,
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
				Intent intent=new Intent(PurchaseAgainActivity.this,InsuranceDeclarationActivity.class);
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
				String relation_str=relation_txt.getText().toString();

				if (datestr.equals("请选择")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							PurchaseAgainActivity.this);

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
							PurchaseAgainActivity.this);

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
							PurchaseAgainActivity.this);

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
							PurchaseAgainActivity.this);

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
							PurchaseAgainActivity.this);

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
							PurchaseAgainActivity.this);

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

					Log.d("submit提交保单的最终数据------", json_value);
					String url = IpConfig.getUri("saveRecOrder");
					setdata(url);

				}
			}
		});

	}

	private void setOldOrderdata(String url) {
		// TODO Auto-generated method stub
		
		
		
		OkHttpUtils.post()
		.url(url)
		.params(key_value)
		.build()
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);

				dialog.dismiss();
				Toast.makeText(PurchaseAgainActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();

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
						JSONObject obj1=obj.getJSONObject("data");
						beanOther=new TouBaoBean();
						//						 "product_id": "30001", --------------产品ID
						//					        "total_amount": "40.00", -----------总价
						//					        "price": "40.00", -------------单价
						//					        "count": "1", ------------份数
						//					        "period": "01", ---------------保障期间
						//					        "package": "PAJH01", -------------套餐code
						//					        "policy_begin": "2015-12-3 ", -----------起保日期
						//						"relationship": "07", -----------------投被保人关系
						//						 "rate": "1", ---------------费率

						beanOther.setProduct_id(obj1.getString("product_id"));
						beanOther.setTotalPrice(obj1.getString("total_amount"));
						beanOther.setPerPrice(obj1.getString("price"));
						beanOther.setInsuranceNumber(obj1.getString("count"));
						beanOther.setBeginDate(obj1.getString("policy_begin"));
						beanOther.setPackageName(obj1.getString("package"));
						beanOther.setPeriod(obj1.getString("period"));
						beanOther.setRealationShip(obj1.getString("relationship"));
						beanOther.setRate(obj1.getString("rate"));

						//						 "name": "王亚东",
						//				            "pinyin": "WANGYADONG",
						//				            "id_type": "01",
						//				            "id_no": "41032319891021001X",
						//				            "birthday": "1989-10-21",
						//				            "sex": "01",
						//				            "tel": "18500212308",
						//				            "email": "trhthyj@163.com"

						JSONObject obj2=obj1.getJSONObject("holder");
						holderOldBean=new TouBaoBean();
						holderOldBean.setChName(obj2.getString("name"));
						holderOldBean.setEnname(obj2.getString("pinyin"));
						holderOldBean.setIdType(obj2.getString("id_type"));
						holderOldBean.setIdNumber(obj2.getString("id_no"));
						holderOldBean.setBirthDate(obj2.getString("birthday"));
						holderOldBean.setSex(obj2.getString("sex"));
						holderOldBean.setPhoneNumber(obj2.getString("tel"));
						holderOldBean.setEmail(obj2.getString("email"));


						insuredBeanOld=new BeiBaoBean();
						JSONObject obj3=obj1.getJSONObject("insurance");
						insuredBeanOld.setChName(obj3.getString("name"));
						insuredBeanOld.setEnname(obj3.getString("pinyin"));
						insuredBeanOld.setIdType(obj3.getString("id_type"));
						insuredBeanOld.setIdNumber(obj3.getString("id_no"));
						insuredBeanOld.setBirthDate(obj3.getString("birthday"));
						insuredBeanOld.setSex(obj3.getString("sex"));
						insuredBeanOld.setPhoneNumber(obj3.getString("tel"));
						insuredBeanOld.setEmail(obj3.getString("email"));

						//
						otherOldBean=new OtherInfoBean();
						JSONObject obj4=obj1.getJSONObject("other");
						otherOldBean.setTripDestination(obj4.getString("to"));
						otherOldBean.setTripGoal(obj4.getString("for"));
						otherOldBean.setVisaCity(obj4.getString("visa_city"));
						otherOldBean.setEmergencyName(obj4.getString("emergency_name"));
						otherOldBean.setEmergencyNum(obj4.getString("emergency_tel"));

						old_success_handler.sendEmptyMessage(0);


					}

				} catch (Exception e) {
					e.printStackTrace();
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

			toubaorenInfo = bundle.getString("holder");

			String name = bundle.getString("name");
			holder.setText(name);
			if (relation_txt.getText().equals("本人") && !toubaorenInfo.isEmpty()) {
				insurance.setText(name);
				beibaorenInfo = toubaorenInfo;
			}
		} else if (resultCode == 2) {
			beibaorenInfo = bundle.getString("insurance");
			String name = bundle.getString("name");
			insurance.setText(name);
			if (relation_txt.getText().equals("本人") && !beibaorenInfo.isEmpty()) {
				holder.setText(name);
				toubaorenInfo = beibaorenInfo;
			}
		} else if (resultCode == 3) {
			otherInfo = bundle.getString("other");
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
				Toast.makeText(PurchaseAgainActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
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
							Toast.makeText(PurchaseAgainActivity.this, errorMsg,
									Toast.LENGTH_SHORT).show();
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

			JSONObject holderObject = new JSONObject(toubaorenInfo);
			JSONObject insuranceObject = new JSONObject(beibaorenInfo);
			JSONObject otherObject = new JSONObject(otherInfo);
			JSONObject benefitObject = new JSONObject("{}");
			jsonObj.put("policy_begin", date);// 向pet对象里面添加值
			jsonObj.put("relationship", beanOther.getRealationShip());
			jsonObj.put("holder", holderObject);
			jsonObj.put("insurance", insuranceObject);
			jsonObj.put("benefit", benefitObject);

			jsonObj.put("other", otherObject);

			jsonObj.put("price", beanOther.getPerPrice());
			jsonObj.put("count", beanOther.getInsuranceNumber());
			jsonObj.put("total_amount", beanOther.getTotalPrice());

			jsonObj.put("product_id", beanOther.getProduct_id());

			jsonObj.put("package", beanOther.getPackageName());
			jsonObj.put("period", beanOther.getPeriod());
			jsonObj.put("rate", beanOther.getRate());

			jsonresult = jsonObj.toString();
			Log.i("SUBMIT_ORDER____", jsonresult);

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
				PurchaseAgainActivity.this, R.layout.text, R.id.tv_text,
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
				beanOther.setRealationShip(code[pos]);
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
	
	public String Json_Value(String name, String pinyin, String id_type,
							 String id_no, String birthday, String sex, String tel, String email) {
		String jsonresult = "";// 定义返回字符串

		try {
			// JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
			// JSONObject jsonObj = new JSONObject();//pet对象，json形式
			// jsonObj.put("petid", pet.getPetid());//向pet对象里面添加值
			// jsonObj.put("petname", pet.getPetname());
			// jsonObj.put("pettype", pet.getPettype());
			// // 把每个数据当作一对象添加到数组里
			// jsonarray.put(jsonObj);//向json数组里面添加pet对象
			// object.put("pet", jsonarray);//向总对象里面添加包含pet的数组
			// jsonresult = object.toString();//生成返回字符串
			//
			// // {"pet":[{"petid":100,"petname":"name1","pettype":"type1"}]}
			//
			JSONObject jsonObj = new JSONObject();// pet对象，json形式
			jsonObj.put("name", name);// 向pet对象里面添加值
			jsonObj.put("pinyin", pinyin);
			jsonObj.put("id_type", id_type);
			jsonObj.put("id_no", id_no);
			jsonObj.put("birthday", birthday);
			jsonObj.put("sex", sex);
			jsonObj.put("tel", tel);
			jsonObj.put("email", email);
			jsonresult = jsonObj.toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}
	
	public String Json_Value(String to, String forstr, String visa_city,
							 String emergency_name, String emergency_tel) {
		String jsonresult = "";// 定义返回字符串

		try {

			JSONObject jsonObj = new JSONObject();// pet对象，json形式
			jsonObj.put("to", to);// 向pet对象里面添加值
			jsonObj.put("for", forstr);
			jsonObj.put("visa_city", visa_city);
			jsonObj.put("emergency_name", emergency_name);
			jsonObj.put("emergency_tel", emergency_tel);

			jsonresult = jsonObj.toString();

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return jsonresult;
	}
}
