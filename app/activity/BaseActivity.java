package com.cloudhome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.ActivityStack;
import com.cloudhome.utils.IntentUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xionghu on 2016/7/27.
 * Email：965705418@qq.com
 */
public class BaseActivity extends AppCompatActivity {
    public static final int RC_PERMISSIONS = 111;
    private PermissionListener mListener;
    private boolean mNeedFinish;
    protected SharedPreferences sp;
    protected SharedPreferences sp2;
    protected SharedPreferences sp3;
    protected SharedPreferences sp4;
    protected SharedPreferences sp5;
    public static String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("userInfo", 0);
        sp2 = getSharedPreferences("otherinfo", 0);
        sp3 = getSharedPreferences("expertmicro", 0);
        sp4 = getSharedPreferences("ActivityInfo", 0);
        sp5 = getSharedPreferences("temp", 0);
        TAG = getClass().getSimpleName();
        ActivityStack.getInstance().addActivity(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onDestroy() {
        ActivityStack.getInstance().removeActivity(this);
        super.onDestroy();
    }

    public void requestPermissions(String[] permissions, PermissionListener listener) {
        if (permissions == null || permissions.length == 0)
            return;
        this.mListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(
                    new String[permissionList.size()]), RC_PERMISSIONS);
        } else {
            mListener.onGranted();
        }
    }

    private boolean shouldShowRequestPermissionRationale(String[] permisions) {
        for (String permission : permisions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_PERMISSIONS:
                if (grantResults.length > 0) {

                    List<String> deniedPermissions = new ArrayList<>();
                    for (String permission : permissions) {
                        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (!deniedPermissions.isEmpty()) {
                        checkForceRequiredPermissionDenied(deniedPermissions.toArray(new String[deniedPermissions.size()]));
                    } else {
                        mListener.onGranted();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void checkForceRequiredPermissionDenied(String[] deniedPermissions) {
        if (shouldShowRequestPermissionRationale(deniedPermissions)) {
            List<String> impermanentDeniedPermissions = new ArrayList<>();
            List<String> permanentDeniedPermissions = new ArrayList<>();
            for (String permission : deniedPermissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    impermanentDeniedPermissions.add(permission);
                } else {
                    permanentDeniedPermissions.add(permission);
                }
            }
            mListener.onDenied(impermanentDeniedPermissions.toArray(new String[impermanentDeniedPermissions.size()]),
                    permanentDeniedPermissions.toArray(new String[permanentDeniedPermissions.size()]));
        } else {
            mListener.onPermanentDenied(deniedPermissions);
        }
    }

    public void showPermissionSettingDialog(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = IntentUtils.getAppDetailsSettingsIntent(
                                getApplicationContext().getPackageName());
                        startActivity(intent);

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void showRequestPermissionRationale(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
