package com.wptdxii.playground.ui.sample.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wptdxii.playground.R;
import com.wptdxii.playground.imageloader.ImageLoader;
import com.wptdxii.domain.model.gank.GankModel;

import java.util.ArrayList;

/**
 * Created by wptdxii on 2016/10/10 0010.
 */

public class SampleRecyclerViewAdapter extends RecyclerView.Adapter<SampleRecyclerViewAdapter.SampleViewHolder> {
    private LayoutInflater mLayoutInflater;
    private ArrayList<GankModel> mDataList;

    public SampleRecyclerViewAdapter(Context context, ArrayList<GankModel> dataList) {
        this.mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_sample_recyclerview, parent, false);
        return new SampleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SampleViewHolder holder, int position) {
        if (mDataList.size() > 0) {
            GankModel gankModel = mDataList.get(position);
            ImageLoader.loadImage(holder.imageView.getContext(), holder.imageView, gankModel.getUrl());
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class SampleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SampleViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgView);
        }
    }
}