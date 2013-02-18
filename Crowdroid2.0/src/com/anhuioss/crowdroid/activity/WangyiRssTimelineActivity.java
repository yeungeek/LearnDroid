package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
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

import com.anhuioss.crowdroid.AtMessageTimelineActivity;
import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CommentTimelineActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.DirectMessageReceiveActivity;
import com.anhuioss.crowdroid.DirectMessageSendActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.MyTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.RetweetOfMeActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.communication.DownloadServiceInterface;
import com.anhuioss.crowdroid.communication.DownloadServiceListener;
import com.anhuioss.crowdroid.communication.TranslationService;
import com.anhuioss.crowdroid.communication.TranslationServiceInterface;
import com.anhuioss.crowdroid.communication.TranslationServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.KeywordFilterData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.TranslationData;
import com.anhuioss.crowdroid.data.UserFilterData;
import com.anhuioss.crowdroid.data.info.BasicInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.operations.ScheduleMonthActivity;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.TranslationHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.service.wangyi.WangyiCommHandler;
import com.anhuioss.crowdroid.service.wangyi.WangyiParserHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.operations.DetailBlogActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

public class WangyiRssTimelineActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	public static final String DOWNLOAD_SERVICE_NAME = ".communication.DownloadService";

	private int size = 0;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ListView listView = null;

	private LinearLayout linearTab = null;

	private RelativeLayout relativeRight = null;

	private RelativeLayout layout_bottom = null;

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
	// right
	private Button btnMention = null;

	private Button btnMyTimeline = null;

	private Button btnDirect = null;

	private Button btnComment = null;

	private Button btnRetweetOfMe = null;

	private Button btnSchedule = null;

	// Tab

	private Button btnDMSend = null;

	private Button btnDMReceived = null;

	private SimpleAdapter adapter;

	protected ArrayList<TimeLineInfo> timeLineDataList;

	protected ArrayList<TimeLineInfo> currentList;

	private int commType;

	/** Image Map for user profile */
	public static HashMap<String, Bitmap> userImageMap = new HashMap<String, Bitmap>();

	private int currentPage = 1;

	private StatusData statusData;

	private AccountData accountData;

	SettingData settingData;

	private CrowdroidApplication crowdroidApplication;

	/** Auto Refresh Timer */
	// AutoRefreshHandler autoRefresh;

	/** Refresh Back */
	boolean refreshBack = false;

	/** Auto Refresh Flag */
	boolean autoRefreshFlag = false;

	private long lastRefreshTime;

	// Translation Engine
	private String translateEngine;

	private String service;

	private MyImageBinder myImageBinder;

	private String imageShow;

	private String headUrl;

	private String userName;

	private String uid;

	private String psw;

	private String cursor_id;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private static boolean isRefreshFromAutoRefresh = false;

	ProgressDialog pd = null;

	private static CommResult result;

	private String rss_id;
	private String title;

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				refresh();
			} else {

				// if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND
				// || commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
				// openSelectDialog(position);
				// } else {
				Intent detail = new Intent(WangyiRssTimelineActivity.this,
						DetailTweetActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("commtype", commType);
				bundle.putSerializable("timelineinfo",
						timeLineDataList.get(position));
				bundle.putSerializable("timelinedatalist", timeLineDataList);
				detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				detail.putExtras(bundle);
				startActivity(detail);
				// }
				//
			}
		}
	};
	//
	// private DownloadServiceInterface downloadServiceInterface;
	//
	// private ApiServiceInterface apiServiceInterface;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// Refresh
				autoRefresh();
				break;
			}
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

	Timer timer = new Timer();
	TimerTask timeTask = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
		}
	};

	private class AutoRefreshTimeTask extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
		}

	}

	// private TranslationServiceInterface translationServiceInterface;
	//
	// private TranslationServiceListener.Stub translationServiceListener = new
	// TranslationServiceListener.Stub() {
	//
	// @Override
	// public void requestCompleted(String engine, long type,
	// String statusCode, String message) throws RemoteException {
	//
	// if (statusCode != null && statusCode.equals("200")) {
	//
	// // Translation Size
	// size = size - 1;
	//
	// for (TimeLineInfo timeLineInfo : currentList) {
	// if (timeLineInfo.getMessageId()
	// .equals(String.valueOf(type))) {
	// timeLineInfo.setStatus(message);
	// }
	// }
	//
	// if (size == 0) {
	//
	// listView.setClickable(true);
	// closeProgressDialog();
	//
	// createListView(currentList);
	//
	// addItemForMoreTweets();
	// }
	// }
	// }
	// };
	//
	// private ApiServiceListener.Stub apiServiceListener = new
	// ApiServiceListener.Stub() {
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public void requestCompleted(String service, int type,
	// String statusCode, String message) throws RemoteException {
	//
	// listView.setClickable(true);
	// // setProgressBarIndeterminateVisibility(false);
	// closeProgressDialog();
	// if (statusCode != null && statusCode.equals("200")
	// && message != null && !message.equals("[]")) {
	//
	// // Parser
	// ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
	// ParseHandler parseHandler = new ParseHandler();
	// timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
	// .parser(service, type, statusCode, message);
	//
	// if (timelineInfoList != null && timelineInfoList.size() > 0) {
	//
	// statusFilter(timelineInfoList);
	// if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
	// if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE
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
	//
	// createListView(timelineInfoList);
	// if (timelineInfoList.size() >=
	// 20&&!(service.equals(IGeneral.SERVICE_NAME_WANGYI)
	// &&(commType==CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE)
	// || commType==CommHandler.TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE)) {
	// addItemForMoreTweets();
	// }
	// if(!timelineInfoList.get(timelineInfoList.size()-1).equals("")){
	// cursor_id=timelineInfoList.get(timelineInfoList.size()-1).getcursor_id();
	// }
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
	// Toast.makeText(WangyiRssTimelineActivity.this,
	// getString(R.string.permission), Toast.LENGTH_SHORT)
	// .show();
	// }
	// }
	// if (!"200".equals(statusCode)) {
	// Toast.makeText(
	// WangyiRssTimelineActivity.this,
	// ErrorMessage.getErrorMessage(WangyiRssTimelineActivity.this,
	// statusCode), Toast.LENGTH_SHORT).show();
	// }
	// }

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

		timeLineDataList = new ArrayList<TimeLineInfo>();

		currentList = new ArrayList<TimeLineInfo>();

		setLayoutResId(R.layout.timeline_layout);

		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		// bottom
		btnHome = (Button) findViewById(R.id.tools_bottom_home);
		btnNewTweet = (Button) findViewById(R.id.tools_bottom_new);
		btnDiscover = (Button) findViewById(R.id.tools_bottom_app);
		btnProfile = (Button) findViewById(R.id.tools_bottom_profile);
		btnMore = (Button) findViewById(R.id.tools_bottom_more);
		// right
		btnMention = (Button) findViewById(R.id.main_at);
		btnMyTimeline = (Button) findViewById(R.id.main_my_timeline);
		btnDirect = (Button) findViewById(R.id.main_direct);
		btnComment = (Button) findViewById(R.id.main_comment);
		btnRetweetOfMe = (Button) findViewById(R.id.main_retweet_by_me);
		btnSchedule = (Button) findViewById(R.id.main_schedule);
		// dm
		btnDMSend = (Button) findViewById(R.id.direct_message_send);
		btnDMReceived = (Button) findViewById(R.id.direct_message_received);
		linearTab = (LinearLayout) findViewById(R.id.linear_layout_tab);
		relativeRight = (RelativeLayout) findViewById(R.id.relativeLayout_right);
		layout_bottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);

		btnHome.setOnClickListener(this);
		btnNewTweet.setOnClickListener(this);
		btnDiscover.setOnClickListener(this);
		btnProfile.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);
		btnComment.setOnClickListener(this);
		btnRetweetOfMe.setOnClickListener(this);
		btnDirect.setOnClickListener(this);
		btnMyTimeline.setOnClickListener(this);
		btnMention.setOnClickListener(this);
		btnDMSend.setOnClickListener(this);
		btnDMReceived.setOnClickListener(this);
		btnSchedule.setOnClickListener(this);

		Bundle bundle = this.getIntent().getExtras();
		rss_id = bundle.getString("rss_id");
		title = bundle.getString("title");

		// 网易翻页标志参数
		cursor_id = "";

		// =====================================================
		// ListView
		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setDivider(null);
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();

		service = statusData.getCurrentService();
		// Prepare Simple Adapter For List View
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
			adapter = new SimpleAdapter(
					this,
					data,
					R.layout.sina_timeline_layout_list_item,
					new String[] { "screenName", "status", "webStatus",
							"retweetStatus", "userImage", "time",
							"retweetedScreenNameStatus", "verified", "web",
							"webRetweet", "important_level", "retweetCount",
							"commentCount", "moreTweets" },
					new int[] { R.id.sina_screen_name, R.id.sina_status,
							R.id.web_status, R.id.web_retweet_status,
							R.id.sina_user_image, R.id.sina_update_time,
							R.id.retweeted_screen_name_status,
							R.id.sina_user_verified, R.id.web_view_status,
							R.id.web_view_retweet_status,
							R.id.important_level_view, R.id.text_retweet_count,
							R.id.text_comment_count, R.id.text_get_more_tweets });
		} else {
			adapter = new SimpleAdapter(this, data,
					R.layout.basic_timeline_layout_list_item, new String[] {
							"screenName", "status", "userImage", "time", "web",
							"level", "moreTweets" }, new int[] {
							R.id.screen_name, R.id.status, R.id.user_image,
							R.id.update_time, R.id.web_view_status,
							R.id.level_view, R.id.text_get_more_tweets });
		}

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
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		autoRefreshFlag = settingData.isAutoRefresh();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		imageShow = settingData.getSelectionShowImage();

		initTimeLineView();

		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);

		if (autoRefreshFlag && timer == null) {
			// Get Refresh Time
			Long refreshTime = Long.valueOf(settingData.getRefreshTime()) * 60 * 1000;
			timer = new Timer();
			timer.schedule(new AutoRefreshTimeTask(), refreshTime, refreshTime); // 启动timer
		}

		// Bind Api Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		// Bind Service
		Intent intentTranslation = new Intent(this, TranslationService.class);
		bindService(intentTranslation, this, Context.BIND_AUTO_CREATE);

		// =================================================================
		// Bind Download Service
		// Intent intentDownload = new Intent(this, DownloadService.class);
		// bindService(intentDownload, this, Context.BIND_AUTO_CREATE);

		// New Instances Of Refresh Thread
		// autoRefresh = new AutoRefreshHandler();
		// // Start Auto Refresh
		// if (autoRefresh != null && !autoRefresh.isAlive()
		// && autoRefreshFlag) {
		// autoRefresh.start();
		// }

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

		// Stop Auto Refresh
		// if (autoRefresh != null && autoRefresh.isAlive()) {
		// autoRefresh.stopAutoRefresh();
		// try {
		// autoRefresh.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// autoRefresh = null;
		// }
		if (timer != null) {// 停止timer
			timer.cancel();
			timer.purge();
			timer = null;
		}
		refreshBack = true;

		// Unbind Service
		unbindService(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"SHARE_INIT_STATUS", 0);
		boolean isFromBeness = sharedPreferences.getBoolean(
				"WHEATHER_FROM_BENESE", false);
		if (isFromBeness) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("WHEATHER_FROM_BENESE", false);
			editor.commit();
			finish();
		}
		// } else if (commType == CommHandler.TYPE_GET_HOME_TIMELINE
		// && keyCode == KeyEvent.KEYCODE_BACK
		// && event.getRepeatCount() == 0) {
		// confirmLogoutDialog();
		// return true;
		// }

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		if (name.getShortClassName().equals(API_SERVICE_NAME)) {
			//
			// apiServiceInterface =
			// ApiServiceInterface.Stub.asInterface(service);
			//
			// // Cancel Notification
			// NotificationManager notificationManager = (NotificationManager)
			// getSystemService(NOTIFICATION_SERVICE);
			// notificationManager.cancel(1);
			//
			// if (((CrowdroidApplication) getApplicationContext())
			// .isComeFromNotification(0)) {
			// data.clear();
			// timeLineDataList.clear();
			// adapter.notifyDataSetChanged();
			// }
			//
			if (data.isEmpty()) {
				// try {
				//
				// isRefreshFromAutoRefresh = false;
				//
				// lastRefreshTime = System.currentTimeMillis();
				//
				// listView.setClickable(false);
				// // setProgressBarIndeterminateVisibility(true);
				showProgressDialog();
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("rss_id", rss_id);
				result = new CommResult();
				result = WangyiCommHandler.getRssTimeline(parameters);
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				Log.v("web", "web");
				try {
					timelineInfoList = WangyiParserHandler
							.parseTimeline1(result.getMessage());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if(timelineInfoList!=null)

				createListView(timelineInfoList);

				// // Prepare Parameters
				// Map<String, Object> parameters;
				// parameters = new HashMap<String, Object>();
				// parameters.put("page", currentPage);
				// if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_WANGYI)){
				// parameters.put("since_id", cursor_id);
				// }
				// if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_WANGYI)&&commType==CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE){
				// Bundle bundle = this.getIntent().getExtras();
				// parameters.put("type", bundle.getString("type"));
				// }
				// if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_RENREN)
				// && (commType == CommHandler.TYPE_GET_STATUS_TIMELINE ||
				// commType == CommHandler.TYPE_GET_BLOG_TIMELINE)) {
				//
				// Bundle bundle = this.getIntent().getExtras();
				// uid = bundle.getString("uid");
				// headUrl = bundle.getString("head_url");
				// userName = bundle.getString("name");
				// parameters.put("id", uid);
				// }
				// if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_TENCENT)
				// && commType == CommHandler.TYPE_GET_AREA_TIMELINE) {
				// // Tencent area timeline
				// parameters.put("stateCode", getIntent().getExtras()
				// .get("stateCode"));
				// parameters.put("cityCode",
				// getIntent().getExtras().get("cityCode"));
				// }
				// parameters.put("uid", statusData.getCurrentUid());
				// if (accountData != null) {
				// parameters.put("screen_name",
				// accountData.getUserScreenName());
				// }
				// // Request
				// apiServiceInterface.request(statusData.getCurrentService(),
				// commType, apiServiceListener, parameters);
				// } catch (RemoteException e) {
				// e.printStackTrace();
				// }
			}
			//
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
			//
			// } else if
			// (name.getShortClassName().equals(DOWNLOAD_SERVICE_NAME)) {
			// downloadServiceInterface = DownloadServiceInterface.Stub
			// .asInterface(service);
			// } else {
			// translationServiceInterface = TranslationServiceInterface.Stub
			// .asInterface(service);
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		// apiServiceInterface = null;
		// downloadServiceInterface = null;
		// translationServiceInterface = null;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);

		// if (hasFocus) {
		//
		// autoRefresh.lockAutoRefresh(false);
		//
		// // Start Auto Refresh
		// if (refreshBack && !autoRefresh.isAlive()
		// && autoRefreshFlag) {
		// autoRefresh.start();
		// }
		//
		// } else {
		//
		// autoRefresh.lockAutoRefresh(true);
		// }

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tools_bottom_home: {
			if (commType != CommHandler.TYPE_GET_HOME_TIMELINE) {
				Intent comment = new Intent(WangyiRssTimelineActivity.this,
						HomeTimelineActivity.class);
				comment.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(comment);
			}
			break;
		}
		case R.id.tools_bottom_new: {
			Intent tweet = new Intent(WangyiRssTimelineActivity.this,
					SendMessageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("action", "timeline");
			bundle.putString("target", "");
			tweet.putExtras(bundle);
			tweet.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(tweet);
			break;
		}
		case R.id.tools_bottom_app: {
			Intent app = null;
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				app = new Intent(WangyiRssTimelineActivity.this,
						SNSDiscoveryActivity.class);
			} else {
				app = new Intent(WangyiRssTimelineActivity.this,
						DiscoveryActivity.class);
			}
			app.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			startActivity(app);
			break;
		}
		case R.id.tools_bottom_profile: {
			Intent intent = new Intent(WangyiRssTimelineActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", "");
			bundle.putString("user_name", "");
			bundle.putString("uid", "");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			startActivity(intent);
			break;
		}
		case R.id.tools_bottom_more: {
			Intent more = new Intent(WangyiRssTimelineActivity.this,
					MoreFunctionActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", accountData.getUserScreenName());
			more.putExtras(bundle);
			more.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(more);
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
			Intent at = new Intent(WangyiRssTimelineActivity.this,
					AtMessageTimelineActivity.class);
			at.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(at);
			break;
		}
		case R.id.main_my_timeline: {
			Intent my = new Intent(WangyiRssTimelineActivity.this,
					MyTimelineActivity.class);
			my.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(my);
			break;
		}
		case R.id.main_direct: {
			Intent direct = new Intent(WangyiRssTimelineActivity.this,
					DirectMessageReceiveActivity.class);
			direct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(direct);
			break;
		}
		case R.id.main_comment: {
			Intent comment = new Intent(WangyiRssTimelineActivity.this,
					CommentTimelineActivity.class);
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(comment);
			break;
		}
		case R.id.main_retweet_by_me: {
			Intent intent = new Intent(WangyiRssTimelineActivity.this,
					RetweetOfMeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		case R.id.direct_message_send: {
			btnDMSend.setTextColor(Color.WHITE);
			btnDMReceived.setTextColor(Color.BLACK);

			Intent direct = new Intent(WangyiRssTimelineActivity.this,
					DirectMessageSendActivity.class);
			direct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(direct);
			break;
		}
		case R.id.direct_message_received: {
			btnDMSend.setTextColor(Color.BLACK);
			btnDMReceived.setTextColor(Color.WHITE);
			Intent receive = new Intent(WangyiRssTimelineActivity.this,
					DirectMessageReceiveActivity.class);
			receive.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(receive);
			break;
		}
		case R.id.main_schedule: {
			Intent intent = new Intent(WangyiRssTimelineActivity.this,
					ScheduleMonthActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}

		// back
		case R.id.head_back: {

			finish();

			break;
		}
		}
	}

	private void initTimeLineView() {

		headName.setText(title);

		// blog init
		if (service.equals(IGeneral.SERVICE_NAME_RENREN)
				&& commType == CommHandler.TYPE_GET_BLOG_TIMELINE) {
			linearTab.setVisibility(View.GONE);
			relativeRight.setVisibility(View.GONE);
			layout_bottom.setVisibility(View.GONE);
			headName.setText(R.string.blog_list);
		}

		// DM init
		if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
			linearTab.setVisibility(View.VISIBLE);
			btnDMReceived.setTextColor(Color.WHITE);
		} else if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND) {
			linearTab.setVisibility(View.VISIBLE);
			btnDMSend.setTextColor(Color.WHITE);
		} else {
			linearTab.setVisibility(View.GONE);
		}
		// Twitter init
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
			btnComment.setVisibility(View.GONE);
			btnRetweetOfMe.setVisibility(View.VISIBLE);
		}
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			btnComment.setVisibility(View.GONE);
		}
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			btnSchedule.setVisibility(View.VISIBLE);
			btnSchedule.setBackgroundResource(R.drawable.right_schedule);
		}
	}

	@Override
	protected void refreshByMenu() {

		// Clear Data
		data.clear();
		currentList.clear();
		timeLineDataList.clear();
		adapter.notifyDataSetChanged();

		// Set Page
		currentPage = 0;
		cursor_id = "";

		// Cancel Notification
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(1);

		isRefreshFromAutoRefresh = false;

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

		lastRefreshTime = System.currentTimeMillis();

		currentPage = currentPage + 1;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("page", currentPage);
		parameters.put("uid", statusData.getCurrentUid());
		if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
			parameters.put("since_id", cursor_id);
		}

		if (service.equals(IGeneral.SERVICE_NAME_RENREN)
				&& (commType == CommHandler.TYPE_GET_STATUS_TIMELINE || commType == CommHandler.TYPE_GET_BLOG_TIMELINE)) {
			parameters.put("id", getIntent().getExtras().getString("uid"));
		}
		// if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
		// parameters.put("cursor_id",
		// getIntent().getExtras().getString("cursor_id"));
		// }

		String pageTime = "0";
		if (currentPage != 1
				&& IGeneral.SERVICE_NAME_TENCENT.equals(statusData
						.getCurrentService())) {
			pageTime = data.get(data.size() - 1).get("pageTime").toString();
			if (commType == CommHandler.TYPE_GET_AREA_TIMELINE) {
				// Tencent area timeline
				parameters.put("stateCode",
						getIntent().getExtras().get("stateCode"));
				parameters.put("cityCode",
						getIntent().getExtras().get("cityCode"));
				parameters.put("pos", currentList.get(0).getPosition());
			}
		}
		parameters.put("rss_id", rss_id);
		result = new CommResult();
		result = WangyiCommHandler.getRssTimeline(parameters);
		ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
		try {
			timelineInfoList = WangyiParserHandler.parseTimeline1(result
					.getMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// if(timelineInfoList!=null)

		createListView(timelineInfoList);
		// try {
		// // Request
		// apiServiceInterface.request(statusData.getCurrentService(),
		// commType, apiServiceListener, parameters);
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(ArrayList<TimeLineInfo> timelineInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();
		BasicInfo.setNetTime();
		// Prepare ArrayList
		for (TimeLineInfo timelineInfo : timelineInfoList) {

			timeLineDataList.add(timelineInfo);

			Map<String, Object> map;
			map = new HashMap<String, Object>();

			// if(commType == CommHandler.TYPE_GET_AT_MESSAGE &&
			// timelineInfo.getRetweetUserInfo() != null){
			// map.put("status",getString(R.string.twitter_alert_retweet_of_me));
			// }

			if (timelineInfo.getRetweetUserInfo() == null) {
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					map.put("screenName", timelineInfo.getUserInfo()
							.getScreenName() == null ? "" : timelineInfo
							.getUserInfo().getUserName()
							+ " "
							+ "@"
							+ timelineInfo.getUserInfo().getScreenName());
				} else {
					if ((service.equals(IGeneral.SERVICE_NAME_RENREN) && commType == CommHandler.TYPE_GET_STATUS_TIMELINE)
							|| (service.equals(IGeneral.SERVICE_NAME_RENREN) && commType == CommHandler.TYPE_GET_BLOG_TIMELINE)) {
						map.put("screenName", userName);
					} else {
						map.put("screenName", timelineInfo.getUserInfo()
								.getScreenName() == null ? "" : timelineInfo
								.getUserInfo().getScreenName());
					}
				}
			} else {
				if (commType == CommHandler.TYPE_GET_AT_MESSAGE) {
					map.put("screenName", timelineInfo.getRetweetUserInfo()
							.getScreenName()
							+ " "
							+ getString(R.string.twitter_alert_retweet_of_me));
				} else {
					map.put("screenName", timelineInfo.getRetweetUserInfo()
							.getScreenName() == null ? "" : timelineInfo
							.getUserInfo().getUserName()
							+ " "
							+ "@"
							+ timelineInfo.getUserInfo().getScreenName()
							+ " "
							+ " [QT by "
							+ timelineInfo.getRetweetUserInfo().getScreenName()
							+ "]");
				}

			}

			String statusImages = timelineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			String status = TagAnalysis.clearImageUrls(
					timelineInfo.getStatus(), statusImages);
			map.put("status", status);
			map.put("webStatus", status);
			if (imageShow.equals(BrowseModeActivity.select[0])
					|| imageShow.equals(BrowseModeActivity.select[1])) {

				if (service.equals(IGeneral.SERVICE_NAME_RENREN)
						&& (commType == CommHandler.TYPE_GET_STATUS_TIMELINE || commType == CommHandler.TYPE_GET_BLOG_TIMELINE)) {
					map.put("userImage", headUrl);
				} else if (commType == CommHandler.TYPE_GET_AT_MESSAGE
						&& timelineInfo.getRetweetUserInfo() != null) {
					map.put("userImage", timelineInfo.getRetweetUserInfo()
							.getUserImageURL());
				} else {
					// Put Download Image
					map.put("userImage", timelineInfo.getUserInfo()
							.getUserImageURL());
				}
			} else {
				// Put Default Image
				map.put("userImage",
						String.valueOf(R.drawable.default_user_image));
			}

			map.put("time", timelineInfo.getFormatTime(
					statusData.getCurrentService(), this));
			if (service.equals(IGeneral.SERVICE_NAME_SINA)
					|| service
							.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
					|| service.equals(IGeneral.SERVICE_NAME_TENCENT)
					|| service.equals(IGeneral.SERVICE_NAME_SOHU)
					|| service.equals(IGeneral.SERVICE_NAME_RENREN)
					|| service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
				if (timelineInfo.isRetweeted()) {
					// retweetLayout.setVisibility(View.VISIBLE);
					myImageBinder.setRetweeted(true);
					myImageBinder.setService(service);
					// map.put("retweetedScreenName",
					// timelineInfo.getUserInfo().getRetweetedScreenName());
					String text = "";
					if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
						text = timelineInfo.getRetweetedStatus();
					} else {
						text = "@"
								+ timelineInfo.getUserInfo()
										.getRetweetedScreenName() + ":\n"
								+ timelineInfo.getRetweetedStatus();
					}

					String retweet = TagAnalysis
							.clearImageUrls(
									text,
									timelineInfo
											.getImageInformationForWebView(
													this,
													TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET));
					map.put("retweetedScreenNameStatus", retweet);
					map.put("retweetStatus", retweet);
				}
				map.put("verified", timelineInfo.getUserInfo().getVerified());
			}

			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)
					|| service.equals(IGeneral.SERVICE_NAME_SINA)
					|| service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
				map.put("pageTime", timelineInfo.getTimeStamp());

				map.put("retweetCount", getString(R.string.retweet_count) + "("
						+ timelineInfo.getRetweetCount() + ")");
				map.put("commentCount", getString(R.string.comment_count) + "("
						+ timelineInfo.getCommentCount() + ")");
			}

			if (imageShow.equals(BrowseModeActivity.select[0])) {
				String str = timelineInfo.getImageInformationForWebView(this,
						TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
				String strRet = timelineInfo.getImageInformationForWebView(
						this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET);

				map.put("web", str);
				map.put("webRetweet", strRet);
			} else {
				map.put("web", "");
				map.put("webRetweet", "");
			}

			addDatas.add(map);
			Log.v("createlist", "list");

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();
		closeProgressDialog();

	}

	public void addItemForMoreTweets() {

		// Check For CFB's Direct Message
		// if (statusData.getCurrentService() != null
		// && statusData.getCurrentService().equals(
		// IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
		// && (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE ||
		// commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND)) {
		// return;
		// }

		if (data.size() > 0
				&& !data.get(data.size() - 1).get("screenName").equals("")) {
			try {
				Map<String, Object> map;
				map = new HashMap<String, Object>();
				map.put("screenName", "");
				// String moreTweets =
				// getResources().getString(R.string.get_more_tweets);
				// String timeData = "";
				// if(IGeneral.SERVICE_NAME_SINA.equals(statusData.getCurrentService())
				// ||
				// IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData.getCurrentService())
				// ||
				// IGeneral.SERVICE_NAME_SOHU.equals(statusData.getCurrentService())
				// ||
				// IGeneral.SERVICE_NAME_TENCENT.equals(statusData.getCurrentService()))
				// {
				// textData = "";
				// timeData =
				// getResources().getString(R.string.get_more_tweets);
				// }
				map.put("moreTweets",
						getResources().getString(R.string.get_more_tweets));

				// map.put("status", textData);
				// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				// android.R.drawable.ic_menu_more);
				// map.put("userImage",
				// String.valueOf(android.R.drawable.ic_menu_more));
				// map.put("time", timeData);
				// map.put("web", "");
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

	// -----------------------------------------------------------------------------
	/**
	 * Load userImage.
	 */
	// -----------------------------------------------------------------------------
	// private void loadUserImage(ArrayList<TimeLineInfo> timelineInfoList) {
	//
	// if (timelineInfoList == null) {
	// return;
	// }
	//
	// // Garbage Collection
	// System.gc();
	//
	// for (TimeLineInfo info : timelineInfoList) {
	//
	// // Get UserInfo
	// UserInfo userInfo = info.getUserInfo();
	//
	// // Set Image To Info
	// userInfo.setUserImage(userImageMap.get(userInfo.getUid()));
	//
	// }
	//
	// // Garbage Collection
	// System.gc();
	//
	// }
	//
	// private void downloadImage(ArrayList<TimeLineInfo> timelineInfoList) {
	//
	// listView.setClickable(false);
	// // setProgressBarIndeterminateVisibility(true);
	// showProgressDialog();
	// // The List For Download
	// ArrayList<UserInfo> downloadUserList = new ArrayList<UserInfo>();
	//
	// // Prepare List For Download
	// for (TimeLineInfo timelineInfo : timelineInfoList) {
	// if (!userImageMap.containsKey(timelineInfo.getUserInfo().getUid())
	// && timelineInfo.getUserInfo().getUid() != null) {
	// downloadUserList.add(timelineInfo.getUserInfo());
	// }
	// }
	//
	// size = downloadUserList.size();
	//
	// if (size == 0) {
	//
	// // Start Autorefresh
	// // if (autoRefresh != null && !autoRefresh.isAlive()
	// // && autoRefreshFlag) {
	// // autoRefresh.start();
	// // }
	//
	// listView.setClickable(true);
	// // setProgressBarIndeterminateVisibility(false);
	// closeProgressDialog();
	// // User Image
	// loadUserImage(timelineInfoList);
	//
	// // Auto Translation
	// autoTranslate(currentList);
	//
	// }
	//
	// // Download
	// for (UserInfo userInfo : downloadUserList) {
	// try {
	//
	// downloadServiceInterface.request(userInfo.getUid(),
	// userInfo.getUserImageURL(), downloadServiceListener);
	//
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /**
	// * Keyword And User Filter
	// *
	// * @param timelineInfoList
	// * @return null
	// */
	// private void statusFilter(ArrayList<TimeLineInfo> timelineInfoList) {
	//
	// // Prepare Data
	// CrowdroidApplication crowdroidApplication = (CrowdroidApplication)
	// getApplicationContext();
	// ArrayList<KeywordFilterData> keywordList = crowdroidApplication
	// .getKeywordFilterList().getAllKeywords();
	// ArrayList<UserFilterData> userNameList = crowdroidApplication
	// .getUserFilterList().getUserFilter(
	// statusData.getCurrentService());
	//
	// // Filter
	// for (TimeLineInfo timeLineInfo : timelineInfoList) {
	//
	// // Keyword Filter
	// for (KeywordFilterData key : keywordList) {
	// if (timeLineInfo.getStatus().contains(key.getKeyword())) {
	// timeLineInfo.setStatus("**********");
	// }
	// }
	//
	// // User Filter
	// for (UserFilterData userName : userNameList) {
	// if (timeLineInfo.getUserInfo().getScreenName()
	// .equals(userName.getUserName())) {
	// timeLineInfo.setStatus("**********");
	// }
	// }
	//
	// }
	//
	// }
	//
	// --------------------------------------------------------------------------
	/**
	 * Delete Timeline Information By Message Id<br>
	 */
	// --------------------------------------------------------------------------
	public void deleteItem(String messageId) {

		// Prepare Data
		ArrayList<TimeLineInfo> timeLineInfos = new ArrayList<TimeLineInfo>();
		for (TimeLineInfo timeLineInfo : timeLineDataList) {
			if (messageId != null
					&& !messageId.equals(timeLineInfo.getMessageId())) {
				timeLineInfos.add(timeLineInfo);
			}
		}

		// Clear
		timeLineDataList.clear();
		data.clear();

		// Create List View
		createListView(timeLineInfos);

		// Add More Tweets If Needs
		addItemForMoreTweets();

	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(WangyiRssTimelineActivity.this);
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