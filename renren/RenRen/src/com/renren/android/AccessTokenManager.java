package com.renren.android;

import android.content.Context;
import android.content.SharedPreferences;

public class AccessTokenManager {
	public String getmHeadurl_Tiny() {
		return mHeadurl_Tiny;
	}

	public void setmHeadurl_Tiny(String mHeadurl_Tiny) {
		this.mHeadurl_Tiny = mHeadurl_Tiny;
	}

	public String getmHeadurl_Main() {
		return mHeadurl_Main;
	}

	public void setmHeadurl_Main(String mHeadurl_Main) {
		this.mHeadurl_Main = mHeadurl_Main;
	}

	public String getMheadurl_Large() {
		return mheadurl_Large;
	}

	public void setMheadurl_Large(String mheadurl_Large) {
		this.mheadurl_Large = mheadurl_Large;
	}

	public String getmAccessToken() {
		return mAccessToken;
	}

	public void setmAccessToken(String mAccessToken) {
		this.mAccessToken = mAccessToken;
	}

	public String getmRefreshToken() {
		return mRefreshToken;
	}

	public void setmRefreshToken(String mRefreshToken) {
		this.mRefreshToken = mRefreshToken;
	}

	public long getmExpriesTime() {
		return mExpriesTime;
	}

	public void setmExpriesTime(long mExpriesTime) {
		this.mExpriesTime = mExpriesTime;
	}

	public long getmCreateTime() {
		return mCreateTime;
	}

	public void setmCreateTime(long mCreateTime) {
		this.mCreateTime = mCreateTime;
	}

	public int getmUid() {
		return mUid;
	}

	public void setmUid(int mUid) {
		this.mUid = mUid;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmHeadurl() {
		return mHeadurl;
	}

	public void setmHeadurl(String mHeadurl) {
		this.mHeadurl = mHeadurl;
	}

	private static final String RENREN_CONFIG = "renren_config";
	private static final String RENREN_CONFIG_ACCESSTOKEN = "renren_config_accesstoken";
	private static final String RENREN_CONFIG_REFRESHTOKEN = "renren_config_refreshtoken";
	private static final String RENREN_CONFIG_EXPRIESTIME = "renren_config_expriestime";
	private static final String RENREN_CONFIG_CREATETIME = "renren_config_createtime";
	private static final String RENREN_CONFIG_UID = "renren_config_uid";
	private static final String RENREN_CONFIG_NAME = "renren_config_name";
	private static final String RENREN_CONFIG_HEADURL = "renren_config_headurl";
	private static final String RENREN_CONFIG_HEADURL_TINY = "renren_config_headurl_tiny";
	private static final String RENREN_CONFIG_HEADURL_MAIN = "renren_config_headurl_main";
	private static final String RENREN_CONFIG_HEADURL_LARGE = "renren_config_headurl_large";
	private static final long ONE_HOUR = 60 * 60 * 1000;
	private Context mContext;

	private String mAccessToken;
	private String mRefreshToken;
	private long mExpriesTime;
	private long mCreateTime;
	private int mUid;
	private String mName;
	private String mHeadurl;
	private String mHeadurl_Tiny;
	private String mHeadurl_Main;
	private String mheadurl_Large;

	public AccessTokenManager(Context context) {
		mContext = context;
		initAccessToken();
	}

	/**
	 * 初始化获取AccessToken
	 */
	private void initAccessToken() {
		if (mContext == null) {
			return;
		}
		SharedPreferences sp = mContext.getSharedPreferences(RENREN_CONFIG,
				Context.MODE_PRIVATE);
		mAccessToken = sp.getString(RENREN_CONFIG_ACCESSTOKEN, null);
		mRefreshToken = sp.getString(RENREN_CONFIG_REFRESHTOKEN, null);
		mExpriesTime = sp.getLong(RENREN_CONFIG_EXPRIESTIME, 0);
		mCreateTime = sp.getLong(RENREN_CONFIG_CREATETIME, 0);
		mUid = sp.getInt(RENREN_CONFIG_UID, 0);
		mName = sp.getString(RENREN_CONFIG_NAME, null);
		mHeadurl = sp.getString(RENREN_CONFIG_HEADURL, null);
		mHeadurl_Tiny = sp.getString(RENREN_CONFIG_HEADURL_TINY, null);
		mHeadurl_Main = sp.getString(RENREN_CONFIG_HEADURL_MAIN, null);
		mheadurl_Large = sp.getString(RENREN_CONFIG_HEADURL_LARGE, null);
		long expries = mCreateTime + mExpriesTime - ONE_HOUR;
		long current = System.currentTimeMillis();
		// AccessToken过期
		if (current > expries) {
			clearAccessToken();
		}
	}

	/**
	 * 返回当前是否存储AccessToken
	 * 
	 * @return true-存在 false-不存在
	 */
	public boolean isAccessTokenExist() {
		if (mAccessToken == null) {
			return false;
		}
		return true;
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
		this.mAccessToken = accessToken;
		this.mRefreshToken = refreshToken;
		this.mExpriesTime = expriesTime;
		this.mName = name;
		this.mUid = uid;
		this.mHeadurl = headurl;
		this.mHeadurl_Tiny = headurl_tiny;
		this.mHeadurl_Main = headurl_main;
		this.mheadurl_Large = headurl_large;
		this.mCreateTime = System.currentTimeMillis();
		SharedPreferences sp = mContext.getSharedPreferences(RENREN_CONFIG,
				Context.MODE_PRIVATE);
		sp.edit().putString(RENREN_CONFIG_ACCESSTOKEN, this.mAccessToken)
				.putString(RENREN_CONFIG_REFRESHTOKEN, this.mRefreshToken)
				.putLong(RENREN_CONFIG_EXPRIESTIME, this.mExpriesTime)
				.putLong(RENREN_CONFIG_CREATETIME, this.mCreateTime)
				.putInt(RENREN_CONFIG_UID, this.mUid)
				.putString(RENREN_CONFIG_NAME, this.mName)
				.putString(RENREN_CONFIG_HEADURL, this.mHeadurl)
				.putString(RENREN_CONFIG_HEADURL_TINY, this.mHeadurl_Tiny)
				.putString(RENREN_CONFIG_HEADURL_MAIN, this.mHeadurl_Main)
				.putString(RENREN_CONFIG_HEADURL_LARGE, this.mheadurl_Large)
				.commit();
	}

	/**
	 * 删除存储的AccessToken
	 */
	private void clearAccessToken() {
		SharedPreferences sp = mContext.getSharedPreferences(RENREN_CONFIG,
				Context.MODE_PRIVATE);
		sp.edit().remove(RENREN_CONFIG_ACCESSTOKEN)
				.remove(RENREN_CONFIG_REFRESHTOKEN)
				.remove(RENREN_CONFIG_EXPRIESTIME)
				.remove(RENREN_CONFIG_CREATETIME).commit();
		this.mAccessToken = null;
		this.mRefreshToken = null;
		this.mExpriesTime = 0;
		this.mCreateTime = 0;
	}

}
