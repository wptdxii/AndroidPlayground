package com.wptdxii.playground.module.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.playground.module.widget.actionprovider.MessageActionProvider;
import com.wptdxii.uiframework.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Toolbar usage as ActionBar
 */
public class ActionBarSampleActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_center_title)
    TextView tvCenterTitle;

    private MessageActionProvider mActionProvider;
    private DrawerArrowDrawable mArrowDrawable;
    private boolean mMenuItemNearMeVisible;
    private int mMessageCount = 0;

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, ActionBarSampleActivity.class);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_toolbar_sample;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        Log.e(TAG, "onSetupContent: " + mMenuItemNearMeVisible );
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        initDrawerArrowDrawable();
    }

    private void initDrawerArrowDrawable() {
        mArrowDrawable = new DrawerArrowDrawable(this);
        mArrowDrawable.setColor(ContextCompat.getColor(this, R.color.colorWhite_FFFFFF));
        mArrowDrawable.setProgress(0);
    }

    private static final String TAG = "ActionBarSampleActivity";

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        getMenuInflater().inflate(R.menu.activity_action_bar_sample, menu);
        MenuItem menuItem = menu.findItem(R.id.action_message);
        mActionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mActionProvider.setOnClickListener(view -> onOptionsItemSelected(menuItem));
        Log.e(TAG, "onCreateOptionsMenu: ");
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e(TAG, "onPrepareOptionsMenu: ");
        modifyItemMessageVisibility(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_message:
                Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
            case R.id.action_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_near_me:
                Toast.makeText(this, "Near Me", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_setting:
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @OnClick(R.id.btn_navigation)
    public void setNavigation() {
        toolbar.setNavigationIcon(mArrowDrawable);
        toolbar.setNavigationOnClickListener(view ->
                Toast.makeText(this, "Navigation", Toast.LENGTH_SHORT).show());
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
        Menu menu = toolbar.getMenu();
        modifyItemMessageVisibility(menu);
    }

    @OnClick(R.id.btn_change_menu)
    public void changeMenu() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.activity_action_bar_sample_alternative);
    }


    @OnClick(R.id.btn_invalidate_menu)
    public void invalidate() {
        invalidateOptionsMenu();
    }

    private void modifyItemMessageVisibility(Menu menu) {
        Log.e(TAG, "modifyItemMessageVisibility: " + mMenuItemNearMeVisible);
        MenuItem itemMessage = menu.findItem(R.id.action_near_me);
        itemMessage.setVisible(mMenuItemNearMeVisible);
        mMenuItemNearMeVisible = !mMenuItemNearMeVisible;
    }

    @OnClick(R.id.btn_action_provider)
    public void actionProvider() {
        mActionProvider.setMessageCount(mMessageCount++ % 3 + 1);
    }
}
