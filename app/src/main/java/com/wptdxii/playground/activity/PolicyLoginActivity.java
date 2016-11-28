package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cloudhome.R;

public class PolicyLoginActivity extends BaseActivity {

	private TextView tab_login_but;
	private TextView tab_reg_but;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.policy_login);

		init();

		initEvent();

	}

	void init() {

		tab_login_but = (TextView) findViewById(R.id.tab_login_but);
		tab_reg_but = (TextView) findViewById(R.id.tab_reg_but);
	}

	void initEvent() {
		tab_login_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PolicyLoginActivity.this, LoginActivity.class);
				PolicyLoginActivity.this.startActivity(intent);

			}
		});
		tab_reg_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PolicyLoginActivity.this,
						RegisterActivity.class);
				PolicyLoginActivity.this.startActivity(intent);

			}
		});
	}


}
