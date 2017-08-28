package com.wptdxii.playground.ui.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

/**
 * Created by wptdxii on 2017/8/22 0022.
 */

public class MainActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setContent(Bundle savedInstanceState) {
        Toolbar toolbar = findView(R.id.toolbar);

        //        toolbar.setNavigationIcon(R.drawable.ic_near_me_white_24dp);
        //        toolbar.setLogo(R.mipmap.ic_launcher);
        //        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));

        // 需要在 setSupportActionBar 之前设置 title 和 subtitle
        toolbar.setTitle("微信");
        //        toolbar.setSubtitle("Hello");

        setSupportActionBar(toolbar);
        //        toolbar.setNavigationIcon(R.drawable.ic_near_me_white_24dp);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 当 toolbar 未设置 navigation icon 时，true可以显示默认图标
            //            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_share:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_search:
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_near_me:
                Toast.makeText(this, "附近", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
