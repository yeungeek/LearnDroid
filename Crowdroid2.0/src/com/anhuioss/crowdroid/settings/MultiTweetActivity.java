package com.anhuioss.crowdroid.settings;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;

public class MultiTweetActivity extends PreferenceActivity {

	/** Preference Screen */
	private PreferenceScreen screen;

	/** Accounts */
	private AccountList accountList;

	@Override
	protected void onStart() {
		super.onStart();

		// Get Application
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountList = crowdroidApplication.getAccountList();

		// Create Views
		CreateViews();

	}

	// --------------------------------------------------------------
	/**
	 * Create Views For This Activity
	 */
	// --------------------------------------------------------------
	private void CreateViews() {

		// Preference Screen
		screen = getPreferenceManager().createPreferenceScreen(this);
		setPreferenceScreen(screen);

		if (accountList.getAccountByService(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS).size() > 0) {

			// CFB
			PreferenceCategory cfbPreferenceCategory = new PreferenceCategory(
					this);
			cfbPreferenceCategory
					.setTitle(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);
			screen.addPreference(cfbPreferenceCategory);

			addAccountByService(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);

		}

		if (accountList.getAccountByService(IGeneral.SERVICE_NAME_TWITTER)
				.size() > 0 && IGeneral.APPLICATION_MODE == 0) {

			// Twitter
			PreferenceCategory twitterPreferenceCategory = new PreferenceCategory(
					this);
			twitterPreferenceCategory.setTitle(IGeneral.SERVICE_NAME_TWITTER);
			screen.addPreference(twitterPreferenceCategory);

			addAccountByService(IGeneral.SERVICE_NAME_TWITTER);

		}

		if (accountList
				.getAccountByService(IGeneral.SERVICE_NAME_TWITTER_PROXY)
				.size() > 0
				&& IGeneral.APPLICATION_MODE == 0) {

			// T-P
			PreferenceCategory twitterProxyPreferenceCategory = new PreferenceCategory(
					this);
			twitterProxyPreferenceCategory
					.setTitle(IGeneral.SERVICE_NAME_TWITTER_PROXY);
			screen.addPreference(twitterProxyPreferenceCategory);

			addAccountByService(IGeneral.SERVICE_NAME_TWITTER_PROXY);

		}

		if (accountList.getAccountByService(IGeneral.SERVICE_NAME_SINA).size() > 0) {

			// Sina
			PreferenceCategory sinaPreferenceCategory = new PreferenceCategory(
					this);
			sinaPreferenceCategory.setTitle(IGeneral.SERVICE_NAME_SINA);
			screen.addPreference(sinaPreferenceCategory);

			addAccountByService(IGeneral.SERVICE_NAME_SINA);

		}

		if (accountList.getAccountByService(IGeneral.SERVICE_NAME_TENCENT)
				.size() > 0) {

			// Tencent
			PreferenceCategory sinaPreferenceCategory = new PreferenceCategory(
					this);
			sinaPreferenceCategory.setTitle(IGeneral.SERVICE_NAME_TENCENT);
			screen.addPreference(sinaPreferenceCategory);

			addAccountByService(IGeneral.SERVICE_NAME_TENCENT);

		}

		if (accountList.getAccountByService(IGeneral.SERVICE_NAME_SOHU).size() > 0) {

			// Sohu
			PreferenceCategory sohuPreferenceCategory = new PreferenceCategory(
					this);
			sohuPreferenceCategory.setTitle(IGeneral.SERVICE_NAME_SOHU);
			screen.addPreference(sohuPreferenceCategory);

			addAccountByService(IGeneral.SERVICE_NAME_SOHU);

		}

	}

	// --------------------------------------------------------------
	/**
	 * Add Account To Service
	 */
	// --------------------------------------------------------------
	private void addAccountByService(String service) {

		for (final AccountData account : accountList
				.getAccountByService(service)) {
			Preference accountPreference = new Preference(this);
			accountPreference.setTitle(account.getUserName());
			accountPreference.setSummary(account.getMultiTweet() ? R.string.on
					: R.string.off);
			accountPreference
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							Builder dlg = new AlertDialog.Builder(
									MultiTweetActivity.this);
							if (account.getMultiTweet()) {
								dlg.setMessage(R.string.close_multi_tweet);
							} else {
								dlg.setMessage(R.string.open_multi_tweet);
							}
							dlg.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {

											// Set Flag For Multi-Tweet
											if (account.getMultiTweet()) {
												account.setMultiTweet(false);
											} else {
												account.setMultiTweet(true);
											}

											// Refresh Account
											accountList.refreshAccount(account);

											// Remove Views
											screen.removeAll();

											// Create Views For This Activity
											// With New Data
											CreateViews();

										}
									})
									.setNegativeButton(
											R.string.cancel,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {

												}
											}).create();
							dlg.show();
							return true;
						}
					});
			screen.addPreference(accountPreference);
		}

	}

}
