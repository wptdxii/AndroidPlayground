package com.wptdxii.uikit.widget.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by wptdxii on 2016/10/11 0011.
 */

public class BaseViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private View mItemView;
    protected List<T> mDataList;
    protected int mLayoutResId;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private View mEmptyView;
    private View mLoadMoreFailedView;

    private boolean mLoadMoreEnable;
    private int pageSize = -1;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    private OnLoadMoreListener mLoadMoreListener;
    public void setmLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;
    }



    public BaseViewAdapter(View itemView, @Nullable List<T> dataList) {

        this.mItemView = itemView;
        this.mDataList = (dataList == null ? new ArrayList<T>() : dataList);

    }

    public BaseViewAdapter(@LayoutRes int layoutResId, @Nullable List<T> dataList) {
        this(null, dataList);
        this.mLayoutResId = layoutResId;
    }


    public BaseViewAdapter(@Nullable List<T> dataList) {

        this(null, dataList);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setLoadMoreEnable(int pageSize) {
        this.mLoadMoreEnable = true;
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void addItemData(T itemData) {
        mDataList.add(itemData);
        this.notifyItemInserted(mDataList.size());
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = (dataList == null) ? new ArrayList<T>() : dataList;
        if (mLoadMoreListener != null) {
            mLoadMoreEnable = true;
        }

        if (mLoadMoreFailedView != null) {
            removeFooterView(mLoadMoreFailedView);
        }


    }

    public void addHeaderView(View headerView, int index) {
        if (mHeaderLayout == null) {
            initLinearLayout(mHeaderLayout, headerView);
        }

        index = (index >= mHeaderLayout.getChildCount() ? -1 : index);
        mHeaderLayout.addView(headerView, index);
        this.notifyDataSetChanged();
    }

    public void addHeaderView(View headerView) {
        addHeaderView(headerView, -1);
    }

    private void initLinearLayout(LinearLayout layout, View view) {
        layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }
    public void addFooterView(View footerView, int index) {
        mLoadMoreEnable = false;
        if (mFooterLayout == null) {

        }
    }
    public void removeHeaderView(View headerView) {
        if (mHeaderLayout == null) {
            return;
        }
        mHeaderLayout.removeView(headerView);
        if (mHeaderLayout.getChildCount() == 0) {
            mHeaderLayout = null;
        }
        this.notifyDataSetChanged();
    }

    public void removeFooterView(View footer) {
        if (mFooterLayout == null) {
            return;
        }

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            mFooterLayout = null;
        }
        this.notifyDataSetChanged();
    }

    public int getHeaderCount() {
        return mHeaderLayout == null ? 0 : 1;
    }

    public int getFooterCount() {
        return mFooterLayout == null ? 0 : 1;
    }

    public int getEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }


}
