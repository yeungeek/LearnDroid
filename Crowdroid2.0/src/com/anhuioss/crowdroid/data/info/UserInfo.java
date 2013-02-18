package com.anhuioss.crowdroid.data.info;

import java.io.Serializable;

import android.graphics.Bitmap;

public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 201202291351L;

	public static final String UID = "uid";

	public static final String USER_IMAGE_URL = "userImageURL";
	public static final String USER_IMAGE = "userImage";
	public static final String SCREENNAME = "screenName";

	private String tag = "";

	private String tagId = "";

	private String uid = "";

	private String screenName = "";

	private String userName = "";

	private String gender = "";

	private String birthday = "";

	private String netWork = "";

	private String homeTown = "";

	private String star = "";

	private String retweetedScreenName = "";

	private String description = "";

	private String followCount = "";

	private String followerCount = "";

	private String notifications = "";

	private String following = "";

	private String userImageURL = "";

	private String groupName = "";

	private String country_code = "";

	private String province_code = "";

	private String city_code = "";

	private Bitmap userImage;

	private int utcOffset;

	private String statusCount;

	private String blogsCount;

	private String albumsCount;

	private String visitorsCount;

	private String follow;

	private String followed;

	private String listCount;

	private boolean verified = false;

	private String retweetUserId;

	private String pageinfo;

	private String columncount;

	public UserInfo() {
	}

	public String getTag() {
		return tag;
	}

	public String getTagId() {
		return tagId;
	}

	public String getUid() {
		return uid;
	}

	public String getUserName() {
		return userName;
	}

	public String getGender() {
		return gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getNetWork() {
		return netWork;
	}

	public String getHomeTown() {
		return homeTown;
	}

	public String getStar() {
		return star;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getRetweetedScreenName() {
		return retweetedScreenName;
	}

	public String getDescription() {
		return description;
	}

	public String getFollowCount() {
		return followCount;
	}

	public String getFollowerCount() {
		return followerCount;
	}

	public String getNotifications() {
		return notifications;
	}

	public String getVisitorsCount() {
		return visitorsCount;
	}

	public String getFollowing() {
		return following;
	}

	public String getUserImageURL() {
		return userImageURL;
	}

	public String getGroupName() {
		return groupName;
	}

	public Bitmap getUserImage() {
		return userImage;
	}

	public int getUtcOffset() {
		return utcOffset;
	}

	public String getStatusCount() {
		return statusCount;
	}

	public String getBlogsCount() {
		return blogsCount;
	}

	public String getAlbumsCount() {
		return albumsCount;
	}

	public String getFollow() {
		return follow;
	}

	public String getFollowed() {
		return followed;
	}

	public String getListCount() {
		return listCount;
	}

	public String getCountry_code() {
		return country_code;
	}

	public String getCity_code() {
		return city_code;
	}

	public String getProvince_code() {
		return province_code;
	}

	public boolean getVerified() {
		return verified;
	}

	public String getRetweetUserId() {
		return retweetUserId;
	}

	public String getPageinfo() {
		return pageinfo;
	}

	public String getColumnCount() {
		return columncount;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public void setNetWork(String netWork) {
		this.netWork = netWork;
	}

	public void setHomeTown(String homeTown) {
		this.homeTown = homeTown;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public void setRetweetUserId(String retweetUserId) {
		this.retweetUserId = retweetUserId;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setRetweetedScreenName(String retweetedScreenName) {
		this.retweetedScreenName = retweetedScreenName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFollowCount(String followCount) {
		this.followCount = followCount;
	}

	public void setFollowerCount(String followerCount) {
		this.followerCount = followerCount;
	}

	public void setCountry_code(String coutry_code) {
		this.country_code = coutry_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public void setProvince_code(String province_code) {
		this.province_code = province_code;
	}

	public void setNotifications(String notifications) {
		this.notifications = notifications;
	}

	public void setFollowing(String following) {
		this.following = following;
	}

	public void setUserImageURL(String userImageURL) {
		this.userImageURL = userImageURL;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setUserImage(Bitmap userImage) {
		this.userImage = userImage;
	}

	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}

	public void setStatusCount(String statusCount) {
		this.statusCount = statusCount;
	}

	public void setBlogsCount(String blogsCount) {
		this.blogsCount = blogsCount;
	}

	public void setAlbumsCount(String albumsCount) {
		this.albumsCount = albumsCount;
	}

	public void setVisitorsCount(String visitorsCount) {
		this.visitorsCount = visitorsCount;
	}

	public void setFollow(String follow) {
		this.follow = follow;
	}

	public void setFollowed(String followed) {
		this.followed = followed;
	}

	public void setListCount(String listCount) {
		this.listCount = listCount;
	}

	public void setVerified(String verified) {
		if (verified != null && verified.equals("true")) {
			this.verified = true;
		}
	}

	public void setPageInfo(String pageinfo) {
		this.pageinfo = pageinfo;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public void setColumnCount(String columnCount) {
		this.columncount = columnCount;
	}
}
