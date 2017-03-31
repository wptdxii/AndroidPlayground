package com.cloudhome.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
public class VerifiedInfoActivity extends BaseActivity {



	private TextView v_truename;
	private TextView v_id;
	private TextView v_agent;
	private TextView v_broker;
	private TextView v_state;
	private TextView practice_num;
	private TextView assessment_num;

	private View view3;
	private View view4;
	private View view5,view6;
	private String user_id;
	private String token;
	private RelativeLayout verify_rel4, verify_rel5, verify_rel6,verify_rel7;
	private RelativeLayout verify_back;
	private TextView title;
	private RelativeLayout rl_right;
//	private Map<String, String> key_value = new HashMap<String, String>();

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

				Toast.makeText(VerifiedInfoActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.verfied_info);


	//	user_id = sp.getString("Login_UID", "none");
	//	token = sp.getString("Login_TOKEN", "none");
		init();
		initEvent();

	}

	void init() {
		title= (TextView) findViewById(R.id.tv_title);
		title.setText("会员认证");
		rl_right=(RelativeLayout) findViewById(R.id.rl_share);
		rl_right.setVisibility(View.INVISIBLE);
		verify_back = (RelativeLayout) findViewById(R.id.rl_back);
		v_state = (TextView) findViewById(R.id.v_state);
		v_truename = (TextView) findViewById(R.id.v_truename);
		v_id = (TextView) findViewById(R.id.v_id);
		v_agent = (TextView) findViewById(R.id.v_agent);
		v_broker = (TextView) findViewById(R.id.v_broker);
		practice_num = (TextView) findViewById(R.id.practice_num);
		assessment_num = (TextView) findViewById(R.id.assessment_num);
	
		verify_rel4 = (RelativeLayout) findViewById(R.id.verify_rel4);
		verify_rel5 = (RelativeLayout) findViewById(R.id.verify_rel5);
		verify_rel6 = (RelativeLayout) findViewById(R.id.verify_rel6);
		verify_rel7 = (RelativeLayout) findViewById(R.id.verify_rel7);
		

		
		view3 = findViewById(R.id.view3);
		view4 = findViewById(R.id.view4);
		view5 = findViewById(R.id.view5);
		view6 = findViewById(R.id.view6);
	}

	void initEvent() {

//		key_value.put("user_id", user_id);
//		key_value.put("token", token);
//
//		Log.d("user_id", user_id);
//		Log.d("token", token);

		String user_state = sp.getString("Login_CERT", "");
		String cert_a = sp.getString("cert_a", "");
		String cert_b = sp.getString("cert_b", "");
		String licence = sp.getString("licence", "");
		String assessment = sp.getString("assessment", "");

		String idno = sp.getString("idno", "none");
		String truename = sp.getString("truename", "none");
		if (user_state.equals("02")) {
			v_state.setText("认证中");

	
			verify_rel4.setVisibility(View.GONE);
			verify_rel5.setVisibility(View.GONE);
			verify_rel6.setVisibility(View.GONE);
			verify_rel7.setVisibility(View.GONE);
			
			view3.setVisibility(View.GONE);
			view4.setVisibility(View.GONE);
			view5.setVisibility(View.GONE);
			view6.setVisibility(View.GONE);
			
			
		} else if (user_state.equals("00")) {
			v_state.setText("已认证");

//			String url = IpConfig.getUri("getCertificate");
//
//			Log.d("url", url);
//			setdata(url);
		}
		v_truename.setText(truename);
		v_id.setText(idno);

		
		if (cert_a.equals("") || cert_a.equals("null")) {
			v_agent.setText("");
		} else {
			v_agent.setText(cert_a);
		}

		if (cert_b.equals("") || cert_b.equals("null")) {

			v_broker.setText("");
		} else {
			v_broker.setText(cert_b);
		}

		if (licence.equals("") || licence.equals("null")) {

			practice_num.setText("");
		} else {
			practice_num.setText(licence);
		}
		if (assessment.equals("") || assessment.equals("null")) {

			assessment_num.setText("");
		} else {
			assessment_num.setText(assessment);
		}
		
	


		verify_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}


	
}
