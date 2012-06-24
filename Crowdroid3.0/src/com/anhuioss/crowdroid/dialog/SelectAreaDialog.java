package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.AreaTimelineActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.service.tencent.TencentParserHandler;

public class SelectAreaDialog extends AlertDialog implements ServiceConnection {

	private TextView countryTitle;

	private TextView stateTitle;

	private TextView cityTitle;

	private Button btnOK;

	private Context mContext;

	/** Spinner */
	private Spinner countrySpinner;

	private Spinner stateSpinner;

	private Spinner citySpinner;
	/** Data For Spinner */
	private List<String> countriesData = new ArrayList<String>();

	private List<String> statesData = new ArrayList<String>();

	private List<String> citiesData = new ArrayList<String>();

	private List<String> trendsTypeData = new ArrayList<String>();

	/** Adapter */
	private ArrayAdapter<String> countriesAdapter;

	private ArrayAdapter<String> statesAdapter;

	private ArrayAdapter<String> citiesAdapter;

	private ArrayList<TimeLineInfo> timelineInfoListData = new ArrayList<TimeLineInfo>();

	private ArrayList<LocationInfo> locationInfoListData = new ArrayList<LocationInfo>();

	private ApiServiceInterface apiServiceInterface;

	private String stateCode = "";

	private String cityCode = "";

	public SelectAreaDialog(Context context) {
		super(context);

		mContext = context;

		// Set View
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutView = inflater.inflate(R.layout.activity_selected_area,
				null);
		setView(layoutView);
		setIcon(android.R.drawable.ic_menu_myplaces);
		setTitle(mContext.getString(R.string.discovery_select_area));

		countryTitle = (TextView) layoutView
				.findViewById(R.id.location_country_title);
		stateTitle = (TextView) layoutView
				.findViewById(R.id.location_state_title);
		cityTitle = (TextView) layoutView
				.findViewById(R.id.location_city_title);

		countrySpinner = (Spinner) layoutView
				.findViewById(R.id.spinner_location_country);
		stateSpinner = (Spinner) layoutView
				.findViewById(R.id.spinner_location_state);
		citySpinner = (Spinner) layoutView
				.findViewById(R.id.spinner_location_city);

		btnOK = (Button) layoutView.findViewById(R.id.send);

		countryTitle.setText(mContext
				.getString(R.string.discovery_select_area_country));
		stateTitle.setText(mContext
				.getString(R.string.discovery_select_area_state));
		cityTitle.setText(mContext
				.getString(R.string.discovery_select_area_city));

		countriesData.add("中国");

		// Create Adapter
		countriesAdapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, countriesData);
		countriesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Set Adapter
		countrySpinner.setAdapter(countriesAdapter);

		createStateSpinner();

		stateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				createCitySpinner(position);
				stateCode = timelineInfoListData.get(position).getStatusId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

		citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				cityCode = locationInfoListData.get(position)
						.getLoationCityCode();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

		btnOK.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(mContext,
						AreaTimelineActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("stateCode", stateCode);
				bundle.putString("cityCode", cityCode);
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mContext.startActivity(intent);
				dismiss();
			}
		});
	}

	private void createStateSpinner() {
		timelineInfoListData = TencentParserHandler
				.parseRegionCodeFromFile(mContext);
		if (timelineInfoListData != null && timelineInfoListData.size() > 0) {
			statesData.clear();
			for (TimeLineInfo timelineInfo : timelineInfoListData) {
				statesData.add(timelineInfo.getStatus());
			}
			// Create State Adapter
			statesAdapter = new ArrayAdapter<String>(getContext(),
					android.R.layout.simple_spinner_item, statesData);
			statesAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Set State Adapter
			stateSpinner.setAdapter(statesAdapter);
		}
	}

	private void createCitySpinner(int position) {
		if (timelineInfoListData != null) {
			citiesData.clear();
			TimeLineInfo timelineInfo = timelineInfoListData.get(position);
			locationInfoListData = timelineInfo.getLocationInfoList();
			if (locationInfoListData.size() > 0) {
				for (LocationInfo locationInfo : locationInfoListData) {
					citiesData.add(locationInfo.getLocationCityName());
				}
			}
			// Create State Adapter
			citiesAdapter = new ArrayAdapter<String>(getContext(),
					android.R.layout.simple_spinner_item, citiesData);
			citiesAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Set State Adapter
			citySpinner.setAdapter(citiesAdapter);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// Bind Service
		Intent intent = new Intent(getContext(), ApiService.class);
		getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();
		// Unbind Service
		getContext().unbindService(this);
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

}
