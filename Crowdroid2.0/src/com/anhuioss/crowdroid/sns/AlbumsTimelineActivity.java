package com.anhuioss.crowdroid.sns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.communication.TranslationService;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.KeywordFilterData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.AddKeywordFilterActivity;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.operations.BrowseAlbumPhotosActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AlbumsTimelineActivity extends BasicActivity implements
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

	protected ArrayList<TimeLineInfo> timeLineDataList;

	protected ArrayList<TimeLineInfo> currentList;

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
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				refresh();
			}
			if (accountData.getUid().equals(
					timeLineDataList.get(position).getUserInfo().getUid())) {
				Intent browse = new Intent(AlbumsTimelineActivity.this,
						BrowseAlbumPhotosActivity.class);
				browse.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				Bundle bundle = new Bundle();
				bundle.putString("uid", timeLineDataList.get(position)
						.getUserInfo().getUid());
				bundle.putString("aid", timeLineDataList.get(position)
						.getStatusId());
				bundle.putString("name", timeLineDataList.get(position)
						.getUserInfo().getScreenName());
				browse.putExtras(bundle);
				startActivity(browse);
			} else {
				if ("-1".equals(timeLineDataList.get(position).getFeedType())) {
					Toast.makeText(AlbumsTimelineActivity.this, "仅用户自己可见",
							Toast.LENGTH_SHORT).show();
				} else if ("99".equals(timeLineDataList.get(position)
						.getFeedType())
						|| "3".equals(timeLineDataList.get(position)
								.getFeedType())
						|| "1".equals(timeLineDataList.get(position)
								.getFeedType())) {
					Intent browse = new Intent(AlbumsTimelineActivity.this,
							BrowseAlbumPhotosActivity.class);
					browse.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("uid", timeLineDataList.get(position)
							.getUserInfo().getUid());
					bundle.putString("aid", timeLineDataList.get(position)
							.getStatusId());
					bundle.putString("visible", timeLineDataList.get(position)
							.getFeedType());
					bundle.putString("name", timeLineDataList.get(position)
							.getUserInfo().getScreenName());
					browse.putExtras(bundle);
					startActivity(browse);
				} else if ("4".equals(timeLineDataList.get(position)
						.getFeedType())) {

					current_position = position;
					LayoutInflater factory = LayoutInflater
							.from(AlbumsTimelineActivity.this);// 提示框
					final View mView = factory.inflate(
							R.layout.dialog_item_edittext_, null);// 这里必须是final的
					final EditText edit = (EditText) mView
							.findViewById(R.id.editText_input);// 获得输入框对象
					edit.setHint(R.string.psword);// 输入框默认值
					new AlertDialog.Builder(AlbumsTimelineActivity.this)
							.setTitle(R.string.password_access)
							// 提示框标题
							.setView(mView)
							.setPositiveButton(
									getString(R.string.confirm),// 提示框的两个按钮
									new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent browse = new Intent(
													AlbumsTimelineActivity.this,
													BrowseAlbumPhotosActivity.class);
											browse.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
											final Bundle bundle = new Bundle();
											// 事件处理
											bundle.putString("password", edit
													.getText().toString());
											bundle.putString(
													"uid",
													timeLineDataList
															.get(current_position)
															.getUserInfo()
															.getUid());
											bundle.putString(
													"aid",
													timeLineDataList.get(
															current_position)
															.getStatusId());
											bundle.putString(
													"visible",
													timeLineDataList.get(
															current_position)
															.getFeedType());
											bundle.putString(
													"name",
													timeLineDataList
															.get(current_position)
															.getUserInfo()
															.getScreenName());
											browse.putExtras(bundle);
											startActivity(browse);
										}
									})
							.setNegativeButton(getString(R.string.cancel), null)
							.create().show();

				}
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
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);

				if (timelineInfoList != null && timelineInfoList.size() > 0) {

					currentList = timelineInfoList;

					createListView(timelineInfoList);
					if (timelineInfoList.size() >= 20) {
						addItemForMoreTweets();
					}

				}
			}
			if (!"200".equals(statusCode)) {
				Toast.makeText(
						AlbumsTimelineActivity.this,
						ErrorMessage.getErrorMessage(
								AlbumsTimelineActivity.this, statusCode),
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

		timeLineDataList = new ArrayList<TimeLineInfo>();

		currentList = new ArrayList<TimeLineInfo>();

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
				R.layout.timeline_list_view_item_album, new String[] {
						"screenName", "web", "time", "commentCount",
						"moreTweets" }, new int[] { R.id.sina_screen_name,
						R.id.web_view_status, R.id.text_retweet_count,
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
			Intent home = new Intent(AlbumsTimelineActivity.this,
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
		headName.setText(R.string.album_list);
		headerRefresh.setBackgroundResource(R.drawable.main_home);
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
				timeLineDataList.clear();
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

					Bundle bundle = this.getIntent().getExtras();
					String uid = bundle.getString("uid");
					parameters.put("uid", uid);

					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_ALBUMS_TIMELINE,
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
		timeLineDataList.clear();
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
		timeLineDataList.clear();
		currentList.clear();
		currentPage = currentPage + 1;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("page", currentPage);
		parameters.put("id", getIntent().getExtras().getString("uid"));

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					commType, apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(ArrayList<TimeLineInfo> timelineInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		// Prepare ArrayList
		for (TimeLineInfo timelineInfo : timelineInfoList) {

			timeLineDataList.add(timelineInfo);

			Map<String, Object> map;
			map = new HashMap<String, Object>();
			map.put("screenName",
					timelineInfo.getUserInfo().getScreenName() == null ? ""
							: timelineInfo.getUserInfo().getScreenName());
			map.put("time", timelineInfo.getTime());
			map.put("commentCount", getString(R.string.comment_count) + "("
					+ timelineInfo.getCommentCount() + ")");
			map.put("web", timelineInfo.getStatus());

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
			progress = new HandleProgressDialog(AlbumsTimelineActivity.this);
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
