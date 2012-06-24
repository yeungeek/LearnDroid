package com.anhuioss.crowdroid.service.sina;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.UserSelectDialog;

public class SinaParserHandler {

	public static ArrayList<TimeLineInfo> parseTimeline(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		try {
			JSONArray jArray = new JSONArray(msg);

			JSONObject jObject;
			JSONObject userObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();

				timeLineInfo.setTime(jObject.getString("created_at"));
				timeLineInfo.setMessageId(jObject.getString("id"));

				// Status And Image URL
				String imageUrl = "";
				if (!jObject.isNull("thumbnail_pic")) {
					imageUrl = "\n" + jObject.getString("thumbnail_pic");
				}
				timeLineInfo.setStatus(jObject.getString("text") + imageUrl);
				timeLineInfo.setFavorite(jObject.getString("favorited"));

				userObject = jObject.getJSONObject("user");

				userInfo.setUid(userObject.getString("id"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setVerified(userObject.getString("verified"));
				try {

					if (jObject.has("retweeted_status")) {
						if (jObject.getString("retweeted_status") != null) {
							JSONObject retweetObject = jObject
									.getJSONObject("retweeted_status");
							timeLineInfo.setRetweeted(true);

							// Status And Image URL
							String retweetedImageUrl = "";
							if (!retweetObject.isNull("thumbnail_pic")) {
								retweetedImageUrl = "\n"
										+ retweetObject
												.getString("thumbnail_pic");
							}
							timeLineInfo.setRetweetedStatus(retweetObject
									.getString("text") + retweetedImageUrl);

							JSONObject originalUserObject = retweetObject
									.getJSONObject("user");
							userInfo.setRetweetedScreenName(originalUserObject
									.getString("screen_name"));
							userInfo.setRetweetUserId(originalUserObject
									.getString("id"));

							// userInfo.setScreenName(originalUserObject.getString("screen_name")
							// + " RT by " +
							// userObject.getString("screen_name"));
						}
					}

				} catch (JSONException e) {

					e.printStackTrace();

				}

				timeLineInfo.setUserInfo(userInfo);

				jsonInfoList.add(timeLineInfo);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonInfoList;
	}

	public static ArrayList<TimeLineInfo> parseDirectMessageReceived(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONArray jArray = new JSONArray(msg);
		JSONObject jObject;
		JSONObject senderObject;
		UserInfo senderInfo = null;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < jArray.length(); i++) {

			jObject = (JSONObject) jArray.get(i);
			senderInfo = new UserInfo();
			timeLineInfo = new TimeLineInfo();

			timeLineInfo.setMessageId(jObject.getString("id"));
			timeLineInfo.setTime(jObject.getString("created_at"));
			timeLineInfo.setStatus(jObject.getString("text"));

			senderObject = jObject.getJSONObject("sender");
			senderInfo.setUid(senderObject.getString("id"));
			senderInfo.setScreenName(senderObject.getString("screen_name"));
			senderInfo.setDescription(senderObject.getString("description"));
			senderInfo.setUserImageURL(senderObject
					.getString("profile_image_url"));
			senderInfo.setFollowerCount(senderObject
					.getString("followers_count"));
			senderInfo.setFollowCount(senderObject.getString("friends_count"));
			senderInfo.setVerified(senderObject.getString("verified"));

			timeLineInfo.setUserInfo(senderInfo);

			jsonInfoList.add(timeLineInfo);

		}

		return jsonInfoList;
	}

	public static ArrayList<TimeLineInfo> parseDirectMessageSent(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONArray jArray = new JSONArray(msg);
		JSONObject jObject;
		JSONObject recipientObject;
		UserInfo recipientInfo = null;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < jArray.length(); i++) {

			jObject = (JSONObject) jArray.get(i);
			recipientInfo = new UserInfo();
			timeLineInfo = new TimeLineInfo();

			timeLineInfo.setMessageId(jObject.getString("id"));
			timeLineInfo.setTime(jObject.getString("created_at"));
			timeLineInfo.setStatus(jObject.getString("text"));

			recipientObject = jObject.getJSONObject("recipient");
			recipientInfo.setUid(recipientObject.getString("id"));
			recipientInfo.setScreenName(recipientObject
					.getString("screen_name"));
			recipientInfo.setDescription(recipientObject
					.getString("description"));
			recipientInfo.setUserImageURL(recipientObject
					.getString("profile_image_url"));
			recipientInfo.setFollowerCount(recipientObject
					.getString("followers_count"));
			recipientInfo.setFollowCount(recipientObject
					.getString("friends_count"));
			recipientInfo.setVerified(recipientObject.getString("verified"));

			timeLineInfo.setUserInfo(recipientInfo);

			jsonInfoList.add(timeLineInfo);

		}

		return jsonInfoList;
	}

	public static ArrayList<UserInfo> parseFollowersInfo(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONObject statuses = new JSONObject(msg);

			// Previous Cursor
			String p = statuses.getString("previous_cursor");
			UserSelectDialog.preCursor = Long.valueOf(p);
			String n = statuses.getString("next_cursor");
			UserSelectDialog.nextCursor = Long.valueOf(n);

			// Users
			JSONArray users = statuses.getJSONArray("users");

			// Get Status, User
			JSONObject status;
			UserInfo userInfo;
			for (int i = 0; i < users.length(); i++) {

				status = (JSONObject) users.get(i);

				// User
				userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));
				userInfo.setVerified(status.getString("verified"));

				// Add To Result
				userInfoList.add(userInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;
	}

	public static ArrayList<UserInfo> parseFollowersList(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONObject statuses = new JSONObject(msg);

			// Next Cursor
			String n = statuses.getString("next_cursor");
			FollowedActivity.nextCursor = Long.valueOf(n);

			// Users
			JSONArray users = statuses.getJSONArray("users");

			// Get Status, User
			JSONObject status;
			UserInfo userInfo;
			for (int i = 0; i < users.length(); i++) {

				status = (JSONObject) users.get(i);

				// User
				userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));
				userInfo.setVerified(status.getString("verified"));

				// Add To Result
				userInfoList.add(userInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;
	}

	public static ArrayList<UserInfo> parseFriendsList(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONObject statuses = new JSONObject(msg);

			// Next Cursor
			String n = statuses.getString("next_cursor");
			FollowActivity.nextCursor = Long.valueOf(n);

			// Users
			JSONArray users = statuses.getJSONArray("users");

			// Get Status, User
			JSONObject status;
			UserInfo userInfo = null;
			for (int i = 0; i < users.length(); i++) {

				status = (JSONObject) users.get(i);

				// User
				userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));
				userInfo.setVerified(status.getString("verified"));

				// Add To Result
				userInfoList.add(userInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;

	}

	public static UserInfo parseUserInfo(String msg) throws JSONException {

		UserInfo userInfo = new UserInfo();
		if (msg == null) {
			return userInfo;
		}

		JSONObject user = new JSONObject(msg);

		userInfo.setUid(user.getString("id"));
		userInfo.setScreenName(user.getString("screen_name"));
		userInfo.setDescription(user.getString("description"));
		userInfo.setUserImageURL(user.getString("profile_image_url"));
		userInfo.setFollowerCount(user.getString("followers_count"));
		userInfo.setFollowCount(user.getString("friends_count"));
		userInfo.setFollowing(user.getString("following"));
		userInfo.setStatusCount(user.getString("statuses_count"));
		userInfo.setVerified(user.getString("verified"));

		return userInfo;
	}

	public static ArrayList<UserInfo> parseStrangersInfo(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			UserInfo userInfo = null;
			for (int i = 0; i < statuses.length(); i++) {

				status = (JSONObject) statuses.get(i);

				// User
				userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));
				userInfo.setVerified(status.getString("verified"));

				// Add To Result
				userInfoList.add(userInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;
	}

	public static String[] parseRelation(String msg) {

		// Prepare Result
		String[] relation = new String[2];
		if (msg == null) {
			return relation;
		}

		try {

			// Root
			JSONObject root = new JSONObject(msg);

			// Relationship
			relation[0] = root.getString("friends");
			relation[1] = relation[0];

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return relation;
	}

	public static String[] parseRelationNew(String msg) {
		String[] relation = new String[2];
		if (msg == null) {
			return relation;
		}
		try {
			JSONObject root = new JSONObject(msg);
			JSONObject relationship = root.getJSONObject("target");

			relation[0] = relationship.getString("following");
			relation[1] = relationship.getString("followed_by");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return relation;
	}

	public static ArrayList<TrendInfo> parseTrendList(String msg) {

		// Prepare Result
		ArrayList<TrendInfo> trendInfoList = new ArrayList<TrendInfo>();
		if (msg == null) {
			return trendInfoList;
		}

		try {

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			TrendInfo trendInfo = null;
			for (int i = 0; i < statuses.length(); i++) {

				status = (JSONObject) statuses.get(i);

				// Trend Information
				trendInfo = new TrendInfo();

				trendInfo.setTrendId(status.getString("trend_id"));
				trendInfo.setNum(status.getString("num"));
				trendInfo.setHotword(status.getString("hotword"));

				// Add To Result
				trendInfoList.add(trendInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return trendInfoList;
	}

	public static ArrayList<TrendInfo> parseTrendListByType(String msg) {

		// Prepare Result
		ArrayList<TrendInfo> trendInfoList = new ArrayList<TrendInfo>();
		if (msg == null) {
			return trendInfoList;
		}

		try {

			// Statuses
			JSONObject statusObject = new JSONObject(msg);
			JSONObject trends = statusObject.getJSONObject("trends");

			// Calendar c = Calendar.getInstance();
			// Date d = c.getTime();
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			// String trendsName = sdf.format(d);
			// 获取时间对象
			String trendTimeStamp = msg.substring(msg.indexOf("trends") + 10,
					msg.indexOf("trends") + 26);

			JSONArray trendList = trends.getJSONArray(trendTimeStamp);

			// Get Status, User
			JSONObject status;
			TrendInfo trendInfo = null;
			for (int i = 0; i < trendList.length(); i++) {

				status = (JSONObject) trendList.get(i);

				// Trend Information
				trendInfo = new TrendInfo();

				trendInfo.setName(status.getString("name"));

				// Add To Result
				trendInfoList.add(trendInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return trendInfoList;

	}

	public static ArrayList<TimeLineInfo> parseCommentTimeline(String message) {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (message == null) {
			return jsonInfoList;
		}
		JSONArray jArray;
		try {
			jArray = new JSONArray(message);
			JSONObject jObject;
			JSONObject userObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {
				jObject = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				timeLineInfo.setTime(jObject.getString("created_at"));
				timeLineInfo.setStatus(jObject.getString("text"));

				userObject = jObject.getJSONObject("user");
				userInfo.setUid(userObject.getString("id"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));

				timeLineInfo.setUserInfo(userInfo);

				jsonInfoList.add(timeLineInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonInfoList;
	}

	public static ArrayList<EmotionInfo> parseEmotions(String message) {

		ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();
		if (message == null) {
			return emotionList;
		}

		try {

			JSONArray emotions = new JSONArray(message);
			JSONObject emotion;
			EmotionInfo emotionInfo = null;
			for (int i = 0; i < emotions.length(); i++) {

				emotion = emotions.getJSONObject(i);
				emotionInfo = new EmotionInfo();

				// Phrase
				if (emotion.has(EmotionInfo.PHRASE)) {
					emotionInfo
							.setPhrase(emotion.getString(EmotionInfo.PHRASE));
				}

				// Type
				if (emotion.has(EmotionInfo.TYPE)) {
					emotionInfo.setType(emotion.getString(EmotionInfo.TYPE));
				}

				// URL
				if (emotion.has(EmotionInfo.URL)) {
					emotionInfo.setUrl(emotion.getString(EmotionInfo.URL));
				}

				// Is hot
				if (emotion.has(EmotionInfo.IS_HOT)) {
					emotionInfo
							.setIsHot(emotion.getBoolean(EmotionInfo.IS_HOT));
				}

				// Is common
				if (emotion.has(EmotionInfo.IS_COMMON)) {
					emotionInfo.setIsCommon(emotion
							.getBoolean(EmotionInfo.IS_COMMON));
				}

				// Order number
				if (emotion.has(EmotionInfo.ORDER_NUMBER)) {
					emotionInfo.setOrderNumber(emotion
							.getInt(EmotionInfo.ORDER_NUMBER));
				}

				// Category
				if (emotion.has(EmotionInfo.CATEGORY)) {
					emotionInfo.setCategory(emotion
							.getString(EmotionInfo.CATEGORY));
				}

				emotionList.add(emotionInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return emotionList;

	}

	public static String[] parseUnreadMessage(String message) {

		String[] count = new String[2];
		if (message == null) {
			return count;
		}

		try {
			JSONObject jsonObject = new JSONObject(message);

			count[0] = jsonObject.getString("followers");
			count[1] = jsonObject.getString("comments");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return count;

	}

	public static ArrayList<UserInfo> parseHotUsers(String message) {

		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (message == null) {
			return userInfoList;
		}

		try {
			JSONArray jsonArray = new JSONArray(message);
			JSONObject userObject;
			UserInfo userInfo = null;
			for (int i = 0; i < jsonArray.length(); i++) {

				userObject = (JSONObject) jsonArray.get(i);

				userInfo = new UserInfo();

				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setStatusCount(userObject.getString("statuses_count"));
				userInfo.setVerified(userObject.getString("verified"));

				userInfoList.add(userInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userInfoList;
	}

	public static ArrayList<TimeLineInfo> parseUserCommentTimeline(
			String message) {

		ArrayList<TimeLineInfo> timeLineInfolist = new ArrayList<TimeLineInfo>();
		if (message == null) {
			return timeLineInfolist;
		}

		try {
			JSONArray jsonArray = new JSONArray(message);
			JSONObject jObject;
			JSONObject userObject;
			JSONObject statusObject;
			JSONObject originalUserObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				jObject = jsonArray.getJSONObject(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				timeLineInfo.setTime(jObject.getString("created_at"));
				timeLineInfo.setStatus(jObject.getString("text"));
				timeLineInfo.setinReplyToStatusId(jObject.getString("id"));

				userObject = jObject.getJSONObject("user");
				userInfo.setUid(userObject.getString("id"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setVerified(userObject.getString("verified"));

				statusObject = jObject.getJSONObject("status");
				timeLineInfo.setStatusId(statusObject.getString("id"));

				JSONObject status = jObject.getJSONObject("status");

				timeLineInfo.setOriginalTweets(status.getString("text"));
				originalUserObject = status.getJSONObject("user");
				timeLineInfo.setCommentUserImage(originalUserObject
						.getString("profile_image_url"));
				timeLineInfo.setCommentUserVerfied(originalUserObject
						.getBoolean("verified"));
				if (jObject.has("reply_comment")) {
					JSONObject reply = jObject.getJSONObject("reply_comment");
					timeLineInfo.setReplyStatus(reply.getString("text"));
					timeLineInfo.setMessageId(reply.getString("id"));
				} else {
					timeLineInfo.setReplyStatus(status.getString("text"));
					timeLineInfo.setMessageId(status.getString("id"));
				}

				timeLineInfo.setUserInfo(userInfo);

				timeLineInfolist.add(timeLineInfo);
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}
		return timeLineInfolist;

	}

	public static TimeLineInfo parseTimelineById(String msg)
			throws JSONException {

		JSONObject jObject = new JSONObject(msg);
		UserInfo userInfo = new UserInfo();
		TimeLineInfo timeLineInfo = new TimeLineInfo();
		if (msg == null) {
			return timeLineInfo;
		}

		timeLineInfo.setTime(jObject.getString("created_at"));
		timeLineInfo.setMessageId(jObject.getString("id"));

		// Status And Image URL
		String imageUrl = "";
		if (!jObject.isNull("thumbnail_pic")) {
			imageUrl = "\n" + jObject.getString("thumbnail_pic");
		}
		timeLineInfo.setStatus(jObject.getString("text") + imageUrl);
		timeLineInfo.setFavorite(jObject.getString("favorited"));

		JSONObject userObject = jObject.getJSONObject("user");

		userInfo.setUid(userObject.getString("id"));
		userInfo.setScreenName(userObject.getString("screen_name"));
		userInfo.setDescription(userObject.getString("description"));
		userInfo.setUserImageURL(userObject.getString("profile_image_url"));
		userInfo.setFollowerCount(userObject.getString("followers_count"));
		userInfo.setFollowCount(userObject.getString("friends_count"));
		userInfo.setVerified(userObject.getString("verified"));
		try {

			if (jObject.has("retweeted_status")) {
				if (jObject.getString("retweeted_status") != null) {
					JSONObject retweetObject = jObject
							.getJSONObject("retweeted_status");
					timeLineInfo.setRetweeted(true);

					// Status And Image URL
					String retweetedImageUrl = "";
					if (!retweetObject.isNull("thumbnail_pic")) {
						retweetedImageUrl = "\n"
								+ retweetObject.getString("thumbnail_pic");
					}
					timeLineInfo.setRetweetedStatus(retweetObject
							.getString("text") + retweetedImageUrl);

					JSONObject originalUserObject = retweetObject
							.getJSONObject("user");
					userInfo.setRetweetedScreenName(originalUserObject
							.getString("screen_name"));
					userInfo.setRetweetUserId(originalUserObject
							.getString("id"));

				}
			}

		} catch (JSONException e) {

			e.printStackTrace();

		}

		timeLineInfo.setUserInfo(userInfo);

		return timeLineInfo;
	}

	public static LocationInfo parseLocationInfo(String msg)
			throws JSONException {

		LocationInfo locationInfo = new LocationInfo();
		if (msg == null) {
			return locationInfo;
		}

		JSONObject location = new JSONObject(msg);
		JSONObject jObject;
		if (location.has("address")) {
			jObject = (JSONObject) location.getJSONObject("address");
			locationInfo.setLocationAddress(jObject.getString("street"));
			locationInfo.setLocationCityName(jObject.getString("city_name"));
			locationInfo.setLocationName(jObject.getString("poi_name"));
		}

		return locationInfo;
	}

	public static ArrayList<LocationInfo> parseLocationList(String msg)
			throws JSONException {

		ArrayList<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
		if (msg == null) {
			return locationInfoList;
		}

		JSONObject location = new JSONObject(msg);
		JSONObject jObject;
		JSONArray poiList;
		if (location.has("pois")) {
			LocationInfo locationInfo;
			poiList = (JSONArray) location.getJSONArray("pois");
			for (int i = 0; i < poiList.length(); i++) {
				jObject = (JSONObject) poiList.getJSONObject(i);
				locationInfo = new LocationInfo();
				locationInfo.setLocationName(jObject.getString("name"));
				locationInfo.setLocationAddress(jObject.getString("address"));
				locationInfo.setLocationCategory(jObject.getString("category"));
				locationInfo.setLocationLongitude(jObject
						.getString("longitude"));
				locationInfo.setLocationLatitude(jObject.getString("latitude"));
				locationInfoList.add(locationInfo);

			}
		}

		return locationInfoList;
	}

	public static ArrayList<TimeLineInfo> parseLBSTimeline(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONArray jArray = new JSONArray(msg);

		JSONObject jObject;
		JSONObject userObject;
		UserInfo userInfo = null;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < jArray.length(); i++) {

			jObject = (JSONObject) jArray.get(i);
			userInfo = new UserInfo();
			timeLineInfo = new TimeLineInfo();

			timeLineInfo.setTime(jObject.getString("created_at"));
			timeLineInfo.setMessageId(jObject.getString("id"));
			if (jObject.isNull("geo")) {
				continue;
			}

			// Status And Image URL
			String imageUrl = "";
			if (!jObject.isNull("thumbnail_pic")) {
				imageUrl = "\n" + jObject.getString("thumbnail_pic");
			}
			timeLineInfo.setStatus(jObject.getString("text") + imageUrl);
			timeLineInfo.setFavorite(jObject.getString("favorited"));

			userObject = jObject.getJSONObject("user");

			userInfo.setUid(userObject.getString("id"));
			userInfo.setScreenName(userObject.getString("screen_name"));
			userInfo.setDescription(userObject.getString("description"));
			userInfo.setUserImageURL(userObject.getString("profile_image_url"));
			userInfo.setFollowerCount(userObject.getString("followers_count"));
			userInfo.setFollowCount(userObject.getString("friends_count"));
			userInfo.setVerified(userObject.getString("verified"));
			try {

				if (jObject.has("retweeted_status")) {
					if (jObject.getString("retweeted_status") != null) {
						JSONObject retweetObject = jObject
								.getJSONObject("retweeted_status");
						timeLineInfo.setRetweeted(true);

						// Status And Image URL
						String retweetedImageUrl = "";
						if (!retweetObject.isNull("thumbnail_pic")) {
							retweetedImageUrl = "\n"
									+ retweetObject.getString("thumbnail_pic");
						}
						timeLineInfo.setRetweetedStatus(retweetObject
								.getString("text") + retweetedImageUrl);

						JSONObject originalUserObject = retweetObject
								.getJSONObject("user");
						userInfo.setRetweetedScreenName(originalUserObject
								.getString("screen_name"));
						userInfo.setRetweetUserId(originalUserObject
								.getString("id"));

						// userInfo.setScreenName(originalUserObject.getString("screen_name")
						// + " RT by " + userObject.getString("screen_name"));
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();

			}

			timeLineInfo.setUserInfo(userInfo);

			jsonInfoList.add(timeLineInfo);

		}

		return jsonInfoList;
	}

	public static ArrayList<TimeLineInfo> parseSuggestionTagsList(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}

		JSONArray jArray = new JSONArray(msg);

		JSONObject jObject;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < jArray.length(); i++) {

			jObject = (JSONObject) jArray.get(i);
			timeLineInfo = new TimeLineInfo();
			timeLineInfo.setStatusId(jObject.getString("id"));
			timeLineInfo.setStatus(jObject.getString("value"));
			timeLineInfoList.add(timeLineInfo);
		}

		return timeLineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseUserTagsList(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}
		try {
			JSONArray jArray = new JSONArray(msg);

			JSONObject jObject;
			TimeLineInfo timeLineInfo = null;
			if (jArray.length() > 0) {
				for (int i = 0; i < jArray.length(); i++) {

					jObject = (JSONObject) jArray.get(i);
					timeLineInfo = new TimeLineInfo();
					String status = jObject.toString();
					if (status.length() >= 2 && status.contains(":")) {
						timeLineInfo.setStatusId(status.substring(2,
								status.lastIndexOf(":") - 1));
						timeLineInfo.setStatus(status.substring(
								status.lastIndexOf(":") + 2,
								status.length() - 2));
						timeLineInfoList.add(timeLineInfo);
					}

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return timeLineInfoList;
	}

}
