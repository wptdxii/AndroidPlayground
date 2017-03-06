package com.cloudhome.bean;

import java.io.Serializable;

/**
 * Created by bkyj-005 on 2017/1/18.
 */

public class MainPopBean implements Serializable{

    /**
     * title : 红包雨
     * imgUrl : http://img.baokeyunguanjia.com/activity/popup/redrain.png
     * url : https://www.baokeyunguanjia.com/activity/activity-list/20170120-20170211/red-packet-rain/index.html
     * needLogin : 0
     * needShare : 0
     * shareTitle :
     * shareDesc :
     * shareImgUrl :
     * shareUrl :
     */

    private String title;
    private String imgUrl;
    private String url;
    private String needLogin;
    private String needShare;
    private String shareTitle;
    private String shareDesc;
    private String shareImgUrl;
    private String shareUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(String needLogin) {
        this.needLogin = needLogin;
    }

    public String getNeedShare() {
        return needShare;
    }

    public void setNeedShare(String needShare) {
        this.needShare = needShare;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareDesc() {
        return shareDesc;
    }

    public void setShareDesc(String shareDesc) {
        this.shareDesc = shareDesc;
    }

    public String getShareImgUrl() {
        return shareImgUrl;
    }

    public void setShareImgUrl(String shareImgUrl) {
        this.shareImgUrl = shareImgUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
