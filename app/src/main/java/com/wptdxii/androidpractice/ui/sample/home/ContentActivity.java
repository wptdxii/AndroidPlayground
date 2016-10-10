package com.wptdxii.androidpractice.ui.sample.home;

import android.content.Context;
import android.content.Intent;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.model.Module;
import com.wptdxii.androidpractice.ui.sample.SwipeRecyclerActivity;
import com.wptdxii.androidpractice.ui.sample.SwipeRecyclerFragmentActivity;
import com.wptdxii.androidpractice.ui.sample.TabViewPagerActivity;
import com.wptdxii.androidpractice.ui.sample.ViewPagerActivity;
import com.wptdxii.androidpractice.ui.sample.recyclerview.SampleRecyclerViewActivity;
import com.wptdxii.androidpractice.ui.base.BaseContentActivity;

import java.util.ArrayList;

public class ContentActivity extends BaseContentActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ContentActivity.class);
        context.startActivity(intent);
    }
    

    @Override
    protected void initToolbarTitle(int titleResId) {
        super.initToolbarTitle(R.string.content_activity_toolbar_title);
    }

    @Override
    protected void addItem(ArrayList<Module> mDataList) {
        mDataList.add(new Module("基于BaseSwipeRecyclerActivity", SwipeRecyclerActivity.class));
        mDataList.add(new Module("基于BaseSwipeRecyclerFragment", SwipeRecyclerFragmentActivity.class));
        mDataList.add(new Module("BottomNavigation+ViewPager", TabViewPagerActivity.class));
        mDataList.add(new Module("ViewPagerActivity", ViewPagerActivity.class));
        mDataList.add(new Module("SampleRecyclerView", SampleRecyclerViewActivity.class));
    }
    
}
