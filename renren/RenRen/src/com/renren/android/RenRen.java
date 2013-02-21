package com.renren.android;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class RenRen {
	public static final String RENREN_API_KEY = "b672003d39494bb7884545a03d5c218a";
	public static final String RENREN_SECRET_KEY = "84f0d691fe494520af294a7b3a1c8870";
	public static final String APIURL = "http://api.renren.com/restserver.do";
	public static final String SCOPE = "read_user_blog,read_user_checkin,read_user_feed,read_user_guestbook,read_user_invitation,read_user_like_history,read_user_message,read_user_notification,read_user_photo,read_user_status,read_user_album,read_user_comment,read_user_share,read_user_request,publish_blog,publish_checkin,publish_feed,publish_share,write_guestbook,send_invitation,send_request,send_message,send_notification,photo_upload,status_update,create_album,publish_comment,operate_like,admin_page";
	public final String FORMAT = "JSON";
	public final String VERSON = "1.0";
	private static AccessTokenManager mAccessTokenManager;

	public RenRen(Context context) {
		if (mAccessTokenManager == null) {
			mAccessTokenManager = new AccessTokenManager(context);
		}
	}

	/**
	 * 获取AccessToken
	 * 
	 * @return AccessToken
	 */
	public String getAccessToken() {
		return mAccessTokenManager.getmAccessToken();
	}

	/**
	 * 获取用户名称
	 * 
	 * @return
	 */
	public String getUserName() {
		return mAccessTokenManager.getmName();
	}

	/**
	 * 获取用户ID
	 * 
	 * @return
	 */
	public int getUserId() {
		return mAccessTokenManager.getmUid();
	}

	/**
	 * 获取用户头像
	 * 
	 * @return
	 */
	public String getUserHeadUrl() {
		return mAccessTokenManager.getmHeadurl();
	}

	/**
	 * 获取用户小头像
	 * 
	 * @return
	 */
	public String getUserHeadUrl_Tiny() {
		return mAccessTokenManager.getmHeadurl_Tiny();
	}

	/**
	 * 获取用户中头像
	 * 
	 * @return
	 */
	public String getUserHeadUrl_Main() {
		return mAccessTokenManager.getmHeadurl_Main();
	}

	/**
	 * 获取用户大头像
	 * 
	 * @return
	 */
	public String getUserHeadUrl_Large() {
		return mAccessTokenManager.getMheadurl_Large();
	}

	/**
	 * 返回当前是否存储AccessToken
	 * 
	 * @return true-存在 false-不存在
	 */
	public boolean isAccessTokenExist() {
		return mAccessTokenManager.isAccessTokenExist();
	}

	/**
	 * 保存AccessToken
	 * 
	 * @param accessToken
	 *            accessToken值
	 * @param refreshToken
	 *            refreshToken值
	 * @param expriesTime
	 *            过期时间 long型
	 * @param uid
	 *            用户的Id
	 * @param name
	 *            用户的名称
	 * @param headurl
	 *            用户的头像url地址
	 */
	public void storeAccessToken(String accessToken, String refreshToken,
			long expriesTime, int uid, String name, String headurl,
			String headurl_tiny, String headurl_main, String headurl_large) {
		mAccessTokenManager.storeAccessToken(accessToken, refreshToken,
				expriesTime, uid, name, headurl, headurl_tiny, headurl_main,
				headurl_large);
	}

	/**
	 * 获取签名
	 * 
	 * @param paramMap
	 * @param secret
	 * @return
	 */
	public String getSignature(Map<String, String> paramMap, String secret) {
		List<String> paramList = new ArrayList<String>(paramMap.size());
		// 1、参数格式化
		for (Map.Entry<String, String> param : paramMap.entrySet()) {
			paramList.add(param.getKey() + "=" + param.getValue());
		}
		// 2、排序并拼接成一个字符串
		Collections.sort(paramList);
		StringBuffer buffer = new StringBuffer();
		for (String param : paramList) {
			buffer.append(param);
		}
		// 3、追加script key
		buffer.append(secret);
		// 4、将拼好的字符串转成MD5值
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			try {
				for (byte b : md.digest(buffer.toString().getBytes("UTF-8"))) {
					result.append(Integer.toHexString((b & 0xf0) >>> 4));
					result.append(Integer.toHexString(b & 0x0f));
				}
			} catch (UnsupportedEncodingException e) {
				for (byte b : md.digest(buffer.toString().getBytes())) {
					result.append(Integer.toHexString((b & 0xf0) >>> 4));
					result.append(Integer.toHexString(b & 0x0f));
				}
			}
			return result.toString();
		} catch (java.security.NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}