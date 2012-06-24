package com.anhuioss.crowdroid.data;

import android.content.Context;
import android.content.SharedPreferences;

public class StatusData {

	// get set
	private Context context;

	private String currentUid;

	private String currentService;

	private String newestAtMessageId;

	private String newestDirectMessageId;

	private String newestGeneralMessageId;

	private String newestRetweetOfMeMessageId;

	private String newestCommentId;

	private String newestFeedShareMessageId;

	private String newestFeedStatusMessageId;

	private String newestFeedAlbumMessageId;

	private String newestFeedBlogMessageId;

	private String lastTranslationFrom;

	private String lastTranslationTo;

	private static final String STATUS = "status";

	public static final String CURRENT_UID = "current_uid";

	public static final String CURRENT_SERVICE = "current_service";

	public static final String NEWEST_AT_MESSAGE_ID = "newest_at_message_id";

	public static final String NEWEST_DIRECT_MESSAGE_ID = "newest_direct_message_id";

	public static final String NEWEST_GENERAL_MESSAGE_ID = "newest_general_message_id";

	public static final String NEWEST_RETWEET_OF_ME_MESSAGE_ID = "newest_retweet_of_me_message_id";

	public static final String NEWEST_COMMENT_ID = "newest_comment_id";

	public static final String NEWEST_FEED_SHARE_MESSAGE_ID = "newest_feed_share_message_id";

	public static final String NEWEST_FEED_STATUS_MESSAGE_ID = "newest_feed_state_message_id";

	public static final String NEWEST_FEED_ALBUM_MESSAGE_ID = "newest_feed_album_message_id";

	public static final String NEWEST_FEED_BLOG_MESSAGE_ID = "newest_feed_blog_message_id";

	public static final String LAST_TRANSLATION_FROM = "last_translation_from";

