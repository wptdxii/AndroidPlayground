package com.wptdxii.playground.ui.sample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.wptdxii.playground.R;
import com.wptdxii.playground.ui.base.BaseSwipeRecyclerFragment;
import com.wptdxii.uiframework.base.BaseActivity;
import com.wptdxii.uikit.widget.bottomnavigation.TabLayout;

import java.util.ArrayList;

/**
 * bottomNavigation+viewPager
 */
public class TabViewPagerActivity extends BaseActivity implements TabLayout.OnTabClickListener {
    private BaseSwipeRecyclerFragment mMsgFragment;
    private BaseSwipeRecyclerFragment mContactFragment;
    private BaseSwipeRecyclerFragment mMomentsFragment;
    private BaseSwipeRecyclerFragment mProfileFragment;
    private ArrayList<TabLayout.Tab> tabs;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    @LayoutRes
    @Override
    protected int setupContentView() {
//        setContentView(R.layout.activity_tab_view_pager, -1, -1, MODE_BACK);
        return R.layout.activity_tab_view_pager;
    }

    @Override
    protected void setupViews() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setOnTabClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new PageChangeListener());
    }

    @Override
    protected void setupData(Bundle savedInstanceState) {
        tabs = new ArrayList<>();
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_msg, R.string.bottom_tab_msg, LazyLoadFragment.class));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_contact, R.string.bottom_tab_contact, LazyLoadFragment.class));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_moments, R.string.bottom_tab_moments, LazyLoadFragment.class));
        tabs.add(new TabLayout.Tab(R.drawable.selector_tab_profile, R.string.bottom_tab_profile, LazyLoadFragment.class));
        mTabLayout.iniData(tabs);
        mTabLayout.setCurrentTab(0);

        //Adapter中要使用tabs的数据，所以设置adapter要在tabs初始化数据后
        FragmentManager fm = getSupportFragmentManager();
        mPagerAdapter = new CusPagerAdapter(fm);
//        mViewPager.setOffscreenPageLimit(3);
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
//            initToolbarTitle(tab.labelResId);
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
                    if (mMsgFragment == null) {
                        mMsgFragment =  new SwipeRecyclerFragment();
                    }
                    fragment = mMsgFragment;
                   // fragment = MsgFragment.newInstance("Msg");
                    break;
                case 1:
                    if (mContactFragment == null) {
                        mContactFragment =  new SwipeRecyclerFragment();
                    }
                    fragment = mContactFragment;
//                    fragment = ContactFragment.newInstance("Contact");
                    break;
                case 2:
                    if (mMomentsFragment == null) {
                        mMomentsFragment =  new SwipeRecyclerFragment();
                    }
                    fragment = mMomentsFragment;
//                    fragment = MomentsFragment.newInstance("Moments");
                    break;
                default:
                    if (mProfileFragment == null) {
                        mProfileFragment =  new SwipeRecyclerFragment();
                    }
                    fragment = mProfileFragment;
//                    fragment = ProfileFragment.newInstance("Profile");
                    break;
            }
            //            Fragment fragment = LazyLoadFragment.newInstance(position);
//            Fragment fragment = new SwipeRecyclerFragment();
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