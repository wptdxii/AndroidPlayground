package com.wptdxii.playground.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class SubmitInsurancePlanBean implements Serializable {
	private boolean isPeriodShow;
	private String periodValueCode;//提交时的code
	private String periodMiddleString;//显示的文字
	private int periodMain;
	private String periodLeftString;
	private int periodCheckItem;
	private ArrayList<String> insurancePeriodCodeList;
	private ArrayList<String> insurancePeriodStringList;
	
	private boolean isFrequencyShow;
	private String frequencyValueCode;
	private String frequencyMiddleString;
	private int frequencyMain;
	private String frequencyLeftString;
	private int frequencyCheckItem;
	private ArrayList<String> insuranceFrequencyCodeList;
	private ArrayList<String> insuranceFrequencyStringList;
	
	private boolean isPayTimeShow;
	private String payTimeValueCode;
	private String payTimeMiddleString;
	private int payTimeMain;
	private String payTimeLeftString;
	private int payTimeCheckItem;
	private ArrayList<String> insurancePayTimeCodeList;
	private ArrayList<String> insurancePayTimeStringList;
	
	private boolean isPlanShow;
	private String planValueCode="";
	private String planMiddleString;
	private String planLeftString;
	private int planCheckItem;
	private int planMain;
	
	
	private ArrayList<String> insurancePlanCodeList;
	private ArrayList<String> insurancePlanStringList;
	
	private boolean isShowWarn=false;
	private String warnText;
	
	private boolean isCheck;
	
	
	private String riskMount;
	private int itemOfIndex;
	private String id;
	
	private String insuranceTitle;
	private String moneyLeft;
	private String insuranceHint;
	
	
	
	
	//记录哪个条目显示
	private boolean isJobShow;
	private int baoe=0;
	
	

	
	public int getPeriodMain() {
		return periodMain;
	}
	public void setPeriodMain(int periodMain) {
		this.periodMain = periodMain;
	}
	public int getFrequencyMain() {
		return frequencyMain;
	}
	public void setFrequencyMain(int frequencyMain) {
		this.frequencyMain = frequencyMain;
	}
	public int getPayTimeMain() {
		return payTimeMain;
	}
	public void setPayTimeMain(int payTimeMain) {
		this.payTimeMain = payTimeMain;
	}
	public String getWarnText() {
		return warnText;
	}
	public void setWarnText(String warnText) {
		this.warnText = warnText;
	}
	public boolean isShowWarn() {
		return isShowWarn;
	}
	public void setShowWarn(boolean isShowWarn) {
		this.isShowWarn = isShowWarn;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPeriodValueCode() {
		return periodValueCode;
	}
	public void setPeriodValueCode(String periodValueCode) {
		this.periodValueCode = periodValueCode;
	}
	public String getFrequencyValueCode() {
		return frequencyValueCode;
	}
	public void setFrequencyValueCode(String frequencyValueCode) {
		this.frequencyValueCode = frequencyValueCode;
	}
	public String getPayTimeValueCode() {
		return payTimeValueCode;
	}
	public void setPayTimeValueCode(String payTimeValueCode) {
		this.payTimeValueCode = payTimeValueCode;
	}
	
	public String getRiskMount() {
		return riskMount;
	}
	public void setRiskMount(String riskMount) {
		this.riskMount = riskMount;
	}
	public int getItemOfIndex() {
		return itemOfIndex;
	}
	public void setItemOfIndex(int itemOfIndex) {
		this.itemOfIndex = itemOfIndex;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public String getPlanValueCode() {
		return planValueCode;
	}
	public void setPlanValueCode(String planValueCode) {
		this.planValueCode = planValueCode;
	}

	public boolean isJobShow() {
		return isJobShow;
	}
	public void setJobShow(boolean isJobShow) {
		this.isJobShow = isJobShow;
	}
	public boolean isPeriodShow() {
		return isPeriodShow;
	}
	public void setPeriodShow(boolean isPeriodShow) {
		this.isPeriodShow = isPeriodShow;
	}
	public boolean isPayTimeShow() {
		return isPayTimeShow;
	}
	public void setPayTimeShow(boolean isPayTimeShow) {
		this.isPayTimeShow = isPayTimeShow;
	}
	public boolean isFrequencyShow() {
		return isFrequencyShow;
	}
	public void setFrequencyShow(boolean isFrequencyShow) {
		this.isFrequencyShow = isFrequencyShow;
	}
	public boolean isPlanShow() {
		return isPlanShow;
	}
	public void setPlanShow(boolean isPlanShow) {
		this.isPlanShow = isPlanShow;
	}
	public int getBaoe() {
		return baoe;
	}
	public void setBaoe(int baoe) {
		this.baoe = baoe;
	}
	public String getPeriodLeftString() {
		return periodLeftString;
	}
	public void setPeriodLeftString(String periodLeftString) {
		this.periodLeftString = periodLeftString;
	}
	public String getFrequencyLeftString() {
		return frequencyLeftString;
	}
	public void setFrequencyLeftString(String frequencyLeftString) {
		this.frequencyLeftString = frequencyLeftString;
	}
	public String getPayTimeLeftString() {
		return payTimeLeftString;
	}
	public void setPayTimeLeftString(String payTimeLeftString) {
		this.payTimeLeftString = payTimeLeftString;
	}
	public String getPlanLeftString() {
		return planLeftString;
	}
	public void setPlanLeftString(String planLeftString) {
		this.planLeftString = planLeftString;
	}
	public int getPeriodCheckItem() {
		return periodCheckItem;
	}
	public void setPeriodCheckItem(int periodCheckItem) {
		this.periodCheckItem = periodCheckItem;
	}
	public int getFrequencyCheckItem() {
		return frequencyCheckItem;
	}
	public void setFrequencyCheckItem(int frequencyCheckItem) {
		this.frequencyCheckItem = frequencyCheckItem;
	}
	public int getPayTimeCheckItem() {
		return payTimeCheckItem;
	}
	public void setPayTimeCheckItem(int payTimeCheckItem) {
		this.payTimeCheckItem = payTimeCheckItem;
	}
	public int getPlanCheckItem() {
		return planCheckItem;
	}
	public void setPlanCheckItem(int planCheckItem) {
		this.planCheckItem = planCheckItem;
	}
	public String getPeriodMiddleString() {
		return periodMiddleString;
	}
	public void setPeriodMiddleString(String periodMiddleString) {
		this.periodMiddleString = periodMiddleString;
	}
	public String getFrequencyMiddleString() {
		return frequencyMiddleString;
	}
	public void setFrequencyMiddleString(String frequencyMiddleString) {
		this.frequencyMiddleString = frequencyMiddleString;
	}
	public String getPayTimeMiddleString() {
		return payTimeMiddleString;
	}
	public void setPayTimeMiddleString(String payTimeMiddleString) {
		this.payTimeMiddleString = payTimeMiddleString;
	}
	public String getPlanMiddleString() {
		return planMiddleString;
	}
	public void setPlanMiddleString(String planMiddleString) {
		this.planMiddleString = planMiddleString;
	}
	public ArrayList<String> getInsurancePeriodCodeList() {
		return insurancePeriodCodeList;
	}
	public void setInsurancePeriodCodeList(ArrayList<String> insurancePeriodCodeList) {
		this.insurancePeriodCodeList = insurancePeriodCodeList;
	}
	public ArrayList<String> getInsurancePeriodStringList() {
		return insurancePeriodStringList;
	}
	public void setInsurancePeriodStringList(
			ArrayList<String> insurancePeriodStringList) {
		this.insurancePeriodStringList = insurancePeriodStringList;
	}
	public ArrayList<String> getInsuranceFrequencyCodeList() {
		return insuranceFrequencyCodeList;
	}
	public void setInsuranceFrequencyCodeList(
			ArrayList<String> insuranceFrequencyCodeList) {
		this.insuranceFrequencyCodeList = insuranceFrequencyCodeList;
	}
	public ArrayList<String> getInsuranceFrequencyStringList() {
		return insuranceFrequencyStringList;
	}
	public void setInsuranceFrequencyStringList(
			ArrayList<String> insuranceFrequencyStringList) {
		this.insuranceFrequencyStringList = insuranceFrequencyStringList;
	}
	public ArrayList<String> getInsurancePayTimeCodeList() {
		return insurancePayTimeCodeList;
	}
	public void setInsurancePayTimeCodeList(
			ArrayList<String> insurancePayTimeCodeList) {
		this.insurancePayTimeCodeList = insurancePayTimeCodeList;
	}
	public ArrayList<String> getInsurancePayTimeStringList() {
		return insurancePayTimeStringList;
	}
	public void setInsurancePayTimeStringList(
			ArrayList<String> insurancePayTimeStringList) {
		this.insurancePayTimeStringList = insurancePayTimeStringList;
	}
	public ArrayList<String> getInsurancePlanCodeList() {
		return insurancePlanCodeList;
	}
	public void setInsurancePlanCodeList(ArrayList<String> insurancePlanCodeList) {
		this.insurancePlanCodeList = insurancePlanCodeList;
	}
	public ArrayList<String> getInsurancePlanStringList() {
		return insurancePlanStringList;
	}
	public void setInsurancePlanStringList(ArrayList<String> insurancePlanStringList) {
		this.insurancePlanStringList = insurancePlanStringList;
	}
	public String getInsuranceTitle() {
		return insuranceTitle;
	}
	public void setInsuranceTitle(String insuranceTitle) {
		this.insuranceTitle = insuranceTitle;
	}
	public String getMoneyLeft() {
		return moneyLeft;
	}
	public void setMoneyLeft(String moneyLeft) {
		this.moneyLeft = moneyLeft;
	}
	public String getInsuranceHint() {
		return insuranceHint;
	}
	public void setInsuranceHint(String insuranceHint) {
		this.insuranceHint = insuranceHint;
	}
	public int getPlanMain() {
		return planMain;
	}
	public void setPlanMain(int planMain) {
		this.planMain = planMain;
	}
	
	
	
	
	
	
	
}
