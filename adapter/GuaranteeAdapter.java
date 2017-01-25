package com.cloudhome.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.bean.GuaranteeBean;
import com.cloudhome.utils.Common;

import java.util.List;

/**
 * Created by wptdxii on 2016/11/10 0010.
 */

public class GuaranteeAdapter extends BaseAdapter {
    private static final int TYPE_SECTION = 0;
    private static final int TYPE_ITEM = 1;
    private List<GuaranteeBean> mData;
    private LayoutInflater mInflater;
    private Context mContext;

    public GuaranteeAdapter(Context context, List<GuaranteeBean> data) {
        this.mData = data;
        mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != mData) {
            for (GuaranteeBean bean : mData) {
                count += bean.getItemCount();
            }
        }

        return count;
    }

    @Override
    public Object getItem(int position) {

        if (null == mData || position < 0 || position > getCount()) {
            return null;
        }

        int categroyFirstIndex = 0;
        for (GuaranteeBean bean : mData) {
            int size = bean.getItemCount();
            int categoryIndex = position - categroyFirstIndex;
            if (categoryIndex < size) {
                return bean.getItem(categoryIndex);
            }

            categroyFirstIndex += size;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (null == mData || position < 0 || position > getCount()) {
            return TYPE_ITEM;
        }

        int categroyFirstIndex = 0;

        for (GuaranteeBean bean : mData) {
            int size = bean.getItemCount();
            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;
            if (categoryIndex == 0) {
                return TYPE_SECTION;
            }

            categroyFirstIndex += size;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SectionHolder sectionHolder = null;
        ItemHolder itemHolder = null;
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_SECTION:
                if (null == convertView) {
                    sectionHolder = new SectionHolder();
                    convertView = mInflater.inflate(R.layout.item_guarantee_section, null);

                    sectionHolder.tvSection = (TextView) convertView.findViewById(R.id.tv_guarantee_catetory);
                    convertView.setTag(sectionHolder);
                } else {
                    sectionHolder = (SectionHolder) convertView.getTag();
                }

                sectionHolder.tvSection.setText((String) getItem(position));
                break;

            case TYPE_ITEM:
                if (null == convertView) {
                    itemHolder = new ItemHolder();
                    convertView = mInflater.inflate(R.layout.item_guarantee, null);

                    itemHolder.rlItem = (RelativeLayout) convertView.findViewById(R.id.rl_guarantee);
                    itemHolder.tvItemKey = (TextView) convertView.findViewById(R.id.tv_guarantee_key);
                    itemHolder.tvItemValue = (TextView) convertView.findViewById(R.id.tv_guarantee_value);
                    convertView.setTag(itemHolder);
                } else {
                    itemHolder = (ItemHolder) convertView.getTag();
                }

                GuaranteeBean.ItemBean bean = (GuaranteeBean.ItemBean) getItem(position);
                itemHolder.tvItemKey.setText(bean.getItemKey());
                itemHolder.tvItemValue.setText(bean.getItemValue());

                setMaxWidth(itemHolder.tvItemKey);
                setMaxWidth(itemHolder.tvItemValue);

                if ("保障责任".equals(bean.getItemKey())) {
                    itemHolder.tvItemValue.setTextColor(Color.parseColor("#666666"));
                    setFakeBoldText(itemHolder.tvItemKey, true);
                    setFakeBoldText(itemHolder.tvItemValue, true);
                } else {
                    itemHolder.tvItemValue.setTextColor(Color.parseColor("#999999"));
                    setFakeBoldText(itemHolder.tvItemKey, false);
                    setFakeBoldText(itemHolder.tvItemValue, false);
                }

                if (position > 1 && ((GuaranteeBean.ItemBean)getItem(1)).getItemKey()
                        .equals(bean.getItemKey())) {
                    setMargins(itemHolder.rlItem, 0, Common.dip2px(mContext, 10), 0, 0);
                } else {
                    setMargins(itemHolder.rlItem, 0, 0, 0, 0);
                }

                if ("客服将与您电话联系，确定指定受益人".equals(bean.getItemKey())) {
                    itemHolder.tvItemKey.setMaxWidth(Common.getScreenWidth(mContext));
                    itemHolder.tvItemValue.setMaxWidth(0);
                    itemHolder.tvItemKey.setTextColor(Color.parseColor("#999999"));
                } else {
                    setMaxWidth(itemHolder.tvItemKey);
                    setMaxWidth(itemHolder.tvItemValue);
                    itemHolder.tvItemKey.setTextColor(Color.parseColor("#666666"));
                }

                break;
        }

        return convertView;
    }

    private void setMaxWidth(TextView textView) {
        textView.setMaxWidth((Common.getScreenWidth(mContext)) / 2);
    }

    private void setFakeBoldText(TextView textView, boolean fakeBoldText) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(fakeBoldText);
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
            layoutParams.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private class SectionHolder {
        TextView tvSection;
    }

    private class ItemHolder {
        RelativeLayout rlItem;
        TextView tvItemKey;
        TextView tvItemValue;
    }
}
