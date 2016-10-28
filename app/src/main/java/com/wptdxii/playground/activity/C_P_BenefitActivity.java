package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

public class C_P_BenefitActivity extends BaseActivity {

	private TextView benefit;
	private RelativeLayout c_p_benefit_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private String benefitstr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.c_p_benefit);
	
		Intent intent = getIntent();
		
		benefitstr = intent.getStringExtra("benefit");
	     
		Log.d("benefitstr",benefitstr+"777");
		
		init();
		initEvent();

	}

	void init() {
		c_p_benefit_back = (RelativeLayout)findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("受益人信息");
		benefit = (TextView)findViewById(R.id.benefit);
	
	
	}

	void initEvent() {

		benefit.setText(benefitstr);
	
		
		c_p_benefit_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

	

	}




}
