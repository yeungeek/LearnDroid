package com.anhuioss.crowdroid.service.tencent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weibo4android.User;

import android.content.Context;

import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;

public class TencentParserHandler {

	public static UserInfo parseUserInfo(String message) {

		UserInfo userInfo = new UserInfo();
		if (message == null) {
			return userInfo;
		}
		try {
			JSONObject user = new JSONObject(message);
			String ret = user.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (user.has("data")) {
				// data
				JSONObject userData = user.getJSONObject("data");

				userInfo.setUid(userData.has("uid") ? userData.getString("uid")
						: "");
				userInfo.setScreenName(userData.getString("nick"));
				userInfo.setUserImageURL("".equals(userData.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
						: userData.getString("head") + "/50");
				userInfo.setUserName(userData.getString("name"));
				userInfo.setDescription(userData.getString("introduction"));
				userInfo.setFollowerCount(userData.getString("fansnum"));
				userInfo.setFollowCount(userData.getString("idolnum"));
				userInfo.setVerified(userData.getString("isvip").equals("0") ? "false"
						: "true");

				try {
					userInfo.setFollow(userData.getString("ismyfans").equals(
							"0") ? "false" : "true");
					userInfo.setFollowed(userData.getString("ismyidol").equals(
							"0") ? "false" : "true");
				} catch (Exception e) {
				}
				userInfo.setStatusCount(userData.getString("tweetnum"));
				userInfo.setCountry_code(userData.getString("country_code"));
				userInfo.setProvince_code(userData.getString("province_code"));
				userInfo.setCity_code(userData.getString("city_code"));
				String text = "";
				String textIds = "";
				if (userData.has("tag") && !userData.isNull("tag")) {
					// Parse image
					ArrayList<String> tags = new ArrayList<String>();
					ArrayList<String> tagIds = new ArrayList<String>();
					try {
						JSONArray tagJsonArray = userData.getJSONArray("tag");
						JSONObject nameObject;
						String tag = null;
						String tagId = null;
						for (int j = 0; j < tagJsonArray.length(); j++) {
							nameObject = tagJsonArray.getJSONObject(j);
							if (nameObject.has("name")) {
								tag = nameObject.getString("name");
								tagId = nameObject.getString("id");
								tags.add(tag);
								tagIds.add(tagId);
							}

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					for (String tag : tags) {
						text = "[" + tag + "]" + " " + text;
					}
					for (String id : tagIds) {
						textIds = "[" + id + "]" + " " + textIds;
					}
					userInfo.setTag(text);
					userInfo.setTagId(textIds);
				}

				if (userInfo.getUid().equals("")) {
					userInfo.setUid(String.valueOf(userInfo.getScreenName()
							.hashCode()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userInfo;
	}

	public static ArrayList<TimeLineInfo> parseTimeline(String message) {
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
		JSONObject userResult;
		try {
			userResult = new JSONObject(message);
			String ret = userResult.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (userResult.has("data")) {

				// Parse data
				JSONObject dataJsonObject = userResult.getJSONObject("data");

				if (dataJsonObject.has("info")) {

					// Parse info
					JSONArray infoJsonArray = dataJsonObject
							.getJSONArray("info");
					JSONObject subInfoJsonObject;
					TimeLineInfo timelineInfo;
					UserInfo userInfo;
					for (int i = 0; i < infoJsonArray.length(); i++) {

						subInfoJsonObject = infoJsonArray.getJSONObject(i);
						timelineInfo = new TimeLineInfo();
						userInfo = new UserInfo();

						if (dataJsonObject.has("pos")) {
							timelineInfo.setPosition(dataJsonObject
									.getString("pos"));
						}

						// Parse timestamp
						if (subInfoJsonObject.has("timestamp")) {
							String time = subInfoJsonObject
									.getString("timestamp");
							timelineInfo.setTimeStamp(time);
							Date date = new Date(Long.valueOf(time) * 1000L);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							timelineInfo.setTime(sdf.format(date));
						}

						// Parse id
						timelineInfo.setMessageId(subInfoJsonObject
								.getString("id"));
						String text = clearHtmlTag(subInfoJsonObject
								.getString("text"));

						if (subInfoJsonObject.has("count")) {
							timelineInfo.setRetweetCount(subInfoJsonObject
									.getString("count"));
						} else {
							timelineInfo.setRetweetCount("");
						}
						if (subInfoJsonObject.has("mcount")) {
							timelineInfo.setCommentCount(subInfoJsonObject
									.getString("mcount"));
						} else {
							timelineInfo.setCommentCount("");
						}

						// first：.isNull()方法 OK
						if (!subInfoJsonObject.isNull("image")) {
							// Parse image
							ArrayList<String> imageUrls = new ArrayList<String>();
							try {
								JSONArray imageUrlsJsonArray = subInfoJsonObject
										.getJSONArray("image");
								int lenght = imageUrlsJsonArray.length();
								if (lenght != 0) {
									for (int j = 0; j < imageUrlsJsonArray
											.length(); j++) {
										String imageUrl = imageUrlsJsonArray
												.getString(j);
										imageUrls.add(imageUrl);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							for (String imageUrl : imageUrls) {
								text = text + "\n" + imageUrl + "/";
							}
						}
						// second：.optJSONObject()方法
						// OK---------------------------------
						// if(subInfoJsonObject.optJSONObject("image") != null){
						// // Parse image
						// ArrayList<String> imageUrls = new
						// ArrayList<String>();
						// try {
						// JSONArray imageUrlsJsonArray =
						// subInfoJsonObject.optJSONArray("image");
						//
						// int lenght = imageUrlsJsonArray.length();
						//
						// if(lenght != 0){
						//
						// for (int j = 0; j < imageUrlsJsonArray.length(); j++)
						// {
						// String imageUrl = imageUrlsJsonArray.getString(j);
						// imageUrls.add(imageUrl);
						// }
						// }
						//
						// } catch (JSONException e) {
						// e.printStackTrace();
						// }
						//
						// // Parse text and set image URLs to text
						// for (String imageUrl : imageUrls) {
						// text = text + "\n" + imageUrl + "/";
						// }
						// }
						// -----------------------------------------------------------------------

						timelineInfo.setStatus(text);

						// Parse uid
						userInfo.setUid(subInfoJsonObject.has("uid") ? subInfoJsonObject
								.getString("uid") : "");

						// Parse nick
						userInfo.setScreenName(subInfoJsonObject.has("nick") ? subInfoJsonObject
								.getString("nick") : "");
						userInfo.setUserName(subInfoJsonObject.has("name") ? subInfoJsonObject
								.getString("name") : "");
						if (subInfoJsonObject.has("head")) {
							userInfo.setUserImageURL(""
									.equals(subInfoJsonObject.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
									: subInfoJsonObject.getString("head")
											+ "/50");
						}

						// Parse isvip
						if (subInfoJsonObject.has("isvip")) {
							userInfo.setVerified(subInfoJsonObject.getString(
									"isvip").equals("0") ? "false" : "true");
						}

						if (userInfo.getUid().equals("")) {
							userInfo.setUid(String.valueOf(userInfo
									.getScreenName().hashCode()));
						}

						// first：
						if (!subInfoJsonObject.isNull("source")
								&& subInfoJsonObject.has("source")) {
							// or second：
							// if(subInfoJsonObject.optJSONObject("source")!=null){
							try {

								// Parse source
								JSONObject retweetObject = subInfoJsonObject
										.optJSONObject("source");
								// or second:
								// JSONObject retweetObject =
								// subInfoJsonObject.optJSONObject("source");
								if (!retweetObject.equals(null)
										&& retweetObject != null) {
									timelineInfo.setRetweeted(true);

									String textRetweet = clearHtmlTag(retweetObject
											.getString("text"));

									if (!retweetObject.isNull("image")) {
										// Parse image
										ArrayList<String> imageUrlsRetweet = new ArrayList<String>();
										try {
											JSONArray imageUrlsJsonArray = retweetObject
													.getJSONArray("image");
											if (imageUrlsJsonArray.length() != 0) {
												String imageUrl = null;
												for (int j = 0; j < imageUrlsJsonArray
														.length(); j++) {
													imageUrl = imageUrlsJsonArray
															.getString(j);
													imageUrlsRetweet
															.add(imageUrl);
												}
											}

										} catch (JSONException e) {
											e.printStackTrace();
										}
										for (String imageUrl : imageUrlsRetweet) {
											textRetweet = textRetweet + "\n"
													+ imageUrl + "/";
										}
									}

									// second:--------------------------------------------------
									// if(subInfoJsonObject.optJSONArray("image")
									// != null){
									// // Parse image
									// ArrayList<String> imageUrlsRetweet = new
									// ArrayList<String>();
									// try {
									// JSONArray retweetImageUrlsJsonArray =
									// retweetObject
									// .optJSONArray("image");
									// if(retweetImageUrlsJsonArray.length() !=
									// 0){
									// for (int j = 0; j <
									// retweetImageUrlsJsonArray.length(); j++)
									// {
									// String imageUrl =
									// retweetImageUrlsJsonArray
									// .getString(j);
									// imageUrlsRetweet.add(imageUrl);
									// }
									// }
									//
									// } catch (JSONException e) {
									// e.printStackTrace();
									// }
									//
									// // Parse text and set image URLs to text
									// for (String imageUrl : imageUrlsRetweet)
									// {
									// textRetweet = textRetweet + "\n" +
									// imageUrl
									// + "/";
									// }
									// }
									// ---------------------------------------------------------
									timelineInfo
											.setRetweetedStatus(textRetweet);

									// Parse nick
									userInfo.setRetweetedScreenName(retweetObject
											.getString("nick"));

									// Parse uid
									userInfo.setRetweetUserId(retweetObject
											.has("uid") ? retweetObject
											.getString("uid") : "");

									// -----------------------------------------------------------
									if (userInfo.getRetweetUserId().equals("")) {
										userInfo.setRetweetUserId(String
												.valueOf(userInfo
														.getRetweetedScreenName()
														.hashCode()));
									}
									// -----------------------------------------------------------

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						timelineInfo.setUserInfo(userInfo);

						timelineInfoList.add(timelineInfo);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Return
		return timelineInfoList;

	}

	public static String parseResponseCode(String msg) {

		if (msg == null) {
			return null;
		}

		try {

			JSONObject status = new JSONObject(msg);
			String ret = status.getString("ret");
			if ("0".equals(ret)) {
				return "200";
			} else if("4".equals(ret)){
				return status.getString("errcode") ;
			} else if("5".equals(ret)){
				return status.getString("errcode") ;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "0";

	}

	public static ArrayList<TimeLineInfo> parseDirectMessageReceived(String msg) {
		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}
		try {
			JSONObject dmSendResult = new JSONObject(msg);

			String ret = dmSendResult.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (dmSendResult.has("data")) {
				// data
				JSONObject dmSendData = dmSendResult.getJSONObject("data");
				// data_info

				JSONArray jArray = dmSendData.getJSONArray("info");

				JSONObject jObject = null;
				TimeLineInfo timeLineInfo = null;
				UserInfo recipientInfo = null;
				for (int i = 0; i < jArray.length(); i++) {

					jObject = (JSONObject) jArray.get(i);
					recipientInfo = new UserInfo();
					timeLineInfo = new TimeLineInfo();

					// Message ID
					timeLineInfo.setMessageId(jObject.getString("id"));

					// Time Stamp
					String time = jObject.getString("timestamp");
					timeLineInfo.setTimeStamp(time);
					timeLineInfo.setRetweetCount("");
					timeLineInfo.setCommentCount("");

					// Time
					Date date = new Date(Long.valueOf(time) * 1000L);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					timeLineInfo.setTime(sdf.format(date));

					// Text
					timeLineInfo.setStatus(clearHtmlTag(jObject
							.getString("text")));

					// User ID
					recipientInfo.setUid(jObject.has("uid") ? jObject
							.getString("uid") : "");

					// User Screen Name
					recipientInfo.setScreenName(jObject.getString("nick"));

					// User Name
					recipientInfo.setUserName(jObject.getString("name"));

					// User Image URL
					recipientInfo
							.setUserImageURL("".equals(jObject
									.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
									: jObject.getString("head") + "/50");

					// VIP
					recipientInfo.setVerified(jObject.getString("isvip")
							.equals("0") ? "false" : "true");

					// -----------------------------------------------------------
					if (recipientInfo.getUid().equals("")) {
						recipientInfo.setUid(String.valueOf(recipientInfo
								.getScreenName().hashCode()));
					}
					// -----------------------------------------------------------

					timeLineInfo.setUserInfo(recipientInfo);

					jsonInfoList.add(timeLineInfo);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonInfoList;

	}

	public static ArrayList<TimeLineInfo> parseDirectMessageSent(String msg) {
		ArrayList<TimeLineInfo> jsonInfoList = new ArrayList<TimeLineInfo>();
		if (msg == null) {
			return jsonInfoList;
		}
		try {
			JSONObject dmSendResult = new JSONObject(msg);

			String ret = dmSendResult.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (dmSendResult.has("data")) {
				// data
				JSONObject dmSendData = dmSendResult.getJSONObject("data");
				// data_info

				JSONArray jArray = dmSendData.getJSONArray("info");
				JSONObject jObject = null;
				TimeLineInfo timeLineInfo = null;
				UserInfo recipientInfo = null;
				for (int i = 0; i < jArray.length(); i++) {

					jObject = (JSONObject) jArray.get(i);
					recipientInfo = new UserInfo();
					timeLineInfo = new TimeLineInfo();

					// Message ID
					timeLineInfo.setMessageId(jObject.getString("id"));

					// Time Stamp
					String time = jObject.getString("timestamp");
					timeLineInfo.setTimeStamp(time);
					timeLineInfo.setRetweetCount("");
					timeLineInfo.setCommentCount("");

					// Time
					Date date = new Date(Long.valueOf(time) * 1000L);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					timeLineInfo.setTime(sdf.format(date));

					// Text
					timeLineInfo.setStatus(clearHtmlTag(jObject
							.getString("text")));

					// User ID
					recipientInfo.setUid(jObject.has("uid") ? jObject
							.getString("uid") : "");

					// User Screen Name
					recipientInfo.setScreenName(jObject.getString("nick"));

					// User Name
					recipientInfo.setUserName(jObject.getString("name"));

					// User Image URL
					recipientInfo
							.setUserImageURL("".equals(jObject
									.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
									: jObject.getString("head") + "/50");

					// VIP
					recipientInfo.setVerified(jObject.getString("isvip")
							.equals("0") ? "false" : "true");

					// -----------------------------------------------------------
					if (recipientInfo.getUid().equals("")) {
						recipientInfo.setUid(String.valueOf(recipientInfo
								.getScreenName().hashCode()));
					}
					// -----------------------------------------------------------

					timeLineInfo.setUserInfo(recipientInfo);

					jsonInfoList.add(timeLineInfo);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

			// ret
			String ret = statuses.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (statuses.has("data")) {
				// data
				JSONObject userData = statuses.getJSONObject("data");
				// data_info
				JSONArray info;

				if (userData.has("info")) {
					info = userData.getJSONArray("info");

					// Get Status, User
					JSONObject status = null;
					UserInfo userInfo = null;
					for (int i = 0; i < info.length(); i++) {

						status = (JSONObject) info.get(i);

						// User
						userInfo = new UserInfo();

						userInfo.setUserImageURL(status.getString("head"));
						userInfo.setUid(status.has("uid") ? status
								.getString("uid") : "");
						userInfo.setFollowerCount(status.getString("fansnum"));
						userInfo.setScreenName(status.getString("nick"));
						userInfo.setUserName(status.getString("name"));
						userInfo.setVerified(status.getString("isvip").equals(
								"0") ? "false" : "true");

						// Add To Result
						userInfoList.add(userInfo);

					}
				}
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

			// ret
			String ret = statuses.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (statuses.has("data")) {
				// data
				JSONObject userData = statuses.getJSONObject("data");
				// data_info
				JSONArray info;

				if (userData.has("info")) {
					info = userData.getJSONArray("info");

					// Get Status, User
					JSONObject status = null;
					UserInfo userInfo = null;
					for (int i = 0; i < info.length(); i++) {

						status = (JSONObject) info.get(i);

						// User
						userInfo = new UserInfo();

						userInfo.setUid(status.has("uid") ? status
								.getString("uid") : "");

						userInfo.setUserImageURL("".equals(status
								.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
								: status.getString("head") + "/50");
						userInfo.setFollowerCount(status.getString("idolnum"));
						userInfo.setFollowCount(status.getString("fansnum"));
						userInfo.setDescription("");
						userInfo.setScreenName(status.getString("nick"));
						userInfo.setUserName(status.getString("name"));
						userInfo.setVerified(status.getString("isvip").equals(
								"0") ? "false" : "true");

						// -----------------------------------------------------------
						if (userInfo.getUid().equals("")) {
							userInfo.setUid(String.valueOf(userInfo
									.getScreenName().hashCode()));
						}
						// -----------------------------------------------------------

						// Add To Result
						userInfoList.add(userInfo);

					}
				}
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

			// ret
			String ret = statuses.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (statuses.has("data")) {
				// data
				JSONObject userData = statuses.getJSONObject("data");
				// data_info
				JSONArray info = userData.getJSONArray("info");

				// Get Status, User
				JSONObject status = null;
				UserInfo userInfo = null;
				for (int i = 0; i < info.length(); i++) {

					status = (JSONObject) info.get(i);

					// User
					userInfo = new UserInfo();

					userInfo.setUid(status.has("uid") ? status.getString("uid")
							: "");

					userInfo.setUserImageURL("".equals(status.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
							: status.getString("head") + "/50");
					userInfo.setFollowerCount(status.getString("idolnum"));
					userInfo.setFollowCount(status.getString("fansnum"));
					userInfo.setDescription("");
					userInfo.setScreenName(status.getString("nick"));
					userInfo.setUserName(status.getString("name"));
					userInfo.setVerified(status.getString("isvip").equals("0") ? "false"
							: "true");

					// -----------------------------------------------------------
					if (userInfo.getUid().equals("")) {
						userInfo.setUid(String.valueOf(userInfo.getScreenName()
								.hashCode()));
					}
					// -----------------------------------------------------------

					// Add To Result
					userInfoList.add(userInfo);

				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Return
		return userInfoList;
	}

	/**
	 * getFindPeopleInfo
	 * 
	 * @param msg
	 * @return
	 */
	public static ArrayList<UserInfo> parseStrangersInfo(String msg) {
		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONObject statuses = new JSONObject(msg);

			// ret
			String ret = statuses.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (statuses.has("data")) {
				JSONObject userData = statuses.getJSONObject("data");
				JSONArray info;

				if (userData.has("info")) {
					info = userData.getJSONArray("info");

					// Get Status, User
					JSONObject status;
					UserInfo userInfo;
					for (int i = 0; i < info.length(); i++) {

						status = (JSONObject) info.get(i);

						// User
						userInfo = new UserInfo();

						userInfo.setUserImageURL(status.getString("head"));
						userInfo.setUid(status.has("openid") ? status
								.getString("openid") : "");
						userInfo.setFollowerCount(status.getString("fansnum"));
						if (!status.has("description")) {
							userInfo.setDescription("");
						} else {
							userInfo.setDescription(status
									.getString("description"));
						}
						userInfo.setFollowCount(status.getString("idolnum"));
						userInfo.setScreenName(status.getString("nick"));
						userInfo.setUserName(status.getString("name"));
						userInfo.setUserImageURL("".equals(status
								.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
								: status.getString("head") + "/50");
						userInfo.setVerified(status.getString("isvip"));

						String text = "";
						if (status.has("tag")) {
							// Parse image
							ArrayList<String> tags = new ArrayList<String>();
							try {
								JSONArray tagJsonArray = status
										.getJSONArray("tag");
								JSONObject nameObject;
								String tag = null;

								for (int j = 0; j < tagJsonArray.length(); j++) {
									nameObject = tagJsonArray.getJSONObject(j);
									if (nameObject.has("name")) {
										tag = nameObject.getString("name");
										tags.add(tag);
									}

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
							for (String tag : tags) {
								text = "[" + tag + "]" + " " + text;
							}
							userInfo.setTag("标签：" + text);
						}

						// Add To Result
						userInfoList.add(userInfo);

					}
				}
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

		return relation;
	}

	public static String[] parseRelationNew(String msg) {
		String[] relation = new String[2];
		// if(msg == null) {
		// return relation;
		// }
		// try{
		// JSONObject root = new JSONObject(msg);
		// JSONObject relationship = root.getJSONObject("target");
		//
		// relation[0] = relationship.getString("following");
		// relation[1] = relationship.getString("followed_by");
		//
		// }catch(JSONException e){
		// e.printStackTrace();
		// }
		//
		return relation;
	}

	public static ArrayList<TrendInfo> parseTrendList(String msg) {
		return null;
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
			JSONObject data = statusObject.getJSONObject("data");
			JSONArray info;
			if (data.has("info")) {
				info = data.getJSONArray("info");
				JSONObject subInfo;
				TrendInfo trendInfo;
				for (int i = 0; i < info.length(); i++) {
					subInfo = info.getJSONObject(i);
					trendInfo = new TrendInfo();
					trendInfo.setName(subInfo.getString("name"));
					trendInfo.setHotword(subInfo.getString("keywords"));
					trendInfoList.add(trendInfo);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return trendInfoList;
	}

	public static ArrayList<TimeLineInfo> parseCommentTimeline(String message) {
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
		JSONObject userResult;
		try {
			userResult = new JSONObject(message);
			String ret = userResult.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (!userResult.isNull("data")) {
				try {
					JSONObject dataJsonObject = userResult
							.getJSONObject("data");
					if (!dataJsonObject.isNull("info")) {
						// Parse info
						JSONArray infoJsonArray = dataJsonObject
								.getJSONArray("info");
						JSONObject subInfoJsonObject;
						TimeLineInfo timelineInfo;
						UserInfo userInfo;
						for (int i = 0; i < infoJsonArray.length(); i++) {

							subInfoJsonObject = infoJsonArray.getJSONObject(i);
							timelineInfo = new TimeLineInfo();
							userInfo = new UserInfo();

							String time = subInfoJsonObject
									.getString("timestamp");
							timelineInfo.setTimeStamp(time);
							Date date = new Date(Long.valueOf(time) * 1000L);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							timelineInfo.setTime(sdf.format(date));

							timelineInfo.setMessageId(subInfoJsonObject
									.getString("id"));
							String text = clearHtmlTag(subInfoJsonObject
									.getString("text"));

							if (subInfoJsonObject.optJSONArray("image") != null) {
								// Parse image
								ArrayList<String> imageUrls = new ArrayList<String>();
								try {
									JSONArray imageUrlsJsonArray = subInfoJsonObject
											.getJSONArray("image");
									for (int j = 0; j < imageUrlsJsonArray
											.length(); j++) {
										String imageUrl = imageUrlsJsonArray
												.getString(j);
										imageUrls.add(imageUrl);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								// Parse text and set image URLs to text
								for (String imageUrl : imageUrls) {
									text = text + "\n" + imageUrl + "/";
								}
							}

							timelineInfo.setStatus(text);

							if (subInfoJsonObject.has("count")) {
								timelineInfo.setRetweetCount(subInfoJsonObject
										.getString("count"));
							} else {
								timelineInfo.setRetweetCount("");
							}
							if (subInfoJsonObject.has("mcount")) {
								timelineInfo.setCommentCount(subInfoJsonObject
										.getString("mcount"));
							} else {
								timelineInfo.setCommentCount("");
							}

							// Parse uid
							userInfo.setUid(subInfoJsonObject.has("uid") ? subInfoJsonObject
									.getString("uid") : "");

							// Parse nick
							userInfo.setScreenName(subInfoJsonObject
									.getString("nick"));
							userInfo.setUserName(subInfoJsonObject
									.getString("name"));
							userInfo.setUserImageURL(""
									.equals(subInfoJsonObject.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
									: subInfoJsonObject.getString("head")
											+ "/50");

							// Parse isvip
							userInfo.setVerified(subInfoJsonObject.getString(
									"isvip").equals("0") ? "false" : "true");

							if (userInfo.getUid().equals("")) {
								userInfo.setUid(String.valueOf(userInfo
										.getScreenName().hashCode()));
							}

							timelineInfo.setUserInfo(userInfo);

							timelineInfoList.add(timelineInfo);

						}

					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Return
		return timelineInfoList;
	}

	public static ArrayList<EmotionInfo> parseEmotions(String message) {

		ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();
		if (message == null) {
			return emotionList;
		}

		// --------add more-------------------------------------
		EmotionInfo emotionInfo15 = new EmotionInfo();
		emotionInfo15.setPhrase("/" + "万圣节");
		emotionInfo15
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/161.gif");
		emotionList.add(emotionInfo15);

		EmotionInfo emotionInfo16 = new EmotionInfo();
		emotionInfo16.setPhrase("/" + "喜糖");
		emotionInfo16
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/150.gif");
		emotionList.add(emotionInfo16);

		EmotionInfo emotionInfo17 = new EmotionInfo();
		emotionInfo17.setPhrase("/" + "红包");
		emotionInfo17
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/151.gif");
		emotionList.add(emotionInfo17);

		EmotionInfo emotionInfo18 = new EmotionInfo();
		emotionInfo18.setPhrase("/" + "百合花");
		emotionInfo18
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/138.gif");
		emotionList.add(emotionInfo18);

		EmotionInfo emotionInfo19 = new EmotionInfo();
		emotionInfo19.setPhrase("/" + "黄丝带");
		emotionInfo19
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/137.gif");
		emotionList.add(emotionInfo19);

		EmotionInfo emotionInfo20 = new EmotionInfo();
		emotionInfo20.setPhrase("/" + "祈福");
		emotionInfo20
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/136.gif");
		emotionList.add(emotionInfo20);

		EmotionInfo emotionInfo21 = new EmotionInfo();
		emotionInfo21.setPhrase("/" + "黑丝带");
		emotionInfo21
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/135.gif");
		emotionList.add(emotionInfo21);

		EmotionInfo emotionInfo22 = new EmotionInfo();
		emotionInfo22.setPhrase("/" + "月饼");
		emotionInfo22
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/152.gif");
		emotionList.add(emotionInfo22);

		EmotionInfo emotionInfo23 = new EmotionInfo();
		emotionInfo23.setPhrase("/" + "酒");
		emotionInfo23
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/153.gif");
		emotionList.add(emotionInfo23);

		EmotionInfo emotionInfo24 = new EmotionInfo();
		emotionInfo24.setPhrase("/" + "蛋黄月饼");
		emotionInfo24
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/156.gif");
		emotionList.add(emotionInfo24);

		EmotionInfo emotionInfo25 = new EmotionInfo();
		emotionInfo25.setPhrase("/" + "玉兔");
		emotionInfo25
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/155.gif");
		emotionList.add(emotionInfo25);

		EmotionInfo emotionInfo26 = new EmotionInfo();
		emotionInfo26.setPhrase("/" + "灯笼");
		emotionInfo26
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/157.gif");
		emotionList.add(emotionInfo26);

		EmotionInfo emotionInfo27 = new EmotionInfo();
		emotionInfo27.setPhrase("/" + "红旗");
		emotionInfo27
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/158.gif");
		emotionList.add(emotionInfo27);

		EmotionInfo emotionInfo28 = new EmotionInfo();
		emotionInfo28.setPhrase("/" + "日历");
		emotionInfo28
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/159.gif");
		emotionList.add(emotionInfo28);

		EmotionInfo emotionInfo29 = new EmotionInfo();
		emotionInfo29.setPhrase("/" + "七休哥");
		emotionInfo29
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/160.gif");
		emotionList.add(emotionInfo29);

		EmotionInfo emotionInfo30 = new EmotionInfo();
		emotionInfo30.setPhrase("/" + "害羞");
		emotionInfo30
				.setUrl("http://mat1.gtimg.com/www/mb/images/cFace/h15.gif");
		emotionList.add(emotionInfo30);

		EmotionInfo emotionInfo0 = new EmotionInfo();
		emotionInfo0.setPhrase("/" + "给力");
		emotionInfo0.setUrl("http://mat1.gtimg.com/www/mb/images/face/162.gif");
		emotionList.add(emotionInfo0);

		EmotionInfo emotionInfo1 = new EmotionInfo();
		emotionInfo1.setPhrase("/" + "围观");
		emotionInfo1.setUrl("http://mat1.gtimg.com/www/mb/images/face/163.gif");
		emotionList.add(emotionInfo1);

		EmotionInfo emotionInfo2 = new EmotionInfo();
		emotionInfo2.setPhrase("/" + "围脖");
		emotionInfo2.setUrl("http://mat1.gtimg.com/www/mb/images/face/164.gif");
		emotionList.add(emotionInfo2);

		EmotionInfo emotionInfo3 = new EmotionInfo();
		emotionInfo3.setPhrase("/" + "雪花");
		emotionInfo3.setUrl("http://mat1.gtimg.com/www/mb/images/face/165.gif");
		emotionList.add(emotionInfo3);

		EmotionInfo emotionInfo4 = new EmotionInfo();
		emotionInfo4.setPhrase("/" + "手套");
		emotionInfo4.setUrl("http://mat1.gtimg.com/www/mb/images/face/166.gif");
		emotionList.add(emotionInfo4);

		EmotionInfo emotionInfo5 = new EmotionInfo();
		emotionInfo5.setPhrase("/" + "袜子");
		emotionInfo5.setUrl("http://mat1.gtimg.com/www/mb/images/face/167.gif");
		emotionList.add(emotionInfo5);

		EmotionInfo emotionInfo6 = new EmotionInfo();
		emotionInfo6.setPhrase("/" + "铃铛");
		emotionInfo6.setUrl("http://mat1.gtimg.com/www/mb/images/face/168.gif");
		emotionList.add(emotionInfo6);

		EmotionInfo emotionInfo7 = new EmotionInfo();
		emotionInfo7.setPhrase("/" + "圣诞帽");
		emotionInfo7.setUrl("http://mat1.gtimg.com/www/mb/images/face/169.gif");
		emotionList.add(emotionInfo7);

		EmotionInfo emotionInfo8 = new EmotionInfo();
		emotionInfo8.setPhrase("/" + "圣诞树");
		emotionInfo8.setUrl("http://mat1.gtimg.com/www/mb/images/face/170.gif");
		emotionList.add(emotionInfo8);

		EmotionInfo emotionInfo9 = new EmotionInfo();
		emotionInfo9.setPhrase("/" + "圣诞老人");
		emotionInfo9.setUrl("http://mat1.gtimg.com/www/mb/images/face/171.gif");
		emotionList.add(emotionInfo9);

		EmotionInfo emotionInfo10 = new EmotionInfo();
		emotionInfo10.setPhrase("/" + "巧克力");
		emotionInfo10
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/172.gif");
		emotionList.add(emotionInfo10);

		EmotionInfo emotionInfo11 = new EmotionInfo();
		emotionInfo11.setPhrase("/" + "福字");
		emotionInfo11
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/173.gif");
		emotionList.add(emotionInfo11);

		EmotionInfo emotionInfo12 = new EmotionInfo();
		emotionInfo12.setPhrase("/" + "鞭炮");
		emotionInfo12
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/174.gif");
		emotionList.add(emotionInfo12);

		EmotionInfo emotionInfo13 = new EmotionInfo();
		emotionInfo13.setPhrase("/" + "腊八粥");
		emotionInfo13
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/175.gif");
		emotionList.add(emotionInfo13);

		EmotionInfo emotionInfo14 = new EmotionInfo();
		emotionInfo14.setPhrase("/" + "龙年");
		emotionInfo14
				.setUrl("http://mat1.gtimg.com/www/mb/images/face/176.gif");
		emotionList.add(emotionInfo14);

		// --------------------------------------------------

		try {

			JSONObject content = new JSONObject(message);
			JSONObject data = content.getJSONObject("data");
			JSONArray emotions = data.getJSONArray("emotions");
			JSONObject emotion = null;
			EmotionInfo emotionInfo = null;
			for (int i = 0; i < emotions.length(); i++) {

				emotion = (JSONObject) emotions.get(i);
				emotionInfo = new EmotionInfo();

				// Name
				if (emotion.has("name")) {
					emotionInfo.setPhrase("/" + emotion.getString("name"));
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

	public static ArrayList<EmotionInfo> parseEmotionsFromFile(Context context) {

		// Prepare Data For Result
		ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();

		try {

			// Get File Name
			String tipsFileName = "emotions.txt";

			// Read Tips File And Get Content
			InputStreamReader inputReader = new InputStreamReader(context
					.getResources().getAssets().open(tipsFileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = bufReader.readLine();
			EmotionInfo emotionInfo = null;
			while (line != null) {

				String[] data = line.split(",");
				if (data.length >= 2) {

					emotionInfo = new EmotionInfo();
					// Name
					emotionInfo.setPhrase("/" + data[1]);
					// URL
					emotionInfo.setUrl(data[0]);

					emotionList.add(emotionInfo);

				}

				line = bufReader.readLine();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return emotionList;

	}

	public static String[] parseUnreadMessage(String message) {

		String[] count = new String[4];
		if (message == null) {
			return count;
		}

		try {

			JSONObject jsonObject = new JSONObject(message);
			String ret = jsonObject.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			JSONObject jsonData = (JSONObject) jsonObject.get("data");
			count[0] = jsonData.getString("home");
			count[1] = jsonData.getString("mentions");
			count[2] = jsonData.getString("private");
			count[3] = jsonData.getString("fans");

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
			JSONObject user = new JSONObject(message);
			String ret = user.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (user.has("data")) {
				JSONArray userData = user.getJSONArray("data");

				JSONObject userObject = null;
				UserInfo userInfo = null;
				for (int i = 0; i < userData.length(); i++) {

					userObject = (JSONObject) userData.get(i);

					userInfo = new UserInfo();

					userInfo.setScreenName(userObject.getString("nick"));
					userInfo.setUserName(userObject.getString("name"));
					userInfo.setUserImageURL("".equals(userObject
							.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
							: userObject.getString("head") + "/50");
					userInfo.setUid(userObject.has("uid") ? userObject
							.getString("uid") : "");

					if (userInfo.getUid().equals("")) {
						userInfo.setUid(String.valueOf(userInfo.getScreenName()
								.hashCode()));
					}

					userInfoList.add(userInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userInfoList;
	}

	public static ArrayList<TimeLineInfo> parseUserCommentTimeline(
			String message) {
		return null;
	}

	public static ArrayList<TimeLineInfo> parseFavoriteList(String message)
			throws JSONException {

		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
		JSONObject userResult;
		try {
			userResult = new JSONObject(message);
			String ret = userResult.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (userResult.has("data")) {

				// Parse data
				JSONObject dataJsonObject = userResult.getJSONObject("data");

				if (dataJsonObject.has("info")) {

					// Parse info
					JSONArray infoJsonArray = dataJsonObject
							.getJSONArray("info");
					JSONObject subInfoJsonObject;
					TimeLineInfo timelineInfo;
					UserInfo userInfo;
					for (int i = 0; i < infoJsonArray.length(); i++) {

						subInfoJsonObject = infoJsonArray.getJSONObject(i);
						timelineInfo = new TimeLineInfo();
						userInfo = new UserInfo();

						// Parse timestamp
						String time = subInfoJsonObject.getString("timestamp");
						timelineInfo.setTimeStamp(time);
						Date date = new Date(Long.valueOf(time) * 1000L);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						timelineInfo.setTime(sdf.format(date));

						// Parse id
						timelineInfo.setMessageId(subInfoJsonObject
								.getString("id"));
						String text = clearHtmlTag(subInfoJsonObject
								.getString("text"));
						if (subInfoJsonObject.has("count")) {
							timelineInfo.setRetweetCount(subInfoJsonObject
									.getString("count"));
						} else {
							timelineInfo.setRetweetCount("");
						}
						if (subInfoJsonObject.has("mcount")) {
							timelineInfo.setCommentCount(subInfoJsonObject
									.getString("mcount"));
						} else {
							timelineInfo.setCommentCount("");
						}
						if (!subInfoJsonObject.isNull("image")) {
							// Parse image
							ArrayList<String> imageUrls = new ArrayList<String>();
							try {
								JSONArray imageUrlsJsonArray = subInfoJsonObject
										.getJSONArray("image");
								int lenght = imageUrlsJsonArray.length();
								if (lenght != 0) {
									for (int j = 0; j < imageUrlsJsonArray
											.length(); j++) {
										String imageUrl = imageUrlsJsonArray
												.getString(j);
										imageUrls.add(imageUrl);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							for (String imageUrl : imageUrls) {
								text = text + "\n" + imageUrl + "/";
							}
						}
						timelineInfo.setStatus(text);

						timelineInfo.setFavorite("true");

						// Parse uid
						userInfo.setUid(subInfoJsonObject.has("uid") ? subInfoJsonObject
								.getString("uid") : "");

						// Parse nick
						userInfo.setScreenName(subInfoJsonObject
								.getString("nick"));
						userInfo.setUserName(subInfoJsonObject
								.getString("name"));
						userInfo.setUserImageURL("".equals(subInfoJsonObject
								.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
								: subInfoJsonObject.getString("head") + "/50");

						// Parse isvip
						userInfo.setVerified(subInfoJsonObject.getString(
								"isvip").equals("0") ? "false" : "true");

						if (userInfo.getUid().equals("")) {
							userInfo.setUid(String.valueOf(userInfo
									.getScreenName().hashCode()));
						}

						if (!subInfoJsonObject.isNull("source")) {
							try {

								// Parse source
								JSONObject retweetObject = subInfoJsonObject
										.optJSONObject("source");
								if (!retweetObject.equals(null)
										&& retweetObject != null) {
									timelineInfo.setRetweeted(true);
									String textRetweet = clearHtmlTag(retweetObject
											.getString("text"));
									if (!retweetObject.isNull("image")) {
										ArrayList<String> imageUrlsRetweet = new ArrayList<String>();
										try {
											JSONArray imageUrlsJsonArray = retweetObject
													.getJSONArray("image");
											if (imageUrlsJsonArray.length() != 0) {
												String imageUrl = null;
												for (int j = 0; j < imageUrlsJsonArray
														.length(); j++) {
													imageUrl = imageUrlsJsonArray
															.getString(j);
													imageUrlsRetweet
															.add(imageUrl);
												}
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
										for (String imageUrl : imageUrlsRetweet) {
											textRetweet = textRetweet + "\n"
													+ imageUrl + "/";
										}
									}
									timelineInfo
											.setRetweetedStatus(textRetweet);
									// Parse nick
									userInfo.setRetweetedScreenName(retweetObject
											.getString("nick"));
									// Parse uid
									userInfo.setRetweetUserId(retweetObject
											.has("uid") ? retweetObject
											.getString("uid") : "");
									if (userInfo.getRetweetUserId().equals("")) {
										userInfo.setRetweetUserId(String
												.valueOf(userInfo
														.getRetweetedScreenName()
														.hashCode()));
									}

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						timelineInfo.setUserInfo(userInfo);

						timelineInfoList.add(timelineInfo);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Return
		return timelineInfoList;
	}

	private static String clearHtmlTag(String html) {
		if (html == null || html.trim().equals("")) {
			return "";
		}
		Pattern pattern = Pattern.compile("<.+?>");
		Matcher matcher = pattern.matcher(html);
		return matcher.replaceAll("");
	}

	public static ArrayList<TimeLineInfo> parseLBSTimeline(String message) {
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
		JSONObject userResult;
		try {
			userResult = new JSONObject(message);
			String ret = userResult.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (userResult.has("data")) {

				// Parse data
				JSONObject dataJsonObject = userResult.getJSONObject("data");

				if (dataJsonObject.has("info")) {

					// Parse info
					JSONArray infoJsonArray = dataJsonObject
							.getJSONArray("info");
					JSONObject subInfoJsonObject;
					TimeLineInfo timelineInfo;
					UserInfo userInfo;
					for (int i = 0; i < infoJsonArray.length(); i++) {

						subInfoJsonObject = infoJsonArray.getJSONObject(i);
						timelineInfo = new TimeLineInfo();
						userInfo = new UserInfo();

						// Parse timestamp
						String time = subInfoJsonObject.getString("timestamp");
						timelineInfo.setTimeStamp(time);
						Date date = new Date(Long.valueOf(time) * 1000L);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						timelineInfo.setTime(sdf.format(date));

						// Parse id
						timelineInfo.setMessageId(subInfoJsonObject
								.getString("id"));
						String text = clearHtmlTag(subInfoJsonObject
								.getString("text"));
						if (subInfoJsonObject.has("location")) {
							timelineInfo.setLocation(" ["
									+ subInfoJsonObject.getString("location")
									+ "]");
						}

						if (subInfoJsonObject.has("count")) {
							timelineInfo.setRetweetCount(subInfoJsonObject
									.getString("count"));
						} else {
							timelineInfo.setRetweetCount("");
						}
						if (subInfoJsonObject.has("mcount")) {
							timelineInfo.setCommentCount(subInfoJsonObject
									.getString("mcount"));
						} else {
							timelineInfo.setCommentCount("");
						}

						// first：.isNull()方法 OK
						if (!subInfoJsonObject.isNull("image")) {
							// Parse image
							ArrayList<String> imageUrls = new ArrayList<String>();
							try {
								JSONArray imageUrlsJsonArray = subInfoJsonObject
										.getJSONArray("image");
								int lenght = imageUrlsJsonArray.length();
								if (lenght != 0) {
									for (int j = 0; j < imageUrlsJsonArray
											.length(); j++) {
										String imageUrl = imageUrlsJsonArray
												.getString(j).replace("app",
														"t3")
												+ "/160";
										imageUrls.add(imageUrl);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							for (String imageUrl : imageUrls) {
								text = text + "\n" + imageUrl;
							}
						}
						timelineInfo.setStatus(text);

						// Parse uid
						userInfo.setUid(subInfoJsonObject.has("uid") ? subInfoJsonObject
								.getString("uid") : "");

						// Parse nick
						userInfo.setScreenName(subInfoJsonObject
								.getString("nick"));
						userInfo.setUserName(subInfoJsonObject
								.getString("name"));
						userInfo.setUserImageURL("".equals(subInfoJsonObject
								.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
								: subInfoJsonObject.getString("head").replace(
										"app", "t2")
										+ "/50");

						// Parse isvip
						userInfo.setVerified(subInfoJsonObject.getString(
								"isvip").equals("0") ? "false" : "true");

						if (userInfo.getUid().equals("")) {
							userInfo.setUid(String.valueOf(userInfo
									.getScreenName().hashCode()));
						}

						timelineInfo.setUserInfo(userInfo);

						timelineInfoList.add(timelineInfo);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Return
		return timelineInfoList;

	}

	public static ArrayList<UserInfo> parseLBSPeople(String msg) {
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONObject statuses = new JSONObject(msg);

			// ret
			String ret = statuses.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (statuses.has("data")) {
				// data
				JSONObject userData = statuses.getJSONObject("data");
				// data_info
				JSONArray info;

				JSONObject status = null;

				UserInfo userInfo = null;

				if (userData.has("info")) {
					info = userData.getJSONArray("info");

					// Get Status, User

					for (int i = 0; i < info.length(); i++) {

						status = (JSONObject) info.get(i);

						// User
						userInfo = new UserInfo();

						if (userData.has("pageinfo")) {
							userInfo.setPageInfo(userData.getString("pageinfo"));
						}
						userInfo.setUid(status.getString("openid"));

						userInfo.setUserImageURL("".equals(status
								.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
								: status.getString("head") + "/50");
						userInfo.setScreenName(status.getString("nick"));
						userInfo.setUserName(status.getString("name"));

						// Add To Result
						userInfoList.add(userInfo);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.gc();
		// Return
		return userInfoList;
	}

	public static ArrayList<LocationInfo> parseLocationList(String msg) {

		// Prepare Result
		ArrayList<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
		if (msg == null) {
			return locationInfoList;
		}
		try {
			// Statuses
			JSONObject statusObject = new JSONObject(msg);
			JSONObject data = statusObject.getJSONObject("detail");
			JSONArray poilist;
			if (data.has("poilist")) {
				poilist = data.getJSONArray("poilist");
				JSONObject subInfo;
				LocationInfo locationInfo;
				for (int i = 0; i < poilist.length(); i++) {
					subInfo = poilist.getJSONObject(i);
					locationInfo = new LocationInfo();
					locationInfo.setLocationName(subInfo.getString("name"));
					locationInfo.setLocationAddress(subInfo.getString("addr"));
					locationInfo.setLocationCategory(subInfo
							.getString("catalog"));
					locationInfoList.add(locationInfo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return locationInfoList;
	}

	public static ArrayList<TimeLineInfo> parseMOODTimeline(String message) {
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
		JSONObject userResult;
		try {
			userResult = new JSONObject(message);
			String ret = userResult.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (userResult.has("data")) {

				// Parse data
				JSONObject dataJsonObject = userResult.getJSONObject("data");
				if (dataJsonObject.has("num")
						&& dataJsonObject.getString("num").equals("0")) {
					String num = dataJsonObject.getString("num");
					TimeLineInfo timelineInfo = new TimeLineInfo();
					timelineInfo.setmoodNum(num);
					timelineInfoList.add(timelineInfo);
				}
				if (dataJsonObject.has("info")) {

					// Parse info
					JSONArray infoJsonArray = dataJsonObject
							.getJSONArray("info");
					JSONObject subInfoJsonObject;
					TimeLineInfo timelineInfo;
					UserInfo userInfo;
					for (int i = 0; i < infoJsonArray.length(); i++) {

						subInfoJsonObject = infoJsonArray.getJSONObject(i);
						timelineInfo = new TimeLineInfo();
						userInfo = new UserInfo();
						timelineInfo.setmoodNum("");
						// Parse timestamp
						if (subInfoJsonObject.has("timestamp")) {
							String time = subInfoJsonObject
									.getString("timestamp");
							timelineInfo.setTimeStamp(time);
							Date date = new Date(Long.valueOf(time) * 1000L);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							timelineInfo.setTime(sdf.format(date));
						}

						// timelineInfo.setEmotionurl(subInfoJsonObject
						// .getString("emotionurl"));

						// Parse id
						timelineInfo.setMessageId(subInfoJsonObject
								.getString("id"));
						String text = clearHtmlTag(subInfoJsonObject
								.getString("text"));
						String emotionurl = subInfoJsonObject.has("emotionurl") ? subInfoJsonObject
								.getString("emotionurl") : "";

						if (subInfoJsonObject.has("count")) {
							timelineInfo.setRetweetCount(subInfoJsonObject
									.getString("count"));
						} else {
							timelineInfo.setRetweetCount("");
						}
						if (subInfoJsonObject.has("mcount")) {
							timelineInfo.setCommentCount(subInfoJsonObject
									.getString("mcount"));
						} else {
							timelineInfo.setCommentCount("");
						}

						text = text + "\n" + emotionurl + "/";

						timelineInfo.setStatus(text);

						// Parse uid
						userInfo.setUid(subInfoJsonObject.has("uid") ? subInfoJsonObject
								.getString("uid") : "");

						// Parse nick
						userInfo.setScreenName(subInfoJsonObject.has("nick") ? subInfoJsonObject
								.getString("nick") : "");
						userInfo.setUserName(subInfoJsonObject.has("name") ? subInfoJsonObject
								.getString("name") : "");
						if (subInfoJsonObject.has("head")) {
							userInfo.setUserImageURL(""
									.equals(subInfoJsonObject.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
									: subInfoJsonObject.getString("head")
											+ "/50");
						}

						// Parse isvip
						if (subInfoJsonObject.has("isvip")) {
							userInfo.setVerified(subInfoJsonObject.getString(
									"isvip").equals("0") ? "false" : "true");
						}

						if (userInfo.getUid().equals("")) {
							userInfo.setUid(String.valueOf(userInfo
									.getScreenName().hashCode()));
						}

						timelineInfo.setUserInfo(userInfo);

						timelineInfoList.add(timelineInfo);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Return
		return timelineInfoList;

	}

	public static ArrayList<UserInfo> parseFamousList(String msg) {
		// Prepare Result
		ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if (msg == null) {
			return userInfoList;
		}

		try {

			// Statuses
			JSONObject statuses = new JSONObject(msg);

			// ret
			String ret = statuses.getString("ret");
			if (!ret.equals("0")) {
				return null;
			}

			if (statuses.has("data")) {
				// data
				JSONObject userData = statuses.getJSONObject("data");
				// data_info
				JSONArray info;

				if (userData.has("info")) {
					info = userData.getJSONArray("info");

					// Get Status, User
					JSONObject status = null;
					UserInfo userInfo = null;
					for (int i = 0; i < info.length(); i++) {

						status = (JSONObject) info.get(i);

						// User
						userInfo = new UserInfo();

						userInfo.setUid(status.has("uid") ? status
								.getString("uid") : "");

						userInfo.setUserImageURL("".equals(status
								.getString("head")) ? "http://mat1.gtimg.com/www/mb/images/head_50.jpg"
								: status.getString("head") + "/50");

						userInfo.setDescription(status.has("brief") ? status
								.getString("brief") : "");
						userInfo.setScreenName(status.getString("nick"));

						userInfo.setUserName(status.getString("account"));

						// -----------------------------------------------------------
						if (userInfo.getUid().equals("")) {
							userInfo.setUid(String.valueOf(userInfo
									.getScreenName().hashCode()));
						}
						// -----------------------------------------------------------

						// Add To Result
						userInfoList.add(userInfo);

					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.gc();

		// Return
		return userInfoList;
	}

	@SuppressWarnings("null")
	public static ArrayList<TimeLineInfo> parseRegionCodeFromFile(
			Context applicationContext) {
		// Prepare Data For Result
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

		try {

			// Get File Name
			String fileName = "region_name_code.txt";

			// Read Tips File And Get Content
			InputStream in = applicationContext.getResources().getAssets()
					.open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			String message = EncodingUtils.getString(buffer, "UTF-8");
			if (message == null || message.equals("[]")) {
				return timelineInfoList;
			}
			JSONArray countries = new JSONArray(message);
			for (int i = 0; i < countries.length(); i++) {
				JSONObject country = countries.getJSONObject(i);
				// locationInfo.setLocationCountryName(country.getString("name"));
				// locationInfo.setLocationCountryCode(country.getString("code"));
				TimeLineInfo timelineInfo = null;
				ArrayList<LocationInfo> locationInfoList = null;
				if (!country.isNull("state")) {
					JSONArray states = country.getJSONArray("state");
					for (int j = 0; j < states.length(); j++) {
						JSONObject state = states.getJSONObject(j);
						timelineInfo = new TimeLineInfo();
						locationInfoList = new ArrayList<LocationInfo>();
						timelineInfo.setStatus(state.getString("name"));
						timelineInfo.setStatusId(state.getString("code"));
						if (!state.isNull("city")) {
							JSONArray cities = state.getJSONArray("city");
							for (int k = 0; k < cities.length(); k++) {
								JSONObject city = cities.getJSONObject(k);
								LocationInfo locationInfo = new LocationInfo();
								locationInfo.setLocationCityName(city
										.getString("name"));
								locationInfo.setLocationCityCode(city
										.getString("code"));
								locationInfoList.add(locationInfo);
							}
						}
						timelineInfo.setLocationInfoList(locationInfoList);
						timelineInfoList.add(timelineInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return timelineInfoList;
	}

}
