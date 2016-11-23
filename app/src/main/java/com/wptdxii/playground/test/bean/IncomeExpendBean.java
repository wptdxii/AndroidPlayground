package com.wptdxii.playground.test.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class IncomeExpendBean implements Serializable {

	private String monthDivider;//分类的名字
	private String addtime;
	private String id;
	private String money;
	private String title;
	private String category;
	private ArrayList<IncomeExpendBean> list;
	
	public String getAddTime() {
		return addtime;
	}
	public void setAddTime(String addtime) {
		this.addtime = addtime;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getMonthDivider() {
		return monthDivider;
	}
	public void setMonthDivider(String monthDivider) {
		this.monthDivider = monthDivider;
	}
	public ArrayList<IncomeExpendBean> getList() {
		return list;
	}
	public void setList(ArrayList<IncomeExpendBean> list) {
		this.list = list;
	}
	
	//得到本分类的item个数
	public int getGroupItemCount(){
		return list.size()+1;
	}
	
	public IncomeExpendBean getItem(int pPosition) {  
        // Category排在第一位  
        if (pPosition == 0) {  
            return this;  
        } else {  
            return list.get(pPosition - 1);  
        }  
    }  
	
	
	
}
