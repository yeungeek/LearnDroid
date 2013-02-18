package com.anhuioss.crowdroid.service.sina;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import android.content.Context;
import android.util.Log;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.TrendsDialog;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

public class SinaCommHandler extends HttpCommunicator {

	private static final String API_KEY = "4097074858";

	public static final String CONSUMER_KEY_SINA = "4097074858";

	public static final String CONSUMER_SECRET_SINA = "e6c623e6d434130245901e9a859c200a";

	public static String userName;

	public static String password;

	private static String accessToken;

	private static Context context;

	private static String mService = IGeneral.SERVICE_NAME_SINA;

	public static Map trendParameterMap;

	public static void setTrendParameter(Map map) {
		SinaCommHandler.trendParameterMap = map;
	}

	public static void setAppContext(Context ctx) {
		context = ctx;
	}

	public static void setAccessToken(String accessToken) {
		SinaCommHandler.accessToken = accessToken;
	}

	public static void setAccount(String userName, String password) {
		SinaCommHandler.userName = userName;
		SinaCommHandler.password = password;
	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getNewToken(Map map) {

		synchronized ("get new token") {

			// Prepare Result
			CommResult result = new CommResult();
			result.setResponseCode(String.valueOf(200));
			String grant_type = (String) map.get("grant_type");
			String code = (String) map.get("code");
			String redirect_uri = "https://api.weibo.com/oauth2/default.html";

			String url = "https://api.weibo.com/oauth2/access_token";

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("grant_type", grant_type);
			params.put("code", code);
			params.put("client_id", CONSUMER_KEY_SINA);
			params.put("client_secret", CONSUMER_SECRET_SINA);
			params.put("redirect_uri", redirect_uri);

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
		String url = "https://api.weibo.com/2/statuses/home_timeline.json?"
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
		String url = "https://api.weibo.com/2/statuses/mentions.json?access_token="
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
		String url = "http://api.t.sina.com.cn/direct_messages/sent.json?source="
				+ API_KEY + "&count=20";

		// Prepare Parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}

		// HTTP Communication
		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static synchronized CommResult getDirectMessageReceived(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/direct_messages.json?source="
				+ API_KEY + "&count=20";

		// Prepare parameters
		Object[] keys = map.keySet().toArray();

		for (int i = 0; i < map.size(); i++) {
			url = String.format(url + "&%s=%s", keys[i], map.get(keys[i]));
		}

		// HTTP Communication
		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult getMyTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/statuses/user_timeline.json?access_token="
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

	@SuppressWarnings("unchecked")
	public static CommResult updateStatus(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/statuses/update.json";

		String status = (String) map.get("status");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("status", status);

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

		String url = "https://api.weibo.com/2/users/show.json?"
				+ "access_token=" + accessToke + "&uid=" + uid;
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
		String url = "https://api.weibo.com/2/friendships/followers.json";

		// url = String.format(url +
		// "?access_token=%s&cursor=%s&uid=%s&count=24",
		// accessToken, map.get("cursor"), map.get("uid"));
		url = String.format(url + "?access_token=%s&page=%s&uid=%s&count=24",
				accessToken, map.get("page"), map.get("uid"));

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
		String url = "https://api.weibo.com/2/friendships/friends.json";

		url = String.format(url + "?access_token=%s&cursor=%s&uid=%s",
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
						.format("https://api.weibo.com/2/users/show.json?access_token=%s&screen_name=%s",
								accessToken,
								URLEncoder.encode(screenName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			url = String
					.format("https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s",
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
					.format("http://api.t.sina.com.cn/users/search.json?source=%s&q=%s&page=%s&count=20",
							API_KEY, URLEncoder.encode(
									(String) map.get("query"), "UTF-8"), map
									.get("page"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult searchInfo(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String url = "";

		try {
			url = String
					.format("http://api.t.sina.com.cn/statuses/search.json?source=%s&q=%s&page=%s&count=20",
							API_KEY, URLEncoder.encode(
									(String) map.get("search"), "UTF-8"), map
									.get("page"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		result = httpGetOauth(mService, url, userName, password);

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
				.format("https://api.weibo.com/2/statuses/user_timeline.json?access_token=%s&uid=%s&page=%s&count=20",
						accessToken, uid, map.get("page"));

		if (uid == null) {
			try {
				url = String
						.format("https://api.weibo.com/2/statuses/user_timeline.json?access_token=%s&screen_name=%s&page=%s&count=20",
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
		try {
			parameters.put("text",
					URLEncoder.encode((String) map.get("message"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = String
				.format("http://api.t.sina.com.cn/direct_messages/new.json?source=%s&id=%s",
						API_KEY, map.get("uid"));

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;
	}

	public static CommResult getFavoriteList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String url = String
				.format("https://api.weibo.com/2/favorites.json?access_token=%s&page=%s",
						accessToken, map.get("page"));

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
					.format("https://api.weibo.com/2/favorites/create.json?access_token=%s&id=%s",
							accessToken, map.get("message_id"));
		} else {
			url = String
					.format("https://api.weibo.com/2/favorites/destroy.json?access_token=%s&id=%s",
							accessToken, map.get("message_id"));
		}

		result = HttpPost(context, url, new HashMap<String, String>());

		return result;

	}

	public static CommResult showRelation(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sina.com.cn/friendships/exists.json?source=%s&user_a=%s&user_b=%s",
						API_KEY, map.get("uid_a"), map.get("uid"));

		result = httpGetOauth(mService, url, userName, password);

		return result;

	}

	public static CommResult showRelationNew(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.weibo.com/2/friendships/show.json?access_token=%s&source_id=%s&target_id=%s",
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
					.format("https://api.weibo.com/2/friendships/create.json?"
							+ "uid=" + map.get("id"));
		} else {
			url = String
					.format("https://api.weibo.com/2/friendships/destroy.json?"
							+ "uid=" + map.get("id"));
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
					.format("https://api.weibo.com/2/statuses/repost.json?id=%s&is_comment=%s&status=%s",
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

		CommResult result = new CommResult();
		String filePath = (String) map.get("filePath");
		String message = (String) map.get("status");

		String url = String
				.format("https://upload.api.weibo.com/2/statuses/upload.json");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		try {
			params.put("status", URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("filePath", filePath);

		try {
			result = HttpPostImage(context, url, params);
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
				.format("https://api.weibo.com/2/statuses/destroy.json?"
						+ "id=" + map.get("message_id"));

		result = HttpPost(context, url, parameters);

		return result;

	}

	public static CommResult getCommentsById(Map map) {

		CommResult result = new CommResult();

		String url = "https://api.weibo.com/2/comments/show.json";

		url = String.format(url + "?access_token=%s&id=%s&page=%s&count=20",
				accessToken, map.get("message_id"), map.get("page"));

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
		parameters.put("comment", (String) map.get("comment"));
		// URLEncoder.encode((String) map.get("comment"), "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		String url = "https://api.weibo.com/2/comments/create.json";

		url = String.format(url + "?access_token=%s&id=%s", accessToken,
				map.get("message_id"));

		result = HttpPost(context, url, parameters);
		return result;
	}

	public static CommResult getTrendList(Map map) {

		CommResult result = new CommResult();

		String url = "https://api.weibo.com/2/trends.json";

		url = String.format(url + "?access_token=%s&uid=%s&count=40",
				accessToken, map.get("uid"));

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
		String url = "https://api.weibo.com/2/search/topics.json";

		// try {
		url = String.format(url + "?source=%s&access_token=%s&q=%s&&count=20",
				API_KEY, accessToken,
				(String) trendParameterMap.get("trend_name"));
		// URLEncoder.encode(
		// (String) trendParameterMap.get("trend_name"),
		// "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult getTrendsByType(Map map) {

		CommResult result = new CommResult();
		String url = String
				.format("https://api.weibo.com/2/trends/%s.json?access_token=%s&base_app=0",
						(String) map.get("type"), accessToken);
		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	public static CommResult getEmotions(Map map) {

		CommResult result = new CommResult();

		String url = "https://api.weibo.com/2/emotions.json";

		url = String.format(url + "?access_token=%s", accessToken);

		try {
			result = HttpGet(context, url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult getUnreadMessage(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://rm.api.weibo.com/2/remind/unread_count.json?access_token=%s",
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
				.format("https://api.weibo.com/2/suggestions/users/hot.json?access_token=%s",
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
				.format("https://api.weibo.com/2/suggestions/users/may_interested.json?access_token=%s&with_reason=1",
						accessToken);

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

		String url = String
				.format("https://rm.api.weibo.com/2/remind/set_count.json?access_token=%s",
						accessToken);

		result = HttpPost(context, url, parameter);

		return result;
	}

	public static CommResult getCommentTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.weibo.com/2/comments/to_me.json?access_token=%s&count=20&page=%s",
						accessToken, map.get("page"));

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
				.format("https://api.weibo.com/2/comments/reply.json?access_token=%s&cid=%s&id=%s",
						accessToken, map.get("cid"), map.get("id"));

		result = HttpPost(context, url, parmeters);

		return result;
	}

	public static CommResult getTimelineById(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.weibo.com/2/statuses/show.json?access_token="
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
				.format("https://api.weibo.com/2/statuses/public_timeline.json?access_token=%s&base_app=0",
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
		String url = "http://api.t.sina.com.cn/account/update_profile.json";

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
				.format("http://api.t.sina.com.cn/location/geocode/geo_to_address.json?source=4097074858&coordinate=%s",
						String.valueOf(map.get("longitude")) + ","
								+ String.valueOf(map.get("latitude")));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult getLBSLocationList(Map map) {

		CommResult result = new CommResult();

		String queryLocation = "";

		String url0 = String.format(
				"https://api.weibo.com/2/location/geo/geo_to_address.json?access_token="
						+ accessToken + "&coordinate=%s",
				String.valueOf(map.get("longitude")) + ","
						+ String.valueOf(map.get("latitude")));

		try {
			result = HttpGet(context, url0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (result != null) {
			JSONObject location;
			try {
				JSONObject object = new JSONObject(result.getMessage());
				JSONArray jArray = object.getJSONArray("geos");
				for (int i = 0; i < jArray.length(); i++) {
					location = (JSONObject) jArray.get(i);
					// if (location.has("address")) {
					// JSONObject jObject = (JSONObject) location
					// .getJSONObject("address");
					queryLocation = location.getString("city_name");
					// }
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			String url;
			if (!("").equals(queryLocation)) {
				url = String.format(
						"https://api.weibo.com/2/location/pois/search/by_geo.json?access_token=%s"
								+ "&coordinate=%s&q=%s&count=20",
						accessToken,
						String.valueOf(map.get("longitude")) + ","
								+ String.valueOf(map.get("latitude")),
						URLEncoder.encode(String.valueOf(queryLocation
								.substring(0, queryLocation.length() - 1)),
								"UTF-8"));
				// url = String.format(
				// "https://api.weibo.com/2/location/pois/search/by_location.json?access_token=%s"
				// + "&q=%s&count=20", accessToken, URLEncoder
				// .encode(String.valueOf(queryLocation.substring(
				// 0, queryLocation.length() - 1)),
				// "UTF-8"));
				result = HttpGet(context, url);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static CommResult UpdateLBSMessage(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/statuses/update.json";

		url = String.format(url + "?access_token=%s", accessToken);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("status", String.valueOf(map.get("locationAddress")));
		params.put("long", String.valueOf(map.get("long")));
		params.put("lat", String.valueOf(map.get("lat")));

		result = HttpPost(context, url, params);

		// Return Result
		return result;
	}

	public static synchronized CommResult getLBSTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/friends_timeline.json?access_token="
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
		if (result != null && result.getMessage() != null
				&& result.getMessage().contains("error")) {
			try {
				JSONObject error = new JSONObject(result.getMessage());
				if (error.getString("error_code").equals("10025")) {
					result.setResponseCode("10025");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

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
		String url = "https://api.weibo.com/2/tags/suggestions.json?access_token="
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

	public static CommResult getnearyPOI(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/nearby/pois.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getlbsuserpoi(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/users/checkins.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getlbssearchpoi(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/location/pois/search/by_location.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getlbssearchpoibygeo(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/location/pois/search/by_geo.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getheretimeline(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/poi_timeline.json?access_token="
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

	public static CommResult getneartimeline(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/nearby_timeline.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult gethereuser(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/pois/users.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getnearuser(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/nearby/users.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getherecomment(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/pois/tips.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getherephoto(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/pois/photos.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getnearphoto(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/nearby/photos.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult updateLBSStatus(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/pois/add_checkin.json";

		String status = (String) map.get("status");
		String poiid = (String) map.get("poiid");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);

		params.put("status", status);

		params.put("poiid", poiid);

		try {
			result = HttpPost(context, url, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static CommResult uploadLBSImage(Map map) {

		CommResult result = new CommResult();
		String filePath = (String) map.get("filePath");
		String message = "";
		message = (String) map.get("status").toString();

		String poiid = (String) map.get("poiid");

		String url = String
				.format("https://api.weibo.com/2/place/pois/add_photo.json");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		try {
			params.put("status", URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("filePath", filePath);
		params.put("poiid", poiid);

		try {
			result = HttpPostImage(context, url, params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult sendcomment(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/place/pois/add_tip.json";

		String status = (String) map.get("status");
		String poiid = (String) map.get("poiid");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);

		params.put("status", status);

		params.put("poiid", poiid);

		try {
			result = HttpPost(context, url, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Return Result
		return result;
	}

	public static CommResult getwayofcar(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/location/line/drive_route.json?access_token="
				+ accessToken + "&count=10";

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

	public static CommResult getwayofbus(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "https://api.weibo.com/2/location/line/bus_route.json?access_token="
				+ accessToken + "&count=10";

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

}
