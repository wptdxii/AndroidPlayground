package com.wptdxii.uikit.widget.recyclerview;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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
   protected static final int HEADER_VIEW = 0x00000111;
   protected static final int EMPTY_VIEW = 0x00000222;
   protected static final int FOOTER_VIEW = 0x00000333;
   protected static final int LOADINGMORE_VIEW = 0x00000444;

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mData;
    protected int mLayoutResId;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private LinearLayout mTempLayout;
    private View mItemView;
    private View mEmptyView;
    private View mLoadMoreFailedView;
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

    public BaseViewAdapter(Context context, View itemView, @Nullable List<T> data) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mItemView = itemView;
        this.mData = (data == null ? new ArrayList<T>() : data);
        this.mAnimation = new AlphaInAnimation();
        this.mInterpolator = new LinearInterpolator();
    }

    public BaseViewAdapter(Context context, @LayoutRes int layoutResId, @Nullable List<T> data) {
        this(context, null, data);
        this.mLayoutResId = layoutResId;
    }


    public BaseViewAdapter(Context context, @Nullable List<T> data) {

        this(context, null, data);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderLayout != null && position == 0) {
            return HEADER_VIEW;
        }

        if (mData.size() == 0 && mEmptyViewEnable && mEmptyView != null && position <= 2) {

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
        } else if (position == (mData.size() + getHeaderCount())) {
            if (mLoadMoreEnable) {
                return LOADINGMORE_VIEW;
            } else {
                return FOOTER_VIEW;
            }
        } else if (position > (mData.size() + getHeaderCount())) {
            return FOOTER_VIEW;
        }

