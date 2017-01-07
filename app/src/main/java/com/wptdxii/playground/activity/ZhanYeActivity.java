package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.fragment.GiftInsuranceFragment;
import com.cloudhome.fragment.TaskLotteryFragment;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.DailySignMsg;
import com.cloudhome.network.GetDailyMsg;
import com.cloudhome.network.Statistics;
import com.cloudhome.view.customview.FloatScrollView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ZhanYeActivity extends BaseActivity implements NetResultListener, FloatScrollView.OnScrollListener,View.OnClickListener{
    private AllPageActivity mActivity;
    SharedPreferences sp;
    private String loginString;
    private String user_id, nickname;
    private View rl_lottery_already_login;
    private View my_login_include;
    private Button tab_login_but;
    private Button tab_reg_but;
    private int currentPosition;

    private RadioGroup rg_top_two;
    private RadioButton rb_left;
    private RadioButton rb_right;
    private FloatScrollView float_scroll;
    private FrameLayout fl_task_lottery;
    public static final int FRAGMENT_TASK_LOTTERY = 0;
    public static final int FRAGMENT_GIFT_INSURANCE =1;
    public Fragment[] mFragments = new Fragment[2];

    //获取天气数据
    public static final int GET_DAILY_MSG =1;
    private Map<String, String> weatherValue=new HashMap<String, String>();
    private ImageView iv_big_bg;
    private RelativeLayout rl_gps;
    private TextView tv_gps;
    private ImageView iv_weather;
    private TextView tv_temperature;
    private RelativeLayout rl_temperature;
    private TextView tv_current_time;
    private TextView tv_should_do;
    private TextView tv_not_do;
    RelativeLayout radiotop;
    RelativeLayout radiodown;
    private String  token;

    private Fragment currentFragment;
    private FragmentManager mFManager;
    private FragmentTransaction bTrans;
    private RelativeLayout rl_qiandao;
    private RelativeLayout iv_back;

    public static final int GET_DAILY_SIGN_MSG=2;
    private DailySignMsg dailySignMsg;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> infoMap;
    private Dialog dialog;

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            if(rg_top_two.getCheckedRadioButtonId()==rb_left.getId()&&mFragments[FRAGMENT_TASK_LOTTERY]!=null){
                TaskLotteryFragment fg= (TaskLotteryFragment) currentFragment;
                fg.refreshView();
            }
            if(rg_top_two.getCheckedRadioButtonId()==rb_right.getId()&&mFragments[FRAGMENT_GIFT_INSURANCE]!=null){
                GiftInsuranceFragment fg= (GiftInsuranceFragment) currentFragment;
                fg.refreshView();
            }
        }

    };
    private ImageView iv_gps_pic;
    private TextView tv_temperature_unit;
    private String Event_ZhanYeTask = "ZhanYeActivity_ZhanYeTask";
    private String Event_ComplimentaryIns = "ZhanYeActivity_ComplimentaryIns";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhan_ye);
        sp = getSharedPreferences("userInfo", 0);
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");

        initView();
        //获取天气数据
        initWeather();

    }

    private void initWeather() {
        GetDailyMsg dailyMsg=new GetDailyMsg(this);
        dailyMsg.execute(weatherValue,GET_DAILY_MSG);
    }

    private void initView() {
        //抽奖页面
//		rl_lottery_already_login=mView.findViewById(R.id.rl_lottery_already_login);
        //登录页面
        iv_back= (RelativeLayout) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        my_login_include=findViewById(R.id.my_login_include);
        tab_login_but = (Button) findViewById(R.id.tab_login_but);
        tab_reg_but = (Button) findViewById(R.id.tab_reg_but);


        fl_task_lottery= (FrameLayout) findViewById(R.id.fl_task_lottery);

        //天气信息
        radiodown= (RelativeLayout) findViewById(R.id.rg_top_two);
        radiotop= (RelativeLayout) findViewById(R.id.rg_top_three);
        float_scroll= (FloatScrollView) findViewById(R.id.float_scroll);
        rg_top_two= (RadioGroup) radiotop.findViewById(R.id.rg_top);
        rb_left= (RadioButton) radiotop.findViewById(R.id.rb_left);
        rb_right=(RadioButton) radiotop.findViewById(R.id.rb_right);

        iv_big_bg= (ImageView) findViewById(R.id.iv_big_bg);
        rl_gps= (RelativeLayout) findViewById(R.id.rl_gps);
        tv_gps= (TextView) findViewById(R.id.tv_gps);
        rl_temperature=(RelativeLayout) findViewById(R.id.rl_temperature);
        iv_weather= (ImageView)findViewById(R.id.iv_weather);
        tv_temperature= (TextView)findViewById(R.id.tv_temperature);
        rl_temperature= (RelativeLayout)findViewById(R.id.rl_temperature);
        tv_current_time= (TextView)findViewById(R.id.tv_current_time);
        tv_should_do= (TextView)findViewById(R.id.tv_should_do);
        tv_not_do= (TextView)findViewById(R.id.tv_not_do);

        rl_qiandao= (RelativeLayout)findViewById(R.id.rl_qiandao);
        rl_qiandao.setOnClickListener(this);

        float_scroll.setOnScrollListener(this);
        findViewById(R.id.rl_topper).getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // TODO Auto-generated method stub
                        onScroll(float_scroll.getScrollY());
                    }
                });
        float_scroll.post(new Runnable() {
            @Override
            public void run() {
                float_scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });




        rg_top_two.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_left:
                        setCurrentPosition(FRAGMENT_TASK_LOTTERY, false, null);
                        break;
                    case R.id.rb_right:
                        setCurrentPosition(FRAGMENT_GIFT_INSURANCE, false, null);
                        break;
                    default:
                        break;
                }
            }
        });
        setCurrentPosition(FRAGMENT_TASK_LOTTERY, false, null);

        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
    }



    private void setCurrentPosition(int currentPosition, boolean b, Object o) {
        mFManager = getSupportFragmentManager();
        Statistics statistics=new Statistics(this);
        switch (currentPosition) {
            case FRAGMENT_TASK_LOTTERY:
                boolean isFirst=false;
                if (mFragments[FRAGMENT_TASK_LOTTERY] == null) {
                    currentFragment = mFragments[FRAGMENT_TASK_LOTTERY] = new TaskLotteryFragment();
                } else {
                    currentFragment = mFragments[FRAGMENT_TASK_LOTTERY];
                    TaskLotteryFragment fg= (TaskLotteryFragment) currentFragment;
                    fg.refreshView();
                }
                rg_top_two.check(R.id.rb_left);
                statistics.execute("develop_active");
                MobclickAgent.onEvent(ZhanYeActivity.this, Event_ZhanYeTask);
                break;
            case FRAGMENT_GIFT_INSURANCE:
                if (mFragments[FRAGMENT_GIFT_INSURANCE] == null) {
                    currentFragment = mFragments[FRAGMENT_GIFT_INSURANCE] = new GiftInsuranceFragment();
                } else {
                    currentFragment = mFragments[FRAGMENT_GIFT_INSURANCE];
                    GiftInsuranceFragment fg= (GiftInsuranceFragment) currentFragment;
                    fg.refreshView();
                }
                rg_top_two.check(R.id.rb_right);
                statistics.execute("develop_give");
                MobclickAgent.onEvent(ZhanYeActivity.this, Event_ComplimentaryIns);
                break;

            default:
                currentFragment = new Fragment();
                break;
        }
//		setCurrentFragment(currentPosition);
        FragmentTransaction bTrans = mFManager.beginTransaction();
        if (!currentFragment.isAdded()) {
            bTrans.add(R.id.fl_task_lottery, mFragments[currentPosition]);
        }
        for (int i = 0; i < mFragments.length; i++) {
            if (i == currentPosition) {
                bTrans.show(mFragments[currentPosition]);
            } else {
                if (mFragments[i] != null) {
                    bTrans.hide(mFragments[i]);
                }
            }
        }
//		if (currentPosition != FRAGMENT_CATEGORY && mFragments[1] != null) {
//			CategoryFragment f = (CategoryFragment) mFragments[1];
//			f.setChoiceMenuVisable(false);
//		}
//		if (currentPosition == FRAGMENT_MAIN_PAGE) {
//			mIsBackStackEnable = false;
//		} else if (this.currentPosition == FRAGMENT_MAIN_PAGE) {
//			mIsBackStackEnable = true;
//		}
        bTrans.commitAllowingStateLoss();
        this.currentPosition = currentPosition;
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
        switch (action)
        {
            case GET_DAILY_MSG:
                if (flag == MyApplication.DATA_OK) {
                    rl_gps= (RelativeLayout) findViewById(R.id.rl_gps);
                    tv_gps= (TextView) findViewById(R.id.tv_gps);
                    iv_gps_pic=(ImageView) findViewById(R.id.iv_gps_pic);
                    iv_gps_pic.setVisibility(View.VISIBLE);
                    rl_temperature=(RelativeLayout) findViewById(R.id.rl_temperature);
                    tv_temperature_unit=(TextView) findViewById(R.id.tv_temperature_unit);
                    tv_temperature_unit.setVisibility(View.VISIBLE);
                    iv_big_bg=(ImageView) findViewById(R.id.iv_big_bg);
                    iv_weather= (ImageView) findViewById(R.id.iv_weather);
                    tv_temperature= (TextView) findViewById(R.id.tv_temperature);
                    rl_temperature= (RelativeLayout) findViewById(R.id.rl_temperature);
                    tv_current_time= (TextView) findViewById(R.id.tv_current_time);
                    tv_current_time.setVisibility(View.VISIBLE);
                    tv_should_do= (TextView) findViewById(R.id.tv_should_do);
                    tv_not_do= (TextView) findViewById(R.id.tv_not_do);

                    tv_gps.setText(weatherValue.get("city"));
                    tv_temperature.setText(weatherValue.get("temperature"));
                    tv_current_time.setText(weatherValue.get("date")+"  "+weatherValue.get("lunar"));
                    tv_should_do.setText(weatherValue.get("suit"));
                    tv_not_do.setText(weatherValue.get("avoid"));

                    Glide.with(ZhanYeActivity.this)
                            .load(weatherValue.get("weatherBgImg"))
                            .centerCrop()
                            .placeholder(R.drawable.sun_day)  //占位图 图片正在加载
                            .error(R.drawable.sun_day)
                            .crossFade()
                            .into(iv_big_bg);
                    Glide.with(ZhanYeActivity.this)
                            .load(weatherValue.get("weatherImg"))
                            .centerCrop()
//                            .placeholder(R.drawable.white)  //占位图 图片正在加载
//                            .error(R.drawable.white)
                            .crossFade()
                            .into(iv_weather);
                    float_scroll.fullScroll(ScrollView.FOCUS_UP);

                } else if (flag == MyApplication.NET_ERROR) {
                }else if(flag == MyApplication.DATA_EMPTY){
                }else if(flag == MyApplication.JSON_ERROR){
                }else if(flag == MyApplication.DATA_ERROR){
                }
                break;
            case GET_DAILY_SIGN_MSG:
                if (flag == MyApplication.DATA_OK) {
                    dialog.dismiss();
                    Intent intent=new Intent(ZhanYeActivity.this, QianDaoActivity.class);
                    intent.putExtra("list",list);
                    intent.putExtra("infoMap",infoMap);
                    startActivity(intent);
                } else if (flag == MyApplication.NET_ERROR) {
                    dialog.dismiss();
                    Toast.makeText(this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
                }else if(flag == MyApplication.DATA_EMPTY){
                    dialog.dismiss();
                }else if(flag == MyApplication.JSON_ERROR){
                    dialog.dismiss();
                }else if(flag == MyApplication.DATA_ERROR){
                    dialog.dismiss();
                    String errmsg=dataObj.toString();
                    Toast.makeText(this, errmsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY,
                radiodown.getTop());
        Log.i("mBuyLayout2ParentTop", scrollY + "====" + radiodown.getTop() + "====" + mBuyLayout2ParentTop + "");
        radiotop.layout(
                0,
                mBuyLayout2ParentTop,
                radiotop.getWidth(),
                mBuyLayout2ParentTop
                        + radiotop
                        .getHeight());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_qiandao:
                Log.i("rl_qiandao","rl_qiandao");
                dialog.show();
                infoMap=new HashMap<String,String>();
                list=new ArrayList<HashMap<String,String>>();
                dailySignMsg=new DailySignMsg(this);
                dailySignMsg.execute(infoMap, user_id, GET_DAILY_SIGN_MSG, list,token);
                break;
        }
    }
}
