package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.umeng.socialize.UMShareAPI;

public class MakeInsuranceWebView extends BaseActivity implements View.OnClickListener{
    private RelativeLayout rl_back;
    private RelativeLayout iv_right;
    private TextView tv_title;
    private WebView webView;

    private String title="";
    private String webUrl;
    private boolean isShare;
    private int type;//区分进入微站2，产品条款1

    private String resultLogo="";//分享出去图标的url
    private String forward_url="";//分享出去的url
    private String resultDescription="";//分享出去的描述信息
    private String resulTitle="";//分享出去的标题
    private BaseUMShare share;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_make_insurance_web_view);
        Intent intent=getIntent();
        isShare=intent.getBooleanExtra("isShare", false);
        webUrl=intent.getStringExtra("url");
        Log.i("webUrl--------", webUrl);
        title=intent.getStringExtra("title");
        initView();
        initWebView();
    }


    private void initView() {
        rl_back= (RelativeLayout) findViewById(R.id.rl_back);
        iv_right=(RelativeLayout) findViewById(R.id.rl_share);
        tv_title= (TextView) findViewById(R.id.tv_title);
        webView= (WebView) findViewById(R.id.wb_make_insurance_template);
        rl_back.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        tv_title.setText(title);



        if(isShare){
            iv_right.setVisibility(View.VISIBLE);
            share=new BaseUMShare(this,resulTitle,resultDescription,forward_url,resultLogo);
        }else{
            iv_right.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings setting = webView.getSettings();
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

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.setDownloadListener(new MyWebViewDownLoadListener());


        webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));



        Log.i("webView---------", webUrl);
        webView.loadUrl(webUrl);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_share:
                share.openShare();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}
