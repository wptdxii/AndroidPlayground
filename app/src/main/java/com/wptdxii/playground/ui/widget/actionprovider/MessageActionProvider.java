package com.wptdxii.playground.ui.widget.actionprovider;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.text.TextUtils;
import android.view.SubMenu;
import android.view.View;
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
    private Context context;
    private View actionView;
    private FrameLayout flBadge;
    private TextView tvBadge;

    public MessageActionProvider(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 当 Toolbar 作为应用栏使用时，创建菜单会回调 Activity 中的 onCreateOptionsMenu() 方法，
     * 在该方法中获去到的 ActionProvider 实例并没有被系统回调 onCreateActionView() 方法，所以在
     * onCreateActionView() 获取的布局控件都为 null，这时在 Activity 的 onCreateOptionsMenu()
     * 方法中使用调用 ActionProvider 中获取到的子控件也都为 null
     * @return
     */
    @Override
    public View onCreateActionView() {
        actionView = View.inflate(context, R.layout.provider_message_action, null);
        flBadge = actionView.findViewById(R.id.fl_badge);
        tvBadge = actionView.findViewById(R.id.tv_badge);
        return actionView;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        actionView.setOnClickListener(onClickListener);
    }

    public void setBadgeCount(int count) {
        flBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        tvBadge.setText(String.valueOf(count));
    }

    public void setBadgeCount(String count) {
        if (TextUtils.isDigitsOnly(count)) {
            setBadgeCount(Integer.parseInt(count));
        }
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        super.onPrepareSubMenu(subMenu);
        subMenu.add("sub1");
        subMenu.add("sub2");
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}

