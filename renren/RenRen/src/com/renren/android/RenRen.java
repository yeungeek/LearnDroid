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
	 * ��ȡAccessToken
	 * 
	 * @return AccessToken
	 */
	public String getAccessToken() {
		return mAccessTokenManager.getmAccessToken();
	}

	/**
	 * ��ȡ�û�����
	 * 
	 * @return
	 */
	public String getUserName() {
		return mAccessTokenManager.getmName();
	}

	/**
	 * ��ȡ�û�ID
	 * 
	 * @return
	 */
	public int getUserId() {
		return mAccessTokenManager.getmUid();
	}

	/**
	 * ��ȡ�û�ͷ��
	 * 
	 * @return
	 */
	public String getUserHeadUrl() {
		return mAccessTokenManager.getmHeadurl();
	}

	/**
	 * ��ȡ�û�Сͷ��
	 * 
	 * @return
	 */
	public String getUserHeadUrl_Tiny() {
		return mAccessTokenManager.getmHeadurl_Tiny();
	}

	/**
	 * ��ȡ�û���ͷ��
	 * 
	 * @return
	 */
	public String getUserHeadUrl_Main() {
		return mAccessTokenManager.getmHeadurl_Main();
	}

	/**
	 * ��ȡ�û���ͷ��
	 * 
	 * @return
	 */
	public String getUserHeadUrl_Large() {
		return mAccessTokenManager.getMheadurl_Large();
	}

	/**
	 * ���ص�ǰ�Ƿ�洢AccessToken
	 * 
	 * @return true-���� false-������
	 */
	public boolean isAccessTokenExist() {
		return mAccessTokenManager.isAccessTokenExist();
	}

	/**
	 * ����AccessToken
	 * 
	 * @param accessToken
	 *            accessTokenֵ
	 * @param refreshToken
	 *            refreshTokenֵ
	 * @param expriesTime
	 *            ����ʱ�� long��
	 * @param uid
	 *            �û���Id
	 * @param name
	 *            �û�������
	 * @param headurl
	 *            �û���ͷ��url��ַ
	 */
	public void storeAccessToken(String accessToken, String refreshToken,
			long expriesTime, int uid, String name, String headurl,
			String headurl_tiny, String headurl_main, String headurl_large) {
		mAccessTokenManager.storeAccessToken(accessToken, refreshToken,
				expriesTime, uid, name, headurl, headurl_tiny, headurl_main,
				headurl_large);
	}

	/**
	 * ��ȡǩ��
	 * 
	 * @param paramMap
	 * @param secret
	 * @return
	 */
	public String getSignature(Map<String, String> paramMap, String secret) {
		List<String> paramList = new ArrayList<String>(paramMap.size());
		// 1��������ʽ��
		for (Map.Entry<String, String> param : paramMap.entrySet()) {
			paramList.add(param.getKey() + "=" + param.getValue());
		}
		// 2������ƴ�ӳ�һ���ַ���
		Collections.sort(paramList);
		StringBuffer buffer = new StringBuffer();
		for (String param : paramList) {
			buffer.append(param);
		}
		// 3��׷��script key
		buffer.append(secret);
		// 4����ƴ�õ��ַ���ת��MD5ֵ
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