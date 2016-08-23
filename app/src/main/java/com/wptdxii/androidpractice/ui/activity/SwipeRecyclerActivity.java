package com.wptdxii.androidpractice.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.imageloader.ImageLoader;
import com.wptdxii.androidpractice.imageloader.ImageLoaderConfig;
import com.wptdxii.androidpractice.model.Benefit;
import com.wptdxii.androidpractice.network.retrofit.api.ApiFactory;
import com.wptdxii.androidpractice.network.retrofit.subscriber.BaseModelSubscriber;
import com.wptdxii.androidpractice.ui.base.BaseSwipeRecyclerActivity;
import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeRecyclerAdapter;
import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeViewHolder;
import com.wptdxii.androidpractice.widget.swiperecycler.SwipeRecycler;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SwipeRecyclerActivity extends BaseSwipeRecyclerActivity<Benefit> {
    private int page = 1;
    private SwipeRecycler mSwipeRecycler;
    private BaseSwipeRecyclerAdapter mAdapter;
    private ArrayList<Benefit> mDataList;
    
    @Override
    protected void initListData(Bundle savedInstanceState) {
        mSwipeRecycler = getSwipeRecycler();
        mAdapter = getAdapter();
        mDataList = getDataList();
        
        //首次进入不现实刷新进度条,默认显示
        //mSwipeRecycler.isInitWithRefreshBar(false);
        mSwipeRecycler.setRefreshing();
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
        //未封装
        //GankApi api = RetrofitClient.getInstance().createApi(GankApi.class);
        //封装后
//        GankApi api = ApiFactory.getGankApi();
//        Call<BaseModel<ArrayList<Benefit>>> call = api.defaultBenefits(20, page++);
//        call.enqueue(new Callback<BaseModel<ArrayList<Benefit>>>() {
//            @Override
//            public void onResponse(Call<BaseModel<ArrayList<Benefit>>> call, Response<BaseModel<ArrayList<Benefit>>> response) {
//                if (action == SwipeRecycler.ACTION_PULL_TO_REFRESH) {
//                    mDataList.clear();
//                }
//                if (response.body() == null || response.body().results.size() == 0) {
//                    mSwipeRecycler.enableLaodMore(false);
//                } else {    
//                    mSwipeRecycler.enableLaodMore(true);
//                    mDataList.addAll(response.body().results);
//                    mAdapter.notifyDataSetChanged();
//                } 
//                
//                mSwipeRecycler.onRefreshCompleted();
//            }
//
//            @Override
//            public void onFailure(Call<BaseModel<ArrayList<Benefit>>> call, Throwable t) {
//                mSwipeRecycler.onRefreshCompleted();
//                
//            }
//        });

       ApiFactory.getGankApi()
               .rxBenefits(20,page++)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new BaseModelSubscriber<ArrayList<Benefit>>() {
                   @Override
                   protected void onSuccess(ArrayList<Benefit> benefits) {
                       if (action == SwipeRecycler.ACTION_PULL_TO_REFRESH) {
                           mDataList.clear();
                       }
                       if (benefits == null || benefits.size() == 0) {
                           mSwipeRecycler.enableLaodMore(false);
                       } else {
                           mSwipeRecycler.enableLaodMore(true);
                           mDataList.addAll(benefits);
                           mAdapter.notifyDataSetChanged();
                       }

                       mSwipeRecycler.onRefreshCompleted();
                   }

                   @Override
                   protected void onFailure(Throwable e) {

                   }
               });
        
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
            Benefit benefit = mDataList.get(position);
            mTextView.setVisibility(View.GONE);
            ImageLoader.getInstance().loadImage(SwipeRecyclerActivity.this, 
                    new ImageLoaderConfig.Builder()
                    .url(benefit.getUrl())
                    .imgView(mImageView)
                    .build()
            );

        }

        @Override
        protected void onItemClick(View view, int position) {
            
        }
    }
}
