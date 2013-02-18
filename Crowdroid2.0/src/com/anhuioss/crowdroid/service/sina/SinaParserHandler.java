package com.anhuioss.crowdroid.service.sina;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.anhuioss.crowdroid.data.info.POIinfo;
import com.anhuioss.crowdroid.data.info.RoadInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TokenInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.data.info.linesInfo;
import com.anhuioss.crowdroid.data.info.stationInfo;
import com.anhuioss.crowdroid.data.info.transfersInfo;
import com.anhuioss.crowdroid.data.info.busrouteInfo;
import com.anhuioss.crowdroid.dialog.UserSelectDialog;

public class SinaParserHandler {

	// public static ArrayList<TimeLineInfo> parseTimeline(String msg)
	// throws JSONException {
	//
	// ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
	// if (msg == null) {
	// return jsonInfoList;
	// }
	//
	// try {
	// JSONArray jArray = new JSONArray(msg);
	//
	// JSONObject jObject;
	// JSONObject userObject;
	// UserInfo userInfo = null;
	// TimeLineInfo timeLineInfo = null;
	// for (int i = 0; i < jArray.length(); i++) {
	//
	// jObject = (JSONObject) jArray.get(i);
	// userInfo = new UserInfo();
	// timeLineInfo = new TimeLineInfo();
	//
	// timeLineInfo.setTime(jObject.getString("created_at"));
	// timeLineInfo.setMessageId(jObject.getString("id"));
	//
	// // Status And Image URL
	// String imageUrl = "";
	// if (!jObject.isNull("thumbnail_pic")) {
	// imageUrl = "\n" + jObject.getString("thumbnail_pic");
	// }
	// timeLineInfo.setStatus(jObject.getString("text") + imageUrl);
	// timeLineInfo.setFavorite(jObject.getString("favorited"));
	//
	// userObject = jObject.getJSONObject("user");
	//
	// userInfo.setUid(userObject.getString("id"));
	// userInfo.setScreenName(userObject.getString("screen_name"));
	// userInfo.setDescription(userObject.getString("description"));
	// userInfo.setUserImageURL(userObject
	// .getString("profile_image_url"));
	// userInfo.setFollowerCount(userObject
	// .getString("followers_count"));
	// userInfo.setFollowCount(userObject.getString("friends_count"));
	// userInfo.setVerified(userObject.getString("verified"));
	// try {
	//
	// if (jObject.has("retweeted_status")) {
	// if (jObject.getString("retweeted_status") != null) {
	// JSONObject retweetObject = jObject
	// .getJSONObject("retweeted_status");
	// timeLineInfo.setRetweeted(true);
	//
	// // Status And Image URL
	// String retweetedImageUrl = "";
	// if (!retweetObject.isNull("thumbnail_pic")) {
	// retweetedImageUrl = "\n"
	// + retweetObject
	// .getString("thumbnail_pic");
	// }
	// timeLineInfo.setRetweetedStatus(retweetObject
	// .getString("text") + retweetedImageUrl);
	//
	// JSONObject originalUserObject = retweetObject
	// .getJSONObject("user");
	// userInfo.setRetweetedScreenName(originalUserObject
	// .getString("screen_name"));
	// userInfo.setRetweetUserId(originalUserObject
	// .getString("id"));
	//
	// // userInfo.setScreenName(originalUserObject.getString("screen_name")
	// // + " RT by " +
	// // userObject.getString("screen_name"));
	// }
	// }
	//
	// } catch (JSONException e) {
	//
	// e.printStackTrace();
	//
	// }
	//
	// timeLineInfo.setUserInfo(userInfo);
	//
	// jsonInfoList.add(timeLineInfo);
	//
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// return jsonInfoList;
	// }

