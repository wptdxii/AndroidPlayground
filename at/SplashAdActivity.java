package com.cloudhome.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.cloudhome.R;
import com.cloudhome.bean.SplashAdBean;
import com.cloudhome.utils.AdFileUtils;
import com.cloudhome.utils.AdPreference;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashAdActivity extends BaseActivity {
    private static final String EVENT_AD = "SplashAdActivity_Ad";
    private static final String EVENT_SKIP = "SplashAdActivity_Skip";
    private static final int MSG_START_ACTIVITY = 1;
    private static final int MSG_COUNT_DOWN = 2;
    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    private int mCountTime = 3;
    private SplashAdBean.DataBean mData;
    private String mToken;
    private String mUserIdEncode;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_START_ACTIVITY) {
                cancelFullScreen();
                Intent intent = new Intent(SplashAdActivity.this, AllPageActivity.class);
                startActivity(intent);
                finish();
            }
            if (msg.what == MSG_COUNT_DOWN) {
                String text = String.format(getString(R.string.splash_skip), --mCountTime);
                btnSkip.setText(text);
                if (mCountTime > 0) {
                    mHandler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
                } else {
                    mHandler.sendEmptyMessage(MSG_START_ACTIVITY);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        String text = String.format(getString(R.string.splash_skip), mCountTime);
        btnSkip.setText(text);
    }

    private void initData() {
        String adPath = getExternalFilesDir(null).getAbsolutePath() + "/MFAd/" +
                AdFileUtils.getImgName(AdPreference.getInstance().getSplashAdPage().getImg());
        sp = this.getSharedPreferences("userInfo", 0);
        sp2 = this.getSharedPreferences("otherinfo", 0);
        sp5 = this.getSharedPreferences("temp", 0);
        mUserIdEncode = sp.getString("Login_UID_ENCODE", "");
        mToken = sp.getString("Login_TOKEN", "");
        mData = AdPreference.getInstance().getSplashAdPage();
        File file = new File(adPath);
        if (file.exists() && file.isFile()) {
            Bitmap bm = BitmapFactory.decodeFile(adPath);
            if (bm != null) {
                ivSplash.setImageBitmap(bm);
            } else {
                AdFileUtils.deleteFile(adPath);
            }
        }
        mHandler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
    }

    @OnClick(R.id.btn_skip)
    public void skip() {
        mHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(SplashAdActivity.this, AllPageActivity.class);
        startActivity(intent);
        MobclickAgent.onEvent(SplashAdActivity.this, EVENT_SKIP);
        finish();
    }

    @OnClick(R.id.iv_splash)
    public void splashAd() {
        String brief = mData.getBrief();
        int is_share = mData.getIsShare();
        String title = mData.getTitle();
        String img = mData.getShare_img();
        String url = mData.getUrl();

        if (!"".equals(mData.getUrl())) {
            mHandler.removeCallbacksAndMessages(null);
            if (url.contains("?")) {
                url = url + "&userId=" + mUserIdEncode + "&token=" + mToken;
            } else {
                url = url + "?userId=" + mUserIdEncode + "&token=" + mToken;
            }

            Intent intentAllPage = new Intent(this, AllPageActivity.class);
            Intent intentHomeWebShare = new Intent(this, HomeWebShareActivity.class);
            intentHomeWebShare.putExtra("brief", brief);
            intentHomeWebShare.putExtra("title", title);
            intentHomeWebShare.putExtra("img", img);
            intentHomeWebShare.putExtra("is_share", is_share + "");
            intentHomeWebShare.putExtra("url", url);
            intentHomeWebShare.putExtra("share_title", title);

            cancelFullScreen();
            Intent[] intents = new Intent[]{intentAllPage, intentHomeWebShare};
            startActivities(intents);
            finish();
            HashMap<String, String> params = new HashMap<>();
            params.put("title", title);
            MobclickAgent.onEvent(SplashAdActivity.this, EVENT_AD, params);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void cancelFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
