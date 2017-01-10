package com.cloudhome.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.BaseUMShare;
import com.cloudhome.bean.GiftNotReceive;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;

public class GiftUnReceiveListAdapter extends BaseAdapter {
    private ArrayList<GiftNotReceive> dataMap;
    private LayoutInflater mInflater;
    private Context context;
    private BaseUMShare share;

    public GiftUnReceiveListAdapter(Context context, ArrayList<GiftNotReceive> dataMap) {
        super();
        this.dataMap = dataMap;
        mInflater = LayoutInflater.from(context);
        this.context = context;

    }

    @Override
    public int getCount() {
        return dataMap.size();
    }

    @Override
    public Object getItem(int position) {
        return dataMap.get(position);
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gift_insurance_order_layout, null);
            holder.productimageurl = (ImageView) convertView.findViewById(R.id.productimageurl);
            holder.productname = (TextView) convertView.findViewById(R.id.productname);
            holder.share_tiem = (TextView) convertView.findViewById(R.id.share_tiem);
            holder.end_time = (TextView) convertView.findViewById(R.id.end_time);
            holder.share_again = (TextView) convertView.findViewById(R.id.share_again);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GiftNotReceive bean = dataMap.get(position);


        Glide.with(context)
                .load(bean.getProduct_icon())
                .centerCrop()
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .crossFade()
                .into( holder.productimageurl);




        holder.productname.setText(bean.getProduct_name());
        holder.share_tiem.setText(bean.getValid_time_begin());
        holder.end_time.setText(bean.getValid_time_end());
        holder.share_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("share", "dianjile");
                String share_title = bean.getShare_title();
                String share_url = bean.getShare_url();
                String brief = bean.getShare_desc();
                String img_url = bean.getShare_icon();
                share=new BaseUMShare(context,share_title, brief,share_url, img_url);
                share.openShare();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView productimageurl;
        TextView productname;
        TextView share_tiem;
        TextView end_time;
        TextView share_again;
    }


}