	public static ArrayList<TimeLineInfo> parseTimeline(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONObject status = new JSONObject(msg);

		if (status.has("statuses")) {
			JSONArray jArray = status.getJSONArray("statuses");

			JSONObject jObject;
			JSONObject userObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				if (jObject.has("deleted")) {
					continue;
				}

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

				timeLineInfo
						.setRetweetCount(jObject.getString("reposts_count"));
				timeLineInfo.setCommentCount(jObject
						.getString("comments_count"));

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

		}

		return jsonInfoList;
	}

	public static ArrayList<TimeLineInfo> parseFavouriteTimeline(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONObject status = new JSONObject(msg);

		if (status.has("favorites")) {
			JSONArray jArray = status.getJSONArray("favorites");

			JSONObject jObject;
			JSONObject favourite;
			JSONObject userObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				if (jObject.has("status")) {

					favourite = (JSONObject) jObject.getJSONObject("status");
					if (favourite.has("deleted")) {
						continue;
					}
					userInfo = new UserInfo();
					timeLineInfo = new TimeLineInfo();

					// if (!jObject.isNull("favorited_time")) {
					// timeLineInfo.setFavorite("true");
					// } else {
					// timeLineInfo.setFavorite("false");
					// }

					timeLineInfo.setTime(favourite.getString("created_at"));
					timeLineInfo.setMessageId(favourite.getString("id"));

					// Status And Image URL
					String imageUrl = "";
					if (!favourite.isNull("thumbnail_pic")) {
						imageUrl = "\n" + favourite.getString("thumbnail_pic");
					}
					timeLineInfo.setStatus(favourite.getString("text")
							+ imageUrl);
					timeLineInfo.setFavorite(favourite.getString("favorited"));

					timeLineInfo.setRetweetCount(favourite
							.getString("reposts_count"));
					timeLineInfo.setCommentCount(favourite
							.getString("comments_count"));

					userObject = favourite.getJSONObject("user");

					userInfo.setUid(userObject.getString("id"));
					userInfo.setScreenName(userObject.getString("screen_name"));
					userInfo.setDescription(userObject.getString("description"));
					userInfo.setUserImageURL(userObject
							.getString("profile_image_url"));
					userInfo.setFollowerCount(userObject
							.getString("followers_count"));
					userInfo.setFollowCount(userObject
							.getString("friends_count"));
					userInfo.setVerified(userObject.getString("verified"));
					try {
						if (favourite.has("retweeted_status")) {
							if (favourite.getString("retweeted_status") != null) {
								JSONObject retweetObject = favourite
										.getJSONObject("retweeted_status");
								timeLineInfo.setRetweeted(true);

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

							}
						}

					} catch (JSONException e) {

						e.printStackTrace();

					}

					timeLineInfo.setUserInfo(userInfo);

					jsonInfoList.add(timeLineInfo);

				}

			}
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
			// if(statuses.getString("next_cursor")!=null){
			String n = statuses.getString("next_cursor");
			FollowActivity.nextCursor = Long.valueOf(n);
			// }

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

	public static ArrayList<UserInfo> parseuserList(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONObject statuses = new JSONObject(msg);

			// Next Cursor
			// if(statuses.getString("next_cursor")!=null){
			// String n = statuses.getString("next_cursor");
			// FollowActivity.nextCursor = Long.valueOf(n);
			// }

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

	public static ArrayList<UserInfo> parseUserSearchInfo(String msg) {

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

	public static ArrayList<TimeLineInfo> parseKeywordSearchInfo(String msg)
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
		try {
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				if (jObject.has("deleted")) {
					continue;
				}

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

				// timeLineInfo
				// .setRetweetCount(jObject.getString("reposts_count"));
				// timeLineInfo.setCommentCount(jObject
				// .getString("comments_count"));

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

				timeLineInfo.setUserInfo(userInfo);

				jsonInfoList.add(timeLineInfo);
			}
		} catch (JSONException e) {

			e.printStackTrace();

		}

		return jsonInfoList;
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
		try {
			JSONObject comment = new JSONObject(message);
			JSONArray jArray = comment.getJSONArray("comments");
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

			count[0] = jsonObject.getString("follower");
			count[1] = jsonObject.getString("cmt");

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
			JSONObject comment = new JSONObject(message);
			JSONArray jsonArray = comment.getJSONArray("comments");
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
				locationInfo.setLocationAddress(jObject.getString("address")
						.replace("[]", ""));
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

		JSONObject lbsObject = new JSONObject(msg);

		JSONArray jArray = lbsObject.getJSONArray("statuses");

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

						// {"weight":"310312","2926":"文字"}
						String s1 = status.substring(2, 8);

						if ("weight".equals(s1)) {
							String id = status.substring(
									status.indexOf(",") + 2,
									status.lastIndexOf(":") - 1);
							String s = status.substring(
									status.lastIndexOf(":") + 2,
									status.length() - 2);
							timeLineInfo.setStatusId(id);
							timeLineInfo.setStatus(s);
						} else {
							// {"10":"旅游","weight":"3266801"}
							timeLineInfo.setStatusId(status.substring(2,
									status.indexOf(":") - 1));
							timeLineInfo.setStatus(status.substring(
									status.indexOf(":") + 2,
									status.indexOf(",") - 1));
						}

						timeLineInfoList.add(timeLineInfo);
					}

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return timeLineInfoList;
	}

	public static ArrayList<POIinfo> parsenearPOI(String msg)
			throws JSONException {

		ArrayList<POIinfo> jsonInfoList = new ArrayList<POIinfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONObject status = new JSONObject(msg);

		if (status.has("pois")) {
			JSONArray jArray = status.getJSONArray("pois");

			JSONObject jObject;
			JSONObject userObject;
			// UserInfo userInfo = null;
			POIinfo poiInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				// userInfo = new UserInfo();
				poiInfo = new POIinfo();

				poiInfo.setpoiid(jObject.getString("poiid"));
				poiInfo.settitle(jObject.getString("title"));
				poiInfo.setaddress(jObject.getString("address"));
				poiInfo.setcategory(jObject.getString("category"));
				poiInfo.setcityare(jObject.getString("city"),
						jObject.getString("province"),
						jObject.getString("country"));
				poiInfo.setphone(jObject.getString("phone"));
				poiInfo.setcategorys(jObject.getString("categorys"));
				poiInfo.setcategorys_name(jObject.getString("category_name"));
				poiInfo.setcoordinate(jObject.getString("lon"),
						jObject.getString("lat"));
				poiInfo.seticon(jObject.getString("icon"));
				poiInfo.setpostcode(jObject.getString("postcode"));
				poiInfo.seturl(jObject.getString("url"));
				poiInfo.setcheckin_num(jObject.getInt("checkin_num"));
				poiInfo.setcheckin_user_num(jObject.getInt("checkin_user_num"));
				poiInfo.setphoto_num(jObject.getInt("photo_num"));
				poiInfo.setdistance(jObject.getInt("distance"));
				poiInfo.settip_num(jObject.getInt("tip_num"));
				poiInfo.settodo_num(jObject.getInt("todo_num"));

				jsonInfoList.add(poiInfo);

			}

		}

		return jsonInfoList;
	}

