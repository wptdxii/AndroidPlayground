package com.wptdxii.playground.ui.sample.home;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.wptdxii.playground.R;
import com.wptdxii.playground.model.Module;
import com.wptdxii.playground.ui.base.BaseContentActivity;
import com.wptdxii.playground.ui.sample.SampleUmengActivity;
import com.wptdxii.playground.ui.sample.SwipeRecyclerActivity;
import com.wptdxii.playground.ui.sample.SwipeRecyclerFragmentActivity;
import com.wptdxii.playground.ui.sample.TabViewPagerActivity;
import com.wptdxii.playground.ui.sample.ViewPagerActivity;
import com.wptdxii.playground.ui.sample.recyclerview.SampleRecyclerViewActivity;
import com.wptdxii.uiframework.widget.toolbarhelper.ToolbarHelper;

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

    @Override
    protected void setupToolbar(ToolbarHelper toolbarHelper) {
        toolbarHelper.setNavigation(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
