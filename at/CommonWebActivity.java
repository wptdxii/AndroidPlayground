package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.cloudhome.utils.Common;
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

    private static final String EXTRA_NEED_SHARE = "needShare";
    private static final String EXTRA_WEB_URL = "web_address";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_IS_GROUP = "isGroup";
    private static final String EXTRA_IS_REFRESH = "isBackRefresh";
    private static final String EXTRA_SHARE_LOGO = "shareLogo";
    private static final String EXTRA_SHARE_TITLE = "shareTitle";
    private static final String EXTRA_SHARE_DESC = "shareDesc";
    private static final String EXTRA_SHARE_URL = "shareUrl";
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

    /**
     * 不需要分享时调用该静态方法启动 CommonWebActivity
     * @param context
     * @param url
     * @param title
     * @param isGroup
     * @param isBackRefresh
     */
    public static void activityStart(Context context, String url, String title,
                                     boolean isGroup, boolean isBackRefresh) {
        activityStart(context, false, url, title, isGroup, isBackRefresh, "", "", "", "");
    }

    /**
     * 需要分享时调用该静态方法启动 CommonWebActivity
     * @param context
     * @param url
     * @param title
     * @param isGroup
     * @param isBackRefresh
     * @param shareLogo
     * @param shareTitle
     * @param shareDesc
     * @param shareUrl
     */
    public static void activityStart(Context context, String url, String title,
                                     boolean isGroup, boolean isBackRefresh, String shareLogo,
                                     String shareTitle, String shareDesc, String shareUrl) {
        activityStart(context, true, url, title, isGroup, isBackRefresh, shareLogo, shareTitle, shareDesc, shareUrl);
    }

    private static void activityStart(Context context, boolean needShare, String url, String title,
                                      boolean isGroup, boolean isBackRefresh, String shareLogo,
                                      String shareTitle, String shareDesc, String shareUrl) {
        Intent intent = new Intent(context, CommonWebActivity.class);
        intent.putExtra(EXTRA_NEED_SHARE, needShare);
        intent.putExtra(EXTRA_WEB_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_IS_GROUP, isGroup);
        intent.putExtra(EXTRA_IS_REFRESH, isBackRefresh);
        intent.putExtra(EXTRA_SHARE_LOGO, shareLogo);
        intent.putExtra(EXTRA_SHARE_TITLE, shareTitle);
        intent.putExtra(EXTRA_SHARE_DESC, shareDesc);
        intent.putExtra(EXTRA_SHARE_URL, shareUrl);
        context.startActivity(intent);
    }

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
        needShare = intent.getBooleanExtra(EXTRA_NEED_SHARE, false);
        url = intent.getStringExtra(EXTRA_WEB_URL);
        title = intent.getStringExtra(EXTRA_TITLE);
        isGroup = intent.getBooleanExtra(EXTRA_IS_GROUP, false);
        isBackRefresh = intent.getBooleanExtra(EXTRA_IS_REFRESH, false);
        if (needShare) {
            shareLogo = intent.getStringExtra(EXTRA_SHARE_LOGO);
            shareTitle = intent.getStringExtra(EXTRA_SHARE_TITLE);
            shareDesc = intent.getStringExtra(EXTRA_SHARE_DESC);
            shareUrl = intent.getStringExtra(EXTRA_SHARE_URL);

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
        iv_back = (RelativeLayout) findViewById(R.id.rl_back);
        top_title = (TextView) findViewById(R.id.tv_title);
        top_title.setText(title);
        rl_right = (RelativeLayout) findViewById(R.id.rl_share);
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

        url=Uri.parse(url).buildUpon()
                .appendQueryParameter("client_type","android")
                .appendQueryParameter("version", Common.getVerName(CommonWebActivity.this))
                .appendQueryParameter("deviceId",MyInterceptor.device_id)
                .appendQueryParameter("sessionToken",MyInterceptor.sessionToken)
                .toString();


        if (!loginString.equals("none")) {
            url=Uri.parse(url).buildUpon()
                    .appendQueryParameter("token",token)
                    .appendQueryParameter("user_id",user_id_encode)
                    .toString();
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



    public class BackPageInterface {
        @JavascriptInterface
        public void backpage(String url) {
            Log.i("backPageUrl", url);
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
            MyOrderListActivity.activityStart(CommonWebActivity.this, true);
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
