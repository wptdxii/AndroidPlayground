package com.wptdxii.playground.ui.provider;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ActionProvider;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wptdxii.playground.R;

/**
 * Created by wptdxii on 2017/11/1 0001 15:33
 * Email: wptdxii@gmail.com
 * Blog: https://github.com/wptdxii
 * Github: https://wptdxii.github.io
 */

public class MessageActionProvider extends ActionProvider {
    private Context mContext;
    private View mActionView;
    private FrameLayout flBadge;
    private TextView tvBadge;

    public MessageActionProvider(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View onCreateActionView() {
        //        mActionView = View.inflate(mContext, R.layout.provider_message_action, null);
        mActionView = LayoutInflater.from(mContext).inflate(R.layout.provider_message_action, null);
        flBadge = mActionView.findViewById(R.id.fl_badge);
        tvBadge = mActionView.findViewById(R.id.tv_badge);
        return mActionView;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mActionView.setOnClickListener(onClickListener);
    }

    public void setBadgeCount(int count) {
        flBadge.setVisibility(count >= 0 ? View.VISIBLE : View.GONE);
        tvBadge.setText(String.valueOf(count));
    }

    public void setBadgeCount(String count) {
        if (TextUtils.isDigitsOnly(count)) {
            setBadgeCount(Integer.parseInt(count));
        }
    }
}

