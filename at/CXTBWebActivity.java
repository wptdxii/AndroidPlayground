package com.cloudhome.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.wxapi.WXPayEntryActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;

public class CXTBWebActivity extends BaseActivity implements OnClickListener {
    private static final String PARAM_USER_ID = "suid";
    private static final String PARAM_CLIENT_TYPE = "client_type";
    private WebView webView;
    private String mUrl;
    private String mBackUrl;
    private TextView tvTitle;
    private String mUserId;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.javascript);
        initData();
        initView();
    }

    private void initData() {
        mUserId = sp.getString("Encrypt_UID", "");
        mUrl = Uri.parse(IpConfig.getIp6()).buildUpon()
                .appendPath("product-car-app")
                .appendPath("index.html")
                .appendQueryParameter(PARAM_USER_ID, mUserId)
                .appendQueryParameter(PARAM_CLIENT_TYPE, "android")
                .build()
                .toString();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        RelativeLayout rlShare = (RelativeLayout) findViewById(R.id.rl_share);
        tvTitle.setText(R.string.activity_car_insurance_web_title);
        rlShare.setVisibility(View.INVISIBLE);
        rlBack.setOnClickListener(this);

        initWebView();
    }

    private void initWebView() {
        webView = (WebView) this.findViewById(R.id.wv_protocol);
        WebSettings setting = webView.getSettings();
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setDomStorageEnabled(true);
        setting.setAllowFileAccess(true);

        // API21以上，在webview里面从https访问http时候会被block
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setting.setAllowUniversalAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = webView.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(webView.getSettings(), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        webView.addJavascriptInterface(new JumpToOtherInterface(
                CXTBWebActivity.this), "mall");
        webView.addJavascriptInterface(new BackInterface(
                CXTBWebActivity.this), "back");
        webView.addJavascriptInterface(new JsInterface(), "android"); // window.Title.getTitle(String title);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url != null) {
                    if (url.contains("cxpay_success")) {
                        WXPayEntryActivity.actionStart(CXTBWebActivity.this, 0);
                    } else if (url.contains("cxpay_fail")) {
                        WXPayEntryActivity.actionStart(CXTBWebActivity.this, 1);
                    }
                }
                super.onPageStarted(view, url, favicon);
            }
        });
        webView.loadUrl(mUrl);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_back) {
            if (!TextUtils.isEmpty(mBackUrl)) {
                webView.loadUrl(mBackUrl);
            } else {
                finish();
            }
        }
    }

    private class JumpToOtherInterface {
        Context mContext;

        JumpToOtherInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void orderlist() {
            MyOrderListActivity.activityStart(CXTBWebActivity.this, true);
            finish();
        }
    }

    private class BackInterface {
        Context mContext;

        BackInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void previous_page() {
            EventBus.getDefault().post(new DisorderJumpEvent(0));
            Intent intent1 = new Intent(CXTBWebActivity.this, AllPageActivity.class);
            startActivity(intent1);
        }
    }

    private class JsInterface {
        @JavascriptInterface
        public void getWebTitle(final String title) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTitle.setText(title);
                }
            });
        }

        @JavascriptInterface
        public void getBackUrl(String url) {
            mBackUrl = url;
        }
    }
}