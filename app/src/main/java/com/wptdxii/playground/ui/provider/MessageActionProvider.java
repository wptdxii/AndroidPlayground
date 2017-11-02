package com.wptdxii.playground.ui.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
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
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.provider_message_action, null);
        return view;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        super.onPrepareSubMenu(subMenu);
        subMenu.clear();
        subMenu.add("Submenu1").setIcon(R.drawable.ic_near_me_black_24dp);
        subMenu.add("Submenu2").setIcon(R.drawable.ic_search_black_24dp);
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
