package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.IpConfig;

public class PolicyInfoWebActivity extends BaseActivity {

	private WebView webView;
	private String url;

    private RelativeLayout jsback;
    private RelativeLayout rl_right;
	private TextView tv_text;
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.policy_info_web);
		
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		Log.d("urlbefore",url);
		
        jsback=(RelativeLayout) findViewById(R.id.iv_back);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText("保单详情");
        jsback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(webView.canGoBack()){
					webView.goBack();
				}else{
					finish();
				}

				
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

		if (url != null && url.contains("?")) {

			url = url + "&" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

		}else{
			url = url + "?" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

		}
//		synCookies(PolicyInfoWebActivity.this, url);


		//webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));


		webView.setWebViewClient(new WebViewClient());
		Log.d("url",url);
		webView.loadUrl(url);
		// 增加接口方法,让html页面调用
		webView.addJavascriptInterface(new Object() {
			// 这里我定义了一个拨打的方法
			
			public void startPhone(String num) {
				Intent intent = new Intent();

				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + num));
				startActivity(intent);
			}

			// 这里我定义了一个拨打的方法
			@SuppressLint("JavascriptInterface")
			public void startPhone2(String num) {
				Intent intent = new Intent();

				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + num));
				startActivity(intent);
			}

		}, "demo");



	}


//
//	@SuppressWarnings("deprecation")
//	public void synCookies(Context context, String url) {
//	        CookieSyncManager.createInstance(context);
//	        CookieManager cookieManager = CookieManager.getInstance();
//	        cookieManager.setAcceptCookie(true);
//		   cookieManager.removeSessionCookie();// 移除
//
//	        cookieManager.removeAllCookie();
//
//
//		  //   String[] cookie = mCookieStr.split(";");
//
//	   //     Cookie[] cookie  = CookieUtil.getCookies().toArray(
//		//			new Cookie[CookieUtil.getCookies().size()]);
//
//	        List<Cookie>   cookies=SimpleCookieJar.getCookies();
//
//
//		//  HttpUrl httpurl = HttpUrl.parse(url);
//
//
//		//  List<Cookie> cookies =   SimpleCookieJar.getCookieList(httpurl);
//
//		  //List<Cookie> cookies =   MyApplication.getMyCookieStore().getCookies();
//
//	        StringBuffer sb = new StringBuffer();
//
//
//	        for ( Cookie cookie : cookies)
//	        {
//
//				String cookieName = cookie.name();
//				String cookieValue = cookie.value();
//				if (!TextUtils.isEmpty(cookieName)
//						&& !TextUtils.isEmpty(cookieValue)) {
//					sb.append(cookieName).append("=");
//					sb.append(cookieValue).append(";");
//				}
//	        }
//
//
//			String[] cookie = sb.toString().split(";");
//	        for (int i = 0; i < cookie.length; i++) {
//	        	  Log.d("cookie____",cookie[i].toString());
//	            cookieManager.setCookie(url, cookie[i].toString());// cookies是在HttpClient中获得的cookie
//	        }
//
//
//	        CookieSyncManager.getInstance().sync();
//	    }
//
//


}