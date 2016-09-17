package com.wptdxii.uikit.widget.bottomnavigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wptdxii.uikit.R;

import java.util.ArrayList;

/**
 * Created by wptdxii on 2016/8/31 0031.
 */
public class TabLayout extends LinearLayout implements View.OnClickListener {
    private ArrayList<Tab> tabs;
    private OnTabClickListener listener;
    private int tabCount;
    private View selectedView;

    public TabLayout(Context context) {
        super(context);
        iniView();
    }


    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView();
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView();
    }

    private void iniView() {
        setOrientation(HORIZONTAL);
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        this.listener = listener;
    }

    public void iniData(ArrayList<Tab> tabs) {
        this.tabs = tabs;

        if (tabs != null && tabs.size() > 0) {
            tabCount = tabs.size();
            TabView tabView;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.weight = 1;

            for (int i = 0; i < tabs.size(); i++) {
                Tab tab = tabs.get(i);
                tabView = new TabView(getContext());
                tabView.setTag(tab);
                tabView.iniData(tab);
                tabView.setOnClickListener(this);
                addView(tabView, layoutParams);
            }
        } else {
            throw new IllegalArgumentException("tabs can't be empty");
        }

    }

    public void setCurrentTab(int i) {
        if (i >= 0 && i < tabCount) {
            View view = getChildAt(i);
            Tab tab = (Tab) view.getTag();
            if (selectedView != view) {
                view.setSelected(true);
                if (selectedView != null) {
                    selectedView.setSelected(false);
                }
                selectedView = view;
                listener.onTabClick(tab, false);
            } else {
                listener.onTabClick(tab, true);
            }
        }
    }

    public void onBadgeChanged(int i, int badgeCount) {
        if (i >= 0 && i < tabCount) {
            TabView tabView = (TabView) getChildAt(i);
            tabView.onBadgeChanged(badgeCount);
        }
    }

    @Override
    public void onClick(View view) {
        Tab tab = (Tab) view.getTag();
        int index = tabs.indexOf(tab);
        setCurrentTab(index);
    }


    public interface OnTabClickListener {
        /**
         * 当前tabview已被选中，返回true
         * 未被选中，返回false
         * @param tab
         * @param isSelected
         */
        void onTabClick(Tab tab, boolean isSelected);
    }

    public class TabView extends LinearLayout {
        private ImageView mTabImg;
        private TextView mTabLabel;

        public TabView(Context context) {
            super(context);
            initView(context);
        }


        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initView(context);
        }

        public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView(context);
        }

        private void initView(Context context) {
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            LayoutInflater.from(context).inflate(R.layout.widget_tab_view, this, true);
            mTabImg = (ImageView) findViewById(R.id.tabImg);
            mTabLabel = (TextView) findViewById(R.id.tabLabel);
        }

        public void iniData(Tab tab) {
            mTabImg.setBackgroundResource(tab.imgResId);
            mTabLabel.setText(tab.labelResId);
        }

        public void onBadgeChanged(int badgeCount) {
            //TODO notify new message, change the badgeView
        }

    }

    public static class Tab {
        public int imgResId;
        public int labelResId;
        public int badgeCount;
        public int menuResId;
        public Class<? extends ITabFragment> targetFragmentClz;

        public Tab(int imgResId, int labelResId) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
        }

        public Tab(int imgResId, int labelResId, int badgeCount) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.badgeCount = badgeCount;
        }

        public Tab(int imgResId, int labelResId, int badgeCount, Class<? extends ITabFragment> targetFragmentClz) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.badgeCount = badgeCount;
            this.targetFragmentClz = targetFragmentClz;
        }

        public Tab(int imgResId, int labelResId, Class<? extends ITabFragment> targetFragmentClz, int menuResId) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.targetFragmentClz = targetFragmentClz;
            this.menuResId = menuResId;
        }

        public Tab(int imgResId, int labelResId, Class<? extends ITabFragment> targetFragmentClz) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.targetFragmentClz = targetFragmentClz;
        }

        public Tab(int imgResId, int labelResId, int badgeCount, int menuResId, Class<? extends ITabFragment> targetFragmentClz) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.badgeCount = badgeCount;
            this.menuResId = menuResId;
            this.targetFragmentClz = targetFragmentClz;
        }
    }

}

