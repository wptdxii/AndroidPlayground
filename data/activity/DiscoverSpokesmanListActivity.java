package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
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

import okhttp3.Call;
public class DiscoverSpokesmanListActivity extends BaseActivity implements
		IXListViewListener {

	private MyAdapter adapter;
	private Map<String, String> key_value = new HashMap<String, String>();
	private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
	private Dialog dialog;
	private ImageView s_m_back;
	private String user_id;
	private String token;
	private int pagenum = 0;
	private Handler mHandler;

	private XListView s_m_xlist;
	private String shareLogo="";

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
				if (list.size() < 1) {
					pagenum--;
				}
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
			dialog.dismiss();
			Toast.makeText(DiscoverSpokesmanListActivity.this, errmsg,
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

				Toast.makeText(DiscoverSpokesmanListActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.discover_spokesman_list);


		init();
		initEvent();

	}

	void init() {
		s_m_back = (ImageView) findViewById(R.id.s_m_back);
		s_m_xlist = (XListView) findViewById(R.id.s_m_xlist);
		s_m_xlist.setPullLoadEnable(true);
		s_m_xlist.setXListViewListener(DiscoverSpokesmanListActivity.this);

		adapter = new MyAdapter(DiscoverSpokesmanListActivity.this);

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

	void initEvent() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		key_value.put("token", token);
		key_value.put("user_id", user_id);
		key_value.put("page", "0");

		final String url = IpConfig.getUri("getFoundList_expert_list");
		setlistdata(url);

		s_m_xlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				int pos = position - 1;

				if (pos >= 0 && pos < adapter.list.size()) {
					shareLogo= adapter.list.get(pos).get("logo").toString();
					Intent intent = new Intent();

					intent.putExtra("title", adapter.list.get(pos).get("title")
							.toString());
					intent.putExtra("img", shareLogo);
					
					intent.putExtra("share_title",
							adapter.list.get(pos).get("share_title").toString());
					intent.putExtra("url", adapter.list.get(pos).get("url")
							.toString());
					intent.putExtra("brief", adapter.list.get(pos).get("brief")
							.toString());
					
				
						intent.putExtra("is_share", "1");
					
					intent.setClass(DiscoverSpokesmanListActivity.this,
							ShareWebActivity.class);

					DiscoverSpokesmanListActivity.this.startActivity(intent);

				}
			}
		});

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
				String status = "false";
				Message message = Message.obtain();

				message.obj = status;

				errcode_handler.sendMessage(message);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Map<String, String> errcode_map = new HashMap<String, String>();
				try {

					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
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
								Map<String, String> map = new HashMap<String, String>();
								// 迭代输出json的key作为map的key

								Iterator<String> iterator = jsonObject2
										.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									String value = jsonObject2
											.getString(key);
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
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.discover_activelist_item, parent, false);

				viewHolder = new ViewHolder();

				// 如何加载图片
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.q_image);
				convertView.setTag(viewHolder);

			} else {

				viewHolder = (ViewHolder) convertView.getTag();
				// viewHolder = (ViewHolder) convertView.getTag();
				resetViewHolder(viewHolder);
			}

			String site_url = IpConfig.getIp3();
			String imgstr = list.get(position).get("img").toString();
			
			String img_url = site_url + imgstr;

			Log.d("img_url", img_url);


			if (imgstr != null && imgstr.length() > 7) {


				Glide.with(DiscoverSpokesmanListActivity.this)
						.load(img_url)
						.placeholder(R.drawable.white)  //占位图 图片正在加载
						.into( viewHolder.imageView);
			}

			return convertView;
		}

	}

	protected void resetViewHolder(ViewHolder p_ViewHolder) {

		p_ViewHolder.imageView.setImageResource(R.drawable.white_bg2);

	}

	class ViewHolder {
		public ImageView imageView;
	}



	private void getRefreshItem() {

		pagenum = 0;
		resultdata.clear();
		key_value.put("page", "0");

		final String url = IpConfig.getUri("getFoundList_expert_list");

		setlistdata(url);

	}

	private void getLoadMoreItem() {
		pagenum++;
		Log.d("555555", pagenum + "");
		key_value.put("page", pagenum + "");
		final String url = IpConfig.getUri("getFoundList_expert_list");

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
