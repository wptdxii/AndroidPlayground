package com.wptdxii.playground.module.sample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseActivity;

public class SwipeRecyclerFragmentActivity extends BaseActivity {
    private SwipeRecyclerFragment fragment;

    @LayoutRes
    @Override
    protected int onCreateContentView() {
//        setContentView(R.layout.activity_swipe_recycler_fragment, -1, -1, MODE_BACK);
        return R.layout.activity_swipe_recycler_fragment;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        //SwipeRecyclerFragment的实现使用了懒加载，不与ViewPager配合使用时不会加载数据，必须重写方法
        //禁止其懒加载
        fragment = new SwipeRecyclerFragment();
        fragment.enableLazyLoad(false);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }
}
