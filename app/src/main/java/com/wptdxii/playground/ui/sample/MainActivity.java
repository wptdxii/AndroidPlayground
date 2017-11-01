package com.wptdxii.playground.ui.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wptdxii on 2017/8/22 0022.
 */

public class MainActivity extends BaseActivity {
    @BindView(R.id.btn_child)
    Button btnChild;

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, MainActivity.class);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreateContent(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        btnChild.setVisibility(View.GONE);
        Toolbar toolbar = findView(R.id.toolbar);
        //        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //        Menu menu = toolbar.getMenu();
        //        if (menu instanceof MenuBuilder) {
        //            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        //        }
        //        toolbar.inflateMenu(R.menu.activity_main);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            //noinspection RestrictedApi
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private static final String TAG = "MainActivity";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected: ");
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
            case R.id.menu_setting:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
