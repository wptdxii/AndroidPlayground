package com.wptdxii.playground.ui.common;

import android.content.Context;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.model.Component;
import com.wptdxii.playground.ui.base.BaseListActivity;
import com.wptdxii.playground.ui.sample.SampleActivity;

import java.util.List;

/**
 * Created by wptdxii on 2017/8/22 0022.
 */

public class MainActivity extends BaseListActivity {

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, MainActivity.class);
    }

    @Override
    protected void onCreateComponentList(List<Component> list) {
        list.add(new Component("Sample", SampleActivity.class));
    }
}
