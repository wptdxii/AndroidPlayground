package com.wptdxii.playground.ui.sample;

import android.support.v7.widget.Toolbar;

import com.wptdxii.playground.model.Component;
import com.wptdxii.playground.ui.base.BaseListActivity;

import java.util.List;

public class SampleActivity extends BaseListActivity {

    @Override
    protected void onSetupToolbar(Toolbar toolbar) {
        toolbar.setTitle("Sample");
    }

    @Override
    protected void onCreateComponentList(List<Component> list) {
        list.add(new Component("ToolbarSample", ToolbarSampleActivity.class));
    }
}
