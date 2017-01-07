package com.cloudhome.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
public class Verified_failure_InfoActivity extends BaseActivity {


	private TextView v_truename;
	private TextView v_id;
	private RelativeLayout verify_call;
	private String idno;
	private String truename;
	private ImageView verify_back,verify_again;
	

	public static Verified_failure_InfoActivity instance = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.verfied_failure_info);

		instance = this;
		init();
		initEvent();

	}

	void init() {
		verify_back = (ImageView) findViewById(R.id.verify_back);
		v_truename = (TextView) findViewById(R.id.v_truename);
		v_id = (TextView) findViewById(R.id.v_id);
		verify_call= (RelativeLayout) findViewById(R.id.verify_call);
		verify_again = (ImageView) findViewById(R.id.verify_again);
	}

	void initEvent() {

	
		idno = sp.getString("idno", "none");
		truename = sp.getString("truename", "none");
		v_truename.setText(truename);
		v_id.setText(idno);

	
		
		verify_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		verify_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				Intent intent = new Intent();
				intent.setClass(Verified_failure_InfoActivity.this, VerifyMemberActivity.class);
				Verified_failure_InfoActivity.this.startActivity(intent);
				
			}
		});
		
		verify_call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ "010-65886012"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				
			}
		});

	}



}
