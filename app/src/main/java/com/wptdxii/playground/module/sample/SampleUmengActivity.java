package com.wptdxii.playground.module.sample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

public class SampleUmengActivity extends BaseActivity {

    @LayoutRes
    @Override
    protected int onCreateContentView() {
        return R.layout.activity_sample_umeng;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
    }
}