package com.wptdxii.playground.activity;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cloudhome.R;
import com.cloudhome.bean.SplashAdBean;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.fragment.DiscoverFragment;
import com.cloudhome.fragment.MainFragment;
import com.cloudhome.fragment.Micro_CommunityFragment;
import com.cloudhome.fragment.UserInfoFragment;
import com.cloudhome.utils.AdFileUtils;
import com.cloudhome.utils.AdPreference;
import com.cloudhome.utils.ConnectionUtil;
import com.cloudhome.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class AllPageActivity extends BaseActivity {
    private RadioGroup mRgMain;
    private RadioButton rbMainPage;
    private RadioButton rbZhanYe;
    private RadioButton rbDiscover;
    private RadioButton rMe;

    private Fragment currentFragment;
    private int currentPosition;
    private FragmentManager mFManager;
    private boolean mIsBackStackEnable;
    private long exitTime = 0;

    /**
     * 主页
     */
    public static final int FRAGMENT_MAIN_PAGE = 0;
    /**
     * 分类
     */
    public static final int FRAGMENT_ZHAN_YE = 1;
    /**
     * 个人中心
     */
    public static final int FRAGMENT_DISCOVER = 2;
    /**
     * 更多
     */
    public static final int FRAGMENT_ME = 3;

    private int oldpage = 0;

    public Fragment[] mFragments = new Fragment[4];

    private String AD_PATH
            = Environment.getExternalStorageDirectory() + "/MFAd/";
    private String Event_MainPage = "AllPageActivity_MainPage";
    private String Event_ZhanYe = "AllPageActivity_ZhanYe";
    private String Event_Discover = "AllPageActivity_Discover";
    private String Event_Mine = "AllPageActivity_Mine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allpage_main);

        if (!ConnectionUtil.isConn(getApplicationContext())) {
            ConnectionUtil.setNetworkMethod(this);
        }
        initView();
        setOnClickListener();
        //接收无序跳转回首页
//        registerDisorderJumpReceiver();
        EventBus.getDefault().register(this);

        getSplashAdData();
    }

    private static final String TAG = "AllPageActivity";

    private void getSplashAdData() {
        String url = IpConfig.getUri2("splashAd");
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        if (response != null && !response.equals("") && !response.equals("null")) {

                            SplashAdBean adBean = JSON.parseObject(response, SplashAdBean.class);
                            List<SplashAdBean.DataBean> data = adBean.getData();
                            int random = new Random().nextInt(data.size());
                            downloadSplashAd(data.get(random));
                        }


                    }
                });
    }

    private void downloadSplashAd(SplashAdBean.DataBean data) {

        AdPreference.getInstance().saveSplashAdPage(data);

        String AD_PATH
                = Environment.getExternalStorageDirectory() + "/MFAd/" + AdFileUtils.getImgName(AdPreference.getInstance().getSplashAdPage().getImg());

        if (data.getImg() != AdPreference.getInstance().getSplashAdPage().getImg()) {
            AdFileUtils.deleteFile(AD_PATH);
        }

        File file = new File(AD_PATH);
        if (!file.exists()) {
            OkHttpUtils.get()
                    .url(data.getImg())
                    .build()
                    .execute(new FileCallBack(this.AD_PATH, AdFileUtils.getImgName(AdPreference.getInstance().getSplashAdPage().getImg())) {
                        @Override
                        public void inProgress(float progress) {

                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            AdPreference.getInstance().clear();
                        }

                        @Override
                        public void onResponse(File response) {

                        }
                    });
        }

    }


   /* private void registerDisorderJumpReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("cloudhome.disorder.jump");
        disorderJumpReceiver = new DisorderJumpReceiver();
        registerReceiver(disorderJumpReceiver, filter);
    }*/

    private void initView() {
        mRgMain = (RadioGroup) findViewById(R.id.rgMain);
        rbMainPage = (RadioButton) findViewById(R.id.rbMainPage);
        rbZhanYe = (RadioButton) findViewById(R.id.rbZhanYe);
        rbDiscover = (RadioButton) findViewById(R.id.rbDiscover);
        rMe = (RadioButton) findViewById(R.id.rMe);

        rbMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentPosition(FRAGMENT_MAIN_PAGE, false, null);
                MobclickAgent.onEvent(AllPageActivity.this, Event_MainPage);
            }
        });
        rbZhanYe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentPosition(FRAGMENT_ZHAN_YE, false, null);
                MobclickAgent.onEvent(AllPageActivity.this, Event_ZhanYe);
            }
        });
        rbDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentPosition(FRAGMENT_DISCOVER, false, null);
                MobclickAgent.onEvent(AllPageActivity.this, Event_Discover);
            }
        });
        rMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentPosition(FRAGMENT_ME, false, null);
                MobclickAgent.onEvent(AllPageActivity.this, Event_Mine);
            }
        });


    }


    private void setOnClickListener() {
        //		setrgOnSlectListener();
        setCurrentPosition(FRAGMENT_MAIN_PAGE, false, null);
    }

    /**
     * @author Djl 设置地步RadioButton的点击事件
     */
    //	private void setrgOnSlectListener() {
    //		mRgMain.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    //			@Override
    //			public void onCheckedChanged(RadioGroup group, int checkedId) {
    //				switch (checkedId) {
    //					case R.id.rbMainPage:
    //						setCurrentPosition(FRAGMENT_MAIN_PAGE, false, null);
    //						oldpage=0;
    //						break;
    //					case R.id.rbZhanYe:
    //						oldpage=1;
    //						setCurrentPosition(FRAGMENT_ZHAN_YE, false, null);
    //						break;
    //					case R.id.rbDiscover:
    //						oldpage=2;
    //						setCurrentPosition(FRAGMENT_DISCOVER, false, null);
    //						break;
    //					case R.id.rMe:
    //						oldpage=3;
    //
    //						setCurrentPosition(FRAGMENT_ME, false, null);
    //						Log.i("here","-------");
    //						break;
    //
    //				}
    //
    //			}
    //		});
    //	}
    public void setCurrentPosition(int currentPosition, boolean isNeedReLoadTheFragment, Bundle args) {
        mFManager = getSupportFragmentManager();
        if (args == null) {
            args = new Bundle();
        }

        switch (currentPosition) {
            case FRAGMENT_MAIN_PAGE:
                if (mFragments[FRAGMENT_MAIN_PAGE] == null) {
                    currentFragment = mFragments[FRAGMENT_MAIN_PAGE] = new MainFragment();
                } else {
                    currentFragment = mFragments[FRAGMENT_MAIN_PAGE];
                }
                // currentFragment = new MainPageFragment();
                rbMainPage.setChecked(true);
                break;

            case FRAGMENT_ZHAN_YE:
                currentFragment = mFragments[FRAGMENT_ZHAN_YE] == null ? mFragments[FRAGMENT_ZHAN_YE] = new Micro_CommunityFragment() : mFragments[FRAGMENT_ZHAN_YE];
                // currentFragment = new CategoryFragment();
                rbZhanYe.setChecked(true);
                break;
            case FRAGMENT_DISCOVER:
                currentFragment = mFragments[FRAGMENT_DISCOVER] == null ? mFragments[FRAGMENT_DISCOVER] = new DiscoverFragment() : mFragments[FRAGMENT_DISCOVER];
                rbDiscover.setChecked(true);
                break;
            case FRAGMENT_ME:
                currentFragment = mFragments[FRAGMENT_ME] == null ? mFragments[FRAGMENT_ME] = new UserInfoFragment() : mFragments[FRAGMENT_ME];
                // currentFragment = new MoreFragment();
                rMe.setChecked(true);
                break;
            default:
                currentFragment = new Fragment();
                break;
        }
        if (currentFragment.getArguments() == null) {
            currentFragment.setArguments(args);
        }
        Log.i("lalal", "1");
        FragmentTransaction bTrans = mFManager.beginTransaction();
        if (!currentFragment.isAdded()) {
            Log.i("lalal", "2");
            bTrans.add(R.id.frame_container, mFragments[currentPosition]);
        }
        for (int i = 0; i < mFragments.length; i++) {
            if (i == currentPosition) {
                Log.i("lalal", "3");
                bTrans.show(mFragments[currentPosition]);
            } else {
                Log.i("lalal", "4");
                if (mFragments[i] != null) {
                    Log.i("lalal", "5");
                    bTrans.hide(mFragments[i]);
                }
            }
        }

        if (currentPosition == FRAGMENT_MAIN_PAGE) {
            mIsBackStackEnable = false;
        } else if (this.currentPosition == FRAGMENT_MAIN_PAGE) {
            mIsBackStackEnable = true;
        }
        Log.i("lalal", "6");
        bTrans.commitAllowingStateLoss();
        Log.i("lalal", "");
        this.currentPosition = currentPosition;
    }

    @Override
    public void onBackPressed() {
        // if (mIsBackStackEnable) {
        // setCurrentPosition(FRAGMENT_MAIN_PAGE, false, null);
        // } else {
        super.onBackPressed();
        // }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    // 监听点击回退按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


   /* public class DisorderJumpReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int page = intent.getIntExtra("page", -1);
            switch (page) {
                case 0:
                    setCurrentPosition(FRAGMENT_MAIN_PAGE, false, null);
                    break;
                case 1:
                    setCurrentPosition(FRAGMENT_ZHAN_YE, false, null);
                    break;
                case 2:
                    setCurrentPosition(FRAGMENT_DISCOVER, false, null);
                    break;
                case 3:
                    setCurrentPosition(FRAGMENT_ME, false, null);
                    break;
            }
        }
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DisorderJumpEvent event){
        int page = event.page;
        switch (page) {
            case 0:
                setCurrentPosition(FRAGMENT_MAIN_PAGE, false, null);
                break;
            case 1:
                setCurrentPosition(FRAGMENT_ZHAN_YE, false, null);
                break;
            case 2:
                setCurrentPosition(FRAGMENT_DISCOVER, false, null);
                break;
            case 3:
                setCurrentPosition(FRAGMENT_ME, false, null);
                break;
        }
    }

}
