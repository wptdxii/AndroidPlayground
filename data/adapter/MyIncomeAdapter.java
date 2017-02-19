package com.cloudhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.List;
import java.util.Map;

public class MyIncomeAdapter extends BaseAdapter {

	private List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;

	public MyIncomeAdapter(Context context) {
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
		MyIncomeHolder holder = null;
		if (convertView == null) {

			holder = new MyIncomeHolder();

			convertView = inflater.inflate(R.layout.my_income_list_item,
					parent,false);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			convertView.setTag(holder);
		} else {

			holder = (MyIncomeHolder) convertView.getTag();
		}

	
		holder.title.setText(list.get(position).get("title"));
		holder.price.setText(list.get(position).get("price"));
		return convertView;
	}

	public static class MyIncomeHolder {

		public	TextView title;
		public	TextView price;
	}

}
