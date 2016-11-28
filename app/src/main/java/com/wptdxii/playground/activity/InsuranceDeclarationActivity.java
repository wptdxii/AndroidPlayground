package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
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

public class InsuranceDeclarationActivity extends BaseActivity {
	private ImageView hot_item_back;
	private WebView wb_insurance_declaration;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_insurance_declaration);
		url=IpConfig.getIp3()+"/templates/steward/rec_statement.tpl.php";
		init();
	}
	
	@SuppressLint("SetJavaScriptEnabled") void init() {
	

          
		hot_item_back = (ImageView) findViewById(R.id.hot_item_back);
		wb_insurance_declaration=(WebView)findViewById(R.id.wb_insurance_declaration);
		hot_item_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});
		
		WebSettings setting = wb_insurance_declaration.getSettings();
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

	    wb_insurance_declaration.setWebChromeClient(new WebChromeClient() {
			public boolean onJsAlert(WebView view, String url, String message,
									 JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});

		wb_insurance_declaration.setWebViewClient(new InterceptingWebViewClient(this, wb_insurance_declaration, true));

		
	    wb_insurance_declaration.loadUrl(url);
	

	}

}
