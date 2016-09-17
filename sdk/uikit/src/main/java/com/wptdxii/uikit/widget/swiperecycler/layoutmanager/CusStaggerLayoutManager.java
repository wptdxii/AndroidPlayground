package com.wptdxii.uikit.widget.swiperecycler.layoutmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.wptdxii.uikit.widget.swiperecycler.BaseSwipeRecyclerAdapter;

/**
 * Created by wptdxii on 2016/7/30 0030.
 */
public class CusStaggerLayoutManager extends StaggeredGridLayoutManager implements ILayoutManager {
    public CusStaggerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CusStaggerLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return this;
    }

    @Override
    public int findLastVisiblePosition() {
        int[] positions = null;
        positions = findLastVisibleItemPositions(positions);
        return positions[0];
    }

    @Override
    public void setAdapter(BaseSwipeRecyclerAdapter adapter) {

    }
}
