package com.wptdxii.uikit.widget.swiperecycler.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.wptdxii.uikit.widget.swiperecycler.BaseSwipeRecyclerAdapter;

/**
 * Created by wptdxii on 2016/7/30 0030.
 */
public class CusLinearLayoutManager extends LinearLayoutManager implements ILayoutManager {
    public CusLinearLayoutManager(Context context) {
        super(context);
        
    }

    public CusLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CusLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return this;
    }

    @Override
    public int findLastVisiblePosition() {
        return findLastVisibleItemPosition();
    }

    @Override
    public void setAdapter(BaseSwipeRecyclerAdapter adapter) {
        
    }
}
