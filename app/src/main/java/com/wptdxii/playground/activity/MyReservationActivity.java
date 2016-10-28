package com.wptdxii.playground.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetSuggestAppointForUserid;
import com.cloudhome.view.customview.PublicLoadPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyReservationActivity extends BaseActivity implements NetResultListener,OnClickListener {
	private RelativeLayout iv_back;
	private TextView top_title;
	private ImageView iv_right;
	private View bottom_line;
	
	private ListView lv_my_reservation;
	private final int GET_SUGGEST_APPOINT= 0;
	private ArrayList<Map<String,String>> list;
	private MyReservationAdapter adapter;

	private String user_id;
	private String token;
	
	private RadioGroup rg_choice;
	
	private PublicLoadPage mLoadPage;
	//记录现在是选中哪一个
	private String currentWithTime="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_reservation);

		user_id  = sp.getString("Login_UID", "");
		token    = sp.getString("Login_TOKEN", "");
		initView();
		initData("01");
	}

	private void initView() {
		mLoadPage = new PublicLoadPage((LinearLayout)findViewById(R.id.common_load)) {
			@Override
			public void onReLoadCLick(LinearLayout layout,
									  RelativeLayout rl_progress, ImageView iv_loaded,
									  TextView tv_loaded, Button btLoad) {
				mLoadPage.startLoad();
				initData(currentWithTime);
			}
		};
		
		iv_back=(RelativeLayout) findViewById(R.id.iv_back);
		top_title= (TextView) findViewById(R.id.tv_text);
		iv_right=(ImageView) findViewById(R.id.iv_right);
		bottom_line= findViewById(R.id.bottom_line);
		top_title.setText("我的预约单");
		iv_right.setVisibility(View.INVISIBLE);
		bottom_line.setVisibility(View.GONE);
		iv_back.setOnClickListener(this);
		lv_my_reservation=(ListView) findViewById(R.id.lv_my_reservation);
		
		rg_choice=(RadioGroup) findViewById(R.id.rg_choice);
		
	    
	    rg_choice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_one_week:
					initData("01");
					break;
				case R.id.rb_one_month:
					initData("02");
					break;
				case R.id.rb_three_month:
					initData("03");
					break;
				default:
					break;
				}
			}
		});
	}

	private void initData(String withtime) {
		currentWithTime=withtime;
		mLoadPage.startLoad();
		list=new ArrayList<Map<String,String>>();
		GetSuggestAppointForUserid suggestAppointRequest=new GetSuggestAppointForUserid(this);
		suggestAppointRequest.execute(user_id,token,withtime,GET_SUGGEST_APPOINT,list);
	}
	
	class MyReservationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHoler holder=null;
			if(convertView==null){
				holder=new ViewHoler();
				convertView= LayoutInflater.from(MyReservationActivity.this).inflate(R.layout.item_my_reservation, null);
				holder.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
				holder.tv_name_value=(TextView) convertView.findViewById(R.id.tv_name_value);
				holder.tv_sex_value=(TextView) convertView.findViewById(R.id.tv_sex_value);
				holder.tv_mobile_value=(TextView) convertView.findViewById(R.id.tv_mobile_value);
				convertView.setTag(holder);
			}else{
				holder=(ViewHoler) convertView.getTag();
			}
			HashMap<String,String> map=(HashMap<String, String>) list.get(position);
			holder.tv_date.setText(map.get("add_time"));
			holder.tv_name_value.setText(map.get("appoint_name"));
			holder.tv_sex_value.setText(map.get("appoint_sex"));
			holder.tv_mobile_value.setText(map.get("mobile"));
			return convertView;
		}
		
		class ViewHoler{
			private TextView tv_date;
			private TextView tv_name_value;
			private TextView tv_sex_value;
			private TextView tv_mobile_value;
		}
		
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
        case GET_SUGGEST_APPOINT:
        	
            if (flag == MyApplication.DATA_OK) {
          	  if(list.size()<=0){
          		mLoadPage.loadFail(MyApplication.NO_DATA, MyApplication.BUTTON_RELOAD, 1);
          	  }else{
          		 mLoadPage.loadSuccess(null, null);
          		adapter=new MyReservationAdapter();
          		lv_my_reservation.setAdapter(adapter);
          	  }
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
