package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.bean.ClauseBean;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ProductClauseActivity extends BaseActivity {
	private ImageView hot_item_back;
	private ListView lv_product_clause;
	private String clause;
	private Dialog dialog;
	private ArrayList clauseBeans=new ArrayList();
	private ClauseAdapter adapter;

	@SuppressLint("HandlerLeak")
	private Handler errcode_handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			dialog.dismiss();
				Toast.makeText(ProductClauseActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
		}

	};
	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");

			dialog.dismiss();
			Toast.makeText(ProductClauseActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();
			if(clauseBeans.size()>0){
				adapter=new ClauseAdapter();
				lv_product_clause.setAdapter(adapter);
			}
		}
	};
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_product_clause);

		Intent intent=getIntent();
		clause=intent.getStringExtra("clause");
		clause=IpConfig.getIp3()+"/"+clause;



		Log.d("clause--------",clause);
		init();
		initEvent();
		
	}
	
	
	void init(){
		
		
	
		
		  dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("请稍后...");
          
		hot_item_back=(ImageView)findViewById(R.id.hot_item_back);
		lv_product_clause=(ListView)findViewById(R.id.lv_product_clause);
		hot_item_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});
		lv_product_clause.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				ClauseBean bean=(ClauseBean) clauseBeans.get(arg2);
				Intent intent=new Intent();
				intent.setClass(ProductClauseActivity.this, ClauseDetailActivity.class);
				intent.putExtra("clause_id",bean.getId());
				intent.putExtra("url", bean.getUrl());
				startActivity(intent);
			}
		});
		
	}
	
	void initEvent(){
		dialog.show();
		setdata(clause);
		
	}
	
	private void setdata(String url) {
		dialog.show();

		
		OkHttpUtils.post()//
		.url(url)
				.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e) {
			
				Log.e("error", "获取数据异常 ", e);
				Toast.makeText(ProductClauseActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}

			@Override
			public void onResponse(String response) {
			
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				Map<String, String> errcode_map = new HashMap<String, String>();

				try {

					if (jsonString.equals("") || jsonString.equals("null")) {

						errcode_handler.sendEmptyMessage(0);

					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						if (!errcode.equals("0")) {
							String errmsg = jsonObject.getString("errmsg");
							errcode_map.put("errmsg", errmsg);

							Message message2 = Message.obtain();

							message2.obj = errcode_map;

							null_handler.sendMessage(message2);
						} else {
							JSONArray dataArray = jsonObject.getJSONArray("data");
							for(int i=0;i<dataArray.length();i++){
								JSONObject obj=dataArray.getJSONObject(i);
								ClauseBean bean=new ClauseBean();
								bean.setId(obj.getString("id"));
								bean.setName(obj.getString("name"));
								bean.setUrl(obj.getString("url"));
								clauseBeans.add(bean);
							}
							handler.sendEmptyMessage(0);

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		});
		
		
		
		
		}
	
	
	public class ClauseAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return clauseBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return clauseBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = View.inflate(ProductClauseActivity.this, R.layout.item_product_clause, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_clause_title =(TextView) convertView.findViewById(R.id.tv_clause_title);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			// viewHolder = (ViewHolder) convertView.getTag();
		}
		ClauseBean bean=(ClauseBean) clauseBeans.get(position);
		viewHolder.tv_clause_title.setText(bean.getName());

		return convertView;
	}
	class ViewHolder{
		public TextView tv_clause_title;
	}
	}



	}
	
	



