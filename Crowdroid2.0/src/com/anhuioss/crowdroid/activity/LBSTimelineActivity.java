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

import android.Manifest.permission;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicSearchActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.BasicInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

public class LBSTimelineActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private CrowdroidApplication crowdroidApplication;

	private StatusData statusData;

	private SettingData settingData;

	private HandleProgressDialog progress;

	private AlertDialog previewDialog;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

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
	private Button btnHeaderBack = null;

	private Button btnHeaderRefresh = null;

	private TextView headName = null;

	// bottom
	private Button btnHome = null;

	private Button btnNewTweet = null;

	private Button btnDiscover = null;

	private Button btnProfile = null;

	private Button btnMore = null;

	private RelativeLayout relativeRight = null;

	private RelativeLayout relativeBottom = null;

	// content
	private ListView listView = null;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	protected ArrayList<TimeLineInfo> timeLineDataList;

	protected ArrayList<TimeLineInfo> currentList;

	private static boolean isRunning = true;

	private SimpleAdapter adapter;

	private MyImageBinder myImageBinder;

	private String imageShow;

	//
	private String cursor_id;

	private double initLongitude;

	private double initLatitude;

	private int currentPage = 1;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			BasicInfo.setNetTime();

			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_LBS_TIMELINE) {

					closeProgressDialog();
					// Parser
					ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
					ParseHandler parseHandler = new ParseHandler();
					timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);
					if (timelineInfoList.size() <= 0) {
						Toast.makeText(LBSTimelineActivity.this,
								getString(R.string.no_more_tweets),
								Toast.LENGTH_LONG).show();
					} else {
						currentList = timelineInfoList;
						createListView(timelineInfoList);
					}
				}
			}
		}
	};
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Intent detail = new Intent(LBSTimelineActivity.this,
					DetailTweetActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("commtype", CommHandler.TYPE_GET_LBS_TIMELINE);
			bundle.putSerializable("timelineinfo",
					timeLineDataList.get(position));
			bundle.putSerializable("timelinedatalist", timeLineDataList);
			detail.putExtras(bundle);
			startActivity(detail);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLayoutResId(R.layout.timeline_layout);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		timeLineDataList = new ArrayList<TimeLineInfo>();
		currentList = new ArrayList<TimeLineInfo>();
		// head
		btnHeaderBack = (Button) findViewById(R.id.head_back);
		btnHeaderRefresh = (Button) findViewById(R.id.head_refresh);
		btnHeaderRefresh.setBackgroundResource(R.drawable.main_home);
		headName = (TextView) findViewById(R.id.head_Name);
		// bottom
		btnHome = (Button) findViewById(R.id.tools_bottom_home);
		btnNewTweet = (Button) findViewById(R.id.tools_bottom_new);
		btnDiscover = (Button) findViewById(R.id.tools_bottom_app);
		btnProfile = (Button) findViewById(R.id.tools_bottom_profile);
		btnMore = (Button) findViewById(R.id.tools_bottom_more);
		// right
		relativeRight = (RelativeLayout) findViewById(R.id.layout_main_right);
		relativeBottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);

		relativeRight.setVisibility(View.GONE);
		relativeBottom.setVisibility(View.GONE);
		// content
		listView = (ListView) findViewById(R.id.list_view);

		// Set Click Listener
		btnHome.setOnClickListener(this);
		btnNewTweet.setOnClickListener(this);
		btnDiscover.setOnClickListener(this);
		btnProfile.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		btnHeaderBack.setOnClickListener(this);
		btnHeaderRefresh.setOnClickListener(this);

		//
		cursor_id = "";

		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);
		listView.setDivider(null);
		// Prepare Simple Adapter For List View

		adapter = new SimpleAdapter(this, data,
				R.layout.sina_basic_timeline_layout_list_item, new String[] {
						"screenName", "status", "userImage", "time",
						"retweetedScreenNameStatus", "verified", "web",
						"webRetweet", "webStatus", "retweetStatus",
						"retweetCount", "commentCount", "moreTweets" },
				new int[] { R.id.screen_name, R.id.status, R.id.user_image,
						R.id.update_time, R.id.retweeted_screen_name_status,
						R.id.sina_user_verified, R.id.web_view_status,
						R.id.web_view_retweet_status, R.id.web_status,
						R.id.web_retweet_status, R.id.text_retweet_count,
						R.id.text_comment_count, R.id.text_get_more_tweets });

		listView.setAdapter(adapter);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		String imagePath = settingData.getWallpaper();
		imageShow = settingData.getSelectionShowImage();
		myImageBinder = new MyImageBinder(settingData.getFontColor(),
				settingData.getFontSize(), null, this);
		adapter.setViewBinder(myImageBinder);

		// Set Background
		loadBackGroundImage(imagePath);
		PackageManager pm = getPackageManager();
		initLBSView();

		if (pm != null
				&& statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				&& pm.checkPermission(permission.ACCESS_FINE_LOCATION,
						getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			startLocationService();
		}
		if (pm != null
				&& statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)
				&& pm.checkPermission(permission.ACCESS_FINE_LOCATION,
						getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			startLocationService();
		}

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// closeGps();
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
							Toast.makeText(LBSTimelineActivity.this,
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

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if (name.getShortClassName().equals(TimelineActivity.API_SERVICE_NAME)) {
			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
			showProgressDialog();
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER)) {
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_LBS_TIMELINE,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_refresh: {
			// Refresh
			Intent intent = new Intent(LBSTimelineActivity.this,
					HomeTimelineActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		// back
		case R.id.head_back: {
			finish();
			break;
		}
		default:

			break;
		}

	}

	private void createListView(ArrayList<TimeLineInfo> timelineInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		// Prepare ArrayList
		for (TimeLineInfo timelineInfo : timelineInfoList) {

			timeLineDataList.add(timelineInfo);

			Map<String, Object> map;
			map = new HashMap<String, Object>();
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER_PROXY)) {

				map.put("screenName", timelineInfo.getUserInfo()
						.getScreenName() == null ? "" : timelineInfo
						.getUserInfo().getUserName()
						+ " "
						+ "@"
						+ timelineInfo.getUserInfo().getScreenName());

			} else {
				map.put("screenName",
						timelineInfo.getUserInfo().getScreenName() == null ? ""
								: timelineInfo.getUserInfo().getScreenName()
										+ timelineInfo.getLocation());
			}

			String statusImages = timelineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			String status = TagAnalysis.clearImageUrls(
					timelineInfo.getStatus(), statusImages);
			map.put("status", status);
			map.put("webStatus", status);
			// map.put("status", timelineInfo.getStatus());
			// map.put("webStatus", timelineInfo.getStatus());
			if (imageShow.equals(BrowseModeActivity.select[0])
					|| imageShow.equals(BrowseModeActivity.select[1])) {
				// Use Download Image
				map.put("userImage", timelineInfo.getUserInfo()
						.getUserImageURL());
			} else {
				// Use Default Image
				map.put("userImage",
						String.valueOf(R.drawable.default_user_image));
			}

			if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
					.getCurrentService())) {
				map.put("pageTime", timelineInfo.getTimeStamp());
				map.put("retweetCount", getString(R.string.retweet_count) + "("
						+ timelineInfo.getRetweetCount() + ")");
				map.put("commentCount", getString(R.string.comment_count) + "("
						+ timelineInfo.getCommentCount() + ")");
			}

			// if (IGeneral.SERVICE_NAME_SINA.equals(statusData
			// .getCurrentService())
			// || IGeneral.SERVICE_NAME_SOHU.equals(statusData
			// .getCurrentService())) {
			// map.put("retweetCount", "");
			// map.put("commentCount", "");
			// }

			if (imageShow.equals(BrowseModeActivity.select[0])) {
				String str = timelineInfo.getImageInformationForWebView(this,
						TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
				String strRet = timelineInfo.getImageInformationForWebView(
						this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET);
				if (str.contains("/files/")) {
					str = "http://www.iconpng.com/png/fatcow/page_attach.png";
				}
				if (strRet.contains("/files/")) {
					strRet = "http://www.iconpng.com/png/fatcow/page_attach.png";
				}
				map.put("web", str);
				map.put("webRetweet", strRet);

			} else {
				map.put("web", "");
				map.put("webRetweet", "");
			}

			map.put("time", timelineInfo.getFormatTime(
					statusData.getCurrentService(), this));
			addDatas.add(map);

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();

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

	public void deleteItemForMoreTweets() {

		int size = data.size();
		for (int i = size - 1; i >= 0; i--) {
			if (data.get(i).get("screenName").equals("")) {
				data.remove(i);
				adapter.notifyDataSetChanged();
				break;
			}
		}

	}

	private void getLBSTimeline() {

		if (data.isEmpty()) {
			showProgressDialog();
			// Location
			if (location != null) {
				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)) {
					parameters.put("latitude", String.valueOf(initLatitude));
					parameters.put("longitude", String.valueOf(initLongitude));
				}
				// 添加的网易部分
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
					parameters.put("lat", String.valueOf(initLatitude));
					parameters.put("long", String.valueOf(initLongitude));
					parameters.put("since_id", cursor_id);
				}
				// Request (get service from status information)
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_LBS_TIMELINE,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (location == null) {
				// Pop up message (could not obtain Location Info)
				locationHandler.sendEmptyMessage(OBTAIN_LOCATION_FAILED);
			}

		}
	}

	private void initLBSView() {
		headName.setText(getString(R.string.discovery_lbs_timeline));

	}

	@Override
	protected void refreshByMenu() {

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
