package com.cloudhome.bean;

import java.io.Serializable;

public class TableRowBean implements Serializable {
 private String insuranceName;
 private String payTime;
 private String baoe;
 private String baofei;
 private String id;
 private String iconUrl;

	private String title1;
	private String title2;
	private String title3;

 
public String getInsuranceName() {
	return insuranceName;
}
public void setInsuranceName(String insuranceName) {
	this.insuranceName = insuranceName;
}
public String getPayTime() {
	return payTime;
}
public void setPayTime(String payTime) {
	this.payTime = payTime;
}
public String getBaoe() {
	return baoe;
}
public void setBaoe(String baoe) {
	this.baoe = baoe;
}
public String getBaofei() {
	return baofei;
}
public void setBaofei(String baofei) {
	this.baofei = baofei;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getIconUrl() {
	return iconUrl;
}
public void setIconUrl(String iconUrl) {
	this.iconUrl = iconUrl;
}

	public String getTitle1() {
		return title1;
	}

	public void setTitle1(String title1) {
		this.title1 = title1;
	}

	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	public String getTitle3() {
		return title3;
	}

	public void setTitle3(String title3) {
		this.title3 = title3;
	}
}