//        return super.getItemViewType(position - getHeaderCount());

        return getItemType(position - getHeaderCount());


    }

    /**
     * override this method when need
     * @param position
     * @return
     */
    protected int getItemType(int position) {
        return super.getItemViewType(position);
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
                viewHolder = createViewHolder(parent);
                break;
        }
        return viewHolder;
    }

    private BaseViewHolder createViewHolder(ViewGroup parent) {
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
                onBind(holder, mData.get(holder.getLayoutPosition() - getHeaderCount()));
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
        int count = mData.size() + getHeaderCount() + getFooterCount() + getLoadingCount();
        if (mData.size() == 0 && mEmptyViewEnable && mEmptyView != null) {
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

    public void setData(List<T> data) {

        this.mData = (data == null) ? new ArrayList<T>() : data;
        if (mOnLoadMoreListener != null) {
            mLoadMoreEnable = true;
        }

        if (mLoadMoreFailedView != null) {
            removeFooterView(mLoadMoreFailedView);
        }
        this.mLastPosition = -1;
        this.notifyDataSetChanged();
    }

    public List<T> getData() {
        return this.mData;
    }

    public void addData(T itemData) {

        mData.add(itemData);
        notifyItemInserted(mData.size());
    }

    public void addData(int position, T itemData) {

        if (position >= 0 && position < mData.size()) {
            mData.add(position, itemData);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, mData.size() - position);

        } else {

            throw new ArrayIndexOutOfBoundsException();

        }
    }

    public boolean removeData(int position) {
        boolean isRemoved = false;
        if (mData != null) {
            mData.remove(position);
            isRemoved = true;
            notifyItemRemoved(position + getHeaderCount());
        }
        return isRemoved;
    }

    public boolean removeData(T itemData) {
        boolean isRemoved = false;
        if (mData != null) {
            mData.remove(itemData);
        }
        return false;
    }

    public void setData(int position, T itemData) {

        if (position >= 0 && position < mData.size()) {

            this.mData.set(position, itemData);
            this.notifyItemChanged(position);

        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public T getData(int position) {
        return this.mData.get(position);
    }

    public void addData(List<T> data) {

        this.mData.addAll(data);

        if (this.mLoadMoreEnable) {
            this.mIsLoadingMore = false;
        }

        this.notifyItemRangeChanged(mData.size() - data.size() + getHeaderCount(), data.size());
    }

    public void addData(int position, List<T> data) {
        if (position >= 0 && position < mData.size()) {
            this.mData.addAll(position, data);
            this.notifyItemInserted(position);
            this.notifyItemRangeChanged(position, mData.size() - position - data.size());
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
        this.mLoadMoreFailedView = failedView;
        failedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFooterView(mLoadMoreFailedView);
                setPageSize(pageSize);
            }
        });
    }

    /**
     * show the default loadmore failed view
     */
    private void showLoadMoreFailedView() {
        loadMoreCompleted();
        if (mLoadMoreFailedView == null) {
            mLoadMoreFailedView = mLayoutInflater.inflate(R.layout.def_load_more_failed, null);
            mLoadMoreFailedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFooterView(mLoadMoreFailedView);
                    setPageSize(pageSize);
                }
            });
        }
        addFooterView(mLoadMoreFailedView);
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
        return mLoadMoreEnable && pageSize != -1 && mOnLoadMoreListener != null && mData.size() >= pageSize;
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
    public void setAnimation(@AnimationType int animationType) {
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
    public void setAnimation(BaseAnimation baseAnimation) {
        this.mAnimationEnable = true;
        this.mCusAnimation = baseAnimation;
    }

    public void setAnimationEnable(boolean animationEnable) {
        this.mAnimationEnable = animationEnable;
    }

    public void isFirstOnly(boolean isFirstOnly) {
        this.mIsFirstOnly = isFirstOnly;
    }

    /**
     * Expand an expandable item
     *
     * @param position
     * @param animEnable
     * @param notify
     * @return
     */
    public int expand(@IntRange(from = 0) int position, boolean animEnable, boolean notify) {
        int subItemCount = 0;
        position -= getHeaderCount();

        IExpandable expandableItem = getExpandableItem(position);
        if (expandableItem == null) {
            return subItemCount;
        }

        if (!hasSubData(expandableItem)) {
            expandableItem.setExpanded(false);
            return subItemCount;
        }

        if (!expandableItem.isExpanded()) {
            List subData = expandableItem.getSubData();
            mData.addAll(position + 1, subData);

            subItemCount += subData.size();
            subItemCount += recursiveExpand(position + 1, subData);
            expandableItem.setExpanded(true);
        }

        position += getHeaderCount();
        if (notify) {
            if (animEnable) {
                notifyItemChanged(position);
                notifyItemRangeInserted(position + 1, subItemCount);
            } else {
                notifyDataSetChanged();
            }
        }

        return subItemCount;
    }

    public int expandAll(@IntRange(from = 0) int position, boolean animEnable, boolean notify) {
        int totalCount = 0;
        position -= getHeaderCount();

        IExpandable expandableItem = getExpandableItem(position);
        if (expandableItem == null) {
            return totalCount;
        }
        if (!hasSubData(expandableItem)) {
            return totalCount;
        }

        for (int i = position; i < mData.size(); i++) {
            T item = mData.get(i);
            if (isExpandable(item)) {
                totalCount += expand(position + getHeaderCount(), animEnable, notify);
            }
        }

        position += getHeaderCount();
        if (notify) {
            if (animEnable) {
                notifyItemRangeInserted(position + 1, totalCount);
            } else {
                notifyDataSetChanged();
            }
        }
        return totalCount;
    }


    private int recursiveExpand(int position, @NonNull List data) {
        int subTotalCount = 0;
        int pos = position + data.size() - 1;
        for (int i = 0; i <= data.size() - 1; i++) {
            if (data.get(i) instanceof IExpandable) {
                IExpandable expandableItem = (IExpandable) data.get(i);
                List subData = expandableItem.getSubData();
                mData.addAll(pos + 1, subData);
                int subItemCount = recursiveExpand(pos + 1, subData);
                pos += subItemCount;
                subTotalCount += subItemCount;
            }
        }
        return subTotalCount;
    }

    /**
     * Collapse an expandable item
     *
     * @param position
     * @param animEnable
     * @param notify
     * @return
     */
    public int collapse(@IntRange(from = 0) int position, boolean animEnable, boolean notify) {
        int subItemCount = 0;
        position -= getHeaderCount();

        IExpandable expandableItem = getExpandableItem(position);
        if (expandableItem == null) {
            return subItemCount;
        }

        subItemCount = recursiveCollapse(position);
        position += getHeaderCount();
        if (notify) {
            if (animEnable) {
                notifyItemChanged(position);
                notifyItemRangeInserted(position + 1, subItemCount);
            } else {
                notifyDataSetChanged();
            }
        }

        return subItemCount;
    }

    private int recursiveCollapse(int position) {
        int subItemCount = 0;
        T item = mData.get(position);

        IExpandable expandableItem = (IExpandable) item;
        if (expandableItem.isExpanded()) {
            List<T> subData = expandableItem.getSubData();
            for (int i = 0; i < subData.size(); i++) {
                T subItem = subData.get(i);
                int pos = getItemPosition(subItem);
                if (pos != -1 && subItem instanceof IExpandable) {
                    subItemCount += recursiveCollapse(pos);
                }
                mData.remove(pos);
                subItemCount++;
            }
        }

        return subItemCount;
    }

    private int getItemPosition(T item) {
        return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
    }

    private boolean hasSubData(IExpandable expandableItem) {
        List<T> itemDatas = expandableItem.getSubData();
        return itemDatas != null && itemDatas.size() > 0;
    }

    private IExpandable getExpandableItem(int position) {
        T item = getData(position);
        if (isExpandable(item)) {
            return (IExpandable) item;
        }
        return null;
    }

    private boolean isExpandable(T item) {
        return item != null && item instanceof IExpandable;
    }

    /**
     * get the position of the item`s parent
     * if the item has no parent, reutrn -1
     * @param item
     * @return
     */
    public int getParentPosition(T item) {
        int position = getItemPosition(item);
        if (position == -1) {
            return -1;
        }

        int level;
        if (item instanceof IExpandable) {
            level = ((IExpandable) item).getLevel();
        } else {
            level = Integer.MAX_VALUE;
        }
        if (level == 0 || level == -1) {
            return -1;
        }

        for (int i = position; i >= 0; i--) {
            T temp = mData.get(i);
            if (temp instanceof IExpandable) {
                IExpandable expandableItem = (IExpandable) temp;
                int tempLevel = expandableItem.getLevel();
                if (tempLevel >= 0 && tempLevel < level) {
                    return i;
                }
            }
        }
        return -1;
    }

}
