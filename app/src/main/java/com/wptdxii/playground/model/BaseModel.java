package com.wptdxii.playground.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wptdxii on 2016/7/31 0031.
 */
public class BaseModel<T> {
    @SerializedName("error")
    public boolean error;
    @SerializedName("results")
    public T results;
}
