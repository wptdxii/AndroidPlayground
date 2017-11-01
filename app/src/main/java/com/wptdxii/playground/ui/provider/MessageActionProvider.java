package com.wptdxii.playground.ui.provider;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.SubMenu;
import android.view.View;

import com.wptdxii.playground.R;

/**
 * Created by wptdxii on 2017/11/1 0001 15:33
 * Email: wptdxii@gmail.com
 * Blog: https://github.com/wptdxii
 * Github: https://wptdxii.github.io
 */

public class MessageActionProvider extends ActionProvider {
    private Context mContext;

    public MessageActionProvider(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View onCreateActionView() {
        View view = View.inflate(mContext, R.layout.provider_message_action, null);
        return null;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.add("Test1").setIcon(R.drawable.ic_arrow_back_white_24dp);
        subMenu.add("Test2").setIcon(R.drawable.ic_logo);
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
