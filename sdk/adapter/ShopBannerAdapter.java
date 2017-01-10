package com.cloudhome.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.bean.ShopBannerData;

import java.util.ArrayList;

public class ShopBannerAdapter extends RecyclingPagerAdapter {

    public interface EventClick {
        void eventClick();
    }

    private Context context;
    private ArrayList<ShopBannerData> datas;

    private EventClick eventClick;

    private int size;

    public ShopBannerAdapter(Context context, ArrayList<ShopBannerData> datas, EventClick eventClick) {
        this.context = context;
        this.datas = datas;
        this.eventClick = eventClick;
        size = datas.size();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    private int getPosition(int position) {
        return position % size;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.imagepager_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_imageview);
            holder.imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    eventClick.eventClick();
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Log.d("3333",datas.get(getPosition(position)).getBannerUrl());
        Glide.with(context).load(datas.get(getPosition(position)).getBannerUrl())
                .into(holder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
