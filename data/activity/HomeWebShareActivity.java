package com.cloudhome.activity;

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
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.network.PosterGenerate;
import com.cloudhome.network.RedRainBack;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.GetIp;
import com.cloudhome.utils.IpConfig;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.Log;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sourceforge.simcpux.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


@SuppressLint("SetJavaScriptEnabled")
public class HomeWebShareActivity extends BaseActivity implements NetResultListener{

    private WebView webView;
    private String url="", title="", imgstr="", img_url="", share_title="", brief="";

    private RelativeLayout jsback;
    private TextView jstext;
    private RelativeLayout js_share;
    private String user_id = "";
    private String token;
    private String loginString;
    private String user_id_encode = "";
    private String suid="";

    private String is_share;
    private String share_url = "";
    private BaseUMShare share;
    private IWXAPI api;
    private String Event_WXPay = "OrderPayActivity_WX";
    private Map<String, String> key_value = new HashMap<String, String>();
    private  String clientip;
    private String user_state;
    private String Event_WebShare = "HomeWebShareActivity_Share";
    private PosterGenerate posterGenerate;
    public static final int POSTER=1;
    private String redPrizeId="";
    public static final int RED_RAIN=2;
    //上一页是否传了shareUrl
    private boolean isHasShareUrl=false;

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
            Toast.makeText(HomeWebShareActivity.this, errmsg,Toast.LENGTH_SHORT).show();
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
        suid=sp.getString("Encrypt_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        user_state = sp.getString("Login_CERT", "");

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
        //上一页是否传了shareUrl
        isHasShareUrl=intent.getBooleanExtra("isHasShareUrl",false);

        if(isHasShareUrl){
            share_url=intent.getStringExtra("shareUrl");
            if (!share_url.contains("userId") && !share_url.contains("token")) {
                if (share_url.contains("?")) {
                    share_url = share_url + "&user_id="+ user_id_encode + "&token=" + token;
                } else {
                    share_url = share_url + "?user_id="+ user_id_encode + "&token=" + token;
                }
            }
        }else{
            if (!url.contains("userId") && !url.contains("token")) {
                if (url.contains("?")) {
                    share_url = url + "&user_id="+ user_id_encode + "&token=" + token;
                } else {
                    share_url = url + "?user_id="+ user_id_encode + "&token=" + token;
                }
            } else {
                share_url = url;
            }
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
            url = url+ "&token="+ token+ "&user_id="+ user_id_encode+ "&suid=" + suid;
//            synCookies(HomeWebShareActivity.this, url);
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
        //鸡年福袋需要开启localstorage
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);
        //API21以上，在webview里面从https访问http时候会被block
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

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
        webView.loadUrl(url);

        // 增加接口方法,让html页面调用
        webView.addJavascriptInterface(new Object() {
            // 这里我定义了一个拨打的方法

            public void startPhone(String num) {
               requestCallPhonePermission(num);
            }

            // 这里我定义了一个拨打的方法
            @SuppressLint("JavascriptInterface")
            public void startPhone2(String num) {
               requestCallPhonePermission(num);
            }

        }, "demo");
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        // JS调用红包
        webView.addJavascriptInterface(new redPackageInterface(HomeWebShareActivity.this), "redPackage");
        webView.addJavascriptInterface(new JumpToOtherInterface(HomeWebShareActivity.this), "mall");
        webView.addJavascriptInterface(new BackInterface(HomeWebShareActivity.this), "back");
        webView.addJavascriptInterface(new CardOrderPayInterface(HomeWebShareActivity.this), "cardOrder");
        webView.addJavascriptInterface(new DirectPayInterface(HomeWebShareActivity.this),"directpay");
        //海报
        webView.addJavascriptInterface(new PosterInterface(HomeWebShareActivity.this),"poster");
        //鸡年福袋
        webView.addJavascriptInterface(new CheckAuthInterface(HomeWebShareActivity.this),"checkauth");
        webView.addJavascriptInterface(new ShareImageInterface(HomeWebShareActivity.this),"shareImage");
        webView.addJavascriptInterface(new RedPacketInterface(HomeWebShareActivity.this),"redPacketRain");
        webView.addJavascriptInterface(this, "android");

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
    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch(action){
            case POSTER:
                if (flag == MyApplication.DATA_OK) {
                    String imageUrl=dataObj.toString();
                    Intent intent=new Intent(HomeWebShareActivity.this,PosterActivity.class);
                    intent.putExtra("imageUrl",imageUrl);
                    startActivity(intent);
                } else if (flag == MyApplication.NET_ERROR) {
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String errmsg=dataObj.toString();
                    Toast.makeText(HomeWebShareActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                }
                break;
            case RED_RAIN:
                webView.reload();
                break;
        }
    }


//    public void synCookies(Context context, String url) {
//        CookieSyncManager.createInstance(context);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        // cookieManager.removeSessionCookie();// 移除
//
//        cookieManager.removeAllCookie();
//        // String[] cookie = mCookieStr.split(";");
//
//        // Cookie[] cookie = CookieUtil.getCookies().toArray(
//        // new Cookie[CookieUtil.getCookies().size()]);
//
//        List<Cookie> cookies = SimpleCookieJar.getCookies();
//
//
//        StringBuffer sb = new StringBuffer();
//
//
//        for (Cookie cookie : cookies) {
//
//            String cookieName = cookie.name();
//            String cookieValue = cookie.value();
//            if (!TextUtils.isEmpty(cookieName)
//                    && !TextUtils.isEmpty(cookieValue)) {
//                sb.append(cookieName).append("=");
//                sb.append(cookieValue).append(";");
//            }
//        }
//
//        String[] cookie = sb.toString().split(";");
//        for (int i = 0; i < cookie.length; i++) {
//            Log.d("cookie[i]", cookie[i]);
//            cookieManager.setCookie(url, cookie[i]);// cookies是在HttpClient中获得的cookie
//        }
//        CookieSyncManager.getInstance().sync();
//    }

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

    @JavascriptInterface
    public void invite() {
        String mShareUrl = IpConfig.getIp4() + "/active/invite_friend/wx_page.html?user_id=" + user_id + "&token=" + token;
        String shareTitle = getString(R.string.invite_share_title);
        String shareDesc = getString(R.string.invite_share_desc);
        BaseUMShare.shareNotQQZone(this, shareTitle, shareDesc, mShareUrl, R.drawable.icon_share_logo);
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
        public void directPay( String id, String orderno) {
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

    public void checkAuthStatus(String codeUrl,String imgUrl){
        //用户状态 00表示已认证   02表示认证中    01表示未认证
        if (user_state.equals("00")) {
            posterGenerate=new PosterGenerate(HomeWebShareActivity.this);
            posterGenerate.execute(codeUrl,imgUrl,user_id,token,POSTER);
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
        public void createPoster(String codeUrl,String imgUrl) {
            checkAuthStatus(codeUrl,imgUrl);
            Log.i("invite",codeUrl);
            Log.i("invite",imgUrl);
        }
    }

    public class CheckAuthInterface {
        Context mContext;
        CheckAuthInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void checkAuthStatus() {
            if (user_state.equals("01")) {
                Intent intent = new Intent();
                intent.setClass(HomeWebShareActivity.this, VerifyMemberActivity.class);
                startActivity(intent);
            } else if (user_state.equals("02")) {
                Intent intent = new Intent();
                intent.setClass(HomeWebShareActivity.this, VerifiedInfoActivity.class);
                startActivity(intent);
            } else if (user_state.equals("03")) {
                Intent intent = new Intent();
                intent.setClass(HomeWebShareActivity.this, Verified_failure_InfoActivity.class);
                startActivity(intent);
            }
        }
    }

    public class ShareImageInterface {
        Context mContext;
        ShareImageInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void shareImg(String ImgUrl) {
            Log.i("shareImag",ImgUrl);
            if(!TextUtils.isEmpty(ImgUrl)){
                BaseUMShare.sharePlatformsPicture(HomeWebShareActivity.this,ImgUrl);
            }
        }
    }

    public class RedPacketInterface {
        Context mContext;
        RedPacketInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void andriodShareCardToOnePlatForm(String prizeId,String cardShareBtnImgSrc) {
            redPrizeId=prizeId;
            BaseUMShare.sharePictureWithListener(2,HomeWebShareActivity.this,cardShareBtnImgSrc,redRainShareListener);

        }
        @JavascriptInterface
        public void andriodShareRedPacketToOnePlatForm(String prizeId,String redPacketShareBtnImgSrc) {
            redPrizeId=prizeId;
            BaseUMShare.sharePictureWithListener(2,HomeWebShareActivity.this,redPacketShareBtnImgSrc,redRainShareListener);
        }
        @JavascriptInterface
        public void andriodShareCardToThreePlatForms(String prizeId,String cardShareBtnImgSrc) {
            BaseUMShare.sharePlatformsPicture(HomeWebShareActivity.this,cardShareBtnImgSrc);
        }
        @JavascriptInterface
        public void goToPosterPage(String pic) {
            Intent intent = new Intent(HomeWebShareActivity.this, PosterActivity.class);
            intent.putExtra("imageUrl", pic);
            startActivity(intent);
        }
    }

    private UMShareListener redRainShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
                //调用分享成功接口
            RedRainBack redRainBack=new RedRainBack(HomeWebShareActivity.this);
            redRainBack.execute(user_id,redPrizeId,RED_RAIN,token);
        }
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };


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
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
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


    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}