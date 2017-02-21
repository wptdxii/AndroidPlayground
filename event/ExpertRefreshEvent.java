package com.cloudhome.event;

/**
 * Created by wptdxii on 2017/1/12 0012.
 */

public class ExpertRefreshEvent {
    private String mThumbUpNum;

    public ExpertRefreshEvent(String thumbUpNum) {
        this.mThumbUpNum = thumbUpNum;
    }

    public String getThumbUpNum() {
        return mThumbUpNum;
    }
}
