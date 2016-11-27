package com.wptdxii.playground.view.customview;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

public abstract class PublicLoadPage implements OnClickListener {


	private RelativeLayout rl_progress;
	private ImageView iv_loaded;
	private LinearLayout layout;
	private TextView tv_loaded;
	private Button btLoad;
	private RelativeLayout rl_null_data;

	private int[] tips = { R.drawable.no_network,R.drawable.order_null_data};

	public PublicLoadPage(LinearLayout loadLayout) {
		layout = loadLayout;

		rl_progress=findviewById(R.id.rl_progress);
		iv_loaded = findviewById(R.id.iv_loaded);
		tv_loaded = findviewById(R.id.tv_loaded);
		btLoad = findviewById(R.id.btLoadPageReTry);
		btLoad.setOnClickListener(this);
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T findviewById(int id) {
		return (T) layout.findViewById(id);
	}

	@Override
	public void onClick(View v) {
		onReLoadCLick(layout, rl_progress, iv_loaded, tv_loaded, btLoad);

	}

	public abstract void onReLoadCLick(LinearLayout layout, RelativeLayout rl_progress,
									   ImageView iv_loaded, TextView tv_loaded, Button btLoad);

	public void startLoad() {
		layout.setVisibility(View.VISIBLE);
		rl_progress.setVisibility(View.VISIBLE);
		iv_loaded.setVisibility(View.GONE);
		tv_loaded.setVisibility(View.GONE);
		btLoad.setVisibility(View.GONE);
	}

	
	public void loadSuccess(String msg1, String msg2) {
		setText(msg1, msg2);
		layout.setVisibility(View.GONE);
	}

	
	public void loadFail(String msg1, String msg2, int failType) {
		setText(msg1, msg2);
		rl_progress.setVisibility(View.GONE);
		iv_loaded.setVisibility(View.VISIBLE);
		tv_loaded.setVisibility(View.VISIBLE);
		btLoad.setVisibility(View.GONE);
//		iv_loaded.setBackgroundResource(tips[failType]);
		iv_loaded.setImageResource(tips[failType]);
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
