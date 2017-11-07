package com.wptdxii.playground.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.playground.model.Component;
import com.wptdxii.uiframework.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wptdxii on 2017/11/6 0006 13:50
 * Email: wptdxii@gmail.com
 * Blog: https://github.com/wptdxii
 * Github: https://wptdxii.github.io
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Component> mList;

    public ListAdapter(List<Component> list) {
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_main_activity, parent, false);
        return new ViewHolder(context, itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Component component = mList.get(position);
        holder.tvComponent.setText(component.getComponent());
        holder.targetActivity = component.getTargetActivity();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_component)
        TextView tvComponent;

        Context context;
        Class<? extends BaseActivity> targetActivity;

        ViewHolder(Context context, View view) {
            super(view);
            this.context = context;
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.tv_component)
        void checkTargetActivity() {
            NavigateUtil.startActivity(context, targetActivity);
        }
    }
}
