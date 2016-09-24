package com.wptdxii.androidpractice.adapter;

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
import com.cloudhome.utils.IpConfig;

import java.util.List;
import java.util.Map;

public class HavebeenpaidAdapter extends BaseAdapter {

	public List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;

	public HavebeenpaidAdapter(Context context) {
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.to_be_paid_item,
					null);

			viewHolder = new ViewHolder();

			viewHolder.t_b_p_image = (ImageView) convertView
					.findViewById(R.id.t_b_p_image);
			viewHolder.subs_code = (TextView) convertView
					.findViewById(R.id.subs_code);
			viewHolder.product_name = (TextView) convertView
					.findViewById(R.id.product_name);
			viewHolder.holder_name = (TextView) convertView
					.findViewById(R.id.holder_name);
			viewHolder.insured_name = (TextView) convertView
					.findViewById(R.id.insured_name);
			viewHolder.period_from = (TextView) convertView
					.findViewById(R.id.period_from);
			viewHolder.period_to = (TextView) convertView
					.findViewById(R.id.period_to);
			viewHolder.insurance_num = (TextView) convertView
					.findViewById(R.id.insurance_num);
			viewHolder.real_payment = (TextView) convertView
					.findViewById(R.id.real_payment);

			convertView.setTag(viewHolder);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
			// viewHolder = (ViewHolder) convertView.getTag();
			resetViewHolder(viewHolder);

		}


		Map<String, String> map = list.get(position);
		String imgstr = IpConfig.getIp3() + "/"
				+ map.get("img_url");


		if (imgstr.length() > 6) {


			Glide.with(context)
					.load(imgstr)
					.centerCrop()
					.placeholder(R.drawable.white)  //占位图 图片正在加载
					.crossFade()
					.into( 	viewHolder.t_b_p_image);



		}

		viewHolder.subs_code.setText("订单流水号:"
				+ map.get("subs_code"));
		viewHolder.product_name.setText(map
				.get("product_name"));
		viewHolder.holder_name.setText(map
				.get("holder_name"));
		viewHolder.insured_name.setText(map
				.get("insurance_name"));
		viewHolder.period_from.setText(map
				.get("period_from")
				+ "至");
		viewHolder.period_to.setText(map.get("period_to"));
		viewHolder.real_payment.setText("￥"
				+ map.get("amount"));

		viewHolder.insurance_num.setText(map.get("counts"));
	
		return convertView;
	}


	protected void resetViewHolder(ViewHolder p_ViewHolder) {

		p_ViewHolder.t_b_p_image.setImageResource(R.drawable.white_bg2);

		p_ViewHolder.subs_code.setText(null);
		p_ViewHolder.product_name.setText(null);
		p_ViewHolder.holder_name.setText(null);
		p_ViewHolder.insured_name.setText(null);

		p_ViewHolder.period_from.setText(null);
		p_ViewHolder.period_to.setText(null);

		p_ViewHolder.real_payment.setText(null);

	}

	public class ViewHolder {

		public ImageView t_b_p_image;
		public TextView subs_code;
		public TextView product_name;
		public TextView holder_name;
		public TextView insured_name;
		public TextView period_from;
		public TextView period_to;
		public TextView insurance_num;
		public TextView real_payment;

	}

}
