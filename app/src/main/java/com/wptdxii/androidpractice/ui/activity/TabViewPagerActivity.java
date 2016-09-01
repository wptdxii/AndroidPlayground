package com.wptdxii.androidpractice.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.ui.base.BaseActivity;
import com.wptdxii.androidpractice.ui.fragment.ContactFragment;
import com.wptdxii.androidpractice.ui.fragment.LazyLoadFragment;
import com.wptdxii.androidpractice.ui.fragment.MomentsFragment;
import com.wptdxii.androidpractice.ui.fragment.MsgFragment;
import com.wptdxii.androidpractice.ui.fragment.ProfileFragment;
import com.wptdxii.androidpractice.widget.bottomnavigation.TabLayout;

import java.util.ArrayList;

/**
 * bottomNavigation+viewPager
 */
public class TabViewPagerActivity extends BaseActivity implements TabLayout.OnTabClickListener {
    private ArrayList<TabLayout.Tab> tabs;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void initToolbarTitle(int titleResId) {
        super.initToolbarTitle(R.string.tabviewpager_activity_toolbar_title);
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_tab_view_pager, -1, -1, MODE_BACK);
    }

    @Override
    protected void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setOnTabClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new PageChangeListener());

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        tabs = new ArrayList<>();
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_msg, R.string.bottom_tab_msg,LazyLoadFragment.class));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_contact, R.string.bottom_tab_contact,LazyLoadFragment.class));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_moments, R.string.bottom_tab_moments,LazyLoadFragment.class));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_profile, R.string.bottom_tab_profile,LazyLoadFragment.class));
        mTabLayout.iniData(tabs);
        mTabLayout.setCurrentTab(0);

        //Adapter中要使用tabs的数据，所以设置adapter要在tabs初始化数据后
        FragmentManager fm = getSupportFragmentManager();
        mPagerAdapter = new CusPagerAdapter(fm);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onTabClick(TabLayout.Tab tab, boolean isSelected) {
        if (isSelected) {
            //TODO refresh
//            CusPagerAdapter adapter = (CusPagerAdapter) mViewPager.getAdapter();
//            LazyLoadFragment fragment = (LazyLoadFragment) adapter.getItem(tabs.indexOf(tab));
            
        } else {
            // TODO switch
            mViewPager.setCurrentItem(tabs.indexOf(tab));
            initToolbarTitle(tab.labelResId);
            //不同的fragment对应不同的menu
//            initToolbarMenu();
        }
    }


    private class CusPagerAdapter extends FragmentPagerAdapter {
        public CusPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
                        switch (position) {
                            case 0:
                                fragment = MsgFragment.newInstance("Msg");
                                break;
                            case 1:
                                fragment = ContactFragment.newInstance("Contact");
                                break;
                            case 2:
                                fragment = MomentsFragment.newInstance("Moments");
                                break;
                            default:
                                fragment = ProfileFragment.newInstance("Profile");
                                break;
                        }
//            Fragment fragment = LazyLoadFragment.newInstance(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return tabs.size();
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            
        }

        @Override
        public void onPageSelected(int position) {
            mTabLayout.setCurrentTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


}