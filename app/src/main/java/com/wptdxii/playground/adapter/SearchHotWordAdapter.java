package com.wptdxii.playground.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.SearchTagModel;
import cn.ubaby.ubaby.ui.view.CustomTextView;

/**
 * @author fyales
 * @since 8/26/15.
 */
public class SearchHotWordAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchTagModel> mList;

    public SearchHotWordAdapter(Context context, List<SearchTagModel> list) {
        mContext = context;
        mList = list;
    }

    public void notifyDataSetChanged(List<SearchTagModel> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SearchTagModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_hot_list_item, null);
            holder = new ViewHolder();
            holder.tagBtn = (CustomTextView) convertView.findViewById(R.id.tag_btn);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        final SearchTagModel searchTagModel = getItem(position);
        holder.tagBtn.setText(searchTagModel.getName());
        return convertView;
    }

    static class ViewHolder {
        CustomTextView tagBtn;
    }
}
