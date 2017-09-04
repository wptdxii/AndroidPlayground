package com.wptdxii.playground.ui.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

/**
 * Created by wptdxii on 2017/9/4 0004.
 */

public class ChildActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_child;
    }

    @Override
    protected void setContent(Bundle savedInstanceState) {
        Toolbar toolbar = findView(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

    }
}
