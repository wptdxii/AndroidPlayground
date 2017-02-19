package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MessageDetailActivity extends BaseActivity {

	private TextView m_d_content;
	private TextView m_d_time;
	private RelativeLayout m_d_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private String user_id;
	private String token;
	private String message_id;
	private String time="";
	private Map<String, String> key_value = new HashMap<String, String>();

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;
			
			String content =data.get("content");
			
			if(content.equals("null"))
			{
				content="";
			}
			m_d_content.setText(content);
			
		}

	};
	
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
				
				Toast.makeText(MessageDetailActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.message_detail);

		Intent intent = getIntent();
		time=intent.getStringExtra("time");
		message_id = intent.getStringExtra("message_id");
		Log.d("44444",message_id);
		key_value.put("message_id", message_id);
		init();
		initEvent();

	}

	void init() {
		m_d_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("通知详情");
		m_d_content = (TextView) findViewById(R.id.m_d_content);
		m_d_time = (TextView) findViewById(R.id.m_d_time);
		m_d_time.setText(time);

	}

	void initEvent() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("token", token);
		key_value.put("user_id", user_id);
		
		m_d_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		final String PRODUCT_URL = IpConfig.getUri("getSystemMessageDetail");
		// new MyTask().execute(PRODUCT_URL);
		setdata(PRODUCT_URL);
	
	}

	private void setdata(String url) {

		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);

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
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {


					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						// Log.d("44444", jsonString);
						JSONObject jsonObject = new JSONObject(jsonString);
						String data = jsonObject.getString("data");
						JSONObject dataObject = new JSONObject(data);

						String content = dataObject.getString("content");

						Log.d("44444", data);

						map.put("content",content);

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
