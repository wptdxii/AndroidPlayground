package com.cloudhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * expandableListView������
 *
 */
public class C_P_I_P_Adapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> group;
	private ArrayList<ArrayList<HashMap<String, String>>> child;

	public C_P_I_P_Adapter(Context context, List<String> group,
			ArrayList<ArrayList<HashMap<String, String>>> child) {
		this.context = context;
		this.group = group;
		this.child = child;
	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return  child.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
	
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.c_p_i_p_group_item, null);
			holder = new ViewHolder();
			holder.policy_title = (TextView) convertView
					.findViewById(R.id.policy_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		       
		holder.policy_title.setText(group.get(groupPosition));
		return convertView;

	}
	
	/**
	 * ��ʾ��child
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.c_p_i_p_child_item, null);
			holder = new ViewHolder();
			holder.policy_name = (TextView) convertView
					.findViewById(R.id.policy_name);
			holder.policy_price = (TextView) convertView
					.findViewById(R.id.policy_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.policy_name.setText(child.get(groupPosition).get(childPosition).get("policy_name"));
		holder.policy_price.setText(child.get(groupPosition).get(childPosition).get("policy_price"));
		return convertView;
	}

	public class ViewHolder {
		public	TextView policy_title;
		public	TextView policy_name;
		public 	TextView policy_price;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
