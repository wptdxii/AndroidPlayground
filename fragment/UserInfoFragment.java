package com.cloudhome.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.AllPageActivity;
import com.cloudhome.activity.CreditsActivity;
import com.cloudhome.activity.FeedbackWebActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.activity.MicroShareWebActivity;
import com.cloudhome.activity.MyInfoActivity;
import com.cloudhome.activity.MyOrderListActivity;
import com.cloudhome.activity.MyWalletActivity;
import com.cloudhome.activity.QianDaoActivity;
import com.cloudhome.activity.RegisterActivity;
import com.cloudhome.activity.SettingsActivity;
import com.cloudhome.activity.SystemMessageActivity;
import com.cloudhome.activity.VerifiedInfoActivity;
import com.cloudhome.activity.Verified_failure_InfoActivity;
import com.cloudhome.activity.VerifyMemberActivity;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.CommissionRefreshEvent;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.event.ModifyUserInfoEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.DailySignMsg;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.RoundImageView;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.umeng.analytics.MobclickAgent;
import com.zcw.togglebutton.ToggleButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class UserInfoFragment extends BaseFragment implements NetResultListener,OnClickListener{

	private AllPageActivity mActivity;
	public static String FRAGMENT_TAG = UserInfoFragment.class.getSimpleName();
	private View mView;
	private RoundImageView info_userIcon;
	private String user_id;
	private String token;
	private String user_id_encode;
	private String user_type = "";
	private String avatar;
	private String user_state;
	private String truename;
    private String cert_a;
    private String cert_b;
    private String licence;
	Map<String, String> key_value = new HashMap<String, String>();
	private RelativeLayout info_usericon_rel;
	SharedPreferences sp;
    SharedPreferences sp5;
	private String loginString;
	private View user_main_include, my_login_include;
	private TextView user_truename;
	private TextView tab_login_but;
	private TextView tab_reg_but;
	private String login_state_old = "";
	//统计接口
	private Statistics statistics;
	private TextView tv_score;
    private RelativeLayout rl_qiandao, rl_commission, rl_my_wallet, rl_my_order, rl_my_score,
            rl_my_micro_station, rl_my_message, rl_feed_back, rl_setting;
    private ToggleButton toggle_commission;
	private boolean hasMicroStation=true;
	private ImageView iv_new_msg;
	private boolean isCheckIn = false;// 是否签到
    private boolean isCommissionShown;
    private View dialogView;
	private String Event_UserInfo = "UserInfoFragment_UserInfo";
	private String Event_SignIN = "UserInfoFragment_SignIn";
	private String Event_MyWallet = "UserInfoFragment_MyWallet";
    private String Event_MyOrder = "UserInfoFragment_MyOrder";
    private String Event_MyScore = "UserInfoFragment_MyScore";
    private String Event_MicroStation = "UserInfoFragment_MicroStation";
    private String Event_MyMessage = "UserInfoFramgent_MyMessage";
	private String Event_FeedBack = "UserInfoFragment_FeedBack";
	private Dialog dialog;
	public static final int GET_DAILY_SIGN_MSG=2;
	private DailySignMsg dailySignMsg;
	private ArrayList<HashMap<String,String>> list;
	private HashMap<String,String> infoMap;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (mView == null) {
			sp = getActivity().getSharedPreferences("userInfo", 0);
            sp5 = getActivity().getSharedPreferences("temp", 0);
			loginString = sp.getString("Login_STATE", "none");
			user_id = sp.getString("Login_UID", "");
			token = sp.getString("Login_TOKEN", "");
			user_id_encode=sp.getString("Login_UID_ENCODE", "");
			user_type = sp.getString("Login_TYPE", "none");
			user_state = sp.getString("Login_CERT", "");
			avatar = sp.getString("avatar", "");
			truename = sp.getString("truename", "");
            cert_a = sp.getString("cert_a", "");
            cert_b = sp.getString("cert_b", "");
            licence = sp.getString("licence", "");
            isCommissionShown = sp5.getBoolean(user_id, false);
			initView(inflater, container);
			EventBus.getDefault().register(this);

		}
		return mView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container) {
		mView = inflater.inflate(R.layout.my_info_main, container, false);
		user_main_include = mView.findViewById(R.id.user_main_include);
		my_login_include = mView.findViewById(R.id.my_login_include);
		tv_score= (TextView) mView.findViewById(R.id.tv_score);
		info_usericon_rel = (RelativeLayout) mView.findViewById(R.id.info_usericon_rel);
		info_userIcon = (RoundImageView) mView.findViewById(R.id.info_userIcon);
		info_userIcon.setRectAdius(Common.dip2px(getActivity(), 2));
		user_truename = (TextView) mView.findViewById(R.id.user_truename);
		rl_my_order=(RelativeLayout) mView.findViewById(R.id.rl_my_order);
		rl_qiandao=(RelativeLayout) mView.findViewById(R.id.rl_qiandao);
        rl_commission = (RelativeLayout) mView.findViewById(R.id.rl_commission);
        toggle_commission = (ToggleButton) rl_commission.findViewById(R.id.toggle_commission);
        if (isCommissionShown) {
            toggle_commission.setToggleOn();
        } else {
            toggle_commission.setToggleOff();
        }
		rl_my_wallet=(RelativeLayout) mView.findViewById(R.id.rl_my_wallet);
		rl_my_score=(RelativeLayout) mView.findViewById(R.id.rl_my_score);
		rl_my_micro_station=(RelativeLayout) mView.findViewById(R.id.rl_my_micro_station);
		rl_my_message=(RelativeLayout) mView.findViewById(R.id.rl_my_message);
		iv_new_msg= (ImageView) mView.findViewById(R.id.iv_new_msg);
		rl_feed_back=(RelativeLayout) mView.findViewById(R.id.rl_feed_back);
		rl_setting=(RelativeLayout) mView.findViewById(R.id.rl_setting);
		info_usericon_rel.setOnClickListener(this);
		rl_qiandao.setOnClickListener(this);
		rl_my_order.setOnClickListener(this);
		rl_my_wallet.setOnClickListener(this);
		rl_my_score.setOnClickListener(this);
		rl_my_micro_station.setOnClickListener(this);
		rl_my_message.setOnClickListener(this);
		rl_feed_back.setOnClickListener(this);
		rl_setting.setOnClickListener(this);

		tab_login_but = (TextView) mView.findViewById(R.id.tab_login_but);
		tab_reg_but = (TextView) mView.findViewById(R.id.tab_reg_but);

		if (loginString.equals("none")) {
			my_login_include.setVisibility(View.VISIBLE);
			user_main_include.setVisibility(View.GONE);
			login_state_old = "false";
			initLoginEvent();
		} else {
			login_state_old = "true";
			initEvent();
			user_main_include.setVisibility(View.VISIBLE);
			my_login_include.setVisibility(View.GONE);
			getMicro();
			getReadStatus();
//			getScore();
		}
		dialog = new Dialog(getActivity(), R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("请稍后...");
	}

	private void getReadStatus() {
		key_value.clear();
		key_value.put("user_id", user_id);
//
		key_value.put("token", token);
		String point_url = IpConfig.getUri("getReadedStatus");
		Log.i("point_url",point_url);
		Log.i("point_urluser_id",user_id);
		Log.i("point_urltoken",token);
		setpoint(point_url);
	}
	private void getScore() {
		key_value.clear();
		key_value.put("user_id", user_id_encode);
		key_value.put("token", token);
		Log.i("getScore",user_id_encode);
		Log.i("getScore",token);

		// 获取积分
		final String PRODUCT_URL = IpConfig.getUri("getScoreAndMoney");
		Log.i("getScore",PRODUCT_URL);
		setScoreData(PRODUCT_URL);
	}

	void initLoginEvent() {
		tab_login_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
			}
		});
		tab_reg_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), RegisterActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}

	void initEvent() {
		if (avatar.length() < 6) {
			if (user_type.equals("02")) {
				info_userIcon.setImageResource(R.drawable.expert_head);
			} else {
				info_userIcon.setImageResource(R.drawable.expert_head);
			}
		} else {
			if (user_type.equals("02")) {


				Glide.with(UserInfoFragment.this)
						.load(avatar)
					//	.placeholder(R.drawable.head_fail) 占位图
						.error(R.drawable.expert_head)
						.crossFade()
						.into(info_userIcon);
			} else {


				Glide.with(UserInfoFragment.this)
						.load(avatar)
						//	.placeholder(R.drawable.head_fail)
						.error(R.drawable.expert_head)
						.crossFade()
						.into(info_userIcon);
			}


		}

		if (truename.equals("") || truename.equals("null")) {
			user_truename.setText("保险人");
		} else {
			user_truename.setText(truename);
		}
        //显示推广费
        toggle_commission.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changeToggle();
	}
        });
    }



    private void changeToggle() {

        if (user_state.equals("00")) {
            if ("".equals(cert_a) && "".equals(cert_b) && "".equals(licence)) {
                CustomDialog.Builder builder = new CustomDialog.Builder(this.getActivity());

                dialogView = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_content_commission, null);
                builder.setContentView(dialogView);
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.dismiss();

                            }
                        });
                builder.create().show();
            } else {
                if (isCommissionShown) {
                    toggle_commission.setToggleOff();
                } else {
                    toggle_commission.setToggleOn();
                }

                isCommissionShown = !isCommissionShown;
                sp5.edit().putBoolean(user_id, isCommissionShown).commit();
                EventBus.getDefault().post(new CommissionRefreshEvent());
            }


        } else if (user_state.equals("01")) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), VerifyMemberActivity.class);
            getActivity().startActivity(intent);
        } else if (user_state.equals("02")) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), VerifiedInfoActivity.class);
            getActivity().startActivity(intent);
        } else if (user_state.equals("03")) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), Verified_failure_InfoActivity.class);
            getActivity().startActivity(intent);
        }


    }

    private static final String TAG = "UserInfoFragment";

    // 获取用户是否可以进我的微站
    public void getMicro() {
		key_value.clear();
        key_value.put("userId", user_id_encode);
		Log.i("getMicro",user_id);
        key_value.put("resName", "我的");
		key_value.put("token", token);
        String user_url = IpConfig.getUri2("getRoleMenus");
        setlistdata(user_url);
    }




	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mActivity = (AllPageActivity) activity;

	}



	private final String mPageName = "UserInfoFragment";

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
		switch(action){
			case GET_DAILY_SIGN_MSG:
				if (flag == MyApplication.DATA_OK) {
					dialog.dismiss();
					Intent intent=new Intent(getActivity(), QianDaoActivity.class);
					intent.putExtra("list",list);
					intent.putExtra("infoMap",infoMap);
					startActivity(intent);
				} else if (flag == MyApplication.NET_ERROR) {
					dialog.dismiss();
					Toast.makeText(getActivity(), "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
				}else if(flag == MyApplication.DATA_EMPTY){
					dialog.dismiss();
				}else if(flag == MyApplication.JSON_ERROR){
					dialog.dismiss();
				}else if(flag == MyApplication.DATA_ERROR){
					dialog.dismiss();
					String errmsg=dataObj.toString();
					Toast.makeText(getActivity(), errmsg, Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}


	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		statistics=new Statistics(UserInfoFragment.this);
		switch(view.getId()){
			case R.id.info_usericon_rel:
				intent.setClass(getActivity(), MyInfoActivity.class);
				getActivity().startActivity(intent);
				MobclickAgent.onEvent(getActivity(), Event_UserInfo);
				break;
			case R.id.rl_qiandao:
//				if(!isCheckIn){
//					String checkInUrl = IpConfig.getUri("CheckIn");
//					checkIn(checkInUrl);
//					MobclickAgent.onEvent(getActivity(), Event_SignIN);
//				}else{
//					Toast.makeText(getActivity(), "已签到", Toast.LENGTH_SHORT).show();
//				}
				dialog.show();
				infoMap=new HashMap<String,String>();
				list=new ArrayList<HashMap<String,String>>();
				dailySignMsg=new DailySignMsg(this);
				dailySignMsg.execute(infoMap, user_id, GET_DAILY_SIGN_MSG, list,token);
				MobclickAgent.onEvent(getActivity(), Event_SignIN);
				break;
			case R.id.rl_my_order:
				intent.setClass(getActivity(), MyOrderListActivity.class);
				getActivity().startActivity(intent);
				statistics.execute("mine_order");
				MobclickAgent.onEvent(getActivity(), Event_MyOrder );
				break;
			case R.id.rl_my_wallet:
				intent.setClass(getActivity(), MyWalletActivity.class);
				getActivity().startActivity(intent);
				statistics.execute("mine_package");
				MobclickAgent.onEvent(getActivity(), Event_MyWallet);
				break;
			case R.id.rl_my_score:
				intent.setClass(getActivity(), CreditsActivity.class);
				getActivity().startActivity(intent);
				statistics.execute("mine_exchange");
                MobclickAgent.onEvent(getActivity(), Event_MyScore);
				break;
			case R.id.rl_my_micro_station:
				if(hasMicroStation){
					String url = IpConfig.getIp()+ "user_id="+ user_id_encode +"&token="+token+ "&mod=getHomepageForExpert";
					String shareurl = IpConfig.getIp() + "user_id=" + user_id_encode+"&token="+token+ "&mod=getHomepageForExpert";
					String img_url;
					if(TextUtils.isEmpty(avatar)){
						String site_url = IpConfig.getIp3();
						img_url = site_url + "/images/homepage_share.jpg";
					}else{
						img_url =avatar;
					}
					if(TextUtils.isEmpty(truename)){
						intent.putExtra("share_title", "资深保险销售专家——保险人");
						intent.putExtra("brief", "您好，我是保险人。愿意为您提供所有保险相关的服务。");
					}else{
						intent.putExtra("share_title", "资深保险销售专家——"+truename);
						intent.putExtra("brief", "您好，我是"+truename+"。愿意为您提供所有保险相关的服务。");
					}
					intent.putExtra("title", "我的微站");
					intent.putExtra("url", url);
					intent.putExtra("shareurl", shareurl);
					intent.putExtra("img_url", img_url);
					intent.setClass(getActivity(), MicroShareWebActivity.class);
					startActivity(intent);
				}else{
					if (user_state.equals("01")) {
						intent.setClass(getActivity(), VerifyMemberActivity.class);
						startActivity(intent);
					} else if (user_state.equals("02") || user_state.equals("00")) {
						intent.setClass(getActivity(), VerifiedInfoActivity.class);
						startActivity(intent);
					} else if (user_state.equals("03")) {
						intent.setClass(getActivity(), Verified_failure_InfoActivity.class);
						startActivity(intent);
					}
				}

				statistics.execute("mine_homepage");
                MobclickAgent.onEvent(getActivity(), Event_MicroStation);
				break;
			case R.id.rl_my_message:
				intent.setClass(getActivity(), SystemMessageActivity.class);
				getActivity().startActivity(intent);
				statistics.execute("index_message");
                MobclickAgent.onEvent(getActivity(), Event_MyMessage);
				break;
			case R.id.rl_feed_back:
				String url2 = IpConfig.getUri("feedback");
				url2 = url2 + "&user_id=" + user_id_encode + "&token=" + token;
				intent.putExtra("url", url2);
				// 从Activity IntentTest跳转到Activity IntentTest01
				intent.setClass(getActivity(), FeedbackWebActivity.class);
				getActivity().startActivity(intent);
				statistics.execute("mine_feedback");
				MobclickAgent.onEvent(getActivity(), Event_FeedBack);
				break;
			case R.id.rl_setting:
				statistics.execute("mine_setting");
				intent.setClass(getActivity(), SettingsActivity.class);
				getActivity().startActivity(intent);
				break;
		}
	}




	@Subscribe (threadMode = ThreadMode.MAIN)
	public void onEvent(LoginEvent event){


		loginString = sp.getString("Login_STATE", "none");
		Log.i("login---",loginString);
		if (loginString.equals("none")) {
			my_login_include.setVisibility(View.VISIBLE);
			user_main_include.setVisibility(View.GONE);
			login_state_old = "false";
			initLoginEvent();
		} else {
			user_main_include.setVisibility(View.VISIBLE);
			my_login_include.setVisibility(View.GONE);
			loginString = sp.getString("Login_STATE", "none");
			user_id = sp.getString("Login_UID", "");
			token = sp.getString("Login_TOKEN", "");
			user_id_encode=sp.getString("Login_UID_ENCODE", "");
			user_type = sp.getString("Login_TYPE", "none");
			user_state = sp.getString("Login_CERT", "");
			truename = sp.getString("truename", "");
			avatar = sp.getString("avatar", "");
			cert_a = sp.getString("cert_a", "");
			cert_b = sp.getString("cert_b", "");
			licence = sp.getString("licence", "");
			isCommissionShown = sp5.getBoolean(user_id, false);
			if (isCommissionShown) {
				toggle_commission.setToggleOn();
			} else {
				toggle_commission.setToggleOff();
			}
			initEvent();
			getMicro();
			getReadStatus();
//			getScore();
			login_state_old = "true";
		}
	}

	@Subscribe (threadMode = ThreadMode.MAIN)
	public void onEvent(ModifyUserInfoEvent event){
		truename = sp.getString("truename", "");
		avatar = sp.getString("avatar", "");
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		user_id_encode=sp.getString("Login_UID_ENCODE", "");
		user_state = sp.getString("Login_CERT", "");
		initEvent();
		getReadStatus();
//		getScore();
	}





	private void setlistdata(String url) {

		Log.d("7777", url);
		OkHttpUtils.get().url(url).params(key_value).build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String response, int id) {
						String jsonString = response;
						Log.d("onSuccess", "onSuccess json = " + jsonString);
						Log.d("onSuccess98988", "onSuccess json = "
								+ jsonString);
						Map<String, String> errcode_map = new HashMap<String, String>();
						try {

							if (jsonString == null || jsonString.equals("")
									|| jsonString.equals("null")) {
								String status = "false";
							} else {

								JSONObject jsonObject = new JSONObject(
										jsonString);

								String errcode = jsonObject
										.getString("errcode");
								if (!errcode.equals("0")) {
									String errmsg = jsonObject
											.getString("errmsg");
									errcode_map.put("errcode", errcode);
									errcode_map.put("errmsg", errmsg);
								} else {

									JSONObject dataObject = jsonObject
											.getJSONObject("data");
									//									JSONArray menusList = dataObject
									//											.getJSONArray("menus")
									//											.getJSONObject(0)
									//											.getJSONArray("menus");
									JSONArray menusArray=dataObject.getJSONArray("menus");
									for(int m=0;m<menusArray.length();m++){
										JSONObject object=menusArray.getJSONObject(m);
										if(object.getString("name").equals("我的")){
											JSONArray menusList=object.getJSONArray("menus");
											for (int i = 0; i < menusList.length(); i++) {
												JSONObject jsonObject2 = menusList.getJSONObject(i);
												if(jsonObject2.getString("name").equals("我的微站")){
													String operate=jsonObject2.getString("operate");
													if(operate.equals("1")){
														hasMicroStation=true;
													}else{
														hasMicroStation=false;
													}
												}
											}
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}

	private void setpoint(String url) {

		OkHttpUtils.post()//
				.url(url)//
				.params(key_value)
				.build()//
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e, int id) {

						Log.e("error", "获取数据异常 ", e);
					}

					@Override
					public void onResponse(String response, int id) {
						Map<String, String> map = new HashMap<String, String>();
						String jsonString = response;
						Log.d("onSuccessread", "onSuccess json = " + jsonString);

						try {

							if (jsonString.equals("") || jsonString.equals("null")) {
							} else {
								// Log.d("44444", jsonString);

								JSONObject jsonObject = new JSONObject(
										jsonString);
								String data = jsonObject.getString("data");

								String errcode = jsonObject
										.getString("errcode");
								if (errcode.equals("0")) {
									iv_new_msg.setVisibility(View.VISIBLE);
								} else {
									iv_new_msg.setVisibility(View.GONE);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}


	//获取积分
	private void setScoreData(String url) {

		OkHttpUtils.get().url(url).params(key_value).build()
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e, int id) {
						Log.e("error", "获取数据异常 ", e);
						Toast.makeText(getActivity(), "网络连接异常，请重试",
								Toast.LENGTH_SHORT).show();
						tv_score.setVisibility(View.GONE);

					}

					@Override
					public void onResponse(String response, int id) {
						String jsonString = response;
						Log.d("onSuccess", "onSuccess json = " + jsonString);
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						try {
							Log.d("5555", jsonString);
							if (jsonString == null || jsonString.equals("")
									|| jsonString.equals("null")) {
								Toast.makeText(getActivity(), "获取积分失败", Toast.LENGTH_SHORT)
										.show();
								tv_score.setVisibility(View.GONE);
							} else {
								Map<String, String> map = new HashMap<String, String>();
								JSONObject jsonObject = new JSONObject(
										jsonString);
								JSONObject dataObject = jsonObject
										.getJSONObject("data");
								String errmsg = jsonObject.getString("errmsg");
								String errcode = jsonObject
										.getString("errcode");
								String score = dataObject.getString("score");
								int checkIn = dataObject.getInt("checkflag");
								tv_score.setVisibility(View.VISIBLE);
								if (checkIn == 0) {
									isCheckIn = false;
								} else if (checkIn == 1) {
									isCheckIn = true;//已签到
								}
								tv_score.setText(score + "分");

							}
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(getActivity(), "获取积分失败", Toast.LENGTH_SHORT).show();
							tv_score.setVisibility(View.GONE);
						}
					}
				});
	}

	public void checkIn(String url) {
		key_value.clear();
		key_value.put("user_id", user_id_encode);
		key_value.put("token", token);

		OkHttpUtils.get()//
				.url(url)//
				.params(key_value)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

						Log.e("error", "获取数据异常 ", e);
						Toast.makeText(getActivity(), "签到失败",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(String response, int id) {
						String jsonString = response;
						Log.d("checkIn", "onSuccess json = " + jsonString);
						try {
							JSONObject obj = new JSONObject(jsonString);
							String errcode = obj.getString("errcode");
							if ("0".equals(errcode)) {
								JSONObject scoreObject = obj
										.getJSONObject("data");
								String add_score = scoreObject
										.getString("add_score");
								Toast.makeText(getActivity(), "+" + add_score + "积分", Toast.LENGTH_SHORT).show();
								// 再次调用获取积分接口
								// 获取积分
								getScore();
								//签到成功后要立即获取是否有新消息
								getReadStatus();
							} else {
								Toast.makeText(getActivity(), "签到失败", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		Log.i("hidden---", "" + hidden);
		if(!hidden){
			if(mView!=null)
				if (!loginString.equals("none")) {
					//获取是否有新消息
					getReadStatus();
					//重新获取积分
//					getScore();
				}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
