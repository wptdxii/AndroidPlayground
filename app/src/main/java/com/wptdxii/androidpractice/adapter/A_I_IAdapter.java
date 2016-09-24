package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.bean.A_I_I_Data;
import com.cloudhome.utils.IpConfig;

import java.util.ArrayList;

public class A_I_IAdapter extends BaseAdapter {

	private ArrayList<A_I_I_Data> list;


	private LayoutInflater inflater = null;
    private Context context;
	public A_I_IAdapter(ArrayList<A_I_I_Data> list, Context context) {
		this.list = list;
		inflater = LayoutInflater.from(context);
		this.context = context;
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
		ViewHolder holder;
		if (convertView == null) {


			convertView = inflater.inflate(R.layout.a_i_i_item, parent, false);


			holder = new ViewHolder();
			holder.aii_img = (ImageView) convertView.findViewById(R.id.aii_img);

		
			convertView.setTag(holder);
			
		} else {

			holder = (ViewHolder) convertView.getTag();
			resetViewHolder(holder);
		}
		
		
	        String img_url=IpConfig.getIp3()+"/"+list.get(position).getImg();
	        




		Glide.with(context)
				.load(img_url)
				.centerCrop()
				.placeholder(R.drawable.white)
				.crossFade()
				.into(holder.aii_img);


		return convertView;
	}

	private void resetViewHolder(ViewHolder p_ViewHolder) {

		p_ViewHolder.aii_img .setImageResource(R.drawable.white_bg2);

	}
	
	public static class ViewHolder {

		public ImageView aii_img;

	}

}
