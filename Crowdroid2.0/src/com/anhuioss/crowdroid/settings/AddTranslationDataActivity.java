package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.TranslationData;
import com.anhuioss.crowdroid.data.TranslationList;

public class AddTranslationDataActivity extends BasicAddPreferenceActivity {

	TranslationList translationList;

	private static String TRANSLATION_ENGINE_FLAG = null;

	/** Language List */
	private String[] languageValues = { "ar",
			// these are not same
			"zh-CHS", "zh-CHT", "nl", "en", "fr", "de", "el", "it", "ja", "ko",
			"pt", "ru", "es" };
	private String[] languageBaiduValues = { "zh", "en", "jp" };
	String ARABIC;
	String CHINISE_SIMPLE;
	String CHINESE_TRADITIONAL;
	String DUTC;
	String ENGLISH;
	String FRENCH;
	String GERMAN;
	String GREEK;
	String ITALIAN;
	String JAPANISE;
	String KOREA;
	String PORTUGESE;
	String RUSSIAN;
	String SPANISH;

	// Current Status
	StatusData statusData;

	/**
	 * Add Clicked Listener
	 */
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {

			LayoutInflater factory = LayoutInflater
					.from(AddTranslationDataActivity.this);

			final View textEntryView = factory.inflate(
					R.layout.dialog_add_translation_data, null);

			// Language From
			final Spinner spinnerFrom = (Spinner) textEntryView
					.findViewById(R.id.dialog_add_translation_from);

			// Language To
			final Spinner spinnerTo = (Spinner) textEntryView
					.findViewById(R.id.dialog_add_translation_to);

			// Get Value
			ARABIC = getString(R.string.arabic);
			CHINISE_SIMPLE = getString(R.string.chinese_simple);
			CHINESE_TRADITIONAL = getString(R.string.chiness_traditional);
			DUTC = getString(R.string.dutch);
			ENGLISH = getString(R.string.english);
			FRENCH = getString(R.string.french);
			GERMAN = getString(R.string.german);
			GREEK = getString(R.string.greek);
			ITALIAN = getString(R.string.italian);
			JAPANISE = getString(R.string.japanese);
			KOREA = getString(R.string.korea);
			PORTUGESE = getString(R.string.portugese);
			RUSSIAN = getString(R.string.russian);
			SPANISH = getString(R.string.spanish);

			/** Language List (Only for display to Spinner) */
			String[] languageNames = { ARABIC, CHINISE_SIMPLE,
					CHINESE_TRADITIONAL, DUTC, ENGLISH, FRENCH, GERMAN, GREEK,
					ITALIAN, JAPANISE, KOREA, PORTUGESE, RUSSIAN, SPANISH };

			String[] baiduLanguageNames = { CHINISE_SIMPLE, ENGLISH, JAPANISE };

			// Create Array List of Language
			ArrayList<String> languageNameList = new ArrayList<String>();

			if ("Baidu".equals(TRANSLATION_ENGINE_FLAG)) {
				for (int i = 0; i < baiduLanguageNames.length; i++) {
					languageNameList.add(languageNames[i]);
				}

				ArrayAdapter<String> baiduLanguageSelectAdapter = new ArrayAdapter<String>(
						AddTranslationDataActivity.this,
						android.R.layout.simple_spinner_item,
						baiduLanguageNames);
				baiduLanguageSelectAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerFrom.setAdapter(baiduLanguageSelectAdapter);

				spinnerFrom
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int position, long id) {
								// TODO select language from ,then adapter
								// language to
								ArrayList<String> languageBaiduNameList = new ArrayList<String>();
								if (position == 0) {
									String[] languageBaiduNames = { ENGLISH,
											JAPANISE };

									for (int i = 0; i < languageBaiduNames.length; i++) {
										languageBaiduNameList
												.add(languageBaiduNames[i]);
									}

								} else if (position == 1) {
									String[] languageBaiduNames = { CHINISE_SIMPLE };

									for (int i = 0; i < languageBaiduNames.length; i++) {
										languageBaiduNameList
												.add(languageBaiduNames[i]);
									}
								} else if (position == 2) {
									String[] languageBaiduNames = { CHINISE_SIMPLE };

									for (int i = 0; i < languageBaiduNames.length; i++) {
										languageBaiduNameList
												.add(languageBaiduNames[i]);
									}
								}

								ArrayAdapter<String> languageBaiduSelectAdapter = new ArrayAdapter<String>(
										AddTranslationDataActivity.this,
										android.R.layout.simple_spinner_item,
										languageBaiduNameList);

								languageBaiduSelectAdapter
										.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

								spinnerTo
										.setAdapter(languageBaiduSelectAdapter);

							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});

			} else if ("Bing".equals(TRANSLATION_ENGINE_FLAG)) {

				for (int i = 0; i < languageNames.length; i++) {
					languageNameList.add(languageNames[i]);
				}
				ArrayAdapter<String> languageSelectAdapter = new ArrayAdapter<String>(
						AddTranslationDataActivity.this,
						android.R.layout.simple_spinner_item, languageNameList);

				// Set Drop Down Views
				languageSelectAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// Set Adapter
				spinnerFrom.setAdapter(languageSelectAdapter);
				spinnerTo.setAdapter(languageSelectAdapter);

			}
			// Set Adapter to Spinner

			// Alert Dialog
			AlertDialog dlg = new AlertDialog.Builder(
					AddTranslationDataActivity.this)
					.setTitle(R.string.add)
					.setView(textEntryView)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									// Find View
									EditText userNameForTranslation = (EditText) textEntryView
											.findViewById(R.id.dialog_add_translation_name);

									// New Instance
									TranslationData translationData = new TranslationData();

									// Set Data
									translationData.setService(statusData
											.getCurrentService());
									translationData
											.setUserName(userNameForTranslation
													.getText().toString());
									translationData
											.setEngine(TRANSLATION_ENGINE_FLAG);

									if ("Baidu".equals(TRANSLATION_ENGINE_FLAG)) {
										translationData
												.setFrom(languageBaiduValues[spinnerFrom
														.getSelectedItemPosition()]);
										switch (spinnerFrom
												.getSelectedItemPosition()) {
										case 0: {
											if (spinnerTo
													.getSelectedItemPosition() == 0) {
												translationData.setTo("en");
											} else if (spinnerTo
													.getSelectedItemPosition() == 1) {
												translationData.setTo("jp");
											}
											break;
										}
										case 1: {
											translationData.setTo("zh");
											break;
										}
										case 2: {
											translationData.setTo("zh");
											break;
										}
										}
									} else if ("Bing"
											.equals(TRANSLATION_ENGINE_FLAG)) {
										translationData
												.setFrom(languageValues[spinnerFrom
														.getSelectedItemPosition()]);
										translationData.setTo(languageValues[spinnerTo
												.getSelectedItemPosition()]);
									}

									// Add Data To List
									translationList
											.addTranslationData(translationData);

									createItemList();

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

	/**
	 * Item Clicked Listener
	 */
	private Preference.OnPreferenceClickListener itemClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {

			final int id = Integer.valueOf(preference.getKey());

			AlertDialog dlg = new AlertDialog.Builder(
					AddTranslationDataActivity.this)
					.setTitle(R.string.confirm)
					.setMessage(R.string.whether_delete)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									TranslationData translationData = translationList
											.getTranslationData(
													statusData
															.getCurrentService())
											.get(id);

									// Delete
									translationList
											.removeTranslationData(translationData);

									createItemList();

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

	@Override
	public void onStart() {
		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		translationList = crowdroidApplication.getTranslationList();
		statusData = crowdroidApplication.getStatusData();

		// translate engine
		Bundle bundle = this.getIntent().getExtras();
		TRANSLATION_ENGINE_FLAG = bundle.getString("TRANSLATE_ENGINE");

		// Init Preference Activity
		initView(getString(R.string.setting_translation_add_auto_translation),
				"",
				getString(R.string.setting_translation_auto_translation_list),
				null, addClickedListener, itemClickedListener);

		createItemList();
	}

	@Override
	protected void createItemList() {
		// Create Item List
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		// Add items from DataList
		for (TranslationData translationData : translationList
				.getTranslationData(statusData.getCurrentService())) {

			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", translationData.getUserName());
			item.put("summary", "From : " + translationData.getFrom()
					+ "\nTo : " + translationData.getTo() + "\nEngine:"
					+ translationData.getEngine());
			itemList.add(item);

		}

		refresh(itemList);
	}

}
