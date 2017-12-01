package com.wptdxii.playground.module.sample;

import android.support.v7.widget.Toolbar;

import com.wptdxii.playground.model.Component;
import com.wptdxii.playground.module.base.BaseListActivity;
import com.wptdxii.playground.module.sample.widget.ConstraintSampleActivity;

import java.util.List;

public class SampleActivity extends BaseListActivity {

    @Override
    protected void onSetupToolbar(Toolbar toolbar) {
        toolbar.setTitle("Sample");
    }

    @Override
    protected void onCreateComponentList(List<Component> list) {
        list.add(new Component("ConstraintLayoutSample", ConstraintSampleActivity.class));
        list.add(new Component("ActionBarSample", ActionBarSampleActivity.class));
        list.add(new Component("ToolbarSample", ToolbarSampleActivity.class));
    }
}
