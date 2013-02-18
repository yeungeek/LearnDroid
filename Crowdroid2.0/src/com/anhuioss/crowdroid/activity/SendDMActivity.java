package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.DirectMessageReceiveActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.DirectMessageSendActivity;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.dialog.AtUserSelectDialog;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.LongTweetDialog;
import com.anhuioss.crowdroid.dialog.TranslateDialog;
import com.anhuioss.crowdroid.dialog.UrlShortenDialog;
import com.anhuioss.crowdroid.dialog.UserSelectDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;

public class SendDMActivity extends Activity implements OnClickListener,
		TextWatcher, ServiceConnection {

	private SharedPreferences sharePreference;

	private SharedPreferences.Editor editor;

	private int page = 0;

	private AlertDialog previewDialog;

	private Handler mHandler = new Handler();

	TextView t_direct_message;

	Button btn_camera;

	Button btn_at;

	Button btn_t;

	Button btn_emotion;

	Button btn_speak;

	Button headBack;

	Button headHome;

	TextView headName;

	EditText directName;

	ImageButton direchNameSearch;

	/** Upload Message */
	TextView uploadMessage;

	/** File Path */
	TextView filePath;

	/** Text */
	EditText updateText;

	/** Translation */
	Button translationButton;

	/** Upload Image */
	Button uploadImageButton;

	/** Shorten URL */
	Button shortenUrlButton;

	/** Long Tweet */
	Button longTweetButton;

	/** Count For Character */
	TextView countText;

	/** Confirm */
	Button okButton;

	/** Cancel */
	Button cancelButton;

	/** Screen Name */
	String screenName = null;

	String userName = null;

	String uid;

	/** Max Count */
	private int MAX_TEXT_COUNT = 140;

	private int MAX_CFB_TEXT_COUNT = 140;

	private StatusData statusData;

	private HandleProgressDialog progress;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();

			if (statusCode != null && Integer.valueOf(statusCode) == 200) {

				if (type == CommHandler.TYPE_GET_EMOTION) {

					ParseHandler parseHandler = new ParseHandler();
					SendMessageActivity.emotionList = (ArrayList<EmotionInfo>) parseHandler
							.parser(service, type, statusCode, message);
					if (!SendMessageActivity.emotionList.isEmpty()) {
						initEmotionView(SendMessageActivity.emotionList);
					}
				}
				if (!screenName.equals("")) {
					finish();
				} else {
					updateText.setText("");
				}
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
					&& statusCode.equals("150")) {

				Toast.makeText(SendDMActivity.this,
						getResources().getString(R.string.send_da_tip),
						Toast.LENGTH_SHORT).show();
				finish();
			} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
				if (statusCode.equals("401")) {
					Toast.makeText(
							SendDMActivity.this,
							getResources().getString(
									R.string.wangyi_send_da_401_tip),
							Toast.LENGTH_SHORT).show();
				} else if (statusCode.equals("404")) {
					Toast.makeText(
							SendDMActivity.this,
							getResources().getString(
									R.string.wangyi_send_da_404_tip),
							Toast.LENGTH_SHORT).show();
				} else if (statusCode.equals("403")) {
					Toast.makeText(
							SendDMActivity.this,
							getResources().getString(
									R.string.wangyi_send_da_403_tip),
							Toast.LENGTH_SHORT).show();
				}

				finish();
			} else {
				Toast.makeText(
						SendDMActivity.this,
						ErrorMessage.getErrorMessage(SendDMActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
				finish();
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
		statusData = crowdroidApplication.getStatusData();
		String imagePath = settingData.getWallpaper();
		loadBackGroundImage(imagePath);
		setContentView(R.layout.send_direct_message);

		sharePreference = getSharedPreferences("SHARE_INIT_DM_STATUS", 0);

		// Find Views
		directName = (EditText) findViewById(R.id.direct_name);
		direchNameSearch = (ImageButton) findViewById(R.id.direct_name_change);
		updateText = (EditText) findViewById(R.id.update_text);
		translationButton = (Button) findViewById(R.id.translateButton);
		uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
		shortenUrlButton = (Button) findViewById(R.id.shortenUrlButton);
		longTweetButton = (Button) findViewById(R.id.longTweetButton);
		countText = (TextView) findViewById(R.id.counterText);
		okButton = (Button) findViewById(R.id.send);

		btn_at = (Button) findViewById(R.id.atButton);
		btn_t = (Button) findViewById(R.id.addTrends);
		btn_emotion = (Button) findViewById(R.id.emotionButton);
		btn_camera = (Button) findViewById(R.id.cameraButton);
		btn_speak = (Button) findViewById(R.id.recorderButton);
		t_direct_message = (TextView) findViewById(R.id.direct_message);

		headBack = (Button) findViewById(R.id.head_back);

		headHome = (Button) findViewById(R.id.head_refresh);

		headName = (TextView) findViewById(R.id.head_Name);

		headHome.setBackgroundResource(R.drawable.main_home);

		headName.setText(R.string.direct_update);

		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			SharedPreferences status = getSharedPreferences("status",
					Context.MODE_PRIVATE);
			countText.setText(status.getString("max_input_charactor", "140"));
			int m = Integer.valueOf(countText.getText().toString());
			MAX_CFB_TEXT_COUNT = m;
		}

		// Set Listener
		directName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// uploadMessage.setText(getResources().getString(R.string.direct_message_to)
				// + " " + s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		direchNameSearch.setOnClickListener(this);
		updateText.setOnClickListener(this);
		updateText.addTextChangedListener(this);
		translationButton.setOnClickListener(this);
		uploadImageButton.setOnClickListener(this);
		shortenUrlButton.setOnClickListener(this);
		longTweetButton.setOnClickListener(this);
		okButton.setOnClickListener(this);
		t_direct_message.setOnClickListener(this);

		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);
		btn_at.setOnClickListener(this);
		btn_t.setOnClickListener(this);
		btn_emotion.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();

		// Get Intent Data
		Bundle bundle = getIntent().getExtras();
		screenName = bundle.getString("name");
		userName = bundle.getString("userName");
		uid = bundle.getString("uid");
		directName.setText(screenName);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		// statusData = crowdroidApplication.getStatusData();

		initViewsByService(statusData.getCurrentService());

		SettingData settingData = crowdroidApplication.getSettingData();
		String imagePath = settingData.getWallpaper();
		String fontColor = settingData.getFontColor();

		// Set Background
		loadBackGroundImage(imagePath);

		notifyTextHasChanged();

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

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
		status.edit()
				.putString("DirecEditText", updateText.getText().toString())
				.commit();
		TimelineActivity.isBackgroundNotificationFlag = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences status = getSharedPreferences("status", MODE_PRIVATE);
		String text = updateText.getText().toString();
		if (text == null && text == "") {
			updateText.setText(status.getString("DirecEditText", ""));
		}
		TimelineActivity.isBackgroundNotificationFlag = false;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		String inital = sharePreference.getString("SHARE_INIT_DM_STATUS", "");
		boolean isFromOutSideInsert = sharePreference.getBoolean(
				"WHEATHER_FROM_DM_OUTSIDE_INSERT", false);
		if (isFromOutSideInsert) {
			editor = sharePreference.edit();
			editor.putBoolean("WHEATHER_FROM_DM_OUTSIDE_INSERT", false);
			editor.commit();
			String front = sharePreference.getString("EDIT_INIT_STATUS_FRONT",
					"");
			String behind = sharePreference.getString(
					"EDIT_INIT_STATUS_BEHIND", "");
			updateText.setText(front + inital + behind);
			updateText.setSelection(updateText.getText().length());
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.direct_name_change: {

			UserSelectDialog usd = new UserSelectDialog(this);
			usd.show();
			break;

		}
		case R.id.cameraButton: {
			CameraActivity.setContextAndEdit(this, updateText);
			Intent intent = new Intent(this, CameraActivity.class);
			startActivity(intent);

		}

		case R.id.head_refresh: {

			Intent home = new Intent(SendDMActivity.this,
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

		case R.id.atButton: {
			AtUserSelectDialog usd = new AtUserSelectDialog(this,
					"send_direct_messsage");
			usd.setTitle("@" + getString(R.string.insert_at_name));
			usd.show();
			break;
		}

		case R.id.addTrends: {
			updateText.setText("#" + getString(R.string.insert_topic) + "#");

			updateText.setSelection(1, getString(R.string.insert_topic)
					.length() + 1);
			break;
		}
		case R.id.direct_message: {
			Intent direct = new Intent(SendDMActivity.this,
					DirectMessageReceiveActivity.class);
			direct.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(direct);
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
		case R.id.send: {

			String directTo = directName.getText().toString();
			if (!directTo.equals("")) {

				try {

					showProgressDialog();

					// Prepare Parameters
					Map<String, Object> parameters;
					parameters = new HashMap<String, Object>();
					parameters.put("message", updateText.getText().toString());
					parameters.put("geo_latutude", "0");
					parameters.put("geo_longitude", "0");
					parameters.put("send_to", directTo);
					parameters.put("uid", uid);
					parameters.put("name", screenName);

					// Request (get service from status information)
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_DIRECT_MESSAGE,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(SendDMActivity.this,
						getResources().getString(R.string.type_or_select_user),
						Toast.LENGTH_SHORT).show();
			}

			break;
		}
		case R.id.head_back: {

			finish();
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
		// filePath.setVisibility(View.GONE);

		if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			longTweetButton.setVisibility(View.GONE);
			uploadImageButton.setVisibility(View.GONE);
		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			uploadImageButton.setVisibility(View.GONE);
			btn_emotion.setVisibility(View.GONE);
		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			uploadImageButton.setVisibility(View.GONE);
			btn_emotion.setVisibility(View.GONE);
		} else if (service.equals(IGeneral.SERVICE_NAME_SINA)
				|| service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			longTweetButton.setVisibility(View.GONE);
			uploadImageButton.setVisibility(View.GONE);
		} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)
				|| service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
			shortenUrlButton.setVisibility(View.GONE);
			longTweetButton.setVisibility(View.GONE);
			uploadImageButton.setVisibility(View.GONE);
		}
		btn_speak.setVisibility(View.GONE);
		btn_at.setVisibility(View.GONE);
		btn_camera.setVisibility(View.GONE);
		btn_t.setVisibility(View.GONE);

	}

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

	private void notifyTextHasChanged() {
		int leftCount = 0;
		if (MAX_CFB_TEXT_COUNT != 140) {
			leftCount = MAX_CFB_TEXT_COUNT
					- updateText.getText().toString().length();
		} else {
			leftCount = MAX_TEXT_COUNT
					- updateText.getText().toString().length();
		}

		countText.setText(String.valueOf(leftCount));

		if (leftCount < 0) {

			countText.setTextColor(Color.RED);

			// Disable Confirm Button
			okButton.setEnabled(false);
			longTweetButton.setEnabled(true);

		} else {

			countText.setTextColor(Color.BLACK);

			// Enable Confirm Button
			okButton.setEnabled(true);
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

	private void showEmotionsDialog() {

		// Initial Page
		page = 0;

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				SendDMActivity.this);
		LayoutInflater inflater = LayoutInflater.from(SendDMActivity.this);
		final View textEntryView = inflater.inflate(
				R.layout.dialog_show_emotions, null);
		builder.setView(textEntryView);
		final WebView webView = (WebView) textEntryView
				.findViewById(R.id.image_web_view);
		webView.loadUrl("file:///android_asset/emotions.html");
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(this, "Image");

		final Button preButton = (Button) textEntryView
				.findViewById(R.id.dialog_emotion_pre);
		final Button nextButton = (Button) textEntryView
				.findViewById(R.id.dialog_emotion_next);

		// Previous Button
		preButton.setEnabled(false);
		if (SendMessageActivity.emotionList != null
				&& SendMessageActivity.emotionList.size() == 30) {
			nextButton.setEnabled(false);
		}
		preButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				page--;
				if (page == 0) {
					preButton.setEnabled(false);
				}
				nextButton.setEnabled(true);

				webView.clearView();
				webView.loadUrl("file:///android_asset/emotions.html");

			}
		});

		// Next Button
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				page++;
				if (page == SendMessageActivity.emotionList.size() / 30 - 1) {
					nextButton.setEnabled(false);
				}
				preButton.setEnabled(true);

				webView.clearView();
				webView.loadUrl("file:///android_asset/emotions.html");

			}
		});

		previewDialog = builder.show();

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
				SendDMActivity.this);
		LayoutInflater inflater = LayoutInflater.from(SendDMActivity.this);
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
		gridView.setAdapter(new GridViewAdapter(SendDMActivity.this,
				previewDialog, emotionData));

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

}