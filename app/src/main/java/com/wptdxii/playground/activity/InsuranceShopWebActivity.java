package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.IpConfig;


public class InsuranceShopWebActivity extends BaseActivity implements OnClickListener {

    public static InsuranceShopWebActivity InsuranceShopWebinstance = null;
    private RelativeLayout iv_back;
    private TextView top_title;
    private RelativeLayout rl_right;
    private String url = "";
    private String resulTitle = "";
    private String resultDescription = "";
    private String share_url = "";
    private String resultLogo = "";
    private WebView wb_insurance_picture;
    private String loginString;

    private String user_id = "";
    private String token;
    private BaseUMShare share;
    private String user_id_encode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.insurance_shop_picture_web);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        resulTitle = intent.getStringExtra("title");
        resultDescription = intent.getStringExtra("biref");
        resultLogo = intent.getStringExtra("img");


        InsuranceShopWebinstance = this;


        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");

        if (url.contains("?")) {

            share_url = url + "&user_id="
                    + user_id_encode+"&token="+token;

        } else {
            share_url = url + "?user_id="
                    + user_id_encode+"&token="+token;

        }


        init();
        share=new BaseUMShare(this,resulTitle,resultDescription,share_url,resultLogo);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);

        if (url.contains("pingan_c")) {

            rl_right.setVisibility(View.GONE);

        } else {
            rl_right.setVisibility(View.VISIBLE);
        }


        top_title.setText(resulTitle);

        rl_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                share.openShare();
            }
        });


        iv_back.setOnClickListener(this);
        wb_insurance_picture = (WebView) findViewById(R.id.wb_insurance_picture);


        WebSettings setting = wb_insurance_picture.getSettings();

        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);

        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wb_insurance_picture.clearCache(true);
        wb_insurance_picture.destroyDrawingCache();

        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        setting.setAppCachePath(appCachePath);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);

        wb_insurance_picture.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        wb_insurance_picture.setDownloadListener(new MyWebViewDownLoadListener());

        //wb_insurance_picture.setWebViewClient(new InterceptingWebViewClient(this, wb_insurance_picture, true));
        wb_insurance_picture.setWebViewClient(new WebViewClient());

        if (url != null && url.contains("?")) {

            url = url + "&" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        } else {
            url = url + "?" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        }

        if (!loginString.equals("none")) {
            url = url
                    + "&token="
                    + token
                    + "&user_id="
                    + user_id_encode;
//            synCookies(InsuranceShopWebActivity.this, url);
        }


        Log.i("88888----------", url);

        wb_insurance_picture.loadUrl(url);
        wb_insurance_picture.addJavascriptInterface(new JumpToPlanInterface(
                InsuranceShopWebActivity.this), "jumptoplan");


        wb_insurance_picture.addJavascriptInterface(new JumpToOtherInterface(
                InsuranceShopWebActivity.this), "mall");
        wb_insurance_picture.addJavascriptInterface(new BackInterface(
                InsuranceShopWebActivity.this), "back");
        wb_insurance_picture.addJavascriptInterface(new CardOrderPayInterface(
                InsuranceShopWebActivity.this), "cardOrder");

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:

                finish();
                break;
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

    public class JumpToPlanInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        JumpToPlanInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void jumpPlan(int id) {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.setClass(InsuranceShopWebActivity.this, MakeInsurancePlanActivity.class);
            InsuranceShopWebActivity.this.startActivity(intent);
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public class JumpToOtherInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        JumpToOtherInterface(Context Context) {
            mContext = Context;
        }


        @JavascriptInterface
        public void orderlist() {

//			Intent intent = new Intent();
//			intent.setClass(InsuranceShopWebActivity.this, MyOrderListActivity.class);
//			InsuranceShopWebActivity.this.startActivity(intent);


            if (InsuranceShopListActivity.InsuranceShopListinstance != null) {

                InsuranceShopListActivity.InsuranceShopListinstance.finish();
            }
            if (InsuranceShopSceneActivity.InsuranceShopSceneinstance != null) {
                InsuranceShopSceneActivity.InsuranceShopSceneinstance.finish();
            }


            SharedPreferences.Editor editor4 = sp4.edit();

            editor4.putInt("page", 4);

            editor4.commit();

            Intent intent = new Intent(InsuranceShopWebActivity.this,
                    MyOrderListActivity.class);
            startActivity(intent);
            finish();


        }

    }

    public class BackInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        BackInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void previous_page() {
            finish();
        }


    }

    public class CardOrderPayInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        CardOrderPayInterface(Context Context) {
            mContext = Context;
        }


        @JavascriptInterface
        public void pay(String productname, String ordercreatetime, String moneys, String id, String orderno) {

            Intent intent = new Intent(InsuranceShopWebActivity.this, OrderPayActivity.class);

            intent.putExtra("title", productname);
            intent.putExtra("time", ordercreatetime);
            intent.putExtra("price", moneys);
            intent.putExtra("id", id);
            intent.putExtra("orderno", orderno);
            intent.putExtra("entrance", "InsuranceShopWeb");

            InsuranceShopWebActivity.this.startActivity(intent);


        }

    }


}
