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

	private static Context context;

	private static String mService = IGeneral.SERVICE_NAME_SINA;

	public static Map trendParameterMap;

	public static void setTrendParameter(Map map) {
		SinaCommHandler.trendParameterMap = map;
	}

	public static void setAppContext(Context ctx) {
		context = ctx;
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

			// Prepare Object From Map
			DefaultOAuthConsumer consumer = (DefaultOAuthConsumer) map
					.get("consumer");
			OAuthProvider provider = (OAuthProvider) map.get("provider");
			String pin = (String) map.get("pinCode");

			try {
				// HTTP Communication
				provider.setOAuth10a(true);
				provider.retrieveAccessToken(consumer, pin);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
				result.setResponseCode(String.valueOf(400));
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
				result.setResponseCode(String.valueOf(400));
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
				result.setResponseCode(String.valueOf(400));
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
				result.setResponseCode(String.valueOf(400));
			} catch (Exception e) {
				e.printStackTrace();
				result.setResponseCode(String.valueOf(400));
			} finally {
				// Prepare Message
				String message = consumer.getToken() + ";"
						+ consumer.getTokenSecret();
				result.setMessage(message);
				return result;
			}

		}

	}

	public static synchronized CommResult getHomeTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/statuses/friends_timeline.json?source="
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

	public static synchronized CommResult getAtMessageTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/statuses/mentions.json?source="
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
		String url = "http://api.t.sina.com.cn/statuses/user_timeline.json?source="
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

	@SuppressWarnings("unchecked")
	public static CommResult updateStatus(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/statuses/update.json";

		url = String.format(url + "?source=%s", API_KEY);

		String status = (String) map.get("status");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("status", status);

		String name = (String) map.get("user_name");
		String pw = (String) map.get("user_password");
		String service = (String) map.get("service");
		if (service == null) {
			result = httpPostOauth(mService, url, params, userName, password);
		} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
			result = httpPostOauth(mService, url, params, name, pw);
		}

		// Return Result
		return result;
	}

	public static CommResult verifyUser(Map map) {

		CommResult result = new CommResult();

		String url = "http://api.t.sina.com.cn/account/verify_credentials.json";

		url = String.format(url + "?source=%s", API_KEY);

		String name = (String) map.get("accessToken");
		String passwd = (String) map.get("tokenSecret");

		result = httpGetOauth(mService, url, name, passwd);

		return result;
	}

	public static CommResult getFollowersList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/statuses/followers.json";

		url = String.format(url + "?source=%s&cursor=%s&user_id=%s&count=20",
				API_KEY, map.get("page"), map.get("uid"));

		// HTTP Communication
		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult getFollowList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/statuses/friends.json";

		url = String.format(url + "?source=%s&cursor=%s&user_id=%s", API_KEY,
				map.get("cursor"), map.get("uid"));

		// HTTP Communication
		result = httpGetOauth(mService, url, userName, password);

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
						.format("http://api.t.sina.com.cn/users/show.json?source=%s&screen_name=%s",
								API_KEY, URLEncoder.encode(screenName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			url = String
					.format("http://api.t.sina.com.cn/users/show.json?source=%s&user_id=%s",
							API_KEY, uid);
		}

		result = httpGetOauth(mService, url, userName, password);

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
				.format("http://api.t.sina.com.cn/statuses/user_timeline.json?source=%s&user_id=%s&page=%s&count=20",
						API_KEY, uid, map.get("page"));

		if (uid == null) {
			try {
				url = String
						.format("http://api.t.sina.com.cn/statuses/user_timeline.json?source=%s&screen_name=%s&page=%s&count=20",
								API_KEY,
								URLEncoder.encode(screenName, "UTF-8"),
								map.get("page"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		result = httpGetOauth(mService, url, userName, password);

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

		String url = String.format(
				"http://api.t.sina.com.cn/favorites.json?source=%s&page=%s",
				API_KEY, map.get("page"));

		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult setFavorite(Map map) {

		CommResult result = new CommResult();

		String url = "";

		if (map.get("type").equals("create")) {
			url = String
					.format("http://api.t.sina.com.cn/favorites/create.json?source=%s&id=%s",
							API_KEY, map.get("message_id"));
		} else {
			url = String
					.format("http://api.t.sina.com.cn/favorites/destroy/:%s.json?source=%s",
							map.get("message_id"), API_KEY);
		}

		result = httpPostOauth(mService, url, new HashMap<String, String>(),
				userName, password);

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
				.format("http://api.t.sina.com.cn/friendships/show.json?source=%s&source_id=%s&target_id=%s",
						API_KEY, map.get("source_id"), map.get("target_id"));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult setFollow(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("source", API_KEY);

		String url = "";

		if (map.get("type").equals("create")) {
			url = String.format(
					"http://api.t.sina.com.cn/friendships/create/:%s.json",
					map.get("id"));
		} else {
			url = String.format(
					"http://api.t.sina.com.cn/friendships/destroy/:%s.json",
					map.get("id"));
		}

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;

	}

	public static CommResult retweet(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("source", API_KEY);
		// try {
		// parameters.put("status", URLEncoder.encode((String)
		// map.get("comment"), "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		String url = "";
		try {
			url = String
					.format("http://api.t.sina.com.cn/statuses/repost.json?id=%s&is_comment=%s&status=%s",
							map.get("message_id"), map.get("is_comment"),
							URLEncoder.encode((String) map.get("comment"),
									"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;

	}

	@SuppressWarnings({ "unchecked" })
	public static CommResult uploadImage(Map map) {

		CommResult result = new CommResult();
		String filePath = (String) map.get("filePath");
		String message = (String) map.get("status");
		String u = String
				.format("http://api.t.sina.com.cn/statuses/upload.json");

		try {

			DefaultOAuthConsumer httpOAuthConsumer = new DefaultOAuthConsumer(
					CONSUMER_KEY_SINA, CONSUMER_SECRET_SINA);
			httpOAuthConsumer.setTokenWithSecret(userName, password);

			URL url = new URL(u);
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();
			request.setDoOutput(true);
			request.setRequestMethod("POST");
			HttpParameters para = new HttpParameters();
			para.put("status",
					URLEncoder.encode(message, "utf-8")
							.replaceAll("\\+", "%20"));
			String boundary = "---------------------------37531613912423";
			String content = "--"
					+ boundary
					+ "\r\nContent-Disposition: form-data; name=\"status\"\r\n\r\n";
			String pic = "\r\n--"
					+ boundary
					+ "\r\nContent-Disposition:form-data; name=\"pic\"; filename=\"image.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n";
			byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes();
			File aFile = new File(filePath);
			FileInputStream stream = new FileInputStream(aFile);
			byte[] file = new byte[(int) aFile.length()];
			stream.read(file);
			request.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			request.setRequestProperty(
					"Content-Length",
					String.valueOf(content.getBytes().length
							+ message.getBytes().length + pic.getBytes().length
							+ aFile.length() + end_data.length));
			httpOAuthConsumer.setAdditionalParameters(para);
			httpOAuthConsumer.sign(request);
			OutputStream ot = request.getOutputStream();
			ot.write(content.getBytes());
			ot.write(message.getBytes());
			ot.write(pic.getBytes());
			ot.write(file);
			ot.write(end_data);
			ot.flush();
			ot.close();
			request.connect();
			result.setResponseCode(String.valueOf(request.getResponseCode()));
			result.setMessage(request.getResponseMessage());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult destroyStatus(Map map) {
		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("source", API_KEY);

		String url = String.format(
				"http://api.t.sina.com.cn/statuses/destroy/:%s.json?",
				map.get("message_id"));

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;

	}

	public static CommResult getCommentsById(Map map) {

		CommResult result = new CommResult();

		String url = "http://api.t.sina.com.cn/statuses/comments.json";

		url = String.format(url + "?source=%s&id=%s&page=%s&count=20", API_KEY,
				map.get("message_id"), map.get("page"));

		result = httpGetOauth(mService, url, userName, password);
		return result;
	}

	public static CommResult updateComments(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		try {
			parameters.put("comment",
					URLEncoder.encode((String) map.get("comment"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String url = "http://api.t.sina.com.cn/statuses/comment.json";

		url = String.format(url + "?source=%s&id=%s", API_KEY,
				map.get("message_id"));

		result = httpPostOauth(mService, url, parameters, userName, password);
		return result;
	}

	public static CommResult getTrendList(Map map) {

		CommResult result = new CommResult();

		String url = "http://api.t.sina.com.cn/trends.json";

		url = String.format(url + "?source=%s&user_id=%s&count=40", API_KEY,
				map.get("uid"));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult getTrendTimeline(Map map) {

		CommResult result = new CommResult();

		if ((Integer) map.get("page") != 1) {
			return result;
		}

		String url = "http://api.t.sina.com.cn/trends/statuses.json";

		try {
			url = String.format(url + "?source=%s&trend_name=%s", API_KEY,
					URLEncoder.encode(
							(String) trendParameterMap.get("trend_name"),
							"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult getTrendsByType(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sina.com.cn/trends/%s.json?&base_app=0&source=%s",
						(String) map.get("type"), API_KEY);

		result = httpGetOauth(mService, url, userName, password);

		return result;

	}

	public static CommResult getEmotions(Map map) {

		CommResult result = new CommResult();

		String url = "http://api.t.sina.com.cn/emotions.json";

		url = String.format(url + "?source=%s", API_KEY);

		result = httpGetOauth(mService, url, userName, password);

		return result;

	}

	public static CommResult getUnreadMessage(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"http://api.t.sina.com.cn/statuses/unread.json?source=%s",
				API_KEY);

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult getHotUsers(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"http://api.t.sina.com.cn/users/hot.json?source=%s", API_KEY);

		result = httpGetOauth(mService, url, userName, password);

		return result;

	}

	public static CommResult getSuggestionUsers(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sina.com.cn/users/suggestions.json?source=%s&with_reason=1",
						API_KEY);

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult clearUnreadMessage(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameter = new HashMap<String, String>();
		parameter.put("type", (String) map.get("type"));

		String url = String.format(
				"http://api.t.sina.com.cn/statuses/reset_count.json?source=%s",
				API_KEY);

		result = httpPostOauth(mService, url, parameter, userName, password);

		return result;
	}

	public static CommResult getCommentTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sina.com.cn/statuses/comments_to_me.json?source=%s&count=20&page=%s",
						API_KEY, map.get("page"));

		result = httpGetOauth(mService, url, userName, password);

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
				.format("http://api.t.sina.com.cn/statuses/reply.json?source=%s&cid=%s&id=%s",
						API_KEY, map.get("cid"), map.get("id"));

		result = httpPostOauth(mService, url, parmeters, userName, password);

		return result;
	}

	public static CommResult getTimelineById(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sina.com.cn/statuses/show/:%s.json?source=4097074858",
						map.get("id"));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static synchronized CommResult getPublicTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sina.com.cn/statuses/public_timeline.json?source=%s&base_app=0",
						API_KEY);

		result = httpGetOauth(mService, url, userName, password);

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

		String url0 = String
				.format("http://api.t.sina.com.cn/location/geocode/geo_to_address.json?source=4097074858&coordinate=%s",
						String.valueOf(map.get("longitude")) + ","
								+ String.valueOf(map.get("latitude")));

		result = httpGetOauth(mService, url0, userName, password);
		if (result != null) {
			JSONObject location;
			try {
				location = new JSONObject(result.getMessage());
				if (location.has("address")) {
					JSONObject jObject = (JSONObject) location
							.getJSONObject("address");
					queryLocation = jObject.getString("city_name");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			String url;
			if (!("").equals(queryLocation)) {
				url = String
						.format("http://api.t.sina.com.cn/location/pois/round.json?source=4097074858&coordinate=%s&q=%s&count=50",
								String.valueOf(map.get("longitude")) + ","
										+ String.valueOf(map.get("latitude")),
								URLEncoder.encode(String.valueOf(queryLocation
										.substring(0,
												queryLocation.length() - 1)),
										"UTF-8"));
				result = httpGetOauth(mService, url, userName, password);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static CommResult UpdateLBSMessage(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/statuses/update.json";

		url = String.format(url + "?source=%s", API_KEY);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("status", String.valueOf(map.get("locationAddress")));
		params.put("long", String.valueOf(map.get("long")));
		params.put("lat", String.valueOf(map.get("lat")));

		String name = (String) map.get("user_name");
		String pw = (String) map.get("user_password");
		String service = (String) map.get("service");
		if (service == null) {
			result = httpPostOauth(mService, url, params, userName, password);
		} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
			result = httpPostOauth(mService, url, params, name, pw);
		}

		// Return Result
		return result;
	}

	public static synchronized CommResult getLBSTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/statuses/friends_timeline.json?source="
				+ API_KEY + "&count=100" + "&feature=1";

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

	public static synchronized CommResult updateTags(Map map) {
		// Prepare result
		CommResult result = new CommResult();
		// URL
		String url = "";
		HashMap<String, String> params = new HashMap<String, String>();
		if (map.containsKey("tagFlag")) {
			url = "http://api.t.sina.com.cn/tags/destroy.json";
			url = String.format(url + "?source=%s", API_KEY);
			params.put("tag_id", map.get("tag_id").toString());
		} else {
			url = "http://api.t.sina.com.cn/tags/create.json";
			url = String.format(url + "?source=%s", API_KEY);

			String tags = map.get("tags").toString();
			params.put("tags", tags);
		}

		result = httpPostOauth(mService, url, params, userName, password);

		// Return Result
		return result;
	}

	public static synchronized CommResult getUserTagsList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/tags.json?source=" + API_KEY
				+ "&count=20";

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

	public static synchronized CommResult getSuggestionsTagsList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sina.com.cn/tags/suggestions.json?source="
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
}
