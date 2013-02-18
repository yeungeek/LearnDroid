package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.dialog.AtUserSelectDialog;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.LongTweetDialog;
import com.anhuioss.crowdroid.dialog.MultiTweetSelectorDialog;
import com.anhuioss.crowdroid.dialog.TranslateDialog;
import com.anhuioss.crowdroid.dialog.UrlShortenDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.AsyncImageLoad;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;
import com.anhuioss.crowdroid.util.Imageback;
import com.anhuioss.crowdroid.util.TagAnalysis;

/**
 * @author oss
 * 
 */
public class SendMessageActivity extends Activity implements OnClickListener,
		TextWatcher, ServiceConnection {

	private com.baidu.mapapi.LocationListener netLocationListener;

	protected static final int OBTAIN_LOCATION_FAILED = 0;

	protected static final int OBTAIN_LOCATION_SUCESSED = 1;

	private SharedPreferences sharePreference;

	private SharedPreferences.Editor editor;

	ImageView image_Preview;

	private String imageUrl;

	/** Upload Message */
	// TextView uploadMessage;

	TextView head_name;

	/** File Path */

	TextView filePath;
	TextView alertFilePath;

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

	/** Confirm */
	Button send;

	/** Cancel */
	Button cancelButton;

	/** Intent Flag */
	String action = null;

	/** Intent Message */
	String target = null;

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

	private StatusData statusData;

	private HandleProgressDialog progress;

	ArrayList<AccountData> multiTweetAccounts;

	/** send data list */
	ArrayList<Object[]> sendTweetList;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	private String outerUrl;

	private String inputtext;

	public static ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();

	public static ArrayList<String> htmlDataListForEmotion;

	public ArrayList<String> imagechoosed = new ArrayList<String>();

	private Handler mHandler = new Handler();

	private AlertDialog previewDialog;

	private String cid;

	private String id;

	private String sourceMessageId;

	private int page = 0;

	private int priorityNumber = 0;

	private static ArrayList<String> multiTweetAccountSelected;

	String atUserListName = null;

	private LinearLayout ly;

	public static int atcount = 0;

	private AsyncImageLoad imageLoader = new AsyncImageLoad();
	private Imageback callbackImpl;

	// 添加附件的数组
	public ArrayList<String> attachedChoosed = new ArrayList<String>();

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();

			// remove first(index = 0) data from list
			if (sendTweetList.size() > 0) {
				sendTweetList.remove(0);
			}

			// tweet(list.get(0))
			if (sendTweetList.size() > 0 && multiTweetChecked.isChecked()) {
				// Wait For A Moment
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tweet(sendTweetList.get(0));
			}

			if (statusCode != null && statusCode.equals("200")) {

				if (service.equals(IGeneral.SERVICE_NAME_WANGYI)
						&& type == CommHandler.TYPE_UPLOAD_IMAGE) {
					ParseHandler parseHandler = new ParseHandler();
					imageUrl = (String) parseHandler.parser(service, type,
							statusCode, message);
					tweetwangyi();
				}

				if (type == CommHandler.TYPE_GET_EMOTION) {

					ParseHandler parseHandler = new ParseHandler();
					emotionList = (ArrayList<EmotionInfo>) parseHandler.parser(
							service, type, statusCode, message);
					if (!emotionList.isEmpty()) {
						// showEmotionsDialog();
						initEmotionView(emotionList);
					}

				} else if (type == CommHandler.TYPE_REPLY_TO_COMMENT
						&& retweetCheckeBox.isChecked()) {

					// Wait For A Moment
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tweet(new Object[] { 0, new AccountData() });

				} else {

					filePath.setText("");

					if (sendTweetList.size() == 0
							|| !multiTweetChecked.isChecked()) {
						if (action != null && action.equals("timeline")) {
							updateText.setText("");
						} else {
							finish();
						}
						createTweetList();
					}

				}
				if (!multiTweetChecked.isChecked()) {
					finish();
				} else if (sendTweetList.size() == 1) {
					finish();
				}
			} else if (statusCode != null
					&& statusCode.equals("250")
					&& service != null
					&& service
							.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				Toast.makeText(SendMessageActivity.this,
						getString(R.string.error_message_rejected_for_ngword),
						Toast.LENGTH_LONG).show();
			} else {
				filePath.setText("");
				Toast.makeText(
						SendMessageActivity.this,
						ErrorMessage.getErrorMessage(SendMessageActivity.this,
								statusCode), Toast.LENGTH_LONG).show();
				if (sendTweetList.size() == 0 || !multiTweetChecked.isChecked()) {
					finish();
				}
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		SettingData settingData = crowdroidApplication.getSettingData();
		String imagePath = settingData.getWallpaper();
		statusData = crowdroidApplication.getStatusData();
		setContentView(R.layout.send_message);
		sharePreference = getSharedPreferences("SHARE_INIT_STATUS", 0);
		// Find Views
		// uploadMessage = (TextView) findViewById(R.id.upload_message);
		filePath = (TextView) findViewById(R.id.file_path);
		alertFilePath = (TextView) findViewById(R.id.txt_alert_files);
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
		send = (Button) findViewById(R.id.send);
		head_name = (TextView) findViewById(R.id.head_Name);

		// images=(WebView) findViewById (R.id.ly3);
		// images.setVerticalScrollBarEnabled(false);
		// images.setHorizontalScrollBarEnabled(true);

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

		Bundle bundle = getIntent().getExtras();
		action = bundle.getString("action");
		target = bundle.getString("target");
		sourceMessageId = bundle.containsKey("message_id") ? bundle
				.getString("message_id") : "";

		if (action != null && target != null && !action.equals("reply")) {
			updateText.setText(target);
			head_name.setText(R.string.comment);
		}

		if (action != null && action.equals("reply")) {
			filePath.setVisibility(View.GONE);
			translationButton.setVisibility(View.GONE);
			uploadImageButton.setVisibility(View.GONE);
			shortenUrlButton.setVisibility(View.GONE);
			multiTweetChecked.setVisibility(View.GONE);
			retweetCheckeBox.setVisibility(View.VISIBLE);

			cid = bundle.getString("cid");
			id = bundle.getString("id");
			updateText.setText(target);
		}

		Intent outerIntent = getIntent();
		String action = outerIntent.getAction();
		String type = outerIntent.getType();

		if (action != null && type != null && Intent.ACTION_SEND.equals(action)
				&& "text/plain".equals(type) && !action.equals("reply")) {

			editor = sharePreference.edit();
			String initContent = sharePreference.getString(
					"SHARE_OUTSIDE_CONTENT", "");

			outerUrl = outerIntent.getStringExtra(Intent.EXTRA_TEXT);
			updateText.setText(initContent + " " + outerUrl);
			editor.putString("SHARE_OUTSIDE_CONTENT", outerUrl).commit();

		}
		filePath.setText("");

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			initAutoComplete("cfb", updateText);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			initAutoComplete("twitter", updateText);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_SINA)) {
			initAutoComplete("sina", updateText);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TENCENT)) {
			initAutoComplete("tencent", updateText);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_SOHU)) {
			initAutoComplete("sohu", updateText);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_WANGYI)) {
			initAutoComplete("wangyi", updateText);
		}
		// if(statusData.getCurrentService().equals(
		// IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)||
		// statusData.getCurrentService().equals(
		// IGeneral.SERVICE_NAME_TWITTER)
		// ){
		// PictureshowActivity.EmptyChoose();
		// }
	}

	@Override
	public void onStart() {
		super.onStart();

		priorityNumber = 0;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			if (RecorderActivity.isSave) {
				filePath.setText(RecorderActivity.getAudioFilePath());
			}

		}

		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
			if (filePath.getText().toString().length() == 0) {
				filePath.setText(CameraActivity.getImageFilePath());
			}
		}

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			SharedPreferences status = getSharedPreferences("status",
					Context.MODE_PRIVATE);
			countText.setText(status.getString("max_input_charactor", "2000"));
			int m = Integer.valueOf(countText.getText().toString());
			MAX_CFB_TEXT_COUNT = m;
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
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_WANGYI)) {
			countText.setText("163");
			MAX_TEXT_COUNT = 163;
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			countText.setText("240");
			MAX_TEXT_COUNT = 240;
		}
		notifyTextHasChanged();

		startCFBLocationService();

		// Get Account For Multi-Tweet
		multiTweetAccounts = crowdroidApplication.getAccountList()
				.getMultiTweetAccount();
		for (AccountData account : multiTweetAccounts) {
			String uid = account.getUid();
			uid = statusData.getCurrentUid();
			String server = account.getService();
			server = statusData.getCurrentService();
			if (account.getUid().equals(statusData.getCurrentUid())
					&& account.getService().equals(
							statusData.getCurrentService())) {
				multiTweetAccounts.remove(account);
				break;
			}
		}

		initViewsByService(statusData.getCurrentService());

		SettingData settingData = crowdroidApplication.getSettingData();
		String imagePath = settingData.getWallpaper();
		String fontColor = settingData.getFontColor();

		// Set Text Color
		// uploadMessage.setTextColor(Integer.valueOf(fontColor));
		filePath.setTextColor(Integer.valueOf(fontColor));

		// Set Background
		loadBackGroundImage(imagePath);

		Intent inputIntent = getIntent();
		Bundle b = inputIntent.getExtras();

		Bundle bundle = getIntent().getExtras();

		// feedback
		if (bundle.containsKey("feedback")) {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				String language = this.getApplicationContext().getResources()
						.getConfiguration().locale.getLanguage();
				if (language != null && language.equals("zh")) {
					updateText.setText("@anhuioss_cn\n" + " " + "#crowdroid "
							+ " ");
				} else if (language != null && language.equals("ja")) {
					updateText.setText("@AnhuiOSS\n" + " " + "#crowdroid "
							+ " ");
				} else {
					updateText.setText("@AnhuiOSS_EN\n" + " " + "#crowdroid "
							+ " ");
				}
			}
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)) {
				updateText.setText("@安徽开源软件有限公司\n" + "#crowdroid#" + " ");
			}
		}
		if (bundle.containsKey("filePath")) {
			image_Preview.setVisibility(View.VISIBLE);
			filePath.setText(bundle.getString("filePath"));
			BitmapFactory.Options opp = new BitmapFactory.Options();
			opp.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(bundle.getString("filePath"),
					opp);
			image_Preview.setImageBitmap(bm);
			imagechoosed.add(bundle.getString("filePath"));
			updateText.setText(sharePreference.getString(
					"EDIT_INIT_STATUS_CAMERA", ""));
			// cfb 发布地图截图的提示内容
			if (bundle.containsKey("location-message")) {
				updateText.setText(bundle.getString("location-message"));
			}
		}
		if (bundle.containsKey("filePath0")) {
			// image_Preview.setVisibility(View.VISIBLE);
			filePath.setText(bundle.getString("filePath0"));
			imagechoosed.add(bundle.getString("filePath"));
			// BitmapFactory.Options opp = new BitmapFactory.Options();
			// opp.inSampleSize = 2;
			// Bitmap bm =
			// BitmapFactory.decodeFile(bundle.getString("filePath"),
			// opp);
			// image_Preview.setImageBitmap(bm);
			// imagechoosed.add(bundle.getString("filePath"));
			// updateText.setText(sharePreference.getString(
			// "EDIT_INIT_STATUS_CAMERA", ""));
			// cfb 发布地图截图的提示内容
			// if (bundle.containsKey("location-message")) {
			// updateText.setText(bundle.getString("location-message"));
			// }
		}

		// insert mention users
		if (bundle.containsKey("atUserName") && atcount == 1) {
			String front = sharePreference.getString("EDIT_INIT_STATUS_FRONT",
					"");
			String behind = sharePreference.getString(
					"EDIT_INIT_STATUS_BEHIND", "");
			updateText.setText(front + bundle.getString("atUserName") + behind);
			updateText.setSelection(updateText.getText().length());
			atcount = 0;
		}
		// add emotion
		if (bundle.containsKey("emotionName")) {
			String front = sharePreference.getString("EDIT_INIT_STATUS_FRONT",
					"");
			String behind = sharePreference.getString(
					"EDIT_INIT_STATUS_BEHIND", "");
			updateText
					.setText(front + bundle.getString("emotionName") + behind);
			updateText.setSelection(updateText.getText().length());

		}
		if (bundle.containsKey("send")) {
			if (b.getString("send") != null) {
				updateText.setText(inputtext);
			}
		}
		if (bundle.getBoolean("tamahiyo", false)) {

			SharedPreferences.Editor editor = sharePreference.edit();
			editor.putBoolean("WHEATHER_FROM_BENESE", true);
			editor.commit();

			if (bundle.containsKey("message")) {
				if (bundle.getString("message") != null) {
					updateText.setText(bundle.getString("message"));
				}
			}
			if (bundle.containsKey("file_path")) {
				if (bundle.getString("file_path") != null) {
					image_Preview.setVisibility(View.VISIBLE);
					filePath.setText(bundle.getString("file_path"));
					BitmapFactory.Options opp = new BitmapFactory.Options();
					opp.inSampleSize = 2;
					Bitmap bm = BitmapFactory.decodeFile(
							bundle.getString("file_path"), opp);
					image_Preview.setImageBitmap(bm);
				}
			}
			bundle.remove("tamahiyo");
		}

		Uri uri = (Uri) b.get(Intent.EXTRA_STREAM);
		if (uri != null) {
			if (uri.toString().startsWith("file://")) {
				filePath.setText(uri.toString().replace("file://", ""));
			} else {
				ContentResolver cr = this.getContentResolver();
				Cursor cursor = cr.query(uri, null, null, null, null);
				if (cursor != null) {
					cursor.moveToFirst();
					filePath.setText(cursor.getString(1));
				}
			}
		}

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		createTweetList();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		filePath.setText("");
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			PictureshowActivity.EmptyChoose();
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		multiTweetAccountSelected = null;
		// Unbind Service
		unbindService(this);

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (progress != null) {
			progress.dismiss();
		}

		SharedPreferences status = getSharedPreferences("status", MODE_PRIVATE);
		status.edit().putString("editText", updateText.getText().toString())
				.commit();
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
		TimelineActivity.isBackgroundNotificationFlag = true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences status = getSharedPreferences("status", MODE_PRIVATE);
		String text = updateText.getText().toString();
		if (text == null && text == "") {
			updateText.setText(status.getString("editText", ""));
		}

		if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData
				.getCurrentService())) {
			// Initial Priority
			priorityNumber = priorityNumber % 3;
			switch (priorityNumber) {
			case 0: {
				priorityText.setText(getString(R.string.priority_normal));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#006000"));
				break;
			}
			case 1: {
				priorityText.setText(getString(R.string.priority_middle));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#660040"));
				break;
			}
			case 2: {
				priorityText.setText(getString(R.string.priority_high));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#ff0000"));
				break;
			}
			default: {
				priorityText.setText(getString(R.string.priority_normal));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#006000"));
			}
			}
		}
		TimelineActivity.isBackgroundNotificationFlag = false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				// 单图发送
				image_Preview.setVisibility(View.VISIBLE);
				Uri selectedImageUri = data.getData();

				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(selectedImageUri, projection,
						null, null, null);
				if (cursor != null) {
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
					if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
							.getCurrentService())) {
						MAX_TEXT_COUNT = 140 - 25;
						notifyTextHasChanged();
					}
				}
			}
			if (requestCode == 2) {
				// 多图发送
				Bundle bundle = data.getExtras();
				ly.removeAllViewsInLayout();
				// 如果没有附件直接赋值
				if (bundle != null) {
					imagechoosed = bundle.getStringArrayList("imagepath");
				}
				filePath.setText(imagechoosed.get(0));
				ly.setVisibility(View.VISIBLE);
				for (int i = 0; i < imagechoosed.size(); i++) {
					ImageView imageitem = new ImageView(this);
					imageitem.setMaxHeight(100);
					imageitem.setMaxWidth(100);
					loadImage(imagechoosed.get(i), imageitem);
					ly.addView(imageitem, 100, 100);
				}
				if (attachedChoosed.size() > 0) {
					for (int i = 0; i < attachedChoosed.size(); i++) {
						imagechoosed.add(attachedChoosed.get(i));
					}
				}
				if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
						.getCurrentService())) {
					MAX_TEXT_COUNT = 140 - 25 * imagechoosed.size();
					notifyTextHasChanged();
				}
				if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
						.equals(statusData.getCurrentService())) {
					// 添加附件的提示路径
					alertFilePath.setVisibility(View.VISIBLE);
					StringBuffer alert = new StringBuffer();
					alert.append(getString(R.string.attach_path) + "\n");

					for (int i = 0; i < imagechoosed.size(); i++) {
						alert.append(imagechoosed.get(i) + "\n");
					}
					alertFilePath.setText(alert.toString());
				}
			}
			if (requestCode == 3) {
				// cfb 上传附件
				try {
					String url = Uri.decode(data.getDataString());
					Uri selectedImageUri = Uri.parse(url);
					String[] projection = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(selectedImageUri, projection,
							null, null, null);
					String selectedImagePath = "";
					if (cursor != null) {
						cursor.moveToFirst();
						int column_index = cursor
								.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
						selectedImagePath = cursor.getString(column_index);
					} else {
						selectedImagePath = url.substring(url
								.indexOf("file///") + 8);
					}

					attachedChoosed.add(selectedImagePath);

					imagechoosed.add(selectedImagePath);
					filePath.setText(selectedImagePath);
					// 添加附件的提示路径
					alertFilePath.setVisibility(View.VISIBLE);
					StringBuffer alert = new StringBuffer();
					alert.append(getString(R.string.attach_path) + "\n");

					for (int i = 0; i < imagechoosed.size(); i++) {
						alert.append(imagechoosed.get(i) + "\n");
					}
					alertFilePath.setText(alert.toString());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

			Intent home = new Intent(SendMessageActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}
		case R.id.translateButton: {

			// Translation
			new TranslateDialog(this, updateText).show();
			break;

		}
		case R.id.uploadImageButton: {

			openSelectDialog();

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
			startActivity(intent);
			this.finish();

			break;
		}
		case R.id.recorderButton: {
			RecorderActivity.setContextAndEdit(this, updateText);
			Intent intent = new Intent(this, RecorderActivity.class);
			startActivity(intent);
			// finish();
			break;
		}
		case R.id.multiTweet: {
			if (multiTweetChecked.isChecked()) {
				if (multiTweetAccounts.isEmpty()) {
					Toast.makeText(SendMessageActivity.this, "No Accounts!",
							Toast.LENGTH_SHORT).show();
				} else {
					new MultiTweetSelectorDialog(this, multiTweetAccounts)
							.show();
				}
			} else {
				multiTweetAccountSelected = null;
			}
			break;
		}
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
			usd = new AtUserSelectDialog(this, "send_message");
			editor.commit();

			usd.setTitle("@" + getString(R.string.insert_at_name));
			usd.show();
			break;
		}
		case R.id.send: {

			if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData
					.getCurrentService())) {
				if (callbackImpl != null) {
					int maxSize = callbackImpl.getTotalBitmapSize();
					if (maxSize > 6 * 1024 * 1024) {
						send.setEnabled(false);
						break;
					} else {
						send.setEnabled(true);
					}
				}
			}

			Intent outerIntent = getIntent();
			String action = outerIntent.getAction();
			String type = outerIntent.getType();

			if (action != null && type != null
					&& Intent.ACTION_SEND.equals(action)
					&& "text/plain".equals(type) && !action.equals("reply")) {
				editor.putString("SHARE_OUTSIDE_CONTENT", "").commit();
			}
			// 新浪发送图片，“分享图片”文字自动填充
			if (!"".equals(filePath.getText().toString())
					&& ("").equals(updateText.getText().toString())
					&& IGeneral.SERVICE_NAME_SINA.equals(statusData
							.getCurrentService())) {
				updateText.setText("分享图片");
			}
			if (("").equals(updateText.getText().toString())) {
				Toast.makeText(SendMessageActivity.this,
						getResources().getString(R.string.sendmsg_tip),
						Toast.LENGTH_LONG).show();
			} else {
				if (updateText.getText().toString().contains("@")) {
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						saveHistory("cfb", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
						saveHistory("twitter", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SINA)) {
						saveHistory("sina", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)) {
						saveHistory("tencent", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)) {
						saveHistory("sohu", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)) {
						saveHistory("wangyi", updateText);
					}
				}
				if (updateText.getText().toString().contains("#")) {
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						saveTrendHistory("cfb", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
						saveTrendHistory("twitter", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SINA)) {
						saveTrendHistory("sina", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)) {
						saveTrendHistory("tencent", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)) {
						saveTrendHistory("sohu", updateText);
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_WANGYI)) {
						saveTrendHistory("wangyi", updateText);
					}
				}

				createTweetList();

				if (action != null && action != "" && action.equals("reply")
						&& !retweetCheckeBox.isChecked()) {
					showProgressDialog();
					replyMessage(statusData.getCurrentService());
				} else if (action != null && action != ""
						&& action.equals("reply")
						&& retweetCheckeBox.isChecked()) {
					showProgressDialog();
					retweetMessage(statusData.getCurrentService());

				}

				else {
					if (sendTweetList.size() > 0) {
						tweet(sendTweetList.get(0));
					}
				}
			}

			break;
		}
		case R.id.priorityTextColor: {
			priorityNumber++;
			priorityNumber = priorityNumber % 3;
			switch (priorityNumber) {
			case 0: {
				priorityText.setText(getString(R.string.priority_normal));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#006000"));
				break;
			}
			case 1: {
				priorityText.setText(getString(R.string.priority_middle));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#660040"));
				break;
			}
			case 2: {
				priorityText.setText(getString(R.string.priority_high));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#ff0000"));
				break;
			}
			default: {
				priorityText.setText(getString(R.string.priority_normal));
				priorityTextColor.setBackgroundColor(Color
						.parseColor("#006000"));
			}
			}
			break;
		}
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

		default: {

		}
		}

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		notifyTextHasChanged();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
	}

	private void initViewsByService(String service) {

		Bundle bundle = getIntent().getExtras();

		if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {

			longTweetButton.setVisibility(View.GONE);
			priorityText.setVisibility(View.VISIBLE);
			priorityTextColor.setVisibility(View.VISIBLE);

		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {

			emotionButton.setVisibility(View.GONE);
			addTrends.setVisibility(View.GONE);

		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {

			emotionButton.setVisibility(View.GONE);

		} else if (service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_TENCENT)
				|| service.equals(IGeneral.SERVICE_NAME_WANGYI)) {

			longTweetButton.setVisibility(View.GONE);
			emotionButton.setVisibility(View.VISIBLE);

		} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
			longTweetButton.setVisibility(View.GONE);
			emotionButton.setVisibility(View.VISIBLE);
			shortenUrlButton.setVisibility(View.GONE);
		} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
			multiTweetChecked.setVisibility(View.GONE);
			filePath.setVisibility(View.GONE);
			uploadImageButton.setVisibility(View.GONE);
			shortenUrlButton.setVisibility(View.GONE);
			longTweetButton.setVisibility(View.GONE);
			cameraButton.setVisibility(View.GONE);
			recorderButton.setVisibility(View.GONE);
			addTrends.setVisibility(View.GONE);
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			head_name.setText(R.string.update_status);
		} else if (action != null && action.equals("reply")) {
			head_name.setText(R.string.comment);
		} else {
			head_name.setText(R.string.update);
		}
	}

	private void replyMessage(String service) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("comment", updateText.getText().toString());
		map.put("cid", cid);
		map.put("id", id);
		map.put("reply_id", sourceMessageId);

		try {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)
					|| service
							.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
					|| service.equals(IGeneral.SERVICE_NAME_SOHU)
					|| service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
				apiServiceInterface.request(service,
						CommHandler.TYPE_REPLY_TO_COMMENT, apiServiceListener,
						map);
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private void retweetMessage(String service) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("comment", updateText.getText().toString());
		map.put("message_id", cid);
		map.put("is_comment", "1");

		try {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)
					|| service
							.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
					|| service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				apiServiceInterface.request(service, CommHandler.TYPE_RETWEET,
						apiServiceListener, map);
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void setEmotion(final String pharse) {

		final int where = updateText.getSelectionStart();

		mHandler.post(new Runnable() {
			public void run() {
				updateText.getText().insert(where, pharse);
				if (previewDialog != null) {
					previewDialog.dismiss();
				}
			}
		});

	}

	// -----------------------------------------------------------------------------
	/**
	 * Set BackGrouned Image.
	 */
	// -----------------------------------------------------------------------------
	private void loadBackGroundImage(String path) {

		if (path == null || path.equals("0")) {
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

	// -------------------------------------------------------
	/**
	 * this method is used to get the current location
	 */
	// --------------------------------------------------------
	// protected void initGps() {
	// location = null;
	// // Get location manager
	// locationManager = (LocationManager)
	// getSystemService(Context.LOCATION_SERVICE);
	//
	// if (locationManager == null) {
	// return;
	// }
	// Criteria criteria = new Criteria();
	// criteria.setAltitudeRequired(false);//提供海拔信息
	// criteria.setBearingRequired(false);//获取方向的精度
	// criteria.setSpeedRequired(false);//设置是否需要速度
	// criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置精度
	// criteria.setPowerRequirement(Criteria.POWER_LOW);//设置电量消耗的级别
	// criteria.setCostAllowed(false);//得到方位信息的时候是否会产生费用
	//
	// final String provider = locationManager.getBestProvider(criteria, true);
	//
	// // Open Gps Module
	// if (provider == null) {
	// new AlertDialog.Builder(this)
	// .setTitle(R.string.open_gps_module_title)
	// .setMessage(R.string.open_gps_module_message)
	// .setPositiveButton(R.string.menu_setting,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// startActivity(new Intent(
	// "android.settings.LOCATION_SOURCE_SETTINGS"));
	// }
	// })
	// .setNegativeButton(R.string.cancel,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	//
	// }
	// }).create().show();
	// closeGps();
	// return;
	// }
	//
	// locationListener = new LocationListener() {
	// public void onLocationChanged(Location loc) {
	// // Get location
	// if (loc != null) {
	// location = loc;
	// locationHandler.sendEmptyMessage(OBTAIN_LOCATION_SUCESSED);
	// }
	// }
	//
	// public void onProviderDisabled(String provider) {}
	// public void onProviderEnabled(String provider) {}
	// public void onStatusChanged(String provider, int status,Bundle extras) {}
	// };
	// // request
	// locationManager.requestLocationUpdates(provider, 60000, 0,
	// locationListener);
	//
	// }

	// public void closeGps() {
	// if (locationManager != null) {
	// if (locationListener != null) {
	// locationManager.removeUpdates(locationListener);
	// locationListener = null;
	// }
	// locationManager = null;
	// }
	// }
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

	private void createTweetList() {

		sendTweetList = new ArrayList<Object[]>();

		// add current accont 0
		Object[] currentTweetData = new Object[2];
		currentTweetData[0] = 0;
		currentTweetData[1] = null;
		sendTweetList.add(currentTweetData);

		// add multi-tweet account 1 - x
		Object[] multiTweetData;
		for (AccountData account : multiTweetAccounts) {
			if (multiTweetAccountSelected == null) {
				multiTweetData = new Object[2];
				multiTweetData[0] = 1;
				multiTweetData[1] = account;
				sendTweetList.add(multiTweetData);
			} else {
				for (String message : multiTweetAccountSelected) {
					if (message.contains(account.getUserScreenName())
							&& message.contains(account.getService())) {
						multiTweetData = new Object[2];
						multiTweetData[0] = 1;
						multiTweetData[1] = account;
						sendTweetList.add(multiTweetData);
					}
				}
			}
		}

	}

	private void tweet(Object[] tweetData) {

		int tweetType = (Integer) tweetData[0];
		AccountData account = (AccountData) tweetData[1];

		try {

			if (tweetType == 0) {

				// Update
				showProgressDialog();

				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("status", updateText.getText().toString());
				parameters.put("reply_id", sourceMessageId);
				parameters.put("important_level",
						String.valueOf(priorityNumber + 2));

				// Location
				if (location != null) {
					parameters.put("geo_latitude",
							String.valueOf(location.getLatitude()));
					parameters.put("geo_longitude",
							String.valueOf(location.getLongitude()));
					Log.e("send_lat", String.valueOf(location.getLatitude()));
					Log.e("send_lon", String.valueOf(location.getLongitude()));
				} else if (location == null
						&& IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
								.equals(statusData.getCurrentService())) {
					// Pop up message (could not obtain Location Info)
					locationHandler.sendEmptyMessage(OBTAIN_LOCATION_FAILED);
				}

				if (filePath.getText().toString() != null
						&& filePath.getText().equals("")) {

					// Request (get service from status information)
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_UPDATE_STATUS, apiServiceListener,
							parameters);
				} else {
					if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS
							.equals(statusData.getCurrentService())
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TWITTER)) {
						parameters.put("filePath", imagechoosed);
					} else {
						parameters.put("filePath", filePath.getText()
								.toString());
					}
					// Request (get service from status information)
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_UPLOAD_IMAGE, apiServiceListener,
							parameters);

				}

			} else if (tweetType == 1) {

				showProgressDialog();

				// Prepare Parameters
				Map<String, Object> parameters1;
				parameters1 = new HashMap<String, Object>();
				parameters1.put("status", updateText.getText().toString());
				parameters1.put("reply_id", sourceMessageId);
				parameters1.put("important_level",
						String.valueOf(priorityNumber + 2));

				// Location
				if (location != null) {
					parameters1.put("geo_latitude",
							String.valueOf(location.getLatitude()));
					parameters1.put("geo_longitude",
							String.valueOf(location.getLongitude()));
				}

				parameters1.put("service", account.getService());
				if (account.getService() != null
						&& (account.getService().equals(
								IGeneral.SERVICE_NAME_TWITTER) || account
								.getService()
								.equals(IGeneral.SERVICE_NAME_SINA))
						|| account.getService().equals(
								IGeneral.SERVICE_NAME_SOHU)) {
					parameters1.put("user_name", account.getAccessToken());
					parameters1.put("user_password", account.getTokenSecret());
				} else {
					parameters1.put("user_name", account.getUserName());
					parameters1.put("user_password", account.getPassword());
					parameters1.put("apiUrl", account.getApiUrl());
				}

				// Request (get service from status information)
				apiServiceInterface.request(account.getService(),
						CommHandler.TYPE_UPDATE_STATUS, apiServiceListener,
						parameters1);

			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private void tweetwangyi() {

		// Update
		showProgressDialog();

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("status", updateText.getText().toString() + imageUrl);
		parameters.put("reply_id", sourceMessageId);
		parameters.put("important_level", String.valueOf(priorityNumber + 2));

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

		// Request (get service from status information)
		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_UPDATE_STATUS, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getEmotionDataForWebView() {

		if (emotionList != null && emotionList.size() >= 30
				&& htmlDataListForEmotion == null) {
			htmlDataListForEmotion = new ArrayList<String>();
			for (int i = 0; i < emotionList.size() / 30; i++) {
				StringBuffer result = new StringBuffer();
				result.append("<table border=\"0\" align=\"center\">");

				for (int x = i * 30; x < (i + 1) * 30; x = x + 6) {
					result.append("<tr>");
					for (int y = 0; y < 6; y++) {
						EmotionInfo emotionInfo = emotionList.get(x + y);
						result.append("<td><img width=\"22px\" height=\"22px\" src=\""
								+ emotionInfo.getUrl()
								+ "\" onClick=\"getImage('"
								+ emotionInfo.getPhrase() + "');\"/></td>");
					}
					result.append("</tr>");
				}

				result.append("</table>");
				htmlDataListForEmotion.add(result.toString());

			}
		}

		return htmlDataListForEmotion.get(page);

		/**
		 * Log.i("", "wangliang : " + System.currentTimeMillis());
		 * 
		 * if (htmlDataForEmotion != null) { Log.i("", "wangliang : y");
		 * Log.i("", "wangliang : " + System.currentTimeMillis()); return
		 * htmlDataForEmotion; }
		 * 
		 * Log.i("", "wangliang : n");
		 * 
		 * StringBuffer result = new StringBuffer();
		 * 
		 * int size = emotionList.size(); size = 30; int i = size / 6; int j =
		 * size % 6;
		 * 
		 * result.append("
		 * <table border=\"0\" align=\"center\">
		 * ");
		 * 
		 * for (int x = 0; x < i; x++) {
		 * 
		 * result.append("
		 * <tr>
		 * ");
		 * 
		 * for (int y = 0; y < 6; y++) {
		 * 
		 * EmotionInfo emotionInfo = emotionList.get(x * 6 + y); result.append("
		 * <td><img width=\"22px\" height=\"22px\" src=\"" +
		 * emotionInfo.getUrl() + "\" onClick=\"getImage('" +
		 * emotionInfo.getPhrase() + "');\"/></td>");
		 * 
		 * }
		 * 
		 * result.append("
		 * </tr>
		 * ");
		 * 
		 * }
		 * 
		 * if (j != 0) {
		 * 
		 * result.append("
		 * <tr>
		 * ");
		 * 
		 * for (int x = 0; x < j; x++) {
		 * 
		 * EmotionInfo emotionInfo = emotionList.get(i * 6 + x); result.append("
		 * <td><img width=\"22px\" height=\"22px\" src=\"" +
		 * emotionInfo.getUrl() + "\" onClick=\"getImage('" +
		 * emotionInfo.getPhrase() + "');\"/></td>");
		 * 
		 * }
		 * 
		 * result.append("
		 * </tr>
		 * ");
		 * 
		 * }
		 * 
		 * result.append("
		 * </table>
		 * ");
		 * 
		 * htmlDataForEmotion = result.toString();
		 * 
		 * Log.i("", "wangliang : " + System.currentTimeMillis());
		 * 
		 * return htmlDataForEmotion;
		 */

	}

	private void notifyTextHasChanged() {
		int leftCount = 0;
		//
		if (MAX_CFB_TEXT_COUNT != 140) {
			leftCount = MAX_CFB_TEXT_COUNT
					- updateText.getText().toString().length();

		} else {
			int count = getTextCount(updateText.getText().toString());
			leftCount = MAX_TEXT_COUNT - count;
		}
		countText.setText(String.valueOf(leftCount));

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
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TWITTER)) {
						Intent intent = new Intent(SendMessageActivity.this,
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

	public static void setMultiTweetSelected(ArrayList<String> multiTweetAccount) {
		multiTweetAccountSelected = multiTweetAccount;
	}

	/**
	 * cfb开启定位
	 */
	private void startCFBLocationService() {
		// TODO Auto-generated method stub

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {

			initLocationNetWork();// 利用百度地图 ，开启定位

			PackageManager pm = getPackageManager();
			if (location == null
					&& netLocationListener == null
					&& pm != null
					&& pm.checkPermission(permission.ACCESS_FINE_LOCATION,
							getPackageName()) == PackageManager.PERMISSION_GRANTED) {

				startLocationService();
			}
		}
	}

	// -----------------------------------------------------
	/**
	 * 啓動手機gps定位
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
							Toast.makeText(SendMessageActivity.this,
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
	 * 關閉手機gps定位
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

		int httpCount;

		int noHttpCount;

		ArrayList<String> urlDataListHttp = new ArrayList<String>();
		ArrayList<String> urlDataListHttps = new ArrayList<String>();
		// 提取Url所需的正则表达式
		String regexStr = "http://[^ ^,^!^;^`^~^\n^，^！^；^\u4e00-\u9fa5]*";
		// 创建正则表达式模版
		Pattern pattern = Pattern.compile(regexStr);
		// 创建Url字段匹配器,待查询字符串Data为其参数
		Matcher m = pattern.matcher(updateText);
		while (m.find()) {
			if (!urlDataListHttp.contains(m.group())) {
				urlDataListHttp.add(m.group());
			}
		}
		if (updateText.contains("https:")) {
			// 提取Url所需的正则表达式
			String regexStr1 = "https://[^ ^,^!^;^`^~^\n^，^！^；^\u4e00-\u9fa5]*";
			// 创建正则表达式模版
			Pattern pattern1 = Pattern.compile(regexStr1);
			// 创建Url字段匹配器,待查询字符串Data为其参数
			Matcher m1 = pattern1.matcher(updateText);
			while (m1.find()) {
				if (!urlDataListHttps.contains(m1.group())) {
					urlDataListHttps.add(m1.group());
				}
			}

		}
		// 获取需要创建的String数组大小
		String strBuf = "";
		for (String urlData : urlDataListHttp) {
			strBuf = strBuf + urlData;
		}
		for (String urlData1 : urlDataListHttps) {
			strBuf = strBuf + urlData1;
		}
		if (MAX_TEXT_TWEETER_COUNT == 140) {
			count = updateText.length() - strBuf.length() + 20
					* urlDataListHttp.size() + urlDataListHttps.size();
		} else {
			if (urlDataListHttp.size() > 0 && urlDataListHttps.size() == 0) { // 只含有http链接
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)) {
					// http sina作为10个字符计算
					httpCount = 10 * urlDataListHttp.size();

					noHttpCount = updateText.length() - strBuf.length();

					count = httpCount + noHttpCount;
				} else {
					httpCount = 11 * urlDataListHttp.size();

					noHttpCount = updateText.length() - strBuf.length();
					// 其他服务器作为11个字符。暂时不考虑CFB
					count = httpCount + noHttpCount;
				}
			} else if (urlDataListHttp.size() == 0
					&& urlDataListHttps.size() > 0) {// 只含有https链接
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)) {
					// sina 將https 作爲17個字符
					httpCount = 17 * urlDataListHttps.size();

					noHttpCount = updateText.length() - strBuf.length();

					count = httpCount + noHttpCount;
				} else {
					httpCount = 11 * urlDataListHttps.size();

					noHttpCount = updateText.length() - strBuf.length();
					// 其他服务器作为11个字符。暂时不考虑CFB
					count = httpCount + noHttpCount;
				}
			} else if (urlDataListHttp.size() > 0
					&& urlDataListHttps.size() > 0) { // 既含有http又含有https
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)) {
					httpCount = 10 * urlDataListHttp.size();
					// https sina作为17个字符计算
					int httpsCount = 17 * urlDataListHttps.size();

					noHttpCount = updateText.length() - strBuf.length();

					count = httpCount + noHttpCount + httpsCount;

				} else {
					httpCount = 2 * 11 * urlDataListHttps.size();

					noHttpCount = updateText.length() - strBuf.length();

					count = httpCount + noHttpCount;
				}
			} else if (urlDataListHttp.size() == 0
					&& urlDataListHttps.size() == 0) { // 既不含有http又不含有https
			 
					
					count =updateText.length();
				
			}

		}

		return count;
	}

	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				SendMessageActivity.this);
		LayoutInflater inflater = LayoutInflater.from(SendMessageActivity.this);
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
		if (action != null && action.equals("blog")) {
			gridView.setAdapter(new GridViewAdapter(this, previewDialog,
					emotionData, "blog"));
		} else {
			// state tweet retweet comment
			gridView.setAdapter(new GridViewAdapter(this, previewDialog,
					emotionData));
		}
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

	private void initAutoComplete(String field, AutoCompleteTextView auto) {
		SharedPreferences sp = getSharedPreferences("network_url", 0);
		String longhistory = sp.getString(field, "nothing");
		String[] hisArrays = longhistory.split(",");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, hisArrays);
		// 只保留最近的50条的记录
		if (hisArrays.length > 50) {
			String[] newArrays = new String[50];
			System.arraycopy(hisArrays, 0, newArrays, 0, 50);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, newArrays);
		}
		auto.setAdapter(adapter);
		auto.setThreshold(1);
		auto.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasFocus) {
					// view.showDropDown();
				}
			}
		});
	}

	private void saveHistory(String field, AutoCompleteTextView auto) {
		String text = auto.getText().toString();

		ArrayList<String> indexAtFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexAtFlag = TagAnalysis.getIndex(auto.getText().toString(), "@");
		} else {
			indexAtFlag = TagAnalysis.getIndexForSina(
					auto.getText().toString(), "@");
		}
		int numverAtFlag = indexAtFlag.size();
		for (int i = 0; i < numverAtFlag / 2; i++) {

			final int start = Integer.valueOf(indexAtFlag.get(i * 2));
			final int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));
			String tag = auto.getText().toString().substring(start, end);
			SharedPreferences sp = getSharedPreferences("network_url", 0);
			String longhistory = sp.getString(field, "nothing");
			if (!longhistory.contains(tag + ",")) {
				StringBuilder sb = new StringBuilder(longhistory);
				sb.insert(0, tag + ",");
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					sp.edit().putString("cfb", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)
						|| statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					sp.edit().putString("twitter", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)) {
					sp.edit().putString("sina", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)) {
					sp.edit().putString("tencent", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {
					sp.edit().putString("sohu", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
					sp.edit().putString("wangyi", sb.toString()).commit();
				}
			}
		}

	}

	private void saveTrendHistory(String field, AutoCompleteTextView auto) {
		String text = auto.getText().toString();

		// 话题
		ArrayList<String> indexHashFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexHashFlag = TagAnalysis.getHashTagIndex(auto.getText()
					.toString(), statusData.getCurrentService());
		} else {
			indexHashFlag = TagAnalysis.getIndexForSina(auto.getText()
					.toString(), "#");
		}

		int number = indexHashFlag.size();
		for (int i = 0; i < number / 2; i++) {

			final int start = Integer.valueOf(indexHashFlag.get(i * 2));
			final int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));
			String tag = auto.getText().toString().substring(start, end);
			SharedPreferences sp = getSharedPreferences("network_url", 0);
			String longhistory = sp.getString(field, "nothing");
			if (!longhistory.contains(tag + ",")) {
				StringBuilder sb = new StringBuilder(longhistory);
				sb.insert(0, tag + ",");
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					sp.edit().putString("cfb", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)
						|| statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					sp.edit().putString("twitter", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)) {
					sp.edit().putString("sina", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)) {
					sp.edit().putString("tencent", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {
					sp.edit().putString("sohu", sb.toString()).commit();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_WANGYI)) {
					sp.edit().putString("wangyi", sb.toString()).commit();
				}
			}
		}

	}

	private void loadImage(final String url, final ImageView image) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		int size = 0;
		ImageView imageView = image;
		callbackImpl = new Imageback(imageView);
		Bitmap cacheImage = imageLoader.loadBitmap(url, callbackImpl);
		if (cacheImage != null) {
			imageView.setImageBitmap(cacheImage);
		}
	}

	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		String type = getMIMEType(f);
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/* 判断文件MimeType的method */
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就跳出软件列表给用户选择 */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
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
					//
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