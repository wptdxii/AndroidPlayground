package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
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
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.cookie.SimpleCookieJar;
import com.zhy.http.okhttp.utils.MyInterceptor;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Cookie;

public class CommonWebActivity extends BaseActivity {


private RelativeLayout iv_back;
    private TextView top_title;
    private RelativeLayout rl_right;
    int x;

    private String shareTitle,shareUrl,shareDesc, shareLogo;
    private boolean needShare;
    private String title;
    private String url="";


    private WebView wb_view;

    private String loginString;
    private String user_id = "";
    private String token;
    private String user_id_encode="";
    private Boolean RefreshBoolean= false;
    public static CommonWebActivity HomeCommonWebinstance=null;
    private boolean isGroup=false;
    private BaseUMShare share;
    private String Event_Share = "CommmonWebActivity_Share";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common_web);


        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        HomeCommonWebinstance =this;

        Intent intent=getIntent();
        needShare=intent.getBooleanExtra("needShare", false);
        url=intent.getStringExtra("web_address");
        title=intent.getStringExtra("title");
        isGroup=intent.getBooleanExtra("isGroup",false);
        if(needShare){
            shareLogo=intent.getStringExtra("shareLogo");
            shareTitle=intent.getStringExtra("shareTitle");
            shareDesc=intent.getStringExtra("shareDesc");


            shareUrl=intent.getStringExtra("shareUrl");

            if ( url.contains("?")) {

                shareUrl = url + "&user_id="
                        + user_id_encode+"&token="+token;

            }else{
                shareUrl = url + "?user_id="
                        + user_id_encode+"&token="+token;

            }

            Log.i("shareLogoooo----",shareLogo);
        }
        init();
        initData();
    }



    @SuppressLint("SetJavaScriptEnabled") private void init() {
        // TODO Auto-generated method stub
        iv_back=(RelativeLayout) findViewById(R.id.iv_back);
        top_title= (TextView) findViewById(R.id.tv_text);
        top_title.setText(title);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        wb_view=(WebView)findViewById(R.id.wb_view);
        if(needShare){
            rl_right.setVisibility(View.VISIBLE);
            if ( url.contains("pingan_c")) {
                rl_right.setVisibility(View.GONE);
            }else{
                rl_right.setVisibility(View.VISIBLE);
                share=new BaseUMShare(this,shareTitle,shareDesc,shareUrl,shareLogo);
            }
        }else{
            rl_right.setVisibility(View.INVISIBLE);
        }



    }

    private void initData() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                    finish();

            }
        });
        rl_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                share.openShare();
                MobclickAgent.onEvent(CommonWebActivity.this, Event_Share);
            }
        });

        WebSettings setting = wb_view.getSettings();
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);


        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wb_view.clearCache(true);
        wb_view.destroyDrawingCache();

        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        setting.setAppCachePath(appCachePath);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);


        wb_view.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        wb_view.setDownloadListener(new MyWebViewDownLoadListener());
      //  wb_view.setWebViewClient(new InterceptingWebViewClient(this, wb_view, true));
        wb_view.addJavascriptInterface(new JumpToOtherInterface(
                CommonWebActivity.this), "mall");
        wb_view.addJavascriptInterface(new BackInterface(
                CommonWebActivity.this), "back");
        wb_view.addJavascriptInterface(new CardOrderPayInterface(
                CommonWebActivity.this), "cardOrder");



        wb_view.setWebViewClient(new WebViewClient());

        if (url != null && url.contains("?")) {


            url = url + "&" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        }else{
            url = url + "?" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;
        }


        if (!loginString.equals("none")) {
            url = url
                    + "&token="
                    + token
                    + "&user_id="
                    + user_id_encode;
            synCookies(CommonWebActivity.this, url);
        }



        wb_view.loadUrl(url);
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


    //禁止webview左右滑动
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x=(int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                ev.setLocation(x, ev.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }





    public class JumpToOtherInterface {
        Context mContext;
        /** Instantiate the interface and set the context */
        JumpToOtherInterface(Context Context) {
            mContext = Context;
        }



        @JavascriptInterface
        public void orderlist()
        {
            Intent intent = new Intent(CommonWebActivity.this,MyOrderListActivity.class);
            intent.putExtra("needDisorderJump",true);
            startActivity(intent);
            finish();


        }

    }
    public class BackInterface {
        Context mContext;
        /** Instantiate the interface and set the context */
        BackInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void previous_page() {
//            Intent intent = new Intent("cloudhome.disorder.jump");
//            intent.putExtra("page", 0);
//            sendBroadcast(intent);
            EventBus.getDefault().post(new DisorderJumpEvent(0));
            Intent intent1 = new Intent(CommonWebActivity.this,AllPageActivity.class);
            startActivity(intent1);
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

            RefreshBoolean = true;

            Intent intent = new Intent(CommonWebActivity.this, OrderPayActivity.class);

            intent.putExtra("title", productname);
            intent.putExtra("time", ordercreatetime);
            intent.putExtra("price", moneys);
            intent.putExtra("id", id);
            intent.putExtra("orderno", orderno);
            intent.putExtra("entrance", "HomeCommonWeb");
            startActivityForResult(intent,100);
        }

    }

    private void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        // cookieManager.removeSessionCookie();// 移除

        cookieManager.removeAllCookie();
        // String[] cookie = mCookieStr.split(";");

        // Cookie[] cookie = CookieUtil.getCookies().toArray(
        // new Cookie[CookieUtil.getCookies().size()]);

        List<Cookie> cookies= SimpleCookieJar.getCookies();



        StringBuffer sb = new StringBuffer();


        for ( Cookie cookie : cookies)
        {

            String cookieName = cookie.name();
            String cookieValue = cookie.value();
            if (!TextUtils.isEmpty(cookieName)
                    && !TextUtils.isEmpty(cookieValue)) {
                sb.append(cookieName).append("=");
                sb.append(cookieValue).append(";");
            }
        }

        String[] cookie = sb.toString().split(";");
        for (int i = 0; i < cookie.length; i++) {
            com.umeng.socialize.utils.Log.d("cookie[i]", cookie[i]);
            cookieManager.setCookie(url, cookie[i]);// cookies是在HttpClient中获得的cookie
        }
        CookieSyncManager.getInstance().sync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==200){
            if(isGroup){
                share.reSetShareContent(shareTitle,shareDesc,shareUrl,shareLogo);
                wb_view.loadUrl(url);
            }else{
                finish();
            }
            }
        }

}
