package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.cloudhome.view.wheel.wheelview.JudgeDate;
import com.cloudhome.view.wheel.wheelview.ScreenInfo;
import com.cloudhome.view.wheel.wheelview.WheelMain;
import com.cloudhome.view.iosalertview.ActionSheetDialog;
import com.cloudhome.view.iosalertview.ActionSheetDialog.OnSheetItemClickListener;
import com.cloudhome.view.iosalertview.ActionSheetDialog.SheetItemColor;
import com.cloudhome.view.iosalertview.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class AddRelationActivity extends BaseActivity {

	private WheelMain wheelMain;
	private String txttime;
	@SuppressLint("SimpleDateFormat")
	private
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private String cityTxt;

	private TextView bank_num_text;
	private String bank_name;
	private String bank_no;
	private Map<String, String> key_value = new HashMap<String, String>();

	private RelativeLayout addrelation_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private TextView relation_add;
	private ClearEditText name_add;
	private TextView sex_add;

	private TextView brith_add;
	private ClearEditText age_add;

	private Button relation_submit;

	private String relation = "";
	private String name = "";
	private String sex = "";
	private String birthday = "";
	private String age = "";
	private String user_id;
	private String token;

	private String customer_a_id;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");

			if (errcode.equals("0")) {

				finish();
			} else {
				String errmsg = data.get("errmsg");
				Toast.makeText(AddRelationActivity.this, errmsg,
						Toast.LENGTH_LONG).show();
			}
			// m_d_content.setText(content);

		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.addrelation);

		Intent intent = getIntent();

		customer_a_id = intent.getStringExtra("customer_id");


		Calendar calendar = Calendar.getInstance();
		txttime = calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH);
		init();
		initEvent();

	}

	private void init() {

		addrelation_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("添加家庭成员");
		relation_add = (TextView) findViewById(R.id.relation_add);

		name_add = (ClearEditText) findViewById(R.id.name_add);
		sex_add = (TextView) findViewById(R.id.sex_add);
		brith_add = (TextView) findViewById(R.id.brith_add);
		age_add = (ClearEditText) findViewById(R.id.age_add);
		relation_submit = (Button) findViewById(R.id.relation_submit);
	}



	private void initEvent() {
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("token", token);
		key_value.put("user_id", user_id);
	
		relation_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				new ActionSheetDialog(AddRelationActivity.this)
						.builder()
						.setTitle("请选择")
						.setCancelable(false)
						.setCanceledOnTouchOutside(true)
						.addSheetItem("父亲", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										relation_add.setText("父亲");
										relation = "01";
									}
								})
						.addSheetItem("母亲", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										relation = "02";
										relation_add.setText("母亲");
									}
								})
						.addSheetItem("丈夫", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										relation = "03";
										relation_add.setText("丈夫");
									}
								})
						.addSheetItem("妻子", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										relation = "04";
										relation_add.setText("妻子");
									}
								})
						.addSheetItem("儿子", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										relation = "05";
										relation_add.setText("儿子");
									}
								})
						.addSheetItem("女儿", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										relation = "06";
										relation_add.setText("女儿");
									}
								}).show();
			}
		});

		sex_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				new ActionSheetDialog(AddRelationActivity.this)
						.builder()
						.setTitle("请选择")
						.setCancelable(false)
						.setCanceledOnTouchOutside(true)
						.addSheetItem("男", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										//sex = "01";
										sex = "男";
										sex_add.setText("男");
									}
								})
						.addSheetItem("女", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										//sex = "02";
										sex = "女";
										sex_add.setText("女");
									}
								}).show();
			}
		});

		relation_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				name = name_add.getText().toString();
				age = age_add.getText().toString();
				if (relation.equals("") || name.equals("") || sex.equals("")
						|| birthday.equals("") || age.equals("")) {

			
					CustomDialog.Builder builder = new CustomDialog.Builder(
							AddRelationActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请将信息补充完整");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					 builder.create().show();
				} else {
					Log.d("7777777", "874545");
					key_value.put("relation", relation);
					key_value.put("name", name);
					key_value.put("sex", sex);
					key_value.put("birthday", birthday);
					key_value.put("age", age);
					key_value.put("customer_a_id", customer_a_id);
					String PRODUCT_URL = IpConfig.getUri("addRelation");
					user_id = sp.getString("Login_UID", "");
					token = sp.getString("Login_TOKEN", "");
					Log.i("kk--userid----",user_id);
					Log.i("kk--token-------",token);
					setdata(PRODUCT_URL);
				}

			}
		});
		brith_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LayoutInflater inflater1 = LayoutInflater
						.from(AddRelationActivity.this);
				final View timepickerview1 = inflater1.inflate(
						R.layout.timepicker, null);
				ScreenInfo screenInfo1 = new ScreenInfo(
						AddRelationActivity.this);
				wheelMain = new WheelMain(timepickerview1);
				wheelMain.screenheight = screenInfo1.getHeight();
				String time1 = txttime;
				Calendar calendar1 = Calendar.getInstance();
				if (JudgeDate.isDate(time1, "yyyy-MM-dd")) {
					try {
						calendar1.setTime(dateFormat.parse(time1));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				int year1 = calendar1.get(Calendar.YEAR);
				int month1 = calendar1.get(Calendar.MONTH);
				int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
				wheelMain.initDateTimePicker(year1, month1, day1);
				final MyAlertDialog dialog = new MyAlertDialog(
						AddRelationActivity.this).builder()
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
						birthday = wheelMain.getTime();
						brith_add.setText(birthday);
						Toast.makeText(getApplicationContext(),
								wheelMain.getTime(), Toast.LENGTH_SHORT).show();
					}
				});
				dialog.show();
			}
		});

		addrelation_back.setOnClickListener(new OnClickListener() {

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
			}

			@Override
			public void onResponse(String response, int id) {
				Map<String, String> map = new HashMap<String, String>();
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);

				try {

					// Log.d("44444", jsonString);
					JSONObject jsonObject = new JSONObject(jsonString);
					String data = jsonObject.getString("data");

					String errcode = jsonObject.getString("errcode");

					String errmsg = jsonObject.getString("errmsg");

					Log.d("44444", data);

					map.put("errcode", errcode);

					map.put("errmsg", errmsg);

					Message message = Message.obtain();

					message.obj = map;

					handler.sendMessage(message);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		
		
		

	}
}
