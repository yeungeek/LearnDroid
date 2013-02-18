package com.anhuioss.crowdroid.service.renren;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import android.content.Context;

import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

public class RenRenCommHandler extends HttpCommunicator {

	public Map<String, String> tokenMap;

	public static String accessToken;

	public static HttpCommunicator sigNature;

	private static Context context;

	public static final String RENREN_API_KEY = "165d021dcd924dab95bd54d272c357fd";
	public static final String RENREN_SECRET = "298e98e4d3854f8ca12d4f098ac75115";
	public static final String RENREN_API_VERSION = "1.0";
	public static final String RENREN_FORMAT = "JSON";
	public static final int RENREN_COUNT = 100;

	public static final String baseUrl = "http://api.renren.com/restserver.do";

	public static void setAppContext(Context ctx) {
		context = ctx;
	}

	public static void setAccount(String accessToken) {
		RenRenCommHandler.accessToken = accessToken;
	}

	@SuppressWarnings({ "finally", "unchecked" })
	public static CommResult getNewToken(Map map) {

		synchronized ("get new token") {

			// Prepare Result
			CommResult result = new CommResult();
			result.setResponseCode(String.valueOf(200));
			String grant_type = (String) map.get("grant_type");
			String code = (String) map.get("code");
			String client_id = "&client_id=" + RENREN_API_KEY;
			String client_secret = "&client_secret=" + RENREN_SECRET;
			String redirect_uri = "&redirect_uri=http://graph.renren.com/oauth/login_success.html";

			try {
				URL url;
				url = new URL("https://graph.renren.com/oauth/token?");
				HttpURLConnection mHttpURLConnection = (HttpURLConnection) url
						.openConnection();
				mHttpURLConnection.setDoInput(true);
				mHttpURLConnection.setDoOutput(true);

				OutputStream out = mHttpURLConnection.getOutputStream();
				out.write((grant_type + code + client_id + client_secret + redirect_uri)
						.getBytes());
				out.flush();
				InputStream in = mHttpURLConnection.getInputStream();
				RenRenParserHandler parse = new RenRenParserHandler();
				Map<String, String> map1 = parse.TokenparseJson(in);
				String access_token = (String) map1.get("access_token");
				String refresh_token = (String) map1.get("refresh_token");
				String expires_in = (String) map.get("expires_in");
				String message = access_token + ";" + refresh_token + ";"
						+ expires_in;
				result.setMessage(message);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;

		}

	}

	public static synchronized CommResult getHomeTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "10,11,20,21,22,23,30,31,32,33,36,50,51,52,53,54,55";

		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + 20);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(20));
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static synchronized CommResult getMyTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "10,11,20,21,30,31,32,33,36,50,51,52,53,54,55";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();
		String uid = map.get("uid").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + 20);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);
		params.add("uid=" + uid);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(20));
		map1.put("page", page);
		map1.put("uid", uid);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static synchronized CommResult getHomeStateTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "10,11";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + 20);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(20));
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static synchronized CommResult getHomePhotoTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "30,31";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + 20);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(20));
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static synchronized CommResult getHomeBlogTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "20,22";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + 20);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(20));
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static synchronized CommResult getHomeShareTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "21,23,32,33,36,50,51,52,53,54,55";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + 20);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(20));
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult verifyUser(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "users.getInfo";
		String access_token = (String) map.get("accessToken");

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getUserInfo(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "users.getProfileInfo";
		String access_token = RenRenCommHandler.accessToken;
		String uid = String.valueOf(map.get("uid"));
		String fields = "base_info,status,visitors_count,blogs_count,albums_count,friends_count,guestbook_count, status_count";

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("uid=" + uid);
		params.add("fields=" + fields);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("uid", uid);
		map1.put("fields", fields);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult updateStatus(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "status.set";
		String access_token = RenRenCommHandler.accessToken;
		String status = map.get("status").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("status=" + status);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("status", status);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult uploadPhotos(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		try {
			List<String> params = new ArrayList<String>();
			String method = "photos.upload";
			String access_token = RenRenCommHandler.accessToken;
			String status = map.get("description").toString();
			String aid = map.get("aid").toString();
			String imageName = map.get("imgName").toString();
			String filePath = map.get("filePath").toString();
			String contentType = "multipart/form-data";

			File upload = new File(filePath);

			byte[] data = getBytes(filePath);

			params.add("method=" + method);
			params.add("v=" + RENREN_API_VERSION);
			params.add("access_token=" + access_token);
			params.add("format=" + RENREN_FORMAT);
			params.add("caption=" + status);
			params.add("aid=" + aid);

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			HashMap<String, String> map1 = new HashMap<String, String>();
			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("v", RENREN_API_VERSION);
			map1.put("access_token", access_token);
			map1.put("format", RENREN_FORMAT);
			map1.put("caption", status);
			map1.put("aid", aid);

			result = doUploadFile(baseUrl, map1, "upload", imageName,
					contentType, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static CommResult retweetState(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();
		List<String> params = new ArrayList<String>();
		String method = "status.forward";
		String access_token = RenRenCommHandler.accessToken;
		String status = map.get("comment").toString();
		String forwardId = map.get("message_id").toString();
		String forwardOwner = map.get("user_id").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("status=" + status);
		params.add("forward_id=" + forwardId);
		params.add("forward_owner=" + forwardOwner);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("status", status);
		map1.put("forward_id", forwardId);
		map1.put("forward_owner", forwardOwner);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getCommentsById(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();
		HashMap<String, String> map1 = new HashMap<String, String>();
		List<String> params = new ArrayList<String>();
		String method = "";
		String feedType = map.get("feed_type").toString();

		String access_token = RenRenCommHandler.accessToken;
		String statusId = map.get("message_id").toString();
		String ownerId = map.get("owner_id").toString();
		String page = map.get("page").toString();

		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);
		params.add("count=" + RENREN_COUNT);

		if ("10".equals(feedType) || "11".equals(feedType)) {

			method = "status.getComment";
			params.add("method=" + method);
			params.add("status_id=" + statusId);
			params.add("owner_id=" + ownerId);

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("status_id", statusId);
			map1.put("owner_id", ownerId);

		} else if ("20".equals(feedType) || "22".equals(feedType)) {
			method = "blog.getComments";
			params.add("method=" + method);
			params.add("id=" + statusId);
			params.add("uid=" + ownerId);

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("id", statusId);
			map1.put("uid", ownerId);
		} else if ("30".equals(feedType) || "31".equals(feedType)) {
			method = "photos.getComments";
			params.add("method=" + method);
			params.add("pid=" + statusId);
			params.add("uid=" + ownerId);

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("pid", statusId);
			map1.put("uid", ownerId);
		} else if ("21".equals(feedType) || "32".equals(feedType)
				|| "33".equals(feedType) || "50".equals(feedType)
				|| "51".equals(feedType) || "52".equals(feedType)
				|| "23".equals(feedType) || "36".equals(feedType)
				|| "53".equals(feedType) || "54".equals(feedType)
				|| "55".equals(feedType)) {
			// share
			// 日志、照片、相册、视频、链接、音乐
			method = "share.getComments";
			params.add("method=" + method);
			params.add("share_id=" + statusId);
			params.add("user_id=" + ownerId);

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("share_id", statusId);
			map1.put("user_id", ownerId);
		}

		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("page", page);
		map1.put("count", String.valueOf(RENREN_COUNT));

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult updateComments(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		String method = "";
		String feedType = map.get("feed_type").toString();
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + RenRenCommHandler.accessToken);
		params.add("format=" + RENREN_FORMAT);

		if ("10".equals(feedType) || "11".equals(feedType)) {
			// state
			method = "status.addComment";

			params.add("method=" + method);

			params.add("status_id=" + map.get("message_id").toString());
			params.add("owner_id=" + map.get("owner_id").toString());
			params.add("content=" + map.get("comment").toString());

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("status_id", map.get("message_id").toString());
			map1.put("owner_id", map.get("owner_id").toString());
			map1.put("content", map.get("comment").toString());
		} else if ("20".equals(feedType) || "22".equals(feedType)) {
			// blog
			method = "blog.addComment";
			params.add("method=" + method);
			params.add("id=" + map.get("message_id").toString());
			params.add("uid=" + map.get("owner_id").toString());
			params.add("content=" + map.get("comment").toString());

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("id", map.get("message_id").toString());
			map1.put("uid", map.get("owner_id").toString());
			map1.put("content", map.get("comment").toString());
		} else if ("30".equals(feedType) || "31".equals(feedType)) {
			// photo
			method = "photos.addComment";
			params.add("method=" + method);
			params.add("pid=" + map.get("message_id").toString());
			params.add("uid=" + map.get("owner_id").toString());
			params.add("content=" + map.get("comment").toString());

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("pid", map.get("message_id").toString());
			map1.put("uid", map.get("owner_id").toString());
			map1.put("content", map.get("comment").toString());
		} else if ("21".equals(feedType) || "32".equals(feedType)
				|| "33".equals(feedType) || "50".equals(feedType)
				|| "51".equals(feedType) || "52".equals(feedType)
				|| "23".equals(feedType) || "36".equals(feedType)
				|| "53".equals(feedType) || "54".equals(feedType)
				|| "55".equals(feedType)) {
			// share
			// 日志、照片、相册、视频、链接、音乐
			method = "share.addComment";
			params.add("method=" + method);
			params.add("share_id=" + map.get("message_id").toString());
			params.add("user_id=" + map.get("owner_id").toString());
			params.add("content=" + map.get("comment").toString());

			String sig = sigNature.getSignature(params, RENREN_SECRET);

			map1.put("sig", sig);
			map1.put("method", method);
			map1.put("share_id", map.get("message_id").toString());
			map1.put("user_id", map.get("owner_id").toString());
			map1.put("content", map.get("comment").toString());
		}
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", RenRenCommHandler.accessToken);
		map1.put("format", RENREN_FORMAT);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult replyToStatusComments(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "status.addComment";
		String access_token = RenRenCommHandler.accessToken;
		String statusId = map.get("status_id").toString();
		String ownerId = map.get("owner_id").toString();
		String content = map.get("content").toString();
		String rid = map.get("rid").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("status_id=" + statusId);
		params.add("owner_id=" + ownerId);
		params.add("content=" + content);
		params.add("rid=" + rid);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("status_id", statusId);
		map1.put("owner_id", ownerId);
		map1.put("content", content);
		map1.put("rid", rid);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getBlogComments(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "blog.getComments";
		String access_token = RenRenCommHandler.accessToken;
		String id = map.get("id").toString();
		String uid = map.get("uid").toString();
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("id=" + id);
		params.add("uid=" + uid);
		params.add("page=" + page);
		params.add("count=" + RENREN_COUNT);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("id", id);
		map1.put("uid", uid);
		map1.put("page", page);
		map1.put("count", String.valueOf(RENREN_COUNT));

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult replyToBlogComments(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "blog.addComment";
		String access_token = RenRenCommHandler.accessToken;
		String id = map.get("id").toString();
		String uid = map.get("uid").toString();
		String content = map.get("content").toString();
		String rid = map.get("rid").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("id=" + id);
		params.add("uid=" + uid);
		params.add("content=" + content);
		params.add("rid=" + rid);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("id", id);
		map1.put("uid", uid);
		map1.put("content", content);
		map1.put("rid", rid);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getPhotosComments(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "photos.getComments";
		String access_token = RenRenCommHandler.accessToken;
		String pid = map.get("pid").toString();
		String uid = map.get("uid").toString();
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("pid=" + pid);
		params.add("uid=" + uid);
		params.add("page=" + page);
		params.add("count" + RENREN_COUNT);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("pid", pid);
		map1.put("uid", uid);
		map1.put("page", page);
		map1.put("count", String.valueOf(RENREN_COUNT));

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult replyToPhotosComments(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "photos.addComment";
		String access_token = RenRenCommHandler.accessToken;
		String pid = map.get("pid").toString();
		String uid = map.get("uid").toString();
		String content = map.get("content").toString();
		String rid = map.get("rid").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("pid=" + pid);
		params.add("uid=" + uid);
		params.add("content=" + content);
		params.add("rid=" + rid);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("pid", pid);
		map1.put("uid", uid);
		map1.put("content", content);
		map1.put("rid", rid);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getStatusContent(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "status.get";
		String access_token = RenRenCommHandler.accessToken;
		String statusId = map.get("status_id").toString();
		String ownerId = map.get("owner_id").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("status_id=" + statusId);
		params.add("owner_id=" + ownerId);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("status_id", statusId);
		map1.put("owner_id", ownerId);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult updateBlog(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "blog.addBlog";
		String access_token = RenRenCommHandler.accessToken;
		String title = map.get("title").toString();
		String content = map.get("status").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("title=" + title);
		params.add("content=" + content);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("title", title);
		map1.put("content", content);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getUserSearch(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "friends.search";
		String access_token = RenRenCommHandler.accessToken;
		String condition = map.get("condition").toString();
		String name = map.get("name").toString();
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + RENREN_COUNT);
		params.add("condition=" + condition);
		params.add("name=" + name);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", String.valueOf(RENREN_COUNT));
		map1.put("condition", condition);
		map1.put("name", name);
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getAlbums(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "photos.getAlbums";
		String access_token = RenRenCommHandler.accessToken;
		String uid = map.get("uid").toString();
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + RENREN_COUNT);
		params.add("uid=" + uid);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", String.valueOf(RENREN_COUNT));
		map1.put("uid", uid);
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getProfileStatus(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "status.gets";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();
		String uid = map.get("id").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + RENREN_COUNT);
		params.add("page=" + page);
		params.add("uid=" + uid);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", String.valueOf(RENREN_COUNT));
		map1.put("page", page);
		map1.put("uid", uid);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getUserBlogList(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "blog.gets";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();
		String uid = map.get("id").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + RENREN_COUNT);
		params.add("page=" + page);
		params.add("uid=" + uid);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", String.valueOf(RENREN_COUNT));
		map1.put("page", page);
		map1.put("uid", uid);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getBlogContent(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "blog.get";
		String access_token = RenRenCommHandler.accessToken;
		String id = map.get("id").toString();
		String uid = map.get("uid").toString();
		// String psw = map.get("psw").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("id=" + id);
		params.add("uid=" + uid);
		// params.add("password=" + psw);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("id", id);
		map1.put("uid", uid);
		// map1.put("password", psw);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getVisitors(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "";
		if (map.containsKey("categoryName")) {
			method = "pages.getFansList";
		} else {
			method = "users.getVisitors";
		}
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + 20);
		params.add("page=" + page);
		if (map.containsKey("categoryName")) {
			params.add("page_id=" + map.get("pageId"));
		}

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", "20");
		map1.put("page", page);
		if (map.containsKey("categoryName")) {
			map1.put("page_id", map.get("pageId").toString());
		}

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getFriends(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "friends.getFriends";
		String access_token = RenRenCommHandler.accessToken;
		String fields = "headurl_with_logo,tinyurl_with_logo";
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + RENREN_COUNT);
		params.add("fields=" + fields);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", String.valueOf(RENREN_COUNT));
		map1.put("fields", fields);
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult getEmotions(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "status.getEmoticons";
		String access_token = RenRenCommHandler.accessToken;

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static CommResult createNewAlbum(Map map) {
		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "photos.createAlbum";
		String access_token = RenRenCommHandler.accessToken;

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("name=" + map.get("name").toString());
		params.add("description=" + map.get("description").toString());
		params.add("visible=" + map.get("visible").toString());
		params.add("password=" + map.get("password").toString());

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("name", map.get("name").toString());
		map1.put("description", map.get("description").toString());
		map1.put("visible", map.get("visible").toString());
		map1.put("password", map.get("password").toString());

		result = httpPost(baseUrl, map1);

		return result;
	}

	/**
	 * 人人上传照片所用方法
	 * 
	 */
	public static CommResult doUploadFile(String reqUrl,
			Map<String, String> parameters, String fileParamName,
			String filename, String contentType, byte[] data) {
		HttpURLConnection urlConn = null;
		CommResult result = new CommResult();
		try {
			urlConn = sendFormdata(reqUrl, parameters, fileParamName, filename,
					contentType, data);
			InputStream in = urlConn.getInputStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for (int i = 0; (i = in.read(buf)) > 0;)
				os.write(buf, 0, i);
			in.close();
			String responseContent = new String(os.toByteArray());
			result.setResponseCode(String.valueOf(urlConn.getResponseCode()));
			result.setMessage(responseContent.trim());
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	private static HttpURLConnection sendFormdata(String reqUrl,
			Map<String, String> parameters, String fileParamName,
			String filename, String contentType, byte[] data) {
		HttpURLConnection urlConn = null;
		try {
			URL url = new URL(reqUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(5000);// （单位：毫秒）jdk
			urlConn.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			urlConn.setDoOutput(true);

			urlConn.setRequestProperty("connection", "keep-alive");

			String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
			urlConn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);

			boundary = "--" + boundary;
			StringBuffer params = new StringBuffer();
			if (parameters != null) {
				for (Iterator<String> iter = parameters.keySet().iterator(); iter
						.hasNext();) {
					String name = iter.next();
					String value = parameters.get(name);
					params.append(boundary + "\r\n");
					params.append("Content-Disposition: form-data; name=\""
							+ name + "\"\r\n\r\n");
					params.append(value);
					params.append("\r\n");
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + fileParamName
					+ "\"; filename=\"" + filename + "\"\r\n");
			sb.append("Content-Type: " + contentType + "\r\n\r\n");
			byte[] fileDiv = sb.toString().getBytes();
			byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes();
			byte[] ps = params.toString().getBytes();

			OutputStream os = urlConn.getOutputStream();
			os.write(ps);
			os.write(fileDiv);
			os.write(data);
			os.write(endData);

			os.flush();
			os.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return urlConn;
	}

	private static byte[] getBytes(String filePath) {
		try {
			FileInputStream in = new FileInputStream(filePath);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for (int i = 0; (i = in.read(buf)) > 0;)
				os.write(buf, 0, i);
			in.close();
			return os.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static CommResult getSerchFriends(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();

		String method = "friends.search";
		String access_token = RenRenCommHandler.accessToken;

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + map.get("page").toString());
		params.add("count=" + 20);

		String condition = "";
		String sig = "";
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);

		String name = map.get("name").toString();
		String flag = map.get("flag").toString();// 标识

		if (flag.equals("student")) // 搜同学
		{
			String school_name = map.get("serch_school_name").toString();
			String schoolType = map.get("schoolType").toString();
			String schoolYear = map.get("schoolYear").toString();
			switch (Integer.parseInt(schoolType)) {
			case 0: { // 大学

				condition = "[{" + "t:" + '"' + "univ" + '"' + "," + "name:"
						+ '"' + school_name + '"' + "," + "year:" + '"'
						+ schoolYear + '"' + "}]";
				params.add("name=" + name);
				params.add("condition=" + condition);
				sig = sigNature.getSignature(params, RENREN_SECRET);
				map1.put("condition", condition);
				break;
			}
			case 1: { // 高中

				condition = "[{" + "t:" + '"' + "high" + '"' + "," + "name:"
						+ '"' + school_name + '"' + "," + "year:" + '"'
						+ schoolYear + '"' + "}]";
				params.add("name=" + name);
				params.add("condition=" + condition);
				sig = sigNature.getSignature(params, RENREN_SECRET);
				map1.put("condition", condition);
				break;
			}
			case 2: { // 初中
				condition = "[{" + "t:" + '"' + "juni" + '"' + "," + "name:"
						+ '"' + school_name + '"' + "," + "year:" + '"'
						+ schoolYear + '"' + "}]";
				params.add("name=" + name);
				params.add("condition=" + condition);
				sig = sigNature.getSignature(params, RENREN_SECRET);
				map1.put("condition", condition);
				break;
			}
			case 3: { // 中专

				condition = "[{" + "t:" + '"' + "sect" + '"' + "," + "name:"
						+ '"' + school_name + '"' + "," + "year:" + '"'
						+ schoolYear + '"' + "}]";

				params.add("name=" + name);
				params.add("condition=" + condition);
				sig = sigNature.getSignature(params, RENREN_SECRET);
				map1.put("condition", condition);
				break;
			}

			case 4: { // 小学

				condition = "[{" + "t:" + '"' + "elem" + '"' + "," + "name:"
						+ '"' + school_name + '"' + "," + "year:" + '"'
						+ schoolYear + '"' + "}]";
				params.add("name=" + name);
				params.add("condition=" + condition);
				sig = sigNature.getSignature(params, RENREN_SECRET);
				map1.put("condition", condition);
				break;
			}

			default:
				break;
			}

		} else if (flag.equals("ts")) { // 搜同事

			condition = "[{" + "t:" + '"' + "work" + '"' + "," + "name:" + '"'
					+ map.get("company_name").toString() + '"' + "}]";
			params.add("name=" + name);
			params.add("condition=" + condition);
			sig = sigNature.getSignature(params, RENREN_SECRET);
			map1.put("condition", condition);
		} else if (flag.equals("friend")) { // 按姓名查找

			condition = "[{" + "t:" + '"' + "birt" + '"' + "," + "year:" + '"'
					+ map.get("year").toString() + '"' + "," + "month:" + '"'
					+ map.get("month").toString() + '"' + "," + "day:" + '"'
					+ map.get("day").toString() + '"' + "}]";
			params.add("name=" + name);
			params.add("condition=" + condition);
			sig = sigNature.getSignature(params, RENREN_SECRET);
			map1.put("condition", condition);
		} else if (flag.equals("name")) { // 快速查找
			params.add("name=" + name);
			sig = sigNature.getSignature(params, RENREN_SECRET);
		} else if (flag.equals("base")) {
			condition = "[{" + "t:" + '"' + "base" + '"' + "," + "gend:" + '"'
					+ map.get("sex").toString() + '"' + "," + "prov:" + '"'
					+ map.get("prov").toString() + '"' + "," + "city:" + '"'
					+ map.get("city").toString() + '"' + "}]";

			params.add("name=" + name);
			params.add("condition=" + condition);
			sig = sigNature.getSignature(params, RENREN_SECRET);
			map1.put("condition", condition);
		}
		map1.put("sig", sig);
		map1.put("name", name);
		map1.put("page", map.get("page").toString());
		map1.put("count", String.valueOf(20));
		result = httpPost(baseUrl, map1);
		return result;
	}

	public static synchronized CommResult getMyStateTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "10";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + 20);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);
		params.add("uid=" + map.get("uid").toString());

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(20));
		map1.put("page", page);
		map1.put("uid", map.get("uid").toString());

		result = httpPost(baseUrl, map1);

		return result;
	}

	public static synchronized CommResult getMyShareTimeline(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "feed.get";
		String type = "21,32,33,50,51,52";
		String access_token = RenRenCommHandler.accessToken;
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("type=" + type);
		params.add("count=" + RENREN_COUNT);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("page=" + page);
		params.add("uid=" + map.get("uid").toString());

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("type", type);
		map1.put("count", String.valueOf(RENREN_COUNT));
		map1.put("page", page);
		map1.put("uid", map.get("uid").toString());

		result = httpPost(baseUrl, map1);

		return result;
	}

	// 浏览某相册的所有照片
	public static CommResult getAlbumPhotos(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "photos.get";
		String access_token = RenRenCommHandler.accessToken;
		String uid = map.get("uid").toString();
		String aid = map.get("aid").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + RENREN_COUNT);
		params.add("uid=" + uid);
		params.add("aid=" + aid);
		if (map.containsKey("password")) {
			params.add("password=" + map.get("password"));
		}

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", String.valueOf(RENREN_COUNT));
		map1.put("uid", uid);
		map1.put("aid", aid);
		if (map.containsKey("password")) {
			map1.put("password", map.get("password").toString());
		}

		result = httpPost(baseUrl, map1);

		return result;
	}

	// 获得page的分类
	public static CommResult getPageCategory(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "pages.getCategories";
		String access_token = RenRenCommHandler.accessToken;

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		// map1.put("count", String.valueOf(RENREN_COUNT));
		// map1.put("uid", uid);
		// map1.put("aid", aid);
		// if (map.containsKey("password")) {
		// map1.put("password", map.get("password").toString());
		// }

		result = httpPost(baseUrl, map1);

		return result;
	}

	// 根据page分类获得列表
	public static CommResult getPageListByCategory(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		List<String> params = new ArrayList<String>();
		String method = "pages.getList";
		String access_token = RenRenCommHandler.accessToken;
		String categoryId = map.get("categoryId").toString();
		String page = map.get("page").toString();

		params.add("method=" + method);
		params.add("v=" + RENREN_API_VERSION);
		params.add("access_token=" + access_token);
		params.add("format=" + RENREN_FORMAT);
		params.add("count=" + "20");
		params.add("category=" + categoryId);
		params.add("page=" + page);

		String sig = sigNature.getSignature(params, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("count", "20");
		map1.put("category", categoryId);
		map1.put("page", page);

		result = httpPost(baseUrl, map1);

		return result;
	}

	// 位置签到
	public static CommResult UpdateLBSMessage(Map map) {

		CommResult result = new CommResult();

		sigNature = new HttpCommunicator();

		// create place
		List<String> paras = new ArrayList<String>();
		String placeId = "";
		String method = "places.create";
		String access_token = RenRenCommHandler.accessToken;

		paras.add("method=" + method);
		paras.add("v=" + RENREN_API_VERSION);
		paras.add("access_token=" + access_token);
		paras.add("format=" + RENREN_FORMAT);
		paras.add("name=" + map.get("locationName"));
		paras.add("address=" + map.get("locationAddress"));
		paras.add("poi_id=" + map.get("poiId"));
		paras.add("longitude=" + map.get("longitude"));
		paras.add("latitude=" + map.get("latitude"));

		String sig = sigNature.getSignature(paras, RENREN_SECRET);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("sig", sig);
		map1.put("method", method);
		map1.put("v", RENREN_API_VERSION);
		map1.put("access_token", access_token);
		map1.put("format", RENREN_FORMAT);
		map1.put("name", map.get("locationName").toString());
		map1.put("address", map.get("locationAddress").toString());
		map1.put("poi_id", map.get("poiId").toString());
		map1.put("longitude", map.get("longitude").toString());
		map1.put("latitude", map.get("latitude").toString());

		result = httpPost(baseUrl, map1);

		if ("200".equals(result.getResponseCode())) {
			try {
				JSONObject jObject = new JSONObject(result.getMessage());
				JSONObject place = jObject.getJSONObject("place");
				placeId = place.getString("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!"".equals(placeId)) {
			// create place
			List<String> params = new ArrayList<String>();
			method = "checkins.checkin";

			params.add("method=" + method);
			params.add("v=" + RENREN_API_VERSION);
			params.add("access_token=" + access_token);
			params.add("format=" + RENREN_FORMAT);
			params.add("place_id=" + placeId);
			params.add("message=" + map.get("status"));

			String signate = sigNature.getSignature(params, RENREN_SECRET);

			HashMap<String, String> parameter = new HashMap<String, String>();
			parameter.put("sig", signate);
			parameter.put("method", method);
			parameter.put("v", RENREN_API_VERSION);
			parameter.put("access_token", access_token);
			parameter.put("format", RENREN_FORMAT);
			parameter.put("place_id", placeId);
			parameter.put("message", map.get("status").toString());

			result = httpPost(baseUrl, parameter);
		}

		return result;
	}
}
