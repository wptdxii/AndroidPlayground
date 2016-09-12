package com.wptdxii.domain.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wptdxii on 2016/9/12 0012.
 */
public class BaseResponse<T> {
    @SerializedName("error")
    public boolean error;
    @SerializedName("results")
    public T results;
}
