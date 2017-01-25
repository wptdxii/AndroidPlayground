package com.cloudhome.activity;

import android.annotation.SuppressLint;
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
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudhome.BuildConfig;
import com.cloudhome.R;
import com.cloudhome.utils.InterceptingWebViewClient;
import com.cloudhome.utils.IpConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressLint("SetJavaScriptEnabled")
public class ClauseDetailActivity extends BaseActivity {
    private ImageView hot_item_back;
    private WebView wb_clause_detail;
    private String clause_id;
    private String token;
    private String user_id;
    private String user_id_encode;
    private String webUrl;
    private String fileUrl;
    private ProgressDialog pBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clause_detail);
        Intent intent = getIntent();
        clause_id = intent.getStringExtra("clause_id");
        fileUrl = intent.getStringExtra("url");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        webUrl = IpConfig.getIp3() + "/index.php?user_id=" + user_id_encode + "&token=" + token + "&mod=getClauseDetail&clause_id=" + clause_id;
        //		webUrl="http://jr.sinosig.com/apitest/doc/PAJH01.pdf";
        if ("".equals(fileUrl) || "null".equals(fileUrl) || null == fileUrl) {
            init();
        } else {
            downLoadFile();
        }


    }


    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    void init() {


        hot_item_back = (ImageView) findViewById(R.id.hot_item_back);
        wb_clause_detail = (WebView) findViewById(R.id.wb_clause_detail);
        hot_item_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }
        });

        WebSettings setting = wb_clause_detail.getSettings();
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        setting.setAppCachePath(appCachePath);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);

        wb_clause_detail.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        wb_clause_detail.setWebViewClient(new InterceptingWebViewClient(this, wb_clause_detail, true));


        wb_clause_detail.loadUrl(webUrl);
    }

    private void downLoadFile() {
        // TODO Auto-generated method stub
        //提示对话框
        AlertDialog.Builder builder = new Builder(ClauseDetailActivity.this);
        builder.setTitle("下载提示").setMessage("是否下载条款文件?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pBar = new ProgressDialog(ClauseDetailActivity.this);
                pBar.setTitle("正在下载");
                pBar.setMessage("请稍后...");
                pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                downAppFile(IpConfig.getIp3() + "/" + fileUrl);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }).show();
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
                    File file = new File(getExternalFilesDir(null).getAbsolutePath(), "sm.pdf");
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


    protected void haveDownLoad() {

        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                // 弹出警告框 提示是否安装新的版本
                Dialog installDialog = new Builder(
                        ClauseDetailActivity.this)
                        .setTitle("产品条款下载完成")
                        .setMessage("是否使用第三方工具打开此pdf文件")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        File file = new File(getExternalFilesDir(null).getAbsolutePath(), "sm.pdf");
                                        Intent intent = getPdfFileIntent(file);
                                        if (intent != null) {
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(ClauseDetailActivity.this, "无法打开此pdf文件",
                                                    Toast.LENGTH_SHORT).show();
                                        }
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
                installDialog.show();
            }
        });
    }

    //android获取一个用于打开PDF文件的intent
    public Intent getPdfFileIntent(File file) {
        if (file.exists() && file.isFile()) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, "application/pdf");
            return intent;
        } else {
            return null;
        }
    }
}
