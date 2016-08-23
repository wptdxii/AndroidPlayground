package com.wptdxii.androidpractice.widget.swiperecycler.layoutmanager;

import android.support.v7.widget.RecyclerView;

import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeRecyclerAdapter;

/**
 * Created by wptdxii on 2016/7/30 0030.
 */
public interface ILayoutManager {
    RecyclerView.LayoutManager getLayoutManager();

    int findLastVisiblePosition();

    void setAdapter(BaseSwipeRecyclerAdapter adapter);
}
