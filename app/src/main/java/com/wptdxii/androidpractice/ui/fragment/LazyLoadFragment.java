package com.wptdxii.androidpractice.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.ui.base.BaseFragment;
import com.wptdxii.androidpractice.ui.base.ITabFragment;

import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wptdxii on 2016/8/31 0031.
 */
public class LazyLoadFragment extends BaseFragment implements ITabFragment{
    private static final String ARGUMENT = "argument";
    private static final String PARAM = "param";
    private TextView mTextView;
    private int page = -1;
    private int param = -1;

    public static LazyLoadFragment newInstance(int page) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT, page);
        LazyLoadFragment fragment = new LazyLoadFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            page = bundle.getInt(ARGUMENT);
        }
        enableLazyLoad(true);


    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lazyload, container, false);
        return view;
    }

    @Override
    protected void initView(View view) {
        this.mTextView = (TextView) view.findViewById(R.id.content);
        mTextView.setText("Page:" + page);
    }

    @Override
    protected void initData() {
        if (param == -1) {
            Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    param = new Random().nextInt(10);
                    subscriber.onNext(param);
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Integer integer) {
                            param = integer;
                            mTextView.append("  init data:" + param);
                        }
                    });
            //两种异步方式
                        loadData();
        } else {
            mTextView.append("  init data:" + param);
        }

    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                param = new Random().nextInt(10);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mTextView.append("  init data:" + param);
                    }
                });
            }
        }).start();

    }


    //viewPager中使用FragmentStatePagerAdapter时，下边两个方法可以不使用

    /**
     * viewPager中使用FragmentStatePagerAdapter时可以使用这种方法保存数据
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PARAM, param);
    }

    /**
     * viewPager中使用FragmentStatePagerAdapter时可以使用这种方法恢复数据
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            //TODO load data cached
            param = savedInstanceState.getInt(PARAM);
        }
    }

    @Override
    public void onMenuItemClick() {
        
    }

    @Override
    public BaseFragment getFragment() {
        return null;
    }
}