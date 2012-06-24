package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;

public class FollowedActivity extends BasicActivity implements OnClickListener,
		ServiceConnection {

	protected ArrayList<UserInfo> userInfoDataList = new ArrayList<UserInfo>();

	protected ArrayList<UserInfo> currentList = new ArrayList<UserInfo>();

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	public static List<String> userIdsForLookup;

	// content
	private ListView listView = null;

	private RelativeLayout relativeRight = null;

	private RelativeLayout relativeBottom = null;

	// head
	private Button headerBack = null;

	private Button headerHome = null;

	private TextView headerName = null;

	private SimpleAdapter adapter;

	private StatusData statusData;

	private SettingData settingData;

	private AccountData accountData;

	private AccountData currentAccount;

	private String screenName;

	private String userName;

	private String uid = "";

	/** Next Cursor (Twitter) */
	public static long nextCursor = -1;

	private int size = 0;

	private int currentPage = 0;

	private boolean flag = false;

	private String imageShow;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				if (nextCursor != 0
						|| (userIdsForLookup != null && userIdsForLookup.size() > 0)) {
					refresh();
				}
			} else {
				// Open Profile
				Intent intent = new Intent(FollowedActivity.this,
						ProfileActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", userInfoDataList.get(position)
						.getScreenName());
				bundle.putString("uid", userInfoDataList.get(position).getUid());
				bundle.putString("user_name", userInfoDataList.get(position)
						.getUserName());
				intent.putExtras(bundle);
				startActivity(intent);

			}

		}

	};

	private DownloadServiceInterface downloadServiceInterface;

	private DownloadServiceListener.Stub downloadServiceListener = new DownloadServiceListener.Stub() {

		@Override
		public void requestCompleted(String uid, String statusCode,
				byte[] message) throws RemoteException {

			try {

				// Get Bitmap
				if (statusCode != null && statusCode.equals("200")) {

					Bitmap bitmap = BitmapFactory.decodeByteArray(message, 0,
							message.length);
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

						// Add More Tweets If Needs
						addItemForMoreTweets();

					}

				}

				System.gc();

			} catch (OutOfMemoryError e) {
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

			if (statusCode != null && statusCode.equals("200")
					&& type != CommHandler.TYPE_CLEAR_UNREAD_MESSAGE) {

				// Parser
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				ParseHandler parseHandler = new ParseHandler();
				userInfoList = (ArrayList<UserInfo>) parseHandler.parser(
						service, type, statusCode, message);

				if (userInfoList.size() > 0) {

					// Get Current List
					currentList = userInfoList;

					// if (imageShow.equals(SettingsActivity.select[0])
					// || imageShow.equals(SettingsActivity.select[1])) {
					// // Download Image
					// downloadImage(userInfoList);
					// } else {
					// // Undownload Image , Create List
					// createListView(userInfoList);
					// }
					createListView(userInfoList);

					// Add More Tweets If Needs
					addItemForMoreTweets();

				}
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancel(2);
				if (flag
						&& statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_SINA)) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("type", "4");
					try {
						apiServiceInterface.request(
								statusData.getCurrentService(),
								CommHandler.TYPE_CLEAR_UNREAD_MESSAGE,
								apiServiceListener, map);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}

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
		headerName.setText(R.string.followers_list);
		headerHome.setBackgroundResource(R.drawable.main_home);

		// Find Views
		listView = (ListView) findViewById(R.id.list_view);

		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		listView.setDivider(null);

		// Prepare Simple Adapter For List View
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {

			adapter = new SimpleAdapter(
					this,
					data,
					R.layout.sina_basic_timeline_layout_list_item,
					new String[] { "screenName", "status", "userImage", "time",
							"verified", "web", "webStatus", "retweetStatus",
							"retweetCount", "commentCount", "moreTweets" },
					new int[] { R.id.screen_name, R.id.status, R.id.user_image,
							R.id.update_time, R.id.sina_user_verified,
							R.id.web_view_status, R.id.web_status,
							R.id.web_retweet_status, R.id.text_retweet_count,
							R.id.text_comment_count, R.id.text_get_more_tweets });
		} else {
			adapter = new SimpleAdapter(this, data,
					R.layout.basic_timeline_layout_list_item, new String[] {
							"screenName", "status", "userImage", "time", "web",
							"moreTweets" }, new int[] { R.id.screen_name,
							R.id.status, R.id.user_image, R.id.update_time,
							R.id.web_view_status, R.id.text_get_more_tweets });
		}

		listView.setAdapter(adapter);

		checkProfile();

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
		nextCursor = -1;
		userIdsForLookup = null;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		settingData = crowdroidApplication.getSettingData();

		imageShow = settingData.getSelectionShowImage();

		Bundle bundle = getIntent().getExtras();

		if (bundle == null) {
			screenName = accountData.getUserScreenName();
			userName = accountData.getUserName();
			uid = accountData.getUid();

		} else if (bundle.getString("context").equals("NotificationService")) {
			screenName = accountData.getUserScreenName();
			userName = accountData.getUserName();
			uid = accountData.getUid();
			flag = true;
			// Save Follower Count
			accountData.setLastUserFollowerCount(bundle
					.getString("follower_count"));
			AccountList accountList = crowdroidApplication.getAccountList();
			accountList.refreshAccount(accountData);
		} else {
			screenName = bundle.getString("screenName");
			userName = bundle.getString("userName");
			uid = bundle.getString("uid");
			// Save Follower Count
			accountData.setLastUserFollowerCount(bundle
					.getString("follower_count"));
			AccountList accountList = crowdroidApplication.getAccountList();
			accountList.refreshAccount(accountData);
		}

		SettingData settingData = crowdroidApplication.getSettingData();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();

		adapter.setViewBinder(new MyImageBinder(fontColor, fontSize, null, this));

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

		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back: {

			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent home = new Intent(FollowedActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}

		default:
			break;
		}
	}

	// -----------------------------------------------------------------------------
	/**
	 * Refresh
	 */
	// -----------------------------------------------------------------------------
	private void refresh() {

		if (apiServiceInterface == null) {
			return;
		}
		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();
		if (IGeneral.SERVICE_NAME_TENCENT
				.equals(statusData.getCurrentService())) {
			// Prepare Parameters
			Map<String, Object> parameter;
			parameter = new HashMap<String, Object>();
			parameter.put("type", "8");
			TencentCommHandler.clearUnreadMessage(parameter);
		}

		currentPage++;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("screen_name", screenName);
		parameters.put("username", userName);
		parameters.put("cursor", String.valueOf(nextCursor));
		parameters.put("page", String.valueOf(currentPage));
		parameters.put("uid", uid);

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_FOLLOWERS_LIST, apiServiceListener,
					parameters);
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
							IGeneral.SERVICE_NAME_SOHU)) {
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

			// Add More Tweets If Needs
			addItemForMoreTweets();

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
			if (((CrowdroidApplication) getApplicationContext())
					.isComeFromNotification(2)) {
				data.clear();
				userInfoDataList.clear();
			}
			if (data.isEmpty()) {
				refresh();
			}
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

	@Override
	protected void refreshByMenu() {
		// Clear Data
		data.clear();
		currentList.clear();
		userInfoDataList.clear();
		adapter.notifyDataSetChanged();

		// Set Page
		currentPage = 0;

		// Cancel Notification
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(2);

		// Refresh
		refresh();
	}

	private void checkProfile() {
		Bundle bundle = this.getIntent().getExtras();
		String screenName = bundle.getString("screenName");

		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)
				&& !currentAccount.getUserScreenName().equals(screenName)) {
			Toast.makeText(this, getString(R.string.permission),
					Toast.LENGTH_SHORT).show();
			finish();
		}
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
