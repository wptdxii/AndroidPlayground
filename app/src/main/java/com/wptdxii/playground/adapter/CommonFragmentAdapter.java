package com.wptdxii.playground.adapter;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

/**
 * Created by fuhongjie on 5/27/16.
 */
public class CommonFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;

    public CommonFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;

    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
