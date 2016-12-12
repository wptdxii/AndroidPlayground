package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class SearchProductActivity extends BaseActivity{

	
	private RelativeLayout main_rel;
	private ListView listview;
	private ClearEditText search_edit;
	private MyAdapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();
	
	private TextView search_quit;
	private String user_state;
	private String loginString;

	private Dialog dialog;
	

	
	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
		//	Map<String, String> data = (Map<String, String>) msg.obj;
			 String data = (String) msg.obj;
			 dialog.dismiss();
			String status = data;
			//String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {


				Toast.makeText(SearchProductActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT)
				.show();
			}
		}

	};

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
		//	Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;
			
		
		
				adapter.setData(list);
				listview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dialog.dismiss();

				if(list.size()<1){
					
					Log.d("4545455","88888888888");
					CustomDialog.Builder builder = new CustomDialog.Builder(
							SearchProductActivity.this);

					builder.setTitle("提示");
					builder.setMessage("没有找到佣金信息");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				}
		}

	};
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_product);
        
        listview =(ListView)findViewById(R.id.listview);

        loginString = sp.getString("Login_STATE", "none");
        user_state = sp.getString("Login_CERT", "none");
        search_edit =(ClearEditText)findViewById(R.id.search_edit);
       
        search_quit =(TextView)findViewById(R.id.search_quit);
        
        
        dialog = new Dialog(this,R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
        
      
        initEvent();
        sosuoViews() ;
    }
    
	void initEvent() {
		
	search_quit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	
	adapter = new MyAdapter(SearchProductActivity.this);
	
	listview.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
								long arg3) {
			if (loginString.equals("none")) {
				Intent intent = new Intent();
				intent.setClass(SearchProductActivity.this,
						LoginActivity.class);
				SearchProductActivity.this.startActivity(intent);
			} else if(user_state.equals("01")||user_state.equals("02"))
			{
				
				CustomDialog.Builder builder = new CustomDialog.Builder(
						SearchProductActivity.this);

				builder.setTitle("提示");
				builder.setMessage("您还是未认证保险专家，请至保客云集或在此完成认证！");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				builder.create().show();
			}else {
				Intent intent = new Intent();
				intent.setClass(SearchProductActivity.this,
						Product_C_InfoActivity.class);

				intent.putExtra("cid", adapter.list.get(pos).get("id")
						.toString());
				intent.putExtra("type", adapter.list.get(pos).get("type")
						.toString());
				SearchProductActivity.this.startActivity(intent);
			}
		}
	});
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		loginString = sp.getString("Login_STATE", "none");
		user_state = sp.getString("Login_CERT", "none");
		
	}
	
	private void sosuoViews() {

		
	
		search_edit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int keyCode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				String pro_sosuo = search_edit.getText().toString();
				if (keyCode  == EditorInfo.IME_ACTION_SEARCH) {// 修改回车键功能
					// 先隐藏键盘
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(SearchProductActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					if(pro_sosuo==null||pro_sosuo.equals("")||pro_sosuo.equals("null"))
					{
						

						CustomDialog.Builder builder = new CustomDialog.Builder(
								SearchProductActivity.this);

						builder.setTitle("提示");
						builder.setMessage("您还没有输入搜索的内容");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

									}
								});
						builder.create().show();
					}else{
					key_value.put("key_word", pro_sosuo);
					dialog.show();
					String url  = IpConfig.getUri("searchCommission");
							
					setdata(url);
					}

				}
				
				return false;
			}
		});
		
//		search_edit.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				// TODO 自动生成的方法存根
//				String pro_sosuo = search_edit.getText().toString();
//				if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能
//					// 先隐藏键盘
//					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
//							.hideSoftInputFromWindow(SearchProductActivity.this
//									.getCurrentFocus().getWindowToken(),
//									InputMethodManager.HIDE_NOT_ALWAYS);
//
//					if(pro_sosuo==null||pro_sosuo.equals("")||pro_sosuo.equals("null"))
//					{
//						
//
//						CustomDialog.Builder builder = new CustomDialog.Builder(
//								SearchProductActivity.this);
//
//						builder.setTitle("提示");
//						builder.setMessage("您还没有输入搜索的内容");
//						builder.setPositiveButton("确定",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										dialog.dismiss();
//
//									}
//								});
//						builder.create().show();
//					}else{
//					key_value.put("key_word", pro_sosuo);
//					dialog.show();
//					String url  = IpConfig.getUri("searchCommission");
//							
//					setdata(url);
//					}
//
//				}
//				return false;
//
//			}
//		});

	}
	
	
	private void setdata(String url){
		
		
		
		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);

				String status ="false";
				Message message = Message.obtain();

				message.obj = status;

				errcode_handler.sendMessage(message);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {


					Log.d("44444", jsonString);
					if(jsonString==null||jsonString.equals("")||jsonString.equals("null"))
					{
						String status ="false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					}
					else{



						JSONObject jsonObject = new JSONObject(jsonString);
						JSONArray dataList = jsonObject.getJSONArray("data");

						for (int i = 0; i < dataList.length(); i++) {
							JSONObject jsonObject2 = dataList.getJSONObject(i);
							Map<String, Object> map = new HashMap<String, Object>();
							// 迭代输出json的key作为map的key

							Iterator<String> iterator = jsonObject2.keys();
							while (iterator.hasNext()) {
								String key = iterator.next();
								Object value = jsonObject2.get(key);
								map.put(key, value);
							}
							list.add(map);
						}
						Message message = Message.obtain();
						Log.d("44545545","99999999");
						message.obj = list;

						handler.sendMessage(message);


					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
	}
	
	public class MyAdapter extends BaseAdapter {

		Context context = null;
		private LayoutInflater layoutInflater;
		private List<Map<String, Object>> list = null;

		public MyAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		public void setData(List<Map<String, Object>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO 自动生成的方法存根
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO 自动生成的方法存根
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO 自动生成的方法存根
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.search_product_item, null);
			} else {
				view = convertView;
			}

			
			 TextView product_name = (TextView) view.findViewById(R.id.product_name);

		
		     
			 product_name.setText(list.get(position).get("insurance_name").toString());
			

			return view;
		
		}

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
