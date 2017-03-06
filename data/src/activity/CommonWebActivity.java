package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
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
import com.cloudhome.event.RefreshEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.PosterGenerate;
import com.cloudhome.network.okhttp.interceptor.MyInterceptor;
import com.cloudhome.utils.Constants;
import com.cloudhome.utils.GetIp;
import com.cloudhome.utils.IpConfig;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class CommonWebActivity extends BaseActivity implements NetResultListener {

    private RelativeLayout iv_back;
    private TextView top_title;
    private RelativeLayout rl_right;
    int x;
    private String shareTitle = "", shareUrl = "", shareDesc = "", shareLogo = "";
    private boolean needShare;
    private String title;
    private String url = "";
    private WebView wb_view;
    private String loginString;
    private String user_id = "";
    private String token;
    private String user_id_encode = "";
    private Boolean RefreshBoolean = false;
    public static CommonWebActivity HomeCommonWebinstance = null;
    private boolean isGroup = false;
    private BaseUMShare share;
    private String Event_Share = "CommmonWebActivity_Share";
    private IWXAPI api;
    private String Event_WXPay = "OrderPayActivity_WX";
    private Map<String, String> key_value = new HashMap<String, String>();
    private String clientip;
    private String backPageUrl;
    private String user_state;
    private PosterGenerate posterGenerate;
    public static final int POSTER = 1;
    private boolean isBackRefresh = false;

    private Handler errcode_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            String data = (String) msg.obj;
            String status = data;
            if (status.equals("false")) {
                Toast.makeText(CommonWebActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
            }
        }

    };

    private Handler null_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;
            String errmsg = data.get("errmsg");
            Toast.makeText(CommonWebActivity.this, errmsg, Toast.LENGTH_SHORT).show();
        }

    };

    private Handler payhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            PayReq data = (PayReq) msg.obj;
            Toast.makeText(CommonWebActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            MyApplication.prepay_id = data.prepayId;
            api.sendReq(data);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common_web);

        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        user_state = sp.getString("Login_CERT", "");
        HomeCommonWebinstance = this;

        Intent intent = getIntent();
        needShare = intent.getBooleanExtra("needShare", false);
        url = intent.getStringExtra("web_address");
        title = intent.getStringExtra("title");
        isGroup = intent.getBooleanExtra("isGroup", false);
        isBackRefresh = intent.getBooleanExtra("isBackRefresh", false);
        if (needShare) {
            shareLogo = intent.getStringExtra("shareLogo");
            shareTitle = intent.getStringExtra("shareTitle");
            shareDesc = intent.getStringExtra("shareDesc");


            shareUrl = intent.getStringExtra("shareUrl");

            if (url.contains("?")) {

                shareUrl = url + "&user_id="
                        + user_id_encode + "&token=" + token;

            } else {
                shareUrl = url + "?user_id="
                        + user_id_encode + "&token=" + token;

            }

        }
        init();
        initData();
        api = WXAPIFactory.createWXAPI(CommonWebActivity.this, getString(R.string.weixin_appid));
        api.registerApp(Constants.APP_ID);
        String nettype = GetIp.getCurrentNetType(CommonWebActivity.this);
        if (nettype.equals("wifi")) {
            clientip = GetIp.getWifiIp(CommonWebActivity.this);
        } else {
            clientip = GetIp.getPhoneIp();
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        // TODO Auto-generated method stub
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        top_title.setText(title);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        wb_view = (WebView) findViewById(R.id.wb_view);
        if (needShare) {
            //            rl_right.setVisibility(View.VISIBLE);
            //            if (url.contains("pingan_c")) {
            //                rl_right.setVisibility(View.GONE);
            //            } else {
            rl_right.setVisibility(View.VISIBLE);
            share = new BaseUMShare(this, shareTitle, shareDesc, shareUrl, shareLogo);
            //            }
        } else {
            rl_right.setVisibility(View.INVISIBLE);
        }


    }

    private void initData() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if ("".equals(backPageUrl) || null == backPageUrl) {

                    if (isBackRefresh) {
                        EventBus.getDefault().post(new RefreshEvent());
                    }

                    finish();
                } else {
                    wb_view.loadUrl(backPageUrl);
                }
            }
        });

        rl_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                share.openShare();
                MobclickAgent.onEvent(CommonWebActivity.this, Event_Share);
            }
        });

        WebSettings setting = wb_view.getSettings();
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);


        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wb_view.clearCache(true);
        wb_view.destroyDrawingCache();

        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        setting.setAppCachePath(appCachePath);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);
        //API21以上，在webview里面从https访问http时候会被block
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        wb_view.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        wb_view.setDownloadListener(new MyWebViewDownLoadListener());
        //  wb_view.setWebViewClient(new InterceptingWebViewClient(this, wb_view, true));
        wb_view.addJavascriptInterface(new JumpToOtherInterface(
                CommonWebActivity.this), "mall");
        wb_view.addJavascriptInterface(new BackInterface(
                CommonWebActivity.this), "back");
        wb_view.addJavascriptInterface(new CardOrderPayInterface(
                CommonWebActivity.this), "cardOrder");
        wb_view.addJavascriptInterface(new DirectPayInterface(CommonWebActivity.this), "directpay");

        wb_view.addJavascriptInterface(new BackPageInterface(), "android");
        wb_view.addJavascriptInterface(new PosterInterface(CommonWebActivity.this), "poster");

        wb_view.setWebViewClient(new WebViewClient());

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
        }

        wb_view.loadUrl(url);
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        if (flag == MyApplication.DATA_OK) {
            String imageUrl = dataObj.toString();
            Intent intent = new Intent(CommonWebActivity.this, PosterActivity.class);
            intent.putExtra("imageUrl", imageUrl);
            startActivity(intent);
        } else if (flag == MyApplication.NET_ERROR) {
        } else if (flag == MyApplication.DATA_EMPTY) {
        } else if (flag == MyApplication.JSON_ERROR) {
        } else if (flag == MyApplication.DATA_ERROR) {
            String errmsg = dataObj.toString();
            Toast.makeText(CommonWebActivity.this, errmsg, Toast.LENGTH_SHORT).show();
        }
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


    //禁止webview左右滑动
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) ev.getX();
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


    private static final String TAG = "CommonWebActivity";

    public class BackPageInterface {
        @JavascriptInterface
        public void backpage(String url) {
            backPageUrl = url;
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
            Intent intent = new Intent(CommonWebActivity.this, MyOrderListActivity.class);
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
            //            Intent intent = new Intent("cloudhome.disorder.jump");
            //            intent.putExtra("page", 0);
            //            sendBroadcast(intent);
            EventBus.getDefault().post(new DisorderJumpEvent(0));
            Intent intent1 = new Intent(CommonWebActivity.this, AllPageActivity.class);
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

            RefreshBoolean = true;

            Intent intent = new Intent(CommonWebActivity.this, OrderPayActivity.class);

            intent.putExtra("title", productname);
            intent.putExtra("time", ordercreatetime);
            intent.putExtra("price", moneys);
            intent.putExtra("id", id);
            intent.putExtra("orderno", orderno);
            intent.putExtra("entrance", "HomeCommonWeb");
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
                Toast.makeText(CommonWebActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
            } else {
                String url = IpConfig.getUri2("getPreparePay");
                key_value.put("userid", user_id);
                key_value.put("token", token);
                key_value.put("id", id);
                key_value.put("clientip", clientip);
                MyApplication.java_wxpay_orderno = orderno;
                setpayData(url);
                MobclickAgent.onEvent(CommonWebActivity.this, Event_WXPay);
            }
        }
    }

    public void checkAuthStatus(String codeUrl, String imgUrl) {
        //用户状态 00表示已认证   02表示认证中    01表示未认证
        if (user_state.equals("00")) {
            posterGenerate = new PosterGenerate(CommonWebActivity.this);
            posterGenerate.execute(codeUrl, imgUrl, user_id, token, POSTER);
        } else if (user_state.equals("01")) {
            Intent intent = new Intent();
            intent.setClass(this, VerifyMemberActivity.class);
            startActivity(intent);
        } else if (user_state.equals("02")) {
            Intent intent = new Intent();
            intent.setClass(this, VerifiedInfoActivity.class);
            startActivity(intent);
        } else if (user_state.equals("03")) {
            Intent intent = new Intent();
            intent.setClass(this, Verified_failure_InfoActivity.class);
            startActivity(intent);
        }
    }

    public class PosterInterface {
        Context mContext;

        PosterInterface(Context Context) {
            mContext = Context;
        }

        @JavascriptInterface
        public void createPoster(String codeUrl, String imgUrl) {
            checkAuthStatus(codeUrl, imgUrl);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 200) {
            if (isGroup) {
                share.reSetShareContent(shareTitle, shareDesc, shareUrl, shareLogo);
                wb_view.loadUrl(url);
            } else {
                finish();
            }
        }
        if (needShare) {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setpayData(String url) {
        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        android.util.Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();
                        message.obj = status;
                        errcode_handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        android.util.Log.d("onSuccess", "onSuccess json = " + jsonString);
                        Map<String, String> errcode_map = new HashMap<String, String>();
                        try {
                            if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isBackRefresh) {
                EventBus.getDefault().post(new RefreshEvent());
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
