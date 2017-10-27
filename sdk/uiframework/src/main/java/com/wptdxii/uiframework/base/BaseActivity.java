package com.wptdxii.uiframework.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.wptdxii.ext.util.ActivityStack;
import com.wptdxii.ext.util.AppStatusTracker;
import com.wptdxii.uiframework.callback.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


/**
 * Created by wptdxii on 2016/7/7 0007.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final int RC_PERMISSIONS = 0x100;
    private PermissionListener mListener;

    @Override
    protected void onStart() {
        super.onStart();
        if (AppStatusTracker.getInstance().checkIfShowGesture()) {
            // TODO startActivity to GestureActivity
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().addActivity(this);
        // 需要在对应的场景先设置AppStatus
        switch (AppStatusTracker.getInstance().getAppStatus()) {
            case AppStatusTracker.STATUS_FORCE_KILLED:
                restartApp();
                break;
            case AppStatusTracker.STATUS_KICK_OUT:
                kickout();
                break;
            case AppStatusTracker.STATUS_LOGOUT:
            case AppStatusTracker.STATUS_OFFLINE:
            case AppStatusTracker.STATUS_ONLINE:
                setContentView(onCreateContentView());
                onCreateContent(savedInstanceState);
                break;
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        ActivityStack.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /**
     * set content view Id
     * @return
     */
    @LayoutRes
    protected abstract int onCreateContentView();

    protected abstract void onCreateContent(Bundle savedInstanceState);

    /**
     * Token失效或者被挤下线执行的操作
     */
    protected void kickout() {
        // TODO show dialog to confirm to ContentActivity or LoginActivity
    }

    /**
     * 重启app
     */
    protected void restartApp() {
        // TODO startActivity to ContentActivity
    }


    //    protected void initToolbar(@StringRes int titleResId, @MenuRes int menuId, final int mode) {
    //        if (mode != MODE_NONE) {
    //            toolbar = (Toolbar) findViewById(R.id.toolbar);
    //            toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
    //            if (mode == MODE_BACK) {
    //                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    //            } //else if(){}
    //
    //            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View view) {
    //                    onNavigationBtnClick(mode);
    //                }
    //            });
    //
    //            initToolbarTitle(titleResId);
    //            initToolbarMenu(menuId);
    //        }
    //    }

    //    protected void initToolbarMenu(@MenuRes int menuId) {
    //        if (toolbar != null) {
    //            toolbar.getMenu().clear();
    //            if (menuId > 0) {
    //                toolbar.inflateMenu(menuId);
    //                toolbar.setOnMenuItemClickListener(this);
    //            }
    //        }
    //    }

    //    protected void initToolbarTitle(@StringRes int titleResId) {
    //        if (titleResId > 0 && toolbarTitle != null) {
    //            toolbarTitle.setText(titleResId);
    //        }
    //    }

    //    protected void onNavigationBtnClick(int mode) {
    //        switch (mode) {
    //            case MODE_BACK:
    //                finish();
    //                break;
    //        }
    //    }

    /**
     * findViewById without force transform
     *
     * @param resId
     * @param <T>
     * @return
     */
    public <T extends View> T findView(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    /**
     * call this method when need to requesst runtime permissions in Activity
     *
     * @param permissions
     * @param listener
     */
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null) {
                    return super.onOptionsItemSelected(item);
                }
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
