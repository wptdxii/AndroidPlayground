package com.wptdxii.uikit.widget.bottomnavigation;

import android.support.v4.app.Fragment;

/**
 * Created by wptdxii on 2016/8/31 0031.
 */
public interface ITabFragment {
    void onMenuItemClick();
//    BaseFragment getFragment();
    Fragment getFragment();
}

