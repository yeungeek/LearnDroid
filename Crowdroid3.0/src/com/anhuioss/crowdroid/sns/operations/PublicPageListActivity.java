package com.anhuioss.crowdroid.sns.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.communication.TranslationService;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.AlbumsTimelineActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class PublicPageListActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ListView listView = null;

	private LinearLayout linearTab = null;

	private RelativeLayout relativeRight = null;

	private RelativeLayout layout_bottom = null;

	// head
	private Button headerBack = null;

	private Button headerRefresh = null;

	private TextView headName = null;

	private SimpleAdapter adapter;

	protected ArrayList<UserInfo> userInfoDataList;

	protected ArrayList<UserInfo> currentList;

	private int commType;

	/** Image Map for user profile */
	public static HashMap<String, Bitmap> userImageMap = new HashMap<String, Bitmap>();

	private int currentPage = 1;

	private StatusData statusData;

	private SettingData settingData;

	private AccountData accountData;

	private CrowdroidApplication crowdroidApplication;

	/** Auto Refresh Timer */
	// AutoRefreshHandler autoRefresh;

	/** Refresh Back */
	boolean refreshBack = false;

	private String service;

	private MyImageBinder myImageBinder;

	private String imageShow;

	private int current_position;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	ProgressDialog pd = null;

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {

			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				refresh();
			} else {

				final CharSequence[] operate = getResources().getStringArray(
						R.array.renren_discovery_page_manager);
				AlertDialog dialog = new AlertDialog.Builder(
						PublicPageListActivity.this).setItems(operate,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									Intent intent = new Intent(
											PublicPageListActivity.this,
											ProfileActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("name", userInfoDataList
											.get(position).getScreenName());
									bundle.putString("uid", userInfoDataList
											.get(position).getUid());
									bundle.putString("user_name",
											userInfoDataList.get(position)
													.getUserName());
									intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									intent.putExtras(bundle);
									startActivity(intent);
								} else if (which == 1) {
									Intent i = new Intent(
											PublicPageListActivity.this,
											PageFollowerListActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("categoryName",
											userInfoDataList.get(position)
													.getScreenName());
									bundle.putString("pageId", userInfoDataList
											.get(position).getUid());
									i.putExtras(bundle);
									i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(i);
								}
							}
						}).create();
				dialog.show();

			}

		}
	};

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			listView.setClickable(true);
			listView.setEnabled(true);
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {
				// Parser
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				ParseHandler parseHandler = new ParseHandler();
				userInfoList = (ArrayList<UserInfo>) parseHandler.parser(
						service, type, statusCode, message);

				if (userInfoList != null && userInfoList.size() > 0) {

					currentList = userInfoList;

					createListView(userInfoList);
					if (userInfoList.size() >= 20) {
						addItemForMoreTweets();
					}

				}
			}
			if (!"200".equals(statusCode)) {
				Toast.makeText(
						PublicPageListActivity.this,
						ErrorMessage.getErrorMessage(
								PublicPageListActivity.this, statusCode),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	// -----------------------------------------------------------------------------
	/**
	 * Communication Type
	 */
	// -----------------------------------------------------------------------------
	protected void setCommType(int commType) {

		this.commType = commType;

	}

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

		userInfoDataList = new ArrayList<UserInfo>();

		currentList = new ArrayList<UserInfo>();

		setLayoutResId(R.layout.timeline_layout);

		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		linearTab = (LinearLayout) findViewById(R.id.linear_layout_tab);
		relativeRight = (RelativeLayout) findViewById(R.id.relativeLayout_right);
		layout_bottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);

		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);

		// =====================================================
		// ListView
		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(onItemClickListener);

		listView.setDivider(null);
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();

		initTimeLineView();

		service = statusData.getCurrentService();
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
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_refresh: {
			Intent home = new Intent(PublicPageListActivity.this,
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

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();

		refreshBack = true;

		// Unbind Service
		unbindService(this);
	}

	private void initTimeLineView() {
		linearTab.setVisibility(View.GONE);
		relativeRight.setVisibility(View.GONE);
		layout_bottom.setVisibility(View.GONE);
		headName.setText(getIntent().getExtras().getString("categoryName"));
		headerRefresh.setBackgroundResource(R.drawable.main_home);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		if (name.getShortClassName().equals(API_SERVICE_NAME)) {

			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

			if (((CrowdroidApplication) getApplicationContext())
					.isComeFromNotification(0)) {
				data.clear();
				userInfoDataList.clear();
				adapter.notifyDataSetChanged();
			}

			if (data.isEmpty()) {
				try {

					listView.setClickable(false);
					// setProgressBarIndeterminateVisibility(true);
					showProgressDialog();

					// Prepare Parameters
					Map<String, Object> parameters;
					parameters = new HashMap<String, Object>();
					parameters.put("page", currentPage);
					parameters.put("categoryId", getIntent().getExtras()
							.getString("categoryId"));

					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_PAGE_LIST_BY_CATEGORY,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
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
		notificationManager.cancel(1);

		// Refresh
		refresh();

	}

	// -----------------------------------------------------------------------------
	/**
	 * Refresh
	 */
	// -----------------------------------------------------------------------------
	public void refresh() {

		listView.setClickable(false);
		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();
		if (apiServiceInterface == null) {
			return;
		}
		userInfoDataList.clear();
		currentList.clear();
		currentPage = currentPage + 1;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("page", currentPage);
		parameters.put("categoryId",
				getIntent().getExtras().getString("categoryId"));

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_PAGE_LIST_BY_CATEGORY,
					apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(ArrayList<UserInfo> userInfoList) {

//		data.clear();
		// userInfoDataList.clear();

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		// Prepare ArrayList
		for (UserInfo userInfo : userInfoList) {

			userInfoDataList.add(userInfo);

			Map<String, Object> map;
			map = new HashMap<String, Object>();
			map.put("screenName", userInfo.getScreenName() == null ? ""
					: userInfo.getScreenName());
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

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(PublicPageListActivity.this);
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
