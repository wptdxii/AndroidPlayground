package com.cloudhome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
public class ModifyRelationActivity extends BaseActivity {

	private PopupWindow popupWindow;
	private ListView listViewSpinner;
	private LinearLayout layout;
	
	private TextView relation_edit;
	

	private String user_id;
	private String token;
	
	private String customer_a_id;
	private String customer_b_id;
	private String relation;
	private String old_code;
	
	
	
	private String[] code={"03","04","01","02","05","06"};
	private String[] name={"父亲","母亲","丈夫","妻子","儿子","女儿"};
	
	private int posi;
	private  Button mod_rela_submit;
	private Map<String, String> key_value = new HashMap<String, String>();
	private RelativeLayout mod_rela_back;
	private RelativeLayout rl_right;
	private TextView tv_text;


	
	List<Map<String, String>> Companylist = new ArrayList<Map<String, String>>();
	private RelativeLayout relation_rel;
	
	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
		//	Map<String, String> data = (Map<String, String>) msg.obj;
			 String data = (String) msg.obj;

			String status = data;
			//String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {
				
				
				Toast.makeText(ModifyRelationActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT)
				.show();
			}
		}

	};
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;
			
			String errcode =data.get("errcode");
			
			if(errcode.equals("0"))
			{
				
				
				finish();
			}else{
				String errmsg = data.get("errmsg");
				CustomDialog.Builder builder = new CustomDialog.Builder(
						ModifyRelationActivity.this);

				builder.setTitle("提示");
				builder.setMessage(errmsg);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								            
							}
						});
				builder.create().show();
			
				
			}
		//	m_d_content.setText(content);
			
		}

	};
	

	
    private void showPopupWindow() {  
        layout = (LinearLayout) LayoutInflater.from(ModifyRelationActivity.this).inflate(  
                R.layout.dialog, null);  
        listViewSpinner = (ListView) layout.findViewById(R.id.lv_dialog);  
        listViewSpinner.setAdapter(new ArrayAdapter<String>(ModifyRelationActivity.this,  
                R.layout.text, R.id.tv_text, name));  
  
        popupWindow = new PopupWindow(ModifyRelationActivity.this);  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
        popupWindow  
        .setWidth(getResources().getDimensionPixelSize(  
                R.dimen.relation_p_width));  
        popupWindow.setHeight(getResources().getDimensionPixelSize(  
                R.dimen.relation_p_height));  
          
        popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点  
        popupWindow.setTouchable(true); // 设置PopupWindow可触摸  
        popupWindow.setOutsideTouchable(false); // 设置非PopupWindow区域可触摸  
//        popupWindow.setAnimationStyle(R.anim.popanim);  
          
        popupWindow.setContentView(layout);  
          
//      popupWindow.showAsDropDown(bt_order);  
        popupWindow.showAsDropDown(relation_edit, (getResources().getDimensionPixelSize(  
                R.dimen.bt_width) - getResources().getDimensionPixelSize(  
                R.dimen.mark_popupwindow_width))/2, 0);  
  
        listViewSpinner.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,  
                    long arg3) {  
            	
            	
				
            
				arg0.setVisibility(View.VISIBLE);
				relation_edit.setText(name[pos]);  
				Log.d("code",code[pos]);
				posi=pos;
				key_value.put("relation",code[pos]);
                popupWindow.dismiss();  
                popupWindow = null;  
            }  
        });  
    }  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.modify_relation);


		Intent intent = getIntent();

		
		customer_a_id = intent.getStringExtra("customer_a_id");
		customer_b_id = intent.getStringExtra("customer_b_id");
		
		relation = intent.getStringExtra("relation");
		old_code = intent.getStringExtra("code");
		
		
		
		
		Log.d("4444444",customer_a_id +"/22/" +customer_b_id+"/22/" + relation +"/22/" +old_code);
		key_value.put("customer_a_id", customer_a_id);
		key_value.put("customer_b_id", customer_b_id);
		
		if(old_code.equals("00")){
			
			key_value.put("relation","");
		}else{
			key_value.put("relation",old_code);
		}
		
		
		init();
		initEvent();

	}

	void init() {
		relation_rel =(RelativeLayout)findViewById(R.id.relation_rel);
		mod_rela_back=(RelativeLayout)findViewById(R.id.iv_back);
		rl_right= (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("编辑家庭成员");
		relation_edit = (TextView)findViewById(R.id.relation_edit);
		
		relation_edit.setText(relation);
		
		
		mod_rela_submit= (Button)findViewById(R.id.mod_rela_submit);
	}

	void initEvent() {
		
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("token", token);
		key_value.put("user_id", user_id);
	
	
		relation_rel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				showPopupWindow();
			}
		});
		mod_rela_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mod_rela_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				final String PRODUCT_URL = IpConfig.getUri("modifyRelation");
				
				setdata(PRODUCT_URL);
				
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


						String errcode = jsonObject.getString("errcode");

						if(errcode.equals("0"))
						{
							Log.d("44444", data);

							map.put("errcode",errcode);
						}else{
							String errmsg = jsonObject.getString("errmsg");


							Log.d("44444", data);

							map.put("errcode", errcode);
							map.put("errmsg", errmsg);
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

}
