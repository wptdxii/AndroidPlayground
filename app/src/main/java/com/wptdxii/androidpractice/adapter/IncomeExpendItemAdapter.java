package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.ArrayList;

public class IncomeExpendItemAdapter extends BaseAdapter {
	private ArrayList<ArrayList<String>> dataMap =new ArrayList<ArrayList<String>>();
	private Context context;
	private LayoutInflater inflater = null;
	
	
	
	public IncomeExpendItemAdapter(Context context, ArrayList<ArrayList<String>> dataMap) {
		super();
		this.dataMap = dataMap;
		this.context=context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataMap.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_income_expend_detail,parent, false);
			holder.tv_text_left = (TextView) convertView.findViewById(R.id.tv_text_left);
			holder.tv_text_right = (TextView) convertView.findViewById(R.id.tv_text_right);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ArrayList<String> key_value_list =dataMap.get(position);
		holder.tv_text_left.setText(key_value_list.get(0));
		holder.tv_text_right.setText(key_value_list.get(1));
		return convertView;
	}

	class ViewHolder {

		public TextView tv_text_left;
		public TextView tv_text_right;
	}

}