	public static final String LAST_TRANSLATION_TO = "last_translation_to";

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public StatusData(Context context) {
		this.context = context;
		readPreference();
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setCurrentUid(String currentUid) {
		this.currentUid = currentUid;
		writePreference(CURRENT_UID, currentUid);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setCurrentService(String currentService) {
		this.currentService = currentService;
		writePreference(CURRENT_SERVICE, currentService);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setNewestAtMessageId(String newestAtMessageId) {
		this.newestAtMessageId = newestAtMessageId;
		writePreference(NEWEST_AT_MESSAGE_ID, this.newestAtMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setNewestDirectMessageId(String newestDirectMessageId) {
		this.newestDirectMessageId = newestDirectMessageId;
		writePreference(NEWEST_DIRECT_MESSAGE_ID, this.newestDirectMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setNewestGeneralMessageId(String newestGeneralMessageId) {
		this.newestGeneralMessageId = newestGeneralMessageId;
		writePreference(NEWEST_GENERAL_MESSAGE_ID, this.newestGeneralMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setNewestRetweetOfMeMessageId(String newestRetweetOfMeMessageId) {
		this.newestRetweetOfMeMessageId = newestRetweetOfMeMessageId;
		writePreference(NEWEST_RETWEET_OF_ME_MESSAGE_ID,
				this.newestRetweetOfMeMessageId);
	}

	public void setNewestCommentId(String newestCommentId) {
		this.newestCommentId = newestCommentId;
		writePreference(NEWEST_COMMENT_ID, this.newestCommentId);
	}

	/*
	 * SNS ----------------RenRen-------------------
	 */
	// -----------------------------------------------------------------------------
	public void setNewestFeedShareMessageId(String newestFeedShareMessageId) {
		this.newestFeedShareMessageId = newestFeedShareMessageId;
		writePreference(NEWEST_FEED_SHARE_MESSAGE_ID,
				this.newestFeedShareMessageId);
	}

	public void setNewestFeedStatusMessageId(String newestFeedStatusMessageId) {
		this.newestFeedStatusMessageId = newestFeedStatusMessageId;
		writePreference(NEWEST_FEED_STATUS_MESSAGE_ID,
				this.newestFeedStatusMessageId);
	}

	public void setNewestFeedAlbumMessageId(String newestFeedAlbumMessageId) {
		this.newestFeedAlbumMessageId = newestFeedAlbumMessageId;
		writePreference(NEWEST_FEED_ALBUM_MESSAGE_ID,
				this.newestFeedAlbumMessageId);
	}

	public void setNewestFeedBlogMessageId(String newestFeedBlogMessageId) {
		this.newestFeedBlogMessageId = newestFeedBlogMessageId;
		writePreference(NEWEST_FEED_BLOG_MESSAGE_ID,
				this.newestFeedBlogMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setLastTranslationFrom(String lastTranslationFrom) {
		this.lastTranslationFrom = lastTranslationFrom;
		writePreference(LAST_TRANSLATION_FROM, lastTranslationFrom);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void setLastTranslationTo(String lastTranslationTo) {
		this.lastTranslationTo = lastTranslationTo;
		writePreference(LAST_TRANSLATION_TO, lastTranslationTo);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public String getCurrentUid() {
		return currentUid;
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public String getCurrentService() {
		return currentService;
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public Long getNewestAtMessageId() {
		return Long.valueOf(newestAtMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public Long getNewestDirectMessageId() {
		return Long.valueOf(newestDirectMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public Long getNewestRetweetOfMeMessageId() {
		return Long.valueOf(newestRetweetOfMeMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public Long getNewestGeneralMessageId() {
		return Long.valueOf(newestGeneralMessageId);
	}

	// cfb
	public Long getNewestCommentId() {
		return Long.valueOf(newestCommentId);
	}

	/*
	 * SNS----------------RenRen---------------
	 */
	public Long getNewestFeedShareMessageId() {
		return Long.valueOf(newestFeedShareMessageId);
	}

	public Long getNewestFeedStatusMessageId() {
		return Long.valueOf(newestFeedStatusMessageId);
	}

	public Long getNewestFeedAlbumMessageId() {
		return Long.valueOf(newestFeedAlbumMessageId);
	}

	public Long getNewestFeedBlogMessageId() {
		return Long.valueOf(newestFeedBlogMessageId);
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public String getLastTranslationFrom() {
		return lastTranslationFrom;
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public String getLastTranslationTo() {
		return lastTranslationTo;
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	private synchronized void writePreference(String key, String value) {

		// Write To Shared Preference
		SharedPreferences status = context.getSharedPreferences(STATUS,
				Context.MODE_PRIVATE);
		status.edit().putString(key, value).commit();

	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	private void readPreference() {

		// Write To Shared Preference
		SharedPreferences status = context.getSharedPreferences(STATUS,
				Context.MODE_PRIVATE);
		currentUid = status.getString(CURRENT_UID, "");
		currentService = status.getString(CURRENT_SERVICE, "");
		newestAtMessageId = status.getString(NEWEST_AT_MESSAGE_ID,
				String.valueOf("0"));
		newestDirectMessageId = status.getString(NEWEST_DIRECT_MESSAGE_ID,
				String.valueOf("0"));
		newestGeneralMessageId = status.getString(NEWEST_GENERAL_MESSAGE_ID,
				String.valueOf("0"));
		newestRetweetOfMeMessageId = status.getString(
				NEWEST_RETWEET_OF_ME_MESSAGE_ID, String.valueOf("0"));
		lastTranslationFrom = status.getString(LAST_TRANSLATION_FROM, "");
		lastTranslationTo = status.getString(LAST_TRANSLATION_TO, "");
		// cfb
		newestCommentId = status.getString(NEWEST_COMMENT_ID,
				String.valueOf("0"));
		// SNS -------RenRen-------------
		newestFeedShareMessageId = status.getString(
				NEWEST_FEED_SHARE_MESSAGE_ID, String.valueOf("0"));
		newestFeedStatusMessageId = status.getString(
				NEWEST_FEED_STATUS_MESSAGE_ID, String.valueOf("0"));
		newestFeedAlbumMessageId = status.getString(
				NEWEST_FEED_ALBUM_MESSAGE_ID, String.valueOf("0"));
		newestFeedBlogMessageId = status.getString(NEWEST_FEED_BLOG_MESSAGE_ID,
				String.valueOf("0"));

	}
}
