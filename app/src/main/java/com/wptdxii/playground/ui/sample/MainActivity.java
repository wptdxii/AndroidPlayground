package com.wptdxii.playground.ui.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;
import com.wptdxii.uiframework.widget.toolbarhelper.ToolbarHelper;

/**
 * Created by wptdxii on 2017/8/22 0022.
 */

public class MainActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setupContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupData(Bundle savedInstanceState) {

    }

    @Override
    protected void setupToolbar(ToolbarHelper toolbarHelper) {
//        toolbarHelper.setNavigation(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
    }

    @Override
    protected void setupViews() {

    }
}
