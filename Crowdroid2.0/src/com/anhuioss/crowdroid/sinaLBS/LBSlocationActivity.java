package com.anhuioss.crowdroid.sinaLBS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.DetailTweetActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.MoreFunctionActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.communication.DownloadServiceInterface;
import com.anhuioss.crowdroid.communication.TranslationService;
import com.anhuioss.crowdroid.communication.TranslationServiceInterface;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.BasicInfo;
import com.anhuioss.crowdroid.data.info.POIinfo;
import com.anhuioss.crowdroid.data.info.RoadInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.transfersInfo;
import com.anhuioss.crowdroid.dialog.AtUserSelectDialog;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.RoadDialog;
import com.anhuioss.crowdroid.map.BasicMenuActivity;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.sns.operations.DetailBlogActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.BaiduContext;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKGeocoderAddressComponent;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class LBSlocationActivity extends MapActivity implements
		ServiceConnection, OnClickListener {

	public static final String API_SERVICE_NAME = ".communication.ApiService";
	String xnext_1 = "";
	String xnext_2 = "";
	String num = "";
	private TabHost tabHost;
	private GeoPoint m_locPoint;
	private Location location;
	Button l1_prev = null;
	Button l1_next = null;
	Button l1_xnext = null;
	Button l2_prev = null;
	Button l2_next = null;
	Button l2_xnext = null;
	Button l3_prev = null;
	Button l3_next = null;
	Button l3_xnext = null;
	ListView list_location = null;
	ListView list_userlocation = null;
	LocationListener mLocationListener = null;
	/** Location Manager(for GPS) */
	private LocationManager locationManager;

	private Timer locationTimer;

	private long time;

	protected static final int OBTAIN_LOCATION_FAILED = 0;

	protected static final int OBTAIN_LOCATION_SUCESSED = 1;

	android.location.LocationListener locationListener;

	ListView list_searchlocation = null;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	protected ArrayList<POIinfo> POIDataList;

	public static ArrayList<RoadInfo> RoadDataList;

	public static ArrayList<transfersInfo> transfersDataList;

	private SimpleAdapter adapter;

	private MKSearch mMKSearch;

	MKLocationManager mLocationManager;

	private MyImageBinder myImageBinder;

	private double lat = 39.98437;

	private double lon = 116.30987;

	private String coordinate = "";

	private int currentPage = 1;

	private StatusData statusData;

	private AccountData accountData;

	SettingData settingData;

	private String service;

	private CrowdroidApplication crowdroidApplication;

	boolean refreshBack = false;

	boolean autoRefreshFlag = false;

	private String imageShow;

	private int commType = 93;

	private static boolean isRunning = true;

	BMapManager mBMapMan = null;

	MapView map = null;

	private ImageButton search = null;

	private EditText search_text = null;

	// private Button search_type = null;

	private Spinner search_type = null;

	// head
	private Button headerBack = null;

	private Button headerRefresh = null;

	private TextView headName = null;

	// bottom
	private Button btnHome = null;

	private Button btnNewTweet = null;

	private Button btnDiscover = null;

	private Button btnProfile = null;

	private Button btnMore = null;

	private String type = "";

	private String city = "";

	String mStrKey = "dpZnIOdS0nP6fihX1c5vUuNN";

	private HandleProgressDialog progress;

	private ApiServiceInterface apiServiceInterface;

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// RenRen
			// if (statusData.getCurrentService().equals(
			// IGeneral.SERVICE_NAME_SINA)) {
			if (commType == CommHandler.TYPE_LBS_GET_SEARCH_POI
					|| commType == CommHandler.TYPE_LBS_GET_WAY_CAR
					|| commType == CommHandler.TYPE_LBS_GET_WAY_BUS) {
				if (city.equals(POIDataList.get(position).getcity() + "市")) {
					Log.v("city", city);
					openwaymanager();
					coordinate = POIDataList.get(position).getcoordinate();
				} else {
					Toast.makeText(LBSlocationActivity.this, "该地点不在当前城市内",
							Toast.LENGTH_SHORT).show();
				}
				// refresh();

			} else {
				Intent detail = new Intent(LBSlocationActivity.this,
						LBSdetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("commtype", commType);
				bundle.putSerializable("poisinfo", POIDataList.get(position));
				bundle.putSerializable("poisdatalist", POIDataList);
				bundle.putFloat("lat", (float) lat);
				bundle.putFloat("long", (float) lon);
				detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				detail.putExtras(bundle);
				startActivity(detail);
			}

			// } else if (commType == CommHandler.TYPE_GET_BLOG_TIMELINE) {
			// Intent detail = new Intent(TimelineActivity.this,
			// DetailBlogActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putSerializable("timelineinfo",
			// POIDataList.get(position));
			// detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// detail.putExtras(bundle);
			// startActivity(detail);
			// } else if (commType ==
			// CommHandler.TYPE_GET_RETWEET_OF_ME_TIME_LINE) {
			// Intent intent = new Intent(TimelineActivity.this,
			// ProfileActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putString("name", timeLineDataList.get(position)
			// .getUserInfo().getScreenName());
			// bundle.putString("uid", timeLineDataList.get(position)
			// .getUserInfo().getUid());
			// bundle.putString("user_name",
			// timeLineDataList.get(position).getUserInfo()
			// .getUserName());
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtras(bundle);
			// startActivity(intent);
			// } else {
			// Intent detail = new Intent(TimelineActivity.this,
			// DetailTweetActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putInt("commtype", commType);
			// bundle.putSerializable("timelineinfo",
			// timeLineDataList.get(position));
			// bundle.putSerializable("timelinedatalist",
			// timeLineDataList);
			// // 人人传递头像以及姓名
			// if (commType == CommHandler.TYPE_GET_STATUS_TIMELINE) {
			// bundle.putString("name", userName);
			// bundle.putString("headUrl", headUrl);
			// }
			// detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// detail.putExtras(bundle);
			// startActivity(detail);
			// }
			// }
			//

		}
	};

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {
				JSONObject status;
				String er_code = null;
				try {
					status = new JSONObject(message);
					er_code = status.getString("error_code");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if ("21903".equals(er_code)) {
					Toast.makeText(LBSlocationActivity.this,
							getString(R.string.msgnull), Toast.LENGTH_SHORT)
							.show();
				}

				l1_xnext.setText(xnext_1 + String.valueOf(currentPage)
						+ xnext_2);
				l2_xnext.setText(xnext_1 + String.valueOf(currentPage)
						+ xnext_2);
				l3_xnext.setText(xnext_1 + String.valueOf(currentPage)
						+ xnext_2);

				if (currentPage < 2) {
					l1_prev.setEnabled(false);
					l2_prev.setEnabled(false);
					l3_prev.setEnabled(false);
				} else {
					l1_prev.setEnabled(true);
					l2_prev.setEnabled(true);
					l3_prev.setEnabled(true);
				}
				// Parser
				ArrayList<POIinfo> POIList = new ArrayList<POIinfo>();
				ArrayList<RoadInfo> roadList = new ArrayList<RoadInfo>();
				ArrayList<transfersInfo> transfersList = new ArrayList<transfersInfo>();
				ParseHandler parseHandler = new ParseHandler();

				if (commType == CommHandler.TYPE_LBS_GET_WAY_CAR) {
					roadList = (ArrayList<RoadInfo>) parseHandler.parser(
							service, type, statusCode, message);
				} else if (commType == CommHandler.TYPE_LBS_GET_WAY_BUS) {
					transfersList = (ArrayList<transfersInfo>) parseHandler
							.parser(service, type, statusCode, message);
				} else {
					POIList = (ArrayList<POIinfo>) parseHandler.parser(service,
							type, statusCode, message);
				}
				if (transfersList != null && transfersList.size() > 0) {
					// transfersDataList.clear();
					transfersDataList = transfersList;
					RoadDialog usd = null;
					usd = new RoadDialog(LBSlocationActivity.this, type);

					usd.setTitle("@" + getString(R.string.insert_at_name));
					usd.show();
				}

				if (roadList != null && roadList.size() > 0) {
					// RoadDataList.clear();
					RoadDataList = roadList;
					RoadDialog usd = null;
					usd = new RoadDialog(LBSlocationActivity.this, type);

					usd.setTitle("@" + getString(R.string.insert_at_name));
					usd.show();
				}

				if (POIList.size() < 10) {
					l1_next.setEnabled(false);
					l2_next.setEnabled(false);
					l3_next.setEnabled(false);
				}

				if (POIList != null && POIList.size() > 0) {

					// statusFilter(timelineInfoList);
					// if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// if (commType ==
					// CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE
					// || commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND
					// || commType == CommHandler.TYPE_GET_AT_MESSAGE
					// || commType == CommHandler.TYPE_GET_MY_TIME_LINE) {
					// if (currentPage == 1 && !isRefreshFromAutoRefresh) {
					// setNewestMessageId(type, timelineInfoList);
					// }
					// }
					// } else {
					// if (currentPage == 1 && !isRefreshFromAutoRefresh) {
					// setNewestMessageId(type, timelineInfoList);
					// }
					// }
					//
					// isRefreshFromAutoRefresh = false;
					//
					// currentList = timelineInfoList;
					POIDataList = POIList;
					createListView(POIList);
					//
					// if (timelineInfoList.size() >= 20
					// && !(service.equals(IGeneral.SERVICE_NAME_WANGYI)
					// && (commType ==
					// CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE) || commType ==
					// CommHandler.TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE)) {
					// addItemForMoreTweets();
					// }
					// if (timelineInfoList.size() >= 19
					// && service.equals(IGeneral.SERVICE_NAME_TWITTER)
					// && commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
					// addItemForMoreTweets();
					// }
					// if (!timelineInfoList.get(timelineInfoList.size() - 1)
					// .equals("")) {
					// cursor_id = timelineInfoList.get(
					// timelineInfoList.size() - 1).getcursor_id();
					// if (commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE
					// || commType == CommHandler.TYPE_GET_USER_COLUMN_TIME_LINE
					// || service
					// .equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// cursor_id = timelineInfoList.get(
					// timelineInfoList.size() - 1).getMessageId();
					// }
					// }
					//
					// // if (imageShow.equals(SettingsActivity.select[0])
					// // || imageShow.equals(SettingsActivity.select[1])) {
					// // // Download Images
					// // downloadImage(timelineInfoList);
					// //
					// // } else {
					// // // Undownload Image
					// // createListView(timelineInfoList);
					// // }
					//
					// } else if ("{}".equals(message)) {
					// Toast.makeText(TimelineActivity.this,
					// getString(R.string.permission), Toast.LENGTH_SHORT)
					// .show();
					// } else if (timelineInfoList == null
					// || timelineInfoList.size() == 0) {
					// Toast.makeText(TimelineActivity.this,
					// getString(R.string.msgnull), Toast.LENGTH_SHORT)
					// .show();
				}
			}
			if ("[]".equals(message) || "null".equals(message)) {
				Toast.makeText(LBSlocationActivity.this,
						getString(R.string.msgnull), Toast.LENGTH_SHORT).show();
			}
			if (!"200".equals(statusCode)) {
				// if ("401".equals(statusCode)
				// && commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
				// Toast.makeText(LBSlocationActivity.this,
				// getResources().getString(R.string.new_token),
				// Toast.LENGTH_SHORT).show();
				// } else {
				Toast.makeText(
						LBSlocationActivity.this,
						ErrorMessage.getErrorMessage(LBSlocationActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
				// }
			}
			if ("21903".equals(statusCode)) {
				Toast.makeText(LBSlocationActivity.this,
						getString(R.string.msgnull), Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sina_lbs_location);

		xnext_2 = getResources().getString(R.string.xnext_2);
		xnext_1 = getResources().getString(R.string.xnext_1);
		num = getResources().getString(R.string.location_user_num);

		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(R.string.discovery_lbs_name);
		// headerRefresh.setBackgroundResource(R.drawable.main_home);

		// bottom
		btnHome = (Button) findViewById(R.id.tools_bottom_home);
		btnNewTweet = (Button) findViewById(R.id.tools_bottom_new);
		btnDiscover = (Button) findViewById(R.id.tools_bottom_app);
		btnProfile = (Button) findViewById(R.id.tools_bottom_profile);
		btnMore = (Button) findViewById(R.id.tools_bottom_more);

		btnHome.setOnClickListener(this);
		btnNewTweet.setOnClickListener(this);
		btnDiscover.setOnClickListener(this);
		btnProfile.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);

		try {
			RelativeLayout articleTab = (RelativeLayout) LayoutInflater.from(
					this).inflate(R.layout.style_tabhost, null);
			TextView articleTabLabel = (TextView) articleTab
					.findViewById(R.id.tab_label);
			articleTabLabel.setText(getResources().getString(
					R.string.now_location));

			RelativeLayout feedTab = (RelativeLayout) LayoutInflater.from(this)
					.inflate(R.layout.style_tabhost, null);
			TextView feedTabLabel = (TextView) feedTab
					.findViewById(R.id.tab_label);
			feedTabLabel.setText(getResources().getString(
					R.string.history_location));

			RelativeLayout bookTab = (RelativeLayout) LayoutInflater.from(this)
					.inflate(R.layout.style_tabhost, null);
			TextView bookTabLabel = (TextView) bookTab
					.findViewById(R.id.tab_label);
			bookTabLabel.setText(getResources().getString(
					R.string.location_albility));

			tabHost = (TabHost) this.findViewById(R.id.TabHost01);
			tabHost.setup();

			tabHost.addTab(tabHost.newTabSpec("tab_1").setIndicator(articleTab)
					.setContent(R.id.LinearLayout1));
			tabHost.addTab(tabHost.newTabSpec("tab_2").setIndicator(feedTab)
					.setContent(R.id.LinearLayout2));
			tabHost.addTab(tabHost.newTabSpec("tab_3").setIndicator(bookTab)
					.setContent(R.id.LinearLayout3));

			// tabHost = (TabHost) this.findViewById(R.id.TabHost01);
			// tabHost.setup();
			//
			// tabHost.addTab(tabHost.newTabSpec("tab_1")
			// .setContent(R.id.LinearLayout1)
			// .setIndicator(this.getResources().getString(R.string.now_location),
			// null));
			// tabHost.addTab(tabHost.newTabSpec("tab_2")
			// .setContent(R.id.LinearLayout2)
			// .setIndicator(this.getResources().getString(R.string.history_location),
			// null));
			// tabHost.addTab(tabHost.newTabSpec("tab_3")
			// .setContent(R.id.LinearLayout3)
			// .setIndicator(this.getResources().getString(R.string.location_albility),
			// null));

			tabHost.setCurrentTab(0);

		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d("EXCEPTION", ex.getMessage());
		}

		l1_prev = (Button) findViewById(R.id.location_prev1);
		l1_next = (Button) findViewById(R.id.location_next1);
		l1_xnext = (Button) findViewById(R.id.location_xnext1);
		l2_prev = (Button) findViewById(R.id.location_prev2);
		l2_next = (Button) findViewById(R.id.location_next2);
		l2_xnext = (Button) findViewById(R.id.location_xnext2);
		l3_prev = (Button) findViewById(R.id.location_prev3);
		l3_next = (Button) findViewById(R.id.location_next3);
		l3_xnext = (Button) findViewById(R.id.location_xnext3);
		l1_prev.setOnClickListener(this);
		l1_prev.setEnabled(false);
		l2_prev.setEnabled(false);
		l3_prev.setEnabled(false);
		l1_next.setOnClickListener(this);
		l1_xnext.setOnClickListener(this);
		l2_prev.setOnClickListener(this);
		l2_next.setOnClickListener(this);
		l2_xnext.setOnClickListener(this);
		l3_prev.setOnClickListener(this);
		l3_next.setOnClickListener(this);
		l3_xnext.setOnClickListener(this);
		list_location = (ListView) findViewById(R.id.list01);
		list_userlocation = (ListView) findViewById(R.id.list02);
		list_searchlocation = (ListView) findViewById(R.id.list03);

		search = (ImageButton) findViewById(R.id.location_search);
		search_text = (EditText) findViewById(R.id.location_search_text);
		// search_type = (Button) findViewById(R.id.location_search_type);
		search_type = (Spinner) findViewById(R.id.location_search_type);

		ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter
				.createFromResource(this, R.array.sina_location_search_type,
						android.R.layout.simple_spinner_item);
		adapterspinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		search_type.setAdapter(adapterspinner);
		search_type.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				changetype(position);
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		list_location.setClickable(true);
		list_userlocation.setOnItemClickListener(onItemClickListener);
		list_location.setOnItemClickListener(onItemClickListener);
		list_searchlocation.setOnItemClickListener(onItemClickListener);
		search.setOnClickListener(this);
		// search_type.setOnClickListener(this);
		list_location.setDivider(null);
		map = (MapView) findViewById(R.id.bmapsView);

		// mBMapMan = new BMapManager(getApplication());
		// mBMapMan.init(mStrKey, null);
		// super.initMapActivity(mBMapMan);

		// mBMapMan = new BMapManager(getApplication());
		// mBMapMan.init("92E3C16084E6DEDE58BF03D3F1135DE8B63B537C", null);
		// super.initMapActivity(mBMapMan);
		// mBMapMan.start();
		// mLocationManager = mBMapMan.getLocationManager();
		// mMKSearch = new MKSearch();
		// mMKSearch.init(mBMapMan, new MySearchListener());
		// // mMKSearch.reverseGeocode(new GeoPoint(31701427, 118500345));
		//
		// mLocationListener = new LocationListener() {
		//
		// @Override
		// public void onLocationChanged(Location location) {
		// if (location != null) {
		// // pd.dismiss();
		// m_locPoint = new GeoPoint(
		// (int) (location.getLatitude() * 1E6),
		// (int) (location.getLongitude() * 1E6));
		//
		// lat = location.getLatitude();
		// lon = location.getLongitude();
		// Log.v("****","*******************************************");
		// refresh();
		// Log.v("****","*******************************************");
		// // mMKSearch.reverseGeocode(new GeoPoint((int) (location
		// // .getLatitude() * 1e6), (int) (location
		// // .getLongitude() * 1e6)));
		// mMKSearch.reverseGeocode(m_locPoint); // 逆地址解析
		//
		// }
		// }
		// };
		// mLocationManager.requestLocationUpdates(mLocationListener);

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		service = statusData.getCurrentService();

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("tab_1")) {
					currentPage = 1;
					l1_xnext.setText(xnext_1 + "1" + xnext_2);
					l2_xnext.setText(xnext_1 + "1" + xnext_2);
					l3_xnext.setText(xnext_1 + "1" + xnext_2);
					commType = 93;
					data.clear();
					refresh();
				}
				if (tabId.equals("tab_2")) {
					l1_xnext.setText(xnext_1 + "1" + xnext_2);
					l2_xnext.setText(xnext_1 + "1" + xnext_2);
					l3_xnext.setText(xnext_1 + "1" + xnext_2);
					currentPage = 1;
					commType = CommHandler.TYPE_LBS_GET_USER_POI;
					data.clear();
					refresh();
				}
				if (tabId.equals("tab_3")) {
					l1_xnext.setText(xnext_1 + "1" + xnext_2);
					l2_xnext.setText(xnext_1 + "1" + xnext_2);
					l3_xnext.setText(xnext_1 + "1" + xnext_2);
					currentPage = 1;
					data.clear();
					// refresh();
				}
			}
		});

		// adapter = new SimpleAdapter(this, data,
		// R.layout.pois_item, new String[] {
		// "title", "address", "Image",
		// "num"}, new int[] {
		// R.id.sina_pois_name, R.id.sina_pois_des, R.id.sina_pois_image,
		// R.id.sina_pois_num});
		// list_location.setAdapter(adapter);
		adapter = new SimpleAdapter(this, data,
				R.layout.sina_timeline_layout_list_item, new String[] {
						"title", "address", "address", "retweetStatus", "icon",
						"time", "retweetedScreenNameStatus", "verified", "web",
						"webRetweet", "important_level", "retweetCount", "num",
						"moreTweets" }, new int[] { R.id.sina_screen_name,
						R.id.sina_status, R.id.web_status,
						R.id.web_retweet_status, R.id.sina_user_image,
						R.id.sina_update_time,
						R.id.retweeted_screen_name_status,
						R.id.sina_user_verified, R.id.web_view_status,
						R.id.web_view_retweet_status,
						R.id.important_level_view, R.id.text_retweet_count,
						R.id.text_comment_count, R.id.text_get_more_tweets });
		// adapter = new SimpleAdapter(this, data,
		// R.layout.pois_item, new String[] {
		// "icon",
		// "title", "address",
		// "num"}, new int[] {
		// R.id.sina_pois_image,
		// R.id.sina_pois_name, R.id.sina_pois_des,
		// R.id.sina_pois_num});
		list_location.setAdapter(adapter);
		list_userlocation.setAdapter(adapter);
		list_searchlocation.setAdapter(adapter);
	}

	protected void createListView(ArrayList<POIinfo> pOIList) {
		// TODO Auto-generated method stub
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		for (POIinfo POIInfo : POIDataList) {

			Map<String, Object> map;
			map = new HashMap<String, Object>();

			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				map.put("title", POIInfo.gettitle());
				map.put("address", POIInfo.getaddress());
				// map.put("icon",
				// "<html><img width=50dx height=50px src='"+POIInfo.geticon()+"' /><html>");
				map.put("icon", POIInfo.geticon());
				map.put("num", num + "(" + POIInfo.getcheckin_user_num() + ")");
			}
			addDatas.add(map);

		}
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tools_bottom_home: {
			if (commType != CommHandler.TYPE_GET_HOME_TIMELINE) {
				Intent home = new Intent(LBSlocationActivity.this,
						HomeTimelineActivity.class);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(home);
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
			break;
		}
		case R.id.tools_bottom_new: {
			Intent tweet = new Intent(LBSlocationActivity.this,
					SendMessageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("action", "timeline");
			bundle.putString("target", "");
			tweet.putExtras(bundle);
			startActivity(tweet);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_app: {
			Intent app = null;
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				app = new Intent(LBSlocationActivity.this,
						SNSDiscoveryActivity.class);
			} else {
				app = new Intent(LBSlocationActivity.this,
						DiscoveryActivity.class);
			}
			app.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(app);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_profile: {
			Intent intent = new Intent(LBSlocationActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", "");
			bundle.putString("user_name", "");
			bundle.putString("uid", "");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_more: {
			Intent more = new Intent(LBSlocationActivity.this,
					MoreFunctionActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", accountData.getUserScreenName());
			more.putExtras(bundle);
			more.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(more);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.head_refresh: {

			// Clear Data
			data.clear();

			adapter.notifyDataSetChanged();
			// Set Page
			currentPage = 0;
			// Cancel Notification
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(1);

			// Refresh
			refresh();
			break;
		}
		case R.id.head_back: {
			// if (commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
			// confirmLogoutDialog();
			// } else {
			finish();
			// }
			break;
		}
		case R.id.location_prev1: {
			data.clear();
			currentPage--;
			refresh();
			break;
		}
		case R.id.location_next1: {
			data.clear();
			currentPage++;
			refresh();
			break;
		}
		case R.id.location_xnext1: {
			data.clear();
			openpagemanager();
			break;
		}
		case R.id.location_prev2: {
			data.clear();
			currentPage--;
			refresh();
			break;
		}
		case R.id.location_next2: {
			data.clear();
			currentPage++;
			refresh();
			break;
		}
		case R.id.location_xnext2: {
			data.clear();
			openpagemanager();
			break;
		}
		case R.id.location_prev3: {
			data.clear();
			currentPage--;
			refresh();
			break;
		}
		case R.id.location_next3: {
			data.clear();
			currentPage++;
			refresh();
			break;
		}
		case R.id.location_xnext3: {
			data.clear();
			openpagemanager();
			break;
		}
		case R.id.location_search: {
			tabHost.setCurrentTab(2);
			data.clear();
			refresh();
			break;
		}
		case R.id.location_search_type: {
			// openrangemanager();
			commType = CommHandler.TYPE_LBS_GET_SEARCH_POI;
			// opentypemanager("全国");
			break;
		}
		}
	}

	@Override
	public void onResume() {
		// BaiduContext.getInstance().getMapManager().getLocationManager()
		// .requestLocationUpdates(mLocationListener);
		// BaiduContext.getInstance().getMapManager().start();
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag=false;
		// pd.show();
	}

	/** Stop the updates when Activity is paused */
	@Override
	public void onPause() {
		// BaiduContext.getInstance().getMapManager().getLocationManager()
		// .removeUpdates(mLocationListener);
		// BaiduContext.getInstance().getMapManager().stop();
		if (mLocationListener != null) {
			CrowdroidApplication.getInstance().getMapManager().stop();
			CrowdroidApplication.getInstance().getMapManager()
					.getLocationManager().removeUpdates(mLocationListener);
			data.clear();
		}
		super.onPause();
		if (progress != null) {
			progress.dismiss();
		}
		TimelineActivity.isBackgroundNotificationFlag=true;

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
		unbindService(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		// isRunning = true;

		initLocationNetWork();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		autoRefreshFlag = settingData.isAutoRefresh();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		imageShow = settingData.getSelectionShowImage();

		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);

		// Bind Api Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		// Bind Service
		Intent intentTranslation = new Intent(this, TranslationService.class);
		bindService(intentTranslation, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		if (name.getShortClassName().equals(API_SERVICE_NAME)) {

			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

			// Cancel Notification
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(1);

			if (((CrowdroidApplication) getApplicationContext())
					.isComeFromNotification(0)) {
				data.clear();
				POIDataList.clear();
				adapter.notifyDataSetChanged();
			}

			// if (data.isEmpty()) {
			// try {
			//
			// // isRefreshFromAutoRefresh = false;
			// //
			// // lastRefreshTime = System.currentTimeMillis();
			//
			// // listView.setClickable(false);
			// // setProgressBarIndeterminateVisibility(true);
			// showProgressDialog();
			//
			// // Prepare Parameters
			// Map<String, Object> parameters;
			// parameters = new HashMap<String, Object>();
			// parameters.put("page", currentPage);
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_WANGYI)
			// // || statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TWITTER)) {
			// // // parameters.put("since_id", cursor_id);
			// // }
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_WANGYI)
			// // && commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE) {
			// // Bundle bundle = this.getIntent().getExtras();
			// // column_id = bundle.getString("id");
			// //
			// // parameters.put("column_id", column_id);
			// // }
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_WANGYI)
			// // && commType ==
			// // CommHandler.TYPE_GET_USER_COLUMN_TIME_LINE) {
			// // Bundle bundle = this.getIntent().getExtras();
			// // column_id = bundle.getString("uid");
			// //
			// // parameters.put("user_id", column_id);
			// // }
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_WANGYI)
			// // && commType == CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE)
			// // {
			// // Bundle bundle = this.getIntent().getExtras();
			// // parameters.put("type", bundle.getString("type"));
			// // }
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_RENREN)
			// // && (commType == CommHandler.TYPE_GET_STATUS_TIMELINE ||
			// // commType == CommHandler.TYPE_GET_BLOG_TIMELINE)) {
			// //
			// // Bundle bundle = this.getIntent().getExtras();
			// // uid = bundle.getString("uid");
			// // headUrl = bundle.getString("head_url");
			// // userName = bundle.getString("name");
			// // parameters.put("id", uid);
			// // }
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TENCENT)
			// // && commType == CommHandler.TYPE_GET_AREA_TIMELINE) {
			// // // Tencent area timeline
			// // parameters.put("stateCode", getIntent().getExtras()
			// // .get("stateCode"));
			// // parameters.put("cityCode",
			// // getIntent().getExtras().get("cityCode"));
			// // }
			// // parameters.put("uid", statusData.getCurrentUid());
			// // if (accountData != null) {
			// // parameters.put("screen_name",
			// // accountData.getUserScreenName());
			// // }
			// // Request
			// // parameters.put("lat ", m_locPoint.getLatitudeE6());
			// // parameters.put("long ", m_locPoint.getLongitudeE6());
			// parameters.put("uid", statusData.getCurrentUid());
			// parameters.put("lat", 31.701426873407925);
			// parameters.put("long", 118.5003451745606);
			// apiServiceInterface.request(statusData.getCurrentService(),
			// commType, apiServiceListener, parameters);
			// } catch (RemoteException e) {
			// e.printStackTrace();
			// }
			// }

			// if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
			// .getCurrentService())) {
			// // Prepare Parameters
			// Map<String, Object> parameter;
			// parameter = new HashMap<String, Object>();
			// switch (commType) {
			// case CommHandler.TYPE_GET_HOME_TIMELINE: {
			// parameter.put("type", "5");
			// TencentCommHandler.clearUnreadMessage(parameter);
			// break;
			// }
			// case CommHandler.TYPE_GET_AT_MESSAGE: {
			// parameter.put("type", "6");
			// TencentCommHandler.clearUnreadMessage(parameter);
			// break;
			// }
			// case CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE: {
			// parameter.put("type", "7");
			// TencentCommHandler.clearUnreadMessage(parameter);
			// break;
			// }
			// default: {
			// // Something wrong.
			// }
			// }
			//
			// }

		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;

	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(LBSlocationActivity.this);
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

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void refresh() {
		try {

			showProgressDialog();

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("page", currentPage);
			if (currentPage < 2) {
				l1_prev.setEnabled(false);
				l2_prev.setEnabled(false);
				l3_prev.setEnabled(false);
			} else {
				l1_prev.setEnabled(true);
				l2_prev.setEnabled(true);
				l3_prev.setEnabled(true);
			}

			if (tabHost.getCurrentTab() == 2) {
				parameters.put("q", search_text.getText().toString());
				parameters.put("category", type);
				parameters.put("begin_coordinate", String.valueOf(lon) + ","
						+ String.valueOf(lat));
				parameters.put("end_coordinate", coordinate);
				// parameters.put("coordinate", coordinate);
			}

			parameters.put("uid", statusData.getCurrentUid());
			parameters.put("lat", lat);
			parameters.put("long", lon);
			apiServiceInterface.request(statusData.getCurrentService(),
					commType, apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void openrangemanager() {
		final CharSequence[] op = getResources().getStringArray(
				R.array.sina_location_search_range);
		AlertDialog dialog = new AlertDialog.Builder(LBSlocationActivity.this)
				.setItems(op, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0: {
							commType = CommHandler.TYPE_LBS_GET_SEARCH_POI;
							opentypemanager("全国");
							break;
						}
						case 1: {
							commType = CommHandler.TYPE_LBS_GET_SEARCH_POI_BY_GEO;
							opentypemanager("当地");
							break;
						}
						case 2: {
							// search_type.setText("公交路线");
							break;
						}
						}
					}
				}).create();
		dialog.show();

	}

	private void opentypemanager(final String location) {
		final CharSequence[] op = getResources().getStringArray(
				R.array.sina_location_search_type);
		AlertDialog dialog = new AlertDialog.Builder(LBSlocationActivity.this)
				.setItems(op, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// search_type.setText(op[which]);
						switch (which) {
						case 0: {
							type = "010000";
							dialog.dismiss();
							break;
						}
						case 1: {
							type = "020000";
							dialog.dismiss();
							break;
						}
						case 2: {
							type = "030000";
							dialog.dismiss();
							break;
						}
						case 3: {
							type = "040000";
							dialog.dismiss();
							break;
						}
						case 4: {
							type = "050000";
							dialog.dismiss();
							break;
						}
						case 5: {
							type = "060000";
							dialog.dismiss();
							break;
						}
						case 6: {
							type = "070000";
							dialog.dismiss();
							break;
						}

						case 7: {
							type = "080000";
							dialog.dismiss();
							break;
						}
						case 8: {
							type = "090000";
							dialog.dismiss();
							break;
						}
						case 9: {
							type = "100000";
							dialog.dismiss();
							break;
						}
						case 10: {
							type = "110000";
							dialog.dismiss();
							break;
						}
						case 11: {
							type = "120000";
							dialog.dismiss();
							break;
						}
						case 12: {
							type = "130000";
							dialog.dismiss();
							break;
						}
						case 13: {
							type = "140000";
							dialog.dismiss();
							break;
						}
						case 14: {
							type = "150000";
							dialog.dismiss();
							break;
						}
						case 15: {
							type = "160000";
							dialog.dismiss();
							break;
						}
						case 16: {
							type = "170000";
							dialog.dismiss();
							break;
						}
						case 17: {
							type = "180000";
							dialog.dismiss();
							break;
						}
						case 18: {
							type = "190000";
							dialog.dismiss();
							break;
						}
						case 19: {
							type = "200000";
							dialog.dismiss();
							break;
						}
						}
					}
				}).create();
		dialog.show();

	}

	private void openwaymanager() {
		final CharSequence[] op = getResources().getStringArray(
				R.array.sina_location_search_way);
		AlertDialog dialog = new AlertDialog.Builder(LBSlocationActivity.this)
				.setItems(op, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0: {
							commType = CommHandler.TYPE_LBS_GET_WAY_CAR;
							refresh();
							break;
						}
						case 1: {
							commType = CommHandler.TYPE_LBS_GET_WAY_BUS;
							refresh();
							break;
						}
						}
					}
				}).create();
		dialog.show();
	}

	private void WayDialog() {
		final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(R.string.logout);
		dlg.setMessage(getResources().getString(R.string.wheter_to_logout))
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((DialogInterface) dlg).dismiss();

							}
						}).create().show();

	}

	private void openpagemanager() {
		final CharSequence[] op = getResources().getStringArray(
				R.array.sina_location_page);

		AlertDialog dialog = new AlertDialog.Builder(LBSlocationActivity.this)
				.setItems(op, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// l1_xnext.setText(xnext_1 + String.valueOf(which + 1)
						// + xnext_2);
						// l2_xnext.setText(xnext_1 + String.valueOf(which + 1)
						// + xnext_2);
						// l3_xnext.setText(xnext_1 + String.valueOf(which + 1)
						// + xnext_2);
						switch (which) {
						case 0: {

							currentPage = 1;
							refresh();
							dialog.dismiss();
							break;
						}
						case 1: {

							currentPage = 2;
							refresh();
							dialog.dismiss();
							break;
						}
						case 2: {

							currentPage = 3;
							dialog.dismiss();
							break;
						}
						case 3: {

							currentPage = 4;
							refresh();
							dialog.dismiss();
							break;
						}
						case 4: {
							currentPage = 5;
							refresh();
							dialog.dismiss();
							break;
						}
						case 5: {
							currentPage = 6;
							refresh();
							dialog.dismiss();
							break;
						}
						case 6: {
							currentPage = 7;
							refresh();
							dialog.dismiss();
							break;
						}

						case 7: {
							currentPage = 8;
							refresh();
							dialog.dismiss();
							break;
						}
						case 8: {
							currentPage = 9;
							refresh();
							dialog.dismiss();
							break;
						}
						case 9: {
							currentPage = 10;
							refresh();
							dialog.dismiss();
							break;
						}
						case 10: {
							currentPage = 11;
							refresh();
							dialog.dismiss();
							break;
						}
						case 11: {
							currentPage = 12;
							refresh();
							dialog.dismiss();
							break;
						}
						case 12: {
							currentPage = 13;
							refresh();
							dialog.dismiss();
							break;
						}
						case 13: {
							currentPage = 14;
							refresh();
							dialog.dismiss();
							break;
						}
						case 14: {
							currentPage = 15;
							refresh();
							dialog.dismiss();
							break;
						}
						case 15: {
							currentPage = 16;
							refresh();
							dialog.dismiss();
							break;
						}
						case 16: {
							currentPage = 17;
							refresh();
							dialog.dismiss();
							break;
						}
						case 17: {
							currentPage = 18;
							refresh();
							dialog.dismiss();
							break;
						}
						case 18: {
							currentPage = 19;
							refresh();
							dialog.dismiss();
							break;
						}
						case 19: {
							currentPage = 20;
							refresh();
							dialog.dismiss();
							break;
						}
						}
					}
				}).create();
		dialog.show();
	}

	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			if (iError == 0) {
				MKGeocoderAddressComponent kk = result.addressComponents;
				city = kk.city;
			}

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetRGCShareUrlResult(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	}

	private void initLocationNetWork() {

		showProgressDialog();

		mLocationListener = new com.baidu.mapapi.LocationListener() {

			public void onLocationChanged(Location loc) {
				if (loc != null) {
					m_locPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
							(int) (loc.getLongitude() * 1E6));
					//
					// mKSearch.reverseGeocode(geoPoint);
					// mMapController.animateTo(geoPoint);
					// mMapController.setCenter(geoPoint); // 设置地图中心点
					//
					// mylocationOverlay.enableMyLocation(); // 启用定位
					// mylocationOverlay.enableCompass(); // 启用指南针
					// mMapView.getOverlays().clear();
					// mMapView.getOverlays().add(mylocationOverlay);
					// 添加到MapView的覆盖物中
					// mMapView.getOverlays().add(new MyOverlay());
					location = loc;
					lat = location.getLatitude();
					lon = location.getLongitude();
					mMKSearch.reverseGeocode(m_locPoint); // 逆地址解析
					refresh();
					Log.e("net_lat", String.valueOf(loc.getLatitude()));
					Log.e("net_lon", String.valueOf(loc.getLongitude()));
				}

			}
		};
		if (mLocationListener != null) {
			CrowdroidApplication.getInstance().getMapManager()
					.getLocationManager()
					.requestLocationUpdates(mLocationListener);
			CrowdroidApplication.getInstance().getMapManager().start();
			mMKSearch = new MKSearch();
			mMKSearch.init(CrowdroidApplication.getInstance().getMapManager(),
					new MySearchListener());
		}
	}

	private void changetype(int which) {
		commType = CommHandler.TYPE_LBS_GET_SEARCH_POI;
		switch (which) {
		case 0: {
			type = "010000";

			break;
		}
		case 1: {
			type = "020000";

			break;
		}
		case 2: {
			type = "030000";

			break;
		}
		case 3: {
			type = "040000";

			break;
		}
		case 4: {
			type = "050000";

			break;
		}
		case 5: {
			type = "060000";

			break;
		}
		case 6: {
			type = "070000";

			break;
		}

		case 7: {
			type = "080000";

			break;
		}
		case 8: {
			type = "090000";

			break;
		}
		case 9: {
			type = "100000";

			break;
		}
		case 10: {
			type = "110000";

			break;
		}
		case 11: {
			type = "120000";

			break;
		}
		case 12: {
			type = "130000";

			break;
		}
		case 13: {
			type = "140000";

			break;
		}
		case 14: {
			type = "150000";

			break;
		}
		case 15: {
			type = "160000";

			break;
		}
		case 16: {
			type = "170000";

			break;
		}
		case 17: {
			type = "180000";

			break;
		}
		case 18: {
			type = "190000";

			break;
		}
		case 19: {
			type = "200000";

			break;
		}
		}
	}

}
