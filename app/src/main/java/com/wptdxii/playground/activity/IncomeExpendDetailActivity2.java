package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.IncomeExpendAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.IncomeExpendBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetPaymentListForWallet;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.customview.PublicLoadPage;

import java.util.ArrayList;

public class IncomeExpendDetailActivity2 extends BaseActivity implements NetResultListener, OnClickListener {

    private final int GET_INCOME_EXPEND_LIST = 0;


    private ListViewScrollView lv_income_expend;

    private String user_id;
    private String token;
    private ArrayList<IncomeExpendBean> dataMap;
    private IncomeExpendAdapter adapter;
    private ArrayList<String> codelist = new ArrayList<String>();
    private PublicLoadPage mLoadPage;
    private TextView tv_text;
    private View p_view;
    private String title;
    private String type = "全部";
    private RelativeLayout rl_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_expend_detail2);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        initView();
        initEvent();
        initData(title, type);
    }


    private void initView() {
        mLoadPage = new PublicLoadPage((LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                mLoadPage.startLoad();
                initData(title, type);
            }
        };

        lv_income_expend = (ListViewScrollView) findViewById(R.id.lv_income_expend);
        tv_text = (TextView) findViewById(R.id.tv_text);
        RelativeLayout iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        p_view = findViewById(R.id.p_view);
        iv_back.setOnClickListener(this);
    }

    private void initEvent() {

        tv_text.setText(title);
        lv_income_expend.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (null != adapter) {
                    IncomeExpendBean bean = (IncomeExpendBean) adapter.getItem(position);
                    Intent intent = new Intent(IncomeExpendDetailActivity2.this, IncomeExpendItemDetailActivity.class);

                    intent.putExtra("payment_id", bean.getId());
                    intent.putExtra("title", bean.getTitle());
                    intent.putExtra("money", bean.getMoney());
                    intent.putExtra("category", bean.getCategory());


                    startActivity(intent);
                }

            }
        });


    }

    private void initData(String title, String type) {

        mLoadPage.startLoad();
        dataMap = new ArrayList<IncomeExpendBean>();
        GetPaymentListForWallet getPaymentListRequest = new GetPaymentListForWallet(this);
        getPaymentListRequest.execute(user_id, token, GET_INCOME_EXPEND_LIST, dataMap, type, title, codelist);

//		getPaymentListRequest.execute("161",token,GET_INCOME_EXPEND_LIST,dataMap,type);
    }


    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_INCOME_EXPEND_LIST:
                if (flag == MyApplication.DATA_OK) {



                    if (dataMap.size() <= 0) {
                        mLoadPage.loadFail("暂无记录", MyApplication.BUTTON_RELOAD, 1);
                        return;
                    }

                    mLoadPage.loadSuccess(null, null);
                    adapter = new IncomeExpendAdapter(IncomeExpendDetailActivity2.this, dataMap);
                    lv_income_expend.setAdapter(adapter);

                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    mLoadPage.loadFail("暂无记录", MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.JSON_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.DATA_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                }
                break;

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;


        }

    }


}
