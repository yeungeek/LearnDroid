package com.anhuioss.crowdroid.service.cfb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;

import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TipInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;

public class CfbParseHandler {

	public static ArrayList<TimeLineInfo> parseTimeLine(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}
		try {
			JSONArray statuses = new JSONArray(msg);
			JSONObject status;
			JSONObject user;
			JSONObject group;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < statuses.length(); i++) {

				// Status
				status = statuses.getJSONObject(i);

				// tweet
				JSONObject tweetObject = status.getJSONObject("tweet");
				timeLineInfo = new TimeLineInfo();
				timeLineInfo.setMessageId(tweetObject.getString("message_id"));
				String resourcePath = "";
				if (tweetObject.getString("attachments") != ";") {
					resourcePath = tweetObject.getString("attachments")
							.replace(";", " ");
				}

				timeLineInfo.setStatus(tweetObject.has("text") ? (Uri
						.decode(tweetObject.getString("text"))
						.replace("：", ":").replaceAll("\\r|\\n|\\t|\\s", " ")
						.replace("〜", "~").replace("&#37;", "%")
						.replace("&#38;", "*").replace("&#43;", "+")
						.replace("&#60;", "<").replace("&#62;", ">")
						.replace("\r", "\n")
						+ "\n" + resourcePath).replaceAll("\n+", "\n")
						: resourcePath);
				timeLineInfo.setFavorite(tweetObject.getString("favorited"));

				timeLineInfo.setTime(tweetObject.getString("create_at"));
				timeLineInfo.setImportantLevel(tweetObject
						.getInt("important_level"));
				if (tweetObject.has("forward_count")) {
					timeLineInfo.setRetweetCount(tweetObject
							.getString("forward_count"));
				} else {
					timeLineInfo.setRetweetCount("");
				}
				if (tweetObject.has("comments_count")) {
					timeLineInfo.setCommentCount(tweetObject
							.getString("comments_count"));
				} else {
					timeLineInfo.setCommentCount("");
				}

				// User
				user = tweetObject.getJSONObject("user");
				UserInfo userInfo = new UserInfo();
				userInfo.setUid(user.getString("id"));
				userInfo.setScreenName(Uri.decode(user.getString("screen_name")));
				userInfo.setDescription(Uri.decode(user
						.getString("description")));
				userInfo.setUserImageURL(user.getString("image_url"));

				// retweet
				if (tweetObject.has("retweet_tweet")) {
					JSONObject retObject = tweetObject
							.getJSONObject("retweet_tweet");
					if (!retObject.toString().equals("{}")) {
						timeLineInfo.setRetweeted(true);

						// Status And Image URL
						String retResourcePath = "";
						if (retObject.has("attachments")) {
							if (retObject.getString("attachments") != ";") {
								retResourcePath = retObject.getString(
										"attachments").replace(";", " ");
							}
						}

						timeLineInfo
								.setRetweetedStatus(retObject.has("text") ? (Uri
										.decode(retObject.getString("text"))
										.replace("：", ":")
										.replaceAll("\\r|\\n|\\t|\\s", " ")
										.replace("〜", "~")
										.replace("&#37;", "%")
										.replace("&#38;", "*")
										.replace("&#43;", "+")
										.replace("&#60;", "<")
										.replace("&#62;", ">")
										.replace("\r", "\n")
										+ "\n" + retResourcePath).replaceAll(
										"\n+", "\n") : retResourcePath);

						JSONObject originalUserObject = retObject
								.getJSONObject("user");
						userInfo.setRetweetedScreenName(Uri
								.decode(originalUserObject
										.getString("screen_name")));
						userInfo.setRetweetUserId(originalUserObject
								.getString("id"));

					}

				}

				timeLineInfo.setUserInfo(userInfo);
				timeLineInfoList.add(timeLineInfo);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return timeLineInfoList;

	}

	// ========================================================================================================
	public static ArrayList<TimeLineInfo> parseTimeLine0(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}

