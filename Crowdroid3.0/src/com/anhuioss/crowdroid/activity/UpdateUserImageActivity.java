package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class UpdateUserImageActivity extends Activity implements
		ServiceConnection, OnClickListener {

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	private StatusData statusData;

	private CrowdroidApplication crowdroidApplication;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private ImageView image_Preview;

	private Button update;

	private Button up;

	private Button headerBack = null;

	private Button headerHome = null;

	private TextView headerName = null;

	private String filePath;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {
				closeProgressDialog();
				finish();

			}
			if (!"200".equals(statusCode)) {

				Toast.makeText(UpdateUserImageActivity.this, ErrorMessage
						.getErrorMessage(UpdateUserImageActivity.this,
								statusCode), Toast.LENGTH_SHORT);
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		setContentView(R.layout.updateuserimage);
		image_Preview = (ImageView) findViewById(R.id.updateimage);
		update = (Button) findViewById(R.id.update);
		up = (Button) findViewById(R.id.up);
		headerBack = (Button) findViewById(R.id.head_back);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerName.setText(R.string.tencent_update_user_image);
		headerHome.setBackgroundResource(R.drawable.main_home);

		image_Preview.setOnClickListener(this);
		update.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		up.setOnClickListener(this);

	}

	@Override
	protected void onStart() {

		super.onStart();

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		super.onStop();
		// Unbind Service
		unbindService(this);

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	private void openSelectDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle(R.string.tencent_select_update_user_image);

		String[] items = null;
		items = getResources().getStringArray(R.array.upload_items);

		dialog.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case 0: {

					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 1);

					break;
				}
				case 1: {

					Intent intent = new Intent(UpdateUserImageActivity.this,
							CameraActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("update", "image");
					intent.putExtras(bundle);
					startActivityForResult(intent, 2);
					break;
				}
				case 2: {
					dialog.dismiss();
					break;
				}
				}

			}
		}).create().show();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.update: {

			tweet();
			finish();
			break;

		}
		case R.id.up: {

			openSelectDialog();
			break;

		}
		case R.id.head_refresh: {
			Intent home = new Intent(UpdateUserImageActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Uri selectedImageUri = data.getData();

				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(selectedImageUri, projection,
						null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String selectedImagePath = cursor.getString(column_index);

				filePath = selectedImagePath;
				startPhotoZoom(data.getData());

			} else if (requestCode == 2) {

				Bundle bundle = data.getExtras();

				if (bundle.containsKey("filePath")) {
					filePath = bundle.getString("filePath");
					File temp = new File(filePath);
					startPhotoZoom(Uri.fromFile(temp));
				}

			} else if (requestCode == 3) {
				if (data != null) {
					setPicToView(data);
				}
			}
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			image_Preview.setImageBitmap(photo);

		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;

	}

	private void tweet() {

		try {
			// Update
			showProgressDialog();

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("filePath", filePath);

			// Request (get service from status information)

			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_UPDATE_USER_IMAGE, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
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
			if (UpdateUserImageActivity.this != null
					&& !UpdateUserImageActivity.this.isFinishing()) {
				progress.dismiss();
			}

		}
	}

}
