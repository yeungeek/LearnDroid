package com.anhuioss.crowdroid.util;

import it.sauronsoftware.base64.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.anhuioss.crowdroid.IGeneral;

/**
 * @return String msg, int statusCode
 * 
 */
public class HttpCommunicator {

	/** Consumer_Kye for Twitter */
	public static String CONSUMER_KEY_TWITTER = "rbkewUHsJc0pdKMmt3PHQ";

	/** Consumer_Secret for Twitter */
	public static String CONSUMER_SECRET_TWITTER = "qnfmOYFFiqzen67Ww6TxqlaeY45Ct7Cm1hOriyU";

	public static final String CONSUMER_KEY_SINA = "4097074858";

	public static final String CONSUMER_SECRET_SINA = "e6c623e6d434130245901e9a859c200a";

	public static final String CONSUMER_KEY_SOHU = "FL2p2T30VCEjYdYC9GJg";

	public static final String CONSUMER_SECRET_SOHU = "1qfIBY6CN5nmq!^OpPGN6qs%5Cli1NX-W^Jv67SM";

	private static final int SET_CONNECTION_TIMEOUT = 50000;

	private static final int SET_SOCKET_TIMEOUT = 200000;

	// -----------------------------------------------------------------------------------
	/**
	 * Oauth2.0 HttpGet
	 */
	// -----------------------------------------------------------------------------------
	protected static CommResult HttpGet(Context context, String url)
			throws IOException {
		synchronized ("http get") {
			CommResult result = new CommResult();

			HttpGet httpGet = new HttpGet(url);

			HttpClient httpClient = getNewHttpClient(context);

			HttpResponse response = httpClient.execute(httpGet);

			InputStream in = response.getEntity().getContent();

			int statusCode = response.getStatusLine().getStatusCode();

			String message = InputStreamToString(in);

			result.setMessage(message);

			result.setResponseCode(String.valueOf(statusCode));

			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Oaunth2.0 HttpPost
	 */
	// -----------------------------------------------------------------------------------
	protected static CommResult HttpPost(Context context, String url,
			HashMap<String, String> map) {
		synchronized ("http post") {
			CommResult result = new CommResult();

			HttpClient httpClient = getNewHttpClient(context);

			HttpPost httpPost = new HttpPost(url);

			ArrayList<BasicNameValuePair> postDate = new ArrayList<BasicNameValuePair>();

			Set<String> set = map.keySet();

			Iterator<String> iterator = set.iterator();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				postDate.add(new BasicNameValuePair(key, map.get(key)));

			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						postDate, HTTP.UTF_8);
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost);

				InputStream in = response.getEntity().getContent();
				int statusCode = response.getStatusLine().getStatusCode();
				String message = InputStreamToString(in);

				result.setMessage(message);
				result.setResponseCode(String.valueOf(statusCode));

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Oaunth2.0 Upload Images
	 */
	// -----------------------------------------------------------------------------------
	protected static CommResult HttpPostImage(Context context, String url,
			HashMap<String, String> map) {
		synchronized ("http post") {
			CommResult result = new CommResult();

			HttpClient httpClient = getNewHttpClient(context);

			HttpPost httpPost = new HttpPost(url);

			MultipartEntity mpEntity = new MultipartEntity();
			FileBody file = new FileBody(new File((String) map.get("filePath")));

			mpEntity.addPart("pic", file);
			try {
				mpEntity.addPart("status",
						new StringBody((String) map.get("status")));
				if (map.containsKey("poiid"))
					mpEntity.addPart("poiid",
							new StringBody((String) map.get("poiid")));
				mpEntity.addPart("access_token",
						new StringBody((String) map.get("access_token")));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				httpPost.setEntity(mpEntity);
				HttpResponse response = httpClient.execute(httpPost);

				InputStream in = response.getEntity().getContent();
				int statusCode = response.getStatusLine().getStatusCode();
				String message = InputStreamToString(in);

				result.setMessage(message);
				result.setResponseCode(String.valueOf(statusCode));

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}

	}

	protected static CommResult HttpPostImage1(Context context, String url,
			HashMap<String, String> map) {
		synchronized ("http post") {
			CommResult result = new CommResult();

			HttpClient httpClient = getNewHttpClient(context);

			HttpPost httpPost = new HttpPost(url);

			MultipartEntity mpEntity = new MultipartEntity();
			FileBody file = new FileBody(new File((String) map.get("filePath")));

			mpEntity.addPart("pic", file);
			try {
				// mpEntity.addPart("status", new
				// StringBody((String)map.get("status")));
				mpEntity.addPart("access_token",
						new StringBody((String) map.get("access_token")));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				httpPost.setEntity(mpEntity);
				HttpResponse response = httpClient.execute(httpPost);

				InputStream in = response.getEntity().getContent();
				int statusCode = response.getStatusLine().getStatusCode();
				String message = InputStreamToString(in);

				result.setMessage(message);
				result.setResponseCode(String.valueOf(statusCode));

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Send Request to Twitter Server and get Input Stream as a result.
	 */
	// -----------------------------------------------------------------------------------
	protected static CommResult httpGet(String url, String name, String password)
			throws IOException {

		synchronized ("http get") {

			CommResult result = new CommResult();

			HttpGet httpGet = new HttpGet(url);

			// Prepare Http Client
			DefaultHttpClient httpClient = new DefaultHttpClient();

			// Auth
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					new UsernamePasswordCredentials(name, password));

			// Connection Param
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 20000);
			// Connect
			HttpResponse response = httpClient.execute(httpGet);
			InputStream in = response.getEntity().getContent();
			String msg = InputStreamToString(in);

			int code = response.getStatusLine().getStatusCode();

			// Set Result
			result.setResponseCode(String.valueOf(code));
			result.setMessage(msg);

			return result;
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Send Update Request to Twitter Server.
	 * 
	 * @param url
	 *            the URL for requesting.
	 * @param map
	 *            store the parameter for requesting.
	 * @return String
	 */
	// -----------------------------------------------------------------------------------
	protected static CommResult httpPost(String url,
			HashMap<String, String> map, String name, String password)
			throws IOException {

		synchronized ("http post") {

			// Prepare result
			CommResult result = new CommResult();

			// Prepare Key
			DefaultHttpClient httpClient = new DefaultHttpClient();

			// Auth
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					new UsernamePasswordCredentials(name, password));

			// Connection Param
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 20000);
			HttpPost httpPost = new HttpPost(url);

			// Set Post Parameters
			ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
			Set<String> set = map.keySet();
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				postData.add(new BasicNameValuePair(key, map.get(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData,
					HTTP.UTF_8);
			httpPost.setEntity(entity);

			// Add Parameter in order to avoid 417 Error
			httpPost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			// Sine Request With Token
			HttpResponse response = httpClient.execute(httpPost);

			int code = response.getStatusLine().getStatusCode();
			InputStream in = response.getEntity().getContent();
			String msg = InputStreamToString(in);

			// Get result
			result.setResponseCode(String.valueOf(code));
			result.setMessage(msg);

			in.close();

			return result;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Http Post
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("finally")
	public static CommResult httpPostForLongTweet(String url,
			HashMap<String, String> map) {

		// Prepare Result Data
		CommResult chResult = new CommResult();
		int statusCode = 0;
		String msg = null;

		try {
			HttpPost httpPost = new HttpPost(url);

			// Set Post Parameters
			ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
			Set<String> set = map.keySet();
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				postData.add(new BasicNameValuePair(key, map.get(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData,
					HTTP.UTF_8);
			httpPost.setEntity(entity);

			// Prepare HttpClient
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000); // Set
																		// Connection
																		// Time
																		// Out
			HttpConnectionParams.setSoTimeout(params, 10000); // Set Data
																// Require
																// Time Out

			// Add Parameter in order to avoid 417 Error
			httpPost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			// Get Response
			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();
			InputStream in = response.getEntity().getContent();
			msg = InputStreamToString(in);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Set result
			chResult.setMessage(msg);
			chResult.setResponseCode(String.valueOf(statusCode));
			return chResult;
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Http Get (OAuth)
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("finally")
	protected static CommResult httpGetOauth(String service, String url,
			String accessToken, String tokenSecret) {

		synchronized ("http get oauth") {

			// Prepare Result Data
			CommResult chResult = new CommResult();
			int statusCode = 0;
			String msg = null;

			try {
				CommonsHttpOAuthConsumer consumer = null;
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Prepare OAuth Key
					consumer = new CommonsHttpOAuthConsumer(
							CONSUMER_KEY_TWITTER, CONSUMER_SECRET_TWITTER);
					consumer.setTokenWithSecret(accessToken, tokenSecret);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Prepare OAuth Key
					consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY_SINA,
							CONSUMER_SECRET_SINA);
					consumer.setTokenWithSecret(accessToken, tokenSecret);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Prepare OAuth Key
					consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY_SOHU,
							CONSUMER_SECRET_SOHU);
					consumer.setTokenWithSecret(accessToken, tokenSecret);
				}

				HttpGet httpGet = new HttpGet(url);

				// Prepare HttpClient
				HttpClient httpClient = new DefaultHttpClient();
				HttpParams params = httpClient.getParams();
				HttpConnectionParams.setConnectionTimeout(params, 5000);
				HttpConnectionParams.setSoTimeout(params, 10000);

				// Sign Request With Token
				consumer.sign(httpGet);

				// Get Response
				HttpResponse response = httpClient.execute(httpGet);
				statusCode = response.getStatusLine().getStatusCode();
				InputStream in = response.getEntity().getContent();
				msg = InputStreamToString(in);

			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// Set result
				chResult.setMessage(msg);
				chResult.setResponseCode(String.valueOf(statusCode));
				return chResult;
			}

		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Http Get (Basic Auth)
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("finally")
	protected static CommResult httpGetBasic(String url, String user,
			String passwd) {

		synchronized ("http get basic") {

			// Prepare Result Data
			CommResult chResult = new CommResult();
			int statusCode = 0;
			String msg = null;

			try {

				URL httpurl = new URL(url);
				HttpURLConnection request = (HttpURLConnection) httpurl
						.openConnection();

				// Basic Auth
				byte[] token = (user + ":" + passwd).getBytes("utf-8");
				String check = "Basic "
						+ new String(Base64.encode(token), "utf-8");
				request.setRequestProperty("Authorization", check);

				// Get Response
				request.connect();
				statusCode = request.getResponseCode();
				InputStream in = request.getInputStream();
				msg = InputStreamToString(in);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				chResult.setMessage(msg);
				chResult.setResponseCode(String.valueOf(statusCode));
				return chResult;
			}
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * Http Post (OAuth)
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("finally")
	protected static CommResult httpPostOauth(String service, String url,
			HashMap<String, String> map, String accessToken, String accessSecret) {

		synchronized ("http post oauth") {

			// Prepare Result Data
			CommResult chResult = new CommResult();
			int statusCode = 0;
			String msg = null;

			try {
				CommonsHttpOAuthConsumer consumer = null;
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Prepare OAuth Key
					consumer = new CommonsHttpOAuthConsumer(
							CONSUMER_KEY_TWITTER, CONSUMER_SECRET_TWITTER);
					consumer.setTokenWithSecret(accessToken, accessSecret);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Prepare OAuth Key
					consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY_SINA,
							CONSUMER_SECRET_SINA);
					consumer.setTokenWithSecret(accessToken, accessSecret);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Prepare OAuth Key
					consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY_SOHU,
							CONSUMER_SECRET_SOHU);
					consumer.setTokenWithSecret(accessToken, accessSecret);
				}

				HttpPost httpPost = new HttpPost(url);

				// Set Post Parameters
				ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
				Set<String> set = map.keySet();
				Iterator<String> iterator = set.iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					postData.add(new BasicNameValuePair(key, map.get(key)));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						postData, HTTP.UTF_8);
				httpPost.setEntity(entity);

				// Prepare HttpClient
				HttpClient httpClient = new DefaultHttpClient();
				HttpParams params = httpClient.getParams();
				HttpConnectionParams.setConnectionTimeout(params, 5000);
				HttpConnectionParams.setSoTimeout(params, 10000);

				// Add Parameter in order to avoid 417 Error
				httpPost.getParams().setBooleanParameter(
						CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

				// Sign Request With Token
				consumer.sign(httpPost);

				// Get Response
				HttpResponse response = httpClient.execute(httpPost);
				statusCode = response.getStatusLine().getStatusCode();
				InputStream in = response.getEntity().getContent();
				msg = InputStreamToString(in);

			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// Set result
				chResult.setMessage(msg);
				chResult.setResponseCode(String.valueOf(statusCode));
				return chResult;
			}
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Http Post (Basic)
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("finally")
	protected static CommResult httpPostBasic(String url,
			HashMap<String, String> map, String user, String passwd) {

		synchronized ("http post basic") {

			// Prepare Result Data
			CommResult chResult = new CommResult();
			int statusCode = 0;
			String msg = null;

			try {

				URL httpurl = new URL(url);
				HttpURLConnection request = (HttpURLConnection) httpurl
						.openConnection();
				request.setDoOutput(true);

				// Basic Auth
				byte[] token = (user + ":" + passwd).getBytes("utf-8");
				String check = "Basic "
						+ new String(Base64.encode(token), "utf-8");
				request.setRequestProperty("Authorization", check);

				request.connect();

				// Put Post Params
				OutputStreamWriter bos = new OutputStreamWriter(
						request.getOutputStream(), "utf-8");
				Set<String> set = map.keySet();
				Iterator<String> iterator = set.iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					bos.write(key + "=" + map.get(key));
				}
				bos.flush();
				bos.close();

				System.out.println(bos.toString());

				// Get Response
				statusCode = request.getResponseCode();
				InputStream in = request.getInputStream();
				msg = InputStreamToString(in);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				chResult.setMessage(msg);
				chResult.setResponseCode(String.valueOf(statusCode));
				return chResult;
			}
		}

	}

	// -----------------------------------------------------------------------------------
	/**
	 * RenRen Http Post
	 */
	// -----------------------------------------------------------------------------------

	@SuppressWarnings("finally")
	protected static CommResult httpPost(String url, HashMap<String, String> map) {

		synchronized ("http post") {

			// Prepare result
			CommResult result = new CommResult();

			// Prepare Key
			DefaultHttpClient httpClient = new DefaultHttpClient();

			// Connection Param
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 20000);
			HttpPost httpPost = new HttpPost(url);

			// Set Post Parameters
			ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
			Set<String> set = map.keySet();
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				postData.add(new BasicNameValuePair(key, map.get(key)));
			}
			UrlEncodedFormEntity entity;
			try {
				entity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);

				httpPost.setEntity(entity);

				// Sine Request With Token
				HttpResponse response = httpClient.execute(httpPost);

				int code = response.getStatusLine().getStatusCode();
				InputStream in = response.getEntity().getContent();
				String msg = InputStreamToString(in);

				// Get result
				result.setResponseCode(String.valueOf(code));
				result.setMessage(msg);

				in.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				return result;
			}

		}
	}

	/**
	 * 获取签名值
	 * 
	 * @param paramList
	 * @param secret
	 * @return
	 */
	public String getSignature(List<String> paramList, String secret) {
		Collections.sort(paramList);

		StringBuffer buffer = new StringBuffer();
		for (String param : paramList) {
			buffer.append(param); // 将参数键值对，以字典序升序排列后，拼接在一起
		}

		buffer.append(secret); // 符串末尾追加上应用的Secret Key
		try { // 下面是将拼好的字符串转成MD5值，然后返回
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

		}

		return null;
	}

	// -----------------------------------------------------------------------------------
	/**
	 * Convert InputStream to String
	 * 
	 * @param is
	 *            a instance of InputStream
	 * @return String
	 */
	// -----------------------------------------------------------------------------------
	@SuppressWarnings("finally")
	protected static String InputStreamToString(InputStream is) {

		String value = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			StringBuffer buf = new StringBuffer();
			String str;

			while ((str = reader.readLine()) != null) {
				buf.append(str);
			}

			value = buf.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return value;
		}

	}

	public static HttpClient getNewHttpClient(Context context) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 20000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			// Set the default socket timeout (SO_TIMEOUT) // in
			// milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setConnectionTimeout(params,
					SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			HttpClient client = new DefaultHttpClient(ccm, params);
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
