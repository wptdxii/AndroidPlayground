package com.wptdxii.androidpractice.ui.activity;

import android.os.Bundle;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.ui.fragment.SwipeRecyclerFragment;
import com.wptdxii.uiframework.base.BaseActivity;

public class SwipeRecyclerFragmentActivity extends BaseActivity {
    private SwipeRecyclerFragment fragment;
    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_swipe_recycler_fragment, -1, -1, MODE_BACK);
        
    }

    @Override
    protected void initView() {
        //SwipeRecyclerFragment的实现使用了懒加载，不与ViewPager配合使用时不会加载数据，必须重写方法
        //禁止其懒加载
        fragment = new SwipeRecyclerFragment();
        fragment.enableLazyLoad(false);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        
    }
}
