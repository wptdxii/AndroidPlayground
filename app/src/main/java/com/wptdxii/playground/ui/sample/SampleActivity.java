package com.wptdxii.playground.ui.sample;

import android.content.Context;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.model.Component;
import com.wptdxii.playground.ui.base.BaseListActivity;
import com.wptdxii.playground.ui.common.MainActivity;

import java.util.List;

public class SampleActivity extends BaseListActivity {

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, MainActivity.class);
    }

    @Override
    protected void onCreateComponentList(List<Component> list) {
        list.add(new Component("ToolbarSample", ToolbarSampleActivity.class));
    }
}
