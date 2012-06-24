package com.anhuioss.crowdroid.service.tencent;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.util.CommResult;
import com.mime.qweibo.OauthKey;
import com.mime.qweibo.QParameter;
import com.mime.qweibo.QWeiboRequest;

public class TencentCommHandler {

	public static final String APP_KEY = "12ff5479ff954007ba7b19054fae0fda";
	public static final String APP_SECRET = "e64f13bb7ae08efa2ba6c16bb45eb3a9";
	public static final String FORMAT = "json";

	private static String accessToken = "";

	private static String tokenSecret = "";

	public static Map trendParameterMap;

	private static Context context;

	public static String getAccessToken() {
		return accessToken;
	}

	public static void setAppContext(Context ctx) {
		context = ctx;
	}

	public static String getTokenSecret() {
		return tokenSecret;
	}

	public static void setTrendParameter(Map map) {
		TencentCommHandler.trendParameterMap = map;
	}

	public static void setAccount(String accessToken, String tokenSecret) {
		TencentCommHandler.accessToken = accessToken;
		TencentCommHandler.tokenSecret = tokenSecret;
	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getNewToken(Map map) {

		synchronized ("get new token") {
			// Prepare Result
			CommResult result = new CommResult();
			String url = "http://open.t.qq.com/cgi-bin/request_token";
			List<QParameter> parameters = new ArrayList<QParameter>();

			OauthKey oauthKey = new OauthKey();
			oauthKey.customKey = APP_KEY;
			oauthKey.customSecrect = APP_SECRET;
			// The OAuth Call back URL(You should encode this url if it
			// contains some unreserved characters).
			oauthKey.callbackUrl = "null";

			QWeiboRequest request = new QWeiboRequest();
			String res = null;
			try {
				res = request.syncRequest(url, "GET", oauthKey, parameters,
						null);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (res == null || res.equals("")) {
				return result;
			}

			String[] tokenArray = res.split("&");

			if (tokenArray.length < 2) {
				return result;
			}

			String strTokenKey = tokenArray[0];
			String strTokenSecrect = tokenArray[1];

			String[] token1 = strTokenKey.split("=");
			if (token1.length < 2) {
				return result;
			}
			String mTokenKey = token1[1];

			String[] token2 = strTokenSecrect.split("=");
			if (token2.length < 2) {
				return result;
			}
			String mTokenSecret = token2[1];

			result.setResponseCode("200");
			result.setMessage(mTokenKey + ";" + mTokenSecret);
			return result;

		}

	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	public static String getUserInfo(String requestToken,
			String requestTokenSecret) {

		// url
		String url = "http://open.t.qq.com/api/user/info";

		List<QParameter> parameters = new ArrayList<QParameter>();

		// prepare oauthKey
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecret;

		parameters.add(new QParameter("format", FORMAT));

		QWeiboRequest request = new QWeiboRequest();

		// prepare res
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return result
		return res;

	}

	@SuppressWarnings("unchecked")
	public static CommResult getAccessToken(Map map) {

		CommResult result = new CommResult();

		String requestToken = (String) map.get("requestToken");
		String requestTokenSecrect = (String) map.get("requestTokenSecrect");
		String verify = (String) map.get("verify");

		String url = "http://open.t.qq.com/cgi-bin/access_token";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecrect;
		oauthKey.verify = verify;

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (res == null || res.equals("")) {
			return null;
		}

		String[] tokenArray = res.split("&");

		if (tokenArray.length < 2) {
			return null;
		}

		String strTokenKey = tokenArray[0];
		String strTokenSecrect = tokenArray[1];

		String[] token1 = strTokenKey.split("=");
		if (token1.length < 2) {
			return null;
		}
		String mTokenKey = token1[1];

		String[] token2 = strTokenSecrect.split("=");
		if (token2.length < 2) {
			return null;
		}
		String mTokenSecret = token2[1];

		result.setResponseCode("200");
		result.setMessage(mTokenKey + ";" + mTokenSecret);

		return result;
	}

	public static synchronized CommResult getHomeTimeline(Map map) {

		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				try {
					pageTime = map.get("pageTime").toString();
				} catch (Exception e) {
				}

			}
		}

		// url
		String url = "http://open.t.qq.com/api/statuses/home_timeline";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("pageflag", page));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("type", String.valueOf(3)));
		parameters.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static synchronized CommResult getAtMessageTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}

		String url = "http://open.t.qq.com/api/statuses/mentions_timeline";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("pageflag", String.valueOf(page)));
		parameter.add(new QParameter("reqnum", String.valueOf(20)));
		parameter.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult getDirectMessageSent(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}

		String url = "http://open.t.qq.com/api/private/send";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("pageflag", String.valueOf(page)));
		parameter.add(new QParameter("reqnum", String.valueOf(20)));
		parameter.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static synchronized CommResult getDirectMessageReceived(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}

		String url = "http://open.t.qq.com/api/private/recv";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("pageflag", String.valueOf(page)));
		parameter.add(new QParameter("reqnum", String.valueOf(20)));
		parameter.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult getMyTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}

		String url = "http://open.t.qq.com/api/statuses/broadcast_timeline";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("pageflag", String.valueOf(page)));
		parameter.add(new QParameter("reqnum", String.valueOf(20)));
		parameter.add(new QParameter("type", String.valueOf(3)));
		parameter.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	@SuppressWarnings("unchecked")
	public static CommResult updateStatus(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String status = map.get("status").toString();
		String clientip = getLocalIpAddress();

		// URL
		String url = "http://open.t.qq.com/api/t/add";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("content", status));
		parameter.add(new QParameter("clientip", clientip));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult updateMoodStatus(Map map) {

		// Prepare result
		CommResult result = new CommResult();
		String signtype = map.get("flag").toString();
		String status = map.get("status").toString();
		String clientip = getLocalIpAddress();

		// URL
		String url = "http://open.t.qq.com/api/t/add_emotion";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("content", status));
		parameter.add(new QParameter("clientip", clientip));
		parameter.add(new QParameter("signtype", signtype));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult verifyUser(Map map) {

		CommResult result = new CommResult();

		return result;
	}

	public static CommResult getFollowersList(Map map) {

		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String username = map.get("username").toString();
		String startindex = String.valueOf(20 * (Integer.parseInt(page) - 1));

		// url
		String url = "http://open.t.qq.com/api/friends/user_fanslist";
		List<QParameter> parameters = new ArrayList<QParameter>();

		// prepare oauthKey
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("startindex", startindex));
		parameters.add(new QParameter("name", username));

		QWeiboRequest request = new QWeiboRequest();

		// prepare res
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		// Return Result
		return result;
	}

	public static CommResult getFollowList(Map map) {

		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String userName = map.get("username").toString();
		String startindex = String.valueOf(20 * (Integer.parseInt(page) - 1));

		// url
		String url = "http://open.t.qq.com/api/friends/user_idollist";
		List<QParameter> parameters = new ArrayList<QParameter>();

		// prepare oauthKey
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("startindex", startindex));
		parameters.add(new QParameter("name", userName));

		QWeiboRequest request = new QWeiboRequest();

		// prepare res
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		// Return Result
		return result;
	}

	public static CommResult getUserInfo(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String userName = (String) map.get("username");
		// url
		String url = "http://open.t.qq.com/api/user/other_info";
		List<QParameter> parameters = new ArrayList<QParameter>();

		// prepare oauthKey
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("name", userName));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public static CommResult getFindPeopleInfo(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String keyword = (String) map.get("query");
		int pageFlag = (Integer) map.get("page");
		String page = String.valueOf(pageFlag);

		String url = "";

		if (map.containsKey("TencentTagFlag")) {
			url = "http://open.t.qq.com/api/search/userbytag";
		} else {
			url = "http://open.t.qq.com/api/search/user";
		}

		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("keyword", keyword));
		parameter.add(new QParameter("pagesize", String.valueOf(20)));
		parameter.add(new QParameter("page", page));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		// Return Result
		return result;
	}

	public static CommResult searchInfo(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String keyword = (String) map.get("search");
		String page = (String) map.get("page");
//		String pageTime = "0";
//		if (!page.equals("")) {
//			if (page.equals("1")) {
//				page = "0";
//				pageTime = "0";
//			} else {
//				page = "1";
//				pageTime = map.get("pageTime").toString();
//			}
//		}

		String url = null;

		if (map.containsKey("TencentSearchFlag")) {
			url = "http://open.t.qq.com/api/search/ht";
		} else {
			url = "http://open.t.qq.com/api/search/t";
		}

		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("keyword", keyword));
		parameter.add(new QParameter("pagesize", String.valueOf(20)));
		parameter.add(new QParameter("page", page));
