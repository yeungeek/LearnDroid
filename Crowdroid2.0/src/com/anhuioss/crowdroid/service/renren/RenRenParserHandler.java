package com.anhuioss.crowdroid.service.renren;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;

public class RenRenParserHandler {

	public static UserInfo parseVerifyInfo(String msg) throws JSONException {

		UserInfo userInfo = new UserInfo();
		if (msg == null) {
			return userInfo;
		}
		try {
			JSONArray jArray = new JSONArray(msg);
			JSONObject jObject;

			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);
				userInfo.setUid(jObject.getString("uid"));
				userInfo.setScreenName(jObject.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public Map<String, String> TokenparseJson(InputStream in) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		byte[] b = new byte[1024];
		try {
			int a = in.read(b);
			String jsonString = new String(b, 0, a);
			String ss = "[" + jsonString.split("\"user")[0] + "\"access_token"
					+ jsonString.split("access_token")[1] + "]";
			JSONArray jsonArray = new JSONArray(ss);
			int length = jsonArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String access_token = object.getString("access_token");
				String expires_in = object.getString("expires_in");
				String refresh_token = object.getString("refresh_token");
				map.put("access_token", access_token);
				map.put("expires_in", expires_in);
				map.put("refresh_token", refresh_token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	public static ArrayList<TimeLineInfo> parseTimeline(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null || msg.equals("")) {
			return timeLineInfoList;
		}
		try {
			JSONArray jArray = new JSONArray(msg);

			JSONObject jObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				String feedType = jObject.getString("feed_type");
				timeLineInfo.setFeedType(feedType);
				// 更新状态的新鲜事
				if (feedType.equals("10")) {

					timeLineInfo.setTime(jObject.getString("update_time"));
					timeLineInfo.setMessageId(jObject.getString("source_id"));
					timeLineInfo.setStatus(jObject.getString("message"));

					userInfo.setUid(jObject.getString("actor_id"));
					userInfo.setScreenName(jObject.getString("name"));
					if (jObject.has("prefix")) {
						userInfo.setDescription(jObject.getString("prefix"));
					} else {
						userInfo.setDescription("");
					}

					userInfo.setUserImageURL(jObject.getString("headurl"));

					JSONObject comment = jObject.getJSONObject("comments");
					timeLineInfo.setCommentCount(comment.getString("count"));

					try {
						JSONArray attachment = jObject
								.getJSONArray("attachment");
						if (attachment.length() > 0) {
							JSONObject retweetObject = attachment
									.getJSONObject(0);
							timeLineInfo.setStatus(jObject.getString("message")
									.replace(
											retweetObject.getString("content"),
											""));
							timeLineInfo.setRetweeted(true);
							timeLineInfo.setRetweetedStatus(retweetObject
									.getString("owner_name")
									+ ":"
									+ retweetObject.getString("content"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				// 更新page状态的新鲜事
				if (feedType.equals("11")) {

					timeLineInfo.setTime(jObject.getString("update_time"));
					timeLineInfo.setMessageId(jObject.getString("source_id"));
					timeLineInfo.setStatus(jObject.getString("message"));

					userInfo.setUid(jObject.getString("actor_id"));
					userInfo.setScreenName(jObject.getString("name"));
					if (jObject.has("title")) {
						userInfo.setDescription(jObject.getString("title"));
					} else {
						userInfo.setDescription("");
					}

					userInfo.setUserImageURL(jObject.getString("headurl"));

					JSONObject comment = jObject.getJSONObject("comments");
					timeLineInfo.setCommentCount(comment.getString("count"));

					try {
						JSONArray attachment = jObject
								.getJSONArray("attachment");
						if (attachment.length() > 0) {
							JSONObject retweetObject = attachment
									.getJSONObject(0);
							timeLineInfo.setStatus(jObject.getString("message")
									.replace(
											retweetObject.getString("content"),
											""));
							timeLineInfo.setRetweeted(true);
							timeLineInfo.setRetweetedStatus(retweetObject
									.getString("owner_name")
									+ ":"
									+ retweetObject.getString("content"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				// 发表日志的新鲜事
				// page发表日志新鲜事
				if (feedType.equals("20") || feedType.equals("22")) {
					timeLineInfo.setTime(jObject.getString("update_time"));
					timeLineInfo.setMessageId(jObject.getString("source_id"));
					JSONArray attachment = jObject.getJSONArray("attachment");
					String imageUrl = "";
					if (attachment.length() > 0) {
						JSONObject attachObject = attachment.getJSONObject(0);
						if (attachObject.getString("media_type")
								.equals("image")) {
							// src 小图 ； raw_src 大图
							imageUrl = attachObject.getString("src");
						}
					}
					timeLineInfo.setStatus("发表日志" + " "
							+ jObject.getString("title") + "\n"
							+ jObject.getString("description") + imageUrl);

					userInfo.setUid(jObject.getString("actor_id"));
					userInfo.setScreenName(jObject.getString("name"));
					userInfo.setDescription(jObject.getString("prefix"));
					userInfo.setUserImageURL(jObject.getString("headurl"));

					JSONObject comment = jObject.getJSONObject("comments");
					timeLineInfo.setCommentCount(comment.getString("count"));
				}
				// 分享日志的新鲜事
				// page 分享日志新鲜事
				if (feedType.equals("21") || feedType.equals("23")) {
					timeLineInfo.setTime(jObject.getString("update_time"));
					timeLineInfo.setMessageId(jObject.getString("source_id"));
					timeLineInfo.setPostId(jObject.getString("post_id"));
					if (jObject.has("trace")) {
						JSONObject trace = jObject.getJSONObject("trace");
						timeLineInfo.setStatus(trace.getString("text"));
					} else {
						timeLineInfo.setStatus(jObject.getString("title"));
					}

					userInfo.setUid(jObject.getString("actor_id"));
					userInfo.setScreenName(jObject.getString("name"));
					userInfo.setDescription(jObject.getString("prefix"));
					userInfo.setUserImageURL(jObject.getString("headurl"));

					JSONObject comment = jObject.getJSONObject("comments");
					timeLineInfo.setCommentCount(comment.getString("count"));
					try {
						JSONArray attachment = jObject
								.getJSONArray("attachment");
						if (attachment.length() > 0) {

							JSONObject retweetObject = attachment
									.getJSONObject(0);
							timeLineInfo.setRetweeted(true);
							timeLineInfo.setRetweetedStatus(retweetObject
									.getString("owner_name")
									+ ":"
									+ jObject.getString("title")
									+ "\n"
									+ jObject.getString("description"));
							timeLineInfo.setMediaId(retweetObject
									.getString("media_id"));
							userInfo.setRetweetedScreenName(retweetObject
									.getString("owner_name"));
							userInfo.setRetweetUserId(retweetObject
									.getString("owner_id"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				if (feedType.equals("30") || feedType.equals("31")) {
					// 上传照片的新鲜事
					// page上传照片的新鲜事
					timeLineInfo.setTime(jObject.getString("update_time"));
					// timeLineInfo.setMessageId(jObject.getString("source_id"));
					JSONArray attachment = jObject.getJSONArray("attachment");
					String imageUrl = "";
					JSONObject attachObject;
					if (attachment.length() > 0) {
						attachObject = attachment.getJSONObject(0);
						timeLineInfo.setMessageId(attachObject
								.getString("media_id"));
						if (attachObject.getString("media_type")
								.equals("photo")) {
							// src 小图 ； raw_src 大图
							imageUrl = attachObject.getString("src");
						}
						if (attachObject.has("content")) {
							timeLineInfo.setStatus(attachObject
									.getString("content") + imageUrl);
						} else {
							timeLineInfo.setStatus(jObject.getString("prefix")
									+ "至" + jObject.getString("title")
									+ imageUrl);
						}
					} else {
						timeLineInfo.setMessageId(jObject
								.getString("source_id"));
					}
					userInfo.setUid(jObject.getString("actor_id"));
					userInfo.setScreenName(jObject.getString("name"));
					userInfo.setDescription(jObject.getString("prefix"));
					userInfo.setUserImageURL(jObject.getString("headurl"));
					JSONObject comment = jObject.getJSONObject("comments");
					timeLineInfo.setCommentCount(comment.getString("count"));
				}
				if (feedType.equals("32") || feedType.equals("33")
						|| feedType.equals("36") || feedType.equals("50")
						|| feedType.equals("52") || feedType.equals("53")
						|| feedType.equals("55")) {
					// 分享照片的新鲜事
					// 分享相册的新鲜事
					// 分享视频的新鲜事
					// 分享音乐的新鲜事
					// page分享照片的新鲜事
					// page分享视频的新鲜事
					// page分享音乐的新鲜事
					timeLineInfo.setTime(jObject.getString("update_time"));
					timeLineInfo.setMessageId(jObject.getString("source_id"));
					timeLineInfo.setPostId(jObject.getString("post_id"));
					if (jObject.has("trace")) {
						timeLineInfo.setStatus(jObject.getJSONObject("trace")
								.getString("text"));
					} else {
						timeLineInfo.setStatus(jObject.getString("title"));
					}

					userInfo.setUid(jObject.getString("actor_id"));
					userInfo.setScreenName(jObject.getString("name"));
					userInfo.setDescription(jObject.getString("prefix"));
					userInfo.setUserImageURL(jObject.getString("headurl"));

					JSONObject comment = jObject.getJSONObject("comments");
					timeLineInfo.setCommentCount(comment.getString("count"));
					try {
						JSONArray attachment = jObject
								.getJSONArray("attachment");
						String attachUrl = "";
						JSONObject attachObject;
						if (attachment.length() > 0) {
							attachObject = attachment.getJSONObject(0);
							// timeLineInfo.setMessageId(attachObject.getString("media_id"));
							if (attachObject.getString("media_type").equals(
									"photo")
									|| attachObject.getString("media_type")
											.equals("album")) {
								// src 小图 ； raw_src 大图
								attachUrl = attachObject.getString("src");
							} else if (attachObject.getString("media_type")
									.equals("video")) {
								attachUrl = attachObject.getString("href");
							}
							JSONObject retweetObject = attachment
									.getJSONObject(0);
							timeLineInfo.setRetweeted(true);
							if (retweetObject.has("owner_name")) {
								timeLineInfo.setRetweetedStatus(retweetObject
										.getString("owner_name")
										+ ":"
										+ jObject.getString("title")
										+ attachUrl);

								userInfo.setRetweetedScreenName(retweetObject
										.getString("owner_name"));
								userInfo.setRetweetUserId(retweetObject
										.getString("owner_id"));

							} else {
								timeLineInfo.setRetweetedStatus(jObject
										.getString("title") + attachUrl);

								userInfo.setRetweetedScreenName("");
								userInfo.setRetweetUserId("");

							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (feedType.equals("51") || feedType.equals("54")) {
					// 分享链接的新鲜事
					// page分享链接的新鲜事
					timeLineInfo.setTime(jObject.getString("update_time"));
					timeLineInfo.setMessageId(jObject.getString("source_id"));
					timeLineInfo.setPostId(jObject.getString("post_id"));
					if (jObject.has("trace")) {
						JSONObject trace = jObject.getJSONObject("trace");
						timeLineInfo.setStatus(trace.getString("text"));
					} else {
						timeLineInfo.setStatus(jObject.getString("message"));
					}

					userInfo.setUid(jObject.getString("actor_id"));
					userInfo.setScreenName(jObject.getString("name"));
					userInfo.setDescription(jObject.getString("prefix"));
					userInfo.setUserImageURL(jObject.getString("headurl"));

					JSONObject comment = jObject.getJSONObject("comments");
					timeLineInfo.setCommentCount(comment.getString("count"));
					try {
						JSONArray attachment = jObject
								.getJSONArray("attachment");
						String attachUrl = "";
						JSONObject attachObject;
						if (attachment.length() > 0) {
							attachObject = attachment.getJSONObject(0);
							if (attachObject.getString("media_type").equals(
									"link")) {
								attachUrl = attachObject.getString("src");
							}
							JSONObject retweetObject = attachment
									.getJSONObject(0);
							timeLineInfo.setRetweeted(true);

							timeLineInfo.setRetweetedStatus(jObject
									.getString("title")
									+ attachObject.getString("href")
									+ " \n"
									+ jObject.getString("description")
									+ attachUrl);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				timeLineInfo.setUserInfo(userInfo);
				timeLineInfoList.add(timeLineInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseVisitors(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}

		try {
			JSONObject jObject = new JSONObject(msg);

			JSONArray jArray = jObject.getJSONArray("visitors");

			JSONObject visitor;
			JSONObject userObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				visitor = (JSONObject) jArray.get(i);
				timeLineInfo = new TimeLineInfo();
				userInfo = new UserInfo();
				userInfo.setUid(visitor.getString("uid"));
				userInfo.setScreenName(visitor.getString("name"));
				userInfo.setUserImageURL(visitor.getString("headurl"));

				timeLineInfo.setUserInfo(userInfo);

				jsonInfoList.add(timeLineInfo);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonInfoList;
	}

	public static ArrayList<UserInfo> parseVisitorList(String msg)
			throws JSONException {

		ArrayList<UserInfo> jsonInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return jsonInfoList;
		}
		try {
			JSONObject jObject = new JSONObject(msg);

			JSONArray jArray = null;
			if (jObject.has("visitors")) {
				jArray = jObject.getJSONArray("visitors");
			} else if (jObject.has("fans")) {
				jArray = jObject.getJSONArray("fans");
			}

			JSONObject visitor;
			JSONObject userObject;
			UserInfo userInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				visitor = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				userInfo.setUid(visitor.getString("uid"));
				userInfo.setScreenName(visitor.getString("name"));
				userInfo.setUserImageURL(visitor.getString("headurl"));

				jsonInfoList.add(userInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonInfoList;
	}

	public static UserInfo parseUserInfo(String msg) {

		UserInfo userInfo = new UserInfo();
		if (msg == null) {
			return userInfo;
		}
		try {
			JSONObject jObject = new JSONObject(msg);

			userInfo.setUid(jObject.getString("uid"));
			userInfo.setScreenName(jObject.getString("name"));
			userInfo.setStar(jObject.getString("star"));
			userInfo.setUserImageURL(jObject.getString("headurl"));
			userInfo.setNetWork(jObject.getString("network_name"));
			userInfo.setStatusCount(jObject.getString("status_count"));
			userInfo.setAlbumsCount(jObject.getString("albums_count"));
			userInfo.setFollowCount(jObject.getString("friends_count"));
			userInfo.setBlogsCount(jObject.getString("blogs_count"));
			userInfo.setVisitorsCount(jObject.getString("visitors_count"));

			if (jObject.has("status")) {
				JSONObject status = jObject.getJSONObject("status");
				userInfo.setDescription(status.getString("content"));
			}
			if (jObject.has("base_info")) {
				JSONObject baseObject = jObject.getJSONObject("base_info");
				userInfo.setGender(baseObject.getString("gender"));
				if (baseObject.has("birth")) {
					JSONObject birth = baseObject.getJSONObject("birth");
					userInfo.setBirthday(birth.getString("birth_year") + "-"
							+ birth.getString("birth_month") + "-"
							+ birth.getString("birth_day"));
					if (baseObject.has("hometown")) {
						JSONObject homeTown = baseObject
								.getJSONObject("hometown");
						userInfo.setHomeTown(homeTown.getString("province")
								+ "-" + homeTown.getString("city"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userInfo;
	}

	public static ArrayList<UserInfo> parseFriendsList(String msg) {

		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			JSONArray friends = new JSONArray(msg);
			JSONObject friendsInfo;
			UserInfo userInfo = null;
			for (int i = 0; i < friends.length(); i++) {
				friendsInfo = (JSONObject) friends.get(i);
				userInfo = new UserInfo();
				userInfo.setUserImageURL(friendsInfo.getString("headurl"));
				userInfo.setScreenName(friendsInfo.getString("name"));
				userInfo.setUid(friendsInfo.getString("id"));

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
				if (emotion.has("emotion")) {
					emotionInfo.setPhrase(emotion.getString("emotion"));
				}

				// URL
				if (emotion.has("icon")) {
					emotionInfo.setUrl(emotion.getString("icon"));
				}

				emotionList.add(emotionInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return emotionList;
	}

	public static ArrayList<TimeLineInfo> parseStatusList(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}
		try {
			JSONArray jArray = new JSONArray(msg);

			JSONObject jObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				// 更新状态的新鲜事
				timeLineInfo.setTime(jObject.getString("time"));
				timeLineInfo.setMessageId(jObject.getString("status_id"));
				timeLineInfo.setStatus(jObject.getString("message"));
				timeLineInfo.setFeedType("10");

				userInfo.setUid(jObject.getString("uid"));
				// userInfo.setScreenName("");
				// userInfo.setDescription("");
				// userInfo.setUserImageURL("");

				try {

					if (jObject.has("forward_message")) {
						timeLineInfo.setStatus(jObject
								.getString("forward_message"));
						timeLineInfo.setRetweeted(true);
						timeLineInfo.setRetweetedStatus(jObject
								.getString("root_username")
								+ ":"
								+ jObject.getString("root_message"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				timeLineInfo.setUserInfo(userInfo);
				timeLineInfoList.add(timeLineInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseBlogTimeline(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null || "{}".equals(msg)) {
			return timeLineInfoList;
		}

		JSONObject jObject = new JSONObject(msg);
		JSONObject blogObject;
		TimeLineInfo timeLineInfo = null;
		UserInfo userInfo = null;
		userInfo = new UserInfo();
		userInfo.setUid(jObject.getString("uid"));

		try {
			JSONArray jArray = jObject.getJSONArray("blogs");
			for (int i = 0; i < jArray.length(); i++) {
				timeLineInfo = new TimeLineInfo();

				blogObject = (JSONObject) jArray.get(i);
				timeLineInfo.setStatus("发表日志" + blogObject.getString("title")
						+ " " + blogObject.getString("content"));
				timeLineInfo.setTime(blogObject.getString("time"));
				// 日志ID
				timeLineInfo.setMessageId(blogObject.getString("id"));
				timeLineInfo.setBlogPassword(blogObject.getString("visable"));
				timeLineInfo.setFeedType("20");
				timeLineInfo.setUserInfo(userInfo);
				timeLineInfoList.add(timeLineInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseAlbumsList(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}
		try {
			JSONArray jArray = new JSONArray(msg);

			JSONObject jObject;
			TimeLineInfo timeLineInfo = null;
			UserInfo userInfo = null;

			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);
				timeLineInfo = new TimeLineInfo();
				userInfo = new UserInfo();
				timeLineInfo.setStatusId(jObject.getString("aid"));
				timeLineInfo.setTime(jObject.getString("create_time"));
				timeLineInfo.setStatus(jObject.getString("url"));
				timeLineInfo
						.setCommentCount(jObject.getString("comment_count"));
				timeLineInfo.setFeedType(jObject.getString("visible"));
				userInfo.setScreenName(jObject.getString("name"));
				userInfo.setUid(jObject.getString("uid"));

				timeLineInfo.setUserInfo(userInfo);
				timeLineInfoList.add(timeLineInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseAlbumPhotos(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}
		try {
			JSONArray jArray = new JSONArray(msg);

			JSONObject jObject;
			TimeLineInfo timeLineInfo = null;
			UserInfo userInfo = null;

			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);
				timeLineInfo = new TimeLineInfo();
				userInfo = new UserInfo();
				timeLineInfo.setStatusId(jObject.getString("pid"));
				timeLineInfo.setMediaId(jObject.getString("aid"));
				timeLineInfo.setTime(jObject.getString("time"));
				timeLineInfo.setStatus(jObject.getString("url_main") + ";"
						+ jObject.getString("url_large"));
				timeLineInfo
						.setCommentCount(jObject.getString("comment_count"));
				timeLineInfo.setOriginalTweets(jObject.getString("caption"));
				userInfo.setUid(jObject.getString("uid"));

				timeLineInfo.setUserInfo(userInfo);
				timeLineInfoList.add(timeLineInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseCommentTimeline(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}
		try {
			JSONArray jArray = null;
			// share
			if (msg.contains("total") && msg.contains("comments")) {
				JSONObject shareObject = new JSONObject(msg);
				jArray = shareObject.getJSONArray("comments");
			} else {
				jArray = new JSONArray(msg);
			}

			JSONObject jObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				jObject = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				//
				timeLineInfo.setTime(jObject.getString("time"));
				userInfo.setUid(jObject.getString("uid"));
				userInfo.setScreenName(jObject.getString("name"));

				// message_id
				if (jObject.has("comment_id")) {
					timeLineInfo.setMessageId(jObject.getString("comment_id"));
				} else if (jObject.has("id")) {
					timeLineInfo.setMessageId(jObject.getString("id"));
				}

				// message
				if (jObject.has("text")) {
					timeLineInfo.setStatus(jObject.getString("text"));
				} else if (jObject.has("content")) {
					timeLineInfo.setStatus(jObject.getString("content"));
				}

				// headUrl
				if (jObject.has("tinyurl")) {
					userInfo.setUserImageURL(jObject.getString("tinyurl"));
				} else if (jObject.has("headurl")) {
					userInfo.setUserImageURL(jObject.getString("headurl"));
				}

				timeLineInfo.setUserInfo(userInfo);
				timeLineInfoList.add(timeLineInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<TimeLineInfo> parseBlogContent(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}
		try {
			JSONObject jObject = new JSONObject(msg);

			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;

			userInfo = new UserInfo();
			timeLineInfo = new TimeLineInfo();
			timeLineInfo.setTime(jObject.getString("time"));
			timeLineInfo.setFeedType("20");
			// 浏览数
			timeLineInfo.setRetweetCount(jObject.getString("view_count"));
			// 日志标题
			timeLineInfo.setStatusId(jObject.getString("title"));
			timeLineInfo.setStatus(jObject.getString("content"));
			userInfo.setScreenName(jObject.getString("name"));
			userInfo.setUserImageURL(jObject.getString("headurl"));
			userInfo.setUid(jObject.getString("uid"));

			timeLineInfo.setUserInfo(userInfo);
			timeLineInfoList.add(timeLineInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<UserInfo> parseSerchUserList(String msg)
			throws JSONException {

		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}
		try {
			JSONObject jObject = new JSONObject(msg);

			JSONArray jArray = jObject.getJSONArray("friends");

			JSONObject friend;
			UserInfo userInfo = null;
			for (int i = 0; i < jArray.length(); i++) {

				friend = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				userInfo.setScreenName(friend.getString("name"));
				userInfo.setUid(friend.getString("id"));
				userInfo.setUserImageURL(friend.getString("tinyurl"));
				userInfo.setDescription(friend.getString("info"));
				userInfo.setFollow(friend.getString("isFriend"));// 0 或
																	// 1，是否为好友，“1”表示是，“0”表示否
				userInfoList.add(userInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfoList;
	}

	public static ArrayList<TimeLineInfo> parsePageCategory(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}

		JSONObject jObject = new JSONObject(msg);
		JSONObject cateObject;
		TimeLineInfo timeLineInfo = null;

		try {
			JSONArray jsonArray = jObject.getJSONArray("categories");
			for (int i = 0; i < jsonArray.length(); i++) {
				cateObject = (JSONObject) jsonArray.get(i);
				JSONArray childArray = cateObject.getJSONArray("child_cate");
				for (int ii = 0; ii < childArray.length(); ii++) {
					timeLineInfo = new TimeLineInfo();
					JSONObject childCate = childArray.getJSONObject(ii);
					timeLineInfo.setStatus(childCate.getString("name"));
					timeLineInfo.setStatusId(childCate.getString("id"));
					timeLineInfoList.add(timeLineInfo);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return timeLineInfoList;
	}

	public static ArrayList<UserInfo> parsePageListByCategory(String msg)
			throws JSONException {

		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}
		JSONObject userObject;
		UserInfo userInfo = null;

		try {
			JSONArray jsonArray = new JSONArray(msg);
			for (int i = 0; i < jsonArray.length(); i++) {
				userObject = (JSONObject) jsonArray.get(i);
				userInfo = new UserInfo();
				if (userObject.has("desc")) {
					userInfo.setDescription(userObject.getString("desc"));
				} else {
					userInfo.setDescription("");
				}
				userInfo.setUid(userObject.getString("page_id"));
				userInfo.setScreenName(userObject.getString("name"));
				userInfo.setUserImageURL(userObject.getString("headurl"));
				userInfo.setFollowCount(userObject.getString("fans_count"));
				userInfoList.add(userInfo);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userInfoList;
	}

}
