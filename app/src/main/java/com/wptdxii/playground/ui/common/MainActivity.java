package com.wptdxii.playground.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.playground.adapter.MainAdapter;
import com.wptdxii.playground.model.Component;
import com.wptdxii.playground.ui.sample.ToolbarSampleActivity;
import com.wptdxii.uiframework.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wptdxii on 2017/8/22 0022.
 */

public class MainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, MainActivity.class);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreateContent(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        List<Component> componentList = getComponents();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new MainAdapter(componentList));
    }

    private List<Component> getComponents() {
        List<Component> componentList = new ArrayList<>();
        componentList.add(new Component("Sample", ToolbarSampleActivity.class));
        return componentList;
    }
}
