package com.anhuioss.crowdroid.data;

import java.util.ArrayList;
import java.util.List;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingData {

	private String DEFAULT_SPEECH_LANGUAGE = "0";

	private boolean DEFAULT_AUTO_REFRESH = false;

	private boolean DEFAULT_REFRESH_TIMELINE = true;

	private String DEFAULT_TIME_SELECTION = "300000";

	private boolean DEFAULT_AUTO_TRANSLATION = false;

	private String DEFAULT_TRANSLATION_ENGINE = "Bing";

	private String DEFAULT_WALLPAPER = null;

	private boolean DEFAULT_NOTIFICATION = false;

	private boolean DEFAULT_DIRECT_MESSAGE = false;

	private boolean DEFAULT_AT_MESSAGE = false;

	private boolean DEFAULT_GENERAL_MESSAGE = false;

	private boolean DEFAULT_FOLLOWER_MESSAGE = false;

	private boolean DEFAULT_UNFOLLOWER_MESSAGE = false;

	private boolean DEFAULT_TWITTER_FOLLOWER_MESSAGE = false;

	private boolean DEFAULT_COMMENT_MESSAGE = false;

	private boolean DEFAULT_FEED_STATE_MESSAGE = false;

	private boolean DEFAULT_FEED_ALBUM_MESSAGE = false;

	private boolean DEFAULT_FEED_BLOG_MESSAGE = false;

	private boolean DEFAULT_FEED_SHARE_MESSAGE = false;

	private String DEFAULT_SHOW_IMAGE = "DISPLAY_ALL_IMAGE";

	private String DEFAULT_FONT_COLOR = String.valueOf(Color.BLACK);

	private String DEFAULT_FONT_SIZE = "14";

	private String AUTO = "";

	private SharedPreferences prefs;

	private Context context;

	// ----------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param Context
	 *            context
	 */
	// ----------------------------------------------------------------------------------
	public SettingData(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether auto refresh function is enabled
	 * 
	 * @return boolean autoRefresh
	 */
	// ----------------------------------------------------------------------------------
	public String getSpeechLanguage() {
		String selectSpeech = prefs.getString("speechlanguageselect",
				DEFAULT_SPEECH_LANGUAGE);
		return selectSpeech;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether auto refresh function is enabled
	 * 
	 * @return boolean autoRefresh
	 */
	// ----------------------------------------------------------------------------------
	public boolean isAutoRefresh() {
		boolean autoRefresh = prefs.getBoolean("autocheck",
				DEFAULT_AUTO_REFRESH);
		return autoRefresh;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether refresh timeline function is enabled
	 * 
	 * @return boolean refreshTimeline
	 */
	// ----------------------------------------------------------------------------------
	public boolean isRefreshTimeline() {
		boolean refreshTimeline = prefs.getBoolean("refreshtimeline",
				DEFAULT_REFRESH_TIMELINE);
		return refreshTimeline;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Get refresh time
	 * 
	 * @return String time
	 */
	// ----------------------------------------------------------------------------------
	public String getRefreshTime() {
		String time = prefs.getString("timeselection", DEFAULT_TIME_SELECTION);
		return time;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether auto translation function is enabled
	 * 
	 * @return boolean autoTranslation
	 */
	// ----------------------------------------------------------------------------------
	public boolean isAutoTranslation() {
		boolean autoTranslation = prefs.getBoolean("autotranslation",
				DEFAULT_AUTO_TRANSLATION);
		return autoTranslation;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Get translation engine
	 * 
	 * @return String engine
	 */
	// ----------------------------------------------------------------------------------
	public String getTranslateEngine() {
		String engine = prefs.getString("translationselection",
				DEFAULT_TRANSLATION_ENGINE);
		return engine;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Get wall paper
	 * 
	 * @return String wall paper
	 */
	// ----------------------------------------------------------------------------------
	public String getWallpaper() {
		String wallpaper = prefs.getString("wallpaper", DEFAULT_WALLPAPER);
		String st = wallpaper;
		return st;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether notification function is enabled
	 * 
	 * @return boolean notification
	 */
	// ----------------------------------------------------------------------------------
	public boolean isNotification() {
		boolean notification = prefs.getBoolean("notificationcheck",
				DEFAULT_NOTIFICATION);
		return notification;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether direct message notification is enabled
	 * 
	 * @return boolean directMessage
	 */
	// ----------------------------------------------------------------------------------
	public boolean isDirectMessage() {
		boolean directMessage = prefs.getBoolean("directmessage",
				DEFAULT_DIRECT_MESSAGE);
		return directMessage;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether at message notification is enabled
	 * 
	 * @return boolean atMessage
	 */
	// ----------------------------------------------------------------------------------
	public boolean isAtMessage() {
		boolean atMessage = prefs.getBoolean("atmessage", DEFAULT_AT_MESSAGE);
		return atMessage;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether general message notification is enabled
	 * 
	 * @return boolean generalMessage
	 */
	// ----------------------------------------------------------------------------------
	public boolean isGeneralMessage() {
		boolean generalMessage = prefs.getBoolean("generalmessage",
				DEFAULT_GENERAL_MESSAGE);
		return generalMessage;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether follower message notification is enabled
	 * 
	 * @return boolean followerMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isFollowerMessage() {
		boolean followerMessage = prefs.getBoolean("followmessage",
				DEFAULT_FOLLOWER_MESSAGE);
		return followerMessage;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether twitter follower message notification is enabled
	 * 
	 * @return boolean twFollowerMessage
	 */
	// ---------------------------------------------------------------------------------
	public boolean isTwitterFollowerMessage() {
		boolean twFollowerMessage = prefs.getBoolean("twitterfollowmessage",
				DEFAULT_TWITTER_FOLLOWER_MESSAGE);
		return twFollowerMessage;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether unfollow message notification is enabled
	 * 
	 * @return boolean unfollowMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isUnfollowerMessage() {
		boolean followerMessage = prefs.getBoolean("unfollowmessage",
				DEFAULT_UNFOLLOWER_MESSAGE);
		return followerMessage;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether uncomment message notification is enabled
	 * 
	 * @return boolean followMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isCommentMessage() {
		boolean commentMessage = prefs.getBoolean("commentmessage",
				DEFAULT_COMMENT_MESSAGE);
		return commentMessage;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether retweet of me message notification is enabled
	 * 
	 * @return boolean followMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isRetweetOfMe() {
		boolean retweetToMe = prefs.getBoolean("retweetofme",
				DEFAULT_COMMENT_MESSAGE);
		return retweetToMe;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether feed state message notification is enabled
	 * 
	 * @return boolean followMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isFeedState() {
		boolean feedStates = prefs.getBoolean("feedstates",
				DEFAULT_FEED_STATE_MESSAGE);
		return feedStates;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether feed albums message notification is enabled
	 * 
	 * @return boolean followMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isFeedAlbum() {
		boolean feedAlbum = prefs.getBoolean("feedalbums",
				DEFAULT_FEED_ALBUM_MESSAGE);
		return feedAlbum;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether feed blogs message notification is enabled
	 * 
	 * @return boolean followMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isFeedBlog() {
		boolean feedBlogs = prefs.getBoolean("feedblogs",
				DEFAULT_FEED_BLOG_MESSAGE);
		return feedBlogs;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether feed share message notification is enabled
	 * 
	 * @return boolean followMeaasge
	 */
	// ---------------------------------------------------------------------------------
	public boolean isFeedShare() {
		boolean feedShare = prefs.getBoolean("feedshare",
				DEFAULT_FEED_SHARE_MESSAGE);
		return feedShare;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Get font color
	 * 
	 * @return String color
	 */
	// ----------------------------------------------------------------------------------
	public String getFontColor() {
		String color = prefs.getString("fontcolorsettings", DEFAULT_FONT_COLOR);
		return color;
	}

	public void setFontColor(String color) {
		prefs.edit().putString("fontcolorsettings", color).commit();
	}

	public void setWallpaperPath(String path) {
		prefs.edit().putString("wallpaper", path).commit();
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Check whether show all image is enabled
	 * 
	 * @return boolean showAllImage
	 */
	// ---------------------------------------------------------------------------------
	public String getSelectionShowImage() {
		String str = prefs.getString("imagepreview", DEFAULT_SHOW_IMAGE);
		return str;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Get font size
	 * 
	 * @return String size
	 */
	// ----------------------------------------------------------------------------------
	public String getFontSize() {
		String size = prefs.getString("fontsizesettings", DEFAULT_FONT_SIZE);
		return size;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * get Gallery Custom
	 * 
	 * @param service
	 * @return
	 */
	// -----------------------------------------------------------------------------------
	public int[] getGalleryCustom(String service) {

		int[] result = {};
		if (service == null) {
			return result;
		}

		List<Integer> galleryCustomList = new ArrayList<Integer>();

		String galleryCustomFileName = service.replace(" ", "_");

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		SharedPreferences status = context.getSharedPreferences(
				galleryCustomFileName, Context.MODE_PRIVATE);

		// Get Statuses
		list.add(status.getBoolean("0", true));
		list.add(status.getBoolean("1", true));
		list.add(status.getBoolean("2", true));
		list.add(status.getBoolean("3", true));
		list.add(status.getBoolean("4", true));
		list.add(status.getBoolean("5", true));
		list.add(status.getBoolean("6", true));
		list.add(status.getBoolean("7", true));
		list.add(status.getBoolean("8", true));
		list.add(status.getBoolean("9", true));
		list.add(status.getBoolean("10", true));
		list.add(status.getBoolean("11", true));
		list.add(status.getBoolean("12", true));
		list.add(status.getBoolean("13", true));
		list.add(status.getBoolean("14", false));
		list.add(status.getBoolean("15", false));
		list.add(status.getBoolean("16", false));
		list.add(status.getBoolean("17", false));
		list.add(status.getBoolean("18", false));
		if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			list.add(status.getBoolean("19", true));
			list.add(status.getBoolean("20", true));
			list.add(status.getBoolean("21", true));
			list.add(status.getBoolean("22", true));
		} else {
			list.add(status.getBoolean("19", false));
			list.add(status.getBoolean("20", false));
			list.add(status.getBoolean("21", false));
			list.add(status.getBoolean("22", false));
		}

		if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
			list.add(status.getBoolean("23", true));
		} else {
			list.add(status.getBoolean("23", false));
		}

		if (service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_TENCENT)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			list.add(status.getBoolean("24", true));
		} else {
			list.add(status.getBoolean("24", false));
		}

		if (service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| service.equals(IGeneral.SERVICE_NAME_SOHU)) {
			list.add(status.getBoolean("25", true));
		} else {
			list.add(status.getBoolean("25", false));
		}

		if (service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			list.add(status.getBoolean("26", true));
		} else {
			list.add(status.getBoolean("26", false));
		}
		if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			list.add(status.getBoolean("27", true));
		} else {
			list.add(status.getBoolean("27", false));
		}
		if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			list.add(status.getBoolean("28", true));
		} else {
			list.add(status.getBoolean("28", false));
		}

		for (int i = 1; i < list.size(); i++) {
			if (list.get(i)) {
				galleryCustomList.add(i);
			}
		}
		result = new int[galleryCustomList.size()];
		for (int i = 0; i < galleryCustomList.size(); i++) {
			result[i] = galleryCustomList.get(i);
		}
		return result;
	}

	public String[] getGroupListSlug() {
		String[] result = {};

		return result;
	}
}
