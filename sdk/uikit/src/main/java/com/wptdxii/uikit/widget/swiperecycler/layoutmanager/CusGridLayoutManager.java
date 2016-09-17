package com.wptdxii.uikit.widget.swiperecycler.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.wptdxii.uikit.widget.swiperecycler.BaseSwipeRecyclerAdapter;

/**
 * Created by wptdxii on 2016/7/30 0030.
 */
public class CusGridLayoutManager extends GridLayoutManager implements ILayoutManager {
    
    public CusGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CusGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public CusGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
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
        FooterSapnSizeLookup footerSpanSizeLookup = new FooterSapnSizeLookup(adapter, getSpanCount());
        setSpanSizeLookup(footerSpanSizeLookup);
    }

    private class FooterSapnSizeLookup extends GridLayoutManager.SpanSizeLookup{
        private BaseSwipeRecyclerAdapter adapter;
        private  int spanCount;
        public FooterSapnSizeLookup(BaseSwipeRecyclerAdapter adapter, int spanCount) {
            this.adapter = adapter;
            this.spanCount = spanCount;
        }

        @Override
        public int getSpanSize(int position) {
            if (adapter.isLoadMoreFooter(position) || adapter.isSectionHeader(position)) {
                return spanCount;
            }
            return 1;
        }
    }
    
}
