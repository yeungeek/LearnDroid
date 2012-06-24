package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.DetailTweetActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class AddGroupListActivity extends PreferenceActivity implements
		ServiceConnection, OnPreferenceClickListener {

	private ApiServiceInterface apiServiceInterface;
	private CrowdroidApplication crowdroidApplication;
	private StatusData statusData;
	private SettingData settingData;
	private AccountData currentAccount;

	private ArrayList<ListInfo> listInfoList = null;

	private SharedPreferences sharePreference;
	private PreferenceScreen screen;
	private CheckBoxPreference defaultPre;
	private CheckBoxPreference listPre;

	protected Preference.OnPreferenceClickListener itemClickedListener;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {

				if (type == CommHandler.TYPE_GET_GROUP_LIST_SLUG) {
					ParseHandler parseHandler = new ParseHandler();

					listInfoList = (ArrayList<ListInfo>) parseHandler.parser(
							service, type, statusCode, message);
					creatPreferenceItem();
				}
			} else {
				Toast.makeText(
						AddGroupListActivity.this,
						ErrorMessage.getErrorMessage(AddGroupListActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public void onStart() {
		super.onStart();

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();

		// Bind Service
		Intent intent = new Intent(AddGroupListActivity.this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("screenName", currentAccount.getUserScreenName());
		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_GROUP_LIST_SLUG, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

	protected void creatPreferenceItem() {

		sharePreference = PreferenceManager
				.getDefaultSharedPreferences(AddGroupListActivity.this);

		screen = getPreferenceManager().createPreferenceScreen(this);
		screen.setTitle(getString(R.string.group_list));
		setPreferenceScreen(screen);

		// default
		boolean defCheckFlag = sharePreference.getBoolean(
				"Setting_Group_List_Default", true);
		defaultPre = new CheckBoxPreference(this);
		if (defaultPre != null) {
			defaultPre.setChecked(defCheckFlag);
		}
		defaultPre.setKey("0");
		defaultPre.setTitle(getString(R.string.group_list_default));
		defaultPre.setSummary(getString(R.string.group_list_default_all));
		defaultPre.setEnabled(true);
		defaultPre.setOnPreferenceClickListener(this);
		screen.addPreference(defaultPre);

		// slugs
		for (int index = 0; index < listInfoList.size(); index++) {
			final boolean slugCheckFlag = sharePreference.getBoolean(
					listInfoList.get(index).getSlug(), false);

			listPre = new CheckBoxPreference(this);
			if (listPre != null) {
				listPre.setChecked(slugCheckFlag);
			}
			listPre.setKey(String.valueOf(index + 1));
			listPre.setTitle(listInfoList.get(index).getSlug());
			listPre.setSummary(listInfoList.get(index).getDescription());
			if (defCheckFlag) {
				listPre.setEnabled(false);
				listPre.setChecked(true);
			} else {
				listPre.setEnabled(true);
			}
			listPre.setOnPreferenceClickListener(this);
			screen.addPreference(listPre);
		}

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals("0")) {
			if (defaultPre.isChecked()) {
				defaultPre.setChecked(true);
				sharePreference.edit()
						.putBoolean("Setting_Group_List_Default", true)
						.commit();
				for (int index = 0; index < listInfoList.size(); index++) {
					screen.findPreference(String.valueOf(index + 1))
							.setEnabled(false);
				}
			} else {
				defaultPre.setChecked(false);
				sharePreference.edit()
						.putBoolean("Setting_Group_List_Default", false)
						.commit();
				for (int index = 0; index < listInfoList.size(); index++) {
					screen.findPreference(String.valueOf(index + 1))
							.setEnabled(true);
				}
			}
		} else {
			for (int index = 0; index < listInfoList.size(); index++) {

				CheckBoxPreference itmePreference = (CheckBoxPreference) screen
						.findPreference(String.valueOf(index + 1));
				String preKey = preference.getKey();
				if (preKey.equals(String.valueOf(index + 1))) {
					if (itmePreference.isChecked()) {

						itmePreference.setChecked(true);
						Toast.makeText(AddGroupListActivity.this, "true",
								Toast.LENGTH_SHORT).show();

						sharePreference
								.edit()
								.putBoolean(preference.getTitle().toString(),
										true).commit();

					} else {
						itmePreference.setChecked(false);
						Toast.makeText(AddGroupListActivity.this, "false",
								Toast.LENGTH_SHORT).show();
						sharePreference
								.edit()
								.putBoolean(preference.getTitle().toString(),
										false).commit();
					}
				}

			}
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		return false;
	}

}