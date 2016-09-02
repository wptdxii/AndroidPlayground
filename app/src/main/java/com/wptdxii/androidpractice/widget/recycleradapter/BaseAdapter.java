package com.wptdxii.androidpractice.widget.recycleradapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wptdxii on 2016/9/2 0002.
 */
public class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private List<T> mData;
    private int mLayoutResId;
    private View mContentView;

    public BaseAdapter(List<T> data) {
        this.mData = data;
    }

    public BaseAdapter(@LayoutRes int layoutResId, List<T> data) {
        mData = (data == null) ? new ArrayList<T>() : data;
        this.mLayoutResId = layoutResId;
    }

    public BaseAdapter(View contentView, List<T> data) {
        this.mContentView = contentView;
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
        mData.add(item);
        notifyItemInserted(positon);
    }
}
