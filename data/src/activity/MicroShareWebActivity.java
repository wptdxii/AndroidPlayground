package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.umeng.socialize.UMShareAPI;

public class MicroShareWebActivity extends BaseActivity implements NetResultListener {

    private WebView webView;
    private String url, title, img_url, share_title, brief = "";
    private RelativeLayout jsback;
    private TextView jstext;
    private RelativeLayout js_share;

    private String share_url;
    //统计接口
    private Statistics statistics = new Statistics(this);
    private boolean needShare = true;
    private BaseUMShare share;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.micro_share);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        //是否需要分享，从我的辖下进来不需要分享
        needShare = intent.getBooleanExtra("needShare", true);
        if (needShare) {
            share_url = intent.getStringExtra("shareurl");
            share_title = intent.getStringExtra("share_title");
            brief = intent.getStringExtra("brief");
            img_url = intent.getStringExtra("img_url");
            share = new BaseUMShare(this, share_title, brief, share_url, img_url);
        }

        jstext = (TextView) findViewById(R.id.tv_text);


        jstext.setText(title);


        jsback = (RelativeLayout) findViewById(R.id.iv_back);
        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        js_share = (RelativeLayout) findViewById(R.id.rl_right);
        if (needShare) {
            js_share.setVisibility(View.VISIBLE);
        } else {
            js_share.setVisibility(View.INVISIBLE);
        }
        js_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                share.openShare();
                statistics.execute("homepage_share");
            }
        });
        webView = (WebView) this.findViewById(R.id.video_webview);
        // button = (Button) this.findViewById(R.id.button);
        WebSettings setting = webView.getSettings();

        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        // setting.setUseWideViewPort(true);
        // setting.setLoadWithOverviewMode(true);
        //
        setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        setting.setLoadWithOverviewMode(true);
        setting.setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));
        Log.i("weizhan", url);
        webView.loadUrl(url);

        webView.addJavascriptInterface(new phoneInterface(
                MicroShareWebActivity.this), "microshare");

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
            requestCallPhonePermission(num);
        }

    }

    private void requestCallPhonePermission(final String mobile) {
        String[] permissions = new String[]{android.Manifest.permission.CALL_PHONE};
        requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mobile));
                startActivity(intent);
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                showRequestPermissionRationale(
                        getString(R.string.msg_callphone_denied));
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                showPermissionSettingDialog(
                        getString(R.string.msg_callphone_permanent_denied));
            }
        });
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

}