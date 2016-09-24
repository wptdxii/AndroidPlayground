package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Proposal_Select_Adapter extends BaseAdapter {

	public ArrayList<HashMap<String, String>> list;

	private Context context;

	private LayoutInflater inflater = null;

	public Proposal_Select_Adapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);

	}

	public void setData(ArrayList<HashMap<String, String>> list) {
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
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.p_s_item, parent, false);

			holder.p_s_rel = (RelativeLayout) convertView
					.findViewById(R.id.p_s_rel);
			holder.policy_title = (TextView) convertView
					.findViewById(R.id.policy_title);
			holder.policy_type_img = (ImageView) convertView
					.findViewById(R.id.policy_type_img);
			holder.p_s_select_img = (ImageView) convertView
					.findViewById(R.id.p_s_select_img);
			holder.info_img = (ImageView) convertView
					.findViewById(R.id.info_img);
			holder.p_s_cb = (CheckBox) convertView.findViewById(R.id.p_s_cb);
			holder.iv_product_right = (ImageView) convertView
					.findViewById(R.id.iv_product_right);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();

			resetViewHolder(holder);

		}


		String icon_url =list.get(position).get("icon");

		
		 Log.d(position+"",icon_url);


		Glide.with(context)
				.load(icon_url)
				.centerCrop()
				.placeholder(R.drawable.white)  //占位图 图片正在加载
				.crossFade()
				.into( holder.info_img);


		String img_url =  list.get(position).get("type_icon");
        



		Glide.with(context)
				.load(img_url)
				.centerCrop()
				.placeholder(R.drawable.white)  //占位图 图片正在加载
				.crossFade()
				.into( holder.policy_type_img);



		String is_group = list.get(position).get("is_group");

		if (is_group.equals("false")) {
			holder.iv_product_right.setVisibility(View.GONE);

		} else {
			holder.iv_product_right.setVisibility(View.VISIBLE);
		}
		
		holder.policy_title.setText(list.get(position).get("name"));

		return convertView;
	}

	public static class ViewHolder {
		public TextView policy_title;
		public ImageView policy_type_img;
		public ImageView p_s_select_img;
		public ImageView info_img;
		public ImageView iv_product_right;
		public CheckBox p_s_cb;
		public RelativeLayout p_s_rel;

	}

	protected void resetViewHolder(ViewHolder p_ViewHolder) {

		p_ViewHolder.policy_title.setText(null);

	}

}
