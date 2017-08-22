package com.wptdxii.playground.ui.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wptdxii.playground.R;
import com.wptdxii.playground.imageloader.ImageLoader;
import com.wptdxii.playground.ui.base.BaseSwipeRecyclerActivity;
import com.wptdxii.uiframework.widget.toolbarhelper.ToolbarHelper;
import com.wptdxii.uikit.widget.swiperecycler.BaseSwipeViewHolder;
import com.wptdxii.uikit.widget.swiperecycler.SwipeRecycler;
import com.wptdxii.data.net.retrofit.api.ApiFactory;
import com.wptdxii.data.net.retrofit.rx.func.RetryFunc;
import com.wptdxii.data.net.retrofit.rx.subscriber.BaseGankResponseSubscriber;
import com.wptdxii.data.net.retrofit.transformer.DefaultTransformer;
import com.wptdxii.domain.model.gank.BaseGankResponse;
import com.wptdxii.domain.model.gank.GankModel;

import java.util.ArrayList;

import retrofit2.Call;

public class SwipeRecyclerActivity extends BaseSwipeRecyclerActivity<GankModel> {
    private int page = 1;
    @Override
    protected void initListData(Bundle savedInstanceState) {
        //首次进入不显示刷新进度条,默认显示
        //mSwipeRecycler.isInitWithRefreshBar(false);
//        mSwipeRecycler.setRefreshing();
    }
    
    

    @Override
    protected BaseSwipeViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe_recycler, parent, false);
        return new SwipeRecyclerViewHolder(view);
    }

    @Override
    public void onRefresh(final int action) {
        if (action == SwipeRecycler.ACTION_PULL_TO_REFRESH) {
            page = 1;
        }
        Call<BaseGankResponse<ArrayList<GankModel>>> call = ApiFactory.getGankApi().getGankList(20, page++);
        ApiFactory.getGankApi()
               .getGankListWithRx(20,page++)
               .compose(new DefaultTransformer<BaseGankResponse<ArrayList<GankModel>>, BaseGankResponse<ArrayList<GankModel>>>())
               .retryWhen(new RetryFunc())//设置网络失败时的重连机制
               .subscribe(new BaseGankResponseSubscriber<ArrayList<GankModel>>() {
                   @Override
                   protected void onSuccess(ArrayList<GankModel> gankModels) {

                       if (action == SwipeRecycler.ACTION_PULL_TO_REFRESH) {
                           mDataList.clear();
                       }
                       if (gankModels == null || gankModels.size() == 0) {
                           mSwipeRecycler.enableLaodMore(false);
                       } else {
                           mSwipeRecycler.enableLaodMore(true);
                           mDataList.addAll(gankModels);
                           mAdapter.notifyDataSetChanged();
                       }

                       mSwipeRecycler.onRefreshCompleted();

                   }

                   @Override
                   protected void onFailure(Throwable e) {
                        mSwipeRecycler.onRefreshCompleted();
                   }
               });
    }

    @Override
    protected void setupToolbar(ToolbarHelper toolbarHelper) {

    }

    private class SwipeRecyclerViewHolder extends BaseSwipeViewHolder {
        TextView mTextView;
        ImageView mImageView;
        public SwipeRecyclerViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.swipteRecyclerItemLabel);
            mImageView = (ImageView) view.findViewById(R.id.swipeRecyclerItemImg);
        }

        @Override
        protected void onBindViewHolder(int position) {
            GankModel gankModel = mDataList.get(position);
            mTextView.setVisibility(View.GONE);
            ImageLoader.loadImage(mImageView.getContext(),mImageView, gankModel.getUrl());
        }

        @Override
        protected void onItemClick(View view, int position) {
            
        }
    }
}




