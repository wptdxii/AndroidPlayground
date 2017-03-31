package com.cloudhome.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
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
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.cloudhome.R.drawable.completed;

public class Profile_EditActivity extends BaseActivity {

	
    private RelativeLayout profile_back;
	private TextView tv_text;
	private Button profile_submit;

	private EditText profile_edit;
	private String user_id;
	private String token;
	private String id;
	private String personal_context;
	private String setValue;
	private Map<String, String> key_value = new HashMap<String, String>();
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");

			if (errcode.equals("0")) {
				
				Editor editor3 = sp3.edit();
				editor3.putString("personal_context", setValue);
			
				editor3.commit();
				
			finish();
			Toast.makeText(Profile_EditActivity.this, "您的个人简介编辑成功！",
					Toast.LENGTH_LONG).show();
			} else {	
				finish();
				Toast.makeText(Profile_EditActivity.this, "您的个人简介编辑失败！",
					Toast.LENGTH_LONG).show();
			}
		
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile_edit);
		

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		
		
		Intent intent = getIntent();

		id = intent.getStringExtra("id");
	
	
		
		
		init();
		initEvent();

	}
	void init() {
		

		profile_back=(RelativeLayout) findViewById(R.id.rl_back);
		tv_text= (TextView) findViewById(R.id.tv_title);
		tv_text.setText("编辑个人简介");
		
		profile_edit = (EditText)findViewById(R.id.profile_edit);
		
		profile_submit = (Button)findViewById(R.id.btn_action);
		profile_submit.setText("保存");
		
		profile_edit.addTextChangedListener(mTextWatcher);
	}
	void initEvent() {
	personal_context = sp3.getString("personal_context", "");
		
		if (!(personal_context == null) && !personal_context.equals("")
				&& !personal_context.equals("null")) {
			profile_edit.setText(personal_context);
			profile_edit.setSelectAllOnFocus(true);
		}
		
		
		user_id  = sp.getString("Login_UID", "");
		token    = sp.getString("Login_TOKEN", "");
		key_value.put("user_id", user_id);
		key_value.put("token", token);

		profile_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		
	
		profile_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			setValue =profile_edit.getText().toString();
				
				if(setValue==null||setValue.equals("")||setValue.equals("null"))
				{
					setValue="";
					key_value.put("setValue", "");
					final String url = IpConfig.getUri("setPersonalContext");
					setdata(url);
				}else{
				
					key_value.put("setValue", setValue);
					final String url = IpConfig.getUri("setPersonalContext");
					Log.i("简介----",setValue);
					setdata(url);
			}}
		});
	}
	
	
	 TextWatcher mTextWatcher = new TextWatcher() {
	        private CharSequence temp;
	        private int editStart ;
	        private int editEnd ;
	        
	        private int maxLen = 200; 
	        @Override
	        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
	                int arg3) {
	            temp = s;
	            
	   
	           
		     
	        }
	       
	        @Override
	        public void onTextChanged(CharSequence s, int arg1, int arg2,
	                int arg3) {
	          
	        	
	        }
	       
	      
			@Override
			public void afterTextChanged(Editable editable) {
	        	  // Editable editable = profile_edit.getText();
			        int len = editable.length();  
			          
			        if(len >maxLen)  
			        {  
			            int selEndIndex = Selection.getSelectionEnd(editable);  
			            String str = editable.toString();  
			            //截取新字符串  
			            String newStr = str.substring(0,maxLen);  
			            profile_edit.setText(newStr);  
			            editable = profile_edit.getText();  
			              
			            //新字符串的长度  
			            int newLen = editable.length();  
			            //旧光标位置超过字符串长度  
			            if(selEndIndex > newLen)  
			            {  
			                selEndIndex = editable.length();  
			            }  
			            //设置新光标所在的位置  
			            Selection.setSelection(editable, selEndIndex);  
			              
			            
			            
			            Toast.makeText(Profile_EditActivity.this, "输入的字数不能超过200字",
								Toast.LENGTH_SHORT).show();
//			        	CustomDialog.Builder builder = new CustomDialog.Builder(
//			        			Profile_EditActivity.this);
//
//						builder.setTitle("提示");
//						builder.setMessage("输入的字数不能超过200字");
//						
//						builder.setPositiveButton("确定",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										dialog.dismiss();
//
//									}
//								});
//						builder.create().show();
			            
			            
			        }
			}
	  };
	private void setdata(String url) {
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {

				Log.e("error", "获取数据异常 ", e);
			}

			@Override
			public void onResponse(String response, int id) {

				Map<String, String> map = new HashMap<String, String>();
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);

				try {

					// Log.d("44444", jsonString);
					JSONObject jsonObject = new JSONObject(jsonString);
					String data = jsonObject.getString("data");

					String errcode = jsonObject.getString("errcode");


					map.put("errcode", errcode);
					Log.d("44444", errcode);
					Message message = Message.obtain();

					message.obj = map;

					handler.sendMessage(message);

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
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
