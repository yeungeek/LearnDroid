package com.renren.android.blog;

public class BlogCommentsResult {
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getIs_whisper() {
		return is_whisper;
	}
	public void setIs_whisper(int is_whisper) {
		this.is_whisper = is_whisper;
	}
	private int id;
	private int uid;
	private String name;
	private String headurl;
	private String time;
	private String content;
	private int is_whisper;
}
