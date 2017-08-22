package com.wptdxii.playground.ui.sample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;
import com.wptdxii.uiframework.widget.toolbarhelper.ToolbarHelper;

public class SampleUmengActivity extends BaseActivity {

    @LayoutRes
    @Override
    protected int setupContentView() {
//        setContentView(R.layout.activity_sample_umeng,
//                R.string.activity_sample_umeng_title, -1, MODE_BACK);
        return R.layout.activity_sample_umeng;
    }

    @Override
    protected void setupData(Bundle savedInstanceState) {

    }

    @Override
    protected void setupToolbar(ToolbarHelper toolbarHelper) {

    }

    @Override
    protected void setupViews() {

    }


}