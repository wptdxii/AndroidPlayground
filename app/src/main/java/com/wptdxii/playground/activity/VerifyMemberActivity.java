package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IdCheckUtil;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class VerifyMemberActivity extends BaseActivity {


	private ClearEditText verify_truename_edit;
	private ClearEditText verify_id_edit;
	private Map<String, String> key_value = new HashMap<String, String>();
	private String verify_truename;
	private String verify_id;
	private String user_id;
	private String token;
	private RelativeLayout verify_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private ImageView verify_submit;
	private boolean isFromRegister=false;
	private Dialog dialog;

	@SuppressLint("HandlerLeak") private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			dialog.dismiss();
			Map<String, String> data = (Map<String, String>) msg.obj;
			String errcode = data.get("errcode");
			if (errcode.equals("0")) {
				String idno = data.get("idno");
				String name = data.get("name");
				Editor editor1 = sp.edit();
				editor1.putString("idno", idno);
				editor1.putString("truename", name);
				editor1.putString("Login_CERT", "02");
				editor1.commit();
				verify_submit.setImageResource(R.drawable.gray_verify_button);
				verify_submit.setClickable(false);
				verify_truename_edit.setKeyListener(null);
				verify_id_edit.setKeyListener(null);
				CustomDialog.Builder builder = new CustomDialog.Builder(
						VerifyMemberActivity.this);
				builder.setTitle("提示");
				builder.setMessage("成功提交验证，并交付相关人员处理，请耐心等待！");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								if(Verified_failure_InfoActivity.instance!=null)
								{
								Verified_failure_InfoActivity.instance.finish();
								}
							}
						});
				builder.create().show();
			} else {
				String errmsg = data.get("errmsg");
				CustomDialog.Builder builder = new CustomDialog.Builder(
						VerifyMemberActivity.this);
				builder.setTitle("提示");
				builder.setMessage(errmsg);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								if(Verified_failure_InfoActivity.instance!=null)
								{
									Verified_failure_InfoActivity.instance.finish();
								}
							}
						});
				builder.create().show();
			}

		}

	};

	@SuppressLint("HandlerLeak") private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;
			String status = data;
			dialog.dismiss();
				Toast.makeText(VerifyMemberActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.verifymember);
		Intent intent=getIntent();
		isFromRegister=intent.getBooleanExtra("isFromRegister",false);
		init();
		initEvent();

	}

	void init() {
		verify_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("资质认证");
		verify_truename_edit = (ClearEditText) findViewById(R.id.verify_truename_edit);
		verify_id_edit = (ClearEditText) findViewById(R.id.verify_id_edit);
		verify_submit = (ImageView) findViewById(R.id.verify_submit);
		verify_truename_edit.addTextChangedListener(Name_TextWatcher);
		verify_id_edit.addTextChangedListener(ID_TextWatcher);

		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(false);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");
	}

	void initEvent() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("user_id", user_id);
		key_value.put("token", token);

		verify_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//如果从注册成功过来
				if(isFromRegister){
					Intent intent=new Intent(VerifyMemberActivity.this,AllPageActivity.class);
					startActivity(intent);
				}else{
					finish();
				}

			}
		});
		verify_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				verify_truename = verify_truename_edit.getText().toString();
				verify_id = verify_id_edit.getText().toString();

				Boolean IDTure = IdCheckUtil.IDCardValidate(verify_id);

				if (verify_truename == null || verify_truename.equals("")
						|| verify_truename.equals("null")) {

					Toast.makeText(VerifyMemberActivity.this, "真实姓名不能为空！",
							Toast.LENGTH_LONG).show();
				} else if (verify_id == null || verify_id.equals("")
						|| verify_id.equals("null")) {

					Toast.makeText(VerifyMemberActivity.this, "身份证号不能不能为空！",
							Toast.LENGTH_LONG).show();
				} else if (!IDTure) {
					Toast.makeText(VerifyMemberActivity.this, "请输入正确的身份证号码!",
							Toast.LENGTH_LONG).show();
				}
		
				else {
					dialog.show();
					key_value.put("name", verify_truename);
					key_value.put("idno", verify_id);
					final String PRODUCT_URL = IpConfig.getUri("certificate");
					// new MyTask().execute(PRODUCT_URL);
					setdata(PRODUCT_URL);
				}
			}
		});

	}

	
	
	TextWatcher Name_TextWatcher = new TextWatcher() {


		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
									  int arg3) {
		
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString();
	
			String verify_id = verify_id_edit.getText().toString();
		
			if(text.equals("null")||text.equals("")||verify_id.equals("null")||verify_id.equals(""))
			{
				verify_submit.setImageResource(R.drawable.gray_verify_button);
			}else{
				verify_submit.setImageResource(R.drawable.orange_verify_button);
			}
			
			
		}
	};

	TextWatcher ID_TextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
									  int arg3) {
		}
		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString();
			String verify_truename = verify_truename_edit.getText().toString();
			if(text.equals("null")||text.equals("")||verify_truename.equals("null")||verify_truename.equals(""))
			{
				verify_submit.setImageResource(R.drawable.gray_verify_button);
			}else{
				verify_submit.setImageResource(R.drawable.orange_verify_button);
			}
		}
	};
	
	private void setdata(String url) {

		OkHttpUtils.post()//
				.url(url)//
				.params(key_value)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						String status = "false";
						Message message = Message.obtain();
						message.obj = status;
						errcode_handler.sendMessage(message);
					}

					@Override
					public void onResponse(String response, int id) {
						Map<String, String> map = new HashMap<String, String>();
						String jsonString = response;
						Log.d("onSuccess", "onSuccess json = " + jsonString);
						try {
							if (jsonString == null || jsonString.equals("")|| jsonString.equals("null")) {
								String status = "false";
								Message message = Message.obtain();
								message.obj = status;
								errcode_handler.sendMessage(message);
							} else {
								// Log.d("44444", jsonString);
								JSONObject jsonObject = new JSONObject(jsonString);
								String errcode = jsonObject.getString("errcode");
								String errmsg = jsonObject.getString("errmsg");
								if(errcode.equals("0")){
									String data = jsonObject.getString("data");
									JSONObject dataObject = new JSONObject(data);
									String cert_a = dataObject.getString("cert_a");
									String cert_b = dataObject.getString("cert_b");
									String idno = dataObject.getString("idno");
									String name = dataObject.getString("name");
									map.put("errcode", errcode);
									map.put("errmsg", errmsg);
									map.put("cert_a", cert_a);
									map.put("cert_b", cert_b);
									map.put("idno", idno);
									map.put("name", name);
								}else{
									map.put("errcode", errcode);
									map.put("errmsg", errmsg);
								}
								Message message = Message.obtain();
								message.obj = map;
								handler.sendMessage(message);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}
