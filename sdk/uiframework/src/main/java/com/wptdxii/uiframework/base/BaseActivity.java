package com.wptdxii.uiframework.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wptdxii.ext.util.ActivityStack;
import com.wptdxii.ext.util.AppStatusTracker;
import com.wptdxii.uiframework.R;


/**
 * Created by wptdxii on 2016/7/7 0007.
 */
public abstract class BaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    protected static final int MODE_NONE = -1;
    protected static final int MODE_BACK = 0;
    protected Toolbar toolbar;
    protected TextView toolbarTitle;

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
                initContentView();
                initView();
                initData(savedInstanceState);
                break;
        }


    }


    @Override
    protected void onDestroy() {
        ActivityStack.getInstance().removeActivity(this);
        super.onDestroy();
    }

    protected abstract void initContentView();

    protected abstract void initView();

    protected abstract void initData(Bundle savedInstanceState);

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
        initToolBar(titleResId, menuId, mode);
    }

    protected void initToolBar(@StringRes int titleResId, @MenuRes int menuId, final int mode) {
        if (mode != MODE_NONE) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if (mode == MODE_BACK) {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
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

    protected void initToolbarMenu(int menuId) {
        if (toolbar != null) {
            toolbar.getMenu().clear();
            if (menuId > 0) {
                toolbar.inflateMenu(menuId);
                toolbar.setOnMenuItemClickListener(this);
            }
        }
    }

    protected void initToolbarTitle(int titleResId) {
        if (titleResId > 0 && toolbarTitle != null) {
            toolbarTitle.setText(titleResId);
        }
    } 
    protected void initToolbarTitle(String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
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
     * @param resId
     * @param <T>
     * @return
     */
    public <T extends View> T findView(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }

}
