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
import com.cloudhome.activity.MyOrder_CarInsuranceActivity;
import com.cloudhome.activity.OrderPayWebActivity;
import com.cloudhome.bean.OrderListCarBean;

import java.util.ArrayList;

public class OrderListCarAdapter extends BaseAdapter {
    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<OrderListCarBean> dataMap;
    private LayoutInflater mInflater;
    private MyOrder_CarInsuranceActivity context;


    public OrderListCarAdapter(MyOrder_CarInsuranceActivity context, ArrayList<OrderListCarBean> dataMap) {
        super();
        this.dataMap = dataMap;
        mInflater = LayoutInflater.from(context);
        this.context = context;

    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != dataMap) {
            for (OrderListCarBean bean : dataMap) {
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

        for (OrderListCarBean bean : dataMap) {

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

        for (OrderListCarBean category : dataMap) {

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


                OrderListCarBean bean = (OrderListCarBean) getItem(position);
                viewHolder.tv_month_category.setText(bean.getMonthDivider());
                break;

            case TYPE_ITEM:

                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.item_data_order_car_list, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.orderno = (TextView) convertView.findViewById(R.id.orderno);
                    viewHolder.status = (TextView) convertView.findViewById(R.id.status);
                    viewHolder.productname = (TextView) convertView.findViewById(R.id.productname);
                    viewHolder.holdername = (TextView) convertView.findViewById(R.id.holdername);
                    viewHolder.taxValue = (TextView) convertView.findViewById(R.id.taxValue);
                    viewHolder.insureperiod = (TextView) convertView.findViewById(R.id.insureperiod);
                    //   viewHolder.fycs = (TextView) convertView.findViewById(R.id.fycs);
                    viewHolder.gotopay = (TextView) convertView.findViewById(R.id.gotopay);
                    viewHolder.cancel_the_order = (TextView) convertView.findViewById(R.id.cancel_the_order);
                    viewHolder.productimageurl = (ImageView) convertView.findViewById(R.id.productimageurl);
                    viewHolder.rel3 = (RelativeLayout) convertView.findViewById(R.id.rel3);


                    viewHolder.rel2 = (RelativeLayout) convertView.findViewById(R.id.rel2);
                    viewHolder.productimageurl2 = (ImageView) convertView.findViewById(R.id.productimageurl2);
                    viewHolder.productname2 = (TextView) convertView.findViewById(R.id.productname2);
                    viewHolder.holdername2 = (TextView) convertView.findViewById(R.id.holdername2);

                    viewHolder.taxValue2 = (TextView) convertView.findViewById(R.id.taxValue2);
                    viewHolder.insureperiod2 = (TextView) convertView.findViewById(R.id.insureperiod2);

                    viewHolder.moneys = (TextView) convertView.findViewById(R.id.moneys);
                    viewHolder.actualTaxFee = (TextView) convertView.findViewById(R.id.actualTaxFee);


                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }




                // 绑定数据
                final OrderListCarBean beanData = (OrderListCarBean) getItem(position);

                viewHolder.orderno.setText("订单号: " + beanData.getOrderno());

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
                            .load(beanData.getProductimageurl())
                            .centerCrop()
                            .placeholder(R.drawable.white_bg)
                            .crossFade()
                            .into( viewHolder.productimageurl);

                viewHolder.productname.setText(beanData.getProductname());
                viewHolder.taxValue.setText(beanData.getConcretePremium());

                viewHolder.holdername.setText("投保人: " + beanData.getHoldername());

                viewHolder.insureperiod.setText("保障期间: " + beanData.getInsureperiod());

                viewHolder.moneys.setText( beanData.getMoneys());
                viewHolder.actualTaxFee.setText( "元  （含车船税"+ beanData.getActualTaxFee()+"元）");


                if (beanData.getProductsize() == 1) {
                    viewHolder.rel2.setVisibility(View.GONE);

                } else {
                    viewHolder.rel2.setVisibility(View.VISIBLE);

                    String Productimageurl2 = beanData.getProductimageurl2();



                        Glide.with(context)
                                .load(Productimageurl2)
                                .centerCrop()
                                .placeholder(R.drawable.white_bg)
                                .crossFade()
                                .into( viewHolder.productimageurl2);



                    viewHolder.productname2.setText(beanData.getProductname2());
                    viewHolder.holdername2.setText("投保人: " + beanData.getHoldername2());

                    viewHolder.taxValue2.setText(beanData.getConcretePremium2());

                    viewHolder.insureperiod2.setText("保障期间: " + beanData.getInsureperiod2());


                }


                //  viewHolder.fycs.setText(beanData.getFycs());
                viewHolder.gotopay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


//                        Intent intent = new Intent(context, OrderPayActivity.class);
//
//                        intent.putExtra("title", beanData.getProductname());
//                        intent.putExtra("time", beanData.getOrdercreatetime());
//                        intent.putExtra("price", beanData.getMoneys());
//                        intent.putExtra("id", beanData.getId());
//                        intent.putExtra("orderno", beanData.getOrderno());
//                        intent.putExtra("entrance", "OrderList");
//
//                        context.startActivity(intent);


                                                Intent intent = new Intent(context, OrderPayWebActivity.class);

                        intent.putExtra("url",beanData.getOrderDetailUrl() );

                        intent.putExtra("title", "确认支付");


                        context.startActivity(intent);

                    }
                });

                viewHolder.cancel_the_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        context.setData(beanData.getOrderno());
                    }

                });
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
        TextView taxValue;
        TextView tv_month_category;
        TextView gotopay;
        TextView cancel_the_order;
        ImageView productimageurl;

        RelativeLayout rel2;

        ImageView productimageurl2;
        TextView productname2;
        TextView holdername2;

        TextView taxValue2;
        TextView insureperiod2;

        TextView  actualTaxFee;
        RelativeLayout rel3;

        //   RelativeLayout fycs_rel;


    }

}
