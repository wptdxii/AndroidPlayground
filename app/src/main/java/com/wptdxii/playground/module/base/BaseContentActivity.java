package com.wptdxii.playground.module.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wptdxii.playground.R;
import com.wptdxii.playground.model.Module;
import com.wptdxii.uikit.widget.swiperecycler.BaseSwipeViewHolder;

import java.util.ArrayList;

/**
 * 目录列表基类
 * Created by wptdxii on 2016/8/3 0003.
 */
public abstract class BaseContentActivity extends BaseSwipeRecyclerActivity<Module>{
    @Override
    protected void initListData(Bundle savedInstanceState) {
        //不显示刷新进度条
//        mSwipeRecycler.isInitWithRefreshBar(false);
//        不可刷新
//        mSwipeRecycler.enablePullToRefresh(false);
//        mSwipeRecycler.setRefreshing();
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
