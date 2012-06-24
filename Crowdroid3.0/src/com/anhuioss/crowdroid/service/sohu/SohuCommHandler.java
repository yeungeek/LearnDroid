package com.anhuioss.crowdroid.service.sohu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;
import android.content.Context;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.settings.SohuOAuthConstant;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

public class SohuCommHandler extends HttpCommunicator {

	public static final String CONSUMER_KEY = "FL2p2T30VCEjYdYC9GJg";

	public static final String CONSUMER_SECRET = "1qfIBY6CN5nmq!^OpPGN6qs%5Cli1NX-W^Jv67SM";

	public static final String REQUEST_TOKEN_URL = "http://api.t.sohu.com/oauth/request_token";

	public static final String AUTHORIZE_URL = "http://api.t.sohu.com/oauth/authorize";

	public static final String ACCESS_TOKEN_URL = "http://api.t.sohu.com/oauth/access_token";

	private static String mService = IGeneral.SERVICE_NAME_SOHU;

	private static String userName;

	private static String password;

	private static Context context;

	private static Map trendParameterMap;

	private static SohuCommHandler instance;

	public static void setAppContext(Context ctx) {
		context = ctx;
	}

	public static SohuCommHandler getInstance() {
		if (instance == null) {
			return instance;
		}
		return new SohuCommHandler();
	}

	public static void setAccount(String userName, String password) {
		SohuCommHandler.userName = userName;
		SohuCommHandler.password = password;
	}

	public static CommResult verifyUser(Map map) {

		CommResult result = new CommResult();

		String url = "http://api.t.sohu.com/account/verify_credentials.json";

		String name = (String) map.get("accessToken");
		String passwd = (String) map.get("tokenSecret");

		result = httpGetOauth(mService, url, name, passwd);

		return result;
	}

