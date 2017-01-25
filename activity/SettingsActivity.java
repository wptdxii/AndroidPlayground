package com.cloudhome.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.BuildConfig;
import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.event.UnreadArticleEvent;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.DataCleanManager;
import com.cloudhome.utils.IpConfig;
import com.umeng.socialize.UMShareAPI;
import com.zcw.togglebutton.ToggleButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class SettingsActivity extends BaseActivity {

    private static final String action = "jason.broadcast.action";
    private AlertDialog dialog;
    private TextView quit_login;
    private RelativeLayout setting_back;
    private TextView tv_text;
    private ImageView iv_right;
    private Dialog dialog1;
    private ToggleButton toggle_micro_card;
    private ToggleButton toggle_allow_other_see;
    private String isShow_in_expertlist;//是否在专家列表展示
    private String isShowMicroCard;//是否显示微名片
    private String toggleType;
    private String clean_data = "";
    private String downPath = "";
    private boolean isNewest = true;
    private String versionBrief = "";
    private ProgressDialog pBar;
    //是否安装
    private AlertDialog dialog6;
    private String appName = "NewAppSample.apk";
    private TextView tv_new_version_left;
    private ImageView iv_new_version;
    private ImageView iv_clear_cache;
    private RelativeLayout setting_rel4;
    private RelativeLayout setting_rel5;
    //展示微名片
    private RelativeLayout rl_micro_card;
    //允许他人看到我
    private RelativeLayout rl_allow_other_see;
    //新版本检测
    private RelativeLayout rl_new_version_check;
    //清除缓存
    private RelativeLayout rl_clear_cache;
    private TextView tv_clear_cache_right;
    //修改密码
    private RelativeLayout rl_modify_password;
    //退出登录
    private RelativeLayout rl_quit;

    private String user_id;
    private String token;
    private Map<String, String> key_value = new HashMap<String, String>();
    private String device_id, os_version;
    //用户是否认证
    private String state;
    private BaseUMShare share;
    private String shareTitle,shareUrl,shareDesc;
    private int shareLogo;
    private static final String TAG = "SettingsActivity";

    @SuppressLint("HandlerLeak")
    private Handler device_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            MyApplication.only_key = data.get("only_key");
            finish();

        }

    };

    private Handler ver_handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    dialog1.dismiss();
                    Map<String, String> data = (Map<String, String>) msg.obj;
                    String errcode = data.get("errcode");
                    String VersionName = data.get("VersionName");
                    String VersionCode = data.get("VersionCode");
                    String VersionShowFlag = data.get("VersionShowFlag");
                    versionBrief = data.get("versionBrief");
                    downPath = data.get("url");

                    int verCode = Common.getVerCode(getApplicationContext());
                    String verName = Common.getVerName(getApplicationContext());
                    int newVerCode = Integer.valueOf(VersionCode);
                    Log.d("newVerCode", newVerCode + "88888");
                    Log.d("verCode", verCode + "88888");
                    if (newVerCode > verCode) {
                        //显示新版本号，提示更新
                        tv_new_version_left.setText("有新版本");
                        iv_new_version.setVisibility(View.VISIBLE);
                        isNewest = false;
                    } else {
                        tv_new_version_left.setText("已是当前最新版本");
                        iv_new_version.setVisibility(View.GONE);
                        isNewest = true;
                    }
                    break;
                case 1:
                    dialog1.dismiss();
                    Toast.makeText(SettingsActivity.this, "版本检测失败", Toast.LENGTH_SHORT).show();
                    String versionName = Common.getVerName(getApplicationContext());
                    tv_new_version_left.setText("当前版本" + versionName);
                    iv_new_version.setVisibility(View.INVISIBLE);
                    isNewest = true;
                    break;
            }
        }
    };

    private Handler logOutHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
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

                    Editor editor4 = sp4.edit();
                    editor4.commit();
                    //发送登陆状态改变的广播
                    EventBus.getDefault().post(new LoginEvent());
                    //隐藏未读文章小红点
                    EventBus.getDefault().post(new UnreadArticleEvent(0));
                    MyInterceptor.sessionToken = "";
                    final String Device_URL = IpConfig.getUri("saveDeviceMsg");
                    setDevice_data(Device_URL);
                    break;
                case 1:
                    Toast.makeText(SettingsActivity.this, "退出登录失败",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;
            String errcode = data.get("errcode");
            if (errcode.equals("0")) {
                if (toggleType.equals("2")) {
                    if (isShow_in_expertlist.equals("1")) {
                        isShow_in_expertlist = "0";
                        toggle_allow_other_see.setToggleOff();
                    } else if (isShow_in_expertlist.equals("0")) {
                        isShow_in_expertlist = "1";
                        toggle_allow_other_see.setToggleOn();
                    }
                    Editor editor1 = sp3.edit();
                    editor1.putString("isShow_in_expertlist", isShow_in_expertlist);
                    editor1.commit();
                } else if (toggleType.equals("1")) {
                    if (isShowMicroCard.equals("1")) {
                        isShowMicroCard = "0";
                        toggle_micro_card.setToggleOff();
                    } else if (isShowMicroCard.equals("0")) {
                        isShowMicroCard = "1";
                        toggle_micro_card.setToggleOn();
                    }
                    Editor editor1 = sp3.edit();
                    editor1.putString("isShowMicroCard", isShowMicroCard);
                    editor1.commit();
                }

            } else {
                if (toggleType.equals("1")) {
                    Toast.makeText(SettingsActivity.this, "微名片不展示设置失败！", Toast.LENGTH_LONG).show();
                } else if (toggleType.equals("2")) {
                    Toast.makeText(SettingsActivity.this, "允许他人看到我设置失败！", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private Handler errcode_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;
            String status = data;
            if (status.equals("false")) {
                Toast.makeText(SettingsActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
            }
        }

    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
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
                init();
                initEvent();
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Secure.getString(SettingsActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;
                }
                init();
                initEvent();
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Secure.getString(SettingsActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;

                }
                init();
                initEvent();
            }
        });
    }

    private void init() {
        state = sp.getString("Login_CERT", "");
        isShow_in_expertlist = sp3.getString("isShow_in_expertlist", "1");
        os_version = android.os.Build.VERSION.RELEASE;
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        shareTitle=getString(R.string.share_app_title);
        shareUrl=getString(R.string.share_app_url);
        shareDesc=getString(R.string.share_app_desc);
        shareLogo=R.drawable.icon_share_logo;
        share=new BaseUMShare(this,shareTitle,shareDesc,shareUrl,shareLogo);
        share.initShare();

        toggle_micro_card = (ToggleButton) findViewById(R.id.toggle_micro_card);
        toggle_allow_other_see = (ToggleButton) findViewById(R.id.toggle_allow_other_see);

        quit_login = (TextView) findViewById(R.id.quit_login);
        setting_rel4 = (RelativeLayout) findViewById(R.id.setting_rel4);
        setting_rel5 = (RelativeLayout) findViewById(R.id.setting_rel5);
        rl_micro_card = (RelativeLayout) findViewById(R.id.rl_micro_card);
        rl_allow_other_see = (RelativeLayout) findViewById(R.id.rl_allow_other_see);
        setting_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_new_version_check = (RelativeLayout) findViewById(R.id.rl_new_version_check);
        rl_clear_cache = (RelativeLayout) findViewById(R.id.rl_clear_cache);
        rl_modify_password=(RelativeLayout) findViewById(R.id.rl_modify_password);
        rl_quit = (RelativeLayout) findViewById(R.id.rl_quit);


        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText("设置");
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setVisibility(View.INVISIBLE);

        tv_new_version_left = (TextView) findViewById(R.id.tv_new_version_left);
        iv_new_version = (ImageView) findViewById(R.id.iv_new_version);
        iv_clear_cache = (ImageView) findViewById(R.id.iv_clear_cache);
        tv_clear_cache_right = (TextView) findViewById(R.id.tv_clear_cache_right);

        key_value.put("user_id", user_id);
        key_value.put("token", token);

        Log.i(TAG, user_id);
        Log.i(TAG, token);

    }

    private void initEvent() {
        if (state.equals("00")) {
            //未认证用户默认开启。已认证用户默认关闭。
            isShowMicroCard = sp3.getString("isShowMicroCard", "0");
            rl_allow_other_see.setVisibility(View.VISIBLE);
        } else {
            isShowMicroCard = sp3.getString("isShowMicroCard", "1");
            rl_allow_other_see.setVisibility(View.GONE);
        }
        try {
            clean_data = DataCleanManager.getTotalCacheSize(SettingsActivity.this);
            if (clean_data.equals("0MB")) {
                iv_clear_cache.setVisibility(View.GONE);
                Log.i("重新设置缓存", clean_data);
            } else {
                iv_clear_cache.setVisibility(View.VISIBLE);

            }
            tv_clear_cache_right.setText(clean_data);

        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog1 = new Dialog(this, R.style.progress_dialog);
        dialog1.setContentView(R.layout.progress_dialog);
        dialog1.setCancelable(true);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog1.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");
        dialog1.show();
        //获取版本信息
        String path = IpConfig.getUri("getVersion");
        setVersiondata(path);

        //展示微名片
        if (isShowMicroCard.equals("0")) {
            toggle_micro_card.setToggleOff();
        } else if (isShowMicroCard.equals("1")) {
            toggle_micro_card.setToggleOn();
        }
        toggle_micro_card.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                toggleType = "1";
                if (isShowMicroCard.equals("0")) {
                    key_value.put("setValue", "1");
                } else if (isShowMicroCard.equals("1")) {
                    key_value.put("setValue", "0");
                }
                String PRODUCT_URL = IpConfig.getUri("setIsShowCard");
                setdata(PRODUCT_URL);
            }
        });


        //允许他人看到我
        if (isShow_in_expertlist.equals("0")) {
            toggle_allow_other_see.setToggleOff();
        } else if (isShow_in_expertlist.equals("1")) {
            toggle_allow_other_see.setToggleOn();
        }
        toggle_allow_other_see.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                toggleType = "2";
                if (isShow_in_expertlist.equals("0")) {
                    key_value.put("setValue", "1");
                } else if (isShow_in_expertlist.equals("1")) {
                    key_value.put("setValue", "0");
                }
                String PRODUCT_URL = IpConfig.getUri("setIsShowInExpertlist");
                setdata(PRODUCT_URL);
            }
        });




        setting_rel4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(SettingsActivity.this,
                        AboutActivity.class);
                startActivity(intent);


            }
        });
