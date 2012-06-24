package com.anhuioss.crowdroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.activity.DetailTweetActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.KeywordSearchActivity;
import com.anhuioss.crowdroid.activity.MoreFunctionActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

public class BasicSearchActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private ArrayAdapter<String> cfb_adapter_history;

	private ArrayAdapter<String> sina_adapter_history;

	private ArrayAdapter<String> sohu_adapter_history;

	private ArrayAdapter<String> tencent_adapter_history;

	private ArrayAdapter<String> twitter_adapter_history;

	private ArrayAdapter<String> twitter_proxy_adapter_history;

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

	// content
	private ListView listView = null;

	private AutoCompleteTextView searchText = null;

	private ImageButton searchButton = null;

	private ImageButton nextPageButton = null;

	private ApiServiceInterface apiServiceInterface;

	private StatusData statusData;

	private SettingData settingData;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	protected ArrayList<TimeLineInfo> timeLineDataList;

	protected ArrayList<TimeLineInfo> currentList;

	private static boolean isRunning = true;

	private SimpleAdapter adapter;

	private MyImageBinder myImageBinder;

	private int commType;

	// search
	private String searchKeyword;

	private String screenName;

	private String userName;

	private String uid;

	// refresh
	/** Auto Refresh Timer */
	AutoRefreshHandler autoRefresh;

	/** Refresh Back */
	boolean refreshBack = false;

	/** Auto Refresh Flag */
	boolean autoRefreshFlag = false;

	private long lastRefreshTime;

	private int i = 0;

	private int currentPage = 0;

	private int size = 0;

	private String imageShow;

	private HandleProgressDialog progress;

	protected void setCommType(int commType) {

		this.commType = commType;

	}

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
			// isRefreshFromAutoRefresh = true;
			// Refresh
			if (commType == CommHandler.TYPE_GET_USER_STATUS_LIST) {
				userTimelineRefresh();
			} else if (commType == CommHandler.TYPE_SEARCH_INFO) {
				keywordSearchRefresh();
			} else if (commType == CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID) {
				retweetedListRefresh();
			} else if (commType == CommHandler.TYPE_GET_GROUP_TIMELINE) {
				groupTimelineRefresh();
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
				if (commType == CommHandler.TYPE_GET_USER_STATUS_LIST) {
					userTimelineRefresh();
				} else if (commType == CommHandler.TYPE_SEARCH_INFO) {
					keywordSearchRefresh();
				} else if (commType == CommHandler.TYPE_GET_GROUP_TIMELINE) {
					groupTimelineRefresh();
				}

			} else {

				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
						&& getIntent().getExtras().containsKey(
								"TencentTrendSearchFlag")) {
					Intent intent = new Intent(BasicSearchActivity.this,
							KeywordSearchActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("keyword", timeLineDataList.get(position)
							.getStatus());
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

				} else {
					Intent detail = new Intent(BasicSearchActivity.this,
							DetailTweetActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("commtype", commType);
					bundle.putSerializable("timelineinfo",
							timeLineDataList.get(position));
					bundle.putSerializable("timelinedatalist", timeLineDataList);
					detail.putExtras(bundle);
					startActivity(detail);
				}

			}

		}

	};

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")
					&& !"[]".equals(message)) {

				// Parser
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);
				if (timelineInfoList.size() <= 0
						&& commType == CommHandler.TYPE_SEARCH_INFO) {
					Toast.makeText(BasicSearchActivity.this,
							getString(R.string.keyword_tip), Toast.LENGTH_LONG)
							.show();
				} else {
					currentList = timelineInfoList;

					createListView(timelineInfoList);

				}

				if (timelineInfoList.size() >= 20
						&& commType != CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID) {
					addItemForMoreTweets();
				}
			} else if (commType == CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID) {
				Toast.makeText(BasicSearchActivity.this,
						getString(R.string.no_more_tweets), Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(
						BasicSearchActivity.this,
						ErrorMessage.getErrorMessage(BasicSearchActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		// Set Gallery
		setLayoutResId(R.layout.basic_search_layout);

		timeLineDataList = new ArrayList<TimeLineInfo>();
		currentList = new ArrayList<TimeLineInfo>();
		// head
		btnHeaderBack = (Button) findViewById(R.id.head_back);
		btnHeaderRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		// bottom
		btnHome = (Button) findViewById(R.id.tools_bottom_home);
		btnNewTweet = (Button) findViewById(R.id.tools_bottom_new);
		btnDiscover = (Button) findViewById(R.id.tools_bottom_app);
		btnProfile = (Button) findViewById(R.id.tools_bottom_profile);
		btnMore = (Button) findViewById(R.id.tools_bottom_more);
		// content
		listView = (ListView) findViewById(R.id.list_view);

		searchText = (AutoCompleteTextView) findViewById(R.id.auto_search_text);

		searchText.setVerticalScrollBarEnabled(false);
		searchText.setVerticalFadingEdgeEnabled(false);

		searchButton = (ImageButton) findViewById(R.id.search_button);
		nextPageButton = (ImageButton) findViewById(R.id.next_page_button);

		// Set Click Listener
		btnHome.setOnClickListener(this);
		btnNewTweet.setOnClickListener(this);
		btnDiscover.setOnClickListener(this);
		btnProfile.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		btnHeaderBack.setOnClickListener(this);
		btnHeaderRefresh.setOnClickListener(this);
		searchButton.setOnClickListener(this);

		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);
		listView.setDivider(null);
		// Prepare Simple Adapter For List View
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {

			adapter = new SimpleAdapter(
					this,
					data,
					R.layout.sina_basic_timeline_layout_list_item,
					new String[] { "screenName", "status", "userImage", "time",
							"retweetedScreenNameStatus", "verified", "web",
							"webRetweet", "webStatus", "retweetStatus",
							"retweetCount", "commentCount", "moreTweets" },
					new int[] { R.id.screen_name, R.id.status, R.id.user_image,
							R.id.update_time,
							R.id.retweeted_screen_name_status,
							R.id.sina_user_verified, R.id.web_view_status,
							R.id.web_view_retweet_status, R.id.web_status,
							R.id.web_retweet_status, R.id.text_retweet_count,
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

	@Override
	public void onStart() {
		super.onStart();

		isRunning = true;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();

		imageShow = settingData.getSelectionShowImage();
		autoRefreshFlag = settingData.isAutoRefresh();
		autoRefresh = new AutoRefreshHandler();
		if (autoRefresh != null && !autoRefresh.isAlive() && autoRefreshFlag) {
			autoRefresh.start();
		}

		searchKeyword = getIntent().getExtras().getString("keyword");
		screenName = getIntent().getStringExtra("name");
		userName = getIntent().getStringExtra("username");
		uid = getIntent().getStringExtra("uid");

		initSearchView();

		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);

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
	public void onStop() {
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
		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tools_bottom_home: {
			Intent comment = new Intent(BasicSearchActivity.this,
					HomeTimelineActivity.class);
			comment.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(comment);
			break;
		}
		case R.id.tools_bottom_new: {
			Intent tweet = new Intent(BasicSearchActivity.this,
					SendMessageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("action", "timeline");
			bundle.putString("target", "");
			tweet.putExtras(bundle);
			tweet.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(tweet);
			break;
		}
		case R.id.tools_bottom_app: {
			Intent app = null;
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				app = new Intent(BasicSearchActivity.this,
						SNSDiscoveryActivity.class);
			} else {
				app = new Intent(BasicSearchActivity.this,
						DiscoveryActivity.class);
			}
			app.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(app);
			break;
		}
		case R.id.tools_bottom_profile: {
			Intent intent = new Intent(BasicSearchActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", "");
			bundle.putString("user_name", "");
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.tools_bottom_more: {
			Intent more = new Intent(BasicSearchActivity.this,
					MoreFunctionActivity.class);
			more.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(more);
			break;
		}
		case R.id.head_refresh: {
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
			// isRefreshFromAutoRefresh = false;
			// Refresh
			if (commType == CommHandler.TYPE_GET_USER_STATUS_LIST) {
				userTimelineRefresh();
			} else if (commType == CommHandler.TYPE_SEARCH_INFO) {
				keywordSearchRefresh();
			} else if (commType == CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID) {
				retweetedListRefresh();
			} else if (commType == CommHandler.TYPE_GET_GROUP_TIMELINE) {
				groupTimelineRefresh();
			}
			break;
		}
		case R.id.search_button: {
			currentPage = 0;
			data.clear();
			timeLineDataList.clear();
			if (commType == CommHandler.TYPE_GET_USER_STATUS_LIST) {
				userTimelineRefresh();
			} else if (commType == CommHandler.TYPE_SEARCH_INFO) {

				keywordSearchRefresh();

				String text = searchText.getText().toString();
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {// cfb历史记录
					SharedPreferences sp = getSharedPreferences(
							"cfb_history_strs", 0);
					String save_Str = sp.getString("cfb_history", "");
					String[] hisArrays = save_Str.split(",");
					for (int i = 0; i < hisArrays.length; i++) {
						if (hisArrays[i].equals(text)) {
							return;
						}
					}
					StringBuilder sb = new StringBuilder(save_Str);
					sb.append(text + ",");
					sp.edit().putString("cfb_history", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals( // sina历史记录
						IGeneral.SERVICE_NAME_SINA)) {
					SharedPreferences sp = getSharedPreferences(
							"sina_history_strs", 0);
					String save_Str = sp.getString("sina_history", "");
					String[] hisArrays = save_Str.split(",");
					for (int i = 0; i < hisArrays.length; i++) {
						if (hisArrays[i].equals(text)) {
							return;
						}
					}
					StringBuilder sb = new StringBuilder(save_Str);
					sb.append(text + ",");
					sp.edit().putString("sina_history", sb.toString()).commit();

				} else if (statusData.getCurrentService().equals( // sohu历史记录
						IGeneral.SERVICE_NAME_SOHU)) {
					SharedPreferences sp = getSharedPreferences(
							"sohu_history_strs", 0);
					String save_Str = sp.getString("sohu_history", "");
					String[] hisArrays = save_Str.split(",");
					for (int i = 0; i < hisArrays.length; i++) {
						if (hisArrays[i].equals(text)) {
							return;
						}
					}
					StringBuilder sb = new StringBuilder(save_Str);
					sb.append(text + ",");
					sp.edit().putString("sohu_history", sb.toString()).commit();

				} else if (statusData.getCurrentService().equals( // tencent历史记录
						IGeneral.SERVICE_NAME_TENCENT)) {
					SharedPreferences sp = getSharedPreferences(
							"tencent_history_strs", 0);
					String save_Str = sp.getString("tencent_history", "");
					String[] hisArrays = save_Str.split(",");
					for (int i = 0; i < hisArrays.length; i++) {
						if (hisArrays[i].equals(text)) {
							return;
						}
					}
					StringBuilder sb = new StringBuilder(save_Str);
					sb.append(text + ",");
					sp.edit().putString("tencent_history", sb.toString())
							.commit();

				} else if (statusData.getCurrentService().equals( // twitter历史记录
						IGeneral.SERVICE_NAME_TWITTER)) {
					SharedPreferences sp = getSharedPreferences(
							"twitter_history_strs", 0);
					String save_Str = sp.getString("twitter_history", "");
					String[] hisArrays = save_Str.split(",");
					for (int i = 0; i < hisArrays.length; i++) {
						if (hisArrays[i].equals(text)) {
							return;
						}
					}
					StringBuilder sb = new StringBuilder(save_Str);
					sb.append(text + ",");
					sp.edit().putString("twitter_history", sb.toString())
							.commit();

				} else if (statusData.getCurrentService().equals( // twitter-proxy历史记录
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					SharedPreferences sp = getSharedPreferences(
							"twitter_proxy_history_strs", 0);
					String save_Str = sp.getString("twitter_proxy_history", "");
					String[] hisArrays = save_Str.split(",");
					for (int i = 0; i < hisArrays.length; i++) {
						if (hisArrays[i].equals(text)) {
							return;
						}
					}
					StringBuilder sb = new StringBuilder(save_Str);
					sb.append(text + ",");
					sp.edit().putString("twitter_proxy_history", sb.toString())
							.commit();

				}

			}
			break;
		}
		// case R.id.next_page_button: {
		// keywordSearchRefresh();
		// break;
		// }
		case R.id.head_back: {
			finish();
			break;
		}
		default: {

		}
		}
	}

	private void initSearchView() {

		switch (commType) {
		case CommHandler.TYPE_SEARCH_INFO: {
			if (getIntent().getExtras().containsKey("TencentTrendSearchFlag")) {
				headName.setText(R.string.discovery_tencent_trend_search);
			} else {
				headName.setText(R.string.keyword_search);
			}
			// search
			searchText.setText(searchKeyword);
			if (searchKeyword != null && !searchKeyword.equals("")) {
				findViewById(R.id.keyword_search_area).setVisibility(View.GONE);
			}
			break;
		}
		case CommHandler.TYPE_GET_USER_STATUS_LIST: {
			headName.setText(R.string.user_timeline);
			// UserTimeline
			if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
					.getCurrentService())) {
				searchText.setText(userName);
				if (userName != null && !userName.equals("")) {
					findViewById(R.id.keyword_search_area).setVisibility(
							View.GONE);
				}
			} else {
				searchText.setText(screenName);
				if (screenName != null && !screenName.equals("")) {
					findViewById(R.id.keyword_search_area).setVisibility(
							View.GONE);
				}
			}
			break;
		}
		case CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID: {

			headName.setText(R.string.retweeted_list);

			findViewById(R.id.keyword_search_area).setVisibility(View.GONE);
			break;
		}
		case CommHandler.TYPE_GET_GROUP_TIMELINE: {

			headName.setText(R.string.group_list_timeline);

			findViewById(R.id.keyword_search_area).setVisibility(View.GONE);
		}
		default:
			break;
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if (name.getShortClassName().equals(TimelineActivity.API_SERVICE_NAME)) {
			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

			if (!searchText.getText().toString().equals("") && data.isEmpty()) {
				if (commType == CommHandler.TYPE_GET_USER_STATUS_LIST) {
					userTimelineRefresh();
				} else if (commType == CommHandler.TYPE_SEARCH_INFO) {
					keywordSearchRefresh();
				}
			}
			if (data.isEmpty()
					&& commType == CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID) {
				try {

					listView.setClickable(false);
					showProgressDialog();
					// Prepare Parameters
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("retweeted_id", getIntent().getExtras()
							.getString("retweeted_id"));

					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			if (data.isEmpty()
					&& commType == CommHandler.TYPE_GET_GROUP_TIMELINE) {
				try {

					listView.setClickable(false);
					showProgressDialog();
					// Prepare Parameters
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put(
							"slug",
							getIntent().getExtras().getString(
									"group_timeline_slug"));
					parameters.put("ownerName", getIntent().getExtras()
							.getString("group_timeline_owner"));
					parameters.put("groupId", getIntent().getExtras()
							.getString("group_timeline_id"));
					parameters.put("page",
							getIntent().getExtras().getString("1"));
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							commType, apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
		// downloadServiceInterface = null;

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

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {// cfb历史记录
			SharedPreferences sp = getSharedPreferences("cfb_history_strs", 0);
			String save_history = sp.getString("cfb_history", "");
			String[] hisArrays = save_history.split(",");
			cfb_adapter_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				cfb_adapter_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(cfb_adapter_history);
		} else if (statusData.getCurrentService().equals( // sina历史记录
				IGeneral.SERVICE_NAME_SINA)) {
			SharedPreferences sp = getSharedPreferences("sina_history_strs", 0);
			String save_history = sp.getString("sina_history", "");
			String[] hisArrays = save_history.split(",");
			sina_adapter_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				sina_adapter_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(sina_adapter_history);

		} else if (statusData.getCurrentService().equals( // sohu历史记录
				IGeneral.SERVICE_NAME_SOHU)) {
			SharedPreferences sp = getSharedPreferences("sohu_history_strs", 0);
			String save_history = sp.getString("sohu_history", "");
			String[] hisArrays = save_history.split(",");
			sohu_adapter_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				sohu_adapter_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(sohu_adapter_history);

		} else if (statusData.getCurrentService().equals( // tencent历史记录
				IGeneral.SERVICE_NAME_TENCENT)) {
			SharedPreferences sp = getSharedPreferences("tencent_history_strs",
					0);
			String save_history = sp.getString("tencent_history", "");
			String[] hisArrays = save_history.split(",");
			tencent_adapter_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				tencent_adapter_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(tencent_adapter_history);

		} else if (statusData.getCurrentService().equals( // twitter历史记录
				IGeneral.SERVICE_NAME_TWITTER)) {
			SharedPreferences sp = getSharedPreferences("twitter_history_strs",
					0);
			String save_history = sp.getString("twitter_history", "");
			String[] hisArrays = save_history.split(",");
			twitter_adapter_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				twitter_adapter_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(twitter_adapter_history);

		} else if (statusData.getCurrentService().equals( // twitter-proxy历史记录
				IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			SharedPreferences sp = getSharedPreferences(
					"twitter_proxy_history_strs", 0);
			String save_history = sp.getString("twitter_proxy_history", "");
			String[] hisArrays = save_history.split(",");
			twitter_proxy_adapter_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				twitter_proxy_adapter_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(twitter_proxy_adapter_history);

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

		// Cancel Notification
		// NotificationManager notificationManager = (NotificationManager)
		// getSystemService(NOTIFICATION_SERVICE);
		// notificationManager.cancel(1);

		// isRefreshFromAutoRefresh = false;

		// Refresh
		if (commType == CommHandler.TYPE_GET_USER_STATUS_LIST) {
			userTimelineRefresh();
		} else if (commType == CommHandler.TYPE_SEARCH_INFO) {
			keywordSearchRefresh();
		} else if (commType == CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID) {
			retweetedListRefresh();
		} else if (commType == CommHandler.TYPE_GET_GROUP_TIMELINE) {
			groupTimelineRefresh();
		}
	}

	private void userTimelineRefresh() {

		showProgressDialog();
		if (apiServiceInterface == null) {
			return;
		}
		lastRefreshTime = System.currentTimeMillis();

		if (searchText.getText().toString().equals("")
				|| searchText.getText().toString().equals("null")) {
			closeProgressDialog();
			return;
		}

		currentPage = currentPage + 1;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("page", String.valueOf(currentPage));
		parameters
				.put("uid",
						(statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_SINA) || statusData
								.getCurrentService().equals(
										IGeneral.SERVICE_NAME_SOHU)) ? null
								: uid);
		parameters.put("screen_name", searchText.getText().toString());
		parameters.put("user_name", searchText.getText().toString());

		String pageTime = "0";
		if (currentPage != 1
				&& IGeneral.SERVICE_NAME_TENCENT.equals(statusData
						.getCurrentService())) {
			pageTime = data.get(data.size() - 1).get("pageTime").toString();
		}
		parameters.put("pageTime", pageTime);

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_USER_STATUS_LIST, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private void keywordSearchRefresh() {

		if (apiServiceInterface == null
				|| searchText.getText().toString().equals("")) {
			return;
		}

		lastRefreshTime = System.currentTimeMillis();

		showProgressDialog();

		currentPage = currentPage + 1;

		String search = searchText.getText().toString();

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("page", String.valueOf(currentPage));
		parameters.put("search", search);

		String pageTime = "0";
		if (currentPage != 1
				&& IGeneral.SERVICE_NAME_TENCENT.equals(statusData
						.getCurrentService())) {
			pageTime = data.get(data.size() - 1).get("pageTime").toString();
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SOHU)) {
			if (getIntent().getExtras().containsKey("SohuSearchTypeFlag")) {
				parameters.put("SohuSearchTypeFlag", getIntent().getExtras()
						.getString("SohuSearchTypeFlag"));
			}
		}
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			if (getIntent().getExtras().containsKey("TencentTrendSearchFlag")) {
				parameters.put("TencentSearchFlag", getIntent().getExtras()
						.getString("TencentTrendSearchFlag"));

			}

		}
		parameters.put("pageTime", pageTime);

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_SEARCH_INFO, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private void retweetedListRefresh() {
		try {

			listView.setClickable(false);
			showProgressDialog();
			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("retweeted_id",
					getIntent().getExtras().getString("retweeted_id"));

			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID,
					apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void groupTimelineRefresh() {

		showProgressDialog();
		if (apiServiceInterface == null) {
			return;
		}
		lastRefreshTime = System.currentTimeMillis();

		currentPage = currentPage + 1;
		try {
			// Prepare Parameters
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("slug",
					getIntent().getExtras().getString("group_timeline_slug"));
			parameters.put("ownerName",
					getIntent().getExtras().getString("group_timeline_owner"));
			parameters.put("groupId",
					getIntent().getExtras().getString("group_timeline_id"));
			parameters.put("page", currentPage);
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					commType, apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
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
				map.put("screenName", timelineInfo.getUserInfo()
						.getScreenName() == null ? "" : timelineInfo
						.getUserInfo().getScreenName());
			}

			String statusImages = timelineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			String status = "";
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)
					&& getIntent().getExtras().containsKey(
							"TencentTrendSearchFlag")) {
				status = timelineInfo.getStatus();
			} else {
				status = TagAnalysis.clearImageUrls(timelineInfo.getStatus(),
						statusImages);
			}

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
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)) {
				if (timelineInfo.isRetweeted()) {
					// retweetLayout.setVisibility(View.VISIBLE);
					myImageBinder.setRetweeted(true);
					myImageBinder.setService(statusData.getCurrentService());
					// map.put("retweetedScreenName",
					// timelineInfo.getUserInfo().getRetweetedScreenName());
					String text = "@"
							+ timelineInfo.getUserInfo()
									.getRetweetedScreenName() + ":\n"
							+ timelineInfo.getRetweetedStatus();
					String retweet = TagAnalysis
							.clearImageUrls(
									text,
									timelineInfo
											.getImageInformationForWebView(
													this,
													TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET));
					map.put("retweetedScreenNameStatus", retweet);
					map.put("retweetStatus", retweet);
					// map.put("retweetedScreenNameStatus", text);
					// map.put("retweetStatus", text);
				}
				map.put("verified", timelineInfo.getUserInfo().getVerified());
			}

			if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData
					.getCurrentService())) {
				map.put("level",
						getLevelDrawableId(timelineInfo.getImportantLevel()));
				map.put("retweetCount", getString(R.string.retweet_count) + "("
						+ timelineInfo.getRetweetCount() + ")");
				map.put("commentCount", getString(R.string.comment_count) + "("
						+ timelineInfo.getCommentCount() + ")");
			}

			if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
					.getCurrentService())) {
				map.put("pageTime", timelineInfo.getTimeStamp());
				map.put("retweetCount", getString(R.string.retweet_count) + "("
						+ timelineInfo.getRetweetCount() + ")");
				map.put("commentCount", getString(R.string.comment_count) + "("
						+ timelineInfo.getCommentCount() + ")");
			}

			if (IGeneral.SERVICE_NAME_SINA.equals(statusData
					.getCurrentService())
					|| IGeneral.SERVICE_NAME_SOHU.equals(statusData
							.getCurrentService())) {
				map.put("retweetCount", "");
				map.put("commentCount", "");
			}

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

			map.put("time",
					timelineInfo.getFormatTime(statusData.getCurrentService()));
			addDatas.add(map);

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();

	}

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
			userInfo.setUserImage(TimelineActivity.userImageMap.get(userInfo
					.getUid()));

		}

		// Garbage Collection
		System.gc();

	}

	public void addItemForMoreTweets() {

		if (data.size() > 0
				&& !data.get(data.size() - 1).get("screenName").equals("")
				|| data.size() > 0
				&& getIntent().getExtras()
						.containsKey("TencentTrendSearchFlag")) {
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

	private Integer getLevelDrawableId(int level) {

		if (level == 2) {
			return R.drawable.normal;
		}
		if (level == 3) {
			return R.drawable.middle;
		}
		if (level == 4) {
			return R.drawable.high;
		}
		return Integer.valueOf(0);

	}

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
						&& currentPage == 1) {

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
