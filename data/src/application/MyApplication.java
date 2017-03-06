package com.cloudhome.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.webkit.WebView;

import com.cloudhome.BuildConfig;
import com.cloudhome.R;
import com.cloudhome.network.okhttp.interceptor.HeaderInterceptor;
import com.cloudhome.network.okhttp.interceptor.MyInterceptor;
import com.cloudhome.network.okhttp.interceptor.TokenInterceptor;
import com.cloudhome.utils.AdPreference;
import com.cloudhome.utils.ChannelUtils;
import com.cloudhome.utils.CustomConstants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MyApplication extends MultiDexApplication {
    private static final long TIMEOUT_READ = 30000L;
    private static final long TIMEOUT_CONNECTION = 30000L;
    private static Application sInstance;

    public final static int DATA_OK = 0; // 请求成功
    public final static int DATA_ERROR = 1; // 请求失败
    public final static int NET_ERROR = 2; // 网络错误
    public final static int DATA_EMPTY = 3; // 数据为空
    public final static int JSON_ERROR = 4; // Json解析错误

    public final static String NO_NET = "网络连接失败";
    public final static String NO_DATA = "暂无数据";
    public final static String FETCH_DATA_FAILED = "获取数据失败";
    public final static String BUTTON_RELOAD = "重新加载";

    public static SharedPreferences sp2;

    public static String only_key = "";

    public static String prepay_id = "";
    public static String java_wxpay_orderno = "";
    public static String prevoius_page = "";
    public static String java_wxpay_entrance = "";
    private static final String TAG = "OkUtils";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        removeTempFromPref();
        JPushInterface.setDebugMode(BuildConfig.DEBUG); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this); // 初始化 JPush

        //		sp2 = this.getSharedPreferences("otherinfo", 0);
        //		only_key = sp2.getString("only_key", "");
        //        OkHttpUtils.getInstance().setContext(mContext);
        //        OkHttpUtils.getInstance().setConnectTimeout(3000, TimeUnit.MILLISECONDS);
        initOkHttpClient();

        AdPreference adPreference = new AdPreference(this);
        initUmeng();
        initWebView();
        MobclickAgent.openActivityDurationTrack(false);

    }

    public static Application getInstance() {
        return sInstance;
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }


    private void initOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
                .addInterceptor(new MyInterceptor())
                .addInterceptor(new TokenInterceptor(MyApplication.getInstance()))
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(initLoggerInterceptor())
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.MILLISECONDS);

        OkHttpUtils.initClient(okHttpClientBuilder.build());

    }

    private HttpLoggingInterceptor initLoggerInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.e(TAG, message);
                    }
                });
        loggingInterceptor.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY: HttpLoggingInterceptor.Level.NONE);

        return loggingInterceptor;
    }

    private void initUmeng() {
        //友盟分享
        UMShareAPI.get(this);
        PlatformConfig.setWeixin(getString(R.string.weixin_appid), getString(R.string.weixin_appsecret));
        //        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
        PlatformConfig.setQQZone(getString(R.string.qq_appid), getString(R.string.qq_appsecret));

        //渠道信息
        String channel = ChannelUtils.getChannel(this);
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this,
                BuildConfig.UMENG_APPKEY, channel);
        MobclickAgent.startWithConfigure(config);
    }

    private void removeTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

}
