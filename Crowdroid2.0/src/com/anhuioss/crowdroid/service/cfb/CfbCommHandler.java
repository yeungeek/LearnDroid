package com.anhuioss.crowdroid.service.cfb;

import it.sauronsoftware.base64.Base64;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;
import com.anhuioss.crowdroid.util.MultiPartFormOutputStream;

public class CfbCommHandler extends HttpCommunicator {

	private static String currentUserName = "";

	private static String currentUserPassword = "";

	private static String serviceIp = "";

	private static Context context;

	private static final String URL_SHORTEN_API_KEY = "R_e453df42aeacd20e7fe5fc1b2ba515c4";

	private static final String url = "http://api.bit.ly/v3/";

	private static final String login = "cnanhuioss";

	private static final String format = "xml";

	// -----------------------------------------------------------------------------------
	/**
	 * Update Status with upload image and gps location
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static CommResult uploadImage(Map map) {

		System.gc();

		ArrayList<String> filePath = (ArrayList<String>) map.get("filePath");
		String message = map.containsKey("status") ? (String) map.get("status")
				: "";
		String importantLevel = map.containsKey("important_level") ? (String) map
				.get("important_level") : "2";
		String latutude = (String) map.get("geo_latitude");
		String longitude = (String) map.get("geo_longitude");
		// String replyId = (map.containsKey("reply_id") ? (String)
		// map.get("reply_id") : "");
		// String qtId = map.containsKey("message_id") ? (String) map
		// .get("message_id") : "";
		boolean isDefaultCommunication = !map.containsKey("service");

		CommResult result = new CommResult();
		try {
			URL url = null;
			if (map.containsKey("retweet")) {
				url = new URL("http://"
						+ (isDefaultCommunication ? serviceIp
								: (String) map.get("apiUrl"))
						+ "/cfb_api/tweets/forward");
				message = (String) map.get("comment");
			} else {
				url = new URL("http://"
						+ (isDefaultCommunication ? serviceIp
								: (String) map.get("apiUrl"))
						+ "/cfb_api/tweets/update");
			}

			// create a boundary string
			String boundary = MultiPartFormOutputStream.createBoundary();
			HttpURLConnection connection = MultiPartFormOutputStream
					.createConnection(url);
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));

			// Basic Auth
			byte[] token = ((isDefaultCommunication ? currentUserName + ":"
					+ currentUserPassword : (String) map.get("user_name") + ":"
					+ (String) map.get("user_password"))).getBytes("utf-8");
			String check = "Basic " + new String(Base64.encode(token), "utf-8");
			connection.setRequestProperty("Authorization", check);

			// set some other request headers...
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");

			// Put Parameters
			MultiPartFormOutputStream out = new MultiPartFormOutputStream(
					connection.getOutputStream(), boundary);
			// upload a file
			for (int i = 0; i < filePath.size(); i++) {
				File file = new File(filePath.get(i));
				String path = filePath.get(i);
				out.writeFile("local_resource" + String.valueOf(i + 1),
						"text/plain", new File(path));

				out.writeField("file_name"+ String.valueOf(i + 1), file.getName().toString());
			}
			// put params
			out.writeField("text", message);
			out.writeField("important_level", importantLevel);
			out.writeField("gps_latitude", latutude);
			out.writeField("gps_longitude", longitude);
			if (map.containsKey("retweet")) {
				out.writeField("message_id",
						String.valueOf(map.get("message_id")));
				out.writeField("is_comment",
						String.valueOf(map.get("is_comment")));
			}
			// out.writeField("reply_id", replyId);
			// out.writeField("qt_id", qtId);

			out.close();
			// Set Result
			int code = connection.getResponseCode();

			result.setResponseCode(String.valueOf(code));

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.gc();

		return result;
	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getShortUrl(Map map) {

		CommResult result = new CommResult();
		String longUrl = (String) map.get("longUrl");
		HttpURLConnection request = null;
		try {

			String encodedUrl = URLEncoder.encode(longUrl, "UTF-8");

			// URL commUrl = new URL(url + "shorten?login=" + login + "&apiKey="
			// + URL_SHORTEN_API_KEY + "&longUrl=" + encodedUrl
			// + "&format=" + format);

			URL commUrl = new URL(
					"http://nie.im/?format=simple&action=shorturl&url="
							+ encodedUrl);
			request = (HttpURLConnection) commUrl.openConnection();
			result.setResponseCode(String.valueOf(request.getResponseCode()));
			result.setMessage(InputStreamToString(request.getInputStream()));

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			request.disconnect();
			return result;
		}

	}

	// private static String parseXML(String msg){
	// InputStream is = new ByteArrayInputStream(msg.getBytes());
	//
	// String newUrl = null;
	//
	// try{
	//
	// // Using Pull Parser
	// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	//
	// factory.setNamespaceAware(true);
	//
	// XmlPullParser xmlPullParser = factory.newPullParser();
	//
	// xmlPullParser.setInput(is, "UTF-8");
	//
	// while(xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
	//
	// if(xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
	//
	// if(xmlPullParser.getName().equals("url")) {
	//
	// xmlPullParser.next();
	// newUrl = xmlPullParser.getText();
	// return newUrl;
	// }
	// }
	// }
	//
	// return newUrl;
	//
	//
	// }catch(Exception e){
	// return newUrl;
	// }
	//
	// }

	public static void setAppContext(Context ctx) {
		context = ctx;
	}

	// -----------------------------------------------------------------------------------------------
	/**
	 * Set Account Info for this Handler. Generally Called after Login Process.
	 */
	// -----------------------------------------------------------------------------------------------
	public static void setAccount(String userName, String password,
			String apiUrl) {

		CfbCommHandler.currentUserName = userName;
		CfbCommHandler.currentUserPassword = password;
		CfbCommHandler.serviceIp = apiUrl;
//		 CfbCommHandler.serviceIp = "192.168.1.185:3007";

	}

