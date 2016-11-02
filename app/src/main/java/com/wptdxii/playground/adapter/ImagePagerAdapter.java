/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.wptdxii.playground.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.salvage.RecyclingPagerAdapter;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.BannerModel;
import cn.ubaby.ubaby.util.ImageHelper;


/**
 * ImagePagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private List list;
    private int size;
    private boolean isInfiniteLoop;
    private OnClickListener onClickListener = null;
    private String from;

    public ImagePagerAdapter(Context context, List list, String from, boolean isInfiniteLoop) {
        this.context = context;
        this.list = list;
        this.size = list.size();
        this.isInfiniteLoop = isInfiniteLoop;
        this.from = from;
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : list.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    public View getView(int position, View view, ViewGroup container) {

        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.auto_scroll_page_item, null);
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
            if (onClickListener != null) {
                view.setOnClickListener(onClickListener);
            }

        } else {
            holder = (ViewHolder) view.getTag();
        }

        BannerModel banner = (BannerModel) list.get(getPosition(position));
        if (!TextUtils.isEmpty(banner.getImgUrl())) {
            if ("player".equals(from)) {
                ImageHelper.displayImage(holder.imageView, banner.getImgUrl(),R.drawable.pic_default_player_show);
            } else if ("find".equals(from)) {
                ImageHelper.displayImage(holder.imageView, banner.getImgUrl(),R.drawable.pic_default_first_banner);
            } else if ("category".equals(from)) {
                ImageHelper.displayImage(holder.imageView, banner.getImgUrl(),R.drawable.pic_default_first_banner);
            }

        }
        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}