	public static ArrayList<POIinfo> parseuserPOI(String msg)
			throws JSONException {

		ArrayList<POIinfo> jsonInfoList = new ArrayList<POIinfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONObject status = new JSONObject(msg);

		if (status.has("pois")) {
			JSONArray jArray = status.getJSONArray("pois");

			JSONObject jObject;
			JSONObject userObject;
			// UserInfo userInfo = null;
			POIinfo poiInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				// userInfo = new UserInfo();
				poiInfo = new POIinfo();

				poiInfo.setpoiid(jObject.getString("poiid"));
				poiInfo.settitle(jObject.getString("title"));
				poiInfo.setaddress(jObject.getString("address"));
				poiInfo.setcategory(jObject.getString("category"));
				poiInfo.setcityare(jObject.getString("city"),
						jObject.getString("province"),
						jObject.getString("country"));
				poiInfo.setphone(jObject.getString("phone"));
				poiInfo.setcategorys(jObject.getString("categorys"));
				poiInfo.setcategorys_name(jObject.getString("category_name"));
				poiInfo.setcoordinate(jObject.getString("lon"),
						jObject.getString("lat"));
				poiInfo.seticon(jObject.getString("icon"));
				poiInfo.setpostcode(jObject.getString("postcode"));
				poiInfo.seturl(jObject.getString("url"));
				poiInfo.setcheckin_time(jObject.getString("checkin_time"));
				poiInfo.setcheckin_user_num(jObject.getInt("checkin_user_num"));
				// poiInfo.setphoto_num(jObject.getInt("photo_num"));
				// poiInfo.setdistance(jObject.getInt("distance"));
				// poiInfo.settip_num(jObject.getInt("tip_num"));
				// poiInfo.settodo_num(jObject.getInt("todo_num"));

				jsonInfoList.add(poiInfo);

			}

		}

