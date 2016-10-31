package com.wptdxii.playground.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.AdministerActivity;
import com.cloudhome.activity.CustomerInfoActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.activity.MainSearchActivity;
import com.cloudhome.activity.MoreExpertActivity;
import com.cloudhome.activity.RegisterActivity;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.MyClientBean;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetCustomer;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.Common;
import com.cloudhome.view.customview.RoundImageView;
import com.cloudhome.view.customview.SearchListView;
import com.cloudhome.view.xlistview.XListView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;


public class Micro_CommunityFragment extends BaseFragment implements NetResultListener,View.OnClickListener, XListView.IXListViewListener {

	SharedPreferences sp;
	private String loginString;
	private String token;
	private String user_id;
	private View mView;
	private View zhan_ye_list, my_login_include;
	private TextView tab_login_but;
	private TextView tab_reg_but;
	//右侧搜索字母
	private SearchListView letterListView;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private HashMap<String, Integer> alphaIndexerClient;
	private String[] sectionsClient;
	private HashMap<String, Integer> alphaIndexerUnder;
	private String[] sectionsUnder;
	private WindowManager windowManager;
	private TextView overlay;
	private Handler handler;
	private OverlayThread overlayThread;
	private ArrayList<MyClientBean> myClientList;
	private static final int GET_CUSTOMER=1;
	//记录我的客户显示还是隐藏
	private boolean isClientShow=false;
	private RelativeLayout rl_expert;
	private RelativeLayout rl_administer;
	private RelativeLayout rl_my_client;
	private XListView lv_my_client;
	//统计接口
	public Statistics statistics;
	private String RBstr = "0";
	public static String expert_jump = "false";

	private RelativeLayout rl_search;
	//arrow
	private ImageView expert_arrow;
	private String Event_MyClient = "Micro_CommunityFragment_MyClient";
	private String Event_Expert = "Micro_CommunityyFragment_Expert";
	private RelativeLayout rl_refresh;//右侧刷新按钮
	private ImageView iv_refresh;
	private Animation animation;
	private boolean isRefresh=false;

