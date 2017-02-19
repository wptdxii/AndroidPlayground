package com.cloudhome.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MakeInsuranceTemplateBean implements Serializable {
	//保险信息 prodinfo
	private String insuranceId;
	private int isHuomian;
	private String insuranceName;
	private String insuranceType;
	private String insuranceType_name;
	//保险金额 riskmount
	private String riskHint;
	private String riskTitle;
	private String riskUnitTitle;
	//item底部保险保费
	private String permHint;
	private String permTitle;
	private String permUnitTitle;
	//存放inputfields
	private ArrayList<MakeInsuranceTemplateInnerBean> innerBeanList;
	//最新的建议书用的豁免字段(字符串)
	private String huomian; 
	//主险的icon
	private String icon; 
	//判断本主险是否有附加险
	private boolean isHasAdditionalInsurance=false;
	//附加险的时候
	
	public String getInsuranceId() {
		return insuranceId;
	}
	public void setInsuranceId(String insuranceId) {
		this.insuranceId = insuranceId;
	}
	public int getIsHuomian() {
		return isHuomian;
	}
	public void setIsHuomian(int isHuomian) {
		this.isHuomian = isHuomian;
	}
	public String getInsuranceName() {
		return insuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}
	public String getInsuranceType() {
		return insuranceType;
	}
	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}
	public String getInsuranceType_name() {
		return insuranceType_name;
	}
	public void setInsuranceType_name(String insuranceType_name) {
		this.insuranceType_name = insuranceType_name;
	}
	public String getRiskHint() {
		return riskHint;
	}
	public void setRiskHint(String riskHint) {
		this.riskHint = riskHint;
	}
	public String getRiskTitle() {
		return riskTitle;
	}
	public void setRiskTitle(String riskTitle) {
		this.riskTitle = riskTitle;
	}
	public String getRiskUnitTitle() {
		return riskUnitTitle;
	}
	public void setRiskUnitTitle(String riskUnitTitle) {
		this.riskUnitTitle = riskUnitTitle;
	}
	public String getPermHint() {
		return permHint;
	}
	public void setPermHint(String permHint) {
		this.permHint = permHint;
	}
	public String getPermTitle() {
		return permTitle;
	}
	public void setPermTitle(String permTitle) {
		this.permTitle = permTitle;
	}
	public String getPermUnitTitle() {
		return permUnitTitle;
	}
	public void setPermUnitTitle(String permUnitTitle) {
		this.permUnitTitle = permUnitTitle;
	}
	public ArrayList<MakeInsuranceTemplateInnerBean> getInnerBeanList() {
		return innerBeanList;
	}
	public void setInnerBeanList(
			ArrayList<MakeInsuranceTemplateInnerBean> innerBeanList) {
		this.innerBeanList = innerBeanList;
	}
	public String getHuomian() {
		return huomian;
	}
	public void setHuomian(String huomian) {
		this.huomian = huomian;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public boolean isHasAdditionalInsurance() {
		return isHasAdditionalInsurance;
	}
	public void setHasAdditionalInsurance(boolean isHasAdditionalInsurance) {
		this.isHasAdditionalInsurance = isHasAdditionalInsurance;
	}
	
	
	
	
	
	
	
	
	
}
