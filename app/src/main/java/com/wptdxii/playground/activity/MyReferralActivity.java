package com.wptdxii.playground.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MyReferralActivity extends BaseActivity {


	private ClearEditText referral_edit;
	private String referral_code;
	private String user_id;
	private String token;
	private Button referral_submit;
	private Map<String, String> key_value = new HashMap<String, String>();
	private ImageView myreferral_back;
	
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;
			
			String errcode =data.get("errcode");
			String errmsg =data.get("errmsg");
			if(errcode.equals("0"))
			{
				String name =data.get("name");
				Editor editor1 = sp2.edit();
				editor1.putString("refer_name", name);
			
				editor1.commit();
				finish();
			}else{
				
			
				
				CustomDialog.Builder builder = new CustomDialog.Builder(
						MyReferralActivity.this);

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
		//	m_d_content.setText(content);
			
		}

	};
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.myreferral);


		init();
		initEvent();

	}

	void init() {
		myreferral_back=(ImageView)findViewById(R.id.myreferral_back);
		referral_edit = (ClearEditText)findViewById(R.id.referral_edit);

		referral_submit= (Button)findViewById(R.id.referral_submit);
	}

	void initEvent() {
		myreferral_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		referral_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				referral_code =referral_edit.getText().toString();
				if(referral_code==null||referral_code.equals("")||referral_code.equals("null"))
				{
					Toast.makeText(MyReferralActivity.this, "推荐人的手机号码或者验证码不能为空", Toast.LENGTH_LONG)
					.show();
				}else{
				user_id = sp.getString("Login_UID", "");
				token = sp.getString("Login_TOKEN", "");
				key_value.put("token", token);
				key_value.put("user_id", user_id);
				key_value.put("mobile", referral_code);
				final String PRODUCT_URL = IpConfig.getUri("setReferral");
				// new MyTask().execute(PRODUCT_URL);
				setdata(PRODUCT_URL);
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
			public void onError(Call call, Exception e) {
			
				Log.e("error", "获取数据异常 ", e);
				
			}

			@Override
			public void onResponse(String response) {
			
				Map<String, String> map = new HashMap<String, String>();
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
			
				try {

					// Log.d("44444", jsonString);
					JSONObject jsonObject = new JSONObject(jsonString);
					String data = jsonObject.getString("data");
			
				    
					String errcode = jsonObject.getString("errcode");
					String errmsg = jsonObject.getString("errmsg");
					
					if(errcode.equals("0"))
					{
					JSONObject dataObject = new JSONObject(data);
					String name = dataObject.getString("name");
					   map.put("name",name);
					Log.d("44444", data);
					}
                     map.put("errcode",errcode);
                  
                     map.put("errmsg",errmsg);
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
