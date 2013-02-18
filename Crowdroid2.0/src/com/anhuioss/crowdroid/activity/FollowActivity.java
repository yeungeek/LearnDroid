package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

/**
 * @author oss
 * 
 */
public class FollowActivity extends BasicActivity implements OnClickListener,
		ServiceConnection {

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ListView listView = null;

	private RelativeLayout relativeRight = null;

	private RelativeLayout relativeBottom = null;

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
	public static long nextCursor = -1;

	public static List<String> userIdsForLookup;

	private int currentPage = 0;

	private String imageShow;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

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

						// Add More Tweets If Needs
						addItemForMoreTweets();

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

					// Add More Tweets If Needs
					addItemForMoreTweets();

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
				if (nextCursor != 0
						|| (userIdsForLookup != null && userIdsForLookup.size() > 0)) {
					refresh();
				}
			} else {
				// Open Profile
				Intent intent = new Intent(FollowActivity.this,
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

					// // Get Current List
					// currentList = userInfoList;
					//
					// if (imageShow.equals(SettingsActivity.select[0])
					// || imageShow.equals(SettingsActivity.select[1])) {
					// // Download Image
					// downloadImage(userInfoList);
					// } else {
					// // UnDownload Image , Create List
					// createListView(userInfoList);
					// }
					createListView(userInfoList);

					// Add More Tweets If Needs
					if (userInfoList.size() >= 20) {
						addItemForMoreTweets();
					}
				}

			} else {
				Toast.makeText(
						FollowActivity.this,
						ErrorMessage.getErrorMessage(FollowActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
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
		settingData = crowdroidApplication.getSettingData();

		imageShow = settingData.getSelectionShowImage();

		screenName = getIntent().getExtras().getString("screenName");
		userName = getIntent().getExtras().getString("userName");
		uid = getIntent().getExtras().getString("uid");

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

		case R.id.head_refresh: {
			Intent home = new Intent(FollowActivity.this,
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

	// -----------------------------------------------------------------------------
	/**
	 * Refresh
	 */
	// -----------------------------------------------------------------------------
	private void refresh() {

		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();

		if (apiServiceInterface == null) {
			return;
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
					CommHandler.TYPE_GET_FRIENDS_LIST, apiServiceListener,
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
