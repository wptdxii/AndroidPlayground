package com.wptdxii.uiframework.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wptdxii on 2016/8/3 0003.
 */
public abstract class BaseFragment extends Fragment {
    private boolean isVisibleToUser;
    private boolean isViewInitialized;
    private boolean isDataInitialized;
    private boolean isLazyLoadEnabled;

    /**
     * 加载布局文件
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected abstract View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 获取控件并初始化控件数据
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 异步加载数据
     */
    protected abstract void initData();

    private static final String TAG = "BaseFragment";

    /**
     * 一般在onAttach()设置是否懒加载
     * 在其他声明周期方法中也可以
     *
     * @param isLazyLoadEnabled
     */
    public void enableLazyLoad(boolean isLazyLoadEnabled) {
        this.isLazyLoadEnabled = isLazyLoadEnabled;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        checkIfLoadData();
    }

    private void checkIfLoadData() {
        if (isVisibleToUser && isViewInitialized && !isDataInitialized) {
            isDataInitialized = true;
            initData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initContentView(inflater, container, savedInstanceState);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        this.isViewInitialized = true;
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLazyLoadEnabled) {
            initData();
        } else {
            if (isDataInitialized) {
                initData();
            } else {
                checkIfLoadData();
            }
        }
    }

    /**
     * 如果需要保存数据，调用onSaveInstanceState()进行保存
     * 然后重写该方法恢复数据
     * 根据需求，一般使用FragmentStatePagerAdapter时这样调用
     * 记得调用super
     *
     * @param savedInstanceState
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.isDataInitialized = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.isViewInitialized = false;
    }

    /**
     * 当与viewPager配合使用时，如果使用FragmentStatePagerAdapter时，整个Fragment会被回收
     * 可以根据需求，在这里进行数据保存
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
