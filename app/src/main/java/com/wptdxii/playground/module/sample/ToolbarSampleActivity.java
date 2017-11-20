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
 * Toolbar usage as standalone widget
 */
public class ToolbarSampleActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_center_title)
    TextView tvCenterTitle;

    private MessageActionProvider mActionProvider;
    private DrawerArrowDrawable mArrowDrawable;
    private boolean mMenuChanged;
    private boolean mMenuItemMessageVisible;
    private int mMessageCount = 0;

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, ToolbarSampleActivity.class);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_toolbar_sample;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        Log.d(TAG, "onSetupContent:");
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

    private static final String TAG = "ToolbarSampleActivity";

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        //        int menuResId = getMenuResId();
        int menuResId = getMenuResId();
        getMenuInflater().inflate(menuResId, menu);
        MenuItem menuItem = menu.findItem(R.id.action_message);
        //        View view = menuItem.getActionView();
        //        TextView tvBadge = view.findViewById(R.id.tv_badge);
        //        tvBadge.setText("5");
        mActionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mActionProvider.setOnClickListener(view -> onOptionsItemSelected(menuItem));

        Log.d(TAG, "onCreateOptionsMenu:");
        return true;
    }

    private int getMenuResId() {
        int menuResId = mMenuChanged ?
                R.menu.activity_toolbar_sample_alternative : R.menu.activity_toolbar_sample;
        mMenuChanged = !mMenuChanged;
        return menuResId;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        modifyItemMessageVisibility(menu);

        Log.d(TAG, "onPrepareOptionsMenu: ");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_message:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
            case R.id.action_search:
                //                mActionProvider.setMessageCount(0);
                //                mActionProvider.onPrepareSubMenu((item.getSubMenu()));
                //                mActionProvider.onPerformDefaultAction();
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_near_me:
                Toast.makeText(this, "附近", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_setting:
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

    @OnClick(R.id.btn_change_menu)
    public void changeMenu() {
        int menuResId = getMenuResId();
        toolbar.getMenu().clear();
        toolbar.inflateMenu(menuResId);
    }

    @OnClick(R.id.btn_modify_menu)
    public void modifyMenu() {
        Menu menu = toolbar.getMenu();
        modifyItemMessageVisibility(menu);
    }

    private void modifyItemMessageVisibility(Menu menu) {
        MenuItem itemMessage = menu.findItem(R.id.action_message);
        itemMessage.setVisible(mMenuItemMessageVisible);
        mMenuItemMessageVisible = !mMenuItemMessageVisible;
    }

    @OnClick(R.id.btn_invalidate_menu)
    public void invalidate() {
        invalidateOptionsMenu();
    }

    @OnClick(R.id.btn_action_provider)
    public void actionProvider() {
        mActionProvider.setMessageCount(mMessageCount++ % 3);
    }
}
