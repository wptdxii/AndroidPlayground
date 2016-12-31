package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.Statistics;
import com.umeng.analytics.MobclickAgent;


public class ZhanYeHuoDongActivity extends BaseActivity implements View.OnClickListener,NetResultListener{
    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private RelativeLayout rl_1;
    private RelativeLayout rl_2;
    private RelativeLayout rl_3;

    public static ZhanYeHuoDongActivity zhanYeHuoDongActivityInstance = null;
    private String Event_ActivityZone = "ZhanYeHuoDongActivity_ActivityZone";
    private String Event_Exhibition = "ZhanYeHuoDongActivity_Exhibition";
    private String Event_Settlement = "ZhanYeHuoDongActivity_Settlement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhan_ye_huo_dong);
        zhanYeHuoDongActivityInstance=this;
        iv_back= (RelativeLayout) findViewById(R.id.iv_back);
        rl_right= (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("展业活动");
        iv_back.setOnClickListener(this);

        rl_1= (RelativeLayout) findViewById(R.id.rl_1);
        rl_2= (RelativeLayout) findViewById(R.id.rl_2);
        rl_3= (RelativeLayout) findViewById(R.id.rl_3);
        rl_1.setOnClickListener(this);
        rl_2.setOnClickListener(this);
        rl_3.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_1:
                intent.setClass(ZhanYeHuoDongActivity.this, DiscoverActiveListActivity.class);
                startActivity(intent);
                MobclickAgent.onEvent(this, Event_ActivityZone);
                break;
            case R.id.rl_2:
                intent.setClass(ZhanYeHuoDongActivity.this, ZhanYeActivity.class);
                startActivity(intent);
                MobclickAgent.onEvent(this, Event_Exhibition);
                break;
            case R.id.rl_3:
                Statistics statistics=new Statistics(ZhanYeHuoDongActivity.this);
                intent.setClass(ZhanYeHuoDongActivity.this, ClaimServiceActivity.class);
                startActivity(intent);
                statistics.execute("index_func_sttlement");
                MobclickAgent.onEvent(this, Event_Settlement);
                break;
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {

    }
}
