package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.ColumnInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.wangyi.WangyiCommHandler;
import com.anhuioss.crowdroid.service.wangyi.WangyiParserHandler;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class ColumnListActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {
	private CrowdroidApplication crowdroidApplication;
	private StatusData statusData;
	private SettingData settingData;
	private AccountData accountData;

	private String screenName;

	private String uid;

	private String userName;
	// head
	private Button headerBack = null;

	private Button headerRefresh = null;

	private TextView headName = null;

	private TextView Desc = null;

	private ListView listView = null;

	private RelativeLayout relativeRight;

	private RelativeLayout relativeBottom;

	private SimpleAdapter adapter;

	private MyImageBinder myImageBinder;

	private ApiServiceInterface apiServiceInterface;

	private UserInfo userInfo;

	private int currentPage = 1;

	private String currentTencentTag = "";

	private String currentTencentTagId = "";

	private ArrayList<ColumnInfo> columnlist;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	protected ArrayList<TimeLineInfo> currentList;

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
				Intent intent = new Intent(ColumnListActivity.this,
						WangyiColumnTimeLineActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("id", (String) columnlist.get(position)
						.getcolumnId());
				bundle.putString("columnName", (String) columnlist
						.get(position).getcolumnName());
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}

		}
	};

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
	// if (type == CommHandler.TYPE_GET_USER_TAGS_LIST) {
	//
	// // Parser
	// ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
	// ParseHandler parseHandler = new ParseHandler();
	// timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
	// .parser(service, type, statusCode, message);
	//
	// if (timelineInfoList != null && timelineInfoList.size() > 0) {
	// currentList = timelineInfoList;
	//
	// createListView(timelineInfoList);
	// if (timelineInfoList.size() >= 20) {
	// addItemForMoreTweets();
	// }
	//
	// } else if ("{}".equals(message)) {
	// Toast.makeText(TagsListActivity.this,
	// getString(R.string.permission),
	// Toast.LENGTH_SHORT).show();
	// }
	// } else if (type == CommHandler.TYPE_GET_USER_INFO) {
	//
	// // Parser
	// ParseHandler parseHandler = new ParseHandler();
	// userInfo = (UserInfo) parseHandler.parser(service, type,
	// statusCode, message);
	//
	// if (userInfo != null && userInfo.getTag() != null) {
	//
	// createTagListView(userInfo);
	//
	// } else if ("{}".equals(message)) {
	// Toast.makeText(TagsListActivity.this,
	// getString(R.string.permission),
	// Toast.LENGTH_SHORT).show();
	// }
	// } else if (type == CommHandler.TYPE_UPDATE_TAGS) {
	// Toast.makeText(TagsListActivity.this,
	// getString(R.string.success), Toast.LENGTH_SHORT)
	// .show();
	// if (statusData.getCurrentService().equals(
	// IGeneral.SERVICE_NAME_TENCENT)) {
	// finish();
	// } else {
	// refresh();
	// }
	//
	// }
	//
	// }
	// if (!"200".equals(statusCode)) {
	// Toast.makeText(
	// TagsListActivity.this,
	// ErrorMessage.getErrorMessage(TagsListActivity.this,
	// statusCode), Toast.LENGTH_SHORT).show();
	// }
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLayoutResId(R.layout.timeline_layout);
		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);

		relativeRight = (RelativeLayout) findViewById(R.id.layout_main_right);
		relativeBottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);
		relativeRight.setVisibility(View.GONE);
		relativeBottom.setVisibility(View.GONE);
		LayoutInflater inflater = LayoutInflater.from(this);
		View list = inflater.inflate(R.layout.sina_timeline_layout_list_item,
				null);
		Desc = (TextView) list.findViewById(R.id.sina_status);
		Desc.setVisibility(View.VISIBLE);

		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);
		headName.setText(getString(R.string.wangyi_column));
		// ListView
		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setDivider(null);
		// adapter = new SimpleAdapter(this, data,
		// R.layout.sina_timeline_layout_list_item, new String[] {
		// "screenName", "status", "webStatus", "retweetStatus",
		// "userImage", "time", "retweetedScreenNameStatus",
		// "verified", "web", "webRetweet", "important_level",
		// "retweetCount", "commentCount", "moreTweets" },
		// new int[] { R.id.sina_screen_name, R.id.sina_status,
		// R.id.web_status, R.id.web_retweet_status,
		// R.id.sina_user_image, R.id.sina_update_time,
		// R.id.retweeted_screen_name_status,
		// R.id.sina_user_verified, R.id.web_view_status,
		// R.id.web_view_retweet_status,
		// R.id.important_level_view, R.id.text_retweet_count,
		// R.id.text_comment_count, R.id.text_get_more_tweets });
		adapter = new SimpleAdapter(this, data,
				R.layout.basic_timeline_layout_list_item, new String[] {
						"screenName", "status", "userImage", "time", "web",
						"level", "moreTweets" }, new int[] { R.id.screen_name,
						R.id.status, R.id.user_image, R.id.update_time,
						R.id.web_view_status, R.id.level_view,
						R.id.text_get_more_tweets });

		listView.setAdapter(adapter);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);

		screenName = getIntent().getExtras().getString("name");
		uid = getIntent().getExtras().getString("uid");
		userName = getIntent().getExtras().getString("user_name");

		if (screenName.equals("")) {
			// Get Screen Name From Current Account
			screenName = accountData.getUserScreenName();
			userName = accountData.getUserName();
			uid = accountData.getUid();
		}
		// Bind Api Service
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_refresh: {
			refresh();
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
		// TODO Auto-generated method stub
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (data.isEmpty()) {
			// if (statusData.getCurrentService().equals(
			// IGeneral.SERVICE_NAME_SINA)) {
			// try {
			// listView.setClickable(false);
			//
			// // Prepare Parameters
			// Map<String, Object> parameters;
			// parameters = new HashMap<String, Object>();
			// parameters.put("page", currentPage);
			// parameters.put("uid", uid);
			// // Request
			// showProgressDialog();
			// apiServiceInterface.request(statusData.getCurrentService(),
			// CommHandler.TYPE_GET_USER_TAGS_LIST,
			// apiServiceListener, parameters);
			// } catch (RemoteException e) {
			// e.printStackTrace();
			// }
			//
			// } else if (statusData.getCurrentService().equals(
			// IGeneral.SERVICE_NAME_TENCENT)) {
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("screen_name", screenName);
			parameters.put("uid", uid);
			parameters.put("username", userName);
			//
			// try {
			// apiServiceInterface.request(statusData.getCurrentService(),
			// CommHandler.TYPE_GET_USER_INFO, apiServiceListener,
			// parameters);
			// } catch (RemoteException e) {
			// e.printStackTrace();
			// }
			//
			// }
			CommResult result = new CommResult();
			result = WangyiCommHandler.getColumnsByUserId(parameters);

			try {
				columnlist = (ArrayList<ColumnInfo>) WangyiParserHandler
						.parseColumnList(result.getMessage());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			createListView(columnlist);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		apiServiceInterface = null;
	}

	public void refresh() {

		listView.setClickable(false);

		if (apiServiceInterface == null) {
			return;
		}
		// if
		// (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)) {
		// // Prepare Parameters
		// Map<String, Object> parameters;
		// parameters = new HashMap<String, Object>();
		// parameters.put("page", currentPage);
		// parameters.put("user_id", accountData.getUid());
		// // Request
		// showProgressDialog();
		//
		// try {
		// // Request
		// apiServiceInterface.request(statusData.getCurrentService(),
		// CommHandler.TYPE_GET_USER_TAGS_LIST,
		// apiServiceListener, parameters);
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		//
		// }
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("screen_name", screenName);
		parameters.put("uid", uid);
		parameters.put("username", userName);
		CommResult result = new CommResult();
		result = WangyiCommHandler.getColumnsByUserId(parameters);
		WangyiParserHandler parseHandler = new WangyiParserHandler();
		ArrayList<ColumnInfo> columnlist;
		try {
			columnlist = (ArrayList<ColumnInfo>) parseHandler
					.parseColumnList(result.getMessage());
			createListView(columnlist);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createListView(ArrayList<ColumnInfo> columnInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();
		data.clear();
		// Prepare ArrayList
		for (ColumnInfo columnInfo : columnInfoList) {

			// timeLineDataList.add(timelineInfo);

			Map<String, Object> map;
			map = new HashMap<String, Object>();

			map.put("screenName", columnInfo.getcolumnName());
			map.put("status", columnInfo.getcolumnDesc());
			// map.put("webStatus", timelineInfo.getStatus());
			// map.put("retweetStatus", "");
			// map.put("userImage", "");
			// map.put("time", "");
			// map.put("retweetedScreenNameStatus", "");
			// map.put("verified", "");
			// map.put("web", "");
			// map.put("webRetweet", "");
			// map.put("important_level", "");
			// map.put("retweetCount", "");
			// map.put("commentCount", "");
			// map.put("moreTweets", "");

			addDatas.add(map);

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();

	}

	// private void createTagListView(UserInfo userInfo) {
	//
	// // Create ListView
	// ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String,
	// Object>>();
	// data.clear();
	// String tags = userInfo.getTag();
	// String tagIds = userInfo.getTagId();
	//
	// if (tags.equals("")) {
	// Toast.makeText(ColumnListActivity.this,
	// getString(R.string.alert_tag_no_data), Toast.LENGTH_LONG)
	// .show();
	// } else {
	// String[] tag = tags.split("]");
	// String[] tagId = tagIds.split("]");
	// // Prepare ArrayList
	// for (int i = 0; i < tag.length - 1; i++) {
	//
	// // timeLineDataList.add(timelineInfo);
	//
	// Map<String, Object> map;
	// map = new HashMap<String, Object>();
	//
	// if (statusData.getCurrentService().equals(
	// IGeneral.SERVICE_NAME_TENCENT)) {
	// map.put("screenName", tag[i].replace("[", ""));
	// map.put("status", tagId[i].replace("[", ""));
	// }
	//
	// addDatas.add(map);
	//
	// }
	//
	// // Add
	// for (Map<String, Object> addData : addDatas) {
	// data.add(addData);
	// }
	//
	// // Notify
	// adapter.notifyDataSetChanged();
	// }
	//
	// }

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
		// TODO Auto-generated method stub
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(ColumnListActivity.this);
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
