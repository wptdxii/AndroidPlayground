package com.wptdxii.playground.bean;


public class HeadImage {


	private String IndexImgurl;
	private String clickUrl;

	// 主界面的数据获取
	private String Name;
	private String Tag;


	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getTag() {
		return Tag;
	}
  
	public void setTag(String tag) {
		Tag = tag;
	}


	// viewpager用（下）
	public String getIndexImgurl() {
		return IndexImgurl;
	}

	public void setIndexImgurl(String indexImgurl) {
		IndexImgurl = indexImgurl;
	}

	public String getClickUrl() {
		return clickUrl;
	}

	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}


}

