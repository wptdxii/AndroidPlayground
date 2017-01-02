package com.cloudhome.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Finance_SectionAdapter extends BaseAdapter{
  
    private List<Map<String, String>> list;
 
    private static HashMap<Integer,Boolean> isSelected;
  
    private Context context;
   
    private LayoutInflater inflater = null;
    
    
    public Finance_SectionAdapter(List<Map<String, String>> list, Context context) {
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
        
            convertView = inflater.inflate(R.layout.finance_section_item, parent, false);
            holder.company_img = (ImageView) convertView.findViewById(R.id.company_img);
            
            holder.price_txt = (TextView) convertView.findViewById(R.id.price_txt);
            holder.price_other = (TextView) convertView.findViewById(R.id.price_other);
            holder.day_txt = (TextView) convertView.findViewById(R.id.day_txt);
            
         
            
            convertView.setTag(holder);
        } else {
         
            holder = (Proposal_CompanyHolder) convertView.getTag();
            }
    
       
            
          String img_url=IpConfig.getIp3()+list.get(position).get("logo");
	        
         Log.d("888888img_url",img_url);



        Glide.with(context)
                .load(img_url)
                .centerCrop()
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .crossFade()
                .into(holder.company_img);


	    	String pricestr = list.get(position).get("description01");
	    	
	    	int i = 0;
			String split = "元";
			StringTokenizer token = new StringTokenizer(pricestr, split);

			String[] Ins_Str = new String[token.countTokens()];

			while (token.hasMoreTokens()) {

				Ins_Str[i] = token.nextToken();
				i++;
			}
			
			if(Ins_Str.length>1)
			{
				 holder.price_txt .setText(Ins_Str[0]+"元");
				 holder.price_other.setText(Ins_Str[1]);
			}
			holder.day_txt.setText(list.get(position).get("description02"));
        return convertView;
    }
   public static class Proposal_CompanyHolder{  
        public ImageView company_img;  
        public TextView price_txt;  
        public TextView  price_other;
        public TextView  day_txt;
    
    }
    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        Finance_SectionAdapter.isSelected = isSelected;
    }

}
