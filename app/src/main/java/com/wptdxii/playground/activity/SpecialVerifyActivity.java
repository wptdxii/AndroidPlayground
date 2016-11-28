package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.CircleImage;

public class SpecialVerifyActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    private Button btn_verify_now;
    private Button btn_hangout;
    private CircleImage civ_head;
    private TextView tv_user_name;
    private String avatar;
    private String truename;
    private String user_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_verify);
        initView();
        initEvent();
    }

    private void initView() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        btn_verify_now= (Button) findViewById(R.id.btn_verify_now);
        btn_hangout= (Button) findViewById(R.id.btn_hangout);
        civ_head= (CircleImage) findViewById(R.id.civ_head);
        tv_user_name= (TextView) findViewById(R.id.tv_user_name);
        top_title.setText("专属认证通道");
        iv_right.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(this);
        btn_verify_now.setOnClickListener(this);
        btn_hangout.setOnClickListener(this);
    }

    private void initEvent() {
        user_type = sp.getString("Login_TYPE", "none");
        avatar = sp.getString("avatar", "");
        truename = sp.getString("truename", "");
        if (truename.equals("") || truename.equals("null")) {
            tv_user_name.setText("保险人");
        } else {
            tv_user_name.setText(truename);
        }
        if (avatar.length() < 6) {
            if (user_type.equals("02")) {
                civ_head.setImageResource(R.drawable.expert_head);
            } else {
                civ_head.setImageResource(R.drawable.expert_head);
            }
        } else {
            if (user_type.equals("02")) {
                Glide.with(SpecialVerifyActivity.this)
                        .load(avatar)
                        //	.placeholder(R.drawable.head_fail) 占位图
                        .error(R.drawable.expert_head)
                        .crossFade()
                        .into(civ_head);
            } else {
                Glide.with(SpecialVerifyActivity.this)
                        .load(avatar)
                        //	.placeholder(R.drawable.head_fail)
                        .error(R.drawable.expert_head)
                        .crossFade()
                        .into(civ_head);
            }
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent=null;
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_verify_now:
                intent=new Intent(SpecialVerifyActivity.this,VerifyMemberActivity.class);
                intent.putExtra("isFromRegister",true);
                startActivity(intent);
                break;
            case R.id.btn_hangout:
                intent=new Intent(SpecialVerifyActivity.this,AllPageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
