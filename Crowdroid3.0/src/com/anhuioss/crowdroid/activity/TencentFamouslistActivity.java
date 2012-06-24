package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
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
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class TencentFamouslistActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	private String service = "";

	private AccountData accountData;

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

	/** Image Map for user profile */
	public static HashMap<String, Bitmap> userImageMap = new HashMap<String, Bitmap>();

	private StatusData statusData;

	private SettingData settingData;

	private CrowdroidApplication crowdroidApplication;

	/** Auto Refresh Timer */
	// AutoRefreshHandler autoRefresh;

	/** Refresh Back */
	boolean refreshBack = false;

	private MyImageBinder myImageBinder;

	private String imageShow;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private String classid;

	private String subclassid;
	
	private String name;

	ProgressDialog pd = null;
	

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {

			Intent intent = new Intent(TencentFamouslistActivity.this,
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

				}
			}
			if (!"200".equals(statusCode)) {
				Toast.makeText(
						TencentFamouslistActivity.this,
						ErrorMessage.getErrorMessage(
								TencentFamouslistActivity.this, statusCode),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_refresh: {
			Intent home = new Intent(TencentFamouslistActivity.this,
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
					parameters.put("classid", classid);
					parameters.put("subclassid", subclassid);

					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_FAMOUS_LIST,
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
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
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

		classid = getIntent().getExtras().getString("classid");

		subclassid = getIntent().getExtras().getString("subclassid");
		
		name =getIntent().getExtras().getString("name");
		
		headName.setText(name);

		// Bind Api Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		refreshBack = true;

		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
	}

	private void createListView(ArrayList<UserInfo> userInfoList) {

		data.clear();
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

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(TencentFamouslistActivity.this);
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

	private void initTimeLineView() {
		linearTab.setVisibility(View.GONE);
		relativeRight.setVisibility(View.GONE);
		layout_bottom.setVisibility(View.GONE);
		headName.setText(getIntent().getExtras().getString("categoryName"));
		headerRefresh.setBackgroundResource(R.drawable.main_home);
	}
}
