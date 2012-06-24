package com.anhuioss.crowdroid.service.twitter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.dialog.UserSelectDialog;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;
import com.anhuioss.crowdroid.util.MultiPartFormOutputStream;

public class TwitterCommHandler extends HttpCommunicator {

	/** Consumer_Kye for Twitter */
	public static String CONSUMER_KEY = "rbkewUHsJc0pdKMmt3PHQ";

	/** Consumer_Secret for Twitter */
	public static String CONSUMER_SECRET = "qnfmOYFFiqzen67Ww6TxqlaeY45Ct7Cm1hOriyU";

	private static final String TWITPIC_API_KEY = "49b501cfebb227634ae716c03bff6b0c";

	public static final int TYPE_FRIENDS = 0;

	public static final int TYPE_FOLLOWERS = 1;

	public static final String API_SERVER = "http://www.twitlonger.com/api_post";

	private static final String APPLICATION = "crowdroid";

	private static final String API_KEY = "S2Uu24588d33Ii70";

	private static String mService = IGeneral.SERVICE_NAME_TWITTER;

	private static String currentUserName = "";

	private static String currentUserPassword = "";

	private static String currentUid = "";

	public static Map listParameterMap;

	private static Context context;

	// //
	// -----------------------------------------------------------------------------------
	// /**
	// * upload image with oauth
	// *
	// * @return CommunicationHandlerResult
	// */
	// //
	// -----------------------------------------------------------------------------------
	// @SuppressWarnings({ "finally", "unchecked" })
	// public static CommResult uploadImage(Map map) {
	//
	// String filePath = (String) map.get("filePath");
	//
	// CommResult result = new CommResult();
	//
	// try {
	// URL url = new URL("http://api.twitpic.com/1/upload.json");
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
	// // Put Parameters
	// MultiPartFormOutputStream out = new MultiPartFormOutputStream(
	// connection.getOutputStream(), boundary);
	//
	// out.writeField("key", TWITPIC_API_KEY);
	// out.writeField("consumer_token", CONSUMER_KEY);
	// out.writeField("consumer_secret", CONSUMER_SECRET);
	// out.writeField("oauth_token", currentUserName);
	// out.writeField("oauth_secret", currentUserPassword);
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

	// -----------------------------------------------------------------------------------
	/**
	 * upload image with oauth
	 * 
	 * @return CommunicationHandlerResult
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult uploadImage(Map map) {

		String filePath = (String) map.get("filePath");

		CommResult result = new CommResult();

		try {
			URL url = new URL("http://api.twitpic.com/2/upload.json");

			// create a boundary string
			String boundary = MultiPartFormOutputStream.createBoundary();
			HttpURLConnection connection = MultiPartFormOutputStream
					.createConnection(url);
			connection.setConnectTimeout(40000);
			connection.setReadTimeout(10000);
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Content-Type",
					MultiPartFormOutputStream.getContentType(boundary));

			// set some other request headers...
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection
					.setRequestProperty("X-Auth-Service-Provider",
							"https://api.twitter.com/1/account/verify_credentials.json");
			connection.setRequestProperty("X-Verify-Credentials-Authorization",
					XVerifyCredentialsUtil.generateTwitterOAuthEchoCredentials(
							currentUserName, currentUserPassword));

			// Put Parameters
			MultiPartFormOutputStream out = new MultiPartFormOutputStream(
					connection.getOutputStream(), boundary);

			// Key
			out.writeField("key", TWITPIC_API_KEY);

			// upload a file
			out.writeFile("media", "text/plain", new File(filePath));
			out.close();

			// Set Result
			int code = connection.getResponseCode();

			if (code == 200) {
				String imageUrl = TwitterParseHandler
						.parseUploadImage(InputStreamToString(connection
								.getInputStream()));
				String status = (String) map.get("status") + " " + imageUrl;
				map.put("status", status);
				result = updateStatus(map);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	public static void setAppContext(Context ctx) {
		context = ctx;
	}

	// -----------------------------------------------------------------------------------------------
	/**
	 * Set Account Info for this Handler. Generally Called after Login Process.
	 */
	// -----------------------------------------------------------------------------------------------
	public static void setAccount(String userName, String password, String uid) {

		TwitterCommHandler.currentUserName = userName;
		TwitterCommHandler.currentUserPassword = password;
		TwitterCommHandler.currentUid = uid;

	}

