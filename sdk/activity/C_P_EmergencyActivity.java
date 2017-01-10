package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
public class C_P_EmergencyActivity extends BaseActivity {

	private TextView emergency_name,emergency_num;
	private RelativeLayout c_p_emergency_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private String emergencyname,emergencynum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.c_p_emergency);
	
		Intent intent = getIntent();
		
	
		
		emergencyname = intent.getStringExtra("emergencyname");
		emergencynum = intent.getStringExtra("emergencynum");
	
		
		init();
		initEvent();

	}

	void init() {
		c_p_emergency_back = (RelativeLayout)findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("紧急联系人");
		emergency_name = (TextView)findViewById(R.id.emergency_name);
		emergency_num = (TextView)findViewById(R.id.emergency_num);
	
	}

	void initEvent() {

		emergency_name.setText(emergencyname);
		emergency_num.setText(emergencynum);
		
		c_p_emergency_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

	

	}




}
