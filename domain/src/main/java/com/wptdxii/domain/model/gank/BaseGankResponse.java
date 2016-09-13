package com.wptdxii.domain.model.gank;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wptdxii on 2016/9/12 0012.
 */
public class BaseGankResponse<T> {
    @SerializedName("error")
    private boolean error;
    @SerializedName("results")
    private T results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
