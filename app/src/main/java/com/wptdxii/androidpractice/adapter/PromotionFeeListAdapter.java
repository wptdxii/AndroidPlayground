package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.CircleImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionFeeListAdapter extends BaseAdapter {

    public List<Map<String, String>> list;
    private Context context;
    private LayoutInflater inflater = null;
    public PromotionFeeListAdapter(Context context, List<Map<String, String>> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.promotion_fee_rank_item, parent, false);

            holder.rank_triangle_img = (ImageView) convertView.findViewById(R.id.rank_triangle_img);
            holder.userIcon    = (CircleImage) convertView.findViewById(R.id.userIcon);
            holder.username    = (TextView) convertView.findViewById(R.id.username);
            holder.income      = (TextView) convertView.findViewById(R.id.income);
            holder.num         = (TextView) convertView.findViewById(R.id.num);
            holder.arrow       = (ImageView) convertView.findViewById(R.id.arrow);
            holder.rank_num    = (TextView) convertView.findViewById(R.id.rank_num);
            holder.rl_top_item = (RelativeLayout) convertView.findViewById(R.id.rl_top_item);
            holder.promotion_fee_relation = (ImageView) convertView.findViewById(R.id.promotion_fee_relation);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resetViewHolder(holder);
        }

  if(position==0){

      holder.rank_triangle_img.setVisibility(View.VISIBLE);
      holder.rank_triangle_img.setImageResource(R.drawable.rank_gold);
      holder.rank_num.setText("1");
  }else if(position==1){
      holder.rank_triangle_img.setVisibility(View.VISIBLE);
      holder.rank_triangle_img.setImageResource(R.drawable.rank_siller);
      holder.rank_num.setText("2");
  }else if(position==2){

      holder.rank_triangle_img.setVisibility(View.VISIBLE);
      holder.rank_triangle_img.setImageResource(R.drawable.rank_cuprum);
      holder.rank_num.setText("3");
  }else {

      holder.rank_triangle_img.setVisibility(View.GONE);
      holder.rank_num.setText("");

  }



        HashMap<String, String> map = (HashMap<String, String>) list.get(position);


        String usrtype =map.get("usrtype");

        if(usrtype.equals("01")){


            holder.rl_top_item.setBackgroundResource(R.color.promotion_fee_list_item_bg);
            holder.promotion_fee_relation.setVisibility(View.GONE);


        }else if(usrtype.equals("02")){


            holder.rl_top_item.setBackgroundResource(R.color.white);
            holder.promotion_fee_relation.setVisibility(View.VISIBLE);

        }else if(usrtype.equals("03")){


            holder.rl_top_item.setBackgroundResource(R.color.white);
            holder.promotion_fee_relation.setVisibility(View.GONE);
        }




        holder.username.setText(map.get("username"));
        holder.income.setText("￥" + map.get("incom"));

        String url = map.get("avatar");


        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .crossFade()
                .into( holder.userIcon);


        String comtype = map.get("comtype");
        if (comtype.equals("kp")) {
            holder.arrow.setImageResource(R.drawable.income_keep);
            holder.num.setText("");
        } else if (comtype.equals("up")) {
            holder.arrow.setImageResource(R.drawable.income_up);
            holder.num.setText(map.get("remark"));
        } else {
            holder.arrow.setImageResource(R.drawable.income_down);
            holder.num.setText(map.get("remark"));
        }

        return convertView;
    }


    protected void resetViewHolder(ViewHolder p_ViewHolder) {

        p_ViewHolder.userIcon.setImageResource(R.drawable.white_bg2);

    }

    class ViewHolder {

        private RelativeLayout rl_top_item;
        private ImageView promotion_fee_relation;
        private ImageView rank_triangle_img;
        private CircleImage userIcon;
        private TextView username;
        private TextView income;
        private TextView num;
        private ImageView arrow;
        private TextView rank_num;
    }

}
