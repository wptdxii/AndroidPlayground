package com.wptdxii.playground.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.event.ModifyUserInfoEvent;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import okhttp3.Call;

public class MyTruenameActivity extends BaseActivity {


	private ClearEditText truename_edit;
	private String truename;
	private String user_id;
	private String token;
	private TextView truename_submit;
	private Map<String, String> key_value = new HashMap<String, String>();
	private ImageView mytruename_back;

	private String user_state;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;
			
			String errcode =data.get("errcode");
			
			if(errcode.equals("0"))
			{
				
							Editor editor1 = sp.edit();
							editor1.putString("truename", truename);
							editor1.commit();
							Toast.makeText(MyTruenameActivity.this, "修改姓名成功", Toast.LENGTH_LONG)
							.show();
							//发送基本信息改变的广播
							EventBus.getDefault().post(new ModifyUserInfoEvent());
							finish();
				
			}else{
				Toast.makeText(MyTruenameActivity.this, "修改姓名失败", Toast.LENGTH_LONG)
				.show();
			}
	
			
		}

	};
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mytruename);
		truename = sp.getString("truename", "");
		user_state = sp.getString("Login_CERT", "");
		init();
		initEvent();

	}

	void init() {
		mytruename_back=(ImageView)findViewById(R.id.mytruename_back);
		truename_edit = (ClearEditText)findViewById(R.id.truename_edit);

		truename_edit.setText(truename);
		
		Editable editable = truename_edit.getText();
		int selEndIndex = Selection.getSelectionEnd(editable);
		selEndIndex = editable.length();
		Selection.setSelection(editable, selEndIndex);
		
		truename_edit.addTextChangedListener(new SearchWather(truename_edit));
		truename_submit= (TextView)findViewById(R.id.truename_submit);
	}

	void initEvent() {
		
		
		
//		if (user_state.equals("00")||user_state.equals("02")) {
//			truename_txt.setVisibility(View.VISIBLE);
//			truename_edit.setVisibility(View.GONE);
//			truename_submit.setVisibility(View.GONE);
//		}
//		else if (user_state.equals("01")||user_state.equals("03")) {
//
//			truename_txt.setVisibility(View.GONE);
//			truename_edit.setVisibility(View.VISIBLE);
//			truename_submit.setVisibility(View.VISIBLE);
//			
//		}
		
		
		mytruename_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		truename_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				truename =truename_edit.getText().toString();
				if(truename==null||truename.equals("")||truename.equals("null"))
				{
					Toast.makeText(MyTruenameActivity.this, "姓名不能为空", Toast.LENGTH_LONG)
					.show();
				}else{
				user_id = sp.getString("Login_UID", "");
				token = sp.getString("Login_TOKEN", "");
				key_value.put("token", token);
				key_value.put("user_id", user_id);
				key_value.put("username", truename);
				final String PRODUCT_URL = IpConfig.getUri("modifyUsername");
				// new MyTask().execute(PRODUCT_URL);
				setdata(PRODUCT_URL);
				}
			}
		});
	
	}
	
	private void setdata(String url) {

		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e) {
			
				Log.e("error", "获取数据异常 ", e);
				
			}

			@Override
			public void onResponse(String response) {
			
				
				Map<String, String> map = new HashMap<String, String>();
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
			
				try {

					// Log.d("44444", jsonString);
					JSONObject jsonObject = new JSONObject(jsonString);
					String data = jsonObject.getString("data");
			
				    
					String errcode = jsonObject.getString("errcode");
					
					Log.d("44444", data);
				
                     map.put("errcode",errcode);
				
					Message message = Message.obtain();

					message.obj = map;

					handler.sendMessage(message);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		
		
		

	}
	
	
	class SearchWather implements TextWatcher {
		   
		   
	      //监听改变的文本框  
	      private EditText editText;
	  
	        
	      public SearchWather(EditText editText){
	          this.editText = editText;  
	      }  
	  
	      @Override
	      public void onTextChanged(CharSequence ss, int start, int before, int count) {
	          String editable = editText.getText().toString();
	          String str = stringFilter(editable);
	          if(!editable.equals(str)){
	              editText.setText(str);
	              //设置新的光标所在位置  
	              editText.setSelection(str.length());
	          }
	      }  
	  
	      @Override
	      public void afterTextChanged(Editable s) {
	  
	      }  
	      @Override
	      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	  
	      }

	  }  
	  
	  public static String stringFilter(String str)throws PatternSyntaxException {
	      // 只允许字母和数字       
	      String regEx  =  "[^\u4E00-\u9FA5]";
	      Pattern p   =   Pattern.compile(regEx);
	      Matcher m   =   p.matcher(str);
	      return   m.replaceAll("").trim();     
	  }
	  

}
