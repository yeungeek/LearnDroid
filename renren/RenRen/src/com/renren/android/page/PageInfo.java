package com.renren.android.page;

public class PageInfo {
	public int getPage_id() {
		return page_id;
	}
	public void setPage_id(int page_id) {
		this.page_id = page_id;
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
	public String getMainurl() {
		return mainurl;
	}
	public void setMainurl(String mainurl) {
		this.mainurl = mainurl;
	}
	public int getStatus_id() {
		return status_id;
	}
	public void setStatus_id(int status_id) {
		this.status_id = status_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getFans_count() {
		return fans_count;
	}
	public void setFans_count(int fans_count) {
		this.fans_count = fans_count;
	}

	public int getAlbums_count() {
		return albums_count;
	}
	public void setAlbums_count(int albums_count) {
		this.albums_count = albums_count;
	}
	public int getBlogs_count() {
		return blogs_count;
	}
	public void setBlogs_count(int blogs_count) {
		this.blogs_count = blogs_count;
	}
	private int page_id;
	private String name;
	private String headurl;
	private String mainurl;
	private int status_id;
	private String content;
	private String time;
	private int fans_count;
	private int albums_count;
	private int blogs_count;
}
