package com.wptdxii.uikit.widget.recyclerview;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.wptdxii.uikit.R;
import com.wptdxii.uikit.widget.recyclerview.animation.AlphaInAnimation;
import com.wptdxii.uikit.widget.recyclerview.animation.AnimationType;
import com.wptdxii.uikit.widget.recyclerview.animation.BaseAnimation;
import com.wptdxii.uikit.widget.recyclerview.animation.ScaleInAnimation;
import com.wptdxii.uikit.widget.recyclerview.animation.SlideInBottomAnimation;
import com.wptdxii.uikit.widget.recyclerview.animation.SlideInLeftAnimation;
import com.wptdxii.uikit.widget.recyclerview.animation.SlideInRightAnimation;
import com.wptdxii.uikit.widget.recyclerview.expandable.IExpandable;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.ALPHAIN;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SCALEIN;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SLIDEIN_BOTTOM;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SLIDEIN_LEFT;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SLIDEIN_RIGHT;

/**
 * Created by wptdxii on 2016/10/11 0011.
 */

public abstract class BaseViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    public static final int HEADER_VIEW = 0x00000111;
    public static final int EMPTY_VIEW = 0x00000222;
    public static final int FOOTER_VIEW = 0x00000333;
    public static final int LOADINGMORE_VIEW = 0x00000444;

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mDataList;
    protected int mLayoutResId;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private LinearLayout mTempLayout;
    private View mItemView;
    private View mEmptyView;
    private View mLoadMoreFailed;
    private View mLoadingMoreView;

    private boolean mLoadMoreEnable = false;
    private boolean mIsLoadingMore = false;
    private boolean mEmptyHeaderEnable = false;
    private boolean mEmptyFooterEnable = false;
    private boolean mEmptyViewEnable = false;
    private int pageSize = -1;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public void setmOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.mOnLoadMoreListener = loadMoreListener;
    }


    public interface SpanSizeLookup {
        int getSpanSize(int position);
    }

    private SpanSizeLookup mSpanSizeLookup;

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }


    private int mDuration = 300;
    private boolean mAnimationEnable;
    private int mLastPosition = -1;
    private BaseAnimation mCusAnimation;
    private BaseAnimation mAnimation;
    private boolean mIsFirstOnly;
    private Interpolator mInterpolator;

    public BaseViewAdapter(Context context, View itemView, @Nullable List<T> dataList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mItemView = itemView;
        this.mDataList = (dataList == null ? new ArrayList<T>() : dataList);
        this.mAnimation = new AlphaInAnimation();
        this.mInterpolator = new LinearInterpolator();
    }

    public BaseViewAdapter(Context context, @LayoutRes int layoutResId, @Nullable List<T> dataList) {
        this(context, null, dataList);
        this.mLayoutResId = layoutResId;
    }


    public BaseViewAdapter(Context context, @Nullable List<T> dataList) {

        this(context, null, dataList);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderLayout != null && position == 0) {
            return HEADER_VIEW;
        }

        if (mDataList.size() == 0 && mEmptyViewEnable && mEmptyView != null && position <= 2) {

            if (mEmptyHeaderEnable || mEmptyFooterEnable) {
                if (position == 0) {
                    if (mHeaderLayout != null) {
                        return HEADER_VIEW;
                    } else {
                        return EMPTY_VIEW;
                    }
                } else if (position == 1) {
                    if (mHeaderLayout != null) {
                        return EMPTY_VIEW;
                    } else if (mFooterLayout != null) {
                        return FOOTER_VIEW;
                    }
                } else if (position == 2) {
                    if (mHeaderLayout != null && mFooterLayout != null) {
                        return FOOTER_VIEW;
                    }
                }
            } else {
                //                if (position == 0) {
                //                    return EMPTY_VIEW;
                //                }
                return EMPTY_VIEW;
            }
        } else if (position == (mDataList.size() + getHeaderCount())) {
            if (mLoadMoreEnable) {
                return LOADINGMORE_VIEW;
            } else {
                return FOOTER_VIEW;
            }
        } else if (position > (mDataList.size() + getHeaderCount())) {
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position - getHeaderCount());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;

        switch (viewType) {
            case HEADER_VIEW:
                viewHolder = new BaseViewHolder(mHeaderLayout);
                break;
            case EMPTY_VIEW:
                viewHolder = new BaseViewHolder(mEmptyView);
                break;
            case FOOTER_VIEW:
                viewHolder = new BaseViewHolder(mFooterLayout);
                break;
            case LOADINGMORE_VIEW:
                viewHolder = createLoadingMoreView(parent);
                break;
            default:
                viewHolder = createDefViewHolder(parent);
                break;
        }
        return viewHolder;
    }

    private BaseViewHolder createDefViewHolder(ViewGroup parent) {
        if (mItemView == null) {
            mItemView = mLayoutInflater.inflate(mLayoutResId, parent, false);
        }

        return new BaseViewHolder(mItemView);
    }

    private BaseViewHolder createLoadingMoreView(ViewGroup parent) {
        if (mLoadingMoreView == null) {
            mLoadingMoreView = mLayoutInflater.inflate(R.layout.def_loading_more, parent, false);
        }

        return new BaseViewHolder(mLoadingMoreView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        switch (viewType) {
            case HEADER_VIEW:
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                break;
            case LOADINGMORE_VIEW:
                bindLoadingMoreView();
                break;
            default:
                onBind(holder, mDataList.get(holder.getLayoutPosition() - getHeaderCount()));
                break;
        }
    }

    /**
     * subclass need to override the method to bind data to holder
     *
     * @param holder
     * @param itemData
     */
    protected abstract void onBind(BaseViewHolder holder, T itemData);

    private void bindLoadingMoreView() {
        if (isLoadingMore() && !mIsLoadingMore) {
            mIsLoadingMore = true;
            mOnLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        int count = mDataList.size() + getHeaderCount() + getFooterCount() + getLoadingCount();
        if (mDataList.size() == 0 && mEmptyViewEnable && mEmptyView != null) {
            if (mEmptyHeaderEnable && mEmptyFooterEnable) {
                count = getHeaderCount() + getFooterCount() + getEmptyViewCount();
            } else if (!mEmptyHeaderEnable && !mEmptyFooterEnable) {
                count = getEmptyViewCount();
            } else if (mEmptyHeaderEnable && !mEmptyFooterEnable) {
                count = getHeaderCount() + getEmptyViewCount();
            } else if (!mEmptyHeaderEnable && mEmptyFooterEnable) {
                count = getEmptyViewCount() + getFooterCount();
            }
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int viewType = holder.getItemViewType();
        if (viewType == HEADER_VIEW || viewType == EMPTY_VIEW || viewType == FOOTER_VIEW ||
                viewType == LOADINGMORE_VIEW) {
            setFullSpan(holder);
        } else {
            setAnimation(holder);
        }
    }

    private void setAnimation(BaseViewHolder holder) {
        if (mAnimationEnable) {
            if (!mIsFirstOnly || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation = null;
                if (mCusAnimation != null) {
                    animation = mCusAnimation;
                } else {
                    animation = mAnimation;
                }

                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    anim.setDuration(mDuration);
                    anim.setInterpolator(mInterpolator);
                    anim.start();
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    /**
     * if orientation is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height
     * <p>
     * if the holder use StaggeredGridLayoutManager it should using all span area
     *
     * @param holder
     */
    protected void setFullSpan(BaseViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridManager = (GridLayoutManager) layoutManager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    int spanCount;
                    if (mSpanSizeLookup == null) {
                        spanCount = (viewType == HEADER_VIEW || viewType == EMPTY_VIEW ||
                                viewType == FOOTER_VIEW || viewType == LOADINGMORE_VIEW) ?
                                gridManager.getSpanCount() : 1;
                    } else {
                        spanCount = (viewType == HEADER_VIEW || viewType == EMPTY_VIEW ||
                                viewType == FOOTER_VIEW || viewType == LOADINGMORE_VIEW) ?
                                gridManager.getSpanCount() :
                                mSpanSizeLookup.getSpanSize(position - getHeaderCount());
                    }

                    return spanCount;
                }
            });
        }

        if (mOnLoadMoreListener != null && pageSize == -1) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            int visibleItemCount = manager.getChildCount();
            setPageSize(visibleItemCount);
        }
    }

    /**
     * set  pagesize when need to load more
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {

        this.mLoadMoreEnable = true;
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setData(List<T> dataList) {

        this.mDataList = (dataList == null) ? new ArrayList<T>() : dataList;
        if (mOnLoadMoreListener != null) {
            mLoadMoreEnable = true;
        }

        if (mLoadMoreFailed != null) {
            removeFooterView(mLoadMoreFailed);
        }
        this.mLastPosition = -1;
        this.notifyDataSetChanged();
    }

    public List<T> getData() {
        return this.mDataList;
    }

    public void addData(T itemData) {

        mDataList.add(itemData);
        notifyItemInserted(mDataList.size());
    }

    public void addData(int position, T itemData) {

        if (position >= 0 && position < mDataList.size()) {
            mDataList.add(position, itemData);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, mDataList.size() - position);

        } else {

            throw new ArrayIndexOutOfBoundsException();

        }
    }

    public boolean removeData(int position) {
        boolean isRemoved = false;
        if (mDataList != null) {
            mDataList.remove(position);
            isRemoved = true;
            notifyItemRemoved(position + getHeaderCount());
        }
        return isRemoved;
    }

    public boolean removeData(T itemData) {
        boolean isRemoved = false;
        if (mDataList != null) {
            mDataList.remove(itemData);
        }
        return false;
    }

    public void setItemData(int position, T itemData) {

        if (position >= 0 && position < mDataList.size()) {

            this.mDataList.set(position, itemData);
            this.notifyItemChanged(position);

        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public T getItemData(int position) {
        return this.mDataList.get(position);
    }

    public void addData(List<T> dataList) {

        this.mDataList.addAll(dataList);

        if (this.mLoadMoreEnable) {
            this.mIsLoadingMore = false;
        }

        this.notifyItemRangeChanged(mDataList.size() - dataList.size() + getHeaderCount(), dataList.size());
    }

    public void addData(int position, List<T> dataList) {
        if (position >= 0 && position < mDataList.size()) {
            this.mDataList.addAll(position, dataList);
            this.notifyItemInserted(position);
            this.notifyItemRangeChanged(position, mDataList.size() - position - dataList.size());
        } else {

            throw new ArrayIndexOutOfBoundsException();
        }
    }


    public void addHeaderView(View headerView, int index) {
        if (mHeaderLayout == null) {
            if (mTempLayout == null) {
                initLinearLayout(mHeaderLayout, headerView);
            } else {
                mHeaderLayout = mTempLayout;
            }
        }

        index = (index >= mHeaderLayout.getChildCount() ? -1 : index);
        mHeaderLayout.addView(headerView, index);
        this.notifyDataSetChanged();
    }

    public void addHeaderView(View headerView) {
        addHeaderView(headerView, -1);
    }


    public void addFooterView(View footerView, int index) {
        mLoadMoreEnable = false;
        if (mFooterLayout == null) {
            if (mTempLayout == null) {
                initLinearLayout(mFooterLayout, footerView);
            } else {
                mFooterLayout = mTempLayout;
            }
        }

        index = (index >= mFooterLayout.getChildCount() ? -1 : index);
        mFooterLayout.addView(footerView, index);
        this.notifyItemChanged(getItemCount());
    }

    public void addFooterView(View footerView) {
        addFooterView(footerView, -1);
    }

    /**
     * init header layout and footer layout, and cache it
     *
     * @param layout
     * @param view
     */
    private void initLinearLayout(LinearLayout layout, View view) {
        layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mTempLayout = layout;
    }

    public void removeHeaderView(View headerView) {

        if (mHeaderLayout == null) {
            return;
        }

        mHeaderLayout.removeView(headerView);
        if (mHeaderLayout.getChildCount() == 0) {
            mHeaderLayout = null;
        }

        this.notifyDataSetChanged();
    }

    public void removeAllHeaderViews() {

        if (mHeaderLayout == null) {
            return;
        }

        mHeaderLayout.removeAllViews();
        mHeaderLayout = null;
    }

    public void removeFooterView(View footer) {

        if (mFooterLayout == null) {
            return;
        }

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            mFooterLayout = null;
        }
        this.notifyDataSetChanged();
    }

    public void removeAllFooterViews() {

        if (mFooterLayout == null) {
            return;
        }

        mFooterLayout.removeAllViews();
        mFooterLayout = null;
    }

    /**
     * custom the loadmore failed view
     *
     * @param failedView
     */
    public void setLoadMoreFailedView(View failedView) {
        this.mLoadMoreFailed = failedView;
        failedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFooterView(mLoadMoreFailed);
                setPageSize(pageSize);
            }
        });
    }

    /**
     * show the default loadmore failed view
     */
    private void showLoadMoreFailedView() {
        loadMoreCompleted();
        if (mLoadMoreFailed == null) {
            mLoadMoreFailed = mLayoutInflater.inflate(R.layout.def_load_more_failed, null);
            mLoadMoreFailed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFooterView(mLoadMoreFailed);
                    setPageSize(pageSize);
                }
            });
        }
        addFooterView(mLoadMoreFailed);
    }

    public void loadMoreCompleted() {
        this.mLoadMoreEnable = false;
        this.mIsLoadingMore = false;
        this.notifyItemChanged(getItemCount());
    }


    public void setEmptyView(boolean showEmptyHeader, boolean showEmptyFooter, View emptyView) {
        this.mEmptyHeaderEnable = showEmptyHeader;
        this.mEmptyFooterEnable = showEmptyFooter;
        this.mEmptyView = emptyView;

        //        if (mTempEmptyView == null) {
        //            this.mTempEmptyView = emptyView;
        //        }

        this.mEmptyViewEnable = true;
    }

    public void setEmptyView(View emptyView) {
        setEmptyView(false, false, emptyView);
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    public void setEmptyViewEnable(boolean emptyViewEnable) {
        this.mEmptyViewEnable = emptyViewEnable;
    }

    public void setLoadingMoreView(View loadingMoreView) {
        this.mLoadingMoreView = loadingMoreView;
    }

    public View getLoadingMoreView() {
        return this.mLoadingMoreView;
    }

    public int getHeaderCount() {
        return mHeaderLayout == null ? 0 : 1;
    }

    public int getFooterCount() {
        return mFooterLayout == null ? 0 : 1;
    }

    public int getLoadingCount() {
        return isLoadingMore() ? 1 : 0;
    }

    public int getEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }

    private boolean isLoadMoreEnable() {
        return mLoadMoreEnable && pageSize != -1 && mOnLoadMoreListener != null && mDataList.size() >= pageSize;
    }

    public boolean isLoadingMore() {
        return this.mIsLoadingMore;
    }

    /**
     * set Animation Duration
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    /**
     * choose one of default animation
     *
     * @param animationType
     */
    public void setDefAnimation(@AnimationType int animationType) {
        this.mAnimationEnable = true;
        this.mCusAnimation = null;
        switch (animationType) {
            case ALPHAIN:
                mAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mAnimation = new SlideInRightAnimation();
                break;
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    /**
     * set custom animation
     *
     * @param baseAnimation
     */
    public void setCusAnimation(BaseAnimation baseAnimation) {
        this.mAnimationEnable = true;
        this.mCusAnimation = baseAnimation;
    }

    public void setAnimationEnable(boolean animationEnable) {
        this.mAnimationEnable = animationEnable;
    }

    public void isFirstOnly(boolean isFirstOnly) {
        this.mIsFirstOnly = isFirstOnly;
    }

    public int expand(@IntRange(from = 0) int position, boolean animEnable, boolean notify) {
        int subItemCount;

        position -= getHeaderCount();
        IExpandable expandableItem = getExpandableItem(position);
        if (expandableItem == null) {
            subItemCount = 0;
        }

        if (!hasSubItems(expandableItem)) {
            expandableItem.setExpandable(false);
            subItemCount = 0;
        }

        if (!expandableItem.isExpandable()) {

        }

        return 0;
    }

    private boolean hasSubItems(IExpandable expandableItem) {
        List<T> itemDatas = expandableItem.getData();
        return itemDatas != null && itemDatas.size() > 0;
    }

    private IExpandable getExpandableItem(int position) {
        T item = getItemData(position);
        if (isExpandable(item)) {
            return (IExpandable) item;
        }
        return null;
    }

    private boolean isExpandable(T item) {
        return item != null && item instanceof IExpandable;
    }

}
