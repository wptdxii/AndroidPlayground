package com.cloudhome.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag_Adapter extends BaseAdapter {

	private List<Map<String, String>> list;

	private static HashMap<Integer, Boolean> isSelected;

	private Context context;

	private LayoutInflater inflater = null;

	public Tag_Adapter(List<Map<String, String>> list, Context context) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();

		initDate();
	}

	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
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

			convertView = inflater.inflate(R.layout.gridview_item, parent, false);
			holder.name = (TextView) convertView.findViewById(R.id.item_name);

			holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		Log.d("7777", position + "");

		holder.name.setText(list.get(position).get("name"));
		if (list.get(position).get("id").equals("0")) {

			holder.name.setTextColor(context.getResources().getColor(
					R.color.textcolor_gray1));
			holder.name.setBackgroundResource(R.drawable.tag_checkbox_normal);

			getIsSelected().put(position, false);

		} else {
			holder.name.setTextColor(context.getResources().getColor(
					R.color.orange_red));
			holder.name.setBackgroundResource(R.drawable.tag_checkbox_pressed);
			getIsSelected().put(position, true);

		}

		holder.cb.setChecked(getIsSelected().get(position));

		return convertView;
	}

	public static class ViewHolder {
		public CheckBox cb;
		public TextView name;
		public TextView ID;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		Tag_Adapter.isSelected = isSelected;
	}

}
