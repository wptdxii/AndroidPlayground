package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.network.retrofit.callback.BaseCallBack;
import com.cloudhome.network.retrofit.entity.SystemMessageEntity;
import com.cloudhome.network.retrofit.service.ApiFactory;

import static com.cloudhome.R.drawable.completed;

public class MessageDetailActivity extends BaseActivity {

	private TextView m_d_content;
	private TextView m_d_time;
	private RelativeLayout m_d_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private Long message_id;
	private String mSuid;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_detail);
		Intent intent = getIntent();
		message_id = Long.valueOf(intent.getIntExtra("messageId",0));
		initView();
		initData();
	}

	void initView() {
		m_d_back = (RelativeLayout) findViewById(R.id.rl_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_share);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_title);
		tv_text.setText("通知详情");
		m_d_content = (TextView) findViewById(R.id.m_d_content);
		m_d_time = (TextView) findViewById(R.id.m_d_time);
		m_d_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	void initData() {
		mSuid=sp.getString("Encrypt_UID","");
		getMessageDetail();
	}

	/**
	 * 获取消息详情
	 */
	private void getMessageDetail() {
		ApiFactory.getHoustonApi()
				.getSystemMessage(message_id,mSuid)
				.enqueue(new BaseCallBack<SystemMessageEntity>() {
					@Override
					protected void onResponse(SystemMessageEntity body) {
						m_d_time.setText(body.getAddTime());
						m_d_content.setText(body.getContent());
					}
				});
	}
}
