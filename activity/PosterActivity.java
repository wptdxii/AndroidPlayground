package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.umeng.socialize.UMShareAPI;

public class PosterActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_text;
    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private ImageView iv_poster;
    private ImageView iv_wechat;
    private ImageView iv_wechat_circle;
    private ImageView iv_qq;
    private String imageUrl="";
    private BaseUMShare share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        Intent intent=getIntent();
        imageUrl=intent.getStringExtra("imageUrl");
//        imageUrl= "http://www.baokeyunguanjia.com/web/active/invite_friend/images/wx_img1.jpg";
        initView();
        initData();
    }


    private void initView() {
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("我的海报");
        iv_back= (RelativeLayout) findViewById(R.id.iv_back);
        rl_right= (RelativeLayout) findViewById(R.id.rl_right);
        iv_poster= (ImageView) findViewById(R.id.iv_poster);
        iv_wechat= (ImageView) findViewById(R.id.iv_wechat);
        iv_wechat_circle= (ImageView) findViewById(R.id.iv_wechat_circle);
        iv_qq= (ImageView) findViewById(R.id.iv_qq);
    }

    private void initData() {
        iv_back.setOnClickListener(this);
        rl_right.setOnClickListener(this);
        iv_wechat.setOnClickListener(this);
        iv_wechat_circle.setOnClickListener(this);
        iv_qq.setOnClickListener(this);
        Glide.with(this)
                .load(imageUrl)
                .into(iv_poster);

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_right:
                if(!TextUtils.isEmpty(imageUrl)){
                    BaseUMShare.sharePlatformsPicture(PosterActivity.this,imageUrl);
                }
                break;
            case R.id.iv_poster:
                break;
            case R.id.iv_wechat:
                if(!TextUtils.isEmpty(imageUrl)){
                    BaseUMShare.sharePicture(1,PosterActivity.this,imageUrl);
                }
                break;
            case R.id.iv_wechat_circle:
                if(!TextUtils.isEmpty(imageUrl)){
                    BaseUMShare.sharePicture(2,PosterActivity.this,imageUrl);
                }
                break;
            case R.id.iv_qq:
                if(!TextUtils.isEmpty(imageUrl)){
                    BaseUMShare.sharePicture(3,PosterActivity.this,imageUrl);
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

}
