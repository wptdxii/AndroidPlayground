package com.cloudhome.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bkyj-005 on 2016/5/30.
 */
public class ChosenAdapter extends BaseAdapter{
    private List<Map<String, Object>> chosenList;
    private Context context;
    private LayoutInflater inflater;
    public ChosenAdapter(List<Map<String, Object>> chosenList, Context context) {
        this.chosenList = chosenList;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chosenList.size();
    }

    @Override
    public Object getItem(int i) {
        return chosenList.get(i);
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
            view=inflater.inflate(R.layout.item_select_recommend,null);
            holder.iv_left= (ImageView) view.findViewById(R.id.iv_left);
            holder.tv_title= (TextView) view.findViewById(R.id.tv_title);
            holder.tv_price= (TextView) view.findViewById(R.id.tv_price);
            holder.tv_price_unit= (TextView) view.findViewById(R.id.tv_price_unit);
            holder.tv_left_title= (TextView) view.findViewById(R.id.tv_left_title);
            holder.tv_left_title_fee= (TextView) view.findViewById(R.id.tv_left_title_fee);
            holder.iv_right_bottom= (ImageView) view.findViewById(R.id.iv_right_bottom);
            holder.rl_top_right= (LinearLayout) view.findViewById(R.id.rl_top_right);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        Map<String, Object> map=chosenList.get(i);
        holder.tv_title.setText(map.get("title").toString());


        Glide.with(context)
                .load(map.get("img").toString())
                .centerCrop()
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .crossFade()
                .into(holder.iv_left);



        holder.tv_price.setText(map.get("price").toString());
        holder.tv_price_unit.setText(map.get("unit").toString());
        holder.tv_left_title.setText(map.get("fee_note").toString());
        holder.tv_left_title_fee.setText(map.get("fee").toString());
        String fee_img=map.get("fee_img").toString();
        if(TextUtils.isEmpty(fee_img)){
            holder.iv_right_bottom.setVisibility(View.INVISIBLE);
        }else{
                  holder.iv_right_bottom.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(fee_img)
                    .centerCrop()
                    .placeholder(R.drawable.white)  //占位图 图片正在加载
                    .crossFade()
                    .into( holder.iv_right_bottom);


        }
        ArrayList<String> list= (ArrayList<String>) map.get("tags");
        Log.i("第"+i+"个的tag个数",list.size()+"");
        holder.rl_top_right.removeAllViews();
        for(int m=0;m<list.size();m++){
            ImageView iv=new ImageView(context);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin= Common.dip2px(context,4);
            iv.setLayoutParams(params);

            Glide.with(context)
                    .load(list.get(m))
                    .centerCrop()
                    .placeholder(R.drawable.white)  //占位图 图片正在加载
                    .crossFade()
                    .into( iv);


            holder.rl_top_right.addView(iv);
        }
        return view;
    }

    class ViewHolder{
        private ImageView iv_left;
        private LinearLayout rl_top_right;
        private TextView tv_title;
        private TextView tv_price;
        private TextView tv_price_unit;
        private TextView tv_left_title;
        private TextView tv_left_title_fee;
        private ImageView iv_right_bottom;
    }
}
