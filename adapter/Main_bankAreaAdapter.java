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

public class Main_bankAreaAdapter extends BaseAdapter {

	public List<Map<String, String>> list;

	private Context context;

	private LayoutInflater inflater = null;

	public Main_bankAreaAdapter(Context context) {
		this.context = context;

		inflater = LayoutInflater.from(context);

	}

	public void setData(List<Map<String, String>> list) {
		this.list = list;

	}

	@Override
	public int getCount() {
		if(list.size()>3)
		{
			return 3;
		}else{
			return list.size();
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		BankAreaHolder holder = null;
		if (convertView == null) {

			holder = new BankAreaHolder();
			convertView = inflater.inflate(
					R.layout.main_bankarea_item, parent, false);
			
			holder.rate = (TextView) convertView.findViewById(R.id.rate);
			holder.note01  = (TextView) convertView.findViewById(R.id.note01);
			holder.note02  = (TextView) convertView.findViewById(R.id.note02);
			holder.note03  = (TextView) convertView.findViewById(R.id.note03);
			holder.note04  = (TextView) convertView.findViewById(R.id.note04);
			holder.view2  = convertView.findViewById(R.id.view2);
			convertView.setTag(holder);
		} else {

			holder = (BankAreaHolder) convertView.getTag();
			
			
		}
		

		holder.rate.setText(list.get(position).get("rate"));
		holder.note01.setText(list.get(position).get("note01"));
		holder.note02.setText(list.get(position).get("note02"));
		holder.note03.setText(list.get(position).get("note03"));
		holder.note04.setText(list.get(position).get("note04"));
		
		if(list.size()>3)
		{
			if(position==2)
			{
			holder.view2.setVisibility(View.GONE);
			}else{
				holder.view2.setVisibility(View.VISIBLE);
			}
		}else if(list.size()<=3){
			
			
			if(position ==list.size()-1)
			{
			holder.view2.setVisibility(View.GONE);
			}else{
				holder.view2.setVisibility(View.VISIBLE);
			}

		
		}
		

		return convertView;
	}

	public static class BankAreaHolder {
		private TextView rate, note01, note02, note03, note04;
		private View view2;
	}

}
