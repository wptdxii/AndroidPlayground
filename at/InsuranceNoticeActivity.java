package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.cloudhome.R;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.cloudhome.utils.IpConfig;
public class InsuranceNoticeActivity extends BaseActivity {
	
	private ImageView hot_item_back;
	private WebView insurance_notice;
	private String web_url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.insurance_notice);
		Intent intent=getIntent();
		web_url=intent.getStringExtra("web_url");
		web_url=IpConfig.getIp3()+"/"+web_url;
		init();
	}
	
	@SuppressLint("SetJavaScriptEnabled") void init() {
		
		
		

     
		
		hot_item_back = (ImageView) findViewById(R.id.hot_item_back);
		insurance_notice=(WebView)findViewById(R.id.wb_insurance_notice);
		hot_item_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});
		
		WebSettings setting = insurance_notice.getSettings();
		// 设置支持javascript
		setting.setJavaScriptEnabled(true);
		setting.setUseWideViewPort(true); 
	    setting.setLoadWithOverviewMode(true); 
	    
		
	    setting.setDomStorageEnabled(true);   
	    setting.setAppCacheMaxSize(1024*1024*8);  
	    String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();  
	    setting.setAppCachePath(appCachePath);  
	    setting.setAllowFileAccess(true);  
	    setting.setAppCacheEnabled(true); 
		insurance_notice.setWebChromeClient(new WebChromeClient() {
			public boolean onJsAlert(WebView view, String url, String message,
									 JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});


		insurance_notice.setWebViewClient(new InterceptingWebViewClient(this, insurance_notice, true));


		
		insurance_notice.loadUrl(web_url);
	

	}

}
