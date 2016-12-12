package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.Statistics;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class TopicShareWebActivity extends BaseActivity implements NetResultListener {

    // private Button button;
    private String url, title, img_url, share_title, brief;
    private LinearLayout js_linear;
    private String position;
    private RelativeLayout jsback;
    private TextView jstext;
    private RelativeLayout js_share;
    private String loginString;
    SharedPreferences sp;
    private String share_url;
    private String user_id = "";
    private String content_id = "";
    private int x;
    private int y;
    private int movtionx;
    private int movtiony;
    private String token;
    private String avatar;
    private String user_name;
    private String user_id_encode="";
    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");
    private Map<String, String> key_value = new HashMap<String, String>();
    //统计接口
    private Statistics statistics = new Statistics(this);

    private FrameLayout videoview;// 全屏时视频加载view
    private WebView videowebview;
    private Boolean islandport = true;//true表示此时是竖屏，false表示此时横屏。
    private View xCustomView;
    private xWebChromeClient xwebchromeclient;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;
    private RelativeLayout rl_top_title;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.micro_share);
        init();
        initwidget();
        initShare();
        videowebview.loadUrl(url);
        Log.d("url", url);

        videowebview.addJavascriptInterface(new phoneInterface(TopicShareWebActivity.this), "microshare");
        videowebview.addJavascriptInterface(new jumpInterface(TopicShareWebActivity.this), "topicshare");
        videowebview.addJavascriptInterface(new picturePolicyInterface(TopicShareWebActivity.this), "picturepolicy");
        videowebview.addJavascriptInterface(new wechatShareInterface(TopicShareWebActivity.this),"wechatshare");
        videowebview.addJavascriptInterface(new wechatCircleShareInterface(TopicShareWebActivity.this),"wechatcircleshare");
        videowebview.addJavascriptInterface(new QQShareInterface(TopicShareWebActivity.this),"qqshare");

    }

    private void init() {
        sp = this.getSharedPreferences("userInfo", 0);
        loginString = sp.getString("Login_STATE", "none");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        key_value.put("token", token);
        Intent intent = getIntent();
        url=intent.getStringExtra("url");
        Log.i("addbefore",url);
        if(url.contains("?")){
            url = intent.getStringExtra("url")+ "&"+IpConfig.getCommon()+ "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;
            Log.i("add1",url);
        }else{
            url = intent.getStringExtra("url") +"?"+IpConfig.getCommon()+ "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;
            Log.i("add2",url);
        }

        share_url = intent.getStringExtra("url");
        Log.i("share_url",share_url);
        js_linear = (LinearLayout) findViewById(R.id.js_linear);
        share_title = intent.getStringExtra("share_title");
        brief = intent.getStringExtra("brief");
        img_url = intent.getStringExtra("img_url");
        content_id = intent.getStringExtra("content_id");
        jstext = (TextView) findViewById(R.id.tv_text);
        title = intent.getStringExtra("title");

        avatar = sp.getString("avatar", "");

        user_name = sp.getString("truename", "");


            jstext.setText(title);

        jsback = (RelativeLayout) findViewById(R.id.iv_back);
        jsback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        js_share = (RelativeLayout) findViewById(R.id.rl_right);
        js_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.registerListener(snsListener);
                mController.openShare(TopicShareWebActivity.this, snsListener);
                statistics.execute("hottopic_share");
            }
        });

        if (!loginString.equals("none")) {

            user_id = sp.getString("Login_UID", "");
            user_id_encode=sp.getString("Login_UID_ENCODE", "");
            share_url = share_url + "&user_id=" + user_id_encode+"&token="+token;
            url = url + "&user_id=" + user_id_encode+"&token="+token;

        }

        rl_top_title= (RelativeLayout) findViewById(R.id.rl_top_title);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initwidget() {
        // TODO Auto-generated method stub
        videoview = (FrameLayout) findViewById(R.id.video_view);
        videowebview = (WebView) findViewById(R.id.video_webview);
        WebSettings ws = videowebview.getSettings();
        /**
         * setAllowFileAccess 启用或禁止WebView访问文件数据 setBlockNetworkImage 是否显示网络图像
         * setBuiltInZoomControls 设置是否支持缩放 setCacheMode 设置缓冲的模式
         * setDefaultFontSize 设置默认的字体大小 setDefaultTextEncodingName 设置在解码时使用的默认编码
         * setFixedFontFamily 设置固定使用的字体 setJavaSciptEnabled 设置是否支持Javascript
         * setLayoutAlgorithm 设置布局方式 setLightTouchEnabled 设置用鼠标激活被选项
         * setSupportZoom 设置是否支持变焦
         * */
        ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
//        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 排版适应屏幕，这个对4.4以下的系统有影响
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        ws.setUseWideViewPort(true);// 可任意比例缩放
        ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// 保存表单数据
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setDomStorageEnabled(true);
        xwebchromeclient = new xWebChromeClient();
        videowebview.setWebChromeClient(xwebchromeclient);

        videowebview.setWebViewClient(new WebViewClient());
    }


    void initShare() {

        String appID = getString(R.string.weixin_appid);
        String appSecret = getString(R.string.weixin_appsecret);
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(TopicShareWebActivity.this,
                appID, appSecret);
        wxHandler.showCompressToast(false);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(
                TopicShareWebActivity.this, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxHandler.showCompressToast(false);
        wxCircleHandler.addToSocialSDK();

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
                TopicShareWebActivity.this, "1104898238", "2DdbISbzGWLBISzz");
        qqSsoHandler.addToSocialSDK();

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);

        // 设置QQ空间分享内容
        QQShareContent qqcontent = new QQShareContent();
        qqcontent.setShareContent(brief);
        qqcontent.setTargetUrl(share_url);
        qqcontent.setTitle(share_title);
        qqcontent.setShareImage(new UMImage(TopicShareWebActivity.this, img_url));
        mController.setShareMedia(qqcontent);

        // 设置QQ空间分享内容
        SinaShareContent sinacontent = new SinaShareContent();
        // +"专业的保险导购平台、海量保险客户等你来！");
        sinacontent.setShareContent(share_title + share_url);
        sinacontent.setShareImage(new UMImage(TopicShareWebActivity.this,
                img_url));
        mController.setShareMedia(sinacontent);

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(brief);
        Log.d("54545", brief);
        Log.d("54545", url);
        Log.d("54545", img_url);
        // 设置title
        weixinContent.setTitle(share_title);
        // 设置分享内容跳转URL
        Log.d("999999", share_url + "777");
        weixinContent.setTargetUrl(share_url);
        // 设置分享图片
        weixinContent.setShareImage(new UMImage(TopicShareWebActivity.this,
                img_url));
        mController.setShareMedia(weixinContent);

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(brief);

        // 设置朋友圈title
        circleMedia.setTitle(share_title);
        circleMedia.setTargetUrl(share_url);
        circleMedia.setShareImage(new UMImage(TopicShareWebActivity.this,
                img_url));
        mController.setShareMedia(circleMedia);
    }




    public class phoneInterface {
        Context mContext;
        phoneInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void startPhone(String num) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + num));
            startActivity(intent);
        }
    }


    public class picturePolicyInterface {
        Context mContext;
        picturePolicyInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void jumpUploadPolicy() {
            if (loginString.equals("none")) {
                Intent intent = new Intent();
                intent.setClass(TopicShareWebActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(TopicShareWebActivity.this, PolicyPictureActivity.class);
                startActivity(intent);
            }
        }
    }

    public class jumpInterface {
        Context mContext;
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
            if(TextUtils.isEmpty(user_name)){
                intent.putExtra("share_title", "资深保险销售专家——保险人");
                intent.putExtra("brief", "您好，我是保险人。愿意为您提供所有保险相关的服务。");
            }else{
                intent.putExtra("share_title", "资深保险销售专家——"+user_name);
                intent.putExtra("brief", "您好，我是"+user_name+"。愿意为您提供所有保险相关的服务。");
            }
            intent.putExtra("title", "我的微站");

            intent.putExtra("url", url);
            intent.putExtra("shareurl", shareurl);
            intent.putExtra("img_url", img_url);

            intent.setClass(TopicShareWebActivity.this, MicroShareWebActivity.class);
            startActivity(intent);
        }
    }

    public class wechatShareInterface {
        Context mContext;
        wechatShareInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void startWechatShare() {
            // 调用直接分享
            SHARE_MEDIA platform =SHARE_MEDIA.WEIXIN;
            mController.directShare(TopicShareWebActivity.this, platform,
                    snsListener);
        }
    }
    public class wechatCircleShareInterface {
        Context mContext;
        wechatCircleShareInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void startWechatCircleShare() {
            // 调用直接分享
            SHARE_MEDIA platform =SHARE_MEDIA.WEIXIN_CIRCLE;
            mController.directShare(TopicShareWebActivity.this, platform,
                    snsListener);
        }
    }
    public class QQShareInterface {
        Context mContext;
        QQShareInterface(Context Context) {
            mContext = Context;
        }
        @JavascriptInterface
        public void startQQShare() {
            // 调用直接分享
            SHARE_MEDIA platform =SHARE_MEDIA.QQ;
            mController.directShare(TopicShareWebActivity.this, platform,
                    snsListener);
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


    public SnsPostListener snsListener = new SnsPostListener() {
        @Override
        public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
            // TODO Auto-generated method stub
            if (arg1 == 200) {
                Log.i("SnsPostListener---------", "分享结束了----------------");
                if (!loginString.equals("none")) {
                    //调用分享成功接口
                    key_value.put("content_id", content_id);
                    key_value.put("user_id", user_id);
                    key_value.put("token", token);
                    if (!loginString.equals("none")) {
                        String url = IpConfig.getUri("getShareMicroReadContent");
                        addToShareTimes(url);
                    }
                }
            }

        }

        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            Log.i("SnsPostListener---------", "分享开始了----------------");
        }
    };


    public void addToShareTimes(String url) {
        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        String status = "false";
                        Message message = Message.obtain();
                        message.obj = status;
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        String jsonString = response;
                        Log.d("onSuccess", "调用成功-------------------");
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                    }
                });


    }


    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                return true;
            } else {
                videowebview.loadUrl("about:blank");
                TopicShareWebActivity.this.finish();
                android.util.Log.i("testwebview", "===>>>2");
            }
        }
