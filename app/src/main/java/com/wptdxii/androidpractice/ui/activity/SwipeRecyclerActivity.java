package com.wptdxii.androidpractice.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.imageloader.ImageLoader;
import com.wptdxii.androidpractice.model.BaseModel;
import com.wptdxii.androidpractice.model.BenefitEntity;
import com.wptdxii.androidpractice.network.retrofit.api.ApiFactory;
import com.wptdxii.androidpractice.network.retrofit.rx.func.RetryFunc;
import com.wptdxii.androidpractice.network.retrofit.rx.subscriber.BaseModelSubscriber;
import com.wptdxii.androidpractice.network.retrofit.transformer.DefaultTransformer;
import com.wptdxii.androidpractice.ui.base.BaseSwipeRecyclerActivity;
import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeRecyclerAdapter;
import com.wptdxii.androidpractice.widget.swiperecycler.BaseSwipeViewHolder;
import com.wptdxii.androidpractice.widget.swiperecycler.SwipeRecycler;

import java.util.ArrayList;

import retrofit2.Call;

public class SwipeRecyclerActivity extends BaseSwipeRecyclerActivity<BenefitEntity> {
    private int page = 1;
    private SwipeRecycler mSwipeRecycler;
    private BaseSwipeRecyclerAdapter mAdapter;
    private ArrayList<BenefitEntity> mDataList;
    
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
        Call<BaseModel<ArrayList<BenefitEntity>>> call = ApiFactory.getGankApi().defaultBenefits(20, page++);
        ApiFactory.getGankApi()
               .rxBenefits(20,page++)
               .compose(new DefaultTransformer<BaseModel<ArrayList<BenefitEntity>>, BaseModel<ArrayList<BenefitEntity>>>())
               .retryWhen(new RetryFunc())//设置网络失败时的重连机制
               .subscribe(new BaseModelSubscriber<ArrayList<BenefitEntity>>() {
                   @Override
                   protected void onSuccess(ArrayList<BenefitEntity> benefitEntities) {
                       if (action == SwipeRecycler.ACTION_PULL_TO_REFRESH) {
                           mDataList.clear();
                       }
                       if (benefitEntities == null || benefitEntities.size() == 0) {
                           mSwipeRecycler.enableLaodMore(false);
                       } else {
                           mSwipeRecycler.enableLaodMore(true);
                           mDataList.addAll(benefitEntities);
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
            BenefitEntity benefitEntity = mDataList.get(position);
            mTextView.setVisibility(View.GONE);
            ImageLoader.loadImage(SwipeRecyclerActivity.this,mImageView, benefitEntity.url);
        }

        @Override
        protected void onItemClick(View view, int position) {
            
        }
    }
}
