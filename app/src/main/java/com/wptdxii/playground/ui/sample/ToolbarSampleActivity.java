package com.wptdxii.playground.ui.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ToolbarSampleActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_center_title)
    TextView tvCenterTitle;
    DrawerArrowDrawable mArrowDrawable;
    //    private MessageActionProvider mActionProvider;

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, ToolbarSampleActivity.class);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_toolbar_sample;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        initDrawerArrowDrawable();
        //        toolbar_center_title.inflateMenu(R.menu.activity_toolbar_sample);
        //        Menu menu = toolbar_center_title.getMenu();
        //        MenuItem menuItem = menu.findItem(R.id.menu_share);
        //        View actionView = menuItem.getActionView();
        //        TextView tvBadge = actionView.findViewById(R.id.tv_badge);
        //        tvBadge.setText("5");
        //        MessageActionProvider mActionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //        mActionProvider.setMessageCount(5);
    }

    private void initDrawerArrowDrawable() {
        mArrowDrawable = new DrawerArrowDrawable(this);
        mArrowDrawable.setColor(ContextCompat.getColor(this, R.color.colorWhite_FFFFFF));
        mArrowDrawable.setProgress(0);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        getMenuInflater().inflate(R.menu.activity_toolbar_sample, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_message);
        //        View view = menuItem.getActionView();
        //        TextView tvBadge = view.findViewById(R.id.tv_badge);
        //        tvBadge.setText("5");
        //        mActionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //        mActionProvider.setOnClickListener(view -> onOptionsItemSelected(menuItem));
        //        mActionProvider.setMessageCount("9");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_message:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            case R.id.menu_search:
                //                mActionProvider.setMessageCount(0);
                //                mActionProvider.onPrepareSubMenu((item.getSubMenu()));
                //                mActionProvider.onPerformDefaultAction();
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

    @OnClick(R.id.btn_navigation)
    public void setNavigation() {
        toolbar.setNavigationIcon(mArrowDrawable);
        toolbar.setNavigationOnClickListener(view -> Toast.makeText(this, "Drawer", Toast.LENGTH_SHORT).show());
    }

    @OnClick(R.id.btn_overflow_menu_button)
    public void setOverflowMenuButton() {
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));
    }

    @OnClick(R.id.btn_logo)
    public void setLogo() {
        toolbar.setLogo(R.mipmap.ic_launcher);
    }

    @OnClick(R.id.btn_title)
    public void setTitle() {
        toolbar.setTitle("Title");
    }

    @OnClick(R.id.btn_subtitle)
    public void setSubtitle() {
        toolbar.setSubtitle("Subtitle");
    }

    @OnClick(R.id.btn_center_title)
    public void setCenterTitle() {
        tvCenterTitle.setText("CenterTitle");
    }

    @OnClick(R.id.btn_modify_menu)
    public void modifyMenu() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.activity_toolbar_sample_alternative);
    }

    @OnClick(R.id.btn_reset_menu)
    public void resetMenu() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.activity_toolbar_sample);
    }
}
