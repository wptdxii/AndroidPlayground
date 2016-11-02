package com.wptdxii.playground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.ui.view.ViewExpandAnimation;

/**
 * Created by Administrator on 2015/12/25.
 */
public class DemoAdapter extends BaseAdapter implements View.OnClickListener {

    private List<String> strings;

    private View mLastView;
    private int expandPosition =-1;
    private int mLastVisibility;

    public DemoAdapter(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_demo_group, null);
            holder.itemTv = (TextView) view.findViewById(R.id.itemTv);
            holder.itemBtn = (Button) view.findViewById(R.id.itemBtn);
            holder.childLayout = (LinearLayout) view.findViewById(R.id.childLayout);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.itemTv.setText(strings.get(position));

        if(expandPosition == position){
            holder.childLayout.setVisibility(mLastVisibility);
        }else{
            holder.childLayout.setVisibility(View.GONE);
        }

        holder.childLayout.measure(0, 0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.childLayout.getLayoutParams();
        params.bottomMargin = -holder.childLayout.getMeasuredHeight();
        holder.childLayout.setVisibility(View.GONE);

        holder.itemBtn.setOnClickListener(this);
        holder.itemBtn.setTag(R.id.tag_holder, holder);
        holder.itemBtn.setTag(R.id.tag_position, position);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemBtn:
                ViewHolder holder=(ViewHolder)v.getTag(R.id.tag_holder);
                holder.childLayout.startAnimation(new ViewExpandAnimation(holder.childLayout));
                break;
        }
    }

    private class ViewHolder {
        private TextView itemTv;
        private Button itemBtn;
        private LinearLayout childLayout;
    }
}
