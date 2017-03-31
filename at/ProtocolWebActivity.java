package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProtocolWebActivity extends BaseActivity {
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_URL = "url";
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.wv_protocol)
    WebView wvProtocol;

    public static void activityStart(Context context, String title, String url) {
        Intent intent = new Intent(context, ProtocolWebActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol_web);
        ButterKnife.bind(this);
        init();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void init() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String url = intent.getStringExtra(EXTRA_URL);

        tvTitle.setText(title);
        rlShare.setVisibility(View.INVISIBLE);

        WebSettings setting = wvProtocol.getSettings();
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDomStorageEnabled(true);
        wvProtocol.setWebChromeClient(new WebChromeClient());
        wvProtocol.setWebViewClient(new WebViewClient());
        wvProtocol.loadUrl(url);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }
}