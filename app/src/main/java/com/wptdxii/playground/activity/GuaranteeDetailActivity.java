package com.wptdxii.playground.activity;

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

public class GuaranteeDetailActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private ListView mListView;
//    private View mFooter;

    private List<GuaranteeBean> mData;

    public static void actionStart(Context context, List<GuaranteeBean> data) {
        Intent intent = new Intent(context, GuaranteeDetailActivity.class);
        intent.putExtra("data", (Serializable) data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarantee_detail);

        initView();
        initData();
    }

    private void initData() {
        mData = (List<GuaranteeBean>) getIntent().getSerializableExtra("data");
        GuaranteeAdapter adapter = new GuaranteeAdapter(this, mData);
        mListView.setAdapter(adapter);

    }

    private void initView() {
        rlBack = (RelativeLayout) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_text);
        rlShare = (RelativeLayout) findViewById(R.id.rl_right);
        mListView = (ListView) findViewById(R.id.lv_guarantee_detail);
//        mFooter = LayoutInflater.from(this).inflate(R.layout.item_guarantee_footer, null);

        rlBack.setOnClickListener(this);
        tvTitle.setText("保障详情");
        rlShare.setVisibility(View.INVISIBLE);
//        mListView.addFooterView(mFooter);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }
}

