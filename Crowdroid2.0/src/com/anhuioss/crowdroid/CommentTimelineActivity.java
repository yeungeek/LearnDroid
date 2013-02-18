package com.anhuioss.crowdroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.AtMessageTimelineActivity;
import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.MyTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.DirectMessageReceiveActivity;
import com.anhuioss.crowdroid.DirectMessageSendActivity;
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
import com.anhuioss.crowdroid.communication.DownloadServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.BasicInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.notification.NotificationService;
import com.anhuioss.crowdroid.operations.ScheduleMonthActivity;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

public class CommentTimelineActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	public static final String DOWNLOAD_SERVICE_NAME = ".communication.DownloadService";

	private String cursor_id = "";

	/** current page **/
	private int currentPage = 1;

	private int size = 0;

	private int selectItem = -1;

	private SimpleAdapter adapter;

	private CrowdroidApplication crowdroidApplication;

	private StatusData statusData;

	private AccountData accountData;

	private SettingData settingData;

	private String imageShow;

	/** Auto Refresh Timer */
	AutoRefreshHandler autoRefresh;

	/** Refresh Back */
	boolean refreshBack = false;

	/** Auto Refresh Flag */
	boolean autoRefreshFlag = false;

	private long lastRefreshTime;

	private static boolean isRefreshFromAutoRefresh = false;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	protected ArrayList<UserInfo> userInfoDataList;

	/** Image Map for user profile */
	public static HashMap<String, Bitmap> userImageMap = new HashMap<String, Bitmap>();

	protected ArrayList<TimeLineInfo> timeLineDataList = new ArrayList<TimeLineInfo>();

	protected ArrayList<TimeLineInfo> currentList = new ArrayList<TimeLineInfo>();

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ListView listView = null;

	private LinearLayout linearTab = null;
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
	// right
	private Button btnMention = null;

	private Button btnDirect = null;

	private Button btnComment = null;

	private Button btnMyTimeline = null;

	private Button btnSchedule = null;

	TimeLineInfo retweetTimelineInfo = null;

	private ApiServiceInterface apiServiceInterface;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			// Refresh
			autoRefresh();

		}

		private void autoRefresh() {

			// Clear Data
			data.clear();
			currentList.clear();
			timeLineDataList.clear();
			adapter.notifyDataSetChanged();

			// Set Page
			currentPage = 0;

			isRefreshFromAutoRefresh = true;

			// Refresh
			refresh();

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

					Bitmap bitmap;
					if (message != null && message.length > 0) {
						bitmap = BitmapFactory.decodeByteArray(message, 0,
								message.length);
					} else {
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.image);
					}
					if (retweetTimelineInfo != null
							&& retweetTimelineInfo.getMessageId() != null) {
						// setProgressBarIndeterminateVisibility(false);
						closeProgressDialog();
						retweetTimelineInfo.getUserInfo().setUserImage(bitmap);
						// new ActionDialog(CommentTimelineActivity.this,
						// retweetTimelineInfo, timeLineDataList).show();
						Intent comment = new Intent(
								CommentTimelineActivity.this,
								DetailTweetActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("commtype",
								CommHandler.TYPE_GET_COMMENT_TIMELINE);
						bundle.putSerializable("timelineinfo",
								retweetTimelineInfo);
						bundle.putSerializable("timelinedatalist",
								timeLineDataList);
						comment.putExtras(bundle);
						startActivity(comment);
						retweetTimelineInfo = null;
						return;
					} else {
						userImageMap.put(uid, bitmap);

						// Download Size
						size = size - 1;

						if (size == 0) {

							// setProgressBarIndeterminateVisibility(false);
							closeProgressDialog();

							// User Image
							loadUserImage(currentList);

							CreateList(currentList);

							addItemForMoreTweets();
						}
					}

				}

				System.gc();

			} catch (OutOfMemoryError e) {
			}

		}
	};

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			BasicInfo.setNetTime();
			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {

				if (type == CommHandler.TYPE_GET_COMMENT_TIMELINE) {
					ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

					ParseHandler parseHandler = new ParseHandler();
					timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);

					currentList = timelineInfoList;
					if (timelineInfoList != null
							&& timelineInfoList.size() > 0
							&& !timelineInfoList.get(
									timelineInfoList.size() - 1).equals("")) {
						cursor_id = timelineInfoList.get(
								timelineInfoList.size() - 1).getcursor_id();
					}

					if (timelineInfoList.size() > 0) {

						if (currentPage == 1 && !isRefreshFromAutoRefresh) {

							setNewestMessageId(type, timelineInfoList);
						}

						isRefreshFromAutoRefresh = false;

						CreateList(timelineInfoList);
						if (timelineInfoList.size() >= 20) {
							addItemForMoreTweets();
						}

						// if (imageShow.equals(SettingsActivity.select[0])
						// || imageShow.equals(SettingsActivity.select[1])) {
						// // Download Image
						// downloadImage(timelineInfoList);
						// } else {
						// // Undownload Image ,Create List
						// CreateList(timelineInfoList);
						// }

					} else {
						Toast.makeText(CommentTimelineActivity.this,
								getResources().getString(R.string.comment_tip),
								Toast.LENGTH_LONG).show();
					}
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					notificationManager.cancel(4);
					// Bundle bundle = getIntent().getExtras();
					// if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
					// &&
					// bundle != null &&
					// bundle.getString("context").equals("NotificationService")){
					Map<String, String> map = new HashMap<String, String>();
					map.put("type", "1");
					try {
						apiServiceInterface.request(
								statusData.getCurrentService(),
								CommHandler.TYPE_CLEAR_UNREAD_MESSAGE,
								apiServiceListener, map);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					// }
				} else if (type == CommHandler.TYPE_GET_TIMELINE_BY_ID) {

					ParseHandler parseHandler = new ParseHandler();
					retweetTimelineInfo = (TimeLineInfo) parseHandler.parser(
							service, type, statusCode, message);

					if (retweetTimelineInfo != null
							&& retweetTimelineInfo.getMessageId() != null) {

						ArrayList<TimeLineInfo> list = new ArrayList<TimeLineInfo>();
						list.add(retweetTimelineInfo);

						// Open Action Dialog
						// new ActionDialog(CommentTimelineActivity.this,
						// retweetTimelineInfo, list).show();
						Intent comment = new Intent(
								CommentTimelineActivity.this,
								DetailTweetActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("commtype",
								CommHandler.TYPE_GET_TIMELINE_BY_ID);
						bundle.putSerializable("timelineinfo",
								retweetTimelineInfo);
						bundle.putSerializable("timelinedatalist", list);
						comment.putExtras(bundle);
						startActivity(comment);
					}

				}
			}

			if (!"200".equals(statusCode)) {
				Toast.makeText(CommentTimelineActivity.this, ErrorMessage
						.getErrorMessage(CommentTimelineActivity.this,
								statusCode), Toast.LENGTH_SHORT);
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		// Set Gallery
		setLayoutResId(R.layout.timeline_layout);
		linearTab = (LinearLayout) findViewById(R.id.linear_layout_tab);
		linearTab.setVisibility(View.GONE);

		btnHeaderBack = (Button) findViewById(R.id.head_back);
		btnHeaderRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);

		btnHome = (Button) findViewById(R.id.tools_bottom_home);
		btnNewTweet = (Button) findViewById(R.id.tools_bottom_new);
		btnDiscover = (Button) findViewById(R.id.tools_bottom_app);
		btnProfile = (Button) findViewById(R.id.tools_bottom_profile);
		btnMore = (Button) findViewById(R.id.tools_bottom_more);

		btnMention = (Button) findViewById(R.id.main_at);
		btnDirect = (Button) findViewById(R.id.main_direct);
		btnComment = (Button) findViewById(R.id.main_comment);
		btnMyTimeline = (Button) findViewById(R.id.main_my_timeline);
		btnSchedule = (Button) findViewById(R.id.main_schedule);

		btnHome.setOnClickListener(this);
		btnNewTweet.setOnClickListener(this);
		btnDiscover.setOnClickListener(this);
		btnProfile.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		btnHeaderBack.setOnClickListener(this);
		btnHeaderRefresh.setOnClickListener(this);
		btnComment.setOnClickListener(this);
		btnDirect.setOnClickListener(this);
		btnMention.setOnClickListener(this);
		btnMyTimeline.setOnClickListener(this);
		btnSchedule.setOnClickListener(this);
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			btnSchedule.setVisibility(View.VISIBLE);
			btnSchedule.setBackgroundResource(R.drawable.right_schedule);
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)) {
			btnDirect.setVisibility(View.GONE);

		}
		// 网易翻页标志参数
		cursor_id = "";

		headName.setText(R.string.comment);

		userInfoDataList = new ArrayList<UserInfo>();

		// Find Views
		listView = (ListView) findViewById(R.id.list_view);

		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);
		listView.setDivider(null);
		// Prepare Simple Adapter For List View

		adapter = new SimpleAdapter(this, data,
				R.layout.sina_comment_timeline_layout_list_item, new String[] {
						"screenName", "status", "userImage", "time",
						"verified", "originalTweet", "webStatus",
						"retweetStatus", "moreTweets" }, new int[] {
						R.id.sina_screen_name, R.id.status, R.id.user_image,
						R.id.sina_update_time, R.id.sina_user_verified,
						R.id.commented_status, R.id.web_status,
						R.id.web_retweet_status, R.id.text_get_more_tweets });
		listView.setAdapter(adapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		isRunning = true;
		// CrowdroidApplication crowdroidApplication = (CrowdroidApplication)
		// getApplicationContext();

		settingData = crowdroidApplication.getSettingData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		autoRefreshFlag = settingData.isAutoRefresh();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();

		// New Instances Of Refresh Thread
		autoRefresh = new AutoRefreshHandler();
		// Start Auto Refresh
		if (autoRefresh != null && !autoRefresh.isAlive() && autoRefreshFlag) {
			autoRefresh.start();
		}

		imageShow = settingData.getSelectionShowImage();

		adapter.setViewBinder(new MyImageBinder(fontColor, fontSize, null, this));

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		// // Bind Download Service
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
		// Stop Auto Refresh
		if (autoRefresh != null && autoRefresh.isAlive()) {
			autoRefresh.stopAutoRefresh();
			try {
				autoRefresh.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			autoRefresh = null;
		}

		refreshBack = true;
		unbindService(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tools_bottom_home: {

			Intent home = new Intent(CommentTimelineActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		}
		case R.id.tools_bottom_new: {
			Intent tweet = new Intent(CommentTimelineActivity.this,
					SendMessageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("action", "timeline");
			bundle.putString("target", "");
			tweet.putExtras(bundle);
			tweet.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(tweet);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_app: {
			Intent app = null;
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				app = new Intent(CommentTimelineActivity.this,
						SNSDiscoveryActivity.class);
			} else {
				app = new Intent(CommentTimelineActivity.this,
						DiscoveryActivity.class);
			}
			app.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(app);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_profile: {
			Intent intent = new Intent(CommentTimelineActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", "");
			bundle.putString("user_name", "");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_more: {
			Intent more = new Intent(CommentTimelineActivity.this,
					MoreFunctionActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", accountData.getUserScreenName());
			more.putExtras(bundle);
			startActivity(more);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.head_refresh: {

			cursor_id = "";

			// Clear Data
			data.clear();
			currentList.clear();
			timeLineDataList.clear();
			adapter.notifyDataSetChanged();
			// Set Page
			currentPage = 0;
			// Cancel Notification
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(1);
			isRefreshFromAutoRefresh = false;
			// Refresh
			refresh();
			break;
		}
		case R.id.main_at: {
			Intent at = new Intent(CommentTimelineActivity.this,
					AtMessageTimelineActivity.class);
			at.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(at);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		}
		case R.id.main_my_timeline: {
			Intent at = new Intent(CommentTimelineActivity.this,
					MyTimelineActivity.class);
			at.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(at);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		}
		case R.id.main_direct: {
			Intent direct = new Intent(CommentTimelineActivity.this,
					DirectMessageSendActivity.class);
			direct.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(direct);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		}
		case R.id.main_comment: {
			break;
		}
		case R.id.main_schedule: {
			Intent intent = new Intent(CommentTimelineActivity.this,
					ScheduleMonthActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			autoRefresh.lockAutoRefresh(false);

			// Start Auto Refresh
			if (refreshBack && !autoRefresh.isAlive() && autoRefreshFlag) {
				autoRefresh.start();
			}

		} else {

			autoRefresh.lockAutoRefresh(true);

		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if (name.getShortClassName().equals(API_SERVICE_NAME)) {

			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

			// Cancel Notification
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(49);

			if (((CrowdroidApplication) getApplicationContext())
					.isComeFromNotification(1)) {
				data.clear();
			}

			if (data.isEmpty()) {
				// setProgressBarIndeterminateVisibility(true);
				showProgressDialog();
				// Prepare Parameter
				Map<String, Object> parameter;
				parameter = new HashMap<String, Object>();
				parameter.put("page", currentPage);
				parameter.put("since_id", cursor_id);

				// HTTP Communication
				try {
					isRefreshFromAutoRefresh = false;

					lastRefreshTime = System.currentTimeMillis();

					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_COMMENT_TIMELINE,
							apiServiceListener, parameter);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		} else if (name.getShortClassName().equals(DOWNLOAD_SERVICE_NAME)) {
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

	ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				refresh();

			} else {
				selectItem = position;
				openSelectDialog(selectItem);

			}
		}

	};

	private void openSelectDialog(final int select) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(
				CommentTimelineActivity.this);
		String[] items = null;
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			items = getResources()
					.getStringArray(R.array.sohu_select_operation);

			dlg.setItems(items, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					switch (which) {
					case 0: {
						showProgressDialog();
						// Prepare Parameter
						Map<String, Object> parameter;
						parameter = new HashMap<String, Object>();
						parameter.put("id", timeLineDataList.get(select)
								.getStatusId());
						parameter.put("page", currentPage);

						Log.i("test", timeLineDataList.get(select)
								.getStatusId());

						// HTTP Communication
						try {
							apiServiceInterface.request(
									statusData.getCurrentService(),
									CommHandler.TYPE_GET_TIMELINE_BY_ID,
									apiServiceListener, parameter);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						break;
					}
					case 1: {
						Intent intent = new Intent(
								CommentTimelineActivity.this,
								ProfileActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("name", timeLineDataList.get(select)
								.getUserInfo().getScreenName());
						bundle.putString("uid", timeLineDataList.get(select)
								.getUserInfo().getUid());
						bundle.putString("user_name",
								timeLineDataList.get(select).getUserInfo()
										.getUserName());
						intent.putExtras(bundle);
						startActivity(intent);
						break;
					}
					}

				}
			}).create().show();
		} else {
			items = getResources().getStringArray(R.array.select_operation);
			dlg.setItems(items, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					switch (which) {
					case 0: {
						Intent intent = new Intent(
								CommentTimelineActivity.this,
								SendMessageActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("action", "reply");
						bundle.putString("target", "");
						bundle.putString("cid", timeLineDataList.get(select)
								.getinReplyToStatusId());
						bundle.putString("id", timeLineDataList.get(select)
								.getMessageId());
						intent.putExtras(bundle);
						startActivity(intent);
						break;
					}
					case 1: {

						showProgressDialog();
						// Prepare Parameter
						Map<String, Object> parameter;
						parameter = new HashMap<String, Object>();
						parameter.put("id", timeLineDataList.get(select)
								.getStatusId());
						parameter.put("page", currentPage);

						Log.i("test", timeLineDataList.get(select)
								.getStatusId());

						// HTTP Communication
						try {
							apiServiceInterface.request(
									statusData.getCurrentService(),
									CommHandler.TYPE_GET_TIMELINE_BY_ID,
									apiServiceListener, parameter);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						break;
					}
					case 2: {
						Intent intent = new Intent(
								CommentTimelineActivity.this,
								ProfileActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("name", timeLineDataList.get(select)
								.getUserInfo().getScreenName());
						bundle.putString("uid", timeLineDataList.get(select)
								.getUserInfo().getUid());
						bundle.putString("user_name",
								timeLineDataList.get(select).getUserInfo()
										.getUserName());
						intent.putExtras(bundle);
						startActivity(intent);
						break;
					}
					}

				}
			}).create().show();
		}

	}

	// -----------------------------------------------------------------------------
	/**
	 * Load userImage.
	 */
	// -----------------------------------------------------------------------------
	private void loadUserImage(ArrayList<TimeLineInfo> timelineInfoList) {

		if (timelineInfoList == null) {
			return;
		}

		// Garbage Collection
		System.gc();

		for (TimeLineInfo info : timelineInfoList) {

			// Get UserInfo
			UserInfo userInfo = info.getUserInfo();

			// Set Image To Info
			userInfo.setUserImage(userImageMap.get(userInfo.getUid()));

		}

		// Garbage Collection
		System.gc();

	}

	private void downloadImage(ArrayList<TimeLineInfo> timelineInfoList) {

		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();

		// The List For Download
		ArrayList<UserInfo> downloadUserList = new ArrayList<UserInfo>();

		// Prepare List For Download
		for (TimeLineInfo timelineInfo : timelineInfoList) {
			if (timelineInfo.getUserInfo().getUid() != null
					&& !userImageMap.containsKey(timelineInfo.getUserInfo()
							.getUid())) {
				downloadUserList.add(timelineInfo.getUserInfo());
			}
		}

		size = downloadUserList.size();

		if (size == 0) {

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();

			if (retweetTimelineInfo != null
					&& retweetTimelineInfo.getMessageId() != null) {
				retweetTimelineInfo.getUserInfo().setUserImage(
						userImageMap.get(retweetTimelineInfo.getUserInfo()
								.getUid()));
				// new ActionDialog(CommentTimelineActivity.this,
				// retweetTimelineInfo, timeLineDataList).show();
				Intent comment = new Intent(CommentTimelineActivity.this,
						DetailTweetActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("commtype", CommHandler.TYPE_GET_COMMENT_TIMELINE);
				bundle.putSerializable("timelineinfo", retweetTimelineInfo);
				bundle.putSerializable("timelinedatalist", timeLineDataList);
				comment.putExtras(bundle);
				startActivity(comment);
				retweetTimelineInfo = null;
				return;
			} else {

				// User Image
				loadUserImage(timelineInfoList);

				CreateList(currentList);

				addItemForMoreTweets();

			}

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

	// Create ListView
	private void CreateList(ArrayList<TimeLineInfo> timeLineInfoList) {

		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();
		for (TimeLineInfo timeLineInfo : timeLineInfoList) {

			timeLineDataList.add(timeLineInfo);
			// Prepare ArrayList

			Map<String, Object> map;
			map = new HashMap<String, Object>();
			map.put("screenName",
					timeLineInfo.getUserInfo().getScreenName() == null ? ""
							: timeLineInfo.getUserInfo().getScreenName());
			String statusImages = timeLineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			String status = TagAnalysis.clearImageUrls(
					timeLineInfo.getStatus(), statusImages);
			map.put("status", status);
			map.put("webStatus", status);
			// map.put("status", timeLineInfo.getStatus());
			// map.put("webStatus", timeLineInfo.getStatus());
			if (imageShow.equals(BrowseModeActivity.select[0])
					|| imageShow.equals(BrowseModeActivity.select[1])) {
				// Use Download Image
				map.put("userImage", timeLineInfo.getUserInfo()
						.getUserImageURL());
			} else {
				// Use Default Image
				map.put("userImage",
						String.valueOf(R.drawable.default_user_image));
			}
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)) {
				map.put("verified", timeLineInfo.getUserInfo().getVerified());
			}
			map.put("originalTweet", "评论我的微博:" + timeLineInfo.getReplyStatus());
			map.put("retweetStatus", "评论我的微博:" + timeLineInfo.getReplyStatus());
			map.put("time", timeLineInfo.getFormatTime(
					statusData.getCurrentService(), this));
			addDatas.add(map);

		}
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}
		adapter.notifyDataSetChanged();
	}

	public void addItemForMoreTweets() {

		if (data.size() > 0
				&& !data.get(data.size() - 1).get("screenName").equals("")) {
			try {
				Map<String, Object> map;
				map = new HashMap<String, Object>();
				map.put("screenName", "");
				// String textData = getResources().getString(
				// R.string.get_more_tweets);
				// String timeData = "";
				// if (IGeneral.SERVICE_NAME_SINA.equals(statusData
				// .getCurrentService())
				// || IGeneral.SERVICE_NAME_SOHU.equals(statusData
				// .getCurrentService())
				// || IGeneral.SERVICE_NAME_TENCENT.equals(statusData
				// .getCurrentService())) {
				// textData = "";
				// timeData = getResources().getString(
				// R.string.get_more_tweets);
				// }
				// map.put("status", textData);
				// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				// android.R.drawable.ic_menu_more);
				// map.put("userImage",
				// String.valueOf(android.R.drawable.ic_menu_more));
				// map.put("time", timeData);
				// map.put("web", "");
				map.put("moreTweets",
						getResources().getString(R.string.get_more_tweets));
				data.add(map);
				adapter.notifyDataSetChanged();
			} catch (OutOfMemoryError e) {
				System.gc();
			}
		}

		// if (data.size() > 0
		// && !data.get(data.size() - 1).get("screenName").equals("")) {
		// try {
		// Map<String, Object> map;
		// map = new HashMap<String, Object>();
		// map.put("screenName", "");
		// map.put("status", getResources().getString(
		// R.string.get_more_comments));
		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		// android.R.drawable.ic_menu_more);
		// map.put("userImage", bitmap);
		// map.put("time", "");
		// map.put("originalTweet", "");
		// data.add(map);
		// adapter.notifyDataSetChanged();
		// } catch (OutOfMemoryError e) {}
		// }

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

	// -----------------------------------------------------------------------------
	/**
	 * Refresh
	 */
	// -----------------------------------------------------------------------------
	private void refresh() {

		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();

		lastRefreshTime = System.currentTimeMillis();

		if (apiServiceInterface == null) {
			return;
		}

		currentPage++;

		// Prepare Parameter
		Map<String, Object> parameter;
		parameter = new HashMap<String, Object>();
		parameter.put("page", currentPage);
		// if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_WANGYI)){
		parameter.put("since_id", cursor_id);
		// }

		// HTTP Communication
		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_COMMENT_TIMELINE, apiServiceListener,
					parameter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void refreshByMenu() {

		// Clear Data
		data.clear();
		currentList.clear();
		timeLineDataList.clear();
		adapter.notifyDataSetChanged();
		retweetTimelineInfo = null;

		// Set Page
		currentPage = 0;

		// Cancel Notification
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(49);

		isRefreshFromAutoRefresh = false;

		// Refresh
		refresh();

	}

	// --------------------------------------------------------------------------
	/**
	 * Handle the Auto Refresh Function<br>
	 */
	// --------------------------------------------------------------------------
	private class AutoRefreshHandler extends Thread {

		boolean refreshFlag = true;

		boolean autoRefreshLock = false;

		// --------------------------------------------------------------------------
		/**
		 * Constructor<br>
		 */
		// --------------------------------------------------------------------------
		public AutoRefreshHandler() {
			super("AutoRefreshHandler");
		}

		// --------------------------------------------------------------------------
		/**
		 * Constructor<br>
		 */
		// --------------------------------------------------------------------------
		public void lockAutoRefresh(boolean lock) {
			autoRefreshLock = lock;
		}

		// --------------------------------------------------------------------------
		/**
		 * Stop this Thread.
		 */
		// --------------------------------------------------------------------------
		public void stopAutoRefresh() {
			refreshFlag = false;
		}

		// --------------------------------------------------------------------------
		/**
		 * Run
		 */
		// --------------------------------------------------------------------------
		public void run() {

			while (refreshFlag) {

				// Get Refresh Time
				Long refreshTime = Long.valueOf(settingData.getRefreshTime()) * 60 * 1000;

				// get interval
				long intervalTime = System.currentTimeMillis()
						- lastRefreshTime;

				// Refresh
				if (!autoRefreshLock && intervalTime > refreshTime
						&& currentPage == 1
						&& !NotificationService.isNotifyChecking) {

					// Send Message to Handler
					mHandler.sendEmptyMessage(0);

				}

				try {
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void setNewestMessageId(int type,
			ArrayList<TimeLineInfo> timeLineInfos) {

		long newestMessageId = 0;

		// for (TimeLineInfo timeLineInfo : timeLineInfos) {
		for (int i = 0; i < timeLineInfos.size(); i++) {
			TimeLineInfo timeLineInfo = timeLineInfos.get(i);
			// sina ==>getinReplyToStatusId cfb/sohu/wangyi==>getMessageId
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)) {
				if (Long.valueOf(timeLineInfo.getinReplyToStatusId()) > newestMessageId) {
					newestMessageId = Long.valueOf(timeLineInfo
							.getinReplyToStatusId());
				}

			} else {
				if (Long.valueOf(timeLineInfo.getMessageId()) > newestMessageId) {
					newestMessageId = Long.valueOf(timeLineInfo.getMessageId());
				}

			}
		}

		switch (type) {
		case CommHandler.TYPE_GET_COMMENT_TIMELINE: {

			statusData.setNewestCommentId(String.valueOf(newestMessageId));
			accountData.setLastCfbCommentId(String.valueOf(newestMessageId));
			break;
		}
		}

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		AccountList accountList = crowdroidApplication.getAccountList();
		accountList.refreshAccount(accountData);

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
