package com.wptdxii.uikit.widget.swiperecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wptdxii on 2016/7/29 0029.
 */
public abstract class BaseSwipeViewHolder extends RecyclerView.ViewHolder {

    public BaseSwipeViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(view, getAdapterPosition());
            }
        });
    }

    protected abstract void onItemClick(View view, int position);

    protected abstract void onBindViewHolder(int position);
}
