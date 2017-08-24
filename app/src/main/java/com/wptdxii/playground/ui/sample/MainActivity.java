package com.wptdxii.playground.ui.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
        toolbarHelper.setNavigation(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Toolbar toolbar = toolbarHelper.getToolbar();
        toolbar.inflateMenu(R.menu.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_share) {
            Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemId == R.id.menu_search) {
            Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (itemId == R.id.menu_near_me) {
            Toast.makeText(this, "附近", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupViews() {

    }
}
