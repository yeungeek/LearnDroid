package com.renren.android.page;

public class PageResult {
	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getPinyin_name() {
		return pinyin_name;
	}

	public void setPinyin_name(String pinyin_name) {
		this.pinyin_name = pinyin_name;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadurl() {
		return headurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getFansCount() {
		return fansCount;
	}

	public void setFansCount(int fansCount) {
		this.fansCount = fansCount;
	}

	public int getIsFan() {
		return isFan;
	}

	public void setIsFan(int isFan) {
		this.isFan = isFan;
	}

	private int pageId;
	private String name;
	private String headurl;
	private String desc;
	private String category_name;
	private int category_id;
	private int fansCount;
	private int isFan;
	private String pinyin_name;
	private String first_name;
}
