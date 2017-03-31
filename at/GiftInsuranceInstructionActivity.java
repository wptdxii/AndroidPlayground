package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.utils.InterceptingWebViewClient;

import static com.cloudhome.R.drawable.completed;

public class GiftInsuranceInstructionActivity extends BaseActivity {



    private RelativeLayout iv_back;
    private TextView top_title;
    private RelativeLayout rl_right;
    private ImageView iv_right;
    int x;

    private boolean needShare;
    private String title;
    private String url="";

    private WebView wb_view;
    private String user_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gift_insurance_instruction);

        user_id = sp.getString("Login_UID", "");

        Intent intent=getIntent();
        url=intent.getStringExtra("web_address");
        title=intent.getStringExtra("title");
        init();
        initData();
    }



    @SuppressLint("SetJavaScriptEnabled") private void init() {
        // TODO Auto-generated method stub
        iv_back=(RelativeLayout) findViewById(R.id.rl_back);
        top_title= (TextView) findViewById(R.id.tv_title);
        top_title.setText(title);
        rl_right=(RelativeLayout) findViewById(R.id.rl_share);
        iv_right=(ImageView)findViewById(R.id.iv_share);
        iv_right.setBackgroundResource(R.drawable.share);
        wb_view=(WebView)findViewById(R.id.wb_view);
        rl_right.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(wb_view.canGoBack()){
                    wb_view.goBack();
                }else{
                    finish();
                }
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
        wb_view.setWebViewClient(new InterceptingWebViewClient(this, wb_view, true));
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

}
