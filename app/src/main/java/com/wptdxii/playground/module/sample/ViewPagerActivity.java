package com.wptdxii.playground.module.sample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

/**
 * 实现数据懒加载
 */
public class ViewPagerActivity extends BaseActivity {
    private ViewPager viewPager;
    private CusPagerAdapter mPagerAdapter;

    @LayoutRes
    @Override
    protected int onCreateContentView() {
        return R.layout.activity_view_pager;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        FragmentManager fm = getSupportFragmentManager();
        mPagerAdapter = new CusPagerAdapter(fm);
        viewPager.setAdapter(mPagerAdapter);
    }

    private class CusPagerAdapter extends FragmentStatePagerAdapter {

        public CusPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return LazyLoadFragment.newInstance(position);
        }
        
        @Override
        public int getCount() {
            return 5;
        }
    }
}