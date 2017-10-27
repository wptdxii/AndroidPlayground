package com.wptdxii.playground.ui.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

public class NextActivity extends BaseActivity {

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_next;
    }

    @Override
    protected void onCreateContent(Bundle savedInstanceState) {
        Toolbar toolbar = findView(R.id.toolbar);
        //        setSupportActionBar(toolbar);
        //        ActionBar actionBar = getSupportActionBar();
        //        actionBar.setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent upIntent = NavUtils.getParentActivityIntent(NextActivity.this);

                if (upIntent == null) {
                    finish();
                    return;
                }

                //                if (NavUtils.shouldUpRecreateTask(NextActivity.this, upIntent)) {
                //                    TaskStackBuilder.create(NextActivity.this)
                //                            .addNextIntentWithParentStack(upIntent)
                //                            .startActivities();
                //                } else {
                //                    NavUtils.navigateUpTo(NextActivity.this, upIntent);
                //                }
                NavUtils.navigateUpTo(NextActivity.this, upIntent);
            }
        });
    }
}
