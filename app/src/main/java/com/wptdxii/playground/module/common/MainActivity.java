package com.wptdxii.playground.module.common;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.wptdxii.ext.util.ActivityStack;
import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.playground.model.Component;
import com.wptdxii.playground.module.base.BaseListActivity;
import com.wptdxii.playground.module.sample.SampleActivity;

import java.util.List;

public class MainActivity extends BaseListActivity {
    private static final int RESIDENCE_TIME = 1000;
    private boolean mIsExited = false;

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, MainActivity.class);
    }

    @Override
    protected void onSetupToolbar(Toolbar toolbar) {
    }

    @Override
    protected void onSetupActionBar(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onCreateComponentList(List<Component> list) {
        list.add(new Component("Sample", SampleActivity.class));
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (!mIsExited) {
            mIsExited = true;
            getWindow().getDecorView().postDelayed(() -> mIsExited = false, RESIDENCE_TIME);
            Toast.makeText(this, R.string.activity_main_click_again_to_exit, Toast.LENGTH_SHORT).show();
        } else {
            ActivityStack.getInstance().finishAll();
        }
    }
}
