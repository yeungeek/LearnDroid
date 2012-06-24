package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.communication.DownloadServiceInterface;
import com.anhuioss.crowdroid.communication.DownloadServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.AtUserSelectDialog;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.TranslateDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;
import com.anhuioss.crowdroid.util.TagAnalysis;

public class CommentActivity extends Activity implements ServiceConnection,
		OnClickListener {

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	public static final String DOWNLOAD_SERVICE_NAME = ".communication.DownloadService";

	private int page = 0;

	private AlertDialog previewDialog;

	private SharedPreferences sharePreference;

	private SharedPreferences.Editor editor;

	private Handler mHandler = new Handler();

	/** Status For Comment **/
	WebView commentStatus;

	/** Update Comment **/
	EditText commentText;

	/** For WebView Click **/
	TextView clickText;

	CheckBox multiTweet;

	/** Confrim Comment **/
	Button okButton;

	Button btn_back;

	Button btn_home;

	Button btn_camera;

	Button btn_at;

	Button btn_t;

	Button btn_emotion;

	Button btn_speak;

	/** Translation */
	Button translationButton;

	/** Upload Image */
	Button uploadImageButton;

	/** Shorten URL */
	Button shortenUrlButton;

	/** Long Tweet */
	Button longTweetButton;

	TextView head_title;

	/** Text Count **/
	TextView textCount;

	/** current page **/
	private int currentPage = 1;

	/** Max Count */
	private int MAX_TEXT_COUNT = 140;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private int size = 0;

	private int selectItem = -1;

	private SimpleAdapter adapter;

	private String messageId = null;
	
	private String messageOwnerId = null;
	
	private StatusData statusData;

	private SettingData settingData;

	private MyImageBinder myImageBinder;

	private String imageShow;

	/** Image Map for user profile */
	public static HashMap<String, Bitmap> userImageMap = new HashMap<String, Bitmap>();

	protected ArrayList<TimeLineInfo> timeLineDataList = new ArrayList<TimeLineInfo>();

	protected ArrayList<TimeLineInfo> currentList;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ListView listView = null;

	private ApiServiceInterface apiServiceInterface;

	private DownloadServiceInterface downloadServiceInterface;

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

						// setProgressBarIndeterminateVisibility(false);
						closeProgressDialog();

						// User Image
						loadUserImage(currentList);

						CreateList(currentList);

						addItemForMoreTweets();
					}

				}

				System.gc();

			} catch (OutOfMemoryError e) {
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
					&& message != null && !message.equals("[null]")) {

				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				if (type == CommHandler.TYPE_GET_EMOTION) {

					ParseHandler parseHandler = new ParseHandler();
					SendMessageActivity.emotionList = (ArrayList<EmotionInfo>) parseHandler
							.parser(service, type, statusCode, message);
					if (!SendMessageActivity.emotionList.isEmpty()) {
						initEmotionView(SendMessageActivity.emotionList);
					}
				} else if (type == CommHandler.TYPE_GET_COMMENTS_BY_ID) {

					ParseHandler parseHandler = new ParseHandler();
					timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);
				}

				commentText.setText("");

				if (timelineInfoList.size() > 0) {
					currentList = timelineInfoList;

					CreateList(timelineInfoList);
					if (timelineInfoList.size() >= 20) {
						addItemForMoreTweets();
					}

					// if (imageShow.equals(SettingsActivity.select[0])
					// || imageShow.equals(SettingsActivity.select[1])) {
					// // Download Image
					// downloadImage(timelineInfoList);
					// } else {
					// // Undownload Image ,Create List
					// CreateList(timelineInfoList);
					// }

				}

			}
			if (!"200".equals(statusCode)) {
				if ("400".equals(statusCode)) {
					Toast.makeText(CommentActivity.this, "您没有权限进行评论",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(CommentActivity.this, ErrorMessage
							.getErrorMessage(CommentActivity.this, statusCode),
							Toast.LENGTH_SHORT);
				}

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();
		String imagePath = settingData.getWallpaper();

		imageShow = settingData.getSelectionShowImage();

		loadBackGroundImage(imagePath);
		// setContentView(R.layout.activity_comments);

		setContentView(R.layout.comments_activity);

		sharePreference = getSharedPreferences("SHARE_INIT_COMMENT_STATUS", 0);

		translationButton = (Button) findViewById(R.id.translateButton);
		uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
		shortenUrlButton = (Button) findViewById(R.id.shortenUrlButton);
		longTweetButton = (Button) findViewById(R.id.longTweetButton);
		btn_at = (Button) findViewById(R.id.atButton);
		btn_t = (Button) findViewById(R.id.addTrends);
		btn_emotion = (Button) findViewById(R.id.emotionButton);
		btn_camera = (Button) findViewById(R.id.cameraButton);
		btn_speak = (Button) findViewById(R.id.recorderButton);
		btn_back = (Button) findViewById(R.id.head_back);
		btn_home = (Button) findViewById(R.id.head_refresh);
		btn_home.setBackgroundResource(R.drawable.main_home);
		head_title = (TextView) findViewById(R.id.head_Name);
		head_title.setText(R.string.comment_count);

		multiTweet = (CheckBox) findViewById(R.id.multiTweet);
		multiTweet.setVisibility(View.GONE);

		// clickText = (TextView) findViewById(R.id.textView);
		// commentStatus = (WebView) findViewById(R.id.show_status);
		commentText = (EditText) findViewById(R.id.update_status);
		okButton = (Button) findViewById(R.id.okButton);
		listView = (ListView) findViewById(R.id.listView);
		textCount = (TextView) findViewById(R.id.status_count);

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

		// clickText.setOnClickListener(clickTextForFinish);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		translationButton.setOnClickListener(this);
		btn_at.setOnClickListener(this);
		btn_t.setOnClickListener(this);
		btn_emotion.setOnClickListener(this);
		listView.setDivider(null);
		okButton.setOnClickListener(updateConfirm);
		if(!statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)){
			listView.setOnItemClickListener(onItemClickListener);
		}	
		commentText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int leftCount = MAX_TEXT_COUNT
						- commentText.getText().toString().length();

				textCount.setText(String.valueOf(leftCount));

				if (leftCount < 0) {

					textCount.setTextColor(Color.RED);
					okButton.setEnabled(false);

				} else {

					textCount.setTextColor(Color.BLACK);
					okButton.setEnabled(true);

				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onStart() {
		super.onStart();
		isRunning = true;
		String tempCommentStatus = null;
		Bundle bundle = getIntent().getExtras();
		messageId = bundle.getString("message_id");
		messageOwnerId = bundle.getString("user_id");
		tempCommentStatus = bundle.getString("message");
		tempCommentStatus = "<marquee direction=left scrollamount=3>"
				+ tempCommentStatus + "</marquee> ";
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		commentText.setTextColor(Integer.valueOf(fontColor));
		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		
		if (bundle.containsKey("feedback")) {
			head_title.setText(R.string.feedback);
		}

		adapter.setViewBinder(myImageBinder);
		textCount.setText("140");
		textCount.setTextColor(Color.BLACK);
		initCommentViews();

		// Bind Api Service
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
	protected void onStop() {
		super.onStop();
		unbindService(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		String inital = sharePreference.getString("SHARE_INIT_COMMENT_STATUS",
				"");
		boolean isFromOutSideInsert = sharePreference.getBoolean(
				"WHEATHER_FROM_COMMENT_OUTSIDE_INSERT", false);
		if (isFromOutSideInsert) {
			editor = sharePreference.edit();
			editor.putBoolean("WHEATHER_FROM_COMMENT_OUTSIDE_INSERT", false);
			editor.commit();

			String front = sharePreference.getString("EDIT_INIT_STATUS_FRONT",
					"");
			String behind = sharePreference.getString(
					"EDIT_INIT_STATUS_BEHIND", "");
			commentText.setText(front + inital + behind);
			commentText.setSelection(commentText.getText().length());
		}

		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if (name.getShortClassName().equals(API_SERVICE_NAME)) {
			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

			// setProgressBarIndeterminateVisibility(true);
			showProgressDialog();

			// Prepare Parameter
			Map<String, Object> parameter;
			parameter = new HashMap<String, Object>();
			parameter.put("page", currentPage);
			parameter.put("message_id", messageId);
			parameter.put("owner_id", messageOwnerId);
			parameter.put("feed_type", getIntent().getExtras().getString("feed_type"));
			// HTTP Communication
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_COMMENTS_BY_ID,
						apiServiceListener, parameter);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else if (name.getShortClassName().equals(DOWNLOAD_SERVICE_NAME)) {
			downloadServiceInterface = DownloadServiceInterface.Stub
					.asInterface(service);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
		downloadServiceInterface = null;
	}

	// Button Click
	Button.OnClickListener updateConfirm = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			String update = commentText.getText().toString();
			// setProgressBarIndeterminateVisibility(true);
			showProgressDialog();
			if (update != null) {
				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("comment", commentText.getText().toString());
				parameters.put("message_id", messageId);
				parameters.put("owner_id", messageOwnerId);
				parameters.put("geo_latutude", "0");
				parameters.put("geo_longitude", "0");
				parameters.put("feed_type", getIntent().getExtras().getString("feed_type"));

				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_UPDATE_COMMENTS,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			finish();
		}
	};

	// TextView Click
	TextView.OnClickListener clickTextForFinish = new TextView.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();

		}
	};

	ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				refresh();

			} else {
				selectItem = position;
				final CharSequence[] item = getResources().getStringArray(
						R.array.reply_item);
				new AlertDialog.Builder(CommentActivity.this).setItems(item,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								commentText.setText(getResources().getString(
										R.string.reply_to_comment)
										+ " "
										+ "@"
										+ data.get(selectItem)
												.get("screenName") + " :");

							}
						}).show();

			}
		}

	};

	// -----------------------------------------------------------------------------
	/**
	 * Set BackGrouned Image.
	 */
	// -----------------------------------------------------------------------------
	private void loadBackGroundImage(String path) {

		if (path == null) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("/") == -1) {
			getWindow().setBackgroundDrawable(
					new ColorDrawable(Integer.parseInt(path)));
		} else {

			File file = new File(path);
			FileInputStream input = null;
			if (file.canRead() && file.isFile() && file.exists()) {
				try {

					input = new FileInputStream(file);

					// Get BitMap and set to background
					BitmapDrawable drawable = new BitmapDrawable(input);
					getWindow().setBackgroundDrawable(drawable);
				} catch (Exception e) {
				}
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

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();

			// User Image
			loadUserImage(timelineInfoList);

			CreateList(currentList);

			addItemForMoreTweets();

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

	// Create ListView
	private void CreateList(ArrayList<TimeLineInfo> timeLineInfoList) {

		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();
		for (TimeLineInfo timeLineInfo : timeLineInfoList) {

			timeLineDataList.add(timeLineInfo);
			// Prepare ArrayList

			Map<String, Object> map;
			map = new HashMap<String, Object>();
			map.put("screenName",
					timeLineInfo.getUserInfo().getScreenName() == null ? ""
							: timeLineInfo.getUserInfo().getScreenName());
			String statusImages = timeLineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			String status = TagAnalysis.clearImageUrls(
					timeLineInfo.getStatus(), statusImages);
			map.put("status", status);
			map.put("webStatus", status);
			// map.put("status", timeLineInfo.getStatus());
			// map.put("webStatus", timeLineInfo.getStatus());
			if (imageShow.equals(BrowseModeActivity.select[0])
					|| imageShow.equals(BrowseModeActivity.select[1])) {
				// Use Download Image
				map.put("userImage", timeLineInfo.getUserInfo()
						.getUserImageURL());
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
				map.put("verified", timeLineInfo.getUserInfo().getVerified());
			}

			if (imageShow.equals(BrowseModeActivity.select[0])) {
				map.put("web", timeLineInfo.getImageInformationForWebView(this,
						TimeLineInfo.TYPE_DATA_IMAGE_URLS));
			} else {
				map.put("web", "");
			}

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)) {
				map.put("retweetCount", getString(R.string.retweet_count) + "("
						+ timeLineInfo.getRetweetCount() + ")");
				map.put("commentCount", getString(R.string.comment_count) + "("
						+ timeLineInfo.getCommentCount() + ")");
			}
			map.put("time",
					timeLineInfo.getFormatTime(statusData.getCurrentService()));
			addDatas.add(map);

		}
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}
		adapter.notifyDataSetChanged();
	}

	public void addItemForMoreTweets() {

		if (data.size() > 0
				&& !data.get(data.size() - 1).get("screenName").equals("")) {
			try {
				Map<String, Object> map;
				map = new HashMap<String, Object>();
				map.put("screenName", "");
				// String textData =
				// getResources().getString(R.string.get_more_tweets);
				// String timeData = "";
				// if(IGeneral.SERVICE_NAME_SINA.equals(statusData.getCurrentService())
				// ||
				// IGeneral.SERVICE_NAME_TENCENT.equals(statusData.getCurrentService())
				// ||
				// IGeneral.SERVICE_NAME_SOHU.equals(statusData.getCurrentService()))
				// {
				// textData = "";
				// timeData =
				// getResources().getString(R.string.get_more_tweets);
				// }
				// map.put("status", textData);
				// // Bitmap bitmap =
				// BitmapFactory.decodeResource(getResources(),
				// android.R.drawable.ic_menu_more);
				// map.put("userImage",
				// String.valueOf(android.R.drawable.ic_menu_more));
				// map.put("time", timeData);
				// map.put("web", "");
				map.put("moreTweets",
						getResources().getString(R.string.get_more_tweets));
				data.add(map);
				adapter.notifyDataSetChanged();
			} catch (OutOfMemoryError e) {
				System.gc();
			}
		}

		// if (data.size() > 0
		// && !data.get(data.size() - 1).get("screenName").equals("")) {
		// try {
		// Map<String, Object> map;
		// map = new HashMap<String, Object>();
		// map.put("screenName", "");
		// map.put("status", getResources().getString(
		// R.string.get_more_comments));
		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		// android.R.drawable.ic_menu_more);
		// map.put("userImage", bitmap);
		// map.put("time", "");
		// data.add(map);
		// adapter.notifyDataSetChanged();
		// } catch (OutOfMemoryError e) {}
		// }

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

	// -----------------------------------------------------------------------------
	/**
	 * Refresh
	 */
	// -----------------------------------------------------------------------------
	private void refresh() {

		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();

		if (apiServiceInterface == null) {
			return;
		}

		currentPage++;

		// Prepare Parameter
		Map<String, Object> parameter;
		parameter = new HashMap<String, Object>();
		parameter.put("page", currentPage);
		parameter.put("message_id", messageId);
		String pageTime = "0";
		if (currentPage != 1
				&& IGeneral.SERVICE_NAME_TENCENT.equals(statusData
						.getCurrentService())) {
			pageTime = timeLineDataList.get(timeLineDataList.size() - 1)
					.getTimeStamp();
			parameter.put("pageTime", pageTime);
		}

		// HTTP Communication
		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_COMMENTS_BY_ID, apiServiceListener,
					parameter);
		} catch (RemoteException e) {
			e.printStackTrace();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_refresh: {

			Intent home = new Intent(CommentActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}
		case R.id.translateButton: {

			// Translation
			new TranslateDialog(this, commentText).show();
			break;

		}
		case R.id.atButton: {

			int index = commentText.getSelectionStart();//获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(commentText.getText()).substring(0, index));
			editor.putString("EDIT_INIT_STATUS_BEHIND",
					String.valueOf(commentText.getText()).substring(index, commentText.getText().length()));
			editor.commit();
			
			AtUserSelectDialog usd = new AtUserSelectDialog(this,
					"comment_message");
			usd.setTitle("@" + getString(R.string.insert_at_name));
			usd.show();
			break;
		}
		case R.id.addTrends: {
			int index = commentText.getSelectionStart();// 获取光标所在位置
			Editable edit = commentText.getEditableText();// 获取EditText的文字
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
					edit.insert(index, "#" + getString(R.string.insert_topic) + "#");// 光标所在位置插入文字
				}
			}
			commentText.setSelection(index + 1, index + getString(R.string.insert_topic)
					.length() + 1);
			break;
		}
		case R.id.emotionButton: {
			
			int index = commentText.getSelectionStart();//获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(commentText.getText()).substring(0, index));
			editor.putString("EDIT_INIT_STATUS_BEHIND",
					String.valueOf(commentText.getText()).substring(index, commentText.getText().length()));
			editor.commit();
			
			if (SendMessageActivity.emotionList.isEmpty()) {

				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_EMOTION, apiServiceListener,
							new HashMap<String, Object>());

				} catch (RemoteException e) {
					e.printStackTrace();
				}

			} else {
				initEmotionView(SendMessageActivity.emotionList);
			}
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

	private void initCommentViews() {

		shortenUrlButton.setVisibility(View.GONE);
		longTweetButton.setVisibility(View.GONE);
		uploadImageButton.setVisibility(View.GONE);
		btn_speak.setVisibility(View.GONE);
		btn_camera.setVisibility(View.GONE);
		if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)){
			btn_at.setVisibility(View.GONE);
			btn_t.setVisibility(View.GONE);
		}

	}

	public void setEmotion(final String pharse) {

		final int where = commentText.getSelectionStart();

		mHandler.post(new Runnable() {
			public void run() {
				commentText.getText().insert(where, pharse);
				if (previewDialog != null) {
					previewDialog.dismiss();
				}
			}
		});

	}

	public String getEmotionDataForWebView() {

		if (SendMessageActivity.emotionList != null
				&& SendMessageActivity.emotionList.size() >= 30
				&& SendMessageActivity.htmlDataListForEmotion == null) {
			SendMessageActivity.htmlDataListForEmotion = new ArrayList<String>();
			for (int i = 0; i < SendMessageActivity.emotionList.size() / 30; i++) {
				StringBuffer result = new StringBuffer();
				result.append("<table border=\"0\" align=\"center\">");

				for (int x = i * 30; x < (i + 1) * 30; x = x + 6) {
					result.append("<tr>");
					for (int y = 0; y < 6; y++) {
						EmotionInfo emotionInfo = SendMessageActivity.emotionList
								.get(x + y);
						result.append("<td><img width=\"22px\" height=\"22px\" src=\""
								+ emotionInfo.getUrl()
								+ "\" onClick=\"getImage('"
								+ emotionInfo.getPhrase() + "');\"/></td>");
					}
					result.append("</tr>");
				}

				result.append("</table>");
				SendMessageActivity.htmlDataListForEmotion.add(result
						.toString());
			}
		}
		return SendMessageActivity.htmlDataListForEmotion.get(page);
	}

	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				CommentActivity.this);
		LayoutInflater inflater = LayoutInflater.from(CommentActivity.this);
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
		gridView.setAdapter(new GridViewAdapter(CommentActivity.this,
				previewDialog, emotionData));

		// 设置emotionDialog 透明度
		WindowManager manager = previewDialog.getWindow().getWindowManager();
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		previewDialog.getWindow().setLayout(width * 9 / 10, height * 2 / 3);
	}
}