package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.sns.operations.UploadPhotosActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class CameraActivity extends Activity implements OnLongClickListener,
		OnFocusChangeListener, Callback, OnClickListener, ServiceConnection {

	private static String imageFilePath = "";
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ImageButton largeButton;
	private ImageButton smallButton;
	private ImageButton recButton;
	private ImageButton cancelButton;
	private ImageButton saveButton;
	private TextView sizeShow;
	private Camera mCamera;
	private ImageView mImageView;
	private byte[] mBitmapData;
	private static String DIR_PATH = "/sdcard/DCIM/Camera";
	private int imageWidth;
	private int imageHeight;

	private static Context context;

	private StatusData statusData;

	private static EditText edit;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			SendMessageActivity sendMessageActivity = (SendMessageActivity) context;
			sendMessageActivity.setTitle(getString(R.string.app_name));
			sendMessageActivity.setProgressBarIndeterminateVisibility(false);

			if (statusCode != null && statusCode.equals("200")) {

				ParseHandler parseHandler = new ParseHandler();
				String iamgeUrl = (String) parseHandler.parser(service, type,
						statusCode, message);
				if (iamgeUrl != null) {
					int length = edit.getText().length();
					edit.append("\n" + iamgeUrl);
					edit.setSelection(length);
				}

			} else {
				Toast.makeText(
						CameraActivity.this,
						ErrorMessage.getErrorMessage(CameraActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_camera_new);

		smallButton = (ImageButton) findViewById(R.id.camera_small);
		largeButton = (ImageButton) findViewById(R.id.camera_large);
		recButton = (ImageButton) findViewById(R.id.camera_rec);
		cancelButton = (ImageButton) findViewById(R.id.camera_cancel);
		saveButton = (ImageButton) findViewById(R.id.camera_save);
		mImageView = (ImageView) findViewById(R.id.myImageView);
		sizeShow = (TextView) findViewById(R.id.size_show);

		smallButton.setOnClickListener(this);
		largeButton.setOnClickListener(this);
		recButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		smallButton.setOnFocusChangeListener(this);
		largeButton.setOnFocusChangeListener(this);
		recButton.setOnFocusChangeListener(this);
		saveButton.setOnFocusChangeListener(this);
		cancelButton.setOnFocusChangeListener(this);

		smallButton.setOnLongClickListener(this);
		largeButton.setOnLongClickListener(this);
		recButton.setOnLongClickListener(this);
		saveButton.setOnLongClickListener(this);
		cancelButton.setOnLongClickListener(this);

		mSurfaceView = (SurfaceView) findViewById(R.id.mySurfaceView);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	@Override
	protected void onStart() {

		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/DCIM/Camera";
		imageWidth = 320;
		imageHeight = 240;
		sizeShow.setText("picture size : 320*240");

		changeButton(false);

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (mCamera == null) {
				mCamera = Camera.open();
			}
		} catch (Exception e) {
		}
		;
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	public void initCamera() {

		try {
			if (mCamera == null) {
				mCamera = Camera.open();
			}
		} catch (Exception e) {
		}
		;

		try {
			Camera.Parameters myParameters = mCamera.getParameters();
			myParameters.setPictureFormat(PixelFormat.JPEG);
			myParameters.setPreviewSize(imageWidth, imageHeight);
			myParameters.setPictureSize(imageWidth, imageHeight);
			mCamera.setParameters(myParameters);
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Camera.Parameters myParameters = mCamera.getParameters();
				myParameters.setPictureFormat(PixelFormat.JPEG);
				myParameters.setPreviewSize(imageWidth, imageHeight);
				mCamera.setParameters(myParameters);
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.camera_large: {
			imageWidth = 640;
			imageHeight = 480;
			sizeShow.setText("picture size : 640*480");
			initCamera();
			break;
		}
		case R.id.camera_small: {
			imageWidth = 320;
			imageHeight = 240;
			sizeShow.setText("picture size : 320*240");
			initCamera();
			break;
		}
		case R.id.camera_rec: {

			changeButton(true);
			mCamera.takePicture(mShutterCallback, myRawCallback, myjpegCallback);
			break;

		}
		case R.id.camera_cancel: {
			changeButton(false);
			initCamera();
			mImageView.setImageBitmap(null);
			break;
		}
		case R.id.camera_save: {
			changeButton(false);
			saveImageFile();
			initCamera();
			mImageView.setImageBitmap(null);
			showUploadProgress();
			Bundle type = this.getIntent().getExtras();
			if (type != null) {
				if (type.containsKey("type")) {
					Intent intent = new Intent(CameraActivity.this,
							UploadPhotosActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("filePath", getImageFilePath());
					bundle.putString("create", "");
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				} else if (type.containsKey("update")) {
					Intent intent = new Intent(CameraActivity.this,
							UpdateUserImageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("filePath", getImageFilePath());
					intent.putExtras(bundle);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			} else {
				Intent intent = new Intent(CameraActivity.this,
						SendMessageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("filePath", getImageFilePath());
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
			break;
		}
		default: {
			break;
		}
		}

	}

	private void showUploadProgress() {

		if (imageFilePath.equals("")) {
			return;
		}

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {

			SendMessageActivity sendMessageActivity = (SendMessageActivity) context;
			sendMessageActivity.setTitle(getString(R.string.app_name)
					+ "        " + getString(R.string.upload_image));
			sendMessageActivity.setProgressBarIndeterminateVisibility(true);

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("filePath", imageFilePath);

			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_UPLOAD_IMAGE, apiServiceListener,
						parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

	}

	private void saveImageFile() {

		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File directory = new File(DIR_PATH);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			// File name
			Calendar c = Calendar.getInstance();
			Date d = c.getTime();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			String fileName = sdf.format(d) + ".jpg";
			File file = new File(directory, fileName);
			imageFilePath = file.getAbsolutePath();
			try {

				file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(mBitmapData);

				fileOutputStream.flush();
				fileOutputStream.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.gc();

		} else {
			Toast.makeText(this, "No SD Card Mounted.", Toast.LENGTH_SHORT)
					.show();
		}

	}

	ShutterCallback mShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {

		}
	};

	PictureCallback myRawCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

		}
	};

	PictureCallback myjpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				mBitmapData = data;
				saveButton.setClickable(true);
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				mImageView.setImageBitmap(bm);
			} catch (OutOfMemoryError e) {
			}
			System.gc();
		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		initCamera();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mImageView.setImageBitmap(null);
		}
	}

	public void changeButton(boolean change) {

		if (change) {

			cancelButton.setEnabled(true);
			saveButton.setEnabled(true);

			smallButton.setEnabled(false);
			largeButton.setEnabled(false);
			recButton.setEnabled(false);

			cancelButton.setFocusable(true);
			saveButton.setFocusable(true);

			smallButton.setFocusable(false);
			largeButton.setFocusable(false);
			recButton.setFocusable(false);

		} else {

			cancelButton.setEnabled(false);
			saveButton.setEnabled(false);

			smallButton.setEnabled(true);
			largeButton.setEnabled(true);
			recButton.setEnabled(true);

			cancelButton.setFocusable(false);
			saveButton.setFocusable(false);

			smallButton.setFocusable(true);
			largeButton.setFocusable(true);
			recButton.setFocusable(true);

		}

	}

	public static String getImageFilePath() {
		String imageFilePathTemp = imageFilePath;
		imageFilePath = "";
		return imageFilePathTemp;
	}

	public static void setContextAndEdit(Context mContext, EditText updateText) {
		context = mContext;
		edit = updateText;
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
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.camera_large: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_large), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.camera_small: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_small), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.camera_rec: {
			Toast.makeText(CameraActivity.this, getString(R.string.camera_rec),
					Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.camera_cancel: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_cancel), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.camera_save: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_save), Toast.LENGTH_SHORT).show();
			break;
		}
		default: {
			break;
		}
		}
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (!hasFocus) {
			return;
		}

		switch (v.getId()) {
		case R.id.camera_large: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_large), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.camera_small: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_small), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.camera_rec: {
			Toast.makeText(CameraActivity.this, getString(R.string.camera_rec),
					Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.camera_cancel: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_cancel), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.camera_save: {
			Toast.makeText(CameraActivity.this,
					getString(R.string.camera_save), Toast.LENGTH_SHORT).show();
			break;
		}
		default: {
			break;
		}
		}

	}

}
