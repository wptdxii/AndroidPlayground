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
import com.cloudhome.utils.IpConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsuranceShopGridAdapter extends BaseAdapter {
	

	private Context context;
	private LayoutInflater layoutInflater;
	
	public List<Map<String, String>> list =new ArrayList<Map<String, String>>();

	public InsuranceShopGridAdapter(Context context) {
		super();
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}

	public void setData(List<Map<String, String>> list) {
		this.list = list;

	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.insurance_shop_gv_item, parent, false);
			holder.iv_insurance_shop=(ImageView) convertView.findViewById(R.id.iv_insurance_shop);
			holder.tv_insurance_shop=(TextView) convertView.findViewById(R.id.tv_insurance_shop);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
	

		String img_url =IpConfig.getIp3()+ list.get(position).get("img_url");




			Glide.with(context)
					.load(img_url)
					.centerCrop()
					.placeholder(R.drawable.white)  //占位图 图片正在加载
					.crossFade()
					.into(holder.iv_insurance_shop);




		holder.tv_insurance_shop.setText(list.get(position).get("title"));
		
		return convertView;
	}
	
	class ViewHolder {
		ImageView iv_insurance_shop;
		TextView tv_insurance_shop;
	}

}
