package com.renren.android.photos;

public class PhotosCommentsResult {
	public int getIs_whisper() {
		return is_whisper;
	}
	public void setIs_whisper(int is_whisper) {
		this.is_whisper = is_whisper;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public long getComment_id() {
		return comment_id;
	}
	public void setComment_id(long comment_id) {
		this.comment_id = comment_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
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
	private int is_whisper;
	private int uid;
	private long comment_id;
	private String text;
	private String time;
	private String source;
	private String name;
	private String headurl;
	
}
