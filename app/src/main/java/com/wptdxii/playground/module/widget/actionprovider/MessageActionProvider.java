package com.wptdxii.playground.module.widget.actionprovider;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wptdxii.playground.R;

/**
 * Created by wptdxii on 2017/11/1 0001 15:33
 * Email: wptdxii@gmail.com
 * Blog: https://github.com/wptdxii
 * Github: https://wptdxii.github.io
 */

public class MessageActionProvider extends ActionProvider {
    private View actionView;
    private TextView tvBadge;

    public MessageActionProvider(Context context) {
        super(context);
        actionView = View.inflate(context, R.layout.provider_message_action, null);
        tvBadge = actionView.findViewById(R.id.tv_badge);
    }

    @Override
    public View onCreateActionView() {
        return actionView;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        actionView.setOnClickListener(onClickListener);
    }

    public void setMessageCount(int count) {
        tvBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        tvBadge.setText(String.valueOf(count));
    }

    public void setMessageCount(String count) {
        if (TextUtils.isDigitsOnly(count)) {
            setMessageCount(Integer.parseInt(count));
        }
    }
}