	private Handler refreshHandler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){
				Toast.makeText(getActivity(),"刷新成功", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity(),"刷新失败", Toast.LENGTH_SHORT).show();
			}
			rl_refresh.setClickable(true);
			iv_refresh.clearAnimation();
			isRefresh=false;
		}
	};


	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("Micro_CommunityFragment","onAttach");
	}



	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
			sp = getActivity().getSharedPreferences("userInfo", 0);
			loginString = sp.getString("Login_STATE", "none");
			user_id = sp.getString("Login_UID", "");
			token = sp.getString("Login_TOKEN", "");
			initView(inflater, container);
			EventBus.getDefault().register(this);
			return mView;
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(LoginEvent event) {
		loginString = sp.getString("Login_STATE", "none");
		user_id = sp.getString("Login_UID", "");
		if (loginString.equals("none")) {
			my_login_include.setVisibility(View.VISIBLE);
			zhan_ye_list.setVisibility(View.GONE);
			initLoginEvent();
		} else {
			Log.i("接收到receiver","000");
			zhan_ye_list.setVisibility(View.VISIBLE);
			my_login_include.setVisibility(View.GONE);
			loginString = sp.getString("Login_STATE", "none");
			user_id = sp.getString("Login_UID", "");
			token = sp.getString("Login_TOKEN", "");
			initEvent();
		}
	}

	private void initView(LayoutInflater inflater, ViewGroup container) {
		mView = inflater.inflate(R.layout.fragment_task_lottery, container, false);
		zhan_ye_list = mView.findViewById(R.id.zhan_ye_list);
		my_login_include = mView.findViewById(R.id.my_login_include);
		tab_login_but = (TextView) mView.findViewById(R.id.tab_login_but);
		tab_reg_but = (TextView) mView.findViewById(R.id.tab_reg_but);

		rl_search=(RelativeLayout) mView.findViewById(R.id.rl_search);
		rl_expert=(RelativeLayout) mView.findViewById(R.id.rl_expert);
		rl_my_client= (RelativeLayout) mView.findViewById(R.id.rl_my_client);
		rl_administer = (RelativeLayout) mView.findViewById(R.id.rl_administer);
		lv_my_client= (XListView) mView.findViewById(R.id.lv_my_client);
		expert_arrow= (ImageView) mView.findViewById(R.id.expert_arrow);
		rl_refresh=(RelativeLayout) mView.findViewById(R.id.rl_refresh);
		iv_refresh= (ImageView) mView.findViewById(R.id.iv_refresh);
		animation= AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_myclient_myunder);
		rl_refresh.setOnClickListener(this);
		rl_search.setOnClickListener(this);
		rl_expert.setOnClickListener(this);
		rl_my_client.setOnClickListener(this);
		rl_administer.setOnClickListener(this);
		lv_my_client.setPullRefreshEnable(true);
		lv_my_client.setXListViewListener(this);
		letterListView = (SearchListView) mView.findViewById(R.id.search_lv);
		if (loginString.equals("none")) {
			my_login_include.setVisibility(View.VISIBLE);
			zhan_ye_list.setVisibility(View.GONE);
			initLoginEvent();
		} else {
			zhan_ye_list.setVisibility(View.VISIBLE);
			my_login_include.setVisibility(View.GONE);
			initEvent();
		}

	}

	private void initEvent() {
		GetCustomer getCustomer=new GetCustomer(this);
		myClientList=new ArrayList<MyClientBean>();
		getCustomer.execute(user_id, GET_CUSTOMER, myClientList,token);
		lv_my_client.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent();
				// 设置传递的参数
				intent.putExtra("customer_id", myClientList.get(i).getClientId());
				intent.setClass(getActivity(),CustomerInfoActivity.class);
				startActivity(intent);
				MobclickAgent.onEvent(getActivity(), Event_MyClient);
			}
		});


	}


	private void initRightSearch() {
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
	}

	void initLoginEvent() {
		tab_login_but.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
			}
		});
		tab_reg_but.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), RegisterActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}


	private final String mPageName = "Micro_CommunityFragment";

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName); // 统计页面
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}

	@Override
	public void ReceiveData(int action, int flag, Object dataObj) {
		switch (action) {
			case GET_CUSTOMER:
				if (flag == MyApplication.DATA_OK) {
					initAlphaIndexer(myClientList,1);
					alphaIndexer=alphaIndexerClient;
					sections=sectionsClient;
					initRightSearch();
					MyAdapter adapterClient=new MyAdapter(myClientList,1);
					lv_my_client.setAdapter(adapterClient);
					if(isRefresh){
						refreshHandler.sendEmptyMessageDelayed(0,2000);
					}
				} else if (flag == MyApplication.NET_ERROR) {

					if(isRefresh){
						refreshHandler.sendEmptyMessageDelayed(0,2000);
					}
				} else if (flag == MyApplication.DATA_EMPTY) {
					if(isRefresh){
						refreshHandler.sendEmptyMessageDelayed(0,2000);
					}
				} else if (flag == MyApplication.JSON_ERROR) {
					if(isRefresh){
						refreshHandler.sendEmptyMessageDelayed(0,2000);
					}
				} else if (flag == MyApplication.DATA_ERROR) {
					if(isRefresh){
						refreshHandler.sendEmptyMessageDelayed(0,2000);
					}
				}
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.rl_search:
				Intent intent1 = new Intent();
				intent1.setClass(getActivity(), MainSearchActivity.class);
				intent1.putExtra("fromWhere",2);
				getActivity().startActivity(intent1);
				break;
			case R.id.rl_my_client:
				if (!isClientShow) {
					lv_my_client.setVisibility(View.VISIBLE);
					alphaIndexer = alphaIndexerClient;
					sections = sectionsClient;
					expert_arrow.setBackgroundResource(R.drawable.icon_down);
				} else {
					expert_arrow.setBackgroundResource(R.drawable.icon_right);
					lv_my_client.setVisibility(View.GONE);
				}
				isClientShow = !isClientShow;

				break;
			case R.id.rl_expert:
				statistics=new Statistics(Micro_CommunityFragment.this);
				statistics.execute("found_expert");
				RBstr = "-1";
				expert_jump = "ture";
				Intent intent = new Intent();
				intent.setClass(getActivity(), MoreExpertActivity.class);
				getActivity().startActivity(intent);

				MobclickAgent.onEvent(getActivity(), Event_Expert);
				break;
			case R.id.rl_administer:
				Intent intent2 = new Intent(getActivity(), AdministerActivity.class);
				startActivity(intent2);
				break;
			case R.id.rl_refresh:
				isRefresh=true;
				iv_refresh.startAnimation(animation);
				rl_refresh.setClickable(false);
				initEvent();
				break;

		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}


	private class LetterListViewListener implements SearchListView.OnTouchingLetterChangedListener {
		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				if(isClientShow){

					lv_my_client.setSelection(position);
				}

//				overlay.setText(sections[position]);
//				overlay.setVisibility(View.VISIBLE);
//				handler.removeCallbacks(overlayThread);
//				// 延迟一秒后执行，让overlay为不可见
//				handler.postDelayed(overlayThread, 1000);
			}
			overlay.setText(s);
			overlay.setVisibility(View.VISIBLE);
			handler.removeCallbacks(overlayThread);
			// 延迟一秒后执行，让overlay为不可见
			handler.postDelayed(overlayThread, 1000);
		}
	}

	private void initAlphaIndexer(ArrayList<MyClientBean> list, int index) {
		if(index==1){
			alphaIndexerClient=new HashMap<String,Integer>();
			sectionsClient=new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).getNameFirstWord();
				// 上一个汉语拼音首字母，如果不存在为""
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).getNameFirstWord() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getNameFirstWord();
					alphaIndexerClient.put(name, i);
					sectionsClient[i] = name;
				}
			}
		}else{
			alphaIndexerUnder=new HashMap<String,Integer>();
			sectionsUnder=new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).getNameFirstWord();
				// 上一个汉语拼音首字母，如果不存在为""
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).getNameFirstWord() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getNameFirstWord();
					alphaIndexerUnder.put(name, i);
					sectionsUnder[i] = name;
				}
			}
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		overlay = (TextView) inflater.inflate(R.layout.serach_overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}
	// 设置overlay不可见
	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	class MyAdapter extends BaseAdapter {
		private ArrayList<MyClientBean> list;
		private int index;

		public MyAdapter(ArrayList<MyClientBean> list, int index) {
			this.list = list;
			this.index = index;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int i) {
			return list.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder holder=null;
			if(view==null){
				holder=new ViewHolder();
				view= LayoutInflater.from(getActivity()).inflate(R.layout.item_zhanye_client_under_list,null);
				holder.iv_round= (RoundImageView) view.findViewById(R.id.iv_round);
				holder.iv_round.setRectAdius(Common.dip2px(getActivity(), 10));
				holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
				view.setTag(holder);
			}else{
				holder= (ViewHolder) view.getTag();
			}
			MyClientBean bean=list.get(i);
			holder.tv_name.setText(bean.getName());
			if(index==1){
				holder.iv_round.setBackgroundResource(R.drawable.people_default_icon);
			}else{
				Glide.with(Micro_CommunityFragment.this)
				.load(bean.getUnderAvatar())
				.placeholder(R.drawable.people_default_icon)
				.error(R.drawable.people_default_icon)//占位图 图片正在加载
				.into(holder.iv_round);
			}
			return view;
		}

		class ViewHolder{
			private RoundImageView iv_round;
			private TextView tv_name;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
