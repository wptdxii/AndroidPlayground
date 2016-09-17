package com.wptdxii.uikit.widget.swiperecycler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wptdxii.uikit.R;


/**
 * Created by wptdxii on 2016/7/29 0029.
 */
public abstract class BaseSwipeRecyclerAdapter extends RecyclerView.Adapter<BaseSwipeViewHolder> {

    private static final int VIEW_TYPE_LOAD_MORE = -1;
    private boolean isLoadMoreFooterShown;
    private LoadMoreFooterViewHolder loadMoreFooterViewHolder;
    private boolean isLoadMoreEnable;

    @Override
    public BaseSwipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            return onCreateLoadMoreFooterViewHolder(parent);
        }
        return onCreateNormalViewHolder(parent, viewType);
    }

    protected abstract BaseSwipeViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType);

    private BaseSwipeViewHolder onCreateLoadMoreFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_swipe_recycler_footer, parent, false);
        loadMoreFooterViewHolder = new LoadMoreFooterViewHolder(view);
        return loadMoreFooterViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseSwipeViewHolder holder, int position) {
        if (isLoadMoreFooter(position)) {
            if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }
        }
        holder.onBindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return getDataCount() + (isLoadMoreFooterShown ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadMoreFooter(position)) {
            return VIEW_TYPE_LOAD_MORE;
        }
        return getItemType(position);
    }

    protected abstract int getItemType(int position);

    protected abstract int getDataCount();

    public void onLoadMoreStateChanged(boolean isShown) {
        this.isLoadMoreFooterShown = isShown;
        if (isShown) {
            notifyItemInserted(getItemCount());
        } else {
            notifyItemRemoved(getItemCount());
        }
    }

    public boolean isLoadMoreFooter(int position) {
        return isLoadMoreFooterShown && position == getItemCount() - 1;
    }
    
    public void onNoMoreFooter() {
        if (loadMoreFooterViewHolder != null) {

            loadMoreFooterViewHolder.onNoMoreFooter();
        }
    }

    public abstract boolean isSectionHeader(int positon);

    private class LoadMoreFooterViewHolder extends BaseSwipeViewHolder {
        private TextView swipeRecyclerFooter;
        private ProgressBar progressBar;

        public LoadMoreFooterViewHolder(View view) {
            super(view);
            swipeRecyclerFooter = (TextView) view.findViewById(R.id.swipeRecyclerFooter);
            progressBar = (ProgressBar) view.findViewById(R.id.loadMoreProgressBar);
        }

        @Override
        protected void onItemClick(View view, int position) {
        }

        @Override
        protected void onBindViewHolder(int position) {

        }

        public void onNoMoreFooter() {
            swipeRecyclerFooter.setText(R.string.no_more_footer);
            progressBar.setVisibility(View.GONE);
        }
    }
}
