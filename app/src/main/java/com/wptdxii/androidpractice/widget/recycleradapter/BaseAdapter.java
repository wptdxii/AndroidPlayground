package com.wptdxii.androidpractice.widget.recycleradapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wptdxii on 2016/9/2 0002.
 */
public class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected List<T> mData;
    protected int mLayoutResId;
    protected View mLayout;
    
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private LinearLayout mEmptyView;

    public BaseAdapter(@LayoutRes int layoutResId,@Nullable List<T> data) {
        mData = (data == null) ? new ArrayList<T>() : data;
        this.mLayoutResId = layoutResId;
    }

    public BaseAdapter(View layout, @Nullable List<T> data) {
        this.mLayout = layout;
        this.mData = data;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void add(int positon, T item) {
        mData.add(positon,item);
        notifyItemInserted(positon);
    }

    public void remove(int positon) {
        mData.remove(positon);
        notifyItemRemoved(positon + getHeaderLayoutCount());
    }

    public int getHeaderLayoutCount() {
        
        return mHeaderLayout == null ? 0 : 1;
    }
    
    public int getFooterLayoutCount() {
        return mFooterLayout == null ? 0 : 1;
    }
    
    public int getEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }

    public void setData(List<T> data) {
        this.mData = data;
        
    }

    public void addData(List<T> additionalData) {
        this.mData.addAll(additionalData);
    }
    
    public List<T>  getData() {
        return  mData;
    }

    public T getItemData(int position) {
        return mData.get(position);
    }














