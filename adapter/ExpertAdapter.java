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
import com.cloudhome.bean.ExpertBean;

import java.util.List;

/**
 * Created by wptdxii on 2017/1/11 0011.
 */

public class ExpertAdapter extends BaseAdapter {
    private Context mContext;
    private List<ExpertBean> mData;
    private LayoutInflater mLayoutInflater;

    public ExpertAdapter(Context context, List<ExpertBean> data) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_expert, parent, false);

            viewHolder.username = (TextView) convertView
                    .findViewById(R.id.pro_user_name);
            viewHolder.companyName = (TextView) convertView
                    .findViewById(R.id.pro_company_name);
            viewHolder.mobileArea = (TextView) convertView
                    .findViewById(R.id.pro_mobile_area);
            viewHolder.likeNum = (TextView) convertView.findViewById(R.id.pro_like_num);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.q_image);
            viewHolder.personalContext = (TextView) convertView.findViewById(R.id.pro_context);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ExpertBean expertBean = mData.get(position);
        viewHolder.username.setText(expertBean.getUser_name());
        viewHolder.companyName.setText(expertBean.getCompany_name() + "|");
        viewHolder.mobileArea.setText(expertBean.getMobile_area());
        viewHolder.likeNum.setText(expertBean.getGood_count() + "赞");
        viewHolder.personalContext.setText(expertBean.getPersonal_context());
        Glide.with(mContext)
                .load(expertBean.getAvatar())
                .placeholder(R.drawable.expert_head)
                .into(viewHolder.avatar);
        return convertView;
    }

    private static class ViewHolder {
        TextView username;
        TextView companyName;
        TextView mobileArea;
        TextView likeNum;
        ImageView avatar;
        TextView personalContext;
    }

}
