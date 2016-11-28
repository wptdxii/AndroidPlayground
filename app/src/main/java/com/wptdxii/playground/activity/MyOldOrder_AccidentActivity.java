package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

public class MyOldOrder_AccidentActivity extends BaseActivity implements OnClickListener {


    private RelativeLayout p_p_rel1, p_p_rel2,iv_back;
    private RelativeLayout rl_right;
    public  static String ordertype;
    private TextView tv_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myoldorder_accident);

        Intent intent = getIntent();
        ordertype = intent.getStringExtra("ordertype");

        init();

    }



    void init() {


        iv_back  = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        p_p_rel1 = (RelativeLayout) findViewById(R.id.p_p_rel1);
        p_p_rel2 = (RelativeLayout) findViewById(R.id.p_p_rel2);
        tv_text  = (TextView) findViewById(R.id.tv_text);

        tv_text.setText(ordertype);

        iv_back.setOnClickListener(this);
        p_p_rel1.setOnClickListener(this);
        p_p_rel2.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {

            case R.id.iv_back:

                finish();

                break;

            case R.id.p_p_rel1:

                 intent = new Intent();

                intent.setClass(MyOldOrder_AccidentActivity.this, TobepaidActivity.class);

                MyOldOrder_AccidentActivity.this.startActivity(intent);

                break;

            case R.id.p_p_rel2:


                intent = new Intent();
                intent.setClass(MyOldOrder_AccidentActivity.this, HavebeenpaidActivity.class);

                MyOldOrder_AccidentActivity.this.startActivity(intent);
                break;
        }

    }


}
