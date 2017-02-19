package com.cloudhome.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
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
import com.cloudhome.network.okhttp.interceptor.MyInterceptor;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.wxapi.WXPayEntryActivity;

import org.greenrobot.eventbus.EventBus;

public class CXTBWebActivity extends BaseActivity {

    private WebView webView;


    private String url, title;

    private RelativeLayout jsback;
    private RelativeLayout rl_right;
    private TextView jstext;
    private String user_id_encode = "";


    private String user_id = "";
    private String token;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.javascript);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");


        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        if (url.contains("?")) {

            url += "&user_id="
                    + user_id_encode + "&token=" + token;

        } else {
            url += "?user_id="
                    + user_id_encode + "&token=" + token;

        }

        init();

    }

    private static final String TAG = "CXTBWebActivity";
    private void init() {

        jstext = (TextView) findViewById(R.id.tv_text);
        jstext.setText(title);
        jsback = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        webView = (WebView) this.findViewById(R.id.webView);

        WebSettings setting = webView.getSettings();


        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);
        webView.destroyDrawingCache();


        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        setting.setAppCachePath(appCachePath);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);
        //API21以上，在webview里面从https访问http时候会被block
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


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

//         webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url != null) {
                    if (url.contains("cxpay_success")) {

                        WXPayEntryActivity.actionStart(CXTBWebActivity.this,0);

                    } else if (url.contains("cxpay_fail")) {

                        WXPayEntryActivity.actionStart(CXTBWebActivity.this,1);
                    }
                }
                super.onPageStarted(view, url, favicon);
            }


        });


        url = url + "&" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;


//        synCookies(CXTBWebActivity.this, url);
        webView.loadUrl(url);


    }


    public class JumpToOtherInterface {
        Context mContext;
        JumpToOtherInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void orderlist() {
            Intent intent = new Intent(CXTBWebActivity.this, MyOrderListActivity.class);
            intent.putExtra("needDisorderJump", true);
            startActivity(intent);
            finish();
        }
    }
    public class BackInterface {
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

//    private void synCookies(Context context, String url) {
//        CookieSyncManager.createInstance(context);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        // cookieManager.removeSessionCookie();// 移除
//
//        cookieManager.removeAllCookie();
//        // String[] cookie = mCookieStr.split(";");
//
//        // Cookie[] cookie = CookieUtil.getCookies().toArray(
//        // new Cookie[CookieUtil.getCookies().size()]);
//
//        List<Cookie> cookies = SimpleCookieJar.getCookies();
//
//
//        StringBuffer sb = new StringBuffer();
//
//
//        for (Cookie cookie : cookies) {
//
//            String cookieName = cookie.name();
//            String cookieValue = cookie.value();
//            if (!TextUtils.isEmpty(cookieName)
//                    && !TextUtils.isEmpty(cookieValue)) {
//                sb.append(cookieName).append("=");
//                sb.append(cookieValue).append(";");
//            }
//        }
//
//        String[] cookie = sb.toString().split(";");
//        for (int i = 0; i < cookie.length; i++) {
//            com.umeng.socialize.utils.Log.d("cookie[i]", cookie[i]);
//            cookieManager.setCookie(url, cookie[i]);// cookies是在HttpClient中获得的cookie
//        }
//        CookieSyncManager.getInstance().sync();
//    }


}