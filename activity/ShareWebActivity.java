package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.IpConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;
public class ShareWebActivity extends BaseActivity {

    private WebView webView;
    // private Button button;
    private String url, title, imgstr, img_url, share_title, brief;
    private String position;
    private RelativeLayout jsback;
    private TextView jstext;
    private RelativeLayout js_share;
    private String site_url;
    private String user_id = "";
    private String token;
    private String loginString;
    private String is_share;
    private String user_id_encode="";

    private BaseUMShare share;

    private Handler RedHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String rp_id = (String) msg.obj;
            url = IpConfig.getIp() + "mod=checkRedpackage&redpackage_id="
                    + rp_id;
            share_title = "保客福利大放送，送钱开抢啦";
            brief = "优惠劵什么的都靠边站吧，保客只送现金哒~手快有，手慢无";
            img_url = IpConfig.getIp3() + "/images/rp_share.png";
            Log.i("分享红包--------------", url + "---" + share_title + "---"
                    + brief + "---" + img_url);
            share.reSetShareContent(share_title,brief,url,img_url);
            share.openShare();
        }

    };

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.javascript);



        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        token = sp.getString("Login_TOKEN", "");

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        imgstr = intent.getStringExtra("img");
        is_share = intent.getStringExtra("is_share");
        share_title = intent.getStringExtra("share_title");
        brief = intent.getStringExtra("brief");
        jstext = (TextView) findViewById(R.id.tv_text);

        img_url = IpConfig.getIp3() + intent.getStringExtra("img");
        Log.i("获取到的图片地址img_url------------", img_url);
            jstext.setText(title);
        share=new BaseUMShare(this,share_title,brief,url,img_url);

        jsback = (RelativeLayout) findViewById(R.id.iv_back);
        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        js_share = (RelativeLayout) findViewById(R.id.rl_right);
        js_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                url = webView.getUrl();
                share.openShare();

            }
        });

        if (url != null && url.contains("?")) {

            url = url + "&" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        }else{
            url = url + "?" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        }

        if (is_share.equals("0")) {
            js_share.setVisibility(View.INVISIBLE);

            if (!loginString.equals("none")) {
                url = url + "token=" + token + "&user_id=" + user_id_encode;
                Log.d("9999999", url);
//                synCookies(ShareWebActivity.this, url);

            }

        } else if (is_share.equals("1")) {
            js_share.setVisibility(View.VISIBLE);
        }

        webView = (WebView) this.findViewById(R.id.webView);
        // button = (Button) this.findViewById(R.id.button);
        WebSettings setting = webView.getSettings();

        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);


        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });


       // webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        // 增加接口方法,让html页面调用
        webView.addJavascriptInterface(new Object() {
            // 这里我定义了一个拨打的方法

            public void startPhone(String num) {
                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + num));
                startActivity(intent);
            }

            // 这里我定义了一个拨打的方法
            @SuppressLint("JavascriptInterface")
            public void startPhone2(String num) {
                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + num));
                startActivity(intent);
            }

        }, "demo");
        // JS调用红包
        webView.addJavascriptInterface(new redPackageInterface(
                ShareWebActivity.this), "redPackage");

    }


//    @SuppressWarnings("deprecation")
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
//            Log.d("cookie[i]", cookie[i]);
//            cookieManager.setCookie(url, cookie[i]);// cookies是在HttpClient中获得的cookie
//        }
//
//        CookieSyncManager.getInstance().sync();
//    }

    public class redPackageInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        redPackageInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void shareRedPackage(String rp_id) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = rp_id;
            RedHandler.sendMessage(msg);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}