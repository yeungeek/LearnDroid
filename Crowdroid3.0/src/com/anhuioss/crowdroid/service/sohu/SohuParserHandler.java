package com.anhuioss.crowdroid.service.sohu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;

public class SohuParserHandler {

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
				if (!jObject.isNull("small_pic")) {
					imageUrl = "\n" + jObject.getString("small_pic");
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
							if (!retweetObject.isNull("small_pic")) {
								retweetedImageUrl = "\n"
										+ retweetObject.getString("small_pic");
							}
							timeLineInfo.setRetweetedStatus(retweetObject
									.getString("text") + retweetedImageUrl);

							JSONObject originalUserObject = retweetObject
									.getJSONObject("user");
							userInfo.setRetweetedScreenName(originalUserObject
									.getString("screen_name"));
							userInfo.setRetweetUserId(originalUserObject
									.getString("id"));

							//
							userInfo.setScreenName(originalUserObject
									.getString("screen_name")
									+ " RT by "
									+ userObject.getString("screen_name"));
						}
					} else {
						String retweetStatus = jObject
								.getString("in_reply_to_status_text");
						if (retweetStatus != null && !retweetStatus.equals("")) {
							timeLineInfo.setRetweeted(true);
							// if(imageUrl !=null && !imageUrl.equals("")){
							// timeLineInfo.setRetweetedStatus(retweetStatus+imageUrl);
							// }else{
							timeLineInfo.setRetweetedStatus(retweetStatus);
							// }

							userInfo.setRetweetedScreenName(jObject
									.getString("in_reply_to_screen_name"));
							userInfo.setRetweetUserId(jObject
									.getString("in_reply_to_user_id"));
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
		UserInfo senderInfo = null;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < jArray.length(); i++) {

			jObject = (JSONObject) jArray.get(i);
			senderInfo = new UserInfo();
			timeLineInfo = new TimeLineInfo();

			timeLineInfo.setMessageId(jObject.getString("id"));
			timeLineInfo.setTime(jObject.getString("created_at"));
			timeLineInfo.setStatus(jObject.getString("text"));

			JSONObject senderObject = jObject.getJSONObject("sender");
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
		UserInfo recipientInfo = null;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < jArray.length(); i++) {

			jObject = (JSONObject) jArray.get(i);
			recipientInfo = new UserInfo();
			timeLineInfo = new TimeLineInfo();

			timeLineInfo.setMessageId(jObject.getString("id"));
			timeLineInfo.setTime(jObject.getString("created_at"));
			timeLineInfo.setStatus(jObject.getString("text"));

			JSONObject recipientObject = jObject.getJSONObject("recipient");
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

			// Next Cursor
			String n = statuses.getString("cursor_id");
			FollowedActivity.nextCursor = Long.valueOf(n);

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
			String n = statuses.getString("cursor_id");
			FollowedActivity.nextCursor = Long.valueOf(n);

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
			String n = statuses.getString("cursor_id");
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

	public static ArrayList<TrendInfo> parseTrendList(String message) {

		return null;
	}

	public static ArrayList<TrendInfo> parseTrendListByType(String message) {
		return null;
	}

	// 获得指定微博的评论列表
	public static ArrayList<TimeLineInfo> parseCommentTimeline(String message) {
		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (message == null) {
			return jsonInfoList;
		}
		JSONArray jArray;
		try {
			jArray = new JSONArray(message);
			JSONObject subJObject;
			JSONObject userObject;
			TimeLineInfo timeLineInfo = null;
			UserInfo userInfo = null;
			for (int i = 0; i < jArray.length(); i++) {
				subJObject = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				timeLineInfo.setTime(subJObject.getString("created_at"));
				timeLineInfo.setStatus(subJObject.getString("text"));

				userObject = subJObject.getJSONObject("user");
				userInfo.setUid(userObject.getString("id"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setVerified(userObject.getString("verified"));

				timeLineInfo.setUserInfo(userInfo);

				jsonInfoList.add(timeLineInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonInfoList;
	}

	public static ArrayList<EmotionInfo> parseEmotions(String message) {
		return null;
	}

	public static String[] parseUnreadMessage(String message) {
		String[] count = new String[2];
		if (message == null) {
			return count;
		}

		try {
			JSONObject jsonObject = new JSONObject(message);

			count[0] = jsonObject.getString("new_followers_count");
			count[1] = jsonObject.getString("new_comment_count");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return count;

	}

	public static ArrayList<UserInfo> parseHotUsers(String message) {
		return null;
	}

	// 获取用户对我的评论列表
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
			TimeLineInfo timeLineInfo = null;
			UserInfo userInfo = null;
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

				timeLineInfo.setStatusId(jObject
						.getString("in_reply_to_status_id"));
				timeLineInfo.setOriginalTweets(jObject
						.getString("in_reply_to_status_text"));
				timeLineInfo.setCommentUserImage(userObject
						.getString("profile_image_url"));
				timeLineInfo.setCommentUserVerfied(userObject
						.getBoolean("verified"));
				timeLineInfo.setReplyStatus(jObject
						.getString("in_reply_to_status_text"));
				timeLineInfo.setMessageId(jObject.getString("id"));

				timeLineInfo.setUserInfo(userInfo);
				timeLineInfolist.add(timeLineInfo);
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}
		return timeLineInfolist;

	}

	public static TimeLineInfo parseTimelineById(String message)
			throws JSONException {

		JSONObject jObject = new JSONObject(message);
		UserInfo userInfo = new UserInfo();
		TimeLineInfo timeLineInfo = new TimeLineInfo();
		if (message == null) {
			return timeLineInfo;
		}

		timeLineInfo.setTime(jObject.getString("created_at"));
		timeLineInfo.setMessageId(jObject.getString("id"));

		String retweetedStatus = jObject.getString("in_reply_to_status_text");
		if (retweetedStatus != null && !retweetedStatus.equals("")) {
			timeLineInfo.setRetweeted(true);
			timeLineInfo.setStatus(retweetedStatus);
			userInfo.setRetweetedScreenName(jObject
					.getString("in_reply_to_screen_name"));
			userInfo.setRetweetUserId(jObject.getString("in_reply_to_user_id"));
		}

		// Status And Image URL
		String imageUrl = "";
		if (!jObject.isNull("small_pic")) {
			imageUrl = "\n" + jObject.getString("small_pic");
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

		timeLineInfo.setUserInfo(userInfo);

		return timeLineInfo;
	}

	public static ArrayList<TimeLineInfo> parseTimelineWithSearch(String msg)
			throws JSONException {
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		JSONObject jObject = new JSONObject(msg);
		if (jObject.has("statuses")) {
			String jStatuses = jObject.getString("statuses");

			return parseTimeline(jStatuses);
		} else if (jObject.has("results")) {
			try {
				JSONArray jArray = jObject.getJSONArray("results");
				JSONObject result;
				UserInfo userInfo = null;
				TimeLineInfo timeLineInfo = null;
				for (int i = 0; i < jArray.length(); i++) {

					result = (JSONObject) jArray.get(i);
					userInfo = new UserInfo();
					timeLineInfo = new TimeLineInfo();

					timeLineInfo.setTime(result.getString("created_at"));
					timeLineInfo.setMessageId(result.getString("id"));
					timeLineInfo.setStatus(result.getString("text"));

					userInfo.setUid(result.getString("from_user_id"));
					userInfo.setScreenName(result.getString("from_user"));
					userInfo.setUserImageURL(result
							.getString("profile_image_url"));

					timeLineInfo.setUserInfo(userInfo);

					timelineInfoList.add(timeLineInfo);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return timelineInfoList;
		}
		return null;
	}

	public static ArrayList<EmotionInfo> parseEmotionsFromFile(
			Context applicationContext) {
		// Prepare Data For Result
		ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();

		try {

			// Get File Name
			String tipsFileName = "sohu_emotions.txt";

			// Read Tips File And Get Content
			InputStreamReader inputReader = new InputStreamReader(
					applicationContext.getResources().getAssets()
							.open(tipsFileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = bufReader.readLine();
			while (line != null) {

				String[] data = line.split(",");
				if (data.length >= 2) {

					EmotionInfo emotionInfo = new EmotionInfo();
					// Name
					emotionInfo.setPhrase(data[0]);
					// URL
					emotionInfo.setUrl(data[1]);

					emotionList.add(emotionInfo);

				}

				line = bufReader.readLine();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return emotionList;
	}

}
