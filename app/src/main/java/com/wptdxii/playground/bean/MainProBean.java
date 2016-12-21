package com.wptdxii.playground.bean;

import java.util.ArrayList;

/**
 * Created by xionghu on 2016/8/27.
 * Email：965705418@qq.com
 */
public class MainProBean {

    private String id;
    private String name;
    private String code;
    private String company;
    private String featuredesc;
    private String rate;
    private String ratepostfix;
    private String price;
    private String pricepostfix;
    private String popularityreal;
    private String url;
    private String imgurl;
    private String tags;
    private boolean train;
    private ArrayList<MainProBean> list;
    private boolean isShare;//产品是否分享
    private String recordId;


    //组合险新加
    private String apply;
    private String feature;
    private String hits;
    private String prices;
    private String productName;

    //广告位
    private String Title;
    private String bannerUrl;
    private String brief;
    private String detailUrl;
    private String flatUrl;
    private String is_share;
    private String loginFlag;
    private String logo;
    private String page;
    private String picLength;
    private String showFlg;

    private boolean isAdvertisement;



    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getCompany() {
        return company;
    }

    public void setFeaturedesc(String featuredesc) {
        this.featuredesc = featuredesc;
    }
    public String getFeaturedesc() {
        return featuredesc;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
    public String getRate() {
        return rate;
    }

    public void setRatepostfix(String ratepostfix) {
        this.ratepostfix = ratepostfix;
    }
    public String getRatepostfix() {
        return ratepostfix;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getPrice() {
        return price;
    }

    public void setPricepostfix(String pricepostfix) {
        this.pricepostfix = pricepostfix;
    }
    public String getPricepostfix() {
        return pricepostfix;
    }

    public void setPopularityreal(String popularityreal) {
        this.popularityreal = popularityreal;
    }
    public String getPopularityreal() {
        return popularityreal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    public String getImgurl() {
        return imgurl;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    public String getTags() {
        return tags;
    }

    public boolean isTrain() {
        return train;
    }

    public void setTrain(boolean train) {
        this.train = train;
    }

    public ArrayList<MainProBean> getList() {
        return list;
    }
    public void setList(ArrayList<MainProBean> list) {
        this.list = list;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getFlatUrl() {
        return flatUrl;
    }

    public void setFlatUrl(String flatUrl) {
        this.flatUrl = flatUrl;
    }

    public String getIs_share() {
        return is_share;
    }

    public void setIs_share(String is_share) {
        this.is_share = is_share;
    }

    public String getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(String loginFlag) {
        this.loginFlag = loginFlag;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPicLength() {
        return picLength;
    }

    public void setPicLength(String picLength) {
        this.picLength = picLength;
    }

    public String getShowFlg() {
        return showFlg;
    }

    public void setShowFlg(String showFlg) {
        this.showFlg = showFlg;
    }

    public boolean isAdvertisement() {
        return isAdvertisement;
    }

    public void setIsAdvertisement(boolean isAdvertisement) {
        this.isAdvertisement = isAdvertisement;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}
