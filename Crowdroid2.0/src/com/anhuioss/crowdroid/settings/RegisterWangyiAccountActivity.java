package com.anhuioss.crowdroid.settings;

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.info.TokenInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class RegisterWangyiAccountActivity extends Activity implements
		OnClickListener, ServiceConnection {

	// Define Views
	WebView webView;
	TextView pinCode;
	LinearLayout tableImage;
	LinearLayout pinTextLinear;
	RelativeLayout relative;
	Button finish;
	Button number0;
	Button number1;
	Button number2;
	Button number3;
	Button number4;
	Button number5;
	Button number6;
	Button number7;
	Button number8;
	Button number9;
	Button cancel;

	// The Contents Of The pinCode
	String message = null;

	private String url1 = "";

	private static String access_token = "";

	private static String code = "";

	// Flag For pinCode
	boolean flag = false;

	/** Consumer (OAuth) */
	private OAuthConsumer consumer;

	/** Provider (OAuth) */
	private OAuthProvider provider;

	public static final String WANGYI_API_KEY = "T8F3ZYaWOlasC33b";

	public static final String WANGYI_API_SECRET = "aLVtMul2WZPQs3ajfyBKvEQgtPBFVqc0";

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	private String accessToken;

	private String tokenSecret;

	private long refreshTime;

	private SharedPreferences tokenSharePre;

	private SharedPreferences.Editor tokenEditor;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				switch (type) {
				case CommHandler.TYPE_GET_NEW_TOKEN: {

					TokenInfo tokenInfo = new TokenInfo();
					try {
						tokenInfo = parseAccessToken(message);
						if (tokenInfo.getRefresh_token() != null
								|| tokenInfo.getRefresh_token().equals("")) {
//							refreshTime = System.currentTimeMillis();
							tokenSharePre = getSharedPreferences("token", 0);
							tokenEditor = tokenSharePre.edit();
							tokenEditor.putString("refresh_token",
									tokenInfo.getRefresh_token());
//							tokenEditor.putLong("refresh_time", refreshTime);
							tokenEditor.commit();
						}
						access_token = tokenInfo.getAccessToken();

						String uid = tokenInfo.getUid();

						getAccountInfo(access_token, uid);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				}
				case CommHandler.TYPE_VERIFY_USER: {

					// Parser
					UserInfo userInfo = new UserInfo();
					ParseHandler parseHandler = new ParseHandler();
					userInfo = (UserInfo) parseHandler.parser(service, type,
							statusCode, message);

					// Succeed
					if (userInfo.getUid() != null) {

						// Prepare Data
						Bundle bundle = new Bundle();
						bundle.putString("userName", userInfo.getScreenName());
						bundle.putString("uid", userInfo.getUid());
						bundle.putString("accessToken", access_token);
						Intent data = new Intent();
						data.putExtras(bundle);
						setResult(0, data);

						// Finish
						finish();

					} else {
						// Error
						showErrorMessage(statusCode);
					}

				}
				default: {

				}
				}

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_twitter_account);

		// Find And Initial Views
		webView = (WebView) findViewById(R.id.web_view);
		relative = (RelativeLayout) findViewById(R.id.RelativeLayout01);
		pinTextLinear = (LinearLayout) findViewById(R.id.LinearLayout01);

		CookieSyncManager.createInstance(this);
		CookieManager cm = CookieManager.getInstance();
		cm.removeSessionCookie();

		// webView.setWebViewClient(new
		// RegisterSinaAccountActivity.WeiboWebViewClient());
		//
		// webView.clearCache(true);
		// webView.getSettings().setJavaScriptEnabled(true);
		// webView.clearHistory();
		// webView.clearFormData();
		// Web View
		// webView.setWebViewClient(new WebViewClient() {
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// webView.loadUrl(url);
		// return true;
		// }
		// });
		// clear cache
		// webView.clearCache(true);
		// webView.getSettings().setJavaScriptEnabled(true);
		// webView.clearHistory();
		// webView.clearFormData();
		// webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		pinCode = (TextView) findViewById(R.id.pin_text);

		tableImage = (LinearLayout) findViewById(R.id.tableImage);
		finish = (Button) findViewById(R.id.finish_button);
		number0 = (Button) findViewById(R.id.Button00);
		number1 = (Button) findViewById(R.id.Button01);
		number2 = (Button) findViewById(R.id.Button02);
		number3 = (Button) findViewById(R.id.Button03);
		number4 = (Button) findViewById(R.id.Button04);
		number5 = (Button) findViewById(R.id.Button05);
		number6 = (Button) findViewById(R.id.Button06);
		number7 = (Button) findViewById(R.id.Button07);
		number8 = (Button) findViewById(R.id.Button08);
		number9 = (Button) findViewById(R.id.Button09);
		cancel = (Button) findViewById(R.id.ButtonCancel);

		message = pinCode.getText().toString();

		tableImage.setVisibility(View.GONE);
		relative.setVisibility(View.GONE);
		pinTextLinear.setVisibility(View.GONE);

		// Set Listener
		pinCode.setOnClickListener(this);
		finish.setOnClickListener(this);
		number0.setOnClickListener(this);
		number1.setOnClickListener(this);
		number2.setOnClickListener(this);
		number3.setOnClickListener(this);
		number4.setOnClickListener(this);
		number5.setOnClickListener(this);
		number6.setOnClickListener(this);
		number7.setOnClickListener(this);
		number8.setOnClickListener(this);
		number9.setOnClickListener(this);
		cancel.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();

		Log.i("Activity", "onStart");
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		loadRequestURL();

	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("Activity", "onStop");
		unbindService(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.finish_button: {

			// Get New Token
			String pin = pinCode.getText().toString();
			if (pin != null && pin.length() > 0) {
				getNewToken(pin);
			}

			break;
		}
		case R.id.Button00: {
			message = message + "0";
			break;
		}
		case R.id.Button01: {
			message = message + "1";
			break;
		}
		case R.id.Button02: {
			message = message + "2";
			break;
		}
		case R.id.Button03: {
			message = message + "3";
			break;
		}
		case R.id.Button04: {
			message = message + "4";
			break;
		}
		case R.id.Button05: {
			message = message + "5";
			break;
		}
		case R.id.Button06: {
			message = message + "6";
			break;
		}
		case R.id.Button07: {
			message = message + "7";
			break;
		}
		case R.id.Button08: {
			message = message + "8";
			break;
		}
		case R.id.Button09: {
			message = message + "9";
			break;
		}
		case R.id.ButtonCancel: {
			if (message.length() > 0) {

				// Delete Processing
				message = message.substring(0, message.length() - 1);
				pinCode.setText(message);

			}
			break;
		}
		case R.id.pin_text: {
			flag = !flag;
			if (flag) {
				// Set Keyboard Display
				tableImage.setVisibility(View.VISIBLE);
			} else {
				// Set The Keyboard Does Not Show
				tableImage.setVisibility(View.GONE);
			}
			break;
		}
		default: {
			// Error
			break;
		}
		}

		// Set Contexts Of The pinCode
		pinCode.setText(message);

	}

	// --------------------------------------------------------------
	/**
	 * Get the Request URL
	 */
	// --------------------------------------------------------------
	private void loadRequestURL() {

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		CookieManager cm = CookieManager.getInstance();
		cm.removeSessionCookie();

		webView.setWebViewClient(new RegisterWangyiAccountActivity.WeiboWebViewClient());
		String display = "mobile";
		// if (isPhoneDevice()) {
		// display = "mobile";
		// }
		// webView.loadUrl("https://api.t.163.com/oauth2/authorize?"
		// + "client_id="
		// + WANGYI_API_KEY
		// + "&response_type=code"
		// + "&display="
		// + display
		// + "&redirect_uri=http://www.anhuioss.com/cn/crowdroid/index.html");

		webView.loadUrl("https://api.t.163.com/oauth2/authorize?"
				+ "client_id="
				+ WANGYI_API_KEY
				+ "&response_type=token"
				+ "&display="
				+ display
				+ "&redirect_uri=http://www.anhuioss.com/cn/crowdroid/index.html");

	}

	// --------------------------------------------------------------
	/**
	 * Get New Token
	 */
	// --------------------------------------------------------------
	private void getNewToken(String pin) {

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		// parameters.put("grant_type", "authorization_code");
		// parameters.put("code", pin);
		parameters.put("grant_type", "refresh_token");
		parameters.put("refresh_token", pin);
		try {
			// HTTP Communication
			apiServiceInterface.request(IGeneral.SERVICE_NAME_WANGYI,
					CommHandler.TYPE_GET_NEW_TOKEN, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	// --------------------------------------------------------------
	/**
	 * Register Tocken Info to DB
	 */
	// --------------------------------------------------------------
	private void getAccountInfo(String accessToken, String uid) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("uid", uid);

		try {
			apiServiceInterface.request(IGeneral.SERVICE_NAME_WANGYI,
					CommHandler.TYPE_VERIFY_USER, apiServiceListener, params);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private void showErrorMessage(String statusCode) {

		if (!statusCode.equals("200")) {

			int code = Integer.parseInt(statusCode);
			selectErrorMessageToShow(code);
		}
	}

	private void selectErrorMessageToShow(int statusCode) {

		switch (statusCode) {

		case 400:

		case 401:

		case 403:

		case 404:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_INPUT,
					Toast.LENGTH_SHORT).show();
			break;

		case 500:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_SERVER,
					Toast.LENGTH_SHORT).show();
			break;

		case 502:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_BADGATEWAY,
					Toast.LENGTH_SHORT).show();
			break;

		case 503:
			Toast.makeText(this,
					ErrorMessage.ERROR_MESSAGE_SERVICE_UNAVAILABLE,
					Toast.LENGTH_SHORT).show();
			break;

		default:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_INPUT,
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			url1 = webView.getUrl();
			String codes;
			String refresh_token;
			Log.e("red_url:", url1);

			if (url1 != null) {

				if (url1.contains("code=")) {
					code = url1.substring(url1.indexOf("code=") + 5,
							url1.length());
					codes = code;

					if (codes != "") {
						getNewToken(codes);
					}
				} else if (url1.contains("refresh_token")) {
					code = url1.substring(url1.indexOf("refresh_token=") + 14,
							url1.length());
					refresh_token = code;

					if (refresh_token != "") {
						getNewToken(refresh_token);
					}
				}
			}
			super.onPageFinished(view, url);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}

	}

	public static TokenInfo parseAccessToken(String msg) throws Exception {
		TokenInfo tokenInfo = new TokenInfo();

		JSONObject token = new JSONObject(msg);

		tokenInfo.setAccessToken(token.getString("access_token"));
		tokenInfo.setExpiresIn(token.getString("expires_in"));
		tokenInfo.setRefresh_token(token.getString("refresh_token"));
		tokenInfo.setUid(token.getString("uid"));

		return tokenInfo;

	}

	// whether has call function
	public boolean isPhoneDevice() {
		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephony.getPhoneType();
		if (type == TelephonyManager.PHONE_TYPE_NONE) {
			Log.i("type", "is Tablet!");
			return false;
		} else {
			Log.i("type", "is Phone!");
			return true;
		}
	}

}
