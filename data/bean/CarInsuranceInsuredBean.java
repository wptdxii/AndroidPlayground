package com.cloudhome.bean;

import java.util.List;

/**
 * Created by wptdxii on 2017/1/13 0013.
 */

public class CarInsuranceInsuredBean {

    /**
     * status : true
     * errcode : 0
     * errmsg : 获取车险询价接口列表成功
     * data : [{"name":"中国人民保险","url":"http://www.epicc.com.cn/ecar/proposal/branchProposal?ID=5101584398","img":"images/company/plcc.png","is_show":"01","alert_msg":"","region":"全国","content":"透明投保 理赔容易"},{"name":"阳光保险95510","url":"http://mcar.youbaoplus.com/car/baseInformation/baseInformation.html?agentCode=W02400658&spsource=NetH5&sellerId=&partnerOrderNo=01393120170113133836AA&accountType=2&accountNo=JD1676847","img":"images/company/200.png","alert_msg":"","is_show":"01","region":"全国","content":"价格实惠 快速理赔"},{"name":"中国平安保险","url":"http://u.pingan.com/upingan/selfweb/insureOfferCustomer.html?mediasource=sc03-zzb-10000-bjygj&marketType=01&partnerCoe=1000235130","img":"/images/company/182.png","is_show":"01","alert_msg":"","region":"四川","content":"精准报价 快捷续保"},{"name":"平安车险","url":"http://u.pingan.com/u/26VvYv2","img":"images/company/182.png","is_show":"01","alert_msg":"","region":"北京","content":"精准报价 快捷续保"}]
     */

    private String status;
    private String errcode;
    private String errmsg;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : 中国人民保险
         * url : http://www.epicc.com.cn/ecar/proposal/branchProposal?ID=5101584398
         * img : images/company/plcc.png
         * is_show : 01
         * alert_msg :
         * region : 全国
         * content : 透明投保 理赔容易
         */

        private String name;
        private String url;
        private String img;
        private String is_show;
        private String alert_msg;
        private String region;
        private String content;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getIs_show() {
            return is_show;
        }

        public void setIs_show(String is_show) {
            this.is_show = is_show;
        }

        public String getAlert_msg() {
            return alert_msg;
        }

        public void setAlert_msg(String alert_msg) {
            this.alert_msg = alert_msg;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
