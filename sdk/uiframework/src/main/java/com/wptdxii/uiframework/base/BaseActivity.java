package com.wptdxii.uiframework.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wptdxii.ext.util.ActivityStack;
import com.wptdxii.ext.util.AppStatusTracker;
import com.wptdxii.uiframework.R;
import com.wptdxii.uiframework.callback.PermissionListener;
import com.wptdxii.uiframework.widget.toolbarhelper.ToolbarHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wptdxii on 2016/7/7 0007.
 */
public abstract class BaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    public static final int RC_PERMISSIONS = 0x100;
    protected static final int MODE_NONE = -1;
    protected static final int MODE_BACK = 0;
    protected Toolbar toolbar;
    protected TextView toolbarTitle;
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
                setContentView(setupContentView());
                Toolbar toolbar = findView(R.id.toolbar);
                if (toolbar != null) {
                    setupToolbar(new ToolbarHelper(this, toolbar));
                }
                setupData(savedInstanceState);
                setupViews();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        ActivityStack.getInstance().removeActivity(this);
        super.onDestroy();
    }

    @LayoutRes
    protected abstract int setupContentView();

    protected abstract void setupData(Bundle savedInstanceState);

    protected abstract void setupToolbar(ToolbarHelper toolbarHelper);

    protected abstract void setupViews();

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

    /**
     * @param layoutResID
     * @param titleResId  -1 没有标题
     * @param menuId      -1 没有menu
     * @param mode        MODE_NONE 没有Toolbar
     */
    protected void setContentView(@LayoutRes int layoutResID, @StringRes int titleResId, @MenuRes int menuId, int mode) {
        super.setContentView(layoutResID);
        initToolbar(titleResId, menuId, mode);
    }

    protected void initToolbar(@StringRes int titleResId, @MenuRes int menuId, final int mode) {
        if (mode != MODE_NONE) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if (mode == MODE_BACK) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            } //else if(){}

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNavigationBtnClick(mode);
                }
            });

            initToolbarTitle(titleResId);
            initToolbarMenu(menuId);
        }
    }

    protected void initToolbarMenu(@MenuRes int menuId) {
        if (toolbar != null) {
            toolbar.getMenu().clear();
            if (menuId > 0) {
                toolbar.inflateMenu(menuId);
                toolbar.setOnMenuItemClickListener(this);
            }
        }
    }

    protected void initToolbarTitle(@StringRes int titleResId) {
        if (titleResId > 0 && toolbarTitle != null) {
            toolbarTitle.setText(titleResId);
        }
    }

    protected void onNavigationBtnClick(int mode) {
        switch (mode) {
            case MODE_BACK:
                finish();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    /**
     * findViewById without force transform
     *
     * @param resId
     * @param <T>
     * @return
     */
    public <T extends View> T findView(@IdRes int resId) {
        return (T) super.findViewById(resId);
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


}
