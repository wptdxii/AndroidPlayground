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
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class AboutActivity extends BaseActivity {
    private static final String APP_NAME_DOWNLOADED = "NewAppSample.apk";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_ver_code)
    TextView tvVerCode;
    @BindView(R.id.iv_check_ver_arrow)
    ImageView ivCheckVerArrow;
    @BindView(R.id.tv_copyright)
    TextView tvCopyright;
    @BindColor(R.color.color6)
    int colorGray;
    @BindColor(R.color.orange_red)
    int colorOrange;

    private AlertDialog mUpdateDialog;
    private Dialog mLoadingDialog;
    private ProgressDialog mDownloadingProgressBar;
    private String mDownloadUrl = "";
    private boolean isNewest = true;
    private String versionBrief = "";
    private AlertDialog mInstallDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        tvTitle.setText(R.string.app_name);
        ivShare.setVisibility(View.INVISIBLE);
        initLoadingDialog();
        Spanned copyText = fromhtml(getString(R.string.about_description));
        tvCopyright.setText(copyText);
    }

    private void initLoadingDialog() {
        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView dialogText = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        dialogText.setText(R.string.dialog_loading);
        mLoadingDialog.show();
    }

    @SuppressWarnings("deprecation")
    private Spanned fromhtml(String html) {
        Spanned spanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(html);
        }
        return spanned;
    }

    private void initData() {
        getAppVersion();
        String qrCodeUrl = IpConfig.getUri("getBarCode");
        getQrCode(qrCodeUrl);
    }

    private void getAppVersion() {
        ApiFactory.getMiscApi()
                .getAppVersion("android")
                .enqueue(new BaseCallBack<AppVersionEntity>() {
                    @Override
                    protected void onResponse(AppVersionEntity body) {
                        if (body == null) {
                            mLoadingDialog.dismiss();
                            Toast.makeText(AboutActivity.this,
                                    R.string.toast_check_ver_error, Toast.LENGTH_SHORT).show();
                            String versionName = Common.getVerName(getApplicationContext());
                            tvVerCode.setText(String.format(getString(
                                    R.string.activity_about_cur_ver), versionName));
                            tvVerCode.setTextColor(colorGray);
                            ivCheckVerArrow.setVisibility(View.INVISIBLE);
                            isNewest = true;
                        } else {
                            mLoadingDialog.dismiss();
                            versionBrief = body.getUpdateNotice();
                            mDownloadUrl = body.getDownloadUrl();
                            String verName = Common.getVerName(getApplicationContext());
                            if (!verName.equals(body.getAppVersion())) {
                                tvVerCode.setText(R.string.activity_about_ver_available);
                                tvVerCode.setTextColor(colorOrange);
                                ivCheckVerArrow.setVisibility(View.VISIBLE);
                                isNewest = false;
                            } else {
                                tvVerCode.setText(String.format(getString(
                                        R.string.activity_about_newest_ver), verName));
                                tvVerCode.setTextColor(colorGray);
                                ivCheckVerArrow.setVisibility(View.INVISIBLE);
                                isNewest = true;
                            }
                        }
                    }
                });
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.rl_check_ver)
    public void checkVersion() {
        if (!isNewest) {
            showUpdateDialog();
        }
    }

    @OnClick(R.id.rl_function)
    public void function() {
        Intent intent = new Intent(AboutActivity.this, FunctionIntroduceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_evaluate)
    public void evaluate() {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            String packageName = getApplication().getPackageName();
            i.setData(Uri.parse("market://details?id=" + packageName));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(AboutActivity.this, R.string.toast_no_market, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rl_join)
    public void join() {
        Intent intent = new Intent(AboutActivity.this, JoinUsActivity.class);
        startActivity(intent);
    }

    private void showUpdateDialog() {
        Builder build = new Builder(AboutActivity.this);
        View contentView = View.inflate(AboutActivity.this, R.layout.dialog_update_prompt, null);
        TextView tv_new_function_desc = (TextView) contentView.findViewById(R.id.tv_new_function_desc);
        Button ok = (Button) contentView.findViewById(R.id.ok);
        Button cancel = (Button) contentView.findViewById(R.id.cancel);
        versionBrief = versionBrief.replace("\\n", "\n");
        tv_new_function_desc.setText(versionBrief);
        build.setCancelable(false);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateDialog.dismiss();
            }
        });
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateDialog.dismiss();
                showProgressBar();
            }
        });
        mUpdateDialog = build.create();
        mUpdateDialog.setView(contentView, 0, 0, 0, 0);
        mUpdateDialog.show();
    }


    private void getQrCode(String url) {
        OkHttpUtils.post()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(AboutActivity.this,
                                R.string.toast_qr_code_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response.equals("") || response.equals("null")) {
                                Toast.makeText(AboutActivity.this,
                                        R.string.toast_qr_code_error, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject data = jsonObject.getJSONObject("data");
                                String img = data.getString("img");
                                Glide.with(AboutActivity.this)
                                        .load(IpConfig.getIp3() + img)
                                        .into(ivQrCode);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showProgressBar() {
        mDownloadingProgressBar = new ProgressDialog(AboutActivity.this);
        mDownloadingProgressBar.setTitle(getString(R.string.activity_about_download_pb_title));
        mDownloadingProgressBar.setMessage(getString(R.string.activity_about_download_pb_msg));
        mDownloadingProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDownloadingProgressBar.setCanceledOnTouchOutside(false);
        mDownloadingProgressBar.setCancelable(false);
        mDownloadingProgressBar.show();
        downAppFile(mDownloadUrl);
    }

    private void downAppFile(final String url) {
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();

                    mDownloadingProgressBar.setMax((int) length);
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
                            mDownloadingProgressBar.setProgress(count);
                            float all = (float) length / 1024 / 1024;
                            float percent = (float) count / 1024 / 1024;
                            mDownloadingProgressBar.setProgressNumberFormat(String.format("%.2fM/%.2fM", percent, all));
                        }
                        fileOutputStream.write(buf, 0, ch);
                    } while (true);
                    is.close();
                    fileOutputStream.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInstallDialog();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void showInstallDialog() {
        mDownloadingProgressBar.cancel();
        Builder installDialog = new Builder(AboutActivity.this);
        installDialog.setTitle(R.string.activity_about_install_dialog_title)
                .setMessage(R.string.activity_about_install_dialog_msg)
                .setPositiveButton(R.string.activity_about_install_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                installApk();
                                finish();
                            }
                        })
                .setNegativeButton(R.string.activity_about_install_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mInstallDialog.dismiss();
                            }
                        });
        mInstallDialog = installDialog.create();
        mInstallDialog.setCancelable(false);
        mInstallDialog.setCanceledOnTouchOutside(false);
        mInstallDialog.show();
    }

    private void installApk() {
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
}
