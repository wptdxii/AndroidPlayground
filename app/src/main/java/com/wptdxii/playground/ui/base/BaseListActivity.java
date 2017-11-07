package com.wptdxii.playground.ui.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.wptdxii.playground.R;
import com.wptdxii.playground.adapter.ListAdapter;
import com.wptdxii.playground.model.Component;
import com.wptdxii.uiframework.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wptdxii on 17-11-6 下午10:28
 * Email: wptdxii@gmail.com
 * Blog: https://wptdxii.github.io
 * Github: https://github.com/wptdxii
 */

public abstract class BaseListActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_base_list;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        onSetupToolbar(toolbar);
        setSupportActionBar(toolbar);
        onSetupActionBar(getSupportActionBar());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.divider);
        itemDecoration.setDrawable(drawable);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        List<Component> componentList = new ArrayList<>();
        onCreateComponentList(componentList);
        recyclerView.setAdapter(new ListAdapter(componentList));
    }

    protected abstract void onSetupToolbar(Toolbar toolbar);

    protected void onSetupActionBar(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected abstract void onCreateComponentList(List<Component> list);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
