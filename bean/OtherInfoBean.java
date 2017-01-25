package com.cloudhome.bean;

import java.io.Serializable;

public class OtherInfoBean implements Serializable {


	
	private String tripDestination;
	private String tripGoal;
	private String visaCity;
	private String emergencyName;
	private String emergencyNum;
	public String getTripDestination() {
		return tripDestination;
	}
	public void setTripDestination(String tripDestination) {
		this.tripDestination = tripDestination;
	}
	public String getTripGoal() {
		return tripGoal;
	}
	public void setTripGoal(String tripGoal) {
		this.tripGoal = tripGoal;
	}
	public String getVisaCity() {
		return visaCity;
	}
	public void setVisaCity(String visaCity) {
		this.visaCity = visaCity;
	}
	public String getEmergencyName() {
		return emergencyName;
	}
	public void setEmergencyName(String emergencyName) {
		this.emergencyName = emergencyName;
	}
	public String getEmergencyNum() {
		return emergencyNum;
	}
	public void setEmergencyNum(String emergencyNum) {
		this.emergencyNum = emergencyNum;
	}
	
	
	
	
	
	
	
	
}
