package com.wptdxii.androidpractice.ui.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeRecyclerAdapter;
import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeViewHolder;
import com.wptdxii.androidpractice.widget.swiperecycler.LinearDividerItemDecoration;
import com.wptdxii.androidpractice.widget.swiperecycler.SwipeRecycler;
import com.wptdxii.androidpractice.widget.swiperecycler.layoutmanager.CusLinearLayoutManager;
import com.wptdxii.androidpractice.widget.swiperecycler.layoutmanager.ILayoutManager;

import java.util.ArrayList;

/**
 * Created by wptdxii on 2016/7/30 0030.
 */
public abstract class BaseSwipeRecyclerActivity<T> extends BaseActivity implements SwipeRecycler.OnSwipeRefreshListener {
    private SwipeRecycler mSwipeRecycler;
    private BaseSwipeRecyclerAdapter mAdapter;
    private ArrayList<T> mDataList;

    public BaseSwipeRecyclerAdapter getAdapter() {
        return mAdapter;
    }
    

    public SwipeRecycler getSwipeRecycler() {
        return mSwipeRecycler;
    }
    

    public ArrayList<T> getDataList() {
        return mDataList;
    }
    

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_base_swipe_recycler, -1, -1, MODE_BACK);
    }

    @Override
    protected void initView() {
        mSwipeRecycler = (SwipeRecycler) findViewById(R.id.swipeRecycler);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mAdapter = new SwipeRecyclerAdapter();
        mDataList = new ArrayList<>();
        mSwipeRecycler.setOnSwipeRefreshListener(this);
        mSwipeRecycler.setLayoutManager(getLayoutManager());
        mSwipeRecycler.addItemDecoration(getItemDecoration());
        mSwipeRecycler.setAdapter(mAdapter);
        initListData(savedInstanceState);
    }

    protected abstract void initListData(Bundle savedInstanceState);

    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new LinearDividerItemDecoration(getApplicationContext(), R.drawable.swipte_recycler_linear_divider);
    }

    protected ILayoutManager getLayoutManager() {
        return new CusLinearLayoutManager(getApplicationContext());
    }


    private class SwipeRecyclerAdapter extends BaseSwipeRecyclerAdapter {
        @Override
        protected BaseSwipeViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(parent, viewType);
        }

        @Override
        protected int getItemType(int position) {
            return BaseSwipeRecyclerActivity.this.getItemType(position);
        }

        @Override
        protected int getDataCount() {
            return mDataList.size();
        }

        @Override
        public boolean isSectionHeader(int positon) {
            return BaseSwipeRecyclerActivity.this.isSectionHeader(positon);
        }
    }

    protected boolean isSectionHeader(int positon) {
        return false;
    }

    protected int getItemType(int position) {
        return 0;
    }

    protected abstract BaseSwipeViewHolder getViewHolder(ViewGroup parent, int viewType);
}
