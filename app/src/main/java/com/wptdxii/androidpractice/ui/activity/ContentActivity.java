package com.wptdxii.androidpractice.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.wptdxii.androidpractice.R;
import com.wptdxii.androidpractice.model.Module;
import com.wptdxii.androidpractice.ui.base.BaseContentActivity;

import java.util.ArrayList;

public class ContentActivity extends BaseContentActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ContentActivity.class);
        context.startActivity(intent);
    }
    

    @Override
    protected void initToolbarTitle(int titleResId) {
        super.initToolbarTitle(R.string.content_activity_toolbar_title);
    }

    @Override
    protected void addItem(ArrayList<Module> mDataList) {
        mDataList.add(new Module("SwipeRecycler",SampleSwipeRecyclerActivity.class));
        mDataList.add(new Module("MainActivity",HomeActivity.class));
        mDataList.add(new Module("SDKDemo", SampleSDKActivity.class));
    }
    
}
