package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

public class Holder_InsuredActivity extends BaseActivity {

	private TextView holder_name, holder_num, insured_name, insured_num;
	private RelativeLayout h_i_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private String holdername,holdernum,insuredname,insurednum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.holder_insured);
	
		Intent intent = getIntent();
		
		holdername = intent.getStringExtra("holdername");
		holdernum = intent.getStringExtra("holdernum");
		insuredname = intent.getStringExtra("insuredname");
		insurednum = intent.getStringExtra("insurednum");
		
		init();
		initEvent();

	}

	void init() {
		h_i_back = (RelativeLayout)findViewById(R.id.rl_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_share);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_title);
		tv_text.setText("投被保人信息");
		holder_name = (TextView)findViewById(R.id.tv_owner_name);
		holder_num = (TextView)findViewById(R.id.holder_num);
		insured_name = (TextView)findViewById(R.id.insured_name);
		insured_num = (TextView)findViewById(R.id.insured_num);
	
	}

	void initEvent() {

		holder_name.setText(holdername);
		holder_num.setText(holdernum);
		insured_name.setText(insuredname);
		insured_num.setText(insurednum);
		
		h_i_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

	

	}




}
