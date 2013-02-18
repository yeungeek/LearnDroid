package com.anhuioss.crowdroid.data;

public class AccountData {

	private String tag;

	private String uid;

	private String service;

	private String apiUrl;

	private String userName;

	private String authType;

	private String password;

	private String accessToken;

	private String tokenSecret;

	private String userScreenName;

	private String lastAtMessageId = String.valueOf("0");

	// private String lastWangYiAtMessageId = String.valueOf("0");

	private String lastDirectMessageId = String.valueOf("0");

	// private String lastWangYiDirectMessageId = String.valueOf("0");

	private String lastGeneralMessageId = String.valueOf("0");

	private String lastRetweetOfMeMessageId = String.valueOf("0");

	private String lastUserFollowerCount = String.valueOf("0");

	private String lastCfbCommentId = String.valueOf("0");

	private String lastFeedShareMessageId = String.valueOf("0");

	private String lastFeedStatusMessageId = String.valueOf("0");

	private String lastFeedAlbumMessageId = String.valueOf("0");

	private String lastFeedBlogMessageId = String.valueOf("0");

	private boolean multiTweet = false;

	public String getTag() {
		return tag;
	}

	public void SetTag(String tag) {
		this.tag = tag;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// -----------------------------------------------------------------------------------
	public AccountData() {

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set UID<br>
	 * 
	 * @param urd
	 */
	// -----------------------------------------------------------------------------------
	public void setUid(String urd) {
		this.uid = urd;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Service<br>
	 * 
	 * @param service
	 */
	// -----------------------------------------------------------------------------------
	public void setService(String service) {
		this.service = service;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set API URL<br>
	 * 
	 * @param apiUrl
	 */
	// -----------------------------------------------------------------------------------
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set User Name<br>
	 * 
	 * @param userName
	 */
	// -----------------------------------------------------------------------------------
	public void setUserName(String userName) {
		this.userName = userName;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Auth Type<br>
	 * 
	 * @param authType
	 */
	// -----------------------------------------------------------------------------------
	public void setAuthType(String authType) {
		this.authType = authType;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Password<br>
	 * 
	 * @param password
	 */
	// -----------------------------------------------------------------------------------
	public void setPassword(String password) {
		this.password = password;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Access Token<br>
	 * 
	 * @param accessToken
	 */
	// -----------------------------------------------------------------------------------
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Token Secret<br>
	 * 
	 * @param tokenSecret
	 */
	// -----------------------------------------------------------------------------------
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set User Screen Name
	 * 
	 * @param userScreenName
	 */
	// ----------------------------------------------------------------------------------
	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Multi Tweet<br>
	 * 
	 * @param multiTweet
	 */
	// -----------------------------------------------------------------------------------
	public void setMultiTweet(boolean multiTweet) {
		this.multiTweet = multiTweet;
	}

	public void setLastAtMessageId(String lastAtMessageId) {
		this.lastAtMessageId = lastAtMessageId;
	}

	// public void setLastWangYiAtMessageId(String lastWangYiAtMessageId) {
	// this.lastWangYiAtMessageId = lastWangYiAtMessageId;
	// }

	public void setLastDirectMessageId(String lastDirectMessageId) {
		this.lastDirectMessageId = lastDirectMessageId;
	}

	// public void setLastWangYiDirectMessageId(String
	// lastWangYiDirectMessageId) {
	// this.lastWangYiDirectMessageId = lastWangYiDirectMessageId;
	// }

	public void setLastGeneralMessageId(String lastGeneralMessageId) {
		this.lastGeneralMessageId = lastGeneralMessageId;
	}

	public void setLastRetweetOfMeMessageId(String lastRetweetOfMeMessageId) {
		this.lastRetweetOfMeMessageId = lastRetweetOfMeMessageId;
	}

	public void setLastUserFollowerCount(String lastUserFollowerCount) {
		this.lastUserFollowerCount = lastUserFollowerCount;
	}

	public void setLastCfbCommentId(String lastCfbComment) {
		this.lastCfbCommentId = lastCfbComment;
	}

	public void setLastFeedShareMessageId(String lastFeedShareMessageId) {
		this.lastFeedShareMessageId = lastFeedShareMessageId;
	}

	public void setLastFeedStatusMessageId(String lastFeedStatusMessageId) {
		this.lastFeedStatusMessageId = lastFeedStatusMessageId;
	}

	public void setLastFeedAlbumMessageId(String lastFeedAlbumMessageId) {
		this.lastFeedAlbumMessageId = lastFeedAlbumMessageId;
	}

	public void setLastFeedBlogMessageId(String lastFeedBlogMessageId) {
		this.lastFeedBlogMessageId = lastFeedBlogMessageId;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get UID<br>
	 * 
	 * @return uid
	 */
	// -----------------------------------------------------------------------------------
	public String getUid() {
		return uid;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Service<br>
	 * 
	 * @return service
	 */
	// -----------------------------------------------------------------------------------
	public String getService() {
		return service;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Api Url<br>
	 * 
	 * @return apiUrl
	 */
	// -----------------------------------------------------------------------------------
	public String getApiUrl() {
		return apiUrl;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get User Name<br>
	 * 
	 * @return userName
	 */
	// -----------------------------------------------------------------------------------
	public String getUserName() {
		return userName;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Auth Type<br>
	 * 
	 * @return authType
	 */
	// -----------------------------------------------------------------------------------
	public String getAuthType() {
		return authType;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Password<br>
	 * 
	 * @return password
	 */
	// -----------------------------------------------------------------------------------
	public String getPassword() {
		return password;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Access Token<br>
	 * 
	 * @return accessToken
	 */
	// -----------------------------------------------------------------------------------
	public String getAccessToken() {
		return accessToken;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Token Secret<br>
	 * 
	 * @return tokenSecret
	 */
	// -----------------------------------------------------------------------------------
	public String getTokenSecret() {
		return tokenSecret;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get User Screen Name
	 * 
	 * @return userScreenName
	 */
	public String getUserScreenName() {
		return userScreenName;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Multi Tweet<br>
	 * 
	 * @return multiTweet
	 */
	// -----------------------------------------------------------------------------------
	public boolean getMultiTweet() {
		return multiTweet;
	}

	public String getLastAtMessageId() {
		return lastAtMessageId;
	}

	// public String getLastWangYiAtMessageId() {
	// return lastWangYiAtMessageId;
	// }

	public String getLastDirectMessageId() {
		return lastDirectMessageId;
	}

	// public String getLastWangYiDirectMessageId() {
	// return lastWangYiDirectMessageId;
	// }

	public String getLastGeneralMessageId() {
		return lastGeneralMessageId;
	}

	public String getLastRetweetOfMeMessageId() {
		return lastRetweetOfMeMessageId;
	}

	public String getLastUserFollowerCount() {
		return lastUserFollowerCount;
	}

	public String getLastCfbCommentId() {
		return lastCfbCommentId;
	}

	public String getLastFeedShareMessageId() {
		return lastFeedShareMessageId;
	}

	public String getLastFeedStatusMessageId() {
		return lastFeedStatusMessageId;
	}

	public String getLastFeedAlbumMessageId() {
		return lastFeedAlbumMessageId;
	}

	public String getLastFeedBlogMessageId() {
		return lastFeedBlogMessageId;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Account XML<br>
	 * 
	 * @return accountXML
	 */
	// -----------------------------------------------------------------------------------
	public String getAccountXML() {
		StringBuffer accountXML = new StringBuffer();
		accountXML
				.append("<account>")
				.append("<at_message_id>")
				.append(lastAtMessageId)
				.append("</at_message_id>")
				// .append("<wangyi_at_message_id>").append(lastWangYiAtMessageId)
				// .append("</wangyi_at_message_id>")
				.append("<direct_message_id>")
				.append(lastDirectMessageId)
				.append("</direct_message_id>")
				// .append("<wangyi_direct_message_id>")
				// .append(lastWangYiDirectMessageId)
				// .append("</wangyi_direct_message_id>")
				.append("<general_message_id>").append(lastGeneralMessageId)
				.append("</general_message_id>")
				.append("<retweet_of_me_message_id>")
				.append(lastRetweetOfMeMessageId)
				.append("</retweet_of_me_message_id>")
				.append("<last_user_follower_count>")
				.append(lastUserFollowerCount)
				.append("</last_user_follower_count>")
				.append("<cfb_comment_id>").append(lastCfbCommentId)
				.append("</cfb_comment_id>").append("<sns_feed_share_id>")
				.append(lastFeedShareMessageId).append("</sns_feed_share_id>")
				.append("<sns_feed_status_id>").append(lastFeedStatusMessageId)
				.append("</sns_feed_status_id>").append("<sns_feed_album_id>")
				.append(lastFeedAlbumMessageId).append("</sns_feed_album_id>")
				.append("<sns_feed_blog_id>").append(lastFeedBlogMessageId)
				.append("</sns_feed_blog_id>").append("<uid>").append(uid)
				.append("</uid>").append("<service>").append(service)
				.append("</service>").append("<api_url>").append(apiUrl)
				.append("</api_url>").append("<user_name>").append(userName)
				.append("</user_name>").append("<user_screen_name>")
				.append(userScreenName).append("</user_screen_name>")
				.append("<auth_type>").append(authType).append("</auth_type>")
				.append("<password>").append(password).append("</password>")
				.append("<access_token>").append(accessToken)
				.append("</access_token>").append("<token_secret>")
				.append(tokenSecret).append("</token_secret>")
				.append("<multi_tweet>").append(multiTweet)
				.append("</multi_tweet>").append("</account>");
		return accountXML.toString();
	}

}
