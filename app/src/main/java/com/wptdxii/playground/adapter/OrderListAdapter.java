package com.cloudhome.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.CommonWebActivity;
import com.cloudhome.activity.MyNewOrderActivity;
import com.cloudhome.bean.OrderListBean;
import com.cloudhome.utils.IpConfig;

import java.util.ArrayList;

public class OrderListAdapter extends BaseAdapter {
    private ArrayList<OrderListBean> dataMap;
    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_ITEM = 1;
    private LayoutInflater mInflater;
    private MyNewOrderActivity context;
    private String mUserId;


    public OrderListAdapter(MyNewOrderActivity context, ArrayList<OrderListBean> dataMap, String userId) {
        super();
        this.dataMap = dataMap;
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mUserId = userId;

    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != dataMap) {
            for (OrderListBean bean : dataMap) {
                count += bean.getGroupItemCount();
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (null == dataMap || position < 0 || position > getCount()) {

            return null;
        }

        // 同一分类内，第一个元素的索引值
        int categroyFirstIndex = 0;

        for (OrderListBean bean : dataMap) {

            int size = bean.getGroupItemCount();


            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;

            // item在当前分类内
            if (categoryIndex < size) {

                return bean.getItem(categoryIndex);
            }

            // 索引移动到当前分类结尾，即下一个分类第一个元素索引
            categroyFirstIndex += size;
        }

        return null;
    }


    @Override
    public int getItemViewType(int position) {
        // 异常情况处理
        if (null == dataMap || position < 0 || position > getCount()) {

            return TYPE_ITEM;
        }

        int categroyFirstIndex = 0;

        for (OrderListBean category : dataMap) {

            int size = category.getGroupItemCount();

            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;


            if (categoryIndex == 0) {

                return TYPE_CATEGORY;

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
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private static final String TAG = "OrderListAdapter";
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        ViewHolder viewHolder;
        switch (itemViewType) {
            case TYPE_CATEGORY:
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.item_category_order_list, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.tv_month_category = (TextView) convertView.findViewById(R.id.tv_month_category);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }


                OrderListBean bean = (OrderListBean) getItem(position);
                viewHolder.tv_month_category.setText(bean.getMonthDivider());
                break;

            case TYPE_ITEM:
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.item_data_order_list, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.orderno = (TextView) convertView.findViewById(R.id.orderno);
                    viewHolder.status = (TextView) convertView.findViewById(R.id.status);
                    viewHolder.productname = (TextView) convertView.findViewById(R.id.productname);
                    viewHolder.holdername = (TextView) convertView.findViewById(R.id.holdername);
                    viewHolder.moneys = (TextView) convertView.findViewById(R.id.moneys);
                    viewHolder.insureperiod = (TextView) convertView.findViewById(R.id.insureperiod);
                    //   viewHolder.fycs = (TextView) convertView.findViewById(R.id.fycs);
                    viewHolder.gotopay = (TextView) convertView.findViewById(R.id.gotopay);
                    viewHolder.cancel_the_order = (TextView) convertView.findViewById(R.id.cancel_the_order);
                    viewHolder.productimageurl = (ImageView) convertView.findViewById(R.id.productimageurl);
                    viewHolder.rel3 = (RelativeLayout) convertView.findViewById(R.id.rel3);
                    viewHolder.moneys_rel = (RelativeLayout) convertView.findViewById(R.id.moneys_rel);
                    //    viewHolder.fycs_rel = (RelativeLayout) convertView.findViewById(R.id.fycs_rel);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }


                if (MyNewOrderActivity.ordertype.equals("卡单")) {
                    viewHolder.rel3.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.rel3.setVisibility(View.GONE);
                }

                if (MyNewOrderActivity.ordertype.equals("赠险")) {
                    viewHolder.moneys_rel.setVisibility(View.GONE);
                } else {
                    viewHolder.moneys_rel.setVisibility(View.VISIBLE);
                }

                // 绑定数据
                final OrderListBean beanData = (OrderListBean) getItem(position);

                viewHolder.orderno.setText("订单号: " + beanData.getOrderNo());

                String status = beanData.getStatus();

                if (status.equals("待支付")) {
                    viewHolder.rel3.setVisibility(View.VISIBLE);
                    viewHolder.gotopay.setVisibility(View.VISIBLE);
                    viewHolder.cancel_the_order.setVisibility(View.VISIBLE);


                } else {

                    //   viewHolder.gotopay.setVisibility(View.GONE);
                    //   viewHolder.cancel_the_order.setVisibility(View.GONE);


                    viewHolder.rel3.setVisibility(View.GONE);
                }
                viewHolder.status.setText(status);


                Glide.with(context)
                        .load(beanData.getProductImageurl())
                        .centerCrop()
                        .placeholder(R.drawable.white_bg)
                        .crossFade()
                        .into(viewHolder.productimageurl);


                viewHolder.productname.setText(beanData.getProductName());
                viewHolder.moneys.setText(beanData.getMoneys());

                viewHolder.holdername.setText("投保人: " + beanData.getHolderName());

                viewHolder.insureperiod.setText("保障期间: " + beanData.getInsurePeriod());

                //  viewHolder.fycs.setText(beanData.getFycs());
                viewHolder.gotopay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String payUrl = IpConfig.getUri4("webPay") + "orderid=" + beanData.getId()
                                + "&userid=" + mUserId ;
                        Intent intent = new Intent(context, CommonWebActivity.class);
                        intent.putExtra("title", "确认支付");
                        intent.putExtra("web_address", payUrl);
                        intent.putExtra("needShare", false);
                        intent.putExtra("isBackRefresh", true);
                        context.startActivity(intent);
                    }
                });

                viewHolder.cancel_the_order.setOnClickListener(new View.OnClickListener() {
                                                                   @Override
                                                                   public void onClick(View v) {

                                                                       context.setData(beanData.getId());
                                                                   }
                                                               }
                );
                break;
        }

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != TYPE_CATEGORY;
    }

    private class ViewHolder {

        TextView orderno;
        TextView status;
        TextView productname;
        TextView holdername;
        TextView moneys;
        TextView insureperiod;
        TextView tv_month_category;
        TextView gotopay;
        TextView cancel_the_order;
        ImageView productimageurl;
        RelativeLayout rel3;
        RelativeLayout moneys_rel;
        //   RelativeLayout fycs_rel;
    }

}