//
//    @Override
//    public int getItemCount() {
//        int i = isLoadMore() ? 1 : 0;
//        int count = mData.size() + i + getHeaderLayoutCount() + getFooterLayoutCount();
//        if (mData.size() == 0 && mEmptyView != null) {
//            /**
//             *  setEmptyView(false) and add emptyView
//             */
//            if (count == 0 && (!mHeadAndEmptyEnable || !mFootAndEmptyEnable)) {
//                count += getmEmptyViewCount();
//                /**
//                 * {@link #setEmptyView(true, true, View)}
//                 */
//            } else if (mHeadAndEmptyEnable || mFootAndEmptyEnable) {
//                count += getmEmptyViewCount();
//            }
//
//            if ((mHeadAndEmptyEnable && getHeaderLayoutCount() == 1 && count == 1) || count == 0) {
//                mEmptyEnable = true;
//                count += getmEmptyViewCount();
//            }
//
//        }
//        return count;
//    }
//
//    /**
//     * Get the type of View that will be created by {@link #getItemView(int, ViewGroup)} for the specified item.
//     *
//     * @param position The position of the item within the adapter's data set whose view type we
//     *                 want.
//     * @return An integer representing the type of View. Two views should share the same type if one
//     * can be converted to the other in {@link #getItemView(int, ViewGroup)}. Note: Integers must be in the
//     * range 0 to {@link #getItemCount()} - 1.
//     */
//    @Override
//    public int getItemViewType(int position) {
//        /**
//         * if set headView and positon =0
//         */
//        if (mHeaderLayout != null && position == 0) {
//            return HEADER_VIEW;
//        }
//        /**
//         * if user has no data and add emptyView and position <2{(headview +emptyView)}
//         */
//        if (mData.size() == 0 && mEmptyEnable && mEmptyView != null && position <= 2) {
//            /**
//             * if set {@link #setEmptyView(boolean, boolean, View)}  position = 1
//             */
//            if ((mHeadAndEmptyEnable || mFootAndEmptyEnable) && position == 1) {
//                /**
//                 * if user want to show headview and footview and emptyView but not add headview
//                 */
//                if (mHeaderLayout == null && mEmptyView != null && mFooterLayout != null) {
//                    return FOOTER_VIEW;
//                    /**
//                     * add headview
//                     */
//                } else if (mHeaderLayout != null && mEmptyView != null) {
//                    return EMPTY_VIEW;
//                }
//            } else if (position == 0) {
//                /**
//                 * has no emptyView just add emptyview
//                 */
//                if (mHeaderLayout == null) {
//                    return EMPTY_VIEW;
//                } else if (mFooterLayout != null)
//
//                    return EMPTY_VIEW;
//
//
//            } else if (position == 2 && (mFootAndEmptyEnable || mHeadAndEmptyEnable) && mHeaderLayout != null && mEmptyView != null) {
//                return FOOTER_VIEW;
//
//            } /**
//             * user forget to set {@link #setEmptyView(boolean, boolean, View)}  but add footview and headview and emptyview
//             */
//            else if ((!mFootAndEmptyEnable || !mHeadAndEmptyEnable) && position == 1 && mFooterLayout != null) {
//                return FOOTER_VIEW;
//            }
//        } else if (mData.size() == 0 && mEmptyView != null && getItemCount() == (mHeadAndEmptyEnable ? 2 : 1) && mEmptyEnable) {
//            return EMPTY_VIEW;
//        } else if (position == mData.size() + getHeaderLayoutCount()) {
//            if (mNextLoadEnable)
//                return LOADING_VIEW;
//            else
//                return FOOTER_VIEW;
//        } else if (position > mData.size() + getHeaderLayoutCount()) {
//            return FOOTER_VIEW;
//        }
//        return getDefItemViewType(position - getHeaderLayoutCount());
//    }
//
//    protected int getDefItemViewType(int position) {
//        return super.getItemViewType(position);
//    }
//
//    @Override
//    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        BaseViewHolder baseViewHolder = null;
//        this.mContext = parent.getContext();
//        this.mLayoutInflater = LayoutInflater.from(mContext);
//        switch (viewType) {
//            case LOADING_VIEW:
//                baseViewHolder = getLoadingView(parent);
//                break;
//            case HEADER_VIEW:
//                baseViewHolder = new BaseViewHolder(mHeaderLayout);
//                break;
//            case EMPTY_VIEW:
//                baseViewHolder = new BaseViewHolder(mEmptyView == mCopyEmptyLayout ? mCopyEmptyLayout : mEmptyView);
//                break;
//            case FOOTER_VIEW:
//                baseViewHolder = new BaseViewHolder(mFooterLayout);
//                break;
//            default:
//                baseViewHolder = onCreateDefViewHolder(parent, viewType);
//        }
//        return baseViewHolder;
//
//    }
//
//
//    private BaseViewHolder getLoadingView(ViewGroup parent) {
//        if (mLoadingView == null) {
//            return createBaseViewHolder(parent, R.layout.def_loading);
//        }
//        return new BaseViewHolder(mLoadingView);
//    }
//
//    /**
//     * Called when a view created by this adapter has been attached to a window.
//     * simple to solve item will layout using all
//     * {@link #setFullSpan(RecyclerView.ViewHolder)}
//     *
//     * @param holder
//     */
//    @Override
//    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        int type = holder.getItemViewType();
//        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
//            setFullSpan(holder);
//        } else {
//            addAnimation(holder);
//        }
//    }
//
//    /**
//     * When set to true, the item will layout using all span area. That means, if orientation
//     * is vertical, the view will have full width; if orientation is horizontal, the view will
//     * have full height.
//     * if the hold view use StaggeredGridLayoutManager they should using all span area
//     *
//     * @param holder True if this item should traverse all spans.
//     */
//    protected void setFullSpan(RecyclerView.ViewHolder holder) {
//        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
//            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//            params.setFullSpan(true);
//        }
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if (manager instanceof GridLayoutManager) {
//            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    int type = getItemViewType(position);
//                    if (mSpanSizeLookup == null)
//                        return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) ? gridManager.getSpanCount() : 1;
//                    else
//                        return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) ? gridManager.getSpanCount() : mSpanSizeLookup.getSpanSize(gridManager, position - getHeaderLayoutCount());
//                }
//            });
//        }
//        recyclerView.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mRequestLoadMoreListener != null && pageSize == -1) {
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                    int visibleItemCount = layoutManager.getChildCount();
//                    Log.e("visibleItemCount", visibleItemCount + "");
//                    openLoadMore(visibleItemCount);
//                }
//            }
//        });
//
//    }
//
//    private boolean flag = true;
//    private SpanSizeLookup mSpanSizeLookup;
//
//    public interface SpanSizeLookup {
//        int getSpanSize(GridLayoutManager gridLayoutManager, int position);
//    }
//
//    /**
//     * @param spanSizeLookup instance to be used to query number of spans occupied by each item
//     */
//    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
//        this.mSpanSizeLookup = spanSizeLookup;
//    }
//
//    /**
//     * To bind different types of holder and solve different the bind events
//     *
//     * @param holder
//     * @param positions
//     * @see #getDefItemViewType(int)
//     */
//    @Override
//    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int positions) {
//        int viewType = holder.getItemViewType();
//
//        switch (viewType) {
//            case 0:
//                convert((BaseViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
//                break;
//            case LOADING_VIEW:
//                addLoadMore(holder);
//                break;
//            case HEADER_VIEW:
//                break;
//            case EMPTY_VIEW:
//                break;
//            case FOOTER_VIEW:
//                break;
//            default:
//                convert((BaseViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
//                onBindDefViewHolder((BaseViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
//                break;
//        }
//
//    }
//
//    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
//        return createBaseViewHolder(parent, mLayoutResId);
//    }
//
//    protected BaseViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
//        if (mContentView == null) {
//            return new BaseViewHolder(getItemView(layoutResId, parent));
//        }
//        return new BaseViewHolder(mContentView);
//    }
//
//    /**
//     * Return root layout of header
//     */
//    public LinearLayout getHeaderLayout() {
//        return mHeaderLayout;
//    }
//
//    /**
//     * Return root layout of footer
//     */
//    public LinearLayout getFooterLayout() {
//        return mFooterLayout;
//    }
//
//    /**
//     * Append header to the rear of the mHeaderLayout.
//     *
//     * @param header
//     */
//    public void addHeaderView(View header) {
//        addHeaderView(header, -1);
//    }
//
//    /**
//     * Add header view to mHeaderLayout and set header view position in mHeaderLayout.
//     * When index = -1 or index >= child count in mHeaderLayout,
//     * the effect of this method is the same as that of {@link #addHeaderView(View)}.
//     *
//     * @param header
//     * @param index  the position in mHeaderLayout of this header.
//     *               When index = -1 or index >= child count in mHeaderLayout,
//     *               the effect of this method is the same as that of {@link #addHeaderView(View)}.
//     */
//    public void addHeaderView(View header, int index) {
//        if (mHeaderLayout == null) {
//            if (mCopyHeaderLayout == null) {
//                mHeaderLayout = new LinearLayout(header.getContext());
//                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
//                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
//                mCopyHeaderLayout = mHeaderLayout;
//            } else {
//                mHeaderLayout = mCopyHeaderLayout;
//            }
//        }
//        index = index >= mHeaderLayout.getChildCount() ? -1 : index;
//        mHeaderLayout.addView(header, index);
//        this.notifyDataSetChanged();
//    }
//
//    /**
//     * Append footer to the rear of the mFooterLayout.
//     *
//     * @param footer
//     */
//    public void addFooterView(View footer) {
//        addFooterView(footer, -1);
//    }
//
//    /**
//     * Add footer view to mFooterLayout and set footer view position in mFooterLayout.
//     * When index = -1 or index >= child count in mFooterLayout,
//     * the effect of this method is the same as that of {@link #addFooterView(View)}.
//     *
//     * @param footer
//     * @param index  the position in mFooterLayout of this footer.
//     *               When index = -1 or index >= child count in mFooterLayout,
//     *               the effect of this method is the same as that of {@link #addFooterView(View)}.
//     */
//    public void addFooterView(View footer, int index) {
//        mNextLoadEnable = false;
//        if (mFooterLayout == null) {
//            if (mCopyFooterLayout == null) {
//                mFooterLayout = new LinearLayout(footer.getContext());
//                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
//                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
//                mCopyFooterLayout = mFooterLayout;
//            } else {
//                mFooterLayout = mCopyFooterLayout;
//            }
//        }
//        index = index >= mFooterLayout.getChildCount() ? -1 : index;
//        mFooterLayout.addView(footer, index);
//        this.notifyDataSetChanged();
//    }
//
//    /**
//     * remove header view from mHeaderLayout.
//     * When the child count of mHeaderLayout is 0, mHeaderLayout will be set to null.
//     *
//     * @param header
//     */
//    public void removeHeaderView(View header) {
//        if (mHeaderLayout == null) return;
//
//        mHeaderLayout.removeView(header);
//        if (mHeaderLayout.getChildCount() == 0) {
//            mHeaderLayout = null;
//        }
//        this.notifyDataSetChanged();
//    }
//
//    /**
//     * remove footer view from mFooterLayout,
//     * When the child count of mFooterLayout is 0, mFooterLayout will be set to null.
//     *
//     * @param footer
//     */
//    public void removeFooterView(View footer) {
//        if (mFooterLayout == null) return;
//
//        mFooterLayout.removeView(footer);
//        if (mFooterLayout.getChildCount() == 0) {
//            mFooterLayout = null;
//        }
//        this.notifyDataSetChanged();
//    }
//
//    /**
//     * remove all header view from mHeaderLayout and set null to mHeaderLayout
//     */
//    public void removeAllHeaderView() {
//        if (mHeaderLayout == null) return;
//
//        mHeaderLayout.removeAllViews();
//        mHeaderLayout = null;
//    }
//
//    /**
//     * remove all footer view from mFooterLayout and set null to mFooterLayout
//     */
//    public void removeAllFooterView() {
//        if (mFooterLayout == null) return;
//
//        mFooterLayout.removeAllViews();
//        mFooterLayout = null;
//    }
//
//    /**
//     * Set the view to show when load more failed.
//     */
//    public void setLoadMoreFailedView(View view) {
//        loadMoreFailedView = view;
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeFooterView(loadMoreFailedView);
//                openLoadMore(pageSize);
//            }
//        });
//    }
//
//    /**
//     * Call this method when load more failed.
//     */
//    public void showLoadMoreFailedView() {
//        loadComplete();
//        if (loadMoreFailedView == null) {
//            loadMoreFailedView = mLayoutInflater.inflate(R.layout.def_load_more_failed, null);
//            loadMoreFailedView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    removeFooterView(loadMoreFailedView);
//                    openLoadMore(pageSize);
//                }
//            });
//        }
//        addFooterView(loadMoreFailedView);
//    }
//
//    /**
//     * Sets the view to show if the adapter is empty
//     */
//    public void setEmptyView(View emptyView) {
//        setEmptyView(false, false, emptyView);
//    }
//
//    /**
//     * @param isHeadAndEmpty false will not show headView if the data is empty true will show emptyView and headView
//     * @param emptyView
//     */
//    public void setEmptyView(boolean isHeadAndEmpty, View emptyView) {
//        setEmptyView(isHeadAndEmpty, false, emptyView);
//    }
//
//    /**
//     * set emptyView show if adapter is empty and want to show headview and footview
//     *
//     * @param isHeadAndEmpty
//     * @param isFootAndEmpty
//     * @param emptyView
//     */
//    public void setEmptyView(boolean isHeadAndEmpty, boolean isFootAndEmpty, View emptyView) {
//        mHeadAndEmptyEnable = isHeadAndEmpty;
//        mFootAndEmptyEnable = isFootAndEmpty;
//        mEmptyView = emptyView;
//        if (mCopyEmptyLayout == null) {
//            mCopyEmptyLayout = emptyView;
//        }
//        mEmptyEnable = true;
//    }
//
//    /**
//     * When the current adapter is empty, the BaseQuickAdapter can display a special view
//     * called the empty view. The empty view is used to provide feedback to the user
//     * that no data is available in this AdapterView.
//     *
//     * @return The view to show if the adapter is empty.
//     */
//    public View getEmptyView() {
//        return mEmptyView;
//    }
//
//
//    /**
//     *
//     */
//    public void loadComplete() {
//        mNextLoadEnable = false;
//        mLoadingMoreEnable = false;
//        notifyDataSetChanged();
//
//    }
//
//
//    private void addLoadMore(RecyclerView.ViewHolder holder) {
//        if (isLoadMore() && !mLoadingMoreEnable) {
//            mLoadingMoreEnable = true;
//            mRequestLoadMoreListener.onLoadMoreRequested();
//        }
//    }
//
//
//    /**
//     * add animation when you want to show time
//     *
//     * @param holder
//     */
//    private void addAnimation(RecyclerView.ViewHolder holder) {
//        if (mOpenAnimationEnable) {
//            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
//                BaseAnimation animation = null;
//                if (mCustomAnimation != null) {
//                    animation = mCustomAnimation;
//                } else {
//                    animation = mSelectAnimation;
//                }
//                for (Animator anim : animation.getAnimators(holder.itemView)) {
//                    startAnim(anim, holder.getLayoutPosition());
//                }
//                mLastPosition = holder.getLayoutPosition();
//            }
//        }
//    }
//
//    /**
//     * set anim to start when loading
//     *
//     * @param anim
//     * @param index
//     */
//    protected void startAnim(Animator anim, int index) {
//        anim.setDuration(mDuration).start();
//        anim.setInterpolator(mInterpolator);
//    }
//
//    /**
//     * Determine whether it is loaded more
//     *
//     * @return
//     */
//    private boolean isLoadMore() {
//        return mNextLoadEnable && pageSize != -1 && mRequestLoadMoreListener != null && mData.size() >= pageSize;
//    }
//
//    /**
//     * @param layoutResId ID for an XML layout resource to load
//     * @param parent      Optional view to be the parent of the generated hierarchy or else simply an object that
//     *                    provides a set of LayoutParams values for root of the returned
//     *                    hierarchy
//     * @return view will be return
//     */
//    protected View getItemView(int layoutResId, ViewGroup parent) {
//        return mLayoutInflater.inflate(layoutResId, parent, false);
//    }
//
//
//    /**
//     * @see #convert(BaseViewHolder, Object) ()
//     * @deprecated This method is deprecated
//     * {@link #convert(BaseViewHolder, Object)} depending on your use case.
//     */
//    @Deprecated
//    protected void onBindDefViewHolder(BaseViewHolder holder, T item) {
//    }
//
//    public interface RequestLoadMoreListener {
//
//        void onLoadMoreRequested();
//
//    }
//
//
//    /**
//     * Set the view animation type.
//     *
//     * @param animationType One of {@link #ALPHAIN}, {@link #SCALEIN}, {@link #SLIDEIN_BOTTOM}, {@link #SLIDEIN_LEFT}, {@link #SLIDEIN_RIGHT}.
//     */
//    public void openLoadAnimation(@AnimationType int animationType) {
//        this.mOpenAnimationEnable = true;
//        mCustomAnimation = null;
//        switch (animationType) {
//            case ALPHAIN:
//                mSelectAnimation = new AlphaInAnimation();
//                break;
//            case SCALEIN:
//                mSelectAnimation = new ScaleInAnimation();
//                break;
//            case SLIDEIN_BOTTOM:
//                mSelectAnimation = new SlideInBottomAnimation();
//                break;
//            case SLIDEIN_LEFT:
//                mSelectAnimation = new SlideInLeftAnimation();
//                break;
//            case SLIDEIN_RIGHT:
//                mSelectAnimation = new SlideInRightAnimation();
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * Set Custom ObjectAnimator
//     *
//     * @param animation ObjectAnimator
//     */
//    public void openLoadAnimation(BaseAnimation animation) {
//        this.mOpenAnimationEnable = true;
//        this.mCustomAnimation = animation;
//    }
//
//    /**
//     * To open the animation when loading
//     */
//    public void openLoadAnimation() {
//        this.mOpenAnimationEnable = true;
//    }
//
//    /**
//     * {@link #addAnimation(RecyclerView.ViewHolder)}
//     *
//     * @param firstOnly true just show anim when first loading false show anim when load the data every time
//     */
//    public void isFirstOnly(boolean firstOnly) {
//        this.mFirstOnlyEnable = firstOnly;
//    }
//
//    /**
//     * Implement this method and use the helper to adapt the view to the given item.
//     *
//     * @param helper A fully initialized helper.
//     * @param item   The item that needs to be displayed.
//     */
//    protected abstract void convert(BaseViewHolder helper, T item);
//
//    /**
//     * Get the row id associated with the specified position in the list.
//     *
//     * @param position The position of the item within the adapter's data set whose row id we want.
//     * @return The id of the item at the specified position.
//     */
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    private int recursiveExpand(int position, @NonNull List list) {
//        int count = 0;
//        int pos = position + list.size() - 1;
//        for (int i = list.size() - 1; i >= 0; i--, pos--) {
//            if (list.get(i) instanceof IExpandable) {
//                IExpandable item = (IExpandable) list.get(i);
//                if (item.isExpanded() && hasSubItems(item)) {
//                    List subList = item.getSubItems();
//                    mData.addAll(pos + 1, subList);
//                    int subItemCount = recursiveExpand(pos + 1, subList);
//                    count += subItemCount;
//                }
//            }
//        }
//        return count;
//
//    }
//
//    /**
//     * Expand an expandable item
//     *
//     * @param position position of the item
//     * @return the number of items that have been added.
//     */
//    public int expand(@IntRange(from = 0) int position) {
//        T item = getItem(position);
//        if (!isExpandable(item)) {
//            return 0;
//        }
//
//        IExpandable expandable = (IExpandable) item;
//        if (!hasSubItems(expandable)) {
//            expandable.setExpanded(false);
//            return 0;
//        }
//        int subItemCount = 0;
//        if (!expandable.isExpanded()) {
//            List list = expandable.getSubItems();
//            mData.addAll(position + 1, list);
//            subItemCount += recursiveExpand(position + 1, list);
//
//            expandable.setExpanded(true);
//            subItemCount += list.size();
//        }
//        notifyItemRangeInserted(position + 1, subItemCount);
//        return subItemCount;
//    }
//
//    private int recursiveCollapse(@IntRange(from = 0) int position) {
//        T item = getItem(position);
//        if (!isExpandable(item)) {
//            return 0;
//        }
//        IExpandable expandable = (IExpandable) item;
//        int subItemCount = 0;
//        if (expandable.isExpanded()) {
//            List<T> subItems = expandable.getSubItems();
//            for (int i = subItems.size() - 1; i >= 0; i--) {
//                T subItem = subItems.get(i);
//                int pos = getItemPosition(subItem);
//                if (pos < 0) {
//                    continue;
//                }
//                if (subItem instanceof IExpandable) {
//                    subItemCount += recursiveCollapse(pos);
//                }
//                mData.remove(pos);
//                subItemCount++;
//            }
//        }
//        return subItemCount;
//    }
//
//    /**
//     * Collapse an expandable item that has been expanded..
//     *
//     * @param position the position of the item
//     * @return the number of subItems collapsed.
//     */
//    public int collapse(@IntRange(from = 0) int position) {
//        T item = getItem(position);
//        if (!isExpandable(item)) {
//            return 0;
//        }
//        IExpandable expandable = (IExpandable) item;
//        int subItemCount = recursiveCollapse(position);
//        expandable.setExpanded(false);
//        notifyItemRangeRemoved(position + 1, subItemCount);
//        return subItemCount;
//    }
//
//    private int getItemPosition(T item) {
//        return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
//    }
//
//    private boolean hasSubItems(IExpandable item) {
//        List list = item.getSubItems();
//        return list != null && list.size() > 0;
//    }
//
//    private boolean isExpandable(T item) {
//        return item != null && item instanceof IExpandable;
//    }

}
