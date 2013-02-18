package com.anhuioss.crowdroid.data.info;

public class ListInfo {

	private String mode;

	private String slug;

	private String description;

	private String follow;

	private String memberCount;

	private String fullName;

	private String name;

	private String id;

	private String subscriberCount;

	private String nextCursor;

	private String previousCursor;

	private UserInfo userInfo;

	// ----------------Set-------------------

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFollow(String follow) {
		this.follow = follow;
	}

	public void setMemberCount(String memberCount) {
		this.memberCount = memberCount;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSubscriberCount(String subscriberCount) {
		this.subscriberCount = subscriberCount;
	}

	public void setNextCursor(String nextCursor) {
		this.nextCursor = nextCursor;
	}

	public void setPrevioutCursor(String previousCursor) {
		this.previousCursor = previousCursor;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	// ----------------Get-------------------

	public String getMode() {
		return mode;
	}

	public String getSlug() {
		return slug;
	}

	public String getDescription() {
		return description;
	}

	public String getFollow() {
		return follow;
	}

	public String getMemberCount() {
		return memberCount;
	}

	public String getFullName() {
		return fullName;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getSubscriberCount() {
		return subscriberCount;
	}

	public String getNextCursor() {
		return nextCursor;
	}

	public String getPriviousCursor() {
		return previousCursor;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public String getListXML() {
		StringBuffer accountXML = new StringBuffer();
		accountXML.append("<list>").append("<slug>")
				.append(slug.replace("&", "&amp;").replace("<", "&lt;"))
				.append("</slug>").append("<description>")
				.append(description.replace("&", "&amp;").replace("<", "&lt;"))
				.append("</description>").append("<full_name>")
				.append(fullName.replace("&", "&amp;").replace("<", "&lt;"))
				.append("</full_name>").append("<name>")
				.append(name.replace("&", "&amp;").replace("<", "&lt;"))
				.append("</name>").append("<id>").append(id).append("</id>")
				.append("<mode>")
				.append(mode.replace("&", "&amp;").replace("<", "&lt;"))
				.append("</mode>").append("</list>");
		return accountXML.toString();
	}

}
