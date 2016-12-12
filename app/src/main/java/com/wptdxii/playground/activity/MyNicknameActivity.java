package com.wptdxii.playground.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MyNicknameActivity extends BaseActivity {


	private ClearEditText nickname_edit;
	private String nickname;
	private String user_id;
	private String token;
	private Button nickname_submit;
	private Map<String, String> key_value = new HashMap<String, String>();
	private RelativeLayout mynickname_back;
	private TextView title;

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
				editor1.putString("nickname", nickname);
				editor1.commit();
				Toast.makeText(MyNicknameActivity.this, "修改昵称成功", Toast.LENGTH_LONG)
				.show();
				finish();
			}else{
				Toast.makeText(MyNicknameActivity.this, "修改昵称失败", Toast.LENGTH_LONG)
				.show();
			}
		}

	};
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.mynickname);
		nickname = sp.getString("nickname", "none");
		init();
		initEvent();

	}

	void init() {
		mynickname_back=(RelativeLayout)findViewById(R.id.iv_back);
		title= (TextView) findViewById(R.id.tv_text);
		title.setText("编辑昵称");
		nickname_edit = (ClearEditText)findViewById(R.id.nickname_edit);
		nickname_edit.setText(nickname);
		
		Editable editable = nickname_edit.getText();
		int selEndIndex = Selection.getSelectionEnd(editable);
		selEndIndex = editable.length();
		Selection.setSelection(editable, selEndIndex);
		
		nickname_submit= (Button)findViewById(R.id.btn_right);
		nickname_submit.setText("保存");
	}

	void initEvent() {
		mynickname_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		nickname_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				nickname =nickname_edit.getText().toString();
				if(nickname==null||nickname.equals("")||nickname.equals("null"))
				{
					Toast.makeText(MyNicknameActivity.this, "昵称不能为空", Toast.LENGTH_LONG)
					.show();
				}else{
				user_id = sp.getString("Login_UID", "");
				token = sp.getString("Login_TOKEN", "");
				key_value.put("token", token);
				key_value.put("user_id", user_id);
				key_value.put("nickname", nickname);
				final String PRODUCT_URL = IpConfig.getUri("modifyNickname");
				// new MyTask().execute(PRODUCT_URL);
					Log.i(TAG, "onClick: "+token);
					Log.i(TAG, "onClick: "+user_id);
					Log.i(TAG, "onClick: "+nickname);
					Log.i(TAG, "onClick: "+PRODUCT_URL);
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

}
