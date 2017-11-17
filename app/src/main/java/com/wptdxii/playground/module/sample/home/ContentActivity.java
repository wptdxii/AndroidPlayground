package com.wptdxii.playground.module.sample.home;

import android.content.Context;
import android.content.Intent;

import com.wptdxii.playground.model.Module;
import com.wptdxii.playground.module.base.BaseContentActivity;
import com.wptdxii.playground.module.sample.SampleUmengActivity;
import com.wptdxii.playground.module.sample.SwipeRecyclerActivity;
import com.wptdxii.playground.module.sample.SwipeRecyclerFragmentActivity;
import com.wptdxii.playground.module.sample.TabViewPagerActivity;
import com.wptdxii.playground.module.sample.ViewPagerActivity;
import com.wptdxii.playground.module.sample.recyclerview.SampleRecyclerViewActivity;

import java.util.ArrayList;


public class ContentActivity extends BaseContentActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ContentActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void addItem(ArrayList<Module> mDataList) {
        mDataList.add(new Module("基于BaseSwipeRecyclerActivity", SwipeRecyclerActivity.class));
        mDataList.add(new Module("基于BaseSwipeRecyclerFragment", SwipeRecyclerFragmentActivity.class));
        mDataList.add(new Module("BottomNavigation+ViewPager", TabViewPagerActivity.class));
        mDataList.add(new Module("ViewPagerActivity", ViewPagerActivity.class));
        mDataList.add(new Module("SampleRecyclerView", SampleRecyclerViewActivity.class));
        mDataList.add(new Module("SampleUmeng", SampleUmengActivity.class));
    }
}
