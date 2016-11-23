package com.wptdxii.playground.test.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderListCarBean implements Serializable {

	private String monthDivider;//分类的名字
	private String ordertype;
	private String orderno;
	private String status;
	private String productname;
	private String productimageurl;
	private String orderDetailUrl;
	private String actualTaxFee;
	private String taxValue;
	private String ordercreatetime;
	private String insureperiod;
	private String holdername;
	private String wageno;
	private String id;
	private String moneys;
	private String fycs;


	private String isshowtuiguangfei;
	private String productname2;
	private String productimageurl2;
	private String orderDetailUrl2;
	private String holdername2;
	private String insureperiod2;
	private String taxValue2;
	private int productsize;


	private ArrayList<OrderListCarBean> list;

	public String getMonthDivider() {
		return monthDivider;
	}

	public void setMonthDivider(String monthDivider) {
		this.monthDivider = monthDivider;
	}

	public String getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getProductimageurl() {
		return productimageurl;
	}

	public void setProductimageurl(String productimageurl) {
		this.productimageurl = productimageurl;
	}

	public String getOrderDetailUrl() {
		return orderDetailUrl;
	}

	public void setOrderDetailUrl(String orderDetailUrl) {
		this.orderDetailUrl = orderDetailUrl;
	}

	public String getActualTaxFee() {
		return actualTaxFee;
	}

	public void setActualTaxFee(String actualTaxFee) {
		this.actualTaxFee = actualTaxFee;
	}

	public String getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(String taxValue) {
		this.taxValue = taxValue;
	}

	public String getOrdercreatetime() {
		return ordercreatetime;
	}

	public void setOrdercreatetime(String ordercreatetime) {
		this.ordercreatetime = ordercreatetime;
	}

	public String getInsureperiod() {
		return insureperiod;
	}

	public void setInsureperiod(String insureperiod) {
		this.insureperiod = insureperiod;
	}

	public String getHoldername() {
		return holdername;
	}

	public void setHoldername(String holdername) {
		this.holdername = holdername;
	}

	public String getWageno() {
		return wageno;
	}

	public void setWageno(String wageno) {
		this.wageno = wageno;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMoneys() {
		return moneys;
	}

	public void setMoneys(String moneys) {
		this.moneys = moneys;
	}

	public String getFycs() {
		return fycs;
	}

	public void setFycs(String fycs) {
		this.fycs = fycs;
	}

	public String getIsshowtuiguangfei() {
		return isshowtuiguangfei;
	}

	public void setIsshowtuiguangfei(String isshowtuiguangfei) {
		this.isshowtuiguangfei = isshowtuiguangfei;
	}

	public String getProductname2() {
		return productname2;
	}

	public void setProductname2(String productname2) {
		this.productname2 = productname2;
	}

	public String getProductimageurl2() {
		return productimageurl2;
	}

	public void setProductimageurl2(String productimageurl2) {
		this.productimageurl2 = productimageurl2;
	}

	public String getOrderDetailUrl2() {
		return orderDetailUrl2;
	}

	public void setOrderDetailUrl2(String orderDetailUrl2) {
		this.orderDetailUrl2 = orderDetailUrl2;
	}

	public String getHoldername2() {
		return holdername2;
	}

	public void setHoldername2(String holdername2) {
		this.holdername2 = holdername2;
	}

	public String getInsureperiod2() {
		return insureperiod2;
	}

	public void setInsureperiod2(String insureperiod2) {
		this.insureperiod2 = insureperiod2;
	}

	public String getTaxValue2() {
		return taxValue2;
	}

	public void setTaxValue2(String taxValue2) {
		this.taxValue2 = taxValue2;
	}

	public int getProductsize() {
		return productsize;
	}

	public void setProductsize(int productsize) {
		this.productsize = productsize;
	}

	public ArrayList<OrderListCarBean> getList() {
		return list;
	}

	public void setList(ArrayList<OrderListCarBean> list) {
		this.list = list;
	}

	//得到本分类的item个数
	public int getGroupItemCount(){
		return list.size()+1;
	}
	
	public OrderListCarBean getItem(int pPosition) {
        // Category排在第一位  
        if (pPosition == 0) {  
            return this;  
        } else {  
            return list.get(pPosition - 1);  
        }  
    }  
	
	
	
}
