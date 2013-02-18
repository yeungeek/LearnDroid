package com.anhuioss.crowdroid.service.twitter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.TwitterEntities;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.UserSelectDialog;

public class TwitterParseHandler {

	public static ArrayList<UserInfo> parseFollowersList(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {

			JSONArray usersJsonArray = new JSONArray(msg);
			int j = 0;

			JSONObject userJsonObject;
			UserInfo userInfo = null;
			for (; j < 20 && j < FollowedActivity.userIdsForLookup.size(); j++) {

				String uIds = FollowedActivity.userIdsForLookup.get(j);

				// Get Users
				for (int i = 0; i < usersJsonArray.length(); i++) {

					userJsonObject = (JSONObject) usersJsonArray.get(i);

					// User
					userInfo = new UserInfo();

					// Add To Result
					if (uIds.equals(userJsonObject.getString("id"))) {

						userInfo.setUserImageURL(userJsonObject
								.getString("profile_image_url"));
						userInfo.setUid(userJsonObject.getString("id"));
						userInfo.setFollowerCount(userJsonObject
								.getString("followers_count"));
						userInfo.setDescription(userJsonObject
								.getString("notifications"));
						userInfo.setDescription(userJsonObject
								.getString("description"));
						userInfo.setFollowCount(userJsonObject
								.getString("friends_count"));
						userInfo.setFollowing(userJsonObject
								.getString("following"));
						userInfo.setScreenName(userJsonObject
								.getString("screen_name"));
						userInfo.setUserName(userJsonObject.getString("name"));

						userInfoList.add(userInfo);
					}
				}

			}

			for (; j > 0; j--) {
				FollowedActivity.userIdsForLookup.remove(0);
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

		try {

			JSONArray usersJsonArray = new JSONArray(msg);
			int j = 0;

			JSONObject userJsonObject;
			UserInfo userInfo = null;
			for (; j < 20 && j < FollowActivity.userIdsForLookup.size(); j++) {

				String uIds = FollowActivity.userIdsForLookup.get(j);
				if (usersJsonArray != null && usersJsonArray.length() > 0) {
					// Get Users
					for (int i = 0; i < usersJsonArray.length(); i++) {

						userJsonObject = (JSONObject) usersJsonArray.get(i);

						// User
						userInfo = new UserInfo();

						// Add To Result
						if (uIds.equals(userJsonObject.getString("id"))) {

							userInfo.setUserImageURL(userJsonObject
									.getString("profile_image_url"));
							userInfo.setUid(userJsonObject.getString("id"));
							userInfo.setFollowerCount(userJsonObject
									.getString("followers_count"));
							userInfo.setDescription(userJsonObject
									.getString("notifications"));
							userInfo.setDescription(userJsonObject
									.getString("description"));
							userInfo.setFollowCount(userJsonObject
									.getString("friends_count"));
							userInfo.setFollowing(userJsonObject
									.getString("following"));
							userInfo.setScreenName(userJsonObject
									.getString("screen_name"));
							userInfo.setUserName(userJsonObject
									.getString("name"));

							userInfoList.add(userInfo);
						}
					}
				}
			}

			for (; j > 0; j--) {
				FollowActivity.userIdsForLookup.remove(0);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;

	}

	public static String parseUploadImage(String msg) {

		String url = null;

		try {

			JSONObject clientJSONObj = new JSONObject(msg);

			url = clientJSONObj.getString("url");

		} catch (Exception e) {

		}

		System.gc();

		return url;
	}

	public static String[] parseRelation(String msg) {

		// Prepare Result
		String[] relation = new String[2];

		try {

			// Root
			JSONObject root = new JSONObject(msg);

			// Relationship
			JSONObject relationship = root.getJSONObject("relationship");

			// Source
			JSONObject source = relationship.getJSONObject("target");
			relation[0] = source.getString("following");
			relation[1] = source.getString("followed_by");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return relation;
	}

	public static UserInfo parseUserInformation(String msg) {

		// Prepare Result
		UserInfo userInfo = new UserInfo();

		try {

			// Statuses
			JSONObject user = new JSONObject(msg);

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
			userInfo.setUserName(user.getString("name"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfo;
	}

	public static ArrayList<UserInfo> parseFollowersInfo(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {

			JSONArray statuses = new JSONArray(msg);
			if (statuses.length() > 0) {
				JSONObject status;
				UserInfo userInfo = null;
				for (int i = 0; i < statuses.length(); i++) {

					status = (JSONObject) statuses.get(i);

					// User
					userInfo = new UserInfo();

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
					userInfo.setUserName(status.getString("name"));

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

	public static ArrayList<TimeLineInfo> parseSearchInfo(String msg) {

		// Prepare Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		ArrayList<TwitterEntities> entitiesList = new ArrayList<TwitterEntities>();

		try {

			// Statuses
			JSONObject status;
			JSONArray statuses = null;
			JSONObject userObject;
			TimeLineInfo timeLineInfo = null;
			status = new JSONObject(msg);
			if (status.has("statuses")) {
				statuses = status.getJSONArray("statuses");
			}

			// Get Status, User

			for (int i = 0; i < statuses.length(); i++) {

				// Status
				timeLineInfo = new TimeLineInfo();

				status = (JSONObject) statuses.get(i);

				timeLineInfo.setTime(status.getString("created_at"));
				if (status.has("entities")) {
					JSONObject entities = status.getJSONObject("entities");
					JSONArray urls = entities.getJSONArray("urls");
					if (urls.length() > 0) {
						JSONObject url;
						for (int j = 0; j < urls.length(); j++) {
							url = (JSONObject) urls.get(j);
							TwitterEntities twitterEntities = new TwitterEntities();
							twitterEntities.setUrl(url.getString("url"));
							// twitterEntities.setDisplayUrl(url.getString("display_url"));
							twitterEntities.setExpandedUrl(url
									.getString("expanded_url"));
							if (!"null"
									.equals(twitterEntities.getExpandedUrl())) {
								entitiesList.add(twitterEntities);
							}
						}
					}

				}
				String text = status.getString("text");
				entitiesList.clear();

				timeLineInfo.setStatus(text.replace("：", ":")
						.replaceAll("\\r|\\n|\\t|\\s", " ")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				timeLineInfo.setMessageId(status.getString("id"));

				// User
				UserInfo userInfo = new UserInfo();

				userObject = status.getJSONObject("user");
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setUserName(userObject.getString("name"));

				// Add User Information
				timeLineInfo.setUserInfo(userInfo);

				// ----------------------------------------------------------------

				// Add To Result
				timelineInfoList.add(timeLineInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return timelineInfoList;

		//
		// // Prepare ResultList
		// ArrayList<TimeLineInfo> timeLineInfoList = new
		// ArrayList<TimeLineInfo>();
		//
		// ArrayList<TwitterEntities> entitiesList = new
		// ArrayList<TwitterEntities>();
		//
		// try {
		//
		// JSONObject clientJSONObj = new JSONObject(msg);
		// if (clientJSONObj != null && clientJSONObj.has("statuses")) {
		// JSONArray searchinfototal = clientJSONObj
		// .getJSONArray("statuses");
		// JSONObject searchinfosingle;
		// TimeLineInfo timeLineInfo = null;
		// for (int i = 0; i < searchinfototal.length(); i++) {
		//
		// searchinfosingle = searchinfototal.getJSONObject(i);
		//
		// timeLineInfo = new TimeLineInfo();
		//
		// // Entities
		// if (searchinfosingle.has("entities")) {
		// JSONObject entities = searchinfosingle
		// .getJSONObject("entities");
		// JSONArray urls = entities.getJSONArray("urls");
		//
		// if (urls.length() > 0) {
		// JSONObject url;
		// for (int j = 0; j < urls.length(); j++) {
		// url = (JSONObject) urls.get(j);
		// TwitterEntities twitterEntities = new TwitterEntities();
		// twitterEntities.setUrl(url.getString("url"));
		// // twitterEntities.setDisplayUrl(url.getString("display_url"));
		// twitterEntities.setExpandedUrl(url
		// .getString("expanded_url"));
		// if (!"null".equals(twitterEntities
		// .getExpandedUrl())) {
		// entitiesList.add(twitterEntities);
		// }
		// }
		// }
		//
		// if (entities.has("media")) {
		// JSONArray medias = entities.getJSONArray("media");
		// if (medias.length() > 0) {
		// JSONObject media;
		// for (int j = 0; j < medias.length(); j++) {
		// media = (JSONObject) medias.get(j);
		// if ("photo".equals(media.getString("type"))) {
		// TwitterEntities twitterEntities = new TwitterEntities();
		// twitterEntities.setUrl(media
		// .getString("url"));
		// // twitterEntities.setDisplayUrl(url.getString("display_url"));
		// twitterEntities.setExpandedUrl(media
		// .getString("media_url"));
		// entitiesList.add(twitterEntities);
		// }
		// }
		// }
		// }
		//
		// }
		//
		// String text = searchinfosingle.getString("text");
		// for (TwitterEntities entities : entitiesList) {
		// text = text.replace(entities.getUrl(),
		// entities.getExpandedUrl());
		// }
		// entitiesList.clear();
		//
		// timeLineInfo.setTime(searchinfosingle.getString(
		// "created_at").replace(",", ""));
		// timeLineInfo
		// .setStatus(text.replaceAll("&lt;", "<")
		// .replace("：", ":")
		// .replaceAll("\\r|\\n|\\t|\\s", " ")
		// .replaceAll("&gt;", ">")
		// .replaceAll("&quot;", "\""));
		// timeLineInfo.setMessageId(searchinfosingle.getString("id"));
		//
		// UserInfo userinfo = new UserInfo();
		//
		// userinfo.setScreenName(searchinfosingle
		// .getString("from_user"));
		// userinfo.setUserName(searchinfosingle
		// .getString("from_user_name"));
		// userinfo.setUserImageURL(searchinfosingle
		// .getString("profile_image_url"));
		// userinfo.setUid(searchinfosingle.getString("from_user_id"));
		//
		// timeLineInfo.setUserInfo(userinfo);
		// timeLineInfoList.add(timeLineInfo);
		//
		// }
		// }
		//
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// System.gc();
		//
		// return timeLineInfoList;
	}

	public static ArrayList<UserInfo> parseStrangersInfo(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

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
				userInfo.setDescription(status.getString("notifications"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));
				userInfo.setUserName(status.getString("name"));

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

	public static ArrayList<TimeLineInfo> parseTimeline(String msg) {

		// Prepare Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		ArrayList<TwitterEntities> entitiesList = new ArrayList<TwitterEntities>();

		try {
			int index = 0;
			String str = msg.substring(0, 1);
			if ("0".equals(str)) {
				msg = msg.substring(1, msg.length());
				index = 1;
			}

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			JSONObject userObject;
			TimeLineInfo timeLineInfo = null;
			for (int i = index; i < statuses.length(); i++) {

				// Status
				timeLineInfo = new TimeLineInfo();

				status = (JSONObject) statuses.get(i);

				timeLineInfo.setFavorite(status.getString("favorited"));
				timeLineInfo.setTime(status.getString("created_at"));

				// Entities
				if (status.has("entities")) {
					JSONObject entities = status.getJSONObject("entities");
					JSONArray urls = entities.getJSONArray("urls");
					if (urls.length() > 0) {
						JSONObject url;
						for (int j = 0; j < urls.length(); j++) {
							url = (JSONObject) urls.get(j);
							TwitterEntities twitterEntities = new TwitterEntities();
							twitterEntities.setUrl(url.getString("url"));
							// twitterEntities.setDisplayUrl(url.getString("display_url"));
							twitterEntities.setExpandedUrl(url
									.getString("expanded_url"));
							if (!"null"
									.equals(twitterEntities.getExpandedUrl())) {
								entitiesList.add(twitterEntities);
							}
						}
					}

					if (entities.has("media")) {
						JSONArray medias = entities.getJSONArray("media");
						if (medias.length() > 0) {
							JSONObject media;
							for (int j = 0; j < medias.length(); j++) {
								media = (JSONObject) medias.get(j);
								if ("photo".equals(media.getString("type"))) {
									TwitterEntities twitterEntities = new TwitterEntities();
									twitterEntities.setUrl(media
											.getString("url"));
									// twitterEntities.setDisplayUrl(url.getString("display_url"));
									twitterEntities.setExpandedUrl(media
											.getString("media_url"));
									entitiesList.add(twitterEntities);
								}
							}
						}
					}

				}

				String text = status.getString("text");
				for (TwitterEntities entities : entitiesList) {
					text = text.replace(entities.getUrl(),
							entities.getExpandedUrl());
				}
				entitiesList.clear();

				timeLineInfo.setStatus(text.replace("：", ":")
						.replaceAll("\\r|\\n|\\t|\\s", " ")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				timeLineInfo.setMessageId(status.getString("id"));
				timeLineInfo.setinReplyToStatusId(status
						.getString("in_reply_to_status_id"));

				if (!"0".equals(status.getString("retweet_count"))) {
					timeLineInfo.setRetweeted(true);
					timeLineInfo.setRetweetCount(status
							.getString("retweet_count"));
				}

				// User
				UserInfo userInfo = new UserInfo();

				userObject = status.getJSONObject("user");
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
				userInfo.setUserName(userObject.getString("name"));

				// Add User Information
				timeLineInfo.setUserInfo(userInfo);

				// ----------------------------------------------------------------

				// Retweet User Information
				if (status.has("retweeted_status")) {

					JSONObject retweetStatus = status
							.getJSONObject("retweeted_status");

					if (!"0".equals(status.getString("retweet_count"))) {
						timeLineInfo.setRetweeted(true);
						timeLineInfo.setRetweetCount(status
								.getString("retweet_count"));
					}
					if (retweetStatus.has("id")) {
						timeLineInfo.setMessageId(status.getString("id"));
					}

					// Entities
					if (status.has("entities")) {
						JSONObject entities = retweetStatus
								.getJSONObject("entities");
						JSONArray urls = entities.getJSONArray("urls");
						if (urls.length() > 0) {
							JSONObject url;
							for (int j = 0; j < urls.length(); j++) {
								url = (JSONObject) urls.get(j);
								TwitterEntities twitterEntities = new TwitterEntities();
								twitterEntities.setUrl(url.getString("url"));
								// twitterEntities.setDisplayUrl(url.getString("display_url"));
								twitterEntities.setExpandedUrl(url
										.getString("expanded_url"));
								if (!"null".equals(twitterEntities
										.getExpandedUrl())) {
									entitiesList.add(twitterEntities);
								}
							}
						}

						if (entities.has("media")) {
							JSONArray medias = entities.getJSONArray("media");
							if (medias.length() > 0) {
								JSONObject media;
								for (int j = 0; j < medias.length(); j++) {
									media = (JSONObject) medias.get(j);
									if ("photo".equals(media.getString("type"))) {
										TwitterEntities twitterEntities = new TwitterEntities();
										twitterEntities.setUrl(media
												.getString("url"));
										// twitterEntities.setDisplayUrl(url.getString("display_url"));
										twitterEntities.setExpandedUrl(media
												.getString("media_url"));
										if (!"null".equals(twitterEntities
												.getExpandedUrl())) {
											entitiesList.add(twitterEntities);
										}
									}
								}
							}
						}

					}

					String retweetText = retweetStatus.getString("text");
					for (TwitterEntities entities : entitiesList) {
						retweetText = retweetText.replace(entities.getUrl(),
								entities.getExpandedUrl());
					}
					entitiesList.clear();

					timeLineInfo.setStatus(retweetText.replace("：", ":")
							.replaceAll("\\r|\\n|\\t|\\s", " ")
							.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
							.replace("〜", "~"));
					UserInfo retweetInfo = new UserInfo();
					JSONObject retweetUserObject = retweetStatus
							.getJSONObject("user");
					retweetInfo.setUserImageURL(retweetUserObject
							.getString("profile_image_url"));
					retweetInfo.setUid(retweetUserObject.getString("id"));
					retweetInfo.setFollowerCount(retweetUserObject
							.getString("followers_count"));
					retweetInfo.setDescription(retweetUserObject
							.getString("notifications"));
					retweetInfo.setDescription(retweetUserObject
							.getString("description"));
					retweetInfo.setFollowCount(retweetUserObject
							.getString("friends_count"));
					retweetInfo.setFollowing(retweetUserObject
							.getString("following"));
					retweetInfo.setScreenName(retweetUserObject
							.getString("screen_name"));
					retweetInfo
							.setUserName(retweetUserObject.getString("name"));
					timeLineInfo.setRetweetUserInfo(userInfo);
					timeLineInfo.setUserInfo(retweetInfo);

				}

				// ----------------------------------------------------------------

				// Add To Result
				timelineInfoList.add(timeLineInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return timelineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseDirectMessage(String msg) {

		// Prepare Result
		ArrayList<TimeLineInfo> directMessageInfoList = new ArrayList<TimeLineInfo>();

		ArrayList<TwitterEntities> entitiesList = new ArrayList<TwitterEntities>();

		try {

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			TimeLineInfo directMessageInfo = null;
			for (int i = 0; i < statuses.length(); i++) {

				// Status
				directMessageInfo = new TimeLineInfo();

				status = (JSONObject) statuses.get(i);
				directMessageInfo.setTime(status.getString("created_at"));

				// Entities
				if (status.has("entities")) {
					JSONObject entities = status.getJSONObject("entities");
					JSONArray urls = entities.getJSONArray("urls");
					if (urls.length() > 0) {
						JSONObject url;
						for (int j = 0; j < urls.length(); j++) {
							url = (JSONObject) urls.get(j);
							TwitterEntities twitterEntities = new TwitterEntities();
							twitterEntities.setUrl(url.getString("url"));
							// twitterEntities.setDisplayUrl(url.getString("display_url"));
							twitterEntities.setExpandedUrl(url
									.getString("expanded_url"));
							if (!"null"
									.equals(twitterEntities.getExpandedUrl())) {
								entitiesList.add(twitterEntities);
							}
						}
					}

					if (entities.has("media")) {
						JSONArray medias = entities.getJSONArray("media");
						if (medias.length() > 0) {
							JSONObject media;
							for (int j = 0; j < medias.length(); j++) {
								media = (JSONObject) urls.get(j);
								if ("photo".equals(media.getString("type"))) {
									TwitterEntities twitterEntities = new TwitterEntities();
									twitterEntities.setUrl(media
											.getString("url"));
									// twitterEntities.setDisplayUrl(url.getString("display_url"));
									twitterEntities.setExpandedUrl(media
											.getString("media_url"));
									entitiesList.add(twitterEntities);
								}
							}
						}
					}

				}

				String text = status.getString("text");
				for (TwitterEntities entities : entitiesList) {
					text = text.replace(entities.getUrl(),
							entities.getExpandedUrl());
				}
				entitiesList.clear();

				directMessageInfo.setStatus(text.replace("：", ":")
						.replaceAll("\\r|\\n|\\t|\\s", " ")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				directMessageInfo.setMessageId(status.getString("id"));

				// User
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
				userInfo.setUserName(userObject.getString("name"));
				// Add To Result
				directMessageInfo.setUserInfo(userInfo);
				directMessageInfoList.add(directMessageInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return directMessageInfoList;
	}

	public static ArrayList<ListInfo> parseLists(String msg) {

		String nextCursor = "-1";

		String previousCursor = "0";

		// Prepare Result
		ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();

		try {

			// Root
			JSONObject root = new JSONObject(msg);

			// Next And Previous Cursor
			nextCursor = root.getString("next_cursor");
			previousCursor = root.getString("previous_cursor");

			// Lists
			JSONArray lists = root.getJSONArray("lists");

			// Get Status, User
			JSONObject list;
			ListInfo listInfo = null;
			UserInfo userInfo = null;
			for (int i = 0; i < lists.length(); i++) {

				// Status
				listInfo = new ListInfo();

				list = (JSONObject) lists.get(i);
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

				// User
				userInfo = new UserInfo();

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
				userInfo.setUserName(userObject.getString("name"));
				// Add To Result
				listInfo.setMode(userInfo.getScreenName());
				listInfo.setUserInfo(userInfo);
				listInfoList.add(listInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return listInfoList;
	}

	public static ArrayList<TimeLineInfo> parseRetweetBy(String msg) {

		// Prepare Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		ArrayList<TwitterEntities> entitiesList = new ArrayList<TwitterEntities>();

		try {

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			JSONObject retweetedStatus;
			JSONObject userObject;
			TimeLineInfo timeLineInfo = null;
			UserInfo userInfo = null;
			for (int i = 0; i < statuses.length(); i++) {

				// Status
				timeLineInfo = new TimeLineInfo();

				status = (JSONObject) statuses.get(i);
				timeLineInfo.setFavorite(status.getString("favorited"));
				timeLineInfo.setTime(status.getString("created_at"));

				// Entities
				if (status.has("entities")) {
					JSONObject entities = status.getJSONObject("entities");
					JSONArray urls = entities.getJSONArray("urls");
					if (urls.length() > 0) {
						JSONObject url;
						for (int j = 0; j < urls.length(); j++) {
							url = (JSONObject) urls.get(j);
							TwitterEntities twitterEntities = new TwitterEntities();
							twitterEntities.setUrl(url.getString("url"));
							// twitterEntities.setDisplayUrl(url.getString("display_url"));
							twitterEntities.setExpandedUrl(url
									.getString("expanded_url"));
							if (!"null"
									.equals(twitterEntities.getExpandedUrl())) {
								entitiesList.add(twitterEntities);
							}
						}
					}

					if (entities.has("media")) {
						JSONArray medias = entities.getJSONArray("media");
						if (medias.length() > 0) {
							JSONObject media;
							for (int j = 0; j < medias.length(); j++) {
								media = (JSONObject) medias.get(j);
								if ("photo".equals(media.getString("type"))) {
									TwitterEntities twitterEntities = new TwitterEntities();
									twitterEntities.setUrl(media
											.getString("url"));
									// twitterEntities.setDisplayUrl(url.getString("display_url"));
									twitterEntities.setExpandedUrl(media
											.getString("media_url"));
									entitiesList.add(twitterEntities);
								}
							}
						}
					}

				}

				String text = status.getString("text");
				for (TwitterEntities entities : entitiesList) {
					text = text.replace(entities.getUrl(),
							entities.getExpandedUrl());
				}
				entitiesList.clear();

				timeLineInfo.setStatus(text.replace("：", ":")
						.replaceAll("\\r|\\n|\\t|\\s", " ")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				timeLineInfo.setMessageId(status.getString("id"));
				timeLineInfo.setinReplyToStatusId(status
						.getString("in_reply_to_status_id"));

				// User
				userInfo = new UserInfo();

				retweetedStatus = status.getJSONObject("retweeted_status");
				userObject = retweetedStatus.getJSONObject("user");
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
				userInfo.setUserName(userObject.getString("name"));
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

	public static TimeLineInfo parseReplyStatus(String msg) {

		// Prepare Result
		TimeLineInfo timeLineInfo = new TimeLineInfo();

		ArrayList<TwitterEntities> entitiesList = new ArrayList<TwitterEntities>();

		try {

			// Statuses
			JSONObject status = new JSONObject(msg);

			timeLineInfo.setFavorite(status.getString("favorited"));
			timeLineInfo.setTime(status.getString("created_at"));

			// Entities
			if (status.has("entities")) {
				JSONObject entities = status.getJSONObject("entities");
				JSONArray urls = entities.getJSONArray("urls");
				if (urls.length() > 0) {
					JSONObject url;
					for (int j = 0; j < urls.length(); j++) {
						url = (JSONObject) urls.get(j);
						TwitterEntities twitterEntities = new TwitterEntities();
						twitterEntities.setUrl(url.getString("url"));
						// twitterEntities.setDisplayUrl(url.getString("display_url"));
						twitterEntities.setExpandedUrl(url
								.getString("expanded_url"));
						if (!"null".equals(twitterEntities.getExpandedUrl())) {
							entitiesList.add(twitterEntities);
						}
					}
				}

				if (entities.has("media")) {
					JSONArray medias = entities.getJSONArray("media");
					if (medias.length() > 0) {
						JSONObject media;
						for (int j = 0; j < medias.length(); j++) {
							media = (JSONObject) medias.get(j);
							if ("photo".equals(media.getString("type"))) {
								TwitterEntities twitterEntities = new TwitterEntities();
								twitterEntities.setUrl(media.getString("url"));
								// twitterEntities.setDisplayUrl(url.getString("display_url"));
								twitterEntities.setExpandedUrl(media
										.getString("media_url"));
								entitiesList.add(twitterEntities);
							}
						}
					}
				}

			}

			String text = status.getString("text");
			for (TwitterEntities entities : entitiesList) {
				text = text.replace(entities.getUrl(),
						entities.getExpandedUrl());
			}
			entitiesList.clear();

			timeLineInfo.setStatus(text.replace("：", ":")
					.replaceAll("\\r|\\n|\\t|\\s", " ").replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">").replace("〜", "~"));
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
			userInfo.setUserName(userObject.getString("name"));
			// Add To Result
			timeLineInfo.setUserInfo(userInfo);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return timeLineInfo;
	}

	public static String parseRegisterMessageToApi(String msg) {

		InputStream is = new ByteArrayInputStream(msg.getBytes());

		String newUrl = null;

		try {

			// Using Pull Parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);

			XmlPullParser xmlPullParser = factory.newPullParser();

			xmlPullParser.setInput(is, "UTF-8");

			while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

				if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {

					if (xmlPullParser.getName().equals("short")) {

						xmlPullParser.next();
						newUrl = xmlPullParser.getText();
						return newUrl;
					}
				}
			}

			return newUrl;

		} catch (Exception e) {
			return newUrl;
		}

	}

	public static void parseIds(String msg, int type) {

		// Prepare Result
		ArrayList<String> userIds = new ArrayList<String>();

		try {

			// Statuses
			JSONObject content = new JSONObject(msg);

			// Previous Cursor
			String p = content.getString("previous_cursor");
			UserSelectDialog.preCursor = Long.valueOf(p);
			String n = content.getString("next_cursor");
			UserSelectDialog.nextCursor = Long.valueOf(n);

			// Next Cursor
			String nextCursor = content.getString("next_cursor");

			// User IDs
			JSONArray ids = content.getJSONArray("ids");

			// Get User IDs
			for (int i = 0; i < ids.length(); i++) {
				userIds.add(ids.get(i).toString());
			}

			if (type == TwitterCommHandler.TYPE_FRIENDS) {
				FollowActivity.nextCursor = Long.valueOf(nextCursor);
				FollowActivity.userIdsForLookup = userIds;
			} else if (type == TwitterCommHandler.TYPE_FOLLOWERS) {
				FollowedActivity.nextCursor = Long.valueOf(nextCursor);
				FollowedActivity.userIdsForLookup = userIds;
			}
			UserSelectDialog.userIdsForLookup = userIds;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		// return userIds;

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

			String trendsName = "";
			String[] message = msg.split("\"");
			if (message.length > 5) {
				trendsName = message[5];
			}

			JSONArray trendList = trends.getJSONArray(trendsName);

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

	public static ArrayList<TrendInfo> parseTrendsAvailable(String msg) {

		// Prepare Result
		ArrayList<TrendInfo> trendInfoList = new ArrayList<TrendInfo>();
		if (msg == null) {
			return trendInfoList;
		}

		try {

			// Trends
			JSONArray trends = new JSONArray(msg);

			// Get Status, User
			JSONObject trend;
			TrendInfo trendInfo = null;
			for (int i = 0; i < trends.length(); i++) {

				// Trend
				trend = (JSONObject) trends.get(i);

				// Trend Information
				trendInfo = new TrendInfo();

				// Name
				trendInfo.setName(trend.getString("name"));

				trendInfo.setCountry(trend.getString("country"));

				// Woeid
				trendInfo.setWoeid(trend.getString("woeid"));

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

	public static ArrayList<TrendInfo> parseTrendsByWoeid(String msg) {

		// Prepare Result
		ArrayList<TrendInfo> trendInfoList = new ArrayList<TrendInfo>();
		if (msg == null) {
			return trendInfoList;
		}

		try {

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Status
			JSONObject status = statuses.getJSONObject(0);

			// Trends
			JSONArray trends = status.getJSONArray("trends");

			// Get Status, User
			JSONObject trend;
			TrendInfo trendInfo = null;
			for (int i = 0; i < trends.length(); i++) {

				// Trend
				trend = (JSONObject) trends.get(i);

				// Trend Information
				trendInfo = new TrendInfo();

				// Name
				trendInfo.setName(trend.getString("name"));

				// Add To Result
				trendInfoList.add(trendInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
			return trendInfoList;
		}

		System.gc();

		// Return
		return trendInfoList;

	}

	public static ArrayList<UserInfo> parseHotUsers(String message) {

		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (message == null) {
			return userInfoList;
		}

		try {
			JSONObject jObject = new JSONObject(message);
			JSONArray jArray = jObject.getJSONArray("users");
			JSONObject userObject;
			UserInfo userInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				userObject = (JSONObject) jArray.get(i);

				userInfo = new UserInfo();

				userInfo.setUserName(userObject.getString("name"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url_https"));
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

	public static ArrayList<TimeLineInfo> parseRetweetedListById(String msg) {

		// Prepare Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		ArrayList<TwitterEntities> entitiesList = new ArrayList<TwitterEntities>();

		try {
			if ("[]".equals(msg)) {
				return null;
			}

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			JSONObject userObject;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < statuses.length(); i++) {

				// Status
				timeLineInfo = new TimeLineInfo();

				status = (JSONObject) statuses.get(i);

				timeLineInfo.setFavorite(status.getString("favorited"));
				timeLineInfo.setTime(status.getString("created_at"));

				String text = status.getString("text");

				timeLineInfo.setStatus(text.replace("：", ":")
						.replaceAll("\\r|\\n|\\t|\\s", " ")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				timeLineInfo.setMessageId(status.getString("id"));
				timeLineInfo.setinReplyToStatusId(status
						.getString("in_reply_to_status_id"));

				// User
				UserInfo userInfo = new UserInfo();

				userObject = status.getJSONObject("user");
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
				userInfo.setUserName(userObject.getString("name"));

				// Add User Information
				timeLineInfo.setUserInfo(userInfo);

				// Retweet User Information
				if (status.has("retweeted_status")) {

					JSONObject retweetStatus = status
							.getJSONObject("retweeted_status");

					String retweetText = retweetStatus.getString("text");

					timeLineInfo.setMessageId(retweetStatus.getString("id"));

					timeLineInfo.setStatus(retweetText.replace("：", ":")
							.replaceAll("\\r|\\n|\\t|\\s", " ")
							.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
							.replace("〜", "~"));
					UserInfo retweetInfo = new UserInfo();
					JSONObject retweetUserObject = retweetStatus
							.getJSONObject("user");
					retweetInfo.setUserImageURL(retweetUserObject
							.getString("profile_image_url"));
					retweetInfo.setUid(retweetUserObject.getString("id"));
					retweetInfo.setFollowerCount(retweetUserObject
							.getString("followers_count"));
					retweetInfo.setDescription(retweetUserObject
							.getString("notifications"));
					retweetInfo.setDescription(retweetUserObject
							.getString("description"));
					retweetInfo.setFollowCount(retweetUserObject
							.getString("friends_count"));
					retweetInfo.setFollowing(retweetUserObject
							.getString("following"));
					retweetInfo.setScreenName(retweetUserObject
							.getString("screen_name"));
					retweetInfo
							.setUserName(retweetUserObject.getString("name"));
					timeLineInfo.setRetweetUserInfo(retweetInfo);
				}

				// Add To Result
				timelineInfoList.add(timeLineInfo);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.gc();
		// Return
		return timelineInfoList;
	}

	public static ArrayList<UserInfo> parseRetweetedUserListById(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {
			if ("[]".equals(msg)) {
				return userInfoList;
			}
			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			for (int i = 0; i < statuses.length(); i++) {

				// Status

				status = (JSONObject) statuses.get(i);

				// User
				UserInfo userInfo = new UserInfo();

				userInfo.setUserImageURL(status.getString("profile_image_url"));
				userInfo.setUid(status.getString("id"));
				userInfo.setFollowerCount(status.getString("followers_count"));
				userInfo.setDescription(status.getString("description"));
				userInfo.setFollowCount(status.getString("friends_count"));
				userInfo.setFollowing(status.getString("following"));
				userInfo.setScreenName(status.getString("screen_name"));
				userInfo.setUserName(status.getString("name"));
				userInfo.setVerified(status.getString("verified"));

				userInfoList.add(userInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.gc();
		// Return
		return userInfoList;
	}

	public static ArrayList<ListInfo> parseSuggestionSulg(String msg) {

		// Prepare Result
		ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();

		try {
			if ("[]".equals(msg)) {
				return listInfoList;
			}
			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			for (int i = 0; i < statuses.length(); i++) {

				// Status

				status = (JSONObject) statuses.get(i);

				// User
				ListInfo listInfo = new ListInfo();

				listInfo.setSlug(status.getString("slug"));
				listInfo.setName(status.getString("name"));
				listInfo.setMemberCount(status.getString("size"));

				listInfoList.add(listInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.gc();
		// Return
		return listInfoList;
	}

	public static ArrayList<ListInfo> parseGroupListSulg(String msg) {

		// Prepare Result
		ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();

		try {
			if ("[]".equals(msg)) {
				return listInfoList;
			}
			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			for (int i = 0; i < statuses.length(); i++) {

				// Status

				status = (JSONObject) statuses.get(i);

				// User
				ListInfo listInfo = new ListInfo();

				listInfo.setSlug(status.getString("slug"));
				listInfo.setId(status.getString("id"));
				listInfo.setName(status.getString("name"));
				listInfo.setDescription(status.getString("description"));

				UserInfo userInfo = new UserInfo();
				JSONObject userObject = status.getJSONObject("user");
				userInfo.setUserName(userObject.getString("name"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setNotifications(userObject.getString("notifications"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setFollowing(userObject.getString("following"));
				userInfo.setListCount(userObject.getString("listed_count"));
				userInfo.setStatusCount(userObject.getString("statuses_count"));
				userInfo.setVerified(userObject.getString("verified"));

				// Add User Information
				listInfo.setUserInfo(userInfo);
				listInfoList.add(listInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.gc();
		// Return
		return listInfoList;
	}

	public static ArrayList<UserInfo> parseGroupUserList(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

		try {

			JSONObject jsonObject = new JSONObject(msg);
			int j = 0;

			JSONArray userArray = jsonObject.getJSONArray("users");
			UserInfo userInfo = null;
			JSONObject userObject = null;
			for (int i = 0; i < userArray.length(); i++) {
				// Get Users
				userObject = (JSONObject) userArray.get(i);

				// User
				userInfo = new UserInfo();

				userInfo.setUserImageURL(userObject
						.getString("profile_image_url"));
				userInfo.setUid(userObject.getString("id"));
				userInfo.setFollowerCount(userObject
						.getString("followers_count"));
				userInfo.setDescription(userObject.getString("description"));
				userInfo.setFollowCount(userObject.getString("friends_count"));
				userInfo.setFollowing(userObject.getString("following"));
				userInfo.setScreenName(userObject.getString("screen_name"));
				userInfo.setUserName(userObject.getString("name"));

				userInfoList.add(userInfo);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;
	}

	public static ArrayList<LocationInfo> parseLocationList(String msg)
			throws JSONException {

		ArrayList<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
		if (msg == null) {
			return locationInfoList;
		}

		JSONObject location = new JSONObject(msg);
		JSONArray places;
		JSONObject place;
		try {
			if (location.has("result")) {
				LocationInfo locationInfo;
				places = (JSONArray) location.getJSONObject("result")
						.getJSONArray("places");
				for (int i = 0; i < places.length(); i++) {
					place = (JSONObject) places.getJSONObject(i);
					locationInfo = new LocationInfo();
					locationInfo.setLocationName(place.getString("name"));
					locationInfo.setLocationAddress(place
							.getString("full_name"));
					locationInfo.setLocationCategory(place.getJSONObject(
							"bounding_box").getString("type"));
					locationInfo.setLocationId(place.getString("id"));
					// locationInfo.setLocationLongitude(place.getString("longitude"));
					// locationInfo.setLocationLatitude(place.getString("latitude"));
					locationInfoList.add(locationInfo);

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return locationInfoList;
	}

	public static ArrayList<TimeLineInfo> parseLBSTimeline(String msg) {

		// Prepare Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		ArrayList<TwitterEntities> entitiesList = new ArrayList<TwitterEntities>();

		if (msg == null) {
			return timelineInfoList;
		}

		try {

			// Statuses
			JSONArray statuses = new JSONArray(msg);

			// Get Status, User
			JSONObject status;
			JSONObject userObject;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < statuses.length(); i++) {

				// Status
				timeLineInfo = new TimeLineInfo();

				status = (JSONObject) statuses.get(i);
				if (status.isNull("geo")) {
					continue;
				}
				timeLineInfo.setFavorite(status.getString("favorited"));
				timeLineInfo.setTime(status.getString("created_at"));

				// Entities
				if (status.has("entities")) {
					JSONObject entities = status.getJSONObject("entities");
					JSONArray urls = entities.getJSONArray("urls");
					if (urls.length() > 0) {
						JSONObject url;
						for (int j = 0; j < urls.length(); j++) {
							url = (JSONObject) urls.get(j);
							TwitterEntities twitterEntities = new TwitterEntities();
							twitterEntities.setUrl(url.getString("url"));
							// twitterEntities.setDisplayUrl(url.getString("display_url"));
							twitterEntities.setExpandedUrl(url
									.getString("expanded_url"));
							if (!"null"
									.equals(twitterEntities.getExpandedUrl())) {
								entitiesList.add(twitterEntities);
							}
						}
					}

					if (entities.has("media")) {
						JSONArray medias = entities.getJSONArray("media");
						if (medias.length() > 0) {
							JSONObject media;
							for (int j = 0; j < medias.length(); j++) {
								media = (JSONObject) medias.get(j);
								if ("photo".equals(media.getString("type"))) {
									TwitterEntities twitterEntities = new TwitterEntities();
									twitterEntities.setUrl(media
											.getString("url"));
									// twitterEntities.setDisplayUrl(url.getString("display_url"));
									twitterEntities.setExpandedUrl(media
											.getString("media_url"));
									entitiesList.add(twitterEntities);
								}
							}
						}
					}

				}

				String text = status.getString("text");
				for (TwitterEntities entities : entitiesList) {
					text = text.replace(entities.getUrl(),
							entities.getExpandedUrl());
				}
				entitiesList.clear();

				timeLineInfo.setStatus(text.replace("：", ":")
						.replaceAll("\\r|\\n|\\t|\\s", " ")
						.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replace("〜", "~"));
				timeLineInfo.setMessageId(status.getString("id"));
				timeLineInfo.setinReplyToStatusId(status
						.getString("in_reply_to_status_id"));

				if (!"0".equals(status.getString("retweet_count"))) {
					timeLineInfo.setRetweeted(true);
					timeLineInfo.setRetweetCount(status
							.getString("retweet_count"));
				}

				// User
				UserInfo userInfo = new UserInfo();

				userObject = status.getJSONObject("user");
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
				userInfo.setUserName(userObject.getString("name"));

				// Add User Information
				timeLineInfo.setUserInfo(userInfo);

				// ----------------------------------------------------------------

				// Retweet User Information
				if (status.has("retweeted_status")) {

					JSONObject retweetStatus = status
							.getJSONObject("retweeted_status");

					if (!"0".equals(status.getString("retweet_count"))) {
						timeLineInfo.setRetweeted(true);
						timeLineInfo.setRetweetCount(status
								.getString("retweet_count"));
					}
					if (retweetStatus.has("id")) {
						timeLineInfo.setMessageId(status.getString("id"));
					}

					// Entities
					if (status.has("entities")) {
						JSONObject entities = retweetStatus
								.getJSONObject("entities");
						JSONArray urls = entities.getJSONArray("urls");
						if (urls.length() > 0) {
							JSONObject url;
							for (int j = 0; j < urls.length(); j++) {
								url = (JSONObject) urls.get(j);
								TwitterEntities twitterEntities = new TwitterEntities();
								twitterEntities.setUrl(url.getString("url"));
								// twitterEntities.setDisplayUrl(url.getString("display_url"));
								twitterEntities.setExpandedUrl(url
										.getString("expanded_url"));
								if (!"null".equals(twitterEntities
										.getExpandedUrl())) {
									entitiesList.add(twitterEntities);
								}
							}
						}

						if (entities.has("media")) {
							JSONArray medias = entities.getJSONArray("media");
							if (medias.length() > 0) {
								JSONObject media;
								for (int j = 0; j < medias.length(); j++) {
									media = (JSONObject) medias.get(j);
									if ("photo".equals(media.getString("type"))) {
										TwitterEntities twitterEntities = new TwitterEntities();
										twitterEntities.setUrl(media
												.getString("url"));
										// twitterEntities.setDisplayUrl(url.getString("display_url"));
										twitterEntities.setExpandedUrl(media
												.getString("media_url"));
										if (!"null".equals(twitterEntities
												.getExpandedUrl())) {
											entitiesList.add(twitterEntities);
										}
									}
								}
							}
						}

					}

					String retweetText = retweetStatus.getString("text");
					for (TwitterEntities entities : entitiesList) {
						retweetText = retweetText.replace(entities.getUrl(),
								entities.getExpandedUrl());
					}
					entitiesList.clear();

					timeLineInfo.setStatus(retweetText.replace("：", ":")
							.replaceAll("\\r|\\n|\\t|\\s", " ")
							.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
							.replace("〜", "~"));
					UserInfo retweetInfo = new UserInfo();
					JSONObject retweetUserObject = retweetStatus
							.getJSONObject("user");
					retweetInfo.setUserImageURL(retweetUserObject
							.getString("profile_image_url"));
					retweetInfo.setUid(retweetUserObject.getString("id"));
					retweetInfo.setFollowerCount(retweetUserObject
							.getString("followers_count"));
					retweetInfo.setDescription(retweetUserObject
							.getString("notifications"));
					retweetInfo.setDescription(retweetUserObject
							.getString("description"));
					retweetInfo.setFollowCount(retweetUserObject
							.getString("friends_count"));
					retweetInfo.setFollowing(retweetUserObject
							.getString("following"));
					retweetInfo.setScreenName(retweetUserObject
							.getString("screen_name"));
					retweetInfo
							.setUserName(retweetUserObject.getString("name"));
					timeLineInfo.setRetweetUserInfo(userInfo);
					timeLineInfo.setUserInfo(retweetInfo);

				}

				// ----------------------------------------------------------------

				// Add To Result
				timelineInfoList.add(timeLineInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return timelineInfoList;
	}

}
