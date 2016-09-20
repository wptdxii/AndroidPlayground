package com.wptdxii.uikit.widget.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wptdxii on 2016/9/2 0002.
 */
public class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private boolean isLoadMoreEnable = false;
    private boolean isLoadingMoreEnable = false;
    private boolean isFirstOnlyEnable =true;
    protected List<T> mData;
    protected int mLayoutResId;
    protected View mLayout;
    
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private LinearLayout mEmptyView;

    public BaseAdapter(@LayoutRes int layoutResId,@Nullable List<T> data) {
        this.mData = (data == null ? new ArrayList<T>() : data);
        this.mLayoutResId = layoutResId;
    }

    public BaseAdapter(View layout, @Nullable List<T> data) {
        this.mLayout = layout;
        this.mData = data;
    }

    public BaseAdapter( @Nullable List<T> data) {
        this.mData = data;
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

    public void add(int positon, T item) {
        mData.add(positon,item);
        notifyItemInserted(positon);
    }

    public void remove(int positon) {
        mData.remove(positon);
        notifyItemRemoved(positon + getHeaderLayoutCount());
    }

    public int getHeaderLayoutCount() {
        
        return mHeaderLayout == null ? 0 : 1;
    }
    
    public int getFooterLayoutCount() {
        return mFooterLayout == null ? 0 : 1;
    }
    
    public int getEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }

    public void setData(List<T> data) {
        this.mData = data;
        
    }

    public void addData(List<T> additionalData) {
        this.mData.addAll(additionalData);
    }
    
    public List<T>  getData() {
        return  mData;
    }

    public T getItemData(int position) {
        return mData.get(position);
    }

}
