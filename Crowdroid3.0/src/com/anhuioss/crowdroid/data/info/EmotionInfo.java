package com.anhuioss.crowdroid.data.info;

public class EmotionInfo {
	
	public static String PHRASE = "phrase";
	
	public static String TYPE = "type";
	
	public static String URL = "url";
	
	public static String IS_HOT = "is_hot";
	
	public static String IS_COMMON = "is_common";
	
	public static String ORDER_NUMBER = "order_number";
	
	public static String CATEGORY = "category";

	private String phrase = null;

	private String type = null;

	private String url = null;

	private boolean isHot = false;
	
	private boolean isCommon = false;

	private int orderNumber = -1;

	private String category = null;

	public void setPhrase(String phrase) {

		this.phrase = phrase;

	}

	public void setType(String type) {

		this.type = type;

	}

	public void setUrl(String url) {

		this.url = url;

	}

	public void setIsHot(boolean isHot) {

		this.isHot = isHot;

	}
	
	public void setIsCommon(boolean isCommon) {
		
		this.isCommon = isCommon;
		
	}

	public void setOrderNumber(int orderNumber) {

		this.orderNumber = orderNumber;

	}

	public void setCategory(String category) {

		this.category = category;

	}

	public String getPhrase() {

		return phrase;

	}

	public String getType() {

		return type;

	}

	public String getUrl() {

		return url;

	}

	public boolean getIsHot() {

		return isHot;

	}
	
	public boolean getIsCommon() {
		
		return isCommon;
		
	}

	public int getOrderNumber() {

		return orderNumber;

	}

	public String getCategory() {

		return category;

	}

}