	public static CommResult getHomeTimeline(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/statuses/friends_timeline.json?count=20";

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

	public static CommResult getAtMessageTimeline(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/statuses/mentions_timeline.json?count=20";

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

	public static CommResult getDirectMessageReceived(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/direct_messages.json?count=2";

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

	public static CommResult getDirectMessageSent(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/direct_messages/sent.json?count=2";

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

	public static CommResult getMyTimeline(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/statuses/user_timeline.json?count=20";

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

	public static CommResult updateStatus(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/statuses/update.json";

		String status = (String) map.get("status");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("status", status);

		String name = (String) map.get("user_name");
		String pw = (String) map.get("user_password");
		String service = (String) map.get("service");
		if (service == null) {
			result = httpPostOauth(mService, url, params, userName, password);
		} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
			result = httpPostOauth(mService, url, params, name, pw);
		}

		// Return Result
		return result;
	}

	public static CommResult destoryStatus(Map map) {
		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();

		String url = String.format(
				"http://api.t.sohu.com/statuses/destroy/%s.json?",
				map.get("message_id"));

		result = httpPostOauth(mService, url, parameters, userName, password);

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
				url = String.format("http://api.t.sohu.com/users/show/%s.json",
						URLEncoder.encode(screenName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			url = String
					.format("http://api.t.sohu.com/users/show/%s.json", uid);
		}

		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult getFollowList(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/statuses/friends/%s.json";

		url = String.format(url + "?cursor=%s&count=20", map.get("uid"),
				map.get("cursor"));

		// HTTP Communication
		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult getFollowersList(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/statuses/followers/%s.json";

		url = String.format(url + "?cursor=%s&count=20", map.get("uid"),
				map.get("cursor"));

		// HTTP Communication
		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult showRelationNew(Map map) {
		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sohu.com/friendships/show.json?source_id=%s&target_id=%s",
						map.get("source_id"), map.get("target_id"));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult directMessage(Map map) {
		CommResult result = new CommResult();
		HashMap<String, String> parameters = new HashMap<String, String>();
		// parameters.put("id", (String) map.get("send_to"));
		try {
			parameters.put("text",
					URLEncoder.encode((String) map.get("message"), "UTF-8"));
			parameters.put("source", CONSUMER_KEY);
			parameters.put("user", (String) map.get("uid"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = String
				.format("http://api.t.sohu.com/direct_messages/new.json");

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;
	}

	public static CommResult setFollow(Map map) {
		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("source", CONSUMER_KEY);

		String url = "";

		if (map.get("type").equals("create")) {
			url = String.format(
					"http://api.t.sohu.com/friendships/create/%s.json",
					map.get("id"));
		} else {
			url = String.format(
					"http://api.t.sohu.com/friendships/destroy/%s.json",
					map.get("id"));
		}

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static CommResult uploadImage(Map map) {

		CommResult result = new CommResult();
		String filePath = (String) map.get("filePath");
		String message = (String) map.get("status");

		String u = String.format("http://api.t.sohu.com/statuses/upload.json");

		try {

			DefaultOAuthConsumer httpOAuthConsumer = new DefaultOAuthConsumer(
					CONSUMER_KEY, CONSUMER_SECRET);
			httpOAuthConsumer.setTokenWithSecret(userName, password);

			URL url = new URL(u);
			HttpURLConnection request = (HttpURLConnection) url
					.openConnection();
			request.setDoOutput(true);
			request.setRequestMethod("POST");

			HttpParameters para = new HttpParameters();
			String status = URLEncoder.encode(message, "utf-8").replaceAll(
					"\\+", "%20");
			para.put("status", status);

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
							+ status.getBytes().length + pic.getBytes().length
							+ aFile.length() + end_data.length));
			httpOAuthConsumer.setAdditionalParameters(para);
			httpOAuthConsumer.sign(request);
			OutputStream ot = request.getOutputStream();
			ot.write(content.getBytes());
			ot.write(status.getBytes());
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

	public static CommResult retweet(Map map) {
		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		String message = (String) map.get("comment");
		if ("".equals(message)) {
			message = context.getString(R.string.sohu_retweet_addstr);
		}
		try {
			parameters.put("status", URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String url = String.format(
				"http://api.t.sohu.com/statuses/transmit/%s.json",
				map.get("message_id"));

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;
	}

	public static CommResult getFindPeopleInfo(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		String url = "";

		try {
			url = String
					.format("http://api.t.sohu.com/users/search.json?q=%s&page=%s&count=20",
							URLEncoder.encode((String) map.get("query"),
									"UTF-8"), map.get("page"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult getFavoriteList(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		String url = String.format(
				"http://api.t.sohu.com/favourites.json?page=%s&count=20",
				map.get("page"));

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
			StatusData statusData = crowdroidApplication.getStatusData();
			uid = statusData.getCurrentUid();
		}

		// URL
		String url = String
				.format("http://api.t.sohu.com/statuses/user_timeline/%s.json?count=20&page=%s",
						uid, map.get("page"));

		if (uid == null) {
			try {
				url = String
						.format("http://api.t.sohu.com/statuses/user_timeline/%s.json?count=20&page=%s",
								URLEncoder.encode(screenName, "UTF-8"),
								map.get("page"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		// HTTP Communication
		result = httpGetOauth(mService, url, userName, password);

		// Return result
		return result;
	}

	public static CommResult setFavorite(Map map) {
		CommResult result = new CommResult();

		String url = "";

		if (map.get("type").equals("create")) {
			url = String.format(
					"http://api.t.sohu.com/favourites/create/%s.json",
					map.get("message_id"));
		} else {
			url = String.format(
					"http://api.t.sohu.com/favourites/destroy/%s.json",
					map.get("message_id"));
		}

		result = httpPostOauth(mService, url, new HashMap<String, String>(),
				userName, password);

		return result;
	}

	public static CommResult searchInfo(Map map) {
		// Prepare result
		CommResult result = new CommResult();

		String url = "";

		try {
			if (map.containsKey("SohuSearchTypeFlag")) {
				url = String
						.format("http://api.t.sohu.com/search.json?q=%s&page=%s&rpp=20",
								URLEncoder.encode((String) map.get("search"),
										"UTF-8"), map.get("page"));
			} else {
				url = String
						.format("http://api.t.sohu.com/statuses/search.json?q=%s&page=%s&rpp=20",
								URLEncoder.encode((String) map.get("search"),
										"UTF-8"), map.get("page"));
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		result = httpGetOauth(mService, url, userName, password);

		// Return Result
		return result;
	}

	public static CommResult getCommentsById(Map map) {
		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sohu.com/statuses/comments/%s.json?count=20&page=%s",
						map.get("message_id"), map.get("page"));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult updateComments(Map map) {
		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		try {
			parameters.put("id", String.valueOf(map.get("message_id")));
			parameters.put("comment",
					URLEncoder.encode((String) map.get("comment"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String url = "http://api.t.sohu.com/statuses/comment.json";

		result = httpPostOauth(mService, url, parameters, userName, password);

		return result;
	}

	// public static CommResult getTrendList(Map map) {
	// return null;
	// }
	//
	// public static CommResult getTrendTimeline(Map map) {
	// return null;
	// }

	public static CommResult getUnreadMessage(Map map) {
		CommResult result = new CommResult();

		String url = String.format("http://api.t.sohu.com/statuses/check.json");

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	// public static CommResult getEmotions(Map map) {
	// return null;
	// }
	//
	// public static CommResult getHotUsers(Map map) {
	// return null;
	// }
	//
	// public static CommResult getSuggestionUsers(Map map) {
	// return null;
	// }

	public static CommResult getCommentTimeline(Map map) {
		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sohu.com/statuses/comments_timeline.json?count=20&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	// public static CommResult replyToComment(Map map) {
	// CommResult result = new CommResult();
	// HashMap<String, String> parmeters = new HashMap<String, String>();
	// try {
	// parmeters.put("comment", URLEncoder.encode((String) map.get("comment"),
	// "UTF-8"));
	// parmeters.put("source",CONSUMER_KEY);
	// parmeters.put("id", (String)map.get("id"));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// String url =
	// String.format("http://api.t.sohu.com/statuses/comment.json");
	// result = httpPostOauth(mService, url, parmeters, userName, password);
	// return result;
	// }

	public static CommResult getTimelineById(Map map) {
		CommResult result = new CommResult();

		String url = String.format(
				"http://api.t.sohu.com/statuses/show/%s.json", map.get("id"));

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult getNewToken(Map map) {

		CommResult result = new CommResult();

		String pin = (String) map.get("pinCode");

		SohuOAuthConstant sohuOAuth = SohuOAuthConstant.getInstance();

		sohuOAuth.setOAuthVerifier(pin);
		sohuOAuth.separateTokenAndSecret(sohuOAuth.getAccessTokenAndSecret());

		String message = sohuOAuth.getOAuthToken() + ";"
				+ sohuOAuth.getOAuthTokenSecret();

		result.setResponseCode(String.valueOf(200));
		result.setMessage(message);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static synchronized CommResult getPublicTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.t.sohu.com/statuses/public_timeline.json");

		result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	// public static CommResult getTrendsByType(Map map) {
	// return null;
	// }
	// public static CommResult getNewToken(Map map) {
	// return null;
	// }

	@SuppressWarnings("unchecked")
	public static CommResult updateProfile(Map map) {

		CommResult result = new CommResult();

		// URL
		String url = "http://api.t.sohu.com/account/update_profile.json";

		String Name = (String) map.get("nick_name");
		String description = (String) map.get("description");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nick_name", Name);
		params.put("description", description);

		String name = (String) map.get("user_name");
		String pw = (String) map.get("user_password");
		String service = (String) map.get("service");
		if (service == null) {
			result = httpPostOauth(mService, url, params, userName, password);
		} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
			result = httpPostOauth(mService, url, params, name, pw);
		}

		// Return Result
		return result;
	}

}
