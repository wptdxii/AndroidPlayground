package com.wptdxii.playground.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderListBean implements Serializable {

	private String monthDivider;//分类的名字
	private String ordertype;
	private String orderno;
	private String status;
	private String productname;
	private String productimageurl;
	private String insureperiod;
	private String holdername;
	private String wageno;
	private String id;
	private String moneys;
	private String fycs;
	private String ordercreatetime;
	private ArrayList<OrderListBean> list;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	private String source;


	public String getMonthDivider() {
		return monthDivider;
	}
	public void setMonthDivider(String monthDivider) {
		this.monthDivider = monthDivider;
	}

	public String getOrderType() {
		return ordertype;
	}
	public void setOrderType(String ordertype) {
		this.ordertype = ordertype;
	}

	public String getOrderNo() {
		return orderno;
	}
	public void setOrderNo(String orderno) {
		this.orderno = orderno;
	}


	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getProductName() {
		return productname;
	}
	public void setProductName(String productname) {
		this.productname = productname;
	}


	public String getProductImageurl() {
		return productimageurl;
	}
	public void setProductImageurl(String productimageurl) {
		this.productimageurl = productimageurl;
	}

	public String getInsurePeriod() {
		return insureperiod;
	}
	public void setInsurePeriod(String insureperiod) {
		this.insureperiod = insureperiod;
	}


	public String getHolderName() {
		return holdername;
	}
	public void setHolderName(String holdername) {
		this.holdername = holdername;
	}


	public String getWageNo() {
		return wageno;
	}
	public void setWageNo(String wageno) {
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

	public String getOrderCreatetime() {
		return ordercreatetime;
	}
	public void setOrderCreatetime(String ordercreatetime) {
		this.ordercreatetime = ordercreatetime;
	}









	public ArrayList<OrderListBean> getList() {
		return list;
	}
	public void setList(ArrayList<OrderListBean> list) {
		this.list = list;
	}




	//得到本分类的item个数
	public int getGroupItemCount(){
		return list.size()+1;
	}
	
	public OrderListBean getItem(int pPosition) {
        // Category排在第一位  
        if (pPosition == 0) {  
            return this;  
        } else {  
            return list.get(pPosition - 1);  
        }  
    }  
	
	
	
}
