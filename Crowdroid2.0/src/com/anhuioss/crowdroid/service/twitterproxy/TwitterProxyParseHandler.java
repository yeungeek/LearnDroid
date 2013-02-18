package com.anhuioss.crowdroid.service.twitterproxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.UserSelectDialog;

public class TwitterProxyParseHandler {

	// ----------------------------------------------------------------------------
	/**
	 * Analyze XML data for timeLine and return UserInformation
	 * 
	 * @param msg
	 * @return TwitterHandler.userInfo
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	// ----------------------------------------------------------------------------
	public static UserInfo parseUserInfo(String msg) {
		UserInfo userInfo = new UserInfo();
		try {
			JSONObject jsonObject = new JSONObject(msg);
			// json parse userInfo
			userInfo.setDescription(jsonObject.getString("description"));
			userInfo.setUserImageURL(jsonObject.getString("profile_image_url"));
			userInfo.setUid(jsonObject.getString("id"));
			userInfo.setFollowerCount(jsonObject.getString("followers_count"));
			userInfo.setDescription(jsonObject.getString("notifications"));
			userInfo.setFollowCount(jsonObject.getString("friends_count"));
			userInfo.setFollowing(jsonObject.getString("following"));
			userInfo.setScreenName(jsonObject.getString("screen_name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userInfo;

	}

	public static ArrayList<TimeLineInfo> parseTimeline(String message) {
		// Prepare Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		try {

			JSONArray jsonArray = new JSONArray(message);

			for (int i = 0; i < jsonArray.length(); i++) {

				// timeline
				TimeLineInfo timeLineInfo = new TimeLineInfo();

				JSONObject status = (JSONObject) jsonArray.get(i);
				timeLineInfo.setFavorite(status.getString("favorited"));
				timeLineInfo.setTime(status.getString("created_at"));
				timeLineInfo.setStatus(status.getString("text")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~").replaceAll("\\r|\\n|\\t|\\s", " "));
				timeLineInfo.setMessageId(status.getString("id"));
				timeLineInfo.setinReplyToStatusId(status
						.getString("in_reply_to_status_id"));

				// UserInfo
				UserInfo userInfo = new UserInfo();

				JSONObject userObject = status.getJSONObject("user");
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setDescription(userObject.getString("notifications"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setFollowing(userObject.getString("following"));
				userInfo.setScreenName(userObject.getString("screen_name"));

				// Add To Result
				timeLineInfo.setUserInfo(userInfo);
				timelineInfoList.add(timeLineInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return timelineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseDirectMessage(String message) {
		ArrayList<TimeLineInfo> directMessageInfoList = new ArrayList<TimeLineInfo>();

		try {

			// Statuses
			JSONArray statuses = new JSONArray(message);

			// Get Status, User
			for (int i = 0; i < statuses.length(); i++) {

				// Status
				TimeLineInfo directMessageInfo = new TimeLineInfo();

				JSONObject status = (JSONObject) statuses.get(i);
				directMessageInfo.setTime(status.getString("created_at"));
				directMessageInfo.setStatus(status.getString("text")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				directMessageInfo.setMessageId(status.getString("id"));

				// UserInfo
				UserInfo userInfo = new UserInfo();

				JSONObject userObject = status.getJSONObject("sender");

				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setDescription(userObject.getString("notifications"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setFollowing(userObject.getString("following"));
				userInfo.setScreenName(userObject.getString("screen_name"));

				// Add To Result
				directMessageInfo.setUserInfo(userInfo);
				directMessageInfoList.add(directMessageInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return directMessageInfoList;
	}

	public static UserInfo parseUserInformation(String message) {
		// Prepare Result
		UserInfo userInfo = new UserInfo();

		try {

			// Statuses
			JSONObject user = new JSONObject(message);

			// Users
			userInfo.setUserImageURL(user.getString("profile_image_url"));
			userInfo.setUid(user.getString("id"));
			userInfo.setFollowerCount(user.getString("followers_count"));
			userInfo.setDescription(user.getString("notifications"));
			userInfo.setDescription(user.getString("description"));
			userInfo.setFollowCount(user.getString("friends_count"));
			userInfo.setFollowing(user.getString("following"));
			userInfo.setStatusCount(user.getString("statuses_count"));
			userInfo.setScreenName(user.getString("screen_name"));
			userInfo.setListCount(user.getString("listed_count"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfo;
	}

	public static ArrayList<UserInfo> parseFriendsList(String message) {
		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {

			// Statuses
			JSONObject statuses = new JSONObject(message);

			// Next Cursor
			String n = statuses.getString("next_cursor");
			FollowActivity.nextCursor = Long.valueOf(n);

			// Users
			JSONArray users = statuses.getJSONArray("users");

			// Get Status, User
			for (int i = 0; i < users.length(); i++) {

				JSONObject status = (JSONObject) users.get(i);

				// User
				UserInfo userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("notifications"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));

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

	public static ArrayList<UserInfo> parseFollowersList(String message) {
		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {

			// Statuses
			JSONObject statuses = new JSONObject(message);

			// Next Cursor
			String n = statuses.getString("next_cursor");
			FollowedActivity.nextCursor = Long.valueOf(n);

			// Users
			JSONArray users = statuses.getJSONArray("users");

			// Get Status, User
			for (int i = 0; i < users.length(); i++) {

				JSONObject status = (JSONObject) users.get(i);

				// User
				UserInfo userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("notifications"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));

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

	public static String[] parseRelation(String message) {
		// Prepare Result
		String[] relation = new String[2];

		try {

			// Root
			JSONObject root = new JSONObject(message);

			// Relationship
			JSONObject relationship = root.getJSONObject("relationship");

			// Source
			JSONObject source = relationship.getJSONObject("source");
			relation[0] = source.getString("following");
			relation[1] = source.getString("followed_by");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return relation;
	}

	public static ArrayList<UserInfo> parseStrangersInfo(String message) {
		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {

			// Statuses
			JSONArray statuses = new JSONArray(message);

			// Get Status, User
			for (int i = 0; i < statuses.length(); i++) {

				JSONObject status = (JSONObject) statuses.get(i);

				// User
				UserInfo userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("notifications"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));

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

	public static ArrayList<TimeLineInfo> parseSearchInfo(String message) {
		// Prepare ResultList
		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

		try {

			JSONObject clientJSONObj = new JSONObject(message);
			JSONArray searchinfototal = clientJSONObj.getJSONArray("results");
			for (int i = 0; i < searchinfototal.length(); i++) {

				JSONObject searchinfosingle = searchinfototal.getJSONObject(i);

				TimeLineInfo timeLineInfo = new TimeLineInfo();

				timeLineInfo.setTime(searchinfosingle.getString("created_at")
						.replace(",", ""));
				timeLineInfo.setStatus(searchinfosingle.getString("text")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replaceAll("&quot;", "\""));
				timeLineInfo.setMessageId(searchinfosingle.getString("id"));

				UserInfo userinfo = new UserInfo();

				userinfo.setScreenName(searchinfosingle.getString("from_user"));
				userinfo.setUserImageURL(searchinfosingle
						.getString("profile_image_url"));
				userinfo.setUid(searchinfosingle.getString("from_user_id"));

				timeLineInfo.setUserInfo(userinfo);
				timeLineInfoList.add(timeLineInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		return timeLineInfoList;
	}

	public static ArrayList<UserInfo> parseFollowersInfo(String message) {
		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {

			JSONArray statuses = new JSONArray(message);
			if (statuses.length() > 0) {
				for (int i = 0; i < statuses.length(); i++) {

					JSONObject status = (JSONObject) statuses.get(i);

					// User
					UserInfo userInfo = new UserInfo();

					userInfo.setUserImageURL(status
							.getString("profile_image_url"));
					userInfo.setUid(status.getString("id"));
					userInfo.setFollowerCount(status
							.getString("followers_count"));
					userInfo.setDescription(status.getString("notifications"));
					userInfo.setDescription(status.getString("description"));
					userInfo.setFollowCount(status.getString("friends_count"));
					userInfo.setFollowing(status.getString("following"));
					userInfo.setScreenName(status.getString("screen_name"));

					// Add To Result
					userInfoList.add(userInfo);

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;
	}

	public static ArrayList<ListInfo> parseLists(String message) {

		String nextCursor = "-1";

		String previousCursor = "0";

		// Prepare Result
		ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();

		try {

			// Root
			JSONObject root = new JSONObject(message);

			// Next And Previous Cursor
			nextCursor = root.getString("next_cursor");
			previousCursor = root.getString("previous_cursor");

			// Lists
			JSONArray lists = root.getJSONArray("lists");

			// Get Status, User
			for (int i = 0; i < lists.length(); i++) {

				// Status
				ListInfo listInfo = new ListInfo();

				JSONObject list = (JSONObject) lists.get(i);
				listInfo.setMode(list.getString("mode"));
				listInfo.setSlug(list.getString("slug"));
				listInfo.setDescription(list.getString("description"));
				listInfo.setFollow(list.getString("following"));
				listInfo.setMemberCount(list.getString("member_count"));
				listInfo.setFullName(list.getString("full_name"));
				listInfo.setName(list.getString("name"));
				listInfo.setId(list.getString("id"));
				listInfo.setSubscriberCount(list.getString("subscriber_count"));
				listInfo.setNextCursor(nextCursor);
				listInfo.setPrevioutCursor(previousCursor);

				// UserInfo
				UserInfo userInfo = new UserInfo();

				JSONObject userObject = list.getJSONObject("user");
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setDescription(userObject.getString("notifications"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setFollowing(userObject.getString("following"));
				userInfo.setScreenName(userObject.getString("screen_name"));

				// Add To Result
				listInfo.setMode(userInfo.getScreenName());
				listInfo.setUserInfo(userInfo);
				listInfoList.add(listInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listInfoList;
	}

	public static ArrayList<TimeLineInfo> parseRetweetBy(String message) {
		// Prepare Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		try {

			// Statuses
			JSONArray statuses = new JSONArray(message);

			// Get Status, User
			for (int i = 0; i < statuses.length(); i++) {

				// Status
				TimeLineInfo timeLineInfo = new TimeLineInfo();

				JSONObject status = (JSONObject) statuses.get(i);
				timeLineInfo.setFavorite(status.getString("favorited"));
				timeLineInfo.setTime(status.getString("created_at"));
				timeLineInfo.setStatus(status.getString("text")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				timeLineInfo.setMessageId(status.getString("id"));
				timeLineInfo.setinReplyToStatusId(status
						.getString("in_reply_to_status_id"));

				// User
				UserInfo userInfo = new UserInfo();

				JSONObject retweetedStatus = status
						.getJSONObject("retweeted_status");
				JSONObject userObject = retweetedStatus.getJSONObject("user");
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setDescription(userObject.getString("notifications"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setFollowing(userObject.getString("following"));
				userInfo.setScreenName(userObject.getString("screen_name"));

				// Add To Result
				timeLineInfo.setUserInfo(userInfo);
				timelineInfoList.add(timeLineInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return timelineInfoList;
	}

	public static TimeLineInfo parseReplyStatus(String message) {
		// Prepare Result
		TimeLineInfo timeLineInfo = new TimeLineInfo();

		try {

			// Statuses
			JSONObject status = new JSONObject(message);

			timeLineInfo.setFavorite(status.getString("favorited"));
			timeLineInfo.setTime(status.getString("created_at"));
			timeLineInfo.setStatus(status.getString("text")
					.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
					.replace("〜", "~"));
			timeLineInfo.setMessageId(status.getString("id"));
			timeLineInfo.setinReplyToStatusId(status
					.getString("in_reply_to_status_id"));

			// User
			UserInfo userInfo = new UserInfo();

			JSONObject userObject = status.getJSONObject("user");
			userInfo.setUserImageURL(userObject.getString("profile_image_url"));
			userInfo.setUid(userObject.getString("id"));
			userInfo.setFollowerCount(userObject.getString("followers_count"));
			userInfo.setDescription(userObject.getString("notifications"));
			userInfo.setDescription(userObject.getString("description"));
			userInfo.setFollowCount(userObject.getString("friends_count"));
			userInfo.setFollowing(userObject.getString("following"));
			userInfo.setScreenName(userObject.getString("screen_name"));

			// Add To Result
			timeLineInfo.setUserInfo(userInfo);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return timeLineInfo;
	}

	public static String parseUploadImage(String msg) {

		String url = null;

		try {

			JSONObject status = new JSONObject(msg);
			if (status.has("images")) {
				JSONObject images = status.getJSONObject("images");
				if (images.has("links")) {
					JSONObject links = images.getJSONObject("links");
					url = links.getString("original");
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		return url;

	}

}
