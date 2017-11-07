package com.wptdxii.playground.ui.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

/**
 * Created by wptdxii on 2017/9/4 0004.
 */

public class ChildActivity extends BaseActivity {

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        Toolbar toolbar = findView(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent upIntent = NavUtils.getParentActivityIntent(ChildActivity.this);
//                if (upIntent == null) {
//                    finish();
//                    return;
//                }
//                NavUtils.navigateUpTo(ChildActivity.this, upIntent);
                Toast.makeText(ChildActivity.this, "Toolbar", Toast.LENGTH_SHORT).show();
            }
        });
        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_near_me_white_24dp);
//        }

        findView(R.id.btn_child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChildActivity.this, NextActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_child;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Toast.makeText(this, "Toast", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        return true;
//    }
}
