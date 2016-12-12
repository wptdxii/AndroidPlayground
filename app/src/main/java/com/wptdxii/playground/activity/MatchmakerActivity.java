package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.Statistics;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.IpConfig;
import com.umeng.socialize.utils.Log;

public class MatchmakerActivity extends BaseActivity implements NetResultListener {
    private WebView webView;
    // private Button button;
    private String url, title;
    private LinearLayout js_linear;
    private String position;
    private RelativeLayout jsback;
    private TextView tv_text;
    private RelativeLayout rl_right;
    private String shareTitle = "这一刻  拉近我们的距离";
    private String shareDesc = "小清新还是文艺范？阳光宅男或职场达人？告诉你不一样的Ta";
    private String shareLogoUrl = IpConfig.getIp3() + "/images/personas_share.png";
    private String loginString;
    private String user_id = "";
    private String token = "";
    private int x;
    private int y;
    private int movtionx;
    private int movtiony;
    private Statistics statistics=new Statistics(MatchmakerActivity.this);
    private BaseUMShare share;
    private String user_id_encode="";

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_maker_web);


        loginString = sp.getString("Login_STATE", "none");
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        title = intent.getStringExtra("title");
        js_linear = (LinearLayout) findViewById(R.id.js_linear);

        jsback = (RelativeLayout) findViewById(R.id.iv_back);
        tv_text = (TextView) findViewById(R.id.tv_text);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);

            tv_text.setText(title);

        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        rl_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                share.openShare();
                statistics.execute("personas_share");
            }
        });

        webView = (WebView) this.findViewById(R.id.webView);
        // button = (Button) this.findViewById(R.id.button);
        webView.setHorizontalScrollBarEnabled(false);
        WebSettings setting = webView.getSettings();

        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        // setting.setUseWideViewPort(true);
        // setting.setLoadWithOverviewMode(true);
        //
        setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        setting.setLoadWithOverviewMode(true);

        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);
        webView.destroyDrawingCache();

        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir()
                .getAbsolutePath();
        setting.setAppCachePath(appCachePath);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);


        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        if (!loginString.equals("none")) {

            user_id = sp.getString("Login_UID", "");
            token = sp.getString("Login_TOKEN", "");
            user_id_encode=sp.getString("Login_UID_ENCODE", "");
            url = url + "user_id=" + user_id_encode+"&token="+token;

        }



        if (url != null && url.contains("?")) {

            url = url + "&" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        }else{
            url = url + "?" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        }
        webView.setWebViewClient(new WebViewClient());
//
//		if(url.contains("gateway")) {
//			webView.setWebViewClient(new InterceptingWebViewClient(this, webView));
//		}else {
//			url=url+"&"+ IpConfig.getCommon()+ "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;
//			webView.setWebViewClient(new WebViewClient());
//		}

        Log.d("url", url);
        webView.loadUrl(url);

        webView.addJavascriptInterface(new phoneInterface(
                MatchmakerActivity.this), "microshare");

        share=new BaseUMShare(this,shareTitle,shareDesc,url,shareLogoUrl);
    }


    // 禁止webview左右滑动
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) ev.getX();
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
    public void ReceiveData(int action, int flag, Object dataObj) {

    }

    public class phoneInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        phoneInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void startPhone(String num) {

            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + num));
            startActivity(intent);

        }

    }

}