package com.cloudhome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.GuaranteeAdapter;
import com.cloudhome.bean.GuaranteeBean;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuaranteeDetailActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.lv_guarantee_detail)
    ListView lvGuaranteeDetail;

    public static void activityStart(Context context, List<GuaranteeBean> data) {
        Intent intent = new Intent(context, GuaranteeDetailActivity.class);
        intent.putExtra("data", (Serializable) data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarantee_detail);
        ButterKnife.bind(this);
        initView();
    }

    @SuppressWarnings("unchecked")
    private void initView() {
        rlShare = (RelativeLayout) findViewById(R.id.rl_share);
        tvTitle.setText("保障详情");
        rlShare.setVisibility(View.INVISIBLE);
        List<GuaranteeBean> data = (List<GuaranteeBean>) getIntent().getSerializableExtra("data");
        GuaranteeAdapter adapter = new GuaranteeAdapter(this, data);
        lvGuaranteeDetail.setAdapter(adapter);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }
}

