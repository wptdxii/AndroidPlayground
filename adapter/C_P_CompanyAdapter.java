package com.cloudhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.bean.C_P_C_Data;

import java.util.ArrayList;
import java.util.HashMap;

public class C_P_CompanyAdapter extends BaseAdapter{
  
    private ArrayList<C_P_C_Data> list;
 
    private static HashMap<Integer,Boolean> isSelected;
  
    private Context context;
   
    private LayoutInflater inflater = null;
    
    
    public C_P_CompanyAdapter(ArrayList<C_P_C_Data> list, Context context) {
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
        ViewHolder holder = null;
            if (convertView == null) {
          
            holder = new ViewHolder();
        
            convertView = inflater.inflate(R.layout.c_gridview_item, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.ID = (TextView) convertView.findViewById(R.id.item_ID);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            
            convertView.setTag(holder);
        } else {
         
            holder = (ViewHolder) convertView.getTag();
            }
    
        holder.ID.setText(list.get(position).getId());
        holder.name.setText(list.get(position).getName());
     
        holder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }
    public static class ViewHolder{  
        public CheckBox cb;  
        public TextView name;  
        public TextView ID;
    }
    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        C_P_CompanyAdapter.isSelected = isSelected;
    }

}
