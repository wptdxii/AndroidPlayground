package com.cloudhome.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.Constants;
import com.cloudhome.view.customview.CircleImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cloudhome.R.id.tv_user_name;

/**
 * 专属认证通道 Activity
 */
public class ExclusiveCertActivity extends BaseActivity {
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView((R.id.iv_avatar))
    CircleImage ivAvatar;
    @BindView(tv_user_name)
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_verify);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        tvTitle.setText(R.string.activity_exclusive_cert_title);
        rlShare.setVisibility(View.INVISIBLE);

        String userType = sp.getString(Constants.SP.Login_Type, "none");
        String avatar = sp.getString(Constants.SP.AVATAR, "");
        String trueName = sp.getString(Constants.SP.TRUE_NAME, "");
        if ("".equals(trueName) || "null".equals(trueName)) {
            tvUserName.setText(R.string.activity_exclusive_cert_user_name);
        } else {
            tvUserName.setText(trueName);
        }
        if (avatar.length() < 6) {
            if ("02".equals(userType)) {
                ivAvatar.setImageResource(R.drawable.expert_head);
            } else {
                ivAvatar.setImageResource(R.drawable.expert_head);
            }
        } else {
            if ("02".equals(userType)) {
                Glide.with(ExclusiveCertActivity.this)
                        .load(avatar)
                        //	.placeholder(R.drawable.head_fail) 占位图
                        .error(R.drawable.expert_head)
                        .crossFade()
                        .into(ivAvatar);
            } else {
                Glide.with(ExclusiveCertActivity.this)
                        .load(avatar)
                        //	.placeholder(R.drawable.head_fail)
                        .error(R.drawable.expert_head)
                        .crossFade()
                        .into(ivAvatar);
            }
        }
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.btn_cert_immediate)
    public void certImmediate() {
        VerifyMemberActivity.activityStart(this, true);
    }

    @OnClick(R.id.btn_hang_out)
    public void hangOut() {
        AllPageActivity.activityStart(this);
    }
}
