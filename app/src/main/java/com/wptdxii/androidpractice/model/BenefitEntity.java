package com.wptdxii.androidpractice.model;

import com.google.gson.annotations.SerializedName;

/**
 * 妹子图Entity
 * 当服务器返回字段不符合规范时，Gson的注解标注
 * Created by wptdxii on 2016/8/1 0001.
 */
public class BenefitEntity {

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
    public String id;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("desc")
    public String desc;
    @SerializedName("publishedAt")
    public String publishedAt;
    @SerializedName("source")
    public String source;
    @SerializedName("type")
    public String type;
    @SerializedName("url")
    public String url;
    @SerializedName("used")
    public boolean used;
}

