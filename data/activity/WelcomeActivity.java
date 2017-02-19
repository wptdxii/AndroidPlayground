package com.cloudhome.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.cloudhome.BuildConfig;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.SplashAdBean;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.network.okhttp.interceptor.MyInterceptor;
import com.cloudhome.network.retrofit.callback.BaseCallBack;
import com.cloudhome.network.retrofit.entity.AppVersionEntity;
import com.cloudhome.network.retrofit.service.ApiFactory;
import com.cloudhome.utils.AdFileUtils;
import com.cloudhome.utils.AdPreference;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

public class WelcomeActivity extends BaseActivity {
    private static final int RC_PHONE_STATE = 100;
    public AlertDialog dialogNew;


    Map<String, String> key_value = new HashMap<String, String>();


    private String loginString, user_id, token;
    private String username, pwMd5;
    private ProgressDialog pBar;
    private String mDownloadUrl = "";
    private String appName = "NewAppSample.apk";
    private String mAppVersion = "";
    private String versionBrief = "";

    public static final int NET_ERROR = 1;

    private boolean isFirstUse;
    private int old_install_Code, current_versionCode;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == NET_ERROR) {
                startNextActivity();
            }
        }
    };
    private Handler device_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;
            MyApplication.only_key = data.get("only_key");
            startNextActivity();

        }

    };
    private String device_id, os_version;
    private Handler cookie_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;
            String errcode = data.get("errcode");
            if (errcode.equals("0")) {
                final String Device_URL = IpConfig.getUri("saveDeviceMsg");
                setDevice_data(Device_URL);
            } else {
                Editor edit;
                edit = sp.edit();
                edit.clear();
                edit.commit();

                Editor edit2;
                edit2 = sp2.edit();
                edit2.clear();
                edit2.commit();
                Editor edit3;
                edit3 = sp3.edit();
                edit3.clear();
                edit3.commit();

                MyInterceptor.sessionToken = "";
                startNextActivity();
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ifShowGuide();
        ifShowAd();
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        Editor editor4 = sp4.edit();

        if (token.equals("null") || token.equals("")) {
            MyInterceptor.sessionToken = "";
        } else {
            MyInterceptor.sessionToken = token;
        }

        editor4.putInt("page", 0);
        editor4.commit();
        os_version = android.os.Build.VERSION.RELEASE;
        requestPhoneStatePermission();
    }

    private void requestPhoneStatePermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        requestPermissions(perms, new PermissionListener() {
            @Override
            public void onGranted() {
                TelephonyManager tm = (TelephonyManager) getBaseContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                device_id = tm.getDeviceId() + "";
                MyInterceptor.device_id = device_id;
                getAppVersion();
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Secure.getString(WelcomeActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;
                }
                getAppVersion();
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Secure.getString(WelcomeActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;

                }
                getAppVersion();
            }
        });
    }

    private void ifShowGuide() {
        SharedPreferences preferences = getSharedPreferences("isFirstUse", MODE_PRIVATE);
        // 读取SharedPreferences中需要的数据
        isFirstUse = preferences.getBoolean("isFirstUse", true);
        // Common.getVerCode 中包名一定要与本应用包名一致
        current_versionCode = Common.getVerCode(getApplicationContext());
        old_install_Code = preferences.getInt("old_install_Code", 0);


    }

    private void ifShowAd() {
        String url = IpConfig.getUri2("splashAd");
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response != null && !response.equals("") && !response.equals("null")) {

                            SplashAdBean adBean = JSON.parseObject(response, SplashAdBean.class);
                            String isShow = adBean.getIsShow();
                            boolean isAdShow = false;
                            if ("1".equals(isShow)) {
                                isAdShow = true;
                            } else {
                                isAdShow = false;
                            }

                            AdPreference.getInstance().saveIfShowAd(isAdShow);
                        }


                    }
                });
    }

    public void showUpdateDialog(String updateMode) {
        // 把json中的“\n”转换成换行
        versionBrief = versionBrief.replace("\\n", "\n");
        Builder updateDialog = new AlertDialog.Builder(WelcomeActivity.this);
        updateDialog.setTitle("发现新版本" + mAppVersion + "，是否更新？")
                .setMessage(versionBrief)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressBar();// 更新当前版本

                    }
                });

        if ("1".equals(updateMode)) {
            updateDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 消掉对话框
                            if (loginString.equals("none")) {
                                final String Device_URL = IpConfig.getUri("saveDeviceMsg");
                                setDevice_data(Device_URL);
                            } else {
                                final String PRODUCT_URL = IpConfig
                                        .getUri("getMemberLogin");
                                username = sp.getString("USER_NAME", "none");
                                pwMd5 = sp.getString("pwMd5", "none");
                                key_value.put("mobile", username);
                                key_value.put("password", pwMd5);
                                setdata(PRODUCT_URL);
                            }
                        }
                    });
        }
        AlertDialog dialogUp = updateDialog.create();
        dialogUp.setCanceledOnTouchOutside(false);
        dialogUp.setCancelable(false);
        dialogUp.show();
    }

    protected void showProgressBar() {
        pBar = new ProgressDialog(WelcomeActivity.this);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍后...");
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setCanceledOnTouchOutside(false);
        pBar.setCancelable(false);
        downAppFile(mDownloadUrl);
    }

    private void setdata(String url) {

        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        Editor edit;
                        edit = sp.edit();
                        edit.clear();
                        edit.commit();

                        Editor edit2;
                        edit2 = sp2.edit();
                        edit2.clear();
                        edit2.commit();

                        Editor edit3;
                        edit3 = sp3.edit();
                        edit3.clear();
                        edit3.commit();


                        MyInterceptor.sessionToken = "";

                        handler.sendEmptyMessageDelayed(NET_ERROR, 500);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);

                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                Editor edit;
                                edit = sp.edit();
                                edit.clear();
                                edit.commit();

                                Editor edit2;
                                edit2 = sp2.edit();
                                edit2.clear();
                                edit2.commit();

                                Editor edit3;
                                edit3 = sp3.edit();
                                edit3.clear();
                                edit3.commit();


                                MyInterceptor.sessionToken = "";
                                startNextActivity();

                            } else {
                                JSONObject jsonObject = new JSONObject(
                                        jsonString);
                                String data = jsonObject.getString("data");

                                String errcode = jsonObject
                                        .getString("errcode");

                                Log.d("44444", data);

                                map.put("errcode", errcode);

                                Message message = Message.obtain();

                                message.obj = map;

                                cookie_handler.sendMessage(message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }

    private void setDevice_data(String url) {

        OkHttpUtils.post().url(url)
                .addParams("user_id", user_id)
                .addParams("token", token)
                .addParams("device_id", device_id)
                .addParams("os_version", os_version).build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        handler.sendEmptyMessageDelayed(NET_ERROR, 500);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onmsg", "onmsg json = " + jsonString);

                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {

                                startNextActivity();

                            } else {

                                JSONObject jsonObject = new JSONObject(
                                        jsonString);

                                String status = jsonObject.getString("status");

                                if (status.equals("true")) {

                                    String only_key = jsonObject.getJSONObject(
                                            "data").getString("only_key");

                                    if (only_key == null || only_key.equals("null")) {


                                        only_key = "";
                                    }
                                    map.put("only_key", only_key);
                                    Message message = Message.obtain();
                                    message.obj = map;
                                    device_handler.sendMessage(message);

                                } else {

                                    startNextActivity();

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getAppVersion();

    }

    private void getAppVersion() {
        ApiFactory.getAppInfoApi()
                .getAppVersion("android")
                .enqueue(new BaseCallBack<AppVersionEntity>() {
                    @Override
                    protected void onResponse(AppVersionEntity body) {
                        if (body == null) {
                            Editor edit;
                            edit = sp.edit();
                            edit.clear();
                            edit.commit();

                            Editor edit2;
                            edit2 = sp2.edit();
                            edit2.clear();
                            edit2.commit();
                            Editor edit3;
                            edit3 = sp3.edit();
                            edit3.clear();
                            edit3.commit();

                            MyInterceptor.sessionToken = "";
                            startNextActivity();

                        } else {
                            mAppVersion = body.getAppVersion();
                            mDownloadUrl = body.getDownloadUrl();
                            versionBrief = body.getUpdateNotice();
                            if (!Common.getVerName(WelcomeActivity.this).equals(mAppVersion)) {
                                showUpdateDialog(body.getUpdateMode());
                            } else {
                                if (loginString.equals("none")) {
                                    final String Device_URL = IpConfig.getUri("saveDeviceMsg");
                                    setDevice_data(Device_URL);
                                } else {
                                    final String PRODUCT_URL = IpConfig
                                            .getUri("getMemberLogin");
                                    username = sp.getString("USER_NAME", "none");
                                    pwMd5 = sp.getString("pwMd5", "none");
                                    key_value.put("mobile", username);
                                    key_value.put("password", pwMd5);
                                    setdata(PRODUCT_URL);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AppVersionEntity> call, Throwable t) {
                        Editor edit;
                        edit = sp.edit();
                        edit.clear();
                        edit.commit();

                        Editor edit2;
                        edit2 = sp2.edit();
                        edit2.clear();
                        edit2.commit();
                        Editor edit3;
                        edit3 = sp3.edit();
                        edit3.clear();
                        edit3.commit();

                        MyInterceptor.sessionToken = "";
                        handler.sendEmptyMessageDelayed(NET_ERROR, 500);
                    }
                });
    }

    protected void downAppFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();

                    pBar.setMax((int) length);// 设置进度条的最大值
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is == null) {
                        throw new RuntimeException("isStream is null");
                    }
                    File file = new File(getExternalFilesDir(null).getAbsolutePath(), appName);
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int ch = -1;
                    int count = 0;
                    do {
                        ch = is.read(buf);
                        if (ch <= 0)
                            break;
                        count += ch;
                        if (length > 0) {
                            pBar.setProgress(count);

                            float all = (float) length / 1024 / 1024;
                            float percent = (float) count / 1024 / 1024;
                            pBar.setProgressNumberFormat(String.format("%.2fM/%.2fM", percent, all));

                        }
                        fileOutputStream.write(buf, 0, ch);
                    } while (true);
                    is.close();
                    fileOutputStream.close();
                    haveDownLoad();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    protected void haveDownLoad() {

        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                // 弹出警告框 提示是否安装新的版本
                Dialog installDialog = new AlertDialog.Builder(
                        WelcomeActivity.this)
                        .setTitle("下载完成")
                        .setMessage("是否安装新的应用")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        installNewApk();
                                        finish();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        finish();
                                    }
                                }).create();
                installDialog.setCancelable(false);
                installDialog.setCanceledOnTouchOutside(false);
                installDialog.show();
            }
        });
    }

    // 安装新的应用
    protected void installNewApk() {
        // TODO Auto-generated method stub
        File apkfile = new File(getExternalFilesDir(null).getAbsolutePath(), appName);
        if ((apkfile.exists() && apkfile.isFile())) {
            Uri apkUri;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", apkfile);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                apkUri = Uri.fromFile(apkfile);
            }
            intent.setDataAndType(apkUri,
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }

    public void startNextActivity() {

        String AD_PATH
                = getExternalFilesDir(null).getAbsolutePath() + "/MFAd/" +
                AdFileUtils.getImgName(AdPreference.getInstance().getSplashAdPage().getImg());
        File file = new File(AD_PATH);
        Bitmap bm = BitmapFactory.decodeFile(AD_PATH);

        boolean isAdShow = AdPreference.getInstance().getIfShowAd();

        if (isFirstUse || old_install_Code == 0
                || current_versionCode > old_install_Code) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else {

            if (file.exists() && bm != null && isAdShow) {
                Intent intent = new Intent(WelcomeActivity.this, SplashAdActivity.class);
                startActivity(intent);
            } else {
                AdFileUtils.deleteFile(AD_PATH);

                cancelFullScreen();
                Intent intent = new Intent(
                        WelcomeActivity.this,
                        AllPageActivity.class);
                startActivity(intent);
            }
        }

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ver_handler.removeCallbacksAndMessages(null);
        device_handler.removeCallbacksAndMessages(null);
        cookie_handler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }

    private void cancelFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    }

}