//        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
//                SHARE_MEDIA.DOUBAN);

        setting_rel5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                share.openShare();

            }
        });

        setting_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        rl_quit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 先调用登出接口
//                String logOutUrl = IpConfig.getUri("logout");
//                setDataLogout(logOutUrl);
                String logOutUrl = IpConfig.getUri2("loginout");
                setDataLogout(logOutUrl);

            }
        });

        rl_new_version_check.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNewest) {
                } else {
                    showUpdateDialog();
                }
            }
        });

        rl_clear_cache.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clean_data.equals("0MB")) {
                } else {
                    try {
                        DataCleanManager.clearAllCache(SettingsActivity.this);

                    } catch (Exception e) {
                        Log.i("清除缓存异常", e.toString());
                        e.printStackTrace();
                    } finally {
                        try {
                            clean_data = DataCleanManager.getTotalCacheSize(SettingsActivity.this);
                        } catch (Exception e) {
                            Log.i("清除缓存异常2", e.toString());
                            e.printStackTrace();
                        }
                        tv_clear_cache_right.setText(clean_data);
                        iv_clear_cache.setVisibility(View.GONE);
                        Toast.makeText(SettingsActivity.this, "缓存已清空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rl_modify_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingsActivity.this,AmendPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setDevice_data(String url) {

        OkHttpUtils.post().url(url)
                .addParams("user_id", user_id)
                .addParams("token",token)
                .addParams("device_id", device_id)
                .addParams("os_version", os_version).build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        finish();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onmsg", "onmsg json = " + jsonString);

                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {

                                finish();

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

                                    finish();

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
        share.reSetShareContent(shareTitle,shareDesc,shareUrl,shareLogo);
    }

    /**
     * 退出登录的时候，调用接口
     *
     * @param
     */
    private void setDataLogout(String url) {
        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        Toast.makeText(SettingsActivity.this, "网络连接失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        try {
                            JSONObject obj = new JSONObject(jsonString);
                            String errcode = obj.getString("errcode");
                            if ("0".equals(errcode)) {
                                logOutHandler.sendEmptyMessage(0);
                            } else {
                                logOutHandler.sendEmptyMessage(1);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            logOutHandler.sendEmptyMessage(1);
                        }
                    }
                });
    }


    //允许别人看到我
    private void setdata(String url) {
        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        Log.e("error", "获取数据异常 ", e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        try {
                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();
                                message.obj = status;
                                errcode_handler.sendMessage(message);
                            } else {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                String data = jsonObject.getString("data");
                                String errcode = jsonObject.getString("errcode");
                                map.put("errcode", errcode);
                                Log.d("44444", errcode);
                                Message message = Message.obtain();
                                message.obj = map;
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取版本信息
    private void setVersiondata(String url) {
        OkHttpUtils.post()//
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        Log.e("error", "获取数据异常 ", e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        try {
                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                Message message = Message.obtain();
                                message.what = 1;
                                ver_handler.sendMessage(message);
                            } else {
                                Log.d("444444", jsonString + "444444");
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONObject data = jsonObject.getJSONObject("data");// 获取JSONArray
                                String errcode = jsonObject.getString("errcode");
                                String VersionName = jsonObject.getString("VersionName");
                                String VersionCode = jsonObject.getString("VersionCode");
                                String VersionShowFlag = jsonObject.getString("VersionShowFlag");
                                int updateType = jsonObject
                                        .getInt("updateType");
                                String versionBrief = jsonObject
                                        .getString("versionBrief");
                                String url = data.getString("url");
                                map.put("errcode", errcode);
                                map.put("VersionName", VersionName);
                                map.put("VersionCode", VersionCode);
                                map.put("VersionShowFlag", VersionShowFlag);
                                map.put("url", url);
                                map.put("updateType", updateType + "");
                                map.put("versionBrief", versionBrief);
                                Message message = Message.obtain();
                                message.obj = map;
                                message.what = 0;
                                ver_handler.sendMessage(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void showUpdateDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(SettingsActivity.this);
        View contentView = View.inflate(SettingsActivity.this, R.layout.dialog_update_prompt, null);
        TextView tv_new_function_desc = (TextView) contentView.findViewById(R.id.tv_new_function_desc);
        Button ok = (Button) contentView.findViewById(R.id.ok);
        Button cancel = (Button) contentView.findViewById(R.id.cancel);
        versionBrief = versionBrief.replace("\\n", "\n");
        tv_new_function_desc.setText(versionBrief);
        build.setCancelable(false);

        //取消键
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 消掉对话框
                dialog.dismiss();
            }
        });

        //确定键的动作
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showProgressBar();
            }
        });

        dialog = build.create();
        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();
    }

    private void showProgressBar() {

        pBar = new ProgressDialog(SettingsActivity.this);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍后...");
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setCanceledOnTouchOutside(false);
        pBar.setCancelable(false);
        downAppFile(downPath);
    }

    private void downAppFile(final String url) {
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

    private void haveDownLoad() {

        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                // 弹出警告框 提示是否安装新的版本
                AlertDialog.Builder installDialog = new AlertDialog.Builder(SettingsActivity.this);

                installDialog.setTitle("下载完成")
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
                                        dialog6.dismiss();
                                    }
                                });
                dialog6 = installDialog.create();
                dialog6.setCancelable(false);
                dialog6.setCanceledOnTouchOutside(false);
                dialog6.show();
            }
        });
    }

    // 安装新的应用
    private void installNewApk() {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }


}
