package com.cloudhome.bean;

import java.io.Serializable;

/**
 * Created by bkyj-005 on 2016/10/18.
 */

public class UserPrizeBean implements Serializable {
    private String addTime;
    private String effectBegin;
    private String effectEnd;
    private String id;
    private String imgUrl;
    private String mold;
    private String mouldName;
    private String state;
    private String stateName;
    private String needMsg;
    private String getType;

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getEffectBegin() {
        return effectBegin;
    }

    public void setEffectBegin(String effectBegin) {
        this.effectBegin = effectBegin;
    }

    public String getEffectEnd() {
        return effectEnd;
    }

    public void setEffectEnd(String effectEnd) {
        this.effectEnd = effectEnd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMold() {
        return mold;
    }

    public void setMold(String mold) {
        this.mold = mold;
    }

    public String getMouldName() {
        return mouldName;
    }

    public void setMouldName(String mouldName) {
        this.mouldName = mouldName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getNeedMsg() {
        return needMsg;
    }

    public void setNeedMsg(String needMsg) {
        this.needMsg = needMsg;
    }

    public String getGetType() {
        return getType;
    }

    public void setGetType(String getType) {
        this.getType = getType;
    }
}
