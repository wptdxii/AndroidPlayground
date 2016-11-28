package com.wptdxii.playground.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;

public class InvitationActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private WebView webView;

    private String mUserId;
    private String mToken;
    private String mShareUrl;
    private BaseUMShare mUMShare;
    private String mUrl;

    public static void actionStart(Context context, String url) {
        Intent intent = new Intent(context, InvitationActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        initView();
        initData();
        initWebView();
    }

    private void initWebView() {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        webView.addJavascriptInterface(this, "android");
        webView.loadUrl(mUrl);
    }

    @JavascriptInterface
    public void invite() {
        mUMShare.openShare();
    }

    private void initData() {
        mUserId = sp.getString("Login_UID_ENCODE", "");
        mToken = sp.getString("Login_TOKEN", "");
        mShareUrl = IpConfig.getIp4() + "/active/invite_friend/wx_page.html?state=" + mUserId + "&token=" + mToken;
        mUrl = getIntent().getStringExtra("url") + "&client_type=android";
        String shareTitle = getString(R.string.invite_share_title);
        String shareDesc = getString(R.string.invite_share_desc);
        mUMShare = new BaseUMShare(this, shareTitle, shareDesc, mShareUrl, R.drawable.icon_share_logo);
        mUMShare.inviteShare();
    }

    private void initView() {
        rlBack = (RelativeLayout) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_text);
        rlShare = (RelativeLayout) findViewById(R.id.rl_right);
        webView = (WebView) findViewById(R.id.webView);

        rlBack.setOnClickListener(this);
        tvTitle.setText("保客云集邀请函");
        rlShare.setVisibility(View.INVISIBLE);
        rlShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }
}
