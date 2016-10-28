package com.wptdxii.playground.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MobileFastLoginActivity extends BaseActivity {



	Map<String, String> key_value = new HashMap<String, String>();
	private EditText et_username;
	private EditText et_password;
	private Button login_in;
	private Button login_forget;
	private Button login_register;
	private ImageView login_back;
	private String pwMd5;

	private String ccyj_reference_flag = "";
	
	private Button btn_fast_login;//手机快速登录
	private Button verify_msg_button;
	private TimeCount time;
	
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");

			Log.d("455454", "455445" + errcode);
			if (errcode.equals("0")) {
				String user_id = data.get("user_id");
				String token = data.get("token");
				String mobile = data.get("mobile");
				String type_code = data.get("type_code");
				String state_code = data.get("state_code");
				String avatar = data.get("avatar");
				String idno = data.get("idno");
				String cert_a = data.get("cert_a");
				String cert_b = data.get("cert_b");
				String licence = data.get("licence");
				String nickname = data.get("nickname");
				String truename = data.get("truename");
				String company_name = data.get("company_name");
				String company = data.get("company");
				String mobile_area = data.get("mobile_area");
				String bank_name = data.get("bank_name");
				String bank_no = data.get("bank_no");
				String refer_name = data.get("refer_name");
				String recomend_code = data.get("recomend_code");
				String sex = data.get("sex");
				String birthday = data.get("birthday");

				if (!type_code.equals("02")) {

					String personal_specialty = data.get("personal_specialty");
					String good_count = data.get("good_count");
					String cert_num_isShowFlg = data.get("cert_num_isShowFlg");
					String isShow_in_expertlist = data
							.get("isShow_in_expertlist");
					String mobile_num_short = data.get("mobile_num_short");
					String personal_context = data.get("personal_context");

					Editor editor3 = sp3.edit();
					editor3.putString("personal_specialty", personal_specialty);
					editor3.putString("good_count", good_count);
					editor3.putString("cert_num_isShowFlg", cert_num_isShowFlg);
					editor3.putString("isShow_in_expertlist",
							isShow_in_expertlist);
					editor3.putString("mobile_num_short", mobile_num_short);
					editor3.putString("personal_context", personal_context);
					editor3.commit();

				}
				setUserInfo(user_id, sex, birthday, idno, token, mobile,
						type_code, state_code, avatar, cert_a, cert_b, licence,
						truename, nickname, company_name, company, mobile_area,
						bank_name, bank_no, refer_name, recomend_code,
						ccyj_reference_flag);
				Toast.makeText(MobileFastLoginActivity.this, "登录成功", Toast.LENGTH_LONG)
						.show();
			} else {
				String errmsg = data.get("errmsg");

				CustomDialog.Builder builder = new CustomDialog.Builder(
						MobileFastLoginActivity.this);

				builder.setTitle("提示");
				builder.setMessage(errmsg);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				builder.create().show();

			}
		}

	};

	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;

			String status = data;
			// String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {

				Toast.makeText(MobileFastLoginActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	private Handler msg_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			finish();

		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.activity_mobile_fast_login);


		init();
		initEvent();

	}

	void init() {
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
		
		et_username = (EditText) findViewById(R.id.et_username);
		String olonum = sp4.getString("old_phone", "");
		et_username.setText(olonum);
		Editable editable = et_username.getText();
		int selEndIndex = Selection.getSelectionEnd(editable);
		selEndIndex = editable.length();
		Selection.setSelection(editable, selEndIndex);
		
		btn_fast_login=(Button) findViewById(R.id.btn_fast_login);

		verify_msg_button = (Button) findViewById(R.id.verify_msg_button);
//		TelephonyManager tm = (TelephonyManager) getBaseContext()
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		device_id = tm.getDeviceId() + "";
//		if (device_id.equals("null") || device_id.equals("")) {
//			device_id = Secure.getString(this.getContentResolver(),
//					Secure.ANDROID_ID);
//		}
//
//		os_version = android.os.Build.VERSION.RELEASE;

		et_password = (EditText) findViewById(R.id.et_password);
		login_in = (Button) findViewById(R.id.login_in);
		login_back = (ImageView) findViewById(R.id.login_back);
		login_register = (Button) findViewById(R.id.login_register);
		login_forget = (Button) findViewById(R.id.login_forget);
	}

	void initEvent() {

		login_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		login_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final String username = et_username.getText().toString();
				String password = et_password.getText().toString();
				pwMd5 = HttpMd5.getMD5(password);

				Log.d("77777", pwMd5);
				int phonelength = username.length();
				int pwstrlength = password.length();
				if (phonelength != 11) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							MobileFastLoginActivity.this);
					builder.setTitle("提示");
					builder.setMessage("请检查手机号码");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else if (pwstrlength < 6) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							MobileFastLoginActivity.this);
					builder.setTitle("提示");
					builder.setMessage("请输入六位以上密码");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {

					String url = IpConfig.getUri("getMemberLogin");

//					Map<String, String> params = new HashMap<String, String>();
//					params.put("mobile", username);
//					params.put("password", pwMd5);
//					Log.d("555444", url);
//					Log.d("555444", username);
//					Log.d("555444", pwMd5);

					
					   Map<String, String> params = new HashMap<String, String>();
					   params.put("mobile", username);
					   params.put("password", pwMd5);

					   
					OkHttpUtils.post()//
					.url(url)//
				    .params(params)//
					.build()//
				
					.execute(new MyStringCallback());
					
			

				}
			}
		});

		
		
		btn_fast_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
	verify_msg_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			
				String url = IpConfig.getUri("getRegisterCode");
				
				key_value.put("action", "bank");
				
				
				OkHttpUtils.post()//
				.url(url)//
				.params(key_value)//
				.build()//
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e) {
						Log.e("error", "获取数据异常 ", e);
						
						String status ="false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					
						
					}

					@Override
					public void onResponse(String response) {
					
						Map<String, String> map = new HashMap<String, String>();
						String jsonString = response;
						Log.d("onSuccess", "onSuccess json = " + jsonString);

					       Log.d("55555",jsonString+"555");
							try {
								
								if(jsonString==null||jsonString.equals("")||jsonString.equals("null"))
								{ 
									String status ="false";
									Message message = Message.obtain();

									message.obj = status;

									errcode_handler.sendMessage(message);
								}
								
								JSONObject jsonObject = new JSONObject(
										jsonString);
							//	Log.d("post", jsonObject + "");

							} catch (JSONException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}

					}
				});
				time.start();// 开始计时
				Toast.makeText(MobileFastLoginActivity.this, "获取中",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	public class MyStringCallback extends StringCallback {

		@Override
		public void onError(okhttp3.Call call, Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResponse(String response) {

			
			String jsonString = response;
			Log.d("onSuccess", "onSuccess json = " + jsonString);

			Map<String, String> map = new HashMap<String, String>();

			try {

				if (jsonString == null || jsonString.equals("")
						|| jsonString.equals("null")) {
					String status = "false";
					Message message = Message.obtain();

					message.obj = status;

					errcode_handler.sendMessage(message);
				} else {
					JSONObject jsonObject = new JSONObject(
							jsonString);

					// String info =
					// jsonObject.getString("info");

					String status = jsonObject
							.getString("status");

					Log.d("post", jsonObject + "");

					String errcode = jsonObject
							.getString("errcode");

					if (status.equals("true")) {
						String data = jsonObject
								.getString("data");
						JSONObject dataObject = new JSONObject(
								data);
						String sex = dataObject
								.getString("sex");
						String birthday = dataObject
								.getString("birthday");
						String mobile = dataObject
								.getString("mobile");
						String user_id = dataObject
								.getString("user_id");
						String token = dataObject
								.getString("token");
						String cert_b = dataObject
								.getString("cert_b");
						String cert_a = dataObject
								.getString("cert_a");
						String licence = dataObject
								.getString("licence");
						String type = dataObject
								.getString("type");
						String nickname = dataObject
								.getString("nickname");
						String state = dataObject
								.getString("state");
						String avatar = dataObject
								.getString("avatar");
						String truename = dataObject
								.getString("name");
						String idno = dataObject
								.getString("idno");
						String company_name = dataObject
								.getString("company_name");
						String company = dataObject
								.getString("company");
						String mobile_area = dataObject
								.getString("mobile_area");
						String bank_name = dataObject
								.getString("bank_name");
						String bank_no = dataObject
								.getString("bank_no");
						ccyj_reference_flag = dataObject
								.getString("ccyj_reference_flag");

						String referral = dataObject
								.getString("referral");
						JSONObject referralObject = new JSONObject(
								referral);
						String refer_name = referralObject
								.getString("name");

						String recomend_code = dataObject
								.getString("recomend_code");

						JSONObject stateObject = new JSONObject(
								state);
						String state_code = stateObject
								.getString("code");

						JSONObject typeObject = new JSONObject(
								type);
						String type_code = typeObject
								.getString("code");
						if (!type_code.equals("02")) {
							String personal_specialty = dataObject
									.getString("personal_specialty");
							String good_count = dataObject
									.getString("good_count");
							String cert_num_isShowFlg = dataObject
									.getString("cert_num_isShowFlg");
							String isShow_in_expertlist = dataObject
									.getString("isShow_in_expertlist");
							String mobile_num_short = dataObject
									.getString("mobile_num_short");
							String personal_context = dataObject
									.getString("personal_context");

							map.put("personal_specialty",
									personal_specialty);
							map.put("good_count", good_count);
							map.put("cert_num_isShowFlg",
									cert_num_isShowFlg);
							map.put("isShow_in_expertlist",
									isShow_in_expertlist);
							map.put("mobile_num_short",
									mobile_num_short);
							map.put("personal_context",
									personal_context);

						}
						map.put("errcode", errcode);
						map.put("mobile", mobile);
						map.put("user_id", user_id);
						map.put("token", token);
						map.put("idno", idno);
						map.put("type_code", type_code);
						map.put("state_code", state_code);
						map.put("avatar", avatar);
						map.put("cert_a", cert_a);
						map.put("cert_b", cert_b);
						map.put("licence", licence);
						map.put("nickname", nickname);
						map.put("truename", truename);

						map.put("sex", sex);
						map.put("birthday", birthday);

						map.put("company_name", company_name);
						map.put("company", company);
						map.put("mobile_area", mobile_area);
						map.put("bank_name", bank_name);
						map.put("bank_no", bank_no);
						map.put("refer_name", refer_name);
						map.put("recomend_code", recomend_code);

						Message message = Message.obtain();

						message.obj = map;

						handler.sendMessage(message);
					} else {

						String errmsg = jsonObject
								.getString("errmsg");
						map.put("errcode", errcode);
						map.put("errmsg", errmsg);
						Message message = Message.obtain();

						message.obj = map;

						handler.sendMessage(message);

					}
				}
			} catch (JSONException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			
	
		}

	}
	private void setUserInfo(String user_id, String sex, String birthday,
							 String idno, String token, String mobile, String type,
							 String state, String avatar, String cert_a, String cert_b,
							 String licence, String truename, String nickname,
							 String company_name, String company, String mobile_area,
							 String bank_name, String bank_no, String refer_name,
							 String recomend_code, String ccyj_reference_flag) {
		if (null == nickname || "null".equals(nickname) || "".equals(nickname)) {
			nickname = "用户" + user_id;
		}

		Editor editor1 = sp.edit();
		editor1.putString("Login_STATE", "SUCCESS");
		editor1.putString("Login_UID", user_id);
		editor1.putString("Login_TOKEN", token);
		editor1.putString("Login_TYPE", type);
		editor1.putString("Login_CERT", state);
		editor1.putString("USER_NAME", mobile);
		editor1.putString("idno", idno);
		editor1.putString("pwMd5", pwMd5);
		editor1.putString("avatar", avatar);
		editor1.putString("cert_a", cert_a);
		editor1.putString("cert_b", cert_b);
		editor1.putString("licence", licence);
		editor1.putString("truename", truename);
		editor1.putString("nickname", nickname);
		editor1.commit();

		Editor editor2 = sp2.edit();
		editor2.putString("company_name", company_name);
		editor2.putString("company", company);
		editor2.putString("mobile_area", mobile_area);
		editor2.putString("bank_name", bank_name);
		editor2.putString("bank_no", bank_no);
		editor2.putString("refer_name", refer_name);
		editor2.putString("recomend_code", recomend_code);
		editor2.putString("sex", sex);
		editor2.putString("birthday", birthday);
		editor2.putString("ccyj_reference_flag", ccyj_reference_flag);
		editor2.commit();

		Editor editor4 = sp4.edit();

		editor4.putString("old_phone", mobile);
		editor4.putString("setting_g_show", "off");
		editor4.putString("ges_show", "has");
		editor4.putString("gesture_pw", "");

		editor4.commit();

		finish();

	}


	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onTick(long millisUntilFinished) {
			verify_msg_button.setClickable(false);
			verify_msg_button.setText("重新获取" + "(" + millisUntilFinished / 1000
					+ ")");
		}

		@Override
		public void onFinish() {
			verify_msg_button.setText("获取验证码");
			verify_msg_button.setClickable(true);

		}
	}

}
