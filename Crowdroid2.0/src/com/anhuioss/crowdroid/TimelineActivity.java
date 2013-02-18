package com.anhuioss.crowdroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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

import com.anhuioss.crowdroid.CommentTimelineActivity;
import com.anhuioss.crowdroid.activity.DetailTweetActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.MoreFunctionActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.activity.SendDMActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
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
import com.anhuioss.crowdroid.data.TranslationList;
import com.anhuioss.crowdroid.data.UserFilterData;
import com.anhuioss.crowdroid.data.info.BasicInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.notification.NotificationService;
import com.anhuioss.crowdroid.operations.ScheduleMonthActivity;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.TranslationHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.settings.AddSinaAccountActivity;
import com.anhuioss.crowdroid.settings.AddWangyiAccountActivity;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sinaLBS.LBSlocationActivity;
import com.anhuioss.crowdroid.sns.operations.DetailBlogActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;
import com.anhuioss.crowdroid.IGeneral;

public class TimelineActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private SharedPreferences notificationSharefer;

	private SharedPreferences.Editor notificationEditor;

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

	private Button btnlbs = null;

	// Tab

	private Button btnDMSend = null;

	private Button btnDMReceived = null;

	// count

	private TextView homecount = null;

	private TextView atcount = null;

	private TextView directcount = null;

	private TextView commentcount = null;

	private TextView followercount = null;

	private TextView statecount = null;

	private TextView retweet_of_me = null;

	private SimpleAdapter adapter;

	protected ArrayList<TimeLineInfo> timeLineDataList;

	protected ArrayList<TimeLineInfo> currentList;

	private int commType;

	/** Image Map for user profile */
	public static HashMap<String, Bitmap> userImageMap = new HashMap<String, Bitmap>();

	private int currentPage = 1;

	private StatusData statusData;

	private AccountData accountData;

	private SettingData settingData;

	private TranslationList translationDataList;

	private int translateMessageIndex;

	private CrowdroidApplication crowdroidApplication;

	/** Auto Refresh Timer */
	AutoRefreshHandler autoRefresh;

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

	private String column_id;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private static boolean isRefreshFromAutoRefresh = false;

	public static boolean isBackgroundNotificationFlag = false;// 通知显示在状态栏上的flag，true代表显示在状态栏

	// private String backgroundNotifyFlag = "";

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
			} else {
				// RenRen
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND
							|| commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
						Intent detail = new Intent(TimelineActivity.this,
								DetailTweetActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("commtype", commType);
						bundle.putSerializable("timelineinfo",
								timeLineDataList.get(position));
						bundle.putSerializable("timelinedatalist",
								timeLineDataList);
						detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						detail.putExtras(bundle);
						startActivity(detail);
					} else if (commType == CommHandler.TYPE_GET_BLOG_TIMELINE) {
						Intent detail = new Intent(TimelineActivity.this,
								DetailBlogActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("timelineinfo",
								timeLineDataList.get(position));
						detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						detail.putExtras(bundle);
						startActivity(detail);
					} else if (commType == CommHandler.TYPE_GET_RETWEET_OF_ME_TIME_LINE) {
						Intent intent = new Intent(TimelineActivity.this,
								ProfileActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("name", timeLineDataList.get(position)
								.getUserInfo().getScreenName());
						bundle.putString("uid", timeLineDataList.get(position)
								.getUserInfo().getUid());
						bundle.putString("user_name",
								timeLineDataList.get(position).getUserInfo()
										.getUserName());
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtras(bundle);
						startActivity(intent);
					} else {
						Intent detail = new Intent(TimelineActivity.this,
								DetailTweetActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("commtype", commType);
						bundle.putSerializable("timelineinfo",
								timeLineDataList.get(position));
						bundle.putSerializable("timelinedatalist",
								timeLineDataList);
						// 人人传递头像以及姓名
						if (commType == CommHandler.TYPE_GET_STATUS_TIMELINE) {
							bundle.putString("name", userName);
							bundle.putString("headUrl", headUrl);
						}
						detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						detail.putExtras(bundle);
						startActivity(detail);
					}
				}
				// CFB、Twitter、Sina、Tencent、Sohu
				else {
					if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
						openSelectDialog(position);
					} else if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND) {

					} else {
						Intent detail = new Intent(TimelineActivity.this,
								DetailTweetActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("commtype", commType);
						bundle.putSerializable("timelineinfo",
								timeLineDataList.get(position));
						bundle.putSerializable("timelinedatalist",
								timeLineDataList);
						detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						detail.putExtras(bundle);
						startActivity(detail);
					}
				}
			}
		}
	};

	private DownloadServiceInterface downloadServiceInterface;

	private ApiServiceInterface apiServiceInterface;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				if (NotificationService.homecount > 0) {
					if (notificationSharefer.contains("notify_home")) {

						notificationEditor.putInt("notify_home",
								NotificationService.homecount);
						notificationEditor.commit();
					} else {
						notificationEditor.putInt("notify_home",
								NotificationService.homecount);
						notificationEditor.commit();
					}
					if (!NotificationService.backgroundNotifyFlag) {
						homecount.setVisibility(View.VISIBLE);
						if (NotificationService.homecount == 20) {
							homecount.setText("20+");

						} else {
							homecount.setText(String
									.valueOf(NotificationService.homecount));

						}
					}
				}
				if (NotificationService.atcount > 0) {
					if (notificationSharefer.contains("notify_at")) {

						notificationEditor.putInt("notify_at",
								NotificationService.atcount);
						notificationEditor.commit();
					} else {
						notificationEditor.putInt("notify_at",
								NotificationService.atcount);
						notificationEditor.commit();
					}
					if (!NotificationService.backgroundNotifyFlag) {
						atcount.setVisibility(View.VISIBLE);
						if (NotificationService.atcount == 20) {
							atcount.setText("20+");
						} else {
							atcount.setText(String
									.valueOf(NotificationService.atcount));
						}
					}

				}
				if (NotificationService.directcount > 0) {

					if (notificationSharefer.contains("notify_direct")) {

						notificationEditor.putInt("notify_direct",
								NotificationService.directcount);
						notificationEditor.commit();
					} else {
						notificationEditor.putInt("notify_direct",
								NotificationService.directcount);
						notificationEditor.commit();
					}
					if (!NotificationService.backgroundNotifyFlag) {
						directcount.setVisibility(View.VISIBLE);
						if (NotificationService.directcount == 20) {
							directcount.setText("20+");
						} else {
							directcount.setText(String
									.valueOf(NotificationService.directcount));
						}
					}

				}
				if (NotificationService.Commentcount > 0) {
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SOHU)) {
						if (!NotificationService.backgroundNotifyFlag) {
							commentcount.setVisibility(View.VISIBLE);
							if (NotificationService.Commentcount == 20) {
								commentcount.setText("20+");
							} else {
								commentcount
										.setText(String
												.valueOf(NotificationService.Commentcount));
							}
						}

					}

				}
				if (NotificationService.CommentCfbcount > 0) {
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SINA)) {
						if (!NotificationService.backgroundNotifyFlag) {
							commentcount.setVisibility(View.VISIBLE);
							if (NotificationService.CommentCfbcount == 20) {
								commentcount.setText("20+");
							} else {
								commentcount
										.setText(String
												.valueOf(NotificationService.CommentCfbcount));
							}
						}

					}

				}
				if (NotificationService.followercount > 0) {
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SOHU)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TENCENT)) {
						if (!NotificationService.backgroundNotifyFlag) {
							followercount.setVisibility(View.VISIBLE);
							if (NotificationService.followercount == 20) {
								followercount.setText("20+");
							} else {
								followercount
										.setText(String
												.valueOf(NotificationService.followercount));
							}
						}

					}

				}
				if (NotificationService.followerTwittercount > 0
						&& statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TWITTER)) {
					if (!NotificationService.backgroundNotifyFlag) {
						followercount.setVisibility(View.VISIBLE);
						if (NotificationService.followercount == 20) {
							followercount.setText("20+");
						} else {
							followercount
									.setText(String
											.valueOf(NotificationService.followerTwittercount));
						}
					}

				}
				// 人人状态数
				if (NotificationService.statecount > 0
						&& statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_RENREN)) {
					if (notificationSharefer.contains("notify_state")) {

						notificationEditor.putInt("notify_state",
								NotificationService.statecount);
						notificationEditor.commit();
					} else {
						notificationEditor.putInt("notify_state",
								NotificationService.statecount);
						notificationEditor.commit();
					}
					if (!NotificationService.backgroundNotifyFlag) {
						statecount.setVisibility(View.VISIBLE);
						if (NotificationService.statecount == 20) {
							statecount.setText("20+");
						} else {
							statecount.setText(String
									.valueOf(NotificationService.statecount));
						}

					}

				}
				if (NotificationService.retweetOfMecount > 0
						&& statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TWITTER)) {
					if (notificationSharefer.contains("notify_retweet_of_me")) {

						notificationEditor.putInt("notify_retweet_of_me",
								NotificationService.retweetOfMecount);
						notificationEditor.commit();
					} else {
						notificationEditor.putInt("notify_retweet_of_me",
								NotificationService.retweetOfMecount);
						notificationEditor.commit();
					}
					if (!NotificationService.backgroundNotifyFlag) {
						retweet_of_me.setVisibility(View.VISIBLE);
						if (NotificationService.retweetOfMecount == 20) {
							retweet_of_me.setText("20+");
						} else {
							retweet_of_me
									.setText(String
											.valueOf(NotificationService.retweetOfMecount));
						}
					}

				}

				break;
			}
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

	private TranslationServiceInterface translationServiceInterface;

	private TranslationServiceListener.Stub translationServiceListener = new TranslationServiceListener.Stub() {

		@Override
		public void requestCompleted(String engine, long type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				// Translation Size
				size = size - 1;

				for (TimeLineInfo timeLineInfo : currentList) {
					if (timeLineInfo.getMessageId()
							.equals(String.valueOf(type))) {
						timeLineInfo.setStatus(message);
					}
				}

				if (size == 0) {

					listView.setClickable(true);
					closeProgressDialog();

					createListView(currentList, "someelse");

					addItemForMoreTweets();
				}
				// Log.e("translate-index",
				// String.valueOf(translateMessageIndex));
				// Log.e("current-message", String.valueOf(message));
				// if(!"".equals(message) && translateMessageIndex!=-1){
				// for (int i = 0;i<currentList.size();i++) {
				// TimeLineInfo timeLineInfo = currentList.get(i);
				// // if (timeLineInfo.getMessageId()
				// // .equals(String.valueOf(type))) {
				// timeLineInfo.setStatus(message);
				// currentList.set(translateMessageIndex, timeLineInfo);
				// // }
				// }
				// }
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
			BasicInfo.setNetTime();
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {

				// Parser
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);

				if (timelineInfoList != null && timelineInfoList.size() > 0) {
					statusFilter(timelineInfoList);

					if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
						if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE
								|| commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND
								|| commType == CommHandler.TYPE_GET_AT_MESSAGE
								|| commType == CommHandler.TYPE_GET_MY_TIME_LINE
						// || commType == CommHandler.TYPE_GET_HOME_TIMELINE
						) {
							if (currentPage == 1 && !isRefreshFromAutoRefresh) {
								setNewestMessageId(type, timelineInfoList);
							} else if (currentPage == 1
									&& !NotificationService.isNotifyChecking) {
								setNewestMessageId(type, timelineInfoList);
							}
						}
					} else {
						if (currentPage == 1 && !isRefreshFromAutoRefresh) {
							setNewestMessageId(type, timelineInfoList);
						} else if (currentPage == 1
								&& !NotificationService.isNotifyChecking) {
							setNewestMessageId(type, timelineInfoList);
						}
					}

					isRefreshFromAutoRefresh = false;

					if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
							.equals(statusData.getCurrentService())
							&& commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
						currentList = timelineInfoList;
						autoTranslate(timelineInfoList);
					} else {
						if (commType == CommHandler.TYPE_GET_AT_MESSAGE) {
							createListView(timelineInfoList, "at");
						} else if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
							createListView(timelineInfoList, "direct");
						} else if (commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
							createListView(timelineInfoList, "home");
						} else if (commType == CommHandler.TYPE_GET_RETWEET_OF_ME_TIME_LINE) {
							createListView(timelineInfoList, "retweet_of_me");
						} else if (commType == CommHandler.TYPE_GET_MY_TIME_LINE) {
							if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_RENREN)) {
								createListView(timelineInfoList, "my");
							} else {
								createListView(timelineInfoList, "");
							}

						} else {
							createListView(timelineInfoList, "");
						}

						if (timelineInfoList.size() >= 20
								&& !(service
										.equals(IGeneral.SERVICE_NAME_WANGYI)
										&& (commType == CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE) || commType == CommHandler.TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE)) {
							addItemForMoreTweets();
						}
						if (timelineInfoList.size() >= 19
								&& service
										.equals(IGeneral.SERVICE_NAME_TWITTER)
								&& commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
							addItemForMoreTweets();
						}
						if (!timelineInfoList.get(timelineInfoList.size() - 1)
								.equals("")) {
							cursor_id = timelineInfoList.get(
									timelineInfoList.size() - 1).getcursor_id();
							if (commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE
									|| commType == CommHandler.TYPE_GET_USER_COLUMN_TIME_LINE
									|| service
											.equals(IGeneral.SERVICE_NAME_TWITTER)) {
								cursor_id = timelineInfoList.get(
										timelineInfoList.size() - 1)
										.getMessageId();
							}
						}

					}
					// if (imageShow.equals(SettingsActivity.select[0])
					// || imageShow.equals(SettingsActivity.select[1])) {
					// // Download Images
					// downloadImage(timelineInfoList);
					//
					// } else {
					// // Undownload Image
					// createListView(timelineInfoList);
					// }

				} else if ("{}".equals(message)) {
					Toast.makeText(TimelineActivity.this,
							getString(R.string.permission), Toast.LENGTH_SHORT)
							.show();
				} else if (timelineInfoList == null
						|| timelineInfoList.size() == 0) {
					Toast.makeText(TimelineActivity.this,
							getString(R.string.msgnull), Toast.LENGTH_SHORT)
							.show();
				}
			}
			if ("[]".equals(message) || "null".equals(message)) {
				Toast.makeText(TimelineActivity.this,
						getString(R.string.msgnull), Toast.LENGTH_SHORT).show();
			}
			if (!"200".equals(statusCode)) {
				if ("401".equals(statusCode)
						&& commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
					Toast.makeText(TimelineActivity.this,
							getResources().getString(R.string.new_token),
							Toast.LENGTH_SHORT).show();
					if (IGeneral.SERVICE_NAME_SINA.equals(statusData
							.getCurrentService())) {
						Intent intent = new Intent(TimelineActivity.this,
								AddSinaAccountActivity.class);
						startActivity(intent);
					} else if (IGeneral.SERVICE_NAME_WANGYI.equals(statusData
							.getCurrentService())) {
						Intent intent = new Intent(TimelineActivity.this,
								AddWangyiAccountActivity.class);
						startActivity(intent);
					}
					finish();
				} else {
					Toast.makeText(
							TimelineActivity.this,
							ErrorMessage.getErrorMessage(TimelineActivity.this,
									statusCode), Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

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

					userImageMap.put(uid, bitmap);

					// Download Size
					size = size - 1;

					if (size == 0) {

						// Start Auto Refresh
						// if (autoRefresh != null && !autoRefresh.isAlive()
						// && autoRefreshFlag) {
						// autoRefresh.start();
						// }

						listView.setClickable(true);
						// setProgressBarIndeterminateVisibility(false);
						closeProgressDialog();
						// User Image
						loadUserImage(currentList);

						// Auto Translation
						// autoTranslate(currentList);
					}
				}
				System.gc();
			} catch (OutOfMemoryError e) {
				System.gc();

				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.image);
				userImageMap.put(uid, bitmap);

				// Download Size
				size = size - 1;

				if (size == 0) {

					// Start Auto Refresh
					// if (autoRefresh != null && !autoRefresh.isAlive()
					// && autoRefreshFlag) {
					// autoRefresh.start();
					// }

					listView.setClickable(true);
					// setProgressBarIndeterminateVisibility(false);
					closeProgressDialog();
					// User Image
					loadUserImage(currentList);

					// Auto Translation
					// autoTranslate(currentList);
				}
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
		btnlbs = (Button) findViewById(R.id.main_lbs);
		// dm
		btnDMSend = (Button) findViewById(R.id.direct_message_send);
		btnDMReceived = (Button) findViewById(R.id.direct_message_received);
		linearTab = (LinearLayout) findViewById(R.id.linear_layout_tab);
		relativeRight = (RelativeLayout) findViewById(R.id.relativeLayout_right);
		layout_bottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);

		// count
		homecount = (TextView) findViewById(R.id.home_count);
		atcount = (TextView) findViewById(R.id.at_count);
		directcount = (TextView) findViewById(R.id.direct_count);
		commentcount = (TextView) findViewById(R.id.comment_count);
		followercount = (TextView) findViewById(R.id.profile_count);
		statecount = (TextView) findViewById(R.id.state_count);
		retweet_of_me = (TextView) findViewById(R.id.retweet_by_me_count);

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
		btnlbs.setOnClickListener(this);
		btnlbs.setVisibility(View.GONE);

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
			adapter = new SimpleAdapter(this, data,
					R.layout.sina_timeline_layout_list_item, new String[] {
							"screenName", "status", "webStatus",
							"retweetStatus", "userImage", "time",
							"retweetedScreenNameStatus", "verified", "web",
							"webRetweet", "important_level", "retweetCount",
							"commentCount", "moreTweets", "notify" },
					new int[] { R.id.sina_screen_name, R.id.sina_status,
							R.id.web_status, R.id.web_retweet_status,
							R.id.sina_user_image, R.id.sina_update_time,
							R.id.retweeted_screen_name_status,
							R.id.sina_user_verified, R.id.web_view_status,
							R.id.web_view_retweet_status,
							R.id.important_level_view, R.id.text_retweet_count,
							R.id.text_comment_count, R.id.text_get_more_tweets,
							R.id.linear_layout_time_line });
		} else {
			adapter = new SimpleAdapter(this, data,
					R.layout.basic_timeline_layout_list_item, new String[] {
							"screenName", "status", "userImage", "time", "web",
							"level", "moreTweets", "notify" }, new int[] {
							R.id.screen_name, R.id.status, R.id.user_image,
							R.id.update_time, R.id.web_view_status,
							R.id.level_view, R.id.text_get_more_tweets,
							R.id.linear_layout_time_line });
		}

		listView.setAdapter(adapter);
		notificationSharefer = getSharedPreferences("notify", 0);
		notificationEditor = notificationSharefer.edit();

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
		translationDataList = crowdroidApplication.getTranslationList();
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
		autoRefresh = new AutoRefreshHandler();
		// // Start Auto Refresh
		if (autoRefresh != null && !autoRefresh.isAlive()) {
			autoRefresh.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
		isBackgroundNotificationFlag = true;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isBackgroundNotificationFlag = false;
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
		NotificationService.homecount = 0;
		NotificationService.followercount = 0;
		NotificationService.followerTwittercount = 0;
		NotificationService.atcount = 0;
		NotificationService.statecount = 0;
		NotificationService.directcount = 0;
		NotificationService.Commentcount = 0;
		NotificationService.retweetOfMecount = 0;
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
		} else if (commType == CommHandler.TYPE_GET_HOME_TIMELINE
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			confirmLogoutDialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
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

					isRefreshFromAutoRefresh = false;

					lastRefreshTime = System.currentTimeMillis();

					listView.setClickable(false);
					// setProgressBarIndeterminateVisibility(true);
					showProgressDialog();

					// Prepare Parameters
					Map<String, Object> parameters;
					parameters = new HashMap<String, Object>();
					parameters.put("page", currentPage);
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TWITTER)) {
						parameters.put("since_id", cursor_id);
					}
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)
							&& commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE) {
						Bundle bundle = this.getIntent().getExtras();
						column_id = bundle.getString("id");

						parameters.put("column_id", column_id);
					}
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)
							&& commType == CommHandler.TYPE_GET_USER_COLUMN_TIME_LINE) {
						Bundle bundle = this.getIntent().getExtras();
						column_id = bundle.getString("uid");

						parameters.put("user_id", column_id);
					}
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)
							&& commType == CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE) {
						Bundle bundle = this.getIntent().getExtras();
						parameters.put("type", bundle.getString("type"));
					}
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)
							&& (commType == CommHandler.TYPE_GET_STATUS_TIMELINE || commType == CommHandler.TYPE_GET_BLOG_TIMELINE)) {

						Bundle bundle = this.getIntent().getExtras();
						uid = bundle.getString("uid");
						headUrl = bundle.getString("head_url");
						userName = bundle.getString("name");
						parameters.put("id", uid);
					}
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)
							&& commType == CommHandler.TYPE_GET_AREA_TIMELINE) {
						// Tencent area timeline
						parameters.put("stateCode", getIntent().getExtras()
								.get("stateCode"));
						parameters.put("cityCode",
								getIntent().getExtras().get("cityCode"));
					}
					parameters.put("uid", statusData.getCurrentUid());
					if (accountData != null) {
						parameters.put("screen_name",
								accountData.getUserScreenName());
					}
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							commType, apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
					.getCurrentService())) {
				// Prepare Parameters
				Map<String, Object> parameter;
				parameter = new HashMap<String, Object>();
				switch (commType) {
				case CommHandler.TYPE_GET_HOME_TIMELINE: {
					parameter.put("type", "5");
					TencentCommHandler.clearUnreadMessage(parameter);
					break;
				}
				case CommHandler.TYPE_GET_AT_MESSAGE: {
					parameter.put("type", "6");
					TencentCommHandler.clearUnreadMessage(parameter);
					break;
				}
				case CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE: {
					parameter.put("type", "7");
					TencentCommHandler.clearUnreadMessage(parameter);
					break;
				}
				default: {
					// Something wrong.
				}
				}

			}

		} else if (name.getShortClassName().equals(DOWNLOAD_SERVICE_NAME)) {
			downloadServiceInterface = DownloadServiceInterface.Stub
					.asInterface(service);
		} else {
			translationServiceInterface = TranslationServiceInterface.Stub
					.asInterface(service);
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
		downloadServiceInterface = null;
		translationServiceInterface = null;
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
			// if (commType != CommHandler.TYPE_GET_HOME_TIMELINE) {
			Intent home = new Intent(TimelineActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			// }
			NotificationService.homecount = 0;
			break;
		}
		case R.id.tools_bottom_new: {
			Intent tweet = new Intent(TimelineActivity.this,
					SendMessageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("action", "timeline");
			bundle.putString("target", "");
			tweet.putExtras(bundle);
			startActivity(tweet);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_app: {
			Intent app = null;
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				app = new Intent(TimelineActivity.this,
						SNSDiscoveryActivity.class);
			} else {
				app = new Intent(TimelineActivity.this, DiscoveryActivity.class);
			}
			app.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(app);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			break;
		}
		case R.id.tools_bottom_profile: {
			Intent intent = new Intent(TimelineActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", "");
			bundle.putString("user_name", "");
			bundle.putString("uid", "");
			if (NotificationService.followercount > 0) {

				bundle.putString("followercount",
						String.valueOf(NotificationService.followercount));
			}
			if (NotificationService.followerTwittercount > 0) {
				bundle.putString("followercount", String
						.valueOf(NotificationService.followerTwittercount));
			}

			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
			NotificationService.followercount = 0;
			NotificationService.followerTwittercount = 0;
			break;
		}
		case R.id.tools_bottom_more: {
			Intent more = new Intent(TimelineActivity.this,
					MoreFunctionActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", accountData.getUserScreenName());
			more.putExtras(bundle);
			more.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
			Intent at = new Intent(TimelineActivity.this,
					AtMessageTimelineActivity.class);
			at.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(at);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			NotificationService.atcount = 0;
			break;
		}
		case R.id.main_my_timeline: {
			Intent my = new Intent(TimelineActivity.this,
					MyTimelineActivity.class);
			my.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(my);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			NotificationService.statecount = 0;
			break;
		}
		case R.id.main_direct: {
			Intent direct = new Intent(TimelineActivity.this,
					DirectMessageReceiveActivity.class);
			direct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(direct);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			NotificationService.directcount = 0;
			break;
		}
		case R.id.main_comment: {
			Intent comment = new Intent(TimelineActivity.this,
					CommentTimelineActivity.class);
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(comment);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			NotificationService.CommentCfbcount = 0;
			break;
		}
		case R.id.main_retweet_by_me: {
			Intent intent = new Intent(TimelineActivity.this,
					RetweetOfMeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			NotificationService.retweetOfMecount = 0;
			break;
		}
		case R.id.direct_message_send: {
			btnDMSend.setTextColor(Color.WHITE);
			btnDMReceived.setTextColor(Color.BLACK);

			Intent direct = new Intent(TimelineActivity.this,
					DirectMessageSendActivity.class);
			direct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(direct);
			break;
		}
		case R.id.direct_message_received: {
			btnDMSend.setTextColor(Color.BLACK);
			btnDMReceived.setTextColor(Color.WHITE);
			Intent receive = new Intent(TimelineActivity.this,
					DirectMessageReceiveActivity.class);
			receive.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(receive);
			break;
		}
		case R.id.main_schedule: {
			Intent intent = new Intent(TimelineActivity.this,
					ScheduleMonthActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		}
		case R.id.main_lbs: {
			Intent intent = new Intent(TimelineActivity.this,
					LBSlocationActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		}

		// back
		case R.id.head_back: {
			if (commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
				confirmLogoutDialog();
			} else {
				finish();
			}
			break;
		}
		}
	}

	private void initTimeLineView() {

		if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
			btnDMReceived.setText(R.string.album);
			btnDMSend.setText(R.string.blog);
			headName.setText(getTitle());
			btnMention.setBackgroundResource(R.drawable.right_share);
			btnMyTimeline.setBackgroundResource(R.drawable.right_status);
			btnDirect.setBackgroundResource(R.drawable.right_album_blog);
			btnRetweetOfMe
					.setBackgroundResource(R.drawable.right_rencent_visitor);
			if (commType == CommHandler.TYPE_GET_AT_MESSAGE) {
				headName.setText(R.string.share);
			} else if (commType == CommHandler.TYPE_GET_MY_TIME_LINE) {
				headName.setText(R.string.status);
			} else if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
				headName.setText(R.string.album);
			} else if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND) {
				headName.setText(R.string.blog);
			} else if (commType == CommHandler.TYPE_GET_RETWEET_OF_ME_TIME_LINE) {
				headName.setText(R.string.visitors);
			} else if (commType == CommHandler.TYPE_GET_STATUS_TIMELINE) {
				headName.setText(R.string.status_list);
			} else if (commType == CommHandler.TYPE_GET_FAVORITE_LIST) {
				headName.setText(R.string.my_feed);
			} else if (commType == CommHandler.TYPE_GET_LIST_TIMELINE) {
				headName.setText(R.string.my_status);
			} else if (commType == CommHandler.TYPE_GET_USER_STATUS_LIST) {
				headName.setText(R.string.my_sharing);
			}
		} else {
			if (commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE) {
				String columnname = getResources().getString(
						R.string.wangyi_column)
						+ "——" + getIntent().getExtras().get("columnName");
				headName.setText(columnname);
			} else if (commType == CommHandler.TYPE_GET_USER_COLUMN_TIME_LINE) {
				String columnname = getIntent().getExtras().get("name") + "::"
						+ getResources().getString(R.string.wangyi_column);
				headName.setText(columnname);
			} else {
				headName.setText(getTitle());
			}
		}
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
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)) {
			btnDirect.setVisibility(View.GONE);
			// btnlbs.setVisibility(View.VISIBLE);
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
		parameters.put("uid", statusData.getCurrentUid());
		if (service.equals(IGeneral.SERVICE_NAME_WANGYI)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
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
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_WANGYI)
				&& commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE) {
			Bundle bundle = this.getIntent().getExtras();
			column_id = bundle.getString("id");

			parameters.put("column_id", column_id);
		}
		parameters.put("pageTime", pageTime);
		if (accountData != null) {
			parameters.put("screen_name", accountData.getUserScreenName());
		}

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
	private void createListView(ArrayList<TimeLineInfo> timelineInfoList,
			String flag) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		// Prepare ArrayList
		// for (TimeLineInfo timelineInfo : timelineInfoList) {
		for (int i = 0; i < timelineInfoList.size(); i++) {
			TimeLineInfo timelineInfo = timelineInfoList.get(i);
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
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				map.put("important_level",
						getLevelDrawableId(timelineInfo.getImportantLevel()));
				map.put("retweetCount", getString(R.string.retweet_count) + "("
						+ timelineInfo.getRetweetCount() + ")");
				map.put("commentCount", getString(R.string.comment_count) + "("
						+ timelineInfo.getCommentCount() + ")");

			}
			// -----------------------------------------------------------------------------
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)
					|| service.equals(IGeneral.SERVICE_NAME_SINA)
					|| service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
				map.put("pageTime", timelineInfo.getTimeStamp());

				map.put("retweetCount", getString(R.string.retweet_count) + "("
						+ timelineInfo.getRetweetCount() + ")");
				map.put("commentCount", getString(R.string.comment_count) + "("
						+ timelineInfo.getCommentCount() + ")");
			}
			// -----------------------------------------------------------------------------
			if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				map.put("retweetCount", "");
				map.put("commentCount", "");
			}
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				map.put("retweetCount", "");
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

			// map.put("notify", "notify");
			int notifyHome = notificationSharefer.getInt("notify_home", 0);
			if (flag.equals("home")) {
				if (notificationSharefer.getInt("notify_home", 0) > i) {

					map.put("notify", "notify");

				} else if (notificationSharefer.getInt("notify_home", 0) == i) {
					notificationEditor.putInt("notify_home", 0);
					notificationEditor.commit();
					map.put("notify", "");
				} else {
					map.put("notify", "");
				}
			} else if (flag.equals("at")) {
				if (notificationSharefer.getInt("notify_at", 0) > i) {

					map.put("notify", "notify");

				} else if (notificationSharefer.getInt("notify_at", 0) == i) {
					notificationEditor.putInt("notify_at", 0);
					notificationEditor.commit();
					map.put("notify", "");
				} else {
					map.put("notify", "");
				}
			} else if (flag.equals("direct")) {
				if (notificationSharefer.getInt("notify_direct", 0) > i) {

					map.put("notify", "notify");

				} else if (notificationSharefer.getInt("notify_direct", 0) == i) {
					notificationEditor.putInt("notify_direct", 0);
					notificationEditor.commit();
					map.put("notify", "");
				} else {
					map.put("notify", "");
				}
			} else if (flag.equals("retweet_of_me")) {
				if (notificationSharefer.getInt("notify_retweet_of_me", 0) > i) {

					map.put("notify", "notify");

				} else if (notificationSharefer.getInt("notify_retweet_of_me",
						0) == i) {
					notificationEditor.putInt("notify_retweet_of_me", 0);
					notificationEditor.commit();
					map.put("notify", "");
				} else {
					map.put("notify", "");
				}
			} else if (flag.equals("my")) {
				if (notificationSharefer.getInt("notify_state", 0) > i) {

					map.put("notify", "notify");

				} else if (notificationSharefer.getInt("notify_state", 0) == i) {
					notificationEditor.putInt("notify_state", 0);
					notificationEditor.commit();
					map.put("notify", "");
				} else {
					map.put("notify", "");
				}
			} else if (flag.equals("")) {

				map.put("notify", "");

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

	/**
	 * 添加翻页
	 */
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

	/**
	 * 删除翻页
	 */
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

		listView.setClickable(false);
		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();
		// The List For Download
		ArrayList<UserInfo> downloadUserList = new ArrayList<UserInfo>();

		// Prepare List For Download
		for (TimeLineInfo timelineInfo : timelineInfoList) {
			if (!userImageMap.containsKey(timelineInfo.getUserInfo().getUid())
					&& timelineInfo.getUserInfo().getUid() != null) {
				downloadUserList.add(timelineInfo.getUserInfo());
			}
		}

		size = downloadUserList.size();

		if (size == 0) {

			// Start Autorefresh
			// if (autoRefresh != null && !autoRefresh.isAlive()
			// && autoRefreshFlag) {
			// autoRefresh.start();
			// }

			listView.setClickable(true);
			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();
			// User Image
			loadUserImage(timelineInfoList);

			// Auto Translation
			// autoTranslate(currentList);

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

	/**
	 * 關鍵字和用戶過濾功能
	 * 
	 * @param timelineInfoList
	 */
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

	/**
	 * 通知功能，設置最新的微博id
	 * 
	 * @param type
	 * @param timeLineInfos
	 */
	private void setNewestMessageId(int type,
			ArrayList<TimeLineInfo> timeLineInfos) {

		long newestMessageId = 0;

		for (TimeLineInfo timeLineInfo : timeLineInfos) {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)
					&& commType == CommHandler.TYPE_GET_AT_MESSAGE) {
				if (Long.valueOf(timeLineInfo.getPostId()) > newestMessageId) {
					newestMessageId = Long.valueOf(timeLineInfo.getPostId());
				}
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_WANGYI)) {
				if (commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
					if (Long.valueOf(timeLineInfo.getStatusId()) > newestMessageId) {
						newestMessageId = Long.valueOf(timeLineInfo
								.getStatusId());
					}
				} else if (commType == CommHandler.TYPE_GET_AT_MESSAGE) {
					if (Long.valueOf(timeLineInfo.getStatusId()) > newestMessageId) {
						newestMessageId = Long.valueOf(timeLineInfo
								.getStatusId());
					}
				} else if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
					if (Long.valueOf(timeLineInfo.getStatusId()) > newestMessageId) {
						newestMessageId = Long.valueOf(timeLineInfo
								.getStatusId());
					}
				}
			} else {
				if (Long.valueOf(timeLineInfo.getMessageId()) > newestMessageId) {
					newestMessageId = Long.valueOf(timeLineInfo.getMessageId());
				}
			}
		}

		switch (type) {
		case CommHandler.TYPE_GET_HOME_TIMELINE: {
			statusData.setNewestGeneralMessageId(String
					.valueOf(newestMessageId));
			accountData
					.setLastGeneralMessageId(String.valueOf(newestMessageId));
			break;
		}
		case CommHandler.TYPE_GET_AT_MESSAGE: {
			// RenRen feed share
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				statusData.setNewestFeedShareMessageId(String
						.valueOf(newestMessageId));
				accountData.setLastFeedShareMessageId(String
						.valueOf(newestMessageId));
			}
			// else if (statusData.getCurrentService().equals(
			// IGeneral.SERVICE_NAME_WANGYI)) {
			// statusData.setNewestWangYiAtMessageId(String
			// .valueOf(newestMessageId));
			// accountData.setLastWangYiAtMessageId(String
			// .valueOf(newestMessageId));
			// }
			else {
				statusData
						.setNewestAtMessageId(String.valueOf(newestMessageId));
				accountData.setLastAtMessageId(String.valueOf(newestMessageId));
			}

			break;
		}
		case CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE: {
			// RenRen feed album
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				statusData.setNewestFeedAlbumMessageId(String
						.valueOf(newestMessageId));
				accountData.setLastFeedAlbumMessageId(String
						.valueOf(newestMessageId));
			}
			// else if (statusData.getCurrentService().equals(
			// IGeneral.SERVICE_NAME_WANGYI)) {
			// statusData.setNewestWangYiDirectMessageId(String
			// .valueOf(newestMessageId));
			// accountData.setLastWangYiDirectMessageId(String
			// .valueOf(newestMessageId));
			// }
			else {
				statusData.setNewestDirectMessageId(String
						.valueOf(newestMessageId));
				accountData.setLastDirectMessageId(String
						.valueOf(newestMessageId));
			}
			break;
		}

		case CommHandler.TYPE_GET_RETWEET_OF_ME_TIME_LINE: {
			statusData.setNewestRetweetOfMeMessageId(String
					.valueOf(newestMessageId));
			accountData.setLastRetweetOfMeMessageId(String
					.valueOf(newestMessageId));
			break;
		}
		// renren feed blog
		case CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND: {
			statusData.setNewestFeedBlogMessageId(String
					.valueOf(newestMessageId));
			accountData.setLastFeedBlogMessageId(String
					.valueOf(newestMessageId));
			break;
		}
		// RenRen feed state
		case CommHandler.TYPE_GET_MY_TIME_LINE: {
			statusData.setNewestFeedStatusMessageId(String
					.valueOf(newestMessageId));
			accountData.setLastFeedStatusMessageId(String
					.valueOf(newestMessageId));
			break;
		}
		}

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		AccountList accountList = crowdroidApplication.getAccountList();
		accountList.refreshAccount(accountData);

	}

	private String format(String time) {

		StringBuffer id = new StringBuffer();

		String tmp = time;
		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(tmp);
		while (m.find()) {
			id.append(m.group());
		}

		return id.toString().substring(0, 8);
	}

	/**
	 * 查看私信的操作
	 * 
	 * @param position
	 */
	private void openSelectDialog(final int position) {

		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		CharSequence items[] = getResources().getStringArray(
				R.array.timeline_operation_select);
		dlg.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserInfo user = new UserInfo();
				user = timeLineDataList.get(position).getUserInfo();
				switch (which) {
				case 0: {
					Intent intent = new Intent(TimelineActivity.this,
							SendDMActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("uid", user.getUid());
					bundle.putString("name", user.getScreenName());
					bundle.putString("user_name", user.getUserName());
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				}
				case 1: {
					Intent intent = new Intent(TimelineActivity.this,
							ProfileActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("uid", user.getUid());
					bundle.putString("name", user.getScreenName());
					bundle.putString("user_name", user.getUserName());
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				}
				}
			}
		}).create().show();
	}

	/**
	 * 確認退出對話框
	 */
	private void confirmLogoutDialog() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(R.string.logout);
		dlg.setMessage(getResources().getString(R.string.wheter_to_logout))
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
								notificationManager.cancelAll();
								Intent i = new Intent(TimelineActivity.this,
										LoginActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.putExtra("autoLogin", false);
								startActivity(i);
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();

	}

	/**
	 * 自動翻譯功能
	 */
	private void autoTranslate(ArrayList<TimeLineInfo> timelineInfoList) {

		// Auto Translation
		if (settingData.isAutoTranslation()) {

			listView.setClickable(false);
			// setProgressBarIndeterminateVisibility(true);
			showProgressDialog();
			// Prepare Data
			CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
			ArrayList<TranslationData> translationList = crowdroidApplication
					.getTranslationList().getTranslationData(
							statusData.getCurrentService());

			// translateEngine = settingData.getTranslateEngine();

			// The List For Download
			ArrayList<TimeLineInfo> translationTimelineInfoList = new ArrayList<TimeLineInfo>();

			// Prepare List For Download
			for (TimeLineInfo timelineInfo : timelineInfoList) {

				for (TranslationData translationData : translationList) {
					if (translationData.getUserName() != null
							&& timelineInfo.getUserInfo().getScreenName() != null
							&& translationData.getUserName().equals(
									timelineInfo.getUserInfo().getScreenName())) {
						timelineInfo
								.setTranslateFrom(translationData.getFrom());
						timelineInfo.setTranslateTo(translationData.getTo());
						timelineInfo.setTranslateEngine(translationData
								.getEngine());
						translationTimelineInfoList.add(timelineInfo);
					}
				}

			}

			size = translationTimelineInfoList.size();

			if (size == 0) {

				listView.setClickable(true);
				// setProgressBarIndeterminateVisibility(false);
				closeProgressDialog();
				// Create ListView
				createListView(currentList, "home");

				// Add More Tweets If Needs
				addItemForMoreTweets();

			}

			// Download
			for (TimeLineInfo timelineInfo : translationTimelineInfoList) {

				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("query", timelineInfo.getStatus());
				parameters.put("from", timelineInfo.getTranslateFrom());
				parameters.put("to", timelineInfo.getTranslateTo());
				parameters.put("message_id", timelineInfo.getMessageId());

				try {
					translationServiceInterface.request(
							timelineInfo.getTranslateEngine(),
							TranslationHandler.TYPE_TRANSLATE,
							translationServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}

		} else {

			listView.setClickable(true);
			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();
			// Create ListView
			createListView(currentList, "home");

			// Add More Tweets If Needs
			addItemForMoreTweets();

		}

	}

	/**
	 * Delete Timeline Information By Message Id<br>
	 */
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
		if (commType == CommHandler.TYPE_GET_AT_MESSAGE) {
			createListView(timeLineInfos, "at");
		} else if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE) {
			createListView(timeLineInfos, "direct");
		} else if (commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
			createListView(timeLineInfos, "home");
		} else if (commType == CommHandler.TYPE_GET_RETWEET_OF_ME_TIME_LINE) {
			createListView(timeLineInfos, "retweet_of_me");
		}

		// Add More Tweets If Needs
		addItemForMoreTweets();

	}

	/**
	 * 顯示微博的級別
	 */
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

	public String getPassWordDialog() {

		AlertDialog.Builder alert = new AlertDialog.Builder(
				TimelineActivity.this);
		alert.setTitle("密码");

		final EditText input = new EditText(TimelineActivity.this);

		alert.setView(input);

		alert.setPositiveButton("Ok",

		new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

				psw = input.getText().toString();

			}

		});

		alert.setNegativeButton("Cancel",

		new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

			}

		});
		alert.show();
		return psw;

	}

	/**
	 * 显示 progress dialog
	 */
	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(TimelineActivity.this);
		}
		progress.show();
	}

	/**
	 * 關閉 progress dialog
	 */
	private void closeProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress != null) {
			progress.dismiss();
		}
	}

	// ===================================================================================
	//
	// ===================================================================================
	//
	// --------------------------------------------------------------------------
	/**
	 * Handle the Auto Refresh Function<br>
	 */
	//
	// --------------------------------------------------------------------------
	private class AutoRefreshHandler extends Thread {

		boolean refreshFlag = true;

		boolean autoRefreshLock = false;

		//
		// --------------------------------------------------------------------------
		/**
		 * Constructor<br>
		 */
		//
		// --------------------------------------------------------------------------
		public AutoRefreshHandler() {
			super("AutoRefreshHandler");
		}

		//
		// --------------------------------------------------------------------------
		/**
		 * Constructor<br>
		 */
		//
		// --------------------------------------------------------------------------
		public void lockAutoRefresh(boolean lock) {
			autoRefreshLock = lock;
		}

		//
		// --------------------------------------------------------------------------
		/**
		 * Stop this Thread.
		 */
		//
		// --------------------------------------------------------------------------
		public void stopAutoRefresh() {
			refreshFlag = false;
		}

		//
		// --------------------------------------------------------------------------
		/**
		 * Run
		 */
		//
		// --------------------------------------------------------------------------
		public void run() {

			while (refreshFlag) {

				// Get Refresh Time
				Long refreshTime = Long.valueOf(settingData.getRefreshTime()) * 60 * 1000;

				// get interval
				long intervalTime = System.currentTimeMillis()
						- lastRefreshTime;

				// Refresh
				// if (!autoRefreshLock && intervalTime > refreshTime
				// && currentPage == 1
				// && !NotificationService.isNotifyChecking) {
				if (!NotificationService.isNotifyChecking) {
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

	// AsyncTask
	// class AsyncAutoRefresh extends AsyncTask<String, Integer, String> {
	// @Override
	// protected void onCancelled() {
	// super.onCancelled();
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	//
	// Long refreshTime = Long.valueOf(settingData.getRefreshTime()) * 60 *
	// 1000;
	//
	// try {
	// Thread.sleep(refreshTime);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// // Clear Data
	// data.clear();
	// currentList.clear();
	// timeLineDataList.clear();
	// adapter.notifyDataSetChanged();
	//
	// // Set Page
	// currentPage = 0;
	// isRefreshFromAutoRefresh = true;
	// }
	//
	// @Override
	// protected void onProgressUpdate(Integer... values) {
	// super.onProgressUpdate(values);
	//
	// }
	//
	// @Override
	// protected String doInBackground(String... params) {
	//
	// // Get Refresh Time
	// // Long refreshTime = Long.valueOf(settingData.getRefreshTime()) * 60 *
	// //1000;
	//
	// // get interval
	// // long intervalTime = System.currentTimeMillis() - lastRefreshTime;
	//
	// // if(intervalTime > refreshTime
	// // && currentPage == 1
	// // && !NotificationService.isNotifyChecking){
	// // progressBar.setProgress(0);
	// // try {
	// // Thread.sleep(refreshTime);
	// // } catch (InterruptedException e) {
	// // e.printStackTrace();
	// // }
	// refresh();
	// // autoRefresh();
	// // progressBar.setProgress(100);
	//
	// // Adapter.notifyDataSetChanged();
	// // 执行添加后不能调用 Adapter.notifyDataSetChanged()更新UI，因为与UI不是同线程
	// // 下面的onPostExecute方法会在doBackground执行后由UI线程调用
	// //params得到的是一个数组，params[0]在这里是"0",params[1]是"第1项"
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// // closeProgressDialog();
	// adapter.notifyDataSetChanged();
	// // 执行完毕，更新UI
	// }
	//
	// }
	//

}