	public static void setListParameter(Map map) {
		TwitterCommHandler.listParameterMap = map;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Home Time Line List
	 * 
	 * @param accessToken
	 *            user's accessToken.
	 * @param accessSecret
	 *            user's tokenSecret.
	 */
	public static synchronized CommResult getHomeTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/home_timeline.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	public static synchronized CommResult getPublicTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/public_timeline.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		// String url =
		// String.format("https://stream.twitter.com/1/statuses/sample.json?delimited=20");

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	public static synchronized CommResult getDirectMessage(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/direct_messages.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	public static CommResult getDirectMessageSent(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/direct_messages/sent.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Update Status
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static CommResult updateStatus(Map map) {
		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/update.json");
		String name = (String) map.get("user_name");
		String password = (String) map.get("user_password");
		String service = (String) map.get("service");
		if (service == null) {
			result = httpPostOauth(mService, url,
					(HashMap<String, String>) map, currentUserName,
					currentUserPassword);
		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			result = httpPostOauth(mService, url,
					(HashMap<String, String>) map, name, password);
		}
		return result;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get a My Time Line List
	 * 
	 * @param accessToken
	 *            user's accessToken.
	 * @param accessSecret
	 *            user's tokenSecret.
	 * @return ArrayList
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static CommResult getMyTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/user_timeline.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get a @ Message List
	 * 
	 * @param accessToken
	 *            user's accessToken.
	 * @param accessSecret
	 *            user's tokenSecret.
	 * @return ArrayList
	 */
	public static synchronized CommResult getAtMessage(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/mentions.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get Token and Token Secret.DefaultOAuthConsumer consumer, OAuthProvider
	 * provider, String pin
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings({ "finally" })
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
				provider.retrieveAccessToken(consumer, pin);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// Prepare Message
				String message = consumer.getToken() + ";"
						+ consumer.getTokenSecret();
				result.setMessage(message);
				return result;
			}

		}

	}

	// -----------------------------------------------------------------------------------------------
	/**
	 * Verify Account
	 */
	public static CommResult verifyUser(Map map) {

		// Prepare Parameter For This Request From Map
		String accessToken = (String) map.get("accessToken");
		String tokenSecret = (String) map.get("tokenSecret");
		CommResult result = httpGetOauth(mService,
				"http://api.twitter.com/account/verify_credentials.json",
				accessToken, tokenSecret);
		return result;

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get User List with Keyword.
	 */
	public static CommResult getFindPeopleInfo(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/users/search.json?include_entities=true&q=%s&page=%s",
						map.get("query"), map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Search Time Line contains specified query.
	 */
	public static CommResult searchinfo(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://search.twitter.com/search.json?callback=?&q=%s&page=%s",
						URLEncoder.encode((String) map.get("search")),
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Get user's status' list
	 * 
	 * @param accessToken
	 *            user's accessToken.
	 * @param accessSecret
	 *            user's tokenSecret.
	 * @return ArrayList
	 */
	public static CommResult getUserStatusList(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/user_timeline.json?screen_name=%s&page=%s",
						map.get("screen_name"), map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Direct Update Status
	 */
	public static CommResult directMessage(Map map) {

		CommResult result = new CommResult();

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("text", (String) map.get("message"));
		parameters.put("screen_name", (String) map.get("send_to"));

		String url = String
				.format("http://api.twitter.com/direct_messages/new.json");

		result = httpPostOauth(mService, url, parameters, currentUserName,
				currentUserPassword);

		return result;
	}

	public static CommResult getFollowersList(Map map) {

		CommResult result = new CommResult();

		String ids = FollowedActivity.getIdsForLookup();
		if (map.get("us") != null) {
			ids = UserSelectDialog.getIdsForLookup();
		}
		if (ids == null) {

			// Get Friends IDs
			String url = String
					.format("http://api.twitter.com/1/followers/ids.json?screen_name=%s&cursor=%s",
							map.get("screen_name"), map.get("cursor"));
			result = httpGetOauth(mService, url, currentUserName,
					currentUserPassword);

			if ("200".equals(result.getResponseCode())) {
				TwitterParseHandler.parseIds(result.getMessage(),
						TYPE_FOLLOWERS);
				url = String
						.format("http://api.twitter.com/1/users/lookup.json?include_entities=true&user_id=%s",
								FollowedActivity.getIdsForLookup());
				return httpGetOauth(mService, url, currentUserName,
						currentUserPassword);
			} else {
				return result;
			}

		} else {
			String url = String
					.format("http://api.twitter.com/1/users/lookup.json?include_entities=true&user_id=%s",
							ids);
			return httpGetOauth(mService, url, currentUserName,
					currentUserPassword);
		}

	}

	public static CommResult getFriendsList(Map map) {

		CommResult result = new CommResult();

		String ids = FollowActivity.getIdsForLookup();
		if (ids == null) {

			// Get Friends IDs
			String url = String
					.format("http://api.twitter.com/1/friends/ids.json?screen_name=%s&cursor=%s",
							map.get("screen_name"), map.get("cursor"));
			result = httpGetOauth(mService, url, currentUserName,
					currentUserPassword);

			if ("200".equals(result.getResponseCode())) {
				TwitterParseHandler.parseIds(result.getMessage(), TYPE_FRIENDS);
				url = String
						.format("http://api.twitter.com/1/users/lookup.json?include_entities=true&user_id=%s",
								FollowActivity.getIdsForLookup());
				return httpGetOauth(mService, url, currentUserName,
						currentUserPassword);
			} else {
				return result;
			}

		} else {
			String url = String
					.format("http://api.twitter.com/1/users/lookup.json?include_entities=true&user_id=%s",
							ids);
			return httpGetOauth(mService, url, currentUserName,
					currentUserPassword);
		}

	}

	public static CommResult getFavoriteList(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/favorites.json?include_entities=true&id=%s&page=%s",
						currentUid, map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult getUserInfo(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/users/show.json?include_entities=true&screen_name=%s",
						map.get("screen_name"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult showRelation(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/1/friendships/show.json?target_screen_name=%s",
						map.get("screen_name"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult setFollow(Map map) {

		CommResult result = new CommResult();

		String url = "";

		if (map.get("type").equals("create")) {
			url = String.format(
					"http://api.twitter.com/friendships/create/%s.json",
					map.get("id"));
		} else {
			url = String.format(
					"http://api.twitter.com/friendships/destroy/%s.json",
					map.get("id"));
		}

		result = httpPostOauth(mService, url, new HashMap<String, String>(),
				currentUserName, currentUserPassword);

		return result;

	}

	public static CommResult retweet(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"http://api.twitter.com/1/statuses/retweet/%s.json",
				map.get("message_id"));

		result = httpPostOauth(mService, url, new HashMap<String, String>(),
				currentUserName, currentUserPassword);

		return result;

	}

	public static CommResult setFavorite(Map map) {

		CommResult result = new CommResult();

		String url = "";

		if (map.get("type").equals("create")) {
			url = String.format(
					"http://api.twitter.com/favorites/create/%s.json",
					map.get("message_id"));
		} else {
			url = String.format(
					"http://api.twitter.com/favorites/destroy/%s.json",
					map.get("message_id"));
		}

		result = httpPostOauth(mService, url, new HashMap<String, String>(),
				currentUserName, currentUserPassword);

		return result;

	}

	public static CommResult getMyLists(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/1/lists/memberships.json?screen_name=%s&cursor=%s",
						map.get("screenName"),
						String.valueOf(map.get("cursor")));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult getFollowLists(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"http://api.twitter.com/1/%s/lists/subscriptions.json",
				map.get("screenName"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult getListTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/1/%s/lists/%s/statuses.json?include_entities=true&page=%s",
						listParameterMap.get("user"),
						listParameterMap.get("id"), map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult getRetweetToMeTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/1/statuses/retweeted_to_me.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult getRetweetByMeTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/1/statuses/retweeted_by_me.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult getRetweetOfMeTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/1/statuses/retweets_of_me.json?include_entities=true&count=21&page=%s",
						map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult getMessageById(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/1/statuses/show/%s.json?include_entities=true&",
						map.get("messageId"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static CommResult destroyStatus(Map map) {

		HashMap<String, String> paramMap = new HashMap<String, String>();
		// Request
		CommResult result = new CommResult();
		String url = String.format(
				"http://api.twitter.com/1/statuses/destroy/%s.json?",
				map.get("message_id"));
		result = httpPostOauth(mService, url, paramMap, currentUserName,
				currentUserPassword);

		return result;
	}

	public static CommResult setFollowList(Map map) {

		CommResult result = new CommResult();

		// http://api.twitter.com/1/lists/subscribers/create.xml?list_id=1
		// http://api.twitter.com/1/lists/subscribers/destroy.xml?list_id=1

		// String url =
		// String.format("http://api.twitter.com/1/%s/%s/subscribers.json",
		// map.get("user"), map.get("list_id"));
		String url = "";

		if (map.get("type").equals("true")) {
			url = "http://api.twitter.com/1/lists/subscribers/destroy.json";
		} else {
			url = "http://api.twitter.com/1/lists/subscribers/create.json";
		}

		HashMap<String, String> parameter = new HashMap<String, String>();
		parameter.put("list_id", (String) map.get("list_id"));

		result = httpPostOauth(mService, url, parameter, currentUserName,
				currentUserPassword);

		return result;

	}

	// --------------------------------------------------------------------------
	/**
	 * Register to Twit Longer. It will return shorten Url if suceeded.
	 */
	// --------------------------------------------------------------------------
	public static CommResult registerMessageToAPI(Map map) {

		CommResult result = new CommResult();

		// Http
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("application", APPLICATION);
		paramMap.put("api_key", API_KEY);
		paramMap.put("username", (String) map.get("userName"));
		paramMap.put("message", (String) map.get("message"));

		result = httpPostForLongTweet(API_SERVER, paramMap);

		return result;

	}

	public static CommResult notificationFollow(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"http://api.twitter.com/notifications/follow.json?user_id=%s",
				map.get("uid"));

		result = httpPostOauth(mService, url, new HashMap<String, String>(),
				currentUserName, currentUserPassword);

		return result;
	}

	public static CommResult notificationLeave(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"http://api.twitter.com/notifications/leave.json?user_id=%s",
				map.get("uid"));

		result = httpPostOauth(mService, url, new HashMap<String, String>(),
				currentUserName, currentUserPassword);

		return result;
	}

	public static synchronized CommResult getTrends(Map map) {

		CommResult result = new CommResult();

		// http://api.twitter.com/1/trends/current.json
		// http://api.twitter.com/1/trends/daily.format
		// http://api.twitter.com/1/trends/weekly.json

		String url = String.format("http://api.twitter.com/1/trends/%s.json?",
				map.get("type"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	public static synchronized CommResult getLocationsAvailiableTrends(Map map) {

		CommResult result = new CommResult();

		String url = "http://api.twitter.com/1/trends/available.json";

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;

	}

	public static synchronized CommResult getSuggestionSlugs(Map map) {
		CommResult result = new CommResult();
		// category
		String url = "https://api.twitter.com/1/users/suggestions.json";
		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);
		return result;

	}

	public static synchronized CommResult getSuggestionUsers(Map map) {
		CommResult result = new CommResult();
		// suggestion user
		String url = String.format(
				"http://api.twitter.com/1/users/suggestions/%s.json",
				URLEncoder.encode(String.valueOf(map.get("slug"))));
		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);
		return result;

	}

	public static synchronized CommResult getRetweetedListById(Map map) {
		CommResult result = new CommResult();
		String url = String.format(
				"http://api.twitter.com/1/statuses/retweets/%s.json",
				String.valueOf(map.get("retweeted_id")));
		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);
		return result;
	}

	public static synchronized CommResult getRetweetedUserListById(Map map) {
		CommResult result = new CommResult();
		String url = String
				.format("https://api.twitter.com/1/statuses/%s/retweeted_by.json?count=100",
						String.valueOf(map.get("retweeted_id")));
		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);
		return result;
	}

	public static CommResult getGroupListSlugs(Map map) {

		CommResult result = new CommResult();

		String url = String.format(
				"https://api.twitter.com/1/lists/all.json?screen_name=%s",
				map.get("screenName"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	public static CommResult getGroupTimeline(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("https://api.twitter.com/1/lists/statuses.json?slug=%s&owner_screen_name=%s&page=%s&include_entities=true",
						map.get("slug"), map.get("ownerName"), map.get("page"));

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}

	public static synchronized CommResult getGroupUserlist(Map map) {
		CommResult result = new CommResult();
		// suggestion user
		String url = String
				.format("https://api.twitter.com/1/lists/members.json?slug=%s&owner_screen_name=%s&cursor=-1",
						map.get("slug"), map.get("screen_name"));
		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);
		return result;
	}

	public static synchronized CommResult getLBSLocationList(Map map) {
		CommResult result = new CommResult();
		// String url = String
		// .format("http://api.twitter.com/1/geo/reverse_geocode.json?lat=37.78215&long=-122.40060");
		// String url =
		// String.format("https://api.twitter.com/1/geo/search.json?lat=%s&long=%s",map.get("latitude"),"-"+map.get("longitude"));
		String url = String
				.format("https://api.twitter.com/1/geo/reverse_geocode.json?lat=%s&long=%s",
						map.get("latitude"), map.get("longitude"));
		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);
		return result;
	}

	public static CommResult updateLBSMessage(Map map) {
		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/update.json");
		String name = (String) map.get("user_name");
		String password = (String) map.get("user_password");
		String service = (String) map.get("service");
		result = httpPostOauth(mService, url, (HashMap<String, String>) map,
				name, password);

		return result;
	}

	public static synchronized CommResult getLBSTimeLine(Map map) {

		CommResult result = new CommResult();

		String url = String
				.format("http://api.twitter.com/statuses/home_timeline.json?include_entities=true&count=100&page=1");

		result = httpGetOauth(mService, url, currentUserName,
				currentUserPassword);

		return result;
	}
}
