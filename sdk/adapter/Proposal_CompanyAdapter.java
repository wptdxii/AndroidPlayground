package com.cloudhome.adapter;

import android.content.Context;
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

public class Proposal_CompanyAdapter extends BaseAdapter{
  
    private List<Map<String, String>> list;
 
    public static HashMap<Integer,Boolean> isSelected;
  
    private Context context;
   
    private LayoutInflater inflater = null;
    
    
    public Proposal_CompanyAdapter(List<Map<String, String>> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }


    private void initDate(){
        for(int i=0; i<list.size();i++) {
            getIsSelected().put(i,false);
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
    	Proposal_CompanyHolder holder = null;
            if (convertView == null) {
          
            holder = new Proposal_CompanyHolder();
        
            convertView = inflater.inflate(R.layout.c_gridview_item, parent,false);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            
            convertView.setTag(holder);
        } else {
         
            holder = (Proposal_CompanyHolder) convertView.getTag();
            }
    
       
        holder.name.setText(list.get(position).get("company_name"));
     
        if(getIsSelected().get(position))
        {
        holder.cb.setChecked(true);
    	holder.name.setTextColor(context.getResources().getColor(
				R.color.orange_red));

		holder.name
				.setBackgroundResource(R.drawable.company_checkbox_pressed);
        }else{
        	 holder.cb.setChecked(false);
        	holder.name.setTextColor(context.getResources().getColor(
					R.color.black));
			holder.name
					.setBackgroundResource(R.drawable.company_checkbox_normal);
        }
        return convertView;
    }
    public static class Proposal_CompanyHolder{  
    	public   CheckBox cb;  
    	public   TextView name;  
    
    }
    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        Proposal_CompanyAdapter.isSelected = isSelected;
    }

}
