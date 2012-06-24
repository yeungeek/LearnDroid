package com.anhuioss.crowdroid.service.twitterproxy;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.service.follow5.Base64;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

public class TwitterProxyCommHandler extends HttpCommunicator {

	/** API Server Address */
	private static String apiServer = "http://api.follow5.com/api";

	/** Account */
	public static String userName;

	/** Password */
	public static String userPassword;

	/** UserId **/
	// public static string userId;

	/** listParameterMap **/
	public static Map listParameterMap;

	private static Context mContext;

	// -----------------------------------------------------------------------------------------------
	/**
	 * Set Account Info for this Handler. Generally Called after Login Process.
	 */
	// -----------------------------------------------------------------------------------------------
	public static void setAccount(String userName, String password,
			String apiUrl) {

		TwitterProxyCommHandler.apiServer = apiUrl;
		TwitterProxyCommHandler.userName = userName;
		TwitterProxyCommHandler.userPassword = password;

	}

	public static void setListParameter(Map<String, String> map) {
		TwitterProxyCommHandler.listParameterMap = map;
	}

	public static void setAppContext(Context context) {
		mContext = context;
	}

	// -----------------------------------------------------------------------------------------------
	/**
	 * Verify Account
	 */
	// -----------------------------------------------------------------------------------------------
	public static CommResult verifyUser(Map map) {

		CommResult result = new CommResult();

		// Prepare Parameter For This Request From Map
		String apiProxyIp = (String) map.get("apiProxyIp");
		String name = (String) map.get("name");
		String password = (String) map.get("password");
		String url = String.format("%s/account/verify_credentials.json",
				apiProxyIp);

		// result = httpGet(url, name, password);
		result = httpGetBasic(url, name, password);

		return result;

	}

