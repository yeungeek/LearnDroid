package com.anhuioss.crowdroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.MoreFunctionActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class BasicUserSearchActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private ArrayAdapter<String> cfb_adapter_user_history;

	private ArrayAdapter<String> sina_adapter_user_history;

	private ArrayAdapter<String> sohu_adapter_user_history;

	private ArrayAdapter<String> tencent_adapter_user_history;

	private ArrayAdapter<String> twitter_adapter_user_history;

	private ArrayAdapter<String> twitter_proxy_adapter_user_history;

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
	private LinearLayout linearKeywordSearchArea = null;

	private AutoCompleteTextView searchText = null;

	private ImageButton searchButton = null;

	private ImageButton nextPageButton = null;

	private ListView listView = null;

	// search
	private String searchUserKeyword = null;

	private int commType;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	protected ArrayList<UserInfo> userInfoDataList;

	protected ArrayList<UserInfo> currentList = new ArrayList<UserInfo>();;

	protected ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

	private SimpleAdapter adapter;

	private StatusData statusData;

	private SettingData settingData;

	private int currentPage = 0;

	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private String imageShow;

	private String tag;
	
	private String flowTag;
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)
					&& position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				snsUserRefresh();
			} else {
				// Start Profile Activity
				Intent intent = new Intent(BasicUserSearchActivity.this,
						ProfileActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {
				// Parser
				ArrayList<UserInfo> timelineInfoList = new ArrayList<UserInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<UserInfo>) parseHandler.parser(
						service, type, statusCode, message);

				if (timelineInfoList.size() <= 0) {
					Toast.makeText(BasicUserSearchActivity.this,
							getResources().getString(R.string.user_tip),
							Toast.LENGTH_LONG).show();
				} else {
					// User Image
					loadUserImage(userInfoList);

					currentList = timelineInfoList;

					// Create ListView
					createListView(currentList);
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)
							&& timelineInfoList.size() >= 20) {
						addItemForMoreTweets();
					}

				}

			}

			if (!"200".equals(statusCode)) {
				if ("0".equals(statusCode)) {
					Toast.makeText(BasicUserSearchActivity.this,
							getResources().getString(R.string.user_tip),
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(
							BasicUserSearchActivity.this,
							ErrorMessage.getErrorMessage(
									BasicUserSearchActivity.this, statusCode),
							Toast.LENGTH_SHORT).show();
				}

			}

		}
	};

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
		// Set Gallery
		// setLayoutResId(R.layout.activity_user_search);
		setLayoutResId(R.layout.basic_search_layout);

		userInfoDataList = new ArrayList<UserInfo>();

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
		linearKeywordSearchArea = (LinearLayout) findViewById(R.id.keyword_search_area);
		listView = (ListView) findViewById(R.id.list_view);
		searchText = (AutoCompleteTextView) findViewById(R.id.auto_search_text);
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
		nextPageButton.setOnClickListener(this);

		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);
		listView.setDivider(null);
		// Prepare Simple Adapter For List View
		adapter = new SimpleAdapter(this, data,
				R.layout.basic_timeline_layout_list_item, new String[] {
						"screenName", "status", "userImage", "time", "web",
						"moreTweets" }, new int[] { R.id.screen_name,
						R.id.status, R.id.user_image, R.id.update_time,
						R.id.web_view_status, R.id.text_get_more_tweets });
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
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();

		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		imageShow = settingData.getSelectionShowImage();

		adapter.setViewBinder(new MyImageBinder(fontColor, fontSize, null, this));

		// init
		initUserSearchView();
		
		flowTag=getIntent().getExtras().getString("keyword");
		searchText.setText(flowTag);
		

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
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

	// -----------------------------------------------------------------------------
	/**
	 * Refresh
	 */
	// -----------------------------------------------------------------------------
	private void refresh() {

		if (apiServiceInterface == null) {
			return;
		}

		data.clear();

		userInfoList.clear();

		userInfoDataList.clear();

		showProgressDialog();

		currentPage = currentPage + 1;

		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();

		if (commType == CommHandler.TYPE_GET_RETWEETED_USER_LIST_BY_ID) {
			// Prepare Parameters

			parameters.put("retweeted_id",
					getIntent().getExtras().getString("retweeted_id"));
			try {
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_RETWEETED_USER_LIST_BY_ID,
						apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				if (commType == CommHandler.TYPE_GET_SUGGESTION_USERS) {
					parameters.put("slug",
							getIntent().getExtras().getString("slugs"));

				} else if (commType == CommHandler.TYPE_GET_GROUP_USER_LIST) {
					parameters.put("slug",
							getIntent().getExtras()
									.getString("group_list_slug"));
					parameters.put("screen_name", getIntent().getExtras()
							.getString("group_list_owner"));
					parameters.put("id",
							getIntent().getExtras().getString("group_list_id"));
				} else if (commType == CommHandler.TYPE_GET_FIND_PEPPLE_INFO) {
					parameters.put("query", searchText.getText().toString());
					parameters.put("page", currentPage);
				}
				try {
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							commType, apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)
					&& commType == CommHandler.TYPE_GET_FIND_PEPPLE_INFO
					&& getIntent().getExtras().containsKey(
							"TencentTagSearchFlag")) {

				parameters.put("query", searchText.getText().toString());
				parameters.put("page", currentPage);
				if (getIntent().getExtras().containsKey("TencentTagSearchFlag")) {
					parameters.put("TencentTagFlag", getIntent().getExtras()
							.getString("TencentTagSearchFlag"));
				}
				try {
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							commType, apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)
					&&commType!=CommHandler.TYPE_GET_SUGGESTION_USERS
					&& getIntent().getExtras().containsKey(
							"TencentTagSearchFlag")) {

				parameters.put("query", searchText.getText().toString());
				parameters.put("page", currentPage);
				if (getIntent().getExtras().containsKey("TencentTagSearchFlag")) {
					parameters.put("TencentTagFlag", getIntent().getExtras()
							.getString("TencentTagSearchFlag"));
				}
				try {
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							commType, apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {

				// Prepare Parameters
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					String name = this.getIntent().getExtras()
							.getString("name");// 姓名
					String searchType = this.getIntent().getExtras()
							.getString("flag");// 标识

					parameters.put("name", name);
					parameters.put("flag", searchType);
					if ("name".equals(searchType)) {// 快速查找

					} else if ("student".equals(searchType)) {// 搜同学

						parameters.put("serch_school_name", getIntent()
								.getExtras().getString("serch_school_name"));
						parameters.put("schoolType", getIntent().getExtras()
								.getInt("schoolType"));
						parameters.put("schoolYear", getIntent().getExtras()
								.getString("schoolYear"));

					} else if ("friend".equals(searchType)) {// 详细信息查找

						parameters.put("year", getIntent().getExtras()
								.getString("year"));
						parameters.put("month", getIntent().getExtras()
								.getString("month"));
						parameters.put("day", getIntent().getExtras()
								.getString("day"));

					} else if ("ts".equals(searchType)) {// 查找同事

						parameters.put("company_name", getIntent().getExtras()
								.getString("company_name"));

					} else if ("base".equals(searchType)) {// 按基本信息查找

						parameters.put("prov", getIntent().getExtras()
								.getString("prov"));
						parameters.put("city", getIntent().getExtras()
								.getString("city"));
						parameters.put("sex", getIntent().getExtras()
								.getString("sex"));
					}

					parameters.put("page", currentPage);
					try {
						// Request
						apiServiceInterface.request(
								statusData.getCurrentService(),
								CommHandler.TYPE_GET_FIND_PEPPLE_INFO,
								apiServiceListener, parameters);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else {
					parameters.put("page", currentPage);
					parameters.put("query", searchText.getText().toString());
					try {
						// Request
						apiServiceInterface.request(
								statusData.getCurrentService(), commType,
								apiServiceListener, parameters);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}

			}
		}

	}

	private void initUserSearchView() {

		// head
		switch (commType) {
		case CommHandler.TYPE_GET_HOT_USERS: {
			headName.setText(R.string.gallery_hot_users_title);
			linearKeywordSearchArea.setVisibility(View.GONE);
			break;
		}
		case CommHandler.TYPE_GET_SUGGESTION_USERS: {
			headName.setText(R.string.gallery_suggestion_users);
			linearKeywordSearchArea.setVisibility(View.GONE);
			break;
		}
		case CommHandler.TYPE_GET_FIND_PEPPLE_INFO: {

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)
					&& getIntent().getExtras().containsKey(
							"TencentTagSearchFlag")) {
				headName.setText(getString(R.string.discovery_serch_user_by_tag));
			} else {
				headName.setText(R.string.user_search);
			}

			nextPageButton.setVisibility(View.VISIBLE);

			if (searchUserKeyword != null && !searchUserKeyword.equals("")) {
				linearKeywordSearchArea.setVisibility(View.GONE);
			}
			break;
		}
		case CommHandler.TYPE_GET_RETWEETED_USER_LIST_BY_ID: {
			headName.setText(R.string.retweeted_user);
			linearKeywordSearchArea.setVisibility(View.GONE);
			break;
		}
		case CommHandler.TYPE_GET_GROUP_USER_LIST: {
			headName.setText(R.string.group_list);
			linearKeywordSearchArea.setVisibility(View.GONE);
			break;
		}
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			headName.setText(R.string.user_search);
			linearKeywordSearchArea.setVisibility(View.GONE);
		}

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
					IGeneral.SERVICE_NAME_TWITTER)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER_PROXY)) {

				map.put("screenName",
						userInfo.getScreenName() == null ? "" : userInfo
								.getUserName()
								+ " "
								+ "@"
								+ userInfo.getScreenName());

			} else {
				map.put("screenName", userInfo.getScreenName() == null ? ""
						: userInfo.getScreenName());
			}

			map.put("userName", userInfo.getUserName());
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)
					&& commType == CommHandler.TYPE_GET_FIND_PEPPLE_INFO
					&& getIntent().getExtras().containsKey(
							"TencentTagSearchFlag")) {
				tag = userInfo.getTag();
				map.put("status", tag);
			} else {
				map.put("status", userInfo.getDescription());
			}

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
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				map.put("birth", userInfo.getBirthday());
				// map.put("sex", userInfo.setGender(gender));
			}

			map.put("web", "");
			map.put("retweetCount", "");
			map.put("commentCount", "");
			map.put("time", "");
			map.put("moreTweets", "");
			addDatas.add(map);

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

		if (commType == CommHandler.TYPE_GET_FIND_PEPPLE_INFO) {
			if (!searchText.getText().toString().equals("")) {
				refresh();
			}
		} else if (commType == CommHandler.TYPE_GET_HOT_USERS
				|| commType == CommHandler.TYPE_GET_SUGGESTION_USERS) {
			if (data.isEmpty()) {
				refresh();
			}
			// }
		} else if (commType == CommHandler.TYPE_GET_RETWEETED_USER_LIST_BY_ID) {
			if (data.isEmpty()) {
				try {

					listView.setClickable(false);
					showProgressDialog();
					// Prepare Parameters
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("retweeted_id", getIntent().getExtras()
							.getString("retweeted_id"));

					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_RETWEETED_USER_LIST_BY_ID,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else if (commType == CommHandler.TYPE_GET_GROUP_USER_LIST
				&& (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER) || statusData
						.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TWITTER_PROXY))) {
			if (data.isEmpty()) {
				try {
					listView.setClickable(false);
					showProgressDialog();
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("slug",
							getIntent().getExtras()
									.getString("group_list_slug"));
					parameters.put("screen_name", getIntent().getExtras()
							.getString("group_list_owner"));
					parameters.put("id",
							getIntent().getExtras().getString("group_list_id"));
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							commType, apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		// RenRen user search
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			if (data.isEmpty()) {
				refresh();
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.tools_bottom_home: {
			Intent comment = new Intent(BasicUserSearchActivity.this,
					HomeTimelineActivity.class);
			startActivity(comment);
			break;
		}
		case R.id.tools_bottom_new: {
			Intent tweet = new Intent(BasicUserSearchActivity.this,
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
				app = new Intent(BasicUserSearchActivity.this,
						SNSDiscoveryActivity.class);
			} else {
				app = new Intent(BasicUserSearchActivity.this,
						DiscoveryActivity.class);
			}
			startActivity(app);
			break;
		}
		case R.id.tools_bottom_profile: {
			Intent intent = new Intent(BasicUserSearchActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", "");
			bundle.putString("user_name", "");
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.tools_bottom_more: {
			Intent more = new Intent(BasicUserSearchActivity.this,
					MoreFunctionActivity.class);
			startActivity(more);
			break;
		}
		case R.id.head_refresh: {
			// Clear Data
			data.clear();
			currentList.clear();
			userInfoDataList.clear();
			adapter.notifyDataSetChanged();
			// Set Page
			currentPage = 0;
			// Refresh
			refresh();
			break;
		}

		case R.id.search_button: {
			userInfoList.clear();
			userInfoDataList.clear();
			currentPage = 0;
			refresh();

			String text = searchText.getText().toString();
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {// cfb历史记录
				SharedPreferences sp = getSharedPreferences(
						"cfb_user_history_strs", 0);
				String save_Str = sp.getString("cfb_user_history", "");
				String[] hisArrays = save_Str.split(",");
				for (int i = 0; i < hisArrays.length; i++) {
					if (hisArrays[i].equals(text)) {
						return;
					}
				}
				StringBuilder sb = new StringBuilder(save_Str);
				sb.append(text + ",");
				sp.edit().putString("cfb_user_history", sb.toString()).commit();
			} else if (statusData.getCurrentService().equals( // sina历史记录
					IGeneral.SERVICE_NAME_SINA)) {
				SharedPreferences sp = getSharedPreferences(
						"sina_user_history_strs", 0);
				String save_Str = sp.getString("sina_user_history", "");
				String[] hisArrays = save_Str.split(",");
				for (int i = 0; i < hisArrays.length; i++) {
					if (hisArrays[i].equals(text)) {
						return;
					}
				}
				StringBuilder sb = new StringBuilder(save_Str);
				sb.append(text + ",");
				sp.edit().putString("sina_user_history", sb.toString())
						.commit();

			} else if (statusData.getCurrentService().equals( // sohu历史记录
					IGeneral.SERVICE_NAME_SOHU)) {
				SharedPreferences sp = getSharedPreferences(
						"sohu_user_history_strs", 0);
				String save_Str = sp.getString("sohu_user_history", "");
				String[] hisArrays = save_Str.split(",");
				for (int i = 0; i < hisArrays.length; i++) {
					if (hisArrays[i].equals(text)) {
						return;
					}
				}
				StringBuilder sb = new StringBuilder(save_Str);
				sb.append(text + ",");
				sp.edit().putString("sohu_user_history", sb.toString())
						.commit();

			} else if (statusData.getCurrentService().equals( // tencent历史记录
					IGeneral.SERVICE_NAME_TENCENT)) {
				SharedPreferences sp = getSharedPreferences(
						"tencent_user_history_strs", 0);
				String save_Str = sp.getString("tencent_user_history", "");
				String[] hisArrays = save_Str.split(",");
				for (int i = 0; i < hisArrays.length; i++) {
					if (hisArrays[i].equals(text)) {
						return;
					}
				}
				StringBuilder sb = new StringBuilder(save_Str);
				sb.append(text + ",");
				sp.edit().putString("tencent_user_history", sb.toString())
						.commit();

			} else if (statusData.getCurrentService().equals( // twitter历史记录
					IGeneral.SERVICE_NAME_TWITTER)) {
				SharedPreferences sp = getSharedPreferences(
						"twitter_user_history_strs", 0);
				String save_Str = sp.getString("twitter_user_history", "");
				String[] hisArrays = save_Str.split(",");
				for (int i = 0; i < hisArrays.length; i++) {
					if (hisArrays[i].equals(text)) {
						return;
					}
				}
				StringBuilder sb = new StringBuilder(save_Str);
				sb.append(text + ",");
				sp.edit().putString("twitter_user_history", sb.toString())
						.commit();

			} else if (statusData.getCurrentService().equals( // twitter-proxy历史记录
					IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				SharedPreferences sp = getSharedPreferences(
						"twitter_proxy_user_history_strs", 0);
				String save_Str = sp
						.getString("twitter_proxy_user_history", "");
				String[] hisArrays = save_Str.split(",");
				for (int i = 0; i < hisArrays.length; i++) {
					if (hisArrays[i].equals(text)) {
						return;
					}
				}
				StringBuilder sb = new StringBuilder(save_Str);
				sb.append(text + ",");
				sp.edit()
						.putString("twitter_proxy_user_history", sb.toString())
						.commit();

			}

			break;
		}
		case R.id.next_page_button: {
			data.clear();
			currentList.clear();
			userInfoDataList.clear();
			adapter.notifyDataSetChanged();
			// Refresh
			refresh();
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		default: {

		}
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {// cfb历史记录
			SharedPreferences sp = getSharedPreferences(
					"cfb_user_history_strs", 0);
			String save_user_history = sp.getString("cfb_user_history", "");
			String[] hisArrays = save_user_history.split(",");
			cfb_adapter_user_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				cfb_adapter_user_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(cfb_adapter_user_history);
		} else if (statusData.getCurrentService().equals( // sina历史记录
				IGeneral.SERVICE_NAME_SINA)) {
			SharedPreferences sp = getSharedPreferences(
					"sina_user_history_strs", 0);
			String save_user_history = sp.getString("sina_user_history", "");
			String[] hisArrays = save_user_history.split(",");
			sina_adapter_user_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				sina_adapter_user_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(sina_adapter_user_history);

		} else if (statusData.getCurrentService().equals( // sohu历史记录
				IGeneral.SERVICE_NAME_SOHU)) {
			SharedPreferences sp = getSharedPreferences(
					"sohu_user_history_strs", 0);
			String save_user_history = sp.getString("sohu_user_history", "");
			String[] hisArrays = save_user_history.split(",");
			sohu_adapter_user_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				sohu_adapter_user_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(sohu_adapter_user_history);

		} else if (statusData.getCurrentService().equals( // tencent历史记录
				IGeneral.SERVICE_NAME_TENCENT)) {
			SharedPreferences sp = getSharedPreferences(
					"tencent_user_history_strs", 0);
			String save_user_history = sp.getString("tencent_user_history", "");
			String[] hisArrays = save_user_history.split(",");
			tencent_adapter_user_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				tencent_adapter_user_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(tencent_adapter_user_history);

		} else if (statusData.getCurrentService().equals( // twitter历史记录
				IGeneral.SERVICE_NAME_TWITTER)) {
			SharedPreferences sp = getSharedPreferences(
					"twitter_user_history_strs", 0);
			String save_user_history = sp.getString("twitter_user_history", "");
			String[] hisArrays = save_user_history.split(",");
			twitter_adapter_user_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				twitter_adapter_user_history = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			searchText.setAdapter(twitter_adapter_user_history);

		} else if (statusData.getCurrentService().equals( // twitter-proxy历史记录
				IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			SharedPreferences sp = getSharedPreferences(
					"twitter_proxy_user_history_strs", 0);
			String save_user_history = sp.getString(
					"twitter_proxy_user_history", "");
			String[] hisArrays = save_user_history.split(",");
			twitter_proxy_adapter_user_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				twitter_proxy_adapter_user_history = new ArrayAdapter<String>(
						this, android.R.layout.simple_dropdown_item_1line,
						newArrays);
			}
			searchText.setAdapter(twitter_proxy_adapter_user_history);

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

	private void snsUserRefresh() {
		if (apiServiceInterface == null) {
			return;
		}
		showProgressDialog();

		currentPage = currentPage + 1;

		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();

		// Prepare Parameters
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			String name = this.getIntent().getExtras().getString("name");// 姓名
			String searchType = this.getIntent().getExtras().getString("flag");// 标识

			parameters.put("name", name);
			parameters.put("flag", searchType);
			if ("name".equals(searchType)) {// 快速查找

			} else if ("student".equals(searchType)) {// 搜同学

				parameters.put("serch_school_name", getIntent().getExtras()
						.getString("serch_school_name"));
				parameters.put("schoolType",
						getIntent().getExtras().getInt("schoolType"));
				parameters.put("schoolYear",
						getIntent().getExtras().getString("schoolYear"));

			} else if ("friend".equals(searchType)) {// 详细信息查找

				parameters.put("year", getIntent().getExtras()
						.getString("year"));
				parameters.put("month",
						getIntent().getExtras().getString("month"));
				parameters.put("day", getIntent().getExtras().getString("day"));

			} else if ("ts".equals(searchType)) {// 查找同事

				parameters.put("company_name", getIntent().getExtras()
						.getString("company_name"));

			} else if ("base".equals(searchType)) {// 按基本信息查找

				parameters.put("prov", getIntent().getExtras()
						.getString("prov"));
				parameters.put("city", getIntent().getExtras()
						.getString("city"));
				parameters.put("sex", getIntent().getExtras().getString("sex"));
			}

			parameters.put("page", currentPage);
			try {
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_FIND_PEPPLE_INFO,
						apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}
}
