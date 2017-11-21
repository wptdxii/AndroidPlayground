package com.wptdxii.playground.module.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wptdxii.ext.util.MenuUtil;
import com.wptdxii.playground.R;
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

    private boolean mMenuItemMessageVisible;
    private int mMessageCount = 0;
    private ActionLayoutHolder mActionLayoutHolder;

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_toolbar_sample;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        Menu menu = toolbar.getMenu();
        MenuUtil.showOptionalIcons(menu);
        initNavigation();
        invalidateMenu();
        initMenuItemClickListener();
    }

    private void initMenuItemClickListener() {
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.action_search:
                    Toast.makeText(ToolbarSampleActivity.this, "Search", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_message:
                    Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_near_me:
                    Toast.makeText(ToolbarSampleActivity.this, "Near Me", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_setting:
                    Toast.makeText(ToolbarSampleActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    private void initNavigation() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view -> {
            Intent upIntent = NavUtils.getParentActivityIntent(ToolbarSampleActivity.this);
            if (upIntent == null) {
                finish();
                return;
            }
            NavUtils.navigateUpTo(ToolbarSampleActivity.this, upIntent);
        });
    }

    private void invalidateMenu() {
        Menu menu = toolbar.getMenu();
        menu.clear();
        toolbar.inflateMenu(R.menu.activity_toolbar_sample);
        MenuItem menuItem = menu.findItem(R.id.action_message);
        View actionView = menuItem.getActionView();
        if (mActionLayoutHolder == null) {
            mActionLayoutHolder = new ActionLayoutHolder(this);
        }

        ButterKnife.bind(mActionLayoutHolder, actionView);
    }

    private DrawerArrowDrawable getDrawerArrowDrawable() {
        DrawerArrowDrawable arrowDrawable = new DrawerArrowDrawable(this);
        arrowDrawable.setColor(ContextCompat.getColor(this, R.color.colorWhite_FFFFFF));
        arrowDrawable.setProgress(0);
        return arrowDrawable;
    }

    @OnClick(R.id.btn_navigation)
    public void setNavigation() {
        toolbar.setNavigationIcon(getDrawerArrowDrawable());
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
        Menu menu = toolbar.getMenu();
        modifyItemMessageVisibility(menu);
    }

    @OnClick(R.id.btn_change_menu)
    public void changeMenu() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.activity_toolbar_sample_alternative);
    }


    private void modifyItemMessageVisibility(Menu menu) {
        MenuItem itemMessage = menu.findItem(R.id.action_message);
        itemMessage.setVisible(mMenuItemMessageVisible);
        mMenuItemMessageVisible = !mMenuItemMessageVisible;
    }

    @OnClick(R.id.btn_invalidate_menu)
    public void invalidate() {
        invalidateMenu();
    }

    @OnClick(R.id.btn_increase)
    public void increaseMessage() {
        if (mMessageCount < 0) {
            mMessageCount = 0;
        }
        mActionLayoutHolder.tvBadge.setVisibility(View.VISIBLE);
        mActionLayoutHolder.tvBadge.setText(String.valueOf(++mMessageCount));
    }

    @OnClick(R.id.btn_decrease)
    public void decreaseMessage() {
        mActionLayoutHolder.tvBadge.setText(String.valueOf(--mMessageCount));
        if (mMessageCount <= 0) {
            mActionLayoutHolder.tvBadge.setVisibility(View.GONE);
        }
    }

    class ActionLayoutHolder {
        @BindView(R.id.tv_badge)
        TextView tvBadge;

        Context mContext;

        ActionLayoutHolder(Context context) {
            this.mContext = context;
        }

        @OnClick(R.id.fl_message)
        void checkMessage() {
            Toast.makeText(mContext, "Message", Toast.LENGTH_SHORT).show();
        }
    }
}
