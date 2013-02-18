package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.util.ErrorMessage;

public abstract class BasicAddPreferenceActivity extends PreferenceActivity {

	/** Preference Screen */
	private PreferenceScreen screen;

	/** Flage For What Is Crowdroid For Business */
	private boolean addWhatIsCrowdroidForBusiness = false;

	/** Onclick Listener (Add) */
	protected Preference.OnPreferenceClickListener addClickedListener;

	/** Onclick Listener (Item) */
	protected Preference.OnPreferenceClickListener itemClickedListener;

	Bundle bundle = new Bundle();

	String nowusername = "";

	// ------------------------------------------------------------------------
	/**
	 * Init Preference
	 */
	// ------------------------------------------------------------------------
	protected void initView(String title, String summary, String categoryTitle,
			ArrayList<Preference> preferences,
			Preference.OnPreferenceClickListener addClickedListener,
			Preference.OnPreferenceClickListener itemClickedListener) {

		this.addClickedListener = addClickedListener;
		this.itemClickedListener = itemClickedListener;

		this.screen = getPreferenceManager().createPreferenceScreen(this);
		setPreferenceScreen(screen);

		// Other Preference
		if (addWhatIsCrowdroidForBusiness) {

			Preference whatIsCrowdroidForBusiness = new Preference(this);
			whatIsCrowdroidForBusiness
					.setTitle(R.string.what_is_crowdroid_for_business);
			screen.addPreference(whatIsCrowdroidForBusiness);
			whatIsCrowdroidForBusiness
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							// Open Web Site What Is Crowdroid for Business
							Uri uri = null;
							String language = getApplicationContext()
									.getResources().getConfiguration().locale
									.getLanguage();
							if (language != null && language.equals("zh")) {
								uri = Uri
										.parse(IGeneral.WHAT_IS_CROWDROID_FOR_BUSINESS_CN);
							} else if (language != null
									&& language.equals("ja")) {
								uri = Uri
										.parse(IGeneral.WHAT_IS_CROWDROID_FOR_BUSINESS_JA);
							} else {
								uri = Uri
										.parse(IGeneral.WHAT_IS_CROWDROID_FOR_BUSINESS_EN);
							}
							Intent i = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(i);
							return true;
						}
					});

		}

		// Add Preference
		Preference addPreference = new Preference(this);
		addPreference.setTitle(title);
		addPreference.setSummary(summary);
		addPreference.setKey("add_basic_preference");
		addPreference.setOnPreferenceClickListener(addClickedListener);
		screen.addPreference(addPreference);

		PreferenceCategory category = new PreferenceCategory(this);
		category.setTitle(categoryTitle);
		category.setKey("category_basic_preference");
		screen.addPreference(category);

	}

	// ------------------------------------------------------------------------
	/**
	 * Create Item Data List
	 */
	// ------------------------------------------------------------------------
	protected abstract void createItemList();

	// -----------------------------------------------------------------
	/**
	 * Refresh View
	 */
	// -----------------------------------------------------------------
	protected void refresh(ArrayList<HashMap<String, String>> list) {

		// Delete Preferences from screen (except add and category)
		int listCount = screen.getPreferenceCount();
		for (int i = listCount - 1; i >= 2; i--) {
			Preference remPref = screen.getPreference(i);
			screen.removePreference(remPref);
		}

		// Create New Items
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, String> map = list.get(i);
			Preference preference = new Preference(this);
			String key = map.get("key");
			if (key == null) {
				key = String.valueOf(i);
			}
			preference.setKey(key);
			preference.setTitle(map.get("title"));
			preference.setSummary(map.get("summary"));
			preference.setOnPreferenceClickListener(itemClickedListener);

			screen.addPreference(preference);

		}

	}

	// ------------------------------------------------------------------------
	/**
	 * Show Error Message
	 * 
	 * @param String
	 *            statusCode
	 */
	// ------------------------------------------------------------------------
	protected void showErrorMessage(String statusCode) {

		int code = 0;
		if (statusCode != null) {
			code = Integer.parseInt(statusCode);
		}

		selectErrorMessageToShow(code);

	}

	// ------------------------------------------------------------------------
	/**
	 * Select Error Message to Show
	 * 
	 * @param int statusCode
	 */
	// ------------------------------------------------------------------------
	protected void selectErrorMessageToShow(int statusCode) {

		switch (statusCode) {

		case 400:

		case 401:

		case 403:

		case 404:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE, Toast.LENGTH_SHORT)
					.show();
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
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	// ------------------------------------------------------------------------
	/**
	 * Enable What Is For Business
	 */
	// ------------------------------------------------------------------------
	protected void showWhatIsCrowdroidForBusiness() {
		addWhatIsCrowdroidForBusiness = true;
	}

	protected void onStart() {
		super.onStart();
		if (this.getIntent() != null) {
			bundle = this.getIntent().getExtras();
			if (bundle != null)
				nowusername = bundle.getString("name");
		}
	}

}
