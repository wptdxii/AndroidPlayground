package com.cloudhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cloudhome.R;
import com.cloudhome.bean.CarInsuranceInsuredBean;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.GlideRoundTransform;

import java.util.List;

/**
 * Created by wptdxii on 2017/1/13 0013.
 */

public class CarInsuranceInsuredAdapter extends BaseAdapter {
    private static final String TAG = "CarInsuranceInsuredAdap";
    private Context mContext;
    private LayoutInflater mLayoutInflator;
    private List<CarInsuranceInsuredBean.DataBean> mData;

    public CarInsuranceInsuredAdapter(Context context, List<CarInsuranceInsuredBean.DataBean> data) {
        this.mContext = context;
        this.mLayoutInflator = LayoutInflater.from(context);
        this.mData = data;
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
        CarInsuranceInsuredBean.DataBean bean = mData.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflator.inflate(R.layout.item_carinsuranceinsured, parent, false);
            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.img_carinsurance_logo);
            viewHolder.tvCategory = (TextView) convertView.findViewById(R.id.tv_carinsurance_category);
            viewHolder.tvIntroduction = (TextView) convertView.findViewById(R.id.tv_carinsurance_introduction);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (bean.getRegion() != null && !"".equals(bean.getRegion()) ) {
            viewHolder.tvCategory.setText(bean.getName() + "(" + bean.getRegion() + ")");
        } else {
            viewHolder.tvCategory.setText(bean.getName());
        }

        viewHolder.tvIntroduction.setText(bean.getContent());
        String logoUrl = IpConfig.getIp3()+"/"+bean.getImage();
        Glide.with(mContext)
                .load(logoUrl)
                .crossFade()
                .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext, 10))
                .into(viewHolder.imgLogo);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imgLogo;
        TextView tvCategory;
        TextView tvIntroduction;
    }
}
