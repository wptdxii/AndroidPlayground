package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.IncomeExpendItemAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetPaymentDetailForWallet;
import com.cloudhome.view.customview.PublicLoadPage;

import java.util.ArrayList;
public class IncomeExpendItemDetailActivity extends BaseActivity implements OnClickListener,NetResultListener{
	private RelativeLayout iv_back;
	private TextView top_title;
	private ImageView iv_right;
	private TextView tv_left;
	private TextView tv_right;
	
	private ListView lv_item_detail;
	private String payment_id;
	private String title;
	private String money;
	private String category;
	

	private String user_id;
	private String token;
	
	 private final int GET_INCOME_EXPEND_ITEM_DETAIL = 0;
	 private ArrayList<ArrayList<String>> dataMap=new ArrayList<ArrayList<String>>();


	 private IncomeExpendItemAdapter adapter;
	 private PublicLoadPage mLoadPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_income_expend_item);

		user_id  = sp.getString("Login_UID", "");
		token    = sp.getString("Login_TOKEN", "");
		
		Intent intent=getIntent();
		payment_id=intent.getStringExtra("payment_id");
		title=intent.getStringExtra("title");
		money=intent.getStringExtra("money");
		category=intent.getStringExtra("category");
		initView();
		initData();
	}

	private void initView() {
		mLoadPage = new PublicLoadPage((LinearLayout)findViewById(R.id.common_load)) {
			@Override
			public void onReLoadCLick(LinearLayout layout,
					RelativeLayout rl_progress, ImageView iv_loaded,
					TextView tv_loaded, Button btLoad) {
				dataMap.clear();
				initData();
			}
		};		
		
		iv_back=(RelativeLayout) findViewById(R.id.iv_back);
		top_title=(TextView) findViewById(R.id.tv_text);
		iv_right=(ImageView) findViewById(R.id.iv_right);
		tv_left=(TextView) findViewById(R.id.tv_left);
		tv_right=(TextView) findViewById(R.id.tv_right);
		tv_left.setText(title);
		if("收入".equals(category)){

			tv_right.setText("+"+money);

		}else if("支出".equals(category)){

			tv_right.setText("-"+money);

		}
		
		top_title.setText("费用详情");
		lv_item_detail= (ListView) findViewById(R.id.lv_item_detail);
		iv_back.setOnClickListener(this);
		iv_right.setVisibility(View.INVISIBLE);
	}

	private void initData() {
		mLoadPage.startLoad();
		GetPaymentDetailForWallet getPaymentDetailRequest=new GetPaymentDetailForWallet(this);
		getPaymentDetailRequest.execute(user_id,token,payment_id,GET_INCOME_EXPEND_ITEM_DETAIL,dataMap);
	}

	@Override
	public void onClick(View v) {
     switch(v.getId()){
     case R.id.iv_back:
    	 finish();
    	 break;
     }		
	}

	@Override
	public void ReceiveData(int action, int flag, Object dataObj) {
		  switch (action) 
		  {
        case GET_INCOME_EXPEND_ITEM_DETAIL:
            if (flag == MyApplication.DATA_OK) {
            	mLoadPage.loadSuccess(null, null);
          	  adapter=new IncomeExpendItemAdapter(IncomeExpendItemDetailActivity.this,dataMap);
          	lv_item_detail.setAdapter(adapter);
            } else if (flag == MyApplication.NET_ERROR) {
            	mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
            }else if(flag == MyApplication.DATA_EMPTY){
            	mLoadPage.loadFail(MyApplication.NO_DATA, MyApplication.BUTTON_RELOAD, 1);
            }else if(flag == MyApplication.JSON_ERROR){
            	mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
            }else if(flag == MyApplication.DATA_ERROR){
            	mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
            }
            break;
       
    }		
		
	}

	

}
