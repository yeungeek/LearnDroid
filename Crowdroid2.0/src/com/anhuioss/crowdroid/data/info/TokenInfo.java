package com.anhuioss.crowdroid.data.info;

/**
 * 
 * 
 * 自定义信息类，保存Oauth2.0认证后，获得的AccessToken，有效期时间，以及Uid
 * 
 */
public class TokenInfo {

	private String accessToken;

	private String expires_in;

	private String refresh_token;

	private String remind_in;

	private String uid;

	/**
	 * Set Token Info
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setExpiresIn(String expires_in) {
		this.expires_in = expires_in;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public void setRemindIn(String remind_in) {
		this.remind_in = remind_in;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Get Token Info
	 */
	public String getAccessToken() {
		return accessToken;
	}

	public String getExpiresIn() {
		return expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public String getRemindIn() {
		return remind_in;
	}

	public String getUid() {
		return uid;
	}
}
