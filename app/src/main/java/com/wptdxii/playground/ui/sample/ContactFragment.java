package com.wptdxii.playground.ui.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wptdxii.playground.R;
import com.wptdxii.uiframework.base.BaseFragment;

/**
 * Created by wptdxii on 2016/9/1 0001.
 */
public class ContactFragment extends BaseFragment {
    private static final String ARGUMENTS = "arguments";
    private TextView mTextView;
    private String content;
    private int param = -1;

    /**
     * 需要向Fragment传递数据时使用该种方式创建Fragment对象
     *
     * @param content
     * @return
     */
    public static ContactFragment newInstance(String content) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENTS, content);
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            content = bundle.getString(ARGUMENTS);
        }
        enableLazyLoad(true);
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        return view;
    }

    @Override
    protected void initView(View view) {
        mTextView = (TextView) view.findViewById(R.id.contact);
        mTextView.setText("Page:");
    }

    @Override
    protected void initData() {
        mTextView.append(content);
    }
}
