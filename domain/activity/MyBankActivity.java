package com.cloudhome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
public class MyBankActivity extends BaseActivity {


    private TextView bank_name_text;
    private TextView bank_num_text;
    private TextView mybank_edit;
    private String bank_name;
    private String bank_no;
    private ImageView mybank_back;

    private RelativeLayout rl_bankcard_account_name;
    private String user_state;//用户状态 00表示已认证   02表示认证中    01表示未认证
    private String user_type = "";//用户类型 00表示A级用户   02表示C端用户  01表示B用户
    private String truename;
    private boolean isAccountShow = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.mybank);


        bank_name = sp2.getString("bank_name", "");
        bank_no = sp2.getString("bank_no", "");

        user_type = sp.getString("Login_TYPE", "none");
        user_state = sp.getString("Login_CERT", "none");
        truename = sp.getString("truename", "");


        Log.d("bank_name", bank_name);

        init();
        initEvent();

    }

    void init() {

        mybank_back = (ImageView) findViewById(R.id.mybank_back);
        bank_name_text = (TextView) findViewById(R.id.bank_name);
        bank_num_text = (TextView) findViewById(R.id.bank_num);
        mybank_edit = (TextView) findViewById(R.id.mybank_edit);

        rl_bankcard_account_name = (RelativeLayout) findViewById(R.id.rl_bankcard_account_name);
        /*if(user_type.equals("02")&&"".equals(truename)){
			rl_bankcard_account_name.setVisibility(View.VISIBLE);
			isAccountShow=true;
		}
		if(user_type.equals("01")&&user_state.equals("01")&&"".equals(truename)){
			rl_bankcard_account_name.setVisibility(View.VISIBLE);
			isAccountShow=true;
		}*/

    }

    void initEvent() {
        mybank_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        bank_name_text.setText(bank_name);
        bank_num_text.setText(bank_no);
        mybank_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                Intent intent = new Intent();

                intent.putExtra("bank_name", bank_name);

                intent.putExtra("bank_num", bank_no);

                intent.setClass(MyBankActivity.this, BankCardEditActivity.class);
                MyBankActivity.this.startActivity(intent);

                finish();

            }
        });

    }



}
