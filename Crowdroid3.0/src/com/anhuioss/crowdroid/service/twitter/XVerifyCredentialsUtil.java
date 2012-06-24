package com.anhuioss.crowdroid.service.twitter;

import java.net.URI;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import android.util.Log;

public class XVerifyCredentialsUtil {

	private static final String LOG_TAG = "XVerifyCredentialsUtil";

	// ----------------------------------------------------------------------------------
	/**
	 * 
	 * @param oauthToken
	 * @param accessToken
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	public static String generateTwitterOAuthEchoCredentials(String oauthToken,
			String accessToken) {
		HashMap<String, String> requestTokenHash = new HashMap<String, String>();
		requestTokenHash.put("oauth_consumer_key",
				TwitterCommHandler.CONSUMER_KEY);
		requestTokenHash.put("oauth_token", oauthToken);
		requestTokenHash.put("oauth_signature_method", "HMAC-SHA1");
		requestTokenHash.put("oauth_timestamp", getTimestamp());
		requestTokenHash.put("oauth_nonce", getNonce());
		requestTokenHash.put("oauth_version", "1.0");
		requestTokenHash.put(
						"oauth_signature",
						generateAccessSignature(
								generateSignatureBaseString(
										"GET",
										"https://api.twitter.com/1/account/verify_credentials.json",
										requestTokenHash, null),
								TwitterCommHandler.CONSUMER_SECRET, accessToken));
		return generateAuthHeader(requestTokenHash);
	}

	// ----------------------------------------------------------------------------------
	/**
	 * generate access signature from signature base string
	 * 
	 * @param signatureBaseString
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	private static String generateAccessSignature(String signatureBaseString,
			String consumerSecret, String accessToken) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(StringCodec.urlencode(consumerSecret));
		buffer.append("&");
		buffer.append(StringCodec.urlencode(accessToken));
		String key = buffer.toString();
		Log.i(LOG_TAG, "access signature key = " + key);
		return StringCodec.hmacSha1Digest(signatureBaseString, key);
	}

	// ----------------------------------------------------------------------------------
	/**
	 * 
	 * @param url
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	private static String getNormalizedURLString(String url) {
		if (url != null) {
			try {
				StringBuffer buffer = new StringBuffer();
				URI uri = new URI(url);
				String scheme = uri.getScheme().toLowerCase();
				buffer.append(scheme); // scheme
				buffer.append("://");
				buffer.append(uri.getHost().toLowerCase()); // host
				int port = uri.getPort();
				if (port != -1
						&& ((scheme.compareTo("http") == 0 && port != 80) || (scheme
								.compareTo("https") == 0 && port != 433))) {
					buffer.append(":");
					buffer.append(port); // port
				}
				buffer.append(uri.getPath()); // path

				String result = buffer.toString();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * 
	 * @param method
	 * @param url
	 * @param params
	 * @param getOrPostParams
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	private static String generateSignatureBaseString(String method,
			String url, HashMap<String, String> params,
			HashMap<String, String> getOrPostParams) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(method);
		buffer.append("&");
		buffer.append(StringCodec.urlencode(getNormalizedURLString(url)));
		buffer.append("&");
		buffer.append(StringCodec.urlencode(getNormalizedRequestParameter(
				params, getOrPostParams)));
		String result = buffer.toString();
		return result;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * 
	 * @param params
	 * @param getOrPostParams
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	private static String getNormalizedRequestParameter(
			HashMap<String, String> params,
			HashMap<String, String> getOrPostParams) {
		StringBuffer buffer = new StringBuffer();
		HashMap<String, String> dict = new HashMap<String, String>();
		if (params != null)
			for (String key : params.keySet())
				dict.put(key, params.get(key));
		if (getOrPostParams != null)
			for (String key : getOrPostParams.keySet())
				dict.put(key, getOrPostParams.get(key));
		if (dict.size() > 0) {
			TreeSet<String> sortedKeys = new TreeSet<String>(dict.keySet()); // sort
																				// keys
			for (String key : sortedKeys) {
				if (key.compareTo("realm") == 0) // skip realm
					continue;
				if (buffer.length() > 0)
					buffer.append("&");
				buffer.append(StringCodec.urlencode(key));
				buffer.append("=");
				buffer.append(StringCodec.urlencode(dict.get(key)));
			}
		}
		String result = buffer.toString();
		return result;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Generate AuthHeader
	 * 
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	private static String generateAuthHeader(HashMap<String, String> params) {
		StringBuffer buffer = new StringBuffer();
		for (String key : params.keySet()) {
			if (buffer.length() > 0)
				buffer.append(",");
			else
				buffer.append("OAuth ");
			buffer.append(StringCodec.urlencode(key));
			buffer.append("=\"");
			buffer.append(StringCodec.urlencode(params.get(key)));
			buffer.append("\"");
		}
		String result = buffer.toString();
		Log.i(LOG_TAG, "auth header = " + result);
		return result;
	}

	// ----------------------------------------------------------------------------------
	/**
	 * generate current timestamp
	 * 
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	private static String getTimestamp() {
		return "" + (System.currentTimeMillis() / 1000);
	}

	// ----------------------------------------------------------------------------------
	/**
	 * generate nonce value
	 * 
	 * @return
	 */
	// ----------------------------------------------------------------------------------
	private static String getNonce() {
		Random rand = new Random(System.currentTimeMillis());
		return StringCodec.md5sum("" + rand.nextLong());
	}

}
