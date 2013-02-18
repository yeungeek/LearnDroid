package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentParserHandler;
import com.mime.qweibo.OauthKey;
import com.mime.qweibo.QParameter;
import com.mime.qweibo.QWeiboRequest;

public class AddTencentAccountActivity extends BasicAddPreferenceActivity
		implements ServiceConnection {

	private AccountList accountList;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	private static String accessToken;

	private static String tokenSecret;

	private String pinCode;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				switch (type) {
				case CommHandler.TYPE_GET_NEW_TOKEN: {
					// // Prepare Parameter For Register Token
					String[] messages = message.split(";");
					accessToken = messages[0];
					tokenSecret = messages[1];
					authorize(accessToken);

					break;
				}
				default: {

				}
				}

			}
		}
	};

	// ------------------------------------------------------------------------------------------------
	/**
	 * Set data to account list
	 */
	// ------------------------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0 && data != null) {

			switch (resultCode) {
			case 0: {
				pinCode = data.getExtras().getString("pinCode");
				getAccessToken(TencentCommHandler.APP_KEY,
						TencentCommHandler.APP_SECRET, accessToken,
						tokenSecret, pinCode);
				break;
			}
			default:
				break;
			}

			// String verifyCode = data.getStringExtra("verify-code");
			// getAccessToken(TencentCommHandler.APP_KEY,
			// TencentCommHandler.APP_SECRET, accessToken, tokenSecret,
			// verifyCode);
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Get access token
	 * 
	 * @param customKey
	 * @param customSecret
	 * @param requestToken
	 * @param requestTokenSecrect
	 * @param verify
	 * @return
	 */
	// ------------------------------------------------------------------------------------------------
	private String getAccessToken(String customKey, String customSecret,
			String requestToken, String requestTokenSecrect, String verify) {

		String url = "http://open.t.qq.com/cgi-bin/access_token";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customKey;
		oauthKey.customSecrect = customSecret;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecrect;
		oauthKey.verify = verify;

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (parseToken(res)) {
			verifyUser();
		}
		return res;
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Parse token
	 * 
	 * @param res
	 * @return
	 */
	// ------------------------------------------------------------------------------------------------
	private boolean parseToken(String res) {
		if (res == null || res.equals("")) {
			return false;
		}

		String[] tokenArray = res.split("&");

		if (tokenArray.length < 2) {
			return false;
		}

		String strTokenKey = tokenArray[0];
		String strTokenSecrect = tokenArray[1];

		String[] token1 = strTokenKey.split("=");
		if (token1.length < 2) {
			return false;
		}
		accessToken = token1[1];

		String[] token2 = strTokenSecrect.split("=");
		if (token2.length < 2) {
			return false;
		}
		tokenSecret = token2[1];

		return true;
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Verify User
	 */
	// ------------------------------------------------------------------------------------------------
	private void verifyUser() {

		// Get User Information
		String msg = TencentCommHandler.getUserInfo(accessToken, tokenSecret);
		UserInfo userInfo = TencentParserHandler.parseUserInfo(msg);

		// Failed
		if (userInfo == null || userInfo.getScreenName().length() == 0) {
			return;
		}

		// Succeed
		AccountData account = new AccountData();
		account.setUid(userInfo.getUid());
		account.setUserName(userInfo.getUserName());
		account.setUserScreenName(userInfo.getScreenName());
		account.setAccessToken(accessToken);
		account.setTokenSecret(tokenSecret);
		account.setService(IGeneral.SERVICE_NAME_TENCENT);
		accountList.addAccount(account);

		createItemList();

		TencentCommHandler.setAccount(accessToken, tokenSecret);

		// Back to Register Activity
		// Intent intent = new Intent(AddTencentAccountActivity.this,
		// LoginActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// startActivity(intent);
		finish();

	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Authorize account
	 * 
	 * @param token
	 */
	// ------------------------------------------------------------------------------------------------
	private void authorize(String token) {
		Intent intent = new Intent(AddTencentAccountActivity.this,
				RegisterTencentAccountActivity.class);
		intent.putExtra("url",
				"http://open.t.qq.com/cgi-bin/authorize?oauth_token=" + token);
		startActivityForResult(intent, 0);
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Add clicked listener
	 */
	// ------------------------------------------------------------------------------------------------
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			try {
				// HTTP Communication
				apiServiceInterface.request(IGeneral.SERVICE_NAME_TENCENT,
						CommHandler.TYPE_GET_NEW_TOKEN, apiServiceListener,
						parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			return true;
		}
	};

	// ------------------------------------------------------------------------------------------------
	/**
	 * Item clicked listener
	 */
	// ------------------------------------------------------------------------------------------------
	private Preference.OnPreferenceClickListener itemClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			final int id = Integer.valueOf(preference.getKey());

			AlertDialog dlg = new AlertDialog.Builder(
					AddTencentAccountActivity.this)
					.setTitle(R.string.confirm)
					.setMessage(R.string.whether_delete)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									final AccountData account = accountList
											.getAccountByService(
													IGeneral.SERVICE_NAME_TENCENT)
											.get(id);
									if (account.getUserName().equals(
											nowusername)
											|| account.getUserScreenName()
													.equals(nowusername)) {
										AlertDialog dialognext = new AlertDialog.Builder(
												AddTencentAccountActivity.this)
												.setTitle(R.string.confirm)
												.setMessage(
														R.string.weather_delate_now)
												.setPositiveButton(
														R.string.ok,
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {

																accountList
																		.removeAccount(account);
																Intent i = new Intent(
																		AddTencentAccountActivity.this,
																		LoginActivity.class);
																i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
																startActivity(i);
																// createItemList();
															}
														})
												.setNegativeButton(
														R.string.cancel,
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																// TODO
																// Auto-generated
																// method stub
																dialog.dismiss();

															}
														}).create();
										dialognext.show();
									} else {
										accountList.removeAccount(account);
										createItemList();
									}

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).create();
			dlg.show();

			return true;
		}
	};

	// ------------------------------------------------------------------------------------------------
	/**
	 * onStart()
	 */
	// ------------------------------------------------------------------------------------------------
	@Override
	public void onStart() {
		super.onStart();

		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountList = crowdroidApplication.getAccountList();

		initView(getString(R.string.add_tencent_account), "",
				getString(R.string.tencent_account_list), null,
				addClickedListener, itemClickedListener);

		createItemList();

	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Called the activity is destroyed
	 */
	// ------------------------------------------------------------------------------------------------
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(this);
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * Create item list
	 */
	// ------------------------------------------------------------------------------------------------
	@Override
	protected void createItemList() {
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		for (AccountData account : accountList
				.getAccountByService(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", account.getUserScreenName());
			item.put("summary", account.getService());
			itemList.add(item);
		}

		refresh(itemList);
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
