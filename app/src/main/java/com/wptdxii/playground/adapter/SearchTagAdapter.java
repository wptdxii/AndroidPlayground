package com.wptdxii.playground.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.ui.view.CustomTextView;

/**
 * Created by wenlong.lu
 * on 2015/9/25.
 */
public class SearchTagAdapter extends BaseAdapter implements View.OnClickListener{

    private Context context;
    private List<String> list;
    private Callback callback;

    public SearchTagAdapter(Context context, List<String> list, Callback callback){
        this.list=list;
        this.context=context;
        this.callback = callback;
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_search_history_item, null);
            holder = new ViewHolder();
            holder.search_history_tv = (CustomTextView) view.findViewById(R.id.search_history_tv);
            holder.deleteLy = (LinearLayout) view.findViewById(R.id.deleteLy);
            holder.deleteLy.setOnClickListener(this);

            view.setTag(holder);
        }else {
            holder=(ViewHolder)view.getTag();
        }
        holder.search_history_tv.setText(list.get(position));
        holder.deleteLy.setTag(R.id.tag_position, position);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deleteLy:
                int position = (int)v.getTag(R.id.tag_position);
                callback.deleteClick(position);
                break;
        }
    }

    private class ViewHolder {
        private CustomTextView search_history_tv;
        private LinearLayout deleteLy;
    }

    public void notifyDataSetChanged(List<String> lists){
        this.list=lists;
        this.notifyDataSetChanged();
    }

    public interface Callback {
        void deleteClick(int psition);
    }
}
