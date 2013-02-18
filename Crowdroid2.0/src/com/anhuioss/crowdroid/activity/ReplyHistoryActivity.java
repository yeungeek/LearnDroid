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
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.BasicInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.util.ImageBuilder;

public class ReplyHistoryActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ListView listView = null;

	// bottom
	private RelativeLayout relativeBottom = null;

	// head
	private Button headerBack = null;

	private Button headerHome = null;

	private TextView headerName = null;

	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private SimpleAdapter adapter;

	private SettingData settingData;

	private AutoCompleteTextView searchText;

	private ImageButton searchButton;

	private String imageShow;

	protected ArrayList<TimeLineInfo> timeLineDataList;

	private StatusData statusData;

	/** Image Map for user profile */
	private HashMap<String, Bitmap> userImageMap = new HashMap<String, Bitmap>();

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// // Open Action Dialog
			// new ActionDialog(ReplyHistoryActivity.this, timeLineDataList
			// .get(position), timeLineDataList).show();

			ArrayList<TimeLineInfo> list = timeLineDataList;

			Intent detail = new Intent(ReplyHistoryActivity.this,
					DetailTweetActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("commtype", CommHandler.TYPE_GET_MESSAGE_BY_ID);
			bundle.putSerializable("timelineinfo", list.get(position));
			bundle.putSerializable("timelinedatalist", list);
			detail.putExtras(bundle);
			startActivity(detail);
		}

	};

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			BasicInfo.setNetTime();
			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")) {

				// Parser
				TimeLineInfo timelineInfo = new TimeLineInfo();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfo = (TimeLineInfo) parseHandler.parser(service,
						type, statusCode, message);

				// if (imageShow.equals(SettingsActivity.select[0])
				// || imageShow.equals(SettingsActivity.select[1])) {
				// // User Image
				// loadUserImage(timelineInfo);
				// }

				// Create ListView
				createListView(timelineInfo);

				if (!timelineInfo.getinReplyToStatusId().equals("null")) {
					searchText.setText(String.valueOf(timelineInfo
							.getinReplyToStatusId()));
					refresh();
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
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		timeLineDataList = new ArrayList<TimeLineInfo>();
		imageShow = settingData.getSelectionShowImage();

		// Set Gallery
		setLayoutResId(R.layout.basic_search_layout);
		// bottom
		relativeBottom = (RelativeLayout) findViewById(R.id.layout_search_bottom);
		relativeBottom.setVisibility(View.GONE);
		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerName.setText(R.string.reply_history);
		headerHome.setBackgroundResource(R.drawable.main_home);

		// Find Views
		listView = (ListView) findViewById(R.id.list_view);
		searchText = (AutoCompleteTextView) findViewById(R.id.auto_search_text);
		searchButton = (ImageButton) findViewById(R.id.search_button);

		// Set Click Listener
		searchButton.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);

		// Prepare Simple Adapter For List View
		adapter = new SimpleAdapter(this, data,
				R.layout.basic_timeline_layout_list_item, new String[] {
						"screenName", "status", "userImage", "webviewstatus",
						"time", "moreTweets" }, new int[] { R.id.screen_name,
						R.id.status, R.id.user_image, R.id.web_view_status,
						R.id.update_time, R.id.text_get_more_tweets });

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

		searchText.setText(getIntent().getStringExtra("messageId"));
		if (!searchText.getText().toString().equals("")) {
			findViewById(R.id.keyword_search_area).setVisibility(View.GONE);
		}

		SettingData settingData = crowdroidApplication.getSettingData();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();

		adapter.setViewBinder(new MyImageBinder(fontColor, fontSize, null, this));

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

		showProgressDialog();

		if (apiServiceInterface == null) {
			closeProgressDialog();
			return;
		}

		if (searchText.getText().toString().equals("")
				|| searchText.getText().toString().equals("null")) {
			closeProgressDialog();
			return;
		}

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("messageId", searchText.getText().toString());

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_MESSAGE_BY_ID, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(TimeLineInfo timelineInfo) {

		// Prepare ArrayList
		timeLineDataList.add(timelineInfo);

		Map<String, Object> map;
		map = new HashMap<String, Object>();
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			map.put("screenName", timelineInfo.getUserInfo().getUserName()
					+ " " + "@" + timelineInfo.getUserInfo().getScreenName());
		} else {
			map.put("screenName", timelineInfo.getUserInfo().getScreenName());
		}

		map.put("status", timelineInfo.getStatus());
		if (imageShow.equals(BrowseModeActivity.select[0])
				|| imageShow.equals(BrowseModeActivity.select[1])) {
			map.put("userImage", timelineInfo.getUserInfo().getUserImageURL());
		} else {
			map.put("userImage", String.valueOf(R.drawable.default_user_image));
		}
		map.put("webviewstatus", "");
		map.put("time", timelineInfo.getFormatTime(
				statusData.getCurrentService(), this));

		// Add
		data.add(map);

		// Notify
		adapter.notifyDataSetChanged();

	}

	// -----------------------------------------------------------------------------
	/**
	 * Load userImage.
	 */
	// -----------------------------------------------------------------------------
	private void loadUserImage(TimeLineInfo info) {

		if (info == null) {
			return;
		}

		// Garbage Collection
		System.gc();

		// Get UserInfo
		UserInfo userInfo = info.getUserInfo();

		if (!userImageMap.containsKey(userInfo.getUid())) {

			try {

				// Prepare User Image
				Bitmap bitmap = ImageBuilder.returnBitMap(userInfo
						.getUserImageURL());
				userImageMap.put(userInfo.getUid(), bitmap);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Set Image To Info
		userInfo.setUserImage(userImageMap.get(userInfo.getUid()));

		// Garbage Collection
		System.gc();

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (data.isEmpty()) {
			refresh();
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
		case R.id.search_button: {
			data.clear();
			refresh();
			break;
		}
		case R.id.head_back: {

			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent home = new Intent(ReplyHistoryActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}
		default: {

		}
		}

	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

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
