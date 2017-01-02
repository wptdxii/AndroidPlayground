package com.cloudhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.bean.IncomeExpendBean;

import java.util.ArrayList;

public class IncomeExpendAdapter extends BaseAdapter {
	private ArrayList<IncomeExpendBean> dataMap;
	private static final int TYPE_CATEGORY= 0;  
    private static final int TYPE_ITEM = 1;  
    private LayoutInflater mInflater; 
    private Context context;
	

	public IncomeExpendAdapter(Context context,ArrayList<IncomeExpendBean> dataMap) {
		super();
		this.dataMap = dataMap;
		mInflater = LayoutInflater.from(context);
		this.context=context;
	}

	@Override
	public int getCount() {
		 int count = 0;  
	        if (null != dataMap) {  
	            for (IncomeExpendBean bean : dataMap) {  
	                count += bean.getGroupItemCount();  
	            }  
	        }  
	        return count;  
	}

	@Override
	public Object getItem(int position) {
		if (null == dataMap || position <  0|| position > getCount()) {

            return null;   
        }

		// 同一分类内，第一个元素的索引值  
        int categroyFirstIndex = 0;

        for (IncomeExpendBean bean : dataMap) {

            int size = bean.getGroupItemCount();

            // 在当前分类中的索引值  
            int categoryIndex = position - categroyFirstIndex;

            // item在当前分类内  
            if (categoryIndex < size) {

                return  bean.getItem( categoryIndex );  
            }  
              
            // 索引移动到当前分类结尾，即下一个分类第一个元素索引  
            categroyFirstIndex += size;  
        }  
          
        return null;  
	}

	
	@Override
	public int getItemViewType(int position) {
		 // 异常情况处理  
        if (null == dataMap || position <  0|| position > getCount()) {

            return TYPE_ITEM;   
        }

        int categroyFirstIndex = 0;  
          
        for (IncomeExpendBean category : dataMap) {

            int size = category.getGroupItemCount();

            // 在当前分类中的索引值  
            int categoryIndex = position - categroyFirstIndex;

            if (categoryIndex == 0) {

                return TYPE_CATEGORY;

            }

            categroyFirstIndex += size;

        }  
        return TYPE_ITEM;  
	}
	
	 @Override  
	    public int getViewTypeCount() {  
	        return 2;  
	    }  

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 int itemViewType = getItemViewType(position);  
	        switch (itemViewType) {  
	        case TYPE_CATEGORY:  
	            if (null == convertView) {  
	                convertView = mInflater.inflate(R.layout.item_category_income_expend, parent, false);  
	            }  
	              
	            TextView tv_month_category = (TextView) convertView.findViewById(R.id.tv_month_category);  
	            IncomeExpendBean bean = (IncomeExpendBean)getItem(position);  
	            tv_month_category.setText(bean.getMonthDivider());  
	            break;  
	  
	        case TYPE_ITEM:  
	            ViewHolder viewHolder = null;  
	            if (null == convertView) {
					convertView = mInflater.inflate(R.layout.item_data_income_expend, parent, false);
	                viewHolder = new ViewHolder();  
	                viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);  
	                viewHolder.tv_income_expend_type =(TextView) convertView.findViewById(R.id.tv_income_expend_type); 
	                viewHolder.tv_money =(TextView) convertView.findViewById(R.id.tv_money);
	                convertView.setTag(viewHolder);  
	            } else {  
	                viewHolder = (ViewHolder) convertView.getTag();  
	            }  


	             // 绑定数据  
	            IncomeExpendBean beanData = (IncomeExpendBean)getItem(position);
	            viewHolder.tv_date.setText(beanData.getAddTime());
	            viewHolder.tv_income_expend_type.setText(beanData.getTitle());  
	            
	            if("收入".equals(beanData.getCategory())){

	            	viewHolder.tv_money.setText("+"+beanData.getMoney());
	            	viewHolder.tv_money.setTextColor(context.getResources().getColor(R.color.orange2));

	            }else if("支出".equals(beanData.getCategory())){

	            	viewHolder.tv_money.setText("-"+beanData.getMoney());
	            	viewHolder.tv_money.setTextColor(context.getResources().getColor(R.color.cost));
				}

	            break;  
	        }  
	  
	        return convertView;  
	}
	
	 @Override  
	    public boolean areAllItemsEnabled() {  
	        return false;  
	    }  
	      
	    @Override  
	    public boolean isEnabled(int position) {  
	        return getItemViewType(position) != TYPE_CATEGORY;  
	    }  
	
	 private class ViewHolder {  
	        TextView tv_date;  
	        TextView tv_income_expend_type;   
	        TextView tv_money;   
	    }  

}
