package com.wptdxii.androidpractice.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wptdxii.androidpractice.R;
import com.wptdxii.uiframework.base.BaseFragment;
import com.wptdxii.uikit.widget.bottomnavigation.ITabFragment;

/**
 * Created by wptdxii on 2016/9/1 0001.
 */
public class MsgFragment extends BaseFragment implements ITabFragment{
    private static final String ARGUMENTS = "arguments";
    private TextView mTextView;
    private String content;
    /**
     * 需要向Fragment传递数据时使用该种方式创建Fragment对象
     *
     * @param content
     * @return
     */
    public static MsgFragment newInstance(String content) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENTS, content);
        MsgFragment fragment = new MsgFragment();
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
        View view = inflater.inflate(R.layout.fragment_msg, container, false);
        return view;
    }

    @Override
    protected void initView(View view) {
        mTextView = (TextView) view.findViewById(R.id.msg);
        mTextView.setText("Page:");
    }

    @Override
    protected void initData() {

        mTextView.append(content);
    }

    @Override
    public void onMenuItemClick() {
        
    }

    @Override
    public BaseFragment getFragment() {
        return null;
    }
}
