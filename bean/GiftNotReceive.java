package com.cloudhome.bean;

import java.io.Serializable;

/**
 * Created by bkyj-005 on 2016/6/28.
 */
public class GiftNotReceive implements Serializable {
    private String product_icon;
    private String product_id;
    private String product_name;
    private String share_desc;
    private String share_icon;
    private String share_title;
    private String share_url;
    private String valid_time_begin;
    private String valid_time_end;

    public String getProduct_icon() {
        return product_icon;
    }

    public void setProduct_icon(String product_icon) {
        this.product_icon = product_icon;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getShare_desc() {
        return share_desc;
    }

    public void setShare_desc(String share_desc) {
        this.share_desc = share_desc;
    }

    public String getShare_icon() {
        return share_icon;
    }

    public void setShare_icon(String share_icon) {
        this.share_icon = share_icon;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getValid_time_begin() {
        return valid_time_begin;
    }

    public void setValid_time_begin(String valid_time_begin) {
        this.valid_time_begin = valid_time_begin;
    }

    public String getValid_time_end() {
        return valid_time_end;
    }

    public void setValid_time_end(String valid_time_end) {
        this.valid_time_end = valid_time_end;
    }
}
