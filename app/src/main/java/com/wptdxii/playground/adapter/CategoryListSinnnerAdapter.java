package com.wptdxii.playground.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.ui.view.CustomTextView;

/**
 * Created by wenlong.lu
 * on 2015/8/6.
 */
public class CategoryListSinnnerAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private String item;

    public CategoryListSinnnerAdapter(Context context, List<String> list, String item) {
        this.context = context;
        this.list = list;
        this.item = item;
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.popupwindow_spinner_item, null);
            holder.spinnerItemTv = (CustomTextView) view.findViewById(R.id.spinnerItemTv);
            holder.lineView = view.findViewById(R.id.lineView);
            view.setTag(holder);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewListener.categoryListId(position);
                }
            });
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (position == list.size() - 1) {
            holder.lineView.setVisibility(View.GONE);
        } else {
            holder.lineView.setVisibility(View.VISIBLE);
        }

        holder.spinnerItemTv.setText(list.get(position));

        if (item.equals(list.get(position))) {
            holder.spinnerItemTv.setTextColor(context.getResources().getColor(R.color.color_c25741));
        } else {
            holder.spinnerItemTv.setTextColor(Color.parseColor("#df9f07"));
        }
        return view;
    }

    class ViewHolder {
        CustomTextView spinnerItemTv;
        View lineView;
    }

    private viewListener onViewListener;

    public void setViewListener(viewListener onViewListener) {
        this.onViewListener = onViewListener;
    }

    public interface viewListener {
        void categoryListId(int id);
    }

}
