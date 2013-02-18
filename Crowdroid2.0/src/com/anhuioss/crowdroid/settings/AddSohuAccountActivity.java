package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;

public class AddSohuAccountActivity extends BasicAddPreferenceActivity
		implements ServiceConnection {

	private AccountList accountList;

	private String accessToken;

	private String tokenSecret;

	private String pinCode;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			if (statusCode != null && statusCode.equals("200")) {

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
					if (userInfo.getUid() != null
							|| !userInfo.getUid().equals("")) {

						String userName = userInfo.getScreenName();
						String uid = userInfo.getUid();
						String userFollowerCount = userInfo.getFollowerCount();
						// Add Account
						AccountData account = new AccountData();

						account.setUserName(userName);
						account.setUserScreenName(userName);
						account.setUid(uid);
						account.setService(IGeneral.SERVICE_NAME_SOHU);
						account.setAccessToken(accessToken);
						account.setTokenSecret(tokenSecret);
						account.setLastUserFollowerCount(userFollowerCount);
						accountList.addAccount(account);
						// Refresh
						createItemList();
						SohuOAuthConstant.clear();
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

	private Preference.OnPreferenceClickListener addSohuClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {

			Intent intent = new Intent(AddSohuAccountActivity.this,
					RegisterSohuAccountActivity.class);
			startActivityForResult(intent, 0);
			return true;
		}
	};

	private Preference.OnPreferenceClickListener itemSohuClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {

			final int id = Integer.valueOf(preference.getKey());

			AlertDialog dialog = new AlertDialog.Builder(
					AddSohuAccountActivity.this)
					.setTitle(R.string.confirm)
					.setMessage(R.string.whether_delete)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									final AccountData account = accountList
											.getAccountByService(
													IGeneral.SERVICE_NAME_SOHU)
											.get(id);
									if (account.getUserName().equals(
											nowusername)
											|| account.getUserScreenName()
													.equals(nowusername)) {
										AlertDialog dialognext = new AlertDialog.Builder(
												AddSohuAccountActivity.this)
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
																		AddSohuAccountActivity.this,
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

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).create();
			dialog.show();

			return true;
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0 && data != null) {
			// Bundle bundle = data.getExtras();
			// if(bundle != null){
			// //Get Data
			// String userName = bundle.getString("userName");
			// String uid = bundle.getString("uid");
			// String accessToken = bundle.getString("accessToken");
			// String tokenSecret = bundle.getString("tokenSecret");
			// String userFollowerCount = bundle.getString("followers_count");
			// //Add Account
			// AccountData account = new AccountData();
			//
			// account.setUserName(userName);
			// account.setUserScreenName(userName);
			// account.setUid(uid);
			// account.setService(IGeneral.SERVICE_NAME_SOHU);
			// account.setAccessToken(accessToken);
			// account.setTokenSecret(tokenSecret);
			// account.setLastUserFollowerCount(userFollowerCount);
			// accountList.addAccount(account);
			// //Refresh
			// createItemList();
			//
			// finish();
			//
			// }
			switch (resultCode) {
			case 0: {
				SohuOAuthConstant.getInstance();
				pinCode = data.getExtras().getString("pinCode");
				getNewToken(pinCode);
				break;
			}
			default:
				break;
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		// if(this.getIntent()!=null){
		// bundle=this.getIntent().getExtras();
		// if(bundle!=null)
		// nowusername=bundle.getString("name");
		// }
		// nowusername=AccountManageActivity.nowusername;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountList = crowdroidApplication.getAccountList(); //

		initView(getString(R.string.add_sohu_account), "",
				getString(R.string.sohu_account_list), null,
				addSohuClickedListener, itemSohuClickedListener);
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
		createItemList();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		unbindService(this);
	}

	@Override
	protected void createItemList() {
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		for (AccountData account : accountList
				.getAccountByService(IGeneral.SERVICE_NAME_SOHU)) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", account.getUserScreenName());
			item.put("summary", account.getService());
			itemList.add(item);
		}

		//
		refresh(itemList);
	}

	private void getNewToken(String pin) {

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("pinCode", pin);

		try {
			// HTTP Communication
			apiServiceInterface.request(IGeneral.SERVICE_NAME_SOHU,
					CommHandler.TYPE_GET_NEW_TOKEN, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void registerToken(String accessToken, String tokenSecret) {

		// Prepare Parameters For Request
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("accessToken", accessToken);
		parameters.put("tokenSecret", tokenSecret);
		try {
			// HTTP Communication
			apiServiceInterface.request(IGeneral.SERVICE_NAME_SOHU,
					CommHandler.TYPE_VERIFY_USER, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
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
