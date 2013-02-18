package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

public class AddRenRenAccountActivity extends BasicAddPreferenceActivity {

	private AccountList accountList;

	// ------------------------------------------------------------------------------------------------
	/**
	 * Add clicked listener
	 */
	// ------------------------------------------------------------------------------------------------
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Intent intent = new Intent(AddRenRenAccountActivity.this,
					RegisterRenRenAccountActivity.class);
			startActivityForResult(intent, 0);
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
					AddRenRenAccountActivity.this)
					.setTitle(R.string.confirm)
					.setMessage(R.string.whether_delete)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									final AccountData account = accountList
											.getAccountByService(
													IGeneral.SERVICE_NAME_RENREN)
											.get(id);
									if (account.getUserName().equals(
											nowusername)
											|| account.getUserScreenName()
													.equals(nowusername)) {
										AlertDialog dialognext = new AlertDialog.Builder(
												AddRenRenAccountActivity.this)
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
																		AddRenRenAccountActivity.this,
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
	 * Set data to account list
	 */
	// ------------------------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == 0 && data != null) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				// Get Data
				String userName = bundle.getString("userName");
				String uid = bundle.getString("uid");
				String accessToken = bundle.getString("accessToken");
				// Add Account
				AccountData account = new AccountData();

				account.setUserName(userName);
				account.setUserScreenName(userName);
				account.setUid(uid);
				account.setService(IGeneral.SERVICE_NAME_RENREN);
				account.setAccessToken(accessToken);
				accountList.addAccount(account);
				// Refresh
				createItemList();

				finish();

			}
		}
	}

	// ------------------------------------------------------------------------------------------------
	/**
	 * onStart()
	 */
	// ------------------------------------------------------------------------------------------------
	@Override
	public void onStart() {
		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountList = crowdroidApplication.getAccountList();

		initView(getString(R.string.add_renren_account), "",
				getString(R.string.renren_account_list), null,
				addClickedListener, itemClickedListener);

		createItemList();

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
				.getAccountByService(IGeneral.SERVICE_NAME_RENREN)) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", account.getUserScreenName());
			item.put("summary", account.getService());
			itemList.add(item);
		}

		refresh(itemList);
	}

}