//		if (!map.containsKey("TencentSearchFlag")) {
//			parameter.add(new QParameter("pageTime", pageTime));
//		}

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		// Return Result
		return result;
	}

	public static CommResult getUserStatusList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String userName = (String) map.get("user_name");

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}

		String url = "http://open.t.qq.com/api/statuses/user_timeline";
		List<QParameter> parameters = new ArrayList<QParameter>();

		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("pageflag", String.valueOf(page)));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("name", userName));
		parameters.add(new QParameter("type", String.valueOf(3)));
		parameters.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult directMessage(Map map) {

		CommResult result = new CommResult();

		String url = "http://open.t.qq.com/api/private/add";

		Object nameObject = map.get("name");
		if (nameObject == null) {
			return result;
		}
		String name = nameObject.toString();
		String status = map.get("message").toString();
		String clientip = getLocalIpAddress();

		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("name", name));
		parameter.add(new QParameter("content", status));
		parameter.add(new QParameter("clientip", clientip));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		// return result

		return result;

	}

	public static CommResult getFavoriteList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}

		String url = "http://open.t.qq.com/api/fav/list_t";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("pageflag", page));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult setFavorite(Map map) {

		CommResult result = new CommResult();
		String id = map.get("message_id").toString();

		String addUrl = "http://open.t.qq.com/api/fav/addt";
		String delUrl = "http://open.t.qq.com/api/fav/delt";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("id", id));
		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			if (map.get("type").equals("create")) {
				res = request.syncRequest(addUrl, "POST", oauthKey, parameters,
						null);
			} else {
				res = request.syncRequest(delUrl, "POST", oauthKey, parameters,
						null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult showRelation(Map map) {

		CommResult result = new CommResult();

		// String url =
		// String.format("http://api.t.sina.com.cn/friendships/exists.json?source=%s&user_a=%s&user_b=%s",
		// API_KEY, map.get("uid_a"), map.get("uid"));
		//
		// result = httpGetOauth(mService, url, userName, password);

		return result;

	}

	public static CommResult showRelationNew(Map map) {

		CommResult result = new CommResult();

		// String url =
		// String.format("http://api.t.sina.com.cn/friendships/show.json?source=%s&source_id=%s&target_id=%s",
		// API_KEY,map.get("source_id"),map.get("target_id"));
		//
		// result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult setFollow(Map map) {

		CommResult result = new CommResult();

		// url
		String url = "";
		String type = map.get("type").toString();
		String name = map.get("username").toString();

		List<QParameter> parameters = new ArrayList<QParameter>();

		// prepare oauthKey
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		if (type.equals("create")) {
			url = "http://open.t.qq.com/api/friends/add";
		} else {
			url = "http://open.t.qq.com/api/friends/del";
		}

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("name", name));

		QWeiboRequest request = new QWeiboRequest();

		// prepare res
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		// Return Result
		return result;

	}

	public static CommResult retweet(Map map) {

		CommResult result = new CommResult();
		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		String commentIsCheck = String.valueOf(map.get("is_comment"));
		if (commentIsCheck.equals("1")) {
			updateComments(map);
		}

		try {

			String content = map.get("comment").toString();
			String reId = map.get("message_id").toString();
			String clientip = getLocalIpAddress();

			String url = "http://open.t.qq.com/api/t/re_add";

			List<QParameter> parameters = new ArrayList<QParameter>();
			OauthKey oauthKey = new OauthKey();
			oauthKey.customKey = APP_KEY;
			oauthKey.customSecrect = APP_SECRET;
			oauthKey.tokenKey = accessToken;
			oauthKey.tokenSecrect = tokenSecret;

			parameters.add(new QParameter("format", FORMAT));
			parameters.add(new QParameter("content", content));
			parameters.add(new QParameter("clientip", clientip));
			parameters.add(new QParameter("reid", reId));

			res = request.syncRequest(url, "POST", oauthKey, parameters, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		return result;

	}

	@SuppressWarnings({ "unchecked" })
	public static CommResult uploadImage(Map map) {

		CommResult result = new CommResult();
		String filePath = (String) map.get("filePath");
		String message = (String) map.get("status");
		if ("".equals(message) && filePath != null && filePath.length() > 0) {
			message = context.getString(R.string.share_image);
		}
		String clientip = getLocalIpAddress();

		// String url
		String url = "http://open.t.qq.com/api/t/add_pic";

		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("content", message));
		parameters.add(new QParameter("clientip", clientip));

		List<QParameter> picfile = new ArrayList<QParameter>();
		picfile.add(new QParameter("pic", filePath));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameters,
					picfile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		return result;

	}

	public static CommResult destroyStatus(Map map) {
		CommResult result = new CommResult();
		String id = map.get("message_id").toString();

		String url = "http://open.t.qq.com/api/t/del";

		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("id", id));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		return result;

	}

	public static CommResult getCommentsById(Map map) {

		CommResult result = new CommResult();
		String messageId = map.get("message_id").toString();
		String page = map.get("page").toString();

		String url = "http://open.t.qq.com/api/t/re_list";
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("rootid", messageId));
		parameter.add(new QParameter("flag", String.valueOf(2)));
		parameter.add(new QParameter("pageflag", String.valueOf(page)));
		parameter.add(new QParameter("reqnum", String.valueOf(20)));
		parameter.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult updateComments(Map map) {

		CommResult result = new CommResult();
		QWeiboRequest request = new QWeiboRequest();
		String res = null;

		try {

			String content = map.get("comment").toString();
			String reId = map.get("message_id").toString();
			String clientip = getLocalIpAddress();

			String url = "http://open.t.qq.com/api/t/comment";

			List<QParameter> parameters = new ArrayList<QParameter>();
			OauthKey oauthKey = new OauthKey();
			oauthKey.customKey = APP_KEY;
			oauthKey.customSecrect = APP_SECRET;
			oauthKey.tokenKey = accessToken;
			oauthKey.tokenSecrect = tokenSecret;

			parameters.add(new QParameter("format", FORMAT));
			parameters.add(new QParameter("content", content));
			parameters.add(new QParameter("clientip", clientip));
			parameters.add(new QParameter("reid", reId));

			res = request.syncRequest(url, "POST", oauthKey, parameters, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult getTrendList(Map map) {

		CommResult result = new CommResult();

		// String url = "http://api.t.sina.com.cn/trends.json";
		//
		// url = String.format(url + "?source=%s&user_id=%s&count=40", API_KEY,
		// map.get("uid"));
		//
		// result = httpGetOauth(mService, url, userName, password);

		return result;
	}

	public static CommResult getTrendTimeline(Map map) {

		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String name = trendParameterMap.get("tencent_name").toString();

		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				pageTime = map.get("pageTime").toString();
			}
		}
		String url = "http://open.t.qq.com/api/statuses/ht_timeline";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("pageflag", String.valueOf(1)));
		parameter.add(new QParameter("httext", name));
		// parameter.add(new QParameter("pageinfo", String.valueOf(page)));
		parameter.add(new QParameter("reqnum", String.valueOf(20)));
		parameter.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult getTrendsByType(Map map) {

		CommResult result = new CommResult();
		String type = map.get("type").toString();

		String url = "http://open.t.qq.com/api/trends/ht";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("type", type));
		parameter.add(new QParameter("reqnum", String.valueOf(20)));
		// parameter.add(new QParameter("pos", String.valueOf(0)));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult getEmotions(Map map) {

		CommResult result = new CommResult();

		String url = "http://open.t.qq.com/api/other/get_emotions";

		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("type", "0"));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult getUnreadMessage(Map map) {

		CommResult result = new CommResult();

		String url = "http://open.t.qq.com/api/info/update";
		List<QParameter> parameter = new ArrayList<QParameter>();

		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("op", "0"));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult getHotUsers(Map map) {

		CommResult result = new CommResult();

		// String url =
		// String.format("http://api.t.sina.com.cn/users/hot.json?source=%s&type=%s",
		// API_KEY,map.get("type"));
		//
		// result = httpGetOauth(mService, url, userName, password);

		return result;

	}

	public static CommResult getSuggestionUsers(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// url
		String url = "http://open.t.qq.com/api/other/kownperson";
		List<QParameter> parameters = new ArrayList<QParameter>();

		// prepare oauthKey
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("ip", getLocalIpAddress()));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static void clearUnreadMessage(Map map) {

		String url = "http://open.t.qq.com/api/info/update";
		String type = (String) map.get("type");

		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		List<QParameter> parameter = new ArrayList<QParameter>();
		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("op", "1"));
		parameter.add(new QParameter("type", type));

		QWeiboRequest request = new QWeiboRequest();
		try {
			request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static CommResult getCommentTimeline(Map map) {

		CommResult result = new CommResult();

		return result;
	}

	public static CommResult replyToComment(Map map) {

		CommResult result = new CommResult();

		String comment = map.get("comment").toString();
		String sourceMsgId = map.get("reply_id").toString();

		String url = "http://open.t.qq.com/api/t/reply";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("content", comment));
		parameters.add(new QParameter("clientip", getLocalIpAddress()));
		parameters.add(new QParameter("reid", sourceMsgId));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult getHotRetweetTimeline(Map map) {

		CommResult result = new CommResult();
		// url
		String url = "http://open.t.qq.com/api/trends/t";
		List<QParameter> parameters = new ArrayList<QParameter>();

		// prepare oauthKey
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		String page = map.get("page").toString();
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
			} else {
				page = "1";
			}
		}

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("pos", page));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static synchronized CommResult getPublicTimeLine(Map map) {

		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				try {
					pageTime = map.get("pageTime").toString();
				} catch (Exception e) {
				}

			}
		}

		String url = "http://open.t.qq.com/api/statuses/public_timeline";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("pageflag", page));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("type", String.valueOf(3)));
		parameters.add(new QParameter("pagetime", pageTime));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static CommResult updateProfile(Map map) {

		// Prepare result
		CommResult result = new CommResult();
		String Name = map.get("nick").toString();
		String description = map.get("introduction").toString();
		// URL
		String url = "http://open.t.qq.com/api/user/update";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("nick", Name));
		parameter.add(new QParameter("introduction", description));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult getLBSTimeline(Map map) {
		CommResult result = new CommResult();
		String url = "http://open.t.qq.com/api/lbs/get_around_new";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));

		parameter.add(new QParameter("longitude", map.get("longitude")
				.toString().substring(0, 9)));
		parameter.add(new QParameter("latitude", map.get("latitude").toString()
				.substring(0, 9)));
		// parameter.add(new QParameter("longitude", map.get("longitude")
		// .toString()));
		// parameter
		// .add(new QParameter("latitude", map.get("latitude").toString()));
		parameter.add(new QParameter("pageinfo", ""));
		parameter.add(new QParameter("pagesize", "1"));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult getLBSPeople(Map map) {
		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageinfo = "";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageinfo = "";
			} else {
				page = "1";
				try {
					pageinfo = map.get("pageinfo").toString();
				} catch (Exception e) {
				}

			}
		}
		String url = "http://open.t.qq.com/api/lbs/get_around_people";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("longitude", map.get("longitude")
				.toString().substring(0, 9)));
		parameter.add(new QParameter("latitude", map.get("latitude").toString()
				.substring(0, 9)));
		parameter.add(new QParameter("pageinfo", pageinfo));
		parameter.add(new QParameter("pagesize", "20"));
		parameter.add(new QParameter("gender", "0"));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

	public static CommResult updateLBSMessage(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// String url0 = "http://open.t.qq.com/api/lbs/update_pos";
		// List<QParameter> parameter0 = new ArrayList<QParameter>();
		// OauthKey oauthKey = new OauthKey();
		// oauthKey.customKey = APP_KEY;
		// oauthKey.customSecrect = APP_SECRET;
		// oauthKey.tokenKey = accessToken;
		// oauthKey.tokenSecrect = tokenSecret;
		//
		// parameter0.add(new QParameter("format", FORMAT));
		// parameter0.add(new QParameter("longitude", map.get("longitude")
		// .toString().substring(0, 9)));
		// parameter0.add(new QParameter("latitude", map.get("latitude")
		// .toString().substring(0, 9)));
		// QWeiboRequest request0 = new QWeiboRequest();
		// String res0 = null;
		// try {
		// res0 = request0.syncRequest(url0, "POST", oauthKey, parameter0,
		// null);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// result.setMessage(res0);
		// // ------------------------------------------------
		//
		String status = map.get("locationAddress").toString()
				+ map.get("status").toString();
		String clientip = getLocalIpAddress();

		// URL
		String url = "http://open.t.qq.com/api/t/add";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("content", status));
		parameter.add(new QParameter("clientip", clientip));
		parameter.add(new QParameter("jing", map.get("longitude").toString()
				.substring(0, 9)));
		parameter.add(new QParameter("wei", map.get("latitude").toString()
				.substring(0, 9)));
		parameter.add(new QParameter("syncflag", "0"));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static CommResult getLBSLocationList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = "http://ugc.map.soso.com/rgeoc/";
		List<QParameter> parameter = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameter.add(new QParameter("format", FORMAT));
		parameter.add(new QParameter("lnglat", map.get("longitude").toString()
				.substring(0, 9)
				+ "," + map.get("latitude").toString().substring(0, 9)));
		parameter.add(new QParameter("reqsrc", "wb"));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (res.contains("poilist")) {
			result.setResponseCode("200");
		} else {
			result.setResponseCode("0");
		}
		result.setMessage(res);
		return result;
	}

	public static CommResult updateUserImage(Map map) {

		CommResult result = new CommResult();
		String filePath = (String) map.get("filePath");

		// String url
		String url = "http://open.t.qq.com/api/user/update_head";

		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));

		List<QParameter> picfile = new ArrayList<QParameter>();
		picfile.add(new QParameter("pic", filePath));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameters,
					picfile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		return result;

	}

	public static CommResult getMoodStatus(Map map) {

		CommResult result = new CommResult();

		String userName = map.get("username").toString();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				try {
					pageTime = map.get("pageTime").toString();
				} catch (Exception e) {
				}

			}
		}

		// String url
		String url = "http://open.t.qq.com/api/user/emotion";

		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("name", userName));
		parameters.add(new QParameter("pageflag", String.valueOf(1)));
		parameters.add(new QParameter("timestamp", pageTime));
		parameters.add(new QParameter("type", String.valueOf(0)));
		parameters.add(new QParameter("contenttype", String.valueOf(0)));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		return result;

	}

	public static CommResult updateTags(Map map) {

		CommResult result = new CommResult();

		// String url
		String url = "";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		if (map.containsKey("tagFlag")) {
			url = "http://open.t.qq.com/api/tag/del";
			parameters.add(new QParameter("tagid", (String) map.get("tag_id")));
		} else {
			url = "http://open.t.qq.com/api/tag/add";
			parameters.add(new QParameter("tag", (String) map.get("tags")));
		}

		parameters.add(new QParameter("format", FORMAT));
		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "POST", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);

		return result;

	}

	public static synchronized CommResult getFamouslist(Map map) {

		CommResult result = new CommResult();

		String classid = map.get("classid").toString();

		String subclassid = map.get("subclassid").toString();
		// url
		String url = "http://open.t.qq.com/api/trends/famouslist";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("classid", classid));
		parameters.add(new QParameter("subclassid", subclassid));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;
	}

	public static synchronized CommResult getAreaTimeline(Map map) {

		CommResult result = new CommResult();

		String page = map.get("page").toString();
		String pageTime = "0";
		if (!page.equals("")) {
			if (page.equals("1")) {
				page = "0";
				pageTime = "0";
			} else {
				page = "1";
				try {
					pageTime = map.get("pageTime").toString();
				} catch (Exception e) {
				}

			}
		}

		// url
		String url = "http://open.t.qq.com/api/statuses/area_timeline";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = APP_KEY;
		oauthKey.customSecrect = APP_SECRET;
		oauthKey.tokenKey = accessToken;
		oauthKey.tokenSecrect = tokenSecret;

		// parameters.add(new QParameter("format", FORMAT));
		// parameters.add(new QParameter("pageflag", page));
		// parameters.add(new QParameter("reqnum", String.valueOf(20)));
		// parameters.add(new QParameter("type", String.valueOf(3)));
		// parameters.add(new QParameter("pagetime", pageTime));

		parameters.add(new QParameter("format", FORMAT));
		parameters.add(new QParameter("pos", "0"));
		parameters.add(new QParameter("reqnum", String.valueOf(20)));
		parameters.add(new QParameter("country", "1"));
		parameters.add(new QParameter("province", map.get("stateCode").toString()));
		parameters.add(new QParameter("city", map.get("cityCode").toString()));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setResponseCode(TencentParserHandler.parseResponseCode(res));
		result.setMessage(res);
		return result;

	}

}