		JSONArray statuses = new JSONArray(msg);
		JSONObject status;
		JSONObject user;
		JSONObject group;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < statuses.length(); i++) {

			// Status
			status = statuses.getJSONObject(i);
			timeLineInfo = new TimeLineInfo();
			timeLineInfo.setMessageId(status.getString("message_id"));
			String resourcePath = status.has("resource_path") ? status
					.getString("resource_path").replace(";", " ") : "";
			// timeLineInfo.setStatus(status.has("text") ?
			// (Uri.decode(status.getString("text")).replace("〜",
			// "~").replace("&#37;", "%").replace("&#38;", "*").replace("&#43;",
			// "+").replace("&#60;", "<").replace("&#62;", ">").replace("\r",
			// "\n") + "\n" + resourcePath).replaceAll("\n+", "\n") :
			// resourcePath);

			timeLineInfo.setStatus(status.has("text") ? (Uri
					.decode(status.getString("text")).replace("：", ":")
					.replaceAll("\\r|\\n|\\t|\\s", " ").replace("〜", "~")
					.replace("&#37;", "%").replace("&#38;", "*")
					.replace("&#43;", "+").replace("&#60;", "<")
					.replace("&#62;", ">").replace("\r", "\n")
					+ "\n" + resourcePath).replaceAll("\n+", "\n")
					: resourcePath);

			timeLineInfo.setTime(status.getString("time"));
			timeLineInfo.setImportantLevel(status.getInt("important_level"));

			// User
			user = status.getJSONObject("user");
			UserInfo userInfo = new UserInfo();
			userInfo.setUid(user.getString("id"));
			userInfo.setScreenName(Uri.decode(user.getString("screen_name")));
			userInfo.setDescription(Uri.decode(user.getString("description")));
			userInfo.setUserImageURL(user.getString("image_url"));

			// QT
			if (status.has("qt_for")) {

				JSONObject qtUser = status.getJSONObject("qt_for");

				// Text
				// String resourcePathQT = status.has("resource_path") ?
				// status.getString("resource_path").replace(";", " ") : "";
				// timeLineInfo.setStatus(qt.has("text") ?
				// (Uri.decode(qt.getString("text")).replace("〜",
				// "~").replace("&#37;", "%").replace("&#38;",
				// "*").replace("&#43;", "+").replace("&#60;",
				// "<").replace("&#62;", ">").replace("\r", "\n") + "\n" +
				// resourcePathQT).replaceAll("\n+", "\n") : resourcePathQT);

				// QT User Info
				// JSONObject qtUser = qt.getJSONObject("user");
				UserInfo qtUserInfo = new UserInfo();
				qtUserInfo.setUid(qtUser.getString("id"));
				qtUserInfo.setScreenName(Uri.decode(qtUser
						.getString("screen_name")));
				qtUserInfo.setDescription(Uri.decode(qtUser
						.getString("description")));
				qtUserInfo.setUserImageURL(qtUser.getString("image_url"));

				timeLineInfo.setRetweetUserInfo(qtUserInfo);

			}

			// Group
			group = user.getJSONObject("group");
			userInfo.setGroupName(Uri.decode(group.getString("group_name")));

			timeLineInfo.setUserInfo(userInfo);

			// if(timeLineInfo.getRetweetUserInfo() != null) {
			// UserInfo temp = timeLineInfo.getRetweetUserInfo();
			// timeLineInfo.setRetweetUserInfo(timeLineInfo.getUserInfo());
			// timeLineInfo.setUserInfo(temp);
			// }

			timeLineInfoList.add(timeLineInfo);

		}

		return timeLineInfoList;

	}

	public static ArrayList<TimeLineInfo> parseDirectMessage(String msg,
			String type) throws XmlPullParserException, IOException,
			JSONException {

		// Prepare ResultList
		ArrayList<TimeLineInfo> directMessageList = new ArrayList<TimeLineInfo>();
		if (msg == null || type == null) {
			return directMessageList;
		}

		JSONArray statuses = new JSONArray(msg);
		JSONObject status;
		JSONObject user;
		JSONObject group;
		UserInfo userInfo = null;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < statuses.length(); i++) {

			// Status
			status = statuses.getJSONObject(i);
			timeLineInfo = new TimeLineInfo();
			timeLineInfo.setMessageId(status.getString("message_id"));

			String resourcePath = status.has("resource_path") ? status
					.getString("resource_path").replace(";", " ") : "";
			timeLineInfo.setStatus(status.has("text") ? (Uri
					.decode(status.getString("text")).replace("：", ":")
					.replaceAll("\\r|\\n|\\t|\\s", " ").replace("〜", "~")
					.replace("&#37;", "%").replace("&#38;", "*")
					.replace("&#43;", "+").replace("&#60;", "<")
					.replace("&#62;", ">").replace("\r", "\n")
					+ "\n" + resourcePath).replaceAll("\n+", "\n")
					: resourcePath);

			// timeLineInfo.setStatus(status.has("text") ?
			// (Uri.decode(status.getString("text")).replace("〜",
			// "~").replace("&#37;", "%").replace("&#38;", "*").replace("&#43;",
			// "+").replace("&#60;", "<").replace("&#62;", ">").replace("\r",
			// "\n")).replaceAll("\n+", "\n") : "");
			timeLineInfo.setTime(status.getString("time"));

			// User
			user = status.getJSONObject("from");
			userInfo = new UserInfo();
			userInfo.setUid(user.getString("id"));
			userInfo.setScreenName(Uri.decode(user.getString("screen_name")));
			userInfo.setDescription(Uri.decode(user.getString("description")));
			userInfo.setUserImageURL(user.getString("image_url"));

			// Group
			group = user.getJSONObject("group");
			userInfo.setGroupName(Uri.decode(group.getString("group_name")));

			timeLineInfo.setUserInfo(userInfo);
			directMessageList.add(timeLineInfo);

		}

		return directMessageList;

	}

	// ----------------------------------------------------------------------------
	/**
	 * Analyze XML data for timeLine and return TimeLine List
	 * 
	 * @param is
	 * @return timeLineList
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	// ----------------------------------------------------------------------------
	// public static ArrayList<TimeLineInfo> parseTimeLine(String msg)
	// throws XmlPullParserException, IOException {
	//
	// // Prepare ResultList
	// ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
	//
	// if(msg == null){
	// return timeLineInfoList;
	// }
	//
	// InputStream is = new ByteArrayInputStream(msg.getBytes());
	//
	// // Using Pull Parser
	// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	// factory.setNamespaceAware(true);
	// XmlPullParser xmlPullParser = factory.newPullParser();
	// xmlPullParser.setInput(is, "UTF-8");
	//
	// //--------------------------------------------------------
	// // Start Parsing XML Data
	// //--------------------------------------------------------
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	//
	// //--------------------------------------------------------
	// // Analyze <status> tag
	// //--------------------------------------------------------
	// if (xmlPullParser.getName().equals("status")) {
	//
	// // Create new Instance of TimeLineInfo
	// TimeLineInfo timeLineInfo = new TimeLineInfo();
	//
	// // Read Tags in status
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	// // Time Stamp
	// if (xmlPullParser.getName().equals("message_id")) {
	// xmlPullParser.next();
	// timeLineInfo.setMessageId(xmlPullParser.getText());
	// continue;
	//
	// }
	// // Message ID
	// if (xmlPullParser.getName().equals("text")) {
	// xmlPullParser.next();
	// timeLineInfo.setStatus(xmlPullParser.getText().replace("〜",
	// "~").replace("&#37;", "%").replace("&#38;", "*").replace("&#43;",
	// "+").replace("&#60;", "<").replace("&#62;", ">").replace("\r", "\n"));
	// continue;
	//
	// }
	// if(xmlPullParser.getName().equals("image_path")){
	// xmlPullParser.next();
	// String text=timeLineInfo.getStatus()+"\n"+xmlPullParser.getText();
	// timeLineInfo.setStatus(text);
	// continue;
	// }
	// // Text
	// if (xmlPullParser.getName().equals("time")) {
	// xmlPullParser.next();
	// timeLineInfo.setTime(xmlPullParser.getText());
	// continue;
	//
	// }
	//
	// //--------------------------------------------------------
	// // Analyze <user> tag in <status>
	// //--------------------------------------------------------
	// if (xmlPullParser.getName().equals("user")) {
	//
	// //Create User Info
	// UserInfo userInfo = new UserInfo();
	//
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) { // Loop3
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	// // User ID
	// if (xmlPullParser.getName().equals("uid")) {
	// xmlPullParser.next();
	// userInfo.setUid(xmlPullParser.getText());
	// continue;
	// }
	// //Screen Name
	// if (xmlPullParser.getName().equals("screen_name")){
	// xmlPullParser.next();
	// userInfo.setScreenName(xmlPullParser.getText());
	// continue;
	// }
	// //Description
	// if (xmlPullParser.getName().equals("description")){
	// xmlPullParser.next();
	// userInfo.setDescription(xmlPullParser.getText());
	// continue;
	// }
	// // User Image
	// if (xmlPullParser.getName().equals("image_url")) {
	// xmlPullParser.next();
	// userInfo.setUserImageURL(xmlPullParser.getText());
	// continue;
	// }
	// // Followers Count
	// if (xmlPullParser.getName().equals("followers_count")) {
	// xmlPullParser.next();
	// userInfo.setFollowerCount(xmlPullParser.getText());
	// continue;
	// }
	// // Friends Count
	// if (xmlPullParser.getName().equals("friends_count")) {
	// xmlPullParser.next();
	// userInfo.setFollowCount(xmlPullParser.getText());
	// continue;
	// }
	// //group
	// if(xmlPullParser.getName().equals("group")) {
	// xmlPullParser.next();
	// userInfo.setGroupName(xmlPullParser.getText());
	// continue;
	// }
	//
	// }
	//
	// // Exit user analyze
	// if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
	// if (xmlPullParser.getName().equals("user")) {
	// timeLineInfo.setUserInfo(userInfo);
	// break;
	// }
	// }
	//
	// }
	//
	// continue;
	// }
	//
	// //--------------------------------------------------------
	// // Analyze <retweet-user> tag in <status>
	// //--------------------------------------------------------
	// if((xmlPullParser.getName().equals("retweet-user"))){
	// UserInfo retweetInfo = new UserInfo();
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	// //--------------------------------------------------------
	// // Analyze <user> tag in <retweet-user>
	// //--------------------------------------------------------
	// if (xmlPullParser.getName().equals("uid")) {
	// xmlPullParser.next();
	// //Create User Info
	// retweetInfo.setUid(xmlPullParser.getText());
	// continue;
	//
	// }
	//
	// if (xmlPullParser.getName().equals("screen_name")) {
	// xmlPullParser.next();
	// //Create User Info
	// retweetInfo.setScreenName(xmlPullParser.getText());
	// continue;
	//
	// }
	//
	// if (xmlPullParser.getName().equals("image_url")) {
	// xmlPullParser.next();
	// //Create User Info
	// retweetInfo.setUserImageURL(xmlPullParser.getText());
	// continue;
	//
	// }
	//
	// //--------------------------------------------------------
	// // (Analyze <retweet-user> tag at end of<status>)
	// //--------------------------------------------------------
	//
	// }
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
	// if (xmlPullParser.getName().equals("retweet-user")) {
	// UserInfo userInfo = timeLineInfo.getUserInfo();
	// timeLineInfo.setRetweetUserInfo(userInfo);
	// // timeLineInfo.setRetweetUserInfo(retweetInfo);
	// timeLineInfo.setUserInfo(retweetInfo);
	// break;
	// }
	// }
	//
	// }
	//
	// }
	//
	//
	//
	// }
	//
	// // End status analyze
	// if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
	// if (xmlPullParser.getName().equals("status")){
	//
	// // Add to List
	// timeLineInfoList.add(timeLineInfo);
	// break;
	// }
	// }
	//
	// }
	// }
	// //--------------------------------------------------------
	// // (Analyze <status> tag)
	// //--------------------------------------------------------
	//
	//
	// }
	//
	// }
	//
	// return timeLineInfoList;
	//
	// }

	// //
	// ----------------------------------------------------------------------------
	// /**
	// * Analyze XML data for timeLine and return TimeLine List
	// *
	// * @param is
	// * @return timeLineList
	// * @throws IOException
	// * @throws XmlPullParserException
	// */
	// //
	// ----------------------------------------------------------------------------
	// public static ArrayList<TimeLineInfo> parseDirectMessage(String msg,
	// String type)
	// throws XmlPullParserException, IOException {
	//
	// // Prepare ResultList
	// ArrayList<TimeLineInfo> directMessageList = new
	// ArrayList<TimeLineInfo>();
	//
	// if(msg == null){
	// return directMessageList;
	// }
	//
	// InputStream is = new ByteArrayInputStream(msg.getBytes());
	//
	// // Using Pull Parser
	// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	// factory.setNamespaceAware(true);
	// XmlPullParser xmlPullParser = factory.newPullParser();
	// xmlPullParser.setInput(is, "UTF-8");
	//
	// //--------------------------------------------------------
	// // Start Parsing XML Data
	// //--------------------------------------------------------
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	//
	// //--------------------------------------------------------
	// // Analyze <status> tag
	// //--------------------------------------------------------
	// if (xmlPullParser.getName().equals("direct_message")) {
	//
	// // Create new Instance of DirectMessageInfo
	// TimeLineInfo info = new TimeLineInfo();
	//
	// // Read Tags in status
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	// // Time Stamp
	// if (xmlPullParser.getName().equals("direct_message_id")) {
	// xmlPullParser.next();
	// info.setMessageId(xmlPullParser.getText());
	// continue;
	//
	// }
	// // Message ID
	// if (xmlPullParser.getName().equals("message")) {
	// xmlPullParser.next();
	// info.setStatus(xmlPullParser.getText().replace("〜", "~").replace("&#37;",
	// "%").replace("&#38;", "*").replace("&#43;", "+").replace("&#60;",
	// "<").replace("&#62;", ">").replace("\r", "\n"));
	// continue;
	//
	// }
	// // Text
	// if (xmlPullParser.getName().equals("time")) {
	// xmlPullParser.next();
	// info.setTime(xmlPullParser.getText());
	// continue;
	//
	// }
	//
	// //--------------------------------------------------------
	// // Analyze <user> tag in <status>
	// //--------------------------------------------------------
	// if (xmlPullParser.getName().equals(type.equals("sent") ? "receiver_info"
	// : "sender_info")) {
	//
	// //Create User Info
	// UserInfo receiverInfo = new UserInfo();
	//
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) { // Loop3
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	// // User Description
	// if (xmlPullParser.getName().equals("description")) {
	// xmlPullParser.next();
	// receiverInfo.setDescription(xmlPullParser.getText());
	// continue;
	// }
	// // Followers Count
	// if (xmlPullParser.getName().equals("followers_count")) {
	// xmlPullParser.next();
	// receiverInfo.setFollowerCount(xmlPullParser.getText());
	// continue;
	// }
	// // Friends Count
	// if (xmlPullParser.getName().equals("friends_count")) {
	// xmlPullParser.next();
	// receiverInfo.setFollowCount(xmlPullParser.getText());
	// continue;
	// }
	// // User Image
	// if (xmlPullParser.getName().equals("image_url")) {
	// xmlPullParser.next();
	// receiverInfo.setUserImageURL(xmlPullParser.getText());
	// continue;
	// }
	// // User Uid
	// if (xmlPullParser.getName().equals("uid")) {
	// xmlPullParser.next();
	// receiverInfo.setUid(xmlPullParser.getText());
	// continue;
	// }
	// //Screen Name
	// if (xmlPullParser.getName().equals("screen_name")){
	// xmlPullParser.next();
	// receiverInfo.setScreenName(xmlPullParser.getText());
	// continue;
	// }
	//
	// }
	//
	// // Exit user analyze
	// if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
	// if (xmlPullParser.getName().equals(type.equals("sent") ? "receiver_info"
	// : "sender_info")) {
	// info.setUserInfo(receiverInfo);
	// break;
	// }
	// }
	//
	// }
	//
	// continue;
	// }
	// //--------------------------------------------------------
	// // (Analyze <user> tag in <status>)
	// //--------------------------------------------------------
	//
	//
	//
	//
	//
	// }
	//
	// // End status analyze
	// if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
	// if (xmlPullParser.getName().equals("direct_message")){
	//
	// // Add to List
	// directMessageList.add(info);
	// break;
	// }
	// }
	//
	// }
	// }
	// //--------------------------------------------------------
	// // (Analyze <status> tag)
	// //--------------------------------------------------------
	//
	//
	// }
	//
	// }
	//
	// return directMessageList;
	//
	// }

	public static UserInfo parseUserInfo(String msg) throws JSONException {

		UserInfo userInfo = new UserInfo();
		if (msg == null) {
			return userInfo;
		}
		try {
			JSONObject user = new JSONObject(msg);
			// JSONObject user = status.getJSONObject("user");

			// User
			userInfo.setUid(user.getString("id"));
			userInfo.setScreenName(Uri.decode(user.getString("screen_name")));
			userInfo.setDescription(Uri.decode(user.getString("description")));
			userInfo.setUserImageURL(user.getString("image_url"));

			// Group
			JSONObject group = user.getJSONObject("group");
			userInfo.setGroupName(Uri.decode(group.getString("group_name")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Analyze XML data for timeLine and return TimeLine List
	 * 
	 * @param is
	 * @return timeLineList
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	// ----------------------------------------------------------------------------
	// public static UserInfo parseUserInfo(String msg)
	// throws XmlPullParserException, IOException {
	//
	// // Prepare ResultList
	// UserInfo userInfo = new UserInfo();
	//
	// if(msg == null){
	// return userInfo;
	// }
	//
	// InputStream is = new ByteArrayInputStream(msg.getBytes());
	//
	// // Using Pull Parser
	// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	// factory.setNamespaceAware(true);
	// XmlPullParser xmlPullParser = factory.newPullParser();
	// xmlPullParser.setInput(is, "UTF-8");
	//
	// //--------------------------------------------------------
	// // Start Parsing XML Data
	// //--------------------------------------------------------
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	//
	// //--------------------------------------------------------
	// // Analyze <status> tag
	// //--------------------------------------------------------
	// if (xmlPullParser.getName().equals("user")) {
	//
	// // Read Tags in status
	// while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	//
	// if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	//
	// // Time Stamp
	// if (xmlPullParser.getName().equals("uid")) {
	// xmlPullParser.next();
	// userInfo.setUid(xmlPullParser.getText());
	// continue;
	//
	// }
	// // Time Stamp
	// if (xmlPullParser.getName().equals("screen_name")) {
	// xmlPullParser.next();
	// userInfo.setScreenName(xmlPullParser.getText());
	// continue;
	//
	// }
	// // // Message ID
	// // if (xmlPullParser.getName().equals("name")) {
	// // xmlPullParser.next();
	// // xmlPullParser.getText();
	// // continue;
	// //
	// // }
	// // Message ID
	// if (xmlPullParser.getName().equals("description")) {
	// xmlPullParser.next();
	// userInfo.setDescription(xmlPullParser.getText());
	// continue;
	//
	// }
	//
	// if (xmlPullParser.getName().equals("image_url")) {
	// xmlPullParser.next();
	// userInfo.setUserImageURL(xmlPullParser.getText());
	// continue;
	// }
	//
	// if (xmlPullParser.getName().equals("group_name")) {
	// xmlPullParser.next();
	// userInfo.setGroupName(xmlPullParser.getText());
	// continue;
	// }
	// // // Message ID
	// // if (xmlPullParser.getName().equals("belong1")) {
	// // xmlPullParser.next();
	// // xmlPullParser.getText();
	// // continue;
	// //
	// // }
	// // // Message ID
	// // if (xmlPullParser.getName().equals("belong2")) {
	// // xmlPullParser.next();
	// // relation[2] = xmlPullParser.getText();
	// // continue;
	// //
	// // }
	// // // Message ID
	// // if (xmlPullParser.getName().equals("time")) {
	// // xmlPullParser.next();
	// // xmlPullParser.getText();
	// // continue;
	// //
	// // }
	// // Message ID
	// if (xmlPullParser.getName().equals("followers_count")) {
	// xmlPullParser.next();
	// userInfo.setFollowerCount(xmlPullParser.getText());
	// continue;
	//
	// }
	// // Message ID
	// if (xmlPullParser.getName().equals("friends_count")) {
	// xmlPullParser.next();
	// userInfo.setFollowCount(xmlPullParser.getText());
	// continue;
	//
	// }
	//
	// }
	//
	// // End status analyze
	// if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
	// if (xmlPullParser.getName().equals("user")){
	// return userInfo;
	// }
	//
	// }
	//
	// }
	//
	// }//end of source
	//
	// }
	//
	// }
	//
	// return userInfo;
	//
	// }

	// ----------------------------------------------------------------------------
	/**
	 * Analyze XML data for timeLine and return TimeLine List
	 * 
	 * @param is
	 * @return timeLineList
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws JSONException
	 */
	// ----------------------------------------------------------------------------
	public static ArrayList<UserInfo> parseFriendsList(String msg)
			throws XmlPullParserException, IOException, JSONException {

		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		JSONArray statuses = new JSONArray(msg);

		JSONObject user;
		JSONObject group;
		UserInfo userInfo = null;
		for (int i = 0; i < statuses.length(); i++) {

			// User
			user = statuses.getJSONObject(i);
			userInfo = new UserInfo();
			userInfo.setUid(user.getString("id"));
			userInfo.setScreenName(Uri.decode(user.getString("screen_name")));
			userInfo.setDescription(Uri.decode(user.getString("description")));
			userInfo.setUserImageURL(user.getString("image_url"));

			// Group
			group = user.getJSONObject("group");
			userInfo.setGroupName(Uri.decode(group.getString("group_name")));

			userInfoList.add(userInfo);

		}

		return userInfoList;

	}

	// ----------------------------------------------------------------------------
	/**
	 * Analyze XML data for timeLine and return TimeLine List
	 * 
	 * @param is
	 * @return timeLineList
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws JSONException
	 */
	// ----------------------------------------------------------------------------
	public static ArrayList<UserInfo> parseFollowersList(String msg)
			throws XmlPullParserException, IOException, JSONException {
		return parseFriendsList(msg);
	}

	public static String parseShortUrl(String msg) {

		// Prepare Result
		String newUrl = "";

		if (msg == null) {
			return newUrl;
		}

		InputStream is = new ByteArrayInputStream(msg.getBytes());

		try {

			// Using Pull Parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);

			XmlPullParser xmlPullParser = factory.newPullParser();

			xmlPullParser.setInput(is, "UTF-8");

			while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

				if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {

					if (xmlPullParser.getName().equals("url")) {

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

	public static String parseCFBSetting(String message) throws JSONException {

		if (message == null) {
			return null;
		}

		HashMap<String, String> settingMap = new HashMap<String, String>();
		JSONObject setting = new JSONObject(message);
		JSONArray parameters = setting.getJSONArray("parameter");
		JSONObject parameter;
		for (int i = 0; i < parameters.length(); i++) {
			parameter = parameters.getJSONObject(i);
			String name = parameter.getString("name");
			String value = parameter.getString("value");
			settingMap.put(name, value);
		}

		String result = settingMap.get("max_tweet_count");
		return result;

	}

	@SuppressWarnings("null")
	public static ArrayList<TipInfo> parseVersionMessage(String message) {

		ArrayList<TipInfo> versionInfoList = new ArrayList<TipInfo>();

		if (message == null) {
			return versionInfoList;
		}
		try {
			JSONObject jObject = new JSONObject(message);
			JSONArray apkArray = jObject.getJSONArray("apk-list");
			TipInfo tipInfo = null;
			JSONObject apkObject;
			for (int i = 0; i < apkArray.length(); i++) {
				apkObject = apkArray.getJSONObject(i);

				tipInfo = new TipInfo();
				if (apkObject.has("type")) {
					tipInfo.setVersionType(Uri.encode(apkObject
							.getString("type")));
				}

				if (apkObject.has("version-name")) {
					tipInfo.setVersionName(Uri.encode(apkObject
							.getString("version-name")));
				}
				if (apkObject.has("version-code")) {
					tipInfo.setVersionCode(Uri.encode(apkObject
							.getString("version-code")));
				}
				if (apkObject.has("download-url")) {
					tipInfo.setDownLoadUrl(apkObject.getString("download-url"));
				}
				versionInfoList.add(tipInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return versionInfoList;

	}

	@SuppressWarnings("null")
	public static ArrayList<TipInfo> parseThemeMessage(String message) {

		ArrayList<TipInfo> themeInfoList = new ArrayList<TipInfo>();

		if (message == null) {
			return themeInfoList;
		}
		try {
			JSONObject jObject = new JSONObject(message);
			JSONArray themeArray = jObject.getJSONArray("theme-list");
			TipInfo tipInfo = null;
			JSONObject themeObject;
			for (int i = 0; i < themeArray.length(); i++) {
				themeObject = themeArray.getJSONObject(i);

				tipInfo = new TipInfo();

				if (themeObject.has("name")) {
					tipInfo.setVersionName(themeObject.getString("name"));
				}
				if (themeObject.has("resolution")) {
					tipInfo.setResolution(Uri.encode(themeObject
							.getString("resolution")));
				}

				if (themeObject.has("size")) {
					tipInfo.setThemeSize(Uri.encode(themeObject
							.getString("size")));
				}
				if (themeObject.has("download-url")) {
					tipInfo.setDownLoadUrl(themeObject
							.getString("download-url"));
				}
				if (themeObject.has("description")) {
					tipInfo.setText(themeObject.getString("description"));
				}
				themeInfoList.add(tipInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return themeInfoList;

	}

	// get commentList By Id
	public static ArrayList<TimeLineInfo> parseCommentTimeline(String message) {

		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (message == null) {
			return jsonInfoList;
		}
		JSONArray jArray;
		try {
			jArray = new JSONArray(message);
			JSONObject commObject;
			JSONObject userObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jArray.length(); i++) {
				commObject = (JSONObject) jArray.get(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				timeLineInfo.setTime(commObject.getString("created_at"));
				timeLineInfo
						.setStatus(Uri.decode(commObject.getString("text")));

				userObject = commObject.getJSONObject("user");
				userInfo.setUid(userObject.getString("id"));
				userInfo.setScreenName(Uri.decode(userObject
						.getString("screen_name")));
				userInfo.setUserImageURL(userObject.getString("image_url"));

				timeLineInfo.setUserInfo(userInfo);

				jsonInfoList.add(timeLineInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonInfoList;
	}

	// get Comment Timeline
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
			JSONObject tweetObject;
			JSONObject originalUserObject;
			UserInfo userInfo = null;
			TimeLineInfo timeLineInfo = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				jObject = jsonArray.getJSONObject(i);
				userInfo = new UserInfo();
				timeLineInfo = new TimeLineInfo();
				timeLineInfo.setTime(jObject.getString("created_at"));

				timeLineInfo.setStatus(Uri.decode(jObject.getString("text"))
						.replace("：", ":").replaceAll("\\r|\\n|\\t|\\s", " ")
						.replace("〜", "~").replace("&#37;", "%")
						.replace("&#38;", "*").replace("&#43;", "+")
						.replace("&#60;", "<").replace("&#62;", ">")
						.replace("\r", "\n").replaceAll("\n+", "\n"));
				timeLineInfo.setinReplyToStatusId(jObject.getString("id"));
				timeLineInfo.setMessageId(jObject.getString("id"));

				userObject = jObject.getJSONObject("user");
				userInfo.setUid(userObject.getString("id"));
				userInfo.setScreenName(Uri.decode(userObject
						.getString("screen_name")));
				userInfo.setUserImageURL(userObject.getString("image_url"));
				tweetObject = jObject.getJSONObject("tweet");
				if (!tweetObject.toString().equals("{}")) {
					timeLineInfo.setStatusId(tweetObject
							.getString("message_id"));

					String resourcePath = "";
					if (tweetObject.getString("attachments") != ";") {
						resourcePath = tweetObject.getString("attachments")
								.replace(";", " ");
					}

					timeLineInfo.setReplyStatus(tweetObject.has("text") ? (Uri
							.decode(tweetObject.getString("text"))
							.replace("：", ":")
							.replaceAll("\\r|\\n|\\t|\\s", " ")
							.replace("〜", "~").replace("&#37;", "%")
							.replace("&#38;", "*").replace("&#43;", "+")
							.replace("&#60;", "<").replace("&#62;", ">")
							.replace("\r", "\n")
							+ "\n" + resourcePath).replaceAll("\n+", "\n")
							: resourcePath);
					timeLineInfo
							.setOriginalTweets(tweetObject.has("text") ? (Uri
									.decode(tweetObject.getString("text"))
									.replace("：", ":")
									.replaceAll("\\r|\\n|\\t|\\s", " ")
									.replace("〜", "~").replace("&#37;", "%")
									.replace("&#38;", "*")
									.replace("&#43;", "+")
									.replace("&#60;", "<")
									.replace("&#62;", ">").replace("\r", "\n")
									+ "\n" + resourcePath).replaceAll("\n+",
									"\n") : resourcePath);

					timeLineInfo.setMessageId(jObject.getString("id"));
					originalUserObject = tweetObject.getJSONObject("user");
					timeLineInfo.setCommentUserImage(originalUserObject
							.getString("image_url"));
				} else {
					timeLineInfo.setReplyStatus("原微博已经删除！");
				}

				timeLineInfo.setUserInfo(userInfo);

				timeLineInfolist.add(timeLineInfo);
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}
		return timeLineInfolist;

	}

	public static TimeLineInfo parseCommentById(String message)
			throws JSONException {

		JSONObject jObject = new JSONObject(message);
		UserInfo userInfo = new UserInfo();
		TimeLineInfo timeLineInfo = new TimeLineInfo();
		if (message == null) {
			return timeLineInfo;
		}
		try {
			JSONObject tweet = jObject.getJSONObject("tweet");
			timeLineInfo.setTime(tweet.getString("create_at"));
			timeLineInfo.setMessageId(tweet.getString("message_id"));

			// Status And Image URL
			String resourcePath = "";
			if (tweet.getString("attachments") != ";") {
				resourcePath = tweet.getString("attachments").replace(";", " ");
			}

			timeLineInfo.setStatus(tweet.has("text") ? (Uri
					.decode(tweet.getString("text")).replace("：", ":")
					.replaceAll("\\r|\\n|\\t|\\s", " ").replace("〜", "~")
					.replace("&#37;", "%").replace("&#38;", "*")
					.replace("&#43;", "+").replace("&#60;", "<")
					.replace("&#62;", ">").replace("\r", "\n")
					+ "\n" + resourcePath).replaceAll("\n+", "\n")
					: resourcePath);

			timeLineInfo.setFavorite(tweet.getString("favorited"));

			JSONObject userObject = tweet.getJSONObject("user");

			userInfo.setUid(userObject.getString("id"));
			userInfo.setScreenName(Uri.decode(userObject
					.getString("screen_name")));
			userInfo.setDescription(Uri.decode(userObject
					.getString("description")));
			userInfo.setUserImageURL(userObject.getString("image_url"));

			// -------------------------
			if (tweet.has("retweet_tweet")) {
				JSONObject retweetObject = tweet.getJSONObject("retweet_tweet");
				if (!retweetObject.toString().equals("{}")) {
					timeLineInfo.setRetweeted(true);

					String retResourcePath = "";
					if (retweetObject.getString("attachments") != ";") {
						retResourcePath = retweetObject
								.getString("attachments").replace(";", " ");
					}

					timeLineInfo
							.setRetweetedStatus(retweetObject.has("text") ? (Uri
									.decode(retweetObject.getString("text"))
									.replace("：", ":")
									.replaceAll("\\r|\\n|\\t|\\s", " ")
									.replace("〜", "~").replace("&#37;", "%")
									.replace("&#38;", "*")
									.replace("&#43;", "+")
									.replace("&#60;", "<")
									.replace("&#62;", ">").replace("\r", "\n")
									+ "\n" + retResourcePath).replaceAll("\n+",
									"\n") : retResourcePath);
					JSONObject retUser = retweetObject.getJSONObject("user");
					userInfo.setRetweetedScreenName(Uri.decode(retUser
							.getString("screen_name")));
					userInfo.setRetweetUserId(retUser.getString("id"));
				}
			}
			// ----------------------------

			timeLineInfo.setUserInfo(userInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return timeLineInfo;
	}

	public static ArrayList<EmotionInfo> parseEmotions(String message) {

		ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();
		if (message == null) {
			return emotionList;
		}

		try {

			JSONArray emotions = new JSONArray(message);
			JSONObject emotion = null;
			EmotionInfo emotionInfo = null;
			for (int i = 0; i < emotions.length(); i++) {

				emotion = (JSONObject) emotions.get(i);
				emotionInfo = new EmotionInfo();

				// Name
				if (emotion.has("name")) {
					emotionInfo
							.setPhrase("[" + emotion.getString("name") + "]");
				}

				// URL
				if (emotion.has("url")) {
					emotionInfo.setUrl(emotion.getString("url"));
				}

				emotionList.add(emotionInfo);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return emotionList;

	}

	// ==================================================================================
	public static ArrayList<TimeLineInfo> parseFavoriteTimeLine(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}

		JSONArray statuses = new JSONArray(msg);
		JSONObject tweetObject;
		JSONObject user;
		JSONObject group;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < statuses.length(); i++) {

			// Status
			tweetObject = statuses.getJSONObject(i);

			// tweet
			timeLineInfo = new TimeLineInfo();
			timeLineInfo.setMessageId(tweetObject.getString("message_id"));
			String resourcePath = "";
			String str = tweetObject.getString("attachments");
			if (tweetObject.getString("attachments") != ";") {
				resourcePath = tweetObject.getString("attachments").replace(
						";", " ");
			}

			timeLineInfo.setStatus(tweetObject.has("text") ? (Uri
					.decode(tweetObject.getString("text")).replace("：", ":")
					.replaceAll("\\r|\\n|\\t|\\s", " ").replace("〜", "~")
					.replace("&#37;", "%").replace("&#38;", "*")
					.replace("&#43;", "+").replace("&#60;", "<")
					.replace("&#62;", ">").replace("\r", "\n")
					+ "\n" + resourcePath).replaceAll("\n+", "\n")
					: resourcePath);
			timeLineInfo.setFavorite(tweetObject.getString("favorited"));

			timeLineInfo.setTime(tweetObject.getString("create_at"));
			timeLineInfo.setImportantLevel(tweetObject
					.getInt("important_level"));
			if (tweetObject.has("forward_count")) {
				timeLineInfo.setRetweetCount(tweetObject
						.getString("forward_count"));
			} else {
				timeLineInfo.setRetweetCount("");
			}
			if (tweetObject.has("comments_count")) {
				timeLineInfo.setCommentCount(tweetObject
						.getString("comments_count"));
			} else {
				timeLineInfo.setCommentCount("");
			}

			// User
			user = tweetObject.getJSONObject("user");
			UserInfo userInfo = new UserInfo();
			userInfo.setUid(user.getString("id"));
			userInfo.setScreenName(Uri.decode(user.getString("screen_name")));
			userInfo.setDescription(Uri.decode(user.getString("description")));
			userInfo.setUserImageURL(user.getString("image_url"));

			// retweet
			if (tweetObject.has("retweet_tweet")) {
				JSONObject retObject = tweetObject
						.getJSONObject("retweet_tweet");
				if (!retObject.toString().equals("{}")) {
					timeLineInfo.setRetweeted(true);

					// Status And Image URL
					String retResourcePath = "";
					if (retObject.has("attachments")) {
						if (retObject.getString("attachments") != ";") {
							retResourcePath = retObject
									.getString("attachments").replace(";", " ");
						}
					}

					timeLineInfo
							.setRetweetedStatus(retObject.has("text") ? (Uri
									.decode(retObject.getString("text"))
									.replace("：", ":")
									.replaceAll("\\r|\\n|\\t|\\s", " ")
									.replace("〜", "~").replace("&#37;", "%")
									.replace("&#38;", "*")
									.replace("&#43;", "+")
									.replace("&#60;", "<")
									.replace("&#62;", ">").replace("\r", "\n")
									+ "\n" + retResourcePath).replaceAll("\n+",
									"\n") : retResourcePath);

					JSONObject originalUserObject = retObject
							.getJSONObject("user");
					userInfo.setRetweetedScreenName(Uri
							.decode(originalUserObject.getString("screen_name")));
					userInfo.setRetweetUserId(originalUserObject
							.getString("id"));

				}

			}

			timeLineInfo.setUserInfo(userInfo);
			timeLineInfoList.add(timeLineInfo);

		}

		return timeLineInfoList;

	}

	public static ArrayList<TimeLineInfo> parseSearchInfo(String msg)
			throws JSONException {

		ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return timeLineInfoList;
		}	
		try{
		JSONArray statuses = new JSONArray(msg);
		JSONObject status;
		JSONObject user;
		JSONObject group;
		TimeLineInfo timeLineInfo = null;
		for (int i = 0; i < statuses.length(); i++) {

			// Status
			status = statuses.getJSONObject(i);

			// tweet
			// JSONObject tweetObject = status.getJSONObject("tweet");
			timeLineInfo = new TimeLineInfo();
			timeLineInfo.setMessageId(status.getString("message_id"));
			String resourcePath = "";
			if (status.getString("attachments") != ";") {
				resourcePath = status.getString("attachments")
						.replace(";", " ");
			}

			timeLineInfo.setStatus(status.has("text") ? (Uri
					.decode(status.getString("text")).replace("：", ":")
					.replaceAll("\\r|\\n|\\t|\\s", " ").replace("〜", "~")
					.replace("&#37;", "%").replace("&#38;", "*")
					.replace("&#43;", "+").replace("&#60;", "<")
					.replace("&#62;", ">").replace("\r", "\n")
					+ "\n" + resourcePath).replaceAll("\n+", "\n")
					: resourcePath);
			timeLineInfo.setFavorite(status.getString("favorited"));

			timeLineInfo.setTime(status.getString("create_at"));
			timeLineInfo.setImportantLevel(status.getInt("important_level"));
			if (status.has("forward_count")) {
				timeLineInfo.setRetweetCount(status.getString("forward_count"));
			} else {
				timeLineInfo.setRetweetCount("");
			}
			if (status.has("comments_count")) {
				timeLineInfo
						.setCommentCount(status.getString("comments_count"));
			} else {
				timeLineInfo.setCommentCount("");
			}

			// User
			user = status.getJSONObject("user");
			UserInfo userInfo = new UserInfo();
			userInfo.setUid(user.getString("id"));
			userInfo.setScreenName(Uri.decode(user.getString("screen_name")));
			userInfo.setDescription(Uri.decode(user.getString("description")));
			userInfo.setUserImageURL(user.getString("image_url"));

			// retweet
			if (status.has("retweet_tweet")) {
				JSONObject retObject = status.getJSONObject("retweet_tweet");
				if (!retObject.toString().equals("{}")) {
					timeLineInfo.setRetweeted(true);

					// Status And Image URL
					String retResourcePath = "";
					if (retObject.has("attachments")) {
						if (retObject.getString("attachments") != ";") {
							retResourcePath = retObject
									.getString("attachments").replace(";", " ");
						}
					}

					timeLineInfo
							.setRetweetedStatus(retObject.has("text") ? (Uri
									.decode(retObject.getString("text"))
									.replace("：", ":")
									.replaceAll("\\r|\\n|\\t|\\s", " ")
									.replace("〜", "~").replace("&#37;", "%")
									.replace("&#38;", "*")
									.replace("&#43;", "+")
									.replace("&#60;", "<")
									.replace("&#62;", ">").replace("\r", "\n")
									+ "\n" + retResourcePath).replaceAll("\n+",
									"\n") : retResourcePath);

					JSONObject originalUserObject = retObject
							.getJSONObject("user");
					userInfo.setRetweetedScreenName(Uri
							.decode(originalUserObject.getString("screen_name")));
					userInfo.setRetweetUserId(originalUserObject
							.getString("id"));

				}

			}

			timeLineInfo.setUserInfo(userInfo);
			timeLineInfoList.add(timeLineInfo);
		}
		}catch (JSONException e) {
			e.printStackTrace();
		}

		return timeLineInfoList;

	}

	public static ArrayList<TrendInfo> parseTrendByType(String msg) {

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

				// trendInfo.setTrendId("");
				trendInfo.setNum(status.getString("count"));
				trendInfo.setHotword(status.getString("name"));
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
}
