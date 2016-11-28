package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.C_P_CompanyAdapter;
import com.cloudhome.adapter.C_P_CompanyAdapter.ViewHolder;
import com.cloudhome.bean.C_P_C_Data;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class C_P_CompanyActivity extends BaseActivity {
	private GridView company_name_gd;
	private C_P_CompanyAdapter mAdapter;
	private ArrayList<C_P_C_Data> persons;
	private Button company_submit;
	private ImageView c_p_company_back;
	private int checkNum = 0;
	private TextView tv_show;
	private Dialog dialog;
	List<String> listItemID = new ArrayList<String>();
	List<String> listItemName = new ArrayList<String>();
	private Handler list_handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

			dialog.dismiss();
			
			C_P_C_Data mPerson;
			for (int i = 0; i < list.size(); i++) {
				mPerson = new C_P_C_Data();
				mPerson.setName(list.get(i).get("company_name"));
				mPerson.setId(list.get(i).get("company_id"));
				persons.add(mPerson);
			}

			mAdapter = new C_P_CompanyAdapter(persons, C_P_CompanyActivity.this);
			company_name_gd.setAdapter(mAdapter);

		}

	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.c_p_company);

		company_name_gd = (GridView) findViewById(R.id.c_name_gd);
		company_submit = (Button) findViewById(R.id.company_submit);
		c_p_company_back = (ImageView) findViewById(R.id.c_p_company_back);
		tv_show = (TextView) findViewById(R.id.tv);

		persons = new ArrayList<C_P_C_Data>();
		
	

	    dialog = new Dialog(this,R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
        
		company_name_gd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {

				Log.d("88888", "7777");
				ViewHolder holder = (ViewHolder) arg1.getTag();
				if (checkNum < 3 || holder.cb.isChecked()) {
					holder.cb.toggle();

					C_P_CompanyAdapter.getIsSelected().put(pos,
							holder.cb.isChecked());

					if (holder.cb.isChecked()) {

						holder.name.setTextColor(getResources().getColor(
								R.color.orange_red));

						holder.name
								.setBackgroundResource(R.drawable.company_checkbox_pressed);

						checkNum++;
					} else {
						holder.name.setTextColor(getResources().getColor(
								R.color.black));
						holder.name
								.setBackgroundResource(R.drawable.company_checkbox_normal);
						checkNum--;
					}

					listItemID.clear();
					listItemName.clear();
					for (int i = 0; i < C_P_CompanyAdapter.getIsSelected().size(); i++) {
						if (C_P_CompanyAdapter.getIsSelected().get(i)) {
							listItemID.add(persons.get(i).getId());
							listItemName.add(persons.get(i).getName());
						}
					}

					if (listItemID.size() == 0) {

						tv_show.setText("请选择承保公司");
					} else {
						StringBuilder sb = new StringBuilder();

						for (int i = 0; i < listItemID.size(); i++) {

							sb.append(listItemName.get(i)).append("，");

						}

						String c_name = sb.substring(0, sb.length() - 1);

						tv_show.setText("您已选择：" + c_name);
					}

				} else {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							C_P_CompanyActivity.this);

					builder.setTitle("提示");
					builder.setMessage("最多选3家承保公司");
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
		});

		company_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String c_name;
				String[] c_id;
				if (listItemID.size() == 0) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							C_P_CompanyActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请选择承保公司");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.dismiss();

								}
							});

					builder.create().show();

				} else {
					StringBuilder sb = new StringBuilder();

					c_id = new String[listItemID.size()];
					for (int i = 0; i < listItemID.size(); i++) {

						sb.append(listItemName.get(i)).append("，");
						c_id[i] = listItemID.get(i);
					}

					c_name = sb.substring(0, sb.length() - 1);

					Intent intent = getIntent();
					intent.putExtra("company_name", c_name);
					intent.putExtra("company_id", c_id);
					setResult(0, intent);

					C_P_CompanyActivity.this.finish();

				}

			}
		});
		c_p_company_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String c_name;
				String[] c_id;

				c_name = "";

				Intent intent = getIntent();
				intent.putExtra("company_name", c_name);
				// intent.putExtra("company_id", c_id);
				setResult(0, intent);

				C_P_CompanyActivity.this.finish();

			}
		});

		dialog.show();
		final String PRODUCT_URL = IpConfig.getUri("getCompanyListForAuto");

		setdata(PRODUCT_URL);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 如果是返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String c_name;
			String[] c_id;

			c_name = "";

			Intent intent = getIntent();
			intent.putExtra("company_name", c_name);
			// intent.putExtra("company_id", c_id);
			setResult(0, intent);

			C_P_CompanyActivity.this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

	private void setdata(String url) {

		
		OkHttpUtils.post()//
		.url(url)
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e) {
			
				Log.e("error", "获取数据异常 ", e);

				dialog.dismiss();
				Toast.makeText(C_P_CompanyActivity.this, "网络连接失败，请确认网络连接后重试",
						Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onResponse(String response) {
			
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				try {

					if (jsonString.equals("") || jsonString.equals("null")) {
						dialog.dismiss();
						Toast.makeText(C_P_CompanyActivity.this, "网络连接失败，请确认网络连接后重试",
								Toast.LENGTH_SHORT).show();
					} else {

						JSONObject jsonObject = new JSONObject(jsonString);
						JSONObject dataObject = jsonObject
								.getJSONObject("data");

						// 迭代输出json的key作为map的key

						Iterator<String> iterator = dataObject.keys();

						while (iterator.hasNext()) {

							Map<String, String> map = new HashMap<String, String>();
							String key = iterator.next();
							String value = dataObject.getString(key);

							Log.d("8888", key);
							Log.d("77777", value);
							map.put("company_id", key);
							map.put("company_name", value);
							list.add(map);
						}

						Message message = Message.obtain();

						message.obj = list;

						list_handler.sendMessage(message);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		

	}



}
