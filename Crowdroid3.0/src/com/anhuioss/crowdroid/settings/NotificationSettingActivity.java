package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.dialog.LicenseDialog;

public class NotificationSettingActivity extends PreferenceActivity {

	private SharedPreferences settings;

	private CrowdroidApplication crowdroidApplication;

	private static int id = 0;

	/** Called when the activity is first created. */
	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.notificatio_setting);
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		String service = crowdroidApplication.getStatusData()
				.getCurrentService();

		if (!(service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_TENCENT) || service
					.equals(IGeneral.SERVICE_NAME_SOHU))) {
			PreferenceScreen category = (PreferenceScreen) findPreference("notification");
			category.removePreference(findPreference("followmessage"));
		}
		if (!(service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| service.equals(IGeneral.SERVICE_NAME_TENCENT) || service
					.equals(IGeneral.SERVICE_NAME_SOHU))) {
			PreferenceScreen category = (PreferenceScreen) findPreference("notification");
			category.removePreference(findPreference("commentmessage"));
		}

		if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			PreferenceScreen category = (PreferenceScreen) findPreference("notification");
			if (category != null) {
				category.removePreference(findPreference("commentmessage"));
			}
		}

		if (!(service.equals(IGeneral.SERVICE_NAME_TWITTER))) {
			PreferenceScreen category = (PreferenceScreen) findPreference("notification");
			category.removePreference(findPreference("unfollowmessage"));
			category.removePreference(findPreference("twitterfollowmessage"));
			category.removePreference(findPreference("retweetofme"));
		}

		// SNS
		if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
			PreferenceScreen category = (PreferenceScreen) findPreference("notification");
			if (category != null) {
				category.removePreference(findPreference("generalmessage"));
				category.removePreference(findPreference("directmessage"));
				category.removePreference(findPreference("atmessage"));
			}
		} else {
			PreferenceScreen category = (PreferenceScreen) findPreference("notification");
			if (category != null) {
				category.removePreference(findPreference("feedshare"));
				category.removePreference(findPreference("feedstates"));
				category.removePreference(findPreference("feedalbums"));
				category.removePreference(findPreference("feedblogs"));
			}
		}

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager managerPrefs = getPreferenceManager();
	}

	// -----------------------------------------------------------------------------
	/**
	 * Called When Activity Is Started.
	 */
	// -----------------------------------------------------------------------------
	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		crowdroidApplication.startAutoCheckTimer();
		super.onStop();

	}

}