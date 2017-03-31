package com.cloudhome.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.event.ModifyUserInfoEvent;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.cloudhome.R.drawable.completed;

public class CreToBlanceActivity extends BaseActivity {

//	SharedPreferences sp2;
	private String user_id;
	private String token;
	private String credits;
	private TextView mycredits;
	private EditText credit_edit;
	private Button balance_submit;
	private RelativeLayout balance_back;
	private RelativeLayout rl_right;
	private TextView tv_title;
	private String consume_score;
	private Map<String, String> key_value = new HashMap<String, String>();
	
	private String user_state;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;
			
			String errcode =data.get("errcode");
			
			//String score =data.get("score");
			
			if(errcode.equals("0"))
			{
				
				//Editor editor = sp2.edit();
				//.putString("score", score);
				
				//editor.commit();
				//发送基本信息改变的广播
				EventBus.getDefault().post(new ModifyUserInfoEvent());
				finish();
				Toast.makeText(CreToBlanceActivity.this, "兑换成功",
						Toast.LENGTH_LONG).show();
			}else{
				String errmsg =data.get("errmsg");
				
				Toast.makeText(CreToBlanceActivity.this, errmsg,
						Toast.LENGTH_LONG).show();
			}
			
	
		}

	};
	
	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
		//	Map<String, String> data = (Map<String, String>) msg.obj;

			String status = (String) msg.obj;
			//String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {
				
				
				Toast.makeText(CreToBlanceActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT)
				.show();
			}
		}

	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.balance);


		user_state = sp.getString("Login_CERT", "");
		Intent intent = getIntent();

		credits = intent.getStringExtra("score");
		
		init();
		initEvent();

	}

	void init() {
		mycredits= (TextView)findViewById(R.id.mycredits);
		credit_edit= (EditText)findViewById(R.id.credit_edit);
		balance_submit= (Button)findViewById(R.id.balance_submit);
		balance_back= (RelativeLayout)findViewById(R.id.rl_back);
		rl_right=(RelativeLayout)findViewById(R.id.rl_share);
		rl_right.setVisibility(View.INVISIBLE);
		tv_title= (TextView) findViewById(R.id.tv_title);
		tv_title.setText("余额兑换");
	}
	void initEvent() {
		mycredits.setText(credits+"分");
	
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("token", token);
		key_value.put("user_id", user_id);
		
		
		/*if(!user_state.equals("00")){
			
			balance_submit.setBackgroundResource(R.drawable.pub_grey_button_style);
			balance_submit.setClickable(false);
		}*/
		
		balance_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	
		
		balance_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				consume_score= credit_edit.getText().toString();
				
				
				Log.d("consume_score",consume_score);
			
				
			if(consume_score==null||consume_score.equals("")||consume_score.equals("null")){
				
				CustomDialog.Builder builder = new CustomDialog.Builder(CreToBlanceActivity.this);
				builder.setTitle("提示");
					builder.setMessage("请输入要兑换的积分数");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						
						}
					});
					builder.create().show();
			}else {
				//int score= Integer.parseInt(consume_score);
				
				int score_length =consume_score.length();
				
				
				int credits_length = credits.length();
				//int score=Integer.valueOf(consume_score);
				
				int creditsnum=	Integer.valueOf(credits);
				
				 
//				if(score>creditsnum)
//				{
//					CustomDialog.Builder builder = new CustomDialog.Builder(CreToBlanceActivity.this);
//					builder.setMessage("提示");
//						builder.setTitle("输入的积分数不能大于现有的积分数");
//						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								dialog.dismiss();
//							
//							}
//						});
//						builder.create().show();
//				}
//				else 
				if(score_length>credits_length)
					{
					
					CustomDialog.Builder builder = new CustomDialog.Builder(CreToBlanceActivity.this);
					
					builder.setTitle("提示");
						builder.setMessage("输入的积分数不能大于现有的积分数");
					
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							
							}
						});
						builder.create().show();
					}else if(score_length==credits_length){
						
						int score= Integer.parseInt(consume_score);
						if(score>creditsnum)
						{
							CustomDialog.Builder builder = new CustomDialog.Builder(CreToBlanceActivity.this);
							
							builder.setTitle("提示");
								builder.setMessage("输入的积分数不能大于现有的积分数");
							
								builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									
									}
								});
								builder.create().show();
						}else{
							
						
							if(score%100!=0)
							{
							CustomDialog.Builder builder = new CustomDialog.Builder(CreToBlanceActivity.this);
							builder.setTitle("提示");
								builder.setMessage("请输入100的整数倍");
								builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									
									}
								});
								builder.create().show();
							
							}else{
								key_value.put("consume_score", score +"");
								final String PRODUCT_URL = IpConfig.getUri("convertMoney");
								// new MyTask().execute(PRODUCT_URL);
								setdata(PRODUCT_URL);
							}
						}
						
						
					}
					else if(score_length<credits_length){
					
						int score= Integer.parseInt(consume_score);
					if(score%100!=0)
					{
					CustomDialog.Builder builder = new CustomDialog.Builder(CreToBlanceActivity.this);
					builder.setTitle("提示");
						builder.setMessage("请输入100的整数倍");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							
							}
						});
						builder.create().show();
					
					}else{
						
						Log.d("8888888888",score+"");
						key_value.put("consume_score", score +"");
						final String PRODUCT_URL = IpConfig.getUri("convertMoney");
						// new MyTask().execute(PRODUCT_URL);
						setdata(PRODUCT_URL);
					}
				}
			}
			}
		});

/*if(!user_state.equals("00")){
			
			balance_submit.setBackgroundResource(R.drawable.pub_grey_button_style);
			balance_submit.setClickable(false);
		}*/
		
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

				String  status ="false";
				Message message = Message.obtain();

				message.obj = status;

				errcode_handler.sendMessage(message);
			}

			@Override
			public void onResponse(String response, int id) {
				Map<String, String> map = new HashMap<String, String>();
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);

				try {

					if(jsonString.equals("")||jsonString.equals("")||jsonString.equals("null"))
					{
						String  status ="false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);

					}else{
						JSONObject jsonObject = new JSONObject(jsonString);
						String data = jsonObject.getString("data");


						String errcode = jsonObject.getString("errcode");
						if(errcode.equals("0"))
						{
							JSONObject dataObject = new JSONObject(data);

							String score = dataObject.getString("score");
							map.put("errcode",errcode);
							map.put("score",score);
						}else{
							String errmsg = jsonObject.getString("errmsg");
							Log.d("44444", data);

							map.put("errcode",errcode);
							map.put("errmsg",errmsg);

						}
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
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v != null ? v.getWindowToken() : null, 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
	}
	
	public  boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			//获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			return !(event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom);
		}
		return false;
	}
}
