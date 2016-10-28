package com.wptdxii.playground.activity;

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
import com.cloudhome.utils.CircleImage;
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
    private ImageView iv_get_gift_insurance;
    private TextView tv_num;

    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> infoMap;

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
        qiandao_gridview= (MyGridView) findViewById(R.id.qiandao_gridview);
        rl_qiandao_instruction= (RelativeLayout) findViewById(R.id.rl_qiandao_instruction);
        iv_my_gift_ticket= (ImageView) findViewById(R.id.iv_my_gift_ticket);
        iv_back= (RelativeLayout) findViewById(R.id.iv_back);
        tv_name= (TextView) findViewById(R.id.tv_name);
        iv_head_circle= (CircleImage) findViewById(R.id.iv_head_circle);
        tv_my_score= (TextView) findViewById(R.id.tv_my_score);
        iv_get_gift_insurance=(ImageView) findViewById(R.id.iv_get_gift_insurance);
        tv_num=(TextView) findViewById(R.id.tv_num);

        Glide.with(QianDaoActivity.this)
                .load(infoMap.get("avatarUrl"))
                .centerCrop()
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .error(R.drawable.white)
                .crossFade()
                .into(iv_head_circle);
        tv_name.setText(infoMap.get("name"));
        tv_my_score.setText(infoMap.get("score"));
        tv_num.setText(infoMap.get("signCount"));
        rl_qiandao_instruction.setOnClickListener(this);
        iv_my_gift_ticket.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        qiandao_gridview.setAdapter(new QiandaoAdapter());
    }

    public void showSuccessDialog() {
        View contentView = View.inflate(this,R.layout.dialog_qiandao_success,null);
        QianDaoDialog builder = new QianDaoDialog(this,contentView, Common.dip2px(this,292),
                Common.dip2px(this,314),R.style.qiandao_dialog);
        builder.show();
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
                Common.dip2px(this,290),R.style.qiandao_dialog);
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

            holder.title.setText(list.get(position).get("mouldName"));
            String url=list.get(position).get("imgUrl");
            Glide.with(QianDaoActivity.this)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.white)  //占位图 图片正在加载
                    .error(R.drawable.white)
                    .crossFade()
                    .into(holder.image);
            return convertView;
        }

        public class QiandaoHolder {

            public TextView title;
            public ImageView image;
            public ImageView iv_already_qiandao;
        }

    }
}
