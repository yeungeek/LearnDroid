package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.dialog.AtUserSelectDialog;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.TranslateDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.sns.operations.UpdateBlogActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class LBSUpdateMessageActivity extends Activity implements
		OnClickListener, TextWatcher, ServiceConnection {

	private SharedPreferences sharePreference;

	private SharedPreferences.Editor editor;

	TextView headName;

	TextView locationNameTitle;

	TextView locationAddressTitle;

	TextView contentTitle;

	EditText locationName;

	EditText locationAddress;

	CrowdroidApplication crowdroidApplication;

	/** location */
	Spinner spinnerLocationAddress;

	/** Data For Spinner */
	private List<String> locationInfoData = new ArrayList<String>();

	/** Adapter */
	private ArrayAdapter<String> locationAddressAdapter;

	/** Text */
	AutoCompleteTextView updateText;

	/** Translation */
	Button translationButton;

	/** Upload Image */
	Button uploadImageButton;

	/** Shorten URL */
	Button shortenUrlButton;

	/** Long Tweet */
	Button longTweetButton;

	Button emotionButton;

	Button cameraButton;

	Button btn_back;

	Button btn_home;

	Button recorderButton;

	Button addTrends;

	Button atButton;

	/** Count For Character */
	TextView countText;

	/** Confirm */
	Button send;

	Button btnLocationMap;

	/** Intent Flag */
	String action = null;

	private StatusData statusData;

	private HandleProgressDialog progress;

	private AlertDialog previewDialog;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	public static ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();

	private ArrayList<LocationInfo> locationSpinnerData = new ArrayList<LocationInfo>();

	private Handler mHandler = new Handler();

	private int MAX_TEXT_COUNT = 140;

	protected static final int OBTAIN_LOCATION_FAILED = 0;

	protected static final int OBTAIN_LOCATION_SUCESSED = 1;

	/** Location Manager(for GPS) */
	private LocationManager locationManager;

	/** Location Listener(for GPS) */
	LocationListener locationListener;

	/** Location (for GPS) */
	Location location;

	private Timer locationTimer;

	private long time;

	private String selectedLocationAddress = "";

	private String selectedLocationId = "";

	private String longitude = "";

	private String latitude = "";

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_EMOTION) {

					ParseHandler parseHandler = new ParseHandler();
					emotionList = (ArrayList<EmotionInfo>) parseHandler.parser(
							service, type, statusCode, message);
					if (!emotionList.isEmpty()) {
						// showEmotionsDialog();
						initEmotionView(emotionList);
					}

				} else if (type == CommHandler.TYPE_GET_LBS_LOCATION_LIST) {
					// Parser
					ArrayList<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
					ParseHandler parseHandler = new ParseHandler();
					locationInfoList = (ArrayList<LocationInfo>) parseHandler
							.parser(service, type, statusCode, message);

					locationSpinnerData = locationInfoList;

					// Create Spinner's Data
					createSpinner(locationInfoList);
				}

				if (type == CommHandler.TYPE_UPDATE_LBS_MESSAGE) {

					if (!statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI))
						finish();
				}
				if (type == CommHandler.TYPE_UPDATE_STATUS
						&& statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_WANGYI)) {
					finish();
				}
			} else if (statusCode.equals("404")) {
				Toast.makeText(
						LBSUpdateMessageActivity.this,
						getResources().getString(R.string.discovery_lbs_no_get),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						LBSUpdateMessageActivity.this,
						ErrorMessage.getErrorMessage(
								LBSUpdateMessageActivity.this, statusCode),
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_update_lbs_message);

		sharePreference = getSharedPreferences("SHARE_INIT_UPDATE_LBS_STATUS",
				0);
		// Find Views
		locationNameTitle = (TextView) findViewById(R.id.location_name_title);
		locationAddressTitle = (TextView) findViewById(R.id.location_address_title);
		contentTitle = (TextView) findViewById(R.id.content);
		locationName = (EditText) findViewById(R.id.location_name);
		locationAddress = (EditText) findViewById(R.id.location_address);
		spinnerLocationAddress = (Spinner) findViewById(R.id.spinner_location_address);
		updateText = (AutoCompleteTextView) findViewById(R.id.update_text);
		translationButton = (Button) findViewById(R.id.translateButton);
		uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
		shortenUrlButton = (Button) findViewById(R.id.shortenUrlButton);
		longTweetButton = (Button) findViewById(R.id.longTweetButton);
		emotionButton = (Button) findViewById(R.id.emotionButton);
		cameraButton = (Button) findViewById(R.id.cameraButton);
		recorderButton = (Button) findViewById(R.id.recorderButton);
		countText = (TextView) findViewById(R.id.counterText);
		send = (Button) findViewById(R.id.send);
		btnLocationMap = (Button) findViewById(R.id.btn_lookup_location_map);
		headName = (TextView) findViewById(R.id.head_Name);

		addTrends = (Button) findViewById(R.id.addTrends);

		atButton = (Button) findViewById(R.id.atButton);
		btn_back = (Button) findViewById(R.id.head_back);
		btn_home = (Button) findViewById(R.id.head_refresh);
		btn_home.setBackgroundResource(R.drawable.main_home);

		// Set Listener
		updateText.setOnClickListener(this);
		updateText.addTextChangedListener(this);
		translationButton.setOnClickListener(this);
		emotionButton.setOnClickListener(this);
		send.setOnClickListener(this);
		btnLocationMap.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);

		// Create Adapter
		locationAddressAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, locationInfoData);
		locationAddressAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Set Adapter
		spinnerLocationAddress.setAdapter(locationAddressAdapter);

		// Set Item Selected Listener For Spinner
		spinnerLocationAddress
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
								.getCurrentService())) {
							selectedLocationAddress = locationSpinnerData.get(
									position).getLocationAddress();
							selectedLocationId = locationSpinnerData.get(
									position).getLocationId();
						} else if (IGeneral.SERVICE_NAME_TENCENT
								.equals(statusData.getCurrentService())) {
							selectedLocationAddress = locationSpinnerData.get(
									position).getLocationAddress()
									+ locationSpinnerData.get(position)
											.getLocationName();
						} else if (IGeneral.SERVICE_NAME_SINA.equals(statusData
								.getCurrentService())) {
							selectedLocationAddress = locationSpinnerData.get(
									position).getLocationAddress()
									+ locationSpinnerData.get(position)
											.getLocationName();
							longitude = locationSpinnerData.get(position)
									.getLocationLongitude();
							latitude = locationSpinnerData.get(position)
									.getlocationLatitude();
						} else if (IGeneral.SERVICE_NAME_WANGYI
								.equals(statusData.getCurrentService())) {
							selectedLocationAddress = locationSpinnerData.get(
									position).getLocationAddress()
									+ locationSpinnerData.get(position)
											.getLocationName();
							longitude = locationSpinnerData.get(position)
									.getLocationLongitude();
							latitude = locationSpinnerData.get(position)
									.getlocationLatitude();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}

				});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		SettingData settingData = crowdroidApplication.getSettingData();
		String imagePath = settingData.getWallpaper();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		imagePath = settingData.getWallpaper();
		// Set Background
		loadBackGroundImage(imagePath);

		initLBSUpdateViews();

		notifyTextHasChanged();

		PackageManager pm = getPackageManager();
		if (pm != null
				&& pm.checkPermission(permission.ACCESS_FINE_LOCATION,
						getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			// initGps();
			startLocationService();
		}

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// closeGps();
		stopLocationService();
		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		String inital = sharePreference.getString(
				"SHARE_INIT_UPDATE_LBS_STATUS", "");
		boolean isFromOutSideInsert = sharePreference.getBoolean(
				"WHEATHER_FROM_LBS_OUTSIDE_INSERT", false);
		if (isFromOutSideInsert) {
			editor = sharePreference.edit();
			editor.putBoolean("WHEATHER_FROM_LBS_OUTSIDE_INSERT", false);
			editor.commit();

			String front = sharePreference.getString("EDIT_INIT_STATUS_FRONT",
					"");
			String behind = sharePreference.getString(
					"EDIT_INIT_STATUS_BEHIND", "");
			updateText.setText(front + inital + behind);
			updateText.setSelection(updateText.getText().length());
		}

		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		notifyTextHasChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

			Intent home = new Intent(LBSUpdateMessageActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}
		case R.id.translateButton: {

			// Translation
			new TranslateDialog(this, updateText).show();
			break;

		}
		case R.id.emotionButton: {

			int index = updateText.getSelectionStart();// 获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(updateText.getText()).substring(0, index));
			editor.putString(
					"EDIT_INIT_STATUS_BEHIND",
					String.valueOf(updateText.getText()).substring(index,
							updateText.getText().length()));
			editor.commit();

			if (SendMessageActivity.emotionList.isEmpty()) {

				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_EMOTION, apiServiceListener,
							new HashMap<String, Object>());

				} catch (RemoteException e) {
					e.printStackTrace();
				}

			} else {
				initEmotionView(SendMessageActivity.emotionList);
			}

			break;

		}
		case R.id.send: {

			// Update
			showProgressDialog();

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();

			parameters.put("status", updateText.getText().toString());

			// Location
			if (location != null) {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
					AccountData account = crowdroidApplication.getAccountList()
							.getCurrentAccount();
					parameters.put("user_name", account.getAccessToken());
					parameters.put("user_password", account.getTokenSecret());
					parameters.put("service", account.getService());
					parameters.put("status", "#" + selectedLocationAddress
							+ " " + updateText.getText().toString());
					parameters.put("place_id", selectedLocationId);
					// parameters.put("lat", "37.78215");
					// parameters.put("long", "-122.40060");
					parameters.put("lat", location.getLatitude());
					parameters.put("long", location.getLongitude());
				} else {

					parameters.put("latitude",
							String.valueOf(location.getLatitude()));
					parameters.put("longitude",
							String.valueOf(location.getLongitude()));

					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)) {
						parameters.put("locationName", locationName.getText()
								.toString());
						parameters.put("locationAddress", locationAddress
								.getText().toString());
						parameters.put("poiId", System.currentTimeMillis());
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)) {
						parameters.put("locationAddress", "#"
								+ selectedLocationAddress + "#");
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SINA)) {
						parameters.put("locationAddress", "#"
								+ selectedLocationAddress + "#"
								+ updateText.getText().toString());
						parameters.put("long", longitude);
						parameters.put("lat", latitude);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)) {
						parameters.put("status", ":" + selectedLocationAddress
								+ ":" + updateText.getText().toString());
						parameters.put("long", longitude);
						parameters.put("lat", latitude);
						try {
							apiServiceInterface.request(
									statusData.getCurrentService(),
									CommHandler.TYPE_UPDATE_STATUS,
									apiServiceListener, parameters);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				// Request (get service from status information)
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_UPDATE_LBS_MESSAGE,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (location == null) {
				// Pop up message (could not obtain Location Info)
				locationHandler.sendEmptyMessage(OBTAIN_LOCATION_FAILED);
			}
			break;
		}
		case R.id.btn_lookup_location_map: {

			if (location != null) {
				String latitude = String.valueOf(location.getLatitude());
				String longitude = String.valueOf(location.getLongitude());
				String url = "http://maps.google.com/maps/api/staticmap?center="
						+ latitude
						+ ","
						+ longitude
						+ "&zoom=16&size=800x800&sensor=false";
				// String url =
				// "http://maps.google.com/maps/api/staticmap?center=37.78215,-122.40060&zoom=16&size=800x800&sensor=false";
				StringBuffer htmlData = new StringBuffer();
				htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				htmlData.append("<center>");
				htmlData.append(
						"<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
						.append(url).append("' />");
				htmlData.append("<center></body></html>");

				Intent intent = new Intent();
				intent.setClass(LBSUpdateMessageActivity.this,
						PreviewImageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				Bundle bundle = new Bundle();
				bundle.putString("url", htmlData.toString());
				bundle.putBoolean("map", true);
				intent.putExtras(bundle);
				startActivity(intent);
			}

		}
		default: {

		}
		}
	}

	private void createSpinner(ArrayList<LocationInfo> locationInfoList) {

		// Clear
		locationInfoData.clear();

		// Change Data
		if (locationInfoList.size() > 0) {
			for (LocationInfo locationInfo : locationInfoList) {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
					locationInfoData.add(locationInfo.getLocationAddress());
				} else {
					locationInfoData.add(locationInfo.getLocationAddress()
							+ "\n" + locationInfo.getLocationName());
				}
			}
		} else {
			// No data for this trend.
			Toast.makeText(this,
					getResources().getString(R.string.discovery_lbs_no_data),
					Toast.LENGTH_SHORT).show();
		}

		// Notify
		locationAddressAdapter.notifyDataSetChanged();

	}

	private void initLBSUpdateViews() {

		headName.setText(getString(R.string.discovery_lbs_name));
		locationNameTitle
				.setText(getString(R.string.discovery_lbs_location_name));
		locationAddressTitle
				.setText(getString(R.string.discovery_lbs_location_address));
		contentTitle.setText(R.string.content);

		countText.setText(String.valueOf(MAX_TEXT_COUNT));

		uploadImageButton.setVisibility(View.GONE);
		shortenUrlButton.setVisibility(View.GONE);
		longTweetButton.setVisibility(View.GONE);
		cameraButton.setVisibility(View.GONE);
		recorderButton.setVisibility(View.GONE);
		addTrends.setVisibility(View.GONE);
		atButton.setVisibility(View.GONE);

		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			locationNameTitle.setVisibility(View.VISIBLE);
			locationName.setVisibility(View.VISIBLE);
			locationAddressTitle.setVisibility(View.VISIBLE);
			locationAddress.setVisibility(View.VISIBLE);
			spinnerLocationAddress.setVisibility(View.GONE);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
			locationName.setVisibility(View.GONE);
			locationAddressTitle.setVisibility(View.GONE);
			locationAddress.setVisibility(View.GONE);
			spinnerLocationAddress.setVisibility(View.VISIBLE);
		}

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			emotionButton.setVisibility(View.GONE);
		}
	}

	private void notifyTextHasChanged() {
		int leftCount = 0;
		//
		// if (MAX_TEXT_COUNT != 140) {
		leftCount = MAX_TEXT_COUNT - updateText.getText().toString().length();
		countText.setText(String.valueOf(leftCount));
		// }

		if (leftCount < 0) {

			countText.setTextColor(Color.RED);

			// Disable Confirm Button
			send.setEnabled(false);

		} else {

			countText.setTextColor(Color.BLACK);

			// Enable Confirm Button
			send.setEnabled(true);
		}
	}

	private void loadBackGroundImage(String path) {

		if (path == null || path.equals("0")) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("-") == 0) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("/") == -1) {
			getWindow().setBackgroundDrawableResource(Integer.valueOf(path));
		} else {
			File file = new File(path);
			FileInputStream input = null;
			if (file.exists() && file.canRead() && file.isFile()) {
				try {
					input = new FileInputStream(file);

					// Get BitMap and set to background
					BitmapDrawable drawable = new BitmapDrawable(input);
					getWindow().setBackgroundDrawable(drawable);
				} catch (Exception e) {

				} catch (OutOfMemoryError e) {

				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				LBSUpdateMessageActivity.this);
		LayoutInflater inflater = LayoutInflater
				.from(LBSUpdateMessageActivity.this);
		final View textEntryView = inflater.inflate(
				R.layout.dialog_show_gridview_emotion, null);
		builder.setView(textEntryView);

		GridView gridView = (GridView) textEntryView
				.findViewById(R.id.gridView_emotions);
		final ArrayList<HashMap<String, String>> emotionData = new ArrayList<HashMap<String, String>>();
		EmotionInfo emotionInfo;

		for (int i = 0; i < emotionInfoList.size(); i++) {
			emotionInfo = emotionInfoList.get(i);
			if (emotionInfo != null) {
				HashMap<String, String> map = new HashMap<String, String>();
				// “你的json地址”+json中取图片的相对地址得到绝对地址
				map.put("itemImage", emotionInfo.getUrl());
				map.put("itemText", emotionInfo.getPhrase());
				emotionData.add(map);
			}
		}
		previewDialog = builder.show();

		// insert blog emotion
		if (action != null && action.equals("blog")) {
			gridView.setAdapter(new GridViewAdapter(this, previewDialog,
					emotionData, "blog"));
		} else {
			// state tweet retweet comment
			gridView.setAdapter(new GridViewAdapter(this, previewDialog,
					emotionData));
		}
		// 设置emotionDialog 透明度
		WindowManager manager = previewDialog.getWindow().getWindowManager();
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		// WindowManager.LayoutParams lp = previewDialog.getWindow()
		// .getAttributes();
		// lp.alpha = 0.0f;

		// 设置Dialog大小
		// previewDialog.getWindow().setAttributes(lp);
		previewDialog.getWindow().setLayout(width * 9 / 10, height * 2 / 3);
	}

	// -----------------------------------------------------
	private void startLocationService() {

		location = null;

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 对于设备没有配备定位功能
		if (locationManager == null) {
			return;
		}

		// @see
		// http://developer.android.com/reference/android/location/LocationManager.html#getBestProvider%28android.location.Criteria,%20boolean%29
		final Criteria criteria = new Criteria();
		// 最佳做法是没有设置PowerRequirement
		// 精度不设置最佳实践
		// criteria.setAccuracy(Criteria.ACCURACY_FINE); ← Accuracy 你必须做的最好模式
		// 这些都是必要的
		criteria.setBearingRequired(false); // 方位不要
		criteria.setSpeedRequired(false); // 速度不要
		criteria.setAltitudeRequired(false); // 高度不要

		final String provider = locationManager.getBestProvider(criteria, true);
		if (provider == null) {
			// 如果没有启用的位置信息，如谷歌地图应用程序
			// [改进的功能您的位置]
			// 开始对话
			new AlertDialog.Builder(this)
					.setTitle(R.string.open_gps_module_title)
					.setMessage(R.string.open_gps_module_message)
					.setPositiveButton(R.string.menu_setting,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
									// 设置屏幕的终端位置へ遷移
									try {
										startActivity(new Intent(
												"android.settings.LOCATION_SOURCE_SETTINGS"));
									} catch (final ActivityNotFoundException e) {
										// 没有位置信息设定画面
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
								} // 何も行わない
							}).create().show();

			stopLocationService();
			return;
		}

		// Toast の表示と LocationListener の生存時間启动计时器。
		locationTimer = new Timer(true);
		time = 0L;
		final Handler handler = new Handler();
		locationTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (time == 1000L) {
							Toast.makeText(
									LBSUpdateMessageActivity.this,
									getResources().getString(
											R.string.discovery_lbs_decide),
									Toast.LENGTH_LONG).show();
						} else if (time >= (30 * 1000L)) {
							Toast.makeText(LBSUpdateMessageActivity.this,
									R.string.obtain_location_info_failed,
									Toast.LENGTH_LONG).show();

							stopLocationService();
						}
						time = time + 1000L;
					}
				});
			}
		}, 0L, 1000L);

		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(final Location loc) {

				if (loc != null) {
					location = loc;
					locationHandler.sendEmptyMessage(OBTAIN_LOCATION_SUCESSED);
					startLBSConnect();
				}
			}

			@Override
			public void onProviderDisabled(final String provider) {
			}

			@Override
			public void onProviderEnabled(final String provider) {
			}

			@Override
			public void onStatusChanged(final String provider,
					final int status, final Bundle extras) {
			}
		};
		locationManager.requestLocationUpdates(provider, 60000, 0,
				locationListener);

	}

	private Handler locationHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case OBTAIN_LOCATION_FAILED: {
				Toast.makeText(getApplicationContext(),
						getString(R.string.obtain_location_info_failed),
						Toast.LENGTH_SHORT).show();
				// closeGps();
				stopLocationService();

				// setting search map
				btnLocationMap.setEnabled(false);

				break;
			}
			case OBTAIN_LOCATION_SUCESSED: {
				Toast.makeText(getApplicationContext(),
						getString(R.string.obtain_location_info),
						Toast.LENGTH_SHORT).show();
				// closeGps();
				stopLocationService();

				// setting search map
				btnLocationMap.setEnabled(true);

				break;
			}
			default:
				break;
			}
		}

	};

	private void stopLocationService() {
		if (locationTimer != null) {
			locationTimer.cancel();
			locationTimer.purge();
			locationTimer = null;
		}
		if (locationManager != null) {
			if (locationListener != null) {
				locationManager.removeUpdates(locationListener);
				locationListener = null;
			}
			locationManager = null;
		}
	}

	private void startLBSConnect() {
		if (IGeneral.SERVICE_NAME_TENCENT
				.equals(statusData.getCurrentService())
				|| IGeneral.SERVICE_NAME_SINA.equals(statusData
						.getCurrentService())
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();

			if (location != null) {
				parameters.put("latitude",
						String.valueOf(location.getLatitude()));
				parameters.put("longitude",
						String.valueOf(location.getLongitude()));
				// Get Service From Current Account
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_LBS_LOCATION_LIST,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void showProgressDialog() {
		if (progress == null) {
			progress = new HandleProgressDialog(this);
		}
		progress.show();

	}

	private void closeProgressDialog() {
		if (progress != null) {
			progress.dismiss();
		}
	}

}
