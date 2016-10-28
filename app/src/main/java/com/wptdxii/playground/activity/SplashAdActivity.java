package com.wptdxii.playground.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.cloudhome.R;
import com.cloudhome.bean.SplashAdBean;
import com.cloudhome.utils.AdFileUtils;
import com.cloudhome.utils.AdPreference;

import java.io.File;

public class SplashAdActivity extends BaseActivity implements View.OnClickListener {
    public static final int START_ALLPAGE_ACTIVITY = 1;
    public static final int COUNT_DOWN = 2;
    private int total = 3;
    private static final String AD_PATH
            = Environment.getExternalStorageDirectory() + "/MFAd/" +
            AdFileUtils.getImgName(AdPreference.getInstance().getSplashAdPage().getImg());

    private SplashAdBean.DataBean data;
    private String loginString;
    private ImageView splahAdImg;
    private Button adSkipBtn;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == START_ALLPAGE_ACTIVITY) {
                cancelFullScreen();
                Intent intent = new Intent(SplashAdActivity.this, AllPageActivity.class);
                startActivity(intent);
                finish();
            }
            if (msg.what == COUNT_DOWN) {
                adSkipBtn.setText("跳过" + --total);
                if (total > 0) {

                    handler.sendEmptyMessageDelayed(COUNT_DOWN, 1000);
                } else {
                    handler.sendEmptyMessage(START_ALLPAGE_ACTIVITY);
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);

        initView();
        initData();


    }

    private void initData() {
        sp = this.getSharedPreferences("userInfo", 0);
        sp2 = this.getSharedPreferences("otherinfo", 0);
        sp5 = this.getSharedPreferences("temp", 0);
        loginString = sp.getString("Login_STATE", "none");
        data = AdPreference.getInstance().getSplashAdPage();
        File file = new File(AD_PATH);
        if (file.exists() && file.isFile()) {
            Bitmap bm = BitmapFactory.decodeFile(AD_PATH);
            if (bm!=null) {
                splahAdImg.setImageBitmap(bm);
            }else {
                AdFileUtils.deleteFile(AD_PATH);
            }
        }

        handler.sendEmptyMessageDelayed(COUNT_DOWN, 1000);
    }

    private void initView() {
        splahAdImg = (ImageView) findViewById(R.id.splash_ad_img);
        adSkipBtn = (Button) findViewById(R.id.ad_skip_btn);

        splahAdImg.setOnClickListener(this);
        adSkipBtn.setOnClickListener(this);
        adSkipBtn.setText("跳过" + total);

    }

    @Override
    public void onClick(View v) {
        handler.removeCallbacksAndMessages(null);
        if (v.getId() == R.id.ad_skip_btn) {
            Intent intent = new Intent(SplashAdActivity.this, AllPageActivity.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.splash_ad_img) {
            String brief = data.getBrief();
            int is_share = data.getIsShare();
            String title =data.getTitle();
            String img = data.getShare_img();
            String url = data.getUrl();


                Intent intentAllPage = new Intent(this, AllPageActivity.class);

                Intent intentHomeWebShare = new Intent(this,HomeWebShareActivity.class);
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
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void cancelFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    }
}
