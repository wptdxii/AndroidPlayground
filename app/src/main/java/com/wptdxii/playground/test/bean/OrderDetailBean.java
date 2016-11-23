package com.wptdxii.playground.test.bean;

import java.util.List;

/**
 * Created by wptdxii on 2016/11/10 0010.
 */

public class OrderDetailBean {
    private String errorCode;
    private String errorMsg;
    private String status;


    private ItemBean orderTitle;
    private List<ItemBean> order;
    private List<ItemBean> policySum;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ItemBean getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(ItemBean orderTitle) {
        this.orderTitle = orderTitle;
    }

    public List<ItemBean> getOrder() {
        return order;
    }

    public void setOrder(List<ItemBean> order) {
        this.order = order;
    }

    public List<ItemBean> getPolicySum() {
        return policySum;
    }

    public void setPolicySum(List<ItemBean> policySum) {
        this.policySum = policySum;
    }

    public List<ItemBean> getPersonSum() {
        return personSum;
    }

    public void setPersonSum(List<ItemBean> personSum) {
        this.personSum = personSum;
    }

    private List<ItemBean> personSum;

    public static class ItemBean {
        private String itemKey;
        private String itemValue;

        public ItemBean(String itemKey, String itemValue) {
            this.itemKey = itemKey;
            this.itemValue = itemValue;
        }

        public String getItemKey() {
            return itemKey;
        }

        public String getItemValue() {
            return itemValue;
        }
    }

}
