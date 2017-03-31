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
import android.provider.Settings.Secure;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
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
import retrofit2.Response;

import static com.cloudhome.network.okhttp.interceptor.MyInterceptor.device_id;

public class WelcomeActivity extends BaseActivity {
    private static final String APP_NAME_DOWNLOADED = "NewAppSample.apk";
    private String mUsername;
    private String mPassword;
    private String mLoginState;
    private String mUserId;
    private String mToken;
    private Map<String, String> mParams = new HashMap<>();
    private ProgressDialog mProgressDialog;
    private String mDownloadUrl = "";
    private String mAppVersion = "";
    private String mVersionBrief = "";
    private boolean mIsFirstUse;
    private int mOldVersionCode;
    private int mNewVersionCode;
    private String mDeviceId;
    private String mBuildVersion;
    private String mGetMemberLoginUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ifShowGuide();
        ifShowAd();
        initData();
        requestPhoneStatePermission();
    }

    private void initData() {
        mLoginState = sp.getString("Login_STATE", "none");
        mUserId = sp.getString("Login_UID", "");
        mToken = sp.getString("Login_TOKEN", "");
        Editor editor4 = sp4.edit();
        mGetMemberLoginUrl = IpConfig.getUri("getMemberLogin");

        if (mToken.equals("null") || mToken.equals("")) {
            MyInterceptor.sessionToken = "";
        } else {
            MyInterceptor.sessionToken = mToken;
        }

        editor4.putInt("page", 0);
        editor4.commit();
        mBuildVersion = Build.VERSION.RELEASE;
    }


    private void requestPhoneStatePermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        requestPermissions(perms, new PermissionListener() {
            @Override
            public void onGranted() {
                TelephonyManager tm = (TelephonyManager) getBaseContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                mDeviceId = tm.getDeviceId() + "";
                device_id = mDeviceId;
                getAppVersion();
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                if (mDeviceId == null || mDeviceId.equals("")) {
                    mDeviceId = Secure.getString(WelcomeActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    device_id = mDeviceId;
                }
                getAppVersion();
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                if (mDeviceId == null || device_id.equals("")) {
                    mDeviceId = Secure.getString(WelcomeActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    device_id = mDeviceId;

                }
                getAppVersion();
            }
        });
    }

    private void ifShowGuide() {
        SharedPreferences preferences = getSharedPreferences("isFirstUse", MODE_PRIVATE);
        mIsFirstUse = preferences.getBoolean("isFirstUse", true);
        mNewVersionCode = Common.getVerCode(getApplicationContext());
        mOldVersionCode = preferences.getInt("old_install_Code", 0);
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
                            AdPreference.getInstance().saveIfShowAd("1".equals(adBean.getIsShow()));
                        }
                    }
                });
    }

    public void showUpdateDialog(String updateMode) {
        // 把json中的“\n”转换成换行
        mVersionBrief = mVersionBrief.replace("\\n", "\n");
        Builder updateDialog = new AlertDialog.Builder(WelcomeActivity.this);
        updateDialog.setTitle("发现新版本" + mAppVersion + "，是否更新？")
                .setMessage(mVersionBrief)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressBar();
                    }
                });

        if ("1".equals(updateMode)) {
            updateDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 消掉对话框
                            if (mLoginState.equals("none")) {
                                final String Device_URL = IpConfig.getUri("saveDeviceMsg");
                                setDeviceData(Device_URL);
                            } else {
                                mUsername = sp.getString("USER_NAME", "none");
                                mPassword = sp.getString("pwMd5", "none");
                                mParams.put("mobile", mUsername);
                                mParams.put("password", mPassword);
                                setdata(mGetMemberLoginUrl);
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
        mProgressDialog = new ProgressDialog(WelcomeActivity.this);
        mProgressDialog.setTitle("正在下载");
        mProgressDialog.setMessage("请稍后...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        downAppFile(mDownloadUrl);
    }

    private void setdata(String url) {
        OkHttpUtils.post()
                .url(url)
                .params(mParams)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        clearData();
                        startNextActivity();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response == null || response.equals("")
                                    || response.equals("null")) {
                                clearData();
                                startNextActivity();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");

                                if (errcode.equals("0")) {
                                    final String Device_URL = IpConfig.getUri("saveDeviceMsg");
                                    setDeviceData(Device_URL);
                                } else {
                                    clearData();
                                    startNextActivity();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }

    private void setDeviceData(String url) {
        OkHttpUtils.post().url(url)
                .addParams("user_id", mUserId)
                .addParams("token", mToken)
                .addParams("device_id", mDeviceId)
                .addParams("os_version", mBuildVersion).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        startNextActivity();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (!(response == null || response.equals("")
                                    || response.equals("null"))) {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                if (status.equals("true")) {
                                    String only_key = jsonObject.getJSONObject("data")
                                            .getString("only_key");
                                    if (only_key == null || only_key.equals("null")) {
                                        only_key = "";
                                    }
                                    MyApplication.only_key = only_key;
                                }
                            }
                            startNextActivity();
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
        ApiFactory.getMiscApi()
                .getAppVersion("android")
                .enqueue(new BaseCallBack<AppVersionEntity>() {
                    @Override
                    protected void onResponse(AppVersionEntity body) {
                    }

                    @Override
                    public void onResponse(retrofit2.Call<AppVersionEntity> call,
                                           Response<AppVersionEntity> response) {
                        if (response == null || response.body() == null
                                || response.body().getAppVersion() == null) {
                            clearData();
                            startNextActivity();
                        } else {
                            AppVersionEntity body = response.body();
                            mAppVersion = body.getAppVersion();
                            mDownloadUrl = body.getDownloadUrl();
                            mVersionBrief = body.getUpdateNotice();
                            if (Common.getVerName(WelcomeActivity.this).compareTo(mAppVersion) < 0) {
                                showUpdateDialog(body.getUpdateMode());
                            } else {
                                if (mLoginState.equals("none")) {
                                    final String Device_URL = IpConfig.getUri("saveDeviceMsg");
                                    setDeviceData(Device_URL);
                                } else {
                                    mUsername = sp.getString("USER_NAME", "none");
                                    mPassword = sp.getString("pwMd5", "none");
                                    mParams.put("mobile", mUsername);
                                    mParams.put("password", mPassword);
                                    setdata(mGetMemberLoginUrl);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AppVersionEntity> call, Throwable t) {
                        clearData();
                        startNextActivity();
                    }
                });
    }

    private void clearData() {
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
    }

    protected void downAppFile(final String url) {
        mProgressDialog.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();

                    mProgressDialog.setMax((int) length);// 设置进度条的最大值
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is == null) {
                        throw new RuntimeException("isStream is null");
                    }
                    File file = new File(getExternalFilesDir(null).getAbsolutePath(), APP_NAME_DOWNLOADED);
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
                            mProgressDialog.setProgress(count);

                            float all = (float) length / 1024 / 1024;
                            float percent = (float) count / 1024 / 1024;
                            mProgressDialog.setProgressNumberFormat(String.format("%.2fM/%.2fM", percent, all));
                        }
                        fileOutputStream.write(buf, 0, ch);
                    } while (true);
                    is.close();
                    fileOutputStream.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            installApk();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    protected void installApk() {
        mProgressDialog.cancel();
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

    private void installNewApk() {
        File apkfile = new File(getExternalFilesDir(null).getAbsolutePath(), APP_NAME_DOWNLOADED);
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

    private void startNextActivity() {
        String adPath
                = getExternalFilesDir(null).getAbsolutePath() + "/MFAd/" +
                AdFileUtils.getImgName(AdPreference.getInstance().getSplashAdPage().getImg());
        File file = new File(adPath);
        Bitmap bm = BitmapFactory.decodeFile(adPath);

        boolean isAdShow = AdPreference.getInstance().getIfShowAd();

        if (mIsFirstUse || mOldVersionCode == 0
                || mNewVersionCode > mOldVersionCode) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else {
            if (file.exists() && bm != null && isAdShow) {
                Intent intent = new Intent(WelcomeActivity.this, SplashAdActivity.class);
                startActivity(intent);
            } else {
                AdFileUtils.deleteFile(adPath);
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

    private void cancelFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
