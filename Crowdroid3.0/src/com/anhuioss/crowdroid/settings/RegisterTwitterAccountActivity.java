package com.anhuioss.crowdroid.settings;

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class RegisterTwitterAccountActivity extends Activity implements
		OnClickListener, ServiceConnection {

	// Define Views
	WebView webView;
	TextView pinCode;
	LinearLayout tableImage;
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

	// Flag For pinCode
	boolean flag = false;

	/** Consumer (OAuth) */
	private DefaultOAuthConsumer consumer;

	/** Provider (OAuth) */
	private OAuthProvider provider;

	/** Consumer_Kye for Twitter */
	public static String CONSUMER_KEY = "rbkewUHsJc0pdKMmt3PHQ";

	/** Consumer_Secret for Twitter */
	public static String CONSUMER_SECRET = "qnfmOYFFiqzen67Ww6TxqlaeY45Ct7Cm1hOriyU";

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;
	
	private String accessToken;
	
	private String tokenSecret;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if(statusCode != null && statusCode.equals("200")){
				
				switch (type) {
				case CommHandler.TYPE_GET_NEW_TOKEN: {

					// Prepare Parameter For Register Token
					String[] messages = message.split(";");
					accessToken = messages[0];
					tokenSecret = messages[1];
					registerToken(accessToken, tokenSecret);

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
						bundle.putString("accessToken", accessToken);
						bundle.putString("tokenSecret", tokenSecret);
						bundle.putString("followers_count", userInfo.getFollowerCount());
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

		// Find And Init Views
		webView = (WebView) findViewById(R.id.web_view);

		CookieSyncManager.createInstance(this);
		CookieManager cm = CookieManager.getInstance();
		cm.removeSessionCookie(); 
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

	// -------------------------------------------------------------------------------------------
	/**
	 * Show Error Dialog
	 */
	// -------------------------------------------------------------------------------------------
	private void showErrorDialot() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.register_error);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).create();
		builder.show();

	}

	// --------------------------------------------------------------
	/**
	 *Get the Request URL
	 */
	// --------------------------------------------------------------
	private void loadRequestURL() {

		// Prepare Data
		DefaultOAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
				CONSUMER_SECRET);
		OAuthProvider provider = new DefaultOAuthProvider(
				"http://api.twitter.com/oauth/request_token",
				"http://api.twitter.com/oauth/access_token",
				"http://api.twitter.com/oauth/authorize");
		String requestUrl = null;

		// Get Request Url
		try {
			requestUrl = provider.retrieveRequestToken(consumer,
					OAuth.OUT_OF_BAND);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// Set Consumer And Provider
			this.consumer = consumer;
			this.provider = provider;

			// Load URL
			if (requestUrl != null) {
				webView.loadUrl(requestUrl);
			}

		}

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
		parameters.put("consumer", consumer);
		parameters.put("provider", provider);
		parameters.put("pinCode", pinCode.getText().toString());
		try {
			// HTTP Communication
			apiServiceInterface.request(IGeneral.SERVICE_NAME_TWITTER,
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
	private void registerToken(String accessToken, String tokenSecret) {

		// Prepare Parameters For Request
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("accessToken", accessToken);
		parameters.put("tokenSecret", tokenSecret);
		try {
			// HTTP Communication
			apiServiceInterface.request(IGeneral.SERVICE_NAME_TWITTER,
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
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_INPUT, Toast.LENGTH_SHORT).show();
			break;
		
		case 500:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_SERVER, Toast.LENGTH_SHORT).show();
			break;
			
		case 502:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_BADGATEWAY, Toast.LENGTH_SHORT).show();
			break;
			
		case 503:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_SERVICE_UNAVAILABLE, Toast.LENGTH_SHORT).show();
			break;
			
		default:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_INPUT, Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		// Get Service's Handler
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

		apiServiceInterface = null;

	}

}
