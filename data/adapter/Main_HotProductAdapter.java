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

import java.util.List;
import java.util.Map;

public class Main_HotProductAdapter extends BaseAdapter {

	public List<Map<String, String>> list;

	private Context context;

	private LayoutInflater inflater = null;

	public Main_HotProductAdapter(Context context) {
		this.context = context;

		inflater = LayoutInflater.from(context);

	}

	public void setData(List<Map<String, String>> list) {
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
		Proposal_CompanyHolder holder = null;
		if (convertView == null) {

			holder = new Proposal_CompanyHolder();

			convertView = inflater.inflate(
					R.layout.hot_product_item, parent, false);
			holder.hot_product_img = (ImageView) convertView
					.findViewById(R.id.hot_product_img);
			holder.title = (TextView) convertView
					.findViewById(R.id.title);
			holder.biref = (TextView) convertView
					.findViewById(R.id.biref);
			holder.price = (TextView) convertView
					.findViewById(R.id.price);
			
			convertView.setTag(holder);
		} else {

			holder = (Proposal_CompanyHolder) convertView.getTag();
			
			
			
		}
	  
		String img_url=IpConfig.getIp3()+list.get(position).get("flat_img");



		Glide.with(context)
				.load(img_url)
				.centerCrop()
				.placeholder(R.drawable.white)  //占位图 图片正在加载
				.crossFade()
				.into(holder.hot_product_img);

		holder.title.setText(list.get(position).get("title"));
		holder.biref.setText(list.get(position).get("biref"));
		holder.price.setText(list.get(position).get("price"));
		
		
		return convertView;
	}

	public static class Proposal_CompanyHolder {

		public ImageView hot_product_img;
		public TextView title;
		public TextView biref;
		public TextView price;

	}

}
