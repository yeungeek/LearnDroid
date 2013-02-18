package com.anhuioss.crowdroid.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.anhuioss.crowdroid.R;

public class AutoRefreshActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.autorefresh_setting);
	}

}