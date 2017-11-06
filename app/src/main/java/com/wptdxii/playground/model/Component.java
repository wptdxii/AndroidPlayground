package com.wptdxii.playground.model;

import com.wptdxii.uiframework.base.BaseActivity;

/**
 * Created by wptdxii on 2017/11/6 0006 14:13
 * Email: wptdxii@gmail.com
 * Blog: https://github.com/wptdxii
 * Github: https://wptdxii.github.io
 */

public class Component {
    private String component;
    private Class<? extends BaseActivity> targetActivity;

    public Component(String component, Class<? extends BaseActivity> targetActivity) {
        this.component = component;
        this.targetActivity = targetActivity;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Class<? extends BaseActivity> getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class<? extends BaseActivity> targetActivity) {
        this.targetActivity = targetActivity;
    }
}
