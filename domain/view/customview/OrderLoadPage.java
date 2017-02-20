package com.cloudhome.view.customview;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

public abstract class OrderLoadPage implements OnClickListener {


	private LinearLayout rl_progress;
	private ImageView iv_loaded;
	private RelativeLayout layout;
	private TextView tv_loaded;
	private Button btLoad;

	private RelativeLayout rl_null_data;
	private TextView tv_msg;
	private Button bt_toshopmall;

	private int[] tips = { R.drawable.no_network,R.drawable.order_null_data};

	public OrderLoadPage(RelativeLayout loadLayout) {
		layout = loadLayout;

		rl_progress=findviewById(R.id.rl_progress);
		iv_loaded = findviewById(R.id.iv_loaded);
		tv_loaded = findviewById(R.id.tv_loaded);
		btLoad = findviewById(R.id.btLoadPageReTry);

		rl_null_data = findviewById(R.id.rl_null_data);
		tv_msg = findviewById(R.id.tv_msg);
		bt_toshopmall = findviewById(R.id.bt_toshopmall);




		btLoad.setOnClickListener(this);
		bt_toshopmall.setOnClickListener(this);


	}

	@SuppressWarnings("unchecked")
	private <T extends View> T findviewById(int id) {
		return (T) layout.findViewById(id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.btLoadPageReTry:

				onReLoadCLick(layout, rl_progress, iv_loaded, tv_loaded, btLoad);

				break;

			case R.id.bt_toshopmall:
				onToShopMallCLick();

				break;

		}



	}

	public abstract void onReLoadCLick(RelativeLayout layout, LinearLayout rl_progress,
			ImageView iv_loaded, TextView tv_loaded, Button btLoad);

	public abstract void onToShopMallCLick();


	public void startLoad() {
		layout.setVisibility(View.VISIBLE);
		rl_progress.setVisibility(View.VISIBLE);



		rl_null_data.setVisibility(View.GONE);
		tv_loaded.setVisibility(View.GONE);
		btLoad.setVisibility(View.GONE);
	}

	
	public void loadSuccess(String msg1, String msg2,int type) {

		switch (type) {

			case 0:

				setText(msg1, msg2);
				layout.setVisibility(View.GONE);

				break;
			case 1:

				layout.setVisibility(View.VISIBLE);
				rl_null_data.setVisibility(View.VISIBLE);
				iv_loaded.setImageResource(tips[1]);
				iv_loaded.setVisibility(View.VISIBLE);
				tv_msg.setVisibility(View.VISIBLE);
				bt_toshopmall.setVisibility(View.VISIBLE);
				rl_progress.setVisibility(View.GONE);
				setText(msg1, msg2);


				break;

		}


	}

	
	public void loadFail(String msg1, String msg2, int failType) {
		setText(msg1, msg2);
		rl_progress.setVisibility(View.GONE);
		iv_loaded.setVisibility(View.VISIBLE);
		tv_loaded.setVisibility(View.VISIBLE);
		btLoad.setVisibility(View.GONE);
//		iv_loaded.setBackgroundResource(tips[failType]);
		iv_loaded.setImageResource(tips[failType]);
		rl_null_data.setVisibility(View.VISIBLE);

		if(failType==0)
		{
			tv_msg.setVisibility(View.INVISIBLE);
			bt_toshopmall.setVisibility(View.INVISIBLE);
		}else{
			tv_msg.setVisibility(View.VISIBLE);
			bt_toshopmall.setVisibility(View.VISIBLE);
		}
	}


	public void setText(String msg1, String msg2) {
		if (msg1 != null) {
			tv_loaded.setText(msg1);
		}
		if (msg2 != null) {
			btLoad.setText(msg2);
		}
	}

}
