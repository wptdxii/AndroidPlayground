package com.cloudhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cloudhome.R;
import com.cloudhome.bean.AdministerChildBean;
import com.cloudhome.bean.AdministerGroupBean;
import com.cloudhome.view.customview.GlideRoundTransform;

import java.util.List;

/**
 * Created by wptdxii on 2016/10/27 0027.
 */

public class AdministerExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<AdministerGroupBean> mGroup;
    private List<List<AdministerChildBean>> mChild;
    private String mUserType;

    public AdministerExpandableListAdapter(Context context, List<AdministerGroupBean> group,
                                           List<List<AdministerChildBean>> child, String userType) {
        this.mContext = context;
        this.mGroup = group;
        this.mChild = child;
        this.mUserType = userType;
    }

    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChild.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChild.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_administer_group, parent, false);
            viewHolder = new GroupViewHolder();
            viewHolder.groupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            viewHolder.groupNumber = (TextView) convertView.findViewById(R.id.tv_group_number);
            viewHolder.groupArrow = (ImageView) convertView.findViewById(R.id.img_group_arrow);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        AdministerGroupBean bean = mGroup.get(groupPosition);
        viewHolder.groupName.setText(bean.getProvinceName());
        viewHolder.groupNumber.setText(bean.getDirectUserTotal() + "");

        if (isExpanded) {
            viewHolder.groupArrow.setBackgroundResource(R.drawable.icon_down);
        } else {
            viewHolder.groupArrow.setBackgroundResource(R.drawable.icon_right);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_administer_child, parent, false);
            viewHolder = new ChildViewHolder();
            viewHolder.childAvatar = (ImageView) convertView.findViewById(R.id.img_client_avatar);
            viewHolder.childAuth = (ImageView) convertView.findViewById(R.id.img_client_auth);
            viewHolder.childName = (TextView) convertView.findViewById(R.id.tv_client_name);
            viewHolder.childPhoneNum = (TextView) convertView.findViewById(R.id.tv_child_phonenumber);
            viewHolder.childMonthlyListNum = (TextView) convertView.findViewById(R.id.tv_child_monthlypolicy_num);
            viewHolder.childScaleNum = (TextView) convertView.findViewById(R.id.tv_client_policyscale_num);
            viewHolder.childAdministerNum = (TextView) convertView.findViewById(R.id.tv_child_administer_num);
            viewHolder.childAdminister = (LinearLayout) convertView.findViewById(R.id.ll_child_administer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        AdministerChildBean bean = mChild.get(groupPosition).get(childPosition);
        viewHolder.childName.setText(bean.getName());
        viewHolder.childPhoneNum.setText(bean.getMobile());
        viewHolder.childMonthlyListNum.setText(bean.getTotal() + "");
        viewHolder.childScaleNum.setText(bean.getAmount() + "");

        if ("00".equals(mUserType)) {
            viewHolder.childAdministerNum.setText(bean.getDirectUserCount() + "");
            viewHolder.childAdminister.setVisibility(View.VISIBLE);
        } else {
            viewHolder.childAdminister.setVisibility(View.GONE);
        }


        if ("00".equals(bean.getState())) {
            viewHolder.childAuth.setVisibility(View.VISIBLE);
        } else {
            viewHolder.childAuth.setVisibility(View.INVISIBLE);
        }

        Glide.with(mContext)
                .load(bean.getAvatar())
                .placeholder(R.drawable.icon_avatar)
                .crossFade()
                .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext, 10))//设置圆角图片
                .into(viewHolder.childAvatar);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class GroupViewHolder {
        TextView groupName;
        TextView groupNumber;
        ImageView groupArrow;
    }

    private static class ChildViewHolder {
        LinearLayout childAdminister;
        ImageView childAvatar;
        ImageView childAuth;
        TextView childName;
        TextView childPhoneNum;
        TextView childAdministerNum;
        TextView childMonthlyListNum;
        TextView childScaleNum;
    }


}