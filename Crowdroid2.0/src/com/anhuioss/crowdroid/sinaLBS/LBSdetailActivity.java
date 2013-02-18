package com.anhuioss.crowdroid.sinaLBS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.CameraActivity;
import com.anhuioss.crowdroid.activity.DetailTweetActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.MoreFunctionActivity;
import com.anhuioss.crowdroid.activity.PictureshowActivity;
import com.anhuioss.crowdroid.activity.PreviewImageActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.activity.RecorderActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.POIinfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.AtUserSelectDialog;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.LongTweetDialog;
import com.anhuioss.crowdroid.dialog.MultiTweetSelectorDialog;
import com.anhuioss.crowdroid.dialog.TranslateDialog;
import com.anhuioss.crowdroid.dialog.UrlShortenDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.operations.DetailBlogActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;
import com.anhuioss.crowdroid.util.TagAnalysis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LBSdetailActivity extends BasicActivity implements
		ServiceConnection, OnClickListener, TextWatcher {

	public static final String API_SERVICE_NAME = ".communication.ApiService";
	private TabHost tabHost;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private SimpleAdapter adapter;

	protected ArrayList<TimeLineInfo> timeLineDataList = new ArrayList<TimeLineInfo>();

	protected ArrayList<UserInfo> userDataList = new ArrayList<UserInfo>();

	protected ArrayList<TimeLineInfo> currentList;

	public static ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();

	protected POIinfo poiinfo;

	private int commType;

	private int currentPage = 1;

	private StatusData statusData;

	private AccountData accountData;

	SettingData settingData;

	private CrowdroidApplication crowdroidApplication;

	/** Auto Refresh Timer */
	// AutoRefreshHandler autoRefresh;

	/** Max Count */
	private int MAX_TEXT_COUNT = 140;

	private int MAX_CFB_TEXT_COUNT = 140;

	private int MAX_TEXT_TWEETER_COUNT = 0;

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

	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private static boolean isRefreshFromAutoRefresh = false;

	ProgressDialog pd = null;

	ListView timelineList = null;

	ListView neartimelineList = null;

	ListView timelineList1 = null;

	ListView neartimelineList1 = null;

	ListView timelineList2 = null;

	ListView neartimelineList2 = null;

	GridView timelineList3 = null;

	GridView neartimelineList3 = null;

	Button here = null;

	Button near = null;

	Button near_n = null;

	Button near_n01 = null;

	Button near_n03 = null;

	Button here1 = null;

	Button near1 = null;

	Button here2 = null;

	Button near2 = null;

	Button here3 = null;

	Button near3 = null;

	// head
	private Button headerBack = null;

	private Button headerRefresh = null;

	private TextView headName = null;

	// bottom
	// private Button btnHome = null;
	//
	// private Button btnNewTweet = null;
	//
	// private Button btnDiscover = null;
	//
	// private Button btnProfile = null;
	//
	// private Button btnMore = null;
	// send
	private SharedPreferences sharePreference;

	private SharedPreferences.Editor editor;

	ImageView image_Preview;

	private String imageUrl;

	/** Upload Message */
	// TextView uploadMessage;

	TextView head_name;

	/** File Path */

	TextView filePath;

	/** Text */
	public static AutoCompleteTextView updateText;

	/** Translation */
	Button translationButton;

	/** Upload Image */
	Button uploadImageButton;

	/** Shorten URL */
	Button shortenUrlButton;

	/** Long Tweet */
	Button longTweetButton;

	Button emotionButton;

	Button cameraButton;

	Button btn_back;

	Button btn_home;

	// Button priorityButton;

	LinearLayout priorityTextColor;

	TextView priorityText;

	Button recorderButton;

	Button addTrends;

	Button atButton;
	//
	// WebView images;

	/** Multi Tweet */
	CheckBox multiTweetChecked;

	/** Retweet */
	CheckBox retweetCheckeBox;

	/** Count For Character */
	TextView countText;

	// TextView countText_comment;

	/** Confirm */
	Button send;

	/** Cancel */
	Button cancelButton;

	private LinearLayout ly;

	private AlertDialog previewDialog;

	String action = null;

	// AutoCompleteTextView cText = null;
	//
	// Button cButton = null;

	// end send

	TextView location_name = null;

	TextView location_des = null;

	WebView location_image = null;

	private float lat;

	private float lon;

	private ApiServiceInterface apiServiceInterface;

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
						IGeneral.SERVICE_NAME_SINA)) {
					if (commType == CommHandler.TYPE_GET_LBS_HERE_TIMELINE
							|| commType == CommHandler.TYPE_GET_LBS_NEAR_TIMELINE) {
						Intent detail = new Intent(LBSdetailActivity.this,
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
					} else if (commType == CommHandler.TYPE_GET_LBS_HERE_USER
							|| commType == CommHandler.TYPE_GET_LBS_NEAR_USER) {
						Intent detail = new Intent(LBSdetailActivity.this,
								ProfileActivity.class);
						Bundle bundle = new Bundle();
						// bundle.putSerializable("timelineinfo",
						// timeLineDataList.get(position));
						bundle.putString("name", userDataList.get(position)
								.getScreenName());
						bundle.putString("uid", userDataList.get(position)
								.getUid());
						bundle.putString("user_name", userDataList
								.get(position).getUserName());
						detail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						detail.putExtras(bundle);
						startActivity(detail);
					} else if (commType == CommHandler.TYPE_GET_LBS_HERE_PHOTO
							|| commType == CommHandler.TYPE_GET_LBS_NEAR_PHOTO) {
						String url = timeLineDataList.get(position).getStatus()
								.split("\n")[1];
						StringBuffer previewHtmlData = new StringBuffer();
						previewHtmlData
								.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
						previewHtmlData.append("<center>");
						previewHtmlData
								.append("<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
								.append(url).append("' />");
						previewHtmlData.append("<center></body></html>");
						if (previewHtmlData.length() > 0) {
							Intent intent = new Intent(LBSdetailActivity.this,
									PreviewImageActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("url", previewHtmlData.toString());
							// bundle.putString("user_name",
							// timeLineDataList.get(position).getUserInfo()
							// .getUserName());
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					} else {

					}
				}

			}
		}
	};

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {

				// Parser
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				ParseHandler parseHandler = new ParseHandler();

				if (type == CommHandler.TYPE_GET_EMOTION) {

					emotionList = (ArrayList<EmotionInfo>) parseHandler.parser(
							service, type, statusCode, message);
					if (!emotionList.isEmpty()) {
						// showEmotionsDialog();
						initEmotionView(emotionList);
					}

				}
				if (commType == CommHandler.TYPE_GET_LBS_HERE_TIMELINE
						|| commType == CommHandler.TYPE_GET_LBS_NEAR_TIMELINE
						|| commType == CommHandler.TYPE_GET_LBS_HERE_COMMENT
						|| commType == CommHandler.TYPE_GET_LBS_NEAR_COMMENT
						|| commType == CommHandler.TYPE_GET_LBS_HERE_PHOTO
						|| commType == CommHandler.TYPE_GET_LBS_NEAR_PHOTO) {
					timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);
				}
				if (commType == CommHandler.TYPE_GET_LBS_HERE_USER
						|| commType == CommHandler.TYPE_GET_LBS_NEAR_USER) {
					userInfoList = (ArrayList<UserInfo>) parseHandler.parser(
							service, type, statusCode, message);
				}
				if (commType == CommHandler.TYPE_LBS_SEND_PHOTO
						|| commType == CommHandler.TYPE_LBS_SEND_STATUS) {
					Toast.makeText(LBSdetailActivity.this,
							getString(R.string.send_success),
							Toast.LENGTH_SHORT).show();
				}
				if (commType == CommHandler.TYPE_LBS_SEND_COMMENT) {
					Toast.makeText(LBSdetailActivity.this,
							getString(R.string.comment_success),
							Toast.LENGTH_SHORT).show();
				}

				if (timelineInfoList != null && timelineInfoList.size() > 0) {

					// statusFilter(timelineInfoList);
					// if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// if (commType ==
					// CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE
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
					// timeLineDataList = timelineInfoList;
					if (commType == CommHandler.TYPE_GET_LBS_HERE_PHOTO) {
						timeLineDataList.clear();
						timeLineDataList = timelineInfoList;
						createPhotosView(timelineInfoList, timelineList3);
					} else if (commType == CommHandler.TYPE_GET_LBS_NEAR_PHOTO) {
						// createPhotosView(timelineInfoList,
						// neartimelineList3);
						timeLineDataList.clear();
						timeLineDataList = timelineInfoList;
						createPhotosView(timelineInfoList, timelineList3);
					} else {
						createListView(timelineInfoList);
						addItemForMoreTweets();
					}

					//
					// if (timelineInfoList.size() >= 20
					// && !(service.equals(IGeneral.SERVICE_NAME_WANGYI)
					// && (commType ==
					// CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE) || commType ==
					// CommHandler.TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE)) {
					// addItemForMoreTweets();
					// }
					if (timelineInfoList.size() >= 19
							&& service.equals(IGeneral.SERVICE_NAME_TWITTER)
							&& commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
						// addItemForMoreTweets();
					}
					// if (!timelineInfoList.get(timelineInfoList.size() - 1)
					// .equals("")) {
					// cursor_id = timelineInfoList.get(
					// timelineInfoList.size() - 1).getcursor_id();
					// if (commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE
					// || commType == CommHandler.TYPE_GET_USER_COLUMN_TIME_LINE
					// || service
					// .equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// cursor_id = timelineInfoList.get(
					// timelineInfoList.size() - 1).getMessageId();
					// }
					// }
					//
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
					// Toast.makeText(TimelineActivity.this,
					// getString(R.string.permission), Toast.LENGTH_SHORT)
					// .show();
					// } else if (timelineInfoList == null
					// || timelineInfoList.size() == 0) {
					// Toast.makeText(TimelineActivity.this,
					// getString(R.string.msgnull), Toast.LENGTH_SHORT)
					// .show();
				}
				if (userInfoList != null && userInfoList.size() > 0) {

					// statusFilter(timelineInfoList);
					// if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// if (commType ==
					// CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE
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
					// timeLineDataList = timelineInfoList;
					createuserListView(userInfoList);
					addItemForMoreTweets();
					//
					// if (timelineInfoList.size() >= 20
					// && !(service.equals(IGeneral.SERVICE_NAME_WANGYI)
					// && (commType ==
					// CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE) || commType ==
					// CommHandler.TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE)) {
					// addItemForMoreTweets();
					// }
					if (timelineInfoList.size() >= 19
							&& service.equals(IGeneral.SERVICE_NAME_TWITTER)
							&& commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
						// addItemForMoreTweets();
					}
					// if (!timelineInfoList.get(timelineInfoList.size() - 1)
					// .equals("")) {
					// cursor_id = timelineInfoList.get(
					// timelineInfoList.size() - 1).getcursor_id();
					// if (commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE
					// || commType == CommHandler.TYPE_GET_USER_COLUMN_TIME_LINE
					// || service
					// .equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// cursor_id = timelineInfoList.get(
					// timelineInfoList.size() - 1).getMessageId();
					// }
					// }
					//
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
					// Toast.makeText(TimelineActivity.this,
					// getString(R.string.permission), Toast.LENGTH_SHORT)
					// .show();
					// } else if (timelineInfoList == null
					// || timelineInfoList.size() == 0) {
					// Toast.makeText(TimelineActivity.this,
					// getString(R.string.msgnull), Toast.LENGTH_SHORT)
					// .show();
				}
			}
			if ("[]".equals(message) || "null".equals(message)) {
				Toast.makeText(LBSdetailActivity.this,
						getString(R.string.msgnull), Toast.LENGTH_SHORT).show();
			}
			if (!"200".equals(statusCode)) {
				// if ("401".equals(statusCode)
				// && commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
				// Toast.makeText(LBSlocationActivity.this,
				// getResources().getString(R.string.new_token),
				// Toast.LENGTH_SHORT).show();
				// } else {
				Toast.makeText(
						LBSdetailActivity.this,
						ErrorMessage.getErrorMessage(LBSdetailActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
				// }
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sina_lbs_informations);

		Bundle bundle = this.getIntent().getExtras();
		poiinfo = (POIinfo) bundle.getSerializable("poisinfo");
		lat = bundle.getFloat("lat");
		lon = bundle.getFloat("long");
		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(R.string.location_detail);
		// // bottom
		// btnHome = (Button) findViewById(R.id.tools_bottom_home);
		// btnNewTweet = (Button) findViewById(R.id.tools_bottom_new);
		// btnDiscover = (Button) findViewById(R.id.tools_bottom_app);
		// btnProfile = (Button) findViewById(R.id.tools_bottom_profile);
		// btnMore = (Button) findViewById(R.id.tools_bottom_more);

		try {
			RelativeLayout timelineTab = (RelativeLayout) LayoutInflater.from(
					this).inflate(R.layout.style_tabhost, null);
			TextView timelineTabLabel = (TextView) timelineTab
					.findViewById(R.id.tab_label);
			timelineTabLabel.setText(getResources().getString(
					R.string.lbs_timeline));

			RelativeLayout userTab = (RelativeLayout) LayoutInflater.from(this)
					.inflate(R.layout.style_tabhost, null);
			TextView userTabLabel = (TextView) userTab
					.findViewById(R.id.tab_label);
			userTabLabel.setText(getResources().getString(R.string.lbs_user));

			RelativeLayout commentTab = (RelativeLayout) LayoutInflater.from(
					this).inflate(R.layout.style_tabhost, null);
			TextView commentTabLabel = (TextView) commentTab
					.findViewById(R.id.tab_label);
			commentTabLabel.setText(getResources().getString(
					R.string.lbs_commit));

			RelativeLayout picTab = (RelativeLayout) LayoutInflater.from(this)
					.inflate(R.layout.style_tabhost, null);
			TextView picTabLabel = (TextView) picTab
					.findViewById(R.id.tab_label);
			picTabLabel.setText(getResources().getString(R.string.lbs_picture));

			RelativeLayout sendTab = (RelativeLayout) LayoutInflater.from(this)
					.inflate(R.layout.style_tabhost, null);
			TextView sendTabLabel = (TextView) sendTab
					.findViewById(R.id.tab_label);
			sendTabLabel.setText(getResources().getString(R.string.lbs_send));

			tabHost = (TabHost) this.findViewById(R.id.TabHost_info);
			tabHost.setup();

			tabHost.addTab(tabHost.newTabSpec("tab_1")
					.setContent(R.id.LinearLayout1_info)
					.setIndicator(timelineTab));
			tabHost.addTab(tabHost.newTabSpec("tab_2")
					.setContent(R.id.LinearLayout2_info).setIndicator(userTab));
			// tabHost.addTab(tabHost.newTabSpec("tab_3")
			// .setContent(R.id.LinearLayout3_info)
			// .setIndicator(commentTab));
			tabHost.addTab(tabHost.newTabSpec("tab_4")
					.setContent(R.id.LinearLayout4_info).setIndicator(picTab));
			tabHost.addTab(tabHost.newTabSpec("tab_5")
					.setContent(R.id.LinearLayout5_info).setIndicator(sendTab));
			tabHost.setCurrentTab(0);

		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d("EXCEPTION", ex.getMessage());
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				data.clear();
				if (tabId == "tab_3") {
					commType = CommHandler.TYPE_GET_LBS_HERE_COMMENT;
					data.clear();
					refresh();
				} else if (tabId == "tab_5") {

					RelativeLayout l1 = (RelativeLayout) findViewById(R.id.relative_1);
					l1.setVisibility(View.GONE);
				}
			}
		});

		timelineList = (ListView) findViewById(R.id.here_list);
		timelineList.setDivider(null);
		// neartimelineList =(ListView) findViewById(R.id.near_list);
		timelineList1 = (ListView) findViewById(R.id.here_list1);
		timelineList1.setDivider(null);
		neartimelineList1 = (ListView) findViewById(R.id.near_list1);
		// timelineList2 = (ListView) findViewById(R.id.here_list2);
		// neartimelineList2 = (ListView) findViewById(R.id.near_list2);
		timelineList3 = (GridView) findViewById(R.id.lbs_here_gridview);
		// neartimelineList3 = (GridView) findViewById(R.id.lbs_near_gridview);
		here = (Button) findViewById(R.id.here);
		near = (Button) findViewById(R.id.near);
		// LayoutParams lp = near.getLayoutParams();
		near_n = (Button) findViewById(R.id.near_n);
		near_n01 = (Button) findViewById(R.id.near_n01);
		near_n03 = (Button) findViewById(R.id.near_n03);

		here1 = (Button) findViewById(R.id.here1);
		near1 = (Button) findViewById(R.id.near1);
		// here2 = (Button) findViewById(R.id.here2);
		// near2 = (Button) findViewById(R.id.near2);
		here3 = (Button) findViewById(R.id.here3);
		near3 = (Button) findViewById(R.id.near3);
		location_name = (TextView) findViewById(R.id.location_name);
		location_des = (TextView) findViewById(R.id.location_des);
		location_image = (WebView) findViewById(R.id.location_image_web);

		// send
		sharePreference = getSharedPreferences("SHARE_INIT_STATUS", 0);

		// Find Views
		// uploadMessage = (TextView) findViewById(R.id.upload_message);
		filePath = (TextView) findViewById(R.id.file_path);
		updateText = (AutoCompleteTextView) findViewById(R.id.update_text);
		translationButton = (Button) findViewById(R.id.translateButton);
		uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
		shortenUrlButton = (Button) findViewById(R.id.shortenUrlButton);
		longTweetButton = (Button) findViewById(R.id.longTweetButton);
		emotionButton = (Button) findViewById(R.id.emotionButton);
		cameraButton = (Button) findViewById(R.id.cameraButton);
		recorderButton = (Button) findViewById(R.id.recorderButton);
		multiTweetChecked = (CheckBox) findViewById(R.id.multiTweet);
		retweetCheckeBox = (CheckBox) findViewById(R.id.retweet_check_box);
		countText = (TextView) findViewById(R.id.counterText);
		// countText_comment = (TextView)
		// findViewById(R.id.counterText_comment);
		// countText.setText("140");
		send = (Button) findViewById(R.id.send);
		head_name = (TextView) findViewById(R.id.head_Name);

		// images=(WebView) findViewById (R.id.ly3);
		// images.setVerticalScrollBarEnabled(false);
		// images.setHorizontalScrollBarEnabled(true);

		// cText = (AutoCompleteTextView) findViewById(R.id.lbs_update_text);
		// cText.addTextChangedListener(this);
		// cButton = (Button) findViewById(R.id.lbs_comment_send);

		ly = (LinearLayout) findViewById(R.id.ly4);

		priorityTextColor = (LinearLayout) findViewById(R.id.priorityTextColor);
		priorityText = (TextView) findViewById(R.id.priorityText);

		addTrends = (Button) findViewById(R.id.addTrends);
		atButton = (Button) findViewById(R.id.atButton);
		image_Preview = (ImageView) findViewById(R.id.webview_preview);
		btn_back = (Button) findViewById(R.id.head_back);
		btn_home = (Button) findViewById(R.id.head_refresh);
		btn_home.setBackgroundResource(R.drawable.main_home);

		float scale = ((CrowdroidApplication) getApplicationContext())
				.getScaleDensity();
		LayoutParams para = image_Preview.getLayoutParams();
		para.height = 180;
		para.width = 180;
		image_Preview.setLayoutParams(para);

		// btnHome.setOnClickListener(this);
		// btnNewTweet.setOnClickListener(this);
		// btnDiscover.setOnClickListener(this);
		// btnProfile.setOnClickListener(this);
		// btnMore.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);

		updateText.setText("我在#" + poiinfo.gettitle() + "#");

		// Set Listener
		updateText.setOnClickListener(this);
		updateText.addTextChangedListener(this);
		translationButton.setOnClickListener(this);
		uploadImageButton.setOnClickListener(this);
		shortenUrlButton.setOnClickListener(this);
		longTweetButton.setOnClickListener(this);
		emotionButton.setOnClickListener(this);
		cameraButton.setOnClickListener(this);
		recorderButton.setOnClickListener(this);
		multiTweetChecked.setOnClickListener(this);
		send.setOnClickListener(this);
		priorityTextColor.setOnClickListener(this);
		addTrends.setOnClickListener(this);
		atButton.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		// cButton.setOnClickListener(this);

		// end send

		location_name.setText(poiinfo.gettitle());
		location_des.setText(poiinfo.getaddress());
		location_image.loadData(
				"<center><img style='max-height: 60px; max-width: 60px;' src='"
						+ poiinfo.geticon() + "'/></center>", "text/html",
				"UTF-8");
		here.setOnClickListener(this);
		near.setOnClickListener(this);
		near_n.setOnClickListener(this);
		near_n01.setOnClickListener(this);
		near_n03.setOnClickListener(this);
		here1.setOnClickListener(this);
		near1.setOnClickListener(this);
		// here2.setOnClickListener(this);
		// near2.setOnClickListener(this);
		here3.setOnClickListener(this);
		near3.setOnClickListener(this);

		timelineList.setOnItemClickListener(onItemClickListener);
		// neartimelineList.setOnItemClickListener(onItemClickListener);
		timelineList1.setOnItemClickListener(onItemClickListener);
		// neartimelineList1.setOnItemClickListener(onItemClickListener);
		// timelineList2.setOnItemClickListener(onItemClickListener);
		// neartimelineList2.setOnItemClickListener(onItemClickListener);
		timelineList3.setOnItemClickListener(onItemClickListener);
		// neartimelineList3.setOnItemClickListener(onItemClickListener);
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		service = statusData.getCurrentService();
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
		// adapter = new SimpleAdapter(this, data,
		// R.layout.basic_timeline_layout_list_item, new String[] {
		// "screenName", "status", "userImage", "time", "web",
		// "level", "moreTweets" }, new int[] {
		// R.id.screen_name, R.id.status, R.id.user_image,
		// R.id.update_time, R.id.web_view_status,
		// R.id.level_view, R.id.text_get_more_tweets });
		// adapter = new SimpleAdapter(this, data,
		// R.layout.pois_item, new String[] {
		// "title", "address",
		// "num"}, new int[] {
		// R.id.sina_pois_name, R.id.sina_pois_des,
		// R.id.sina_pois_num});
		timelineList.setAdapter(adapter);
		// neartimelineList.setAdapter(adapter);
		timelineList1.setAdapter(adapter);
		// neartimelineList1.setAdapter(adapter);
		// timelineList2.setAdapter(adapter);
		// neartimelineList2.setAdapter(adapter);
		// timelineList3.setAdapter(adapter);
		// neartimelineList3.setAdapter(adapter);

	}

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

		notifyTextHasChanged();

		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);

		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		unbindService(this);
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
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
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
		parameters.put("uid", statusData.getCurrentUid());
		// if (service.equals(IGeneral.SERVICE_NAME_WANGYI)
		// || service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
		// parameters.put("since_id", cursor_id);
		// }

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
		// if
		// (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_WANGYI)
		// && commType == CommHandler.TYPE_GET_COLUMN_TIME_LINE) {
		// Bundle bundle = this.getIntent().getExtras();
		// column_id = bundle.getString("id");
		//
		// parameters.put("column_id", column_id);
		// }
		parameters.put("pageTime", pageTime);
		if (accountData != null) {
			parameters.put("screen_name", accountData.getUserScreenName());
		}
		if (commType == CommHandler.TYPE_GET_LBS_NEAR_TIMELINE
				|| commType == CommHandler.TYPE_GET_LBS_NEAR_USER
				|| commType == CommHandler.TYPE_GET_LBS_NEAR_PHOTO) {
			parameters.put("lat", lat);
			parameters.put("long", lon);
		}

		parameters.put("poiid", poiinfo.getpoiid());

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					commType, apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		// adapter = new SimpleAdapter(this, data,
		// R.layout.pois_item, new String[] {
		// "title", "address",
		// "num"}, new int[] {
		// R.id.sina_pois_name, R.id.sina_pois_des,
		// R.id.sina_pois_num});
		// timelineList.setAdapter(adapter);
	}

	protected void createuserListView(ArrayList<UserInfo> timelineInfoList) {
		// TODO Auto-generated method stub
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		userDataList.clear();

		for (UserInfo timelineInfo : timelineInfoList) {

			userDataList.add(timelineInfo);
			Map<String, Object> map;
			map = new HashMap<String, Object>();

			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				map.put("screenName", timelineInfo.getScreenName());
				map.put("webStatus", timelineInfo.getDescription());
				map.put("userImage", timelineInfo.getUserImageURL());
			}
			addDatas.add(map);

		}
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}
		adapter.notifyDataSetChanged();
	}

	private void createPhotosView(ArrayList<TimeLineInfo> timelineInfoList,
			GridView gridview) {

		ArrayList<HashMap<String, String>> listImageItem = new ArrayList<HashMap<String, String>>();

		for (TimeLineInfo timelineInfo : timelineInfoList) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("itemImage", timelineInfo.getStatus());
			listImageItem.add(map);
		}
		// SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
		// R.layout.activity_discovery_gridview_item, new String[] {
		// "ItemImage"}, new int[] { R.id.ItemImage,});
		gridview.setAdapter(new GridViewAdapter(this, timelineInfoList,
				listImageItem, commType));

		// Add
		// for (Map<String, String> addData : listImageItem) {
		// data.add(addData);
		// }
	}

	private void createListView(ArrayList<TimeLineInfo> timelineInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();
		timeLineDataList.clear();
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
					// if ((service.equals(IGeneral.SERVICE_NAME_RENREN) &&
					// commType == CommHandler.TYPE_GET_STATUS_TIMELINE)
					// || (service.equals(IGeneral.SERVICE_NAME_RENREN) &&
					// commType == CommHandler.TYPE_GET_BLOG_TIMELINE)) {
					// map.put("screenName", userName);
					// } else {
					map.put("screenName", timelineInfo.getUserInfo()
							.getScreenName() == null ? "" : timelineInfo
							.getUserInfo().getScreenName());
					// }
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
					// map.put("userImage", headUrl);
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
			// if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS))
			// {
			// map.put("important_level",
			// getLevelDrawableId(timelineInfo.getImportantLevel()));
			// map.put("retweetCount", getString(R.string.retweet_count) + "("
			// + timelineInfo.getRetweetCount() + ")");
			// map.put("commentCount", getString(R.string.comment_count) + "("
			// + timelineInfo.getCommentCount() + ")");
			//
			// }
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
	public void onClick(View view) {
		switch (view.getId()) {
		// case R.id.tools_bottom_home: {
		// // if (commType != CommHandler.TYPE_GET_HOME_TIMELINE) {
		// Intent home = new Intent(LBSdetailActivity.this,
		// HomeTimelineActivity.class);
		// home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(home);
		// overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
		// // }
		// break;
		// }
		// case R.id.tools_bottom_new: {
		// Intent tweet = new Intent(LBSdetailActivity.this,
		// SendMessageActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("action", "timeline");
		// bundle.putString("target", "");
		// tweet.putExtras(bundle);
		// startActivity(tweet);
		// overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
		// break;
		// }
		// case R.id.tools_bottom_app: {
		// Intent app = null;
		// if (statusData.getCurrentService().equals(
		// IGeneral.SERVICE_NAME_RENREN)) {
		// app = new Intent(LBSdetailActivity.this,
		// SNSDiscoveryActivity.class);
		// } else {
		// app = new Intent(LBSdetailActivity.this, DiscoveryActivity.class);
		// }
		// app.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(app);
		// overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
		// break;
		// }
		// case R.id.tools_bottom_profile: {
		// Intent intent = new Intent(LBSdetailActivity.this,
		// ProfileActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("name", "");
		// bundle.putString("user_name", "");
		// bundle.putString("uid", "");
		// intent.putExtras(bundle);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
		// break;
		// }
		// case R.id.tools_bottom_more: {
		// Intent more = new Intent(LBSdetailActivity.this,
		// MoreFunctionActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("name", accountData.getUserScreenName());
		// more.putExtras(bundle);
		// more.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(more);
		// overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
		// break;
		// }
		case R.id.head_refresh: {
			// cursor_id = "";
			// Clear Data
			// data.clear();
			// currentList.clear();
			// timeLineDataList.clear();
			// adapter.notifyDataSetChanged();
			// // Set Page
			// currentPage = 0;
			// // Cancel Notification
			// NotificationManager notificationManager = (NotificationManager)
			// getSystemService(NOTIFICATION_SERVICE);
			// notificationManager.cancel(1);
			// isRefreshFromAutoRefresh = false;
			// // Refresh
			// refresh();
			Intent home = new Intent(LBSdetailActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		}
		case R.id.head_back: {
			// if (commType == CommHandler.TYPE_GET_HOME_TIMELINE) {
			// confirmLogoutDialog();
			// } else {
			finish();
			// }
			break;
		}
		case R.id.here: {
			commType = CommHandler.TYPE_GET_LBS_HERE_TIMELINE;

			// if(timelineList.getVisibility()==View.GONE){
			near.setVisibility(View.GONE);
			near_n.setVisibility(View.VISIBLE);
			timelineList.setVisibility(View.VISIBLE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// RelativeLayout.LayoutParams lp = new
			// RelativeLayout.LayoutParams(50,50);
			// lp.addRule(RelativeLayout.ALIGN_TOP);
			//
			// near.setLayoutParams(lp);
			// }else{
			// timelineList.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}
		case R.id.near: {
			commType = CommHandler.TYPE_GET_LBS_NEAR_TIMELINE;

			// if(timelineList.getVisibility()==View.GONE){
			timelineList.setVisibility(View.VISIBLE);
			near.setVisibility(View.VISIBLE);
			near_n.setVisibility(View.GONE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// neartimelineList.setVisibility(View.GONE);
			// data.clear();
			// }
			//

			break;
		}
		case R.id.near_n: {
			commType = CommHandler.TYPE_GET_LBS_NEAR_TIMELINE;

			// if(neartimelineList.getVisibility()==View.GONE){
			// neartimelineList.setVisibility(View.VISIBLE);
			near.setVisibility(View.VISIBLE);
			near_n.setVisibility(View.GONE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// neartimelineList.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}
		case R.id.here1: {
			commType = CommHandler.TYPE_GET_LBS_HERE_USER;

			// if(timelineList1.getVisibility()==View.GONE){
			near1.setVisibility(View.GONE);
			near_n01.setVisibility(View.VISIBLE);
			timelineList1.setVisibility(View.VISIBLE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// timelineList1.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}
		case R.id.near1: {
			commType = CommHandler.TYPE_GET_LBS_NEAR_USER;

			// if(neartimelineList1.getVisibility()==View.GONE){
			neartimelineList1.setVisibility(View.VISIBLE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// neartimelineList1.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}
		case R.id.near_n01: {
			commType = CommHandler.TYPE_GET_LBS_NEAR_USER;

			// if(neartimelineList.getVisibility()==View.GONE){
			// neartimelineList.setVisibility(View.VISIBLE);
			near1.setVisibility(View.VISIBLE);
			near_n01.setVisibility(View.GONE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// neartimelineList.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}
		case R.id.here2: {
			// commType=CommHandler.TYPE_GET_LBS_HERE_COMMENT;
			//
			// if(timelineList2.getVisibility()==View.GONE){
			// timelineList.setVisibility(View.VISIBLE);
			// refresh();
			// }else{
			// timelineList2.setVisibility(View.GONE);
			// }

			break;
		}
		case R.id.near2: {
			// commType=CommHandler.TYPE_GET_LBS_NEAR_COMMENT;
			//
			// if(neartimelineList2.getVisibility()==View.GONE){
			// neartimelineList2.setVisibility(View.VISIBLE);
			// refresh();
			// }else{
			// neartimelineList2.setVisibility(View.GONE);
			// }
			//

			break;
		}
		case R.id.here3: {
			commType = CommHandler.TYPE_GET_LBS_HERE_PHOTO;

			// if(timelineList3.getVisibility()==View.GONE){
			near3.setVisibility(View.GONE);
			near_n03.setVisibility(View.VISIBLE);
			timelineList3.setVisibility(View.VISIBLE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// timelineList3.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}
		case R.id.near3: {
			commType = CommHandler.TYPE_GET_LBS_NEAR_PHOTO;

			// if(timelineList3.getVisibility()==View.GONE){
			timelineList3.setVisibility(View.VISIBLE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// timelineList3.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}
		case R.id.near_n03: {
			commType = CommHandler.TYPE_GET_LBS_NEAR_PHOTO;

			// if(neartimelineList3.getVisibility()==View.GONE){
			near3.setVisibility(View.VISIBLE);
			near_n03.setVisibility(View.GONE);
			timelineList3.setVisibility(View.VISIBLE);
			timeLineDataList.clear();
			data.clear();
			currentPage = 1;
			refresh();
			// }else{
			// neartimelineList3.setVisibility(View.GONE);
			// data.clear();
			// }

			break;
		}

		// send
		case R.id.translateButton: {

			// Translation
			new TranslateDialog(this, updateText).show();
			break;

		}
		case R.id.uploadImageButton: {

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER)) {
				// ly.removeAllViewsInLayout();
				// ly.setVisibility(View.GONE);
				Intent intent = new Intent(LBSdetailActivity.this,
						PictureshowActivity.class);

				startActivityForResult(intent, 2);

			} else {
				openSelectDialog();
			}
			break;

		}
		case R.id.shortenUrlButton: {
			new UrlShortenDialog(this, updateText).show();
			break;
		}
		case R.id.longTweetButton: {

			new LongTweetDialog(this, updateText).show();

			Toast.makeText(this, getResources().getString(R.string.long_tweet),
					Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.emotionButton: {

			int index = updateText.getSelectionStart();// 获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(updateText.getText()).substring(0, index));
			editor.putString(
					"EDIT_INIT_STATUS_BEHIND",
					String.valueOf(updateText.getText()).substring(index,
							updateText.getText().length()));
			editor.commit();

			if (emotionList.isEmpty()) {

				try {

					showProgressDialog();

					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_EMOTION, apiServiceListener,
							new HashMap<String, Object>());

				} catch (RemoteException e) {
					e.printStackTrace();
				}

			} else {
				initEmotionView(emotionList);
				// showEmotionsDialog();

			}
			break;

		}
		case R.id.cameraButton: {

			sharePreference
					.edit()
					.putString("EDIT_INIT_STATUS_CAMERA",
							updateText.getText().toString()).commit();
			CameraActivity.setContextAndEdit(this, updateText);
			Intent intent = new Intent(this, CameraActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("lbs", "lbs");
			intent.putExtras(bundle);
			startActivityForResult(intent, 3);

			break;
		}
		case R.id.recorderButton: {
			RecorderActivity.setContextAndEdit(this, updateText);
			Intent intent = new Intent(this, RecorderActivity.class);
			startActivity(intent);
			// finish();
			break;
		}
		// case R.id.multiTweet: {
		// if (multiTweetChecked.isChecked()) {
		// if (multiTweetAccounts.isEmpty()) {
		// Toast.makeText(LBSdetailActivity.this, "No Accounts!",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// new MultiTweetSelectorDialog(this, multiTweetAccounts)
		// .show();
		// }
		// } else {
		// multiTweetAccountSelected = null;
		// }
		// break;
		// }
		case R.id.atButton: {

			int index = updateText.getSelectionStart();// 获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(updateText.getText()).substring(0, index));
			editor.putString(
					"EDIT_INIT_STATUS_BEHIND",
					String.valueOf(updateText.getText()).substring(index,
							updateText.getText().length()));

			AtUserSelectDialog usd = null;
			usd = new AtUserSelectDialog(this, "send_message_lbs");
			editor.commit();

			usd.setTitle("@" + getString(R.string.insert_at_name));
			usd.show();
			break;
		}
		case R.id.send: {

			// if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
			// .equals(statusData.getCurrentService())) {
			// if(callbackImpl !=null){
			// int maxSize = callbackImpl.getTotalBitmapSize();
			// if(maxSize>6*1024*1024){
			// send.setEnabled(false);
			// break;
			// } else{
			// send.setEnabled(true);
			// }
			// }
			// }

			Intent outerIntent = getIntent();
			String action = outerIntent.getAction();
			String type = outerIntent.getType();

			if (action != null && type != null
					&& Intent.ACTION_SEND.equals(action)
					&& "text/plain".equals(type) && !action.equals("reply")) {
				editor.putString("SHARE_OUTSIDE_CONTENT", "").commit();
			}
			if (("").equals(updateText.getText().toString())) {
				Toast.makeText(LBSdetailActivity.this,
						getResources().getString(R.string.sendmsg_tip),
						Toast.LENGTH_LONG).show();
			} else {
				// if (updateText.getText().toString().contains("@")) {
				// if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// saveHistory("cfb", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_TWITTER)
				// || statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// saveHistory("twitter", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_SINA)) {
				// saveHistory("sina", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_TENCENT)) {
				// saveHistory("tencent", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_SOHU)) {
				// saveHistory("sohu", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_WANGYI)) {
				// saveHistory("wangyi", updateText);
				// }
				// }
				// if (updateText.getText().toString().contains("#")) {
				// if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// saveTrendHistory("cfb", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_TWITTER)
				// || statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// saveTrendHistory("twitter", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_SINA)) {
				// saveTrendHistory("sina", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_TENCENT)) {
				// saveTrendHistory("tencent", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_SOHU)) {
				// saveTrendHistory("sohu", updateText);
				// } else if (statusData.getCurrentService().equals(
				// IGeneral.SERVICE_NAME_WANGYI)) {
				// saveTrendHistory("wangyi", updateText);
				// }
				// }

				// createTweetList();

				// if (action != null && action != "" && action.equals("reply")
				// && !retweetCheckeBox.isChecked()) {
				// showProgressDialog();
				// replyMessage(statusData.getCurrentService());
				// } else if (action != null && action != ""
				// && action.equals("reply")
				// && retweetCheckeBox.isChecked()) {
				// showProgressDialog();
				// retweetMessage(statusData.getCurrentService());
				//
				// }
				//
				// else {
				// if (sendTweetList.size() > 0) {
				// tweet(sendTweetList.get(0));
				// }
				// }

				tweet();
			}

			break;
		}

		case R.id.lbs_comment_send: {

			// if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
			// .equals(statusData.getCurrentService())) {
			// if(callbackImpl !=null){
			// int maxSize = callbackImpl.getTotalBitmapSize();
			// if(maxSize>6*1024*1024){
			// send.setEnabled(false);
			// break;
			// } else{
			// send.setEnabled(true);
			// }
			// }
			// }

			// Intent outerIntent = getIntent();
			// String action = outerIntent.getAction();
			// String type = outerIntent.getType();
			//
			// if (action != null && type != null
			// && Intent.ACTION_SEND.equals(action)
			// && "text/plain".equals(type) && !action.equals("reply")) {
			// editor.putString("SHARE_OUTSIDE_CONTENT", "").commit();
			// }
			// if (("").equals(cText.getText().toString())) {
			// Toast.makeText(LBSdetailActivity.this,
			// getResources().getString(R.string.sendmsg_tip),
			// Toast.LENGTH_LONG).show();
			// } else {
			// // if (updateText.getText().toString().contains("@")) {
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			// // saveHistory("cfb", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TWITTER)
			// // || statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			// // saveHistory("twitter", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_SINA)) {
			// // saveHistory("sina", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TENCENT)) {
			// // saveHistory("tencent", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_SOHU)) {
			// // saveHistory("sohu", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_WANGYI)) {
			// // saveHistory("wangyi", updateText);
			// // }
			// // }
			// // if (updateText.getText().toString().contains("#")) {
			// // if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			// // saveTrendHistory("cfb", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TWITTER)
			// // || statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			// // saveTrendHistory("twitter", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_SINA)) {
			// // saveTrendHistory("sina", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TENCENT)) {
			// // saveTrendHistory("tencent", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_SOHU)) {
			// // saveTrendHistory("sohu", updateText);
			// // } else if (statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_WANGYI)) {
			// // saveTrendHistory("wangyi", updateText);
			// // }
			// // }
			//
			// // createTweetList();
			//
			// // if (action != null && action != "" && action.equals("reply")
			// // && !retweetCheckeBox.isChecked()) {
			// // showProgressDialog();
			// // replyMessage(statusData.getCurrentService());
			// // } else if (action != null && action != ""
			// // && action.equals("reply")
			// // && retweetCheckeBox.isChecked()) {
			// // showProgressDialog();
			// // retweetMessage(statusData.getCurrentService());
			// //
			// // }
			// //
			// // else {
			// // if (sendTweetList.size() > 0) {
			// // tweet(sendTweetList.get(0));
			// // }
			// // }
			//
			// send();
			// }

			break;
		}
		// case R.id.priorityTextColor: {
		// priorityNumber++;
		// priorityNumber = priorityNumber % 3;
		// switch (priorityNumber) {
		// case 0: {
		// priorityText.setText(getString(R.string.priority_normal));
		// priorityTextColor.setBackgroundColor(Color
		// .parseColor("#006000"));
		// break;
		// }
		// case 1: {
		// priorityText.setText(getString(R.string.priority_middle));
		// priorityTextColor.setBackgroundColor(Color
		// .parseColor("#660040"));
		// break;
		// }
		// case 2: {
		// priorityText.setText(getString(R.string.priority_high));
		// priorityTextColor.setBackgroundColor(Color
		// .parseColor("#ff0000"));
		// break;
		// }
		// default: {
		// priorityText.setText(getString(R.string.priority_normal));
		// priorityTextColor.setBackgroundColor(Color
		// .parseColor("#006000"));
		// }
		// }
		// break;
		// }
		case R.id.addTrends: {

			int index = updateText.getSelectionStart();// 获取光标所在位置
			Editable edit = updateText.getEditableText();// 获取EditText的文字
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER_PROXY)) {

				if (index < 0 || index >= edit.length()) {
					edit.append("#" + getString(R.string.insert_topic));
				} else {
					edit.insert(index, "#" + getString(R.string.insert_topic));// 光标所在位置插入文字
				}

			} else {
				if (index < 0 || index >= edit.length()) {
					edit.append("#" + getString(R.string.insert_topic) + "#");
				} else {
					edit.insert(index, "#" + getString(R.string.insert_topic)
							+ "#");// 光标所在位置插入文字
				}
			}

			updateText.setSelection(index + 1,
					index + getString(R.string.insert_topic).length() + 1);

			break;
		}
		// end send
		}
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(LBSdetailActivity.this);
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

	// send
	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				LBSdetailActivity.this);
		LayoutInflater inflater = LayoutInflater.from(LBSdetailActivity.this);
		final View textEntryView = inflater.inflate(
				R.layout.dialog_show_gridview_emotion, null);
		builder.setView(textEntryView);

		GridView gridView = (GridView) textEntryView
				.findViewById(R.id.gridView_emotions);
		final ArrayList<HashMap<String, String>> emotionData = new ArrayList<HashMap<String, String>>();
		EmotionInfo emotionInfo;

		for (int i = 0; i < emotionInfoList.size(); i++) {
			emotionInfo = emotionInfoList.get(i);
			if (emotionInfo != null) {
				HashMap<String, String> map = new HashMap<String, String>();
				// “你的json地址”+json中取图片的相对地址得到绝对地址
				map.put("itemImage", emotionInfo.getUrl());
				map.put("itemText", emotionInfo.getPhrase());
				emotionData.add(map);
			}
		}
		previewDialog = builder.show();

		// insert blog emotion
		// if (action != null && action.equals("blog")) {
		// gridView.setAdapter(new GridViewAdapter(this, previewDialog,
		// emotionData, "blog"));
		// } else {
		// state tweet retweet comment
		gridView.setAdapter(new GridViewAdapter(this, previewDialog,
				emotionData));
		// }
		// 设置emotionDialog 透明度
		WindowManager manager = previewDialog.getWindow().getWindowManager();
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		// WindowManager.LayoutParams lp = previewDialog.getWindow()
		// .getAttributes();
		// lp.alpha = 0.0f;

		// 设置Dialog大小
		// previewDialog.getWindow().setAttributes(lp);
		previewDialog.getWindow().setLayout(width * 9 / 10, height * 2 / 3);
	}

	// send
	private void openSelectDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		String[] items = null;
		// if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData
		// .getCurrentService())) {
		// items = getResources().getStringArray(R.array.upload_items_cfb);
		// } else {
		items = getResources().getStringArray(R.array.upload_items);
		// }
		// items = getResources().getStringArray(R.array.sohu_select_operation);

		dialog.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case 0: {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, 1);
					break;
				}
				case 1: {
					// Upload files only for CFB
					break;
				}
				}

			}
		}).create().show();

	}

	private void tweet() {

		// int tweetType = (Integer) tweetData[0];
		// AccountData account = (AccountData) tweetData[1];

		try {

			// if (tweetType == 0) {

			// Update
			showProgressDialog();

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("status", updateText.getText().toString());
			parameters.put("poiid", poiinfo.getpoiid());
			// parameters.put("reply_id", sourceMessageId);
			// parameters.put("important_level",
			// String.valueOf(priorityNumber + 2));

			// Location
			// if (location != null) {
			// parameters.put("geo_latitude",
			// String.valueOf(location.getLatitude()));
			// parameters.put("geo_longitude",
			// String.valueOf(location.getLongitude()));
			// } else if (location == null
			// && IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
			// .equals(statusData.getCurrentService())) {
			// // Pop up message (could not obtain Location Info)
			// locationHandler.sendEmptyMessage(OBTAIN_LOCATION_FAILED);
			// }

			if (filePath.getText().toString() != null
					&& filePath.getText().equals("")) {

				commType = CommHandler.TYPE_LBS_SEND_STATUS;

				// Request (get service from status information)
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_LBS_SEND_STATUS, apiServiceListener,
						parameters);
				Log.v("aaa", "000000000000000000000000000000000");
			} else {

				commType = CommHandler.TYPE_LBS_SEND_PHOTO;
				parameters.put("filePath", filePath.getText().toString());

				// Request (get service from status information)
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_LBS_SEND_PHOTO, apiServiceListener,
						parameters);

			}

			// } else if (tweetType == 1) {
			//
			// showProgressDialog();
			//
			// // Prepare Parameters
			// Map<String, Object> parameters1;
			// parameters1 = new HashMap<String, Object>();
			// parameters1.put("status", updateText.getText().toString());
			// parameters1.put("reply_id", sourceMessageId);
			// parameters1.put("important_level",
			// String.valueOf(priorityNumber + 2));
			//
			// // Location
			// if (location != null) {
			// parameters1.put("geo_latitude",
			// String.valueOf(location.getLatitude()));
			// parameters1.put("geo_longitude",
			// String.valueOf(location.getLongitude()));
			// }
			//
			// parameters1.put("service", account.getService());
			// if (account.getService() != null
			// && (account.getService().equals(
			// IGeneral.SERVICE_NAME_TWITTER) || account
			// .getService()
			// .equals(IGeneral.SERVICE_NAME_SINA))
			// || account.getService().equals(
			// IGeneral.SERVICE_NAME_SOHU)) {
			// parameters1.put("user_name", account.getAccessToken());
			// parameters1.put("user_password", account.getTokenSecret());
			// } else {
			// parameters1.put("user_name", account.getUserName());
			// parameters1.put("user_password", account.getPassword());
			// parameters1.put("apiUrl", account.getApiUrl());
			// }
			//
			// // Request (get service from status information)
			// apiServiceInterface.request(account.getService(),
			// CommHandler.TYPE_UPDATE_STATUS, apiServiceListener,
			// parameters1);
			//
			// }

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private void send() {

		// int tweetType = (Integer) tweetData[0];
		// AccountData account = (AccountData) tweetData[1];

		try {

			// if (tweetType == 0) {

			// Update
			showProgressDialog();

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			// parameters.put("status", cText.getText().toString());
			parameters.put("poiid", poiinfo.getpoiid());
			commType = CommHandler.TYPE_LBS_SEND_COMMENT;
			// parameters.put("reply_id", sourceMessageId);
			// parameters.put("important_level",
			// String.valueOf(priorityNumber + 2));

			// Location
			// if (location != null) {
			// parameters.put("geo_latitude",
			// String.valueOf(location.getLatitude()));
			// parameters.put("geo_longitude",
			// String.valueOf(location.getLongitude()));
			// } else if (location == null
			// && IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
			// .equals(statusData.getCurrentService())) {
			// // Pop up message (could not obtain Location Info)
			// locationHandler.sendEmptyMessage(OBTAIN_LOCATION_FAILED);
			// }

			// if (filePath.getText().toString() != null
			// && filePath.getText().equals("")) {
			//
			// // Request (get service from status information)
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_LBS_SEND_COMMENT, apiServiceListener,
					parameters);
			// } else {
			// // if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
			// // .equals(statusData.getCurrentService())
			// // || statusData.getCurrentService().equals(
			// // IGeneral.SERVICE_NAME_TWITTER)) {
			// // parameters.put("filePath", imagechoosed);
			// // } else {
			// parameters.put("filePath", filePath.getText()
			// .toString());
			// // }
			// // Request (get service from status information)
			// apiServiceInterface.request(statusData.getCurrentService(),
			// CommHandler.TYPE_LBS_SEND_PHOTO, apiServiceListener,
			// parameters);
			// }

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		notifyTextHasChanged();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {

				image_Preview.setVisibility(View.VISIBLE);
				Uri selectedImageUri = data.getData();

				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(selectedImageUri, projection,
						null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String selectedImagePath = cursor.getString(column_index);

				filePath.setText(selectedImagePath);
				String filePath0 = selectedImagePath;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(filePath0, options);
				image_Preview.setImageBitmap(bm);
				// if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
				// .getCurrentService())) {
				// MAX_TEXT_COUNT = 140 - 25;
				// notifyTextHasChanged();
				// }
			}
			if (requestCode == 3) {
				image_Preview.setVisibility(View.VISIBLE);
				Bundle bundle = data.getExtras();
				String filepath = bundle.getString("filePath");
				filePath.setText(filepath);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(filepath, options);
				image_Preview.setImageBitmap(bm);
			}

			if (requestCode == 2) {

				// Bundle bundle = data.getExtras();
				// ly.removeAllViewsInLayout();
				// // if(bundle.getStringArrayList("imagepath")==null){
				// imagechoosed = bundle.getStringArrayList("imagepath");
				//
				// filePath.setText(imagechoosed.get(0));
				// // images.setVisibility(View.VISIBLE);
				// // images.setBackgroundColor(0);
				// // StringBuilder result = new StringBuilder();
				// // result.append("<html>");
				// // result.append("<center>");
				// // for(int i=0;i<imagechoosed.size();i++){
				// // //
				// //
				// result.append("<img width=80dp; height=80dp; src='file:///"+imagechoosed.get(i)+"'/>");
				// //
				// result.append("<img width=80dp; height=80dp; src='file:///"+imagechoosed.get(i)+"'/>");
				// // }
				// // result.append("</center>");
				// // result.append("</html>");
				// // images.loadDataWithBaseURL(null,result.toString(),
				// // "text/html", "UTF-8",null);
				// // image_Preview.setVisibility(View.VISIBLE);
				// // BitmapFactory.Options options = new
				// BitmapFactory.Options();
				// // options.inSampleSize = 2;
				// // Bitmap bm = BitmapFactory.decodeFile(imagechoosed.get(0),
				// // options);
				// // image_Preview.setImageBitmap(bm);
				// ly.setVisibility(View.VISIBLE);
				// for (int i = 0; i < imagechoosed.size(); i++) {
				// ImageView imageitem = new ImageView(this);
				// imageitem.setMaxHeight(100);
				// imageitem.setMaxWidth(100);
				// loadImage(imagechoosed.get(i), imageitem);
				// ly.addView(imageitem, 100, 100);
				// }
				// if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
				// .getCurrentService())) {
				// MAX_TEXT_COUNT = 140 - 25 * imagechoosed.size();
				// notifyTextHasChanged();
				// }

			}
		}
	}

	@Override
	protected void refreshByMenu() {
	}

	private void notifyTextHasChanged() {
		int leftCount = 0;
		int count_comment = 0;
		//
		if (MAX_CFB_TEXT_COUNT != 140) {
			leftCount = MAX_CFB_TEXT_COUNT
					- updateText.getText().toString().length();
			// count_comment = MAX_CFB_TEXT_COUNT
			// - cText.getText().toString().length();

		} else {
			int count = getTextCount(updateText.getText().toString());
			leftCount = MAX_TEXT_COUNT - count;
			// count_comment = MAX_TEXT_COUNT
			// - getTextCount(cText.getText().toString());
		}
		countText.setText(String.valueOf(leftCount));
		// countText_comment.setText(String.valueOf(count_comment));

		if (leftCount < 0) {

			countText.setTextColor(Color.RED);

			// Disable Confirm Button
			send.setEnabled(false);
			longTweetButton.setEnabled(true);

		} else {

			countText.setTextColor(Color.BLACK);

			// Enable Confirm Button
			send.setEnabled(true);
			longTweetButton.setEnabled(false);
		}
		// if (count_comment < 0) {
		//
		// countText_comment.setTextColor(Color.RED);
		//
		// // Disable Confirm Button
		// cButton.setEnabled(false);
		// longTweetButton.setEnabled(true);
		//
		// } else {
		//
		// countText_comment.setTextColor(Color.BLACK);
		//
		// // Enable Confirm Button
		// cButton.setEnabled(true);
		// longTweetButton.setEnabled(false);
		// }
	}

	private int getTextCount(String updateText) {
		int count = 0;
		ArrayList<String> urlDataList = new ArrayList<String>();
		ArrayList<String> urlDataList1 = new ArrayList<String>();
		// 提取Url所需的正则表达式
		String regexStr = "http://[^ ^,^!^;^`^~^\n^，^！^；]*";
		// 创建正则表达式模版
		Pattern pattern = Pattern.compile(regexStr);
		// 创建Url字段匹配器,待查询字符串Data为其参数
		Matcher m = pattern.matcher(updateText);
		while (m.find()) {
			if (!urlDataList.contains(m.group())) {
				urlDataList.add(m.group());
			}
		}
		if (updateText.contains("https:")) {
			// 提取Url所需的正则表达式
			String regexStr1 = "https://[^ ^,^!^;^`^~^\n^，^！^；]*";
			// 创建正则表达式模版
			Pattern pattern1 = Pattern.compile(regexStr1);
			// 创建Url字段匹配器,待查询字符串Data为其参数
			Matcher m1 = pattern1.matcher(updateText);
			while (m1.find()) {
				if (!urlDataList1.contains(m1.group())) {
					urlDataList1.add(m1.group());
				}
			}
		}
		// 获取需要创建的String数组大小
		String strBuf = "";
		for (String urlData : urlDataList) {
			strBuf = strBuf + urlData;
		}
		for (String urlData1 : urlDataList1) {
			strBuf = strBuf + urlData1;
		}
		if (MAX_TEXT_TWEETER_COUNT == 140) {
			count = updateText.length() - strBuf.length() + 20
					* urlDataList.size() + urlDataList1.size();
		} else {
			count = updateText.length() - strBuf.length() + 11
					* urlDataList.size();
		}

		return count;
	}

}
