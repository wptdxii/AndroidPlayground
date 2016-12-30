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

import java.util.List;
import java.util.Map;

/**
 * Created by bkyj-005 on 2016/5/30.
 */
public class MedicalAdapter extends BaseAdapter{
    private List<Map<String, Object>> medicalList;
    private Context context;
    private LayoutInflater inflater;
    public MedicalAdapter(List<Map<String, Object>> medicalList, Context context) {
        this.medicalList = medicalList;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return medicalList.size();
    }

    @Override
    public Object getItem(int i) {
        return medicalList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            holder=new ViewHolder();
            view=inflater.inflate(R.layout.item_medical_security,null);
            holder.iv_left= (ImageView) view.findViewById(R.id.iv_left);
            holder.tv_title= (TextView) view.findViewById(R.id.tv_title);
            holder.tv_price= (TextView) view.findViewById(R.id.tv_price);
            holder.tv_period= (TextView) view.findViewById(R.id.tv_period);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        Map<String, Object> map=medicalList.get(i);
        holder.tv_title.setText(map.get("title").toString());

        Glide.with(context)
                .load(map.get("img").toString())
                .centerCrop()
                .placeholder(R.drawable.white_bg)
                .crossFade()
                .into(holder.iv_left);


        holder.tv_price.setText(map.get("amount").toString());
        holder.tv_period.setText(map.get("period").toString());
        return view;
    }

    class ViewHolder{
        private ImageView iv_left;
        private TextView tv_title;
        private TextView tv_price;
        private TextView tv_period;
    }
}
