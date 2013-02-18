package com.anhuioss.crowdroid.data.info;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.util.NtpClient;

//-----------------------------------------------------------------------------------
/**
 * This class is use for store the message from the xml files.<br>
 * You can get the value using getXXX() method.<br>
 * You can store the value using setXXX(String value) method.
 */
// -----------------------------------------------------------------------------------
public abstract class BasicInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 201202291418L;
	public static final String MESSAGEID = "messageId";
	public static final String STATUS = "status";
	public static final String TIME = "time";
	public static long nettime = 0;

	/** Message ID */
	private String messageId = "";
	/** favorite flag */
	private String favorite = "";

	/** Status */
	private String status = "";

	/** Retweeted Status */
	private String retweetedStatus = "";

	/** Updated Time */
	private String time = "";

	private String timeStamp = "";

	private String tem_time = "";

	private String translateFrom = "";

	private String translateTo = "";

	private String translateEnagine = "";

	/** User Info */
	private UserInfo userInfo;

	/** inReplyToStatusId to find reply who */
	private String inReplyToStatusId;

	private String originalTweets;

	private String commentUserImage;

	private Boolean commentUserVerfied;

	private String replyStatus;

	private String retweetCount = "";

	private String commentCount = "";

	private String feedType = "";

	private String ownerId = "";

	private String mediaId = "";

	private String blogPsw = "";

	private String postId = "";

	private String location = "";

	private String pos;

	private LocationInfo locationInfo;

	private ArrayList<LocationInfo> locationInfoList;

	private int myear;

	private int mmonth;

	private int mday;

	private int mhour;

	private int mminute;

	// -----------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// -----------------------------------------------------------------------------------
	public BasicInfo() {
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public void setRetweetCount(String retweetCount) {
		this.retweetCount = retweetCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

	public void setBlogPassword(String blogPsw) {
		this.blogPsw = blogPsw;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLocationInfo(LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
	}

	public void setLocationInfoList(ArrayList<LocationInfo> locationInfoList) {
		this.locationInfoList = locationInfoList;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Message ID<br>
	 * 
	 * @param messageId
	 */
	// -----------------------------------------------------------------------------------
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public void setPosition(String pos) {
		this.pos = pos;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * set the favorite method
	 */
	// -----------------------------------------------------------------------------------
	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Status.<br>
	 * 
	 * @param status
	 */
	// -----------------------------------------------------------------------------------
	public void setStatus(String status) {
		this.status = status;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Retweeted Status
	 * 
	 * @param retweetedStatus
	 */
	// -----------------------------------------------------------------------------------
	public void setRetweetedStatus(String retweetedStatus) {
		this.retweetedStatus = retweetedStatus;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set Time.<br>
	 * 
	 * @param time
	 */
	// -----------------------------------------------------------------------------------
	public void setTime(String time) {
		this.time = time;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set User Info.<br>
	 * 
	 * @param userInfo
	 * @return null
	 */
	// -----------------------------------------------------------------------------------
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public void setOriginalTweets(String originalTweets) {
		this.originalTweets = originalTweets;
	}

	public void setCommentUserImage(String commentUserImage) {
		this.commentUserImage = commentUserImage;
	}

	public void setCommentUserVerfied(Boolean commentUserVerfied) {
		this.commentUserVerfied = commentUserVerfied;
	}

	public void setReplyStatus(String replyStatus) {
		this.replyStatus = replyStatus;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Set in reply status id.<br>
	 * 
	 * @param inreplytostatusid
	 * @return null
	 */
	// -----------------------------------------------------------------------------------
	public void setinReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public void setTranslateFrom(String translateFrom) {
		this.translateFrom = translateFrom;
	}

	public void setTranslateTo(String translateTo) {
		this.translateTo = translateTo;
	}

	public void setTranslateEngine(String translateEnagine) {
		this.translateEnagine = translateEnagine;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Message ID.<br>
	 * 
	 * @return messageId <br>
	 */
	// -----------------------------------------------------------------------------------
	public String getMessageId() {
		return messageId;
	}

	public String getPostId() {
		return postId;
	}

	public String getPosition() {
		return pos;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public String getMediaId() {
		return mediaId;
	}

	public String getFeedType() {
		return feedType;
	}

	public String getRetweetCount() {
		return retweetCount;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public String getBlogPassword() {
		return blogPsw;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * get the getinReplyToStatusId<br>
	 * 
	 * @return String<br>
	 */
	// -----------------------------------------------------------------------------------
	public String getinReplyToStatusId() {
		return inReplyToStatusId;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * get the favorite status<br>
	 * 
	 * @return String<br>
	 */
	// -----------------------------------------------------------------------------------
	public String getFavorite() {
		return favorite;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Status.<br>
	 * 
	 * @return status
	 */
	// -----------------------------------------------------------------------------------
	public String getStatus() {
		return status;
	}

	public String getOriginalTweets() {
		return originalTweets;
	}

	public String getCommentUserImage() {
		return commentUserImage;
	}

	public String getLocation() {
		return location;
	}

	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	public ArrayList<LocationInfo> getLocationInfoList() {
		return locationInfoList;
	}

	/**
	 * Get Retweeted Status
	 * 
	 * @return String retweetedStatus
	 */
	public String getRetweetedStatus() {
		return retweetedStatus;
	}

	public Boolean getCommentUserVerfied() {
		return commentUserVerfied;
	}

	public String getReplyStatus() {
		return replyStatus;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Time.<br>
	 * 
	 * @return time
	 */
	// -----------------------------------------------------------------------------------
	public String getTime() {
		return time;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * 设置自动翻译
	 * 
	 * @return
	 */
	public String getTranslateFrom() {
		return translateFrom;
	}

	public String getTranslateTo() {
		return translateTo;
	}

	public String getTranslateEngine() {
		return translateEnagine;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * get format time
	 * 
	 * @return format time
	 * @throws
	 */
	// -------------------------------------------------------------------------------------
	public String getFormatTime(String service, Context ctx) {

		String ttime = new String(time);
		if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
			return ttime;
		}

		// Prepare Format
		String DATE_PATTERN_OUT = "yyyy-MM-dd HH:mm:ss";
		String DATE_PATTERN_IN = null;
		if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| service.equals(IGeneral.SERVICE_NAME_SOHU)
				|| service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
			// local change to China and Japan ,time will be wrong

			String month = ttime.substring(4, 7);
			if (month.equals("Jan")) {
				ttime = ttime.replace(month, "01");
			} else if (month.equals("Feb")) {
				ttime = ttime.replace(month, "02");
			} else if (month.equals("Mar")) {
				ttime = ttime.replace(month, "03");
			} else if (month.equals("Apr")) {
				ttime = ttime.replace(month, "04");
			} else if (month.equals("May")) {
				ttime = ttime.replace(month, "05");
			} else if (month.equals("Jun")) {
				ttime = ttime.replace(month, "06");
			} else if (month.equals("Jul")) {
				ttime = ttime.replace(month, "07");
			} else if (month.equals("Aug")) {
				ttime = ttime.replace(month, "08");
			} else if (month.equals("Sep")) {
				ttime = ttime.replace(month, "09");
			} else if (month.equals("Oct")) {
				ttime = ttime.replace(month, "10");
			} else if (month.equals("Nov")) {
				ttime = ttime.replace(month, "11");
			} else if (month.equals("Dec")) {
				ttime = ttime.replace(month, "12");
			}
			ttime = ttime.substring(4);
			DATE_PATTERN_IN = "MM dd HH:mm:ss Z yyyy";
		} else {
			DATE_PATTERN_IN = "yyyy-MM-dd HH:mm:ss";
		}
		String formatTime;
		// 获取网络时间服务器时间
		// NtpClient client = new NtpClient();
		// long time = 0;
		// if (client.requestTime("pool.ntp.org", 30000)) {
		// time = client.getNtpTime() + System.nanoTime() / 1000
		// - client.getNtpTimeReference();
		// } else {
		// time = System.currentTimeMillis();
		// }
		final Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(nettime);
		myear = mCalendar.get(Calendar.YEAR);
		mmonth = mCalendar.get(Calendar.MONTH);
		mday = mCalendar.get(Calendar.DAY_OF_MONTH);
		mhour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mminute = mCalendar.get(Calendar.MINUTE);

		Calendar scalendar = Calendar.getInstance();

		// Get Date Object from String
		SimpleDateFormat sourceFormat = new SimpleDateFormat(DATE_PATTERN_IN);
		Date date = null;
		try {
			date = sourceFormat.parse(ttime);
			scalendar.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		long stime = scalendar.getTimeInMillis();
		long etime = nettime - stime;
		if (etime <= 0) {
			etime = 0;
		}
		if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			// etime = etime - 6*60 * 1000;
		}
		if (etime >= 1000 * 60 * 60 * 48) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_OUT);
			formatTime = sdf.format(date);
		} else if (mday != scalendar.get(Calendar.DAY_OF_MONTH)) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			formatTime = (String) ctx.getResources().getString(
					R.string.yestoday)
					+ "" + sdf.format(date);
		} else if (etime < 1000 * 60) {
			formatTime = String.valueOf(etime / 1000)
					+ (String) ctx.getResources().getString(R.string.seconds);
		} else if (etime < 1000 * 60 * 60) {
			formatTime = String.valueOf(etime / (1000 * 60))
					+ (String) ctx.getResources().getString(R.string.minutes);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			formatTime = (String) ctx.getResources().getString(R.string.today)
					+ " " + sdf.format(date);
		}

		// Get String from Date

		return formatTime;

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get User Info.<br>
	 * 
	 * @return userInfo
	 */
	// -----------------------------------------------------------------------------------
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public static void setNetTime() {
		NtpClient client = new NtpClient();

		if (client.requestTime("pool.ntp.org", 3000)) {
			nettime = client.getNtpTime() + System.nanoTime() / 1000
					- client.getNtpTimeReference();
		} else {
			nettime = System.currentTimeMillis();
		}
	}

}