package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RegisterMsgActivity extends BaseActivity {
	private TimeCount time;
	private Button verify_msg_button;
	private String phonenum, pwMd5;
	private String user_type, recomend_codeing;
	private EditText reg_code;
	private EditText et_password;
	private EditText reg_password_confirm;
	private EditText et_referral_code;
	private Button register_complete;

	private String passwordstr;
	private String referral_code;
	private RelativeLayout verify_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private Map<String, String> params = new HashMap<String, String>();

	private Map<String, String> key_value = new HashMap<String, String>();
	private static final String TAG = "RegisterMsgActivity";

	private Handler referral_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");
			String errmsg = data.get("errmsg");
			if (errcode.equals("0")) {
				String name = data.get("name");
				Editor editor1 = sp2.edit();
				editor1.putString("refer_name", name);

				editor1.commit();

				/*Intent intent = new Intent(RegisterMsgActivity.this,
						AllPageActivity.class);

				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				startActivity(intent);
				finish();*/
				EventBus.getDefault().post(new LoginEvent());
				EventBus.getDefault().post(new DisorderJumpEvent(3));
				Intent intent1 = new Intent(RegisterMsgActivity.this,AllPageActivity.class);
				startActivity(intent1);
				finish();

			} else {

				CustomDialog.Builder builder = new CustomDialog.Builder(
						RegisterMsgActivity.this);

				builder.setTitle("提示");
				builder.setMessage(errmsg);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								/*Intent intent = new Intent(
										RegisterMsgActivity.this,
										AllPageActivity.class);

								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
								startActivity(intent);
								finish();*/
								EventBus.getDefault().post(new LoginEvent());
								EventBus.getDefault().post(new DisorderJumpEvent(3));
								Intent intent1 = new Intent(RegisterMsgActivity.this,AllPageActivity.class);
								startActivity(intent1);
								finish();
							}
						});
				builder.create().show();
			}


		}

	};
	private Handler login_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");

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
				String assessment = data.get("assessment");

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
				setUserInfo(user_id, sex, idno, token, mobile, type_code,
						state_code, avatar, cert_a, cert_b,licence, assessment,truename, nickname,
						company_name, company, mobile_area, bank_name, bank_no,
						refer_name, recomend_code);
				Toast.makeText(RegisterMsgActivity.this, "注册成功",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(RegisterMsgActivity.this, "注册成功,登录失败",
						Toast.LENGTH_LONG).show();
			}
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");

			Log.d("455454", "455445" + errcode);
			if (errcode.equals("0")) {

				pwMd5 = HttpMd5.getMD5(passwordstr);

				params.put("mobile", phonenum);
				params.put("password", pwMd5);

				Log.d("555444", pwMd5);

				String url = IpConfig.getUri("getMemberLogin");

				setData(url);

			} else {
				String errmsg = data.get("errmsg");
				Toast.makeText(RegisterMsgActivity.this, errmsg,
						Toast.LENGTH_LONG).show();
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

				Toast.makeText(RegisterMsgActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_verification);
		Intent intent = getIntent();
		phonenum = intent.getStringExtra("phone");
		user_type = intent.getStringExtra("user_type");
		recomend_codeing = intent.getStringExtra("recomend_code");

		Log.d("4444", phonenum);
		init();
		initEvent();

	}

	private void init() {
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
		verify_msg_button = (Button) findViewById(R.id.verify_msg_button);

		reg_code = (EditText) findViewById(R.id.reg_code);
		et_password = (EditText) findViewById(R.id.reg_password);
		reg_password_confirm = (EditText) findViewById(R.id.reg_password_confirm);
		et_referral_code = (EditText) findViewById(R.id.et_referral_code);
		verify_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("手机快速注册");
		register_complete = (Button) findViewById(R.id.register_complete);
	}

	private void initEvent() {

		if (recomend_codeing.equals("") || recomend_codeing.equals("null")) {
			et_referral_code.setVisibility(View.VISIBLE);
		} else {
			et_referral_code.setVisibility(View.GONE);
		}

		verify_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		register_complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				referral_code = et_referral_code.getText().toString();
				final String vercode = reg_code.getText().toString();
				passwordstr = et_password.getText().toString();
				String pwstrconfirm = reg_password_confirm.getText().toString();

				int length = vercode.length();
				int pwstrlength = passwordstr.length();
				int pwclength = pwstrconfirm.length();

				if (length != 6) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							RegisterMsgActivity.this);
					builder.setTitle("提示");
					builder.setMessage("请检查验证码");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else if (pwstrlength < 6 || pwstrlength > 16) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							RegisterMsgActivity.this);
					builder.setTitle("提示");
					builder.setMessage("请输入6 ~ 16位的密码");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else if (pwstrlength != pwclength) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							RegisterMsgActivity.this);
					builder.setTitle("提示");
					builder.setMessage("密码不一致");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();

				} else if (!passwordstr.equals(pwstrconfirm)) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							RegisterMsgActivity.this);
					builder.setTitle("提示");
					builder.setMessage("密码不一致");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				}

				else {

					String path = IpConfig.getUri("getRegister");

					OkHttpUtils.post().url(path).addParams("mobile", phonenum)
							.addParams("password", passwordstr)
							.addParams("verification", vercode)
							.addParams("user_type", user_type).build()
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
									Map<String, String> map = new HashMap<String, String>();

									String jsonString = response;

									Log.d("onSuccess", "onSuccess json = "
											+ jsonString);

									try {

										if (null == jsonString
												|| "null".equals(jsonString)
												|| "".equals(jsonString)) {
											Looper.prepare();
											Toast.makeText(
													RegisterMsgActivity.this,
													"注册失败", Toast.LENGTH_SHORT).show();
											Looper.loop();
											return;
										}
										JSONObject jsonObject = new JSONObject(
												jsonString);
										Log.d("post", jsonObject + "");
										String status = jsonObject
												.getString("status");
										String errcode = jsonObject
												.getString("errcode");
										if (status.equals("true")) {
											String data = jsonObject
													.getString("data");
											JSONObject dataObject = new JSONObject(
													data);
											String mobile = dataObject
													.getString("mobile");
											String user_id = dataObject
													.getString("user_id");
											String token = dataObject
													.getString("token");

											map.put("errcode", errcode);
											map.put("mobile", mobile);
											map.put("user_id", user_id);
											map.put("token", token);
											Message message = Message.obtain();

											message.obj = map;

											handler.sendMessage(message);
										} else {
											String errmsg = jsonObject
													.getString("errmsg");
											map.put("errmsg", errmsg);
											map.put("errcode", errcode);

											Message message = Message.obtain();

											message.obj = map;

											handler.sendMessage(message);
										}

									} catch (JSONException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}
								}
							});

			
				}
			}
		});

		verify_msg_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				
			
				
				
				String path = IpConfig.getUri("getRegisterCode");
				
				OkHttpUtils.post().url(path)
				.addParams("mobile", phonenum)
				.addParams("action", "regist")
				.build()
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
						Map<String, String> map = new HashMap<String, String>();

						String jsonString = response;

						Log.d("onSuccess", "onSuccess json = "
								+ jsonString);

						if (null == jsonString
								|| "null".equals(jsonString)
								|| "".equals(jsonString)) {
							Looper.prepare();
							Toast.makeText(
									RegisterMsgActivity.this,
									"获取验证码失败", Toast.LENGTH_SHORT).show();
							Looper.loop();
						}

					}
				});
				
				
			
				time.start();// 开始计时
				Toast.makeText(RegisterMsgActivity.this, "获取中",
						Toast.LENGTH_SHORT).show();
			}
		});
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

	

	private void setData(String url) {

		OkHttpUtils.post()
				.url(url)
				.params(params)
				.build()
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e, int id) {

						Log.e("error", "获取数据异常 ", e);
					}

					@Override
					public void onResponse(String response, int id) {
						String jsonString = response;
						Log.d("onSuccess", "onSuccess json = " + jsonString);

						Map<String, String> map = new HashMap<String, String>();

						try {
							JSONObject jsonObject = new JSONObject(jsonString);
							// String info = jsonObject.getString("info");
							String status = jsonObject.getString("status");

							Log.d("post", jsonObject + "");
							String errcode = jsonObject.getString("errcode");

							if (status.equals("true")) {
								String data = jsonObject.getString("data");
								JSONObject dataObject = new JSONObject(data);

								String sex = dataObject.getString("sex");
								String mobile = dataObject.getString("mobile");
								String user_id = dataObject
										.getString("user_id");
								String token = dataObject.getString("token");
								String cert_b = dataObject.getString("cert_b");
								String cert_a = dataObject.getString("cert_a");
								String licence = dataObject.getString("licence");
								String assessment = dataObject.getString("assessment");

								String type = dataObject.getString("type");
								String nickname = dataObject
										.getString("nickname");
								String state = dataObject.getString("state");
								String avatar = dataObject.getString("avatar");
								String truename = dataObject.getString("name");
								String idno = dataObject.getString("idno");
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

								String referral = dataObject
										.getString("referral");
								JSONObject referralObject = new JSONObject(
										referral);

								String refer_name = referralObject
										.getString("name");
								String recomend_code = dataObject
										.getString("recomend_code");

								JSONObject stateObject = new JSONObject(state);
								String state_code = stateObject
										.getString("code");

								JSONObject typeObject = new JSONObject(type);
								String type_code = typeObject.getString("code");
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
								map.put("sex", sex);
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
								map.put("assessment", assessment);
								map.put("nickname", nickname);
								map.put("truename", truename);
								map.put("company_name", company_name);
								map.put("company", company);
								map.put("mobile_area", mobile_area);
								map.put("bank_name", bank_name);
								map.put("bank_no", bank_no);
								map.put("refer_name", refer_name);
								map.put("recomend_code", recomend_code);

								Message message = Message.obtain();

								message.obj = map;

								login_handler.sendMessage(message);
							} else {
								map.put("errcode", errcode);

								Message message = Message.obtain();

								message.obj = map;

								login_handler.sendMessage(message);
							}

						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
				});

	}

	private void setUserInfo(String user_id, String sex, String idno,
							 String token, String mobile, String type, String state,
							 String avatar, String cert_a, String cert_b, String licence, String assessment, String truename,
							 String nickname, String company_name, String company,
							 String mobile_area, String bank_name, String bank_no,
							 String refer_name, String recomend_code) {

		
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
		editor1.putString("assessment", assessment);
		
		editor1.putString("truename", truename);
		editor1.putString("nickname", nickname);
		editor1.commit();

		Editor editor2 = sp2.edit();
		editor2.putString("company_name", company_name);
		editor2.putString("company", company);
		editor2.putString("mobile_area", mobile_area);
		editor2.putString("bank_name", bank_name);
		editor2.putString("bank_no", bank_no);
		editor2.putString("refer_name", "");
		editor2.putString("sex", "01");
		editor2.putString("recomend_code", recomend_code);
		editor2.commit();

		Editor editor4 = sp4.edit();

		editor4.putInt("page", 4);
		editor4.putString("old_phone", mobile);
		editor4.commit();

		if (recomend_codeing.equals("") || recomend_codeing.equals("null")) {

			if (!referral_code.equals("") && !referral_code.equals("null")) {

				key_value.put("token", token);
				key_value.put("user_id", user_id);
				key_value.put("mobile", referral_code);
				final String PRODUCT_URL = IpConfig.getUri("setReferral");

				// new MyTask().execute(PRODUCT_URL);
				Log.i(TAG, "11111");
				setreferral(PRODUCT_URL);
			} else {
//				Intent intent = new Intent(this, AllPageActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//				finish();
				Log.i(TAG, "222222");
				EventBus.getDefault().post(new LoginEvent());
				EventBus.getDefault().post(new DisorderJumpEvent(3));
				Intent intent1 = new Intent(RegisterMsgActivity.this,AllPageActivity.class);
				startActivity(intent1);
				finish();
			}
		} else {
			Log.i(TAG, "33333");
			Log.d("token", token + "77");
			Log.d("user_id", user_id + "77");

			key_value.put("token", token);
			key_value.put("user_id", user_id);
			key_value.put("mobile", recomend_codeing);
			final String PRODUCT_URL = IpConfig.getUri("setReferral");

			// new MyTask().execute(PRODUCT_URL);

			setreferral(PRODUCT_URL);
		}
	}

	private void setreferral(String url) {

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

							if (errcode.equals("0")) {
								JSONObject dataObject = new JSONObject(data);
								String name = dataObject.getString("name");
								map.put("name", name);
								Log.d("44444", data);
							}
							map.put("errcode", errcode);

							map.put("errmsg", errmsg);
							Message message = Message.obtain();

							message.obj = map;

							referral_handler.sendMessage(message);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}


}
