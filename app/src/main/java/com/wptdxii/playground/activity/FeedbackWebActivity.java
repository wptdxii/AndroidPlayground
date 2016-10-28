package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.zhy.http.okhttp.cookie.SimpleCookieJar;

import java.util.List;

import okhttp3.Cookie;

public class FeedbackWebActivity extends BaseActivity {

	private WebView webView;
	//private Button button;
	private String url;
    private RelativeLayout jsback;
    private RelativeLayout rl_right;
    private TextView jstext;
   
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_web);
	
		
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
	
        jsback=(RelativeLayout) findViewById(R.id.iv_back);
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		jstext= (TextView) findViewById(R.id.tv_text);
		jstext.setText("意见反馈");
        jsback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 finish();
				
			}
		});
		webView = (WebView) this.findViewById(R.id.webView);
	//	button = (Button) this.findViewById(R.id.button);
		WebSettings setting = webView.getSettings();
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

		synCookies(FeedbackWebActivity.this, url);




		webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));
		webView.loadUrl(url);
//		// 增加接口方法,让html页面调用
//		webView.addJavascriptInterface(new Object() {
//			// 这里我定义了一个拨打的方法
//			
//			@SuppressWarnings("unused")
//			public void startPhone(String num) {
//				Intent intent = new Intent();
//
//				intent.setAction(Intent.ACTION_CALL);
//				intent.setData(Uri.parse("tel:" + num));
//				startActivity(intent);
//			}
//
//			// 这里我定义了一个拨打的方法
//			@SuppressLint("JavascriptInterface")
//			public void startPhone2(String num) {
//				Intent intent = new Intent();
//
//				intent.setAction(Intent.ACTION_CALL);
//				intent.setData(Uri.parse("tel:" + num));
//				startActivity(intent);
//			}
//
//		}, "microshare");

		webView.addJavascriptInterface(new phoneInterface(
				FeedbackWebActivity.this), "microshare");
	
	}

	public class phoneInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		phoneInterface(Context Context) {
			mContext = Context;
		}

		@JavascriptInterface
		public void startPhone(String num) {

			Intent intent = new Intent();

			intent.setAction(Intent.ACTION_CALL);
			Log.d("tel:", num+"");
			intent.setData(Uri.parse("tel:" + num));
			startActivity(intent);
		
		}

	
	
		
	}

	

	  public void synCookies(Context context, String url) {
	        CookieSyncManager.createInstance(context);
	        CookieManager cookieManager = CookieManager.getInstance();
	        cookieManager.setAcceptCookie(true);  
//	      cookieManager.removeSessionCookie();// 移除  
	          
	        cookieManager.removeAllCookie();  
	     //   String[] cookie = mCookieStr.split(";");  
	        
	   //     Cookie[] cookie  = CookieUtil.getCookies().toArray(
		//			new Cookie[CookieUtil.getCookies().size()]);
	      
   List<Cookie> cookies=SimpleCookieJar.getCookies();


	        
	        StringBuffer sb = new StringBuffer();
	        
	        
	        for ( Cookie cookie : cookies)
	        {
	        	 
				String cookieName = cookie.name();
				String cookieValue = cookie.value();
				if (!TextUtils.isEmpty(cookieName)
						&& !TextUtils.isEmpty(cookieValue)) {
					sb.append(cookieName).append("=");
					sb.append(cookieValue).append(";");
				}
	        }
			
			String[] cookie = sb.toString().split(";");
	        for (int i = 0; i < cookie.length; i++) {  
	        	  Log.d("cookie[i]",cookie[i]);
	            cookieManager.setCookie(url, cookie[i]);// cookies是在HttpClient中获得的cookie  
	        }  
	  
	  
	        CookieSyncManager.getInstance().sync();
	    } 


}