//       这里如果 return true的话会屏蔽掉音量键
        return false;
    }

    /**
     * 判断是否是全屏
     *
     * @return
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        xwebchromeclient.onHideCustomView();
    }

    /**
     * 处理Javascript的对话框、网站图标、网站标题以及网页加载进度等
     *
     * @author
     */
    public class xWebChromeClient extends WebChromeClient {
        private Bitmap xdefaltvideo;
        private View xprogressvideo;

        @Override
        //播放网络视频时全屏会被调用的方法
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            if (islandport) {
            } else {

//                ii = "1";
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            videowebview.setVisibility(View.GONE);
            //如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            videoview.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            videoview.setVisibility(View.VISIBLE);
            Log.i("全屏会被调用", "onShowCustomView");
        }

        @Override
        //视频播放退出全屏会被调用的
        public void onHideCustomView() {

            if (xCustomView == null)//不是全屏播放状态
                return;
            // Hide the custom view.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            videoview.removeView(xCustomView);
            xCustomView = null;
            videoview.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();

            videowebview.setVisibility(View.VISIBLE);

            Log.i("退出全屏会被调用", "onHideCustomView");
        }

        //视频加载添加默认图标
        @Override
        public Bitmap getDefaultVideoPoster() {
            //Log.i(LOGTAG, "here in on getDefaultVideoPoster");
            if (xdefaltvideo == null) {
                xdefaltvideo = BitmapFactory.decodeResource(
                        getResources(), R.drawable.black_bg);
            }
            return xdefaltvideo;
        }

        //视频加载时进程loading
        @Override
        public View getVideoLoadingProgressView() {
            //Log.i(LOGTAG, "here in on getVideoLoadingPregressView");

            if (xprogressvideo == null) {
                LayoutInflater inflater = LayoutInflater.from(TopicShareWebActivity.this);
//				xprogressvideo = inflater.inflate(R.layout.progress_layout, null);
            }
            Log.i("视频加载", "getVideoLoadingProgressView");
            return null;
        }

        //网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            (TopicShareWebActivity.this).setTitle(title);
        }


    }


    /**
     * 当横竖屏切换时会调用该方法
     *
     * @author
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rl_top_title.setVisibility(View.GONE);
            islandport = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            rl_top_title.setVisibility(View.VISIBLE);
            islandport = true;
        }
    }



    public void onResume() {
        super.onResume();
        videowebview.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        videowebview.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initShare();
    }


}