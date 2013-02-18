package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest.permission;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.communication.DownloadService;
import com.anhuioss.crowdroid.communication.DownloadServiceInterface;
import com.anhuioss.crowdroid.communication.DownloadServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class LBSGetAroundPeopleActivity extends BasicActivity implements
		OnClickListener, ServiceConnection {

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	Map<String, Object> parameters = new HashMap<String, Object>();

	private ListView listView = null;

	private RelativeLayout relativeRight = null;

	private RelativeLayout relativeBottom = null;

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

	// head
	private Button headerBack = null;

	private Button headerHome = null;

	private TextView headerName = null;

	private int size = 0;

	private SimpleAdapter adapter;

	protected ArrayList<UserInfo> userInfoDataList;

	protected ArrayList<UserInfo> currentList = new ArrayList<UserInfo>();

	private StatusData statusData;

	private SettingData settingData;

	private AccountData currentAccount;

	private String screenName;

	private String userName;

	private String uid = "";

	/** Next Cursor (Twitter) */
	public static String pageinfo = "";

	public static List<String> userIdsForLookup;

	private int currentPage = 0;

	private String imageShow;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private double initLongitude;

	private double initLatitude;

	private DownloadServiceInterface downloadServiceInterface;

	private DownloadServiceListener.Stub downloadServiceListener = new DownloadServiceListener.Stub() {

		@Override
		public void requestCompleted(String uid, String statusCode,
				byte[] message) throws RemoteException {

			try {

				// Get Bitmap
				if (statusCode != null && statusCode.equals("200")) {

					Bitmap bitmap;
					if (message != null && message.length > 0) {
						bitmap = BitmapFactory.decodeByteArray(message, 0,
								message.length);
					} else {
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.image);
					}

					TimelineActivity.userImageMap.put(uid, bitmap);

					// Download Size
					size = size - 1;

					if (size == 0) {

						// setProgressBarIndeterminateVisibility(false);
						closeProgressDialog();

						// User Image
						loadUserImage(currentList);

						// Create ListView
						createListView(currentList);

					}

				}

				System.gc();

			} catch (OutOfMemoryError e) {

				System.gc();

				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.image);

				TimelineActivity.userImageMap.put(uid, bitmap);

				// Download Size
				size = size - 1;

				if (size == 0) {

					// setProgressBarIndeterminateVisibility(false);
					closeProgressDialog();

					// User Image
					loadUserImage(currentList);

					// Create ListView
					createListView(currentList);

				}
			}

		}
	};

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				if (pageinfo != ""
						|| (userIdsForLookup != null && userIdsForLookup.size() > 0)) {
					refresh();
				}
			} else {
				Intent intent = new Intent(LBSGetAroundPeopleActivity.this,
						ProfileActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", userInfoDataList.get(position)
						.getScreenName());
				bundle.putString("uid", userInfoDataList.get(position).getUid());
				bundle.putString("user_name", userInfoDataList.get(position)
						.getUserName());
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtras(bundle);
				startActivity(intent);
			}

		}

	};

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")) {

				// Parser
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				ParseHandler parseHandler = new ParseHandler();
				userInfoList = (ArrayList<UserInfo>) parseHandler.parser(
						service, type, statusCode, message);

				if (userInfoList.size() > 0) {

					createListView(userInfoList);
					addItemForMoreTweets();

				}

			} else {
				Toast.makeText(
						LBSGetAroundPeopleActivity.this,
						ErrorMessage.getErrorMessage(
								LBSGetAroundPeopleActivity.this, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	// -----------------------------------------------------------------------------
	/**
	 * Called when Activity is Created.
	 */
	// -----------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();

		// Set Gallery
		setLayoutResId(R.layout.timeline_layout);

		userInfoDataList = new ArrayList<UserInfo>();

		// right
		relativeRight = (RelativeLayout) findViewById(R.id.layout_main_right);
		relativeBottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);

		relativeRight.setVisibility(View.GONE);
		relativeBottom.setVisibility(View.GONE);
		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerName.setText(R.string.friends_list);
		headerHome.setBackgroundResource(R.drawable.main_home);

		// Find Views
		listView = (ListView) findViewById(R.id.list_view);
		listView.setDivider(null);

		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);

		adapter = new SimpleAdapter(this, data,
				R.layout.sina_basic_timeline_layout_list_item, new String[] {
						"screenName", "status", "userImage", "time",
						"verified", "web", "webStatus", "retweetStatus",
						"retweetCount", "commentCount", "moreTweets" },
				new int[] { R.id.screen_name, R.id.status, R.id.user_image,
						R.id.update_time, R.id.sina_user_verified,
						R.id.web_view_status, R.id.web_status,
						R.id.web_retweet_status, R.id.text_retweet_count,
						R.id.text_comment_count, R.id.text_get_more_tweets });

		listView.setAdapter(adapter);

	}

	// -----------------------------------------------------------------------------
	/**
	 * Called When Activity Is Started.
	 */
	// -----------------------------------------------------------------------------
	@Override
	public void onStart() {
		super.onStart();
		isRunning = true;
		userIdsForLookup = null;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();

		imageShow = settingData.getSelectionShowImage();

		// screenName = getIntent().getExtras().getString("screenName");
		// userName = getIntent().getExtras().getString("userName");
		// uid = getIntent().getExtras().getString("uid");

		SettingData settingData = crowdroidApplication.getSettingData();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();

		adapter.setViewBinder(new MyImageBinder(fontColor, fontSize, null, this));

		PackageManager pm = getPackageManager();

		if (pm != null
				&& pm.checkPermission(permission.ACCESS_FINE_LOCATION,
						getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			startLocationService();
		}

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		// Bind Download Service
		// Intent intentDownload = new Intent(this, DownloadService.class);
		// bindService(intentDownload, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		stopLocationService();
		// Unbind Service
		unbindService(this);
	}

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

		// // 效果作为最后获得一分钟之久的位置信息。
		// final Location lastKnownLocation =
		// locationManager.getLastKnownLocation(provider);
		// // XXX - 请更改确定必要的标准。
		// if (lastKnownLocation != null && (new Date().getTime() -
		// lastKnownLocation.getTime()) <= (5 * 60 * 1000L)) {
		// location = lastKnownLocation;
		// setLocation(lastKnownLocation);
		// new myThread().start();
		// return;
		// }

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
							// Toast.makeText(SendMessageActivity.this,
							// "开始确定位置", Toast.LENGTH_LONG).show();
						} else if (time >= (30 * 1000L)) {
							Toast.makeText(LBSGetAroundPeopleActivity.this,
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
					initLongitude = location.getLongitude();
					initLatitude = location.getLatitude();
					getLBSTimeline();
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

	private Handler locationHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case OBTAIN_LOCATION_FAILED: {
				Toast.makeText(getApplicationContext(),
						getString(R.string.obtain_location_info_failed),
						Toast.LENGTH_SHORT).show();
				stopLocationService();
				break;
			}
			case OBTAIN_LOCATION_SUCESSED: {
				// Toast.makeText(getApplicationContext(),
				// getString(R.string.obtain_location_info),
				// Toast.LENGTH_SHORT).show();
				// closeGps();
				stopLocationService();
				break;
			}
			default:
				break;
			}
		}

	};

	private void getLBSTimeline() {
		showProgressDialog();

		// Location
		if (location != null) {
			// Prepare Parameters

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)) {
				currentPage++;
				parameters.put("latitude", String.valueOf(initLatitude));
				parameters.put("longitude", String.valueOf(initLongitude));
				parameters.put("pageinfo", String.valueOf(pageinfo));
				parameters.put("page", String.valueOf(currentPage));
				try {
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_AROUNG_PEOPLE_BY_LBS,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_WANGYI)) {
				currentPage++;
				parameters.put("lat", String.valueOf(initLatitude));
				parameters.put("long", String.valueOf(initLongitude));
				try {
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_AROUNG_PEOPLE_BY_LBS,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else if (location == null) {
			// Pop up message (could not obtain Location Info)
			locationHandler.sendEmptyMessage(OBTAIN_LOCATION_FAILED);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.head_refresh: {
			Intent home = new Intent(LBSGetAroundPeopleActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}
		case R.id.head_back: {

			finish();
			break;
		}
		default:
			break;
		}
	}

	private void refresh() {
		showProgressDialog();

		if (apiServiceInterface == null) {
			return;
		}
		currentPage++;
		parameters.put("latitude", String.valueOf(initLatitude));
		parameters.put("longitude", String.valueOf(initLongitude));
		parameters.put("pageinfo", pageinfo);
		parameters.put("page", String.valueOf(currentPage));
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_WANGYI)) {
			parameters.put("lat", String.valueOf(initLatitude));
			parameters.put("long", String.valueOf(initLongitude));
		}

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_AROUNG_PEOPLE_BY_LBS,
					apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public static String getIdsForLookup() {

		if (userIdsForLookup == null || userIdsForLookup.size() == 0) {
			return null;
		}

		StringBuffer userIds = new StringBuffer();
		int i = 0;

		for (; i < 20 && i < userIdsForLookup.size(); i++) {
			userIds.append(userIdsForLookup.get(i)).append(",");
		}

		// for(; i > 0 ; i--) {
		// userIdsForLookup.remove(0);
		// }

		return userIds.subSequence(0, userIds.length() - 1).toString();
	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(ArrayList<UserInfo> userInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		// Prepare ArrayList
		for (UserInfo userInfo : userInfoList) {

			userInfoDataList.add(userInfo);

			Map<String, Object> map;
			map = new HashMap<String, Object>();
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)) {
				map.put("screenName", userInfo.getUserName() + " " + "@"
						+ userInfo.getScreenName());
			} else {
				map.put("screenName", userInfo.getScreenName() == null ? ""
						: userInfo.getScreenName());
			}
			pageinfo = userInfo.getPageinfo();
			map.put("userName", userInfo.getUserName());
			map.put("status", userInfo.getDescription());
			map.put("webStatus", userInfo.getDescription());
			if (imageShow.equals(BrowseModeActivity.select[0])
					|| imageShow.equals(BrowseModeActivity.select[1])) {
				// Use Download Image
				map.put("userImage", userInfo.getUserImageURL());
			} else {
				// Use Default Image
				map.put("userImage",
						String.valueOf(R.drawable.default_user_image));
			}
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)) {
				map.put("verified", userInfo.getVerified());
			}

			map.put("web", "");
			map.put("retweetCount", "");
			map.put("commentCount", "");
			map.put("time", "");
			addDatas.add(map);

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();

	}

	// -----------------------------------------------------------------------------
	/**
	 * Load userImage.
	 */
	// -----------------------------------------------------------------------------
	private void loadUserImage(ArrayList<UserInfo> userInfoList) {

		if (userInfoList == null) {
			return;
		}

		// Garbage Collection
		System.gc();

		for (UserInfo info : userInfoList) {

			// Set Image To Info
			info.setUserImage(TimelineActivity.userImageMap.get(info.getUid()));

		}

		// Garbage Collection
		System.gc();

	}

	private void downloadImage(ArrayList<UserInfo> userInfoList) {

		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();

		// The List For Download
		ArrayList<UserInfo> downloadUserList = new ArrayList<UserInfo>();

		// Prepare List For Download
		for (UserInfo userInfo : userInfoList) {
			if (!TimelineActivity.userImageMap.containsKey(userInfo.getUid())
					&& userInfo.getUid() != null) {
				downloadUserList.add(userInfo);
			}
		}

		size = downloadUserList.size();

		if (size == 0) {

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();

			// User Image
			loadUserImage(userInfoList);

			// Create ListView
			createListView(userInfoList);

		}

		// Download
		for (UserInfo userInfo : downloadUserList) {
			try {

				downloadServiceInterface.request(userInfo.getUid(),
						userInfo.getUserImageURL(), downloadServiceListener);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		if (name.getShortClassName().equals(TimelineActivity.API_SERVICE_NAME)) {
			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

		} else {
			downloadServiceInterface = DownloadServiceInterface.Stub
					.asInterface(service);
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
		downloadServiceInterface = null;

	}

	public void addItemForMoreTweets() {

		if (data.size() > 0
				&& !data.get(data.size() - 1).get("screenName").equals("")) {
			try {
				Map<String, Object> map;
				map = new HashMap<String, Object>();
				map.put("screenName", "");
				map.put("moreTweets",
						getResources().getString(R.string.get_more_tweets));
				data.add(map);
				adapter.notifyDataSetChanged();
			} catch (OutOfMemoryError e) {
				System.gc();
			}
		}
	}

	private void deleteItemForMoreTweets() {
		int size = data.size();
		for (int i = size - 1; i >= 0; i--) {
			if (data.get(i).get("screenName").equals("")) {
				data.remove(i);
				adapter.notifyDataSetChanged();
				break;
			}
		}

	}

	@Override
	protected void refreshByMenu() {
		data.clear();
		currentList.clear();
		userInfoDataList.clear();
		adapter.notifyDataSetChanged();

		// Set Page
		currentPage = 0;

		// Refresh
		refresh();
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(this);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress != null) {
			progress.dismiss();
		}
	}
}
