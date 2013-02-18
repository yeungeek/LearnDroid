package com.anhuioss.crowdroid.sns.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.CameraActivity;
import com.anhuioss.crowdroid.activity.RecorderActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
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
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
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
import android.net.Uri;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateBlogActivity extends Activity implements OnClickListener,
		TextWatcher, ServiceConnection {

	private SharedPreferences sharePreference;

	private SharedPreferences.Editor editor;

	TextView head_name;

	TextView title_info;

	TextView content;

	/** Title */

	EditText title;

	/** Text */
	AutoCompleteTextView updateText;

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

	Button recorderButton;

	Button addTrends;

	Button atButton;

	/** Count For Character */
	TextView countText;

	/** Confirm */
	Button send;

	/** Intent Flag */
	String action = null;

	private StatusData statusData;

	private HandleProgressDialog progress;

	private AlertDialog previewDialog;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	public static ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();

	private Handler mHandler = new Handler();

	private int MAX_TEXT_COUNT = 140;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_EMOTION) {

					ParseHandler parseHandler = new ParseHandler();
					emotionList = (ArrayList<EmotionInfo>) parseHandler.parser(
							service, type, statusCode, message);
					if (!emotionList.isEmpty()) {
						// showEmotionsDialog();
						initEmotionView(emotionList);
					}

				}

				if (type == CommHandler.TYPE_UPDATE_BLOG) {
					finish();
					// Intent intent = new
					// Intent(UpdateBlogActivity.this,SNSDiscoveryActivity.class);
					// startActivity(intent);
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
		setContentView(R.layout.activity_update_blog);
		sharePreference = getSharedPreferences("SHARE_INIT_STATUS", 0);

		// Find Views
		// uploadMessage = (TextView) findViewById(R.id.upload_message);
		title_info = (TextView) findViewById(R.id.title_info);
		content = (TextView) findViewById(R.id.content);
		title = (EditText) findViewById(R.id.title);
		updateText = (AutoCompleteTextView) findViewById(R.id.update_text);
		translationButton = (Button) findViewById(R.id.translateButton);
		uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
		shortenUrlButton = (Button) findViewById(R.id.shortenUrlButton);
		longTweetButton = (Button) findViewById(R.id.longTweetButton);
		emotionButton = (Button) findViewById(R.id.emotionButton);
		cameraButton = (Button) findViewById(R.id.cameraButton);
		recorderButton = (Button) findViewById(R.id.recorderButton);
		countText = (TextView) findViewById(R.id.counterText);
		send = (Button) findViewById(R.id.send);
		head_name = (TextView) findViewById(R.id.head_Name);

		addTrends = (Button) findViewById(R.id.addTrends);

		atButton = (Button) findViewById(R.id.atButton);
		btn_back = (Button) findViewById(R.id.head_back);
		btn_home = (Button) findViewById(R.id.head_refresh);
		btn_home.setBackgroundResource(R.drawable.main_home);

		// Set Listener
		updateText.setOnClickListener(this);
		updateText.addTextChangedListener(this);
		translationButton.setOnClickListener(this);
		emotionButton.setOnClickListener(this);
		send.setOnClickListener(this);
		atButton.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);

		head_name.setText(R.string.update_blog);

		title_info.setText(R.string.title);
		content.setText(R.string.content);
		countText.setText("1000");
		MAX_TEXT_COUNT = 1000;

	}

	@Override
	public void onStart() {
		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		notifyTextHasChanged();

		initViewsByService(statusData.getCurrentService());

		SettingData settingData = crowdroidApplication.getSettingData();
		String imagePath = settingData.getWallpaper();
		String fontColor = settingData.getFontColor();

		// Set Background
		loadBackGroundImage(imagePath);

		Intent inputIntent = getIntent();
		Bundle b = inputIntent.getExtras();

		Bundle bundle = getIntent().getExtras();
		action = bundle.getString("action");

		// insert mention users
		if (bundle.containsKey("atUserName")) {
			String front = sharePreference.getString("EDIT_INIT_STATUS_FRONT",
					"");
			String behind = sharePreference.getString(
					"EDIT_INIT_STATUS_BEHIND", "");
			updateText.setText(front + bundle.getString("atUserName") + behind);
			updateText.setSelection(updateText.getText().length());
			title.setText(sharePreference.getString("EDIT_INIT_BLOG_TITLE", ""));
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
			title.setText(sharePreference.getString("EDIT_INIT_BLOG_TITLE", ""));

		}

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	private void initViewsByService(String service) {

		Bundle bundle = getIntent().getExtras();

		if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
			uploadImageButton.setVisibility(View.GONE);
			shortenUrlButton.setVisibility(View.GONE);
			longTweetButton.setVisibility(View.GONE);
			cameraButton.setVisibility(View.GONE);
			recorderButton.setVisibility(View.GONE);
			addTrends.setVisibility(View.GONE);
			head_name.setText(R.string.update_blog);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		status.edit().putString("editText", updateText.getText().toString())
				.commit();
		TimelineActivity.isBackgroundNotificationFlag = true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag = false;
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

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		notifyTextHasChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

			Intent home = new Intent(UpdateBlogActivity.this,
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
		case R.id.emotionButton: {

			int index = updateText.getSelectionStart();// 获取光标所在位置
			editor = sharePreference.edit();
			editor.putString("EDIT_INIT_STATUS_FRONT",
					String.valueOf(updateText.getText()).substring(0, index));
			editor.putString(
					"EDIT_INIT_STATUS_BEHIND",
					String.valueOf(updateText.getText()).substring(index,
							updateText.getText().length()));
			editor.putString("EDIT_INIT_BLOG_TITLE",
					String.valueOf(title.getText()));
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
			if (action != null && action.equals("blog")) {
				usd = new AtUserSelectDialog(this, "send_message_blog");
				editor.putString("EDIT_INIT_BLOG_TITLE",
						String.valueOf(title.getText()));
			}
			editor.commit();

			usd.setTitle("@" + getString(R.string.insert_at_name));
			usd.show();
			break;
		}
		case R.id.send: {

			// Update
			showProgressDialog();

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("status", updateText.getText().toString());
			parameters.put("title", title.getText().toString());

			if (action.equals("blog") && action != null) {

				// Request (get service from status information)
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_UPDATE_BLOG, apiServiceListener,
							parameters);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		default: {

		}
		}
	}

	private void notifyTextHasChanged() {
		int leftCount = 0;
		//
		if (MAX_TEXT_COUNT != 140) {
			leftCount = MAX_TEXT_COUNT
					- updateText.getText().toString().length();

		}
		countText.setText(String.valueOf(leftCount));

		if (leftCount < 0) {

			countText.setTextColor(Color.RED);

			// Disable Confirm Button
			send.setEnabled(false);

		} else {

			countText.setTextColor(Color.BLACK);

			// Enable Confirm Button
			send.setEnabled(true);
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

	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				UpdateBlogActivity.this);
		LayoutInflater inflater = LayoutInflater.from(UpdateBlogActivity.this);
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

}
