package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;
public class MyLoginFragment extends BaseFragment {

    private final String mPageName = "MyLoginFragment";
    private View mView;
    private TextView tab_login_but;
    private TextView tab_reg_but;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {

            initView(inflater, container);

            initEvent();
        }
        return mView;
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.my_login, container, false);
        tab_login_but = (TextView) mView.findViewById(R.id.tab_login_but);
        tab_reg_but = (TextView) mView.findViewById(R.id.tab_reg_but);

    }

    void initEvent() {

        tab_login_but.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();
                intent.setClass(getActivity(),
                        LoginActivity.class);
                getActivity().startActivity(intent);


            }
        });
        tab_reg_but.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(getActivity(),
                        RegisterActivity.class);
                getActivity().startActivity(intent);


            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName); // 统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

}
