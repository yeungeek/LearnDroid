package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.AtUserSelectDialog;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.TranslateDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.util.AsyncDataLoad;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;
import com.anhuioss.crowdroid.util.MyClickableSpan;
import com.anhuioss.crowdroid.util.TagAnalysis;
import com.anhuioss.crowdroid.util.AsyncDataLoad.ImageCallback;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RetweetMessageActivity extends Activity implements
		OnClickListener, OnTouchListener, ServiceConnection {

	private com.baidu.mapapi.LocationListener netLocationListener;

	protected static final int OBTAIN_LOCATION_FAILED = 0;

	protected static final int OBTAIN_LOCATION_SUCESSED = 1;

	private CrowdroidApplication crowdroidApplication;

	private SharedPreferences sharePreference;

	private SharedPreferences.Editor editor;

	private TextView status;

	private HandleProgressDialog progress;

	private TextView statusTextView;
	private TextView retweetStatusTextView;
	// webview
	private WebView statusImageWebView;
	private WebView retweetStatusImageWebView;

	private String initPreviewState;

	// head
	private Button headBack;
	private TextView headName;
	private Button headHome;

	private String imageShow;
	private String text;
	TextView countText;

	/** Flag for First Displayed */
	private boolean isFirstDisplayed = true;

	/** Crowdroid Status Data */
	private StatusData statusData;
	private AccountData currentAccount;
	private SettingData settingData;

	private int page = 0;

	private ArrayList<TimeLineInfo> timeLineDataList;

	public static ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();

	public ArrayList<String> imagechoosed = new ArrayList<String>();

	private TimeLineInfo timeLineInfo;
	private TextView alertFilePath;
	private EditText commentText;
	private Button sendButton;
	private CheckBox commentToUserCheckBox;
	private CheckBox commentToSourUserCheckBox;
	private Button addTrends;

	/** Upload Image */
	private Button uploadImageButton;

	/** Shorten URL */
	private Button shortenUrlButton;

	/** Long Tweet */
	private Button longTweetButton;

	private Button cameraButton;

	private Button recorderButton;

	Button btn_back;

	Button btn_home;

	// image hrefUrls
	private String hrefImageUrlsData = "";
	private String hrefRetweetedImageUrlsData = "";
	private String hrefAttachUrls = "";
	private String hrefRetweetedAttachUrls = "";

	private String hrefVedioUrls = "";
	private String hrefRetweetedVedioUrls = "";

	private Button atButton;
	private Button emotionButton;
	private Button translationButton;
	private int clickTimes = 0;
	private int is_comment = 0;
	private Handler mHandler = new Handler();
	private String initStatus = "";
	private String fontSize;
	private String fontColor;

	private AlertDialog dlg;

	private AlertDialog previewDialog;

	/** Location Manager(for GPS) */
	private LocationManager locationManager;

	/** Location Listener(for GPS) */
	LocationListener locationListener;

	/** Location (for GPS) */
	Location location;

	private Timer locationTimer;

	private long time;

	/** Max Count */
	private int MAX_TEXT_COUNT = 140;

	private int MAX_CFB_TEXT_COUNT = 140;

	private int MAX_TEXT_TWEETER_COUNT = 0;

	// 添加附件的数组
	public ArrayList<String> attachedChoosed = new ArrayList<String>();

	/** API Service Interface */
	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			setProgressEnable(false);

			if (statusCode != null && statusCode.equals("200")) {
				if (type == CommHandler.TYPE_GET_EMOTION) {

					ParseHandler parseHandler = new ParseHandler();
					SendMessageActivity.emotionList = (ArrayList<EmotionInfo>) parseHandler
							.parser(service, type, statusCode, message);
					if (!SendMessageActivity.emotionList.isEmpty()) {
						initEmotionView(SendMessageActivity.emotionList);
					}
				} else {

					if ((statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SINA)
							|| statusData
									.getCurrentService()
									.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TENCENT)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SOHU) || statusData
							.getCurrentService().equals(
									IGeneral.SERVICE_NAME_WANGYI))
							&& type == CommHandler.TYPE_RETWEET) {
						commentText.setText("");
					}

					// if (type == CommHandler.TYPE_DESTROY) {
					// TimelineActivity timelineActivity = (TimelineActivity)
					// mContext;
					// timelineActivity
					// .deleteItem(timeLineInfo.getMessageId());
					// dismiss();
					// }

				}
				finish();

			} else {
				Toast.makeText(
						RetweetMessageActivity.this,
						ErrorMessage.getErrorMessage(
								RetweetMessageActivity.this, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		imageShow = settingData.getSelectionShowImage();

		initPreviewState = settingData.getSelectionShowImage();

		String imagePath = settingData.getWallpaper();
		loadBackGroundImage(imagePath);

		// bundle status
		Bundle bundle = this.getIntent().getExtras();
		timeLineDataList = (ArrayList<TimeLineInfo>) bundle
				.getSerializable("timelinedatalist");
		timeLineInfo = (TimeLineInfo) bundle.getSerializable("timelineinfo");

		setContentView(R.layout.retweet_and_comment_activity);

		sharePreference = getSharedPreferences("SHARE_INIT_RETWEET_STATUS", 0);

		// head-----------------------------
		headBack = (Button) findViewById(R.id.head_back);
		headHome = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);

		alertFilePath = (TextView) findViewById(R.id.txt_alert_files);
		commentText = (EditText) findViewById(R.id.update_status);

		sendButton = (Button) findViewById(R.id.okButton);

		countText = (TextView) findViewById(R.id.counterText);

		commentToUserCheckBox = (CheckBox) findViewById(R.id.multiTweet);

		commentToSourUserCheckBox = (CheckBox) findViewById(R.id.multiTweet2);

		btn_back = (Button) findViewById(R.id.head_back);

		btn_home = (Button) findViewById(R.id.head_refresh);

		btn_home.setBackgroundResource(R.drawable.main_home);

		// bottom-----------------------------------
		emotionButton = (Button) findViewById(R.id.emotionButton);

		translationButton = (Button) findViewById(R.id.translateButton);

		addTrends = (Button) findViewById(R.id.addTrends);

		atButton = (Button) findViewById(R.id.atButton);

		uploadImageButton = (Button) findViewById(R.id.uploadImageButton);

		shortenUrlButton = (Button) findViewById(R.id.shortenUrlButton);

		recorderButton = (Button) findViewById(R.id.recorderButton);

		cameraButton = (Button) findViewById(R.id.cameraButton);

		longTweetButton = (Button) findViewById(R.id.longTweetButton);

		// content--------------------------------

		statusTextView = (TextView) findViewById(R.id.translateStatus);
		retweetStatusTextView = (TextView) findViewById(R.id.retweetStatusTextView);
		statusImageWebView = (WebView) findViewById(R.id.statusImageWebView);
		retweetStatusImageWebView = (WebView) findViewById(R.id.retweetStatusImageWebView);

		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);
		emotionButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
		translationButton.setOnClickListener(this);
		addTrends.setOnClickListener(this);
		atButton.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		uploadImageButton.setOnClickListener(this);

		if (!statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS))
			uploadImageButton.setVisibility(View.GONE);
		shortenUrlButton.setVisibility(View.GONE);
		recorderButton.setVisibility(View.GONE);
		cameraButton.setVisibility(View.GONE);
		longTweetButton.setVisibility(View.GONE);

		headName.setText(R.string.retweet_count);
		// Web Settings

		WebSettings statusImagebSettings = statusImageWebView.getSettings();
		statusImagebSettings.setSavePassword(false);
		statusImagebSettings.setSaveFormData(false);
		statusImagebSettings.setJavaScriptEnabled(true);
		statusImagebSettings.setSupportZoom(true);

		WebSettings retweetImageWebSettings = retweetStatusImageWebView
				.getSettings();
		retweetImageWebSettings.setSavePassword(false);
		retweetImageWebSettings.setSaveFormData(false);
		retweetImageWebSettings.setJavaScriptEnabled(true);
		retweetImageWebSettings.setSupportZoom(true);

		statusImageWebView.setBackgroundColor(Color.TRANSPARENT);
		statusImageWebView.setVerticalScrollBarEnabled(false);
		statusImageWebView.setHorizontalScrollBarEnabled(false);

		retweetStatusImageWebView.setBackgroundColor(Color.TRANSPARENT);
		retweetStatusImageWebView.setVerticalScrollBarEnabled(false);
		retweetStatusImageWebView.setHorizontalScrollBarEnabled(false);

		String statusImageForWebView = getStatusImageForWebView();
		// String statusImageForWebView =
		// "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body><center><center><a href='http://player.youku.com/player.php/sid/XNDg4NDU4Mzky=/v.swf'><img style='max-height: 400px; max-width:200px; margin-top:4px;' src='http://www.uimaker.com/uploads/allimg/120716/1_120716013221_1.png' /></a><center></body></html>";

		String retweetStatusImageForWebView = getRetweetStatusImageForWebView();

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			retweetStatusImageWebView.setVisibility(View.GONE);
		}

		if (statusImageForWebView != null && statusImageForWebView.length() > 0) {
			statusImageWebView.loadDataWithBaseURL("", statusImageForWebView,
					"text/html", "utf-8", "");
			// statusImageWebView.loadData(statusImageForWebView, "text/html",
			// "utf-8");
			statusImageWebView.setVisibility(View.VISIBLE);
		} else {
			statusImageWebView.setVisibility(View.GONE);
		}

		if (retweetStatusImageForWebView != null
				&& retweetStatusImageForWebView.length() > 0) {
			retweetStatusImageWebView.loadDataWithBaseURL("",
					retweetStatusImageForWebView, "text/html", "utf-8", "");

		} else {
			retweetStatusImageWebView.setVisibility(View.GONE);
		}

		if (timeLineInfo.getStatus() != null
				&& timeLineInfo.isRetweeted()
				&& !statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {
			commentText.setText("//@"
					+ timeLineInfo.getUserInfo().getScreenName() + ":"
					+ timeLineInfo.getStatus());
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_WANGYI)) {
				commentText.setText("||@"
						+ timeLineInfo.getUserInfo().getScreenName() + ":"
						+ timeLineInfo.getStatus());
			}
		} else {
			commentText.setText("");
		}
		if (timeLineInfo.getUserInfo().getRetweetedScreenName() != null
				&& timeLineInfo.isRetweeted()
				&& statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)) {
			commentToSourUserCheckBox.setVisibility(View.VISIBLE);
			commentToSourUserCheckBox.setText(String.format(
					this.getString(R.string.comment_to_original_author),
					timeLineInfo.getUserInfo().getRetweetedScreenName()));
		} else {
			commentToSourUserCheckBox.setVisibility(View.GONE);
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SOHU)) {
			emotionButton.setVisibility(View.VISIBLE);
			commentToUserCheckBox.setVisibility(View.GONE);
		}
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			emotionButton.setVisibility(View.VISIBLE);
			commentToUserCheckBox.setVisibility(View.VISIBLE);
		}
		if (timeLineInfo.getUserInfo().getRetweetedScreenName() != null
				&& timeLineInfo.isRetweeted()
				&& statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			commentToSourUserCheckBox.setVisibility(View.VISIBLE);
			commentToSourUserCheckBox.setText(String.format(
					this.getString(R.string.comment_to_original_author),
					timeLineInfo.getUserInfo().getRetweetedScreenName()));
		}

		commentToUserCheckBox.setText(String.format(this
				.getString(R.string.also_comment_to), timeLineInfo
				.getUserInfo().getScreenName()));

		commentText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int leftCount = MAX_TEXT_COUNT
						- commentText.getText().toString().length();

				countText.setText(String.valueOf(leftCount));

				if (leftCount < 0) {

					countText.setTextColor(Color.RED);
					sendButton.setEnabled(false);

				} else {

					countText.setTextColor(Color.BLACK);
					sendButton.setEnabled(true);

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

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		fontSize = settingData.getFontSize();
		fontColor = settingData.getFontColor();
		String imagePath = settingData.getWallpaper();
		loadBackGroundImage(imagePath);
		initDetailView();
		text = timeLineInfo.getStatus();

		initStatus = timeLineInfo.getStatus();
		setStatusState();
		if (!"".equals(timeLineInfo.getRetweetedStatus())
				|| timeLineInfo.getRetweetedStatus() == null) {
			setRetweetedStatusState();
		} else {
			retweetStatusTextView.setVisibility(View.GONE);

		}

		// Bind Service
		Intent intent = new Intent(RetweetMessageActivity.this,
				ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			SharedPreferences status = getSharedPreferences("status",
					Context.MODE_PRIVATE);
			countText.setText(status.getString("max_input_charactor", "140"));
			int m = Integer.valueOf(countText.getText().toString());
			MAX_TEXT_COUNT = m;
		}

		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SOHU)) {
			countText.setText("1000");
			MAX_CFB_TEXT_COUNT = 1000;
		}
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			countText.setText("140");
			MAX_TEXT_TWEETER_COUNT = 140;
		}
		notifyTextHasChanged();
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			startCFBLocationService();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 2) {
				// 多图发送
				Bundle bundle = data.getExtras();
				// 如果没有附件直接赋值
				if (bundle != null) {
					imagechoosed = bundle.getStringArrayList("imagepath");
				}
				if (attachedChoosed.size() > 0) {
					for (int i = 0; i < attachedChoosed.size(); i++) {
						imagechoosed.add(attachedChoosed.get(i));
					}
				}
				alertFilePath.setVisibility(View.VISIBLE);
				StringBuffer alert = new StringBuffer();
				alert.append(getString(R.string.attach_path) + "\n");

				for (int i = 0; i < imagechoosed.size(); i++) {
					alert.append(imagechoosed.get(i) + "\n");
				}
				alertFilePath.setText(alert.toString());
			}
			if (requestCode == 3) {
				// cfb 上传附件
				try {
					alertFilePath.setVisibility(View.VISIBLE);
					String url = Uri.decode(data.getDataString());
					Uri selectedImageUri = Uri.parse(url);
					// Uri selectedImageUri = data.getData();
					String[] projection = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(selectedImageUri, projection,
							null, null, null);
					String selectedImagePath = "";
					if (cursor != null) {
						int column_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						selectedImagePath = cursor.getString(column_index);
					} else {
						selectedImagePath = url.substring(url
								.indexOf("file///") + 8);
					}

					attachedChoosed.add(selectedImagePath);

					imagechoosed.add(selectedImagePath);
					// 添加附件的提示路径
					StringBuffer alert = new StringBuffer();
					alert.append("附件路径：\n");

					for (int i = 0; i < imagechoosed.size(); i++) {
						alert.append(imagechoosed.get(i) + "\n");
					}
					alertFilePath.setText(alert.toString());
				} catch (Exception e) {

				}
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.statusImageWebView: {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// statusImageWebView.requestFocus();
				if (!hrefAttachUrls.equals("")
						&& hrefAttachUrls.contains("/files/")) {
					Uri uri = Uri.parse(hrefAttachUrls);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else if (!hrefVedioUrls.equals("")
						&& hrefVedioUrls.contains(".swf")) {
					Uri uri = Uri.parse(hrefVedioUrls);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(RetweetMessageActivity.this,
							PreviewImageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("url", hrefImageUrlsData);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}

			break;
		}
		case R.id.retweetStatusImageWebView: {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// statusImageWebView.requestFocus();
				if (!hrefRetweetedAttachUrls.equals("")
						&& hrefRetweetedAttachUrls.contains("/files/")) {
					Uri uri = Uri.parse(hrefRetweetedAttachUrls);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else if (!hrefRetweetedVedioUrls.equals("")
						&& hrefRetweetedVedioUrls.contains(".swf")) {
					Uri uri = Uri.parse(hrefRetweetedVedioUrls);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(RetweetMessageActivity.this,
							PreviewImageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("url", hrefRetweetedImageUrlsData);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
			break;
		}
		}
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			if (netLocationListener != null) {
				CrowdroidApplication.getInstance().getMapManager()
						.getLocationManager()
						.removeUpdates(netLocationListener);
				CrowdroidApplication.getInstance().getMapManager().stop();
			} else {
				stopLocationService();
			}
		}

	}

	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();
		stopLocationService();
		// Unbind Service
		unbindService(this);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		String inital = sharePreference.getString("SHARE_INIT_RETWEET_STATUS",
				"");
		boolean isFromOutSideInsert = sharePreference.getBoolean(
				"WHEATHER_FROM_RETWEET_OUTSIDE_INSERT", false);
		if (isFromOutSideInsert) {
			editor = sharePreference.edit();
			editor.putBoolean("WHEATHER_FROM_RETWEET_OUTSIDE_INSERT", false);
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

	private void initDetailView() {

		countText.setText("140");
		countText.setTextColor(Color.BLACK);

		// font size color
		statusTextView.setTextSize(Float.valueOf(fontSize) * 1.1f);
		retweetStatusTextView.setTextSize(Float.valueOf(fontSize) * 1.0f);
		if (fontColor.contains("-")) {
			statusTextView.setTextColor(Integer.valueOf(fontColor));
			retweetStatusTextView.setTextColor(Integer.valueOf(fontColor));
		} else {
			statusTextView.setTextColor(getResources().getColor(
					Integer.valueOf(fontColor)));
			retweetStatusTextView.setTextColor(getResources().getColor(
					Integer.valueOf(fontColor)));
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			atButton.setVisibility(View.GONE);
			addTrends.setVisibility(View.GONE);
			countText.setText("240");
			commentText.setText("");
			// commentText.setText("转自"
			// + timeLineInfo.getUserInfo().getScreenName());
		}
	}

	private void setStatusState() {

		// font size color
		statusTextView.setTextSize(Float.valueOf(fontSize) * 1.1f);
		retweetStatusTextView.setTextSize(Float.valueOf(fontSize) * 1.0f);
		if (fontColor.contains("-")) {
			statusTextView.setTextColor(Integer.valueOf(fontColor));
			retweetStatusTextView.setTextColor(Integer.valueOf(fontColor));
		} else {
			statusTextView.setTextColor(getResources().getColor(
					Integer.valueOf(fontColor)));
			retweetStatusTextView.setTextColor(getResources().getColor(
					Integer.valueOf(fontColor)));
		}
		String imageUrlString = timeLineInfo.getImageInformationForWebView(
				this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
		initStatus = TagAnalysis.clearImageUrls(initStatus, imageUrlString);
		// Extract Hash
		initStatus = initStatus.replaceAll("\r", "");
		if (initStatus == null) {
			initStatus = "";
		}

		// 话题
		ArrayList<String> indexHashFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexHashFlag = TagAnalysis.getHashTagIndex(initStatus,
					statusData.getCurrentService());
		} else {
			indexHashFlag = TagAnalysis.getIndexForSina(initStatus, "#");
		}

		int number = indexHashFlag.size();

		final SpannableString spanString = new SpannableString(initStatus);

		for (int i = 0; i < number / 2; i++) {

			final int start = Integer.valueOf(indexHashFlag.get(i * 2));
			final int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {

						@Override
						public void onClick(View widget) {

							String tag = initStatus.substring(start, end);
							Intent i = new Intent(RetweetMessageActivity.this,
									KeywordSearchActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("keyword", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);

						}

					});

			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// @user
		ArrayList<String> indexAtFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexAtFlag = TagAnalysis.getIndex(initStatus, "@");
		} else {
			indexAtFlag = TagAnalysis.getIndexForSina(initStatus, "@");
		}

		int numverAtFlag = indexAtFlag.size();

		for (int i = 0; i < numverAtFlag / 2; i++) {

			final int start = Integer.valueOf(indexAtFlag.get(i * 2));
			final int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {
						@Override
						public void onClick(View widget) {

							String tag = initStatus.substring(start + 1, end);
							Intent i = null;
							if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
									.getCurrentService())
									|| IGeneral.SERVICE_NAME_TWITTER_PROXY
											.equals(statusData
													.getCurrentService())) {
								i = new Intent(RetweetMessageActivity.this,
										ProfileActivity.class);
							} else {
								i = new Intent(RetweetMessageActivity.this,
										UserSearchActivity.class);
							}
							Bundle bundle = new Bundle();
							bundle.putString("name", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					});
			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// emotions replace
		AsyncDataLoad imageLoader = new AsyncDataLoad();
		String phrase = null;
		ArrayList<String> indexEmotionsFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
			indexEmotionsFlag = TagAnalysis.getEmotionsIndexFlag(initStatus,
					statusData.getCurrentService());

			int numberEmotionFlag = indexEmotionsFlag.size();
			if (numberEmotionFlag > 0) {
				for (int i = 0; i < numberEmotionFlag / 2; i++) {

					final int start = Integer.valueOf(indexEmotionsFlag
							.get(i * 2));
					final int end = Integer.valueOf(indexEmotionsFlag
							.get(i * 2 + 1));
					phrase = initStatus.substring(start, end);
					if (phrase != null) {
						for (final EmotionInfo emotion : SendMessageActivity.emotionList) {
							if (phrase.equals(emotion.getPhrase())) {
								String url = emotion.getUrl();
								if (statusData.getCurrentService().equals(
										IGeneral.SERVICE_NAME_SINA)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_SOHU)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_WANGYI)) {
									// sina/sohu [爱心]
									phrase = phrase.substring(1,
											phrase.length() - 1);

									try {
										url = url.replace(phrase, URLEncoder
												.encode(phrase, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData
										.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
									try {
										String encodeStr = url.substring(
												url.lastIndexOf("/") + 1,
												url.lastIndexOf("."));
										url = url.replace(encodeStr, URLEncoder
												.encode(encodeStr, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_TENCENT)) {
									url = emotion.getUrl();
								}
								imageLoader.loadDrawable(url,
										new ImageCallback() {
											public void imageLoaded(
													Drawable imageDrawable,
													String imageUrl) {

												imageDrawable.setBounds(0, 0,
														22, 22);
												ImageSpan span = new ImageSpan(
														imageDrawable,
														ImageSpan.ALIGN_BASELINE);
												spanString
														.setSpan(
																span,
																start,
																end,
																Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

												statusTextView
														.setText(spanString);
												statusTextView
														.setMovementMethod(LinkMovementMethod
																.getInstance());
											}
										});
							}

						}

					}
				}
			} else {
				statusTextView.setText(spanString);
				statusTextView.setMovementMethod(LinkMovementMethod
						.getInstance());
			}

		} else {
			statusTextView.setText(spanString);
			statusTextView.setMovementMethod(LinkMovementMethod.getInstance());
		}

	}

	// --------------------------retweet status------
	private void setRetweetedStatusState() {

		String retweetedStatus;
		if (("").equals(timeLineInfo.getRetweetedStatus())
				|| timeLineInfo.getRetweetedStatus() == null) {
			retweetedStatus = "";
		} else {
			retweetedStatus = "@"
					+ timeLineInfo.getUserInfo().getRetweetedScreenName()
					+ ":\n" + timeLineInfo.getRetweetedStatus();
			String imageUrlString = timeLineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET);
			retweetedStatus = TagAnalysis.clearImageUrls(retweetedStatus,
					imageUrlString);
		}
		// Extract Hash
		retweetedStatus = retweetedStatus.replaceAll("\r", "");
		if (retweetedStatus == null) {
			retweetedStatus = "";
		}
		final String initRetweetStatus = retweetedStatus;
		// 话题
		ArrayList<String> indexHashFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexHashFlag = TagAnalysis.getHashTagIndex(initRetweetStatus,
					statusData.getCurrentService());
		} else {
			indexHashFlag = TagAnalysis.getIndexForSina(initRetweetStatus, "#");
		}
		int number = indexHashFlag.size();

		final SpannableString spanString = new SpannableString(
				initRetweetStatus);

		for (int i = 0; i < number / 2; i++) {

			final int start = Integer.valueOf(indexHashFlag.get(i * 2));
			final int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {

						@Override
						public void onClick(View widget) {

							String tag = initRetweetStatus
									.substring(start, end);
							Intent i = new Intent(RetweetMessageActivity.this,
									KeywordSearchActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("keyword", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					});
			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// @user
		ArrayList<String> indexAtFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexAtFlag = TagAnalysis.getIndex(initRetweetStatus, "@");
		} else {
			indexAtFlag = TagAnalysis.getIndexForSina(initRetweetStatus, "@");
		}
		int numverAtFlag = indexAtFlag.size();

		for (int i = 0; i < numverAtFlag / 2; i++) {

			final int start = Integer.valueOf(indexAtFlag.get(i * 2));
			final int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {
						@Override
						public void onClick(View widget) {

							String tag = initRetweetStatus.substring(start + 1,
									end);
							Intent i = null;
							if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
									.getCurrentService())
									|| IGeneral.SERVICE_NAME_TWITTER_PROXY
											.equals(statusData
													.getCurrentService())) {
								i = new Intent(RetweetMessageActivity.this,
										ProfileActivity.class);
							} else {
								i = new Intent(RetweetMessageActivity.this,
										UserSearchActivity.class);
							}
							Bundle bundle = new Bundle();
							bundle.putString("name", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					});
			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		// emotions replace
		AsyncDataLoad imageLoader = new AsyncDataLoad();
		String phrase = null;
		ArrayList<String> indexEmotionsFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
			indexEmotionsFlag = TagAnalysis.getEmotionsIndexFlag(
					initRetweetStatus, statusData.getCurrentService());

			int numberEmotionFlag = indexEmotionsFlag.size();
			if (numberEmotionFlag > 0) {
				for (int i = 0; i < numberEmotionFlag / 2; i++) {

					final int start = Integer.valueOf(indexEmotionsFlag
							.get(i * 2));
					final int end = Integer.valueOf(indexEmotionsFlag
							.get(i * 2 + 1));
					phrase = initRetweetStatus.substring(start, end);
					if (phrase != null) {
						for (final EmotionInfo emotion : SendMessageActivity.emotionList) {
							if (phrase.equals(emotion.getPhrase())) {
								String url = emotion.getUrl();
								if (statusData.getCurrentService().equals(
										IGeneral.SERVICE_NAME_SINA)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_SOHU)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_WANGYI)) {
									// sina/sohu [爱心]
									phrase = phrase.substring(1,
											phrase.length() - 1);

									try {
										url = url.replace(phrase, URLEncoder
												.encode(phrase, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData
										.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
									try {
										String encodeStr = url.substring(
												url.lastIndexOf("/") + 1,
												url.lastIndexOf("."));
										url = url.replace(encodeStr, URLEncoder
												.encode(encodeStr, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_TENCENT)) {
									url = emotion.getUrl();
								}
								imageLoader.loadDrawable(url,
										new ImageCallback() {
											public void imageLoaded(
													Drawable imageDrawable,
													String imageUrl) {

												imageDrawable.setBounds(0, 0,
														22, 22);
												ImageSpan span = new ImageSpan(
														imageDrawable,
														ImageSpan.ALIGN_BASELINE);
												spanString
														.setSpan(
																span,
																start,
																end,
																Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

												retweetStatusTextView
														.setText(spanString);
												retweetStatusTextView
														.setMovementMethod(LinkMovementMethod
																.getInstance());
											}
										});
							}

						}

					}
				}
			} else {
				retweetStatusTextView.setText(spanString);
				retweetStatusTextView.setMovementMethod(LinkMovementMethod
						.getInstance());
			}

		} else {
			retweetStatusTextView.setText(spanString);
			retweetStatusTextView.setMovementMethod(LinkMovementMethod
					.getInstance());
		}
	}

	public String getStatusImageForWebView() {

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			hrefImageUrlsData = timeLineInfo.getImageInformationForWebView(
					RetweetMessageActivity.this, TimeLineInfo.TYPE_DATA_IMAGE);
			return timeLineInfo.getImageInformationForWebView(
					RetweetMessageActivity.this, TimeLineInfo.TYPE_DATA_IMAGE);
		}

		String imageUrlString = timeLineInfo.getImageInformationForWebView(
				this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);

		StringBuffer htmlData = new StringBuffer();
		StringBuffer previewHtmlData = new StringBuffer();
		if (!initPreviewState.equals(BrowseModeActivity.select[0])) {
			hrefImageUrlsData = "";
			return "";
		}

		if ((statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS) || statusData
				.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT))
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {

			String[] imageUrls = imageUrlString.split(";");

			if (imageUrls.length > 0) {
				htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				htmlData.append("<center>");
				previewHtmlData
						.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				previewHtmlData.append("<center>");

				for (int i = 0; i < imageUrls.length; i++) {
					String srcImageUrl = imageUrls[i];
					String hrefImageUrl = imageUrls[i];
					// cfb 附件attach
					if (imageUrls[i].contains("/files/")) {
						hrefAttachUrls = imageUrls[i];
						srcImageUrl = "http://www.iconpng.com/png/coquette/attachment3.png";
					}
					if (imageUrls[i].contains(".swf")) {
						hrefVedioUrls = imageUrls[i];
						// srcImageUrl =
						// "http://www.uimaker.com/uploads/allimg/120716/1_120716013221_1.png";

						srcImageUrl = "file:///android_res/drawable/vedioicon.png";
						// srcImageUrl =
						// "file:///android_res/drawable/about.png";
						// srcImageUrl="http://icons.iconarchive.com/icons/deleket/sleek-xp-basic/72/Attach-icon.png";
					}
					// Sina 图片显示
					if (imageUrls[i].contains("sinaimg.cn/thumbnail")) {
						hrefImageUrl = imageUrls[i].replace("thumbnail",
								"large");
					}
					// Tencent
					else if (imageUrls[i].contains("qpic.cn/mblogpic")) {

						srcImageUrl = imageUrls[i] + "/160";
						hrefImageUrl = imageUrls[i] + "/460";
					}
					// Sohu
					else if (imageUrls[i].contains("t.itc.cn/mblog/pic")) {
						hrefImageUrl = imageUrls[i].replace(" ", "f_");
					}
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						htmlData.append(
								"<img style='max-width:160px;max-height:300px;'  src='")
								.append(srcImageUrl).append("' />");
					} else {
						htmlData.append(
								"<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
								.append(srcImageUrl).append("' />");
					}

					previewHtmlData
							.append("<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
							.append(hrefImageUrl).append("' />");
					if (i != imageUrls.length - 1) {
						htmlData.append("<br>");
						previewHtmlData.append("<br>");
					}
				}
				htmlData.append("<center></body></html>");
				previewHtmlData.append("<center></body></html>");
			} else {
				htmlData.append("");
				previewHtmlData.append("");
			}
		}
		hrefImageUrlsData = previewHtmlData.toString();
		return htmlData.toString();
	}

	public String getRetweetStatusImageForWebView() {

		StringBuffer previewRetweetedHtmlData = new StringBuffer();
		// create html source
		if (timeLineInfo.isRetweeted()) {

			StringBuffer htmlData = new StringBuffer();

			String imageUrlString = timeLineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET);

			if (!initPreviewState.equals(BrowseModeActivity.select[0])) {
				hrefRetweetedImageUrlsData = "";
				return "";
			}

			String[] imageUrls = imageUrlString.split(";");
			if (imageUrls.length > 0) {
				float scale = ((CrowdroidApplication) getApplicationContext())
						.getScaleDensity();
				htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				htmlData.append("<center>");
				previewRetweetedHtmlData
						.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				previewRetweetedHtmlData.append("<center>");
				for (int i = 0; i < imageUrls.length; i++) {
					String srcImageUrl = imageUrls[i];
					String hrefImageUrl = imageUrls[i];
					// cfb attach
					if (imageUrls[i].contains("/files/")) {
						hrefRetweetedAttachUrls = imageUrls[i];
						srcImageUrl = "http://www.iconpng.com/png/coquette/attachment3.png";
					}
					if (imageUrls[i].contains(".swf")) {
						hrefRetweetedVedioUrls = imageUrls[i];
						srcImageUrl = "file:///android_res/drawable/vedioicon.png";
						// srcImageUrl =
						// "http://www.uimaker.com/uploads/allimg/120716/1_120716013221_1.png";
					}
					// Sina large image
					if (imageUrls[i].contains("sinaimg.cn/thumbnail")) {
						hrefImageUrl = imageUrls[i].replace("thumbnail",
								"large");
						// htmlData.append(
						// "<a href='" + hrefImageUrl
						// + "'><img style='max-height:"
						// + (300 * scale) + "px; max-width:"
						// + (160 * scale)
						// + "px; margin-top:4px;' src='")
						// .append(srcImageUrl).append("' /></a>");
					} else if (imageUrls[i].contains("qpic.cn/mblogpic")) {
						// hrefImageUrl =
						// imageUrls[i].replace(String.valueOf(imageUrls[i].charAt(imageUrls[i].length()-3)),
						// "4");
						srcImageUrl = imageUrls[i] + "/160";
						hrefImageUrl = imageUrls[i] + "/460";
						// htmlData.append(
						// "<a href='" + hrefImageUrl
						// + "'><img style='max-height:"
						// + (300 * scale) + "px; max-width:"
						// + (160 * scale)
						// + "px; margin-top:4px;' src='")
						// .append(srcImageUrl).append("' /></a>");
					}
					// Sohu
					else if (imageUrls[i].contains("t.itc.cn/mblog/pic")) {
						hrefImageUrl = imageUrls[i].replace(" ", "f_");
						// htmlData.append(
						// "<a href='"
						// + hrefImageUrl
						// +
						// "'><img style='max-height: 400px; max-width:200px; margin-top:4px;' src='")
						// .append(srcImageUrl).append("' /></a>");
					}
					// else {
					// htmlData.append(
					// "<a href='" + hrefImageUrl
					// + "'><img style='max-height:"
					// + (300 * scale) + "px; max-width:"
					// + (160 * scale)
					// + "px; margin-top:4px;' src='")
					// .append(srcImageUrl).append("' /></a>");
					// }

					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						htmlData.append(
								"<img style='max-width:160px;max-height:300px'  src='")
								.append(srcImageUrl).append("' />");
					} else {
						htmlData.append(
								"<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
								.append(srcImageUrl).append("' />");
					}

					previewRetweetedHtmlData
							.append("<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
							.append(hrefImageUrl).append("' />");
					if (i != imageUrls.length - 1) {
						htmlData.append("<br>");
						previewRetweetedHtmlData.append("<br>");
					}
				}
				htmlData.append("<center></body></html>");
				previewRetweetedHtmlData.append("<center></body></html>");
			} else {
				htmlData.append("");
				previewRetweetedHtmlData.append("");
			}
			hrefRetweetedImageUrlsData = previewRetweetedHtmlData.toString();
			return htmlData.toString();

		} else {
			hrefRetweetedImageUrlsData = "";
			return "";
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

	private void notifyTextHasChanged() {
		int leftCount = 0;
		//
		if (MAX_CFB_TEXT_COUNT != 140) {
			leftCount = MAX_CFB_TEXT_COUNT
					- commentText.getText().toString().length();

		} else {
			int count = getTextCount(commentText.getText().toString());
			leftCount = MAX_TEXT_COUNT - count;
		}
		countText.setText(String.valueOf(leftCount));

		if (leftCount < 0) {

			countText.setTextColor(Color.RED);

			// Disable Confirm Button
			sendButton.setEnabled(false);
			longTweetButton.setEnabled(true);

		} else {

			countText.setTextColor(Color.BLACK);

			// Enable Confirm Button
			sendButton.setEnabled(true);
			longTweetButton.setEnabled(false);

		}
	}

	private void showProgressDialog() {
		if (progress == null) {
			progress = new HandleProgressDialog(this);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (progress != null) {
			progress.dismiss();
		}
	}

	private void setProgressEnable(boolean flag) {

		if (flag) {
			// progressBar.setVisibility(View.VISIBLE);
			showProgressDialog();
		} else {
			// progressBar.setVisibility(View.GONE);
			closeProgressDialog();
		}
	}

	public int setIsComment() {
		if (commentToUserCheckBox.isChecked()
				&& commentToSourUserCheckBox.isChecked()) {
			is_comment = 3;
		} else if (commentToSourUserCheckBox.isChecked()) {
			is_comment = 2;
		} else if (commentToUserCheckBox.isChecked()) {
			is_comment = 1;
		} else {
			is_comment = 0;
		}
		return is_comment;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.head_refresh: {

			Intent home = new Intent(RetweetMessageActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}

		case R.id.emotionButton: {

			int index = commentText.getSelectionStart();// 获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(commentText.getText()).substring(0, index));
			editor.putString(
					"EDIT_INIT_STATUS_BEHIND",
					String.valueOf(commentText.getText()).substring(index,
							commentText.getText().length()));
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

				// showEmotionsDialog();
				initEmotionView(SendMessageActivity.emotionList);

			}

			break;
		}
		case R.id.okButton: {

			// progressBar.setIndeterminate(true);
			setProgressEnable(true);
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();

			// Location
			if (location != null) {
				parameters.put("geo_latitude",
						String.valueOf(location.getLatitude()));
				parameters.put("geo_longitude",
						String.valueOf(location.getLongitude()));
			} else if (location == null
					&& IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
							.equals(statusData.getCurrentService())) {
				// Pop up message (could not obtain Location Info)
				locationHandler.sendEmptyMessage(OBTAIN_LOCATION_FAILED);
			}

			parameters.put("is_comment", setIsComment());
			parameters.put("message_id", timeLineInfo.getMessageId());
			parameters.put("comment", commentText.getText().toString());

			parameters.put("user_id", timeLineInfo.getUserInfo().getUid());
			try {
				if (!IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
						.equals(statusData.getCurrentService()))
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_RETWEET, apiServiceListener,
							parameters);
				else {
					if (imagechoosed != null && imagechoosed.size() <= 0) {
						apiServiceInterface.request(
								statusData.getCurrentService(),
								CommHandler.TYPE_RETWEET, apiServiceListener,
								parameters);
					} else if (imagechoosed != null && imagechoosed.size() > 0) {
						parameters.put("retweet", "true");
						parameters.put("filePath", imagechoosed);
						apiServiceInterface.request(
								statusData.getCurrentService(),
								CommHandler.TYPE_UPLOAD_IMAGE,
								apiServiceListener, parameters);
					}
				}
				// ---------------------------------
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.translateButton: {

			// Translation
			new TranslateDialog(this, commentText).show();
			break;

		}
		case R.id.atButton: {

			int index = commentText.getSelectionStart();// 获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(commentText.getText()).substring(0, index));
			editor.putString(
					"EDIT_INIT_STATUS_BEHIND",
					String.valueOf(commentText.getText()).substring(index,
							commentText.getText().length()));
			editor.commit();

			AtUserSelectDialog usd = new AtUserSelectDialog(this,
					"retweet_message");
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
					edit.insert(index, "#" + getString(R.string.insert_topic)
							+ "#");// 光标所在位置插入文字
				}
			}
			commentText.setSelection(index + 1,
					index + getString(R.string.insert_topic).length() + 1);
			break;
		}
		case R.id.uploadImageButton: {
			openSelectDialog();
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

	private Handler locationHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case OBTAIN_LOCATION_FAILED: {
				Toast.makeText(getApplicationContext(),
						getString(R.string.obtain_location_info_failed),
						Toast.LENGTH_SHORT).show();
				// closeGps();
				stopLocationService();
				break;
			}
			case OBTAIN_LOCATION_SUCESSED: {
				Toast.makeText(getApplicationContext(),
						getString(R.string.obtain_location_info),
						Toast.LENGTH_SHORT).show();
				// closeGps();
				stopLocationService();
				break;
			}
			default:
				break;
			}
		}

	};

	/**
	 * 开启gps定位
	 */
	private void startCFBLocationService() {
		// TODO Auto-generated method stub
		initLocationNetWork();// 利用百度地图 ，开启定位

		PackageManager pm = getPackageManager();
		if (location == null
				&& netLocationListener != null
				&& pm != null
				&& pm.checkPermission(permission.ACCESS_FINE_LOCATION,
						getPackageName()) == PackageManager.PERMISSION_GRANTED) {

			startLocationService();
		}

	}

	/**
	 * 通过手机gps定位
	 */
	private void startLocationService() {

		location = null;

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 对于设备没有配备定位功能
		if (locationManager == null) {
			return;
		}

		// @see
		// http://developer.android.com/reference/android/location/LocationManager.html#getBestProvider%28android.location.Criteria,%20boolean%29
		final Criteria criteria = new Criteria();
		// 最佳做法是没有设置PowerRequirement
		// 精度不设置最佳实践
		// criteria.setAccuracy(Criteria.ACCURACY_FINE); ← Accuracy 你必须做的最好模式
		// 这些都是必要的
		criteria.setBearingRequired(false); // 方位不要
		criteria.setSpeedRequired(false); // 速度不要
		criteria.setAltitudeRequired(false); // 高度不要

		final String provider = locationManager.getBestProvider(criteria, true);
		if (provider == null) {
			// 如果没有启用的位置信息，如谷歌地图应用程序
			// [改进的功能您的位置]
			// 开始对话
			new AlertDialog.Builder(this)
					.setTitle(R.string.open_gps_module_title)
					.setMessage(R.string.open_gps_module_message)
					.setPositiveButton(R.string.menu_setting,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
									// 设置屏幕的终端位置へ遷移
									try {
										startActivity(new Intent(
												"android.settings.LOCATION_SOURCE_SETTINGS"));
									} catch (final ActivityNotFoundException e) {
										// 没有位置信息设定画面
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
								} // 何も行わない
							}).create().show();

			stopLocationService();
			return;
		}

		// // 效果作为最后获得一分钟之久的位置信息。
		// final Location lastKnownLocation =
		// locationManager.getLastKnownLocation(provider);
		// // XXX - 请更改确定必要的标准。
		// if (lastKnownLocation != null && (new Date().getTime() -
		// lastKnownLocation.getTime()) <= (5 * 60 * 1000L)) {
		// location = lastKnownLocation;
		// setLocation(lastKnownLocation);
		// new myThread().start();
		// return;
		// }

		// Toast の表示と LocationListener の生存時間启动计时器。
		locationTimer = new Timer(true);
		time = 0L;
		final Handler handler = new Handler();
		locationTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (time == 1000L) {
							// Toast.makeText(SendMessageActivity.this,
							// "开始确定位置", Toast.LENGTH_LONG).show();
						} else if (time >= (30 * 1000L)) {
							Toast.makeText(RetweetMessageActivity.this,
									R.string.obtain_location_info_failed,
									Toast.LENGTH_LONG).show();

							stopLocationService();
						}
						time = time + 1000L;
					}
				});
			}
		}, 0L, 1000L);

		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(final Location loc) {

				if (loc != null) {
					location = loc;
					locationHandler.sendEmptyMessage(OBTAIN_LOCATION_SUCESSED);
				}
			}

			@Override
			public void onProviderDisabled(final String provider) {
			}

			@Override
			public void onProviderEnabled(final String provider) {
			}

			@Override
			public void onStatusChanged(final String provider,
					final int status, final Bundle extras) {
			}
		};
		locationManager.requestLocationUpdates(provider, 60000, 0,
				locationListener);

	}

	/**
	 * 关闭手机gps定位
	 */
	private void stopLocationService() {
		if (locationTimer != null) {
			locationTimer.cancel();
			locationTimer.purge();
			locationTimer = null;
		}
		if (locationManager != null) {
			if (locationListener != null) {
				locationManager.removeUpdates(locationListener);
				locationListener = null;
			}
			locationManager = null;
		}
	}

	private int getTextCount(String updateText) {
		int count = 0;
		ArrayList<String> urlDataList = new ArrayList<String>();
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
		// 获取需要创建的String数组大小
		String strBuf = "";
		for (String urlData : urlDataList) {
			strBuf = strBuf + urlData;
		}
		if (MAX_TEXT_TWEETER_COUNT == 140) {
			count = updateText.length() - strBuf.length() + 20
					* urlDataList.size();
		} else {
			count = updateText.length() - strBuf.length() + 11
					* urlDataList.size();
		}

		return count;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Set BackGrouned Image.
	 */
	// -----------------------------------------------------------------------------
	private void loadBackGroundImage(String path) {

		if (path == null) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("-") == 0) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("/") == -1) {
			getWindow().setBackgroundDrawableResource(Integer.valueOf(path));
		} else {
			File file = new File(path);
			FileInputStream input = null;
			if (file.exists() && file.canRead() && file.isFile()) {
				try {
					input = new FileInputStream(file);
					// Get BitMap and set to background
					BitmapDrawable drawable = new BitmapDrawable(input);
					getWindow().setBackgroundDrawable(drawable);
				} catch (Exception e) {

				} catch (OutOfMemoryError e) {

				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
	}

	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				RetweetMessageActivity.this);
		LayoutInflater inflater = LayoutInflater
				.from(RetweetMessageActivity.this);
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
		gridView.setAdapter(new GridViewAdapter(RetweetMessageActivity.this,
				previewDialog, emotionData));

		// 设置emotionDialog 透明度
		WindowManager manager = previewDialog.getWindow().getWindowManager();
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		previewDialog.getWindow().setLayout(width * 9 / 10, height * 2 / 3);
	}

	private void openSelectDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		String[] items = null;
		if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData
				.getCurrentService())) {
			items = getResources().getStringArray(R.array.upload_items_cfb);
		} else {
			items = getResources().getStringArray(R.array.upload_items);
		}
		// items = getResources().getStringArray(R.array.sohu_select_operation);

		dialog.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case 0: {
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						Intent intent = new Intent(RetweetMessageActivity.this,
								PictureshowActivity.class);
						startActivityForResult(intent, 2);

					} else {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(intent, 1);
					}
					break;
				}
				case 1: {
					// Upload files only for CFB
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						Intent intent = new Intent();
						intent.setType("*/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(intent, 3);
					} else {
						dialog.dismiss();
					}
					break;
				}
				}

			}
		}).create().show();

	}

	/**
	 * 百度api通過網絡獲取定位信息
	 */
	private void initLocationNetWork() {

		netLocationListener = new com.baidu.mapapi.LocationListener() {

			public void onLocationChanged(Location loc) {
				if (loc != null) {
					// geoPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
					// (int) (loc.getLongitude() * 1E6));
					//
					// mKSearch.reverseGeocode(geoPoint);
					// mMapController.animateTo(geoPoint);
					// mMapController.setCenter(geoPoint); // 设置地图中心点
					// mylocationOverlay.enableMyLocation(); // 启用定位
					// mylocationOverlay.enableCompass(); // 启用指南针
					// mMapView.getOverlays().clear();
					// mMapView.getOverlays().add(mylocationOverlay);
					// 添加到MapView的覆盖物中
					// mMapView.getOverlays().add(new MyOverlay());
					location = loc;
					Log.e("net_lat", String.valueOf(loc.getLatitude()));
					Log.e("net_lon", String.valueOf(loc.getLongitude()));
				}

			}
		};
		if (netLocationListener != null) {
			CrowdroidApplication.getInstance().getMapManager()
					.getLocationManager()
					.requestLocationUpdates(netLocationListener);
			CrowdroidApplication.getInstance().getMapManager().start();
		}
	}

}
