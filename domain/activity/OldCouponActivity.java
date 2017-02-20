package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.MyCouponAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserPrizeBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetUserPrize;

import java.util.ArrayList;

public class OldCouponActivity extends BaseActivity implements View.OnClickListener,NetResultListener {
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
    private MyCouponAdapter myCouponAdapter;
    private RelativeLayout no_ticket;
    private ImageView iv_get_ticket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_coupon);
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
        no_ticket= (RelativeLayout) findViewById(R.id.no_ticket);
        iv_get_ticket= (ImageView) findViewById(R.id.iv_get_ticket);
        rl_back.setOnClickListener(this);
        iv_get_ticket.setOnClickListener(this);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text.setText("往期礼券");
        lv_my_coupon = (ListView) findViewById(R.id.lv_old_coupon);
    }

    private void initData() {
        list = new ArrayList<UserPrizeBean>();
        getUserPrize = new GetUserPrize(this);
        getUserPrize.execute(user_id, list, GET_USER_PRIZE,token,"yes");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_back:
                finish();
            case R.id.iv_get_ticket:
                Intent backIntent=new Intent();
                setResult(200,backIntent);
                finish();
                break;
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_USER_PRIZE:
                if (flag == MyApplication.DATA_OK) {
                    if(list.size()<=0){
                        lv_my_coupon.setVisibility(View.GONE);
                        no_ticket.setVisibility(View.VISIBLE);
                    }
                    myCouponAdapter = new MyCouponAdapter(list, OldCouponActivity.this,2);
                    lv_my_coupon.setAdapter(myCouponAdapter);
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(OldCouponActivity.this,"网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                } else if (flag == MyApplication.JSON_ERROR) {
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                } else if (flag == MyApplication.DATA_ERROR) {
                    String errmsg=dataObj.toString();
                    Toast.makeText(OldCouponActivity.this,errmsg,Toast.LENGTH_SHORT).show();
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
