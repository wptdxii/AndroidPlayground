package com.wptdxii.uikit.widget.swiperecycler;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wptdxii.uikit.R;
import com.wptdxii.uikit.widget.swiperecycler.layoutmanager.ILayoutManager;


/**
 * Created by wptdxii on 2016/7/29 0029.
 */
public class SwipeRecycler extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    public static final int ACTION_IDLE = 0;
    public static final int ACTION_PULL_TO_REFRESH = 1;
    public static final int ACTION_LOAD_MORE_REFRESH = 2;
    private int mCurrentState = ACTION_IDLE;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private OnSwipeRefreshListener listener;
    private boolean isLoadMoreEnable = false;
    private boolean isPullToRefreshEnable = true;
    private boolean isRefreshBarShown = true;
    private BaseSwipeRecyclerAdapter mAdapter;
    private ILayoutManager mLayoutManager;

    public SwipeRecycler(Context context) {
        super(context);
        initView();
    }


    public SwipeRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SwipeRecycler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_swipe_recycler, this, true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.swipeRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mCurrentState == ACTION_IDLE && isLoadMoreEnable && checkIfNeedLoadMore()) {
                    mCurrentState = ACTION_LOAD_MORE_REFRESH;
                    mAdapter.onLoadMoreStateChanged(true);
                    mSwipeRefreshLayout.setEnabled(false);
                    listener.onRefresh(ACTION_LOAD_MORE_REFRESH);
                }
            }
        });


    }

    public void setLayoutManager(ILayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        mRecyclerView.setLayoutManager(layoutManager.getLayoutManager());
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        if (decor != null) {
            mRecyclerView.addItemDecoration(decor);
        }
    }

    public void setAdapter(BaseSwipeRecyclerAdapter adapter) {
        mAdapter = adapter;
        mLayoutManager.setAdapter(adapter);
        mRecyclerView.setAdapter(adapter);
    }

    private boolean checkIfNeedLoadMore() {
        int lastVisibleItemPosition = mLayoutManager.findLastVisiblePosition();
        int totalCount = mLayoutManager.getLayoutManager().getItemCount();
        return totalCount - lastVisibleItemPosition < 5;
    }

    public void enableLaodMore(boolean enable) {
        this.isLoadMoreEnable = enable;

    }

    public void enablePullToRefresh(boolean enable) {
        isPullToRefreshEnable = enable;
        mSwipeRefreshLayout.setEnabled(enable);
    }
    public void isInitWithRefreshBar(boolean isRefreshBarShown) {
        this.isRefreshBarShown = isRefreshBarShown;
        
    }
    @Override
    public void onRefresh() {
        mCurrentState = ACTION_PULL_TO_REFRESH;
        listener.onRefresh(ACTION_PULL_TO_REFRESH);
    }
    
    public void onLoadMore() {
        mCurrentState = ACTION_LOAD_MORE_REFRESH;
        listener.onRefresh(ACTION_LOAD_MORE_REFRESH);
    }

    public void onRefreshCompleted() {
        switch (mCurrentState) {
            case ACTION_PULL_TO_REFRESH:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case ACTION_LOAD_MORE_REFRESH:
                if (isLoadMoreEnable) {
                    mAdapter.onLoadMoreStateChanged(false);
                } else {
                    mAdapter.onNoMoreFooter();
                }
                
                if (isPullToRefreshEnable) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                break;
        }
        mCurrentState = ACTION_IDLE;
    }

    public void setOnSwipeRefreshListener(OnSwipeRefreshListener listener) {
        this.listener = listener;
    }

    public void setSelection(int position) {
        mRecyclerView.scrollToPosition(position);
    }
    public void setRefreshing() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(isRefreshBarShown);
                onRefresh();
            }
        });
    }
    
    public interface OnSwipeRefreshListener {
        void onRefresh(int action);
    }
}
