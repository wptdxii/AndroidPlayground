package com.wptdxii.playground.ui.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.playground.ui.provider.MessageActionProvider;
import com.wptdxii.uiframework.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wptdxii on 2017/8/22 0022.
 */

public class MainActivity extends BaseActivity {
    @BindView(R.id.btn_child)
    Button btnChild;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private MessageActionProvider actionProvider;

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
        //        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));
        setSupportActionBar(toolbar);
//        toolbar.inflateMenu(R.menu.activity_main);
//        Menu menu = toolbar.getMenu();
//        MenuItem menuItem = menu.findItem(R.id.menu_share);
//        View actionView = menuItem.getActionView();
//        TextView tvBadge = actionView.findViewById(R.id.tv_badge);
//        tvBadge.setText("5");
//        MessageActionProvider actionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        actionProvider.setBadgeCount(5);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_share);
//        View view = menuItem.getActionView();
//        TextView tvBadge = view.findViewById(R.id.tv_badge);
//        tvBadge.setText("5");
        actionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        actionProvider.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onOptionsItemSelected(menuItem);
//            }
//        });
//        actionProvider.setBadgeCount("0");

        return super.onCreateOptionsMenu(menu);
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
                actionProvider.setBadgeCount(9);
//                actionProvider.onPrepareSubMenu((item.getSubMenu()));
//                actionProvider.onPerformDefaultAction();
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
