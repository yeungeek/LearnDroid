package com.anhuioss.crowdroid.settings;

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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

public class RegisterRenRenAccountActivity extends Activity implements
		ServiceConnection {

	WebView webView;

	LinearLayout ly;

	RelativeLayout relative;

	String url1 = "";
	public static String access_token = "";
	public static String refresh_token = "";
	public static String expires_in = "";
	public static String userid = "";
	public static String code = "";

	public Map<String, String> tokenMap;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	public static final String RENREN_API_KEY = "165d021dcd924dab95bd54d272c357fd";
	public static final String RENREN_SECRET = "298e98e4d3854f8ca12d4f098ac75115";

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				switch (type) {
				case CommHandler.TYPE_GET_NEW_TOKEN: {

					// Prepare Parameter For Register Token
					String[] messages = message.split(";");
					access_token = messages[0];
					refresh_token = messages[1];
					expires_in = messages[2];
					registerToken(access_token);

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

		CookieSyncManager.createInstance(this);
		CookieManager cm = CookieManager.getInstance();
		cm.removeSessionCookie(); 
		
		ly = (LinearLayout) findViewById(R.id.LinearLayout01);

		relative = (RelativeLayout) findViewById(R.id.RelativeLayout01);

		ly.setVisibility(View.GONE);

		relative.setVisibility(View.GONE);

		// Web View
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}
		});
		webView.clearCache(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.clearHistory();
		webView.clearFormData();

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

	// --------------------------------------------------------------
	/**
	 * Get the Request URL
	 */
	// --------------------------------------------------------------
	private void loadRequestURL() {

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// wv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		CookieManager cm = CookieManager.getInstance();
		cm.removeSessionCookie();

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				url1 = webView.getUrl();
				String codes;

				if (url1 != null) {

					if (url1.contains("code=")) {
						code = url1.substring(url1.indexOf("code=") + 5,
								url1.length());
						codes = code;

						if (codes != "") {
							getNewToken(codes);
						}
					}
				}
				super.onPageFinished(view, url);
			}

		});

		webView.loadUrl("https://graph.renren.com/oauth/authorize?"
				+ "client_id="
				+ RENREN_API_KEY
				+ "&response_type=code"
				+ "&display=touch&redirect_uri=http://graph.renren.com/oauth/login_success.html"
				+ "&scope=status_update publish_blog read_user_feed read_user_status read_user_share read_user_comment create_album read_user_photo read_user_blog photo_upload publish_share publish_feed publish_comment read_user_album publish_checkin");
	}

	// --------------------------------------------------------------
	/**
	 * Get New Token
	 */
	// --------------------------------------------------------------
	private void getNewToken(String codes) {

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("grant_type", "grant_type=authorization_code");
		parameters.put("code", "&code=" + codes);
		try {
			// HTTP Communication
			apiServiceInterface.request(IGeneral.SERVICE_NAME_RENREN,
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
	private void registerToken(String accessToken) {

		// Prepare Parameters For Request
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("accessToken", accessToken);
		try {
			// HTTP Communication
			apiServiceInterface.request(IGeneral.SERVICE_NAME_RENREN,
					CommHandler.TYPE_VERIFY_USER, apiServiceListener,
					parameters);
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

}