package com.anhuioss.crowdroid.settings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.anhuioss.crowdroid.util.SohuClientUtil;
import com.mime.qweibo.utils.Base64Encoder;

public class SohuOAuthConstant {
	private String encoding = "utf-8";

	private String oauthRequestMethod = "GET";
	
	private String baseUrl;
	
	private static String consumerKey;
	
	private static String consumerSecret;
	
	private static String oauthToken;
	
	private static String oauthTokenSecret;
	
	private static String oauthVerifier;
	
	private String oauthNonce;
	
	private String oauthTimestamp;
	
	private String oauthSignatureMethod = "HMAC-SHA1";

	private String oauthVersion = "1.0";
	
	private String oauthSignature;
	
	private static SohuOAuthConstant instance;
	
	private Random random = new Random();

	private String oauthCallback = "";
	
	private static final String SOHU_APPKEY = "FL2p2T30VCEjYdYC9GJg";
	
	private static final String SOHU_APPSECRET = "1qfIBY6CN5nmq!^OpPGN6qs%5Cli1NX-W^Jv67SM";
	
	private static final String SOHU_REQUEST_TOKEN_URL = "http://api.t.sohu.com/oauth/request_token";
	
	private static final String SOHU_AUTHORIZE_URL = "http://api.t.sohu.com/oauth/authorize";
	
	private static final String SOHU_ACCESS_TOKEN_URL = "http://api.t.sohu.com/oauth/access_token";

	public static void clear() {
		oauthToken = null;
		oauthTokenSecret = null;
		oauthVerifier = null;
	}

	public void setOauthTokenAndOauthTokenSercet(String oauth_token,
			String oauth_token_secret, String oauth_verifier) {
		this.oauthToken = oauth_token;
		this.oauthTokenSecret = oauth_token_secret;
		this.oauthVerifier = oauth_verifier;
	}

	public static synchronized SohuOAuthConstant getInstance() {
		consumerKey = SOHU_APPKEY;
		consumerSecret = SOHU_APPSECRET;
		if (instance == null) {
			instance = new SohuOAuthConstant();
		}
		return instance;
	}
	
	public void setKeyAndSecret(String key, String secret) {
		consumerKey = key;
		consumerSecret = secret;
	}

	public String getRequestUrl() throws Exception {
		this.oauthNonce = getOAuthNonce();
		this.oauthTimestamp = getOAuthTimestamp();
		String baseUrlString = getBaseString();
		this.oauthSignature = getOAuthSignature(baseUrlString);

		return this.baseUrl + "?" + baseUrlString + "&oauth_signature="
				+ URLEncoder.encode(this.oauthSignature, this.encoding);
	}

	private String getOAuthNonce() {
		return String.valueOf(this.random.nextInt(9876599) + 123400);
	}

	private String getOAuthTimestamp() {
		return String.valueOf(System.currentTimeMillis()).substring(0, 10);
	}

	private String getBaseString() throws UnsupportedEncodingException {
		String bsss = "";

		bsss = bsss + "oauth_consumer_key="
				+ URLEncoder.encode(consumerKey, this.encoding);
		bsss = bsss + "&oauth_nonce="
				+ URLEncoder.encode(this.oauthNonce, this.encoding);
		bsss = bsss + "&oauth_signature_method="
				+ URLEncoder.encode(this.oauthSignatureMethod, this.encoding);
		bsss = bsss + "&oauth_timestamp="
				+ URLEncoder.encode(this.oauthTimestamp, this.encoding);
		if ((this.oauthToken != null) && (this.oauthToken.length() > 0)) {
			bsss = bsss + "&oauth_token="
					+ URLEncoder.encode(this.oauthToken, this.encoding);
		}
		if ((this.oauthVerifier != null) && (this.oauthVerifier.length() > 0)) {
			bsss = bsss + "&oauth_verifier="
					+ URLEncoder.encode(this.oauthVerifier, this.encoding);
		}
		bsss = bsss + "&oauth_version="
				+ URLEncoder.encode(this.oauthVersion, this.encoding);
		return bsss;
	}

	public String getOAuthSignature(String data) throws Exception {
		String oauthSignature = null;
		byte[] byteHMAC = (byte[]) null;
		try {
			String bss = this.oauthRequestMethod + "&"
					+ URLEncoder.encode(this.baseUrl, this.encoding) + "&";
			String bsss = URLEncoder.encode(data, this.encoding);
			String urlString = bss + bsss;
			String oauthKey = URLEncoder.encode(consumerSecret, this.encoding)
					+ "&"
					+ (((this.oauthTokenSecret == null) || (this.oauthTokenSecret
							.equals(""))) ? "" : URLEncoder.encode(
							this.oauthTokenSecret, this.encoding));
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(
					oauthKey.getBytes("US-ASCII"), "HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(urlString.getBytes("US-ASCII"));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
		}
		new Base64Encoder();
		oauthSignature = Base64Encoder.encode(byteHMAC);
		return oauthSignature;
	}

	private String getOAuthTokenAndSecret() {
		String oauthTokenAndSecret = null;
		this.baseUrl = "http://api.t.sohu.com/oauth/request_token";
		try {
			String url = getRequestUrl();
			oauthTokenAndSecret = SohuClientUtil.get(url, this.encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oauthTokenAndSecret;
	}

	public String getAccessTokenAndSecret() {
		String accessTokenAndSecret = null;
		this.baseUrl = "http://api.t.sohu.com/oauth/access_token";
		try {
			String url = getRequestUrl();
			accessTokenAndSecret = SohuClientUtil.get(url, this.encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessTokenAndSecret;
	}

	public void separateTokenAndSecret(String sTokenAndSecret) {
		String[] tokenAndSecret = sTokenAndSecret.split("&");
		String[] token = tokenAndSecret[0].split("=");
		String[] secret = tokenAndSecret[1].split("=");
		this.oauthToken = token[1];
		this.oauthTokenSecret = secret[1];
	}

	public String getAuthorizUrl() {
		String oauthTokenAndSecret = getOAuthTokenAndSecret();
		separateTokenAndSecret(oauthTokenAndSecret);
		String token = "oauth_token=" + this.oauthToken;
		String url = "http://api.t.sohu.com/oauth/authorize?" + token + "&oauth_callback=oob&clientType=phone";

		return url;
	}

	public void setOAuthVerifier(String oauthVerifier) {
		this.oauthVerifier = oauthVerifier;
	}

	public String getOAuthToken() {
		return this.oauthToken;
	}

	public void setOAuthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}

	public String getOAuthTokenSecret() {
		return this.oauthTokenSecret;
	}

	public void setOAuthTokenSecret(String oauthTokenSecret) {
		this.oauthTokenSecret = oauthTokenSecret;
	}

	public void setOAuthCallback(String oauthCallback) {
		this.oauthCallback = oauthCallback;
	}
}
