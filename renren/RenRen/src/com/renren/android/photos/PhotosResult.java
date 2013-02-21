package com.renren.android.photos;

public class PhotosResult {
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getAid() {
		return aid;
	}

	public void setAid(long aid) {
		this.aid = aid;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUrl_large() {
		return url_large;
	}

	public void setUrl_large(String url_large) {
		this.url_large = url_large;
	}

	public String getUrl_head() {
		return url_head;
	}

	public void setUrl_head(String url_head) {
		this.url_head = url_head;
	}

	public String getUrl_tiny() {
		return url_tiny;
	}

	public void setUrl_tiny(String url_tiny) {
		this.url_tiny = url_tiny;
	}

	public String getUrl_main() {
		return url_main;
	}

	public void setUrl_main(String url_main) {
		this.url_main = url_main;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	private int uid;
	private long aid;
	private long pid;
	private String time;
	private String url_large;
	private String url_head;
	private String url_tiny;
	private String url_main;
	private String caption;
	private int view_count;
	private int comment_count;
}