	// -----------------------------------------------------------------------------------------------
	/**
	 * Verify Account
	 */
	// -----------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static CommResult valifyUser(Map map) {
		return CfbCommHandler.getUserInfoForValify(map);
	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static synchronized CommResult getHomeTimeline(Map map) {

		// Prepare Result
		CommResult result = new CommResult();

		// Prepare URL
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/tweets/home_timeline?count=20&page=%s",
				map.get("page"));
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;
		}

	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static synchronized CommResult getAtMessageTimeline(Map map) {

		// Prepare Result
		CommResult result = new CommResult();

		// String url = String.format("http://" + serviceIp +
		// "/api/my_at_timeline?page=%s", map.get("page"));
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/tweets/at_timeline?count=20&page=%s",
				map.get("page"));
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;
		}
	}

	@SuppressWarnings({ "unchecked", "finally" })
	public static CommResult getDirectMessageSend(Map map) {

		// Prepare Result
		CommResult result = new CommResult();

		// URL
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/dm/direct_message_sent?page=%s", map.get("page"));

		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;
		}

	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static synchronized CommResult getDirectMessageReceive(Map map) {

		// Prepare Result
		CommResult result = new CommResult();

		// URL
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/dm/direct_message_received?page=%s",
				map.get("page"));

		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;
		}

	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getMyTimeline(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		// String url = String.format("http://" + serviceIp +
		// "/api/user_timeline?user_id=%s&page=%s", map.get("uid"),
		// map.get("page"));
		String url = String
				.format("http://"
						+ serviceIp
						+ "/cfb_api/tweets/user_timeline?count=20&screen_name=%s&page=%s",
						map.get("screen_name"), map.get("page"));
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Update Status"http://" + serviceIp + "/API_General_Crowdroid/"
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static CommResult updateStatus(Map map) {

		CommResult result = new CommResult();
		// String url = "http://" + serviceIp + "/api/update_status";
		String url = "http://" + serviceIp + "/cfb_api/tweets/update";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (map.containsKey("status")) {
			String str = map.get("status").toString();
			if (str != "") {
				int count = 0;
				int first = 0;
				int second = 0;
				Pattern p = Pattern.compile("#");
				Matcher m = p.matcher(str);
				while (m.find()) {
					count++;
				}
				if (count >= 2) {
					for (int i = 0; i < str.length(); i++) {
						if (str.charAt(i) == '#') {
							first = i;
							break;
						}
					}
					for (int i = first + 1; i < str.length(); i++) {
						if (str.charAt(i) == '#') {
							second = i;
							break;
						}
					}
					str = str.substring(0, first)
					// + "%23"
							+ URLEncoder
									.encode(str.substring(first, first + 1))
							+ str.substring(first + 1, second)
							// + "%23"
							+ URLEncoder.encode(str.substring(second,
									second + 1)) + str.substring(second + 1);
				}

				paramMap.put("text", str);
			} else {
				paramMap.put("text", "");
			}
		}

		if (map.containsKey("geo_latitude")) {
			double lat = Double.parseDouble(map.get("geo_latitude").toString());
			double lon = Double
					.parseDouble(map.get("geo_longitude").toString());
			paramMap.put("gps_latitude", String.valueOf(lat));
			paramMap.put("gps_longitude", String.valueOf(lon));

		}

		// String gps = parseGPS(lon,lat,lat,lon);
		paramMap.put(
				"important_level",
				map.containsKey("important_level") ? (String) map
						.get("important_level") : "2");
		// paramMap.put("reply_id", map.containsKey("reply_id") ? (String)
		// map.get("reply_id") : "");
		// paramMap.put("qt_id", map.containsKey("message_id") ? (String)
		// map.get("message_id") : "");

		try {

			if (map.containsKey("service")) {
				String name = (String) map.get("user_name");
				String password = (String) map.get("user_password");
				String ip = (String) map.get("apiUrl");
				url = "http://" + ip + "/cfb_api/tweets/update";
				result = httpPost(url, (HashMap<String, String>) paramMap,
						name, password);
			} else {
				result = httpPost(url, (HashMap<String, String>) paramMap,
						currentUserName, currentUserPassword);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Destroy
	 * 
	 * @param messageId
	 *            the id of the message that you selected.
	 * @param accessToken
	 *            the user's accessToken.
	 * @param accessSecret
	 *            the user's tokenSecret.
	 * @return int
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getFindPeopleInfo(Map map) {

		CommResult result = new CommResult();
		// String url = String.format("http://" + serviceIp +
		// "/api/user_search?keyword=%s&page=%s", map .get("query"),
		// map.get("page"));
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/search/user?keyword=%s&page=%s", map.get("query"),
				map.get("page"));

		try {
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	// -----------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public static CommResult retweet(Map map) {
		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();

		String gps_latitude = "";
		String gps_longitude = "";
		if (map.containsKey("geo_longitude") || map.containsKey("geo_latutude")) {
			gps_latitude = (String) map.get("geo_latitude");
			gps_longitude = (String) map.get("geo_longitude");
		}
		String url = "";
		String commentIsCheck = String.valueOf(map.get("is_comment"));
		url = String
				.format("http://"
						+ serviceIp
						+ "/cfb_api/tweets/forward?message_id=%s&text=%s&is_comment=%s&gps_latitude=%s&gps_longitude=%s",
						map.get("message_id"),
						URLEncoder.encode((String) map.get("comment")),
						Integer.valueOf(commentIsCheck), gps_latitude,
						gps_longitude);

		try {
			result = httpPost(url, parameters, currentUserName,
					currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult updateComments(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();

		parameters.put("text", map.get("comment").toString());

		String url;
		try {
			url = String.format("http://" + serviceIp
					+ "/cfb_api/tweets/comment?tweet_id=%s",
					map.get("message_id"));
			result = httpPost(url, parameters, currentUserName,
					currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getCommentsById(Map map) {

		CommResult result = new CommResult();

		String url = String.format("http://" + serviceIp
				+ "/cfb_api/comments/view?page=%s&count=20&status_id=%s",
				map.get("page"), map.get("message_id"));

		try {
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getCommentsTimeine(Map map) {

		CommResult result = new CommResult();

		String url = String.format("http://" + serviceIp
				+ "/cfb_api/comments/comments_received?page=%s&count=20",
				map.get("page"));

		if (currentUserName.equals("")) {
			result.setMessage(null);
			result.setResponseCode(null);
			return result;
		}
		try {
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getCommentById(Map map) {

		CommResult result = new CommResult();

		String url = String.format("http://" + serviceIp
				+ "/cfb_api/tweets/show_status?id=%s", map.get("id"));

		try {
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get User Info
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getUserInfo(Map map) {

		CommResult result = new CommResult();
		// String url = String.format( "http://" + serviceIp +
		// "/api/user_profile?user_id=%s", map.get("uid"));
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/users/show?user_id=%s", map.get("uid"));
		try {
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get User Info
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getUserInfoForValify(Map map) {

		CommResult result = new CommResult();
		String url = String.format("http://%s/cfb_api/vu/verify_user",
				map.get("apiUrl"));
		try {
			result = httpGet(url, (String) map.get("name"),
					(String) map.get("password"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (result != null && result.getMessage() == null) {
					JSONObject jObject = new JSONObject(result.getMessage());
				}
			} catch (Exception e) {
				result.setResponseCode("401");
			}
			if (result.getMessage() == null || result.getResponseCode() == null) {
				result.setResponseCode("401");
			}
			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Destroy
	 * 
	 * @param sendTo
	 *            sent to the user's id
	 * @param locationArray
	 *            the user's location
	 * @param messageId
	 *            the id of the message that you selected.
	 * @param userName
	 *            the user's userName.
	 * @param userPassword
	 *            the user's userPassword.
	 * @param message
	 *            the direct message
	 * @return CommunicationHandlerResult
	 */

	@SuppressWarnings("unchecked")
	public static CommResult directMessage(Map map) {

		CommResult result = new CommResult();
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("to_user_id", (String) map.get("uid"));
		parameters.put("text", (String) map.get("message"));
		// String url = "http://" + serviceIp + "/api/update_direct_message";
		String url = "http://" + serviceIp + "/cfb_api/dm/update";
		try {
			result = httpPost(url, parameters, currentUserName,
					currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get FriendList.
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getUserList(Map map) {

		CommResult result = new CommResult();
		// String url = String.format("http://" + serviceIp +
		// "/api/user_search?page=%s", map .get("page"));
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/search/user?page=%s", map.get("page"));

		try {
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Destroy
	 * 
	 * @param messageId
	 *            the id of the message that you selected.
	 * @param accessToken
	 *            the user's accessToken.
	 * @param accessSecret
	 *            the user's tokenSecret.
	 * @return int
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult searchInfo(Map map) {

		CommResult result = new CommResult();
		String keyword = (String) map.get("search");
		String page = (String) map.get("page");
		// String url = "http://" + serviceIp + "/api/";
		String url = "http://" + serviceIp;
		if (keyword.startsWith("#")) {
			url = String.format(url + "hash_tag_search?tag_name=%s&page=%s",
					keyword.substring(1, keyword.length()), page);
		} else {
			url = String.format(url
					+ "/cfb_api/search/keyword?keyword=%s&page=%s", keyword,
					page);
		}

		try {
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get USER Status List
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getUserStatusList(Map map) {

		// Prepare result
		CommResult result = new CommResult();

		// URL
		String url = String
				.format("http://"
						+ serviceIp
						+ "/cfb_api/tweets/user_timeline?screen_name=%s&page=%s&count=20",
						map.get("screen_name"), map.get("page"));
		// String url = String.format("http://" + serviceIp +
		// "/cfb_api/tweets/user_timeline?page=%s&count=20&user_id=%s",map.get("page"),map.get("uid"));
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;
		}

	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult destroyStatus(Map map) {

		CommResult result = new CommResult();
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("message_id", (String) map.get("message_id"));
		// String url = String.format("http://" + serviceIp +
		// "/api/delete_status");
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/tweets/delete");
		try {
			result = httpPost(url, paramMap, currentUserName,
					currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	@SuppressWarnings("unchecked")
	public static CommResult getCFBsetting(Map map) {

		CommResult result = new CommResult();
		String name = (String) map.get("name");
		String password = (String) map.get("password");
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/list/setting_list");
		try {
			result = httpGet(url, name, password);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult setFavorite(Map map) {

		CommResult result = new CommResult();

		String url = "";
		//
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (map.get("type").equals("create")) {
			paramMap.put("id", (String) map.get("message_id"));
			url = String.format("http://" + serviceIp
					+ "/cfb_api/tweets/collection");
		} else {
			paramMap.put("status_id", (String) map.get("message_id"));
			url = String.format("http://" + serviceIp
					+ "/cfb_api/favorite/delete");
		}
		try {
			result = httpPost(url, paramMap, currentUserName,
					currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult replyToComment(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parmeters = new HashMap<String, String>();
		try {
			parmeters.put("text",
					URLEncoder.encode((String) map.get("comment"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String url = String.format("http://" + serviceIp
				+ "/cfb_api/tweets/comment?tweet_id=%s", map.get("cid"));

		try {
			result = httpPost(url, parmeters, currentUserName,
					currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("finally")
	public static CommResult getFavoriteList(Map map) {

		// Prepare result
		CommResult result = new CommResult();
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/favorite/view?page=%s&count=20&user_id=%s",
				map.get("page"), map.get("uid"));
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	@SuppressWarnings("finally")
	public static CommResult getTrendsByType(Map map) {

		// Prepare result
		CommResult result = new CommResult();
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/tweets/get_trends");
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	@SuppressWarnings("finally")
	public static CommResult getEmotions(Map map) {

		// Prepare result
		CommResult result = new CommResult();
		String url = String.format("http://" + serviceIp
				+ "/cfb_api/tweets/get_expressions");
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	// =============================================================================
	@SuppressWarnings("unchecked")
	public static CommResult getVersionMessage(Map map) {

		CommResult result = new CommResult();
		String url = String
				.format("http://www.anhuioss.com/download/files/backgrounds/update-versions.json");
		try {
			result = httpGet(url, "", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult getThemeMessage(Map map) {

		CommResult result = new CommResult();
		String url = String
				.format("http://www.anhuioss.com/download/files/backgrounds/update-theme.json");
		try {
			result = httpGet(url, "", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// get Notification broadcast messgage
	public static CommResult getBroadCastMessage(String httpUrl) {
		CommResult result = new CommResult();
		String url = String.format(httpUrl);
		try {
			result = httpGet(url, "", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("finally")
	public static synchronized CommResult getScheduleType(Map map) {

		// Prepare Result
		CommResult result = new CommResult();

		String url = String.format("http://" + serviceIp
				+ "/cfb_api/calendars/calendar_category");
		// .format("http://192.168.1.140:3001/cfb_api/calendars/calendar_category");
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result

			return result;

		}
	}

	@SuppressWarnings("finally")
	public static synchronized CommResult getScheduleUser(Map map) {

		// Prepare Result
		CommResult result = new CommResult();

		String url = String.format("http://" + serviceIp
				+ "/cfb_api/calendars/users");
		// .format("http://192.168.1.140:3001/cfb_api/calendars/users");
		try {
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;

		}
	}

	public static CommResult updateScadule(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();

		parameters.put("calendartitle", map.get("calendartitle").toString());// 标题
		parameters.put("participants", map.get("participants").toString());// 参与者
		parameters.put("calendartype", map.get("calendartype").toString());// 类别
		parameters
				.put("calendarcontent", map.get("calendarcontent").toString());// 描述
		parameters.put("startdate", map.get("startdate").toString());// 开始时间
		parameters.put("starthour", map.get("starthour").toString());
		parameters.put("startminute", map.get("startminute").toString());
		parameters.put("enddate", map.get("enddate").toString());// 结束时间
		parameters.put("endthour", map.get("endthour").toString());
		parameters.put("endminute", map.get("endminute").toString());

		String url;
		try {
			url = String.format("http://" + serviceIp
					+ "/cfb_api/calendars/add_calendar");
			// .format("http://192.168.1.140:3001/cfb_api/calendars/add_calendar");
			result = httpPost(url, parameters, currentUserName,
					currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("finally")
	public static synchronized CommResult getMonthSchedule(Map map) {

		// Prepare Result
		CommResult result = new CommResult();
		try {

			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/calendars/show_calendars?user_id=%s&startdate=%s&enddate=%s",
							// .format("http://192.168.1.140:3001/cfb_api/calendars/show_calendars?user_id=%s&startdate=%s&enddate=%s",
							map.get("userId"), map.get("startDate"),
							map.get("endDate"));
			// HTTP Communication
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Return Result
			return result;

		}

	}

	public static CommResult getAlbums(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/show_albums");
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult getAlbumPhotos(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/show_pictures?album_id=%s",
					map.get("id"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult getAlbumDocuments(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/show_documents?album_id=%s",
					map.get("id"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult getAlbumVideos(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/show_videos?album_id=%s",
					map.get("id"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult setAlbumCover(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/set_album_cover?picture_id=%s",
					map.get("id"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult uploadPhoto(Map map) {

		String message = map.containsKey("description") ? (String) map
				.get("description") : "";
		String album_id = map.containsKey("id") ? (String) map.get("id") : "";

		String filePath = (String) map.get("filePath");

		CommResult result = new CommResult();

		try {
			URL url = new URL("http://" + serviceIp
					+ "/cfb_api/files_center/upload_picture");
			// create a boundary string
			String boundary = MultiPartFormOutputStream.createBoundary();
			HttpURLConnection connection;

			connection = MultiPartFormOutputStream.createConnection(url);

			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));

			// Basic Auth
			byte[] token = (currentUserName + ":" + currentUserPassword)
					.getBytes("utf-8");
			String check = "Basic " + new String(Base64.encode(token), "utf-8");
			connection.setRequestProperty("Authorization", check);

			// set some other request headers...
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");

			// Put Parameters
			MultiPartFormOutputStream out = new MultiPartFormOutputStream(
					connection.getOutputStream(), boundary);
			// upload a file
			File file = new File(filePath);
			out.writeFile("local_img", "text/plain", file);

			// put params
			out.writeField("description", message);
			out.writeField("album_id", album_id);
			out.writeField("file_name", file.getName().toString());

			out.close();
			// Set Result
			int code = connection.getResponseCode();
			Log.e("code", String.valueOf(code));
			result.setResponseCode(String.valueOf(code));
			result.setMessage(connection.getResponseMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult uploadVideo(Map map) {

		CommResult result = new CommResult();
		// String url = "http://" + serviceIp + "/api/update_status";
		String url = "http://" + serviceIp + "/cfb_api/files_center/upload_video";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(
				"important_level",
				map.containsKey("important_level") ? (String) map
						.get("important_level") : "2");
		// paramMap.put("reply_id", map.containsKey("reply_id") ? (String)
		// map.get("reply_id") : "");
		// paramMap.put("qt_id", map.containsKey("message_id") ? (String)
		// map.get("message_id") : "");
		paramMap.put("album_id", (String) map.get("id"));
		paramMap.put("description", (String) map.get("description"));
		paramMap.put("videourl", (String) map.get("url"));

		try {

			if (map.containsKey("service")) {
				String name = (String) map.get("user_name");
				String password = (String) map.get("user_password");
				String ip = (String) map.get("apiUrl");
				url = "http://" + ip + "/cfb_api/tweets/update";
				result = httpPost(url, (HashMap<String, String>) paramMap,
						name, password);
			} else {
				result = httpPost(url, (HashMap<String, String>) paramMap,
						currentUserName, currentUserPassword);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static CommResult uploadDocument(Map map) {

		String message = map.containsKey("description") ? (String) map
				.get("description") : "";
		String album_id = map.containsKey("id") ? (String) map.get("id") : "";

		String filePath = (String) map.get("filePath");

		CommResult result = new CommResult();

		try {
			URL url = new URL("http://" + serviceIp
					+ "/cfb_api/files_center/upload_document");
			// create a boundary string
			String boundary = MultiPartFormOutputStream.createBoundary();
			HttpURLConnection connection;

			connection = MultiPartFormOutputStream.createConnection(url);

			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));

			// Basic Auth
			byte[] token = (currentUserName + ":" + currentUserPassword)
					.getBytes("utf-8");
			String check = "Basic " + new String(Base64.encode(token), "utf-8");
			connection.setRequestProperty("Authorization", check);

			// set some other request headers...
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");

			// Put Parameters
			MultiPartFormOutputStream out = new MultiPartFormOutputStream(
					connection.getOutputStream(), boundary);
			// upload a file
			File file = new File(filePath);
			out.writeFile("local_file", "text/plain", file);

			// put params
			out.writeField("description", message);
			out.writeField("album_id", album_id);
			out.writeField("file_name", file.getName().toString());

			out.close();
			// Set Result
			int code = connection.getResponseCode();
			Log.e("code", String.valueOf(code));
			result.setResponseCode(String.valueOf(code));
			result.setMessage(connection.getResponseMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult commentPhotos(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/files_center/comment_picture?picture_id=%s&content=%s&recipient_id=%s&flag=%s",
							map.get("id"), map.get("comment"),
							map.get("user_id"), map.get("flag"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult commentDocuments(Map map) {
		CommResult result = new CommResult();
		String url = String
				.format("http://"
						+ serviceIp
						+ "/cfb_api/files_center/comment_document?document_id=%s&content=%s&recipient_id=%s&flag=%s",
						map.get("id"), map.get("comment"), map.get("user_id"),
						map.get("flag"));

		try {

			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult commentVideos(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/files_center/comment_video?video_id=%s&content=%s&recipient_id=%s&flag=%s",
							map.get("id"), map.get("comment"),
							map.get("user_id"), map.get("flag"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult photoCommentList(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/get_pic_comments?picture_id=%s",
					map.get("pid"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult documentCommentList(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/get_doc_comments?document_id=%s",
					map.get("pid"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult videoCommentList(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/get_video_comments?video_id=%s",
					map.get("pid"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult createAlbum(Map map) {
		CommResult result = new CommResult();
		String name = (String) map.get("album_name");
		String des = (String) map.get("album_description");

//		byte[] token;
//		try {
//			token = (name).getBytes("utf-8");
//			name = new String(Base64.encode(token), "utf-8");
//			token = (des).getBytes("utf-8");
//			des = new String(Base64.encode(token), "utf-8");
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		try {
			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/files_center/create_album?album_name=%s&album_description=%s&album_mark=%s",
							URLEncoder
							.encode(name), URLEncoder
							.encode(des), map.get("album_mark"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult delAlbum(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/delete_album?album_id=%s",
					map.get("album_id"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult delAlbumPhoto(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/files_center/delete_files?album_id=%s&file_id=%s",
							map.get("albumId"), map.get("fileId"));

			// String url = String
			// .format("http://" +"192.168.1.101:3007"
			// + "/cfb_api/files_center/delete_files?album_id=%s&file_id=%s",
			// map.get("album_id"),map.get("file_id"));
			// result =
			// httpGet("http://exp.crowdroid.com/cfb_api/files_center/delete_files?album_id=13&file_id=66",
			// currentUserName, currentUserPassword);
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult tweetPhoto(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/files_center/picture_rt?picture_id=%s&content=%s",
							map.get("id"), map.get("content"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult tweetDocument(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/files_center/document_rt?document_id=%s&content=%s",
							map.get("id"), map.get("content"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult tweetVideo(Map map) {
		CommResult result = new CommResult();

		try {
			String url = String.format("http://" + serviceIp
					+ "/cfb_api/files_center/video_rt?video_id=%s&content=%s",
					map.get("id"), map.get("content"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}

	public static CommResult getDetailMsg(Map map) {
		CommResult result = new CommResult();

		try {
			// String url = String.format("http://" + serviceIp
			// + "/cfb_api/files_center/video_rt?video_id=%s&content=%s",
			// map.get("id"), map.get("content"));
			// result = httpGet(url, currentUserName, currentUserPassword);

			// http://192.168.1.174:3007/cfb_api/files_center/show_message?album_id=3&message_id=120

			String url = String
					.format("http://"
							+ serviceIp
							+ "/cfb_api/files_center/show_message?album_id=%s&message_id=%s",
							map.get("album_id"), map.get("id"));
			result = httpGet(url, currentUserName, currentUserPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}

		return result;

	}
}