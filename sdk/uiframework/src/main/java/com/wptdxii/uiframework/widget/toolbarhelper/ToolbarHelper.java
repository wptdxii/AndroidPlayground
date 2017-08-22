package com.wptdxii.uiframework.widget.toolbarhelper;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by wptdxii on 17-8-21.
 */

public class ToolbarHelper {
    private Context mContxt;
    private Toolbar mToolbar;

    public ToolbarHelper(Context context, Toolbar toolbar) {
        this.mContxt = context;
        this.mToolbar = toolbar;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setNavigation(@DrawableRes int resId, View.OnClickListener listener) {
        mToolbar.setNavigationIcon(resId);
        mToolbar.setNavigationOnClickListener(listener);
    }
}
