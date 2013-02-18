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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.dialog.LicenseDialog;

public class TranslationSettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private SharedPreferences settings;

	private String translationEngine = null;

	private ListPreference translateEngineListPreference;

	private Preference addTranslatePreference;

	/** Called when the activity is first created. */
	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.translation_setting);

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager managerPrefs = getPreferenceManager();
		translateEngineListPreference = (ListPreference) managerPrefs
				.findPreference("translationselection");

		translationEngine = "Baidu";

		settings.registerOnSharedPreferenceChangeListener(this);

		// 添加自动翻译功能
		addTranslatePreference = (Preference) findPreference("add_translation_data");
		addTranslatePreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(
								TranslationSettingActivity.this,
								AddTranslationDataActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("TRANSLATE_ENGINE", translationEngine);
						intent.putExtras(bundle);
						startActivity(intent);
						return false;
					}
				});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equals("translationselection")) {
			translationEngine = translateEngineListPreference.getValue();
		}
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
		super.onStop();

	}

}