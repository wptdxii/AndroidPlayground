package com.cloudhome.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cloudhome.R;
public class FunctionGuideActivity extends BaseActivity {
	
	private RelativeLayout rl_a;
	private RelativeLayout rl_b;
	private RelativeLayout rl_c;
	private ImageView iv_a_bottom;
	private ImageView iv_b_right;
	private ImageView iv_c_bottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_function_guide);

		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		rl_a=(RelativeLayout) findViewById(R.id.rl_a);
		rl_b=(RelativeLayout) findViewById(R.id.rl_b);
		rl_c=(RelativeLayout) findViewById(R.id.rl_c);
		iv_a_bottom=(ImageView) findViewById(R.id.iv_a_bottom);
		iv_b_right=(ImageView) findViewById(R.id.iv_b_right);
		iv_c_bottom=(ImageView) findViewById(R.id.iv_c_bottom);
		
		iv_a_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				rl_a.setVisibility(View.GONE);
			}
		});
		
		iv_b_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferences preferences = getSharedPreferences("isFirstShowMain",1);
				Editor editor = preferences.edit();
				editor.putBoolean("isFirstShowMain", false);
				editor.commit();
				Intent intent=new Intent();
				setResult(13);
				finish();
			}
		});
		
		iv_c_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				rl_c.setVisibility(View.GONE);
			}
		});
	}
}
