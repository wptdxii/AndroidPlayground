package com.wptdxii.androidpractice.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.model.Module;
import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeViewHolder;
import com.wptdxii.androidpractice.widget.swiperecycler.SwipeRecycler;

import java.util.ArrayList;

/**
 * 目录列表基类
 * Created by wptdxii on 2016/8/3 0003.
 */
public abstract class BaseContentActivity extends BaseSwipeRecyclerActivity<Module>{

    private SwipeRecycler mSwipeRecycler;
    private ArrayList<Module> mDataList;
    
    @Override
    protected void initListData(Bundle savedInstanceState) {
        mSwipeRecycler = getSwipeRecycler();
        mDataList = getDataList();
        //不显示刷新进度条
        mSwipeRecycler.isInitWithRefreshBar(false);
        //不可刷新
        mSwipeRecycler.enablePullToRefresh(false);
        mSwipeRecycler.setRefreshing();
    }

    @Override
    protected BaseSwipeViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_base_content_item, parent, false);
        return new BaseContentActivityViewHolder(view);
    }

    @Override
    public void onRefresh(int action) {
        addItem(mDataList);
        mSwipeRecycler.onRefreshCompleted();
    }

    protected abstract void addItem(ArrayList<Module> mDataList);

    private class BaseContentActivityViewHolder extends BaseSwipeViewHolder {
        private TextView baseContentActivityItemLabel;
        public BaseContentActivityViewHolder(View view) {
            super(view);
            baseContentActivityItemLabel = (TextView) view.findViewById(R.id.baseContentActivityItemLabel);
        }

        @Override
        protected void onItemClick(View view, int position) {
            Intent intent = new Intent(BaseContentActivity.this, mDataList.get(position).getTargetActivity());
            startActivity(intent);
        }

        @Override
        protected void onBindViewHolder(int position) {
            baseContentActivityItemLabel.setText(mDataList.get(position).getModelName());
        }
    }
}