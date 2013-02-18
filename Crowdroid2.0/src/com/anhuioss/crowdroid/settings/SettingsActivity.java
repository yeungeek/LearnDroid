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

public class SettingsActivity extends PreferenceActivity {

	private SharedPreferences settings;

	private CrowdroidApplication crowdroidApplication;

	private SettingData settingData;

	private static int id = 0;

	private String translationEngine = null;
	private ListPreference translateEngineListPreference;

	public static final String select[] = { "DISPLAY_ALL_IMAGE",
			"DISABLE_IMAGES_PREVIEW", "DISABLE_ALL_IMAGES" };

	/** Called when the activity is first created. */
	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		settingData = crowdroidApplication.getSettingData();

		addPreferencesFromResource(R.xml.setting);

		String service = crowdroidApplication.getStatusData()
				.getCurrentService();

		PreferenceScreen screen = (PreferenceScreen) findPreference("root");
		screen.removePreference(findPreference("account"));

		if (!(service.equals(IGeneral.SERVICE_NAME_TWITTER) || service
				.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY))) {
			screen.removePreference(findPreference("list"));
		}

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
			screen.removePreference(findPreference("casualwatch"));
		}

		// if (IGeneral.APPLICATION_MODE == 1) {
		// PreferenceCategory category = (PreferenceCategory)
		// findPreference("account_group");
		// category.removePreference(findPreference("twitter"));
		// category.removePreference(findPreference("twitter_proxy"));
		// }
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager managerPrefs = getPreferenceManager();
		translateEngineListPreference = (ListPreference) managerPrefs
				.findPreference("translationselection");
		settings.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				// TODO Auto-generated method stub
				if (key.equals("translationselection")) {
					translationEngine = translateEngineListPreference
							.getValue();
				}
			}

		});
	}

	// -----------------------------------------------------------------------------
	/**
	 * Called When Activity Is Started.
	 */
	// -----------------------------------------------------------------------------
	@Override
	public void onStart() {
		super.onStart();

		initPreferenceData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initPreferenceData();
	}

	@Override
	protected void onStop() {

		// CrowdroidApplication crowdroidApplication = (CrowdroidApplication)
		// getApplicationContext();
		// crowdroidApplication.refreshImageRes();
		// crowdroidApplication.startAutoCheckTimer();
		super.onStop();

	}

	// ----------------------------------------------
	/**
	 * Called when Preference has clicked.
	 **/
	// ----------------------------------------------
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			final Preference preference) {

		// -----------------
		// Wall Paper
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("wallpaper")) {

			final CharSequence[] background = getResources().getStringArray(
					R.array.select_background);
			AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
					.setItems(background, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) { // Picture
								Intent picture_select = new Intent();
								picture_select.setType("image/*");
								picture_select
										.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(picture_select, 1);

							} else if (which == 1) { // color
								// Open Dialog
								SelectColorDialog colorDialog = new SelectColorDialog(
										preference.getContext(), preference
												.getKey());
								colorDialog
										.setTitle(getString(R.string.setting_wallpaper));
								colorDialog.show();
							} else if (which == 2) { // theme
								Intent intent = new Intent();
								intent.setClass(SettingsActivity.this,
										SelectThemeActivity.class);
								startActivity(intent);

							}

						}
					}).create();
			dialog.show();

		}

		// -----------------
		// Keyword Fileter
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("fontcolorsettings")) {
			// Open Dialog
			SelectColorDialog colorDialog = new SelectColorDialog(
					preference.getContext(), preference.getKey());
			colorDialog.setTitle(getString(R.string.setting_font_color));
			colorDialog.show();
		}

		// -----------------
		// Keyword Fileter
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("keyword_filter")) {
			Intent intent = new Intent();
			intent.setClass(this, AddKeywordFilterActivity.class);
			startActivity(intent);
		}

		// -----------------
		// User Fileter
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("user_filter")) {
			Intent intent = new Intent();
			intent.setClass(this, AddUserFilterActivity.class);
			startActivity(intent);
		}

		// -----------------
		// Add Translation Data
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("translationselection")) {

			translationEngine = translateEngineListPreference.getValue();
			// return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
		if (preference.getKey() != null
				&& preference.getKey().equals("add_translation_data")) {
			Intent intent = new Intent();
			intent.setClass(this, AddTranslationDataActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("TRANSLATE_ENGINE", translationEngine);
			intent.putExtras(bundle);
			startActivity(intent);
		}

		// -----------------
		// CFB
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("crowdroid_for_business")) {
			Intent intent = new Intent();
			intent.setClass(this, AddCrowdroidForBusinessAccountActivity.class);
			startActivity(intent);
		}

		// -----------------
		// Twitter
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("twitter")) {
			Intent intent = new Intent();
			intent.setClass(this, AddTwitterAccountActivity.class);
			startActivity(intent);
		}

		// -----------------
		// Twitter Proxy
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("twitter_proxy")) {
			Intent intent = new Intent();
			intent.setClass(this, AddTwitterProxyAccountActivity.class);
			startActivity(intent);
		}

		// -----------------
		// Sina
		// -----------------
		if (preference.getKey() != null && preference.getKey().equals("sina")) {
			Intent intent = new Intent();
			intent.setClass(this, AddSinaAccountActivity.class);
			startActivity(intent);
		}

		// -----------------
		// List
		// -----------------
		if (preference.getKey() != null && preference.getKey().equals("list")) {
			Intent intent = new Intent();
			intent.setClass(this, AddListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		}

		// -----------------
		// Gallery
		// -----------------
		// if (preference.getKey() != null
		// && preference.getKey().equals("gallery_custom")) {
		// Intent intent = new Intent();
		// intent.setClass(this, GalleryCustomActivity.class);
		// startActivity(intent);
		// }

		// -----------------
		// Multi-Tweet
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("multi_tweet")) {
			Intent intent = new Intent();
			intent.setClass(this, MultiTweetActivity.class);
			startActivity(intent);
		}

		// -----------------
		// License
		// -----------------
		if (preference.getKey() != null
				&& preference.getKey().equals("license")) {
			new LicenseDialog(this).show();
		}

		// -----------------
		// Help
		// -----------------
		if (preference.getKey() != null && preference.getKey().equals("help")) {
			// Open Web Site Help
			Uri uri = null;
			String language = getApplicationContext().getResources()
					.getConfiguration().locale.getLanguage();
			if (language != null && language.equals("zh")) {
				uri = Uri.parse(IGeneral.HELP_URL_CN);
			} else if (language != null && language.equals("ja")) {
				uri = Uri.parse(IGeneral.HELP_URL_JA);
			} else {
				uri = Uri.parse(IGeneral.HELP_URL_EN);
			}
			Intent i = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(i);
		}

		if (preference.getKey() != null
				&& preference.getKey().equals("imagepreview")) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(SettingsActivity.this);

			int choice = prefs.getInt("dialog_choince_id", 0);
			final CharSequence[] items = getResources().getStringArray(
					R.array.image_show_select);
			AlertDialog.Builder dlg = new AlertDialog.Builder(
					SettingsActivity.this);
			dlg.setTitle(R.string.setting_display_image)
					.setSingleChoiceItems(items, choice, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							id = which;
						}
					})
					.setPositiveButton(R.string.ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(SettingsActivity.this);
							prefs.edit().putString("imagepreview", select[id])
									.commit();
							prefs.edit().putInt("dialog_choince_id", id)
									.commit();
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).create().show();
		}

		// ---------------
		// Casual Watch
		// ---------------
		if (preference.getKey() != null
				&& preference.getKey().equals("casualwatch")) {

			Intent intent = new Intent(SettingsActivity.this,
					CasualWatchSettingActivity.class);
			startActivity(intent);

		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			Uri uri = data.getData();
			String path = getRealPathFromURI(uri);
			settingData.setWallpaperPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ---------------------------------------------------------
	/**
	 * Get Real Path of Image file from URI data
	 */
	// ---------------------------------------------------------
	public String getRealPathFromURI(Uri contentUri) {

		String path;

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		path = cursor.getString(column_index);
		cursor.close();

		return path;
	}

	/**
	 * ----------------------------- init casual watch setting
	 */
	private void initPreferenceData() {

		SharedPreferences restore = PreferenceManager
				.getDefaultSharedPreferences(this);
		String radioPreStr = restore.getString("Casual_Watch_Type", "");
		if (!radioPreStr.equals("")) {
			String key = radioPreStr.substring(radioPreStr.indexOf("[") + 1,
					radioPreStr.indexOf("]"));
			String summary = radioPreStr.substring(
					radioPreStr.indexOf("]") + 1, radioPreStr.length());
			Preference preference = (Preference) findPreference("casualwatch");
			if (preference == null) {
				return;
			}
			if (key.equals("0")) {
				preference.setSummary(summary);
			} else if (key.equals("1")) {
				preference
						.setSummary(getString(R.string.setting_casual_watch_region)
								+ ":" + summary);
			} else if (key.equals("2")) {
				preference
						.setSummary(getString(R.string.setting_casual_watch_search)
								+ ":" + summary);
			}
		}
	}

	// ============================================================

}
