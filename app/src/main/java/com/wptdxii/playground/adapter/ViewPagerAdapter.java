/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.wptdxii.playground.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.salvage.RecyclingPagerAdapter;

import java.util.List;


/**
 * ViewPagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ViewPagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private List list;
    private int size;
    private boolean isInfiniteLoop;

    public ViewPagerAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
        this.size = list.size();
        isInfiniteLoop = false;
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
        return (View) list.get(position);
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop
     *            the isInfiniteLoop to set
     */
    public ViewPagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }

}