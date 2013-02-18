package com.anhuioss.crowdroid.service.wangyi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.data.StatusData;

import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

public class WangyiCommHandler extends HttpCommunicator {

	private static final String API_KEY = "T8F3ZYaWOlasC33b";

	public static final String CONSUMER_KEY_WANGYI = "T8F3ZYaWOlasC33b";

	public static final String CONSUMER_SECRET_WANGYI = "aLVtMul2WZPQs3ajfyBKvEQgtPBFVqc0";

	public static String userName;

	public static String password;

	private static String accessToken;

	private static Context context;

	private static String mService = IGeneral.SERVICE_NAME_WANGYI;

	public static Map trendParameterMap;

	public static void setTrendParameter(Map map) {
		WangyiCommHandler.trendParameterMap = map;
	}

	public static void setAppContext(Context ctx) {
		context = ctx;
	}

	public static void setAccessToken(String accessToken) {
		WangyiCommHandler.accessToken = accessToken;
	}

	public static void setAccount(String userName, String password) {
		WangyiCommHandler.userName = userName;
		WangyiCommHandler.password = password;
	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getNewToken(Map map) {

		synchronized ("get new token") {

			// Prepare Result
			CommResult result = new CommResult();
			result.setResponseCode(String.valueOf(200));
			String grant_type = (String) map.get("grant_type");
			String code = (String) map.get("code");

			String url = "https://api.t.163.com/oauth2/access_token";

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("grant_type", grant_type);
			// params.put("code", code);
			params.put("client_id", CONSUMER_KEY_WANGYI);
			params.put("client_secret", CONSUMER_SECRET_WANGYI);
			// params.put("redirect_uri",
			// "http://www.anhuioss.com/cn/crowdroid/index.html");
			params.put("refresh_token", map.get("refresh_token").toString());
			try {
				result = HttpPost(context, url, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

	}

	public static synchronized CommResult getHomeTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/statuses/home_timeline.json?"
				+ "access_token=" + accessToken + "&count=20";

		// Prepare Parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}

		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;

	}

	public static synchronized CommResult getAtMessageTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/statuses/mentions.json?access_token="
				+ accessToken + "&count=20";

		// Prepare Parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}

		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult getDirectMessageSent(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/direct_messages/sent.json?access_token="
				+ accessToken + "&count=20";

		// Prepare Parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}

		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static synchronized CommResult getDirectMessageReceived(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/direct_messages.json?access_token="
				+ accessToken + "&count=20";

		// Prepare parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}

		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult getMyTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();
		String uid = (String) map.get("uid");

		// URL
		String url = "https://api.t.163.com/statuses/user_timeline.json?user_id="
				+ uid + "&access_token=" + accessToken + "&count=20";

		// Prepare parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}

		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	@SuppressWarnings("unchecked")
	public static CommResult updateStatus(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/statuses/update.json";

		String status = (String) map.get("status");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("status", status);
		if (map.get("long") != null && map.get("lat") != null
				&& !map.get("long").equals("") && !map.get("lat").equals("")) {
			params.put("long", (String) map.get("long"));
			params.put("lat", (String) map.get("lat"));
		}

		try {
			result = HttpPost(context, url, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static CommResult verifyUser(Map map) {

		CommResult result = new CommResult();
		String accessToke = (String) map.get("access_token");
		String uid = (String) map.get("uid");

		String url = "https://api.t.163.com/users/show.json?" + "access_token="
				+ accessToke + "&id=" + uid;
		url = String.valueOf(url);

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult getFollowersList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/statuses/followers.json";

		url = String.format(url
				+ "?access_token=%s&cursor=%s&user_id=%s&count=20",
				accessToken, map.get("cursor"), map.get("uid"));

		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static CommResult getFollowList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/statuses/friends.json";

		url = String.format(url
				+ "?access_token=%s&cursor=%s&user_id=%s&count=20",
				accessToken, map.get("cursor"), map.get("uid"));

		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static CommResult getUserInfo(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String screenName = (String) map.get("screen_name");
		String uid = (String) map.get("uid");

		if (screenName.equals(userName)) {
			CrowdroidApplication crowdroidApplication = (CrowdroidApplication) context
					.getApplicationContext();
			StatusData status = crowdroidApplication.getStatusData();
			uid = status.getCurrentUid();
		}
		String url = null;
		if (uid == null || uid.equals("")) {
			try {
				url = String
						.format("https://api.t.163.com/users/show.json?access_token=%s&screen_name=%s",
								accessToken,
								URLEncoder.encode(screenName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			url = String
					.format("https://api.t.163.com/users/show.json?access_token=%s&id=%s",
							accessToken, uid);
		}

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult getFindPeopleInfo(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String url = "";

		try {
			url = String
					.format("https://api.t.163.com/users/search.json?access_token=%s&q=%s&page=%s&count=20",
							accessToken, URLEncoder.encode(
									(String) map.get("query"), "UTF-8"), map
									.get("page"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult searchInfo(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String url = "";

		try {
			url = String
					.format("https://api.t.163.com/statuses/search.json?access_token=%s&q=%s&page=%s&count=20",
							accessToken, URLEncoder.encode(
									(String) map.get("search"), "UTF-8"), map
									.get("page"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult getUserStatusList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String screenName = (String) map.get("screen_name");
		String uid = (String) map.get("uid");

		if (screenName.equals(userName)) {
			CrowdroidApplication crowdroidApplication = (CrowdroidApplication) context
					.getApplicationContext();
			StatusData status = crowdroidApplication.getStatusData();
			uid = status.getCurrentUid();
		}

		String url = String
				.format("https://api.t.163.com/statuses/user_timeline.json?access_token=%s&user_id=%s&page=%s&count=20",
						accessToken, uid, map.get("page"));

		if (uid == null) {
			try {
				url = String
						.format("https://api.t.163.com/statuses/user_timeline.json?access_token=%s&name=%s&page=%s&count=20",
								accessToken,
								URLEncoder.encode(screenName, "UTF-8"),
								map.get("page"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult directMessage(Map map) {

		CommResult result = new CommResult();
		HashMap<String, String> parameters = new HashMap<String, String>();
		// parameters.put("id", (String) map.get("send_to"));
		// try {
		// parameters.put("text",
		// URLEncoder.encode((String) map.get("message"), "UTF-8"));
		//
		parameters.put("text", map.get("message").toString());
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		String url = String
				.format("https://api.t.163.com/direct_messages/new.json?user=%s&access_token=%s",
						map.get("name"), accessToken);

		result = HttpPost(context, url, parameters);
		JSONObject js;
		try {
			js = new JSONObject(result.getMessage());
			if (js.has("errors")) {
				JSONArray ja = js.getJSONArray("errors");
				JSONObject msg = ja.getJSONObject(0);
				if (msg.has("code")) {
					if (msg.getString("code").equals("401")) {
						result.setResponseCode("401");
					} else if (msg.getString("code").equals("403")) {
						result.setResponseCode("403");
					} else if (msg.getString("code").equals("404")) {
						result.setResponseCode("404");
					}

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult getFavoriteList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/favorites/%s.json?access_token=%s&since_id=%s&count=20",
						map.get("uid"), accessToken, map.get("since_id"));

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult setFavorite(Map map) {

		CommResult result = new CommResult();

		String url = "";

		if (map.get("type").equals("create")) {
			url = String
					.format("https://api.t.163.com/favorites/create/%s.json?access_token=%s",
							map.get("message_id"), accessToken);
		} else {
			url = String
					.format("https://api.t.163.com/favorites/destroy/%s.json?access_token=%s",
							map.get("message_id"), accessToken);
		}

		result = HttpPost(context, url, new HashMap<String, String>());

		return result;

	}

	// public static CommResult showRelation(Map map) {
	//
	// CommResult result = new CommResult();
	//
	// String url = String
	// .format("http://api.t.163.com.cn/friendships/show.json?source=%s&user_a=%s&user_b=%s",
	// API_KEY, map.get("uid_a"), map.get("uid"));
	//
	// result = httpGetOauth(mService, url, userName, password);
	//
	// return result;
	//
	// }

	public static CommResult showRelationNew(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/friendships/show.json?access_token=%s&source_id=%s&target_id=%s",
						accessToken, map.get("source_id"), map.get("target_id"));

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult setFollow(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("access_token", accessToken);

		String url = "";

		if (map.get("type").equals("create")) {
			url = String
					.format("https://api.t.163.com/friendships/create.json?"
							+ "user_id=" + map.get("id"));
		} else {
			url = String
					.format("https://api.t.163.com/friendships/destroy.json?"
							+ "user_id=" + map.get("id"));
		}

		result = HttpPost(context, url, parameters);

		return result;

	}

	public static CommResult retweet(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("access_token", accessToken);
		// try {
		// parameters.put("status", URLEncoder.encode((String)
		// map.get("comment"), "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		String url = "";
		try {
			url = String
					.format("https://api.t.163.com/statuses/retweet/%s.json?is_comment=%s&status=%s",
							map.get("message_id"), map.get("is_comment"),
							URLEncoder.encode((String) map.get("comment"),
									"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		result = HttpPost(context, url, parameters);

		return result;

	}

	@SuppressWarnings({ "unchecked" })
	public static CommResult uploadImage(Map map) {

		// CommResult result = new CommResult();
		// String filePath = (String) map.get("filePath");
		// String message = "";
		// message = (String) map.get("status").toString();
		//
		// String url = String
		// .format("https://upload.api.weibo.com/2/statuses/upload.json");
		//
		// HashMap<String, String> params = new HashMap<String, String>();
		// params.put("access_token", accessToken);
		// try {
		// params.put("status", URLEncoder.encode(message, "UTF-8"));
		// } catch (UnsupportedEncodingException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// params.put("filePath", filePath);
		//
		// try {
		// result = HttpPostImage(context, url, params);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// String
		// url="https://api.t.163.com/statuses/upload.json?access_token="+accessToken;
		CommResult result = new CommResult();
		String filePath = (String) map.get("filePath");
		String message = "";
		message = (String) map.get("status").toString();

		String url = String
				.format("https://api.t.163.com/statuses/upload.json");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("filePath", filePath);

		try {
			result = HttpPostImage1(context, url, params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult destroyStatus(Map map) {
		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("access_token", accessToken);

		String url = String
				.format("https://api.t.163.com/statuses/destroy/%s.json?access_token=%s",
						map.get("message_id"), accessToken);

		result = HttpPost(context, url, parameters);

		return result;

	}

	public static CommResult getCommentsById(Map map) {

		CommResult result = new CommResult();

		// String url = "https://api.t.163.com/statuses/comments/%s.json";

		String url = String
				.format("https://api.t.163.com/statuses/comments/%s.json?access_token=%s&page=%s&count=20",
						map.get("message_id"), accessToken, map.get("page"));

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult updateComments(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		// try {
		parameters.put("status",
		// URLEncoder.encode((String) map.get("comment"), "UTF-8"));
				(String) map.get("comment"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		String url = "https://api.t.163.com/statuses/reply.json";

		url = String.format(url + "?access_token=%s&id=%s", accessToken,
				map.get("message_id"));

		result = HttpPost(context, url, parameters);
		return result;
	}

	public static CommResult getTrendList(Map map) {

		CommResult result = new CommResult();

		String url = "https://api.t.163.com/trends/recommended.json";

		url = String.format(url + "?access_token=%s&count=40", accessToken);

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getTrendTimeline(Map map) {

		CommResult result = new CommResult();

		if ((Integer) map.get("page") != 1) {
			return result;
		}

		// String url = "http://api.t.sina.com.cn/trends/statuses.json";
		//
		// try {
		// url = String.format(url + "?source=%s&trend_name=%s", API_KEY,
		// URLEncoder.encode(
		// (String) trendParameterMap.get("trend_name"),
		// "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		String url = "https://api.t.163.com/search/topics.json";

		try {
			url = String.format(url
					+ "?source=%s&access_token=%s&q=%s&&count=20", API_KEY,
					accessToken, URLEncoder.encode(
							(String) trendParameterMap.get("trend_name"),
							"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult getTrendsByType(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"https://api.t.163.com/trends/%s.json?&base_app=0&source=%s",
				(String) map.get("type"), API_KEY);

		result = httpGetOauth(mService, url, userName, password);

		return result;

	}

	// public static CommResult getEmotions(Map map) {
	//
	// CommResult result = new CommResult();
	//
	// String url = "https://api.t.163.com/emotions.json";
	//
	// url = String.format(url + "?access_token=%s", accessToken);
	//
	// try {
	// result = HttpGet(context, url);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return result;
	//
	// }

	public static CommResult getUnreadMessage(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/reminds/message/latest.json?access_token=%s",
						accessToken);

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult getHotUsers(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/users/suggestions_i_followers.json?access_token=%s",
						accessToken);

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult getSuggestionUsers(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/users/suggestions.json?access_token=%s&page_no=%s&page_size=30",
						accessToken, map.get("page"));

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult clearUnreadMessage(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameter = new HashMap<String, String>();
		parameter.put("type", (String) map.get("type"));

		String url = String.format(
				"https://api.t.163.com/remind/set_count.json?access_token=%s",
				accessToken);

		result = HttpPost(context, url, parameter);

		return result;
	}

	public static CommResult getCommentTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/statuses/comments_to_me.json?access_token=%s&count=20&since_id=%s",
						accessToken, map.get("since_id"));

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult replyToComment(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parmeters = new HashMap<String, String>();
		try {
			parmeters.put("comment",
					URLEncoder.encode((String) map.get("comment"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String url = String
				.format("https://api.t.163.com/statuses/reply.json?access_token=%s&cid=%s&id=%s",
						accessToken, map.get("cid"), map.get("id"));

		result = HttpPost(context, url, parmeters);

		return result;
	}

	public static CommResult getTimelineById(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/statuses/show.json?access_token="
						+ accessToken + "&id=" + map.get("id"));

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static synchronized CommResult getPublicTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com/statuses/public_timeline.json?access_token=%s&base_app=0",
						accessToken);

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static CommResult updateProfile(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com.cn/account/update_profile.json";

		url = String.format(url + "?source=%s", API_KEY);

		String screenName = (String) map.get("name");
		String description = (String) map.get("description");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("name", screenName);
		// params.put("description", description);

		result = httpPostOauth(mService, url, params, userName, password);

		// String url =
		// String.format("http://api.t.sina.com.cn/account/update_profile.json?source=%s&name=%s&description=%s",
		// API_KEY,map.get("name"),map.get("description"));
		//
		// result = httpPostOauth(mService, url, new HashMap<String, String>() ,
		// userName, password);

		// Return Result
		return result;
	}

	public static CommResult getMyLocation(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.t.163.com.cn/location/geocode/geo_to_address.json?source=4097074858&coordinate=%s",
						String.valueOf(map.get("longitude")) + ","
								+ String.valueOf(map.get("latitude")));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult getLBSLocationList(Map map) {

		CommResult result = new CommResult();

		String queryLocation = "";

		String url0 = String
				.format("https://api.t.163.com/location/venues.json?access_token=%s&long=%s&lat=%s",
						accessToken, String.valueOf(map.get("longitude")),
						String.valueOf(map.get("latitude")));

		try {
			result = HttpGet(context, url0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// if (result != null) {
		// JSONObject location;
		// try {
		// JSONObject object = new JSONObject(result.getMessage());
		// JSONArray jArray = object.getJSONArray("geos");
		// for (int i = 0; i < jArray.length(); i++) {
		// location = (JSONObject) jArray.get(i);
		// // if (location.has("address")) {
		// // JSONObject jObject = (JSONObject) location
		// // .getJSONObject("address");
		// queryLocation = location.getString("city_name");
		// // }
		// }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// try {
		// String url;
		// if (!("").equals(queryLocation)) {
		// url = String.format(
		// "https://api.t.163.com/location/venues.json?access_token=%s"
		// + "&long=%s&lat=%s&q=%s&count=20",
		// accessToken,
		// String.valueOf(map.get("longitude")),
		// String.valueOf(map.get("latitude")),
		// URLEncoder.encode(String.valueOf(queryLocation
		// .substring(0, queryLocation.length() - 1)),
		// "UTF-8"));
		// // url = String.format(
		// //
		// "https://api.weibo.com/2/location/pois/search/by_location.json?access_token=%s"
		// // + "&q=%s&count=20", accessToken, URLEncoder
		// // .encode(String.valueOf(queryLocation.substring(
		// // 0, queryLocation.length() - 1)),
		// // "UTF-8"));
		// result = HttpGet(context, url);
		// }
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return result;
	}

	@SuppressWarnings("unchecked")
	public static CommResult UpdateLBSMessage(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/location/report.json";

		url = String.format(url + "?access_token=%s&lat=%s&long=%s",
				accessToken, String.valueOf(map.get("lat")),
				String.valueOf(map.get("long")));

		// HashMap<String, String> params = new HashMap<String, String>();
		// // params.put("status", String.valueOf(map.get("locationAddress")));
		// params.put("long", String.valueOf(map.get("long")));
		// params.put("lat", String.valueOf(map.get("lat")));

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static synchronized CommResult getLBSTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/statuses/location_timeline.json?access_token="
				+ accessToken + "&count=50";

		// Prepare Parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}
		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static synchronized CommResult updateTags(Map map) {
		// Prepare result
		CommResult result = new CommResult();
		// URL
		String url = "";
		HashMap<String, String> params = new HashMap<String, String>();
		if (map.containsKey("tagFlag")) {
			url = "https://api.weibo.com/2/tags/destroy.json";
			url = String.format(url + "?access_token=%s", accessToken);
			params.put("tag_id", map.get("tag_id").toString());
		} else {
			url = "https://api.weibo.com/2/tags/create.json";
			url = String.format(url + "?access_token=%s", accessToken);

			String tags = map.get("tags").toString();
			params.put("tags", tags);
		}

		result = HttpPost(context, url, params);

		// Return Result
		return result;
	}

	public static synchronized CommResult getUserTagsList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/tags.json?access_token="
				+ accessToken + "&count=20";

		// Prepare Parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}
		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static synchronized CommResult getSuggestionsTagsList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.t.163.com/trends/recommend_tags.json?access_token="
				+ accessToken + "&count=10";

		// Prepare Parameters
		Object[] keys = map.keySet().toArray();

		url = String.format(url);
		// HTTP Communication
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static CommResult getHotFollowRetweetTimeline(Map map) {
		CommResult result = new CommResult();

		String url = "https://api.t.163.com/statuses/topFollowRetweets.json?access_token="
				+ accessToken;

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult getHotRetweetTimeline(Map map) {
		CommResult result = new CommResult();

		String type = "oneHour";
		if (map.get("type") != null) {
			type = (String) map.get("type");
		}

		String url = "https://api.t.163.com/statuses/topRetweets.json?access_token="
				+ accessToken + "&type=" + type;

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return Result
		return result;
	}

	public static CommResult getLBSPeople(Map map) {
		CommResult result = new CommResult();

		String url = "https://api.t.163.com/location/search_neighbors.json?access_token="
				+ accessToken
				+ "&lat="
				+ String.valueOf(map.get("lat"))
				+ "&long=" + String.valueOf(map.get("long"));
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getRssTimeline(Map map) {
		CommResult result = new CommResult();
		String url = "https://api.t.163.com/statuses/rss_timeline.json?access_token="
				+ accessToken
				+ "&rss_id="
				+ (String) map.get("rss_id")
				+ "&since_id=" + (String) map.get("since_id");
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult Image(Map map) {
		CommResult result = new CommResult();

		String pic = (String) map.get("filePath");

		String url = "https://api.t.163.com/statuses/upload.json?access_token="
				+ accessToken + "&pic=" + pic;
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getColumnTimelineByUserID(Map map) {
		CommResult result = new CommResult();

		String url = "https://api.t.163.com/statuses/user_column_timeline.json?access_token="
				+ accessToken
				+ "&user_id="
				+ (String) map.get("user_id")
				+ "&since_id=" + (String) map.get("since_id");
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getColumnTimelineBycolumnId(Map map) {
		CommResult result = new CommResult();
		String url = "https://api.t.163.com/statuses/column_timeline/"
				+ (String) map.get("column_id") + ".json?access_token="
				+ accessToken + "&since_id=" + (String) map.get("since_id");
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getColumnsByUserId(Map map) {
		CommResult result = new CommResult();

		String url = "https://api.t.163.com/column/info.json?access_token="
				+ accessToken + "&user_id=" + (String) map.get("uid");
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}