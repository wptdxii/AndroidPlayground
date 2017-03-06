package com.cloudhome.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.BuildConfig;
import com.cloudhome.R;
import com.cloudhome.network.retrofit.callback.BaseCallBack;
import com.cloudhome.network.retrofit.entity.AppVersionEntity;
import com.cloudhome.network.retrofit.service.ApiFactory;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;

public class AboutActivity extends BaseActivity implements OnClickListener {
    private AlertDialog dialog;
    private TextView tv_version_code;
    private ImageView iv_right_arrow1;
    private Dialog dialog1;//请求网络
    private String downPath = "";
    private boolean isNewest = true;
    private ProgressDialog pBar;
    private String appName = "NewAppSample.apk";
    private String versionBrief = "";
    private TextView  tv_bootom_description;
    //是否安装
    private AlertDialog dialog6;
    //二维码图片
    private ImageView iv_two_dimension_code;
    private Handler handler = new Handler();

    private Handler barCodeHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String img = (String) msg.obj;
                    Glide.with(AboutActivity.this).load(IpConfig.getIp3() + img).into(iv_two_dimension_code);
                    break;
                case 1:
                    Toast.makeText(AboutActivity.this, "获取二维码失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(AboutActivity.this, "获取二维码失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        RelativeLayout iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        TextView top_title = (TextView) findViewById(R.id.tv_text);
        ImageView iv_right = (ImageView) findViewById(R.id.iv_right);
        top_title.setText(R.string.app_name);
        iv_right.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(this);

        RelativeLayout rl_check_new_version = (RelativeLayout) findViewById(R.id.rl_check_new_version);
        tv_version_code = (TextView) findViewById(R.id.tv_version_code);
        iv_right_arrow1 = (ImageView) findViewById(R.id.iv_right_arrow1);
        RelativeLayout rl_function_introduce = (RelativeLayout) findViewById(R.id.rl_function_introduce);
        RelativeLayout rl_join_us = (RelativeLayout) findViewById(R.id.rl_join_us);
        RelativeLayout rl_score_us = (RelativeLayout) findViewById(R.id.rl_score_us);
        iv_two_dimension_code = (ImageView) findViewById(R.id.iv_two_dimension_code);

        tv_bootom_description  = (TextView) findViewById(R.id.tv_bootom_description);
        tv_bootom_description.setText(Html.fromHtml(getResources().getString(
                R.string.about_description)));

        rl_check_new_version.setOnClickListener(this);
        rl_function_introduce.setOnClickListener(this);
        rl_join_us.setOnClickListener(this);
        rl_score_us.setOnClickListener(this);

        dialog1 = new Dialog(this, R.style.progress_dialog);
        dialog1.setContentView(R.layout.progress_dialog);
        dialog1.setCancelable(true);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog1.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");
        dialog1.show();

        getAppVersion();

        String BarCode_path = IpConfig.getUri("getBarCode");
        getBarCode(BarCode_path);
    }

    private void getAppVersion() {
        ApiFactory.getAppInfoApi()
                .getAppVersion("android")
                .enqueue(new BaseCallBack<AppVersionEntity>() {
                    @Override
                    protected void onResponse(AppVersionEntity body) {
                        if (body == null) {
                            dialog1.dismiss();
                            Toast.makeText(AboutActivity.this, "版本检测失败", Toast.LENGTH_SHORT).show();
                            String versionName = Common.getVerName(getApplicationContext());
                            tv_version_code.setText("当前版本" + versionName);
                            tv_version_code.setTextColor(getResources().getColor(R.color.color6));
                            iv_right_arrow1.setVisibility(View.INVISIBLE);
                            isNewest = true;
                        } else {
                            dialog1.dismiss();
                            versionBrief = body.getUpdateNotice();
                            downPath = body.getDownloadUrl();
                            String verName = Common.getVerName(getApplicationContext());
                            if (!verName.equals(body.getAppVersion())) {
                                //显示新版本号，提示更新
                                tv_version_code.setText("有新版本");
                                tv_version_code.setTextColor(getResources().getColor(R.color.orange_red));
                                iv_right_arrow1.setVisibility(View.VISIBLE);
                                isNewest = false;
                            } else {
                                tv_version_code.setText("已是最新版本" + verName);
                                tv_version_code.setTextColor(getResources().getColor(R.color.color6));
                                iv_right_arrow1.setVisibility(View.INVISIBLE);
                                isNewest = true;
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_check_new_version:
                if (isNewest) {
                    return;
                } else {
                    showUpdateDialog();
                }
                break;
            case R.id.rl_function_introduce:
                intent = new Intent(AboutActivity.this, FunctionIntroduceActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_score_us:
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    String packageName = getApplication().getPackageName();
                    i.setData(Uri.parse("market://details?id=" + packageName));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, "您的手机上没有安装Android应用市场", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.rl_join_us:
                intent = new Intent(AboutActivity.this, JoinUsActivity.class);
                startActivity(intent);
                break;

        }
    }


    private void showUpdateDialog() {
        AlertDialog.Builder build = new Builder(AboutActivity.this);
        View contentView = View.inflate(AboutActivity.this, R.layout.dialog_update_prompt, null);
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


    private void getBarCode(String url) {
        OkHttpUtils.post()//
                .url(url)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);

                        Message message = Message.obtain();
                        message.what = 2;
                        barCodeHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        String jsonString = response;

                        try {
                            if (jsonString.equals("") || jsonString.equals("null")) {
                                Message message = Message.obtain();
                                message.what = 1;
                                barCodeHandler.sendMessage(message);
                            } else {
                                Log.d("444444", jsonString + "444444");
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONObject data = jsonObject.getJSONObject("data");// 获取JSONArray
                                String img = data.getString("img");
                                Message message = Message.obtain();
                                message.obj = img;
                                message.what = 0;
                                barCodeHandler.sendMessage(message);
                            }
                        } catch (JSONException e) {
                            // TODO 自动生成的 catch 块
                            e.printStackTrace();

                        }
                    }
                });


    }


    private void showProgressBar() {
        pBar = new ProgressDialog(AboutActivity.this);
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

                    Log.d("DownTag", (int)length+"");
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
                Builder installDialog = new AlertDialog.Builder(AboutActivity.this);

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
}
