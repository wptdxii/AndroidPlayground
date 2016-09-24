package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.List;
import java.util.Map;

public class ManagementXiaXiaAdapter extends BaseAdapter {

	private List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;

	public ManagementXiaXiaAdapter(Context context) {
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
		ManagementXiaXiaHolder holder;
		if (convertView == null) {

			holder = new ManagementXiaXiaHolder();

			convertView = inflater.inflate(R.layout.management_xiaixa_list_item,
					parent, false);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.num = (TextView) convertView.findViewById(R.id.num);
			
			convertView.setTag(holder);
		} else {

			holder = (ManagementXiaXiaHolder) convertView.getTag();
		}

		holder.title.setText(list.get(position).get("title"));
		holder.num.setText(list.get(position).get("num"));
		return convertView;
	}

	public static class ManagementXiaXiaHolder {

		public TextView title;
		public TextView num;
	}

}
