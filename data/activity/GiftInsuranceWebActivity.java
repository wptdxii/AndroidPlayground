package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.IpConfig;
/**
 * Created by yangguangbaoxian on 2016/6/6.
 */
public class GiftInsuranceWebActivity extends BaseActivity {




    private RelativeLayout iv_back;
    private TextView top_title;
    private RelativeLayout rl_right;
    private ImageView iv_right;
    private ImageView iv_pic;
    int x;

    private String title;
    private String url="";

    private WebView wb_view;
    private static Context myContext;
    private String loginString;

    private String user_id = "";
    private String token;
    private String user_id_encode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gift_insurance_web);
        Intent intent=getIntent();
        url=intent.getStringExtra("web_address");
        title=intent.getStringExtra("title");
        myContext=this;


        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        Intent backIntent=new Intent();
        setResult(210,backIntent);
        init();
        initData();
    }



    @SuppressLint("SetJavaScriptEnabled") private void init() {
        // TODO Auto-generated method stub
        iv_back=(RelativeLayout) findViewById(R.id.iv_back);
        top_title= (TextView) findViewById(R.id.tv_text);
        top_title.setText(title);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        iv_right=(ImageView)findViewById(R.id.iv_right);
        iv_pic=(ImageView)findViewById(R.id.iv_pic);
        iv_right.setBackgroundResource(R.drawable.share);
        iv_pic.setImageResource(R.drawable.icon_cancel);
        wb_view=(WebView)findViewById(R.id.wb_view);
        rl_right.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        WebSettings setting = wb_view.getSettings();
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

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
       // wb_view.setWebViewClient(new InterceptingWebViewClient(this, wb_view, true));
        wb_view.setWebViewClient(new WebViewClient());
        wb_view.setDownloadListener(new MyWebViewDownLoadListener());
        wb_view.addJavascriptInterface(new JumpToOtherInterface(GiftInsuranceWebActivity.this), "mall");
        wb_view.addJavascriptInterface(new BackInterface(GiftInsuranceWebActivity.this), "back");
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
//            synCookies(GiftInsuranceWebActivity.this, url);
        }
        wb_view.loadUrl(url);
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
        /** Instantiate the interface and set the context */
        JumpToOtherInterface(Context Context) {
            mContext = Context;
        }



        @JavascriptInterface
        public void orderlist()
        {
            Intent intent = new Intent();
            intent.setClass(GiftInsuranceWebActivity.this, MyOrderListActivity.class);
            intent.putExtra("needDisorderJump",true);
            GiftInsuranceWebActivity.this.startActivity(intent);

//            if (ZhanYeActivity.ZhanYeinstance != null) {
//
//                ZhanYeActivity.ZhanYeinstance.finish();
//            }
//            if(ZhanYeHuoDongActivity.zhanYeHuoDongActivityInstance!=null){
//                ZhanYeHuoDongActivity.zhanYeHuoDongActivityInstance.finish();
//            }
//
//            if (GiftInsuranceActivity.GiftInsuranceinstance != null) {
//
//                GiftInsuranceActivity.GiftInsuranceinstance.finish();
//            }


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
            finish();
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
//        List<Cookie> cookies= SimpleCookieJar.getCookies();
//
//
//
//        StringBuffer sb = new StringBuffer();
//
//
//        for ( Cookie cookie : cookies)
//        {
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
