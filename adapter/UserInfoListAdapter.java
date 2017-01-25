package com.cloudhome.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;

import java.util.List;
import java.util.Map;

public class UserInfoListAdapter extends BaseAdapter {

	public List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;

	public UserInfoListAdapter(Context context) {
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

	@SuppressLint("InflateParams") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		UserInfo_Holder holder = null;
		if (convertView == null) {

			holder = new UserInfo_Holder();

			convertView = inflater.inflate(R.layout.userinfo_list_item,
					null);
		
			holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
			holder.iv_name = (TextView) convertView.findViewById(R.id.iv_name);
		
			
			
			convertView.setTag(holder);
		} else {

			holder = (UserInfo_Holder) convertView.getTag();
			
			resetViewHolder(holder);
		}

		String img_url = list.get(position).get("icon");



		holder.iv_name.setText(list.get(position).get("name"));

		Glide.with(context)
				.load(img_url)
				.centerCrop()
				.placeholder(R.drawable.white)  //占位图 图片正在加载
				.crossFade()
				.into( holder.iv_img);
	
		return convertView;
	}

	
	protected void resetViewHolder(UserInfo_Holder p_ViewHolder) {

		p_ViewHolder.iv_img.setImageResource(R.drawable.white_bg);

	}
	
	public static class  UserInfo_Holder {

		public ImageView iv_img;
		public TextView iv_name;

	}

}
