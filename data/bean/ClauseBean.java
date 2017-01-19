package com.cloudhome.bean;

import java.io.Serializable;

public class ClauseBean implements Serializable {
	private String id;
	private String name;
	private String url;//此url不为空表示的是有pdf文件需要下载
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	
	
}
