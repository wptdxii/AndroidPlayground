package com.wptdxii.playground.bean;

import java.util.List;

/**
 * Created by wptdxii on 2016/9/23 0023.
 */

public class SplashAdBean {

    /**
     * errcode : 0
     * data : [{"brief":"第一帧开屏广告的描述","img":"http://123.57.37.193:8080/gateway/images/pre_ad_001.jpg","isShare":1,"share_img":"http://123.57.37.193:8080/gateway/images/pre_share_001.jpg","title":"第一帧开屏广告","url":"http://www.baidu.com"},{"brief":"第二帧开屏广告的描述","img":"http://123.57.37.193:8080/gateway/images/pre_ad_002.jpg","isShare":1,"share_img":"http://123.57.37.193:8080/gateway/images/pre_share_002.jpg","title":"第二帧开屏广告","url":"http://www.baidu.com"},{"brief":"第三帧开屏广告的描述","img":"http://123.57.37.193:8080/gateway/images/pre_ad_003.jpg","isShare":1,"share_img":"http://123.57.37.193:8080/gateway/images/pre_share_003.jpg","title":"第三帧开屏广告","url":"http://www.baidu.com"},{"brief":"第四帧开屏广告的描述","img":"http://123.57.37.193:8080/gateway/images/pre_ad_004.jpg","isShare":1,"share_img":"http://123.57.37.193:8080/gateway/images/pre_share_004.jpg","title":"第四帧开屏广告","url":"http://www.baidu.com"}]
     * errmsg : success
     * status : true
     */

    private int errcode;
    private String errmsg;
    private boolean status;
    private String isShow;
    /**
     * brief : 第一帧开屏广告的描述
     * img : http://123.57.37.193:8080/gateway/images/pre_ad_001.jpg
     * isShare : 1
     * share_img : http://123.57.37.193:8080/gateway/images/pre_share_001.jpg
     * title : 第一帧开屏广告
     * url : http://www.baidu.com
     */

    private List<DataBean> data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String brief;
        private String img;
        private int isShare;
        private String share_img;
        private String title;
        private String url;

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getIsShare() {
            return isShare;
        }

        public void setIsShare(int isShare) {
            this.isShare = isShare;
        }

        public String getShare_img() {
            return share_img;
        }

        public void setShare_img(String share_img) {
            this.share_img = share_img;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
