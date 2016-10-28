package com.wptdxii.playground.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.MyCouponAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserPrizeBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetUserPrize;

import java.util.ArrayList;

public class MyCouponsActivity extends BaseActivity implements View.OnClickListener,NetResultListener {

    public static final int GET_USER_PRIZE = 1;
    private RelativeLayout rl_back;
    private TextView tv_text;
    private RelativeLayout rl_right;
    private ListView lv_my_coupon;
    private GetUserPrize getUserPrize;
    private ArrayList<UserPrizeBean> list;
    private String loginString;
    private String user_id;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        initView();
        initData();
    }

    private void initView() {
        rl_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        tv_text = (TextView) findViewById(R.id.tv_text);
        rl_back.setOnClickListener(this);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text.setText("我的礼券");
        lv_my_coupon = (ListView) findViewById(R.id.lv_my_coupon);
    }

    private void initData() {
        list = new ArrayList<UserPrizeBean>();
        getUserPrize = new GetUserPrize(this);
        getUserPrize.execute(user_id, list, GET_USER_PRIZE,token);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_USER_PRIZE:
                if (flag == MyApplication.DATA_OK) {
                    if (list.size() > 0) {
                        lv_my_coupon.setAdapter(new MyCouponAdapter(list, MyCouponsActivity.this));
                    }


                } else if (flag == MyApplication.NET_ERROR) {
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                }
                break;
        }

    }
}
