package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.utils.GetIp;
import com.cloudhome.utils.IpConfig;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.utils.Log;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.SimpleCookieJar;
import com.zhy.http.okhttp.utils.MyInterceptor;

import net.sourceforge.simcpux.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Cookie;

@SuppressLint("SetJavaScriptEnabled")
public class HomeWebShareActivity extends BaseActivity {

    private WebView webView;
    private String url="", title="", imgstr="", img_url="", share_title="", brief="";

    private RelativeLayout jsback;
    private TextView jstext;
    private RelativeLayout js_share;
    private String user_id = "";
    private String token;
    private String loginString;
    private String user_id_encode = "";

    private String is_share;
    private String share_url = "";
    private BaseUMShare share;
    private IWXAPI api;
    private String Event_WXPay = "OrderPayActivity_WX";
    private Map<String, String> key_value = new HashMap<String, String>();
    private String clientip;

    public static HomeWebShareActivity HomeWebShareinstance = null;

    private String Event_WebShare = "HomeWebShareActivity_Share";

    private Boolean RefreshBoolean = false;
    private Handler RedHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String rp_id = (String) msg.obj;
            url = IpConfig.getIp() + "mod=checkRedpackage&redpackage_id="
                    + rp_id;
            share_title = "保客福利大放送，送钱开抢啦";
            brief = "优惠劵什么的都靠边站吧，保客只送现金哒~手快有，手慢无";
            img_url = IpConfig.getIp3() + "/images/rp_share.png";
            Log.i("分享红包--------------", url + "---" + share_title + "---"
                    + brief + "---" + img_url);
            share.reSetShareContent(share_title, brief, share_url, img_url);
            share.openShare();
        }
    };

    private Handler errcode_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            String data = (String) msg.obj;
            String status = data;
            android.util.Log.d("455454", "455445" + status);
            if (status.equals("false")) {
                Toast.makeText(HomeWebShareActivity.this,"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
            }
        }

    };

    private Handler null_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;
            String errmsg = data.get("errmsg");
            Toast.makeText(HomeWebShareActivity.this, errmsg, Toast.LENGTH_SHORT).show();
        }

    };

    private Handler payhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            PayReq data = (PayReq) msg.obj;
            Toast.makeText(HomeWebShareActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            MyApplication.prepay_id = data.prepayId;
            api.sendReq(data);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.javascript);


        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        HomeWebShareinstance = this;

        Intent intent = getIntent();

        // webview加载的地址
        url = intent.getStringExtra("url");
        // 本页title
        title = intent.getStringExtra("title");
        // 分享出去的logo
        img_url = intent.getStringExtra("img");
        // 分享出去的title
        share_title = intent.getStringExtra("share_title");
        // 分享出去的描述
        brief = intent.getStringExtra("brief");
        // 是否需要分享
        is_share = intent.getStringExtra("is_share");


        if (!url.contains("userId") && !url.contains("token")) {
            if (url.contains("?")) {

                share_url = url + "&user_id="
                        + user_id_encode + "&token=" + token;

            } else {
                share_url = url + "?user_id="
                        + user_id_encode + "&token=" + token;
            }
        } else {
            share_url = url;
        }


        init();
        share = new BaseUMShare(this, share_title, brief, share_url, img_url);
        initWeb();

        api = WXAPIFactory.createWXAPI(HomeWebShareActivity.this, getString(R.string.weixin_appid));
        api.registerApp(Constants.APP_ID);
        String nettype= GetIp.getCurrentNetType(HomeWebShareActivity.this);
        if(nettype.equals("wifi")){
            clientip=  GetIp.getWifiIp(HomeWebShareActivity.this);
        }else{
            clientip=  GetIp.getPhoneIp();
        }
    }

    void init() {


        jsback = (RelativeLayout) findViewById(R.id.iv_back);
        jstext = (TextView) findViewById(R.id.tv_text);
        js_share = (RelativeLayout) findViewById(R.id.rl_right);
        webView = (WebView) this.findViewById(R.id.webView);


        if (url != null && url.contains("?")) {

            url = url + "&" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        } else {
            url = url + "?" + IpConfig.getCommon() + "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        }

        if (!loginString.equals("none")) {
            url = url
                    + "&token="
                    + token
                    + "&user_id="
                    + user_id_encode;
            synCookies(HomeWebShareActivity.this, url);
        }


        jstext.setText(title);

        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        js_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                url = webView.getUrl();
                share.openShare();
                MobclickAgent.onEvent(HomeWebShareActivity.this, Event_WebShare);
            }
        });

        if (is_share.equals("0")) {
            js_share.setVisibility(View.INVISIBLE);
        } else if (is_share.equals("1")) {
            if (url.contains("pingan_c")) {

                js_share.setVisibility(View.INVISIBLE);

            } else {
                js_share.setVisibility(View.VISIBLE);
            }

        }


    }

    @SuppressLint("JavascriptInterface")
    private void initWeb() {
        WebSettings setting = webView.getSettings();
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);


        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);
        webView.destroyDrawingCache();


        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });


        //	webView.setWebViewClient(new InterceptingWebViewClient(this, webView, true));

        webView.setWebViewClient(new WebViewClient());
        Log.i("网址--------", url);
        Log.i("网址--------", share_url);
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
        // JS调用红包
        webView.addJavascriptInterface(new redPackageInterface(HomeWebShareActivity.this), "redPackage");
        webView.addJavascriptInterface(new JumpToOtherInterface(HomeWebShareActivity.this), "mall");
        webView.addJavascriptInterface(new BackInterface(HomeWebShareActivity.this), "back");
        webView.addJavascriptInterface(new CardOrderPayInterface(HomeWebShareActivity.this), "cardOrder");
        webView.addJavascriptInterface(new DirectPayInterface(HomeWebShareActivity.this),"directpay");

    }



    public void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        // cookieManager.removeSessionCookie();// 移除

        cookieManager.removeAllCookie();
        // String[] cookie = mCookieStr.split(";");

        // Cookie[] cookie = CookieUtil.getCookies().toArray(
        // new Cookie[CookieUtil.getCookies().size()]);

        List<Cookie> cookies = SimpleCookieJar.getCookies();


        StringBuffer sb = new StringBuffer();


        for (Cookie cookie : cookies) {

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
            Log.d("cookie[i]", cookie[i]);
            cookieManager.setCookie(url, cookie[i]);// cookies是在HttpClient中获得的cookie
        }
        CookieSyncManager.getInstance().sync();
    }

    public class redPackageInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        redPackageInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void shareRedPackage(final String rp_id) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = rp_id;
            RedHandler.sendMessage(msg);
        }
    }


    public class JumpToOtherInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        JumpToOtherInterface(Context Context) {
            mContext = Context;
        }


        @JavascriptInterface
        public void orderlist() {
            Intent intent = new Intent(HomeWebShareActivity.this, MyOrderListActivity.class);
            intent.putExtra("needDisorderJump", true);
            startActivity(intent);
            finish();
        }

    }

    public class BackInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        BackInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void previous_page() {
            //			Intent intent = new Intent("cloudhome.disorder.jump");
            //			intent.putExtra("page", 0);
            //			sendBroadcast(intent);
            EventBus.getDefault().post(new DisorderJumpEvent(0));
            Intent intent1 = new Intent(HomeWebShareActivity.this, AllPageActivity.class);
            startActivity(intent1);
        }


    }


    public class CardOrderPayInterface {
        Context mContext;
        /**
         * Instantiate the interface and set the context
         */
        CardOrderPayInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void pay(String productname, String ordercreatetime, String moneys, String id, String orderno) {
            Intent intent = new Intent(HomeWebShareActivity.this, OrderPayActivity.class);
            intent.putExtra("title", productname);
            intent.putExtra("time", ordercreatetime);
            intent.putExtra("price", moneys);
            intent.putExtra("id", id);
            intent.putExtra("orderno", orderno);
            intent.putExtra("entrance", "HomeWebShare");
            startActivityForResult(intent, 100);
        }
    }

    public class DirectPayInterface {
        Context mContext;

        DirectPayInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void directPay(String id, String orderno) {
            boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
            if (!isPaySupported) {
                Toast.makeText(HomeWebShareActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
            } else {
                String url = IpConfig.getUri2("getPreparePay");
                key_value.put("userid", user_id);
                key_value.put("token", token);
                key_value.put("id", id);
                key_value.put("clientip", clientip);
                MyApplication.java_wxpay_orderno = orderno;
                setpayData(url);
                MobclickAgent.onEvent(HomeWebShareActivity.this, Event_WXPay);
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 200) {
            webView.loadUrl(url);
        }
    }

    private void setpayData(String url) {
        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        android.util.Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();
                        message.obj = status;
                        errcode_handler.sendMessage(message);
                    }
                    @Override
                    public void onResponse(String response) {
                        String jsonString = response;
                        android.util.Log.d("onSuccess", "onSuccess json = " + jsonString);
                        Map<String, String> errcode_map = new HashMap<String, String>();
                        try {
                            if (jsonString == null || jsonString.equals("")|| jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();
                                message.obj = status;
                                errcode_handler.sendMessage(message);
                            } else {
                                JSONObject json = new JSONObject(jsonString);
                                String errcode = json.getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = json.getString("errmsg");
                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);
                                    Message message2 = Message.obtain();
                                    message2.obj = errcode_map;
                                    null_handler.sendMessage(message2);
                                } else {
                                    JSONObject data = json.getJSONObject("data");
                                    PayReq req = new PayReq();
                                    req.appId = data.getString("appid");
                                    req.partnerId = data.getString("partnerid");
                                    req.packageValue = data.getString("package");
                                    req.nonceStr = data.getString("noncestr");
                                    req.prepayId = data.getString("prepayid");
                                    req.timeStamp = data.getString("timestamp");
                                    android.util.Log.d("44444", req.timeStamp);
                                    req.sign = data.getString("sign");
                                    android.util.Log.d("444445", req.sign);
                                    req.extData = "app data"; // optional
                                    Message message = Message.obtain();
                                    message.obj = req;
                                    payhandler.sendMessage(message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}