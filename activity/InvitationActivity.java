package com.cloudhome.activity;

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
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.PosterGenerate;
import com.cloudhome.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

public class InvitationActivity extends BaseActivity implements View.OnClickListener, NetResultListener {
    private String Event_Invite = "InvitationActivity_Invite";
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private WebView webView;
    private String user_state;
    private String mUserId;
    private String mToken;
    private String mShareUrl;
    private BaseUMShare mUMShare;
    private String mUrl;
    private PosterGenerate posterGenerate;
    public static final int POSTER = 1;
    String shareTitle = "";
    String shareDesc = "";

    public static void actionStart(Context context, String url) {
        Intent intent = new Intent(context, InvitationActivity.class);
        //        url="http://10.10.10.25:1213/projects/invite_friend/app_page.html?state=211&client_type=android";
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
        webView.addJavascriptInterface(new PosterInterface(InvitationActivity.this), "poster");
        webView.loadUrl(mUrl);
    }

    @JavascriptInterface
    public void invite() {
        BaseUMShare.shareNotQQZone(this, shareTitle, shareDesc, mShareUrl, R.drawable.icon_share_logo);
        MobclickAgent.onEvent(this, Event_Invite);
    }

    private void initData() {
        mUserId = sp.getString("Login_UID_ENCODE", "");
        mToken = sp.getString("Login_TOKEN", "");
        user_state = sp.getString("Login_CERT", "");
        mShareUrl = IpConfig.getIp4() + "/active/invite_friend/wx_page.html?user_id=" + mUserId + "&token=" + mToken;
        mUrl = getIntent().getStringExtra("url") + "&client_type=android";
        shareTitle = getString(R.string.invite_share_title);
        shareDesc = getString(R.string.invite_share_desc);
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

    public class PosterInterface {
        Context mContext;

        PosterInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void createPoster(String codeUrl, String imgUrl) {
            checkAuthStatus(codeUrl, imgUrl);
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        if (flag == MyApplication.DATA_OK) {
            String imageUrl = dataObj.toString();
            Intent intent = new Intent(InvitationActivity.this, PosterActivity.class);
            intent.putExtra("imageUrl", imageUrl);
            startActivity(intent);
        } else if (flag == MyApplication.NET_ERROR) {
        } else if (flag == MyApplication.DATA_EMPTY) {
        } else if (flag == MyApplication.JSON_ERROR) {
        } else if (flag == MyApplication.DATA_ERROR) {
            String errmsg = dataObj.toString();
            Toast.makeText(InvitationActivity.this, errmsg, Toast.LENGTH_SHORT).show();
        }
    }

    public void checkAuthStatus(String codeUrl, String imgUrl) {
        //用户状态 00表示已认证   02表示认证中    01表示未认证
        if (user_state.equals("00")) {
            posterGenerate = new PosterGenerate(InvitationActivity.this);
            posterGenerate.execute(codeUrl, imgUrl, mUserId, mToken, POSTER);
        } else if (user_state.equals("01")) {
            Intent intent = new Intent();
            intent.setClass(this, VerifyMemberActivity.class);
            startActivity(intent);
        } else if (user_state.equals("02")) {
            Intent intent = new Intent();
            intent.setClass(this, VerifiedInfoActivity.class);
            startActivity(intent);
        } else if (user_state.equals("03")) {
            Intent intent = new Intent();
            intent.setClass(this, Verified_failure_InfoActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}
