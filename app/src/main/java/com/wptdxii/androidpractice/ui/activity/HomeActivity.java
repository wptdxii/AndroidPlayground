package com.wptdxii.androidpractice.ui.activity;

import android.os.Bundle;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.ui.base.BaseActivity;
import com.wptdxii.androidpractice.widget.bottomnavigation.TabLayout;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements TabLayout.OnTabClickListener {
    private TabLayout mTabLayout;

    @Override
    protected void initToolbarTitle(int titleResId) {
        super.initToolbarTitle(R.string.home_activity_toolbar_title);
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_home, -1, -1, MODE_BACK);
    }

    @Override
    protected void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setOnTabClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        ArrayList<TabLayout.Tab> tabs = new ArrayList<>();
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_msg, R.string.bottom_tab_msg));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_contact, R.string.bottom_tab_contact));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_moments, R.string.bottom_tab_moments));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_profile, R.string.bottom_tab_profile));
        mTabLayout.iniData(tabs);
        mTabLayout.setCurrentTab(0);
    }

    @Override
    public void onTabClick(TabLayout.Tab tab) {
        
    }
}