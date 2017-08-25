package com.wptdxii.playground.ui.sample.recyclerview;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wptdxii.data.net.retrofit.api.ApiFactory;
import com.wptdxii.data.net.retrofit.rx.func.RetryFunc;
import com.wptdxii.data.net.retrofit.rx.subscriber.BaseGankResponseSubscriber;
import com.wptdxii.data.net.retrofit.transformer.DefaultTransformer;
import com.wptdxii.domain.model.gank.BaseGankResponse;
import com.wptdxii.domain.model.gank.GankModel;
import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

import java.util.ArrayList;

public class SampleRecyclerViewActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<GankModel> mDataList;
    private SampleRecyclerViewAdapter mAdapter;

    @LayoutRes
    @Override
    protected int getContentViewId() {
        return R.layout.activity_sample_recyclerview;
    }

    @Override
    protected void setContent(Bundle savedInstanceState) {
        mRecyclerView = findView(R.id.mRecyclerView);
        ApiFactory.getGankApi()
                .getGankListWithRx(100, 1)
                .compose(new DefaultTransformer<BaseGankResponse<ArrayList<GankModel>>, BaseGankResponse<ArrayList<GankModel>>>())
                .retryWhen(new RetryFunc())
                .subscribe(new BaseGankResponseSubscriber<ArrayList<GankModel>>() {
                    @Override
                    protected void onSuccess(ArrayList<GankModel> gankModels) {
                        mDataList = gankModels;
                        mAdapter = new SampleRecyclerViewAdapter(SampleRecyclerViewActivity.this, mDataList);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(SampleRecyclerViewActivity.this));
                        //                        mRecyclerView.setLayoutManager(new GridLayoutManager(SampleRecyclerViewActivity.this, 3));
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    protected void onFailure(Throwable e) {

                    }
                });

    }
}