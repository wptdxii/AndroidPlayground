package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.view.customview.CircleImage;
import com.cloudhome.utils.Common;
import com.cloudhome.view.customview.MyGridView;
import com.cloudhome.view.customview.QianDaoDialog;
import com.cloudhome.view.iosalertview.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class QianDaoActivity extends BaseActivity implements View.OnClickListener{
    private MyGridView qiandao_gridview;
    private CustomDialog dialog;
    private RelativeLayout rl_qiandao_instruction;
    private ImageView iv_my_gift_ticket;
    private RelativeLayout iv_back;
    private TextView tv_name;
    private CircleImage iv_head_circle;
    private TextView tv_my_score;
    private TextView tv_num;

    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> infoMap;
    private QiandaoAdapter adapter;
    private TextView tv_qiandao;
    private int signTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_qian_dao);
        Intent intent=getIntent();
        infoMap= (HashMap<String, String>) intent.getSerializableExtra("infoMap");
        list= (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("list");
        initView();
    }

    private void initView() {
        String code=infoMap.get("code").toString();

        if(code.equals("0")){
            String note=infoMap.get("note").toString();
            String imgUrl=infoMap.get("imgUrl").toString();
            showSuccessDialog(note,imgUrl);
        }

        qiandao_gridview= (MyGridView) findViewById(R.id.qiandao_gridview);
        rl_qiandao_instruction= (RelativeLayout) findViewById(R.id.rl_qiandao_instruction);
        iv_my_gift_ticket= (ImageView) findViewById(R.id.iv_my_gift_ticket);
        iv_back= (RelativeLayout) findViewById(R.id.iv_back);
        tv_name= (TextView) findViewById(R.id.tv_name);
        iv_head_circle= (CircleImage) findViewById(R.id.iv_head_circle);
        tv_my_score= (TextView) findViewById(R.id.tv_my_score);
        tv_num=(TextView) findViewById(R.id.tv_num);
        tv_qiandao=(TextView) findViewById(R.id.tv_qiandao);
        tv_qiandao.setText("今日已签到");

        initHeadName();
        signTime=Integer.parseInt(infoMap.get("signCount"));
        tv_my_score.setText(infoMap.get("score"));
        tv_num.setText(infoMap.get("signCount"));
        rl_qiandao_instruction.setOnClickListener(this);
        iv_my_gift_ticket.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        adapter=new QiandaoAdapter();
        qiandao_gridview.setAdapter(adapter);
    }

    private void initHeadName() {
        String avatar=sp.getString("avatar", "");
        String user_type = sp.getString("Login_TYPE", "none");
        String truename = sp.getString("truename", "");
        if (avatar.length() < 6) {
            if (user_type.equals("02")) {
                iv_head_circle.setImageResource(R.drawable.expert_head);
            } else {
                iv_head_circle.setImageResource(R.drawable.expert_head);
            }
        } else {
            if (user_type.equals("02")) {
                Glide.with(QianDaoActivity.this)
                        .load(avatar)
                        //	.placeholder(R.drawable.head_fail) 占位图
                        .error(R.drawable.expert_head)
                        .crossFade()
                        .into(iv_head_circle);
            } else {
                Glide.with(QianDaoActivity.this)
                        .load(avatar)
                        //	.placeholder(R.drawable.head_fail)
                        .error(R.drawable.expert_head)
                        .crossFade()
                        .into(iv_head_circle);
            }
        }
        if (truename.equals("") || truename.equals("null")) {
            tv_name.setText("保险人");
        } else {
            tv_name.setText(truename);
        }
    }

    public void showSuccessDialog(String note,String imgUrl) {
        View contentView = View.inflate(this,R.layout.dialog_qiandao_success,null);
        ImageView iv_qiandao= (ImageView) contentView.findViewById(R.id.iv_qiandao);
        TextView tv_desc= (TextView) contentView.findViewById(R.id.tv_desc);
        RelativeLayout rl_close= (RelativeLayout) contentView.findViewById(R.id.rl_close);

        tv_desc.setText(note);
        Glide.with(QianDaoActivity.this)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .error(R.drawable.blue_default_qiandao)
                .crossFade()
                .into(iv_qiandao);
        final QianDaoDialog builder = new QianDaoDialog(this,contentView, Common.dip2px(this,290),
                Common.dip2px(this,260),R.style.qiandao_dialog);
        builder.show();
        rl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_qiandao_instruction:
                showInstructionDialog();
                break;
            case R.id.iv_my_gift_ticket:
                Intent intent=new Intent(QianDaoActivity.this,MyCouponsActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showInstructionDialog() {
        View contentView = View.inflate(this,R.layout.dialog_qiandao_instruction,null);
        QianDaoDialog builder = new QianDaoDialog(this,contentView, Common.dip2px(this,290),
                Common.dip2px(this,320),R.style.qiandao_dialog);
        builder.show();
    }

    class QiandaoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            QiandaoHolder holder = null;
            if (convertView == null) {
                holder = new QiandaoHolder();
                convertView = LayoutInflater.from(QianDaoActivity.this).inflate(R.layout.item_qiandao_map,null);
                holder.title = (TextView) convertView.findViewById(R.id.tv_info);
                holder.image = (ImageView) convertView.findViewById(R.id.iv_qiandao);
                holder.iv_already_qiandao= (ImageView) convertView.findViewById(R.id.iv_already_qiandao);
                convertView.setTag(holder);
            } else {
                holder = (QiandaoHolder) convertView.getTag();
            }
            if(position < signTime) {
                holder.iv_already_qiandao.setVisibility(View.VISIBLE);
                holder.title.setTextColor(getResources().getColor(R.color.title_blue));
            }else{
                holder.iv_already_qiandao.setVisibility(View.INVISIBLE);
                holder.title.setTextColor(getResources().getColor(R.color.color9));
            }

            holder.title.setText(list.get(position).get("mouldName"));
            String url=list.get(position).get("imgUrl");
            Glide.with(QianDaoActivity.this)
                    .load(url)
                    .centerCrop()
                    .error(R.drawable.sign_day)
                    .crossFade()
                    .into(holder.image);
            return convertView;
        }

        public class QiandaoHolder {

            public	TextView title;
            public	ImageView image;
            public	ImageView iv_already_qiandao;
        }

    }
}
