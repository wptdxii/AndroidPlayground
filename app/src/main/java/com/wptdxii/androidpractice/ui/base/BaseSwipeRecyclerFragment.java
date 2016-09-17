package com.wptdxii.androidpractice.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wptdxii.androidpractice.R;
import com.wptdxii.uikit.widget.swiperecycler.BaseSwipeRecyclerAdapter;
import com.wptdxii.uikit.widget.swiperecycler.BaseSwipeViewHolder;
import com.wptdxii.uikit.widget.swiperecycler.LinearDividerItemDecoration;
import com.wptdxii.uikit.widget.swiperecycler.SwipeRecycler;
import com.wptdxii.uikit.widget.swiperecycler.layoutmanager.CusLinearLayoutManager;
import com.wptdxii.uikit.widget.swiperecycler.layoutmanager.ILayoutManager;
import com.wptdxii.uiframework.base.BaseFragment;

import java.util.ArrayList;

/**
 * Created by wptdxii on 2016/9/1 0001.
 */
public abstract class BaseSwipeRecyclerFragment<T> extends BaseFragment implements SwipeRecycler.OnSwipeRefreshListener {
   protected SwipeRecycler mSwipeRecycler;
   protected BaseSwipeRecyclerAdapter mAdapter;
   protected ArrayList<T> mDataList;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        enableLazyLoad(true);
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_swipe_recycler, container, false);
        return view;
    }

    @Override
    protected void initView(View view) {
        mSwipeRecycler = (SwipeRecycler) view.findViewById(R.id.swipeRecycler);
        mAdapter = new SwipeRecyclerAdapter();
        mSwipeRecycler.setOnSwipeRefreshListener(this);
        mSwipeRecycler.setLayoutManager(getLayoutManager());
        mSwipeRecycler.addItemDecoration(getItemDecoration());
        mSwipeRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        initListData();
    }
    protected abstract void initListData();

    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new LinearDividerItemDecoration(getActivity(), R.drawable.swipte_recycler_linear_divider);
    }

    protected ILayoutManager getLayoutManager() {
        return new CusLinearLayoutManager(getActivity());
    }

    private class SwipeRecyclerAdapter extends BaseSwipeRecyclerAdapter {
        @Override
        protected BaseSwipeViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(parent, viewType);
        }

        @Override
        protected int getItemType(int position) {
            return BaseSwipeRecyclerFragment.this.getItemType(position);
        }

        @Override
        protected int getDataCount() {
            return (mDataList != null)?mDataList.size():0;
        }

        @Override
        public boolean isSectionHeader(int positon) {
            return BaseSwipeRecyclerFragment.this.isSectionHeader(positon);
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
