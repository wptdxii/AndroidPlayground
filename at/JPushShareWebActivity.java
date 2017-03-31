package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.cloudhome.utils.IpConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;
public class JPushShareWebActivity extends BaseActivity {

	private WebView webView;
	// private Button button;
	private String url, title, img_url, share_title, content;
	private LinearLayout js_linear;
	private String position;
	private RelativeLayout jsback;
	private TextView jstext;
	private RelativeLayout js_share;
	private String loginString;

	private String share_url;
	private String user_id="";
	private String token="";
	private BaseUMShare share;
	private String user_id_encode="";


	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.micro_share);

		loginString = sp.getString("Login_STATE", "none");
		Intent intent = getIntent();
		url = intent.getStringExtra("url");

		share_url = intent.getStringExtra("share_url");
		title = intent.getStringExtra("title");
		js_linear = (LinearLayout) findViewById(R.id.js_linear);
		share_title = intent.getStringExtra("share_title");
		content = intent.getStringExtra("content");
		img_url = intent.getStringExtra("img_url");
		
		jstext = (TextView) findViewById(R.id.tv_title);


			jstext.setText(title);


		jsback = (RelativeLayout) findViewById(R.id.rl_back);
		jsback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor4 = sp4.edit();
				editor4.putInt("page", 0);
				editor4.commit();

				Intent intent = new Intent(JPushShareWebActivity.this,
						AllPageActivity.class);

				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				

			}
		});
		js_share = (RelativeLayout) findViewById(R.id.rl_share);
		js_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share.openShare();
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

		if (!loginString.equals("none")) {
			user_id_encode=sp.getString("Login_UID_ENCODE", "");
			user_id = sp.getString("Login_UID", "");
			token = sp.getString("Login_TOKEN", "");
			share_url = share_url + "&user_id=" + user_id_encode+"&token="+token;
			url = url + "&user_id=" + user_id_encode+"&token="+token;
			
		}

		share=new BaseUMShare(this,share_title,content,share_url,img_url);

		Log.d("url", url);

		webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));
		webView.loadUrl(url);

		webView.addJavascriptInterface(new phoneInterface(
				JPushShareWebActivity.this), "microshare");

		webView.addJavascriptInterface(new jumpInterface(
				JPushShareWebActivity.this), "topicshare");
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

	public class phoneInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		phoneInterface(Context Context) {
			mContext = Context;
		}

		@JavascriptInterface
		public void startPhone(String num) {
			requestCallPhonePermission(num);
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

			String url = IpConfig.getIp()
					+ "user_id="
					+ user_id_encode+"&token="+token + "&mod=getHomepageForExpert";
			String shareurl = IpConfig.getIp() + "user_id=" + user_id_encode+"&token="+token
					+ "&mod=getHomepageForExpert";
			Intent intent = new Intent();

			String site_url = IpConfig.getIp3();

			String img_url = site_url + "/images/homepage_share.jpg";
			intent.putExtra("title", "微站分享效果图");

			intent.putExtra("share_title", "话语权缘于专业");
			intent.putExtra("url", url);
			intent.putExtra("shareurl", shareurl);
			intent.putExtra("img_url", img_url);
			intent.putExtra("brief", "保险行业的专家，我在这里等待着你。");
			intent.setClass(JPushShareWebActivity.this,
					MicroShareWebActivity.class);

			JPushShareWebActivity.this.startActivity(intent);

		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 如果是返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
		
			Editor editor4 = sp4.edit();
			editor4.putInt("page", 0);
			editor4.commit();

			Intent intent = new Intent(JPushShareWebActivity.this,
					AllPageActivity.class);

			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			
		
	
			
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

	}


}