package com.cloudhome.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.activity.MainSearchActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class CategorylistAdapter extends BaseAdapter {

	public ArrayList<HashMap<String, String>> list;
	private LayoutInflater inflater = null;
	private MainSearchActivity context;

	public CategorylistAdapter(MainSearchActivity context) {
		this.context = context;

		inflater = LayoutInflater.from(context);
	}

	public void setData(ArrayList<HashMap<String, String>> list) {
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
		ViewHolder holder ;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.main_search_tab_list_item,
					parent, false);
			holder = new ViewHolder();

			holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
			holder.iv_gouxuan= (ImageView) convertView.findViewById(R.id.iv_gouxuan);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}



		holder.tv_name.setText(list.get(position).get("name"));

if(context.categorypos==position)
{
	holder.iv_gouxuan.setBackgroundResource(R.drawable.icon_gouxuan);
	holder.tv_name.setTextColor(context.getResources().getColor(R.color.title_blue));
}else{

	holder.iv_gouxuan.setBackgroundColor(Color.parseColor("#ffffff"));
	holder.tv_name.setTextColor(context.getResources().getColor(R.color.color6));
}

		return convertView;

	}



	public static class ViewHolder {

		 TextView tv_name;
		 ImageView iv_gouxuan;

	}

}