	// home timeline
	@SuppressWarnings("rawtypes")
	public static synchronized CommResult getHomeTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/home_timeline.json?count=20&page=%s", apiServer,
				map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	// my timeline
	@SuppressWarnings("rawtypes")
	public static CommResult getMyTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/user_timeline.json?count=20&page=%s", apiServer,
				map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	// Received directMessage
	@SuppressWarnings("rawtypes")
	public static synchronized CommResult getDirectMessage(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/direct_messages.json?count=21&page=%s",
				apiServer, map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	// directMessageSend
	@SuppressWarnings("rawtypes")
	public static CommResult getDirectMessageSent(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/direct_messages/sent.json?count=21&page=%s", apiServer,
				map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	public static synchronized CommResult getPublicTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("%s/statuses/public_timeline.json?include_entities=true&count=21&page=%s",
						apiServer, map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	// updateStatus
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static CommResult updateStatus(Map map) {

		CommResult result = new CommResult();
		String url = "";

		String name = (String) map.get("user_name");
		String password = (String) map.get("user_password");
		String apiUrl = (String) map.get("apiUrl");
		String service = (String) map.get("service");
		try {
			if (service == null) {
				url = String.format("%s/statuses/update.json", apiServer);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("status",
						URLEncoder.encode((String) map.get("status"), "UTF-8"));
				result = httpPostBasic(url, params, userName, userPassword);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				url = String.format("%s/statuses/update.json", apiUrl);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("status",
						URLEncoder.encode((String) map.get("status"), "UTF-8"));
				result = httpPostBasic(url, params, name, password);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return result;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * upload image with Account
	 * 
	 * @return CommunicationHandlerResult
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static CommResult uploadImage(Map map) {

		CommResult result = new CommResult();
		String imagePath = (String) map.get("filePath");

		FileInputStream fis = null;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {

			// Read Image File To byteArrayOutputStream
			fis = new FileInputStream(imagePath);
			int n = 0;
			byte[] buf = new byte[1024];
			while ((n = fis.read(buf, 0, 1024)) > -1) {
				byteArrayOutputStream.write(buf, 0, n);
			}
			fis.close();

			String data = Base64.encodeToString(
					byteArrayOutputStream.toByteArray(), false);

			HttpPost hpost = new HttpPost(
					"http://api.imgur.com/2/account/images.json");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("image", data));
			nameValuePairs.add(new BasicNameValuePair("type", "base64"));

			hpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(
					"0529305aea0b1517fd2c5a21bc38834604eaf9b0d",
					"575c2dbfd2c8af5c65eff58b03b057f8");
			consumer.setTokenWithSecret(IGeneral.IMGUR_ACCESS_TOKEN,
					IGeneral.IMGUR_TOKEN_SECRET);

			consumer.sign(hpost);

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse resp = client.execute(hpost);

			// Parse Image Url
			String imageUrl = TwitterProxyParseHandler
					.parseUploadImage(EntityUtils.toString(resp.getEntity()));

			// Update Status With Image Url
			if (imageUrl != null) {
				String status = (String) map.get("status") + " " + imageUrl;
				map.put("status", status);
				result = updateStatus(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;

	}

	// @Message
	@SuppressWarnings("rawtypes")
	public static synchronized CommResult getAtMessage(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/mentions.json?count=21&page=%s", apiServer,
				map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	// findPeopleInfo
	@SuppressWarnings("rawtypes")
	public static CommResult getFindPeopleInfo(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/users/search.json?q=%s&page=%s",
				apiServer, map.get("query"), map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// searchInfo
	@SuppressWarnings("rawtypes")
	public static CommResult searchInfo(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/search.json?callback=?&q=%s&page=%s",
				apiServer, map.get("search"), map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// userSatusList
	@SuppressWarnings("rawtypes")
	public static CommResult getUserStatusList(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/user_timeline.json?screen_name=%s&page=%s",
				apiServer, map.get("screen_name"), map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// send directMessage
	@SuppressWarnings("rawtypes")
	public static CommResult directMessage(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("text", (String) map.get("message"));

		String url = String.format(
				"%s/direct_messages/new.json?screen_name=%s", apiServer,
				map.get("send_to"));

		result = httpPostBasic(url, parameters, userName, userPassword);

		return result;
	}

	// FollowersList
	@SuppressWarnings("rawtypes")
	public static CommResult getFollowersList(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/followers.json?screen_name=%s&cursor=%s",
				apiServer, map.get("screen_name"), map.get("cursor"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// FriendsList
	@SuppressWarnings("rawtypes")
	public static CommResult getFriendsList(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/friends.json?screen_name=%s&cursor=%s", apiServer,
				map.get("screen_name"), map.get("cursor"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// FavoriteList
	@SuppressWarnings("rawtypes")
	public static CommResult getFavoriteList(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/favorites.json?count=20&page=%s",
				apiServer, map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// userInfo
	@SuppressWarnings("rawtypes")
	public static CommResult getUserInfo(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/users/show.json?screen_name=%s",
				apiServer, map.get("screen_name"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// showRelation
	@SuppressWarnings("rawtypes")
	public static CommResult showRelation(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/friendships/show.json?target_screen_name=%s", apiServer,
				map.get("screen_name"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	// setFollow
	@SuppressWarnings("rawtypes")
	public static CommResult setFollow(Map map) {

		CommResult result = new CommResult();

		String url = "";

		if (map.get("type").equals("create")) {
			url = String.format("%s/friendships/create/%s.json", apiServer,
					map.get("id"));
		} else {
			url = String.format("%s/friendships/destroy/%s.json", apiServer,
					map.get("id"));
		}

		result = httpPostBasic(url, new HashMap<String, String>(), userName,
				userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult retweet(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/statuses/retweet/%s.json", apiServer,
				map.get("message_id"));

		result = httpPostBasic(url, new HashMap<String, String>(), userName,
				userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult setFavorite(Map map) {

		CommResult result = new CommResult();

		String url = "";

		if (map.get("type").equals("create")) {
			url = String.format("%s/favorites/create/%s.json", apiServer,
					map.get("message_id"));
		} else {
			url = String.format("%s/favorites/destroy/%s.json", apiServer,
					map.get("message_id"));
		}

		result = httpPostBasic(url, new HashMap<String, String>(), userName,
				userPassword);

		return result;

	}

	// myLists
	@SuppressWarnings({ "rawtypes" })
	public static CommResult getMyLists(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/lists/memberships.json?screen_name=%s&cursor=%s",
				apiServer, map.get("screenName"),
				String.valueOf(map.get("cursor")));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult getFollowLists(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/%s/lists/subscriptions.json", apiServer,
				map.get("screenName"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult getListTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/%s/lists/%s/statuses.json?page=%s",
				apiServer, listParameterMap.get("user"),
				listParameterMap.get("id"), map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult getRetweetToMeTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/retweeted_to_me.json?count=21&page=%s", apiServer,
				map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult getRetweetByMeTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/retweeted_by_me.json?count=21&page=%s", apiServer,
				map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult getRetweetOfMeTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"%s/statuses/retweets_of_me.json?count=21&page=%s", apiServer,
				map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	@SuppressWarnings({ "rawtypes" })
	public static CommResult getMessageById(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/statuses/show/%s.json?", apiServer,
				map.get("messageId"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	@SuppressWarnings("rawtypes")
	public static CommResult destroyStatus(Map map) {
		// Request
		CommResult result = new CommResult();

		String url = String.format("%s/statuses/destroy/%s.json?", apiServer,
				map.get("message_id"));
		result = httpPostBasic(url, new HashMap<String, String>(), userName,
				userPassword);

		return result;
	}

	// @SuppressWarnings({ "finally", "rawtypes" })
	// public static CommResult uploadImage(Map map) {
	//
	// String filePath = (String) map.get("filePath");
	//
	// CommResult result = new CommResult();
	//
	// try {
	// String u = String.format("%s/upload.json", apiServer);
	// URL url = new URL(u);
	//
	// // create a boundary string
	// String boundary = MultiPartFormOutputStream.createBoundary();
	// HttpURLConnection connection = MultiPartFormOutputStream
	// .createConnection(url);
	// connection.setRequestProperty("Accept", "*/*");
	// connection.setRequestProperty("Content-Type",
	// MultiPartFormOutputStream.getContentType(boundary));
	//
	// // set some other request headers...
	// connection.setRequestProperty("Connection", "Keep-Alive");
	// connection.setRequestProperty("Cache-Control", "no-cache");
	//
	// byte[] token = (userName + ":" + userPassword).getBytes("utf-8");
	// String check = "Basic " + new String(Base64.encode(token), "utf-8");
	// connection.setRequestProperty("Authorization", check);
	//
	//
	// // Put Parameters
	// MultiPartFormOutputStream out = new MultiPartFormOutputStream(
	// connection.getOutputStream(), boundary);
	//
	//
	// // out.writeField("oauth_token", userName);
	// // out.writeField("oauth_secret", userPassword);
	//
	// // upload a file
	// out.writeFile("media", "text/plain", new File(filePath));
	// out.close();
	//
	// // Set Result
	// int code = connection.getResponseCode();
	// // get image url xml file
	// InputStream in = connection.getInputStream();
	//
	// result.setResponseCode(String.valueOf(code));
	//
	// result.setMessage(InputStreamToString(in));
	//
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// return result;
	// }
	// }

	@SuppressWarnings("unchecked")
	public static CommResult setFollowList(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/%s/%s/subscribers.json", apiServer,
				map.get("user"), map.get("list_id"));

		HashMap<String, String> parameter = new HashMap<String, String>();
		if (map.get("type").equals("true")) {
			parameter.put("_method", "DELETE");
		}

		// result = httpPostOauth(url, parameter, userName, userPassword);
		result = httpPostBasic(url, parameter, userName, userPassword);

		return result;

	}

	public static synchronized CommResult getTrends(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/trends/%s.json?", apiServer,
				map.get("type"));

		result = httpGetBasic(url, userName, userPassword);

		return result;

	}

	public static synchronized CommResult getLocationsAvailiableTrends(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/trends/available.json", apiServer);

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	public static CommResult getGroupListSlugs(Map map) {

		CommResult result = new CommResult();

		String url = String.format("%s/lists/all.json?screen_name=%s",
				apiServer, map.get("screenName"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	public static CommResult getGroupTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("%s/lists/statuses.json?slug=%s&owner_screen_name=%s&page=%s&include_entities=true",
						apiServer, map.get("slug"), map.get("ownerName"),
						map.get("page"));

		result = httpGetBasic(url, userName, userPassword);

		return result;
	}

	public static synchronized CommResult getGroupUserlist(Map map) {
		CommResult result = new CommResult();
		// suggestion user
		String url = String.format(
				"%s/lists/members.json?slug=%s&owner_screen_name=%s&cursor=-1",
				apiServer, map.get("slug"), map.get("screen_name"));
		result = httpGetBasic(url, userName, userPassword);
		return result;

	}

}
