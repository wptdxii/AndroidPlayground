package com.wptdxii.playground.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class InsuranceTemplateBean implements Serializable {
	private String id;
	private String suggest_id;
	private String product_id;
	private String product_name;
	private String company_id;
	private String master_id;//如果是附加险，依附的主险id
	private double prem;//本条目的产品保费
	private int amount;
	private int ismust;
	private int ishuomian;
	private int  display_order;//展示的顺序，可不穿
	private String add_time;
	private ArrayList<String> factorsName;
	private ArrayList<String> factorsValue;
	//最新的huomian
	private String huomian;
	private String must;
	private String is_main;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSuggest_id() {
		return suggest_id;
	}
	public void setSuggest_id(String suggest_id) {
		this.suggest_id = suggest_id;
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
	public String getCompany_id() {
		return company_id;
	}
	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}
	public String getMaster_id() {
		return master_id;
	}
	public void setMaster_id(String master_id) {
		this.master_id = master_id;
	}

	
	public int getIsmust() {
		return ismust;
	}
	public void setIsmust(int ismust) {
		this.ismust = ismust;
	}
	public int getIshuomian() {
		return ishuomian;
	}
	public void setIshuomian(int ishuomian) {
		this.ishuomian = ishuomian;
	}
	public int getDisplay_order() {
		return display_order;
	}
	public void setDisplay_order(int display_order) {
		this.display_order = display_order;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public ArrayList<String> getFactorsName() {
		return factorsName;
	}
	public void setFactorsName(ArrayList<String> factorsName) {
		this.factorsName = factorsName;
	}
	public ArrayList<String> getFactorsValue() {
		return factorsValue;
	}
	public void setFactorsValue(ArrayList<String> factorsValue) {
		this.factorsValue = factorsValue;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public double getPrem() {
		return prem;
	}
	public void setPrem(double prem) {
		this.prem = prem;
	}
	public String getHuomian() {
		return huomian;
	}
	public void setHuomian(String huomian) {
		this.huomian = huomian;
	}
	public String getMust() {
		return must;
	}
	public void setMust(String must) {
		this.must = must;
	}


	public String getIs_main() {
		return is_main;
	}

	public void setIs_main(String is_main) {
		this.is_main = is_main;
	}
}
