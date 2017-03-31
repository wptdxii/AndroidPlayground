package com.cloudhome.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.InterceptingWebViewClient;

public class JPushWebActivity extends BaseActivity {


    private WebView webView;
    //private Button button;
    private String url, title;
    private RelativeLayout jsback;
    private RelativeLayout rl_right;
    private TextView jstext;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.javascript);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");

        jstext = (TextView) findViewById(R.id.tv_title);
        jstext.setText(title);
        jsback = (RelativeLayout) findViewById(R.id.rl_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_share);
        rl_right.setVisibility(View.INVISIBLE);
        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Editor editor4 = sp4.edit();

                editor4.putInt("page", 0);

                editor4.commit();

                Intent intent = new Intent(JPushWebActivity.this,
                        AllPageActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


            }
        });
        webView = (WebView) this.findViewById(R.id.wv_protocol);
        //	button = (Button) this.findViewById(R.id.button);
        WebSettings setting = webView.getSettings();


        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));
        webView.loadUrl(url);
        // 增加接口方法,让html页面调用
        webView.addJavascriptInterface(new Object() {
            // 这里我定义了一个拨打的方法

            public void startPhone(String num) {
               requestCallPhonePermission(num);
            }

            // 这里我定义了一个拨打的方法
            @SuppressLint("JavascriptInterface")
            public void startPhone2(String num) {
                requestCallPhonePermission(num);
            }

        }, "demo");


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

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            Editor editor4 = sp4.edit();
            editor4.putInt("page", 0);
            editor4.commit();

            Intent intent = new Intent(JPushWebActivity.this,
                    AllPageActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();


        }
        return super.onKeyDown(keyCode, event);
    }




}