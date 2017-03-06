package com.cloudhome.adapter;

import android.content.Context;

import com.cloudhome.view.wheel.wheelcity.adapters.AbstractWheelTextAdapter;

/**
 * Created by wptdxii on 2017/1/12 0012.
 */

public class ExpertWheelAdapter extends AbstractWheelTextAdapter {
    private Context mContext;
    private String[] mCountries;
    public ExpertWheelAdapter(Context context, int itemResource,
                              int itemTextResourceId,String[] countries) {
        super(context, itemResource, NO_RESOURCE);
        this.mContext = context;
        this.mCountries = countries;
        setItemTextResource(itemTextResourceId);
    }
    @Override
    protected CharSequence getItemText(int index) {
        return mCountries[index];
    }

    @Override
    public int getItemsCount() {
        return mCountries.length;
    }
}
