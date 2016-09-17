package com.wptdxii.androidpractice.model;

import com.wptdxii.uiframework.base.BaseActivity;

/**
 * Created by wptdxii on 2016/8/2 0002.
 */
public class Module {
    private String modelName;
    private Class<? extends BaseActivity> targetActivity;

    public Module() {
    }

    public Module(String modelName, Class<? extends BaseActivity> targetActivity) {
        this.modelName = modelName;
        this.targetActivity = targetActivity;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Class<? extends BaseActivity> getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class<? extends BaseActivity> targetActivity) {
        this.targetActivity = targetActivity;
    }
}
