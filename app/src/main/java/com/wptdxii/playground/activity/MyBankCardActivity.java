package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.BankCardListAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.PublicLoadPage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class MyBankCardActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout iv_back;
	private TextView top_title;
	private ImageView iv_right;
	private ListView lv_bank_card;
	private RelativeLayout rl_add_bank_card;
	

	private String user_id;
	private String token;
	private HashMap<String, String> key_value = new HashMap<String, String>();
	private ArrayList<HashMap<String,String>> cardList;
	private BankCardListAdapter adapter;
	private PublicLoadPage mLoadPage;
	public static Boolean RefreshFlag = false;


	private View addcardView;

	private Handler handler = new Handler() {

		@SuppressWarnings("unchecked")

		@Override
		public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case 0:
			mLoadPage.loadSuccess(null, null);
			rl_add_bank_card.setVisibility(View.VISIBLE);



				adapter=new BankCardListAdapter(MyBankCardActivity.this,cardList);

			if(lv_bank_card.getFooterViewsCount()==0){
				lv_bank_card.addFooterView(addcardView);
			}




				lv_bank_card.setAdapter(adapter);


			break;
		case 1:
			mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
			break;
		case 2:
			mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
			break;

		default:
			break;
		}
		}

	};
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_bank_card);

		user_id  = sp.getString("Login_UID", "");
		token    = sp.getString("Login_TOKEN", "");
		initView();
		initData();
	}
	
	



	private void initView() {

		 addcardView = LayoutInflater.from(MyBankCardActivity.this).inflate(R.layout.mybankcard_footview,
				null);
		rl_add_bank_card= (RelativeLayout) addcardView.findViewById(R.id.rl_add_bank_card);


		mLoadPage = new PublicLoadPage((LinearLayout)findViewById(R.id.common_load)) {
			@Override
			public void onReLoadCLick(LinearLayout layout,
									  RelativeLayout rl_progress, ImageView iv_loaded,
									  TextView tv_loaded, Button btLoad) {
				mLoadPage.startLoad();
				getBankcardList(url);
			}
		};
		
		iv_back=(RelativeLayout) findViewById(R.id.iv_back);
		top_title= (TextView) findViewById(R.id.tv_text);
		iv_right=(ImageView) findViewById(R.id.iv_right);
		top_title.setText("我的银行卡");
		iv_right.setVisibility(View.INVISIBLE);
		iv_back.setOnClickListener(this);
		
		lv_bank_card=(ListView) findViewById(R.id.lv_bank_card);
		

		rl_add_bank_card.setOnClickListener(this);
		
		lv_bank_card.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				HashMap<String, String> card_info = cardList.get(position);
				Intent intent = new Intent(MyBankCardActivity.this, MyBankCardInfoActivity.class);
				intent.putExtra("card_info", card_info);
				startActivityForResult(intent, 300);



			}
		});


	}

	private void initData() {
		mLoadPage.startLoad();



		url = IpConfig.getUri2("getmyBankCards");
		key_value.put("userId", user_id);
		key_value.put("token", token);


		getBankcardList(url);
		Log.i("getBankcardList", url + "userId=" + user_id + "&token=" + token);
		Log.i("url-----", url );
	}




	/**获取银行卡列表
	 * @param url
	 */
	private void getBankcardList(String url) {

		cardList=new ArrayList<HashMap<String,String>>();
		OkHttpUtils.post()
		.url(url)
		.params(key_value)//不带参数
		.build()
		.execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);
				try {
					if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();
						message.obj = status;
					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						String errcode = jsonObject.getString("errcode");

						if (errcode.equals("0")) {

							JSONArray dataArray = jsonObject.getJSONArray("data");
							for (int i = 0; i < dataArray.length(); i++) {
								JSONObject obj = (JSONObject) dataArray.get(i);
								HashMap<String, String> dataMap = new HashMap<String, String>();

								dataMap.put("id", obj.getString("id"));
								dataMap.put("bankCardNo", obj.getString("bankCardNo"));


								JSONObject bankbinDtoobj = obj.getJSONObject("bankbinDto");

								dataMap.put("bankColor", bankbinDtoobj.getString("bankColor"));
								dataMap.put("bankName", bankbinDtoobj.getString("bankName"));
								dataMap.put("cardsType", bankbinDtoobj.getString("cardsType"));

								dataMap.put("bankLogoImg", bankbinDtoobj.getString("bankLogoImg"));

								dataMap.put("bankTel", bankbinDtoobj.getString("bankTel"));


								cardList.add(dataMap);
							}
							Message msg = new Message();
							msg.what = 0;
							handler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
				}
			}
		});

	}






	@Override
	public void onClick(View v) {
		Intent intent=null;
		switch(v.getId()){
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_add_bank_card:
			intent=new Intent(MyBankCardActivity.this,BankCardEditActivity.class);


			startActivityForResult(intent, 300);

			break;

		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==300&resultCode==200){
			initData();
		}
	}
}
