package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.cloudhome.utils.IpConfig;
import com.umeng.socialize.UMShareAPI;

public class JoinUsActivity extends BaseActivity {
    private TextView tv_text;
    private RelativeLayout iv_back;
    private ImageView iv_right;
    private WebView wb_join_us;
    private String brief;
    private String share_title;
    private String img_url;
    private String url;
    private BaseUMShare share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join_us);
        initView();
        share=new BaseUMShare(this,share_title,brief,url,img_url);
        initWeb();

    }

    private void initView() {
        tv_text = (TextView) findViewById(R.id.tv_title);
        iv_back = (RelativeLayout) findViewById(R.id.rl_back);
        iv_right = (ImageView) findViewById(R.id.iv_share);
        wb_join_us = (WebView) findViewById(R.id.wb_join_us);

        tv_text.setText("加入我们");
        iv_right.setImageResource(R.drawable.share);

        brief = "我们在互联网和保险的交叉路，欢迎各种大咖来露两把刷子。";
        share_title = "一起来颠覆传统保险吧！";
        img_url = IpConfig.getIp3() + "/images/join_us_share.png";
        url = IpConfig.getIp3() + "/templates/steward/join_us.php";


        iv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               share.openShare();
            }
        });

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }



    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb() {
        WebSettings setting = wb_join_us.getSettings();
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDomStorageEnabled(true);

        wb_join_us.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        wb_join_us.setWebViewClient(new InterceptingWebViewClient(this, wb_join_us, true));

        wb_join_us.loadUrl(url);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

}
