package com.wptdxii.androidpractice.ui.activity;


import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.model.Module;
import com.wptdxii.androidpractice.ui.base.BaseContentActivity;

import java.util.ArrayList;

public class SampleSwipeRecyclerActivity extends BaseContentActivity {
    @Override
    protected void initToolbarTitle(int titleResId) {
        super.initToolbarTitle(R.string.sample_swipte_recycler_activity_toolbar_title);
    }

    @Override
    protected void addItem(ArrayList<Module> mDataList) {
        mDataList.add(new Module("基于BaseSwipeRecyclerActivity", SwipeRecyclerActivity.class));
    }
}