		return jsonInfoList;
	}

	public static ArrayList<POIinfo> parsesearchPOI(String msg)
			throws JSONException {

		ArrayList<POIinfo> jsonInfoList = new ArrayList<POIinfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONObject status = new JSONObject(msg);

		if (status.has("pois")) {
			JSONArray jArray = status.getJSONArray("pois");

			JSONObject jObject;
			JSONObject userObject;
			// UserInfo userInfo = null;
			POIinfo poiInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				// userInfo = new UserInfo();
				poiInfo = new POIinfo();

				poiInfo.setpoiid(jObject.getString("pid"));
				poiInfo.settitle(jObject.getString("name"));
				if (jObject.getString("address").equals("[]")) {
					poiInfo.setaddress("");
				} else {
					poiInfo.setaddress(jObject.getString("address"));
				}

				poiInfo.setcategory(jObject.getString("category"));
				poiInfo.setcityare(jObject.getString("city_name"),
						jObject.getString("province_name"));
				poiInfo.setphone(jObject.getString("telephone"));
				// poiInfo.setcategorys(jObject.getString("categorys"));
				// poiInfo.setcategorys_name(jObject.getString("category_name"));
				poiInfo.setcoordinate(jObject.getString("longitude"),
						jObject.getString("latitude"));
				// poiInfo.seticon(jObject.getString("icon"));
				// poiInfo.setpostcode(jObject.getString("postcode"));
				// poiInfo.seturl(jObject.getString("url"));
				// poiInfo.setcheckin_time(jObject.getString("checkin_time"));
				// poiInfo.setcheckin_user_num(jObject.getInt("checkin_user_num"));
				// poiInfo.setphoto_num(jObject.getInt("photo_num"));
				// poiInfo.setdistance(jObject.getInt("distance"));
				// poiInfo.settip_num(jObject.getInt("tip_num"));
				// poiInfo.settodo_num(jObject.getInt("todo_num"));
				// "pid": [],
				// "longitude": "116.30987",
				// "latitude": "39.98438",
				// "name": "三个贵州人(理想国际大厦西)",
				// "city_name": "北京",
				// "province_name": "北京",
				// "address": "北四环西路58",
				// "telephone": "010-82607678",
				// "category": "50114",
				// "navigator": "",
				// "pic_url": ""
				jsonInfoList.add(poiInfo);

			}

		}

		return jsonInfoList;
	}

	public static ArrayList<RoadInfo> parsewaycar(String msg)
			throws JSONException {

		ArrayList<RoadInfo> jsonInfoList = new ArrayList<RoadInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONObject status = new JSONObject(msg);

		if (status.has("roads")) {
			JSONArray jArray = status.getJSONArray("roads");

			JSONObject jObject;
			JSONObject userObject;
			// UserInfo userInfo = null;
			POIinfo poiInfo = null;
			RoadInfo roadinfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				// userInfo = new UserInfo();
				// poiInfo = new POIinfo();
				roadinfo = new RoadInfo();

				// poiInfo.setpoiid(jObject.getString("pid"));
				// poiInfo.settitle(jObject.getString("name"));
				// poiInfo.setaddress(jObject.getString("address"));
				// poiInfo.setcategory(jObject.getString("category"));
				// poiInfo.setcityare(jObject.getString("city_name"),
				// jObject.getString("province_name"));
				// poiInfo.setphone(jObject.getString("telephone"));
				// // poiInfo.setcategorys(jObject.getString("categorys"));
				// //
				// poiInfo.setcategorys_name(jObject.getString("category_name"));
				// poiInfo.setcoordinate(jObject.getString("longitude"),
				// jObject.getString("latitude"));
				// jsonInfoList.add(poiInfo);

				roadinfo.setrid(jObject.getString("rid"));
				roadinfo.setAssist_info(jObject.getString("assist_info"));
				roadinfo.setbegin_coordinate(jObject
						.getString("begin_coordinate"));
				roadinfo.setcoordinates(jObject.getString("coordinates"));
				roadinfo.setdirection(jObject.getString("direction"));
				roadinfo.setend_coordinate(jObject.getString("end_coordinate"));
				roadinfo.setgrade(jObject.getString("grade"));
				roadinfo.setnacigation_tag(jObject.getString("navigation_tag"));
				roadinfo.setroad_length(jObject.getString("road_length"));
				roadinfo.setroad_name(jObject.getString("road_name"));
				roadinfo.setroad_sign(jObject.getString("road_sign"));
				roadinfo.setrun_time(jObject.getString("run_time"));
				jsonInfoList.add(roadinfo);
				// "rid": "1",
				// "road_name": "",
				// "coordinates": "",
				// "begin_coordinate": "",
				// "end_coordinate": "",
				// "road_length": "",
				// "road_sign": "",
				// "action": "",
				// "run_time": "",
				// "grade": "",
				// "direction": "",
				// "assist_info": "",
				// "navigation_tag": ""

			}

		}

		return jsonInfoList;
	}

	@SuppressWarnings("null")
	public static ArrayList<transfersInfo> parsewaybus(String msg)
			throws JSONException {

		ArrayList<transfersInfo> jsonInfoList = new ArrayList<transfersInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		JSONObject status = new JSONObject(msg);
		// "type": "0",
		// "city": "0010",
		// "begin_pid": "P010A00CHR9",
		// "begin_coordinate": "116.22106,39.90652",
		// "end_pid": "P010A00CWWJ",
		// "end_coordinate": "116.27505,40.00236",
		// "extension_info": "0",
		// "transfers": [

		if (status.has("transfers")) {
			JSONArray jArray = status.getJSONArray("transfers");

			JSONObject jObject;
			JSONObject userObject;
			// UserInfo userInfo = null;
			POIinfo poiInfo = null;
			transfersInfo lineinfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);

				// "result_id": "1",
				// "distance": "18534米",
				// "expense": "0",
				// "before_len": "160米",
				// "after_len": "820米",
				// "nav_count": "2",
				// "drive_coordinates": "116.22106,39.90652|116.22106,39.90594",
				// "lines": [
				// {
				// "seg_id": "1",
				// "name": "598路",
				// "distance": "4470米",
				// "after_len": "160米",
				// "stations_num": "7",
				// "stations": [
				// {
				// "name": "西直门",
				// "longitude": "116.21946",
				// "latitude": "116.21946",
				// "district": "110102",
				// "station_info": "地铁4号线(公益西桥-安河桥北)",
				// "address": "北京西直门",
				// "telephone": ""
				// },

				// userInfo = new UserInfo();
				// poiInfo = new POIinfo();
				lineinfo = new transfersInfo();
				lineinfo.setresult_id(jObject.getString("result_id"));
				lineinfo.setdistance(jObject.getString("distance"));
				// lineinfo.setexpense(jObject.getString("expense"));
				lineinfo.setbefore_len(jObject.getString("before_len"));
				lineinfo.setafter_len(jObject.getString("after_len"));
				lineinfo.setnav_count(jObject.getString("nav_count"));
				// lineinfo.setdrive_coordinates(jObject.getString("drive_coordinates"));
				JSONArray lines = jObject.getJSONArray("lines");
				linesInfo linesinfo = null;
				JSONObject lObject;
				ArrayList<linesInfo> linesInfoList = new ArrayList<linesInfo>();
				for (int j = 0; j < lines.length(); j++) {

					lObject = (JSONObject) lines.get(j);
					linesinfo = new linesInfo();
					// String name=lObject.getString("name");
					linesinfo.setseg_id(lObject.getString("seg_id"));
					linesinfo.setname(lObject.getString("name"));
					linesinfo.setdistance(lObject.getString("distance"));
					linesinfo.setafter_len(lObject.getString("after_len"));
					linesinfo
							.setstations_num(lObject.getString("stations_num"));
					if (!lObject.getString("stations_num").equals("0")) {
						JSONArray stations = lObject.getJSONArray("stations");
						stationInfo stationinfo = null;
						JSONObject sObject;
						ArrayList<stationInfo> stationInfoList = new ArrayList<stationInfo>();
						for (int k = 0; k < stations.length(); k++) {
							sObject = (JSONObject) stations.get(k);
							stationinfo = new stationInfo();
							stationinfo.setname(sObject.getString("name"));
							stationinfo
									.setaddress(sObject.getString("address"));
							stationinfo.setdistrict(sObject
									.getString("district"));
							stationinfo.setlatitude(sObject
									.getString("latitude"));
							stationinfo.setlongitude(sObject
									.getString("longtitude"));
							stationinfo.setstation_info(sObject
									.getString("station_info"));
							stationInfoList.add(stationinfo);
						}
						linesinfo.setstations(stationInfoList);
					}
					linesInfoList.add(linesinfo);

				}
				lineinfo.setlines(linesInfoList);
				jsonInfoList.add(lineinfo);

				// poiInfo.setpoiid(jObject.getString("pid"));
				// poiInfo.settitle(jObject.getString("name"));
				// poiInfo.setaddress(jObject.getString("address"));
				// poiInfo.setcategory(jObject.getString("category"));
				// poiInfo.setcityare(jObject.getString("city_name"),
				// jObject.getString("province_name"));
				// poiInfo.setphone(jObject.getString("telephone"));
				// // poiInfo.setcategorys(jObject.getString("categorys"));
				// //
				// poiInfo.setcategorys_name(jObject.getString("category_name"));
				// poiInfo.setcoordinate(jObject.getString("longitude"),
				// jObject.getString("latitude"));
				// jsonInfoList.add(poiInfo);

				// roadinfo.setrid(jObject.getString("rid"));
				// roadinfo.setAssist_info(jObject.getString("assist_info"));
				// roadinfo.setbegin_coordinate(jObject.getString("begin_coordinate"));
				// roadinfo.setcoordinates(jObject.getString("coordinates"));
				// roadinfo.setdirection(jObject.getString("direction"));
				// roadinfo.setend_coordinate(jObject.getString("end_coordinate"));
				// roadinfo.setgrade(jObject.getString("grade"));
				// roadinfo.setnacigation_tag(jObject.getString("navigation_tag"));
				// roadinfo.setroad_length(jObject.getString("road_length"));
				// roadinfo.setroad_name(jObject.getString("road_name"));
				// roadinfo.setroad_sign(jObject.getString("road_sign"));
				// roadinfo.setrun_time(jObject.getString("run_time"));
				// jsonInfoList.add(roadinfo);
				// "rid": "1",
				// "road_name": "",
				// "coordinates": "",
				// "begin_coordinate": "",
				// "end_coordinate": "",
				// "road_length": "",
				// "road_sign": "",
				// "action": "",
				// "run_time": "",
				// "grade": "",
				// "direction": "",
				// "assist_info": "",
				// "navigation_tag": ""

			}

		}

		return jsonInfoList;
	}

}
