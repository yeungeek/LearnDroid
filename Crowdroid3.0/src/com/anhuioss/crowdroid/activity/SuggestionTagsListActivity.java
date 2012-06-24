package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.operations.DetailBlogActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

public class SuggestionTagsListActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {
	private CrowdroidApplication crowdroidApplication;
	private StatusData statusData;
	private SettingData settingData;
	// head
	private Button headerBack = null;

	private Button headerRefresh = null;

	private TextView headName = null;

	private ListView listView = null;

	private SimpleAdapter adapter;

	private MyImageBinder myImageBinder;

	private ApiServiceInterface apiServiceInterface;

	private int currentPage = 1;

	private RelativeLayout relativeRight;

	private RelativeLayout relativeBottom;

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
			}
			// CFB、Twitter、Sina、Tencent、Sohu
			else {
				String[] items = null;
				items = getResources().getStringArray(
						R.array.suggestion_tags_add);
				AlertDialog dialog = new AlertDialog.Builder(
						SuggestionTagsListActivity.this).setItems(items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int pos) {
								switch (pos) {
								case 0: {
									// ====================
									showProgressDialog();
									// Prepare Parameters
									Map<String, Object> parameters;
									parameters = new HashMap<String, Object>();
									parameters.put("tags", data.get(position)
											.get("screenName"));
									// Request
									try {
										apiServiceInterface.request(
												statusData.getCurrentService(),
												CommHandler.TYPE_UPDATE_TAGS,
												apiServiceListener, parameters);
									} catch (RemoteException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									break;
								}
								case 1: {
									Intent intent = new Intent(
											SuggestionTagsListActivity.this,
											UserSearchActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("keyword", data.get(position)
											.get("screenName").toString());
									intent.putExtras(bundle);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
									break;
								}

								default:
									break;
								}

							}
						}).create();
				dialog.show();

				// Intent detail = new Intent(TimelineActivity.this,
				// DetailTweetActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putInt("commtype", commType);
				// bundle.putSerializable("timelineinfo",
				// timeLineDataList.get(position));
				// bundle.putSerializable("timelinedatalist", timeLineDataList);
				// detail.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// detail.putExtras(bundle);
				// startActivity(detail);
			}

		}
	};
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			listView.setClickable(true);
			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {
				if (type == CommHandler.TYPE_GET_SUGGESTED_TAGS_LIST) {
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

					} else if ("{}".equals(message)) {
						Toast.makeText(SuggestionTagsListActivity.this,
								getString(R.string.permission),
								Toast.LENGTH_SHORT).show();
					}
				} else if (type == CommHandler.TYPE_UPDATE_TAGS) {
					Toast.makeText(SuggestionTagsListActivity.this,
							getString(R.string.success), Toast.LENGTH_SHORT)
							.show();
				}

			} else if (type == CommHandler.TYPE_UPDATE_TAGS
					&& message.equals("[]")) {
				
				Toast.makeText(SuggestionTagsListActivity.this,
						getString(R.string.alert_tag_add_overflow),
						Toast.LENGTH_SHORT).show();
			}
			if (!"200".equals(statusCode)) {
				Toast.makeText(
						SuggestionTagsListActivity.this,
						ErrorMessage.getErrorMessage(
								SuggestionTagsListActivity.this, statusCode),
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
		setLayoutResId(R.layout.timeline_layout);
		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);

		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);
		headName.setText(getString(R.string.discovery_sina_tag_suggest));

		relativeRight = (RelativeLayout) findViewById(R.id.layout_main_right);
		relativeBottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);
		relativeRight.setVisibility(View.GONE);
		relativeBottom.setVisibility(View.GONE);

		// ListView
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

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);
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
			try {
				listView.setClickable(false);

				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("page", currentPage);

				// Request
				showProgressDialog();
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_SUGGESTED_TAGS_LIST,
						apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		apiServiceInterface = null;
	}

	public void refresh() {

		listView.setClickable(false);
		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();

		if (apiServiceInterface == null) {
			return;
		}

		currentPage = currentPage + 1;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("page", currentPage);
		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_SUGGESTED_TAGS_LIST,
					apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void createListView(ArrayList<TimeLineInfo> timelineInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();
		data.clear();
		// Prepare ArrayList
		for (TimeLineInfo timelineInfo : timelineInfoList) {

			// timeLineDataList.add(timelineInfo);

			Map<String, Object> map;
			map = new HashMap<String, Object>();

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)) {
				map.put("screenName", timelineInfo.getStatus());
				// map.put("status", "");
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

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(SuggestionTagsListActivity.this);
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
