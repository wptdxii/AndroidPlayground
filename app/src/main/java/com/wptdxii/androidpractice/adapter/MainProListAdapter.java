package com.wptdxii.androidpractice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cloudhome.R;
import com.cloudhome.bean.MainProBean;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.GlideRoundTransform;

import java.util.ArrayList;


public class MainProListAdapter extends BaseAdapter {

	public ArrayList<MainProBean> list;
	private Context context;
	private LayoutInflater inflater = null;
	private boolean isGroup=false;
	final int TYPE_PRODUCT = 0;
	final int TYPE_ADVERTISE = 1;
	private int advertiseWidth;
	private int advertiseHeight;
	SharedPreferences sp;
	private String loginString;
	private String user_state;
    private boolean isCommisionShown = true;

	public MainProListAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		advertiseWidth = Common.getScreenWidth(context)-Common.dip2px(context,10)*2;
		advertiseHeight= (int) (advertiseWidth/2.96);
		sp = context.getSharedPreferences("userInfo", 0);

		Log.i("advertiseWidth---",advertiseWidth+"");
		Log.i("advertiseHeight---",advertiseHeight+"");
	}
	public MainProListAdapter(Context context, boolean isGroup) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.isGroup=isGroup;
		advertiseWidth = Common.getScreenWidth(context)-Common.dip2px(context,10)*2;
		advertiseHeight= (int) (advertiseWidth/2.96);
		sp = context.getSharedPreferences("userInfo", 0);
		Log.i("advertiseWidth---",advertiseWidth+"");
		Log.i("advertiseHeight---",advertiseHeight+"");
	}

	public void setData(ArrayList<MainProBean> list) {
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

	@SuppressLint("InflateParams") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder =null;
		AdvertiseHolder holder2=null;
		int type=getItemViewType(position);
		if (convertView == null) {
			switch (type){
				case TYPE_PRODUCT:
					holder = new ViewHolder();
					convertView = inflater.inflate(R.layout.item_main_product,parent,false);
					holder.iv_pro_img = (ImageView) convertView.findViewById(R.id.iv_pro_img);
					holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
					holder.iv_eye  = (ImageView) convertView.findViewById(R.id.iv_eye);
					holder.tv_popularityReal = (TextView) convertView.findViewById(R.id.tv_popularityReal);
                    holder.tv_commision_title = (TextView) convertView.findViewById(R.id.tv_commision_title);
					holder.tv_commision = (TextView) convertView.findViewById(R.id.tv_commision);
                    holder.tv_symbol = (TextView) convertView.findViewById(R.id.tv_symbol);
					holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
					holder.view =  convertView.findViewById(R.id.view);
					convertView.setTag(holder);
					break;
				case TYPE_ADVERTISE:
					holder2 = new AdvertiseHolder();
					convertView = inflater.inflate(R.layout.item_main_advertisement,parent,false);
					holder2.tv_advertise_name= (TextView) convertView.findViewById(R.id.tv_advertise_name);
					holder2.iv_advertise= (ImageView) convertView.findViewById(R.id.iv_advertise);
					RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) holder2.iv_advertise.getLayoutParams();
					params.height=advertiseHeight;
					params.width=advertiseWidth;
					holder2.iv_advertise.setLayoutParams(params);
					holder2.view2=convertView.findViewById(R.id.view);
					convertView.setTag(holder2);
					break;
			}
		} else {
			switch (type) {
				case TYPE_PRODUCT:
					holder = (ViewHolder) convertView.getTag();
					break;
				case TYPE_ADVERTISE:
					holder2=(AdvertiseHolder)convertView.getTag();
					break;
			}

			}


        final MainProBean bean = list.get(position);
        switch (type) {
            case TYPE_PRODUCT:
                //                holder.tv_commision.setOnClickListener(new View.OnClickListener() {
                //                    @Override
                //                    public void onClick(View view) {
                //                        loginString = sp.getString("Login_STATE", "none");
                //                        user_state = sp.getString("Login_CERT", "none");
                //                        //00已认证 01未认证 02认证中 03认证失败
                //                        if (loginString.equals("none")) {
                //                            Intent intent = new Intent();
                //                            intent.setClass(context, LoginActivity.class);
                //                            context.startActivity(intent);
                //                        } else if (user_state.equals("00")) {
                //                            Toast.makeText(context, bean.getRatepostfix() + bean.getRate() + "%", Toast.LENGTH_SHORT).show();
                //                        } else if (user_state.equals("01")) {
                //                            Intent intent = new Intent();
                //                            intent.setClass(context, VerifyMemberActivity.class);
                //                            context.startActivity(intent);
                //                        } else if (user_state.equals("02")) {
                //                            Intent intent = new Intent();
                //                            intent.setClass(context, VerifiedInfoActivity.class);
                //                            context.startActivity(intent);
                //                        } else if (user_state.equals("03")) {
                //                            Intent intent = new Intent();
                //                            intent.setClass(context, Verified_failure_InfoActivity.class);
                //                            context.startActivity(intent);
                //                        }
                //
                //                    }
                //                });
                String url = IpConfig.getIp3() + bean.getImgurl();
                holder.tv_title.setText(bean.getName());

				if(isGroup){
					holder.tv_desc.setText(bean.getFeature());
					holder.tv_price.setText(bean.getPrices());
					holder.tv_commision.setVisibility(View.GONE);
					holder.tv_popularityReal.setText(bean.getHits());
                    holder.tv_commision_title.setVisibility(View.GONE);
                    holder.tv_commision.setVisibility(View.GONE);
                    holder.tv_symbol.setVisibility(View.GONE);
					Glide.with(context)
							.load(url)
                            .placeholder(R.drawable.ic_composite)
                            .error(R.drawable.ic_composite)
							.crossFade()
							.transform(new CenterCrop(context),new GlideRoundTransform(context, 8))  //设置圆角图片
							.into(holder.iv_pro_img);
				}else{
					Log.i("TYPE_PRODUCT",url);
					holder.tv_desc.setText(bean.getFeaturedesc());
                    holder.tv_commision.setText(bean.getRate() + "%");
                    holder.tv_price.setText(bean.getPrice());
                    if (isCommisionShown) {
                        holder.tv_commision_title.setVisibility(View.VISIBLE);
                        holder.tv_commision.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_commision_title.setVisibility(View.INVISIBLE);
                        holder.tv_commision.setVisibility(View.INVISIBLE);
                    }
					holder.tv_popularityReal.setText(bean.getPopularityreal());
					Glide.with(context)
							.load(url)
							.placeholder(R.drawable.white)
							.crossFade()
							.transform(new CenterCrop(context),new GlideRoundTransform(context, 8))  //设置圆角图片
							.into(holder.iv_pro_img);
				}
				break;
			case TYPE_ADVERTISE:
				holder2.tv_advertise_name.setText(bean.getTitle());
				String url2=bean.getBannerUrl();
				Log.i("TYPE_ADVERTISE",url2);
				Glide.with(context)
						.load(url2)
						.placeholder(R.drawable.white)
						.crossFade()

						.into(holder2.iv_advertise);
				break;
		}


		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if(list.get(position).isAdvertisement()){
			return TYPE_ADVERTISE;
		}else{
			return TYPE_PRODUCT;
		}
	}

	public static class ViewHolder {
		 ImageView iv_pro_img;
		 TextView tv_title;
		 TextView tv_desc;
		 ImageView iv_eye;
		 TextView tv_popularityReal;
        TextView tv_commision_title;
		 TextView tv_commision;
        TextView tv_symbol;
		 TextView tv_price;
		 View view;
	}
	public static class AdvertiseHolder {
		TextView tv_advertise_name;
		ImageView iv_advertise;
		View view2;
	}

    public void isCommissionShown(boolean isCommisionShown) {
        this.isCommisionShown = isCommisionShown;
    }
}
