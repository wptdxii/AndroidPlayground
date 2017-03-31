package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.listener.PermissionListener;

import java.lang.reflect.Method;

public class FeedbackWebActivity extends BaseActivity {

    private WebView webView;
    //private Button button;
    private String url;
    private RelativeLayout jsback;
    private RelativeLayout rl_right;
    private TextView jstext;
    private static final String TAG = "FeedbackWebActivity";

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_web);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        jsback = (RelativeLayout) findViewById(R.id.rl_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_share);
        rl_right.setVisibility(View.INVISIBLE);
        jstext = (TextView) findViewById(R.id.tv_title);
        jstext.setText("意见反馈");
        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        webView = (WebView) this.findViewById(R.id.wv_protocol);
        //	button = (Button) this.findViewById(R.id.button);
        WebSettings setting = webView.getSettings();
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDomStorageEnabled(true);

        // 解决WebView跨域问题
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

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

        });

        webView.addJavascriptInterface(new phoneInterface(
                FeedbackWebActivity.this), "microshare");

        webView.loadUrl(url);
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

}