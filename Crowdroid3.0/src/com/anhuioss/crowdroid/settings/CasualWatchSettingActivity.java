package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class CasualWatchSettingActivity extends PreferenceActivity implements
		ServiceConnection {

	private ApiServiceInterface apiServiceInterface;

	private StatusData statusData;

	/** Preference Screen */
	private PreferenceScreen preScreen;
	private PreferenceCategory category;
	private RadioPreference radioPreferencePublic;
	private RadioPreference radioPreferenceRegion;
	private RadioPreference radioPreferenceSearch;
	private SharedPreferences sharePref;
	private Editor mEditor;

	/** Data For Spinner */
	private List<String> trendsTypeData = new ArrayList<String>();
	private ArrayList<TrendInfo> trendSpinnertData = new ArrayList<TrendInfo>();
	private ArrayAdapter<String> trendsTypeAdapter;
	private String region = null;

	private EditText mEditKeyword;
	private Spinner trendsTypeSpinner;

	/** Onclick Listener (Item) */
	protected Preference.OnPreferenceClickListener itemClickedListener;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_LOCATIONS_AVAILIABLE_TRENDS) {

					// Parser
					ArrayList<TrendInfo> listInfoList = new ArrayList<TrendInfo>();
					ParseHandler parseHandler = new ParseHandler();
					listInfoList = (ArrayList<TrendInfo>) parseHandler.parser(
							service, type, statusCode, message);

					trendSpinnertData = listInfoList;

					// Create Spinner's Data
					createSpinner(listInfoList);
				}

			} else {
				Toast.makeText(
						CasualWatchSettingActivity.this,
						ErrorMessage.getErrorMessage(
								CasualWatchSettingActivity.this, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_causal_watch_preference);

		preScreen = (PreferenceScreen) findPreference("casual_watch_screen");
		category = (PreferenceCategory) findPreference("casual_watch_category");
		radioPreferencePublic = (RadioPreference) findPreference("casual_watch_public");
		radioPreferenceRegion = (RadioPreference) findPreference("casual_watch_region");
		radioPreferenceSearch = (RadioPreference) findPreference("casual_watch_search");

	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind Service
		Intent intent = new Intent(CasualWatchSettingActivity.this,
				ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		initDatas();

	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		if (preference.getKey() != null
				&& preference.getKey().equals("casual_watch_public")) {
			if (radioPreferencePublic.isChecked()) {
				radioPreferenceRegion.setChecked(false);
				radioPreferenceSearch.setChecked(false);

				radioPreferenceRegion.setSummary("");
				radioPreferenceSearch.setSummary("");

				sharePref = PreferenceManager
						.getDefaultSharedPreferences(CasualWatchSettingActivity.this);
				mEditor = sharePref.edit();
				mEditor.putString("Casual_Watch_Type", "[0]"
						+ getString(R.string.setting_casual_watch_public));
				mEditor.commit();

				preference.setSummary(R.string.setting_casual_watch_public);

			} else {

				preference.setSummary("");
			}

		} else if (preference.getKey() != null
				&& preference.getKey().equals("casual_watch_region")) {
			if (radioPreferenceRegion.isChecked()) {
				radioPreferencePublic.setChecked(false);
				radioPreferenceSearch.setChecked(false);

				radioPreferencePublic.setSummary("");
				radioPreferenceSearch.setSummary("");

				showRegionDialog();

			} else {

				preference.setSummary("");

			}

		} else if (preference.getKey() != null
				&& preference.getKey().equals("casual_watch_search")) {
			if (radioPreferenceSearch.isChecked()) {
				radioPreferencePublic.setChecked(false);
				radioPreferenceRegion.setChecked(false);

				radioPreferencePublic.setSummary("");
				radioPreferenceRegion.setSummary("");

				showKeywordDialog();

			} else {

				preference.setSummary("");

			}

		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	private void showRegionDialog() {

		CrowdroidApplication app = (CrowdroidApplication) radioPreferenceRegion
				.getContext().getApplicationContext();
		statusData = app.getStatusData();

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory
				.inflate(R.layout.dialog_trends, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				CasualWatchSettingActivity.this);
		alert.setTitle(R.string.setting_casual_watch_region);
		alert.setView(textEntryView);

		trendsTypeSpinner = (Spinner) textEntryView
				.findViewById(R.id.dialog_trends_type);
		ListView mListView = (ListView) textEntryView
				.findViewById(R.id.dialog_trends_list);
		mListView.setVisibility(View.GONE);

		// Create Adapter
		trendsTypeAdapter = new ArrayAdapter<String>(
				radioPreferenceRegion.getContext(),
				android.R.layout.simple_spinner_item, trendsTypeData);
		trendsTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Set Adapter
		trendsTypeSpinner.setAdapter(trendsTypeAdapter);

		trendsTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int position, long id) {
						region = trendSpinnertData.get(position).getName().toString();
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		alert.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						sharePref = PreferenceManager
								.getDefaultSharedPreferences(CasualWatchSettingActivity.this);
						mEditor = sharePref.edit();
						mEditor.putString("Casual_Watch_Type", "[1]" + region);
						mEditor.commit();

						radioPreferenceRegion.setSummary(region);
					}
				});
		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						radioPreferenceRegion.setChecked(false);
					}
				});
		alert.show();

		if (IGeneral.SERVICE_NAME_TWITTER
				.equals(statusData.getCurrentService())
				|| IGeneral.SERVICE_NAME_TWITTER_PROXY
				.equals(statusData.getCurrentService())) {
			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();

			// Get Service From Current Account
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_LOCATIONS_AVAILIABLE_TRENDS,
						apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	private void showKeywordDialog() {

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.dialog_keyword_search, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				CasualWatchSettingActivity.this);
		alert.setTitle(R.string.setting_casual_watch_search);
		alert.setView(textEntryView);
		alert.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mEditKeyword = (EditText) textEntryView
								.findViewById(R.id.editText_dialog_keyword_search);
						String keyword = mEditKeyword.getText().toString();
						if (!keyword.equals("")) {
							
							sharePref = PreferenceManager
									.getDefaultSharedPreferences(CasualWatchSettingActivity.this);
							mEditor = sharePref.edit();
							mEditor.putString("Casual_Watch_Type", "[2]"
									+ keyword);
							mEditor.commit();

							radioPreferenceSearch.setSummary(keyword);

						} else {
							radioPreferenceSearch.setChecked(false);
//							Toast.makeText(
//									CasualWatchSettingActivity.this,
//									R.string.setting_casual_watch_search_keyword,
//									Toast.LENGTH_SHORT).show();
						}
					}
				});

		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						radioPreferenceSearch.setChecked(false);
					}
				});
		alert.show();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;

	}

	private void createSpinner(ArrayList<TrendInfo> listInfoList) {
		// Clear
		trendsTypeData.clear();

		// Change Data
		if (listInfoList.size() > 0) {
			for (TrendInfo listInfo : listInfoList) {
//				if (!trendsTypeData.contains(listInfo.getCountry())) {
					trendsTypeData.add(listInfo.getName());
//				}
			}
		} else {
			// No data for this trend.
			Toast.makeText(
					CasualWatchSettingActivity.this,
					radioPreferenceRegion.getContext().getString(
							R.string.trends_no_data), Toast.LENGTH_SHORT)
					.show();
		}

		// Notify
		trendsTypeAdapter.notifyDataSetChanged();
	}

	private void initDatas() {
		
		SharedPreferences restore = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		String radioPreStr = restore.getString("Casual_Watch_Type", "");
		if(!radioPreStr.equals("")){
			
			String key = radioPreStr.substring(radioPreStr.indexOf("[")+1,radioPreStr.indexOf("]"));
			String summery = radioPreStr.substring(radioPreStr.indexOf("]")+1,
					radioPreStr.length());					
			 if(key.equals("0")){
			 radioPreferencePublic.setChecked(true);
			 radioPreferencePublic.setSummary(summery);
			 radioPreferenceRegion.setChecked(false);
			 radioPreferenceRegion.setSummary("");
			 radioPreferenceSearch.setChecked(false);
			 radioPreferenceSearch.setSummary("");
			 }
			
			 if(key.equals("1") ){
			 radioPreferenceRegion.setChecked(true);
			 radioPreferenceRegion.setSummary(summery);
			 radioPreferencePublic.setChecked(false);
			 radioPreferencePublic.setSummary("");
			 radioPreferenceSearch.setChecked(false);
			 radioPreferenceSearch.setSummary("");
			 }
			 if(key.equals("2")){
			 radioPreferenceSearch.setChecked(true);
			 radioPreferenceSearch.setSummary(summery);
			 radioPreferenceRegion.setChecked(false);
			 radioPreferenceRegion.setSummary("");
			 radioPreferencePublic.setChecked(false);
			 radioPreferencePublic.setSummary("");
			 }
			
		}

	}

}
