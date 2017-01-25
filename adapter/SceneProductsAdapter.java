package com.cloudhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.InsuranceShopSceneActivity;
import com.cloudhome.utils.IpConfig;

import java.util.List;
import java.util.Map;

public class SceneProductsAdapter extends BaseAdapter {

	private List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;

	public SceneProductsAdapter(Context context) {
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

			convertView = inflater.inflate(R.layout.scene_products_list_item,
					null);

			holder.small_image = (ImageView) convertView
					.findViewById(R.id.small_image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.descriptions = (TextView) convertView
					.findViewById(R.id.descriptions);
			holder.promotion_expenses = (TextView) convertView
					.findViewById(R.id.promotion_expenses);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.p_expenses_add_rel = (RelativeLayout) convertView
					.findViewById(R.id.p_expenses_add_rel);
			holder.p_expenses_add_txt = (TextView) convertView
					.findViewById(R.id.p_expenses_add_txt);

			convertView.setTag(holder);
		} else {

			holder = (Proposal_CompanyHolder) convertView.getTag();
		}



		holder.title.setText(list.get(position).get("name"));
		holder.descriptions.setText(list.get(position).get("feature_desc"));

		String rate_extrastr = list.get(position).get("rate_extra");
		String rate          = list.get(position).get("rate");

		if (InsuranceShopSceneActivity.promotion_expenses_show) {

			if (rate_extrastr.equals("") || rate_extrastr.equals("null")) {
				holder.p_expenses_add_rel.setVisibility(View.INVISIBLE);

			} else {
				holder.p_expenses_add_rel.setVisibility(View.VISIBLE);
			}

			if (rate.equals("") || rate.equals("null")) {
				holder.promotion_expenses.setVisibility(View.INVISIBLE);

			} else {
				holder.promotion_expenses.setVisibility(View.VISIBLE);
			}

			holder.promotion_expenses.setText("推广费:"
					+ rate);
			holder.p_expenses_add_txt.setText(rate_extrastr);

		} else {
			holder.promotion_expenses.setVisibility(View.INVISIBLE);
			holder.p_expenses_add_rel.setVisibility(View.INVISIBLE);
		}

		holder.price.setText(list.get(position).get("price"));

		String img_url = IpConfig.getIp3() + list.get(position).get("img_url");



			Glide.with(context)
					.load(img_url)
					.centerCrop()
					.placeholder(R.drawable.white)  //占位图 图片正在加载
					.crossFade()
					.into( holder.small_image);

		return convertView;
	}

	public static class Proposal_CompanyHolder {


		public ImageView small_image;
		public TextView title;
		public TextView descriptions;
		public TextView promotion_expenses;
		public TextView price;
		public RelativeLayout p_expenses_add_rel;
		public TextView p_expenses_add_txt;
	}

}
