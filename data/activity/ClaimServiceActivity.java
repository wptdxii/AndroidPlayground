package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;

public class ClaimServiceActivity extends BaseActivity implements
		IXListViewListener {

	private ViewHolder holder = null;
	private MyAdapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();
	private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
	private Dialog dialog;
	private RelativeLayout s_m_back;
	private RelativeLayout rl_right;
	private TextView tv_text;
	private String user_id;
	private String token;
	private int pagenum = 0;
	private Handler mHandler;

	private XListView s_m_xlist;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

			resultdata.addAll(list);
			if (pagenum == 0) {

				adapter.setData(resultdata);
				s_m_xlist.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dialog.dismiss();

			} else {

				adapter.notifyDataSetChanged();
				s_m_xlist.stopLoadMore();

			}

		}

	};

	private Handler null_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errmsg = data.get("errmsg");
			s_m_xlist.stopLoadMore();
			dialog.dismiss();
			Toast.makeText(ClaimServiceActivity.this, errmsg,
					Toast.LENGTH_SHORT).show();

		}

	};

	private Handler errcode_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;

			dialog.dismiss();
			String status = data;
			// String errcode = data;
			Log.d("455454", "455445" + status);
			if (status.equals("false")) {

				Toast.makeText(ClaimServiceActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.claim_service);

		init();
		initEvent();

	}

	private void init() {
		s_m_back = (RelativeLayout) findViewById(R.id.iv_back);
		rl_right= (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("理赔服务");
		s_m_xlist = (XListView) findViewById(R.id.s_m_xlist);
		s_m_xlist.setPullLoadEnable(true);
		s_m_xlist.setXListViewListener(ClaimServiceActivity.this);

		adapter = new MyAdapter(ClaimServiceActivity.this);

		dialog = new Dialog(this, R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView p_dialog = (TextView) dialog
				.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");

		mHandler = new Handler();
	}

	private void initEvent() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("token", token);
		key_value.put("user_id", user_id);
		key_value.put("page", "0");

		dialog.show();
		final String url = IpConfig.getUri("getCompanyListForCommission");
		setlistdata(url);

		s_m_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private void setlistdata(String url) {

		
		
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
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Map<String, String> errcode_map = new HashMap<String, String>();
				try {

					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						// Log.d("44444", jsonString);
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");
						if (!errcode.equals("0")) {
							String errmsg = jsonObject.getString("errmsg");

							errcode_map.put("errcode", errcode);
							errcode_map.put("errmsg", errmsg);

							Message message2 = Message.obtain();

							message2.obj = errcode_map;

							null_handler.sendMessage(message2);

						} else {
							JSONArray dataList = jsonObject
									.getJSONArray("data");

							for (int i = 0; i < dataList.length(); i++) {
								JSONObject jsonObject2 = dataList
										.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									Object value = jsonObject2.get(key);
									map.put(key, value);
								}
								list.add(map);
							}

							Message message = Message.obtain();

							message.obj = list;

							handler.sendMessage(message);
						}
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.claim_service_item, null);

				holder.copmany_name = (TextView) convertView
						.findViewById(R.id.copmany_name);
				holder.claim_phone = (TextView) convertView
						.findViewById(R.id.claim_phone);
				holder.claim_service_rel2 = (RelativeLayout) convertView
						.findViewById(R.id.claim_service_rel2);
				holder.claim_service_rel1 = (RelativeLayout) convertView
						.findViewById(R.id.claim_service_rel1);
				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
				resetViewHolder(holder);
			}

			// setText(list.get(position).get("title").toString());
			holder.copmany_name.setText(list.get(position).get("name")
					.toString());

			final String tel = list.get(position).get("mobile").toString();

			int i = 0;
			String split = "|";
			StringTokenizer token = new StringTokenizer(tel, split);

			String[]  phoneArray = new String[token.countTokens()];

			while (token.hasMoreTokens()) {

				phoneArray[i] = token.nextToken();
				i++;
			}

			if (phoneArray.length >= 1) {
				holder.claim_phone.setText(phoneArray[0]);
				list.get(position).put("mobile",phoneArray[0]);
			}else{
				list.get(position).put("mobile","");
			}
			
			holder.claim_service_rel2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					String phonestr = list.get(position).get("mobile").toString();

					Log.d("77777",phonestr+"123");
					if (phonestr.equals("")) {

					} else {
						requestCallPhonePermission(phonestr);
					}

				}
			});

			holder.claim_service_rel1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					
					
					String phonestr = list.get(position).get("mobile").toString();

					
					Log.d("77777",phonestr+"123");
					Intent intent = new Intent();

					// 设置传递的参数

					intent.putExtra("companycode", list.get(position)
							.get("code").toString());
					intent.putExtra("companyname", list.get(position)
							.get("name").toString());

					if (phonestr.equals("")
							) {
						intent.putExtra("phone", "");
					} else {
						intent.putExtra("phone", phonestr);
					}
					intent.setClass(ClaimServiceActivity.this,
							Claim_CompanyActivity.class);

					// 启动intent的Activity

					ClaimServiceActivity.this.startActivity(intent);

				}
			});
			return convertView;
		}

		private void requestCallPhonePermission(final String phone) {
				String[] permissions = new String[]{android.Manifest.permission.CALL_PHONE};
				ClaimServiceActivity.this.requestPermissions(permissions, new PermissionListener() {
					@Override
					public void onGranted() {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + phone));
						startActivity(intent);
					}

					@Override
					public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
						ClaimServiceActivity.this
								.showRequestPermissionRationale(
										getString(R.string.msg_callphone_denied));
					}

					@Override
					public void onPermanentDenied(String[] permanentDeniedPermissions) {
						ClaimServiceActivity.this
								.showPermissionSettingDialog(
										getString(R.string.msg_callphone_permanent_denied));
					}
				});
		}

	}

	private void resetViewHolder(ViewHolder p_ViewHolder) {

		p_ViewHolder.claim_phone.setText("");

	}
	
	
	class ViewHolder {
		TextView copmany_name;
		TextView claim_phone;
		RelativeLayout claim_service_rel2;
		RelativeLayout claim_service_rel1;
	}



	private void getRefreshItem() {

		pagenum = 0;
		resultdata.clear();
		key_value.put("page", "0");

		final String url = IpConfig.getUri("getCompanyListForCommission");

		setlistdata(url);

	}

	private void getLoadMoreItem() {
		pagenum++;
		Log.d("555555", pagenum + "");
		key_value.put("page", pagenum + "");
		final String url = IpConfig.getUri("getCompanyListForCommission");

		setlistdata(url);
	}

	@Override
	public void onRefresh() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				s_m_xlist.stopLoadMore();
				getRefreshItem();

				adapter.notifyDataSetChanged();

				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {

		mHandler.postDelayed(new Runnable() {
			public void run() {

				s_m_xlist.stopRefresh();
				getLoadMoreItem();

				// adapter.notifyDataSetChanged();

				// mListView.stopLoadMore();

			}
		}, 2000);

	}

	private void onLoad() {

		s_m_xlist.stopRefresh();

		s_m_xlist.stopLoadMore();

		s_m_xlist.setRefreshTime("刚刚");

	}

}
