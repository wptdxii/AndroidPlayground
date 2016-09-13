package com.wptdxii.domain.model.gank;

import com.google.gson.annotations.SerializedName;

/**
 * 妹子图Entity
 * 当服务器返回字段不符合规范时，Gson的注解标注
 * Created by wptdxii on 2016/8/1 0001.
 */
public class GankModel {

    /**
     * "_id": "57bc5238421aa9125fa3ed70",
     * "createdAt": "2016-08-23T21:40:08.159Z",
     * "desc": "8.24",
     * "publishedAt": "2016-08-24T11:38:48.733Z",
     * "source": "chrome",
     * "type": "福利",
     * "url": "http://ww3.sinaimg.cn/large/610dc034jw1f740f701gqj20u011hgo9.jpg",
     * "used": true,
     * "who": "daimajia"
     */
    //返回字段_id可以与Bean中的id对应
    @SerializedName("_id")
    private String id;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("desc")
    private String desc;
    @SerializedName("publishedAt")
    private String publishedAt;
    @SerializedName("source")
    private String source;
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;
    @SerializedName("used")
    private boolean used;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}

