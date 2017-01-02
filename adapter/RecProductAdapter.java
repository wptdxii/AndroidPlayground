package com.cloudhome.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.activity.RecProductDetailActivity;

import java.util.List;
import java.util.Map;

public class RecProductAdapter extends BaseAdapter {

	public List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;


	public RecProductAdapter(Context context) {
		this.context = context;

		inflater = LayoutInflater.from(context);
	}

	public void setData(List<Map<String, String>> list) {
		this.list = list;

	}

	@Override
	public int getCount() {

		if(list.size()<=2)
		{
			return list.size();
		}else if(RecProductDetailActivity.morePriceShow){
			return list.size();
		}else{
			return 2;
		}



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
			convertView = inflater.inflate(R.layout.rec_p_d_item,
					null);

			viewHolder = new ViewHolder();

			viewHolder.p_name = (TextView) convertView.findViewById(R.id.p_name);
			viewHolder.p_price = (TextView) convertView.findViewById(R.id.p_price);

			convertView.setTag(viewHolder);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
			// viewHolder = (ViewHolder) convertView.getTag();
			resetViewHolder(viewHolder);

		}



		Log.d("44444", list.get(position).get("name"));
		viewHolder.p_name.setText(list.get(position).get("name"));

		viewHolder.p_price.setText(list.get(position).get("charge"));
		return convertView;
	}


	protected void resetViewHolder(ViewHolder p_ViewHolder) {


		p_ViewHolder.p_name.setText(null);
		p_ViewHolder.p_price.setText(null);


	}

	public class ViewHolder {

		public TextView p_name;
		public TextView p_price;


	}

}
