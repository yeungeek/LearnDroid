package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.KeywordFilterData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.UserFilterData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

public class MoodStatusTimelineActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private StatusData statusData;

	private CrowdroidApplication crowdroidApplication;

	private AccountData currentAccount;

	private SettingData settingData;

	private String service;

	private String screenName;

	private String uid;

	private String userName;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	protected ArrayList<TimeLineInfo> timeLineDataList;

	private MyImageBinder myImageBinder;

	private String imageShow;

	private long lastRefreshTime;

	private int currentPage = 1;

	private SimpleAdapter adapter;

	private LinearLayout linearTab = null;

	private RelativeLayout right = null;
	
	private RelativeLayout buttom=null;

	// head
	private Button headerBack = null;

	private Button headerRefresh = null;

	private TextView headName = null;

	private ListView listView = null;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);

				if (timelineInfoList != null && timelineInfoList.size() > 0) {

					String num=null;
					for(TimeLineInfo timelineInfo:timelineInfoList){
						num=timelineInfo.getmoodNum();
					}	
					if(num.equals("0")){
						Toast.makeText(MoodStatusTimelineActivity.this, getString(R.string.alert_mood_no_data), Toast.LENGTH_LONG).show();
					}else
					{
						statusFilter(timelineInfoList);
						createListView(timelineInfoList);
						if (timelineInfoList.size() >= 20) {
							addItemForMoreTweets();
						}
					}		

				} else if ("{}".equals(message)) {
					Toast.makeText(MoodStatusTimelineActivity.this,
							getString(R.string.permission), Toast.LENGTH_SHORT)
							.show();
				}
			}
			if (!"200".equals(statusCode)) {

				Toast.makeText(MoodStatusTimelineActivity.this, ErrorMessage
						.getErrorMessage(MoodStatusTimelineActivity.this, statusCode),
						Toast.LENGTH_SHORT);
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
				refresh();
			} else {
				Intent detail = new Intent(MoodStatusTimelineActivity.this,
						DetailTweetActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("commtype", "TYPE_GET_MOOD_STATUS");
				bundle.putSerializable("timelineinfo",
						timeLineDataList.get(position));
				bundle.putSerializable("timelinedatalist", timeLineDataList);
				detail.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				detail.putExtras(bundle);
				startActivity(detail);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.timeline_layout);

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		timeLineDataList = new ArrayList<TimeLineInfo>();
		service = statusData.getCurrentService();

		linearTab = (LinearLayout) findViewById(R.id.linear_layout_tab);
		linearTab.setVisibility(View.GONE);

		right = (RelativeLayout) findViewById(R.id.layout_main_right);
		right.setVisibility(View.GONE);
		
		buttom=(RelativeLayout)findViewById(R.id.layout_main_bottom);
		buttom.setVisibility(View.GONE);

		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headerRefresh.setBackgroundResource(R.drawable.main_home);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(getString(R.string.discovery_tencent_get_mood_status));

		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setDivider(null);

		adapter = new SimpleAdapter(this, data,
				R.layout.sina_timeline_layout_list_item, new String[] {
						"screenName", "status", "webStatus", "retweetStatus",
						"userImage", "time", "retweetedScreenNameStatus",
						"verified", "web", "webRetweet", "important_level",
						"retweetCount", "commentCount", "moreTweets" },
				new int[] { R.id.sina_screen_name, R.id.sina_status,
						R.id.web_status, R.id.web_retweet_status,
						R.id.sina_user_image, R.id.sina_update_time,
						R.id.retweeted_screen_name_status,
						R.id.sina_user_verified, R.id.web_view_status,
						R.id.web_view_retweet_status,
						R.id.important_level_view, R.id.text_retweet_count,
						R.id.text_comment_count, R.id.text_get_more_tweets });

		listView.setAdapter(adapter);

		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		screenName = getIntent().getExtras().getString("name");
		uid = getIntent().getExtras().getString("uid");
		userName = getIntent().getExtras().getString("user_name");

		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		settingData = crowdroidApplication.getSettingData();

		imageShow = settingData.getSelectionShowImage();

		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);

		if (screenName.equals("")) {
			// Get Screen Name From Current Account
			screenName = currentAccount.getUserScreenName();
			userName = currentAccount.getUserName();
			uid = currentAccount.getUid();
		}
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		// Unbind Service
		unbindService(this);

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (data.isEmpty()) {
			try {

				lastRefreshTime = System.currentTimeMillis();

				listView.setClickable(false);
				// setProgressBarIndeterminateVisibility(true);
				showProgressDialog();

				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("page", currentPage);
				parameters.put("screen_name", screenName);
				parameters.put("uid", uid);
				parameters.put("username", userName);
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_MOOD_STATUS_LIST, apiServiceListener,
						parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;

	}

	private void createListView(ArrayList<TimeLineInfo> timelineInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		// Prepare ArrayList
		for (TimeLineInfo timelineInfo : timelineInfoList) {

			timeLineDataList.add(timelineInfo);
			Map<String, Object> map;
			map = new HashMap<String, Object>();

			if (timelineInfo.getRetweetUserInfo() == null) {

				map.put("screenName", timelineInfo.getUserInfo()
						.getScreenName() == null ? "" : timelineInfo
						.getUserInfo().getScreenName());
			}

			String statusImages = timelineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			String status = TagAnalysis.clearImageUrls(
					timelineInfo.getStatus(), statusImages);
			map.put("status", status);
//			map.put("webStatus", status);
			if (imageShow.equals(BrowseModeActivity.select[0])
					|| imageShow.equals(BrowseModeActivity.select[1])) {

				// Put Download Image
				map.put("userImage", timelineInfo.getUserInfo()
						.getUserImageURL());

			} else {
				// Put Default Image
				map.put("userImage",
						String.valueOf(R.drawable.default_user_image));
			}

			map.put("time",
					timelineInfo.getFormatTime(statusData.getCurrentService()));

			map.put("pageTime", timelineInfo.getTimeStamp());

			map.put("retweetCount", getString(R.string.retweet_count) + "("
					+ timelineInfo.getRetweetCount() + ")");
			map.put("commentCount", getString(R.string.comment_count) + "("
					+ timelineInfo.getCommentCount() + ")");

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

				String retweet = TagAnalysis.clearImageUrls(text, timelineInfo
						.getImageInformationForWebView(this,
								TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET));
				map.put("retweetedScreenNameStatus", retweet);
				map.put("retweetStatus", retweet);
			}
			map.put("verified", timelineInfo.getUserInfo().getVerified());

			String str = timelineInfo.getImageInformationForWebView(this,
					TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			map.put("web", "");
			map.put("webStatus", status + ";" + str); 
			map.put("webRetweet", "");

			addDatas.add(map);

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();

	}

	public void refresh() {

		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();
		lastRefreshTime = System.currentTimeMillis();

		if (apiServiceInterface == null) {
			return;
		}

		currentPage = currentPage + 1;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("page", currentPage);
		parameters.put("username", userName);
		String pageTime = "0";
		if (currentPage != 1) {
			pageTime = data.get(data.size() - 1).get("pageTime").toString();
		}
		parameters.put("pageTime", pageTime);
		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_MOOD_STATUS_LIST, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
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
			if (MoodStatusTimelineActivity.this != null
					&& !MoodStatusTimelineActivity.this.isFinishing()) {
				progress.dismiss();
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_refresh: {
			Intent home = new Intent(MoodStatusTimelineActivity.this,
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
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

	private void statusFilter(ArrayList<TimeLineInfo> timelineInfoList) {

		// Prepare Data
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		ArrayList<KeywordFilterData> keywordList = crowdroidApplication
				.getKeywordFilterList().getAllKeywords();
		ArrayList<UserFilterData> userNameList = crowdroidApplication
				.getUserFilterList().getUserFilter(
						statusData.getCurrentService());

		// Filter
		for (TimeLineInfo timeLineInfo : timelineInfoList) {

			// Keyword Filter
			for (KeywordFilterData key : keywordList) {
				if (timeLineInfo.getStatus().contains(key.getKeyword())) {
					timeLineInfo.setStatus("**********");
				}
			}

			// User Filter
			for (UserFilterData userName : userNameList) {
				if (timeLineInfo.getUserInfo().getScreenName()
						.equals(userName.getUserName())) {
					timeLineInfo.setStatus("**********");
				}
			}

		}

	}

}
