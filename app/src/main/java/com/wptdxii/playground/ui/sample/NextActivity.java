package com.wptdxii.playground.ui.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

public class NextActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_next;
    }

    @Override
    protected void setContent(Bundle savedInstanceState) {
        Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
