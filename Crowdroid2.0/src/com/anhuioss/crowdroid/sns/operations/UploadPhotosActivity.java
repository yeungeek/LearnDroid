package com.anhuioss.crowdroid.sns.operations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.CameraActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UploadPhotosActivity extends Activity implements OnClickListener,
		TextWatcher, ServiceConnection {

	private Spinner albumSelect;

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

	Button recorderButton;

	Button addTrends;

	Button atButton;

	private Button upload;

	private Button btn_back;

	private Button btn_home;

	private TextView headName;

	private ImageView preview;

	private EditText description;

	private TextView counterText;

	private HandleProgressDialog progress;

	private StatusData statusData;

	private AccountData accountData;

	protected ArrayList<TimeLineInfo> currentList;

	private String albums_id;

	private String filePath;

	private String imageName;

	private int MAX_TEXT_COUNT = 140;

	private static int intent_Flag = 0;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	private Handler mHandler = new Handler();

	private static boolean isRunning = true;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				// Parser
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);

				if (timelineInfoList != null && timelineInfoList.size() > 0) {

					currentList = timelineInfoList;
				}

				initAlbums();

				if (type == CommHandler.TYPE_UPLOAD_IMAGE) {
					Toast.makeText(UploadPhotosActivity.this,
							getString(R.string.success), Toast.LENGTH_SHORT)
							.show();
					closeProgressDialog();
					finish();
				}

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		SettingData settingData = crowdroidApplication.getSettingData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		String imagePath = settingData.getWallpaper();
		statusData = crowdroidApplication.getStatusData();
		setContentView(R.layout.activity_upload_photos);

		translationButton = (Button) findViewById(R.id.translateButton);
		uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
		shortenUrlButton = (Button) findViewById(R.id.shortenUrlButton);
		longTweetButton = (Button) findViewById(R.id.longTweetButton);
		emotionButton = (Button) findViewById(R.id.emotionButton);
		cameraButton = (Button) findViewById(R.id.cameraButton);
		recorderButton = (Button) findViewById(R.id.recorderButton);
		addTrends = (Button) findViewById(R.id.addTrends);
		atButton = (Button) findViewById(R.id.atButton);

		btn_back = (Button) findViewById(R.id.head_back);
		btn_home = (Button) findViewById(R.id.head_refresh);
		btn_home.setBackgroundResource(R.drawable.header_clean);
		headName = (TextView) findViewById(R.id.head_Name);
		albumSelect = (Spinner) findViewById(R.id.select_albums);
		upload = (Button) findViewById(R.id.upload);
		description = (EditText) findViewById(R.id.description);
		counterText = (TextView) findViewById(R.id.counterText);
		preview = (ImageView) findViewById(R.id.webview_preview);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle.containsKey("create")) {

		} else {

			final CharSequence[] operate = getResources().getStringArray(
					R.array.renren_discovery_upload_photo_manager);
			AlertDialog dialog = new AlertDialog.Builder(
					UploadPhotosActivity.this).setItems(operate,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {

								intent_Flag = 0;
								Intent intent = new Intent();
								intent.setType("image/*");
								intent.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intent, 1);

							} else if (which == 1) {

								intent_Flag = 1;
								CameraActivity.setContextAndEdit(
										UploadPhotosActivity.this, description);
								Intent intent = new Intent(
										UploadPhotosActivity.this,
										CameraActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("type", "upload");
								intent.putExtras(bundle);
								startActivity(intent);
							}
						}
					}).create();
			dialog.show();
		}
		initView();

		description.setOnClickListener(this);
		description.addTextChangedListener(this);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		uploadImageButton.setOnClickListener(this);
		cameraButton.setOnClickListener(this);
		upload.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Bundle bundle = getIntent().getExtras();

		if (intent_Flag == 1) {

			if (bundle.containsKey("filePath")) {
				preview.setVisibility(View.VISIBLE);
				filePath = bundle.getString("filePath");
				imageName = getFileName(filePath);
				BitmapFactory.Options opp = new BitmapFactory.Options();
				opp.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(
						bundle.getString("filePath"), opp);
				preview.setImageBitmap(bm);
			}
		}

		notifyTextHasChanged();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TimelineActivity.isBackgroundNotificationFlag = true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag = false;
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void initView() {
		translationButton.setVisibility(View.GONE);
		shortenUrlButton.setVisibility(View.GONE);
		longTweetButton.setVisibility(View.GONE);
		emotionButton.setVisibility(View.GONE);
		recorderButton.setVisibility(View.GONE);
		addTrends.setVisibility(View.GONE);
		atButton.setVisibility(View.GONE);

		counterText.setText("140");
		headName.setText(R.string.upload_photos);
		LayoutParams para = preview.getLayoutParams();
		para.height = 180;
		para.width = 180;

		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();

		int width = display.getWidth();
		int height = display.getHeight();

		LayoutParams param = preview.getLayoutParams();
		param.height = height * 1 / 3;
		param.width = width * 9 / 10;

		preview.setLayoutParams(param);

	}

	public void initAlbums() {

		// Create Adapter
		List<String> albumList = new ArrayList<String>();
		for (int i = 0; i < currentList.size(); i++) {

			albumList.add(currentList.get(i).getUserInfo().getScreenName());
		}

		ArrayAdapter<String> albumListAdapter = new ArrayAdapter<String>(
				UploadPhotosActivity.this,
				android.R.layout.simple_spinner_item, albumList);
		albumListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		albumSelect.setAdapter(albumListAdapter);

		albumSelect.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				albums_id = currentList.get(position).getStatusId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {

				preview.setVisibility(View.VISIBLE);
				Uri selectedImageUri = data.getData();

				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(selectedImageUri, projection,
						null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String selectedImagePath = cursor.getString(column_index);

				filePath = selectedImagePath;
				imageName = getFileName(filePath);
				String filePath0 = selectedImagePath;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(filePath0, options);
				preview.setImageBitmap(bm);

			}
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		// Prepare Parameter
		// Bundle bundle = this.getIntent().getExtras();
		// String uid = bundle.getString("uid");

		Map<String, Object> parameter;
		parameter = new HashMap<String, Object>();
		parameter.put("uid", accountData.getUid());
		parameter.put("page", String.valueOf(1));
		// HTTP Communication
		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_ALBUMS_TIMELINE, apiServiceListener,
					parameter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub

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

			description.setText("");
			break;
		}
		case R.id.uploadImageButton: {
			intent_Flag = 0;
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, 1);
			break;

		}
		case R.id.cameraButton: {
			intent_Flag = 1;
			CameraActivity.setContextAndEdit(this, description);
			Intent intent = new Intent(this, CameraActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("type", "upload");
			intent.putExtras(bundle);
			startActivity(intent);

			break;
		}
		case R.id.upload: {
			showProgressDialog();
			Map<String, Object> parameter;
			parameter = new HashMap<String, Object>();
			parameter.put("aid", albums_id);
			parameter.put("description", description.getText().toString());
			parameter.put("filePath", filePath);
			parameter.put("imgName", imageName);
			// HTTP Communication
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_UPLOAD_IMAGE, apiServiceListener,
						parameter);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		default:
			break;
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

	private void notifyTextHasChanged() {
		int leftCount = 0;
		//
		if (MAX_TEXT_COUNT != 140) {
			leftCount = MAX_TEXT_COUNT
					- description.getText().toString().length();

		} else {
			int count = getTextCount(description.getText().toString());
			leftCount = MAX_TEXT_COUNT - count;
		}
		counterText.setText(String.valueOf(leftCount));

		if (leftCount < 0) {

			counterText.setTextColor(Color.RED);

			// Disable Confirm Button
			upload.setEnabled(false);

		} else {

			counterText.setTextColor(Color.BLACK);

			// Enable Confirm Button
			upload.setEnabled(true);
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
		count = updateText.length() - strBuf.length() + 11 * urlDataList.size();

		return count;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (isBlank(filePath)) {
			return "";
		}

		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串<br>
	 * 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */

	public static boolean isBlank(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

}
