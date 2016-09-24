package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.List;
import java.util.Map;

public class IncomeRankAdapter extends BaseAdapter {

	private List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;

	public IncomeRankAdapter(Context context) {
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
		IncomeRankHolder holder = null;
		if (convertView == null) {

			holder = new IncomeRankHolder();

			convertView = inflater.inflate(R.layout.income_rank_list_item,parent,false);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.income = (TextView) convertView.findViewById(R.id.income);
			holder.incom_rank_name_lin = (LinearLayout) convertView.findViewById(R.id.incom_rank_name_lin);
			
			convertView.setTag(holder);
		} else {

			holder = (IncomeRankHolder) convertView.getTag();
		}

		if(position%2==0)
		{
			holder.incom_rank_name_lin.setBackgroundResource(R.color.white);
			
		}else{
			holder.incom_rank_name_lin.setBackgroundResource(R.color.management_allowance_bg);
		}
		

		holder.date.setText(list.get(position).get("date"));
		holder.name.setText(list.get(position).get("name"));
		holder.price.setText(list.get(position).get("price"));
		holder.income.setText(list.get(position).get("income"));
		
	
		
		return convertView;
	}

	public static class IncomeRankHolder {

		public TextView date;
		public TextView name;
		public TextView price;
		public TextView income;
		public LinearLayout incom_rank_name_lin;
	}

}
