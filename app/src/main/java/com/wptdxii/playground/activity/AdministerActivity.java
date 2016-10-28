package com.wptdxii.playground.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.AdministerExpandalbeListAdapter;
import com.cloudhome.view.xlistview.ExpandListView;

import java.util.ArrayList;
import java.util.List;

public class AdministerActivity extends BaseActivity implements View.OnClickListener, ExpandListView.IXListViewListener {
    private List<String> mGroup;
    private List<List<String>> mChild;
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private RelativeLayout rlAdminister;
    private ImageView imgAdministerArrow;
    private ExpandListView elvAdminister;
    private boolean mAdministerShown = false;
    private AdministerExpandalbeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administer);
        initView();
        initData();
    }

    private void initData() {
        mGroup = new ArrayList<>();
        mChild = new ArrayList<>();

        mGroup.add("北京");
        mGroup.add("北京");
        mGroup.add("北京");

        List<String> child1 = new ArrayList<>();
        child1.add("欧阳");
        child1.add("欧阳");
        child1.add("欧阳");
        child1.add("欧阳");
        child1.add("欧阳");
        child1.add("欧阳");
        child1.add("欧阳");

        List<String> child2 = new ArrayList<>();
        child2.add("西门");
        child2.add("西门");
        child2.add("西门");
        child2.add("西门");
        child2.add("西门");
        child2.add("西门");
        child2.add("西门");

        List<String> child3 = new ArrayList<>();
        child3.add("吹雪");
        child3.add("吹雪");
        child3.add("吹雪");
        child3.add("吹雪");
        child3.add("吹雪");
        child3.add("吹雪");
        child3.add("吹雪");

        mChild.add(child1);
        mChild.add(child2);
        mChild.add(child3);

        adapter = new AdministerExpandalbeListAdapter(this, mGroup, mChild);
        elvAdminister.setAdapter(adapter);
    }

    private void initView() {
        rlBack = (RelativeLayout) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_text);
        rlShare = (RelativeLayout) findViewById(R.id.rl_right);
        rlAdminister = (RelativeLayout) findViewById(R.id.rl_administer);
        imgAdministerArrow = (ImageView) findViewById(R.id.img_arrow_administer);
        elvAdminister = (ExpandListView) findViewById(R.id.elv_administer);

        rlBack.setOnClickListener(this);
        tvTitle.setText("辖下");
        rlShare.setVisibility(View.GONE);
        rlAdminister.setOnClickListener(this);
        elvAdminister.setPullRefreshEnable(true);
        elvAdminister.setPullLoadEnable(false);
        elvAdminister.setXListViewListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.rl_administer) {
            if (!mAdministerShown) {
                imgAdministerArrow.setBackgroundResource(R.drawable.icon_down);
                elvAdminister.setVisibility(View.VISIBLE);
            } else {
                imgAdministerArrow.setBackgroundResource(R.drawable.icon_right);
                elvAdminister.setVisibility(View.GONE);
            }

            mAdministerShown = !mAdministerShown;
        }
    }

    @Override
    public void onRefresh() {
        //TODO refresh
    }

    @Override
    public void onLoadMore() {

    }
}