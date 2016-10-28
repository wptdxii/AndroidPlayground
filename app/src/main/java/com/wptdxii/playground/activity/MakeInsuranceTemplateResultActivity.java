package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.cloudhome.utils.IpConfig;

public class MakeInsuranceTemplateResultActivity extends BaseActivity {
	private RelativeLayout iv_back;
	private TextView top_title;
	private RelativeLayout rl_right;
	private String url="";
	private String resultLogo,resulTitle,resultDescription,forward_url;
	private String shareTitle,shareUrl,shareDesc,shareLogo;

	private WebView wbTemplate;
	private int x;
	private String user_id="";
	private String token="";
	private String user_id_encode="";
	//头像信息
	private String avatar;
	private String truename;
	//
	private BaseUMShare share;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_template_result);

		avatar = sp.getString("avatar", "");
		truename = sp.getString("truename", "");
		Intent intent=getIntent();
		url=intent.getStringExtra("url");

		resultLogo=shareLogo=intent.getStringExtra("logourl");
		resulTitle=shareTitle=intent.getStringExtra("title");
		resultDescription=shareDesc=intent.getStringExtra("description");
		forward_url=shareUrl=intent.getStringExtra("forward_url");
	
		resultLogo=resultLogo;
		forward_url=forward_url;
		url=url+"&"+IpConfig.getCommon();
		
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		Log.i("resultLogo-------", resultLogo);
		Log.i("forward_url------", forward_url);
		user_id_encode=sp.getString("Login_UID_ENCODE", "");
		init();
		share=new BaseUMShare(this,resulTitle,resultDescription,forward_url,resultLogo);
	}


	@SuppressLint("SetJavaScriptEnabled") private void init() {
		// TODO Auto-generated method stub
		iv_back=(RelativeLayout) findViewById(R.id.iv_back);
		top_title= (TextView) findViewById(R.id.tv_text);
		rl_right=(RelativeLayout) findViewById(R.id.rl_right);
		top_title.setText("预览建议书");

         wbTemplate=(WebView)findViewById(R.id.wb_make_insurance_template);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
					finish();
			}
		});
		rl_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				share.openShare();
			}
		});
		
		
		WebSettings setting = wbTemplate.getSettings();
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

	    wbTemplate.setWebChromeClient(new WebChromeClient() {
			public boolean onJsAlert(WebView view, String url, String message,
									 JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});

		wbTemplate.setWebViewClient(new InterceptingWebViewClient(this, wbTemplate, true));


		Log.i("wbTemplate.loadUrl(-", url);
		
		wbTemplate.setDownloadListener(new MyWebViewDownLoadListener());
	    wbTemplate.loadUrl(url);
	    wbTemplate.addJavascriptInterface(new phoneInterface(
				MakeInsuranceTemplateResultActivity.this), "microshare");
	    wbTemplate.addJavascriptInterface(new jumpInterface(
	    		MakeInsuranceTemplateResultActivity.this), "topicshare");
		wbTemplate.addJavascriptInterface(new clauseInterface(
				MakeInsuranceTemplateResultActivity.this), "productclause");
		wbTemplate.addJavascriptInterface(new microstationInterface(
				MakeInsuranceTemplateResultActivity.this), "microstation");
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
			intent.setData(Uri.parse("tel:" + num));
			Log.i("startPhone------", num);
			startActivity(intent);
		}
	}

	//产品条款
	public class clauseInterface {
		Context mContext;
		clauseInterface(Context Context) {
			mContext = Context;
		}
		@JavascriptInterface
		public void startClause(String address) {
			Intent intent = new Intent(MakeInsuranceTemplateResultActivity.this,MakeInsuranceWebView.class);
			//临时测试前缀地址
			//正式
//			address="http://www.baokeyun.com:8080/M1"+address;
			//测试
			address=IpConfig.getIp4()+address;

			intent.putExtra("url",address);
			intent.putExtra("isShare",false);
			intent.putExtra("title","产品条款");
			startActivity(intent);
			Log.i("startClause------", address);
		}
	}

	//进入微站
	public class microstationInterface {
		Context mContext;
		microstationInterface(Context Context) {
			mContext = Context;
		}
		@JavascriptInterface
		public void enterMicroStation(String address) {
			Log.d("enterMicroStation------",address);


			String url = IpConfig.getIp()+ address;




			String shareurl = IpConfig.getIp() + "user_id=" + user_id_encode+"&token="+token+ "&mod=getHomepageForExpert";
			Intent intent = new Intent();
			String img_url;
			if(TextUtils.isEmpty(avatar)){
				String site_url = IpConfig.getIp3();
				img_url = site_url + "/images/homepage_share.jpg";
			}else{
				img_url =avatar;
			}
			if(TextUtils.isEmpty(truename)){
				intent.putExtra("share_title", "资深保险销售专家——保险人");
				intent.putExtra("brief", "您好，我是保险人。愿意为您提供所有保险相关的服务。");
			}else{
				intent.putExtra("share_title", "资深保险销售专家——"+truename);
				intent.putExtra("brief", "您好，我是"+truename+"。愿意为您提供所有保险相关的服务。");
			}
			intent.putExtra("title", "我的微站");

			intent.putExtra("url", url);
			intent.putExtra("shareurl", shareurl);
			intent.putExtra("img_url", img_url);

			intent.setClass(MakeInsuranceTemplateResultActivity.this, MicroShareWebActivity.class);
			startActivity(intent);
		}
	}
	
	public class jumpInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		jumpInterface(Context Context) {
			mContext = Context;
		}

		@JavascriptInterface
		public void jump() {

			String url = IpConfig.getIp()+ "user_id=" + user_id_encode+"&token="+token+ "&mod=getHomepageForExpert";





			String shareurl = IpConfig.getIp() + "user_id=" + user_id_encode+"&token="+token+ "&mod=getHomepageForExpert";
			Intent intent = new Intent();
			String img_url;
			if(TextUtils.isEmpty(avatar)){
				String site_url = IpConfig.getIp3();
				img_url = site_url + "/images/homepage_share.jpg";
			}else{
				img_url =avatar;
			}
			if(TextUtils.isEmpty(truename)){
				intent.putExtra("share_title", "资深保险销售专家——保险人");
				intent.putExtra("brief", "您好，我是保险人。愿意为您提供所有保险相关的服务。");
			}else{
				intent.putExtra("share_title", "资深保险销售专家——"+truename);
				intent.putExtra("brief", "您好，我是"+truename+"。愿意为您提供所有保险相关的服务。");
			}
			intent.putExtra("title", "我的微站");

			intent.putExtra("url", url);
			intent.putExtra("shareurl", shareurl);
			intent.putExtra("img_url", img_url);

			intent.setClass(MakeInsuranceTemplateResultActivity.this, MicroShareWebActivity.class);
			startActivity(intent);

		}

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
		share.reSetShareContent(resulTitle,resultDescription,forward_url,resultLogo);
	}
}
