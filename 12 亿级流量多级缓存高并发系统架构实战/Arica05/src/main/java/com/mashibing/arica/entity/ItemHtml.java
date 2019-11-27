package com.mashibing.arica.entity;

public class ItemHtml extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 生成文件的状态，有没有生成成功。
	private String htmlStatus;
	private String location;
	public String getHtmlStatus() {
		return htmlStatus;
	}
	public void setHtmlStatus(String htmlStatus) {
		this.htmlStatus = htmlStatus;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	
	
}
