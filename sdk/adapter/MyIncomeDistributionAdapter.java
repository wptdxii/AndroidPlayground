package com.cloudhome.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.List;
import java.util.Map;

public class MyIncomeDistributionAdapter extends BaseAdapter {

	public List<Map<String, String>> list;

	private Context context;

	private LayoutInflater inflater = null;

	public MyIncomeDistributionAdapter(Context context) {
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
		MyIncomeDistributionHolder holder = null;
		if (convertView == null) {

			holder = new MyIncomeDistributionHolder();

			convertView = inflater.inflate(
					R.layout.my_income_distribution_item, parent, false);


			holder.point   = (ImageView) convertView.findViewById(R.id.point);
			holder.name    = (TextView) convertView.findViewById(R.id.name);
			holder.income  = (TextView) convertView.findViewById(R.id.income);

			convertView.setTag(holder);
		} else {

			holder = (MyIncomeDistributionHolder) convertView.getTag();

		}

		Map<String, String> map =list.get(position);

		GradientDrawable myGrad = (GradientDrawable)holder.point.getBackground();
		int color = Color.parseColor("#"+map.get("color"));
		myGrad.setColor(color);


		holder.name.setText(map.get("title"));

		holder.income.setText(map.get("income"));



		
		
		return convertView;
	}

	public static class MyIncomeDistributionHolder {

		private ImageView point;
		private TextView name;
		private TextView income;

	}

}
